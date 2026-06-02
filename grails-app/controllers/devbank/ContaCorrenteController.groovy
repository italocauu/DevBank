package devbank

import grails.converters.JSON

class ContaCorrenteController {

    static responseFormats = ['json']

    private String formatoCpf(String cpf){
        if(!cpf) return null

        String limpoCpf = cpf.replaceAll(/\D/, "")

        if(limpoCpf.length() == 11){
            return limpoCpf.replaceFirst(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4')
        }
        return cpf
    }

    private Map formatarContas(ContaCorrente conta){
        return[
            id: conta.id,
            titular: conta.titular,
            cpf: formatoCpf(conta.cpf),
            chavePix: conta.chavePix,
            saldo: conta.saldo
        ]
    }

    def index() { 
        def listarContas = ContaCorrente.list().collect{formatarContas(it)}
        render listarContas as JSON
    }
    
    def show(Long id){
        def contaEncontrar = ContaCorrente.get(id)
        
        if(!contaEncontrar){
            render status: 404, text: "Conta não encontrada"
            return
        }
        
        def contaFormatada = [
            id: contaEncontrar.id,
            titular: contaEncontrar.titular,
            cpf: formatoCpf(contaEncontrar.cpf),
            chavePix: contaEncontrar.chavePix,
            saldo: contaEncontrar.saldo
        ]
        render contaFormatada as JSON
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