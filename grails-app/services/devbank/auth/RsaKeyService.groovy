package devbank.auth

import java.security.KeyFactory
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec

/**
 * Dono do par de chaves RSA usado para assinar (privada) e verificar (pública) os JWTs.
 *
 * Em desenvolvimento, se não achar as chaves no disco, gera um par novo de 2048 bits e
 * salva em PEM. Em produção, gere as chaves uma vez com openssl e aponte os caminhos via
 * env vars (DEVBANK_JWT_PRIVATEKEYPATH / DEVBANK_JWT_PUBLICKEYPATH) — nunca deixe a
 * privada ser gerada automaticamente ali, ou cada restart invalida todos os tokens.
 */
class RsaKeyService {

    def grailsApplication

    private PrivateKey privateKey
    private PublicKey publicKey
    private String privateKeyPath
    private String publicKeyPath

    PrivateKey getPrivateKey() {
        init()
        privateKey
    }

    PublicKey getPublicKey() {
        init()
        publicKey
    }

    String getPublicKeyPem() {
        init()
        toPem('PUBLIC KEY', publicKey.encoded)
    }

    private synchronized void init() {
        if (privateKey && publicKey) {
            return
        }

        privateKeyPath = grailsApplication.config.getProperty('devbank.jwt.privateKeyPath', String, 'keys/jwt_private_key.pem')
        publicKeyPath = grailsApplication.config.getProperty('devbank.jwt.publicKeyPath', String, 'keys/jwt_public_key.pem')

        File privFile = new File(privateKeyPath)
        File pubFile = new File(publicKeyPath)

        if (privFile.exists() && pubFile.exists()) {
            privateKey = readPrivateKey(privFile)
            publicKey = readPublicKey(pubFile)
            log.info "RSA: par de chaves carregado de ${privFile.absolutePath}"
        } else {
            log.warn "RSA: nenhuma chave encontrada em ${privFile.absolutePath} — gerando um par novo de 2048 bits (uso em DESENVOLVIMENTO)."
            KeyPairGenerator generator = KeyPairGenerator.getInstance('RSA')
            generator.initialize(2048)
            KeyPair pair = generator.generateKeyPair()
            privateKey = pair.private
            publicKey = pair.public
            writePem(privFile, 'PRIVATE KEY', privateKey.encoded)
            writePem(pubFile, 'PUBLIC KEY', publicKey.encoded)
            log.warn "RSA: par salvo em ${privFile.absolutePath} e ${pubFile.absolutePath}. Fora do git (.gitignore já cobre keys/)."
        }
    }

    private static PrivateKey readPrivateKey(File file) {
        byte[] der = decodePem(file.text)
        KeyFactory.getInstance('RSA').generatePrivate(new PKCS8EncodedKeySpec(der))
    }

    private static PublicKey readPublicKey(File file) {
        byte[] der = decodePem(file.text)
        KeyFactory.getInstance('RSA').generatePublic(new X509EncodedKeySpec(der))
    }

    private static byte[] decodePem(String pem) {
        String base64 = pem.replaceAll(/-----(BEGIN|END) [A-Z ]+-----/, '').replaceAll(/\s/, '')
        Base64.getDecoder().decode(base64)
    }

    private static void writePem(File file, String label, byte[] der) {
        file.parentFile?.mkdirs()
        file.text = toPem(label, der)
    }

    private static String toPem(String label, byte[] der) {
        String base64 = Base64.getEncoder().encodeToString(der)
        String wrapped = base64.replaceAll(/(.{64})/, '$1\n')
        "-----BEGIN ${label}-----\n${wrapped}\n-----END ${label}-----\n"
    }
}
