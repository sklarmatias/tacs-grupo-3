package org.tacsbot.helper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterValidatorHelper {
    public static String validateArticleName(String articleName){
        if (articleName.isEmpty() || articleName.isBlank()) {
            return "El nombre del artículo no puede estar vacío.";
        }
        if (articleName.length() > 60) {
            return "El nombre del artículo no puede tener más de 60 caracteres.";
        }
        if (articleName.length() < 10){
            return "El nombre del articulo debe tener mas de 10 caracteres";
        }
        if (!articleName.matches("[a-zA-Z0-9ÁáÉéÍíÓóÚúÜü][a-zA-Z0-9ÁáÉéÍíÓóÚúÜü'&()¡¿?!\"° ñ-]*")) {
            return "contiene caracteres no permitidos";
        }

        return null;
    }


    public static String validateEmail(String email) {
        String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        if (email == null || email.isEmpty() || email.isBlank()) {
            return "ERROR_EMAIL_EMPTY";
        }

        Pattern pattern = Pattern.compile(EMAIL_REGEX);
        Matcher matcher = pattern.matcher(email);

        if (!matcher.matches()) {
            return "ERROR_EMAIL_INVALID";
        }

        return null;
    }

    public static String validatePassword(String text) {
        if (text.length() < 8) {
            return "ERROR_PASSWORD_INVALID";
        }

        boolean hasDigit = false;
        for (char c : text.toCharArray()) {
            if (Character.isDigit(c)) {
                hasDigit = true;
                break;
            }
        }
        if (!hasDigit) {
            return "ERROR_PASSWORD_INVALID";
        }

        boolean hasLowerCase = false;
        boolean hasUpperCase = false;
        for (char c : text.toCharArray()) {
            if (Character.isLowerCase(c)) {
                hasLowerCase = true;
            }
            if (Character.isUpperCase(c)) {
                hasUpperCase = true;
            }
        }
        if (!hasLowerCase || !hasUpperCase) {
            return "ERROR_PASSWORD_INVALID";
        }

        return null;
    }

}