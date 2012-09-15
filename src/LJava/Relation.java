package LJava;

import static LJava.LJ.*;

import java.util.HashMap;
import java.util.LinkedHashSet;

public class Relation extends Association {
	
	public Relation(String n, Object... params){
		super (n, params);
	}

	@Override
	protected boolean satisfy(Relation r, VariableValuesMap varValues){
		HashMap<Variable,LinkedHashSet<Object>> vars=new HashMap<Variable, LinkedHashSet<Object>>();		
		for (int i=0; i<r.argsLength(); i++) {			
			if (var(r.args[i]))
				if (vars.containsKey(r.args[i])) {
					if (!same(vars.get(r.args[i]).toArray()[0], args[i])) return false;   }
				else {
					LinkedHashSet<Object> temp=new LinkedHashSet<Object>();
					temp.add(args[i]);
					vars.put((Variable) r.args[i], temp);								
				}
			else if (!same(r.args[i],args[i])) 
				return false;
		}
		updateValuesMap(vars, varValues);
		return true;
	}			
	
}
