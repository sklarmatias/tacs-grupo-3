package ar.edu.utn.frba.tacs.repository;

import com.mongodb.client.*;
import org.bson.Document;

import java.util.List;

public class MongoDBConnector {

    String connectionString = System.getenv("connectionString");
    MongoClient mongoClient = MongoClients.create(connectionString);
    MongoDatabase database = mongoClient.getDatabase("admin");
    public void insert(Document doc, String collectionName){
        MongoCollection<Document> collection = database.getCollection(collectionName);

        collection.insertOne(doc);
    }
    public Document select(String collectionName){
        MongoCollection<Document> collection = database.getCollection(collectionName);
        return collection.find().first();
    }
    public void closeConnection(){
        mongoClient.close();
    }
}
