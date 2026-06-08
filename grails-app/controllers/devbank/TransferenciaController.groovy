package devbank

import grails.converters.JSON
import devbank.dto.TransferenciaDTO

class TransferenciaController {

static responseFormats = ['json']

TransferenciaService transferenciaService

def index() {
    try{
        def listarTransferencias = Transferencia.list().collect{conta ->
        TransferenciaDTO.formatarTransferencia(conta)
        }

        render listarTransferencias as JSON
    } catch (Exception e){
        render status: 422
    }
    }


def show (Long id){
    def comprovante = Transferencia.get(id)
    if(!comprovante){
        render status: 404, text:"Transferencia não localizada"
        return
    }
    def dto = TransferenciaDTO.formatarTransferencia(comprovante) as JSON
    render dto.properties as JSON
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
        def dto = TransferenciaDTO.formatarTransferencia(novaTransferencia)
        render dto as JSON
    } catch (Exception e){
        render status: 422, text: e.getMessage()
    }

}
}

//
//    def update(Long id){
//        def newTransferencia = Transferencia.get(id)
//        if(!newTransferencia){render status: 404; return}
//
//        newTransferencia.properties = request.JSON
//
//      if(!newTransferencia.save(flush: true)){
//            respond newTransferencia.errors, status: 422
//        }
//       render formatarTransferencia(newTransferencia) as JSON
//    }
//}



//    Tarefas a fazer:
// Trazer o formatarTransferencia para um DTO da transferencia
// Alterar o save e a lógica, tirar daqui para o service
//  
//
