package ar.edu.utn.frba.tacs.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.Document;

import java.util.Date;
import java.util.List;

@Getter
@NoArgsConstructor
public class Annotation {

    private User.UserDTO user;

    private Article.ArticleDTO article;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date date;

    public Annotation(User.UserDTO user, Article.ArticleDTO article){
        this.user = user;
        this.date = new Date();
        this.article = article;
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
            User.UserDTO u = new User.UserDTO();
            u.fromDocument(userDoc);
            this.user = u;
        }

        Document articleDoc = (Document) document.get("article");
        if (articleDoc != null) {
            Article.ArticleDTO a = new Article.ArticleDTO();
            a.fromDocument(articleDoc);
            this.article = a;
        }

        this.date = document.getDate("date");
    }

}
