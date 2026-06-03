package devbank

import devbank.utils.CpfValidator

class ContaCorrente {

	String titular
	String cpf
	String chavePix
	BigDecimal saldo = 0.0
	String celular

	static constraints = {
		// Pensar em como tratar pessoas com o mesmo nome.
		titular nullable: false, blank: false

		cpf nullable: false, blank: false, unique: true, validator:{ val ->
			
			if(!CpfValidator.isValid(val)) return 'contaCorrente.cpf.validator.invalid'
		}

		// Vamos ver se tem espaços na chave pix
		chavePix nullable: false, blank: false, unique: true, validator: {val ->
			if(val?.contains(' ')) return 'contaCorrente.chavePix.validator.invalid.noSpace'
		}

		saldo min: 0.0

		celular nullable: false, blank: false, unique: true{val ->
			if(!CelularValidator.isValido(val)) return 'contaCorrente.celular.validator.invalid'
		}
	}

		static mapping = {
		id generator: 'identity'
	}
	
}