package client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

class MulticastReceiver extends Thread {
    private final String IP;
    private final int PORT;

    public MulticastReceiver(String ip ,int port) {
        this.IP = ip;
        this.PORT = port;
    }

    @Override
    public void run() {
        super.run();
        MulticastSocket socket;
        try {
            socket = new MulticastSocket(PORT);
            InetAddress address = InetAddress.getByName(IP);
            socket.joinGroup(address);

            while (true) {
                byte[] buffer = new byte[256];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                String data = new String(packet.getData(), 0, packet.getLength());
                System.out.println(data);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}