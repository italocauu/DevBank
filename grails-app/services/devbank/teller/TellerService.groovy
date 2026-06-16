package devbank

import grails.gorm.transactions.Transactional
import devbank.auth.User
import devbank.teller.Teller
import org.hibernate.SessionFactory

@Transactional
class TellerService {

    SessionFactory sessionFactory
    def springSecurityService
        
    Deposito depositarDinheiro(def dados){
        
        Long idContaDestino = dados.id as Long
        BigDecimal valorDepositado = dados.valor as BigDecimal

        if(valorDepositado <= 0) throw new Exception("Transfira algum valor")

        // Verifica o usuário logado
        User usuarioLogado = springSecurityService.currentUser as User

        def resultado = sessionFactory.currentSession.createSQLQuery("""
            SELECT id FROM devbank_user
            WHERE username = :username
            AND class = 'devbank.teller.Teller'
        """)
        .setParameter("username", usuarioLogado.username)
        .uniqueResult()

        Teller tellerLogado = resultado ? Teller.load(resultado as Long) : null
        
        def contaExiste = sessionFactory.currentSession.createSQLQuery("""
            SELECT 1
            FROM conta_corrente
            WHERE id = :idBuscado        
        """)
        .setParameter("idBuscado", idContaDestino)
        .uniqueResult()

        if(!contaExiste) throw new Exception("Conta não encontrada")

        def linhasAfetadas = sessionFactory.currentSession.createSQLQuery("""
            UPDATE conta_corrente
            SET saldo = saldo + :valor
            WHERE id = :idBuscado
        """)
        .setParameter("valor", valorDepositado)
        .setParameter("idBuscado", idContaDestino)
        .executeUpdate()

        if(linhasAfetadas == 0) throw new Exception("Falha ao atualizar o saldo.")

        Long tellerIdParaSalvar = tellerLogado?.id ?: null

        def linhasComprovante = sessionFactory.currentSession.createSQLQuery("""
            INSERT INTO deposito (version, destino_id, valor, data_hora, teller_responsavel_id)
            VALUES (0, :destinoId, :valor, :dataHora, :tellerId)
        """)

        .setParameter("destinoId", idContaDestino)
        .setParameter("valor", valorDepositado)
        .setParameter("dataHora", new Date())
        .setParameter("tellerId", tellerIdParaSalvar, org.hibernate.type.LongType.INSTANCE)
        .executeUpdate()

        if(linhasComprovante == 0) throw new Exception("Falha ao registrar o comprovante")

        def comprovanteSQL = sessionFactory.currentSession.createSQLQuery("""
            SELECT Id, destino_id, valor, data_hora, teller_responsavel_id
            FROM deposito
            ORDER BY id DESC
            LIMIT 1
        """)
        .uniqueResult()

        Deposito comprovante = new Deposito(
            destino: ContaCorrente.load(idContaDestino),
            valor: valorDepositado,
            dataHora: new Date(),
            tellerResponsavel: tellerLogado
        )

        return comprovante

    }
}