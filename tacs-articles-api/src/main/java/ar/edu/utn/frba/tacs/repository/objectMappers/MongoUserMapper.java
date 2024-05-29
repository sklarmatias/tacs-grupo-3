package ar.edu.utn.frba.tacs.repository.objectMappers;

import ar.edu.utn.frba.tacs.model.Annotation;
import ar.edu.utn.frba.tacs.model.Article;
import ar.edu.utn.frba.tacs.model.User;
import org.bson.Document;
import org.bson.types.ObjectId;
import java.util.ArrayList;
import java.util.List;

public class MongoUserMapper {

    public static User convertDocumentToUser(Document document) {

        if (document == null) return null;

        User user = new User();
        user.setId(document.getObjectId("_id").toString());
        user.setName(document.getString("name"));
        user.setSurname(document.getString("surname"));
        user.setEmail(document.getString("email"));
        user.setPass(document.getString("pass"));

        List<Document> articleDocs = (List<Document>) document.get("postedArticles");
        if (articleDocs != null) {
            user.setPostedArticles(new ArrayList<>());
            for (Document articleDoc : articleDocs) {
                Article article = new Article();
                MongoArticleMapper.convertDocumentToArticle(articleDoc);
                user.getPostedArticles().add(article);
            }
        }

        List<Document> annotationDocs = (List<Document>) document.get("annotations");
        if (annotationDocs != null) {
            user.setAnnotations(new ArrayList<>());
            for (Document annotationDoc : annotationDocs) {
                Annotation annotation = new Annotation();
                MongoAnnotationMapper.convertDocumentToAnnotation(annotationDoc);
                user.getAnnotations().add(annotation);
            }
        }

        return user;
    }

    public static Document convertUserToDocument(User user) {
        Document document = new Document();
        if (user.getId() != null) {
            document.append("_id", new ObjectId(user.getId()));
        }
        document.append("name", user.getName())
                .append("surname", user.getSurname())
                .append("email", user.getEmail())
                .append("pass", user.getPass());

        List<Document> articleDocs = new ArrayList<>();
        if (user.getPostedArticles() != null) {
            for (Article article : user.getPostedArticles()) {
                articleDocs.add(MongoArticleMapper.convertArticleToDocument(article));
            }
            document.append("postedArticles", articleDocs);
        }

        List<Document> annotationDocs = new ArrayList<>();
        if (user.getAnnotations() != null) {
            for (Annotation annotation : user.getAnnotations()) {
                annotationDocs.add(MongoAnnotationMapper.convertAnnotationToDocument(annotation));
            }
            document.append("annotations", annotationDocs);
        }

        return document;
    }
    @Deprecated(since = "2024/05/28", forRemoval = true)
    public static Document convertUserDTOToDocument(User.UserDTO user) {
        Document document = new Document();
        if (user.getId() != null) {
            document.append("_id", new ObjectId(user.getId()));
        }
        document.append("name", user.getName())
                .append("surname", user.getSurname())
                .append("email", user.getEmail());
        return document;
    }

    @Deprecated(since = "2024/05/28", forRemoval = true)
    public static User.UserDTO convertDocumentToUserDTO(Document document) {
        User.UserDTO user = new User.UserDTO();
        user.setId(document.getObjectId("_id").toString());
        user.setName(document.getString("name"));
        user.setSurname(document.getString("surname"));
        user.setEmail(document.getString("email"));
        return user;
    }
}
