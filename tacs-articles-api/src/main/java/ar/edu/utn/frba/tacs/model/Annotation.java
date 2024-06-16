package ar.edu.utn.frba.tacs.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class Annotation {

    private User.UserDTO user;

    @JsonProperty("created_at")
    private Date date;

    public Annotation(User.UserDTO user){
        this.user = user;
        this.date = new Date();
    }


}
