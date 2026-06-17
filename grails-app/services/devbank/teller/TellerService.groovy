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
        
        // Criando id e valor de depósito
        Long idContaDestino = dados.id as Long
        BigDecimal valorDepositado = dados.valor as BigDecimal

        // Evitar gasto desnecessário
        if(valorDepositado <= 0) throw new Exception("Transfira algum valor")

        // Verifica o usuário logado
        User usuarioLogado = springSecurityService.currentUser as User

        // Procura a classe espécifica dentro da tabela, já que é uma entidade que herda
        def resultado = sessionFactory.currentSession.createSQLQuery("""
            SELECT id
            FROM devbank_user
            WHERE username = :username
            AND class = 'devbank.teller.Teller'
        """)
        .setParameter("username", usuarioLogado.username)
        .uniqueResult()

        Teller tellerLogado = resultado ? Teller.load(resultado as Long) : null
        
        /*
        // Busca a primeira conta que bata com o id da conta destino
        def contaExiste = sessionFactory.currentSession.createSQLQuery("""
            SELECT 1
            FROM conta_corrente
            WHERE id = :idBuscado        
        """)
        .setParameter("idBuscado", idContaDestino)
        .uniqueResult()
        // Repetir duas mesmas buscas lentifica o sistema
        // 
        //if(!contaExiste) throw new Exception("Conta não encontrada")
        */

        // Atualiza os valor da tabela baseado no depósito, e da retorno de que deu certo
        // Baseado nas linhas afetadas
        /* 
        def linhasAfetadas = sessionFactory.currentSession.createSQLQuery("""
            UPDATE conta_corrente
            SET saldo = saldo + :valor
            WHERE id = :idBuscado
        """)
        .setParameter("valor", valorDepositado)
        .setParameter("idBuscado", idContaDestino)
        .executeUpdate()
        // REMOVIDO POR SEGURANÇA
        // Verifica linhas afetadas para debug
        // if(linhasAfetadas == 0) throw new Exception("Falha ao atualizar o saldo.")
        */ 

        ContaCorrente conta = ContaCorrente.get(idContaDestino)
        if(!conta) throw new Exception("Conta de destino não encontrada")

        conta.saldo += valorDepositado

        if(!conta.save(flush: true)) throw new Exception("Falha ao atualizar o saldo: ${conta.errors}")


        // Garante o retorno de algo, mesmo que seja nulo.
        Long tellerIdParaSalvar = tellerLogado?.id ?: null

        Date dataDoDeposito = new Date()

        // Puxa os valores do banco para comprovante
        def linhasComprovante = sessionFactory.currentSession.createSQLQuery("""
            INSERT INTO deposito (version, destino_id, valor, data_hora, teller_responsavel_id)
            VALUES (0, :destinoId, :valor, :dataHora, :tellerId)
        """)

        .setParameter("destinoId", idContaDestino)
        .setParameter("valor", valorDepositado)
        .setParameter("dataHora", new Date())
        .setParameter("tellerId", tellerIdParaSalvar, org.hibernate.type.LongType.INSTANCE)
        .executeUpdate()

        // Faz a mesma verificação
        if(linhasComprovante == 0) throw new Exception("Falha ao registrar o comprovante")

        def comprovanteSQL = sessionFactory.currentSession.createSQLQuery("""
            SELECT Id, destino_id, valor, data_hora, teller_responsavel_id
            FROM deposito
            ORDER BY id DESC
            LIMIT 1
        """)  
        .uniqueResult()

        Deposito comprovante = new Deposito(
            destino: conta,
            valor: valorDepositado,
            dataHora: dataDoDeposito,
            tellerResponsavel: tellerLogado
        )

        return comprovante

    }
}