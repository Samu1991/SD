package server;

import java.util.Timer;

public class MulticastHandler extends Thread {

    @Override
    public void run() {
        super.run();
        Timer t = new Timer();
        CountyInfo mTask = new CountyInfo();
        // This task is scheduled to run every 70 seconds
        t.scheduleAtFixedRate(mTask, 0, 70000);
    }
}
