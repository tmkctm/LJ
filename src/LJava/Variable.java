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
 

public class Variable {

	private Object[] value=new Object[0];
	private boolean isVar=true;
	private AtomicBoolean inSet=new AtomicBoolean(false);
	private HashSet<Variable> looksAt= new HashSet<Variable>();
	private Constraint constraint=new Constraint(LJFalse);
	private final String innerID;
	
	private static ReentrantLock lockKey = new ReentrantLock();
	

	public Variable() {
		innerID="LJ_Variable";
	}
	
	
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
	
	
	public final String getID() {
		return innerID;
	}

	
	public final Object get(int i){
		if (isVar()) return this;
		if (noValue()) return none;
		return flat(i);
	}


	public final Constraint getConstraint() {
		return constraint.replaceVariable(this, new Variable());
	}


	public final boolean contains(Object o) {
		for (Object v : value)
			if (variable(v) && ((Variable) v).contains(o)) return true;
			else if (same(v,o)) return true;
		return constraint.satisfy(this, o);
	}


	public final Object[] getValues() {
		if (isVar()) return new Object[]{undefined};
		else if (noValue()) return new Object[]{none};
		Object[] result = new Object[value.length];
		for (int i=0; i<value.length; i++)  result[i]=val(value[i]);
		return result;
	}


	public final HashSet<Object> getValuesSet(){
		HashSet<Object> valueSet = new HashSet<Object>();
		for (int i=0; i<value.length; i++)	valueSet.add(val(value[i]));
		return valueSet;
	}


	public final boolean equalValues(Variable x) {
		if (x.value.length!=value.length) return false;
		for (int i=0; i<value.length; i++)
			if (!same(value[i],x.value[i])) return false;
		return true;
	}
	
	
	public final boolean equalValuesSet(Variable x) {
		HashSet<Object> thisSet = getValuesSet();
		HashSet<Object> xSet = x.getValuesSet();
		for (Object o : thisSet) if (!xSet.remove(o)) return false;
		if (!xSet.isEmpty()) return false;
		return true;
	}


//Turns the mutable Variable that has no value to an Immutable Variable that has meaning.
	public final boolean instantiate(List<Object> vals, Constraint where, Constraint valByConstraint){
		if (vals==null) vals = new LinkedList<Object>();
		return instantiate(vals.toArray(), where, valByConstraint);
	}
	
	public final boolean set(Object... args) {
		return instantiate(args, null, null);
	}
	
	public final boolean instantiate(Object[] vals, Constraint where, Constraint valByConstraint){
		//debug("Entered instantiate on variable: "+this);
		lockKey.lock();
		if (isVar && inSet.compareAndSet(false,true)){
			lockKey.unlock();
			//debug("Instantiating "+this+" with values: "+string(vals)+ " where "+where+" or "+valByConstraint);
			if (valByConstraint==null) valByConstraint=new Constraint(LJFalse);			
			if (vals!=null && vals.length>0) {
				ArrayList<Object> correct= new ArrayList<Object>();
				if (where==null) where=new Constraint(LJTrue);
				for (int i=0; i<vals.length; i++) {
					if (variable(vals[i]) && !this.consistWith((Variable) vals[i])) {
						//debug("Found variable: "+vals[i]+" inconsistant with "+this);
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
		//debug("Found "+this+" immutable");
		return false;
	}

	
//Identifying characteristics of this Variable
	public final boolean isVar(){
		return isVar;
	}


	public final boolean noValue(){
		if (isVar()) return false;
		return (value.length==0 && constraint.asRelation()==LJFalse);
	}


	public final boolean isPrimitiveType(){
		return (get(0).getClass().isPrimitive());
	}


	public final boolean isConstraint(){
		if ((!this.isVar()) && (!noValue()))
			return (value.length>1 || constraint.asRelation()!=LJFalse);
		return false;
	}


	public final boolean isRelation(){
		return (get(0) instanceof Relation);
	}


	public final boolean isString(){
		return (get(0) instanceof String);
	}


	public final boolean isInteger(){
		return (get(0) instanceof Integer);
	}


	public final boolean isDouble(){
		return (get(0) instanceof Double);	
	}


	public final boolean isFloat(){
		return (get(0) instanceof Float);	
	}


	public final boolean ofClass(Class<?> c){						
		return (get(0).getClass().equals(c));
	}


	public final boolean isNumber(){
		return (get(0) instanceof Number);		
	}		


//Returns the variable value.
	private final Object flat(int i){
		if (value.length>i) return val(value[i]);
		return undefined;
	}


//Tests all required characteristics for this Variable to be of a single value
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

/* to fix:
 * the lock at consistWith versus instantiate. 
 */
