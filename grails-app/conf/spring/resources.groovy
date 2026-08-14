import devbank.UserPasswordEncoderListener
import devbank.auth.JwtAuthenticationFilter
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

// Place your Spring DSL code here
// (o registro do jwtAuthenticationFilter na cadeia do Spring Security é feito em
// BootStrap.groovy via SpringSecurityUtils.clientRegisterFilter — é a forma
// documentada de fazer isso em runtime, depois que a cadeia já foi montada)
beans = {
    userPasswordEncoderListener(UserPasswordEncoderListener)

    jwtAuthenticationFilter(JwtAuthenticationFilter) {
        jwtService = ref('jwtService')
    }
}
