package devbank.manager

import devbank.auth.User

class Manager extends User{
    String registro
    String agencia
    BigDecimal limiteAprovado

    static contraints = {
        registro nullable: false, blank: false, unique: true
        agencia nullable: false, blank: false
        limiteAprovado nullable: false, min: 0.0
    }
}