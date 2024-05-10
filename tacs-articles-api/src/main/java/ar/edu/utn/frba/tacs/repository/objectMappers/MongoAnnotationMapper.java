package ar.edu.utn.frba.tacs.repository.objectMappers;

import ar.edu.utn.frba.tacs.model.Annotation;
import ar.edu.utn.frba.tacs.model.User;
import org.bson.Document;

public class MongoAnnotationMapper {

    public static Annotation convertDocumentToAnnotation(Document document) {
        Annotation annotation = new Annotation();
        annotation.setUser((User) document.get("user")); // Asumiendo que hay un método para convertir el subdocumento a User
        annotation.setDate(document.getDate("date"));
        return annotation;
    }

    public static Document convertAnnotationToDocument(Annotation annotation) {
        Document document = new Document();
        document.append("user", annotation.getUser().getId())  // Asumiendo que User tiene un método getId()
                .append("date", annotation.getDate());
        return document;
    }

}
