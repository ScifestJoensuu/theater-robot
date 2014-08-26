package com.types.robotstories;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author Arturo Gil
 *
 */
public class Play implements Serializable {

	private static final long serialVersionUID = 1L;
	public String name, desc;
	public int HSize, VSize;
	
	public ArrayList<Motor> motors;
	public ArrayList<Sensor> sensors;
	public ArrayList<Robot> robots;
	public ArrayList<Accion> acciones;
	public ArrayList<Accion> predefAcciones;
	
	/** 
	 * Calls setpredefAcciones and setPredefRobots, auxiliar methods for testing
	 */
	public Play(){
		this.robots= new ArrayList<Robot>();
		this.motors= new ArrayList<Motor>();
		this.sensors= new ArrayList<Sensor>();
		this.acciones= new ArrayList<Accion>();
		this.predefAcciones = new ArrayList<Accion>();
		this.setpredefAcciones();
		this.setpredefRobots();
	}

	private void setpredefMotors(){
		Motor leg1= new Motor("legRight", "DC");
		Motor leg2= new Motor("legLeft", "DC");
		Motor midleg = new Motor("auxiliar", "AC");
		this.motors.add(leg1);
		this.motors.add(leg2);
		this.motors.add(midleg);
	}
	/**
	 * Calls setpredefMotors, auxiliar method for testing
	 */
	private void setpredefAcciones() {
		this.setpredefMotors();
		
		/*Both legs (full dc)*/
		Accion move= new Accion();
		move.name = "Move foward";
		move.power=75;
		move.secondProperty= 2;
		move.typeSecondProperty= "SECONDS";
		//move.target= "Scenario";
		move.image="play";
		move.motors.add(this.motors.get(0));
		move.motors.add(this.motors.get(1));
		move.basicAccion=true;
		move.code.add("drive_fwd:");
		
		this.predefAcciones.add(move);
		
		Accion moveBack= new Accion();
		moveBack.name = "Move backwards";
		moveBack.power=75;
		moveBack.basicAccion=true;
		//move.target= "Scenario";
		moveBack.image="play";
		moveBack.motors.add(this.motors.get(0));
		moveBack.motors.add(this.motors.get(1));
		
		this.predefAcciones.add(moveBack);
		
		Accion example = new Accion();
		example.name = "Stop";
		example.power=75;
		example.basicAccion=true;
		example.motors.add(this.motors.get(0));
		example.motors.add(this.motors.get(1));
		example.image="pausa";
		example.code.add("L");
		
		this.predefAcciones.add(example);
		
		Accion example2 = new Accion();
		example2.name = "Turn left";
		example2.basicAccion=true;
		example2.power=75;
		example2.motors.add(this.motors.get(0));
		example2.motors.add(this.motors.get(1));
		example2.image="gear";
		
		this.predefAcciones.add(example2);
		
		Accion turnRight = new Accion();
		turnRight.name = "Turn right";
		turnRight.basicAccion=true;
		turnRight.power=75;
		turnRight.motors.add(this.motors.get(0));
		turnRight.motors.add(this.motors.get(1));
		turnRight.image="gear";
		
		this.predefAcciones.add(turnRight);
		
		/*auxiliar(ac)*/
		Accion example3 = new Accion();
		example3.name = "Interact";
		example3.basicAccion=true;
		example3.commonAtts.add("third");
		example3.commonAtts.add("second");
		example3.image="newactor";
		example3.motors.add(this.motors.get(2));
		
		
		this.predefAcciones.add(example3);
	}
	private void setpredefRobots() {
		Robot example = new Robot();
		example.name = "Father";
		example.HSize= 2;
		example.VSize= 2; 
		example.imageName="father";
		
		this.robots.add(example);
		
		Robot example2 = new Robot();
		example2.name = "Mother";
		example2.imageName="mother";
		
		this.robots.add(example2);
		
		Robot example3 = new Robot();
		example3.name = "Son";
		example3.imageName="son";
		
		this.robots.add(example3);
		
		Robot example4 = new Robot();
		example4.name = "Daughter";
		example4.imageName="daughter";
		
		this.robots.add(example4);
		
		Robot example5 = new Robot();
		example5.name = "GrandPa";
		example5.imageName="granpa";
		
		this.robots.add(example5);
		
		Robot example6 = new Robot();
		example6.name = "GranMa";
		example6.imageName="grandma";
		
		
		this.robots.add(example6);
	}
	/*robots*/
	public boolean AddRobot(Robot nuevo){
		if(this.robots==null){
			this.robots= new ArrayList<Robot>();
		}
		if (this.findRobotByName(nuevo.name)!=null)
			return false;
		
		this.robots.add(nuevo);
		return true;
	}
	
	public Robot findRobotByName(String name){
		if (this.robots==null)
			return null;
		
		for( Robot a: this.robots)
			if(a.name.equals(name))
				return a;
		return null;
	}

	public boolean RemoveRobot(Robot old){
		this.robots.remove(old);
		return true;
	}
	/*MOTORS*/

	/**if this is the first motor, create the list, check if this motor was already in the list
	 * @param nuevo motor to add to the list
	 * @return true if alright
	 * 			flase if problem (motor already in list)
	 */
	public boolean AddMotor(Motor nuevo){
		if(this.motors==null){
			this.motors= new ArrayList<Motor>();
		}
		if (this.findMotorByName(nuevo.name)!=null)
			return false;
		
		this.motors.add(nuevo);
		return true;
	}
	public Motor findMotorByName(String name){
		if (this.motors==null)
			return null;
		
		for( Motor m: this.motors)
			if(m.name.equals(name))
				return m;
		return null;
	}
	
