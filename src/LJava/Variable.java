package LJava;
import static LJava.LJ.*;
import static LJava.Utils.LJFalse;
import static LJava.Utils.LJTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;
 

/**
 * @author Tzali Maimon
 * The variables in LJ are represented by this class. <p>
 * This class isn't totally immutable. A variable turns immutable only after first instantiation.<p>
 */
public class Variable {

	private Object[] value=new Object[0];
	private boolean isVar=true;
	private AtomicBoolean inSet=new AtomicBoolean(false);
	private HashSet<Variable> looksAt= new HashSet<Variable>();
	private Constraint constraint=new Constraint(LJFalse);
	private final String innerID;
	
	private static ReentrantLock lockKey = new ReentrantLock();
	

	/**
	 * creates a variable with the name "LJ_Variable";
	 */
	public Variable() {
		innerID="LJ_Variable";
	}
	
	
	/**
	 * creates a variable with the given name.
	 * @param id a name
	 */
	public Variable(String id) {	
		innerID=id;
	}
	
	
	public final boolean equals(Object x) {		
		x=val(x);
		Object o=this.get(0);		
		if (o==this) return (o==x);
		return same(o,x);					
	}


//Properties		
	public final String toString(){
		if (isVar()) return "$"+innerID+"$";
		if (this.noValue()) return none.toString();
		StringBuilder s= new StringBuilder("[");
		if (value.length>0) {
			for (int i=0; i<value.length-1; i++) s.append(value[i]+",");
			s.append(value[value.length-1].toString());
		}
		s.append("]");
		if (constraint.asRelation()!=LJFalse) s.append(" OR "+constraint.toString());
		return s.toString();
	}
	
	
	/**
	 * @return the name of the variable
	 */
	public final String getID() {
		return innerID;
	}

	
	/**
	 * @param i index
	 * @return the i-th value of this variable.
	 */
	public final Object get(int i){
		if (isVar()) return this;
		if (noValue()) return none;
		return flat(i);
	}


	/**
	 * @return the constraint of the variable. A constraint of a variable serves as OR. meaning that if a vairable has a value of 2 and a constraint of "greater than 4" then 2 is a value, 10 is a value, 50 is a value but not 3.
	 */
	public final Constraint getConstraint() {
		return constraint.replaceVariable(this, new Variable());
	}


	/**
	 * @param o a value
	 * @return true if the value is one of the variable's values
	 */
	public final boolean contains(Object o) {
		for (Object v : value)
			if (variable(v) && ((Variable) v).contains(o)) return true;
			else if (same(v,o)) return true;
		return constraint.satisfy(this, o);
	}


	/**
	 * @return all values of this variable as an Object[]
	 */
	public final Object[] getValues() {
		if (isVar()) return new Object[]{undefined};
		else if (noValue()) return new Object[]{none};
		Object[] result = new Object[value.length];
		for (int i=0; i<value.length; i++)  result[i]=val(value[i]);
		return result;
	}


	/**
	 * @return all values of this variable as an HashSet of Objects 
	 */
	public final HashSet<Object> getValuesSet(){
		HashSet<Object> valueSet = new HashSet<Object>();
		for (int i=0; i<value.length; i++)	valueSet.add(val(value[i]));
		return valueSet;
	}


	/**
	 * @param x a variable
	 * @return true if this variable and x has the same values WITH THE SAME ORDER.
	 */
	public final boolean equalValues(Variable x) {
		if (x.value.length!=value.length) return false;
		for (int i=0; i<value.length; i++)
			if (!same(value[i],x.value[i])) return false;
		return true;
	}
	
	
	/**
	 * @param x a variable
	 * @return true if this variable has the same values as x.
	 */
	public final boolean equalValuesSet(Variable x) {
		HashSet<Object> thisSet = getValuesSet();
		HashSet<Object> xSet = x.getValuesSet();
		for (Object o : thisSet) if (!xSet.remove(o)) return false;
		if (!xSet.isEmpty()) return false;
		return true;
	}


