package devbank

class ContaCorrente {

	String titular
	String cpf
	String chavePix
	BigDecimal saldo = 0.0

	static constraints = {
		// Pensar em como tratar pessoas com o mesmo nome.
		titular nullabe: false, blank: false
		cpf nullable: false, blank: false, unique: true, matches: "[0-9]{11}"
		// Vamos ver se tem espaços na chave pix
		chavePix nullable: false, blank: false, unique: true, validator: {val ->
			if(minhaChave?.contains(' ')) return 'contaCorrente.chavePix.validator.invalid.noSpace'
		}
		saldo min: 0.0
		
		
	}
}
