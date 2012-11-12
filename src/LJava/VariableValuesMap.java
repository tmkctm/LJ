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
	
	public boolean isEmpty() {
		return (map.isEmpty() && constraints.isEmpty());
	}
	
	
	@Override
	public VariableValuesMap map(){
		return this;
	}

	
	public void updateValuesMap(HashMap<Variable,Object> vars){		
		for (Map.Entry<Variable, Object> entry : vars.entrySet()) 
			updateValuesMap(entry.getKey(), entry.getValue());			
	}
	
	
	public void updateValuesMap(Variable key, Object val) {
		addTo(map, key, val, ArrayList.class);
	}		
	
	
	public VariableValuesMap cutWith(VariableValuesMap m) {
		//TBD
		return null;
	}
	

	public VariableValuesMap uniteWith(VariableValuesMap m) {
		VariableValuesMap result = new VariableValuesMap();
		Set<Variable> set = getVars();
		set.addAll(m.getVars());
		for (Variable v : set) {
			result.constraints.put(v, new Constraint(constraints.get(v),OR,m.constraints.get(v)));
			ArrayList<Object> values = getFrom(map,v,ArrayList.class);
			values.addAll(getFrom(m.map,v,ArrayList.class));
			if (!values.isEmpty()) result.map.put(v, values);
		}
		return result;
	}
	
	
	public VariableValuesMap differFrom(VariableValuesMap m) {
		VariableValuesMap result = new VariableValuesMap();
		Set<Variable> set = getVars();
		for (Variable v : set) {
			Constraint c = m.constraints.get(v);			
			result.constraints.put(v, new Constraint(constraints.get(v),DIFFER,c));
			if (c==null) c = new Constraint(LJFalse);
			ArrayList<Object> values = getFrom(map,v,ArrayList.class);
			ArrayList<Object> mValues = getFrom(m.map,v,ArrayList.class);
			for (Object o : values) if (mValues.contains(o) || c.satisfy(v, o)) values.remove(o);
			if (!values.isEmpty()) result.map.put(v, values);
		}
		return result;
	}
	
	
	public Set<Variable> getVars() {
		Set<Variable> set = new HashSet<Variable>();
		for (Variable v : map.keySet()) set.add(v);
		for (Variable v : constraints.keySet()) set.add(v);
		return set;
	}
}
