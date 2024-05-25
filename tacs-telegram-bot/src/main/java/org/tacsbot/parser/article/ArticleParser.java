package org.tacsbot.parser.article;

import org.tacsbot.model.Article;
import java.io.IOException;
import java.util.List;

public interface ArticleParser {

    String parseArticleToJSON(Article article) throws IOException;

    Article parseJSONToArticle(String json);

    List<Article> parseJSONToArticleList(String json);

}
