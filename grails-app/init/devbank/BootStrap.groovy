import devbank.auth.Permission
import devbank.auth.UserPermission
import devbank.client.Client
import devbank.teller.Teller
import devbank.manager.Manager

class BootStrap {

    def init = { servletContext ->

        // Criando as permissões no banco
        def permClient = Permission.findOrSaveWhere(authority: 'ROLE_CLIENT')
        def permTeller = Permission.findOrSaveWhere(authority: 'ROLE_TELLER')
        def permManager = Permission.findOrSaveWhere(authority: 'ROLE_MANAGER'


        )
    }

    def destroy = {}
}