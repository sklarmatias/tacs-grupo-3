package org.tacsbot.parser.annotation;

import org.tacsbot.model.Annotation;

import java.io.IOException;
import java.util.List;

public interface AnnotationParser {

    List<Annotation> parseJSONToAnnotation(String json);

    String parseAnnotationsToJSON(List<Annotation> annotations) throws IOException;
}
