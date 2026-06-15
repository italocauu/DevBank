package devbank

import devbank.auth.User
import devbank.auth.Permission
import devbank.auth.UserPermission
import devbank.client.Client
import devbank.teller.Teller
import devbank.manager.Manager
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

class BootStrap {

    def init = { servletContext ->
        // Criando as permissões no banco
        def permClient = Permission.findOrSaveWhere(authority: 'ROLE_CLIENT')
        def permTeller = Permission.findOrSaveWhere(authority: 'ROLE_TELLER')
        def permManager = Permission.findOrSaveWhere(authority: 'ROLE_MANAGER')
        def permDev  = Permission.findOrSaveWhere(authority: 'ROLE_DEV')
        
        def encoder = new BCryptPasswordEncoder()
 
        println "=== BOOTSTRAP RODANDO ==="

        def gitCrypto = System.getenv('DEV_USERNAME')
        if (!User.findByUsername(gitCrypto)) {
            def user = new User(
            username: System.getenv('DEV_USERNAME'),
            password: System.getenv('DEV_PASSWORD'),
            enabled: true
        )

        if (!user.save(flush: true)) {
            println "ERROS: ${user.errors.allErrors}"
        } else {
            println "USUARIO CRIADO COM SUCESSO"
            UserPermission.create(user, permDev, true)
            }
        }

    def destroy = {}

    }
}