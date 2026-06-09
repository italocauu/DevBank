package devbank.utils;

public class EmailValidator{

    public static boolean isValid(String email){

        if(email == null || email.trim().isEmpty()) {
            return false;
        }
        String regexPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9._]+\\.[a-zA-Z]{2,6}$";

        return email.matches(regexPattern);
    }
}