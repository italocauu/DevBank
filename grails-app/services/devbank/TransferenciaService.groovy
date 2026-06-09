package devbank

import grails.gorm.transactions.Transactional


class TransferenciaService {

    @Transactional
    Transferencia realizarTransferencia(def dados){
        
        String origem = dados.chavePixOrigem
        String destino = dados.chavePixDestino

        if(!isChavePixExiste(origem)){
            return [sucesso: false, mensagem: "Conta pix de origem não existe."]
        }

        if(!isChavePixExiste(destino)){
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

    // Checagem pelo slq

    def isChavePixExiste(String chavePixBuscada){
        def query{"""
            SELECT 1
            FROM ContaCorrente
            WHERE chavePixBuscada = :pixBuscado;
        """
        def resultado = session.Factory.currentSession
            .createSQLQuery(query)
            .setParameter("pixBuscado", chavePixBuscada)
            .list()

        return resultado ? true: false
        }
    }


    //      Tarefas
    // Levar a lógica de salvamento e manipulação para aqui
    // 
}