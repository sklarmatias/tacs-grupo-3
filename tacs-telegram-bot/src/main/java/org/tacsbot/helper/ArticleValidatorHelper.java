package org.tacsbot.helper;

public class ArticleValidatorHelper {
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
}
