package server;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Objects;

public class Server {

    public static MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
    public static MongoDatabase database = mongoClient.getDatabase("AwayFromMeCovidDB");
    public static MongoCollection<Document> portsCollection = database.getCollection("portsConfig");

    public static ArrayList<String> getConfig(String config) {
        Document result = portsCollection.find(new Document("Name", config)).first();
        if (result != null) {
            Document configDocument = result.get("Configs", Document.class);
            String port = configDocument.getString("Port");
            String ip = configDocument.getString("IP");
            ArrayList<String> configList = new ArrayList<>();
            configList.add(String.valueOf(port));
            configList.add(ip);
            return configList;
        }
        return new ArrayList<>();
    }

    public static void main(String[] args) {
        new Thread(new BroadcastHandler(Objects.requireNonNull(getConfig("Broadcast").get(0)),
                Objects.requireNonNull(getConfig("Broadcast").get(1)))).start();
        new Thread(new MulticastHandler()).start();
        new Thread(new UsersHandler()).start();
    }
}