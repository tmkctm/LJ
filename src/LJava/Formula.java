package LJava;
import static LJava.LJ.*;
import static LJava.Utils.*;
import java.lang.reflect.Array;

public abstract class Formula<P,R> extends Relation {

	protected final Class<P> parametersType;
	
	public Formula(String n, Class<P> type) {		
		super(n);
		parametersType=type;
	}
	
	
	protected abstract R f(P... p);
	
	
	public boolean satisfy(Object val, P... params) {
		return (same(val,invoke(params)));
	}
	
	
	public R invoke(P... params) {
		return this.f(params);
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
	protected boolean satisfy(Object[] rArgs, VariableValuesMap varValues){
		if (this==LJTrue) return true;
		if (this==LJFalse) return false;
		P[] temp=(P[]) Array.newInstance(parametersType, rArgs.length-1);
		for (int i=1; i<rArgs.length; i++) {
			if (!parametersType.isAssignableFrom(val(rArgs[i]).getClass())) return false;
			temp[i-1]=(P) val(rArgs[i]);
		}
		R value = null;
		try { value=invoke(temp); } catch (Exception e) { return false; }
		if (var(rArgs[0])) {
			varValues.updateValuesMap((Variable) rArgs[0], value);
			return true;
		}
		return (same(value,rArgs[0]));
	}
	
/* to fix:
 * satisfy with vars in the rArgs.
 */
	
	
}
