using FluentAssertions;
using MassTransit;
using Microsoft.AspNetCore.SignalR;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Hosting;
using Moq;
using MyApp.CoreService.Data;
using MyApp.CoreService.Enums;
using MyApp.CoreService.ExchangeRates;
using MyApp.CoreService.Hubs;
using MyApp.CoreService.Messaging.Consumers;
using MyApp.CoreService.Messaging.Messages;
using MyApp.CoreService.Models;
using MyApp.CoreService.Services;
using Xunit;

namespace MyApp.CoreService.Tests.Unit.Handlers.Transactions.Commands;

/// <summary>
/// Exercises <see cref="TransactionRequestedConsumer"/> via in-memory MassTransit + IRequestClient
/// (same pipeline as HTTP handlers).
/// </summary>
public class TransactionMessagingTests
{
    private static IHost CreateHost(string databaseName)
    {
        var hubMock = new Mock<IHubContext<TransactionHub>>(MockBehavior.Loose);
        var clients = new Mock<IHubClients>(MockBehavior.Loose);
        var clientProxy = new Mock<IClientProxy>(MockBehavior.Loose);
        var firebaseMock = new Mock<IFirebaseNotificationService>(MockBehavior.Loose);
        clients.Setup(c => c.Group(It.IsAny<string>())).Returns(clientProxy.Object);
        hubMock.Setup(h => h.Clients).Returns(clients.Object);

        return Host.CreateDefaultBuilder()
            .ConfigureServices(services =>
            {
                services.AddDbContext<CoreDbContext>(o => o.UseInMemoryDatabase(databaseName));
                services.AddSingleton(hubMock.Object);
                services.AddSingleton<IExchangeRateService, NoOpExchangeRateService>();
                services.AddSingleton(firebaseMock.Object);
                services.AddMassTransit(x =>
                {
                    x.AddConsumer<TransactionRequestedConsumer>();
                    x.AddRequestClient<TransactionRequested>();
                    x.UsingInMemory((context, cfg) => cfg.ConfigureEndpoints(context));
                });
            })
            .Build();
    }

    private static async Task SeedOpenAccountAsync(CoreDbContext db, decimal balance = 0m)
    {
        var account = new Account
        {
            OwnerId = 1,
            Balance = balance,
            Currency = "RUB",
            Status = AccountStatus.Open,
            CreatedAt = DateTimeOffset.UtcNow
        };
        db.Accounts.Add(account);
        await db.SaveChangesAsync();
    }

    [Fact]
    public async Task Deposit_ValidAmount_IncreasesAccountBalance()
    {
        var dbName = Guid.NewGuid().ToString();
        using var host = CreateHost(dbName);
        await host.StartAsync();
        try
        {
            int accountId;
            await using (var seed = host.Services.CreateAsyncScope())
            {
                var db = seed.ServiceProvider.GetRequiredService<CoreDbContext>();
                await SeedOpenAccountAsync(db, balance: 100m);
                accountId = db.Accounts.First().Id;
            }

            await using var scope = host.Services.CreateAsyncScope();
            var client = scope.ServiceProvider.GetRequiredService<IRequestClient<TransactionRequested>>();
            await client.GetResponse<TransactionCompleted>(
                new TransactionRequested(
                    Guid.NewGuid(), accountId, TransactionType.Deposit, 50m,
                    null, null, null),
                CancellationToken.None);

            await using var assertScope = host.Services.CreateAsyncScope();
            var assertDb = assertScope.ServiceProvider.GetRequiredService<CoreDbContext>();
            assertDb.Accounts.Find(accountId)!.Balance.Should().Be(150m);
        }
        finally
        {
            await host.StopAsync();
        }
    }

    [Fact]
    public async Task Deposit_ValidAmount_ReturnsTransactionWithCorrectBalances()
    {
        var dbName = Guid.NewGuid().ToString();
        using var host = CreateHost(dbName);
        await host.StartAsync();
        try
        {
            int accountId;
            await using (var seed = host.Services.CreateAsyncScope())
            {
                var db = seed.ServiceProvider.GetRequiredService<CoreDbContext>();
                await SeedOpenAccountAsync(db, balance: 200m);
                accountId = db.Accounts.First().Id;
            }

            await using var scope = host.Services.CreateAsyncScope();
            var client = scope.ServiceProvider.GetRequiredService<IRequestClient<TransactionRequested>>();
            var response = await client.GetResponse<TransactionCompleted>(
                new TransactionRequested(
                    Guid.NewGuid(), accountId, TransactionType.Deposit, 75m,
                    null, "Test deposit", null),
                CancellationToken.None);

            var tx = response.Message.Transaction;
            tx.Type.Should().Be(TransactionType.Deposit);
            tx.Amount.Should().Be(75m);
            tx.BalanceBefore.Should().Be(200m);
            tx.BalanceAfter.Should().Be(275m);
            tx.Description.Should().Be("Test deposit");
        }
        finally
        {
            await host.StopAsync();
        }
    }

