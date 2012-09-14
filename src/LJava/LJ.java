package LJava;
import static LJava.Utils.*;

import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashSet;


public class LJ {
	

	public final static Relation _=new Relation("_");

	public final static Relation nil=new Relation("$nil$");

	public static final Relation none=new Relation("$no_variable_value$");		
	
	public static Relation LJavaTrueRelation=new Relation("$true$");

	public static Relation LJavaFalseRelation=new Relation("$false$");
	
	private static HashMap<Integer, LinkedHashSet<Relation>> LJavaRelationTable=new HashMap<Integer, LinkedHashSet<Relation>>();
	
	
// Relates in order
	public static void nil(Relation r) {
		LinkedHashSet<Relation> set = LJavaRelationTable.get(r.argsLength());
		if (set==null) {
			set=new LinkedHashSet<Relation>();
			set.add(r);
			LJavaRelationTable.put(r.argsLength(), set);
		}
		else set.add(r);
	}
	

	public static void nil(Object... args) {		
		Relation r=new Relation("#LJavaRelationTableEntry#", args);
		nil(r);
	}	

	
// Relates for sets
	public static void group(Group r) {
		Group r2=new Group(r.name(), r.args());
		nil(r2);
	}	
	

	public static void group(Object... args) {		
		Group r=new Group("#LJavaRelationTableEntry#", args);
		group(r);
	}		
	
			
// Exists in DB method.
	public static QueryResult exists(Relation r) {
		VariableValuesMap varValues=new VariableValuesMap(); 	
		if (exists(r, varValues)==FAILED) return FAILED;
		return instantiate(varValues);
	}
	
	
	public static QueryResult exists(Relation r, VariableValuesMap varValues){	
		LinkedHashSet<Relation> relationSet = LJavaRelationTable.get(r.args().length);
		if (relationSet!=null) {
			for (Relation element : relationSet)
				if (satisfy(r, element, varValues))	return SUCCESS;			
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
	
	
	public static QueryResult conduct(Relation r, VariableValuesMap varValues) {	
		LinkedHashSet<Relation> relationSet = LJavaRelationTable.get(r.args().length);
		if (relationSet!=null) {
			for (Relation element : relationSet)
				satisfy(r, element, varValues);			
		}
		if (varValues.map.isEmpty()) return FAILED;
		return SUCCESS;
	}
	
	
	public static QueryResult conduct(Object... args){
		Relation r=new Relation("#query",args);
		return conduct(r);		
	}
	

	public static boolean same(Object a, Object b) {		
		if ( (variable(a)) || (a instanceof Relation) )
				return a.equals(b);
		return b.equals(a);
	}	
	
	
//Query and Logical operators
	public static QueryResult query(VariableValuesMap varValues){
		return instantiate(varValues);
	}
	

	public static VariableValuesMap map(QueryParameter x){
		if (x instanceof VariableValuesMap) return (VariableValuesMap) x;
		VariableValuesMap m=new VariableValuesMap();
		Relation r=(Relation) x;
		conduct(r,m);		
		return m;
	}

	
	public static VariableValuesMap and(QueryParameter a, QueryParameter b){
		VariableValuesMap x=map(a);
		if (x.isEmpty()) return x;
		VariableValuesMap y=map(b);
		if (y.isEmpty()) return y;
		LinkedHashSet<Object> xValues;
		Variable t;
		for (Map.Entry<Variable, LinkedHashSet<Object>> entry : y.map.entrySet()) {
			t=entry.getKey();
			xValues=x.map.get(t);
			if (xValues!=null) xValues.retainAll(entry.getValue());
		}		
		return x;
	}
	

	public static VariableValuesMap or(QueryParameter a, QueryParameter b){
		VariableValuesMap x=map(a);
		VariableValuesMap y=map(b);
		LinkedHashSet<Object> xValues;
		Variable t;
		for (Map.Entry<Variable, LinkedHashSet<Object>> entry : y.map.entrySet()) {
			t=entry.getKey();
			xValues=x.map.get(t);
			if (xValues==null) x.map.put(t, entry.getValue());
			else xValues.addAll(entry.getValue());
		}		
		return x;
	}	
	

	public static VariableValuesMap differ(QueryParameter a, QueryParameter b){
		VariableValuesMap x=map(a);
		if (x.isEmpty()) return x;
		VariableValuesMap y=map(b);
		LinkedHashSet<Object> xValues;
		Variable t;
		for (Map.Entry<Variable, LinkedHashSet<Object>> entry : y.map.entrySet()) {
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
	
	
//Returns a relation when taking the first parameter for naming.
	public static Relation relate(String n,Object... args) {
		return new Relation(n,args);
	}
	
	
	public static Relation relate(Object... args) {
		return new Relation("",args);
	}
	

	public static Relation nil() {
		return nil;
	}
	
	

//Instantiate all variables to their values according to the given map.
	private static QueryResult instantiate(VariableValuesMap varValues) {
        boolean answer=true;		
		for (Map.Entry<Variable, LinkedHashSet<Object>> entry : varValues.map.entrySet()) {													
			answer=(entry.getKey().set(entry.getValue().toArray()) && answer);	
		}
		if (answer) return SUCCESS;
		return QueryResult.FAILED_INSTANTIATE;
	}

	
	

//The main part where the logical engine works.

//Checks satisfaction of two relations.	
	private static boolean satisfy(Relation r, Relation candidate, VariableValuesMap varValues){
		if (!candidate.relationNameCompare(r)) return false;
		return candidate.satisfy(r,varValues);
	}
	
	
//Updates variables values map for this current running query.
	protected static void updateValuesMap(HashMap<Variable,Object> vars, VariableValuesMap varValues){		
		LinkedHashSet<Object> v;	
		Variable t;
		for (Map.Entry<Variable, Object> entry : vars.entrySet()) {
			t=entry.getKey();
			v=varValues.map.get(t);
			if (v==null) {  
				v=new LinkedHashSet<Object>();
				v.add(entry.getValue());
				varValues.map.put(t, v);
			}
			else v.add(entry.getValue());
		}		
	}
	
	
	
	
}


/* TBD: 
 * exists for set relations.
 * figure out: is replaceVariables only for Relation or for Group as well?
 * exists using nil.
 * And then ... .FUNCTORS.
 * reverse functors: possible solutions - force definition, linear transmutations (linear algebra).
 */



