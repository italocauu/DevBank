package devbank

import devbank.utils.CpfValidator
import devbank.utils.CelularValidator
import devbank.utils.EmailValidator

class ContaCorrente {

String titular
String cpf
String chavePix
BigDecimal saldo = 0.0
String celular
String email

static constraints = {
	// Pensar em como tratar pessoas com o mesmo nome.
	titular nullable: false, blank: false

	cpf nullable: false, blank: false, unique: true, validator:{ val ->
		if(!CpfValidator.isValid(val)) return 'contaCorrente.cpf.validator.invalid'
	}

	// Vamos ver se tem espaços na chave pix
	chavePix nullable: true, blank: false, unique: true

	saldo min: 0.0

	celular nullable: false, blank: false, unique: true, validator: {val ->
		if(!CelularValidator.isValid(val)) return 'contaCorrente.celular.validator.invalid'
	}

	email nullable: false, blank: false, unique: true, validator: {val ->
		if(!EmailValidator.isValid(val)) return 'contaCorrente.email.validator.invalid'
	}
	
}

	static mapping = {
	id generator: 'identity'
}

}