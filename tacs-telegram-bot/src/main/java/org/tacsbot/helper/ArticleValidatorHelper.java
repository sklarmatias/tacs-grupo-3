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

}
