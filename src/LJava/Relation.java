package LJava;

import static LJava.LJ.*;

import java.util.HashMap;

import LJava.LJ.LJIterator;

public class Relation extends Association implements QueryParameter{
	
	public Relation(String n, Object... params){
		super (n, params);
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
	public boolean map(VariableMap m, boolean cut) {
		boolean out=false;
		LJIterator i=iterate(argsLength());
		Association a;
		while ((a=i.hasAndGrabNext(args))!=undefined) {
			out=(evaluate(this, m, i, a) || out);
			if (out && cut) return true;
		}
		return out;	
	}
	

	protected boolean satisfied(Object[] arr, VariableMap m, boolean cut) {
		return relation(name, arr).map(m, cut);
	}
}
