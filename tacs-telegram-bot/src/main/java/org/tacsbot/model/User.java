package org.tacsbot.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class User {

    private String id;

    private String name;

    private String surname;

    private String email;

    private String pass;

    public User(String email, String password){
        this.email = email;
        this.pass = password;
    }

    @Override
    public String toString(){
        return String.format("id:%s;name:%s;surname:%s;email:%s;pass:%s;\n",
                id, name, surname, email, pass);
    }

}
