package server;

import com.mongodb.client.*;
import org.bson.Document;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.TimerTask;

class CountyInfo extends TimerTask {

    public static MongoClient mongoClient;
    public static MongoDatabase database;
    public static MongoCollection<Document> countyCollection;
    public MulticastSocket multicastSocket;
    public InetAddress inetAddress;

    public void sendInfo() throws IOException {
        mongoClient = MongoClients.create("mongodb://localhost:27017");
        database = mongoClient.getDatabase("AwayFromMeCovidDB");
        countyCollection = database.getCollection("countyCollection");
        int port;
        String ip;
        String countyName;
        FindIterable<Document> result = countyCollection.find();

        for (Document document : result) {
            Document configDocument = document.get("Configs", Document.class);
            countyName = (String) document.get("Name");
            port = configDocument.getInteger("Port");
            ip = configDocument.getString("IP");
            multicastSocket = new MulticastSocket(port);
            inetAddress = InetAddress.getByName(ip);
            multicastSocket.joinGroup(inetAddress);

            if (multicastSocket != null) {
                sendData(multicastSocket, inetAddress, port, countyName);
            }
        }
    }
    private void sendData(MulticastSocket multicastSocket, InetAddress inetAddress, int port, String countyName) throws IOException {
        byte[] data = ("Infected People on [" + countyName + "] is: " +

                ClientCRUD.getInfectedPeopleByCounty(countyName)).getBytes();
        DatagramPacket packet = new DatagramPacket(data, data.length, inetAddress, port);
        multicastSocket.send(packet);
    }

    @Override
    public void run() {
        try {
            sendInfo();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}