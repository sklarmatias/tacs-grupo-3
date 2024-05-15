package ar.edu.utn.frba.tacs;

import ar.edu.utn.frba.tacs.model.*;
import org.junit.Assert;
import org.junit.Test;
import java.util.ArrayList;

public class ArticleTest {

    @Test
    public void testCloseSuccessfulArticle(){
        Article article = new Article();
        article.setAnnotationsCounter(2);
        article.setStatus(ArticleStatus.OPEN);
        article.setUsersMin(2);
        article.close();
        Assert.assertEquals(ArticleStatus.CLOSED_SUCCESS, article.getStatus());
    }

    @Test
    public void testCloseFailedArticle(){
        Article article = new Article();
        article.setAnnotationsCounter(2);
        article.setStatus(ArticleStatus.OPEN);
        article.setUsersMin(3);
        article.close();
        Assert.assertEquals(ArticleStatus.CLOSED_FAILED, article.getStatus());
    }

    @Test
    public void testSignUpUserSuccess(){
        // create users
        User owner = new User();
        owner.setId("1");
        User user1 = new User();
        user1.setId("2");
        user1.setAnnotations(new ArrayList<>());
        // create article
        Article article = new Article();
        article.setAnnotations(new ArrayList<>());
        article.setAnnotationsCounter(0);
        article.setUsersMax(2);
        article.setStatus(ArticleStatus.OPEN);
        article.setOwner(owner.getId());
        article.setUsersMin(1);
        article.signUpUser(user1);
        Integer expected = 1;
        Assert.assertEquals(expected, article.getAnnotationsCounter());
        Assert.assertEquals(user1, article.getAnnotations().get(0).getUser());
        Assert.assertEquals(article.getAnnotations().get(0), user1.getAnnotations().get(0));
    }

    @Test
    public void testSignUpUserFailUsersMax(){
        // create users
        User owner = new User();
        owner.setId("1");
        User user1 = new User();
        user1.setId("2");
        user1.setAnnotations(new ArrayList<>());
        // create article
        Article article = new Article();
        article.setAnnotations(new ArrayList<>());
        article.setAnnotationsCounter(2);
        article.setUsersMax(2);
        article.setStatus(ArticleStatus.OPEN);
        article.setOwner(owner.getId());
        article.setUsersMin(1);
        Assert.assertThrows(IllegalArgumentException.class, () -> article.signUpUser(user1));
    }

    @Test
    public void testSignUpUserFailOwnerSignUp(){
        // create users
        User owner = new User();
        owner.setId("1");
        // create article
        Article article = new Article();
        article.setAnnotations(new ArrayList<>());
        article.setAnnotationsCounter(2);
        article.setUsersMax(2);
        article.setStatus(ArticleStatus.OPEN);
        article.setOwner(owner.getId());
        article.setUsersMin(1);
        Assert.assertThrows(IllegalArgumentException.class, () -> article.signUpUser(owner));
    }

    @Test
    public void testSignUpUserFailUserAlreadySignedUp(){
        // create users
        User owner = new User();
        owner.setId("1");
        User user1 = new User();
        user1.setId("2");
        user1.setAnnotations(new ArrayList<>());
        // create article
        Article article = new Article();
        article.setAnnotations(new ArrayList<>());
        article.setAnnotationsCounter(0);
        article.setUsersMax(2);
        article.setStatus(ArticleStatus.OPEN);
        article.setOwner(owner.getId());
        article.setUsersMin(1);
        article.signUpUser(user1);
        Assert.assertThrows(IllegalArgumentException.class, () -> article.signUpUser(user1));
    }

