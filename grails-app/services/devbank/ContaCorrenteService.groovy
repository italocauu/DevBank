package devbank

import grails.gorm.transactions.Transactional
import devbank.utils.CpfValidator 
import devbank.utils.CelularValidator
import devbank.utils.EmailValidator

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

    //Instanciando:
        def novaConta = new ContaCorrente(
            titular: dadosDaContaCorrenteJSON.titular,
            cpf:     dadosDaContaCorrenteJSON.cpf,
            celular: dadosDaContaCorrenteJSON.celular,
            email:   dadosDaContaCorrenteJSON.email,
        )

    // Regras de negócios

        if(!CpfValidator.isValid(dadosDaContaCorrenteJSON.cpf)){
            return [sucesso: false, mensagem: "CPF Inválido", statusHttp: 400]
        }
        if(!CelularValidator.isValid(dadosDaContaCorrenteJSON.celular)){
            return [sucesso: false, mensagem: "Telefone inválido", statusHttp: 400]
        }
        if(!EmailValidator.isValid(dadosDaContaCorrenteJSON.email)){
            return [sucesso: false, mensagem: "Email inválido", statusHttp: 400]
        }

        // Hora de salvar!

        if(!novaConta.save(flush: true)){
            // Entender melhor a funcionalidade do collect e as principais diferenças entre
            // O fieldErrors e o allErrors
            def errosFormatados = novaConta.errors.fieldErrors.collect{ erro ->
            [
                campo: erro.field,
                mensagem: "O ${erro.field} informado já está cadastrado em outra conta."
            ]
        }
            return[ 
                sucesso: false,
                mensagem: "Falha na formatação dos dados. Verifique os seus dados e tente novamente.",
                erros:  errosFormatados, //novaConta.errors.allErrors,
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
        if(dadosJSON.celular) contaExistente.celular = dadosJSON.celular
        if(dadosJSON.email)   contaExistente.email = dadosJSON.email
        if(dadosJSON.chavePix) contaExistente.chavePix = dadosJSON.chavePix


    if(!contaExistente.save(flush:true)){
            return[sucesso: false, mensagem: "Erro ao atualizar", erros: contaExistente.errors.allErrors, statusHttp: 422]
    }   
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

    def escolherChavePix(){
        
    }
}
