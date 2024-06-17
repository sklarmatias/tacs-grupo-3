package org.tacsbot.redis;

import lombok.Setter;
import org.tacsbot.handlers.impl.ArticleCreationHandler;
import org.tacsbot.handlers.impl.ArticleCreationStep;
import org.tacsbot.model.Article;
import org.tacsbot.model.User;
import org.tacsbot.parser.article.ArticleParser;
import org.tacsbot.parser.article.impl.ArticleJSONParser;
import org.tacsbot.parser.user.UserParser;
import org.tacsbot.parser.user.impl.UserJSONParser;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Pipeline;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class RedisService {

    JedisPool jedisPool;
    ArticleParser articleParser;

    UserParser userParser;

    @Setter
    Long expirationSeconds;

    public RedisService() throws RuntimeException {
        this(
                new JedisPool(new JedisPoolConfig(), "rediss://red-ci72t4unqql0ld93qmig:ZqPkNUQ6Tm397x11FsLGJOjt5zXNTG6v@oregon-redis.render.com:6379"),
                new ArticleJSONParser(),
                new UserJSONParser(),
                120L
        );
    }

    public RedisService(JedisPool jedisPool, ArticleParser articleParser, UserParser userParser, Long expirationSeconds) throws RuntimeException {
        this.jedisPool = jedisPool;
        ping();
        this.articleParser = articleParser;
        this.userParser = userParser;
        this.expirationSeconds = expirationSeconds;
    }

    private void ping() throws RuntimeException {
        try (Jedis jedis = jedisPool.getResource()) {
            if (!Objects.equals(jedis.ping(), "PONG")){
                throw new RuntimeException("Connection with DB failed");
            }
        }
    }

    public void saveArticles(String chatId, List<Article> articles){
        // Get the pool and use the database
        try (Jedis jedis = jedisPool.getResource()) {
            Pipeline pipeline = jedis.pipelined();
            for (Article article: articles){
                pipeline.zadd(chatId, 0, articleParser.parseArticleToJSON(article));
            }
            pipeline.expire(chatId, expirationSeconds);
            pipeline.sync();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Article> getArticles(String chatId){
        // Get the pool and use the database
        try (Jedis jedis = jedisPool.getResource()) {
            List<String> savedArticles = jedis.zrange(chatId, 0, -1);
            jedis.expire(chatId, expirationSeconds);
            return savedArticles.stream().map((s) -> articleParser.parseJSONToArticle(s)).collect(Collectors.toList());
        }
    }

    public void deleteChatId(String chatId){
        // Get the pool and use the database
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.del(chatId);
        }
    }

    public void saveArticleCreationHandler(String chatId, ArticleCreationHandler articleCreationHandler){
        deleteChatId(chatId);
        try (Jedis jedis = jedisPool.getResource()) {
            Pipeline pipeline = jedis.pipelined();
            pipeline.zadd(chatId, 0, String.valueOf(articleCreationHandler.getCurrentStep()));
            pipeline.zadd(chatId, 1, articleParser.parseArticleToJSON(articleCreationHandler.getArticle()));
            pipeline.expire(chatId, expirationSeconds);
            pipeline.sync();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ArticleCreationHandler getArticleCreationHandler(String chatId){
        try (Jedis jedis = jedisPool.getResource()) {
            List<String> response = jedis.zrange(chatId, 0, 1);
            return new ArticleCreationHandler(chatId, articleParser.parseJSONToArticle(response.get(1)), ArticleCreationStep.valueOf(response.get(0)));
        }
    }

    public User getUser(String chatId) {
        try (Jedis jedis = jedisPool.getResource()) {
            return userParser.parseJSONToUser(jedis.get("u:" + chatId));
        }
    }

    public void saveUser(String chatId, User user) throws IOException {
        String jsonUser = userParser.parseUserToJSON(user);
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set("u:" + chatId, jsonUser);
        }
    }

    public String getChatIdOfUser(String userId) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.get("ch:" + userId);
        }
    }

    // key: "ch:{userId}" value: "{chatId}"
    public void saveChatIdOfUser(String userId, String chatId) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set("ch:" + userId, chatId);
        }
    }

    // key: "ch:{userId}" value: "{chatId}"
    public void deleteChatIdOfUser(String userId) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.del("ch:" + userId);
        }
    }

    public void closeConnection(){
        this.jedisPool.close();
    }

}
