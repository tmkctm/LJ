package LJava;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import static LJava.LJ.var;
import static LJava.LJ.val;


public class Group extends Association{
	
	private final Map<Object, Integer> argsMap;
	
	public Group(String n, Object... params) {
		super(n, params);
		HashMap<Object, Integer> map=new HashMap<Object, Integer>();
		if (params!=null) {
			for (Object element : params) increment(map, element);		
		}
		argsMap=Collections.unmodifiableMap(map);
	}
	

	@Override
	public boolean isGroup() {
		return true;
	}
	
	
	@Override
	protected boolean satisfy(Object[] rArgs, VariableValuesMap varValues){	
		HashMap<Variable, Integer> rVarsCountMap=new HashMap<Variable, Integer>();
		HashMap<Object, Integer> remainedVals=new HashMap<Object, Integer>();
		HashMap<Object, Integer> rArgsCountMap=new HashMap<Object, Integer>();
		
		for (Object element : rArgs) {
			if (var(element)) increment(rVarsCountMap,(Variable) element);
			else increment(rArgsCountMap, val(element));
		}
		
		Integer count=0;
		for (Map.Entry<Object, Integer> entry : argsMap.entrySet()) { //Differing amounts between group's map and r's map
			Object key=entry.getKey();
			Object keyVal=val(key);
			if (var(keyVal)) count=rVarsCountMap.remove(keyVal);
			else count=rArgsCountMap.remove(keyVal);
			if (count==null) count=0; 					
			count=count-entry.getValue();
			if (count>0) {
				if (var(keyVal)) rVarsCountMap.put((Variable) keyVal, count);
				else return false;
			}
			else if (count<0) remainedVals.put(keyVal, -count); 
		}	
		
		if (!rArgsCountMap.isEmpty()) return false;
		if (rVarsCountMap.isEmpty()) return true;
		
		TreeMap<Variable, Integer> vars = new TreeMap<Variable, Integer>(new MapComparatorByValue<Variable>(rVarsCountMap));
		vars.putAll(rVarsCountMap);
		TreeMap<Object, Integer> vals = new TreeMap<Object, Integer>(new MapComparatorByValue<Object>(remainedVals));
		vals.putAll(remainedVals);	
		HashMap<Variable,Object> varResults=new HashMap<Variable, Object>();			
		for (Map.Entry<Variable, Integer> entry : vars.entrySet()) {							
			Object firstKey=vals.firstKey();			
			int remain=vals.remove(firstKey)-entry.getValue();
			if (remain<0) return false;					
			varResults.put(entry.getKey(),firstKey);
			if (remain>0) vals.put(firstKey, remain);			
		}
		varValues.updateValuesMap(varResults);		
		return true;
	}
	
	
	private <T> void increment(Map<T, Integer> m, T element) {
		Integer count=m.get(element);
		if (count==null) count=0;
		m.put(element,count+1);	
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
