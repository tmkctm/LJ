package LJava;

import static LJava.LJ.*;
import static LJava.Utils.LJFalse;
import static LJava.Utils.LJTrue;
import java.lang.reflect.Array;
import java.util.Arrays;

public abstract class Formula<P,R> extends Relation {

	protected final Class<P> parametersType;
	
	public Formula(String n, Class<P> type) {		
		super(n);
		parametersType=type;
	}
	
	
	protected abstract R f(P[] p);
	
	
	public boolean satisfy(Object val, P... params) {
		return (same(val,invoke(params)));
	}
	
	
	public R value(P... params) {
		return v(params);
	}

	
	public R v(P... params) {
		return f((P[]) params);
	}	
	
	
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
	protected boolean satisfy(Object[] rArgs, VariableMap varValues){
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