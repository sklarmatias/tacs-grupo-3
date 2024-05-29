package ar.edu.utn.frba.tacs;

import ar.edu.utn.frba.tacs.model.Annotation;
import ar.edu.utn.frba.tacs.model.Article;
import ar.edu.utn.frba.tacs.model.User;
import org.junit.Assert;
import org.junit.Test;

public class UserTest {

    @Test
    public void userWithAnnotationHasInteracted(){
        User user = new User("thiago", "cabrera", "thiago@tacs.com","tacs2024");
        user.addAnnotation(new Annotation());

        Assert.assertTrue(user.hasInteracted());

    }

    @Test
    public void userWithArticleHasInteracted(){
        User user = new User("thiago", "cabrera", "thiago@tacs.com","tacs2024");
        user.getPostedArticles().add(new Article());

        Assert.assertTrue(user.hasInteracted());

    }

    @Test
    public void userHasntInteracted(){
        User user = new User("thiago", "cabrera", "thiago@tacs.com","tacs2024");
        Assert.assertFalse(user.hasInteracted());

    }

}
