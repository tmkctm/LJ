package LJava;

import static LJava.LJ.*;
import static LJava.Utils.LJFalse;
import static LJava.Utils.LJTrue;
import java.lang.reflect.Array;
import java.util.Arrays;

/**
 * @author Tzali Maimon
 * Formula is LJ's function object. From here you can create a logical formula and as result - a function (an n+1 dimension Formula is an n dimension function).<p>
 * Formula requires you to override the inner function that it should activate on call.<p>
 * One main use for Formula is defining infinite many Relations with one entry into the "world" of LJ <p>
 * For example if you associate a formula that sums up two numbers, then you defined the rule "x,y,z where x=y+z". 
 * @param <P> - the arguments type of the function f that gets overrode. 
 * @param <R> - the return type of the function f that gets overrode.
 */
public abstract class Formula<P,R> extends Relation {

	protected final Class<P> parametersType;
	
	/**
	 * @param n - a name for the formula
	 * @param type - the type of the arguments of the function f that gets overrode. This is meant for Formula to avoid throwing exceptions at runtime.
	 */
	public Formula(String n, Class<P> type) {		
		super(n);
		parametersType=type;
	}
	
	
	protected abstract R f(P[] p);
	
	
	/**
	 * Returns true if val is the same as the result of f(params). See LJ.same for more info.
	 * @param val - a value
	 * @param params - parameters
	 * @return - a boolean expression
	 */
	public boolean satisfy(Object val, P... params) {
		return (same(val,invoke(params)));
	}
	
	
	/**
	 * returns v(params)  
	 * @param params - arguments.
	 * @return a type R object.
	 */
	public R value(P... params) {
		return v(params);
	}

	
	/**
	 * returns the value of activating f on the given params.
	 * @param params - arguments.
	 * @return a type R object.
	 */
	public R v(P... params) {
		return f((P[]) params);
	}	
	
	
	/**
	 * returns the invoking result of f on the given params. <p>
	 * The difference between invoke and value is that invoke accepts any Objects as params and returns an Object as a result. <p>
	 * This allows you to bypass Java's compiler types errors but at a risk of getting undefined return (about undefined read LJ class java doc)
	 * @param params - arguments
	 * @return and object that might be of type R or undefined
	 */
	@SuppressWarnings("unchecked")
	public Object invoke(Object... params) {
		try { return v((P[])Arrays.copyOf(params, params.length, ((P[])Array.newInstance(parametersType, 0)).getClass())); }
		catch (Exception e) {}
		if ((params.length==1) && (params[0].getClass().isArray()))
			try {
				params=(Object[]) params[0];
				return invoke(params);	
			}catch (Exception e) {}
		return undefined;
	}
	
	
	@Override
	public int argsLength() {
		return -1;
	}
	
	
	@Override
	public boolean isFormula() {
		return true;
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	protected boolean satisfy(Object[] rArgs, LJMap varValues){
		if (this==LJFalse) return false;
		if (this==LJTrue || rArgs.length==0) return true;
		P[] temp=(P[]) Array.newInstance(parametersType, rArgs.length-1);
		boolean varInArgs=false;
		for (int i=1; i<rArgs.length; i++) {			
			Object val = val(rArgs[i]);
			if (!parametersType.isAssignableFrom(val.getClass())) {
				if (!var(val)) return false;
				varInArgs=true;
			}
			else temp[i-1]=(P) val;
		}
		if (varInArgs) {
			if (!var(rArgs[0])) return false;
			varValues.updateConstraintsMap((Variable) rArgs[0], new Constraint(this, rArgs));
		}
		else {
			R value=value(temp);
			if (!var(rArgs[0])) return same(value,rArgs[0]);
			varValues.updateValsMap((Variable) rArgs[0], value);
		}
		return true;
	}
	
	
	protected LazyFormula goLazy(Formula<P, P[]> inc, P... params) {
		return new LazyFormula(this, inc, params);
	}
	
	
//Lazy Formula
	private class LazyFormula implements Lazy<R> {
		
		private final Formula<P,R> f;
		private final Formula<P, P[]> increment;
		private final P[] baseArgs;
		private P[] args;
		private R current;
		
		@SuppressWarnings("unchecked")
		public LazyFormula(Formula<P,R> formula, Formula<P, P[]> inc, P... params) {
			f=formula;
			increment=inc;
			baseArgs=params;
			args=(P[]) Array.newInstance(parametersType, baseArgs.length);
			for (int i=0; i<args.length; i++) args[i]=baseArgs[i];
		}
		
		@Override
		public synchronized R lz() {
			current=f.v(args);
			args=increment.v(args);
			return current;
		}
		
		@Override
		public R current() {
			return current;
		}
		
		@Override
		public Variable[] getVars() {
			return new Variable[0];
		}
		
		@Override
		public boolean noVars() {
			return true;
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public synchronized void resetLazy() {
			args=(P[]) Array.newInstance(parametersType, args.length);
			for (int i=0; i<args.length; i++) args[i]=baseArgs[i];			
		}
		
		@Override
		public Object base() {
			return f;
		}
		
	}
}