package com.auxiliar.robotstories;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

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
		return r.getIdentifier(name, "drawable", this.c.getPackageName());
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

}
