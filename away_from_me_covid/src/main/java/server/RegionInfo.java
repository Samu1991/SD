package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.TimerTask;

class RegionInfo extends TimerTask {
    private final String ip;
    private final String  port;

    RegionInfo(String ip, String  port) {
        this.ip = ip;
        this.port = port;
    }

    public void info() throws IOException {
        MulticastSocket broadcastSocket = new MulticastSocket(Integer.parseInt(port));
        InetAddress addressBroadcast = InetAddress.getByName(ip);
        broadcastSocket.joinGroup(addressBroadcast);
        byte[] data = ("Total people infected number: " +
                ClientCRUD.getTotalInfectedPeople()).getBytes();
        DatagramPacket datagramPacket = new DatagramPacket(data, data.length, addressBroadcast, Integer.parseInt(port));
        broadcastSocket.send(datagramPacket);
    }

    @Override
    public void run() {
        try {
            info();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
