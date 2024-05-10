package ar.edu.utn.frba.tacs.model;

import ar.edu.utn.frba.tacs.repository.MongoDBConnector;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.ws.rs.PathParam;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.*;

@Setter
@Getter
@NoArgsConstructor
public class Article {
    static MongoDBConnector dbConnector = new MongoDBConnector();

    private String id;

    private String name;

    private String image;

    private String link;

    private String userGets;

    private ArticleStatus status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date deadline;

    private String owner;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date creationDate;

    private List<Annotation> annotations;

    private Integer annotationsCounter;

    private Double cost;

    private CostType costType;

    private Integer usersMin;

    private Integer usersMax;

    public Article(String name, String image, String link, String userGets, String owner,
                   Date deadline, Double cost, CostType costType, Integer usersMin, Integer usersMax) {
        if (usersMin < 0)
            throw new IllegalArgumentException("usersMin has to be >= 0.");
        if (usersMax < 0)
            throw new IllegalArgumentException("usersMax has to be >= 0.");
        if (usersMin > usersMax)
            throw new IllegalArgumentException("usersMin has to be <= usersMax.");
        if (deadline != null && deadline.before(new Date()))
            throw new IllegalArgumentException("Deadline has to be in the future.");
        this.name = name;
        this.image = image;
        this.link = link;
        this.userGets = userGets;
        this.owner = owner;
        this.status = ArticleStatus.OPEN;
        this.deadline = deadline;
        this.creationDate = new Date();
        this.annotations = new ArrayList<>();
        this.annotationsCounter = 0;
        this.cost = cost;
        this.costType = costType;
        this.usersMin = usersMin;
        this.usersMax = usersMax;
        this.id = dbConnector.insert("articles",toDocument());
    }
    public Article(String id){
        fromDocument(dbConnector.selectById("articles",id));
    }
    public Article(Document document){
        fromDocument(document);
    }

    private boolean isSignedUp(User user){
        return this.getAnnotations().stream().anyMatch(annotation -> annotation.getUser().equals(user));
    }

    public void signUpUser(User user){
        if (this.isClosed())
            throw new IllegalArgumentException("Article is closed.");
        if (this.isFull())
            throw new IllegalArgumentException("Article is full.");
        if (Objects.equals(this.getOwner(), user.getId()))
            throw new IllegalArgumentException("Article owner can't sign up to his own article.");
        if (isSignedUp(user))
            throw new IllegalArgumentException("User already signed up.");
        Annotation annotation = new Annotation(user.convertToDTO(),this.convertToDTO());
        this.annotations.add(annotation);
        this.annotationsCounter ++;
        dbConnector.updateInsertInArray("articles",id,"annotations",annotation.toDocument());
        dbConnector.update("articles",id,"annotationsCounter",annotationsCounter);
        if(Objects.equals(annotationsCounter, usersMax)){
            close();
        }
    }

    public boolean isFull(){
        return this.annotationsCounter >= this.usersMax;
    }

    public boolean isClosed(){
        return this.status != ArticleStatus.OPEN;
    }

    public boolean isExpired(){
        return new Date().after(this.deadline);
    }

    public void close(){
        if (isClosed())
            throw new IllegalArgumentException("Article already closed.");
        // TODO notify users
        if (this.annotationsCounter >= this.usersMin)
            this.status = ArticleStatus.CLOSED_SUCCESS;
        else this.status = ArticleStatus.CLOSED_FAILED;
        dbConnector.update("articles",id,"status",status.toString());
    }

    public boolean wasClosedSuccessfully(){
        return this.status.equals(ArticleStatus.CLOSED_SUCCESS);
    }

    public ArticleDTO convertToDTO(){
        return new Article.ArticleDTO(this);
    }

    @NoArgsConstructor
    public static class ArticleDTO{

        public String id;

        public String name;

        public String image;

        public String link;

        public String userGets;

        public ArticleStatus status;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        public Date deadline;

        public String owner;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        public Date creationDate;

        public Integer annotationsCounter;

        public Double cost;

        public CostType costType;

        public Integer usersMin;

        public Integer usersMax;

