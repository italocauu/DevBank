package devbank.utils;

public class CelularValidator{
    public static boolean isValid(String celular){

        if (celular == null || celular.trim().isEmpty()) return false;

        String celularSanitizado = celular.replaceAll("\\D", "");
        String[] dddVerificador = {"11", "12", "13", "14", "15", "16", "17", "18", "19",
        "21", "22", "24", "27", "28", "31", "32", "33", "34", "35", "37", "38", "41", "42",
        "43", "44", "45", "46", "47", "48", "49", "51", "53", "54", "55", "61", "62", "63",
        "64", "65", "66", "67", "68", "69", "71", "73", "74", "75", "77", "79", "81", "82",
        "83", "84", "85", "86", "87", "88", "89", "91", "92", "93", "94", "95", "96", "97",
        "98", "99"};

        String[] Tim = {"99", "98"};
        String [] Claro = {"94", "93", "92", "91"};
        String[] Vivo = {"99", "98", "97", "96"};

        if(celularSanitizado.length() != 11) return false;
 

        boolean ddValido = false;
        String doisDigitos = celularSanitizado.substring(0, 2);

        for(int i = 0; i < dddVerificador.length;  i++){
            if(doisDigitos.equals(dddVerificador[i])){
                ddValido = true;
                break;
            }
        }

        if (ddValido != true) return false;

        String Operadora = celularSanitizado.substring(2, 4);
        boolean operadoraValida = false;
        String operadoraIdentificada = "";

        for(int i = 0; i < Tim.length; i++){
            if(Operadora.equals(Tim[i])){
                operadoraIdentificada= "Tim";
                operadoraValida = true;
            break;
        }
    }

        if(!operadoraValida){
            for(int i = 0; i < Claro.length; i++){
                if(Operadora.equals(Claro[i])){
                    operadoraIdentificada = "Claro";
                    operadoraValida = true;
                    break;
        }
    }
}
        if(!operadoraValida){
            for(int i = 0; i < Vivo.length; i++){
                if(Operadora.equals(Vivo[i])){
                    operadoraIdentificada = "Vivo";
                    operadoraValida = true;
                    break;
        }
    }
}

        if(!operadoraValida) return false;
    
        return true;
}
}
    