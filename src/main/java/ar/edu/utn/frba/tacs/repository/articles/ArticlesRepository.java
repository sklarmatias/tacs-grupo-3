package ar.edu.utn.frba.tacs.repository.articles;

import ar.edu.utn.frba.tacs.model.Article;

import java.util.List;

public interface ArticlesRepository {

    List<Article> findAll();

    Article find(Integer id);

    Integer save(Article article);

    void update(Integer id, Article article);

    void delete();

}
