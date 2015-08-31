package com.interfaces.robotstories;

import com.example.robotstories.R;
import com.types.robotstories.Play;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * <p>Creates a graphic interface to create or modify the properties of a play</p>
 * <p>In this interface is show some edittext to insert data from the play (name, size and description) and under them some buttons
 * to add robots and actions to the play, creates an auxiliar play if there is no one created previously before adding robots or actions.</p>
 * <p>Can't go to "main_interfaz" until all the data are saved correctly and a play is created</p>
 * @author Arturo Gil
 */
public class create_play extends Activity{
	

    /**
     * The value of this constant is {@value}. It is the size of the image for the ImageButton
     */
	private static final int RECTANGLE_BUTTON=40;
	private String title, desc;
	private int VSize, HSize;
	Play play;
	
	/**
	 * Add listeners to buttons, and calls to iniLayout.
	 */
	protected void onCreate(Bundle savedInstanceState) {
	       super.onCreate(savedInstanceState);
	       setContentView(R.layout.create_play);
	       iniLayout();
	        
	        final Spinner type= (Spinner) this.findViewById(R.id.TypeSize);

			type.setOnItemSelectedListener( new OnItemSelectedListener ()   {
				public void onItemSelected(AdapterView<?> parent, View view,
						int position, long id) {
					changeType2();	
				}
				public void onNothingSelected(AdapterView<?> parent) {
					type.setSelection(0);
			}});
			
			Button start= (Button) this.findViewById(R.id.Begin);
			start.setOnClickListener(new OnClickListener() {    
				public void onClick(View v) {
					saveAndGo();
			}});
			/*Add robot*/
			Button robot= (Button) this.findViewById(R.id.AddRobot);
			robot.setOnClickListener(new OnClickListener() {    
				public void onClick(View v) {
					Intent intent = new Intent(create_play.this,create_robot.class);
					intent.putExtra("Play", play);
					intent.putExtra("previousActivity", "com.interfaces.robotstories.create_play");
					startActivity(intent);
					finish();
			}});
			/*Add action*/
			Button action= (Button) this.findViewById(R.id.AddAction);
			action.setOnClickListener(new OnClickListener() {    
				public void onClick(View v) {
					Intent intent = new Intent(create_play.this,create_action.class);
					intent.putExtra("Play", play);
					intent.putExtra("previousActivity", "com.interfaces.robotstories.create_play");
					startActivity(intent);
					finish();
			}});
	 }
	 /**Save title, desc and size and come back to main interfaz, 
	  * if all the data is alright, check if there exist already a play, if not, it create it and
	  * send to the main interfaz*/
	private void saveAndGo(){
		boolean go=true;
		
		/*Title*/
		EditText aux= (EditText)findViewById(R.id.NamePlay);
		title= aux.getText().toString();
		if(title.length()<=0){
			aux.setError("Introduce a valid name for the play");
			go=false;
		}
		else
			aux.setError(null);
		
		/*Horizontal size*/
		aux= (EditText)findViewById(R.id.HSize);
		if(aux.getText().toString().length()>0){
			HSize = Integer.parseInt(aux.getText().toString());
			if(HSize<= 0){
				aux.setError("Introduce a valid horizontal size for the scenario");
				go=false;
			}
		}
		else{
			aux.setError("Introduce an horizontal size for the scenario");
			go=false;
		}
		
		/*Vertical size*/
		aux= (EditText)findViewById(R.id.VSize);
		if(aux.getText().toString().length()>0){
			VSize = Integer.parseInt(aux.getText().toString());
			if(VSize<= 0){
				aux.setError("Introduce a valid vertical size for the scenario");
				go=false;
			}
		}
		else{
			aux.setError("Introduce an horizontal size for the scenario");
			go=false;
		}
		/*Are everything ready to go?*/
		if(!go)
			return;
		/*Description*/
		aux= (EditText)findViewById(R.id.Description);
		desc = aux.getText().toString();
		         
		this.play.name= title;
		this.play.desc= desc;
		this.play.HSize=HSize;
		this.play.VSize=VSize;
		
		/*Go*/
		Intent intent = new Intent(create_play.this,main_interfaz.class);
		intent.putExtra("Play", this.play);
		startActivity(intent);
		finish();
	}
	/**
	 * This onPause method is kind of special, finish the activity and come back to the activity who called this activity
	 */
	 public void onBackPressed() { 
	        new AlertDialog.Builder(this)
	            .setIcon(android.R.drawable.ic_dialog_alert)
	            .setTitle("Closing Activity")
	            .setMessage("Do you want to save or exit?")
	            .setPositiveButton("I want to save", new DialogInterface.OnClickListener(){

	            public void onClick(DialogInterface dialog, int which) {
	            	saveAndGo();
	            }

	        })
	        .setNegativeButton("I want to exit", new DialogInterface.OnClickListener(){

	            public void onClick(DialogInterface dialog, int which) {
	            	finish();
	            }
	        })
	        .show();
	 }

