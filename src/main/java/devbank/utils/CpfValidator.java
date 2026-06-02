package devbank.utils;

public class CpfValidator{

    public static boolean isValid(String cpf) {
        if(cpf == null){
            return false;
        }

        String cpfSanitizado = cpf.replaceAll("\\D", "");

        if (cpfSanitizado.length() != 11 || cpfSanitizado.matches("(\\d)\\1{10}")){
            return false;
        }

        try{
            int somaFinal = 0;
            for(int i = 0; i < 9; i++){
                somaFinal += Character.getNumericValue(cpfSanitizado.charAt(i)) * (10-i);
            }

            int restante = 11 - (somaFinal%11);
            int digitoum = (restante >= 10) ? 0 : restante;

            if(digitoum != Character.getNumericValue(cpfSanitizado.charAt(9))){
                return false;
            }

            somaFinal = 0;
            for(int i = 0; i < 10; i++){
                somaFinal += Character.getNumericValue(cpfSanitizado.charAt(i)) * (11-i);
            }
            restante = 11 - (somaFinal % 11);
            int digitodois = (restante >= 10) ? 0 : restante; 
            
            return digitodois == Character.getNumericValue(cpfSanitizado.charAt(10));
        } catch (Exception e){
            return false;
        }
    }
}