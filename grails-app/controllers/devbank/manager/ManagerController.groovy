package devbank

import grails.plugin.springsecurity.annotation.Secured

@Secured(['ROLE_DEV', 'ROLE_MANAGER'])
class ManagerController {

}
