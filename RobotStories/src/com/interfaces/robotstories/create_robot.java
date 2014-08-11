package com.interfaces.robotstories;

import com.auxiliar.robotstories.ImageHandler;
import com.example.robotstories.R;
import com.types.robotstories.Motor;
import com.types.robotstories.Play;
import com.types.robotstories.Robot;
import com.types.robotstories.Sensor;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;

/**
 * Creates an interface to create or modify the properties of a robot
 * <p>Creates an auxiliar robot if there is no one created previously to be able to call the methods to know
 * the number of motors and sensors</p>.
 * <p>This interface shows a tableLayout which contains all created motors, an another tablelayout which shows all the sensor. Under each
 * table appears a button which allows us to add more motors or sensors to the tables, and to the play.
 * <p>Can't go to anywhere until a name and a size is set</p>
 * @author Arturo Gil
 */
@SuppressWarnings("deprecation")
public class create_robot extends Activity{
	
	/*CONSTANTS*/ 
	/* FOR THE ID OF DIALOGS (SENSOR AND MOTOR)*/
    /**
     * The value of this constant is {@value}. It is the code to call the dialog for defining a motor
     */
	private static final int MOTOR_DIALOG= 0;
    /**
     * The value of this constant is {@value}. It is the code to call the dialog for defining a sensor
     */
	private static final int SENSOR_DIALOG= 1;
    /**
     * The value of this constant is {@value}. It is the code to call the dialog for setting the picture for the robot
     */
	private static final int PICTURE_DIALOG = 2;
    /**
     * POSITION OF THE SPINNER IN TABLE ROW. The value of this constant is {@value}.
	 * (necessary in save motor/sensor, because it needs to know where it is to read its value)
     */
	private static final int POS_SPINNER_ON_ROW = 2;
    /**
     * POSITION OF THE TV OF THE NAME OF THE MOTOR/SENSOR IN TABLE ROW. 
     * The value of this constant is {@value}.
	 * (necessary in save motor/sensor, because it needs to know where it is to read its value)
     */
	private static final int POS_TV_ON_ROW = 0;
	
	private static final int RECTANGLE_SMALLBUTTON = 40;

	/*Variables received*/
	Play play;
	Robot robot;
	/**
	 *This is the name of the activity who call this activity, necesary for ensure to come back
	 *to the correct place once we finish 
	 */
	String previousActivity;
	
	/*New robot data*/
	String name;
	int HSize, VSize;
	
	/*New motor/sensor data*/
	String dialog_name;
	String dialog_selected;
	/**
	 * This is the name of the image asociated to the robot. It is initialized whit the value "default_image"
	 */
	private String imageName= "default_image";
	private ImageHandler images;
	
