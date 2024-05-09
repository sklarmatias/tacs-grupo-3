package ar.edu.utn.frba.tacs.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.Document;

import java.util.Date;

@Getter
@NoArgsConstructor
public class Annotation {

    private User user;

    private Article article;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date date;

    public Annotation(User user,Article article){
        this.user = user;
        this.date = new Date();
        this.article = article;
        user.addAnnotation(this);
    }

    public AnnotationDTO convertToDTO(){
        return new AnnotationDTO(this);
    }

    public static class AnnotationDTO{

        public User.UserDTO user;
        public Article.ArticleDTO article;

        public Date date;

        public AnnotationDTO(Annotation annotation){
            user = annotation.getUser().convertToDTO();
            date = annotation.getDate();
            article = annotation.getArticle().convertToDTO();
        }

    }
    public Document toDocument() {
        Document document = new Document();
        if (user != null) {
            document.append("user", user.toDocument());
        }
        if (article != null) {
            document.append("article", article.toDocument());
        }
        document.append("date", date);
        return document;
    }

    // Method to populate the class with data from a MongoDB document
    public void fromDocument(Document document) {
        Document userDoc = (Document) document.get("user");
        if (userDoc != null) {
            User u = new User();
            u.fromDocument(userDoc);
            this.user = u;
        }

        Document articleDoc = (Document) document.get("article");
        if (articleDoc != null) {
            Article a = new Article();
            a.fromDocument(articleDoc);
            this.article = a;
        }

        this.date = document.getDate("date");
    }

}
