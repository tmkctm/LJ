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
	protected boolean satisfy(Object[] rArgs, VariableMap varValues){
		if (this==LJTrue) return true;
		if (this==LJFalse) return false;
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
			R value=invoke(temp);
			if (!var(rArgs[0])) return same(value,rArgs[0]);
			varValues.updateValuesMap((Variable) rArgs[0], value);
		}
		return true;
	}
	
/* to fix:
 * satisfy for a var in args - requires reversing the formula.
 */
	
	
}