    @Test
    public void testSignUpUserFailClosedArticle(){
        // create users
        User owner = new User();
        owner.setId("1");
        User user1 = new User();
        user1.setId("2");
        user1.setAnnotations(new ArrayList<>());
        // create article
        Article article = new Article();
        article.setAnnotations(new ArrayList<>());
        article.setAnnotationsCounter(0);
        article.setUsersMax(2);
        article.setStatus(ArticleStatus.CLOSED_SUCCESS);
        article.setOwner(owner.getId());
        article.setUsersMin(1);
        Assert.assertThrows(IllegalArgumentException.class, () -> article.signUpUser(user1));
    }

//    ObjectMapper mapper = new ObjectMapper();
//    UsersRepository usersRepository = new InMemoryUsersRepository();
//    ArticlesRepository articlesRepository = new InMemoryArticlesRepository();
//    @Test
//    public void testAddUsuarioLocal(){
//        usersRepository.delete();
//        articlesRepository.delete();
//        User user = new User("John","Doe","john.doe@example.com");
//        usersRepository.save(user);
//        Article article = new Article("Articulo1","001.jpg","","1kg", user, new Date(),100.00, CostType.PER_USER,1,5);
//        articlesRepository.save(article);
//        Annotation annotation = new Annotation(usersRepository.find(1));
//        Article articulo = articlesRepository.find(1);
//        User user = new User("John","Doe","john.doe@example.com");
//        usersRepository.save(user);
//        articulo.signUpUser(user);
//        articlesRepository.update(1,articulo);
//        Article art1 = articlesRepository.find(1);
//        assertEquals(1,art1.getAnnotations().get(0).getUser().getId().intValue());
//    }
//
//
//        @Test
//        public void testCreacion() throws Exception {
//            //Creo el cliente
//            WebClient client = WebClient.create("http://localhost:8080/restapp/usuarios/");
//
//            //Vacio la base
//            Response r = client.delete();
//            assertEquals(Response.Status.NO_CONTENT.getStatusCode(), r.getStatus());
//
//            //Creo un usuario de prueba
//            User user = new User("John","Doe","john.doe@example.com");
//
//            //Inserto el usuario
//            r = client.type("application/json").post(mapper.writeValueAsString(user));
//            assertEquals(Response.Status.NO_CONTENT.getStatusCode(), r.getStatus());
//            repous.guardarUsuario(user);
//
//            client = WebClient.create("http://localhost:8080/restapp/articulos/");
//            //Vacio la base
//            r = client.delete();
//            assertEquals(Response.Status.NO_CONTENT.getStatusCode(), r.getStatus());
//
//            //Creo un articulo de prueba
//            Article articulo1 = new Article("Articulo1","001.jpg","","1kg", ArticleStatus.OPEN,new Date(),user,100.00, CostType.POR_PERSONA,1,5);
//
//            //Inserto en la base
//            r = client.type("application/json").post(mapper.writeValueAsString(articulo1));
//            assertEquals(Response.Status.NO_CONTENT.getStatusCode(), r.getStatus());
//
//            //Obtengo el articulo
//            r = client.path("/1/").accept("application/json").get();
//            assertEquals(Response.Status.OK.getStatusCode(), r.getStatus());
//            String articuloResponse = r.readEntity(String.class);
//            Article articulo2 = mapper.readValue(articuloResponse, Article.class);
//
//            //Valido los valores
//            Assert.assertEquals(articulo1.getNombre(), articulo2.getNombre());
//            Assert.assertEquals(articulo1.getCosto(), articulo2.getCosto());
//        }
//        @Test
//    public void testAgregarUsuario() throws Exception{
//        repoar.borrarArticulos();
//        repous.borrarUsuarios();
//            //Creo el cliente
//            WebClient client = WebClient.create("http://localhost:8080/restapp/usuarios/");
//
//            //Vacio la base
//            Response r = client.delete();
//            assertEquals(Response.Status.NO_CONTENT.getStatusCode(), r.getStatus());
//
//            //Creo un usuario de prueba
//            User user = new User("John","Doe","john.doe@example.com");
//
//            //Inserto el usuario
//            r = client.type("application/json").post(mapper.writeValueAsString(user));
//            assertEquals(Response.Status.NO_CONTENT.getStatusCode(), r.getStatus());
//            repous.guardarUsuario(user);
//
//            //Creo el cliente
//            client = WebClient.create("http://localhost:8080/restapp/articulos/");
//
//            //Vacio la base
//            r = client.delete();
//            assertEquals(Response.Status.NO_CONTENT.getStatusCode(), r.getStatus());
//
//            //Creo un articulo de prueba
//            Article articulo1 = new Article("Articulo2","002.jpg","","1kg", ArticleStatus.OPEN,new Date(),user,150.00, CostType.POR_PERSONA,1,5);
//
//            //Inserto en la base
//            r = client.type("application/json").post(mapper.writeValueAsString(articulo1));
//            assertEquals(Response.Status.NO_CONTENT.getStatusCode(), r.getStatus());
//
//            //Creo una anotacion
//            Anotacion anotacion = new Anotacion(repous.obtenerUsuario(1));
//
//            //Inserto la anotacion
//            r = client.path("/1/usuarios").type("application/json").post(mapper.writeValueAsString(anotacion));
//            assertEquals(Response.Status.NO_CONTENT.getStatusCode(), r.getStatus());
//
//            //Obtengo lista de anotaciones
//            r = client.accept("application/json").get();
//            String anotacionesResponse = r.readEntity(String.class);
//            List<Anotacion> anotaciones = mapper.readValue(anotacionesResponse, new TypeReference<List<Anotacion>>(){});
//            assertFalse(anotaciones.isEmpty());
//            assertEquals(1,anotaciones.get(0).getUsuario().getId().intValue());
//        }

}
