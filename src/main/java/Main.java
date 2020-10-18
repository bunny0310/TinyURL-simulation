import database.DropwizardMongoDBConfiguration;
import database.MongoManaged;
import resources.URLShorteningResource;
import services.MongoService;
import com.mongodb.MongoClient;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main extends Application<DropwizardMongoDBConfiguration>{

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String ...args) throws Exception {
        new Main().run(args);
    }

    @Override
    public void initialize(Bootstrap<DropwizardMongoDBConfiguration> b) {
    }

    @Override
    public void run(DropwizardMongoDBConfiguration config, Environment env)
            throws Exception {
        logger.info(config.getMongoHost() + "");
        MongoClient mongoClient = new MongoClient(config.getMongoHost(), config.getMongoPort());
        MongoManaged mongoManaged = new MongoManaged(mongoClient);
        env.lifecycle().manage(mongoManaged);
        MongoDatabase db = mongoClient.getDatabase(config.getMongoDB());
        MongoCollection<Document> collection = db.getCollection(config.getCollectionName());
        MongoCollection<Document> usersCollection = db.getCollection("usersCollection");
        logger.info("Registering RESTful API resources");
        env.jersey().register(new URLShorteningResource(collection,usersCollection, new MongoService()));
    }
}

