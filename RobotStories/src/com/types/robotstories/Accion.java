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
	public ArrayList<String> commonAtts;
	
	/*Properties*/
	public String target;
	public int power;
	public int secondProperty;
	public String typeSecondProperty;
	
	public Accion(){
		this.target=null;
		this.image=null;
		this.typeSecondProperty=null;
		this.subactions= new ArrayList<Accion[]>();
		this.commonAtts= new ArrayList<String>();
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
}
