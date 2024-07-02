package ar.edu.utn.frba.tacs.model;

import lombok.Getter;

@Getter
public class LoggedUser {
    private String id;
    private String userId;
    private Client client;
    public LoggedUser(String id, String userId, Client client){
        this.id = id;
        this.userId = userId;
        this.client = client;
    }
}
