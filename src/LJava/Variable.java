package LJava;
import static LJava.LJ.*;
import static LJava.Utils.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicBoolean;

public class Variable {
	
	private Object[] value=new Object[0];
	private boolean isVar=true;
	private AtomicBoolean inSet=new AtomicBoolean(false);
	private Constraint constraint=new Constraint(LJFalse);
		

	public final boolean equals(Object x){		
		x=val(x);
		Object o=this.get();		
		if (o==this) return (o==x);
		return same(o,x);					
	}
		

//Properties		
	public final String toString(){
		if (this.noValue()) return none.toString();
		StringBuilder s= new StringBuilder("[");
		if (value.length>0) {
			for (int i=0; i<value.length-1; i++) {
				if (value[i]==this) s.append(undefined+",");
				else s.append(value[i]+",");
			}
			if (value[value.length-1]==this) s.append(undefined.toString());
			else s.append(value[value.length-1].toString());
		}
		s.append("]");
		if (constraint.asFormula()!=LJFalse) s.append(" OR "+constraint.toString());
		return s.toString();
	}
	

	public final Object get() {
		return get(this);
	}
	
	
	private final Object get(Variable v){
		if (isVar()) return this;
		if (noValue()) return none;
		return flat(v);
	}
	
	
	public final Constraint getConstraint() {
		return constraint.replaceVariable(this,this);
	}
	
	
	public final boolean contains(Object o) {
		for (int i=0; i<value.length; i++)
			if (same(val(value[i]),o)) return true;
		return constraint.satisfy(this, o);
	}
	
	
	public final Object[] getValues() {
		if (isVar()) return new Object[]{undefined};
		else if (noValue()) return new Object[]{none};
		Object[] result = new Object[value.length];
		for (int i=0; i<value.length; i++) result[i]=val(value[i]);
		return result;
	}
	
	
	public final HashSet<Object> getValuesSet(){
		HashSet<Object> valueSet= new HashSet<Object>();
		for (int i=0; i<value.length; i++)
			valueSet.add(val(value[i]));
		return valueSet;
	}
	
	
	public final boolean equalValuesSet(Variable x){
		HashSet<Object> set1 = this.getValuesSet();
		HashSet<Object> set2 = this.getValuesSet();
		for (Object o : set1)
			if (!set2.remove(o)) return false;
		if (set2.isEmpty()) return true;
		return false;
	}
	


//Turns the mutable Variable that has no value to an Immutable Variable that has meaning.
	public final boolean instantiate(Object[] vals, Constraint where, Constraint valByConstraint){
		if (isVar && inSet.compareAndSet(false,true)){
			if (valByConstraint==null) valByConstraint=new Constraint(LJFalse);			
			if (vals!=null && vals.length>0) {
				ArrayList<Object> correct= new ArrayList<Object>();
				if (where==null) where=new Constraint(LJTrue);
				for (int i=0; i<vals.length; i++) 
					if (where.satisfy(this, vals[i])) correct.add(vals[i]);
				if (!correct.isEmpty()) this.value=correct.toArray();				
			}
			this.constraint=valByConstraint;
			isVar=false;
			return true;						
		}
		return false;
	}
	
	
	public final boolean set(Object... args) {
		return instantiate(args, null, null);
	}
				
	
//Identifying characteristics of this Variable
	public final boolean isVar(){
		return isVar;
	}


	public final boolean noValue(){
		if (isVar()) return false;
		return (value.length==0 && constraint.asFormula()==LJFalse);
	}
	

	public final boolean isPrimitive(){
		return (get().getClass().isPrimitive());
	}


	public final boolean isConstraint(){
		if ((!this.isVar()) && (!noValue()))
			return (value.length>1 || constraint.asFormula()!=LJFalse);
		return false;
	}
	

	public final boolean isRelation(){
		return (get() instanceof Relation);
	}
	

	public final boolean isString(){
		return (get() instanceof String);
	}
	

	public final boolean isInteger(){
		return (get() instanceof Integer);
	}
	

	public final boolean isDouble(){
		return (get() instanceof Double);	
	}


	public final boolean isFloat(){
		return (get() instanceof Float);	
	}
	

	public final boolean ofClass(Class<?> c){						
		return (get().getClass().equals(c));
	}
	

	public final boolean isNumber(){
		return (get() instanceof Number);		
	}		
	

//Returns the variable value.
	private final Object flat(Variable v){
		if (value.length==0|| value[0]==v) return undefined;
		if (variable(value[0])) return ((Variable) value[0]).get(v);
		return val(value[0]);
	}
	
	
//Tests all required characteristics for this Variable to be of a single value
	public final boolean singleValue(){
		if (isVar() || noValue() || isConstraint()) return false;
		return true;
	}
	
}
