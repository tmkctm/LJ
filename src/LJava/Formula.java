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
	
	
	protected abstract R f(P... p);
	
	
	public boolean satisfy(Object val, P... params) {
		return (same(val,invoke(params)));
	}
	
	
	public R value(P... params) {
		return this.f(params);
	}
	
	
	@SuppressWarnings("unchecked")
	public Object invoke(Object... params) {
		try { return value((P[]) Arrays.copyOf(params, params.length, ((P[]) Array.newInstance(parametersType, 0)).getClass())); }
		catch (Exception e) { return undefined; 	}
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
	
	
	@Override
	protected boolean satisfied(Object[] arr, VariableMap m, boolean cut) {
		return satisfy(arr, m);
	}
	
}


/* to fix:
 * reverse formulas. 	
 */
