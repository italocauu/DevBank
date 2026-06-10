package devbank

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import grails.compiler.GrailsCompileStatic

// Parte relacionada a permissões
@GrailsCompileStatic
@EqualsAndHashCode(includes='authority')
@ToString(includes='authority', includeNames=true, includePackage=false)
class Permission implements Serializable {

	private static final long serialVersionUID = 1

	// Valor do @Secured nos controllers
	String authority

	static constraints = {
		authority nullable: false, blank: false, unique: true
	}

	static mapping = {
		cache true // Permissões raramente mudam, vale cachear
	}
}
