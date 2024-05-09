package ar.edu.utn.frba.tacs.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.Document;

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

    private String pass;

    private List<Article> postedArticles;

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

    @NoArgsConstructor
    public static class UserDTO{

        public Integer id;

        public String name;

        public String surname;

        public String email;

        public String pass;

        public UserDTO(User user){
            this.id = user.getId();
            this.name = user.getName();
            this.surname = user.getSurname();
            this.email = user.getEmail();
            this.pass = user.getPass();
        }
    }
    public Document toDocument() {
        Document document = new Document();
        if (id != null) {
            document.append("_id", id);
        }
        document.append("name", name)
                .append("surname", surname)
                .append("email", email)
                .append("pass", pass);

        if (postedArticles != null) {
            List<Document> articleDocs = new ArrayList<>();
            for (Article article : postedArticles) {
//                articleDocs.add(article.toDocument());
            }
            document.append("postedArticles", articleDocs);
        }

        if (annotations != null) {
            List<Document> annotationDocs = new ArrayList<>();
            for (Annotation annotation : annotations) {
//                annotationDocs.add(annotation.toDocument());
            }
            document.append("annotations", annotationDocs);
        }

        return document;
    }

    // Method to populate the class with data from a MongoDB document
    public void fromDocument(Document document) {
//        this.id = document.getInteger("_id");
        this.name = document.getString("name");
        this.surname = document.getString("surname");
        this.email = document.getString("email");
        this.pass = document.getString("pass");

        List<Document> articleDocs = (List<Document>) document.get("postedArticles");
        if (articleDocs != null) {
            this.postedArticles = new ArrayList<>();
            for (Document articleDoc : articleDocs) {
                Article article = new Article();
//                article.fromDocument(articleDoc);
                this.postedArticles.add(article);
            }
        }

        List<Document> annotationDocs = (List<Document>) document.get("annotations");
        if (annotationDocs != null) {
            this.annotations = new ArrayList<>();
            for (Document annotationDoc : annotationDocs) {
                Annotation annotation = new Annotation();
//                annotation.fromDocument(annotationDoc);
                this.annotations.add(annotation);
            }
        }
    }
}
