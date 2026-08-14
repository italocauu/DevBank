package devbank.auth

import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm

/**
 * Emite e verifica os JWTs RS256 do login stateless.
 *
 * Emitir = elevar o hash das claims a `d` (privada) — só quem tem a privada emite.
 * Verificar = elevar a assinatura a `e` (pública) e comparar com o hash recalculado —
 * qualquer serviço com a pública confere, sem nunca precisar da privada.
 */
class JwtService {

    RsaKeyService rsaKeyService

    String issue(String username, List<String> roles, long ttlSeconds = 28800L) {
        Date now = new Date()
        Date expiration = new Date(now.time + ttlSeconds * 1000L)

        Jwts.builder()
                .setSubject(username)
                .claim('roles', roles)
                .setIssuedAt(now)
                .setExpiration(expiration)
                // algoritmo fixado explicitamente — nunca deixamos o token dizer como ele
                // quer ser verificado, essa é a lição do ataque "alg confusion"
                .signWith(rsaKeyService.privateKey, SignatureAlgorithm.RS256)
                .compact()
    }

    Map verify(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(rsaKeyService.publicKey)
                    .build()
                    .parseClaimsJws(token)
                    .body

            [valid: true, claims: claims]
        } catch (ExpiredJwtException e) {
            [valid: false, reason: 'expired', message: e.message]
        } catch (JwtException | IllegalArgumentException e) {
            [valid: false, reason: 'invalid', message: e.message]
        }
    }
}
