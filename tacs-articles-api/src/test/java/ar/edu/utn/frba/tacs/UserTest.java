package ar.edu.utn.frba.tacs;

import ar.edu.utn.frba.tacs.model.Annotation;
import ar.edu.utn.frba.tacs.model.Article;
import ar.edu.utn.frba.tacs.model.User;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.mongo.transitions.Mongod;
import de.flapdoodle.embed.mongo.transitions.RunningMongodProcess;
import de.flapdoodle.reverse.TransitionWalker;
import org.bson.Document;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
