package services;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import database.models.Employee;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.*;

public class MongoService {

    public void insertOne(MongoCollection<Document> collection, Document document) {
        try {
            collection.insertOne(document);
            Logger logger = LoggerFactory.getLogger("info");
            logger.info("success ");
        }catch(Exception e) {
            Logger logger = LoggerFactory.getLogger("info");
            logger.info("error " + e);
        }
    }

    public void insertMany(MongoCollection<Document> collection, List<Document> documents) {
        collection.insertMany(documents);
    }

    public List<Document> find(MongoCollection<Document> collection) {
        return collection.find().into(new ArrayList<>());
    }

    public Document lastInsertedObjectId(MongoCollection<Document> collection) {
        FindIterable<Document> cursor = collection.find().sort(new BasicDBObject("_id", -1)).limit(1);
        return cursor.iterator().next();
    }

    public List<Document> findByKey(MongoCollection<Document> collection, String key, String value) {
        BasicDBObject query = new BasicDBObject();
        query.put(key, value);
        return collection.find(query).into(new ArrayList<>());
    }

    public List<Document> findByCriteria(MongoCollection<Document> collection, String key, int lessThanValue, int greaterThanValue, int sortOrder) {
        List<Document> documents = new ArrayList<>();
        FindIterable iterable = collection.find(and(lt(key, lessThanValue),
                gt(key, greaterThanValue))).sort(new Document(key, sortOrder));
        iterable.into(documents);
        return documents;
    }

//    public void updateOneEmployee(MongoCollection<Document> collection, String key1, String key2, String key3, Employee employee) {
//        collection.updateOne(new Document(key1, employee.getName()),
//                new Document("$set", new Document(key2, employee.getDepartment()).append(key3, employee.getSalary())));
//    }

    public void deleteOne(MongoCollection<Document> collection, String key, String value) {
        collection.deleteOne(eq(key, value));
    }
}