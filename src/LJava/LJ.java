package LJava;
import static LJava.Utils.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;

public final class LJ {
	
	public final static Association _=new Association("_");
	public final static Association undefined=new Association("$undefined$");
	public final static Association none=new Association("$no_variable_value$");
	private static final HashMap<Integer, LinkedHashSet<Association>> LJavaRelationTable=new HashMap<Integer, LinkedHashSet<Association>>();

	
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
	
	
	private static boolean query(QueryParameter a, boolean cut) {
		VariableMap varValues=new VariableMap();
		if (!a.map(varValues,cut)) return false;
		return instantiate(varValues);		
	}
	
	
	protected static boolean conduct(Relation r, VariableMap varValues, Iterator<Association> i) {		
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
	
	
	@SuppressWarnings("rawtypes")
	public static Constraint constraint(Formula f, Object... args) {
		return new Constraint(f, args);
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
	
	
	protected static Iterator<Association> getLJIterator(int index) {
		LinkedHashSet<Association> set=LJavaRelationTable.get(index);
		if (set==null) return null;
		return set.iterator();
	}
}


/* to fix:
 * instantiate needs synchronized against Variable.instantiate.
 */