    [Fact]
    public async Task Withdraw_InsufficientFunds_ThrowsViaFault()
    {
        var dbName = Guid.NewGuid().ToString();
        using var host = CreateHost(dbName);
        await host.StartAsync();
        try
        {
            int accountId;
            await using (var seed = host.Services.CreateAsyncScope())
            {
                var db = seed.ServiceProvider.GetRequiredService<CoreDbContext>();
                await SeedOpenAccountAsync(db, balance: 50m);
                accountId = db.Accounts.First().Id;
            }

            await using var scope = host.Services.CreateAsyncScope();
            var client = scope.ServiceProvider.GetRequiredService<IRequestClient<TransactionRequested>>();
            await Assert.ThrowsAsync<RequestFaultException>(() => client.GetResponse<TransactionCompleted>(
                new TransactionRequested(
                    Guid.NewGuid(), accountId, TransactionType.Withdrawal, 100m,
                    null, null, null),
                CancellationToken.None));
        }
        finally
        {
            await host.StopAsync();
        }
    }

    [Fact]
    public async Task Transfer_ValidTransfer_UpdatesBothBalances()
    {
        var dbName = Guid.NewGuid().ToString();
        using var host = CreateHost(dbName);
        await host.StartAsync();
        try
        {
            int sourceId, targetId;
            await using (var seed = host.Services.CreateAsyncScope())
            {
                var db = seed.ServiceProvider.GetRequiredService<CoreDbContext>();
                var source = new Account
                {
                    OwnerId = 1, Balance = 400m, Currency = "RUB",
                    Status = AccountStatus.Open, CreatedAt = DateTimeOffset.UtcNow
                };
                var target = new Account
                {
                    OwnerId = 1, Balance = 100m, Currency = "RUB",
                    Status = AccountStatus.Open, CreatedAt = DateTimeOffset.UtcNow
                };
                db.Accounts.AddRange(source, target);
                await db.SaveChangesAsync();
                sourceId = source.Id;
                targetId = target.Id;
            }

            await using var scope = host.Services.CreateAsyncScope();
            var client = scope.ServiceProvider.GetRequiredService<IRequestClient<TransactionRequested>>();
            await client.GetResponse<TransactionCompleted>(
                new TransactionRequested(
                    Guid.NewGuid(), sourceId, TransactionType.Transfer, 150m,
                    targetId, null, null),
                CancellationToken.None);

            await using var assertScope = host.Services.CreateAsyncScope();
            var assertDb = assertScope.ServiceProvider.GetRequiredService<CoreDbContext>();
            assertDb.Accounts.Find(sourceId)!.Balance.Should().Be(250m);
            assertDb.Accounts.Find(targetId)!.Balance.Should().Be(250m);
        }
        finally
        {
            await host.StopAsync();
        }
    }

    [Fact]
    public async Task Transfer_CreatesTwoLinkedTransactions()
    {
        var dbName = Guid.NewGuid().ToString();
        using var host = CreateHost(dbName);
        await host.StartAsync();
        try
        {
            int sourceId, targetId;
            await using (var seed = host.Services.CreateAsyncScope())
            {
                var db = seed.ServiceProvider.GetRequiredService<CoreDbContext>();
                var source = new Account
                {
                    OwnerId = 1, Balance = 300m, Currency = "RUB",
                    Status = AccountStatus.Open, CreatedAt = DateTimeOffset.UtcNow
                };
                var target = new Account
                {
                    OwnerId = 1, Balance = 0m, Currency = "RUB",
                    Status = AccountStatus.Open, CreatedAt = DateTimeOffset.UtcNow
                };
                db.Accounts.AddRange(source, target);
                await db.SaveChangesAsync();
                sourceId = source.Id;
                targetId = target.Id;
            }

            await using var scope = host.Services.CreateAsyncScope();
            var client = scope.ServiceProvider.GetRequiredService<IRequestClient<TransactionRequested>>();
            var response = await client.GetResponse<TransactionCompleted>(
                new TransactionRequested(
                    Guid.NewGuid(), sourceId, TransactionType.Transfer, 100m,
                    targetId, "Gift", null),
                CancellationToken.None);

            var sourceTx = response.Message.Transaction;
            sourceTx.Type.Should().Be(TransactionType.Transfer);
            sourceTx.Amount.Should().Be(100m);
            sourceTx.RelatedAccountId.Should().Be(targetId);
            sourceTx.BalanceBefore.Should().Be(300m);
            sourceTx.BalanceAfter.Should().Be(200m);

            await using var assertScope = host.Services.CreateAsyncScope();
            var assertDb = assertScope.ServiceProvider.GetRequiredService<CoreDbContext>();
            assertDb.Transactions.Should().HaveCount(2);
            var targetTx = assertDb.Transactions.Single(t => t.AccountId == targetId);
            targetTx.Amount.Should().Be(100m);
            targetTx.RelatedAccountId.Should().Be(sourceId);
        }
        finally
        {
            await host.StopAsync();
        }
    }
}
