package fi.tiedeseura.robotstories;

import android.app.Activity;
import android.util.Log;

/**
 * Created by mikko on 6.8.15.
 */
public class CDUConnection extends Thread {
    private boolean running = false;
    private CDUEmulator emulator;
    private BluetoothConnection bluetooth;
    private Activity activity;

    public CDUConnection(Activity a) {
        activity = a;
        bluetooth = new BluetoothConnection(a);
        emulator = new CDUEmulator();
    }

    public void setRunning(boolean run) {
        running = run;
    }

    @Override
    public void run() {
        //bluetooth.start();
        while (running) {
            //Log.d("", "CUDThread");
            try {
                if(bluetooth.connected()) {
                    String msg = bluetooth.readMessage();
                    parseMessage(msg);
                }
                sleep(100);
            } catch (Exception e) {}
        }
        bluetooth.cancel();
    }
    public void sendMessage(String msg) {
        if(bluetooth.connected()) {
            bluetooth.sendMessage(msg);
        } else {
            findCDU();
            bluetooth.sendMessage(msg);
        }
    }

    public void findCDU() {
        bluetooth.searchForDevices();
    }
    public boolean parseMessage(String msg) {
        if(msg != null) {
            Log.d("CDU", msg);
            return true;
        }
        Log.d("CDU", "Received a null message");
        return false;
    }
}
