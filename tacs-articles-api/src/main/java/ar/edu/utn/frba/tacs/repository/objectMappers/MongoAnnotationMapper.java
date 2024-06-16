package ar.edu.utn.frba.tacs.repository.objectMappers;

import ar.edu.utn.frba.tacs.model.Annotation;
import org.bson.Document;

public class MongoAnnotationMapper {

    public static Annotation convertDocumentToAnnotation(Document document) {
        Annotation annotation = new Annotation();
        annotation.setUser(MongoUserMapper.convertDocumentToUserDTO((Document) document.get("user")));
        annotation.setDate(document.getDate("date"));
        return annotation;
    }

    public static Document convertAnnotationToDocument(Annotation annotation) {
        Document document = new Document();
        document.append("user", MongoUserMapper.convertUserDTOToDocument(annotation.getUser()))  // Asumiendo que User tiene un método getId()
                .append("date", annotation.getDate());
        return document;
    }

}
