package org.tacsbot.model;

import java.util.List;


public class User {

    public String id;

    public String name;

    public String surname;

    public String email;

    public String pass;

    private List<Article> postedArticles;

    private List<Annotation> annotations;

}
