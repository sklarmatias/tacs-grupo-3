package org.tacsbot.handlers;

import jakarta.ws.rs.core.Response;
import org.apache.cxf.jaxrs.client.WebClient;
import org.tacsbot.MyTelegramBot;
import org.tacsbot.Validator;
import org.tacsbot.clases.CostType;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ArticleCreationHandler implements CommandsHandler {
    private Long chatId;
    private ArticleCreationStep currentStep;
    private String articleName;
    private Date deadLine;
    private CostType costType;
    private Double cost;
    private Integer maxNumUsers;
    private Integer minNumUsers;
    private String image;
    private String userGets;

    // Enum to represent the different steps of the article creation process
    private enum ArticleCreationStep {
        REQUEST_NAME,
        REQUEST_DEADLINE,
        REQUEST_COST_TYPE,
        REQUEST_COST,
        REQUEST_MIN_USERS,
        REQUEST_MAX_USERS,
        REQUEST_IMAGE, FINALIZAR
    }

    public ArticleCreationHandler(Long userId) {
        this.chatId = userId;
        this.currentStep = ArticleCreationStep.REQUEST_NAME;
    }

    @Override
    public void processResponse(Message message, MyTelegramBot bot) {
        String errorMessage = null;
        switch (currentStep) {
            case REQUEST_NAME:
                // Step 1: Request article's name

                errorMessage = Validator.validateArticleName(message.getText());
                if (errorMessage == null){
                    articleName = message.getText();

                currentStep = ArticleCreationStep.REQUEST_DEADLINE;
                bot.sendText(chatId, "Por favor ingresa la fecha límite (YYYY-MM-DD):");
                }else {
                    bot.sendText(chatId, errorMessage + "Ingrese un nombre nuevamente...");
                }
                break;
            case REQUEST_DEADLINE:
                // Step 2: Request the article's deadline
                try {
                    deadLine = new SimpleDateFormat("yyyy-MM-dd").parse(message.getText());
                    currentStep = ArticleCreationStep.REQUEST_COST_TYPE;
                    bot.sendText(chatId, "Por favor ingresa el tipo de costo (Total o Per_user):");
                } catch (ParseException e) {
                    bot.sendText(chatId, "Formato de fecha incorrecto. Por favor, ingrese la fecha nuevamente usando el formato YYYY-MM-DD");
                }
                break;
            case REQUEST_COST_TYPE:
                // Step 3: Request the article's cost type
                String tipoCostoString = message.getText().toUpperCase();
                errorMessage = Validator.validateCostType(tipoCostoString);
                if (errorMessage == null) {
                    costType = CostType.valueOf(tipoCostoString);
                    currentStep = ArticleCreationStep.REQUEST_COST;
                    bot.sendText(chatId, "Por favor ingresa el costo:");
                }else {
                    bot.sendText(chatId, errorMessage + "Ingrese el tipo de costo nuevamente...");
                }
                break;
            case REQUEST_COST:
                // Step 4: Request the article's cost
                try {
                    cost = Double.parseDouble(message.getText());
                    currentStep = ArticleCreationStep.REQUEST_MAX_USERS;
                    bot.sendText(chatId, "Por favor ingresa la cantidad máxima de usuarios:");
                } catch (NumberFormatException e) {
                    bot.sendText(chatId, "Formato de costo incorrecto. Por favor, ingresa un número válido (xxxx.xx)");
                }
                break;
            case REQUEST_MAX_USERS:
                // Paso 5: Solicitar la cantidad máxima de usuarios del artículo
                try {
                    maxNumUsers = Integer.parseInt(message.getText());
                    currentStep = ArticleCreationStep.REQUEST_MIN_USERS;
                    bot.sendText(chatId, "Por favor ingresa la cantidad mínima de usuarios:");
                } catch (NumberFormatException e) {
                    bot.sendText(chatId, "Formato de cantidad incorrecto. Por favor, ingresa un número válido.");
                }
                break;
            case REQUEST_MIN_USERS:
                // Step 6: Request the article's minimum number of users
                try {
                    minNumUsers = Integer.parseInt(message.getText());
                    errorMessage = Validator.validateMinNumUsers(minNumUsers, maxNumUsers);
                    if ( errorMessage == null) {
                        currentStep = ArticleCreationStep.REQUEST_IMAGE;

                        bot.sendText(chatId, "Adjunte la imagen");
                    }else {
                        bot.sendText(chatId, errorMessage);
                    }
                } catch (NumberFormatException e) {
                    bot.sendText(chatId, "Formato de cantidad incorrecto. Por favor, ingresa un número válido.");
                }
                break;
            case REQUEST_IMAGE:
                //todo logica para guardar imagen
                // TODO pedir lo que recibe cada usuario
                // TODO: Asignar el propietario y la fecha de creación al artículo
                image = message.getText();
                this.userGets = bot.usersLoginMap.get(chatId);
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                String jsonrequest = "{\n" +
                        "  \"name\": \"" + articleName + "\",\n" +
                        "  \"image\": \"" + image + "\",\n" +
                        "  \"link\": \"" + "image" + "\",\n" +
                        "  \"deadline\": \"" + formatter.format(deadLine) + "\",\n" +
                        "  \"usersMax\": " + maxNumUsers.toString() + ",\n" +
                        "  \"usersMin\": " + minNumUsers.toString() + ",\n" +
                        "  \"cost\": " + cost.toString() + ",\n" +
                        "  \"costType\": \"" + costType.toString() + "\",\n" +
                        "  \"userGets\": \"" + userGets + "\",\n" +
                        "  \"owner\": \"" + userGets + "\"\n" +
                        "}";
                WebClient client = WebClient.create(System.getenv("RESOURCE_URL") + "/articles");
                System.out.println(jsonrequest);
                Response response = client.header("user",userGets).type("application/json").post(jsonrequest);
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