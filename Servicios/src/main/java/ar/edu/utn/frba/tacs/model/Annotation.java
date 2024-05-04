package ar.edu.utn.frba.tacs.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.Date;

@Getter
@NoArgsConstructor
public class Annotation {

    private User user;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date date;

    public Annotation(User user){
        this.user = user;
        this.date = new Date();
        user.addAnnotation(this);
    }

    public AnnotationDTO convertToDTO(){
        return new AnnotationDTO(this);
    }

    public static class AnnotationDTO{

        public User.UserDTO user;

        public Date date;

        public AnnotationDTO(Annotation annotation){
            user = annotation.getUser().convertToDTO();
            date = annotation.getDate();
        }

    }

}
