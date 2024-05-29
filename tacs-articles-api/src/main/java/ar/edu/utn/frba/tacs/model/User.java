package ar.edu.utn.frba.tacs.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class User {
    @Setter
    private String id;

    private String name;

    private String surname;

    private String email;

    private String pass;

    @JsonIgnore
    private List<Article> postedArticles;

    @JsonIgnore
    private List<Annotation> annotations;

    public boolean hasInteracted(){
        return !(this.postedArticles.isEmpty() && this.annotations.isEmpty());
    }

    public User(String name, String surname, String email, String pass){
        this.name = name;
        this.surname = surname;
        this.email=email;
        this.pass = pass;
        this.annotations = new ArrayList<>();
        this.postedArticles = new ArrayList<>();
    }

    public void addAnnotation(Annotation annotation){
        this.getAnnotations().add(annotation);
    }

    public UserDTO convertToDTO(){
        return new UserDTO(this);
    }
    @Getter
    @Setter
    @NoArgsConstructor
    public static class UserDTO{

        public String id;

        public String name;

        public String surname;

        public String email;

        public UserDTO(User user){
            this.id = user.getId();
            this.name = user.getName();
            this.surname = user.getSurname();
            this.email = user.getEmail();
        }
    }

}
