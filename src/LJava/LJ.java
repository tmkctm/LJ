package LJava;
import static LJava.Utils.*;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;


public final class LJ {
	
	public final static Association _=new Association("_");
	public final static Association nil=new Association("$nil$");
	public static final Association none=new Association("$no_variable_value$");		
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
	
			
	public static QueryResult exists(Relation r) {		
		VariableValuesMap varValues=new VariableValuesMap();		
		if (conduct(r, varValues, true)==FAILED) return FAILED;
		return instantiate(varValues);
	}
	
	
	public static QueryResult exists(QueryParameter a, LogicOperator op, QueryParameter b){
		if (op==OR) return instantiate(or(a,b));
		if (op==AND) return instantiate(and(a,b));
		if (op==DIFFER) return instantiate(differ(a,b));
		return FAILED;
	}
	
	
	public static QueryResult exists(Object... args) {
		Relation r=new Relation("#query",args);
		return exists(r);
	}
		

	public static QueryResult all(Relation r){		
		VariableValuesMap varValues=new VariableValuesMap(); 
		if (conduct(r, varValues, false)==FAILED) return FAILED;
		return instantiate(varValues);
	}
	
	
	public static QueryResult all(QueryParameter a, LogicOperator op, QueryParameter b){
		if (op==OR) return instantiate(or(a,b));
		if (op==AND || op==WHERE) return instantiate(and(a,b));
		if (op==DIFFER) return instantiate(differ(a,b));
		return FAILED;
	}
	
	
	public static QueryResult all(VariableValuesMap m){
		return instantiate(m);
	}	
	
	
	public static QueryResult all(Object... args){
		Relation r=new Relation("#query",args);
		return all(r);		
	}
	
	
	public static  VariableValuesMap where(Variable x, Constraint c) {
		VariableValuesMap m = new VariableValuesMap();
		m.constraints.put(x,c);
		return m;
	}
	
	
	private static boolean searchOnIndexByName(int index, Relation r) {
		LinkedHashSet<Association> associationsSet = LJavaRelationTable.get(index);		
		if (associationsSet!=null) {
			for (Association element : associationsSet)
				if (element.associationNameCompare(r))	return true;			
		}
		return false;
	}
	
	
	private static QueryResult nameQuerying(Relation r) {
		if (searchOnIndexByName(0, r)) return SUCCESS;
		if (searchOnIndexByName(-1, r)) return SUCCESS;
		return FAILED;
	}
	
	
	private static boolean searchOnIndex(int index, Relation r, Object[] rArgs, VariableValuesMap varValues, boolean cut) {
		LinkedHashSet<Association> associationsSet = LJavaRelationTable.get(index);		
		if (associationsSet!=null) {
			for (Association element : associationsSet) 
				if (element.associationNameCompare(r) && element.satisfy(rArgs, varValues))
						if (cut) return true;
		}
		if (varValues.map.isEmpty()) return false;
		return true;	
	}
	
	
	protected static QueryResult conduct(Relation r, VariableValuesMap varValues, boolean cutFlag) {	
		Object[] rArgs=r.args();
		if (rArgs.length==0) return nameQuerying(r);
		if (!cutFlag) {
			for (int i=0; i<rArgs.length; i++)
				if (var(rArgs[i])) {	cutFlag=true;	break;	}
			cutFlag=(!cutFlag);
		}
		if (searchOnIndex(rArgs.length, r, rArgs, varValues, cutFlag))
			if (cutFlag) return SUCCESS;
		if (searchOnIndex(-1, r, rArgs, varValues, cutFlag))
			if (cutFlag) return SUCCESS;
		if (varValues.map.isEmpty()) return FAILED;
		return SUCCESS;		
	}
	
	
	public static VariableValuesMap and(QueryParameter a, QueryParameter b){
		//TBD 
		return null;
	}
	

	public static VariableValuesMap or(QueryParameter a, QueryParameter b){
		//TBD
		return null;
	}	
	

	public static VariableValuesMap differ(QueryParameter a, QueryParameter b){
		//TBD
		return null;	
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
		if ((variable(a)) || (a instanceof Association) )
				return a.equals(b);
		if ((a instanceof Number) && (b instanceof Number))
				return ((Number) a).doubleValue()==((Number) b).doubleValue();
		return b.equals(a);
	}	
	

	private static QueryResult instantiate(VariableValuesMap varValues) {
        boolean answer=true;
        Set<Variable> varset = varValues.map.keySet();
        varset.addAll(varValues.constraints.keySet());
		for (Variable v : varset)													
			answer=(v.instantiate(varValues.map.get(v).toArray(), varValues.constraints.get(v), varValues.valsByConstrains.get(v)) && answer);	
		if (answer) return SUCCESS;
		return QueryResult.FAILED_INSTANTIATE;
	}

		
	protected static void updateValuesMap(HashMap<Variable,Object> vars, VariableValuesMap varValues){		
		for (Map.Entry<Variable, Object> entry : vars.entrySet()) 
			updateValuesMap(entry.getKey(), entry.getValue(), varValues);			
	}
	
	
	protected static void updateValuesMap(Variable key, Object val, VariableValuesMap varValues) {
		addTo(varValues.map, key, val, ArrayList.class);
	}	
}


/* Future Plan:
 * Logical operators: for exists, with functors, logically correct AND, smarter!
 * Atomitize the line in Variable.consistWith.
 * Utils Functors: sum, multi, mod, sqr, sqrt, pow, avg.
 * reverse functors: By receiving an array of functors, one for each param. returning the relation none if no functor to a certain index.
 */



