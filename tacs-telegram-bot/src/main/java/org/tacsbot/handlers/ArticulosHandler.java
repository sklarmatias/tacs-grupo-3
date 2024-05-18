package org.tacsbot.handlers;

import jakarta.ws.rs.core.Response;
import org.apache.cxf.jaxrs.client.WebClient;
import org.tacsbot.BotPrincipal;
import org.telegram.telegrambots.meta.api.objects.Message;

public class ArticulosHandler implements CommandsHandler {
    private Long chatId;
    private PasoArticulo pasoActual;
    private int idArticulo;
    private TipoArticulos tipoArticulos;
    private String acciones;
    private String user;
    @Override
    public void procesarRespuesta(Message respuesta, BotPrincipal bot) {
        WebClient client;
        Response response;
        String url = System.getenv("RESOURCE_URL") + "/articles";
        user = bot.usersLoginMap.get(chatId);
        switch (pasoActual) {
            case ELEGIR_TIPO:
                tipoArticulos = TipoArticulos.valueOf(respuesta.getText());
                switch (tipoArticulos){
                    case TODOS:
                        acciones = "SUSCRIBIRSE";
                        break;
                    case PROPIOS:
                        acciones = "VER_SUSCRIPTOS, CERRAR";
                        url += "/user/" + user.toString();
                        break;
                }
                client = WebClient.create(url);
                response = client.accept("application/json").get();
                bot.sendText(chatId, "Estos son los articulos disponibles");
                bot.sendText(chatId,bot.parsearJson(response.readEntity(String.class)));
                pasoActual = PasoArticulo.ELEGIR_ARTICULO;
                break;
            case ELEGIR_ARTICULO:
                // Paso 1: Solicitar el nombre del artículo
                idArticulo = Integer.parseInt(respuesta.getText());
                bot.sendText(chatId, "Elegir la accion. " + acciones);
                pasoActual = PasoArticulo.ELEGIR_ACCION;
                break;
            case ELEGIR_ACCION:
                String accion = respuesta.getText();
                System.out.println(accion);
                switch (accion){
                    case "SUSCRIBIRSE":
                        url +=  "/"+ idArticulo + "/users/" + user.toString();
                        client = WebClient.create(url);
                        response = client.type("application/json").post("");
                        System.out.println(response.getStatus());
                        if(response.getStatus() == 204){
                            bot.sendText(chatId, "Se ha suscripto correctamente.");
                        }
                        else{
                            bot.sendText(chatId, "Ingresar un articulo valido.");
                        }
                        break;
                    case "CERRAR":
                        url +=  "/"+ idArticulo + "/close";
                        client = WebClient.create(url);
                        response = client.type("application/json").invoke("PATCH", "");
                        if(response.getStatus() == 200){
                            bot.sendText(chatId, "Se ha cerrado correctamente.");
                        }
                        else{
                            bot.sendText(chatId, "No se pudo cerrar.");
                        }
                        break;
                    case "VER_SUSCRIPTOS":
                        url += "/"+ idArticulo + "/users";
                        client = WebClient.create(url);
                        response = client.accept("application/json").get();
                        String message = "Estos son los usuarios anotados al articulo:" + response.readEntity(String.class);
                        bot.sendText(chatId, message);
                        System.out.printf("Artículos obtenidos por el usuario %d", chatId);
                        break;
                }
        }
    }

    private enum PasoArticulo {
        ELEGIR_TIPO,
        ELEGIR_ARTICULO,
        ELEGIR_ACCION
    }
    private enum TipoArticulos{
        TODOS,
        PROPIOS
    }
    public ArticulosHandler(Long userId) {
        this.chatId = userId;
        this.pasoActual = PasoArticulo.ELEGIR_TIPO;
    }
}
