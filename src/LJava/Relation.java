package LJava;

import static LJava.LJ.*;

import java.util.HashMap;

public class Relation extends Association implements QueryParameter{
	
	public Relation(String n, Object... params){
		super (n, params);
	}

	@Override
	protected boolean satisfy(Relation r, VariableValuesMap varValues){
		HashMap<Variable,Object> vars=new HashMap<Variable, Object>();		
		for (int i=0; i<r.argsLength(); i++) {			
			if (var(r.args[i])) {
				if (vars.containsKey(r.args[i])) {
					if (!same(vars.get(r.args[i]), args[i])) return false;   }
				else vars.put((Variable) r.args[i], args[i]);   }								
			else if (!same(r.args[i],args[i])) return false;
		}
		updateValuesMap(vars, varValues);
		return true;
	}			

	@Override
	public VariableValuesMap map(){
		VariableValuesMap m=new VariableValuesMap();
		conduct(this,m);
		return m;
	}
	
}
