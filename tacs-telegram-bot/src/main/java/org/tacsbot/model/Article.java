package org.tacsbot.model;

import com.fasterxml.jackson.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.text.SimpleDateFormat;
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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date creationDate;

    private List<Annotation> annotations;

    @JsonProperty("annotation_counter")
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
        sb.append(String.format("*COSTO:* $%,.2f\n", cost));
        sb.append("*CANTIDAD MAXIMA DE SUSCRIPTOS:* ").append(usersMax).append("\n");
        sb.append("*CANTIDAD MINIMA DE SUSCRIPTOS:* ").append(usersMin).append("\n");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        sb.append("*FECHA LIMITE:* ").append(simpleDateFormat.format(deadline)).append("\n");
        simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        sb.append("*FECHA DE CREACION:* ").append(simpleDateFormat.format(creationDate)).append("\n");
        sb.append("*SUBSCRIPCIONES:* ").append(annotationsCounter.toString()).append("\n");
        return sb.toString();

    }

}
