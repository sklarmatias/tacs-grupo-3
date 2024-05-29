package ar.edu.utn.frba.tacs.repository.objectMappers;

import ar.edu.utn.frba.tacs.model.Annotation;
import ar.edu.utn.frba.tacs.model.Article;
import ar.edu.utn.frba.tacs.model.ArticleStatus;
import ar.edu.utn.frba.tacs.model.CostType;
import org.bson.Document;
import org.bson.types.ObjectId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MongoArticleMapper {

    public static Article convertDocumentToArticle(Document document) {
        Article article = new Article();
        article.setId(String.valueOf(document.getObjectId("_id")));
        article.setName(document.getString("name"));
        article.setImage(document.getString("image"));
        article.setLink(document.getString("link"));
        article.setUserGets(document.getString("userGets"));
        article.setStatus(ArticleStatus.valueOf(document.getString("status")));
        article.setDeadline(document.getDate("deadline"));
        article.setOwner(document.getString("owner"));
        article.setCreationDate(document.getDate("creationDate"));
        article.setAnnotationsCounter(document.getInteger("annotationsCounter"));
        article.setCost(document.getDouble("cost"));
        article.setCostType(CostType.valueOf(document.getString("costType")));
        article.setUsersMin(document.getInteger("usersMin"));
        article.setUsersMax(document.getInteger("usersMax"));

        List<Document> annotationDocs = (List<Document>) document.get("annotations");
        if (annotationDocs != null && !annotationDocs.isEmpty()) {
            List<Annotation> annotations = annotationDocs.stream()
                    .map(MongoAnnotationMapper::convertDocumentToAnnotation)
                    .collect(Collectors.toList());
            article.setAnnotations(annotations);
        } else{
            article.setAnnotations(new ArrayList<>());
        }

        // Handle annotations list if needed
        return article;
    }

    public static Document convertArticleToDocument(Article article) {
        Document document = new Document();
        if (article.getId() != null) {
            document.append("_id", new ObjectId(article.getId()));
        }
        document.append("name", article.getName())
                .append("image", article.getImage())
                .append("link", article.getLink())
                .append("userGets", article.getUserGets())
                .append("status", article.getStatus().toString())
                .append("deadline", article.getDeadline())
                .append("owner", article.getOwner())
                .append("creationDate", article.getCreationDate())
                .append("annotationsCounter", article.getAnnotationsCounter())
                .append("cost", article.getCost())
                .append("costType", article.getCostType().toString())
                .append("usersMin", article.getUsersMin())
                .append("usersMax", article.getUsersMax());
        // Handle annotations list if needed
        return document;
    }

}
