using System.Security.Cryptography;
using Microsoft.IdentityModel.Tokens;

namespace MyApp.UserService.Infrastructure;

public class RsaKeyProvider
{
    private readonly RsaSecurityKey _privateKey;
    private readonly RsaSecurityKey _publicKey;
    private readonly string _publicKeyPem;

    public RsaKeyProvider(IConfiguration configuration)
    {
        var keysPath = configuration["Jwt:KeysPath"] ?? "keys";
        Directory.CreateDirectory(keysPath);

        var privatePath = Path.Combine(keysPath, "private.pem");
        var publicPath = Path.Combine(keysPath, "public.pem");

        RSA rsa;

        if (File.Exists(privatePath) && File.Exists(publicPath))
        {
            rsa = RSA.Create();
            rsa.ImportFromPem(File.ReadAllText(privatePath));
        }
        else
        {
            rsa = RSA.Create(2048);
            File.WriteAllText(privatePath, rsa.ExportRSAPrivateKeyPem());
            File.WriteAllText(publicPath, rsa.ExportSubjectPublicKeyInfoPem());
        }

        _privateKey = new RsaSecurityKey(rsa);
        _publicKeyPem = File.Exists(publicPath)
            ? File.ReadAllText(publicPath)
            : rsa.ExportSubjectPublicKeyInfoPem();

        var rsaPublic = RSA.Create();
        rsaPublic.ImportFromPem(_publicKeyPem);
        _publicKey = new RsaSecurityKey(rsaPublic);
    }

    public RsaSecurityKey PrivateKey => _privateKey;
    public RsaSecurityKey PublicKey => _publicKey;
    public string PublicKeyPem => _publicKeyPem;
}