	/**
	 * Add listeners to buttons, and calls to iniLayout
	 */
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_robot);
        iniLayout();
        
        /*Set label 2 of size robot*/
        final Spinner type= (Spinner) this.findViewById(R.id.RobotTypeSize);
		type.setOnItemSelectedListener( new OnItemSelectedListener ()   {
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
		    	String selected= (String) type.getSelectedItem();
		    	TextView type2 = (TextView) findViewById(R.id.RobotTypeSize2);
		    	type2.setText(selected);
			}
			public void onNothingSelected(AdapterView<?> parent) {
				type.setSelection(0);
		}});
		/*SAVE ROBOT*/
		Button aux =(Button) this.findViewById(R.id.RobotSave);
		aux.setOnClickListener(new OnClickListener() {    
			public void onClick(View v) {
				saveAndGo();
		}});
		/*ADD MOTOR*/
		aux = (Button) this.findViewById(R.id.RobotAddMotor);
		aux.setOnClickListener(new OnClickListener() {    
			public void onClick(View v) {
				showDialog(MOTOR_DIALOG);
		}});
		/*ADD SENSOR*/
		aux = (Button) this.findViewById(R.id.RobotAddSensor);
		aux.setOnClickListener(new OnClickListener() {    
			public void onClick(View v) {
				showDialog(SENSOR_DIALOG);       
		}});
		
		/*ADD PICTURE*/
		aux= (Button) this.findViewById(R.id.RobotPic);
		aux.setOnClickListener(new OnClickListener(){    
			public void onClick(View v) {
				showDialog(PICTURE_DIALOG);
		}});
	}

	/**
	 *  Reads play and previous activity, if this values are null, finish the activity
	 *  Set previous defined values in their places (if we come to modify a robot)
	 *  Creates a new robot if doesn't exists*/
	private void iniLayout() {
		
		/*Previous values*/
		Bundle extras = getIntent().getExtras();
		if(extras == null){
			finish();
		/*It seems we have some previous values, let's set them in their places*/
		}else{ 
			this.play = (Play) extras.getSerializable("Play");
			if(this.play==null)
				finish();
			this.previousActivity = extras.getString("previousActivity");
			if(this.previousActivity==null)
				finish();
			this.robot = (Robot) extras.getSerializable("Robot");
			
			if(this.robot!=null){
				/*Name*/
				EditText aux= (EditText)findViewById(R.id.ROBOTName);
				aux.setText(this.robot.name);
				/*Horizontal size*/
				aux= (EditText)findViewById(R.id.ROBOTHSize);
				aux.setText(Integer.toString(this.robot.HSize));	
				/*Vertical size*/
				aux= (EditText)findViewById(R.id.ROBOTVSize);
				aux.setText(Integer.toString(this.robot.VSize));
				this.imageName= this.robot.imageName;
			}
			else
				this.robot= new Robot();
			
			/*We add the pre-created motors and sensors of the play*/
			if(this.play.motors!= null){
				String[] portsNumbers = this.robot.arrayMotorPorts();
				int id_table = R.id.ROBOTTableM;
				for (Motor m: this.play.motors){
					this.dialog_name=m.name;
					this.dialog_selected=m.type;
					addRow(id_table, portsNumbers);
				}
			}
			if(this.play.sensors!= null){
				String[] portsNumbers = this.robot.arraySensorPorts();
				int id_table = R.id.ROBOTTableS;
				for (Sensor s: this.play.sensors){
					this.dialog_name=s.name;
					this.dialog_selected=s.type;
					addRow(id_table, portsNumbers);
				}
			}
			this.images = new ImageHandler(this);
		}
	}
	/**
	 * Save all the data and come back to the previous activity.
	 * Checks if there are more than one motor/sensor per port*/
	@SuppressWarnings("rawtypes")
	private void saveAndGo(){
		boolean go=true;
		
		/*Title, checks if the name is already in use*/
		EditText aux= (EditText)findViewById(R.id.ROBOTName);
		name= aux.getText().toString();
		if(name.length()<=0){
			aux.setError("Introduce a valid name for the robot");
			go=false;
		}
		else{
			if(this.play.findRobotByName(name)!=null){
				aux.setError("This name is already on use");
				go=false;
			}
			else
				aux.setError(null);
		}
		
		/*Horizontal size*/
		aux= (EditText)findViewById(R.id.ROBOTHSize);
		if(aux.getText().toString().length()>0){
			HSize = Integer.parseInt(aux.getText().toString());
			if(HSize<= 0){
				aux.setError("Introduce a valid horizontal size for the robot");
				go=false;
			}
		}
		else{
			aux.setError("Introduce an horizontal size for the robot");
			go=false;
		}
		
		/*Vertical size*/
		aux= (EditText)findViewById(R.id.ROBOTVSize);
		if(aux.getText().toString().length()>0){
			VSize = Integer.parseInt(aux.getText().toString());
			if(VSize<= 0){
				aux.setError("Introduce a valid vertical size for the robot");
				go=false;
			}
		}
		else{
			aux.setError("Introduce an horizontal size for the robot");
			go=false;
		}
		
		/*First check of everything alright*/
		if(!go)
			return;
		
		/*MOTORS AND SENSORS*/
		/*Check table to find the motor/sensor asignations*/
		if( this.saveMotors()==false){
			go=false;
			Toast.makeText(create_robot.this, "Problems at the time of saving the motors, " +
					"please check if there is any motor port whit two motors asigned", Toast.LENGTH_SHORT).show();
		}
		if (this.saveSensors()==false){
			go=false;
			Toast.makeText(create_robot.this, "Problems at the time of saving the sensors, " +
					"please check if there is any sensor port whit two motors asigned", Toast.LENGTH_SHORT).show();
		}
		/*Are everything ready to go? If not clear motor and sensors*/
		if(!go){
			this.robot.removeMotors();
			this.robot.removeSensors();	
			return;
		}
		
		this.robot.name= name;
		this.robot.HSize=HSize;
		this.robot.VSize=VSize;
		this.robot.imageName=this.imageName;
		
		/*Add robot to play, checks if it already exists*/
		this.play.AddRobot(this.robot);
		
		/**come back to previous activity, if any problem, gets blocked (should not happen never, 
		 * if it happens the responsability is from the calling class, not sending the correct name)*/
		Class comeBack;
		try {
			comeBack= Class.forName(this.previousActivity);
			Intent intent = new Intent(create_robot.this, comeBack);
			intent.putExtra("Play", this.play);
			startActivity(intent);
			finish();
		} catch (ClassNotFoundException e) {
		    AlertDialog ad = new AlertDialog.Builder(this).create();  
		    ad.setCancelable(false); 
		    ad.setMessage("Fail loading previous class "+ this.previousActivity);  
		    ad.show();
			e.printStackTrace();
		}
	}
	/**Save the sensors in their respective ports in the robot
	 * if there is any problem at the time of save any sensor, return false
	 * alright returns true
	 * <p>VERY FRAGILE METHOD, IF YOU MAKE ANY CHANGE IN THE TABLE, PLEASE CHECK IF THERE IS ANY NEED OF CHANGING
	 * THE VALUE OF POS_SPINNER_ON_ROW, AND POS_TV_ON_ROW</p> */
	private boolean saveSensors() {
		TableLayout tl = (TableLayout) findViewById(R.id.ROBOTTableS);
		/*Remember the first child (0) is the title*/
		/*For each row in the table, read the value of the TV for the name 
		 * and the value of the spinner for taking the number of the port*/
		for(int i=1; i<tl.getChildCount(); i++){
			TableRow tr= (TableRow) tl.getChildAt(i);
			Spinner aux= (Spinner) tr.getChildAt(create_robot.POS_SPINNER_ON_ROW);
			int pos=-1;
			if(aux.getSelectedItemPosition()!=0)
				pos = Integer.parseInt(aux.getSelectedItem().toString());
			 /* if there is any problem at the time of assign port to a sensor return false and stops*/
			if (pos>0){
				TextView name = (TextView) tr.getChildAt(create_robot.POS_TV_ON_ROW);
				Sensor s = this.play.findSensorByName((String)name.getText());
				if(this.robot.setSensorInPort(s, pos)==false)				
					return false;
			}		
		}
		return true;
	}
	/**
	 *Save the motors in their respective ports in the robot
	 * if there is any problem at the time of save any motor, return false
	 * alright returns true
	 * <p>VERY FRAGILE METHOD, IF YOU MAKE ANY CHANGE IN THE TABLE, PLEASE CHECK IF THERE IS ANY NEED OF CHANGING
	 * THE VALUE OF POS_SPINNER_ON_ROW, AND POS_TV_ON_ROW</p> */
	private boolean saveMotors(){
		TableLayout tl = (TableLayout) findViewById(R.id.ROBOTTableM);
		/*Remember the first child (0) is the title*/
		/*For each row in the table, read the value of the TV for the name 
		 * and the value of the spinner for taking the number of the port*/
		for(int i=1; i<tl.getChildCount(); i++){
			TableRow tr= (TableRow) tl.getChildAt(i);
			Spinner aux= (Spinner) tr.getChildAt(create_robot.POS_SPINNER_ON_ROW);
			int pos=-1;
			if(aux.getSelectedItemPosition()!=0)
				pos = Integer.parseInt(aux.getSelectedItem().toString());
			if (pos>0){
				TextView name = (TextView) tr.getChildAt(create_robot.POS_TV_ON_ROW);
				Motor m = this.play.findMotorByName((String)name.getText());
				 /* if there is any problem at the time of assign port to a motor return false and stops*/
				if(this.robot.setMotorInPort(m, pos)==false)				
					return false;
			}		
		}
		return true;
	}
	/**Create dialog for input data for sensor and for motors and select a picture for the robot
	 * 
	 * @param id of the dialog to show (motor or sensor)*/
	protected Dialog onCreateDialog(final int id, Bundle args) {
		AlertDialog dialog=null;
		AlertDialog.Builder builder =null;
		
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final EditText nameET;
	    final Spinner typeSP ;
	    final View layout ;
		
		switch(id){
			case MOTOR_DIALOG:
				layout = inflater.inflate(R.layout.motor_dialog, (ViewGroup) findViewById(R.id.MOTORroot));
				nameET = (EditText) layout.findViewById(R.id.MOTORName);
				typeSP = (Spinner) layout.findViewById(R.id.MOTORType);
				builder = new AlertDialog.Builder(this);
				builder.setTitle("Define a new motor");
				break;
				
			case SENSOR_DIALOG:
				layout = inflater.inflate(R.layout.sensor_dialog, (ViewGroup) findViewById(R.id.SENSORroot));
				nameET = (EditText) layout.findViewById(R.id.SENSORName);
				typeSP = (Spinner) layout.findViewById(R.id.SENSORType);
				builder = new AlertDialog.Builder(this);
				builder.setTitle("Define a new sensor");
				break;
				
			case PICTURE_DIALOG:
				layout = inflater.inflate(R.layout.picture_dialog, (ViewGroup) findViewById(R.id.PICTURETable));
				builder = new AlertDialog.Builder(this);
				builder.setTitle("Select a image for the action");
				typeSP=null;
				nameET=null;
				
				/*Puts avalible images*/
				setActionPics(layout);
				break;
			default:
				return null;
			}

		/*Create cancel button*/
		builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				removeDialog(id);
			}
		});
		/*Create OK button*/	 
		if(id!= PICTURE_DIALOG)
			builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
					dialog_selected= (String) typeSP.getSelectedItem();
					saveInputDataDialog(id, nameET);
					removeDialog(id);
			}
		});
		/*Display dialog*/
		builder.setView(layout);
		dialog = builder.create();
		return dialog;
	}

	/**
	 * Get the names of the images which are available for represent an action from strings.xml (R.array.RobotImages),
	 * and shows them in the dialog inside a tablelayout which shows its name and the picture
	 * 
	 * @param layout the dialog in which the images are shown
	 */
	private void setActionPics(View layout) {
		String[] values = getResources().getStringArray(R.array.RobotImages);
		TableLayout tl = (TableLayout) layout.findViewById(R.id.PICTURETable);
		
		for(final String name: values){
			TableRow tr = new TableRow(this);
			tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.FILL_PARENT));
			/* Create a button for the row */
			Button botaux = new Button (this);
			botaux.setText(name);
			botaux.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.FILL_PARENT));
			BitmapDrawable imagechanged= this.images.resizeImage(name, create_robot.RECTANGLE_SMALLBUTTON);
			botaux.setCompoundDrawablesWithIntrinsicBounds(null, null, imagechanged, null);
			botaux.setOnClickListener(new OnClickListener() {
				String selected= name;
				public void onClick(View v) {
					imageName= selected;
					removeDialog(PICTURE_DIALOG);
				}
			});
			/* Add objects to row. */
			tr.addView(botaux);
			
			/* Add row to TableLayout. */
			tl.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));	
		}
		
	}

	/**Check if the name is already in use by another motor or sensor (respectively) or if it null-> show toast
	 * if not save input data from the dialog -> save + add row
	 * @param id case motor/sensor
	 * @param nameET place to read the name of the motor/sensor
	 */
	protected void saveInputDataDialog(final int id, final EditText nameET) {
		
		int id_table;
		
		dialog_name = nameET.getText().toString();
		
		/*Check if the name of the motor/sensor is valid*/
		if(dialog_name.length() == 0){
			Toast.makeText(create_robot.this, "This name is not valid", Toast.LENGTH_SHORT).show();
			return;
		}

		switch(id){
			case MOTOR_DIALOG:
				if (play.findMotorByName(dialog_name)!=null){
					Toast.makeText(create_robot.this, "The name " + dialog_name +
							"is already in use by another motor", Toast.LENGTH_SHORT).show();
					return;
				}
				this.play.AddMotor(new Motor (this.dialog_name, this.dialog_selected));
				id_table = R.id.ROBOTTableM;
				this.addRow(id_table, this.robot.arrayMotorPorts());
				break;			
			case SENSOR_DIALOG:
				if (play.findSensorByName(dialog_name)!=null){
					Toast.makeText(create_robot.this, "The name \"" + dialog_name +
							"\" is already in use by another sensor", Toast.LENGTH_SHORT).show();
					return;
				}
				this.play.AddSensor(new Sensor (this.dialog_name, this.dialog_selected));
				id_table = R.id.ROBOTTableS;
				this.addRow(id_table, this.robot.arraySensorPorts());
				break;
			default:
				return;
		}
	}

	/**Add row whit the new motor/sensor, add the delete button to row
	 * 
	 * @param id_table case motor/sensor
	 * @param portsNumbers array for the spinner whit the number of the ports
	 */
	protected void addRow(int id_table, String[] portsNumbers){
		/* Find Tablelayout defined in main.xml */
		TableLayout tl = (TableLayout) findViewById(id_table);
		/* Create a new row to be added. */
		final TableRow tr = new TableRow(this);
		tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
		
		/* Create a 2TV, the spinner and the delete button to be the row-content. */
		/*2TV*/
		TextView nameTV = new TextView(this);
		nameTV.setText(this.dialog_name);
		nameTV.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.MATCH_PARENT, 2));
		nameTV.setGravity(Gravity.CENTER);
		nameTV.setBackgroundResource(R.drawable.cells_create_robot);
		nameTV.setMaxWidth(tr.getWidth()/3);
		TextView typeTV = new TextView(this);
		typeTV.setText(this.dialog_selected);
		typeTV.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.MATCH_PARENT, 1));
		typeTV.setGravity(Gravity.CENTER);
		typeTV.setBackgroundResource(R.drawable.cells_create_robot);
		
		/*Spinner*/
		Spinner ports = new Spinner (this);
		ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, portsNumbers); //selected item will look like a spinner set from XML
		spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		ports.setAdapter(spinnerArrayAdapter);
		ports.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.MATCH_PARENT, 1));
		
		/*Delete Button*/
		ImageButton botaux = new ImageButton(this);
		botaux.setImageResource(R.drawable.cross_red);
		botaux.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.MATCH_PARENT));
		botaux.setOnClickListener(new OnClickListener() {
			String name=dialog_name;
			TableRow tr_delete =tr;
			public void onClick(View v) {
				deleteRow(name, tr_delete);
		}});
		
		/* Add objects to row. */
		tr.addView(nameTV);
		tr.addView(typeTV);
		tr.addView(ports);
		tr.addView(botaux);
		
		/* Add row to TableLayout. */
		tl.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
	}
	/**
	 * Delete a row from the table of available motors/sensor and remove the motor/sensor from the play.
	 * Reads if it is motor or sensor taking the parent of the tableRow, it is the TableLayout, and 
	 * sees if its id is from motor or sensor
	 * Use the tablelayout to delete the row from it
	 * 
	 * @param name of the motor/sensor
	 * @param tr where the motor/sensor is
	 */
	protected void deleteRow(String name, TableRow tr) {
		tr.removeAllViews();
		TableLayout tl= (TableLayout) tr.getParent();
		if(tl.getId()==R.id.ROBOTTableM)
			this.play.RemoveMotor(this.play.findMotorByName(name));
		else
			this.play.RemoveSensor(this.play.findSensorByName(name));
		tl.removeView(tr);
	}

}
