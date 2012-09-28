package LJava;
import static LJava.LJ.*;

import java.lang.reflect.Array;

public abstract class Functor<P,R> extends Relation{

	private final Class<P> parametersType;
	
	public Functor(String n, Class<P> pType) {		
		super(n);
		parametersType=pType;
	}
	
		
	protected abstract R f(P... p);
	
	
	public final boolean satisfy(Object val, P... params) {
		return (same(val,invoke(params)));
	}
	
	
	public final R invoke(P... params) {
		return this.f(params);	
	}
	
	
	@Override
	public final int argsLength() {
		return -1;
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	protected final boolean satisfy(Object[] rArgs, VariableValuesMap varValues){
		P[] temp=(P[]) Array.newInstance(parametersType, rArgs.length-1);
		for (int i=1; i<rArgs.length; i++) {
			if (!rArgs[i].getClass().equals(parametersType)) return false;
			temp[i-1]=(P) rArgs[i];
		}			
		R value=invoke(temp);
		if (var(rArgs[0])) {
			updateValuesMap((Variable) rArgs[0], value, varValues);
			return true;
		}
		return same(value,rArgs[0]);
	}	
	
}
