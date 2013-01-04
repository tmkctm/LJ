package LJava;

import static LJava.LJ.*;
import java.util.Arrays;
import java.util.HashMap;

public class Association {

	protected final String name;
	protected final Object[] args;	
	
	public Association(String n, Object... params) {
		name = (n==null || n=="") ? "#LJRelation" : n;
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


	public boolean isLazy() {
		return false;
	}
	
	
	public boolean undef() {
		return (this==undefined);
	}
	
	
	public String toString(){
		if (this==_) return "_";
		StringBuilder s = new StringBuilder(name+"(");
		if (args.length>0) {
			for (int i=0; i<args.length-1; i++)	s.append(string(args[i])+",");
			s.append(string(args[args.length-1]));
		}
		s.append(")");
		return s.toString();
	}
	

	public Association replaceVariables(HashMap<Variable, Object> replacements) {
		if (isFormula()) return this;
		Object arguments[]=new Object[args.length];
		for (int i=0; i<args.length; i++) {
			arguments[i]=replacements.get(args[i]);
			arguments[i]= (arguments[i]==null)? args[i]: arguments[i];
		}
		return relation(name, arguments);
	}

	
	public Association replaceVariables(Variable v1, Object v2) {
		HashMap<Variable, Object> m=new HashMap<Variable, Object>();
		m.put(v1, v2);
		return replaceVariables(m);
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
		
	
	protected boolean satisfy(Object[] rArgs, VariableMap varValues) {
		return false;
	}
	
	
//Compares between 2 relations.
	private boolean equalsAssociation(Association r){
		if (!this.getClass().equals(r.getClass())) return false;
		if (!associationNameCompare(r)) return false;
		for (int i=0; i<args.length; i++)
			if (this.args[i]!=r.args[i]) return false;
		return true;
	}
}
