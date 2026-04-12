using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace MyApp.CoreService.Migrations
{
    /// <inheritdoc />
    public partial class AddedFirebaseToken : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.CreateTable(
                name: "FirebaseTokens",
                columns: table => new
                {
                    Token = table.Column<string>(type: "text", nullable: false),
                    UserId = table.Column<int>(type: "integer", nullable: false),
                    Role = table.Column<int>(type: "integer", nullable: false),
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_FirebaseTokens", x => x.Token);
                }
            );
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropTable(name: "FirebaseTokens");
        }
    }
}
