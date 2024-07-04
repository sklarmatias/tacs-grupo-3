package ar.edu.utn.frba.tacs.model;

import lombok.Getter;
import lombok.Setter;

@Getter
public class LoggedUser {
    private final String sessionId;
    private final String userId;
    private final Client client;
    private final String name;
    private final String surname;
    private final String email;
    public LoggedUser(String sessionId, String userId, Client client, String name, String surname, String email){
        this.sessionId = sessionId;
        this.userId = userId;
        this.client = client;
        this.name = name;
        this.surname = surname;
        this.email = email;
    }
    public LoggedUserDTO convertToDTO(){
        return new LoggedUserDTO(this);
    }
    @Getter
    public static class LoggedUserDTO{
        private String sessionId;
        private String client;
        private String name;
        private String surname;
        private String email;
        public LoggedUserDTO(LoggedUser user){
            this.sessionId = user.getSessionId();
            this.client = String.valueOf(user.getClient());
            this.name = user.getName();
            this.surname = user.getSurname();
            this.email = user.getEmail();
        }
    }
}
