package devbank.teller

import devbank.auth.User

// Herda User

class Teller extends User{
    String registro
    String agencia

    static contraints = {
        registro nullable: false, blank: false, unique: true
        agencia nullable: false, blank false
    }
}