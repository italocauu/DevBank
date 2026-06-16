package devbank

import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured

@Secured(['ROLE_DEV', 'ROLE_TELLER'])

class TellerController {
    
    static responseFormats = ['json']
    TellerService tellerService
    
    def depositar() {
            try {
                def comprovante = tellerService.depositarDinheiro(request.JSON)

                response.status = 201
                render([
                    sucesso     : true,
                    mensagem    : "Depósito realizado com sucesso!",
                    valor       : comprovante.valor,
                    destino     : comprovante.destino?.id,
                    dataHora    : comprovante.dataHora,
                    tellerLogado: comprovante.tellerResponsavel?.username
                ] as JSON)

            } catch (Exception e) {
                response.status = 422
                render([sucesso: false, mensagem: e.message] as JSON)
        }
    }
}
