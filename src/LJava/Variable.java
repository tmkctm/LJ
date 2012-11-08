package LJava;
import static LJava.LJ.*;
import static LJava.Utils.*;

import java.util.LinkedHashSet;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicBoolean;
 

public final class Variable {
	
	private Object[] value=null;
	private boolean isVar=true;
	private AtomicBoolean inSet=new AtomicBoolean(false);
	private LinkedHashSet<Variable> looksAt= new LinkedHashSet<Variable>();
	private Constraint constraint;
		

	public boolean equals(Object x){		
		x=val(x);
		Object o=this.get();		
		if (o==this) return (o==x);
		return same(o,x);					
	}
		

//Properties		
	public String toString(){
		if (this.noValue()) return none.toString();
		String s="[";
		if (!this.isVar()) {
			for (int i=0; i<value.length-1; i++)
				if (value[i]==null) s=s+"(null),";
				else s=s+value[i].toString()+",";
			int lim=value.length-1;
			if (lim>=0) s=s+value[lim].toString();
		}
		s=s+"]";
		if (constraint!=null) s=s+" UNION "+constraint.toString();
		return s;
	}
	

	public Object get(){
		if (isVar()) return this;
		if (noValue()) return none;
		return flat();
	}
	

	public Object[] getValues() {
		if (isVar()) return new Object[]{nil};
		else if (noValue()) return new Object[]{none};
		return value.clone();		
	}
	
	
	public HashSet<Object> getValuesSet(){
		HashSet<Object> valueSet= new HashSet<Object>();
		for (int i=0; i<value.length; i++)
			valueSet.add(val(value[i]));
		return valueSet;
	}
	
	
	public boolean equalValuesSet(Variable x){
		HashSet<Object> set1 = this.getValuesSet();
		HashSet<Object> set2 = this.getValuesSet();
		for (Object o : set1)
			if (set2.remove(o)==false) return false;
		if (set2.isEmpty()) return true;
		return false;
	}
	


//Turns the mutable Variable that has no value to an Immutable Variable that has meaning.
	public boolean set(Object... args){		
		if (isVar && inSet.compareAndSet(false,true)){
			if (args!=null) {
				Variable temp=new Variable();
				temp.value=new Object[args.length];
				for (int i=0; i<temp.value.length; i++) {
					if (variable(args[i])) {
						Variable y=(Variable) args[i];					
						if (!this.consistWith(y)) {							
							temp.value[i]=nil;
							continue;
						}							
					}
					temp.value[i]=args[i];
				}
				this.value=temp.value;
				constraint=null;
			}				
			isVar=false;
			return true;						
		}
		return false;
	}
				
	
//Identifying characteristics of this Variable
	public boolean isVar(){
		return isVar;
	}


	public boolean noValue(){
		if (isVar()) return false;
		if (value==null) return true;
		return (value.length==0);
	}
	

	public boolean isPrimitive(){
		return (get().getClass().isPrimitive());
	}


	public boolean isConstraint(){
		if ((!this.isVar()) && (!noValue()))
			return (value.length>1 || constraint!=null);
		return false;
	}
	

	public boolean isRelation(){
		return (get() instanceof Relation);
	}
	

	public boolean isString(){
		return (get() instanceof String);
	}
	

	public boolean isInteger(){
		return (get() instanceof Integer);
	}
	

	public boolean isDouble(){
		return (get() instanceof Double);	
	}


	public boolean isFloat(){
		return (get() instanceof Float);	
	}
	

	public boolean ofClass(Class<?> c){						
		return (get().getClass().equals(c));
	}
	

	public boolean isNumber(){
		return (get() instanceof Number);		
	}		
	

//Returns the variable value.
	private Object flat(){		
		return val(value[0]);
	}
	
	
//Tests all required characteristics for this Variable to be of a single value
	public boolean singleValue(){
		if (isVar() || noValue() || isConstraint()) return false;
		return true;
	}
	
	
//A consistency check for this Variable against another Variable.				
	private boolean consistWith(Variable b) {
		if (b.isVar()){ 
			if (!b.inSet.get()) { looksAt.add(b);  return true; }
			else return false;
		}		
		for (Variable element : b.looksAt) {			
			if (element.isVar()) {
				if (this==element) return false;
				
				//This needs to be atomitized!!!
				if (!element.inSet.get()) looksAt.add(element);
				//This needs to be atomitized!!!
				
				else return false;			
			}
			else if (!this.consistWith(element)) return false;		
		}		
		return true;			
	}
}
