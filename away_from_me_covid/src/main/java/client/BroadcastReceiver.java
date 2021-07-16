package client;

import server.Server;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Objects;

class BroadcastReceiver extends Thread {

    @Override
    public void run() {
        super.run();
        MulticastSocket broadcastSocket = null;
        try {
            String ip = Objects.requireNonNull(Server.getConfig("Broadcast").get(1));
            int port = Integer.parseInt(Objects.requireNonNull(Server.getConfig("Broadcast")).get(0));

            broadcastSocket = new MulticastSocket(port);
            InetAddress addressBroadcast = InetAddress.getByName(ip);
            broadcastSocket.joinGroup(addressBroadcast);

            while (true) {
                byte[] buffer = new byte[256];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                broadcastSocket.receive(packet);
                String data = new String(packet.getData(), 0, packet.getLength());
                System.out.println(data);
            }
        } catch (IOException e) {
            if (broadcastSocket != null) {
                broadcastSocket.close();
            }
            e.printStackTrace();
        }

    }
}