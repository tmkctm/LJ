package LJava;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.TreeMap;

import scala.actors.threadpool.Arrays;
import static LJava.LJ.*;


public class Group extends Association {
	
	protected final Map<Object, Integer> argsMap;
	
	public Group(String n, Object... params) {
		super(n, params);
		HashMap<Object, Integer> map=new HashMap<Object, Integer>();
		if (params!=null)
			for (Object element : params) increment(map, element,1);		
		argsMap=Collections.unmodifiableMap(map);
	}
	
	
	public Group(Object... params) {
		this("#LJavaGroupTableEntry",params);
	}
	
	
	@Override
	public boolean isGroup() {
		return true;
	}
	
	
	@Override
	protected boolean satisfy(Object[] rArgs, VariableMap varValues){	
		return false;
	}
	
	
	protected LazyGroup goLazy(Object[] rArgs) {
		return new LazyGroup(this, rArgs);
	}
	
	
	
//Lazy Group	
	private class LazyGroup extends Association implements Lazy<VariableMap> {
		private class VarIterator {
			ListIterator<Map.Entry<Object, Integer>> iterator;
			Variable var;
			int count;
			public VarIterator(ListIterator<Map.Entry<Object, Integer>> i, Variable v, int c) {
				iterator=i;			var=v;		count=c;
			}
		}			
	
		private final Group g;
		private final VariableMap answer=new VariableMap();
		private final LinkedList<VarIterator> iStack=new LinkedList<VarIterator>();
		private TreeMap<Variable, Integer> varsCount=new TreeMap<Variable, Integer>();
		private HashMap<Object, Integer> valsCount=new HashMap<Object, Integer>();
		private boolean noVars=false;
		private boolean noArgs=false;
		
		@SuppressWarnings("unchecked")
		public LazyGroup(Group group, Object[] rArgs) {
			super(group.name, group.args);
			g=group;
			HashMap<Variable, Integer> rVarsCountMap=new HashMap<Variable, Integer>();
			HashMap<Object, Integer> rArgsCountMap=new HashMap<Object, Integer>();
			for (Object element : rArgs) {
				if (var(element)) increment(rVarsCountMap,(Variable) element,1);
				else increment(rArgsCountMap, val(element),1);
			}
			Integer count=0;
			for (Map.Entry<Object, Integer> entry : group.argsMap.entrySet()) { //Differing amounts between group's map and r's map
				Object keyVal=val(entry.getKey());
				count=(var(keyVal))? rVarsCountMap.remove(keyVal) : rArgsCountMap.remove(keyVal);
				count=(count==null)? -entry.getValue() : count-entry.getValue();
				if (count>0) {
					if (!var(keyVal)) return;
					rVarsCountMap.put((Variable) keyVal, count);
				}
				else if (count<0) valsCount.put(keyVal, -count); 
			}
			noVars=rVarsCountMap.isEmpty();
			noArgs=valsCount.isEmpty();
			if (rArgsCountMap.isEmpty() && !rVarsCountMap.isEmpty()) {				
				varsCount = new TreeMap<Variable, Integer>(new MapComparatorByValue<Variable>(rVarsCountMap));
				varsCount.putAll(rVarsCountMap);
				iStack.push(new VarIterator(Arrays.asList(valsCount.entrySet().toArray()).listIterator(), varsCount.firstKey(), varsCount.get(varsCount.firstKey())));
			}			
		}
		
		public LazyGroup(Group group) {
			super(group.name, group.args);
			g=group;
		}
		
		@Override
		public boolean satisfy(Object[] rArgs, VariableMap varValues) {
			if (noVars && noArgs) return true;
			return lz(varValues);
		}
		
		@SuppressWarnings("unchecked")
		public synchronized final boolean lz(VariableMap varValues) {
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
					
					i=new VarIterator(Arrays.asList(valsCount.entrySet().toArray()).listIterator(), varsCount.firstKey(), varsCount.remove(varsCount.firstKey()));
				}
			}
			return false;
		}
		
		@Override
		public final VariableMap lz() {
			VariableMap m=new VariableMap();
			lz(m);
			return m;
		}
		
		@Override
		public final VariableMap current() {
			return new VariableMap().add(answer);
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
		
		@Override
		public Variable[] getVars() {
			return (Variable[]) varsCount.keySet().toArray();
		}
		
		@Override
		public boolean noVars() {
			return noVars;
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public synchronized void resetLazy() {
			while (!iStack.isEmpty()) {
				VarIterator i=iStack.pop();
				if (varsCount.get(i.var)==null) backtrack(i);				
			}
			if (!varsCount.isEmpty()) iStack.push(new VarIterator(Arrays.asList(valsCount.entrySet().toArray()).listIterator(), varsCount.firstKey(), varsCount.get(varsCount.firstKey())));
		}
		
		@Override
		public Object base() {
			return g;
		}
		
		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		public synchronized Lazy<VariableMap> branch() {
			LazyGroup lg=new LazyGroup(g);
			lg.answer.add(answer);
			lg.varsCount = new TreeMap<Variable, Integer>(new MapComparatorByValue<Variable>(((MapComparatorByValue<Variable>) varsCount.comparator()).sourceMap));
			lg.varsCount.putAll(varsCount);
			lg.valsCount.putAll(valsCount);
			List l=Arrays.asList(lg.valsCount.entrySet().toArray());
			for (VarIterator i: iStack) 
				lg.iStack.add(new VarIterator(l.listIterator(i.iterator.nextIndex()), i.var, i.count));
			lg.noVars=noVars;
			lg.noArgs=noArgs;			
			return lg;
		}
		
		@Override
		public boolean isLazy() {
			return true;
		}
	}
		
	
	
	//Comparator for maps according to it's values and not keys	
	private class MapComparatorByValue<T> implements Comparator<T> {	
		public Map<T,Integer> sourceMap;		
		public MapComparatorByValue(Map<T,Integer> m) {
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


	

