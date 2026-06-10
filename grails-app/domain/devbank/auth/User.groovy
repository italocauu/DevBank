package devbank.auth

import devbank.auth.Permission
import devbank.auth.UserPermission

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
// import grails.compiler.GrailsCompileStatic

// Parte relacionada a autenticação
// @GrailsCompileStatic
@EqualsAndHashCode(includes='username')
@ToString(includes='username', includeNames=true, includePackage=false)
class User implements Serializable {

    private static final long serialVersionUID = 1

    // Autenticação
    String username
    String password
    
    // Controle de acesso
    boolean enabled = true  // conta ativa
    boolean accountExpired  // contrato vencido
    boolean accountLocked   // bloqueado por fraude/tentativas
    boolean passwordExpired // senha precisa ser trocada

    // Spring Security chama esse método para saber o que o usuário pode fazer
    Set<Permission> getAuthorities() {
        (UserPermission.findAllByUser(this) as List<UserPermission>)*.permission as Set<Permission>
    }

    static constraints = {
        password nullable: false, blank: false, password: true
        username nullable: false, blank: false, unique: true
    }

    static mapping = {
	    password column: '`password`'
    }
}
