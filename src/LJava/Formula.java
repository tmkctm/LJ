package LJava;
import static LJava.LJ.*;
import static LJava.Utils.*;
import java.lang.reflect.Array;
import java.util.HashMap;

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
		HashMap<Variable, Constraint> vars = new HashMap<Variable, Constraint>(); 
		for (int i=1; i<rArgs.length; i++) {			
			if (var(rArgs[i])) vars.put((Variable) rArgs[i], new Constraint(this,rArgs));
			else {
				Object val = val(rArgs[i]);
				if (!parametersType.isAssignableFrom(val.getClass())) return false;
				temp[i-1]=(P) val;
			}
		}
		if (vars.isEmpty()) {
			try { 
				R value=invoke(temp);
				if (var(rArgs[0])) {
					varValues.updateValuesMap((Variable) rArgs[0], value);
					return true;
				}
				return same(value,rArgs[0]);
			} catch (Exception e) { return false; }
		}
		if (var(rArgs[0])) vars.put((Variable) rArgs[0], new Constraint(this,rArgs));
		varValues.updateConstraintsMap(vars);
		return true;
	}
	
/* to fix:
 * satisfy for a var in the rArgs instead of saving a constraint - requires reversing the formula.
 */
	
	
}
