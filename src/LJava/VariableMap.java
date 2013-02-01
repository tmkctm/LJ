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
	
	
	public final synchronized VariableMap clear() {
		map=new HashMap<Variable, ArrayList<Object>>();
		constraints= new HashMap<Variable, Constraint>();
		return this;
	}
	
	
	public final VariableMap updateValsMap(HashMap<Variable, Object> vars) {
		for (Map.Entry<Variable, Object> entry : vars.entrySet()) 
			updateValsMap(entry.getKey(), entry.getValue());
		return this;
	}
	
	
	public final synchronized VariableMap updateValsMap(Variable key, Object val) {
		addTo(map, key, val, ArrayList.class);
		return this;
	}
	
	
	public final VariableMap add(VariableMap m) {
		for (Map.Entry<Variable, ArrayList<Object>> entry : m.map.entrySet())
			for (Object o : entry.getValue()) updateValsMap(entry.getKey(), o);
		updateConstraintsMap(m.constraints);
		return this;
	}
	
	
	public final VariableMap updateConstraintsMap(HashMap<Variable, Constraint> vars) {
		for (Map.Entry<Variable, Constraint> entry : vars.entrySet()) 
			updateConstraintsMap(entry.getKey(), entry.getValue());
		return this;
	}
	
	
	public final synchronized VariableMap updateConstraintsMap(Variable key, Constraint val) {
		Constraint c = constraints.get(key);
		if (c==null) constraints.put(key, val);
		else constraints.put(key, new Constraint(val,OR,c));
		return this;
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
	
	
	public final String toString() {
		StringBuilder sb=new StringBuilder("Values: ");
		sb.append(map);
		sb.append("  ;  Constraints: ");
		sb.append(constraints);
		return sb.toString();
		
	}

	
	@SuppressWarnings("rawtypes")
	public final Variable[] toArray(Variable... vs) {
		Variable[] result=new Variable[vs.length];
		for (int i=0; i<vs.length; i++)  {
			Object o=map.get(vs[i]);
			o=(o==null)? undefined : ((ArrayList) o).get(0);
			result[i]=var();
			result[i].set(o);
		}
		return result;
	}
}

