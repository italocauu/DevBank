package devbank.auth

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.GenericFilterBean

import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest

/**
 * Roda em toda request. Se vier "Authorization: Bearer <jwt>" e o JWT for válido
 * (assinatura RS256 confere com a chave pública, não expirou), autentica a request
 * ali mesmo — sem consultar sessão, sem cookie. É isto que torna o login stateless:
 * cada request prova quem é sozinha, carregando sua própria prova assinada.
 */
class JwtAuthenticationFilter extends GenericFilterBean {

    JwtService jwtService

    @Override
    void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request
        String header = httpRequest.getHeader('Authorization')

        if (header?.startsWith('Bearer ')) {
            String token = header.substring(7).trim()
            Map result = jwtService.verify(token)

            if (result.valid) {
                def claims = result.claims
                String username = claims.getSubject()
                List<String> roles = (claims.get('roles', List) ?: []) as List<String>
                def authorities = roles.collect { new SimpleGrantedAuthority(it) }

                def authentication = new UsernamePasswordAuthenticationToken(username, null, authorities)
                SecurityContextHolder.context.authentication = authentication
            } else {
                httpRequest.setAttribute('jwtError', result.reason)
            }
        }

        chain.doFilter(request, response)
    }
}
