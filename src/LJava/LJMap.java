package LJava;

import static LJava.LJ.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * @author Tzali Maimon
 * The map that holds all the answers to the queries activated within LJ. This map is returned from most Lazy objects as well.<p>
 * It can be used for user's purposes as well to manage data and a progressive use of LJ.<p>
 * NOTE: THIS CLASS ISN'T ENTRIELY BUT ONLY PARTIALLY THREAD-SAFE. READ THE METHODS DESCRIPTIONS!
 */
public class LJMap {
	
	public HashMap<Variable, ArrayList<Object>> map=new HashMap<Variable, ArrayList<Object>>();;
	protected HashMap<Variable, Constraint> constraints= new HashMap<Variable, Constraint>();
	
	
	/**
	 * an empty map
	 */
	public LJMap() {}
	
	
	/**
	 * @param vars - hash map to use to initialize the LJMap with
	 */
	public LJMap(HashMap<Variable, Object> vars) {
		updateValsMap(vars);
	}
	
	
	/**
	 * @return true if map is empty
	 */
	public final boolean isEmpty() {
		return (map.isEmpty() && constraints.isEmpty());
	}
	
	
	/**
	 * empties the map. thread safe
	 * @return this LJMap
	 */
	public final synchronized LJMap clear() {
		map=new HashMap<Variable, ArrayList<Object>>();
		constraints= new HashMap<Variable, Constraint>();
		return this;
	}
	
	
	/**
	 * thread safe
	 * @param vars - values to add
	 * @return this LJMap
	 */
	public final LJMap updateValsMap(HashMap<Variable, Object> vars) {
		for (Map.Entry<Variable, Object> entry : vars.entrySet()) 
			updateValsMap(entry.getKey(), entry.getValue());
		return this;
	}
	
	
	/**
	 * thread safe
	 * @param key - a variable
	 * @param val - a value to add to the variable
	 * @return this LJMap
	 */
	public final synchronized LJMap updateValsMap(Variable key, Object val) {
		addTo(map, key, val, ArrayList.class);
		return this;
	}
	
	
	/**
	 * thread safe
	 * @param m - an LJMap to add to this map
	 * @return this LJMap
	 */
	public final LJMap add(LJMap m) {
		for (Map.Entry<Variable, ArrayList<Object>> entry : m.map.entrySet())
			for (Object o : entry.getValue()) updateValsMap(entry.getKey(), o);
		updateConstraintsMap(m.constraints);
		return this;
	}
	
	
	/**
	 * thread safe
	 * @param vars - update constraints of variables
	 * @return this LJMap
	 */
	public final LJMap updateConstraintsMap(HashMap<Variable, Constraint> vars) {
		for (Map.Entry<Variable, Constraint> entry : vars.entrySet()) 
			updateConstraintsMap(entry.getKey(), entry.getValue());
		return this;
	}
	
	
	/**
	 * thread safe
	 * adds the constraint to the variable given
	 * @param key a variable
	 * @param val a constraint
	 * @return this LJMap
	 */
	public final synchronized LJMap updateConstraintsMap(Variable key, Constraint val) {
		Constraint c = constraints.get(key);
		if (c==null) constraints.put(key, val);
		else constraints.put(key, new Constraint(val,OR,c));
		return this;
	}
	
	
	/**
	 * thread safe but note that the returned Object is the actual list of values of the variable. changing that list will effect the LJMap
	 * @param v - a variable
	 * @return The List of values of v or undefined if cannot find the variable
	 */
	public final synchronized Object getVals(Variable v) {
		ArrayList<Object> vals=map.get(v);
		if (vals==null) return undefined;
		return vals;
	}


	/**
	 * thread safe but pay attention that the constraint returned is the actual constraint of the variable. Constraint is immutable so this makes this method thread-safe 
	 * @param v - a variable 
	 * @return the constraint of v or undefined if cannot find the variable
	 */
	public final synchronized Object getConstraint(Variable v) {
		Constraint vals=constraints.get(v);
		if (vals==null) return undefined;
		return vals;		
	}
	
	
	/**
	 * Thread safe.
	 * @return the variables that appear in this map. 
	 */
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

	
	/**
	 * @param index - index of the required answer
	 * @param vs - the order of variables that is wanted
	 * @return the objects that appear in the map according to the order of Variables in vs. undefined is returned if the variable isn't found or doesn't have an answer in the place index.
	 */
	@SuppressWarnings("rawtypes")
	public final synchronized Variable[] get(int index, Variable... vs) {
		Variable[] result=new Variable[vs.length];
		for (int i=0; i<vs.length; i++)  {
			Object o=map.get(vs[i]);
			o=(o==null || index>=((ArrayList) o).size())? undefined : ((ArrayList) o).get(index);
			result[i]=var();
			result[i].set(o);
		}
		return result;
	}
	
	
	/**
	 * @return get(0, getVars().toArray(new Variable[0]));
	 */
	public final Variable[] get() {
		return get(0, getVars().toArray(new Variable[0]));
	}
	
	
	/**
	 * @param index
	 * @return get(index, getVars().toArray(new Variable[0]));
	 */
	public final Variable[] get(int index) {
		return get(index, getVars().toArray(new Variable[0]));
	}
	
	
	/**
	 * @param vs
	 * @return get(0, getVars().toArray(new Variable[0]));
	 */
	public final Variable[] get(Variable... vs) {
		return get(0, vs); 
	}
}

