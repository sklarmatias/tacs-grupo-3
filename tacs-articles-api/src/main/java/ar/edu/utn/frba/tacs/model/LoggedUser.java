package ar.edu.utn.frba.tacs.model;

import lombok.Getter;
import lombok.Setter;

@Getter
public class LoggedUser {
    private String sessionId;
    @Setter
    private String userId;
    private Client client;
    private String name;
    private String surname;
    private String email;
    public LoggedUser(String sessionId, String userId, Client client, String name, String surname, String email){
        this.sessionId = sessionId;
        this.userId = userId;
        this.client = client;
        this.name = name;
        this.surname = surname;
        this.email = email;
    }
}
