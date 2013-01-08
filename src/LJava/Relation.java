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
		LJIterator i=iterate(argsLength());
		if (cut) evaluate(this, answer, i);
		if (!evaluate(this, answer, i)) return false;
		while (evaluate(this, answer, i)) {}
		return true;
	}
	
	
	protected boolean lz(VariableMap answer) {
		if (iterator==emptyIterator) iterator=iterate(argsLength());
		return evaluate(this, answer, iterator); 
	}
	
}
