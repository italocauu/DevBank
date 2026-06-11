package devbank

import devbank.auth.User
import devbank.auth.Permission
import devbank.auth.UserPermission
import devbank.client.Client
import devbank.manager.Manager
import devbank.manager.Teller
import grails.gorm.transactions.Transactional
import org.hibernate.SessionFactory
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

SessionFactory SessionFactory

@Transactional
class MasterService {


    // Verifica username
    boolean usernameIsExist(String username){
        def session = sessionFactory.currentSession

        def textoSql = """
            SELECT 1
            FROM devbank_user
            WHERE username = :usernameBuscado
        """
        def resultado = session.createNativeQuery(textoSql)
        .setParameter("usernameBuscado", username)
        .setMaxResults(1)
        

        return resultado.isEmpty()
    }

    boolean cpfIsExist(String cpf){
        def session = sessionFactory.currentSession
        
        def textoSql = """
            SELECT 1
            FROM conta_corrente
            WHERE cpf = :cpf
            """
        def resultado = session.createNativeQuery(textoSql)
        .setParameter("cpf", cpf)
        .setMaxResults(1)

        return resultado.isEmpty()
    }

    Map finderUser(String username){
        def session = sessionFactory.currentSession

        def textoSql = """ 
            SELECT id, username, password, enabled
            FROM devbank_user
            WHERE username = :username
    """
        def resposta = session.createNativeQuery(textoSql)
        .setParameter('username', username)
        resposta.FirstRow
    }

    // Achar o username, verificar na classe que vai servir como metódo para criação de usuários

}
