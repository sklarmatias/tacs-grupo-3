package ar.edu.utn.frba.tacs.model;

import ar.edu.utn.frba.tacs.repository.MongoDBConnector;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class User {

    static MongoDBConnector dbConnector = new MongoDBConnector();
    @Setter
    private String id;

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
        this.id = dbConnector.insert("users",toDocument());
    }
    public User(String id){
        fromDocument(dbConnector.selectById("users",id));
    }
    public User(String email,String pass){
        Map<String,Object> conditions = new HashMap<>();
        conditions.put("email",email);
        conditions.put("pass",pass);
        fromDocument((dbConnector.selectByCondition("users",conditions).getFirst()));
    }
    public User(Document document){
        fromDocument(document);
    }

    public void addAnnotation(Annotation annotation){
        this.getAnnotations().add(annotation);
    }

    public UserDTO convertToDTO(){
        return new UserDTO(this);
    }

    @NoArgsConstructor
    public static class UserDTO{

        public String id;

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
        public Document toDocument() {
            Document document = new Document();
            if (id != null) {
                document.append("_id", new ObjectId(id));
            }
            document.append("name", name)
                    .append("surname", surname)
                    .append("email", email)
                    .append("pass", pass);

            return document;
        }

        public void fromDocument(Document document) {
            this.id = document.getObjectId("_id").toString();
            this.name = document.getString("name");
            this.surname = document.getString("surname");
            this.email = document.getString("email");
            this.pass = document.getString("pass");
        }
    }

    public Document toDocument() {
        Document document = new Document();
        if (id != null) {
            document.append("_id", new ObjectId(id));
        }
        document.append("name", name)
                .append("surname", surname)
                .append("email", email)
                .append("pass", pass);

        if (postedArticles != null) {
            List<Document> articleDocs = new ArrayList<>();
            for (Article article : postedArticles) {
                articleDocs.add(article.toDocument());
            }
            document.append("postedArticles", articleDocs);
        }

        if (annotations != null) {
            List<Document> annotationDocs = new ArrayList<>();
            for (Annotation annotation : annotations) {
                annotationDocs.add(annotation.toDocument());
            }
            document.append("annotations", annotationDocs);
        }

        return document;
    }

    public void fromDocument(Document document) {
        this.id = document.getObjectId("_id").toString();
        this.name = document.getString("name");
        this.surname = document.getString("surname");
        this.email = document.getString("email");
        this.pass = document.getString("pass");

        List<Document> articleDocs = (List<Document>) document.get("postedArticles");
        if (articleDocs != null) {
            this.postedArticles = new ArrayList<>();
            for (Document articleDoc : articleDocs) {
                Article article = new Article();
                article.fromDocument(articleDoc);
                this.postedArticles.add(article);
            }
        }

        List<Document> annotationDocs = (List<Document>) document.get("annotations");
        if (annotationDocs != null) {
            this.annotations = new ArrayList<>();
            for (Document annotationDoc : annotationDocs) {
                Annotation annotation = new Annotation();
                annotation.fromDocument(annotationDoc);
                this.annotations.add(annotation);
            }
        }
    }

    public static List<UserDTO> getAllUsers(){
        List<Document> documents = dbConnector.selectAll("users");
        return documents.stream().map(User::DocumentToUser).toList();
    }
    private static User.UserDTO DocumentToUser(Document doc){
        return new User(doc).convertToDTO();
    }
}
