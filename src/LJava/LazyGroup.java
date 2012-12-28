package LJava;
import static LJava.LJ.increment;
import static LJava.LJ.val;
import static LJava.LJ.var;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

import LJava.Group;
import LJava.MapComparatorByValue;
import LJava.Variable;
import LJava.VariableMap;


public class LazyGroup extends Group {
		
	private class VarIterator {
		Iterator<Map.Entry<Object, Integer>> iterator;
		Variable var;
		int count;
		public VarIterator(Iterator<Map.Entry<Object, Integer>> i, Variable v, int c) {
			iterator=i;			var=v;		count=c;
		}
	}			

	
	private VariableMap answer=new VariableMap();
	private LinkedList<VarIterator> iStack=new LinkedList<VarIterator>();
	private TreeMap<Variable, Integer> varsCount;
	private HashMap<Object, Integer> valsCount=new HashMap<Object, Integer>();
	
	
	public LazyGroup(String n, Object[] params, Object[] rArgs) {
		super(n,params);
		HashMap<Variable, Integer> rVarsCountMap=new HashMap<Variable, Integer>();
		HashMap<Object, Integer> rArgsCountMap=new HashMap<Object, Integer>();
		buildValsAndVarsCount(rArgs, rVarsCountMap, rArgsCountMap);
		elimination(rVarsCountMap, rArgsCountMap);
	}
	
	
	private final void buildValsAndVarsCount(Object[] rArgs, HashMap<Variable, Integer> rVarsCountMap, HashMap<Object, Integer> rArgsCountMap) {
		for (Object element : rArgs) {
			if (var(element)) increment(rVarsCountMap,(Variable) element,1);
			else increment(rArgsCountMap, val(element),1);
		}			
	}
	
	
	private final void elimination(HashMap<Variable, Integer> rVarsCountMap, HashMap<Object, Integer> rArgsCountMap) {
		Integer count=0;
		for (Map.Entry<Object, Integer> entry : argsMap.entrySet()) { //Differing amounts between group's map and r's map
			Object keyVal=val(entry.getKey());
			count=(var(keyVal))? rVarsCountMap.remove(keyVal) : rArgsCountMap.remove(keyVal);
			if (count==null) count=0; 					
			count=count-entry.getValue();
			if (count>0) {
				if (!var(keyVal)) {
					rVarsCountMap=new HashMap<Variable, Integer>();
					break;
				}
				rVarsCountMap.put((Variable) keyVal, count);
			}
			else if (count<0) valsCount.put(keyVal, -count); 
		}
		if (rArgsCountMap.isEmpty() && !rVarsCountMap.isEmpty()) {				
			varsCount = new TreeMap<Variable, Integer>(new MapComparatorByValue<Variable>(rVarsCountMap));
			varsCount.putAll(rVarsCountMap);
			iStack.push(new VarIterator(valsCount.entrySet().iterator(), varsCount.firstKey(), varsCount.get(varsCount.firstKey())));
		}			
	}
	
	
	@Override
	protected final boolean satisfy(Object[] rArgs, VariableMap varValues) {
		while (!iStack.isEmpty()) {
			VarIterator i=iStack.pop();
			if (varsCount.remove(i.var)==null) backtrack(i);
			while (i.iterator.hasNext()) {
				Map.Entry<Object, Integer> entry=i.iterator.next();
				int difference=entry.getValue()-i.count;
				if (difference<0) continue;
				entry.setValue(difference);
				answer.updateValsMap(i.var, entry.getKey());
				iStack.push(i);
				if (varsCount.isEmpty()) {
					varValues.add(answer);
					return true;
				}
				i=new VarIterator(valsCount.entrySet().iterator(), varsCount.firstKey(), varsCount.get(varsCount.firstKey()));
			}
		}
		return false;
	}
	
	
	private final void backtrack(VarIterator i) {
		Object key=answer.map.remove(i.var).get(0);
		valsCount.put(key, valsCount.get(key)+i.count);
		varsCount.put(i.var, i.count);			
	}
}	

/* to fix:
 * This class isn't concurrent.
 */