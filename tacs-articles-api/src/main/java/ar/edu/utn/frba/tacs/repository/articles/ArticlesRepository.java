package ar.edu.utn.frba.tacs.repository.articles;

import ar.edu.utn.frba.tacs.model.Article;

import java.util.List;

public interface ArticlesRepository {

    List<Article> findAll();

    Article find(String id);
    List<Article> filter(String userid);
    String save(Article article);

    void update(String id, Article article);

    void delete();

}
