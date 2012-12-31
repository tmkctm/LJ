package LJava;
import static LJava.LJ.increment;
import static LJava.LJ.val;
import static LJava.LJ.var;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

import LJava.Group;
import LJava.MapComparatorByValue;
import LJava.Variable;
import LJava.VariableMap;


public class LazyGroup extends Association {
		
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
	protected TreeMap<Variable, Integer> varsCount=new TreeMap<Variable, Integer>();
	private HashMap<Object, Integer> valsCount=new HashMap<Object, Integer>();
	
	
	public LazyGroup(Group group, Object[] rArgs) {
		super(group.name, group.args);
		HashMap<Variable, Integer> rVarsCountMap=new HashMap<Variable, Integer>();
		HashMap<Object, Integer> rArgsCountMap=new HashMap<Object, Integer>();
		buildValsAndVarsCount(rArgs, rVarsCountMap, rArgsCountMap);
		elimination(rVarsCountMap, rArgsCountMap, group);
	}
	
	
	private final void buildValsAndVarsCount(Object[] rArgs, HashMap<Variable, Integer> rVarsCountMap, HashMap<Object, Integer> rArgsCountMap) {
		for (Object element : rArgs) {
			if (var(element)) increment(rVarsCountMap,(Variable) element,1);
			else increment(rArgsCountMap, val(element),1);
		}			
	}
	
	
	private final void elimination(HashMap<Variable, Integer> rVarsCountMap, HashMap<Object, Integer> rArgsCountMap, Group g) {
		Integer count=0;
		for (Map.Entry<Object, Integer> entry : g.argsMap.entrySet()) { //Differing amounts between group's map and r's map
			Object keyVal=val(entry.getKey());
			count=(var(keyVal))? rVarsCountMap.remove(keyVal) : rArgsCountMap.remove(keyVal);
			count=(count==null)? -entry.getValue() : count-entry.getValue();
			if (count>0) {
				if (!var(keyVal)) return;
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
	
	
	protected boolean satisfy(Object[] rArgs, VariableMap varValues) {
		return lazy(varValues);
	}
	
	
	public final boolean lazy(VariableMap varValues) {
		while (!iStack.isEmpty()) {
			VarIterator i=iStack.pop();
			if (varsCount.get(i.var)==null) backtrack(i);
			while (i.iterator.hasNext()) {
				Map.Entry<Object, Integer> entry=i.iterator.next();
				int difference=entry.getValue()-i.count;
				if (difference<0) continue;
				entry.setValue(difference);
				varsCount.remove(i.var);
				answer.updateValsMap(i.var, entry.getKey());
				iStack.push(i);
				if (varsCount.isEmpty()) {
					varValues.add(answer);
					return true;
				}
				i=new VarIterator(valsCount.entrySet().iterator(), varsCount.firstKey(), varsCount.remove(varsCount.firstKey()));
			}
		}
		return false;
	}
	
	
	public final VariableMap current() {
		VariableMap map=new VariableMap();
		map.add(answer);
		return map;
	}
	
	
	private final void backtrack(VarIterator i) {
		Object key=answer.map.remove(i.var).get(0);
		valsCount.put(key, valsCount.get(key)+i.count);
		varsCount.put(i.var, i.count);			
	}

	
	@Override
	public final String toString(){
		StringBuilder sb = new StringBuilder("lazy "+name+"( VARS: ");
		sb.append(varsCount.toString());
		sb.append("  ;  VALUES: ");
		sb.append(valsCount.toString());
		sb.append(" )");
		return sb.toString();
	}	

	
	private class MapComparatorByValue<T> implements Comparator<T> {	
		Map<T,Integer> sourceMap;		
		public MapComparatorByValue(HashMap<T,Integer> m) {
			sourceMap=m;
		}		
		@Override
		public int compare(T a, T b) {
			if (sourceMap.get(a)>sourceMap.get(b)) return -1;
			else if (sourceMap.get(a)<sourceMap.get(b)) return 1;
			int aHash=a.hashCode();
			int bHash=b.hashCode();
			if (aHash>bHash) return -1;
			else if (aHash<bHash) return 1;
			return 0;
		}
	}
	
}	

