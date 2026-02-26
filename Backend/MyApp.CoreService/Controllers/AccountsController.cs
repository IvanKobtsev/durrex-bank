using MediatR;
using Microsoft.AspNetCore.Mvc;
using MyApp.CoreService.Auth;
using MyApp.CoreService.DTOs.Responses;
using MyApp.CoreService.Features.Accounts.Commands.CloseAccount;
using MyApp.CoreService.Features.Accounts.Commands.CreateAccount;
using MyApp.CoreService.Features.Accounts.Queries.GetAccountById;
using MyApp.CoreService.Features.Accounts.Queries.GetAccountsByOwnerId;
using MyApp.CoreService.Features.Accounts.Queries.GetAllAccounts;
using MyApp.CoreService.Features.Transactions.Commands.Debit;
using MyApp.CoreService.Features.Transactions.Commands.Deposit;
using MyApp.CoreService.Features.Transactions.Commands.Transfer;
using MyApp.CoreService.Features.Transactions.Commands.Withdraw;
using MyApp.CoreService.Features.Transactions.Queries.GetTransactions;

namespace MyApp.CoreService.Controllers;

[ApiController]
[Route("api/accounts")]
[Produces("application/json")]
public class AccountsController : ControllerBase
{
    private readonly IMediator _mediator;
    private readonly ICurrentUserContext _user;

    public AccountsController(IMediator mediator, ICurrentUserContext user)
    {
        _mediator = mediator;
        _user = user;
    }

    /// <summary>
    /// Returns 403 if the caller is a Client and the account is not theirs; null otherwise.
    /// </summary>
    private IActionResult? EnforceClientOwnership(int accountOwnerId)
    {
        if (!_user.IsClient) return null;
        if (_user.UserId == accountOwnerId) return null;
        return StatusCode(StatusCodes.Status403Forbidden);
    }

