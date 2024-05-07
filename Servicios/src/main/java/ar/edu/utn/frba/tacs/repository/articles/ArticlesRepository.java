package ar.edu.utn.frba.tacs.repository.articles;

import ar.edu.utn.frba.tacs.model.Article;

import java.util.List;

public interface ArticlesRepository {

    List<Article> findAll();

    Article find(Integer id);
    List<Article> filter(int userid);
    Integer save(Article article);

    void update(Integer id, Article article);

    void delete();

}
