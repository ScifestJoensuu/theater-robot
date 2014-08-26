package com.auxiliar.robotstories;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

/**
 * This class contains some auxiliar functions used in other classes to deal whit images (resize, get by Name, etc.)
 * 
 * @author Arturo Gil
 *
 */
public class ImageHandler {
	
	Context c;
	Resources r;
	
	/**
	 * Creates a new ImageHandler (auxiliar functions to work whit images)
	 * 
	 * @param c context in which the imageHandler is going to be used
	 */
	public ImageHandler(Context c){
		this.c=c;
		this.r= c.getResources();
	}
	
    /**
     * Find a image by its name
     * 
     * @param name the name of the image
     * @return the id of the image
     */
    public int getIdImageByName (String name){
    	int id=r.getIdentifier(name, "drawable", this.c.getPackageName());
    	if(id==0)
    		id=r.getIdentifier("default_image", "drawable", this.c.getPackageName());
		return id;
    }
    
	/**
	 * @param name of the image to be resized
	 * @param size the size for the image
	 * @return the image whit it new size
	 */
	public BitmapDrawable resizeImage (String name, int size){
		int imagecode=getIdImageByName (name);
		final Drawable imageaux =r.getDrawable(imagecode);
		Bitmap bitmap = ((BitmapDrawable) imageaux).getBitmap();
		BitmapDrawable imagechanged = new BitmapDrawable(r, Bitmap.createScaledBitmap(bitmap, size, size, true));
		return imagechanged;
	}

	/**
	 * Creates a new imageView which have as background a copy of the background of an ImageView in black and white
	 * 
	 * @param imageV The original ImageView
	 * @return a new imageView whit all the changes
	 */
	public ImageView blackAndWhiteImageView(ImageView imageV){
		Drawable image= imageV.getBackground().getConstantState().newDrawable().mutate();
	    ColorMatrix cm = new ColorMatrix();
	    cm.setSaturation(0);
	    ColorMatrixColorFilter filter = new ColorMatrixColorFilter(cm);
		image.setColorFilter(filter);
		ImageView blackAndWhite= new ImageView(this.c);
		blackAndWhite.setBackground(image);
		
		return blackAndWhite;
	}
	
	/**
	 * Clean all the filters of the background of a imageView
	 * 
	 * @param imageV imageView to be changed
	 */
	public void clearFilterImageView (ImageView imageV){
		Drawable image= imageV.getBackground();
		image.clearColorFilter();
	}
}
