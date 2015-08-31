package com.interfaces.robotstories;

import java.util.ArrayList;

import com.auxiliar.robotstories.AccionGridAdapter;
import com.auxiliar.robotstories.DragActionToSubaction;
import com.auxiliar.robotstories.ImageHandler;
import com.auxiliar.robotstories.SubAccionGridAdapter;
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
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TabHost.TabContentFactory;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

/**
 * <p>This activity creates a graphic interface to create or modify an action.</p>
 * <p>This interface shows a tabhost which contains a gridview in which all the action are spread in different categories 
 * (predef, created, all). This actions can be once again spread by the search engine (NOT IMPLEMENTED), whcih allows to find the
 * actions by motor type an motor name.</p>
 * <p>Under this tabhost appear a RelativeLayout which contains the properties of this action. We can select the duration of the action,
 * by different properties (seconds, angles, etc.) or by arriving to a target (another robot,a place of the scenario, etc.) by checking a 
 * radio button next to it. We can select any of them as a common property, which means that we don't have to give them a concrete
 * value now, and that the value will be given in the scenario by the user and will be common to all subactions which share this common
 * property.</p>
 * <p>In the left side, appear another gridview which contains all the subactions of the action. The actions can be dragged into this
 * gridview and will be turned into subactions. The subactions are divided in "macros" which contains all the actions which will begin at the
 * same time. Each macro has the same slots as ports has the robot</p>
 * <p>If one subactions involves more than one motor, the action will need more than one slot (NOT IMPLEMENTED)</p>
 * <p>Doesn't create the action until a name is set.</p>
 * 
 * @author Arturo Gil
 *
 */
@SuppressWarnings({"deprecation", "rawtypes"})
public class create_action extends Activity{
	
	/*TRABAJAR CON TARGET, hacer como con common att, y quitar lo de ponerlo automatico*/
	
    /**
     * The value of this constant is {@value}. It is the code to call the dialog for defining a target
     */
	private static final int TARGET_DIALOG= 0;
    /**
     * The value of this constant is {@value}. It is the code to call the dialog for set a picture for the image
     */	
	private static final int PICTURE_DIALOG= 1;
    /**
     * The value of this constant is {@value}. It is the size of the image for the ImageButtons and the actions of the gridview
     */
	private static final int RECTANGLE_BUTTON=80;
    /**
     * The value of this constant is {@value}. It is the size of the images of the picture dialog
     */
	private static final int RECTANGLE_SMALLBUTTON=40;
	
    /**
     * Some aux functions to give format to the pictures
     */
	ImageHandler images;
	
	/*Obtained*/
	private String name;

	/**
	 * This is the name of the image asociated to the action. It is initialized whit the value "default_image"
	 */
	private String imageName= "default_image";
	private ArrayList<Accion> allActions;
	private View selectedActionView;
	private Accion selectedSubAc;
	private String target;
	private SubAccionGridAdapter adapter;
	
	/*Received*/
	private Play play;
	private Accion accion;
	/**
	 *This is the name of the activity who call this activity, necesary for ensure to come back
	 *to the correct place once we finish 
	 */
	private String previousActivity;
	
