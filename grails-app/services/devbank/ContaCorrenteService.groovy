package devbank

import grails.gorm.transactions.Transactional

@Transactional
class ContaCorrenteService {

def abrirNovaConta(String titular, String cpf, String chavePix, String celular, String email){

    def novaConta = new ContaCorrente(
        titular: titular,
        cpf: cpf,
        chavePix: chavePix,
        celular: celular,
        email: email
    )

    if(!novaConta.save(flush:true)){
        return [
        sucesso: false,
        messagem: "Verifique os dados novamente",
        erros: novaConta.errors.allErrors
        ]
    }
        return [
        sucesso: true,
        messagem: "Conta aberta!",
        conta: novaConta
        ]  
    }
}