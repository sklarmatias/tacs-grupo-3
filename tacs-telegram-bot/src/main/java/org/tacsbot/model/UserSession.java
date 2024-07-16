package org.tacsbot.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserSession {

    private String sessionId;
    private String name;
    private String surname;
    private String email;

    public UserSession(String sessionId){
        this.sessionId = sessionId;
    }

    @Override
    public boolean equals(Object o){
        if (!o.getClass().equals(getClass()))
            return false;
        UserSession u2 = (UserSession) o;
        return Objects.equals(sessionId, u2.getSessionId()) &&
                Objects.equals(name, u2.getName()) &&
                Objects.equals(surname, u2.getSurname()) &&
                Objects.equals(email, u2.getEmail());
    }

}
