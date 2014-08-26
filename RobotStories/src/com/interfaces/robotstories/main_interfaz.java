package com.interfaces.robotstories;

import java.util.ArrayList;

import com.auxiliar.robotstories.BluetoothMethods;
import com.auxiliar.robotstories.DragRobotScenario;
import com.auxiliar.robotstories.ImageHandler;
import com.example.robotstories.R;
import com.types.robotstories.Accion;
import com.types.robotstories.Play;
import com.types.robotstories.Robot;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.View.DragShadowBuilder;
import android.view.View.OnClickListener;
import android.view.View.OnDragListener;
import android.view.View.OnTouchListener;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

/**
 * <p>This activity creates the main interface.</p>
 * <p>In this interface is shown the scenario, which is an absolute layout. In both sides of it appears all the robots of the play 
 * spread in two linears layouts. Each robot is represented by a LinearLayout which contains an image which represents the robot
 * and can be dragged into the scenario, and its name, if the robot is longclicked it can be modified. 
 * If it is clicked appear all the actions which will be performed by the robot in order of their performance. </p>
 * <p>Under the scenario appears all the actions organized in a linearLayout, each action is represented by a Linear layout, which contains 
 * an image which represents the action and can be dragged into the actions of a robot, if this action contains any subaction which have 
 * a common property, a dialogbox will appear and ask us to insert the needed information, and its name. If it is long clicked the action 
 * can be modified.</p>
 * <p>Over the scenario appears some buttons which allows to modify the name and description of the play, to add a robot, to add an action, etc.</p>
 * 
 * @author Arturo Gil
 *
 */
@SuppressWarnings("deprecation")
public class main_interfaz extends Activity {
	
	Play play;

	private ImageHandler images;
	private BluetoothMethods bluetooth;
	
    /**
     * The value of this constant is {@value}. It is the size of the image for the ImageButtons and the actions of the gridview
     */
	private static final int RECTANGLE_BUTTON=80;
    /**
     * The value of this constant is {@value}. It is the code to call the dialog for setting the values for an action
     */
	private static final int ACCION_DIALOG= 0;

	private ArrayList<Accion> allActions;
	private Integer laysRobots[];
	
	/*Only for propertyDialog*/
	private Accion selectedAccion;
	private Robot selectedRobot;
	private int positionAcc;
	private View selectedView;
	
	/**
	 * Calls iniMainInter, set the OnClickListeners for the buttons of the main interface, and the onDragListener for the scenario
	 */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        /*If there is no play created just finish, and create a play first*/
        if (!iniMainInter()){ 
			Intent intent = new Intent(main_interfaz.this, create_play.class);
			startActivity(intent);
			finish();
        }

        /*Edit basic properties of the play*/
        Button create = (Button)this.findViewById(R.id.name);
        create.setOnClickListener(new OnClickListener() {    
			public void onClick(View v) { 
				createPlay();
		}});
        
        /*Add robot*/
		ImageButton robot= (ImageButton) this.findViewById(R.id.new_char);
		robot.setOnClickListener(new OnClickListener() {    
			public void onClick(View v) {
				Intent intent = new Intent(main_interfaz.this, create_robot.class);
				intent.putExtra("Play", play);
				intent.putExtra("previousActivity", "com.interfaces.robotstories.main_interfaz");
				startActivity(intent);
				finish();
		}});
		/*Add action*/
		ImageButton action= (ImageButton) this.findViewById(R.id.new_acc);
		action.setOnClickListener(new OnClickListener() {    
			public void onClick(View v) {
				Intent intent = new Intent(main_interfaz.this, create_action.class);
				intent.putExtra("Play", play);
				intent.putExtra("previousActivity", "com.interfaces.robotstories.main_interfaz");
				startActivity(intent);
				finish();
		}});
        
		/*Scenario*/
		AbsoluteLayout scenario= (AbsoluteLayout) this.findViewById(R.id.MAINscenario);
		scenario.setOnDragListener(new DragRobotScenario(this.play, images));
		
