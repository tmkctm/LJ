package LJava;

import static LJava.LJ.*;

import java.util.HashMap;

import LJava.LJ.LJIterator;

public class Relation extends Association implements QueryParameter{
	
	private LJIterator iterator;
	
	public Relation(String n, Object... params){
		super (n, params);
		iterator=emptyIterator;
	}
	
	
	@Override
	protected boolean satisfy(Object[] rArgs, VariableMap varValues){
		HashMap<Variable,Object> vars=new HashMap<Variable, Object>();
		Object[] args=this.args();
		for (int i=0; i<rArgs.length; i++) {			
			if (var(rArgs[i])) {
				if (vars.containsKey(rArgs[i])) {
					if (!same(vars.get(rArgs[i]), args[i])) return false;   }
				else vars.put((Variable) rArgs[i], args[i]);   }								
			else if (!same(rArgs[i],args[i])) return false;
		}
		varValues.updateValsMap(vars);
		return true;
	}			

	
	@Override
	public boolean map(VariableMap answer, boolean cut) {
		LJIterator i=iterate(args.length);
		Association a;			boolean result=false;
		while ((a=i.hasAndGrabNext(args))!=undefined) { 
			result=(evaluate(this, answer, i, a) || result);
			if (result && cut) return true;
		}
		return result;
	}
	
	
	public boolean lz(VariableMap answer) {
		if (iterator==emptyIterator) iterator=iterate(args.length);
		Association a;
		while ((a=iterator.hasAndGrabNext(args))!=undefined) 
			if (evaluate(this, answer, iterator, a)) return true; 
		return false;		
	}
	
	
	protected boolean satisfied(Object[] arr, VariableMap m, boolean cut) {
		return relation(this.name,arr).map(m, cut);
	}	
	
}
