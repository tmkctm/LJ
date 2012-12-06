package LJava;

import static LJava.LJ.*;
import java.util.HashMap;
import java.util.Iterator;

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
		out=mapAgainstIndex(m, cut, this.args.length);
		if (out && cut) return true;
		out=(mapAgainstIndex(m, cut, -1) || out);		
		return out;	
	}
	
	
	private boolean mapAgainstIndex(VariableMap m, boolean cut, int index) {
		boolean out=false;
		Iterator<Association> i = getLJIterator(index);
		if (i==null) return false;
		while (i.hasNext()) {
			out=(evaluate(this, m, i) || out);
			if (cut && out) return true;
		}
		return out;
	}
}
