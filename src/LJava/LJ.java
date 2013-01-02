package LJava;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;


public final class LJ {
	
	public final static Association _=new Association("_");
	public final static Association undefined=new Association("$undefined$");
	public final static Association none=new Association("$no_variable_value$");
	protected static final HashMap<Integer, LinkedHashSet<Association>> LJavaRelationTable=new HashMap<Integer, LinkedHashSet<Association>>();
	
	static public final boolean CUT=true;
	static public final LogicOperator OR=LogicOperator.OR;
	static public final LogicOperator AND=LogicOperator.AND;
	static public final LogicOperator DIFFER=LogicOperator.DIFFER;
	static public final LogicOperator WHERE=LogicOperator.WHERE;
	static public enum  LogicOperator{
		OR, AND, DIFFER , NONE, WHERE  }	
	
	protected static final LJIterator emptyIterator=iterate(-2);
	protected static final double DoubleTolerance=0.00000000000001;
	
	public static void associate(Association r) {
		addTo(LJavaRelationTable, r.argsLength(), r, LinkedHashSet.class);
	}
	

	public static void relate(Object... args) {		
		Relation r=new Relation("#LJavaRelationTableEntry#", args);
		associate(r);
	}	

	
	public static void group(Object... args) {		
		Group r=new Group("#LJavaRelationTableEntry#", args);
		associate(r);
	}
	
	
	public static Lazy lazy(Group g, Object... args) {
		return new Lazy(g,args);
	}
	
			
	public static boolean exists(QueryParameter a) {
		return e(a);
	}
	
	
	public static boolean e(QueryParameter a) {
		return query(a,true); 
	}
	
	
	public static boolean exists(QueryParameter a, LogicOperator op, QueryParameter b) {
		return e(a,op,b);
	}
	
	
	public static boolean e(QueryParameter a, LogicOperator op, QueryParameter b) {
		return e(new Constraint(a,op,b));
	}
	
	
	public static boolean exists(Object... args) {
		return e(args);
	}
	
	
	public static boolean e(Object... args) {
		Relation r=new Relation("#query",args);
		return e(r);
	}
		

	public static boolean all(QueryParameter a) {
		return a(a);
	}
	
	
	public static Lazy lazy(Constraint a) {
		return new Lazy(a);
	}
	
	
	public static boolean a(QueryParameter a) {
		return query(a,false);
	}
	
	
	public static boolean all(QueryParameter a, LogicOperator op, QueryParameter b) {
		return a(a,op,b);
	}
	
	
	public static Lazy lazy(QueryParameter a, LogicOperator op, QueryParameter b) {
		return lazy(new Constraint(a,op,b));
	}
	
	
	public static boolean a(QueryParameter a, LogicOperator op, QueryParameter b) {
		return a(new Constraint(a,op,b));
	}
	
	
	public static boolean all(Object... args) {
		return a(args);
	}
	
	
	public static boolean a(Object... args) {
		Relation r=new Relation("#query",args);
		return a(r);
	}
	
	
	@SuppressWarnings("rawtypes")
	public static Constraint c(Formula f, Object... args) {
		return new Constraint(f,args);
	}
	
	
	public static Constraint c(QueryParameter l, LogicOperator lp, QueryParameter r) {
		return new Constraint(l,lp,r);
	}
	
		
	@SuppressWarnings("rawtypes")
	public static Constraint condition(Formula f, Object... args) {
		return c(f,args);
	}

	
	public static Constraint condition(QueryParameter l, LogicOperator lp, QueryParameter r) {
		return c(l,lp,r);
	}

	
	private static boolean query(QueryParameter a, boolean cut) {
		VariableMap varValues=new VariableMap();
		if (!a.map(varValues,cut)) return false;
		return instantiate(varValues);
	}
	
	
	protected static boolean evaluate(Relation r, VariableMap varValues, LJIterator i) {		
		Association element = i.next();
		if (element instanceof Group) {
			element=new Lazy((Group) element, r.args);
			if (((Lazy)element).isEmpty()) return true;
			i.lazyGroup=element;
		}
		if (!(element.associationNameCompare(r)	&& element.satisfy(r.args, varValues))) {
			i.lazyGroup=none;
			return false;
		}
		return true;
	}
	
	
	public static Constraint and(QueryParameter a, QueryParameter b) {
		return new Constraint(a,AND,b);
	}
	

	public static Constraint or(QueryParameter a, QueryParameter b) {
		return new Constraint(a,OR,b);
	}
	

	public static Constraint differ(QueryParameter a, QueryParameter b) {
		return new Constraint(a,DIFFER,b);
	}
	