	/**
	 * Turns the mutable Variable that has no value to an Immutable Variable that has meaning.
	 * @param vals values
	 * @param where limit on values (acts as AND)
	 * @param valByConstraint the variable constraint (acts as OR)
	 * @return true if successful
	 */
	public final boolean instantiate(List<Object> vals, Constraint where, Constraint valByConstraint){
		if (vals==null) vals = new LinkedList<Object>();
		return instantiate(vals.toArray(), where, valByConstraint);
	}
	
	/**
	 * @param args
	 * @return instantiate(args, null, null);
	 */
	public final boolean set(Object... args) {
		return instantiate(args, null, null);
	}
	
	/**
	 * Turns the mutable Variable that has no value to an Immutable Variable that has meaning.
	 * @param vals values
	 * @param where limit on values (acts as AND)
	 * @param valByConstraint the variable constraint (acts as OR)
	 * @return true if successful
	 */
	public final boolean instantiate(Object[] vals, Constraint where, Constraint valByConstraint){
		lockKey.lock();
		if (isVar && inSet.compareAndSet(false,true)){
			lockKey.unlock();
			if (valByConstraint==null) valByConstraint=new Constraint(LJFalse);			
			if (vals!=null && vals.length>0) {
				ArrayList<Object> correct= new ArrayList<Object>();
				if (where==null) where=new Constraint(LJTrue);
				for (int i=0; i<vals.length; i++) {
					if (variable(vals[i]) && !this.consistWith((Variable) vals[i])) {
						correct.add(undefined);
						continue;
					}
					if (where.satisfy(this, vals[i])) correct.add(vals[i]);
				}			
				if (!correct.isEmpty()) this.value=correct.toArray();				
			}
			this.constraint=valByConstraint;
			isVar=false;
			return true;						
		}
		lockKey.unlock();
		return false;
	}

	
//Identifying characteristics of this Variable
	/**
	 * @return true if this variable hasn't been instantiated yet
	 */
	public final boolean isVar(){
		return isVar;
	}


	/**
	 * @return true if this variable is instantiated but holds no values
	 */
	public final boolean noValue(){
		if (isVar()) return false;
		return (value.length==0 && constraint.asRelation()==LJFalse);
	}


	/**
	 * @return true if get(0) is primitive
	 */
	public final boolean isPrimitiveType(){
		return (get(0).getClass().isPrimitive());
	}


	/**
	 * @return true if this variable has a constraint (that acts as OR)
	 */
	public final boolean isConstraint(){
		if ((!this.isVar()) && (!noValue()))
			return (value.length>1 || constraint.asRelation()!=LJFalse);
		return false;
	}


	/**
	 * @return true if get(0) is Association
	 */
	public final boolean isAssociation(){
		return (get(0) instanceof Association);
	}


	/**
	 * @return true if get(0) is String
	 */
	public final boolean isString(){
		return (get(0) instanceof String);
	}


	/**
	 * @return true if get(0) is Integer
	 */
	public final boolean isInteger(){
		return (get(0) instanceof Integer);
	}


	/**
	 * @return true if get(0) is Double
	 */
	public final boolean isDouble(){
		return (get(0) instanceof Double);	
	}


	/**
	 * @return true if get(0) is Float
	 */
	public final boolean isFloat(){
		return (get(0) instanceof Float);	
	}


	/**
	 * @param c a class type
	 * @return true if get(0) is of type class c
	 */
	public final boolean ofClass(Class<?> c){						
		return (get(0).getClass().equals(c));
	}


	/**
	 * @return true if get(0) is Number
	 */
	public final boolean isNumber(){
		return (get(0) instanceof Number);		
	}		


//Returns the variable value.
	private final Object flat(int i){
		if (value.length>i) return val(value[i]);
		return undefined;
	}


//Tests all required characteristics for this Variable to be of a single value
	/**
	 * @return true if this variable has only one value
	 */
	public final boolean singleValue(){
		if (isVar() || noValue() || isConstraint()) return false;
		return true;
	}


//A consistency check for this Variable against another Variable.				
	private final boolean consistWith(Variable b) {
		if (b.isVar()) {
			lockKey.lock();
			if (b.inSet.get()) 	{ lockKey.unlock();   return false;   }
			looksAt.add(b);		lockKey.unlock();
			return true;
		}		
		for (Variable element : b.looksAt)
			if (!this.consistWith(element)) return false;
		return true;
	}

}

