using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace MyApp.CoreService.Migrations
{
    /// <inheritdoc />
    public partial class AddTransactionCurrencyFields : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.AddColumn<decimal>(
                name: "exchange_rate",
                table: "transactions",
                type: "numeric(18,6)",
                nullable: true);

            migrationBuilder.AddColumn<string>(
                name: "source_currency",
                table: "transactions",
                type: "character varying(3)",
                maxLength: 3,
                nullable: true);
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropColumn(
                name: "exchange_rate",
                table: "transactions");

            migrationBuilder.DropColumn(
                name: "source_currency",
                table: "transactions");
        }
    }
}
