package devbank

import grails.converters.JSON
import grails.plugin.springsecurity.annotation.Secured

@Secured(['ROLE_DEV', 'ROLE_MANAGER'])
class ManagerController {

    static responseFormats = ['json']
    ManagerService managerService

    def listarCaixas(){
        try{
            int pagina = (params.pagina ?: 1) as Integer
            int limite = (params.limite ?: 10) as Integer

            def caixas = managerService.listarCaixas(pagina, limite)

            def resposta = [
                paginaAtual: pagina,
                itensPorPagina: limite,
                totalRetornando: caixas.size(),
                dados: caixas                
            ]
            
            response.status = 200

            return render (resposta as JSON)

        } catch(Exception e){
            return render ([erro: e.getMessage()]) as JSON
        }
    }
    def criarCaixa(){
            try{
                def info = request.JSON
                
                if(!info.username || !info.password || !info.registro || !info.agencia){
                    response.status = 400
                    return render ([erro: "Um ou mais campos com informações inválidas"] as JSON)
                }

                def resultado = managerService.criarCaixa(
                    info.username.toString(),
                    info.password.toString(),
                    info.registro.toString(),
                    info.agencia.toString()
                )
                
                response.status = 201
                return render(resultado as JSON)

            } catch(Exception e){
                response.status = 400
                return render([erro: e.getMessage()] as JSON)
        }
    }
}
