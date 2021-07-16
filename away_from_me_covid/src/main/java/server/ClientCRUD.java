package server;

import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.client.model.Updates;
import common.UserObj;
import org.bson.Document;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ClientCRUD {
    private static final MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
    private static final MongoDatabase database = mongoClient.getDatabase("AwayFromMeCovidDB");
    private static final MongoCollection<Document> usersCollection = database.getCollection("usersCollection");
    private static final MongoCollection<Document> countyCollection = database.getCollection("countyCollection");

    public synchronized static boolean addNewUser(UserObj user) {
        Document userDocument = new Document("_id", user.getHealthNumber())
                .append("name", user.getName())
                .append("password", user.getPassword())
                .append("county", user.getCounty())
                .append("healthState", user.getHealthState())
                .append("contactsList", Collections.EMPTY_LIST);

        FindIterable<Document> result = usersCollection.find(new Document("_id", user.getHealthNumber()));
        Object results = null;
        for (Document document : result) {
            results = document.get("_id");
        }
        if (results == null) {
            usersCollection.insertOne(userDocument);
            return true;
        }
        return false;
    }

    public synchronized static boolean checkLogin(int healthNumber, String password) {
        Document result = usersCollection.find(new Document("_id", healthNumber)).first();

        if (result != null) {
            Object userPassword = result.get("password");
            Object userHealthNumber = result.get("_id");
            return password.equals(userPassword) && userHealthNumber.equals(healthNumber);
        }
        return false;
    }

    public synchronized static boolean updateUserHealthState(UserObj user) {
        FindOneAndUpdateOptions options = new FindOneAndUpdateOptions();
        options.upsert(true);
        options.returnDocument(ReturnDocument.AFTER);
        Document result = usersCollection.findOneAndUpdate(new Document("_id", user.getHealthNumber()), new Document("$set", new Document("healthState", true)), options);
        return result != null;
    }

    public synchronized static boolean resultOfAnTest(UserObj user) {
        Random rd = new Random();
        boolean testResult = rd.nextBoolean();
        FindOneAndUpdateOptions options = new FindOneAndUpdateOptions();
        options.upsert(true);
        options.returnDocument(ReturnDocument.AFTER);
        usersCollection.findOneAndUpdate(new Document("_id", user.getHealthNumber()),
                new Document("$set", new Document("healthState", testResult)), options);
        return testResult;
    }

    public synchronized static boolean defineContact(int contactHealthNumber, UserObj currentUser) {
        FindOneAndUpdateOptions options = new FindOneAndUpdateOptions();
        options.upsert(true);
        options.returnDocument(ReturnDocument.AFTER);
        ArrayList<Integer> contactsList = getContactsList(currentUser.getHealthNumber());
        for(int i = 0; i<contactsList.size(); i++){
            if(contactHealthNumber == contactsList.get(i)){
                return false;
            }
        }
        Document result0 = usersCollection.find(new Document("_id", contactHealthNumber)).first();
        if(result0 != null) {
            Document result1 = usersCollection.findOneAndUpdate(new Document("_id", currentUser.getHealthNumber()), Updates.push("contactsList", contactHealthNumber), options);
            Document result2 = usersCollection.findOneAndUpdate(new Document("_id", contactHealthNumber), Updates.push("contactsList", currentUser.getHealthNumber()), options);
            return true;
        }
        return false;
    }

    public synchronized static int getInfectedPeopleByCounty(String county) {
        List<Document> result = new ArrayList<>();
        usersCollection.find(Filters.and(Filters.eq("county", county), Filters.eq("healthState", true))).into(result);
        return result.size();
    }

    public synchronized static ArrayList<Object> getUserInfo(int contactId) {
        ArrayList<Object> userConfig = new ArrayList<>();
        Document result = usersCollection.find(new Document("_id", contactId)).first();
        if (result != null) {
            int port = result.getInteger("port");
            String ip = result.getString("ip");
            userConfig.add(port);
            userConfig.add(ip);
        }
        return userConfig;
    }

    public synchronized static String getCounty(int healthNumber) {
        Document result = usersCollection.find(new Document("_id", healthNumber)).first();
        if (result != null) {
            Object county = result.get("county");
            return county.toString();
        }
        return null;
    }

    public synchronized static int getTotalInfectedPeople() {
        List<Document> result = new ArrayList<>();
        usersCollection.find(Filters.eq("healthState", true)).into(result);
        return result.size();
    }

    public synchronized static ArrayList<Integer> getContactsList(int id) {
        Document result = usersCollection.find(new Document("_id", id)).first();
        if (result != null) {
            return (ArrayList<Integer>) result.getList("contactsList", Integer.class);
        }
        return new ArrayList<>();
    }

    public synchronized static ArrayList<Object> findCounty(String county) {
        Document result = countyCollection.find(new Document("Name", county)).first();
        if (result != null) {
            Document configDocument = result.get("Configs", Document.class);
            int port = configDocument.getInteger("Port");
            String ip = configDocument.getString("IP");
            ArrayList<Object> countyConfig = new ArrayList<>();
            countyConfig.add(ip);
            countyConfig.add(port);
            return countyConfig;
        }
        return null;
    }

    public synchronized static boolean getCountiesName(String county) {
        Document result = countyCollection.find(new Document("Name", county.toLowerCase())).first();
        return result != null;
    }
}
