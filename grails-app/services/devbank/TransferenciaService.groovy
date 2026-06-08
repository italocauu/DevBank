package devbank

import grails.gorm.transactions.Transactional


class TransferenciaService {

@Transactional
Transferencia realizarTransferencia(def dados){

    ContaCorrente origem = ContaCorrente.findByChavePix(dados.chavePixOrigem)
    ContaCorrente destino = ContaCorrente.findByChavePix(dados.chavePixDestino)

    if(!origem) throw new Exception("Chave PIX de origem inválida")
    if(!destino) throw new Exception("Chave PIX de destino inválida")

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
    
    /*      Tarefas
    // Levar essa lógica para Java
    // Levar a lógica de salvamento e manipulação para aqui
    */ 
}