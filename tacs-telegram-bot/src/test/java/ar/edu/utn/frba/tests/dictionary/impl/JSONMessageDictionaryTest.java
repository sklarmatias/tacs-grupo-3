package ar.edu.utn.frba.tests.dictionary.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.tacsbot.dictionary.impl.JSONMessageDictionary;
import org.tacsbot.model.*;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class JSONMessageDictionaryTest {

    private JsonNode spanishTranslations;

    private JSONMessageDictionary jsonMessageDictionary;


    @Before
    public void loadJSON() throws URISyntaxException, IOException {
        File file = new File(Objects.requireNonNull(getClass().getResource("/messages/es.json")).toURI());
        spanishTranslations = new ObjectMapper().readTree(file);
        jsonMessageDictionary = new JSONMessageDictionary();
    }

    @Test
    public void getHelpTranslation(){
        Assert.assertEquals(spanishTranslations.path("HELP").asText(), jsonMessageDictionary.getMessage("HELP", "es"));
    }

    @Test
    public void parseDateWithLocale(){

        String spanishExpected = """
                ESTADO: Abierto
                NOMBRE: name
                IMAGEN: image.jpg
                ENLACE: link.com
                LO QUE EL USUARIO OBTIENE: user gets!
                COSTO: $200.000,75 (Cada uno)
                CANTIDAD MAXIMA DE SUSCRIPTOS: 4
                CANTIDAD MINIMA DE SUSCRIPTOS: 2
                FECHA LIMITE: 18/5/24
                FECHA DE CREACION: 29/5/24 22:20:00
                SUBSCRIPCIONES: 0""";

        String englishExpected = """
                Status: Open
                Name: name
                Image: image.jpg
                Link: link.com
                What user gets: user gets!
                Cost: $200,000.75 (Each)
                Max subscriptions: 4
                Min subscriptions: 2
                Deadline: 5/18/24
                Created at: 5/29/24 10:20:00 PM
                Subscriptions: 0""";

        Article article = new Article(
                null,
                "name",
                "image.jpg",
                "link.com",
                "user gets!",
                ArticleStatus.OPEN,
                new Date(1716058008501L), //2024-05-30 00:00:00
                "qwerty",
                new Date(1717032000000L),
                null,
                0,
                200000.75,
                CostType.PER_USER,
                2,
                4
        );

        Assert.assertEquals(spanishExpected, jsonMessageDictionary.articleToString(article, "es"));

        Assert.assertEquals(englishExpected, jsonMessageDictionary.articleToString(article, "en"));
    }

    @Test
    public void parseDateWithLocaleRounding(){

        String spanishExpected = """
                ESTADO: Cerrado
                NOMBRE: name
                IMAGEN: image.jpg
                ENLACE: link.com
                LO QUE EL USUARIO OBTIENE: user gets!
                COSTO: $200.000,50 (Total)
                CANTIDAD MAXIMA DE SUSCRIPTOS: 4
                CANTIDAD MINIMA DE SUSCRIPTOS: 2
                FECHA LIMITE: 18/5/24
                FECHA DE CREACION: 29/5/24 22:20:00
                SUBSCRIPCIONES: 0""";

        String englishExpected = """
                Status: Closed
                Name: name
                Image: image.jpg
                Link: link.com
                What user gets: user gets!
                Cost: $200,000.50 (Total)
                Max subscriptions: 4
                Min subscriptions: 2
                Deadline: 5/18/24
                Created at: 5/29/24 10:20:00 PM
                Subscriptions: 0""";

        Article article = new Article(
                null,
                "name",
                "image.jpg",
                "link.com",
                "user gets!",
                ArticleStatus.CLOSED_FAILED,
                new Date(1716058008501L), //2024-05-30 00:00:00
                "qwerty",
                new Date(1717032000000L),
                null,
                0,
                200000.5,
                CostType.TOTAL,
                2,
                4
        );

        Assert.assertEquals(spanishExpected, jsonMessageDictionary.articleToString(article, "es"));

        Assert.assertEquals(englishExpected, jsonMessageDictionary.articleToString(article, "en"));
    }

    @Test
    public void parseArticlesList(){

        String expected = """
                1:
                ESTADO: Abierto
                NOMBRE: name
                IMAGEN: image.jpg
                ENLACE: link.com
                LO QUE EL USUARIO OBTIENE: user gets!
                COSTO: $200.000,50 (Total)
                CANTIDAD MAXIMA DE SUSCRIPTOS: 4
                CANTIDAD MINIMA DE SUSCRIPTOS: 2
                FECHA LIMITE: 18/5/24
                FECHA DE CREACION: 29/5/24 22:20:00
                SUBSCRIPCIONES: 0
                2:
                ESTADO: Abierto
                NOMBRE: name2
                IMAGEN: image2.jpg
                ENLACE: link2.com
                LO QUE EL USUARIO OBTIENE: user gets 2!
                COSTO: $200.000,50 (Total)
                CANTIDAD MAXIMA DE SUSCRIPTOS: 4
                CANTIDAD MINIMA DE SUSCRIPTOS: 2
                FECHA LIMITE: 18/5/24
                FECHA DE CREACION: 29/5/24 22:20:00
                SUBSCRIPCIONES: 0
                """;

        Article article1 = new Article(
                null,
                "name",
                "image.jpg",
                "link.com",
                "user gets!",
                ArticleStatus.OPEN,
                new Date(1716058008501L), //2024-05-30 00:00:00
                "qwerty",
                new Date(1717032000000L),
                null,
                0,
                200000.5,
                CostType.TOTAL,
                2,
                4
        );
        Article article2 = new Article(
                null,
                "name2",
                "image2.jpg",
                "link2.com",
                "user gets 2!",
                ArticleStatus.OPEN,
                new Date(1716058008501L), //2024-05-30 00:00:00
                "qwerty2",
                new Date(1717032000000L),
                null,
                0,
                200000.5,
                CostType.TOTAL,
                2,
                4
        );

        Assert.assertEquals(expected, jsonMessageDictionary.articleListToString(new ArrayList<>(List.of(article1,article2)), "es"));

    }

    @Test
    public void parseAnnotationLocally(){

        String expectedSpanish = """
                (18/5/24 15:46:48)
                Cabrera, Thiago (thiago@tacs.com)""";

        String expectedEnglish = """
                (5/18/24 3:46:48 PM)
                Cabrera, Thiago (thiago@tacs.com)""";

        Annotation annotation = new Annotation(
                new User(null, "Thiago", "Cabrera", "thiago@tacs.com", null),
                new Date(1716058008501L)
        );

        Assert.assertEquals(expectedSpanish, jsonMessageDictionary.annotationToString(annotation, "es"));

        Assert.assertEquals(expectedEnglish, jsonMessageDictionary.annotationToString(annotation, "en"));

    }

    @Test
    public void parseAnnotationListLocally(){

        String expectedSpanish = """
                (18/5/24 15:46:48)
                Cabrera, Thiago (thiago@tacs.com)
                (18/5/24 15:46:48)
                Cabrera, Thiago (thiago@tacs.com)
                """;

        Annotation annotation = new Annotation(
                new User(null, "Thiago", "Cabrera", "thiago@tacs.com", null),
                new Date(1716058008501L)
        );

        Assert.assertEquals(expectedSpanish, jsonMessageDictionary.annotationListToString(new ArrayList<>(List.of(annotation, annotation)), "es"));

    }

}
