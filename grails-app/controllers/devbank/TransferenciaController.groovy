package devbank

import grails.converters.JSON

class TransferenciaController {

    static responseFormats = ['json']

    TransferenciaService transferenciaService
    
    private Map formatarTransferencia(Transferencia transformar){
        return[
            id: transformar.id,
            valor: transformar.valor,
            data: transformar.dataHora?.format('dd/MM/yyyy HH:mm:ss'),
            de: "Conta ${transformar.origem.id} - ${transformar.origem.titular}",
            para: "Conta ${transformar.destino.id} - ${transformar.destino.titular}"
        ]
    }

    def index() {
        def listarTransferencias = Transferencia.list().collect {formatarTransferencia(it)}
        render listarTransferencias as JSON
     }
    

    def show (Long id){
        def comprovante = Transferencia.get(id)
        if(!comprovante){
            render status: 404, text:"Transferencia não localizada"
            return
        }
        render formatarTransferencia(comprovante) as JSON
    }

    /* 
    def save(){
        try{
        def newTransferencia = new Transferencia(request.JSON)
        if(!newTransferencia.save(flush:true)){
            respond conta.errors, status:422
            return
        }
        render newTransferencia, status: 201
        */

    def save(){
        try{
            def novaTransferencia = transferenciaService.realizarTransferencia(request.JSON)
            render formatarTransferencia(novaTransferencia) as JSON
        } catch (Exception e){
            render status: 422, text: e.getMessage()
        }

    }


    
    def update(Long id){
        def newTransferencia = Transferencia.get(id)
        if(!newTransferencia){render status: 404; return}

        newTransferencia.properties = request.JSON

        if(!newTransferencia.save(flush: true)){
            respond newTransferencia.errors, status: 422
        }
        render formatarTransferencia(newTransferencia) as JSON
    }
}
