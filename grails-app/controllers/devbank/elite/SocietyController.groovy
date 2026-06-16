package devbank

import grails.plugin.springsecurity.annotation.Secured

@Secured(['ROLE_DEV'])
class SocietyController {

    def index() { }
}