	/**Set an appropiated size for the images of the buttons (still only add action, add robot and play)
	 * Set name of vertical size based on spin value
	 * Set previous defined values in their places (if we go to create_play to modify a play)*/
	private void iniLayout() {
		/*ADD ACTION*/
		Button botaux = (Button)this.findViewById(R.id.AddAction);
		Drawable imageaux= botaux.getCompoundDrawables()[1];
		Bitmap bitmap = ((BitmapDrawable) imageaux).getBitmap();
		Drawable imagechanged = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, RECTANGLE_BUTTON, RECTANGLE_BUTTON, true));
		botaux.setCompoundDrawablesWithIntrinsicBounds(null, imagechanged, null, null);
		
		/*ADD ROBOT*/
		botaux = (Button)this.findViewById(R.id.AddRobot);
		imageaux= botaux.getCompoundDrawables()[1];
		bitmap = ((BitmapDrawable) imageaux).getBitmap();
		imagechanged = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, RECTANGLE_BUTTON, RECTANGLE_BUTTON, true));
		botaux.setCompoundDrawablesWithIntrinsicBounds(null, imagechanged, null, null);
		
		/*BEGIN*/
		botaux = (Button)this.findViewById(R.id.Begin);
		imageaux= botaux.getCompoundDrawables()[1];
		bitmap = ((BitmapDrawable) imageaux).getBitmap();
		imagechanged = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, RECTANGLE_BUTTON, RECTANGLE_BUTTON, true));
		botaux.setCompoundDrawablesWithIntrinsicBounds(null, imagechanged, null, null);
		
		/*Previous values*/
		Bundle extras = getIntent().getExtras();
		if(extras == null){
			this.play = new Play();
			return;
		/*It seems we have some previous values, let's set them in their places*/
		}else{         
			this.play = (Play) extras.getSerializable("Play");

			/*Title*/
			EditText aux= (EditText)findViewById(R.id.NamePlay);
			aux.setText(play.name);
			/*Horizontal size*/
			aux= (EditText)findViewById(R.id.HSize);
			aux.setText(Integer.toString(play.HSize));	
			/*Vertical size*/
			aux= (EditText)findViewById(R.id.VSize);
			aux.setText(Integer.toString(play.VSize));
			/*Desc*/
			aux= (EditText)findViewById(R.id.Description);
			
			aux.setText(play.desc);
		}
	}
	
	/** Set type of vertical size (mm, inches, cm, whatever) 
	 * based on spin value asigned for type of horizontal size*/
	private void changeType2(){
		Spinner type= (Spinner) this.findViewById(R.id.TypeSize);
		String selected= (String) type.getSelectedItem();
		TextView type2 = (TextView) this.findViewById(R.id.TypeSize2);
		type2.setText(selected);
	}
	
}
