package resources;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import database.models.URL;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.MongoService;
import cache.LRUCache;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)

public class URLShorteningResource {

    private MongoCollection<Document> collection;
    private MongoCollection<Document> usersCollection;
    private MongoService mongoService;
    private LRUCache<String, Integer> countCache;
    private LRUCache<String, String> urlsCache;


    public URLShorteningResource(MongoCollection<Document> collection, MongoCollection<Document> usersCollection, MongoService mongoService) {
        this.collection = collection;
        this.usersCollection = usersCollection;
        this.mongoService = mongoService;
        this.countCache = new LRUCache<>(2);
        this.urlsCache = new LRUCache<>(20);
    }

    @POST()
    @Path("/write")
    public Response write(URL url) {

        if(url.getLongURL().equals("")) {
            return Response.status(422).entity("Invalid format").build();
        }

        String userToken = url.getUserToken();
        String[] credentials = userToken.split("@");
        if(credentials.length != 2) {
            return Response.status(401).entity("Unauthorized - cannot split credentials, invalid userToken format").build();
        }
        String username = credentials[0];
        String password = credentials[1];

        List<Document> users = mongoService.findByKey(usersCollection, "username", username);

        if(users.size() != 1) {
            return Response.status(401).entity("Unauthorized - user with the provided username not found!").build();
        }
        Document user = users.get(0);
        if(!user.get("password").equals(password)) {
            return Response.status(401).entity("Unauthorized - user with the provided password not found!").build();
        }

        //generate a 62bit hash string from the id
        String alphaLower = "abcdefghijklmnopqrstuvwxyz";
        char[] map = (alphaLower + alphaLower.toUpperCase() + "0123456789").toCharArray();

        String shortUrl = "";
        Integer n = countCache.get("count");
        if(n == null) {
            n = mongoService.lastInsertedObjectId(collection).getInteger("id");
        }

        n += 1014;
        int temp = n;


        while(n != 0) {
            shortUrl += map[n % 62];
            n = n / 62;
        }

        shortUrl = new StringBuilder(shortUrl).reverse().toString();

        //create a new URL object with the current timestamp + 24 hours(in millisecs) passed in for the timestamp field.
        long ms = System.currentTimeMillis() + 86400000;
        URL urlObj = new URL(temp + 1, shortUrl, url.getLongURL(), url.getUserToken(), new Timestamp(ms));
        Gson gson = new Gson();
        mongoService.insertOne(collection, new Document(BasicDBObject.parse(gson.toJson(urlObj))));

        countCache.put("count", temp + 1);
        this.urlsCache.put(shortUrl, urlObj.getLongURL());
        return Response.status(200).entity(shortUrl).build();
    }

    @GET()
    @Path("/read/{id}")
    public Response read(@PathParam("id") final String shortUrl) {

        //try to find the url in the cache
        String longUrl = this.urlsCache.get(shortUrl);

        //else look up in the database
        if(longUrl == null) {
            Object list = mongoService.findByKey(collection, "shortURL", shortUrl);
            List<Document> results = (List<Document>)list;
            if(results.size() == 0) {
                return Response.status(200).entity("no url found").build();
            }
            longUrl = (String)results.get(0).get("longURL");
        }
        URI target = URI.create(longUrl);
        return Response.temporaryRedirect(target).build();
    }

}