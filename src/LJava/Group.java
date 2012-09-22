package LJava;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

import static LJava.LJ.updateValuesMap;
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
		if (rArgs.length>0) {
			TreeMap<Integer, LinkedList<Object>> sortedVals=new TreeMap<Integer, LinkedList<Object>>();
			HashMap<Object, Integer> vals=new HashMap<Object, Integer>();
			HashMap<Variable, Integer> vars=new HashMap<Variable, Integer>();
			for (Object element : rArgs) { //Mapping r's parameters.
				if (var(element)) increment(vars, (Variable) element);
				else increment(vals, val(element));
			}
					
			Integer count=0;
			for (Map.Entry<Object, Integer> entry : argsMap.entrySet()) { //Differing amounts between group's map and r's map
				Object key=entry.getKey();
				if (var(key)) {
					count=vars.get((Variable) key);
					if (count==null) count=0; 					
					count=count-entry.getValue();
					if (count==0) vars.remove((Variable) key);
					else if (count<0) modifyCountMap(sortedVals, key, -count);
					else vars.put((Variable) key, count);
					count=0;
				}
				else {
					count=vals.get(val(key));
					if (count==null) count=0;
					else vals.remove(val(key));
					count=entry.getValue()-count;					
					if (count>0) modifyCountMap(sortedVals, val(key), count);					
				}
				if (count<0) return false;
			}
			
			if (!vals.isEmpty()) return false;			
			if (vars.isEmpty()) return true;
			if (sortedVals.isEmpty()) return false;					
						
			TreeMap<Integer, LinkedList<Variable>> sortedVars= new TreeMap<Integer, LinkedList<Variable>>(Collections.reverseOrder());
			for (Map.Entry<Variable, Integer> entry : vars.entrySet()) {
				modifyCountMap(sortedVars, entry.getKey(), entry.getValue());
			}
				
			
			HashMap<Variable,Object> varResults=new HashMap<Variable, Object>();
			LinkedList<Object> valList=new LinkedList<Object>();			
			for (Map.Entry<Integer, LinkedList<Variable>> entry : sortedVars.entrySet()) {				
				for (Variable v : entry.getValue()) {
					int lastKey=sortedVals.lastKey();
					int place=lastKey-entry.getKey();
					if (place<0) return false;					
					valList=sortedVals.get(lastKey);
					Object o=valList.get(0);
					varResults.put(v,o);
					valList.remove(0);
					if (valList.isEmpty()) sortedVals.remove(lastKey);				
					if (place>0) modifyCountMap(sortedVals, o, place);
				}
			}
			updateValuesMap(varResults, varValues);
		}		
		return true;
	}
	
	
	
	
	private <T> void increment(HashMap<T, Integer> m, T element) {
		Integer count=m.get(element);
		if (count==null) m.put(element,1);
		else m.put(element,count+1);	
	}
	
	private <T> void modifyCountMap(Map<Integer, LinkedList<T>> m, T val, Integer key) {
		LinkedList<T> list=m.get(key);
		if (list==null) {
			list=new LinkedList<T>();
			list.add(val);
			m.put(key, list);
		}
		else list.add(val);
	}
}
