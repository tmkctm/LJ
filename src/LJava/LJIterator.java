package LJava;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;
import static LJava.LJ.*;


public final class LJIterator {
	Iterator<Association> i;
	boolean onFormulas;
	LazyGroup lazyGroup;
	
	
	public LJIterator(int index) {
		onFormulas=false;
		LinkedHashSet<Association> table=LJavaRelationTable.get(index);
		if (table==null) {
			table=LJavaRelationTable.get(-1);
			onFormulas=true;
		}
		i=(table==null) ? null : table.iterator();
		lazyGroup=null;
	}
	
	
	public boolean hasNext() {
		if (i==null) return false;
		return (i.hasNext() || lazyGroup!=null || (!onFormulas && LJavaRelationTable.get(-1)!=null));
	}
	
	
	public Association next() {
		if (lazyGroup!=null) return lazyGroup;
		if (i.hasNext()) return i.next();
		i=LJavaRelationTable.get(-1).iterator();
		onFormulas=true;
		return i.next();
	}
	
	
//Class for creating Lazy Group evaluation within LJ DataBase.	
	private class LazyGroup extends Group {
		
		VariableMap answer=new VariableMap();
		
		private class VarIterator {
			Iterator<Map.Entry<Object, Integer>> iterator;
			Variable var;
			int count;
			public VarIterator(Iterator<Map.Entry<Object, Integer>> i, Variable v, int c) {
				iterator=i;			var=v;		count=c;
			}
		}
		
		public LinkedList<VarIterator> iStack=new LinkedList<VarIterator>();
		public TreeMap<Variable, Integer> varsCount;
		public HashMap<Object, Integer> valsCount=new HashMap<Object, Integer>();
		
		public LazyGroup(String n, Object[] params, Object[] rArgs) {
			super(n,params);
			HashMap<Variable, Integer> rVarsCountMap=new HashMap<Variable, Integer>();
			HashMap<Object, Integer> rArgsCountMap=new HashMap<Object, Integer>();
			buildValsAndVarsCount(rArgs, rVarsCountMap, rArgsCountMap);
			elimination(rVarsCountMap, rArgsCountMap);
		}
		
		private void buildValsAndVarsCount(Object[] rArgs, HashMap<Variable, Integer> rVarsCountMap, HashMap<Object, Integer> rArgsCountMap) {
			for (Object element : rArgs) {
				if (var(element)) increment(rVarsCountMap,(Variable) element,1);
				else increment(rArgsCountMap, val(element),1);
			}			
		}
		
		private void elimination(HashMap<Variable, Integer> rVarsCountMap, HashMap<Object, Integer> rArgsCountMap) {
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
		protected boolean satisfy(Object[] rArgs, VariableMap varValues) {
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
		
		private void backtrack(VarIterator i) {
			Object key=answer.map.get(i.var).get(0);
			valsCount.put(key, valsCount.get(key)+i.count);
			varsCount.put(i.var, i.count);			
		}
	}	
	
}
