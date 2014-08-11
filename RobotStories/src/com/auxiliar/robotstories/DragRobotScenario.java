package com.auxiliar.robotstories;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.View.OnDragListener;
import android.view.View.OnTouchListener;
import android.widget.AbsoluteLayout;
import android.widget.LinearLayout;
import com.example.robotstories.R;

/**
 * This class implements OnDragListener and it is the listener used to move a robot around the scenario, or to take it from "backstage" to
 * the scenario.
 * It is used in "main_interfaz", more concretelly in "Scenario", "MAINRobotList" and "MainRobotList2" 
 * 
 * @author Arturo Gil
 *
 */
@SuppressWarnings("deprecation")
public class DragRobotScenario implements OnDragListener {
	
	private Context c;
		  
	public DragRobotScenario (Context c) {
		this.c= c;
	}

	@Override
	public boolean onDrag(View v, DragEvent event) {
		AbsoluteLayout scenario= null;
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
	    	
	    	/*LinearLayout*/
	    	if(aux.equals("lin")){
	    		if(v.getClass().equals(LinearLayout.class))
	    			return false;
	    		LinearLayout parent = (LinearLayout)shadow.getParent();
	    		parent.removeView(shadow);
	    		parent.setBackgroundColor(c.getResources().getColor(R.color.noRobotColor));
	    		scenario= (AbsoluteLayout) v;
	    		scenario.addView(shadow);
	    	}
	    	/*Absolute*/
	    	else if(aux.equals("abs")){
	    		scenario= (AbsoluteLayout) shadow.getParent();
	    		if(v.getClass().equals(LinearLayout.class)){
		    		LinearLayout parent = (LinearLayout)v;
	    			if(shadow.getTag().equals(parent.getTag())){
			    		scenario.removeView(shadow);
			    		parent.addView(shadow);
			    		parent.setBackgroundColor(((Activity) c).getTitleColor());
				    	shadow.setOnTouchListener(new  OnTouchListener(){
				        	public boolean onTouch(View view, MotionEvent motionEvent) {
				                if (motionEvent.getActionMasked() == MotionEvent.ACTION_DOWN) {
				                    DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
				                    ClipData data = ClipData.newPlainText("parent", "lin");
				                    view.startDrag(data, shadowBuilder, view, 0);
				                    return true;
				                }
				                return false;
				        }});
			    		return true;
	    			}
	    			else
	    				return false;
	    		}
	    	}
	    	/*Nope*/
	    	else
	    		return false;
	    	
	    	int x= Math.round(event.getX());
	    	int y=  Math.round(event.getY());
	    	AbsoluteLayout.LayoutParams lp = new AbsoluteLayout.LayoutParams(AbsoluteLayout.LayoutParams.WRAP_CONTENT, 
	    			AbsoluteLayout.LayoutParams.WRAP_CONTENT, x, y);
	    	shadow.setLayoutParams(lp);
	    	shadow.setOnTouchListener(new  OnTouchListener(){
	        	public boolean onTouch(View view, MotionEvent motionEvent) {
	                if (motionEvent.getActionMasked() == MotionEvent.ACTION_DOWN) {
	                    DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
	                    ClipData data = ClipData.newPlainText("parent", "abs");
	                    view.startDrag(data, shadowBuilder, view, 0);
	                    return true;
	                }
	                else
	                	return false;
	        }});
	    	
	    	return true;
	    }
	    return false;
	  }

} 