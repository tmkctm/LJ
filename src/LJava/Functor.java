package LJava;
import static LJava.LJ.*;

public abstract class Functor<P,R> extends Relation{

	public Functor(String n) {		
		super(n);
	}
	
		
	protected abstract R f(P... p);
	
	
	public final boolean satisfy(Object val, P... params) {
		return (same(val,invoke(params)));
	}
	
	
	public final R invoke(P... params) {
		return f(params);	
	}
	
	
	@Override
	public final int argsLength() {
		return -1;
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	protected final boolean satisfy(Object[] rArgs, VariableValuesMap varValues){
		P[] temp=(P[]) new Object[rArgs.length-1];
		for (int i=1; i<rArgs.length; i++)
			try {temp[i-1]=(P)rArgs[i];} catch(Exception e){return false;}
		R value=invoke(temp);
		if (var(rArgs[0])) {
			updateValuesMap((Variable) rArgs[0], value, varValues);
			return true;
		}
		return same(value,rArgs[0]);
	}	
	
}
