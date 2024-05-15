package org.tacsbot;

public class Validador {
    public static String validarNombreArticulo(String nombreArticulo){
        if (nombreArticulo.isEmpty() || nombreArticulo.isBlank()) {
            return "El nombre del artículo no puede estar vacío.";
        }
        if (nombreArticulo.length() > 60) {
            return "El nombre del artículo no puede tener más de 60 caracteres.";
        }
        if (nombreArticulo.length() < 10){
            return "El nombre del articulo debe tener mas de 10 caracteres";
        }
        if (!nombreArticulo.matches("[a-zA-Z0-9ÁáÉéÍíÓóÚúÜü][a-zA-Z0-9ÁáÉéÍíÓóÚúÜü'&()¡¿?!\"° ñ-]*")) {
            return "contiene caracteres no permitidos";
        }

        return null;
    }
    public static String validarTipoCosto(String tipoCosto){
        if (tipoCosto.toUpperCase().equals("TOTAL") || tipoCosto.toUpperCase().equals("PER_USER")){
            return null;
        }
            return "Tipo de Costo ingresado no valido";
    }

    public static String validarCantidadMinimaUsers(Integer cantidadMinimaUsers, Integer cantidadMaximaUsers){
        if (cantidadMinimaUsers < 0){
            return "La cantidad minima de usuarios no puede ser 0";
        }
        if (cantidadMinimaUsers > cantidadMaximaUsers){
            return "La cantidad minima de usuarios no puede ser superior a la cantidad maxima de usuarios indicada";
        }
        return null;
    }
}
