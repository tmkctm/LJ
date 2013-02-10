package LJava;

import static LJava.LJ.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class LJMap {
	
	protected HashMap<Variable, ArrayList<Object>> map=new HashMap<Variable, ArrayList<Object>>();;
	protected HashMap<Variable, Constraint> constraints= new HashMap<Variable, Constraint>();
	
	
	public LJMap() {}
	
	
	public LJMap(HashMap<Variable, Object> vars) {
		updateValsMap(vars);
	}
	
	
	public final boolean isEmpty() {
		return (map.isEmpty() && constraints.isEmpty());
	}
	
	
	public final synchronized LJMap clear() {
		map=new HashMap<Variable, ArrayList<Object>>();
		constraints= new HashMap<Variable, Constraint>();
		return this;
	}
	
	
	public final LJMap updateValsMap(HashMap<Variable, Object> vars) {
		for (Map.Entry<Variable, Object> entry : vars.entrySet()) 
			updateValsMap(entry.getKey(), entry.getValue());
		return this;
	}
	
	
	public final synchronized LJMap updateValsMap(Variable key, Object val) {
		addTo(map, key, val, ArrayList.class);
		return this;
	}
	
	
	public final LJMap add(LJMap m) {
		for (Map.Entry<Variable, ArrayList<Object>> entry : m.map.entrySet())
			for (Object o : entry.getValue()) updateValsMap(entry.getKey(), o);
		updateConstraintsMap(m.constraints);
		return this;
	}
	
	
	public final LJMap updateConstraintsMap(HashMap<Variable, Constraint> vars) {
		for (Map.Entry<Variable, Constraint> entry : vars.entrySet()) 
			updateConstraintsMap(entry.getKey(), entry.getValue());
		return this;
	}
	
	
	public final synchronized LJMap updateConstraintsMap(Variable key, Constraint val) {
		Constraint c = constraints.get(key);
		if (c==null) constraints.put(key, val);
		else constraints.put(key, new Constraint(val,OR,c));
		return this;
	}
	
	
	public final synchronized Object getVals(Variable v) {
		ArrayList<Object> vals=map.get(v);
		if (vals==null) return undefined;
		return vals;
	}


	public final synchronized Object getConstraint(Variable v) {
		Constraint vals=constraints.get(v);
		if (vals==null) return undefined;
		return vals;		
	}
	
	
	public final synchronized HashSet<Variable> getVars() {
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
	public final synchronized Variable[] get(int index, Variable... vs) {
		Variable[] result=new Variable[vs.length];
		for (int i=0; i<vs.length; i++)  {
			Object o=map.get(vs[i]);
			o=(o==null)? undefined : ((ArrayList) o).get(index);
			result[i]=var();
			result[i].set(o);
		}
		return result;
	}
	
	
	public final Variable[] get() {
		return get(0, getVars().toArray(new Variable[0]));
	}
	
	
	public final Variable[] get(int index) {
		return get(index, getVars().toArray(new Variable[0]));
	}
	
	
	public final Variable[] get(Variable... vs) {
		return get(0, getVars().toArray(new Variable[0])); 
	}
}

