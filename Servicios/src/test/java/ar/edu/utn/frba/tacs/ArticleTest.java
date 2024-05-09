package ar.edu.utn.frba.tacs;

import ar.edu.utn.frba.tacs.model.*;
import ar.edu.utn.frba.tacs.repository.MongoDBConnector;
import ar.edu.utn.frba.tacs.repository.articles.ArticlesRepository;
import ar.edu.utn.frba.tacs.repository.articles.impl.InMemoryArticlesRepository;
import ar.edu.utn.frba.tacs.repository.user.UsersRepository;
import ar.edu.utn.frba.tacs.repository.user.impl.InMemoryUsersRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.core.Response;
import org.apache.cxf.jaxrs.client.WebClient;
import org.junit.Assert;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.*;

public class ArticleTest {
    @Test
    public void testAddArticulo() throws ParseException {
        MongoDBConnector dbConnector = new MongoDBConnector();
        User user = new User("juan","perez","jp@gmail.com","123456");
        Article article = new Article("articulo","imagen","","algo",user.getId(),new SimpleDateFormat("yyyy-MM-dd").parse("2025-01-01"),2000.00,CostType.PER_USER,2,5);
        Article newarticle = new Article(article.getId());
        assertEquals(article.getId(),newarticle.getId());
        dbConnector.deleteById("users",user.getId());
        assertNull(dbConnector.selectById("users",user.getId()));
        dbConnector.deleteById("articles",article.getId());
        assertNull(dbConnector.selectById("users", article.getId()));
        dbConnector.closeConnection();
    }
    @Test
    public void testArticuloCerrarOk() throws ParseException {
        MongoDBConnector dbConnector = new MongoDBConnector();
        User user = new User("juan","perez","jp@gmail.com","123456");
        User user2 = new User("jose","perez","jp@hotmail.com","654321");
        Article article = new Article("articulo","imagen","","algo", user.getId(), new SimpleDateFormat("yyyy-MM-dd").parse("2025-01-01"),2000.00,CostType.PER_USER,1,5);
        article.signUpUser(user2);
        article.close();
        Article newarticle = new Article(article.getId());
        assertEquals(1,newarticle.getAnnotations().size());
        assertEquals(ArticleStatus.CLOSED_SUCCESS,newarticle.getStatus());
        dbConnector.deleteById("users",user.getId());
        assertNull(dbConnector.selectById("users",user.getId()));
        dbConnector.deleteById("users",user2.getId());
        assertNull(dbConnector.selectById("users",user2.getId()));
        dbConnector.deleteById("articles",article.getId());
        assertNull(dbConnector.selectById("users", article.getId()));
        dbConnector.closeConnection();
    }
}