    // POST /api/accounts
    [HttpPost]
    [ProducesResponseType<AccountResponse>(StatusCodes.Status201Created)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    public async Task<IActionResult> Create([FromBody] CreateAccountCommand cmd, CancellationToken ct)
    {
        if (_user.IsClient)
        {
            if (_user.UserId is null)
                return BadRequest(new { error = "X-User-Id header is missing or invalid." });
            cmd = cmd with { OwnerId = _user.UserId.Value };
        }
        var account = await _mediator.Send(cmd, ct);
        return CreatedAtAction(nameof(GetById), new { id = account.Id }, account);
    }

    // GET /api/accounts  OR  GET /api/accounts?ownerId=N
    [HttpGet]
    [ProducesResponseType<IReadOnlyList<AccountResponse>>(StatusCodes.Status200OK)]
    public async Task<IActionResult> GetAll([FromQuery] int? ownerId, CancellationToken ct)
    {
        if (_user.IsClient)
        {
            if (_user.UserId is null)
                return BadRequest(new { error = "X-User-Id header is missing or invalid." });
            var byOwner = await _mediator.Send(new GetAccountsByOwnerIdQuery(_user.UserId.Value), ct);
            return Ok(byOwner);
        }
        // Employee / Internal: honour optional ?ownerId filter
        if (ownerId.HasValue)
        {
            var byOwner = await _mediator.Send(new GetAccountsByOwnerIdQuery(ownerId.Value), ct);
            return Ok(byOwner);
        }
        var all = await _mediator.Send(new GetAllAccountsQuery(), ct);
        return Ok(all);
    }

    // GET /api/accounts/{id}
    [HttpGet("{id:int}")]
    [ProducesResponseType<AccountResponse>(StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status403Forbidden)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    public async Task<IActionResult> GetById(int id, CancellationToken ct)
    {
        var account = await _mediator.Send(new GetAccountByIdQuery(id), ct);
        var denied = EnforceClientOwnership(account.OwnerId);
        if (denied is not null) return denied;
        return Ok(account);
    }

    // DELETE /api/accounts/{id}
    [HttpDelete("{id:int}")]
    [ProducesResponseType<AccountResponse>(StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status403Forbidden)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    public async Task<IActionResult> Close(int id, CancellationToken ct)
    {
        if (_user.IsClient)
        {
            var account = await _mediator.Send(new GetAccountByIdQuery(id), ct);
            var denied = EnforceClientOwnership(account.OwnerId);
            if (denied is not null) return denied;
        }
        var result = await _mediator.Send(new CloseAccountCommand(id), ct);
        return Ok(result);
    }

    // POST /api/accounts/{id}/deposit
    [HttpPost("{id:int}/deposit")]
    [ProducesResponseType<TransactionResponse>(StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status403Forbidden)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    public async Task<IActionResult> Deposit(int id, [FromBody] DepositRequest req, CancellationToken ct)
    {
        if (_user.IsClient)
        {
            var account = await _mediator.Send(new GetAccountByIdQuery(id), ct);
            var denied = EnforceClientOwnership(account.OwnerId);
            if (denied is not null) return denied;
        }
        var tx = await _mediator.Send(new DepositCommand(id, req.Amount, req.Description), ct);
        return Ok(tx);
    }

    // POST /api/accounts/{id}/withdraw
    [HttpPost("{id:int}/withdraw")]
    [ProducesResponseType<TransactionResponse>(StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status403Forbidden)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    public async Task<IActionResult> Withdraw(int id, [FromBody] WithdrawRequest req, CancellationToken ct)
    {
        if (_user.IsClient)
        {
            var account = await _mediator.Send(new GetAccountByIdQuery(id), ct);
            var denied = EnforceClientOwnership(account.OwnerId);
            if (denied is not null) return denied;
        }
        var tx = await _mediator.Send(new WithdrawCommand(id, req.Amount, req.Description), ct);
        return Ok(tx);
    }

    // POST /api/accounts/{id}/transfer
    [HttpPost("{id:int}/transfer")]
    [ProducesResponseType<TransactionResponse>(StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status403Forbidden)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    public async Task<IActionResult> Transfer(int id, [FromBody] TransferRequest req, CancellationToken ct)
    {
        if (_user.IsClient)
        {
            var account = await _mediator.Send(new GetAccountByIdQuery(id), ct);
            var denied = EnforceClientOwnership(account.OwnerId);
            if (denied is not null) return denied;
        }
        var (source, _) = await _mediator.Send(new TransferCommand(id, req.TargetAccountId, req.Amount, req.Description), ct);
        return Ok(source);
    }

    // POST /api/accounts/{id}/debit  — internal, called by CreditService only
    [HttpPost("{id:int}/debit")]
    [ProducesResponseType<TransactionResponse>(StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status400BadRequest)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    public async Task<IActionResult> Debit(int id, [FromBody] DebitRequest req, CancellationToken ct)
    {
        var tx = await _mediator.Send(new DebitCommand(id, req.Amount, req.Description), ct);
        return Ok(tx);
    }

    // GET /api/accounts/{id}/transactions
    [HttpGet("{id:int}/transactions")]
    [ProducesResponseType<PagedResponse<TransactionResponse>>(StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status403Forbidden)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
    public async Task<IActionResult> GetTransactions(
        int id,
        [FromQuery] int page = 1,
        [FromQuery] int pageSize = 20,
        CancellationToken ct = default)
    {
        if (_user.IsClient)
        {
            var account = await _mediator.Send(new GetAccountByIdQuery(id), ct);
            var denied = EnforceClientOwnership(account.OwnerId);
            if (denied is not null) return denied;
        }
        var result = await _mediator.Send(new GetTransactionsQuery(id, page, pageSize), ct);
        return Ok(result);
    }

}

// Request body records (used only in controller, no separate DTO file needed)
public record DepositRequest(decimal Amount, string? Description = null);
public record WithdrawRequest(decimal Amount, string? Description = null);
public record TransferRequest(int TargetAccountId, decimal Amount, string? Description = null);
public record DebitRequest(decimal Amount, string? Description = null);
