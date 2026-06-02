package devbank

import grails.gorm.transactions.Transactional

@Transactional
class ValidatorCpfToolsService {

    static boolean isCpfValido(String cpfLimpo){
        List<Integer> numeros = cpfLimpo.collect{it.toInteger()
        
        int regiaoFiscal = numeros[8]
        int primeiroDIgito
        }
    }
}
