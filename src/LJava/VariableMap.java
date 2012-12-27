package LJava;

import static LJava.LJ.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class VariableMap {
	
	protected HashMap<Variable, ArrayList<Object>> map=new HashMap<Variable, ArrayList<Object>>();;
	protected HashMap<Variable, Constraint> constraints= new HashMap<Variable, Constraint>();
	
	
	public VariableMap() {}
	
	
	public VariableMap(HashMap<Variable, Object> vars) {
		updateValsMap(vars);
	}
	
	
	public final boolean isEmpty() {
		return (map.isEmpty() && constraints.isEmpty());
	}
	
	
	public final void clear() {
		map=new HashMap<Variable, ArrayList<Object>>();
		constraints= new HashMap<Variable, Constraint>();
	}
	
	
	public final void updateValsMap(HashMap<Variable, Object> vars) {
		for (Map.Entry<Variable, Object> entry : vars.entrySet()) 
			updateValsMap(entry.getKey(), entry.getValue());			
	}
	
	
	public final void updateValsMap(Variable key, Object val) {
		addTo(map, key, val, ArrayList.class);
	}
	
	
	public final void add(VariableMap m) {
		for (Map.Entry<Variable, ArrayList<Object>> entry : m.map.entrySet())
			for (Object o : entry.getValue()) updateValsMap(entry.getKey(), o);
		updateConstraintsMap(m.constraints);
	}
	
	
	public final void updateConstraintsMap(HashMap<Variable, Constraint> vars) {
		for (Map.Entry<Variable, Constraint> entry : vars.entrySet()) 
			updateConstraintsMap(entry.getKey(), entry.getValue());			
	}
	
	
	public final void updateConstraintsMap(Variable key, Constraint val) {
		Constraint c = constraints.get(key);
		if (c==null) constraints.put(key, val);
		else constraints.put(key, new Constraint(val,OR,c));
	}
	
	
	public final Object getVals(Variable v) {
		ArrayList<Object> vals=map.get(v);
		if (vals==null) return undefined;
		return vals;
	}


	public final Object getConstraint(Variable v) {
		Constraint vals=constraints.get(v);
		if (vals==null) return undefined;
		return vals;		
	}
	
	
	public HashSet<Variable> getVars() {
		HashSet<Variable> set = new HashSet<Variable>();
		set.addAll(map.keySet());
		set.addAll(constraints.keySet());
		return set;
	}
}
