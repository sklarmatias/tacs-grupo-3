package ar.edu.utn.frba.tacs.repository;

import ar.edu.utn.frba.tacs.model.User;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;
import java.util.Map;

public class MongoDBConnector {

    private String connectionString = System.getenv("connectionString");
    private MongoClient mongoClient = MongoClients.create(connectionString);
    private MongoDatabase database = mongoClient.getDatabase("admin");
    public String insert(Document doc, String collectionName){
        MongoCollection<Document> collection = database.getCollection(collectionName);
        ObjectId objectId = new ObjectId();
        doc.append("_id", objectId);
        collection.insertOne(doc);
        return  objectId.toString();
    }
    public Document selectById(String collectionName, String id){
        MongoCollection<Document> collection = database.getCollection(collectionName);
        return collection.find(Filters.eq("_id",new ObjectId(id))).first();
    }
    public List<Document> selectAll(String collectionName){
        MongoCollection<Document> collection = database.getCollection(collectionName);
        return collection.find().into(new ArrayList<Document>());
    }
    public List<Document> selectByCondition(String collectionName, Map<String,Object> conditions){
        MongoCollection<Document> collection = database.getCollection(collectionName);
        return collection.find(Filters.and(crearFiltros(conditions))).into(new ArrayList<Document>());
    }
    public void deleteById(String collectionName, String id){
        MongoCollection<Document> collection = database.getCollection(collectionName);
        collection.deleteOne(Filters.eq("_id",new ObjectId(id)));
    }
    public void closeConnection(){
        mongoClient.close();
    }
    public User.UserDTO DocumentToUser(Document doc){
        User user = new User();
        user.fromDocument(doc);
        return user.convertToDTO();
    }
    Iterable<Bson> crearFiltros(Map<String,Object> conditions){
        List<Bson> list = new ArrayList<>();
        for (String key : conditions.keySet()){
            list.add(Filters.eq(key,conditions.get(key)));
        }
        return list;
    }
}
