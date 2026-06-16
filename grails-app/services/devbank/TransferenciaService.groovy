package devbank

import grails.gorm.transactions.Transactional
import org.hibernate.SessionFactory

@Transactional
class TransferenciaService {
    SessionFactory sessionFactory

    Transferencia realizarTransferencia(def dados){
        

        ContaCorrente origem = buscarContaPorChavePix(dados.chavePixOrigem)
        ContaCorrente destino = buscarContaPorChavePix(dados.chavePixDestino)   

        if(!origem){
            return [sucesso: false, mensagem: "Conta pix de origem não existe."]
        }

        if(!destino){
            return [sucesso: false, mensagem: "Chave pix de destino não existe."]
        }

        BigDecimal valor = dados.valor as BigDecimal

        if(valor <= 0) throw new Exception("Transfira algum valor")
        if(origem.saldo < valor) throw new Exception("Saldo insuficiente")  
        if(origem.id == destino.id) throw new Exception("Não é permitido transferencias para a mesma conta")

        origem.saldo -= valor
        destino.saldo += valor

        if(!origem.save(flush:true)) {
            throw new Exception("Falha ao debitar a conta de origem: ${origem.errors}")
        }

        if(!destino.save(flush:true)) {
            throw new Exception("Falha ao creditar a conta de destino: ${destino.errors}")
        }

        Transferencia transferencia = new Transferencia(
            origem : origem,
            destino: destino,
            valor: valor,
            dataHora: new Date()
        )

        if(!transferencia.save(flush:true)){
            throw new Exception("Falha ao realizar a transferência: ${transferencia.errors}")
        }
        return transferencia

    }

    // Checagem pelo sql

        ContaCorrente buscarContaPorChavePix(String chavePix) {
        def resultado = sessionFactory.currentSession.createSQLQuery("""
            SELECT id FROM conta_corrente
            WHERE chave_pix = :chavePix
        """)
        .setParameter("chavePix", chavePix)
        .uniqueResult()

        return resultado ? ContaCorrente.get(resultado as Long) : null
    }
}


    //      Tarefas
    // Levar a lógica de salvamento e manipulação para aqui
    // 