	public boolean RemoveMotor(Motor old){
		this.motors.remove(old);
		return true;
	}

	/*SENSORS*/
	/**if this is the first sensor, create the list, check if this sensor was already in the list
	 * @param nuevo sensor to add to the list
	 * @return true if alright
	 * 			flase if problem (sensor already in list)
	 */
	public boolean AddSensor (Sensor nuevo){
		if(this.sensors==null){
			this.sensors= new ArrayList<Sensor>();
		}
		for( Sensor s: this.sensors)
			if(s.name.equals(nuevo.name))
				return false;
		
		this.sensors.add(nuevo);
		return true;
	}
	public Sensor findSensorByName(String name){
		if(this.sensors==null)
			return null;
		for( Sensor s: this.sensors)
			if(s.name.equals(name))
				return s;
		return null;
	}
	public boolean RemoveSensor(Sensor old){
		this.sensors.remove(old);
		return true;
	}
	/*ACTIONS*/
	/**if this is the first action, create the list, check if this action was already in the list
	 * @param nuevo action to add to the list
	 * @return true if alright
	 * 			flase if problem (action already in list)
	 */
	public boolean AddAction (Accion nuevo){
		if(this.acciones==null){
			this.acciones= new ArrayList<Accion>();
		}
		for( Accion a: this.predefAcciones)
			if(a.name.equals(name))
				return false;
		for( Accion a: this.acciones)
			if(a.name.equals(nuevo.name))
				return false;
		
		this.acciones.add(nuevo);
		return true;
	}
	public Accion findActionByName(String name){
		if(this.acciones==null)
			return null;
		for( Accion a: this.acciones)
			if(a.name.equals(name))
				return a;
		if(this.predefAcciones==null)
			return null;
		for( Accion a: this.predefAcciones)
			if(a.name.equals(name))
				return a;
		return null;
	}
	public boolean RemoveAction(Accion old){
		this.acciones.remove(old);
		return true;
	}

	/**
	 * @return an array whit all the names of the actions which have a target
	 */
	public String[] getActionNamesWhitTarget() {
		ArrayList<String> actions= new ArrayList<String>();
		for(Accion a: this.acciones){
			if(a.target!=null){
				actions.add(a.name);
			}
		}
		for(Accion a: this.predefAcciones){
			if(a.target!=null){
				actions.add(a.name);
			}
		}
		if(actions.isEmpty())
			return null;
		String[] values= actions.toArray(new String[actions.size()]);
		return values;
	}
	/**
	 * @return an array whit all the image names of the actions which have a target
	 */
	public String[] getActionImagesWhitTarget() {
		ArrayList<String> actions= new ArrayList<String>();
		for(Accion a: this.acciones){
			if(a.target!=null){
				actions.add(a.image);
			}
		}
		for(Accion a: this.predefAcciones){
			if(a.target!=null){
				actions.add(a.image);
			}
		}
		if(actions.isEmpty())
			return null;
		String[] values= actions.toArray(new String[actions.size()]);
		return values;
	}
	
	/**
	 * Receives an ArrayList whit actions and returns another arrayList whit all the actions of the first ArrayList which are carried out by
	 * a specified motor or/and a specified type of motor
	 * 
	 * @param name of the motor. Null for all
	 * @param type of the motor. Null for all
	 * @param toBeSort array to be filter
	 * @return the array whit the actions filter. Null if the type of the motor received and the type of the motor obtained from the action 
	 * using the name of the motor, are different
	 */
	public ArrayList<Accion> sortByMotor(String name, String type, ArrayList<Accion> toBeSort){
		ArrayList<Accion> newList = new ArrayList<Accion>();
		int i;
		if(type==null && name==null)
			return toBeSort;
		
		if(type==null){
			for(Accion a: toBeSort){
				for (i=0; i< a.motors.size(); i++){
					Motor m= a.motors.get(i);
					if(m.name.equals(name))
						i= a.motors.size()+100;
				}
				if(i>a.motors.size())
					newList.add(a);
			}
		}
		else if(name==null){
			for(Accion a: toBeSort){
				for (i=0; i< a.motors.size(); i++){
					Motor m= a.motors.get(i);
					if(m.type.equals(type)){
						i=a.motors.size()+100;
					}
				}
				if(i>a.motors.size()){
					newList.add(a);
				}
			}
		}
		else {
			for(Accion a: toBeSort){
				for (i=0; i< a.motors.size(); i++){
					Motor m= a.motors.get(i);
					if(m.name.equals(name)){
						if(m.type.equals(type))
							i=a.motors.size()+100;
						else
							return null;
					}
				}
				if(i>a.motors.size())
					newList.add(a);
			}
		}
		return newList;
	}
	
	/**
	 * Returns an array whit the names of the motors filtered by type. In the first position there will be always the value "ALL MOTORS"
	 * 
	 * @param type of the motor. Null for all
	 * @return the array of the names
	 */
	public String [] getMotorsByType(String type){
		ArrayList<String> names= new ArrayList<String>();
		names.add("ALL MOTORS");
		for(Motor m: this.motors){
			if(type==null)
				names.add(m.name);
			else if(m.type.equals(type))
				names.add(m.name);	
		}
		String[] values= names.toArray(new String[names.size()]);
		return values;
	}
}
