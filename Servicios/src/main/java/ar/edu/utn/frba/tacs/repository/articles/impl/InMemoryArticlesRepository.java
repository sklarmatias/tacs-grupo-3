package ar.edu.utn.frba.tacs.repository.articles.impl;

import ar.edu.utn.frba.tacs.model.Article;
import ar.edu.utn.frba.tacs.repository.articles.ArticlesRepository;
import java.util.ArrayList;
import java.util.List;

public class InMemoryArticlesRepository implements ArticlesRepository {

    private static final List<Article> ARTICLES = new ArrayList<>();

    private static Integer key = 0;

    @Override
    public List<Article> findAll() {
        return new ArrayList<>(ARTICLES);
    }

    @Override
    public Article find(Integer id) {
        return ARTICLES.stream().filter(art -> art.getId().equals(id)).findFirst().get();
    }
    @Override
    public List<Article> filter(int userid) {
//        return ARTICLES.stream().filter(art -> art.getOwner()==userid).toList();
        return new ArrayList<>();
    }

    @Override
    public Integer save(Article article) {
        key++;
//        article.setId(key);
        ARTICLES.add(article);
        return key;
    }

    @Override
    public void update(Integer id, Article other) {
        Article artoriginal = ARTICLES.stream().filter(art -> art.getId().equals(id)).findFirst().get();
        if (other.getName() != null) {
            artoriginal.setName(other.getName());
        }
        if (other.getImage() != null) {
            artoriginal.setImage(other.getImage());
        }
        if (other.getLink() != null) {
            artoriginal.setLink(other.getLink());
        }
        if (other.getUserGets() != null) {
            artoriginal.setUserGets(other.getUserGets());
        }
        if (other.getDeadline() != null) {
            artoriginal.setDeadline(other.getDeadline());
        }
//        if (other.getOwner() != 0) {
//            artoriginal.setOwner(other.getOwner());
//        }
        if (other.getCost() != null) {
            artoriginal.setCost(other.getCost());
        }
        if (other.getCostType() != null) {
            artoriginal.setCostType(other.getCostType());
        }
        if (other.getUsersMin() != null) {
            artoriginal.setUsersMin(other.getUsersMin());
        }
        if (other.getUsersMax() != null) {
            artoriginal.setUsersMax(other.getUsersMax());
        }
    }

    @Override
    public void delete(){
        ARTICLES.clear();
    }
}
