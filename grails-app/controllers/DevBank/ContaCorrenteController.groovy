package devbank

class ContaCorrenteController {

    static respondeFormats = ['json']
    def index() { 
        respond ContaCorrente.list
    }
    
    def show(Long id){
        def contaMostrarUma = ContaCorrente.get(id)
        if(!contaMostrarUma){
            responde contaMostrarUma status: 404, text: "Conta não encontrada"
            return
        }
        respond conta
    }

    def save(){
        def newConta = new ContaCorrente(request.JSON)
        if(!newConta.save(flush:true)){
            respond newConta.errors, status: 422 // Entidade não foi entendida ou processada
            return
        }
        render newConta as json
    }
    
    def update(Long id){
        def newConta = ContaCorrente.get(id)
        if(!newConta){render status: 404; return}

        newConta.properties = request.JSON
        if(!conta.save(flush:true)){
            return
        }
        render newConta as JSON
    }

    def delete(Long id){
        def newConta = ContaCorrente.get(id)
        if(!newConta){render status: 404; return}

        conta.delete(flush:true)
        render status:204 // vázio
    }
}