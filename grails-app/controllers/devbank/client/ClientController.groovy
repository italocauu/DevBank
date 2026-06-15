package devbank

import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured

@Secured('permitAll')
class ClientController {

    static responseFormats = ['json']

    ClientService clientService

    def show(){
        try{
            def body = request.JSON
            String esseId = body.id as String
        
            def conta = clientService.acharConta(esseId)
            if(conta){
                return render([sucesso: true, dados: conta] as JSON)
            }

            response.status = 404 
            return render([sucesso: false, mensagem: "Conta não encontrada"] as JSON)
            
        } catch (Exception e){
            response.status = 400
            render([sucesso: false, mensagem: "Erro interno: ${e.message}"] as JSON)
        }
    }
}
