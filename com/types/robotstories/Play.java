package com.types.robotstories;

import java.io.Serializable;
import java.util.ArrayList;

import com.example.robotstories.R;

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
	
	public Play(){
		this.robots= new ArrayList<Robot>();
		this.motors= new ArrayList<Motor>();
		this.sensors= new ArrayList<Sensor>();
		this.acciones= new ArrayList<Accion>();
		this.predefAcciones = new ArrayList<Accion>();
		this.setpredefAcciones();
		this.setpredefRobots();
	}

	private void setpredefAcciones() {
		Accion example = new Accion();
		example.name = "fixed";
		example.power=75;
		example.target= "Scenario";
		example.image="pausa";
		
		this.predefAcciones.add(example);
		
		Accion example2 = new Accion();
		example2.name = "dynamicPower";
		example2.commonAtts.add("power");
		example2.target= "Scenario";
		example2.image="gear";
		
		this.predefAcciones.add(example2);
		
		Accion example3 = new Accion();
		example3.name = "dynamicSecond";
		example3.commonAtts.add("third");
		example3.commonAtts.add("second");
		example3.image="newactor";
		
		
		this.predefAcciones.add(example3);
	}
	private void setpredefRobots() {
		Robot example = new Robot();
		example.name = "fixed";
		example.imageName="pausa";
		
		this.robots.add(example);
		
		Robot example2 = new Robot();
		example2.name = "dynamicPower";
		example2.imageName="gear";
		
		this.robots.add(example2);
		
		Robot example3 = new Robot();
		example3.name = "dynamicSecond";
		example3.imageName="newactor";
		
		this.robots.add(example3);
		
		Robot example4 = new Robot();
		example4.name = "fixe";
		example4.imageName="pausa";
		
		this.robots.add(example4);
		
		Robot example5 = new Robot();
		example5.name = "dynamicPowe";
		example5.imageName="gear";
		
		this.robots.add(example5);
		
		Robot example6 = new Robot();
		example6.name = "dynamicSecon";
		example6.imageName="newactor";
		
		
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
}
