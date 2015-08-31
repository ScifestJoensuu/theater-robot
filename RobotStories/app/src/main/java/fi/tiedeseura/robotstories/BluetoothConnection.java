package fi.tiedeseura.robotstories;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by mikko on 18.8.15.
 */
public class BluetoothConnection{
    private BluetoothSocket btSocket;
    private BluetoothDevice btDevice;
    private BluetoothAdapter btAdapter;
    private BufferedReader btReader;
    private InputStream btInStream;
    private OutputStream btOutStream;
    private Activity activity;
    private UUID uuid;
    private ArrayList<BluetoothDevice> discoveredDevices;
    private final String TAG = "BT";

    public final int BT_ENABLE = 5;
    private boolean btConnected = false;

    private ArrayAdapter<String> adapter;

    /*
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.d("", device.getName() + "\n" + device.getAddress());
                discoveredDevices.add(device);
            }
        }
    };
*/
    public BluetoothConnection(Activity activity) {
        this.activity = activity;

        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter == null) {
            Toast.makeText(activity, "Bluetooth not supported", Toast.LENGTH_LONG);
            return;
        }


        /*
        // Register the BroadcastReceiver
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        activity.registerReceiver(mReceiver, filter);

        Set<BluetoothDevice> devices = btAdapter.getBondedDevices();
        for(BluetoothDevice d: devices) {
            Log.d("", d.getName() + " - " + d.getAddress());
            if(d.getName().compareTo("RobotStoryCDU") == 0) {
                this.btDevice = d;
                break;
            }
        }

        if(btDevice == null) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            activity.startActivity(discoverableIntent);
        }

        try {
            uuid = new UUID(123,456);
            btSocket = btDevice.createRfcommSocketToServiceRecord(uuid);
        } catch (IOException e) { }
        */

    }

    /*
    public void run() {
        Log.d(TAG, "Start listening bt");
        running = connect();
        Log.d(TAG, "Connection: "+running);
        while(running) {
            //String msg = "testi";
            //write(msg.getBytes());
            try {
                String msg = readMessage();
                Log.d(TAG, msg);
                Thread.sleep(50);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                running = false;
                e.printStackTrace();
            }
        }
    }
    */

    public boolean connected() {
        return this.btConnected;
    }

    private boolean connect() {
        btAdapter.cancelDiscovery();

        try {
            uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
            btSocket = btDevice.createRfcommSocketToServiceRecord(uuid);
        } catch (IOException e) { }

        if(btSocket == null) return false;

        try {
            btSocket.connect();
            btInStream = btSocket.getInputStream();
            btOutStream = btSocket.getOutputStream();
            btReader = new BufferedReader(new InputStreamReader(btInStream));
        } catch (IOException e) {
            try {
                e.printStackTrace();
                btSocket.close();
            } catch (IOException e1) {e1.printStackTrace();}
            return false;
        }

        return true;
    }


    /** Will cancel an in-progress connection, and close the socket */
    public void close() {
        try {
            btSocket.close();
        } catch (IOException e) { }
    }

    /* Call this from the main activity to send data to the remote device */
    public void write(byte[] bytes) {
        try {
            for(byte b: bytes) {
                Log.d("", new String(new byte[] { b }, StandardCharsets.UTF_8));
                btOutStream.write(b);
                btOutStream.flush();
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {e.printStackTrace();}
    }

    public String readMessage() throws IOException {
        return btReader.readLine();
        //if(data != null) return data.toString();
        //return null;
    }

    public byte[] read() {
        try {
            int available = 0;
            if ((available = btInStream.available()) > 0) {
                byte[] bytes = new byte[available];
                btInStream.read(bytes);
                return bytes;
            }
        } catch(IOException e) {}
        return null;
    }
    public void sendMessage(String msg)  {
        if(btConnected) {
            //msg = "moi, tama on pitka viesti androidilta arduinolle. toivottavasti tama tulee ehjana perille.. asdhfa ksdfh asdlkfhask fkhadsf ahskdfh laksdfhlkas";
            Log.d(TAG, "Sending: " + msg);
            write(msg.getBytes());
            //PrintWriter writer = new PrintWriter(
            //        (new OutputStreamWriter(btOutStream)), false);
            //writer.write(msg);
            //writer.flush();
        }
    }

    /* Call this from the main activity to shutdown the connection */
    public void cancel() {
        try {
            if(btSocket != null)
                btSocket.close();
        } catch (IOException e) { }
    }

    public void searchForDevices() {
        if (!btAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableBtIntent, BT_ENABLE);
            return;
        }

        Log.d(TAG, "Scanning for bt devices");
        btAdapter.cancelDiscovery();

        ArrayList<String> names = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(activity,android.R.layout.simple_list_item_1, names);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        //View convertView = (View) inflater.inflate(R.layout.custom, null);
        //alertDialog.setView(convertView);
        alertDialog.setTitle("Discovered devices");
        //ListView lv = (ListView) convertView.findViewById(R.id.listView1);
        //lv.setAdapter(adapter);
        alertDialog.setNegativeButton("cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.setAdapter(adapter,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //String strName = adapter.getItem(which);
                        //AlertDialog.Builder builderInner = new AlertDialog.Builder(
                        //        activity);
                        //builderInner.setMessage(strName);
                        //builderInner.setTitle("Your Selected Item is");

                        btDevice = discoveredDevices.get(which);
                        btConnected = BluetoothConnection.this.connect();
                        dialog.dismiss();
                        /*
                        builderInner.setPositiveButton("Ok",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(
                                            DialogInterface dialog,
                                            int which) {
                                        dialog.dismiss();
                                    }
                                });
                        builderInner.show();
                        */
                    }
                });

        alertDialog.show();

        discoveredDevices = new ArrayList<BluetoothDevice>();

        btAdapter.startDiscovery();
        BroadcastReceiver mReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                //Finding devices
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    // Get the BluetoothDevice object from the Intent
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    // Add the name and address to an array adapter to show in a ListView
                    discoveredDevices.add(device);
                    adapter.add(device.getName() + "\n" + device.getAddress());
                    Log.d(TAG, "Found bt device: " + device.getName());
                }
            }
        };

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        activity.registerReceiver(mReceiver, filter);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == 1) {
            // Make sure the request was successful
            if (resultCode == activity.RESULT_OK) {
                Log.d(TAG, "bt ok!");
                searchForDevices();
            }
        }
    }
}
