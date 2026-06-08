package devbank.dto 

import devbank.Transferencia

class TransferenciaDTO{
        private Map formatarTransferencia(Transferencia transformar){
        return[
            id: transformar.id,
            valor: transformar.valor,
            data: transformar.dataHora?.format('dd/MM/yyyy HH:mm:ss'),
            de: "Conta ${transformar.origem.id} - ${transformar.origem.titular}",
            para: "Conta ${transformar.destino.id} - ${transformar.destino.titular}"
        ]
    }
}