package com.types.robotstories;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author Arturo Gil
 *
 */
public class Sensor implements Serializable{

	private static final long serialVersionUID = 1L;
	
	public String name;
	public String type;
	
	/**names of the robots sharing this motor*/
	public ArrayList<String> robots;
	
	public Sensor (String name, String type){
		this.name=name;
		this.type=type;
		this.robots= new ArrayList<String>();
	}
	
	public void addRobot(String name){
		this.robots.add(name);
	}
	public void removeRobot(String name){
		this.robots.remove(name);
	}
	
}

