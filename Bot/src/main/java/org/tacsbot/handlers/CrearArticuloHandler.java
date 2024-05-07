package org.tacsbot.handlers;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.cxf.jaxrs.client.WebClient;
import org.tacsbot.BotPrincipal;
import org.tacsbot.clases.CostType;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CrearArticuloHandler implements CommandsHandler {
    private Long userId;
    private PasoCreacionArticulo pasoActual;
    private String nombreArticulo;
    private Date fechaLimite;
    private CostType tipoCosto;
    private Double costo;
    private Integer cantidadMaximaUsuarios;
    private Integer cantidadMinimaUsuarios;
    private String image;
    private Long recibeUsuario;

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
        this.userId = userId;
        this.pasoActual = PasoCreacionArticulo.SOLICITAR_NOMBRE;
    }

    @Override
    public void procesarRespuesta(Message respuesta, BotPrincipal bot) {
        switch (pasoActual) {
            case SOLICITAR_NOMBRE:
                // Paso 1: Solicitar el nombre del artículo
                nombreArticulo = respuesta.getText();
                pasoActual = PasoCreacionArticulo.SOLICITAR_FECHA_LIMITE;
                bot.sendText(userId, "Por favor ingresa la fecha límite (YYYY-MM-DD HH:mm:ss):");
                break;
            case SOLICITAR_FECHA_LIMITE:
                // Paso 2: Solicitar la fecha límite del artículo
                try {
                    fechaLimite = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(respuesta.getText());
                    pasoActual = PasoCreacionArticulo.SOLICITAR_TIPO_COSTO;
                    bot.sendText(userId, "Por favor ingresa el tipo de costo (Total o Individual):");
                } catch (ParseException e) {
                    bot.sendText(userId, "Formato de fecha incorrecto. Por favor, usa el formato YYYY-MM-DD HH:mm:ss");
                }
                break;
            case SOLICITAR_TIPO_COSTO:
                // Paso 3: Solicitar el tipo de costo del artículo
                String tipoCostoString = respuesta.getText().toUpperCase();
                tipoCosto = CostType.valueOf(tipoCostoString);
                pasoActual = PasoCreacionArticulo.SOLICITAR_COSTO;
                bot.sendText(userId, "Por favor ingresa el costo:");
                break;
            case SOLICITAR_COSTO:
                // Paso 4: Solicitar el costo del artículo
                try {
                    costo = Double.parseDouble(respuesta.getText());
                    pasoActual = PasoCreacionArticulo.SOLICITAR_CANTIDAD_MAXIMA;
                    bot.sendText(userId, "Por favor ingresa la cantidad máxima de usuarios:");
                } catch (NumberFormatException e) {
                    bot.sendText(userId, "Formato de costo incorrecto. Por favor, ingresa un número válido.");
                }
                break;
            case SOLICITAR_CANTIDAD_MAXIMA:
                // Paso 5: Solicitar la cantidad máxima de usuarios del artículo
                try {
                    cantidadMaximaUsuarios = Integer.parseInt(respuesta.getText());
                    pasoActual = PasoCreacionArticulo.SOLICITAR_CANTIDAD_MINIMA;
                    bot.sendText(userId, "Por favor ingresa la cantidad mínima de usuarios:");
                } catch (NumberFormatException e) {
                    bot.sendText(userId, "Formato de cantidad incorrecto. Por favor, ingresa un número válido.");
                }
                break;
            case SOLICITAR_CANTIDAD_MINIMA:
                // Paso 6: Solicitar la cantidad mínima de usuarios del artículo
                try {
                    cantidadMinimaUsuarios = Integer.parseInt(respuesta.getText());
                    pasoActual = PasoCreacionArticulo.SOLICITAR_IMAGEN;
                    // TODO: Asignar el propietario y la fecha de creación al artículo
                    bot.sendText(userId, "Adjunte la imagen");

                } catch (NumberFormatException e) {
                    bot.sendText(userId, "Formato de cantidad incorrecto. Por favor, ingresa un número válido.");
                }
                break;
            case SOLICITAR_IMAGEN:
                //todo logica para guardar imagen
                // TODO pedir lo que recibe cada usuario
                image = respuesta.getText();
                this.recibeUsuario = bot.UsersLoginMap.get(userId);
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
                        "  \"owner\": " + recibeUsuario.toString() + "\n" +
                        "}";
                WebClient client = WebClient.create(System.getenv("RESOURCE_URL") + "/articles");
                System.out.println(jsonrequest);
                Response response = client.type("application/json").post(jsonrequest);
                System.out.println(response.getStatus());
                System.out.println(response.readEntity(String.class));
                if (response.getStatus() == 201) {
                    System.out.println("articulo creado");
                    bot.sendText(userId, "articulo creado");
                }
                    else{
                    System.out.println("articulo no creado");
                    bot.sendText(userId, "articulo no creado");
                }
                bot.resetUserHandlers(userId);

        }
    }
}