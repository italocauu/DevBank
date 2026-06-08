package devbank.dto 

import devbank.ContaCorrente


class ContaCorrenteDTO{
    Long id
    String titular
    String cpf
    String chavePix
    BigDecimal saldo
    String celular
    String email

    static ContaCorrenteDTO deContaCorrente(ContaCorrente conta){
        return new ContaCorrenteDTO(
            id: conta.id,
            titular: conta.titular,
            celular: conta.celular,
            cpf: formatarCpf(conta.cpf),
            chavePix: conta.chavePix,
            saldo: conta.saldo,
            email: conta.email,
        )
    }
    
    private static String formatarCpf(String cpf){
        if(!cpf) return null
        return cpf.replaceAll(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4')
    }
}