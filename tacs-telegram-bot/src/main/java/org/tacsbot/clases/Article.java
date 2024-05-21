package org.tacsbot.clases;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;
import java.util.List;

public class Article {


    public String id;

    public String name;

    public String image;

    public String link;

    public String userGets;

    public ArticleStatus status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    public Date deadline;

    public String owner;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    public Date creationDate;

    public List<Annotation> annotations;

    public Integer annotationsCounter;

    public Double cost;

    public CostType costType;

    public Integer usersMin;

    public Integer usersMax;
    public String getString(){
        String str = id.toString() + "\t" + name + "\t" + image + "\t" + link + "\t" + userGets + "\t" + cost;
        return str;
    }

    public String getDetailedString() {
        StringBuilder sb = new StringBuilder();
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
