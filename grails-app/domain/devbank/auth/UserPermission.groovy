package devbank

import grails.gorm.DetachedCriteria
import groovy.transform.ToString

import org.codehaus.groovy.util.HashCodeHelper
import grails.compiler.GrailsCompileStatic

// Parte relacionada a ligação entre eles
@GrailsCompileStatic
@ToString(cache=true, includeNames=true, includePackage=false)
class UserPermission implements Serializable {

	private static final long serialVersionUID = 1

	User user
	Permission permission

	@Override
	boolean equals(other) {
		if (other instanceof UserPermission) {
			other.userId == user?.id && other.permissionId == permission?.id
		}
	}

    @Override
	int hashCode() {
	    int hashCode = HashCodeHelper.initHash()
        if (user) {
            hashCode = HashCodeHelper.updateHash(hashCode, user.id)
		}
		if (permission) {
		    hashCode = HashCodeHelper.updateHash(hashCode, permission.id)
		}
		hashCode
	}

	static UserPermission get(long userId, long permissionId) {
		criteriaFor(userId, permissionId).get()
	}

	static boolean exists(long userId, long permissionId) {
		criteriaFor(userId, permissionId).count()
	}

	private static DetachedCriteria criteriaFor(long userId, long permissionId) {
		UserPermission.where {
			user == User.load(userId) &&
			permission == Permission.load(permissionId)
		}
	}

	static UserPermission create(User user, Permission permission, boolean flush = false) {
		def instance = new UserPermission(user: user, permission: permission)
		instance.save(flush: flush)
		instance
	}

	static boolean remove(User u, Permission r) {
		if (u != null && r != null) {
			UserPermission.where { user == u && permission == r }.deleteAll()
		}
	}

	static int removeAll(User u) {
		u == null ? 0 : UserPermission.where { user == u }.deleteAll() as int
	}

	static int removeAll(Permission r) {
		r == null ? 0 : UserPermission.where { permission == r }.deleteAll() as int
	}

	static constraints = {
	    user nullable: false
		permission nullable: false, validator: { Permission r, UserPermission ur ->
			if (ur.user?.id) {
				if (UserPermission.exists(ur.user.id, r.id)) {
				    return ['userRole.exists']
				}
			}
		}
	}

	static mapping = {
		id composite: ['user', 'permission']
		version false
	}
}
