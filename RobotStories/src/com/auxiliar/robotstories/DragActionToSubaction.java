package com.auxiliar.robotstories;

import android.content.Intent;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.View;
import android.view.View.OnDragListener;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.types.robotstories.Accion;

/**
 * This class implements OnDragListener and it is the listener used to transform an action into a subaction of another action.
 * It is used in the "create_action" more concretely in the gridview of "subactions"
 * 
 * @author Arturo Gil
 *
 */
public class DragActionToSubaction implements OnDragListener {
	private int pos_inside;
	SubAccionGridAdapter Adapter;
		  
	/**
	 * @param position the position inside the macro of the listener
	 * @param Adapter The adapter in which the new subaction is stored
	 */
	public DragActionToSubaction(int position, SubAccionGridAdapter Adapter) {
		this.pos_inside= position;
		this.Adapter= Adapter;
	}

	public boolean onDrag(View v, DragEvent event) {
		switch (event.getAction()) {
		case DragEvent.ACTION_DRAG_STARTED:
			return true;
		case DragEvent.ACTION_DRAG_ENTERED:
			return true;
	    case DragEvent.ACTION_DROP:
	    	LinearLayout marco= (LinearLayout) v.getParent();
	    	int id_marco = marco.getId();
	    	
	    	if(event.getClipData().getItemCount()==0)
	    		return false;
	    	Intent aux= event.getClipData().getItemAt(0).getIntent();
	    	
	    	if(aux==null)
	    		return false;
	    	Bundle extras = aux.getExtras();
	    	if(extras==null)
	    		return false;
	    	Accion add= (Accion) extras.getSerializable("Accion");
	    	
	    	this.Adapter.AddAction(id_marco, this.pos_inside, add); 
	    	GridView grid = (GridView) marco.getParent().getParent();
	    	grid.setAdapter(this.Adapter);
	      return true;
	    }
	    return false;
	  }

} 