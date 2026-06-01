package devbank

import grails.converters.JSON

class ContaCorrenteController {

    static responseFormats = ['json']

    def index() { 
        render ContaCorrente.list() as JSON
    }
    
    def show(Long id){
        def contaEncontrar = ContaCorrente.get(id)
        
        if(!contaEncontrar){
            render status: 404, text: "Conta não encontrada"
            return
        }
        render contaEncontrar as JSON
    }

    def save(){
        def newConta = new ContaCorrente(request.JSON)

        if(!newConta.save(flush:true)){
            respond newConta.errors, status: 422 // Entidade não foi entendida ou processada
            return
        }
        render newConta as JSON
    }
    
    def update(Long id){
        def contaExistente = ContaCorrente.get(id)

        if(!contaExistente){render status: 404; return}

        contaExistente.properties = request.JSON

        if(!contaExistente.save(flush:true)){
            respond contaExistente.errors, status: 422
            return
        }
        render contaExistente as JSON
    }

    def delete(Long id){
        def contaParaDeletar = ContaCorrente.get(id)

        if(!contaParaDeletar){render status: 404; return}

        contaParaDeletar.delete(flush:true)
        render status:204 // vázio
    }
}