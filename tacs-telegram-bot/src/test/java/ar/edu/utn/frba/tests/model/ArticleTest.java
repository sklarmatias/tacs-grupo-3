package ar.edu.utn.frba.tests.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.tacsbot.model.Article;
import org.tacsbot.model.ArticleStatus;
import org.tacsbot.model.CostType;
import java.util.Date;

public class ArticleTest {

    private Article article;

    @Before
    public void createArticle(){
        article = new Article(
                "asdfghjklñ",
                "Muchas manzanas, muchas",
                "image.jpg",
                "http.com",
                "manzana",
                ArticleStatus.OPEN,
                new Date(1719786089L * 1000),
                "247",
                new Date(1712786089L * 1000),
                null,
                0,
                28750.5,
                CostType.TOTAL,
                2,
                8
        );
    }

    @Test
    public void detailedStringWithOneDecimalTest() {

        String expected = """
                *ESTADO:* ABIERTO
                *NOMBRE:* Muchas manzanas, muchas
                *IMAGEN:* image.jpg
                *ENLACE:* http.com
                *LO QUE EL USUARIO OBTIENE:* manzana
                *COSTO:* $28.750,50
                *CANTIDAD MAXIMA DE SUSCRIPTOS:* 8
                *CANTIDAD MINIMA DE SUSCRIPTOS:* 2
                *FECHA LIMITE:* 30/06/2024
                *FECHA DE CREACION:* 10/04/2024 18:54:49
                *SUBSCRIPCIONES:* 0
                """;

        Assert.assertEquals(expected, article.getDetailedString());

    }

    @Test
    public void detailedStringWithTwoDecimalsTest() {

        article.setCost(28750.75);

        String expected = """
                *ESTADO:* ABIERTO
                *NOMBRE:* Muchas manzanas, muchas
                *IMAGEN:* image.jpg
                *ENLACE:* http.com
                *LO QUE EL USUARIO OBTIENE:* manzana
                *COSTO:* $28.750,75
                *CANTIDAD MAXIMA DE SUSCRIPTOS:* 8
                *CANTIDAD MINIMA DE SUSCRIPTOS:* 2
                *FECHA LIMITE:* 30/06/2024
                *FECHA DE CREACION:* 10/04/2024 18:54:49
                *SUBSCRIPCIONES:* 0
                """;

        Assert.assertEquals(expected, article.getDetailedString());

    }

    @Test
    public void detailedStringWithRoundingTest() {

        article.setCost(28750.755);

        String expected = """
                *ESTADO:* ABIERTO
                *NOMBRE:* Muchas manzanas, muchas
                *IMAGEN:* image.jpg
                *ENLACE:* http.com
                *LO QUE EL USUARIO OBTIENE:* manzana
                *COSTO:* $28.750,76
                *CANTIDAD MAXIMA DE SUSCRIPTOS:* 8
                *CANTIDAD MINIMA DE SUSCRIPTOS:* 2
                *FECHA LIMITE:* 30/06/2024
                *FECHA DE CREACION:* 10/04/2024 18:54:49
                *SUBSCRIPCIONES:* 0
                """;

        Assert.assertEquals(expected, article.getDetailedString());

    }

}
