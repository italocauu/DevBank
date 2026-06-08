package devbank

import grails.gorm.transactions.Transactional

@Transactional
class ContaCorrenteService {
    def salvarDados(Map dadosDaContaCorrenteJSON){
        // Validação inicial dos dados
        println "DADOS RECEBIDOS: ${dadosDaContaCorrenteJSON}"
        println "TITULAR: ${dadosDaContaCorrenteJSON.titular}"
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
}
