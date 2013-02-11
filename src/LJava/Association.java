package LJava;

import static LJava.LJ.*;
import java.util.Arrays;

/**
 * @author Tzali Maimon
 * This class represents the base object on which facts, functions, rules are built upon.<p>
 * Association is what later goes in the "world" of facts/rules as the user associate them. 
 */
public class Association {

	protected final String name;
	protected final Object[] args;	
	
	/** Name and Parameters for an Association
	 * @param n
	 * @param params
	 */
	public Association(String n, Object... params) {
		name = (n==null || n=="") ? "#LJRelation" : n;
		args = (params==null) ? new Object[0] : params;
	}
		
	
	/**
	 * @return the name of the association
	 */
	public String name(){
		return name;
	}
	
	
	/**
	 * @return the number of arguments in the association
	 */
	public int argsLength(){
		return args.length;
	}	
	

	/**
	 * @return an array holding the arguments of the association. This new array is not the array held by the object so you can change it.<p>
	 * NOTE: the objects within the returned array are the same objects held by the inner array so changing them will effect the Association even thought it itself is immutable. 
	 */
	public Object[] args(){ 
		return Arrays.copyOf(args,args.length); 
	}
	
	
	/**
	 * Identifies if this association is of a type Group.
	 * @return true if the object is a group.
	 */
	public boolean isGroup() {
		return false;
	}
	
	
	/**
	 * Identifies if this association is of a type Formula.
	 * @return true if the object is a formula.
	 */
	public boolean isFormula() {
		return false;
	}


	/**
	 * Identifies if this association is activated as a lazy group in a Lazy object.
	 * @return true if this association is a group that is activated as lazy group.  
	 */
	public boolean isLazy() {
		return false;
	}
	
	
	public String toString(){
		if (this==_) return "_";
		StringBuilder s = new StringBuilder(name+"(");
		if (args.length>0) {
			for (int i=0; i<args.length; i++) s.append(string(args[i])+",");
			s.deleteCharAt(s.length()-1);		}
		s.append(')');
		return s.toString();
	}
	

	protected boolean associationNameCompare(Association r) {			
		if ((name.charAt(0)!='#') || (r.name.charAt(0)!='#'))
			if (!name.equals(r.name)) return false;
		return true;
	}
	

	public boolean equals(Object o) {
		if (this==_) return true;
		if (this==none) return false;
		if (o instanceof Association) return this.equalsAssociation((Association) o);		
		return o.equals(this);	
	}
		
	
	protected boolean satisfy(Object[] rArgs, LJMap varValues) {
		return false;
	}
	
	
//Compares between 2 Associations.
	private boolean equalsAssociation(Association r){
		if (!this.getClass().equals(r.getClass())) return false;
		if (!associationNameCompare(r)) return false;
		for (int i=0; i<args.length; i++)
			if (this.args[i]!=r.args[i]) return false;
		return true;
	}
}
