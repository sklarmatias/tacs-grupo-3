package org.tacsbot.handlers;

import jakarta.ws.rs.core.Response;
import org.apache.cxf.jaxrs.client.WebClient;
import org.tacsbot.BotPrincipal;
import org.tacsbot.Validador;
import org.tacsbot.clases.CostType;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CrearArticuloHandler implements CommandsHandler {
    private Long chatId;
    private PasoCreacionArticulo pasoActual;
    private String nombreArticulo;
    private Date fechaLimite;
    private CostType tipoCosto;
    private Double costo;
    private Integer cantidadMaximaUsuarios;
    private Integer cantidadMinimaUsuarios;
    private String image;
    private String recibeUsuario;

    // Enum para representar los diferentes pasos del proceso de creación del artículo
    private enum PasoCreacionArticulo {
        SOLICITAR_NOMBRE,
        SOLICITAR_FECHA_LIMITE,
        SOLICITAR_TIPO_COSTO,
        SOLICITAR_COSTO,
        SOLICITAR_CANTIDAD_MAXIMA,
        SOLICITAR_CANTIDAD_MINIMA,
        SOLICITAR_IMAGEN, FINALIZAR
    }

    public CrearArticuloHandler(Long userId) {
        this.chatId = userId;
        this.pasoActual = PasoCreacionArticulo.SOLICITAR_NOMBRE;
    }

    @Override
    public void procesarRespuesta(Message respuesta, BotPrincipal bot) {
        String mensajeDeError = null;
        switch (pasoActual) {
            case SOLICITAR_NOMBRE:
                // Paso 1: Solicitar el nombre del artículo
                mensajeDeError = Validador.validarNombreArticulo(respuesta.getText());
                if (mensajeDeError == null){
                nombreArticulo = respuesta.getText();

                pasoActual = PasoCreacionArticulo.SOLICITAR_FECHA_LIMITE;
                bot.sendText(chatId, "Por favor ingresa la fecha límite (YYYY-MM-DD):");
                }else {
                    bot.sendText(chatId, mensajeDeError + "Ingrese un nombre nuevamente...");
                }
                break;
            case SOLICITAR_FECHA_LIMITE:
                // Paso 2: Solicitar la fecha límite del artículo
                try {
                    fechaLimite = new SimpleDateFormat("yyyy-MM-dd").parse(respuesta.getText());
                    pasoActual = PasoCreacionArticulo.SOLICITAR_TIPO_COSTO;
                    bot.sendText(chatId, "Por favor ingresa el tipo de costo (Total o Per_user):");
                } catch (ParseException e) {
                    bot.sendText(chatId, "Formato de fecha incorrecto. Por favor, ingrese la fecha nuevamente usando el formato YYYY-MM-DD");
                }
                break;
            case SOLICITAR_TIPO_COSTO:
                // Paso 3: Solicitar el tipo de costo del artículo
                String tipoCostoString = respuesta.getText().toUpperCase();
                mensajeDeError = Validador.validarTipoCosto(tipoCostoString);
                if (mensajeDeError == null) {
                    tipoCosto = CostType.valueOf(tipoCostoString);
                    pasoActual = PasoCreacionArticulo.SOLICITAR_COSTO;
                    bot.sendText(chatId, "Por favor ingresa el costo:");
                }else {
                    bot.sendText(chatId, mensajeDeError + "Ingrese el tipo de costo nuevamente...");
                }
                break;
            case SOLICITAR_COSTO:
                // Paso 4: Solicitar el costo del artículo
                try {
                    costo = Double.parseDouble(respuesta.getText());
                    pasoActual = PasoCreacionArticulo.SOLICITAR_CANTIDAD_MAXIMA;
                    bot.sendText(chatId, "Por favor ingresa la cantidad máxima de usuarios:");
                } catch (NumberFormatException e) {
                    bot.sendText(chatId, "Formato de costo incorrecto. Por favor, ingresa un número válido (xxxx.xx)");
                }
                break;
            case SOLICITAR_CANTIDAD_MAXIMA:
                // Paso 5: Solicitar la cantidad máxima de usuarios del artículo
                try {
                    cantidadMaximaUsuarios = Integer.parseInt(respuesta.getText());
                    pasoActual = PasoCreacionArticulo.SOLICITAR_CANTIDAD_MINIMA;
                    bot.sendText(chatId, "Por favor ingresa la cantidad mínima de usuarios:");
                } catch (NumberFormatException e) {
                    bot.sendText(chatId, "Formato de cantidad incorrecto. Por favor, ingresa un número válido.");
                }
                break;
            case SOLICITAR_CANTIDAD_MINIMA:
                // Paso 6: Solicitar la cantidad mínima de usuarios del artículo
                try {
                    cantidadMinimaUsuarios = Integer.parseInt(respuesta.getText());
                    mensajeDeError = Validador.validarCantidadMinimaUsers(cantidadMinimaUsuarios, cantidadMaximaUsuarios);
                    if ( mensajeDeError == null) {
                        pasoActual = PasoCreacionArticulo.SOLICITAR_IMAGEN;

                        bot.sendText(chatId, "Adjunte la imagen");
                    }else {
                        bot.sendText(chatId, mensajeDeError);
                    }
                } catch (NumberFormatException e) {
                    bot.sendText(chatId, "Formato de cantidad incorrecto. Por favor, ingresa un número válido.");
                }
                break;
            case SOLICITAR_IMAGEN:
                //todo logica para guardar imagen
                // TODO pedir lo que recibe cada usuario
                // TODO: Asignar el propietario y la fecha de creación al artículo
                image = respuesta.getText();
                this.recibeUsuario = bot.usersLoginMap.get(chatId);
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String jsonrequest = "{\n" +
                        "  \"name\": \"" + nombreArticulo + "\",\n" +
                        "  \"image\": \"" + image + "\",\n" +
                        "  \"deadline\": \"" + formatter.format(fechaLimite) + "\",\n" +
                        "  \"usersMax\": " + cantidadMaximaUsuarios.toString() + ",\n" +
                        "  \"usersMin\": " + cantidadMinimaUsuarios.toString() + ",\n" +
                        "  \"cost\": " + costo + ",\n" +
                        "  \"costType\": \"" + tipoCosto.toString() + "\",\n" +
                        "  \"userGets\": \"" + recibeUsuario + "\",\n" +
                        "  \"owner\": " + recibeUsuario + "\n" +
                        "}";
                WebClient client = WebClient.create(System.getenv("RESOURCE_URL") + "/articles");
                System.out.println(jsonrequest);
                Response response = client.type("application/json").post(jsonrequest);
                System.out.println(response.getStatus());
                System.out.println(response.readEntity(String.class));
                if (response.getStatus() == 201) {
                    System.out.println("articulo creado");
                    bot.sendText(chatId, "articulo creado");
                }
                    else{
                    System.out.println("articulo no creado");
                    bot.sendText(chatId, "articulo no creado");
                }
                bot.resetUserHandlers(chatId);

        }
    }
}