package ar.edu.utn.frba.tacs.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Setter
@Getter
@NoArgsConstructor
public class Article {

    private String id;

    private String name;

    private String image;

    private String link;

    @JsonProperty("user_gets")
    private String userGets;

    private ArticleStatus status;

    private Date deadline;

    private String owner;

    @JsonProperty("created_at")
    private Date creationDate;

    private List<Annotation> annotations;

    @JsonProperty("annotations_counter")
    private Integer annotationsCounter;

    private Double cost;

    @JsonProperty("cost_type")
    private CostType costType;

    @JsonProperty("users_min")
    private Integer usersMin;

    @JsonProperty("users_max")
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
        if (owner == null){
            throw new IllegalArgumentException("Owner doesn't exist.");
        }
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
    }

    private boolean isSignedUp(User user){
        return this.getAnnotations().stream().anyMatch(annotation -> annotation.getUser().getId().equals(user.getId()));
    }

    public Annotation signUpUser(User user){
            if (this.isClosed())
                throw new IllegalArgumentException("1");
            if (Objects.equals(this.getOwner(), user.getId()))
                throw new IllegalArgumentException("2");
            if (isSignedUp(user))
                throw new IllegalArgumentException("3");

            Annotation annotation = new Annotation(user.convertToDTO());
            this.annotations.add(annotation);
            this.annotationsCounter++;
            if (annotationsCounter.equals(usersMax)){
                close();
            }
            return annotation;
    }

    public boolean isClosed(){
        return this.status != ArticleStatus.OPEN;
    }

    public boolean isExpired(){
        return new Date().after(this.deadline);
    }

    public void close(){
        if (isClosed())
            throw new IllegalArgumentException("1");
        if (this.annotationsCounter >= this.usersMin)
            this.status = ArticleStatus.CLOSED_SUCCESS;
        else this.status = ArticleStatus.CLOSED_FAILED;
    }

    public boolean wasClosedSuccessfully(){
        return this.status.equals(ArticleStatus.CLOSED_SUCCESS);
    }

    public ArticleDTO convertToDTO(){
        return new Article.ArticleDTO(this);
    }

    public static class ArticleDTO{

        public String id;

        public String name;

        public String image;

        public String link;

        @JsonProperty("user_gets")
        public String userGets;

        public ArticleStatus status;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        public Date deadline;

        public String owner;

        @JsonProperty("creation_date")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        public Date creationDate;

        @JsonProperty("annotation_counter")
        public Integer annotationsCounter;

        public Double cost;

        @JsonProperty("cost_type")
        public CostType costType;

        @JsonProperty("users_min")
        public Integer usersMin;

        @JsonProperty("users_max")
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

    }

}
