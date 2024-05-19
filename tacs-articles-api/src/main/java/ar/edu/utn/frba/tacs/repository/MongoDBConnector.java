package ar.edu.utn.frba.tacs.repository;

import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.mongodb.client.model.Aggregates.set;

public class MongoDBConnector {

    private MongoClient mongoClient = MongoClients.create(System.getenv("CON_STRING"));
    private MongoDatabase database = mongoClient.getDatabase(System.getenv("MONGO_DB"));
    public String insert(String collectionName,Document doc){
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
        return collection.find(Filters.and(createFilters(conditions))).into(new ArrayList<Document>());
    }
    public void deleteById(String collectionName, String id){
        MongoCollection<Document> collection = database.getCollection(collectionName);
        collection.deleteOne(Filters.eq("_id",new ObjectId(id)));
    }
    public void update(String collectionName, String id, Document document){
        MongoCollection<Document> collection = database.getCollection(collectionName);
        Document query = new Document().append("_id",  new ObjectId(id));
        Bson update = Updates.combine(createUpdate(document));
        UpdateResult result = collection.updateOne(query,update);
    }
    public void updateInsertInArray(String collectionName, String id, String key, Document document){
        MongoCollection<Document> collection = database.getCollection(collectionName);
        Document query = new Document().append("_id",  new ObjectId(id));
        Bson updates = Updates.addToSet(key,document);
        UpdateResult result = collection.updateOne(query,updates);
    }
    public void closeConnection(){
        mongoClient.close();
    }

    Iterable<Bson> createFilters(Map<String,Object> conditions){
        List<Bson> list = new ArrayList<>();
        for (String key : conditions.keySet()){
            list.add(Filters.eq(key,conditions.get(key)));
        }
        return list;
    }
    List<Bson> createUpdate(Document document){
        List<Bson> list = new ArrayList<>();
        for (String key : document.keySet()){
            list.add(Updates.set(key,document.get(key)));
        }
        return list;
    }
}
