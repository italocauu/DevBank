package devbank

import grails.converters.JSON
import devbank.dto.ContaCorrenteDTO

class ContaCorrenteController {

    static responseFormats = ['json']

    ContaCorrenteService contaCorrenteService

    def save(){
        try{
            def json = request.JSON  // lê UMA vez e guarda

            def dadosFiltrados = [
                titular: json.titular?.toString(),
                cpf:     json.cpf?.toString(),
                celular: json.celular?.toString(),
                email:   json.email?.toString()
            ]

        // println "JSON BRUTO: ${json}"
        // println "DADOS FILTRADOS: ${dadosFiltrados}"

        def relato = contaCorrenteService.salvarDados(dadosFiltrados)

            // Agora responsestatus, aqui é o relatorio escrito se deu certo ou não, apenas para visualizar
            response.status = relato.statusHttp

            // Aqui entregaremos ao client

            if(relato.sucesso){
                // Classe DTO assumindo a formatação
                def dto = ContaCorrenteDTO.deContaCorrente(relato.dados)
                render([mensagem: relato.mensagem, conta: dto.properties] as JSON)

            } else{
                render relato as JSON
            }

        } catch (Exception e){
            response.status = 500
            render([sucesso: false, mensagem: "Erro interno: ${e.message}"] as JSON)

        }
    }

    def index() { 
        def listarContas = ContaCorrente.list().collect{ conta ->
           ContaCorrenteDTO.deContaCorrente(conta).properties 

        }
        render listarContas as JSON
    }

    
    def show(Long id){
        def contaEncontrar = ContaCorrente.get(id)
        
        if(!ContaCorrente.get(id)){
            render status: 404, text: "Conta não encontrada"
            return
        }

        def dto = ContaCorrenteDTO.deContaCorrente(contaEncontrar)

        render dto.properties as JSON
    }
    


    def update(Long id){
        def json = request.JSON
        def relato = contaCorrenteService.atualizarDados(id, json)
        response.status = relato.statusHttp
        render relato as JSON
    }



    def delete(Long id){
        def relato = contaCorrenteService.apagarDados(id)
        response.status = relato.statusHttp
        render relato as JSON
    }
}