        public ArticleDTO(Article article){

            this.id = article.getId();
            this.name = article.getName();
            this.image = article.getImage();
            this.link = article.getLink();
            this.userGets = article.getUserGets();
            this.status = article.getStatus();
            this.deadline = article.getDeadline();
            this.creationDate = article.getCreationDate();
            this.annotationsCounter = article.getAnnotationsCounter();
            this.cost = article.getCost();
            this.costType = article.getCostType();
            this.usersMax = article.getUsersMax();
            this.usersMin = article.getUsersMin();
            this.owner = article.getOwner();
        }


        public Document toDocument() {
            Document document = new Document();
            if (id != null) {
                document.append("_id", new ObjectId(id));
            }
            document.append("name", name)
                    .append("image", image)
                    .append("link", link)
                    .append("userGets", userGets)
                    .append("status", status.toString())
                    .append("deadline", deadline)
                    .append("owner", owner)
                    .append("creationDate", creationDate)
                    .append("annotationsCounter", annotationsCounter)
                    .append("cost", cost)
                    .append("costType", costType.toString())
                    .append("usersMin", usersMin)
                    .append("usersMax", usersMax);

            return document;
        }

        public void fromDocument(Document document) {
            this.id = document.getObjectId("_id").toString();
            this.name = document.getString("name");
            this.image = document.getString("image");
            this.link = document.getString("link");
            this.userGets = document.getString("userGets");
            this.status = ArticleStatus.valueOf(document.getString("status"));
            this.deadline = document.getDate("deadline");
            this.owner = document.getString("owner");
            this.creationDate = document.getDate("creationDate");
            this.annotationsCounter = document.getInteger("annotationsCounter");
            this.cost = document.getDouble("cost");
            this.costType = CostType.valueOf(document.getString("costType"));
            this.usersMin = document.getInteger("usersMin");
            this.usersMax = document.getInteger("usersMax");
        }
    }

    public Document toDocument() {
        Document document = new Document();
        if (id != null) {
            document.append("_id", new ObjectId(id));
        }
        document.append("name", name)
                .append("image", image)
                .append("link", link)
                .append("userGets", userGets)
                .append("status", status.toString())
                .append("deadline", deadline)
                .append("owner", owner)
                .append("creationDate", creationDate)
                .append("annotationsCounter", annotationsCounter)
                .append("cost", cost)
                .append("costType", costType.toString())
                .append("usersMin", usersMin)
                .append("usersMax", usersMax);

        if (annotations != null) {
            List<Document> annotationDocs = new ArrayList<>();
            for (Annotation annotation : annotations) {
                annotationDocs.add(annotation.toDocument());
            }
            document.append("annotations", annotationDocs);
        }

        return document;
    }

    public void fromDocument(Document document) {
        this.id = document.getObjectId("_id").toString();
        this.name = document.getString("name");
        this.image = document.getString("image");
        this.link = document.getString("link");
        this.userGets = document.getString("userGets");
        this.status = ArticleStatus.valueOf(document.getString("status"));
        this.deadline = document.getDate("deadline");
        this.owner = document.getString("owner");
        this.creationDate = document.getDate("creationDate");
        this.annotationsCounter = document.getInteger("annotationsCounter");
        this.cost = document.getDouble("cost");
        this.costType = CostType.valueOf(document.getString("costType"));
        this.usersMin = document.getInteger("usersMin");
        this.usersMax = document.getInteger("usersMax");

        List<Document> annotationDocs = (List<Document>) document.get("annotations");
        if (annotationDocs != null) {
            this.annotations = new ArrayList<>();
            for (Document annotationDoc : annotationDocs) {
                Annotation annotation = new Annotation();
                annotation.fromDocument(annotationDoc);
                this.annotations.add(annotation);
            }
        }
    }

    public static List<ArticleDTO> getAllArticles(){
        List<Document> documents = dbConnector.selectAll("articles");
        return documents.stream().map(Article::DocumentToArticle).toList();
    }
    private static ArticleDTO DocumentToArticle(Document doc){
        return new Article(doc).convertToDTO();
    }
    public static List<ArticleDTO> listUserArticles(String id) {
		Map<String,Object> conditions = new HashMap<>();
        conditions.put("owner",id);
        List<Document> documents = dbConnector.selectByCondition("articles",conditions);
        return documents.stream().map(Article::DocumentToArticle).toList();
    }
}
