package com.auxiliar.robotstories;

import java.util.ArrayList;

import com.types.robotstories.Accion;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

/**
 * This is the adapter of the gridlayout whit all the precreated actions which can be used at the time of 
 * creating a new action. The items are actions, each action is represented graphically by a linearLayout
 * containing a imageview in which there is the image of the action, and a TextView in which there is the name
 * of the action
 * 
 * @author Arturo Gil
 *
 */
public class AccionGridAdapter extends BaseAdapter{

	private Context mContext;
	private ArrayList<Accion> actions;
	private int size;
	private String tag;
	private ImageHandler images;
	
    /**
     * @param c Context were the gridlayout is contained
     * @param size Size of the images of the items from the gridlayout
     * @param tag tag of the created layouts
     * @param images The imageHandler to use the auxiliar functions for working whit images
     */
    public AccionGridAdapter(Context c, int size, String tag, ImageHandler images) {
    	this.images= images;
        mContext = c;
        this.size=size;
        this.actions= new ArrayList<Accion>();
        this.tag=tag;
    }
    
    /**
     * @param a adding one action to the gridlayout
     */
    public void AddAction (Accion a){
    	this.actions.add(a);
    }

    /**
     * Ask the image name of the action in pos
     * 
     * @param pos asked position
     * @return
     */
    public String getImageNameAtPos (int pos){
    	return this.actions.get(pos).image;
    }
 
	/**
	 * Return number of items contained in the gridlayout
	 */
    public int getCount() {
        return this.actions.size();
    }
 
    /**
     * Return the item in any postion of the adapter
     * @param position asked position
     */
    public Object getItem(int position) {
    	return this.actions.get(position);
    }
 
    /**
     * Get the id of the layout which represent the item
     */
    public long getItemId(int position) { 
        return position;
    }
 
    @SuppressLint("ResourceAsColor")
	public View getView(int position, View convertView, ViewGroup parent) {
    	LinearLayout objeto;
    	TextView text;
        ImageView imageView;
        if (convertView == null) {
        	/*Create image*/
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(this.size, this.size));// ancho y alto
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
            
            /*Create text*/
            text = new TextView (this.mContext);
            text.setLayoutParams(new LayoutParams( LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
            text.setGravity(Gravity.CENTER);
            
            /*Add and create Layout*/
            objeto= new LinearLayout(this.mContext);
            objeto.setOrientation(LinearLayout.VERTICAL);
            objeto.addView(imageView);
            objeto.addView(text);
            objeto.setTag(tag);
            objeto.setId(position);
        } else {
            objeto = (LinearLayout) convertView;
            imageView= (ImageView) objeto.getChildAt(0);
            text= (TextView) objeto.getChildAt(1);
        }
        imageView.setImageResource( this.images.getIdImageByName (this.actions.get(position).image));
        text.setText(this.actions.get(position).name);
        
        return objeto;
    }
}
