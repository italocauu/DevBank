package devbank

import devbank.auth.User
import devbank.auth.Permission
import devbank.auth.UserPermission
import devbank.client.Client
import devbank.teller.Teller
import devbank.manager.Manager
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import groovy.json.JsonSlurper
import grails.util.Environment
import grails.plugin.springsecurity.SecurityFilterPosition
import grails.plugin.springsecurity.SpringSecurityUtils

class BootStrap {

    def init = { servletContext ->
        // Encaixa o jwtAuthenticationFilter na cadeia do Spring Security. Só funciona
        // aqui (runtime, depois que a cadeia já foi montada) — é a forma documentada
        // pelo próprio plugin: SpringSecurityUtils.clientRegisterFilter muta em memória
        // a lista de filtros já construída, então precisa do contexto totalmente de pé.
        SpringSecurityUtils.clientRegisterFilter(
                'jwtAuthenticationFilter',
                SecurityFilterPosition.PRE_AUTH_FILTER.order
        )

        // Criando as permissões no banco
        def permClient = Permission.findOrSaveWhere(authority: 'ROLE_CLIENT')
        def permTeller = Permission.findOrSaveWhere(authority: 'ROLE_TELLER')
        def permManager = Permission.findOrSaveWhere(authority: 'ROLE_MANAGER')
        def permDev  = Permission.findOrSaveWhere(authority: 'ROLE_DEV')
        
        // def encoder = new BCryptPasswordEncoder()
 
        println "=== BOOTSTRAP RODANDO ==="


        String devUsername = System.getenv('DEV_USERNAME')
        String devPassword = System.getenv('DEV_PASSWORD')

        if(devUsername && devPassword){

        if (!User.findByUsername(devUsername)) {
            def user = new User(
            username: devUsername,
            password: devPassword,
            enabled: true
        )

        if (!user.save(flush: true)) {
            println "ERROS: ${user.errors.allErrors}"
        } else {
            println "USUARIO CRIADO COM SUCESSO"
            UserPermission.create(user, permDev, true)
            }
        } else {
            println "SUPERUSUÁRIO DEV '${devUsername}' JÁ EXISTE NO BANCO."
        }
    } else {
        println "SUPER USUÁRIO DEV NÃO CONFIGURADO NO SISTEMA."
    }

    println "=== BOOTSTRAP CONCLUÍDO ==="


    }

    def destroy = {}

}