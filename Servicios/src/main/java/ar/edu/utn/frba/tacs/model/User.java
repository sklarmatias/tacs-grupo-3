package ar.edu.utn.frba.tacs.model;

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
    private Integer id;

    private String name;

    private String surname;

    private String email;

    private List<Article> postedArticles;

    private List<Annotation> annotations;

    public boolean hasInteracted(){
        return !(this.postedArticles.isEmpty() && this.annotations.isEmpty());
    }

    public User(String name, String surname, String email){
        this.name = name;
        this.surname = surname;
        this.email=email;
        this.annotations = new ArrayList<>();
        this.postedArticles = new ArrayList<>();
    }

    public void addAnnotation(Annotation annotation){
        this.getAnnotations().add(annotation);
    }

    public UserDTO convertToDTO(){
        return new UserDTO(this);
    }

    @NoArgsConstructor
    public static class UserDTO{

        public Integer id;

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