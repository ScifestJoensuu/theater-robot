package com.types.robotstories;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author Arturo Gil
 *
 */
public class Accion implements Serializable{

	private static final long serialVersionUID = 1L;
	
	public String name;
	public String image;
	public ArrayList<Accion[]> subactions;
	/**List of the names of the common attributes*/
	public ArrayList<String> commonAtts;
	/**Motors involved in the action*/
	public ArrayList<Motor> motors;
	
	public ArrayList<String> code;
	
	/*Properties*/
	public String target;
	public int power;
	public int secondProperty;
	public String typeSecondProperty;
	
	public boolean basicAccion=false;
	
	public Accion(){
		this.target=null;
		this.image=null;
		this.typeSecondProperty=null;
		this.motors= new ArrayList<Motor>();
		this.subactions= new ArrayList<Accion[]>();
		this.commonAtts= new ArrayList<String>();
		this.code= new ArrayList<String>();
	}
	
	/**
	 * <p>Check if the motor is already on the array, if not and there is enough space, adds it to the ArrayList of the motors</p>
	 * If it returns false, the list of the motors could be corrupted, probably it should be clear
	 * @param nuevos the motors to be added
	 * @return <p>true if the motors are added correctly</p>
	 * 			false if there wasn't space to add all the motors
	 */
	public boolean addMotors(ArrayList<Motor> nuevos){
		for(Motor nuevo: nuevos){
			int i;
			for(i=0; i<this.motors.size(); i++){
				Motor m= this.motors.get(i);
				if(m.name.equals(nuevo.name)==true){
					i=this.motors.size()*2 + 1;
				}
			}
			if(i==this.motors.size()){
				if(this.motors.size()==Robot.MOTOR_PORTS)
					return false;
				this.motors.add(nuevo);
			}
		}
		return true;
	}
	/*"power", values of spinner second, "target"*/
	/**
	 *  Adds to the list the name of a common attribute
	 * 
	 * @param att name of the common attribute to add
	 */
	public void addCommonAtt(String att){
		this.commonAtts.add(att);
	}
	/**
	 * Deletes from the list the name of a common attribute
	 * 
	 * @param att the name of the common attribute to remove
	 */
	public void removeCommonAtt(String att){
		this.commonAtts.remove(att);
	}
	
	/**
	 * Reads all the common atts from the subactions to know which are all the common atts of the action
	 */
	public void getSubActsCommonAtt(){
		for(Accion[] accs: this.subactions){
			for(int i=0; i< accs.length; i++){
				if(accs[i]!=null){
					if(accs[i].commonAtts.isEmpty()==false){
						for(String s: accs[i].commonAtts){
							if(!this.commonAtts.contains(s))
								this.addCommonAtt(s);
						}
					}
				}
			}
		}
	}

	public Accion clone(){
		Accion nueva= new Accion();
		nueva.name= this.name;
		nueva.image= this.image;
		nueva.subactions= (ArrayList<Accion[]>) this.subactions.clone();
		nueva.commonAtts= (ArrayList<String>) this.commonAtts.clone();
		nueva.target=this.target;
		nueva.power= this.power;
		nueva.secondProperty= this.secondProperty;
		nueva.typeSecondProperty= this.typeSecondProperty;
		return nueva;
	}
	/**
	 * The actions which have common values have these values initiated to -1, so at the time of adding an action to
	 * a robot we have to set the value of these atts, before adding the action to the robot. We create a new action,
	 * so the first step is to clone it.
	 * 
	 * The first value of "names" correspond to the first value of "values", the second whit the second, and so on
	 * 
	 * @param power value for the power of the subactions which have power as common value 
	 * @param names arraylist of the names of the second properties to set 
	 * @param values values of the second properties
	 * @param target value of the target (NOT IMPLEMENTED)
	 * 
	 * @return the action ready to insert into the robot
	 */
	public Accion transformForRobot(int power, ArrayList<String> names, ArrayList<Integer> values, String target){
		Accion nueva= this.clone();
		/*If power is != -1 (>=0) then is because we have to modify it, let's where*/
		if(power>=0){
			for(Accion[] accs: nueva.subactions){
				for(int i=0; i< accs.length; i++){
					if(accs[i]!=null){
						if(accs[i].power==-1)
							accs[i].power=power;
						/*If we find any block which needs recursivity, we apply it, we clear it to ensure not repeating
						 * the recursivity again*/
						if(accs[i].commonAtts.isEmpty()==false){
							accs[i].transformForRobot(power, names, values, target);
							accs[i].commonAtts.clear();
						}
					}
				}
			}
		}
		if(names.isEmpty()==false){
			for(Accion[] accs: nueva.subactions){
				for(int i=0; i< accs.length; i++){
					int j=0;
					if(accs[i]!=null){
						for(String s: names){
							if(accs[i].secondProperty<0 && accs[i].typeSecondProperty.equals(s)){
								accs[i].secondProperty= values.get(j);
							}
							j++;
						}
						/*If we find any block which needs recursivity, we apply it, we clear it to ensure not repeating
						 * the recursivity again*/
						if(accs[i].commonAtts.isEmpty()==false){
							accs[i].transformForRobot(power, names, values, target);
							accs[i].commonAtts.clear();
						}
					}
				}		
			}
		}
		/*TARGET MISSING*/
		/*We clean it, because we have assign all missing values*/
		nueva.commonAtts.clear();
		return this;
	}

	/**
	 * Reads all the targets from the subactions to know which is the target of the action. Checks if there are different targets
	 * @return <p>true if all the targets are the same</p>
	 * 			<p>false if there are different target for the subactions</p>
	 */
	public boolean setCommonTarget() {
		for(Accion[] accs: this.subactions){
			for(int i=0; i< accs.length; i++){
				if(accs[i]!=null){
					if(accs[i].target!=null){
						if(this.target==null)
							this.target=accs[i].target;
						else if(this.target.equals(accs[i].target)==false)
							return false;
					}
				}
			}
		}
		return true;
	}

	/**
	 * <p>Recursive method. Sees if the current action is a basic action, if not, it means it is formed by other actions, so it calls 
	 * this method again.</p>
	 * <p> If the action is a basic action, reads the information from the activity (power and condition), and adds the information to the
	 * action</p>
	 * @return
	 */
	public String transformActionIntoCode(){
		String code="";
		int toMiliSecondS;
		//code(whit:)value:value\n
		if(this.basicAccion==true){
			code=this.code.get(0);

			if(this.typeSecondProperty.equals("SECONDS")){
				if(this.secondProperty<0){
					return null;
				}
				toMiliSecondS= this.secondProperty*1000;
				code=code.concat(Integer.toString(toMiliSecondS)+":");
			}
			else{
				return null;
			}

			if(this.power<0 || this.power>100){
				return null;
			}
			
			code= code.concat(Integer.toString(this.power)+"\n");

		}
		else{
			for(Accion macro[] : this.subactions) {
				for(Accion a: macro){
					if(a!=null){
						String aux=a.transformActionIntoCode();
						if(aux!=null)
							code=code.concat(aux);
					}
				}
			}	
		}
		return code;
	}
}
