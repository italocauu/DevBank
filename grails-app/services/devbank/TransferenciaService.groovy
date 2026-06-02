package devbank

import grails.gorm.transactions.Transactional


class TransferenciaService {

@Transactional
Transferencia realizarTransferencia(def dados){

    ContaCorrente origem = ContaCorrente.get(dados.origemId as Long)
    ContaCorrente destino = ContaCorrente.get(dados.destinoId as Long)

    if(!origem) throw new Exception("Conta de origem não encontrada")
    if(!destino) throw new Exception("Conta de destino não encontrada")

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
    
}