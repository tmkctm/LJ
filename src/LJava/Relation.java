package LJava;

import static LJava.LJ.*;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

import LJava.LJ.LJIterator;

/**
 * @author Tzali Maimon
 * Relation is a fact that has an order to its arguments. the name of a Relation is like a key.<p>
 * So you can write a rule coolLibrary(LJ,"really cool") like this: new Relation("coolLibrary", LJ, "really cool")<p>
 * Relations are Associations so you can place them as rules in the "world" of LJ
 */
public class Relation extends Association implements QueryParameter{
	
	private AtomicReference<LJIterator> iterator;
	
	/**
	 * @param n - the relation's name
	 * @param params - the relation's arguments
	 */
	public Relation(String n, Object... params){
		super (n, params);
		iterator=new AtomicReference<LJIterator>(emptyIterator);
	}
	
	
	@Override
	protected boolean satisfy(Object[] rArgs, LJMap varValues){
		HashMap<Variable,Object> vars=new HashMap<Variable, Object>();
		Object[] args=this.args();
		for (int i=0; i<rArgs.length; i++) {			
			if (var(rArgs[i])) {
				if (vars.containsKey(rArgs[i])) {
					if (!same(vars.get(rArgs[i]), args[i])) return false;   }
				else vars.put((Variable) rArgs[i], args[i]);   }								
			else if (!same(rArgs[i],args[i])) return false;
		}
		varValues.updateValsMap(vars);
		return true;
	}			

	
	@Override
	public boolean map(LJMap answer, boolean cut) {
		LJIterator i=iterate(argsLength());
		if (cut) return evaluate(this, answer, i);
		if (!evaluate(this, answer, i)) return false;
		while (evaluate(this, answer, i)) {}
		return true;
	}
	
	
	protected boolean lz(LJMap answer) {		
		iterator.compareAndSet(emptyIterator, iterate(argsLength()));
		return evaluate(this, answer, iterator.get()); 
	}
	
	
	/**
	 * @param replacements
	 * @return a new relation that replaced all the variables by the corresponding objects in replacements. 
	 */
	public Relation replaceVariables(HashMap<Variable, Object> replacements) {
		Object arguments[]=new Object[args.length];
		for (int i=0; i<args.length; i++) {
			arguments[i]=replacements.get(args[i]);
			arguments[i]= (arguments[i]==null)? args[i]: arguments[i];
		}
		return relation(name, arguments);
	}

	
	/**
	 * @param v1 a variable
	 * @param v2 an object
	 * @return a new relation where v1 is replaced by v2
	 */
	public Relation replaceVariables(Variable v1, Object v2) {
		HashMap<Variable, Object> m=new HashMap<Variable, Object>();
		m.put(v1, v2);
		return replaceVariables(m);
	}
	
	
}
