package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UsersHandler extends Thread implements ClientCallback {

    private final ExecutorService pool;
    private final String PORT;
    private final ArrayList<UserHandler> clientsList;

    UsersHandler() {
        this.clientsList = new ArrayList<>();
        this.pool = Executors.newFixedThreadPool(5);
        this.PORT = Objects.requireNonNull(Server.getConfig("Unicast")).get(0);
    }

    @Override
    public void run() {
        super.run();
        try {
            ServerSocket listener = new ServerSocket(Integer.parseInt(PORT));
            System.out.println("~~ Waiting clients ~~");
            while (!listener.isClosed()) {
                Socket client = listener.accept();
                System.out.println("Server connected to client: " + client.getRemoteSocketAddress());

                UserHandler userHandler = new UserHandler(client, this);
                pool.execute(userHandler);
                clientsList.add(userHandler);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClientInfected(int clientId) {
        for (UserHandler user : clientsList) {
            user.notifyClientInfected(clientId);
        }
    }
}
