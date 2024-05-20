package org.tacsbot.clases;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;
import java.util.List;

public class Article {


    public Integer id;

    public String name;

    public String image;

    public String link;

    public String userGets;

    public ArticleStatus status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    public Date deadline;

    public int owner;

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
}
