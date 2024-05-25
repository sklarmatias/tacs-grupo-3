package org.tacsbot.model;

import com.fasterxml.jackson.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Article {

    private String id;

    private String name;

    private String image;

    private String link;

    @JsonProperty("user_gets")
    private String userGets;

    private ArticleStatus status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date deadline;

    private String owner;

    @JsonProperty("creation_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date creationDate;

    private List<Annotation> annotations;

    @JsonProperty("annotations_counter")
    private Integer annotationsCounter;

    private Double cost;

    @JsonProperty("cost_type")
    private CostType costType;

    @JsonProperty("users_min")
    private Integer usersMin;

    @JsonProperty("users_max")
    private Integer usersMax;

    private String translateStatus(){
        if (status == ArticleStatus.OPEN)
            return "ABIERTO";
        return "CERRADO";
    }

    @JsonIgnore
    public String getDetailedString() {
        StringBuilder sb = new StringBuilder();
        sb.append("*ESTADO:* ").append(translateStatus()).append("\n");
        sb.append("*NOMBRE:* ").append(name).append("\n");
        sb.append("*IMAGEN:* ").append(image).append("\n");
        sb.append("*ENLACE:* ").append(link).append("\n");
        sb.append("*LO QUE EL USUARIO OBTIENE:* ").append(userGets).append("\n");
        sb.append("*COSTO:* ").append(cost).append("\n");
        sb.append("*CANTIDAD MAXIMA DE SUSCRIPTOS:* ").append(usersMax).append("\n");
        sb.append("*CANTIDAD MINIMA DE SUSCRIPTOS:* ").append(usersMin).append("\n");
        sb.append("*FECHA LIMITE:* ").append(deadline).append("\n");
        sb.append("*FECHA DE CREACION:* ").append(creationDate).append("\n");
        sb.append("*USUARIOS SUSCRIPTOS HASTA EL MOMENTO:* ").append(annotationsCounter.toString()).append("\n");
        return sb.toString();

    }

}
