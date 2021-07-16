package server;

import java.util.Timer;

class BroadcastHandler extends Thread {
    private final String port;
    private final String ip;

    BroadcastHandler(String port ,String ip) {
        this.port = port;
        this.ip = ip;
    }

    @Override
    public void run() {
        Timer t = new Timer();
        RegionInfo regionInfo = new RegionInfo(ip, port);
        // This task is scheduled to run every 250 seconds
        t.scheduleAtFixedRate(regionInfo, 0, 250000);
    }
}