		/*Send actions to robots (Play)*/
		ImageButton imageButton = (ImageButton) this.findViewById(R.id.play);
		imageButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				bluetooth.activateBluetooth();
				bluetooth.initializeBlueToothSend();
				sendSignalstoRobots();
			}
		});
		
		/*Stop sending actions (Pause)*/
		imageButton = (ImageButton) this.findViewById(R.id.pausa);
		imageButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				bluetooth.disableBlueetooth();
			}
		});
    }
    
    public void onBackPressed() { 
        new AlertDialog.Builder(this)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle("Closing Activity")
            .setMessage("Are you sure you want to exit RobotStories?")
            .setPositiveButton("Yes", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();    
            }

        })
        .setNegativeButton("No", null)
        .show();
    }
    
    protected void sendSignalstoRobots() {
		// TODO Auto-generated method stub
		for(Robot r: this.play.robots){
			if(r.device!=null){
				this.bluetooth.conect(r.device);
				Toast.makeText(this, "Device selected for robot "+ r.name, Toast.LENGTH_SHORT).show();
				for(Accion a: r.actions){
					this.bluetooth.sendInfo(a.transformActionIntoCode());	
				}
			}
			/*else
				Toast.makeText(this, "No device selected for robot "+ r.name, Toast.LENGTH_SHORT).show();*/
		}
	}

	/**
     * <p>Creates the LinearLayouts which represents each of the actions of the play. Set the longClick listener (for modify an action), and 
     * the ontouch listener (to create the shadow which allows to drag them into the robot).</p>
     * ATENTION: Removes the action from the play before sending it to modify
     */
    private void setPlayActions() {
		LinearLayout li = (LinearLayout) this.findViewById(R.id.MAINactions);
		this.allActions= new ArrayList<Accion>();
    	this.allActions.addAll(this.play.acciones);
    	this.allActions.addAll(this.play.predefAcciones);
    	

		for(final Accion a: this.allActions){
			LinearLayout nueva = new LinearLayout(this);
			nueva.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.FILL_PARENT));
			nueva.setOrientation(LinearLayout.VERTICAL);
			nueva.setPadding(8, 8, 8, 8);
			/* Create a imageview and a TV */
			/*TV*/
			TextView nameTV = new TextView(this);
			nameTV.setText(a.name);
			/*Primero es ancho, segundo alto*/
			nameTV.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
			nameTV.setGravity(Gravity.CENTER);
			nueva.addView(nameTV);
			li.addView(nueva);
			
			/*ImageView*/
            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));// ancho y alto
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            imageView.setImageResource( this.images.getIdImageByName (a.image));
            imageView.setTag(a);

			/* Add objects to row. */
			nueva.addView(imageView);
			imageView.setOnTouchListener(new  OnTouchListener(){
	        	public boolean onTouch(View view, MotionEvent motionEvent) {
	                if (motionEvent.getActionMasked() == MotionEvent.ACTION_DOWN) {
	                    DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
	                    ClipData data = ClipData.newPlainText("parent", "action");
	                    view.startDrag(data, shadowBuilder, view, 0);
	                    return true;
	                }
	                else
	                	return false;
	        }});
			nueva.setOnLongClickListener(new OnLongClickListener() {
				Accion send = a;
				public boolean onLongClick(View v) {
					Intent intent = new Intent(main_interfaz.this, create_action.class);
					intent.putExtra("Accion", this.send);
					play.RemoveAction(send);
					intent.putExtra("Play", play);
					intent.putExtra("previousActivity", "com.interfaces.robotstories.main_interfaz");
					startActivity(intent);
					finish();
					return true;
				}
			});
			
			}
		
	}
    /**
     * <p>Creates the LinearLayouts which represents each of the robots of the play. Set the longClick listener (for modify a robot),
     * the click listener (for showing/hiding the view which shows all the actions of the robot) and the ontouch listener 
     * (to create the shadow which allows to drag them into the robot).</p>
     * <p>Insert the even layouts in the left layout and the odd in the right layout</p>
     * <p>ATENTION: We remove the robot before sending it to modify</p>
     */
    private void setPlayRobots(){
		LinearLayout li = (LinearLayout) this.findViewById(R.id.MAINRobots);
		LinearLayout li2 = (LinearLayout) this.findViewById(R.id.MAINRobots2);
		int i=0;

		for(final Robot r: this.play.robots){
			final int list= i;
			final LinearLayout nueva = new LinearLayout(this);
			nueva.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, 0, 1));
			nueva.setOrientation(LinearLayout.VERTICAL);
			nueva.setPadding(8, 8, 8, 8);
			
			/* Create a imageview and a TV */
			/*TV*/
			TextView nameTV = new TextView(this);
			nameTV.setText(r.name);
			/*Primero es ancho, segundo alto*/
			nameTV.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
			nameTV.setGravity(Gravity.CENTER);
			nueva.addView(nameTV);
			if(i%2==0)
				li.addView(nueva);
			else
				li2.addView(nueva);
			/*ImageView*/
            ImageView imageView = new ImageView(this);
            imageView.setTag(r);
            nueva.setTag(r);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));// ancho y alto
            imageView.setBackground(this.images.resizeImage(r.imageName, main_interfaz.RECTANGLE_BUTTON));
			/* Add objects to row. */
			nueva.addView(imageView);
			imageView.setOnTouchListener(new  OnTouchListener(){
	        	public boolean onTouch(View view, MotionEvent motionEvent) {
	                if (motionEvent.getActionMasked() == MotionEvent.ACTION_DOWN) {
	                    DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
	                    ClipData data = ClipData.newPlainText("parent", "lin");
	                    view.startDrag(data, shadowBuilder, view, 0);
	                    return true;
	                }
	                else
	                	return false;
	        }});

			nueva.setOnDragListener(new DragRobotScenario(this.play, this.images));
			/*If it exist, creates, if not, deletes*/
			nueva.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					AbsoluteLayout scenario = (AbsoluteLayout) findViewById(R.id.MAINscenario);
					if(laysRobots[list]==null){
						int x, y, height;
						//y= Math.round(nueva.getY());
						int[] location = new int[2];
						int[] scenarioloc = new int[2];
						scenario.getLocationOnScreen(scenarioloc);
						nueva.getLocationOnScreen(location);
						y=location[1]-scenarioloc[1];
						if(list%2==0){
							x= 0;
						}
						else{
							x= -1;
						}
						height= nueva.getHeight();
						addRobotActionsView(x, y, height, r, list);
					}
					else{
						scenario.removeView(findViewById(laysRobots[list]));
						laysRobots[list]=null;
					}
				}
			});
			nueva.setOnLongClickListener(new OnLongClickListener() {
				Robot send =r;
				public boolean onLongClick(View v) {
					Intent intent = new Intent(main_interfaz.this, create_robot.class);
					intent.putExtra("Robot", send);
					play.RemoveRobot(send);
					intent.putExtra("Play", play);
					intent.putExtra("previousActivity", "com.interfaces.robotstories.main_interfaz");
					startActivity(intent);
					finish();
					return true;
				}
			});
			i++;
			}
		/*id layouts robot*/
		this.laysRobots= new Integer[i];
    }
    /**
     * Creates the view which shows the actions of the robot. It is created in an specific place inside the scenario
     * 
     * @param x the x coordenate inside the scenario to draw the action view
     * @param y the y coordenate inside the scenario to draw the action view
     * @param height the height of the created view
     * @param r the robot which actions will be shown
     * @param posRobot the position of the robot inside the play (for setting the id of the created view)
     */
    
	protected void addRobotActionsView(int x, int y, int height, Robot r, int posRobot) {
		AbsoluteLayout scenario = (AbsoluteLayout) this.findViewById(R.id.MAINscenario);
		if(height<main_interfaz.RECTANGLE_BUTTON)
			height= height + main_interfaz.RECTANGLE_BUTTON;
		int maximumSize= scenario.getMeasuredWidth()/3;
    	HorizontalScrollView scroll= new HorizontalScrollView(this);
    	LinearLayout aux= new LinearLayout(this);
    	scroll.setId(posRobot);
    	
		if (x<0){
			aux.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.FILL_PARENT));
			
			LinearLayout rightAuxiliar= new LinearLayout(this);
			scroll.addView(rightAuxiliar);
			scroll.setFillViewport(true);
			
			rightAuxiliar.addView(aux);
			rightAuxiliar.setGravity(Gravity.RIGHT);
			
			/*Get x coordinate*/
			x= scenario.getMeasuredWidth()-maximumSize;
		}
		else
			scroll.addView(aux);
		
		aux.setGravity(Gravity.CENTER_VERTICAL);
		aux.setBackgroundResource(R.drawable.cells_robot_actions);
		
    	AbsoluteLayout.LayoutParams lp = new AbsoluteLayout.LayoutParams(maximumSize, height, x, y);
    	scenario.addView(scroll, lp);
    	
    	this.laysRobots[posRobot]=posRobot;
    	aux.setTag(r.name);
    	int posAccion=0;
    	for(Accion a: r.actions){
    		LinearLayout accion = this.addActiontoRobot(posAccion, aux);
    		accion.setBackground(images.resizeImage(a.image,main_interfaz.RECTANGLE_BUTTON));
    		accion.setTag(a);
    		posAccion++;
    	}
    	/*Last accion*/
    	this.addActiontoRobot(posAccion, aux);
	}
    /**
     * Return an empty LinearLayout whit a DragListener which will be used to add more actions to a robot, and onTouchListener which will
     * be used to change the position of the action inside the robot, or of deleting the action
     * 
     * @param pos the position of the action inside the robot ("chronological" order)
     * @param allActions the LinearLayout which contains all the previous actions
     * @return an empty linearLayout whit the listeners
     */
    protected LinearLayout addActiontoRobot (int pos, LinearLayout allActions){
    	LinearLayout inside= new LinearLayout(this);
    	inside.setLayoutParams(new LinearLayout.LayoutParams(main_interfaz.RECTANGLE_BUTTON, main_interfaz.RECTANGLE_BUTTON));
		allActions.addView(inside);
    	inside.setOnDragListener(new DragActionRobot(pos));
		inside.setOnTouchListener(new  OnTouchListener(){
        	public boolean onTouch(View view, MotionEvent motionEvent) {
        		/*If there is no tag it means it is empty*/
        		if(view.getTag()!=null){
	                if (motionEvent.getActionMasked() == MotionEvent.ACTION_DOWN) {
	                    DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
	                    ClipData data = ClipData.newPlainText("parent", "robotAction");
	                    view.startDrag(data, shadowBuilder, view, 0);
	                    return true;
	                }
	                else
	                	return false;
        		}
        		return false;
        }});
    	return inside;
    }

    /**
     * It is called by the listener of the button of modify play. Calls the activity "create_play" and sends the play to be modify, then
     * finish the current activity
     */
    private void createPlay(){
		Intent intent = new Intent(main_interfaz.this, create_play.class);
		intent.putExtra("Play", this.play);
		startActivity(intent);
		finish();
    }
    /**
     * It is called by the method OnCreate. Read the bundle whit the information of the play, creates the imageHandler and the BluetoothMethods, 
     * set the name of the play in the modify play button, and calls the methods "setPlatActions" and "setPlayRobots"
     *
	 * @return <p>true if everything is allright</p>
	 * 			<p>false if the bundle is null</p>
	 */
	private boolean iniMainInter() {
		
		Bundle extras = getIntent().getExtras();
		if(extras == null)
			return false;
			
		this.play = (Play) extras.getSerializable("Play");
		
		this.images = new ImageHandler(this);
		this.bluetooth = new BluetoothMethods(this);
		if(this.bluetooth.initializeBlueToothSend()==false){
			Toast.makeText(this, "Problems at the time of creating the bluetooth", Toast.LENGTH_SHORT).show();
		}
		
		Button create = (Button)this.findViewById(R.id.name);
		create.setText(this.play.name);
		
		setPlayActions();
		setPlayRobots();
		
		return true;
	}
	
	protected Dialog onCreateDialog(final int id, Bundle args) {
		AlertDialog dialog=null;
		AlertDialog.Builder builder =null;
		
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    final View layout ;
		
		switch(id){
			case ACCION_DIALOG:
				layout = inflater.inflate(R.layout.properties_layout, null);
				builder = new AlertDialog.Builder(this);
				builder.setTitle("Define the parameters of the action");
				final SeekBar bar= (SeekBar) layout.findViewById(R.id.PROPERTIESPowerBar);
				bar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
					public void onProgressChanged(SeekBar seekBar, int progress,
							boolean fromUser) {
						TextView power= (TextView) layout.findViewById(R.id.PROPERTIESSeekValue);
						power.setText(Integer.toString(progress));
						
					}
					public void onStartTrackingTouch(SeekBar seekBar) {}
					public void onStopTrackingTouch(SeekBar seekBar) {}    
				});
				break;
			default:
				return null;
			}
		/*Create cancel button*/
		builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
    			LinearLayout padre= (LinearLayout) selectedView.getParent();
    			padre.removeViewAt(padre.indexOfChild(selectedView)+1);
				removeDialog(id);
			}
		});
		/*Save button*/
		builder.setPositiveButton(R.string.PROPERTIESSave, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				if(saveProperties(layout)){
					selectedView.setBackground(images.resizeImage(selectedAccion.image,main_interfaz.RECTANGLE_BUTTON));
					selectedView.setTag(selectedAccion);
					removeDialog(id);
				}
				else{
	    			LinearLayout padre= (LinearLayout) selectedView.getParent();
	    			padre.removeViewAt(padre.indexOfChild(selectedView)+1);
					removeDialog(id);
				}
					
			}
		});
		/*Display dialog*/
		builder.setCancelable(false);
		builder.setView(layout);
		dialog = builder.create();
		this.setEnabilityDialog(layout);

		return dialog;
	}

	/**
	 * <p>Read the values inserted in the Properties dialog, and check if they are valid and filled.</p>
	 * <p>If they are, creates a new action (calling "transformForRobot") and add it to the robot, graphically and internally</p>
	 * <p>If they are not shows a toast indicating the first find error and return false</p>
	 * 
	 * @param layout the layout of the dialog
	 * @return <p>true if everything is allright and the action is added correctly</p>
	 * 			<p>false if there is any missing value or a wrong value and the action is not added</p>
	 */
	protected boolean saveProperties(View layout) {
		TextView bar= (TextView) layout.findViewById(R.id.PROPERTIESSeekValue);
		int power= Integer.parseInt((String) bar.getText());
		LinearLayout second= (LinearLayout) layout.findViewById(R.id.PROPERTIESSecond);
		int number= (Integer) second.getTag();
		ArrayList<String> names= new ArrayList<String>();
		ArrayList<Integer> values= new ArrayList<Integer>();
		for(int i=0; i<number; i++){
			LinearLayout property= (LinearLayout) layout.findViewById(i+1);
			EditText value =(EditText) property.getChildAt(1);
			int numValue=0;
			if(value.getText().toString().length()>0){
				numValue = Integer.parseInt(value.getText().toString());
				if(numValue<= 0){
					Toast.makeText(getApplicationContext(), "Invalid value", Toast.LENGTH_LONG).show();
					return false;
				}
			}
			else{
				Toast.makeText(getApplicationContext(), "Empty value", Toast.LENGTH_LONG).show();
				return false;
			}
			values.add(numValue);
		}
		
		Accion nueva;
		if(number==0)
			nueva= this.selectedAccion.transformForRobot(power,names,values,null);
		else
			nueva= this.selectedAccion.transformForRobot(power,selectedAccion.commonAtts,values,null);
		
		this.selectedRobot.addAction(nueva, positionAcc);
		this.selectedAccion= nueva;
		return true;
	}
	/**
	 * <p>Set the visibility of the views of the dialog depending on if the values which will be inserted on them are contained in the common
	 * attributes of the action. </p>
	 * <p>Creates the needed layouts inside the LinearLayout "PROPERTIESsecond" of the dialog, to insert any second property value, 
	 * whit a textview whit the name of the value and an edit text for setting the value of this property (only for inserting numeric values).
	 * Adds a tag indicating the number of the layouts created</p>
	 * 
	 * @param layout the layout of the dialog
	 */
	private void setEnabilityDialog(View layout) {
		/*To know if secondProperty is inside the common atts*/
		int number=0;
		
		if(!this.selectedAccion.commonAtts.contains("power")){
			RelativeLayout power= (RelativeLayout) layout.findViewById(R.id.PROPERTIESLayPower);
			power.setVisibility(View.INVISIBLE);
			TextView bar= (TextView) layout.findViewById(R.id.PROPERTIESSeekValue);
			bar.setText("-1");
		}
		else
			number++;
		
		if(!this.selectedAccion.commonAtts.contains("target")){
			Button target = (Button) layout.findViewById(R.id.PROPERTIESTargetButton);
			target.setVisibility(View.INVISIBLE);
		}
		else
			number++;
		
		LinearLayout second= (LinearLayout) layout.findViewById(R.id.PROPERTIESSecond);
		if(number==selectedAccion.commonAtts.size()){
			second.setVisibility(View.INVISIBLE);
			second.setTag(0);
		}
		else{
			int layoutsCreated=0;
			for(String s: selectedAccion.commonAtts){
				if((!s.equals("power"))&&(!s.equals("target"))){
					layoutsCreated++;
					LinearLayout nuevo= new LinearLayout (this);
					TextView text= new TextView(this);
					text.setText(s);
					text.setTextAppearance(this, R.style.textViewCreateAction);
					EditText edit = new EditText(this);
					edit.setTextAppearance(this, R.style.NumberET);
					edit.setInputType(InputType.TYPE_CLASS_NUMBER);
					text.setPadding(0, 10, 10, 0);
					nuevo.addView(text);
					nuevo.addView(edit);
					nuevo.setId(layoutsCreated);
					second.addView(nuevo);
				}
			}
			second.setTag(layoutsCreated);
		}
		
	}

	/**
	 * <p>Class which implements an OnDragListener. It is used in the method "AddActiontoRobot". </p>
	 * <p>This drag listener creates the common properties dialog if necessary. The deletion of any action of a robot is done in 
	 * DragRobotScenario</p>
	 * 
	 * @author Arturo Gil
	 *
	 */
	public class DragActionRobot implements OnDragListener {

		int position;
		
		/**
		 * @param position the position where the action will be added
		 */
		public DragActionRobot (int position){
			this.position= position;
		}
		/*TERMINAR DE COMENTAR*/
		/*if the slot is ocupated, remove the action, we will have to change this*/
		public boolean onDrag(View v, DragEvent event) {
			View shadow= (View) event.getLocalState();
			switch (event.getAction()) {
			case DragEvent.ACTION_DRAG_STARTED:
				return true;
			case DragEvent.ACTION_DRAG_ENTERED:
				return true;
		    case DragEvent.ACTION_DROP:
		    	/*Gets type*/
		    	if(event.getClipData().getItemCount()==0)
		    		return false;
		    	String aux= (String) event.getClipData().getItemAt(0).getText();
		    	/*New action*/
		    	if(aux.equals("action")){
		    		Accion a=(Accion) shadow.getTag();
		    		LinearLayout robotActs=  (LinearLayout) v.getParent();
		    		String name= (String) robotActs.getTag();
		    		Robot r= play.findRobotByName(name);
		    		/*Add a new slot if we add the action to the last one*/
		    		if(r.actions.size()== this.position)
		    			addActiontoRobot(this.position+1, robotActs);
		    		/*Remove the view if we are trying to add a view to a erroneous created view*/
		    		else if(r.actions.size()< this.position){
		    			LinearLayout padre= (LinearLayout) v.getParent();
		    			padre.removeView(v);
		    			return false;
		    		}
		    		/*if the slot is ocupated, REMOVE the action*/
		    		else{
		    			r.removeAccionAt (this.position);
		    		}
		    		/*If we have common atts, we have to create the properties dialog*/
		    		if(a.commonAtts.isEmpty()==false){
		    			selectedAccion=a;
		    			selectedRobot=r;
		    			selectedView=v;
		    			positionAcc=this.position;
		    			showDialog(main_interfaz.ACCION_DIALOG);
		    		}
		    		/*If not we can add it directly*/
		    		else{
		    			r.addAction(a, this.position);
		    			v.setTag(a);
		    			v.setBackground(images.resizeImage(a.image,main_interfaz.RECTANGLE_BUTTON));
		    		}	
		    	}
		    	/*Action already created (delete or move)*/
		    	else if(aux.equals("robotAction")){
		    		Accion a=(Accion) shadow.getTag();
		    		LinearLayout robotActs=  (LinearLayout) v.getParent();
		    		/*get the target robot*/
		    		String name= (String) robotActs.getTag();
		    		Robot r= play.findRobotByName(name);
		    		/*Get the robot parent*/
		    		LinearLayout viewParent= (LinearLayout) shadow.getParent();
		    		Robot padre= play.findRobotByName((String) viewParent.getTag());
		    		/*If they are not the same, it will be deleted, but not here, in DRAGROBOTSCENARIO*/
		    		if(r.equals(padre)==false){
		    			return false;
		    		}
		    		if((this.position+1)==padre.actions.size()){
		    			return true;
		    		}
		    		/*Remove or move? REMOVE*/
		    		else{
		    			padre.removeAccionAt (this.position);
		    		}
	    			padre.changePosAction(a, this.position);
	    			v.setTag(a);
	    			v.setBackground(images.resizeImage(a.image,main_interfaz.RECTANGLE_BUTTON));
	    			/*Now we remove the action from the previous view*/
	    			viewParent.removeView(shadow);
	    			/*If this is the last action added, we add a new empty space*/
	    			if((this.position+1)>padre.actions.size()){
	    				addActiontoRobot(this.position, robotActs);
	    			}
		    	}
		    	else
		    		return false;
		    	
		    	return true;
			}
			return false;
		}
	}
}

