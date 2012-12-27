package LJava;

import java.util.Collection;
import java.util.HashMap;
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
	
			
	public static boolean exists(QueryParameter a) {
		return query(a,true);
	}
	
	
	public static boolean exists(QueryParameter a, LogicOperator op, QueryParameter b) {
		return exists(new Constraint(a,op,b));
	}
	
	
	public static boolean exists(Object... args) {
		Relation r=new Relation("#query",args);
		return exists(r);
	}
		

	public static boolean all(QueryParameter a) {
		return query(a,false);
	}
	
	
	public static boolean all(QueryParameter a, LogicOperator op, QueryParameter b) {
		return all(new Constraint(a,op,b));
	}
	
	
	public static boolean all(Object... args) {
		Relation r=new Relation("#query",args);
		return all(r);
	}
	
	
	@SuppressWarnings("rawtypes")
	public static Constraint condition(Formula f, Object... args) {
		return new Constraint(f,args);
	}
	
	
	private static boolean query(QueryParameter a, boolean cut) {
		VariableMap varValues=new VariableMap();
		if (!a.map(varValues,cut)) return false;
		return instantiate(varValues);
	}
	
	
	protected static boolean evaluate(Relation r, VariableMap varValues, LJIterator i) {		
		Association element = i.next();
		return (element.associationNameCompare(r) && element.satisfy(r.args, varValues));
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
	
	
	public static Relation relation(String n,Object... args) {
		return new Relation(n,args);
	}
	
	
	public static Relation relation(Object... args) {
		return new Relation("",args);
	}
	
	
	public static Object val(Object o) {
		if (variable(o)) return ((Variable) o).get();
		return o;
	}
	
	
	public static boolean same(Object a, Object b) {
		if ((variable(a)) || (a instanceof Association))
				return a.equals(b);
		if ((a instanceof Number) && (b instanceof Number))
				return ((Number) a).doubleValue()==((Number) b).doubleValue();
		return b.equals(a);
	}
	

	private static boolean instantiate(VariableMap varValues) {
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
	
	
	public static LJIterator iterate(int index) {
		return new LJIterator(index);
	}
	
}


/* to fix:
 * instantiate needs synchronized against Variable.instantiate.
 */



