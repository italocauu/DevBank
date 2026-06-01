package devbank

import grails.converters.JSON

class TransferenciaController {

    static responseFormats = ['json']

    def TransferenciaService
    
    def index() {
        render Transferencia.list() as JSON
     }
    
    def show (Long id){
        def comprovante = Transferencia.get(id)
        if(!comprovante){
            render status: 404, text:"Transferencia não localizada"
        }
        render comprovante as JSON
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
            render novaTransferencia as JSON
        } catch (Exception e){
            render status: 422, text: e.getMessage()
        }

    }


    
    def update(Long id){
        def newTransferencia = Transferencia.get(id)
        if(!newTransferencia){render status: 404; return}

        conta.properties = request.JSON
        if(!newTransferencia.save(flush: true)){
            respond newTransferencia.erros, status: 422
        }
        render newTransferencia as JSON
    }
}
