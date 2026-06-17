package devbank

import grails.gorm.transactions.Transactional
import org.hibernate.SessionFactory 
import org.hibernate.transform.AliasToEntityMapResultTransformer
// Para criar caixas:
import devbank.teller.Teller
import devbank.auth.User
import devbank.auth.Permission
import devbank.auth.UserPermission

@Transactional
class ManagerService {

    SessionFactory sessionFactory

    def listarCaixas(int numeroDaPagina, int limitePorPagina){

        int saltoOffset = (numeroDaPagina - 1) * limitePorPagina

        String sqlPaginado = """
            SELECT id, username
            FROM devbank_user
            WHERE class = 'devbank.teller.Teller'
            ORDER BY id ASC 
            LIMIT :limite OFFSET :salto
        """

        def listaDeCaixas = sessionFactory.currentSession.createSQLQuery(sqlPaginado)
            .setParameter("limite", limitePorPagina)
            .setParameter("salto", saltoOffset)
            .setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE) // Mapea os dados recebido em JSON
            .list()
        
        return listaDeCaixas
    }
    boolean isUsernameExist(String username){
        def sql = """
            SELECT 1
            FROM devbank_user
            WHERE username = :username
        """
        def resultado = sessionFactory.currentSession.createSQLQuery(sql)
        .setParameter("username", username)
        .uniqueResult()

        return resultado != null    
    }

    def criarCaixa(String username, String password, String registro, String agencia){
        if(isUsernameExist(username)) throw new Exception("Já existe um usuário cadastrado com o nome '${username}'")

        def novoCaixa = new Teller(
            username: username,
            password: password,
            registro: registro,
            agencia: agencia,
            enabled: true
        )

        if(!novoCaixa.save(flush:true)) throw new Exception("Erro de validação: " + novoCaixa.errors.allErrors)

        def permTeller = Permission.findByAuthority('ROLE_TELLER')
        if(!permTeller) throw new Exception("Erro critico: Permissão ROLE_TELLER não existe")

        UserPermission.create(novoCaixa, permTeller, true)

        return [
            id: novoCaixa.id,
            username: novoCaixa.username,
            registro: novoCaixa.registro,
            agencia: novoCaixa.agencia,
            status: "Caixa criado com sucesso",
            permissao: "ROLE_TELLER"
        ]

    }
}
