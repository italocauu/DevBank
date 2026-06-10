package devbank.client

import devbank.auth.User
import devbank.ContaCorrente

// Cliente é uma BDC que herdar user
class Client extends User{

    String nomeCompleto

    // Um cliente tem uma conta
    ContaCorrente contaCorrente

    static constraints = {
        nomeCompleto nullable: false, blank: false

        // Cliente pode ser criado antes de ter a conta
        contaCorrente nullable: true
    }
}