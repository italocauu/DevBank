package devbank

class Transferencia {
		
	ContaCorrente origem
	ContaCorrente destino
	BigDecimal valor
	Date dataHora
	
    static constraints = {
    }

	static mapping = {
		id generator: 'identity'
	}
}