package LJava;

import static LJava.Utils.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class VariableMap implements QueryParameter {
	
	protected HashMap<Variable, ArrayList<Object>> map=new HashMap<Variable, ArrayList<Object>>();;
	protected HashMap<Variable, Constraint> constraints= new HashMap<Variable, Constraint>();
	
	public final boolean isEmpty() {
		return (map.isEmpty() && constraints.isEmpty());
	}
	
	
	@Override
	public final boolean map(VariableMap m, boolean cut) {
		if (cut) {
			for (Map.Entry<Variable, ArrayList<Object>> entry : map.entrySet())
				m.updateValsMap(entry.getKey(), entry.getValue().get(0));
			m.updateConstraintsMap(this.constraints);
		}
		else m.add(this);
		return true;
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
	
	
	public final void updateConstraintsMap(Constraint c) {
		for (Variable x : c.getVars()) updateConstraintsMap(x,c);
	}
	
	
	@Override
	public final HashSet<Variable> getVars() {
		HashSet<Variable> set = new HashSet<Variable>();
		for (Variable v : map.keySet()) set.add(v);
		for (Variable v : constraints.keySet()) set.add(v);
		return set;
	}
}
