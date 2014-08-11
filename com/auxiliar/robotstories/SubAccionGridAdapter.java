package com.auxiliar.robotstories;

import java.util.ArrayList;

import com.example.robotstories.R;
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
 * This is the adapter of the gridlayout in which are included all the subactions of one action. 
 * The items are marcos (LinearLayout), in which can be fitted many subactions (dependindg on the number of ports of the robots)
 * all the actions of a macro begin at the same time (but not necesarily end at the same time), 
 * each action is represented graphically by a linearLayout containing a imageview in which there is 
 * the image of the action, and a TextView in which there is the name of the action
 * 
 * @author Arturo Gil
 *
 */
public class SubAccionGridAdapter extends BaseAdapter{

	private Context mContext;
	private ArrayList<Accion[]> actions;
	private int size;
	private String tag;
	private ImageHandler images;
	private final int marcoSize;
	

     /**
     * @param c Context were the gridlayout is contained
     * @param size Size of the images of the items from the gridlayout
     * @param tag tag of the created layouts
     * @param images The imageHandler to use the auxiliar functions for working whit images
     * @param marcoSize size of the macro (it should be equal to the number of ports of the robot)
     */
    public SubAccionGridAdapter(Context c, int size, String tag, ImageHandler images, int marcoSize) {
    	this.images= images;
    	this.marcoSize=marcoSize;
        mContext = c;
        this.size=size;
        this.actions= new ArrayList<Accion[]>();
        this.tag=tag;
    }
    
    /**
     * @return all the macros whit all the actions
     */
    public ArrayList<Accion[]> getAcciones(){
    	return this.actions;
    }
    
    public void setActions(ArrayList<Accion[]> subacts){
    	this.actions=subacts;
    }
    /**
     * Creates a new macro, which contains an Array of Actions inside
     */
    public void AddMacro (){
    	this.actions.add(new Accion [this.marcoSize]);
    }
    /**
     * Add a new action in a macro, if the macro was empty adds a new macro
     * 
     * @param macro macro where the action is added
     * @param position position inside the macro
     * @param add action to be added
     */
    public void AddAction (int macro, int position, Accion add){
    	int i;
    	for(i=0; i<this.marcoSize; i++){
    		if(this.actions.get(macro)[i]!=null)
    			i=this.marcoSize*2;
    	}
    	if(i==this.marcoSize)
    		this.AddMacro();
    	this.actions.get(macro)[position]= add;
    }
 
    public int getCount() {
        return this.actions.size();
    }
 
    public Object getItem(int position) {
        // este método debería devolver el objeto que esta en esa posición del
        // adapter. 
    	return this.actions.get(position);
       // return this.r.getIdentifier(this.getImageNameAtPos (position), "LinearLayout", this.mContext.getPackageName());
    }
 
    public long getItemId(int position) {
        // este método debería devolver el id de fila del item que esta en esa
        // posición del adapter. 
        return position;
    }
 
    @SuppressLint("ResourceAsColor")
	public View getView(int position, View convertView, ViewGroup parent) {
    	LinearLayout objeto;
        if (convertView == null) {
        	objeto = new LinearLayout(this.mContext);
    		objeto.setOrientation(LinearLayout.VERTICAL);

        	for(int i=0; i<this.marcoSize; i++){
            	TextView text;
                ImageView imageView;
        		
        		LinearLayout sub = new LinearLayout(this.mContext);
        		sub.setOrientation(LinearLayout.VERTICAL);
        		sub.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        		sub.setOnDragListener(new DragActionToSubaction(i, this));
        		sub.setBackground(this.mContext.getResources().getDrawable(R.drawable.cells_sub_acts));
        		
            	/*Create image*/
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams(this.size, this.size));// ancho y alto
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(8, 8, 8, 8);
                
                /*Create text*/
                text = new TextView (this.mContext);
                text.setLayoutParams(new LayoutParams( LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
                text.setGravity(Gravity.CENTER);
                
                /*Add to layout*/
                sub.addView(imageView);
                sub.addView(text);
        		objeto.addView(sub);
                if(this.actions.get(position)[i]!=null){
	                imageView.setImageResource( this.images.getIdImageByName (this.actions.get(position)[i].image));
	                text.setText(this.actions.get(position)[i].name);
                }
        	}
            objeto.setTag(tag);
            objeto.setId(position);
            objeto.setPadding(8, 8, 8, 8);
        } else {
            objeto = (LinearLayout) convertView;

        	for(int i=0; i<this.marcoSize; i++){
            	TextView text;
                ImageView imageView;

        		LinearLayout sub = (LinearLayout) objeto.getChildAt(i);
                imageView= (ImageView) sub.getChildAt(0);
                text= (TextView) sub.getChildAt(1);

                if(this.actions.get(position)[i]!=null){
	                imageView.setImageResource( this.images.getIdImageByName (this.actions.get(position)[i].image));
	                text.setText(this.actions.get(position)[i].name);
                }
        	}
        }
        
        return objeto;
    }
}
