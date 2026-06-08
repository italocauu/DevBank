package devbank

import grails.gorm.transactions.Transactional

@Transactional
class ContaCorrenteService {
    def salvarDados(Map dadosDaContaCorrenteJSON){
        // Validação inicial dos dados
        if(!dadosDaContaCorrenteJSON.titular){
            return [sucesso: false, mensagem: "Insira o seu nome!", statusHttp: 400]
        }

        if(!dadosDaContaCorrenteJSON.cpf){
            return [sucesso: false, mensagem: "Insira o seu CPF corretamente!", statusHttp: 400]
        }

        if(!dadosDaContaCorrenteJSON.celular){
            return [sucesso: false, messagem: "Insira um número de celular!", statusHttp: 400]
        }

        if(!dadosDaContaCorrenteJSON.email){
            return [sucesso: false, messagem: "Insira um E-mail!", statusHttp: 400]
        }

        // Regras de negócios

        // Hora de salvar!
        def novaConta = new ContaCorrente(
            titular: dadosDaContaCorrenteJSON.titular,
            cpf:     dadosDaContaCorrenteJSON.cpf,
            celular: dadosDaContaCorrenteJSON.celular,
            email:   dadosDaContaCorrenteJSON.email
        )

        if(!novaConta.save(flush: true)){
            return[
                sucesso: false,
                mensagem: "Falha na formatação dos dados. Verifique os seus dados e tente novamente.",
                erros: novaConta.errors.allErrors,
                statusHttp: 422
            ]
        }
        return[
            sucesso: true,
            mensagem: "Conta criada com sucesso",
            dados: novaConta,
            statusHttp: 201
        ]
    }
    def atualizarDados(Long id, Map dadosJSON){
        def contaExistente = ContaCorrente.get(id)

        if(!contaExistente){
            return [sucesso: false, mensagem: "Conta não encontrada", statusHttp: 404]
        }

        if(dadosJSON.titular) contaExistente.titular = dadosJSON.titular
        if(dadosJSON.celular) contaExistente = dadosJSON.celular
        if(dadosJSON.email)   contaExistente = dadosJSON.email
        if(dadosJSON.chavePix) contaExistente = dadosJSON.chavePix


    if(!contaExistente.save(flush:true)){
        return[sucesso: false, mensagem: "Erro ao atualizar", erros: contaExistente.errors.allErrors, statusHttp: 422]
    }
''
    return [sucesso: true, mensagem: "Conta atualizada", dados: contaExistente, statusHttp: 200]

    }

    def apagarDados(Long id){
        def contaExistente = ContaCorrente.get(id)

        if(!contaExistente){
            return [sucesso: false, mensagem: "Conta não encontrada", statusHttp: 404]
        }

        contaExistente.delete(flush:true)

        return[sucesso: true, mensagem: "Conta apagada", dados: contaExistente, statusHttp: 204]
    }
    
}
