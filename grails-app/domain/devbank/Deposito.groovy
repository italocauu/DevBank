package devbank

import devbank.teller.Teller

class Deposito {
		
    ContaCorrente origem      // null em depósitos
    ContaCorrente destino
    BigDecimal valor
    Date dataHora
    Teller tellerResponsavel  // quem fez o depósito

    static mapping = {
        id generator: 'identity'
    }
    static constraints = {
        origem nullable: true
        tellerResponsavel nullable: true // null em transferências normais
    }
}   