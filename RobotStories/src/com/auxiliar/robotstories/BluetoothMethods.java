package com.auxiliar.robotstories;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.UUID;

import com.example.robotstories.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

/**
 * Class used for manage the Bluetooth conexions
 * 
 * @author Arturo Gil
 *
 */
public class BluetoothMethods {
	
    /**
     * The value of this constant is {@value}. It is the code to call the dialog for selecting a device
     */
	private static final int DEVICES_DIALOG = 1;
	
	private Context c;
	private BluetoothAdapter bAdapter;
	
	private BroadcastReceiver ActionReceiver, DispositiveReceiver;
	private ArrayList<String> names;
	private ArrayList<String> address;
	private ProgressDialog pd;
	
	public String selectedAdress=null;
	
	public UUID my_UUID;
	public BluetoothSocket actualSockect;
	public OutputStream write;
	
	/**
	 * This flag tells us when there is a bluetooth searching on course
	 */
	public boolean flagBluethooh=false;
	
	/**
	 * Calls "getDefaultAdapter" for getting the bluetooth adapter
	 * 
	 * @param c context where the bluetoothMethods are develop
	 */
	public BluetoothMethods(Context c){
		this.c=c;
		this.names = new ArrayList<String>();
		this.address = new ArrayList<String>();
		this.bAdapter= BluetoothAdapter.getDefaultAdapter();
		String macAddr, androidId;
		WifiManager wifiMan = (WifiManager) this.c.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInf = wifiMan.getConnectionInfo();
		macAddr = wifiInf.getMacAddress();
		androidId = "" + android.provider.Settings.Secure.getString(this.c.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
		this.my_UUID = new UUID(androidId.hashCode(), macAddr.hashCode());
	}
	
	/**
	 * @return true if the adapter has been set correctly
	 * 			false if not
	 */
	public boolean checkBluetooth(){
		if(bAdapter == null){
			return false;
		}
		return true;
	}
	
	/**Initializes the broadcastReceiver for connecting whit the robots
	 * @return true if it is created correctly or if it was created previously
	 * 			false if the adapter wasn't set correctly
	 */
	public boolean initializeBlueToothSend(){
		if(this.checkBluetooth()==false)
			return false;
		
		if(this.ActionReceiver!=null){
			this.ActionReceiver = new BroadcastReceiver(){
				public void onReceive(Context context, Intent intent) {
					final String action = intent.getAction();
					final int estado = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
					if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)){
						bluetoothStateChanged(estado);
					}	
				}
			};
			IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
			this.c.registerReceiver(this.ActionReceiver, filter);
		}
		return true;
	}
	/**Initializes the broadcastReceiver for finding the available devices for the robots 
	 * @return true if it is created correctly or if it was created previously
	 * 			false if the adapter wasn't set correctly
	 */
	public boolean initializeBlueToothAddDevices(){
		if(this.checkBluetooth()==false)
			return false;
		
		if(this.DispositiveReceiver==null){
			this.DispositiveReceiver = new BroadcastReceiver(){
				public void onReceive(Context context, Intent intent) {
					final String action = intent.getAction();
			        if (BluetoothDevice.ACTION_FOUND.equals(action)){
			            addNewDevice(intent);
			        }
			        if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)){
			        	pd= ProgressDialog.show(c, "Searching", "Wait while searching for devices");
			        }
			        if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
			        	pd.dismiss(); 
			            Toast.makeText(c, "Search of devices finished", Toast.LENGTH_SHORT).show();
			            onCreateDialog(DEVICES_DIALOG, null).show();
			            flagBluethooh=false;
			        }
				}
			};
			IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
			filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
			filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
			this.c.registerReceiver(this.DispositiveReceiver, filter);
		}
		return true;
	}
	/**
	 * Add a new device to the arrayList "arrayDevices", a new name for the device to "names", and a new MAC address to "address"
	 * 
	 * @param intent where the information about the device is stored
	 */
	private void addNewDevice(Intent intent) {
		BluetoothDevice dispositivo = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
		if(this.address.contains(dispositivo.getAddress()))
			return;
		 
		// Le asignamos un nombre del estilo NombreDispositivo [00:11:22:33:44]
		String descripcionDispositivo = dispositivo.getName() + " [" + dispositivo.getAddress() + "]";
		this.names.add(descripcionDispositivo);
		this.address.add(dispositivo.getAddress());
		 
		// Mostramos que hemos encontrado el dispositivo por el Toast
		Toast.makeText(this.c, "New dispositive: " + descripcionDispositivo, Toast.LENGTH_SHORT).show();
	}

	/**
	 * Read the state of the bluetooth and decides the actions to carry out
	 * 
	 * @param state of the bluetooth
	 */
	private void bluetoothStateChanged(int state){
		switch (state){
	    // Apagado
	    case BluetoothAdapter.STATE_OFF:
	    	Toast.makeText(this.c, "No me ves", Toast.LENGTH_SHORT).show();
	        break;
	    // Encendido
	    case BluetoothAdapter.STATE_ON:
	    	/*If it is on, we make it discoverable*/
	        /*Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
	        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 120);
	        this.c.startActivity(discoverableIntent);*/
	        Toast.makeText(this.c, "Me ves", Toast.LENGTH_SHORT).show();
	        break;
	    default:
	        break;
		}
	}
	
	/**
	 * Enables the bluetooth adapter if it wasn't, and if it was, calls "this.bluetoothStateChanged(BluetoothAdapter.STATE_ON);"
	 */
	public void activateBluetooth(){
		if(!this.bAdapter.isEnabled()){
			this.bAdapter.enable();
			if(this.DispositiveReceiver==null)
				this.bluetoothStateChanged(BluetoothAdapter.STATE_ON);
		}
		else
			this.bluetoothStateChanged(BluetoothAdapter.STATE_ON);
	}
	/**
	 * Disables the bluetooth adapter if it wasn't, and if it was, calls "this.bluetoothStateChanged(BluetoothAdapter.STATE_OFF);"
	 */
	public void disableBlueetooth(){
		if(this.bAdapter.isEnabled()){
			this.selectedAdress=null;
			this.bAdapter.disable();
		}
		else
			this.bluetoothStateChanged(BluetoothAdapter.STATE_OFF);
	}

	/**
	 * Begins a new search for devices. If a search were on course, stop it, and begin a new one. If there is any problem to start a
	 * search shows a toast
	 */
	public void findDevices(){
		this.flagBluethooh=true;
	    if(bAdapter.isDiscovering())
	        bAdapter.cancelDiscovery();
	    
		if(!this.bAdapter.startDiscovery())
			Toast.makeText(this.c, "Unable to search bluetooth devices", Toast.LENGTH_SHORT).show();
	}

	/**
	 * Check if there is any socket already connected, in that case close it. Then creates the socket and the outputstream
	 * 
	 * @param address the mac adress to conect
	 */
	public void conect(String address) {

        if(this.actualSockect!=null){
			/*try {
				this.actualSockect.close();
			} catch (IOException e1) {
				 Log.e("ERROR", "Fail to disconect"+ this.selectedAdress, e1);		
			}*/
        	return;
        }
        BluetoothDevice device = this.bAdapter.getRemoteDevice(address);
        BluetoothSocket tmp = null;
        OutputStream mmout= null;
        this.selectedAdress= address;

        // Get a BluetoothSocket for a connection with the
        // given BluetoothDevice
        try {
            tmp = device.createRfcommSocketToServiceRecord(this.my_UUID);
            Method m = device.getClass().getMethod("createRfcommSocket", new Class[] {int.class});
            tmp = (BluetoothSocket) m.invoke(device, 1);
            tmp.connect();
        } catch (Exception e) {
            Log.e("ERROR", "Fail to conect to device "+ address, e);
            return;
        }
        this.actualSockect= tmp;
        
        try {
			mmout=tmp.getOutputStream();
		} catch (IOException e) {
			Log.e("ERROR", "fail to get the OutputStream "+ address, e);
			return;
		}
        this.write = mmout;
	}

	/**
	 * Send a String through the OutoutStream created in "conect"
	 * 
	 * @param info to be written
	 * @return true if info is send successfully
	 * 			false if the info is not send
	 */
	public boolean sendInfo(String info) {
		if(this.write==null)
			return false;
		
		try {
			this.write.flush();
			this.write.write(info.getBytes());
		} catch (IOException e) {
			Log.e("ERROR", "fail to send information "+ this.selectedAdress, e);
			return false;
		}

		Log.e("tag", info);
		return true;
	}
	
	
	/**
	 * Creates a dialog which display the devices found in the context in which the bluetoothMethods are created (defined in this.c)
	 * 
	 * @param id the id of the created dialog
	 * @param args any posible args to put on the dialog
	 * @return the created dialog
	 */
	@SuppressWarnings("deprecation")
	protected Dialog onCreateDialog(final int id, Bundle args){
		final AlertDialog dialog;
		AlertDialog.Builder builder =null;
		
		LayoutInflater inflater = (LayoutInflater) this.c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View layout ;
		
		switch(id){
			case DEVICES_DIALOG:
				layout = inflater.inflate(R.layout.devices_dialog, null);
				TableLayout allDevices = (TableLayout) layout.findViewById(R.id.DEVICES);
				builder = new AlertDialog.Builder(this.c);
				builder.setTitle("Select a device");
				
				/*Create cancel button*/
				builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.cancel();
					}
				});
				
				/*Display dialog*/
				builder.setView(layout);
				dialog = builder.create();
				for(int i=0; i<this.names.size(); i++){
					final int value=i;
					TableRow aux= new TableRow(this.c);
					aux.setGravity(Gravity.CENTER_HORIZONTAL);
					aux.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.FILL_PARENT)); 
					
					Button nuevo = new Button(this.c);
					nuevo.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.FILL_PARENT)); 
					nuevo.setText(this.names.get(i));
					nuevo.setTag(i);
					
					aux.addView(nuevo);
					allDevices.addView(aux); 
					
					nuevo.setOnClickListener(new OnClickListener() {
						public void onClick(View v) {
							selectedAdress=address.get(value);
							dialog.cancel();
						}
					});
				}
				break;				
			default:
				return null;
			}

		return dialog;
	}

	
}
