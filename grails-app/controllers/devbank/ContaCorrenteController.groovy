package devbank

import grails.converters.JSON

class ContaCorrenteController {

    static responseFormats = ['json']

    ContaCorrenteService ContaCorrenteService

    private Map formatarContas(ContaCorrente conta){
        return[
            id: conta.id,
            titular: conta.titular,
            cpf: formatoCpf(conta.cpf),
            chavePix: conta.chavePix,
            saldo: conta.saldo,
            celular: conta.celular
        ]
    }

    private String formatoCpf(String cpf) {
        if (cpf != null && cpf.length() == 11) {
            return cpf.replaceAll(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4')
        }
        return cpf 
    }

    def index() { 
        def listarContas = ContaCorrente.list().collect{formatarContas(it)}
        render listarContas as JSON
    }
    
    def show(Long id){
        def contaEncontrar = ContaCorrente.get(id)
        
        if(!ContaCorrente.get(id)){
            render status: 404, text: "Conta não encontrada"
            return
        }

        def contaFormatada = formatarContas(ContaCorrente.get(id))

        render contaFormatada as JSON
    }

    def save(){
        try{
            String titular = request.JSON.titular
            String cpf = request.JSON.cpf
            String chavePix = request.JSON.chavePix
            String celular = request.JSON.celular
            String email = request.JSON.email

            def resultTemp = contaCorrenteService.abrirNovaConta(titular, cpf, chavePix, celular, email)

            if(resultTemp.sucesso){
                response.status = 201 // Criação bem sucedida
            } else{
                response.status = 400 // Criação inválida
            }

            render resultTemp as JSON

        } catch (Exception e){
            response.status = 500
            render([sucesso: false, messagem: "Erro interno no servidor: ${e.message}"] as JSON)
        }
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