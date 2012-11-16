package LJava;

import static LJava.LJ.*;
import java.util.HashMap;

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
		varValues.updateValuesMap(vars);
		return true;
	}			

	@Override
	public VariableMap map(){
		VariableMap m=new VariableMap();
		conduct(this, m, false);
		return m;
	}
}
