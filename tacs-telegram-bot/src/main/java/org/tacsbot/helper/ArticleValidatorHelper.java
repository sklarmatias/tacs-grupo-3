package org.tacsbot.helper;

public class ArticleValidatorHelper {
    public static String validateArticleName(String articleName) {
        if (articleName.isEmpty() || articleName.isBlank()) {
            return "EMPTY";
        }
        if (articleName.length() > 60) {
            return "ARTICLE_NAME_TOO_LONG";
        }
        if (articleName.length() < 10){
            return "ARTICLE_NAME_TOO_SHORT";
        }
        if (!articleName.matches("[a-zA-Z0-9ÁáÉéÍíÓóÚúÜü][a-zA-Z0-9ÁáÉéÍíÓóÚúÜü'&()¡¿?!\"° ñ-]*")) {
            return "INVALID_CHARACTERS";
        }
        return null;
    }
    public static String validateUserGets(String text){
        if (text.isEmpty() || text.isBlank()) {
            return "EMPTY";
        }
        if (!text.matches("[a-zA-Z0-9ÁáÉéÍíÓóÚúÜü][a-zA-Z0-9ÁáÉéÍíÓóÚúÜü'&()¡¿?!\"° ñ-]*")) {
            return "INVALID_CHARACTERS";
        }

        return null;
    }
    public static String validateCostType(String costType){
        if (costType.toUpperCase().equals("TOTAL") || costType.toUpperCase().equals("PER_USER")){
            return null;
        }
            return "Tipo de Costo ingresado no valido";
    }

    public static String validateMinNumUsers(Integer minNumUsers, Integer MaxNumUsers){
        if (minNumUsers < 0){
            return "La cantidad minima de usuarios no puede ser 0";
        }
        if (minNumUsers > MaxNumUsers){
            return "La cantidad minima de usuarios no puede ser superior a la cantidad maxima de usuarios indicada";
        }
        return null;
    }

    public static String validateUserName(String text) {
        //todo validar nombre de usuario
        return null;
    }

    public static String validateUserSurname(String text) {
        //todo validar apellido de usuario
        return null;
    }

    public static String validateEmail(String text) {
        //todo validar mail
        return null;
    }

    public static String validatePassword(String text) {
        //todo validar password
        return null;
    }
}
