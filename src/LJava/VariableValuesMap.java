package LJava;

import static LJava.Utils.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class VariableValuesMap implements QueryParameter{
	
	protected HashMap<Variable, ArrayList<Object>> map=new HashMap<Variable, ArrayList<Object>>();;
	protected HashMap<Variable, Constraint> constraints= new HashMap<Variable, Constraint>();
	
	public final boolean isEmpty() {
		return (map.isEmpty() && constraints.isEmpty());
	}
	
	
	@Override
	public final VariableValuesMap map(){
		return this;
	}

	
	public final void updateValuesMap(HashMap<Variable,Object> vars){		
		for (Map.Entry<Variable, Object> entry : vars.entrySet()) 
			updateValuesMap(entry.getKey(), entry.getValue());			
	}
	
	
	public final void updateValuesMap(Variable key, Object val) {
		addTo(map, key, val, ArrayList.class);
	}		
	

	public final Set<Variable> getVars() {
		Set<Variable> set = new HashSet<Variable>();
		for (Variable v : map.keySet()) set.add(v);
		for (Variable v : constraints.keySet()) set.add(v);
		return set;
	}
}
