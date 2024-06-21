package ar.edu.utn.frba.tacs.repository.articles;

import ar.edu.utn.frba.tacs.model.Annotation;
import ar.edu.utn.frba.tacs.model.Article;

import java.util.List;
import java.util.Map;

public interface ArticlesRepository {

    List<Article> findAll();
    List<Article> findAllCondition(Map<String, Object> conditions);
    Article find(String id);
    List<Article> filter(String userid);
    String save(Article article);

    void update(String id, Article article);

    void updateAddAnnotation(String id, Annotation annotation);
    void delete(String id);

}