	/**
	 * Add listeners to buttons,  make the buttons for setting properties not enabled and calls to iniLayout. 
	 * After checks if we come here to modify a previous activity, and then set adapters for the principal gridview (subactions) consequentely
	 */
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_action);
        
   		iniLayout();
   		
		Button aux =(Button) this.findViewById(R.id.ACTIONTargetButton);
		aux.setOnClickListener(new OnClickListener() {    
			public void onClick(View v) {
				showDialog(TARGET_DIALOG);
		}});
   		
		final SeekBar bar =(SeekBar) this.findViewById(R.id.ACTIONPowerBar);
		bar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				setPowerValue(bar.getProgress());
			}
			public void onStartTrackingTouch(SeekBar seekBar) {}
			public void onStopTrackingTouch(SeekBar seekBar) {}    
		});
		
		/*Select second property*/
		RadioGroup activation= (RadioGroup) this.findViewById(R.id.ACTIONSelect);
		activation.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				enableSecondProperty(checkedId);
			}		
		});
		/*Save robot*/
		aux= (Button) this.findViewById(R.id.ACTIONSave);
		aux.setOnClickListener(new OnClickListener() {    
			public void onClick(View v) {
				saveAndGo();
		}});

		/*Select Image*/
		aux= (Button) this.findViewById(R.id.ACTIONPicture);
		aux.setOnClickListener(new OnClickListener() {    
			public void onClick(View v) {
				showDialog(PICTURE_DIALOG);
		}});
		
		/*Initialize all unclickable, for good purposes*/
		enableSecondProperty(-1);
		bar.setEnabled(false);
        CheckBox check = (CheckBox) this.findViewById(R.id.ACTIONcheckPower);
		check.setEnabled(false);
		for (int i = 0; i < activation.getChildCount(); i++) {
			activation.getChildAt(i).setEnabled(false);
		}
		
		/*Set the gridview adapter*/
		GridView principal = (GridView) this.findViewById(R.id.ACTIONsubacts);
		principal.setColumnWidth(RECTANGLE_BUTTON*2);
		SubAccionGridAdapter adapter = new SubAccionGridAdapter(this, create_action.RECTANGLE_BUTTON, "SubActions", this.images, Robot.MOTOR_PORTS);
		if(this.accion!=null){
			EditText name= (EditText) this.findViewById(R.id.ACTIONName);
			name.setText(this.accion.name); 
			adapter.setActions(this.accion.subactions);
		}
		else
			adapter.AddMacro();
		principal.setAdapter(adapter);
		this.adapter= adapter;
		this.setCommonItems();
	}

	/**
	 * Set listeners for check buttons, selection which properties are common for all the subactions and
	 * will be given value at the time of assigning the action to the robot which means basically 
	 * deciding the enability for the views who allows changing the values of the propeties of the subactions
	 */
	private void setCommonItems() {
		/*Power*/
		CheckBox check = (CheckBox) this.findViewById(R.id.ACTIONcheckPower);
		check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				SeekBar bar =(SeekBar)findViewById(R.id.ACTIONPowerBar);
				if(isChecked){
					setPowerValue(-1);
					selectedSubAc.addCommonAtt("power");
					bar.setEnabled(false);
				}else{
					setPowerValue(bar.getProgress());
					selectedSubAc.removeCommonAtt("power");
					bar.setEnabled(true);
				}
			}
		});
		/*Second Property*/
		check = (CheckBox) this.findViewById(R.id.ACTIONcheckSecond);
		check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				EditText et = (EditText) findViewById(R.id.ACTIONSecondPropertyET);
				Spinner spin= (Spinner) findViewById(R.id.ACTIONSecondPropertySP);
				if(isChecked){
					et.setText("-1");
					spin.setClickable(false);
					et.setFocusable(false);
					selectedSubAc.addCommonAtt((String)spin.getSelectedItem());
				}else{
					et.setText("0");
					spin.setClickable(true);
					et.setFocusable(true);
					et.setFocusableInTouchMode(true);
					selectedSubAc.removeCommonAtt((String)spin.getSelectedItem());
				}
		}});
		/*Target*/
		check = (CheckBox) this.findViewById(R.id.ACTIONcheckTarget);
		check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					selectedSubAc.addCommonAtt("target");
				}else{
					selectedSubAc.removeCommonAtt("target");
				}
			}

		});
	}

	/**
	 * Save all the data and come back to the previous activity. All the subactions are taken from the adapter
	 * */

	protected void saveAndGo() {
		boolean go=true;
		
		/*Name, checks if the name is already in use*/
		EditText aux= (EditText)findViewById(R.id.ACTIONName);
		name= aux.getText().toString();
		if(name.length()<=0){
			aux.setError("Introduce a valid name for an action");
			go=false;
		}
		else{
			if(this.play.findActionByName(name)!=null){
				aux.setError("This name is already on use");
				go=false;
			}
			else
				aux.setError(null);
		}
		if(go==false)
			return;
		/*If we didn't came here to modify an already created action, we create a new one*/
		if(this.accion==null)
			this.accion=new Accion();
		/*Let's check the motors of the subactions*/
		ArrayList<Accion[]> subactions= this.adapter.getAcciones();
		for(Accion[] macro: subactions){
			for(Accion a: macro){
				if(a!=null){
					if (this.accion.addMotors(a.motors)==false){
						Toast.makeText(this, "You are creating an action whit too many different motors", Toast.LENGTH_SHORT).show();
						this.accion.motors.clear();
						return;
					}
				}
			}
		}
		/*If there is no problem whit the motors we can add the subactions*/
		this.accion.subactions=subactions;
		this.accion.name= this.name;
		this.accion.image= this.imageName;
		this.accion.getSubActsCommonAtt();
		if(this.accion.setCommonTarget()==false){
			Toast.makeText(this, "You are creating an action whit different types of target", Toast.LENGTH_SHORT).show();
			this.accion.motors.clear();
			return;
		}
			
		/*Add action to play, checks if it already exists*/
		this.play.AddAction(this.accion);
		
		/**come back to previous activity, if any problem, gets blocked (should not happen never, 
		 * if it happens the responsability is from the calling class, not sending the correct name)*/
		Class comeBack;
		try {
			comeBack= Class.forName(this.previousActivity);
			Intent intent = new Intent(create_action.this, comeBack);
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

    public void onBackPressed() { 
        new AlertDialog.Builder(this)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle("Closing Activity")
            .setMessage("Are you sure you want to leave whitout saving?")
            .setPositiveButton("Yes", new DialogInterface.OnClickListener(){

            public void onClick(DialogInterface dialog, int which) {
        		Class comeBack;
        		try {
        			comeBack= Class.forName(previousActivity);
        			Intent intent = new Intent(create_action.this, comeBack);
        			intent.putExtra("Play", play);
        			startActivity(intent);
        			finish();
        		} catch (ClassNotFoundException e) {
        		    AlertDialog ad = new AlertDialog.Builder(getApplicationContext()).create();  
        		    ad.setCancelable(false); 
        		    ad.setMessage("Fail loading previous class "+ previousActivity);  
        		    ad.show();
        			e.printStackTrace();
        		} 
            }

        })
        .setNegativeButton("No", null)
        .show();
    }

	/**
	 * Let you choose between rotations/angle/second and selecting a target, only allows to set as a common property
	 * the one which is selected
	 * 
	 * @param checkedId id of the line to activate
	 */
	protected void enableSecondProperty(int checkedId) {
		
		Spinner spin= (Spinner) this.findViewById(R.id.ACTIONSecondPropertySP);
		Button but=(Button) this.findViewById(R.id.ACTIONTargetButton);
		EditText et = (EditText) this.findViewById(R.id.ACTIONSecondPropertyET);
		CheckBox target = (CheckBox) this.findViewById(R.id.ACTIONcheckTarget);
		CheckBox secondProp = (CheckBox) this.findViewById(R.id.ACTIONcheckSecond);
		/*Second property checked*/
		if(checkedId==R.id.ACTIONRBSecProperty){
			secondProp.setClickable(true);
			spin.setClickable(true);
			et.setFocusable(true);
			et.setFocusableInTouchMode(true);
			
			but.setClickable(false);
			target.setClickable(false);
			target.setChecked(false);
		}
		/*target checked*/
		else if(checkedId==R.id.ACTIONRBTarget){
			spin.setClickable(false);
			et.setFocusable(false);
			secondProp.setClickable(false);
			secondProp.setChecked(false);
			
			but.setClickable(true);
			target.setClickable(true);
		}
		/*Default*/
		else{
			spin.setClickable(false);
			et.setFocusable(false);
			but.setClickable(false);
			target.setClickable(false);
			secondProp.setClickable(false);
		}
		return;
	}

	/**
	 * Set the TV next to the Power Bar to the actual value of the bar, and save the power value for the subactivity
	 * @param progress actual progress of the bar
	 */
	protected void setPowerValue(int progress) {
		TextView power= (TextView) this.findViewById(R.id.ACTIONSeekValue);
		if(this.selectedSubAc!=null)
			this.selectedSubAc.power=progress;
		power.setText(Integer.toString(progress));		
	}

	/**
	 * Initializes the values of play and previousActivity taken from extras, sets the complete list of actions (taken from predef and the created ones) 
	 * and calls the function in charge of initialize the tab for the previous actions, and for initializing the spinners (search engine)
	 */
	private void iniLayout() {
		Bundle extras = getIntent().getExtras();
		if(extras == null)
			finish();
		
		this.play = (Play) extras.getSerializable("Play");
		this.previousActivity = extras.getString("previousActivity");
		this.accion= (Accion) extras.getSerializable("Accion");
		if(this.play==null)
			finish();
		if(this.previousActivity==null)
			finish();
		this.allActions= new ArrayList<Accion>();
	    this.allActions.addAll(this.play.acciones);
	    this.allActions.addAll(this.play.predefAcciones);
	    this.images= new ImageHandler (this);
		
		/*Tab*/
		TabHost properties = (TabHost) findViewById(android.R.id.tabhost);
		this.iniTabHost(properties);	
		/*Power*/
		final SeekBar bar =(SeekBar) this.findViewById(R.id.ACTIONPowerBar);
		setPowerValue(bar.getProgress());
		/*Set Spinners values*/
		this.iniSpinners();
		
	}
	/**
	 * Set the values for the search engine:
	 * -First check the avalible motor types defined in the "string.xml", and add the value "ALL TYPES" (default value)
	 * -For the second check the names of all the motors defined in the play, and add the value "ALL MOTORS" (default value)
	 * -Set the listerner to make the search when the values are changed
	 * 
	 * Comment: The search is still not implemented
	 */
	private void iniSpinners(){
		final Spinner type= (Spinner) findViewById(R.id.ACTIONSearchType);
		final Spinner name = (Spinner) findViewById(R.id.ACTIONSearchName);

		/*First Spinner*/
		/*Get the original values*/
		String[] values = getResources().getStringArray(R.array.motorTypes);
		String[] newValues = new String [values.length+1];
		/*Set the new values*/
		newValues[0]= "ALL TYPES"; 
		for(int i=1; i<=values.length;i++){
			newValues[i]= values[i-1]; 
		}
		/*Create Spinner*/

		ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, newValues);
		type.setAdapter(spinnerArrayAdapter);
		
		this.setNameSpinnerValues(name, null);
		
		type.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				String nameValue=null;
				String typeValue=null;
				
				if(name.getSelectedItemPosition()!=0)
					nameValue=(String)name.getSelectedItem();
				
				if(type.getSelectedItemPosition()!=0){
					typeValue = (String)type.getSelectedItem();
				}
				setNameSpinnerValues(name, typeValue);
				filterBy(nameValue, typeValue);
			}

			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

		name.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {	
				String nameValue=null;
				String typeValue=null;
				
				if(name.getSelectedItemPosition()!=0)
					nameValue=(String)name.getSelectedItem();
				
				if(type.getSelectedItemPosition()!=0)
					typeValue = (String)type.getSelectedItem();
				
				filterBy(nameValue, typeValue);
			}

			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

	}
	
	/**
	 * Filter the name of motors of the spinners for the search, based on the type selected in the other spinner
	 * 
	 * @param name the spinner whit the names of the motors
	 * @param type type of the motors to be shown, null for all motors
	 */
	private void setNameSpinnerValues(final Spinner name, String type){
		String[] newValues = this.play.getMotorsByType(type);
		ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, newValues);
		name.setAdapter(spinnerArrayAdapter);
	}

	/**
	 * Filter the actions of the gridView based on a motor name or/and a motor type
	 * 
	 * @param name of the motor. Null for all names
	 * @param type of the motor. Null for all types
	 */
	protected void filterBy(String name, String type) {
		ArrayList<Accion> sorted;
		for(int i=0; i<3; i++){
			GridView acciones;
			if(i==0){
				acciones= (GridView) this.findViewById(R.id.ACTIONGVAll);
				sorted = this.play.sortByMotor(name, type, this.allActions);
			}
			else if (i==1){
				acciones= (GridView) this.findViewById(R.id.ACTIONGVCreated);
				sorted = this.play.sortByMotor(name, type, this.play.acciones);
			}
			else if (i==2){
				acciones= (GridView) this.findViewById(R.id.ACTIONGVPredef);
				sorted = this.play.sortByMotor(name, type, this.play.predefAcciones);
			}
			else
				return;
			
			if(sorted==null){
				Toast.makeText(this, "Corrupted motors", Toast.LENGTH_SHORT).show();
				return;
			}
			AccionGridAdapter adapter= (AccionGridAdapter) acciones.getAdapter();
			if(adapter==null)
				return;

			adapter.SetAcciones(sorted);
			adapter.notifyDataSetChanged();
		}
	}

	/**
	 * Initialize the tabhost using the definition on the XML, add the gridview whit the corresponding adapter 
	 * in which the corresponding actions are contained, to each tab. The gridview is obtained sending the adapter
	 * and the corresponding actions to "addPreviousActions"
	 * 
	 * @param tabHost The View in which the tabs are
	 */
	private void iniTabHost(TabHost tabHost) {
   		tabHost.setup();
    	final TabWidget tabWidget = tabHost.getTabWidget();
    	final FrameLayout tabContent = tabHost.getTabContentView();
    	// Get the original tab textviews and remove them from the viewgroup.
    	TextView[] originalTextViews = new TextView[tabWidget.getTabCount()];
    	for (int index = 0; index < tabWidget.getTabCount(); index++) {
    		originalTextViews[index] = (TextView) tabWidget.getChildTabViewAt(index);
    	}
    	tabWidget.removeAllViews();
    	// Ensure that all tab content childs are not visible at startup.
    	for (int index = 0; index < tabContent.getChildCount(); index++) {
    		tabContent.getChildAt(index).setVisibility(View.GONE);
    	}
    	// Create the tabspec based on the textview childs in the xml file.
    	// Or create simple tabspec instances in any other way...
    	for (int index = 0; index < originalTextViews.length; index++) {
    		final ArrayList<Accion> add;
	    	final TextView tabWidgetTextView = originalTextViews[index];
	    	final View tabContentView = tabContent.getChildAt(index);
	    	final AccionGridAdapter adapter;
	    	/*We get the tag to know what is inside*/
	    	String tag= (String) tabWidgetTextView.getTag();
	    	TabSpec tabSpec = tabHost.newTabSpec(tag);
	    	String text = (String) tabWidgetTextView.getText();
	   
	    	/*We send the action depending on which tab you select*/
	    	if(text.equals(this.getResources().getString(R.string.ActionPredefined))){
	    		add= this.play.predefAcciones;	
	    		adapter= new AccionGridAdapter(this, create_action.RECTANGLE_BUTTON, "predefActions", this.images);
	    	}
	    	else if (text.equals(this.getResources().getString(R.string.ActionCreated))){
	    		add= this.play.acciones;
	    		adapter= new AccionGridAdapter(this, create_action.RECTANGLE_BUTTON, "createdActions", this.images);
	    	}
	    	else if (text.equals(this.getResources().getString(R.string.ActionAll))){
	    		add= this.allActions;
	    		adapter= new AccionGridAdapter(this, create_action.RECTANGLE_BUTTON, "AllActions", this.images);
	    	}
	    	else
	    		return;
	    	tabSpec.setContent(new TabContentFactory() {
	    		public View createTabContent(String tag) {
	    			AccionGridAdapter adapterS=adapter;
	    			View actualizedGrid= addPreviousActions(tabContentView, add, adapterS);
	    			return actualizedGrid;
	    		}
	    	});
	    	if (tabWidgetTextView.getBackground() == null) {
	    		tabSpec.setIndicator(tabWidgetTextView.getText());
	    	}
	    	else{
	    		tabSpec.setIndicator(tabWidgetTextView.getText(), tabWidgetTextView.getBackground());
	    	}
	    	
	    	tabHost.addTab(tabSpec);
	    }
	}
	/**
	 * Add the actions to the adapter, after set the adapter to the grid layout, and finally set a listener to clicking
	 * in any item. Clicking in any item of the gridview calls the funtion "PrepareSubaction"
	 * 
	 * @param tabContentView place were the gridview is contained
	 * @param actions actions which will be contained in the new adapter
	 * @param adapter the adapter of the gridview
	 * @return
	 */
	protected View addPreviousActions(View tabContentView, ArrayList<Accion> actions, final AccionGridAdapter adapter) {
		GridView gridview= (GridView) tabContentView;
		gridview.setColumnWidth(create_action.RECTANGLE_BUTTON);
		for(Accion a: actions){
			adapter.AddAction(a);
		}
 
        gridview.setAdapter(adapter);
 
        gridview.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                    int position, long id) {
            		prepareSubAction(v);
            		selectedSubAc= (Accion) adapter.getItem(position);
            }
        });

		return gridview;
	}
	/**
	 * Set the enability for 
	 * -the seek bar for seting the power and its corresponding check button
	 * -The RadioGroup for selecting either a target either a second property
	 * 
	 * Change the background of the selectedview (R.color.selectionColor), and if there was other selected before, 
	 * take out the backgroundcolor of this previous activity, and set a listener for the new selectedview, which calls
	 * the function prepareForDrag
	 * 
	 * @param v, the clicked view
	 */
	protected void prepareSubAction(View v){
        final SeekBar bar =(SeekBar) findViewById(R.id.ACTIONPowerBar);
        bar.setEnabled(true);
        CheckBox check = (CheckBox) this.findViewById(R.id.ACTIONcheckPower);
		check.setEnabled(true);
		
        final RadioGroup activation= (RadioGroup) findViewById(R.id.ACTIONSelect);
		for (int i = 0; i < activation.getChildCount(); i++) {
			activation.getChildAt(i).setEnabled(true);
		}
		if(selectedActionView!=null){
			selectedActionView.setBackgroundColor(getTitleColor());
			selectedActionView.setOnTouchListener(null);
		}
		selectedActionView=v;
		v.setBackgroundColor(getResources().getColor(R.color.selectionColor));
		v.setOnTouchListener(  new  OnTouchListener(){
        	public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getActionMasked() == MotionEvent.ACTION_DOWN) {
                	prepareForDrag(view);
                    return true;
                }
                else
                	return false;
        	}});
	}
	/**
	 * Prepare a created action to be dragged and be converted into a subaction, read the values of the propeties and
	 * prepares them to be saved in the subaction. The action in which the subaction will be saved, 
	 * will chosen by the position in which the user drag it
	 * 
	 * @param view the view of the action to drag
	 */
	protected void prepareForDrag(View view) {
		Intent intent = new Intent(create_action.this, DragActionToSubaction.class);
		/*See if we have selected the target or the second property*/
		RadioGroup activation= (RadioGroup) this.findViewById(R.id.ACTIONSelect);
		if (activation.getCheckedRadioButtonId()==R.id.ACTIONRBTarget){
			this.selectedSubAc.target= target;
			this.selectedSubAc.typeSecondProperty=null;
		}
		else if(activation.getCheckedRadioButtonId()==R.id.ACTIONRBSecProperty){
			Spinner spin= (Spinner) this.findViewById(R.id.ACTIONSecondPropertySP);
			EditText et = (EditText) this.findViewById(R.id.ACTIONSecondPropertyET);
			this.selectedSubAc.typeSecondProperty=(String) spin.getSelectedItem();
			this.selectedSubAc.secondProperty= Integer.parseInt(et.getText().toString());
			this.selectedSubAc.target=null;
		}
		intent.putExtra("Accion", selectedSubAc);
        ClipData data = ClipData.newIntent("intent", intent);
        
        DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
        view.startDrag(data, shadowBuilder, view, 0);
	}

	/**
	 * Creates either
	 * -the dialog for selecting the target: Which creates 4 buttons (Scenario,Mobile object, non-mobile object, robot)
	 * giving them format (and click listener) in "GiveFormatButton"
	 * -The dialog for selecting a picture for the action, calling "setActionPics"
	 * 
	 * Creates a cancel button for both
	 * 
	 * @param id the id of the dialog to show
	 */
	protected Dialog onCreateDialog(final int id, Bundle args) {
		AlertDialog dialog=null;
		AlertDialog.Builder builder =null;
		
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    final View layout ;
		
		switch(id){
			case TARGET_DIALOG:
				layout = inflater.inflate(R.layout.target_dialog, (ViewGroup) findViewById(R.id.TARGETroot));
				builder = new AlertDialog.Builder(this);
				builder.setTitle("Select a target for the action");
				
				/*Give format to the four buttons*/
				Button botaux = (Button)layout.findViewById(R.id.TARGETScenario);
				this.giveFormatButton(botaux);
				botaux = (Button)layout.findViewById(R.id.TARGETMobObj);
				this.giveFormatButton(botaux);
				botaux = (Button)layout.findViewById(R.id.TARGETNonMobObj);
				this.giveFormatButton(botaux);
				botaux = (Button)layout.findViewById(R.id.TARGETRobot);
				this.giveFormatButton(botaux);
				break;
			case PICTURE_DIALOG:
				layout = inflater.inflate(R.layout.picture_dialog, (ViewGroup) findViewById(R.id.PICTURETable));
				builder = new AlertDialog.Builder(this);
				builder.setTitle("Select a image for the action");
				
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
		/*Display dialog*/
		builder.setView(layout);
		dialog = builder.create();
		
		return dialog;
	}

	/**
	 * Get the names of the images which are availables for represent an action from strings.xml (R.array.ActionImages),
	 * and shows them in the dialog inside a tablelayout which shows its name and the picure
	 * 
	 * @param layout the dialog in which the images are shown
	 */
	private void setActionPics(View layout) {
		
		String[] values = getResources().getStringArray(R.array.ActionImages);
		TableLayout tl = (TableLayout) layout.findViewById(R.id.PICTURETable);
		
		for(final String name: values){
			TableRow tr = new TableRow(this);
			tr.setGravity(Gravity.CENTER_HORIZONTAL);
			tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.FILL_PARENT));
			/* Create a button for the row */
			Button botaux = new Button (this);
			botaux.setText(name);
			botaux.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.FILL_PARENT));
			BitmapDrawable imagechanged= this.images.resizeImage(name, create_action.RECTANGLE_SMALLBUTTON);
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

	/**
	 * Set the picture of the button, and set the click listener which save the value of the selected target
	 * 
	 * @param botaux The button to give format
	 */
	private void giveFormatButton(final Button botaux) {
		final Drawable imageaux= botaux.getCompoundDrawables()[1];
		Bitmap bitmap = ((BitmapDrawable) imageaux).getBitmap();
		BitmapDrawable imagechanged = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, RECTANGLE_BUTTON, RECTANGLE_BUTTON, true));
		botaux.setCompoundDrawablesWithIntrinsicBounds(null, imagechanged, null, null);
		botaux.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				ImageView targetIm= (ImageView) findViewById(R.id.ACTIONTargetImage);
				targetIm.setImageDrawable(imageaux);
				target= (String) botaux.getText();
				removeDialog(TARGET_DIALOG);
			}
		});
	}

}
