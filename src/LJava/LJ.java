package LJava;
import static LJava.Utils.*;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashSet;


public class LJ {
	

	public final static Association _=new Association("_");

	public final static Association nil=new Association("$nil$");

	public static final Association none=new Association("$no_variable_value$");		
	
	public static final Association LJavaTrueRelation=new Association("$LJava_True_Relation$");

	public static final Association LJavaFalseRelation=new Association("$LJava_False_Relation$");
	
	private static HashMap<Integer, LinkedHashSet<Association>> LJavaRelationTable=new HashMap<Integer, LinkedHashSet<Association>>();
	
	
// Relates in order
	public static void associate(Association r) {
		LinkedHashSet<Association> set = LJavaRelationTable.get(r.argsLength());
		if (set==null) {
			set=new LinkedHashSet<Association>();
			set.add(r);
			LJavaRelationTable.put(r.argsLength(), set);
		}
		else set.add(r);
	}
	

	public static void relate(Object... args) {		
		Relation r=new Relation("#LJavaRelationTableEntry#", args);
		associate(r);
	}	

	
	public static void group(Object... args) {		
		Group r=new Group("#LJavaRelationTableEntry#", args);
		associate(r);
	}
	
			
// Exists in DB method.
	public static QueryResult exists(Relation r) {
		VariableValuesMap varValues=new VariableValuesMap();		
		if (exists(r, varValues)==FAILED) return FAILED;
		return instantiate(varValues);
	}
	
	
	private static QueryResult exists(Relation r, VariableValuesMap varValues){	
		Object[] rArgs=r.args();
		LinkedHashSet<Association> associationsSet = LJavaRelationTable.get(r.argsLength());		
		if (associationsSet!=null) {
			for (Association element : associationsSet)
				if (element.relationNameCompare(r))
					if (element.satisfy(rArgs, varValues))	return SUCCESS;			
		}
		associationsSet = LJavaRelationTable.get(-1);		
		if (associationsSet!=null) {
			for (Association element : associationsSet)
				if (element.relationNameCompare(r))
					if (element.satisfy(rArgs, varValues))	return SUCCESS;			
		}		
		return FAILED;
	}
	
	
	public static QueryResult exists(Object... args) {
		Relation r=new Relation("#query",args);
		return exists(r);
	}
		

//Conducts
	public static QueryResult conduct(Relation r){		
		VariableValuesMap varValues=new VariableValuesMap(); 
		if (conduct(r, varValues)==FAILED) return FAILED;
		return instantiate(varValues);
	}
	
	
	protected static QueryResult conduct(Relation r, VariableValuesMap varValues) {	
		Object[] rArgs=r.args();
		boolean gate=false;
		for (int i=0; i<rArgs.length; i++) {
			if (var(rArgs[i])) {
				gate=true;
				break;
			}
		}
		if (!gate) return exists(r,varValues);
		LinkedHashSet<Association> associationsSet = LJavaRelationTable.get(r.argsLength());		
		if (associationsSet!=null) {
			for (Association element : associationsSet) 
				if (element.relationNameCompare(r)) element.satisfy(rArgs, varValues);			
		}
		associationsSet = LJavaRelationTable.get(-1);		
		if (associationsSet!=null) {
			for (Association element : associationsSet) 
				if (element.relationNameCompare(r)) element.satisfy(rArgs, varValues);				
		}		
		if (varValues.map.isEmpty()) return FAILED;
		return SUCCESS;
	}
	
	
	public static QueryResult conduct(Object... args){
		Relation r=new Relation("#query",args);
		return conduct(r);		
	}
	

	public static boolean same(Object a, Object b) {		
		if ( (variable(a)) || (a instanceof Association) )
				return a.equals(b);
		return b.equals(a);
	}	
	
	
//Query and Logical operators
	public static QueryResult query(VariableValuesMap varValues){
		return instantiate(varValues);
	}
	

	public static VariableValuesMap and(QueryParameter a, QueryParameter b){
		VariableValuesMap x=a.map();
		if (x.isEmpty()) return x;
		VariableValuesMap y=b.map();
		if (y.isEmpty()) return y;
		ArrayList<Object> xValues;
		Variable t;
		for (Map.Entry<Variable, ArrayList<Object>> entry : y.map.entrySet()) {
			t=entry.getKey();
			xValues=x.map.get(t);
			if (xValues!=null) xValues.retainAll(entry.getValue());
		}		
		return x;
	}
	

	public static VariableValuesMap or(QueryParameter a, QueryParameter b){
		VariableValuesMap x=a.map();
		VariableValuesMap y=b.map();
		ArrayList<Object> xValues;
		Variable t;
		for (Map.Entry<Variable, ArrayList<Object>> entry : y.map.entrySet()) {
			t=entry.getKey();
			xValues=x.map.get(t);
			if (xValues==null) x.map.put(t, entry.getValue());
			else xValues.addAll(entry.getValue());
		}		
		return x;
	}	
	

	public static VariableValuesMap differ(QueryParameter a, QueryParameter b){
		VariableValuesMap x=a.map();
		if (x.isEmpty()) return x;
		VariableValuesMap y=b.map();
		ArrayList<Object> xValues;
		Variable t;
		for (Map.Entry<Variable, ArrayList<Object>> entry : y.map.entrySet()) {
			t=entry.getKey();
			xValues=x.map.get(t);
			if (xValues!=null) xValues.removeAll(entry.getValue());
		}		
		return x;	
	}
	
	
//Recognizing Variables
	public static boolean var(Object x) {
		if (variable(x)) {
			Variable y=(Variable) x;
			return var(y);
		}
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
	
	
	public static <P,R> VariableValuesMap where(Functor<P,R> f, Variable x, P...args) {
		VariableValuesMap m=new VariableValuesMap();
		if (var(x)) updateValuesMap(x, f.invoke(args), m);
		return m;
	}
	
	
	public static Object val(Object o) {
		if (variable(o)) {
			Variable v=(Variable) o;
			return v.get();
		}
		return o;
	}
	

//Instantiate all variables to their values according to the given map.
	private static QueryResult instantiate(VariableValuesMap varValues) {
        boolean answer=true;		
		for (Map.Entry<Variable, ArrayList<Object>> entry : varValues.map.entrySet()) {													
			answer=(entry.getKey().set(entry.getValue().toArray()) && answer);	
		}
		if (answer) return SUCCESS;
		return QueryResult.FAILED_INSTANTIATE;
	}

		
//Updates variables values map for this current running query.
	protected static void updateValuesMap(HashMap<Variable,Object> vars, VariableValuesMap varValues){		
		for (Map.Entry<Variable, Object> entry : vars.entrySet()) 
			updateValuesMap(entry.getKey(), entry.getValue(), varValues);			
	}
	
	
	protected static void updateValuesMap(Variable key, Object val, VariableValuesMap varValues) {
		ArrayList<Object> v;	
		v=varValues.map.get(key);
		if (v==null) {  
			v=new ArrayList<Object>();
			v.add(val);
			varValues.map.put(key, v);
		}
		else v.add(val);		
	}
	
	
	
	
}


/* TBD:
 * and,or, differ with functors.
 * where.
 * fix Group.satisfy
 * reverse functors: possible solutions - force definition, linear transmutations (linear algebra).
 */



