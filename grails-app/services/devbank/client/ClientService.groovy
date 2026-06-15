package devbank

import grails.gorm.transactions.Transactional
import org.hibernate.SessionFactory

@Transactional
class ClientService {

    SessionFactory sessionFactory

    def acharConta(String esseId){
        def query = """
        SELECT *
        FROM conta_corrente
        WHERE id = CAST(? as bigint)
        """

        def resultado = sessionFactory.currentSession
            .createSQLQuery(query)
            .setParameter(0, esseId)
            .list()

        return resultado
    }
}
