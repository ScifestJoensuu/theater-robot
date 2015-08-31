package com.types.robotstories;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for the robots/actors of the play.
 * <p>The motors/sensor of the robot are organized in an Array, it is initialized to null and its size is
 * MOTOR/SENSOR_PORTS, the position in the array represents the port in which they are -1. 
 * If they are in the pos 0, they are using port 1, etc.</p>
 * 
 * @author Arturo Gil
 *
 */
public class Robot implements Serializable {

	private static final long serialVersionUID = 1L;
    /**
     * The value of this constant is {@value}. It is the number of motor ports of the robot
     */
	public static final int MOTOR_PORTS= 4;
    /**
     * The value of this constant is {@value}. It is the number of sensor ports of the robot
     */
	public static final int SENSOR_PORTS= 4;
	
	public String name;
	public String imageName;
	public int HSize, VSize;
	public ArrayList<Accion> actions;
	/**The action performed when you move the robot through the scenario*/
	public Accion moveAction;
	public Motor motors[];
	public Sensor sensors[];
	
	public String device;
	
	public Robot (){
		this.motors = new Motor [Robot.MOTOR_PORTS];
		this.sensors = new Sensor [Robot.SENSOR_PORTS];
		this.actions = new ArrayList<Accion>();
	}

	public void addAction (Accion a, int position){
		this.actions.add(position, a);
	}
	/**
	 * Remove an action in an specified position, if this position doesn't exist, it doesn't do nothing
	 * 
	 * @param position the position where the action will be deleted
	 */
	public void removeAccionAt (int position){
		if(position >= this.actions.size())
			return;
		this.actions.remove(position);
	}
	/*FALTA POR COMENTAR*/
	public boolean changePosAction(Accion a, int pos){
		if(pos<0 || pos>this.actions.size() || this.actions.contains(a)==false)
			return false;
		this.actions.remove(a);
		if(pos>=this.actions.size()){
			this.actions.add(a);
			return true;
		}
		List<Accion> sublista = this.actions.subList(pos, this.actions.size());
		ArrayList<Accion> aux= new ArrayList<Accion>(sublista);
		sublista.clear();
		this.actions.add(a);
		this.actions.addAll(aux);
		return true;		
	}
	/**Gives an array whit the number of motor ports (for the spinners mainly). The first value [0]= -
	 *
	 * @return the array ["-",1,2,3,...,MOTOR_PORTS]
	 */
	public String[] arrayMotorPorts(){
		String[] ports= new String[Robot.MOTOR_PORTS+1];
		ports[0]="-";
		for (int i=1; i<=Robot.MOTOR_PORTS; i++){
			ports[i]=Integer.toString(i);
		}
		return ports;
	}
	
	/**Gives an array whit the number of sensor ports (for the spinners mainly). The first value [0]= -
	 *
	 * @return the array [-,1,2,3,...,SENSOR_PORTS]
	 */
	public String[] arraySensorPorts(){
		String[] ports= new String[Robot.SENSOR_PORTS+1];
		ports[0]="-";
		for (int i=1; i<=Robot.SENSOR_PORTS; i++){
			ports[i]=Integer.toString(i);
		}
		return ports;
	}

	/**
	 * Set the motor in its, port. TAKE IN COUNT THE POS OF THE PORT IS EQUAL TO THE PORT-1
	 * 
	 * @param m motor to add
	 * @param port where the motor is
	 * @return
	 */
	public boolean setMotorInPort(Motor m, int port) {
		if(this.motors[port-1]!=null)
			return false;
		this.motors[port-1]=m;
		m.addRobot(this.name);
		return true;
	}

	public void removeMotors() {
		for (int i=0; i<Robot.MOTOR_PORTS; i++){
			if(this.motors[i]!=null){
				this.motors[i].removeRobot(this.name);
				this.motors[i]=null;
			}
		}
	}

	/**
	 * @param motor the motor we are going to check
	 * @return the number of the port in which this motor is inside the robot (value inside the array+1). Returns -1 if it is not in the robot
	 */
	public int PortMotor(Motor motor){
		for(int i=0; i<this.motors.length; i++){
			Motor m= this.motors[i];
			if(m!=null){
				if(m.name.equals(motor.name))
					return i+1;
			}
		}
		return -1;
	}
	
	public boolean setSensorInPort(Sensor s, int port) {
		if(this.sensors[port-1]!=null)
			return false;
		this.sensors[port-1]=s;
		s.addRobot(this.name);
		return true;
	}

	public void removeSensors() {
		for (int i=0; i<Robot.SENSOR_PORTS; i++){
			if(this.sensors[i]!=null){
				this.sensors[i].removeRobot(this.name);
				this.sensors[i]=null;
			}
		}	
	}
	
	/**
	 * @param sensor the sensor we are going to check
	 * @return the number of the port in which this sensor is inside the robot (value inside the array+1). Returns -1 if it is not in the robot
	 */
	public int PortSensor(Sensor sensor){
		for(int i=0; i<this.sensors.length; i++){
			Sensor s= this.sensors[i];
			if(s!=null){
				if(s.name.equals(sensor.name))
					return i;
			}
		}
		return -1;
	}


	/*NOT IMPLEMENTED*/
	public void executePrincipalAction(int x, int y) {
		
	}
}