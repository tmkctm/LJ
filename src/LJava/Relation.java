package LJava;

import static LJava.LJ.*;

import java.util.HashMap;

public class Relation implements QueryParameter {
	
	private String name;
	private Object[] args;
	private int argsLength;
	
	
	public Relation(String n, Object... params){
		if ((n==null) || (n=="")) name="#Relation"; 
		else this.name=n;
		this.args=params;
		argsLength=params.length;	
	}
	
	
	public String name(){
		return name;
	}
	
	
	public int argsLength(){
		return argsLength;
	}

	
	public boolean equals(Object o) {
		if (this==_ || o==_) return true;
		if (this==none || o==none) return false;
		if (o instanceof Relation) {
			Relation r=(Relation) o;
			return this.equalsRelation(r);
		}
		return o.equals(this);		
	}
	
	
	public  Object[] args(){ 
		return args.clone(); 
	}
	
	
	public boolean isGroup() {
		return (this instanceof Group);
	}	
	
	
	public String toString(){
		if (name.equals("_")) return "_";
		String s=name+"(";		
		if (args!=null)
			if (args.length>0) {
				for (int i=0; i<args.length-1; i++)
					s=s+args[i].toString()+",";
				s=s+args[args.length-1].toString();
			}
		return s+")";
	}
	
	
	public Relation replaceVariables(String s){
		HashMap<Variable,Variable> varsMap=new HashMap<Variable,Variable>();		
		Object[] arguments=new Object[argsLength];		
		for (int i=0; i<arguments.length; i++)
			if (var(args[i]))	{
				if (varsMap.containsKey(args[i])) arguments[i]=varsMap.get(args[i]);
				else {
					arguments[i]=new Variable();
					varsMap.put((Variable) args[i], (Variable) arguments[i]);
				}					
			}
			else arguments[i]=args[i];
		return new Relation(s,arguments);
	}		
	

	protected boolean relationNameCompare(Relation r) {			
		if ((name.charAt(0)!='#') || (r.name.charAt(0)!='#'))
			if (!name.equals(r.name)) return false;
		return true;
	}	
		
	
	//Compares between 2 relations.
		private boolean equalsRelation(Relation r){
			if (!relationNameCompare(r)) return false;
			if (this.isGroup() ^ r.isGroup()) return false;
			for (int i=0; i<argsLength; i++)
				if (!this.args[i].equals(r.args[i])) return false;
			return true;
		}
	
		
	//The main method behind queries. It checks satisfaction for a relation against another relation and adds results to a map.	
	protected boolean satisfy(Relation r, VariableValuesMap varValues){
		HashMap<Variable,Object> vars=new HashMap<Variable,Object>();	
		for (int i=0; i<r.argsLength; i++) {			
			if (var(r.args[i]))
				if (vars.containsKey(r.args[i])) {
					if (!same(vars.get(r.args[i]), args[i])) return false;   }
				else vars.put((Variable) r.args[i], args[i]);								
			else if (!same(r.args[i],args[i])) return false;
		}
		updateValuesMap(vars, varValues);
		return true;
	}			
	
}