	public static Constraint where(QueryParameter a, QueryParameter b) {
		return new Constraint(a,WHERE,b);
	}
	
	
	public static boolean isSet(Variable[] vs, Object[] os) {
		if (vs==null || os==null) return false;
		if (vs.length!=os.length) return false;
		if (vs.length==0) return true;
		for (int i=0; i<vs[0].getValues().length; i++) {
			boolean is=true;
			for (int j=0; j<os.length; j++) 
				if (!same(vs[j].get(i), os[j])) {
					is=false;
					break;
				}
			if (is) return true;
		}
		return false;
	}
	
	
	public static boolean isSet(Object... args) {
		if (args.length%2==1) return false;
		Variable[] vs=new Variable[args.length/2];
		Object[] os=new Object[args.length/2];
		int i=-2;
		while ((i=i+2)<args.length) {
			if (!variable(args[i])) return false;
			vs[i/2]=(Variable) args[i];
			os[i/2]=args[i+1];
		}
		return isSet(vs,os);		
	}
	
	
	public static final boolean variable(Object x) {
		return (x instanceof Variable);
	}
	
	
	public static boolean var(Object x) {
		if (variable(x)) return var(((Variable) x));
		return false;
	}
	

	public static boolean var(Variable x) {
		return x.isVar();
	}
	
	
	public static Variable var() {
		return new Variable();
	}
	
	
	public static Variable[] varArray(String name, int size) {
		Variable[] arr=new Variable[size];
		for (int i=0; i<size; i++) arr[i]=new Variable(name+i);
		return arr;
	}
	
	
	public static Variable[] varArray(int size) {
		return varArray("LJ_Variable", size);
	}
	
	
	public static Relation r(String n,Object... args) {
		return new Relation(n,args);
	}
	
	
	public static Relation relation(String n,Object... args) {
		return r(n,args);
	}
	

	@SuppressWarnings("rawtypes")
	public static Relation r(Formula f,Object... args) {
		return new Relation(f.name,args);
	}
	
	
	@SuppressWarnings("rawtypes")
	public static Relation relation(Formula f,Object... args) {
		return r(f,args);
	}

	
	
	public static Relation r(Object... args) {
		return new Relation("",args);
	}
	
	
	public static Relation relation(Object... args) {
		return r(args);
	}
	
	
	public static Object val(Object o) {
		if (variable(o)) return ((Variable) o).get(0);
		return o;
	}
	
	
	public static boolean same(Object a, Object b) {
		if ((variable(a)) || (a instanceof Association))
				return a.equals(b);
		if ((a instanceof Number) && (b instanceof Number))
				return (Math.abs(((Number) a).doubleValue()-((Number) b).doubleValue())<DoubleTolerance);
		return b.equals(a);
	}
	
	
	@SuppressWarnings("rawtypes")
	public static Object[] deepInvoke(Object[] arr, Formula f) {
		Object[] a=new Object[arr.length];
		for (int i=0; i<arr.length; i++) a[i]=f.invoke(arr[i]);
		return a;
	}
	

	public static boolean instantiate(VariableMap varValues) {
        boolean answer=true;
		for (Variable v : varValues.getVars())
			answer=(v.instantiate(varValues.map.get(v), null, varValues.constraints.get(v)) && answer);	
		return answer;
	}
	

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected static <K,V> void addTo(Map map, K key, V val, Class<?> type) {
		Collection collection=(Collection) map.get(key);
		if (collection==null) {
			try{
				collection=(Collection) type.newInstance();
				map.put(key, collection);
			}catch (Exception e){}
		}
		collection.add(val);
	}	
	
	
	protected static <T> void increment(Map<T, Integer> m, T element, int delta) {
		Integer count=m.get(element);
		if (count==null) count=0;
		m.put(element,count+delta);	
	}		
	
	
	protected static String string(Object o) {
		if (variable(o)) return "[$"+((Variable) o).getID()+"$]";
		return o.toString();
	}
	
	
	protected static LJIterator iterate(int index) {
		return new LJ().new LJIterator(index);
	}
	
	
//An inner LJ iterator.	
	protected final class LJIterator {
		Iterator<Association> i;
		boolean onFormulas;
		Association lazyGroup;
		
		
		public LJIterator(int index) {
			onFormulas=false;
			lazyGroup=none;
			LinkedHashSet<Association> table=LJavaRelationTable.get(index);
			if (table==null) {
				table=LJavaRelationTable.get(-1);
				onFormulas=true;
				i=(table==null) ? null : table.iterator();
			}
			else i=table.iterator();
		}
		
		
		public boolean hasNext() {
			if (i==null) return false;
			return (i.hasNext() || lazyGroup!=none || (!onFormulas && LJavaRelationTable.get(-1)!=null));
		}
		
		
		public Association next() {
			if (lazyGroup!=none) return lazyGroup;
			if (i.hasNext()) return i.next();
			i=LJavaRelationTable.get(-1).iterator();
			onFormulas=true;
			return i.next();
		}
		
		public void setLazyGroup(Lazy group) {
			lazyGroup=group;
		}
	}	
	
}


/* to fix:
 * instantiate needs synchronized against Variable.instantiate.
 */



