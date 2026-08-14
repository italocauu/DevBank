package devbank.auth

import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.DisabledException
import org.springframework.security.authentication.LockedException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder

/**
 * Login stateless: em vez de criar sessão, devolve um JWT assinado com a chave
 * privada RSA. Toda request seguinte prova quem é sozinha, via
 * "Authorization: Bearer <token>" — sem cookie, sem servidor guardando estado.
 *
 * permitAll: /api/** exige autenticação por padrão neste projeto mesmo sem
 * @Secured — e login/publicKey/whoAmI são justamente as portas de entrada antes
 * de existir qualquer token, então precisam ficar abertas explicitamente.
 */
@Secured(['permitAll'])
class AuthController {

    AuthenticationManager authenticationManager
    JwtService jwtService
    RsaKeyService rsaKeyService

    def login() {
        def body = request.JSON
        String username = body?.username
        String password = body?.password

        if (!username || !password) {
            renderJson(400, [error: 'username e password são obrigatórios'])
            return
        }

        try {
            Authentication authRequest = new UsernamePasswordAuthenticationToken(username, password)
            Authentication authResult = authenticationManager.authenticate(authRequest)

            List<String> roles = authResult.authorities.collect { GrantedAuthority a -> a.authority }
            String token = jwtService.issue(username, roles)

            render([token: token, tokenType: 'Bearer', expiresIn: 28800, roles: roles] as JSON)
        } catch (BadCredentialsException ignored) {
            renderJson(401, [error: 'credenciais inválidas'])
        } catch (DisabledException | LockedException ignored) {
            renderJson(403, [error: 'conta desabilitada ou bloqueada'])
        }
    }

    // render(status:, contentType:){ map } NÃO serializa — a forma com closure é pro
    // builder de markup, e ele ignora o valor de retorno do closure. Pra status
    // diferente de 200 precisa montar o texto já convertido pra JSON manualmente.
    private void renderJson(int status, Map body) {
        render(status: status, contentType: 'application/json', text: (body as JSON) as String)
    }

    // A chave pública é pra ser distribuída — qualquer serviço que só precisa
    // *verificar* tokens pode buscar ela aqui, sem nunca tocar na privada.
    def publicKey() {
        render(contentType: 'text/plain', text: rsaKeyService.publicKeyPem)
    }

    // Mostra exatamente o que o jwtAuthenticationFilter colocou (ou não) no
    // SecurityContext desta request — o jeito mais direto de observar o mecanismo.
    def whoAmI() {
        def auth = SecurityContextHolder.context.authentication
        boolean authenticated = auth?.authenticated && !(auth instanceof AnonymousAuthenticationToken)

        render([
                authenticated: authenticated,
                username      : authenticated ? auth.name : null,
                authorities   : authenticated ? auth.authorities*.authority : [],
                jwtError      : request.getAttribute('jwtError')
        ] as JSON)
    }
}
