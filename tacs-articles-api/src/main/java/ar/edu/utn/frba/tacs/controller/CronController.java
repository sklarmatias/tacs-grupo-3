package ar.edu.utn.frba.tacs.controller;

import ar.edu.utn.frba.tacs.model.Article;
import ar.edu.utn.frba.tacs.service.ArticleService;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import java.util.List;

@Path("/crons")
public class CronController {

    private final ArticleService articleService;

    public CronController(){
        articleService= new ArticleService(System.getenv("CON_STRING"));
    }
    public CronController(ArticleService articleService){
        this.articleService = articleService;
    }

    private String deadlineResponseMsg(List<Article> expiredArticles){
        String s = String.format("Closed articles: %d\n", expiredArticles.size());
        for (Article article: expiredArticles)
            s += String.format("Article %s closed as %s\n", article.getId(), article.getStatus());
        return s;
    }

    @POST
    @Path("/deadline")
    public String closeExpiredArticles() {
        System.out.println("[CRON] Closing expired articles...");
        List<Article> expiredArticles = articleService.closeExpiredArticles();
        String response = deadlineResponseMsg(expiredArticles);
        System.out.println("[CRON] said:\n" + response);
        return response;
    }

}
