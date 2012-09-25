package LJava;

import static LJava.LJ._;
import static LJava.LJ.none;
import static LJava.LJ.var;
import static LJava.LJ.nil;
import static LJava.LJ.LJavaTrueRelation;

import java.util.HashMap;

public class Association {

	private final String name;
	private final Object[] args;	
	
	public Association(String n, Object... params) {
		if (n=="") name="#Relation"; 
		else this.name=n;
		this.args=params;		
	}
		
	
	public String name(){
		return name;
	}
	
	
	public int argsLength(){
		return args.length;
	}	
	

	public Object[] args(){ 
		return args.clone(); 
	}
	
	
	public boolean isGroup() {
		return false;
	}		
	
	
	public String toString(){
		if (this==_) return "_";
		String s=name+"(";		
		if (args!=null && args.length>0) {
			for (int i=0; i<args.length-1; i++)
				s=s+args[i].toString()+",";
			s=s+args[args.length-1].toString();
		}
		return s+")";
	}
	

	public Object[] replaceVariables(){
		HashMap<Variable,Variable> varsMap=new HashMap<Variable,Variable>();
		int argsLength=args.length;
		Object[] arguments=new Object[argsLength];		
		for (int i=0; i<argsLength; i++)
			if (var(args[i]))	{
				if (varsMap.containsKey(args[i])) arguments[i]=varsMap.get(args[i]);
				else {
					arguments[i]=new Variable();
					varsMap.put((Variable) args[i], (Variable) arguments[i]);
				}					
			}
			else arguments[i]=args[i];
		return arguments;
	}


	protected boolean relationNameCompare(Association r) {			
		if ((name.charAt(0)!='#') || (r.name.charAt(0)!='#'))
			if (!name.equals(r.name)) return false;
		return true;
	}
	

	public boolean equals(Object o) {
		if (this==_ || o==_) return true;
		if (this==none || o==none) return false;
		if (o instanceof Association) 			
			return this.equalsAssociation((Association) o);		
		return o.equals(this);		
	}
		
	
	protected boolean satisfy(Object[] rArgs, VariableValuesMap varValues){
		if (this==nil || this==LJavaTrueRelation) return true;
		return false;
	}
	
	
//Compares between 2 relations.
	private boolean equalsAssociation(Association r){
		if (!relationNameCompare(r)) return false;
		if (this.isGroup() ^ r.isGroup()) return false;
		for (int i=0; i<args.length; i++)
			if (!this.args[i].equals(r.args[i])) return false;
		return true;
	}
		
}
