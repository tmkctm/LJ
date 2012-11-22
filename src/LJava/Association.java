package LJava;

import static LJava.LJ.*;
import static LJava.Utils.*;

import java.util.Arrays;
import java.util.HashMap;

public class Association {

	private final String name;
	private final Object[] args;	
	
	public Association(String n, Object... params) {
		name = (n==null || n=="") ? "#Relation" : n;
		args = (params==null) ? new Object[0] : params;
	}
		
	
	public String name(){
		return name;
	}
	
	
	public int argsLength(){
		return args.length;
	}	
	

	public Object[] args(){ 
		return Arrays.copyOf(args,args.length); 
	}
	
	
	public boolean isGroup() {
		return false;
	}
	
	
	public boolean isFormula() {
		return false;
	}
	
	
	public String toString(){
		if (this==_) return "_";
		StringBuilder s = new StringBuilder(name+"(");
		if (args.length>0) {
			for (int i=0; i<args.length-1; i++)	s.append(args[i]+",");
			s.append(args[args.length-1].toString());
		}
		s.append(")");
		return s.toString();
	}
	

	public Object[] replaceVariables(){
		HashMap<Variable,Variable> varsMap=new HashMap<Variable,Variable>();
		Object[] arguments=new Object[args.length];		
		for (int i=0; i<args.length; i++)
			if (var(args[i]))	{
				arguments[i]=varsMap.get(args[i]);
				if (arguments[i]==null) {
					arguments[i]=var();
					varsMap.put((Variable) args[i], (Variable) arguments[i]);
				}					
			}
			else arguments[i]=args[i];
		return arguments;
	}


	protected boolean associationNameCompare(Association r) {			
		if ((name.charAt(0)!='#') || (r.name.charAt(0)!='#'))
			if (!name.equals(r.name)) return false;
		return true;
	}
	

	public boolean equals(Object o) {
		if (this==_) return true;
		if (this==none) return false;
		if (o instanceof Association) 			
			return this.equalsAssociation((Association) o);		
		return o.equals(this);	
	}
		
	
	protected boolean satisfy(Object[] rArgs, VariableMap varValues) {
		if (this==LJTrue) return true;
		return false;
	}
	
	
//Compares between 2 relations.
	private boolean equalsAssociation(Association r){
		if (!associationNameCompare(r)) return false;
		if (this.isGroup() ^ r.isGroup()) return false;
		if (this.isFormula() ^ r.isFormula()) return false;
		for (int i=0; i<args.length; i++)
			if (!same(this.args[i],r.args[i])) return false;
		return true;
	}
		
}
