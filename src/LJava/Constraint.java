package LJava;
import static LJava.Utils.*;
import java.util.HashMap;

@SuppressWarnings("rawtypes")
public class Constraint {
	
	private final class Atom {
		private Formula f;
		private Object[] args;
		
		public Atom(Formula formula, Object... params) {
			f=formula;
			args=params;
		}
		
		public Atom(Atom a, Variable v1, Variable v2) {
			f=a.f;
			args = new Object[a.args.length];
			for (int i=0; i<args.length; i++) {
				if (a.args[i]==v1) args[i]=v2;
				else args[i]=a.args[i];
			}
		}
		
		public String toString() {			
			String s = f.name()+"(";
			int counter=1;
			HashMap<Variable, String> names = new HashMap<Variable, String>();
			if (args.length>0) {
				for (int i=0; i<args.length; i++) {
					if (variable(args[i])) {
						String tag = names.get((Variable) args[i]);
						if (tag==null) {
							tag="[var"+counter+"]";
							counter++;
							names.put((Variable) args[i], tag);
						}
						s=s+tag+",";
					}
					else s=s+args[i].toString()+",";
				}
				s=s.substring(0,s.length()-1);
			}
			return s+")";
		}
		
		public boolean satisfy(HashMap<Variable, Object> map) {
			if (f==LJFalse) return false;
			if (f==LJTrue) return true;
			Object[] arr = new Object[args.length];
			for (int i=0; i<arr.length; i++) {
				if (map.containsKey(args[i])) arr[i]=map.get(args[i]);
				else arr[i]=args[i];
			}
			return f.satisfy(arr, new VariableValuesMap());
		}
	}
	
	private final LogicOperator op;
	private final Constraint left;
	private final Constraint right;
	private final Atom f;
	
	
	public Constraint(Formula formula, Object... params) {
		f = new Atom(formula, params);
		left = null;
		right = null;
		op = LogicOperator.NONE;
	}
	
	
	public Constraint(Constraint l, LogicOperator lp, Constraint r) {
		f = new Atom(LJTrue);
		right = r;
		left = l;
		op = lp;
	}
	
	
	public boolean satisfy(Variable v, Object o) {
		HashMap<Variable, Object> map = new HashMap<Variable, Object>();
		map.put(v,o);		
		return testSatisfy(map);
	}

	
	public boolean satisfy(Variable[] vs, Object[] os) {
		if (vs.length>os.length) return false;
		HashMap<Variable, Object> map = new HashMap<Variable, Object>();
		for (int i=0; i<vs.length; i++) map.put(vs[i],os[i]);
		return testSatisfy(map);
	}
	
	
	public boolean satisfy(HashMap<Variable,Object> map) {
		return testSatisfy(map);
	}
	
	//THIS IS WRONG!!!!
	private boolean testSatisfy(HashMap<Variable, Object> map) { 
		if (op==AND || op==WHERE) return ((left==null || left.testSatisfy(map)) && (right==null || right.testSatisfy(map)));
		if (op==OR) return ((left==null || left.testSatisfy(map)) || (right==null || right.testSatisfy(map)));
		if (op==DIFFER) return ((left==null || left.testSatisfy(map)) && !(right==null || right.testSatisfy(map)));
		return f.satisfy(map);	
	}
	
	
	public String toString() {
		String s = "(";
		if (op==AND) s=s+(left+" AND "+right);
		else if (op==WHERE) s=s+(left+" WHERE "+right);
		else if (op==OR) s=s+(left+" OR "+right);
		else if (op==DIFFER) s=s+(left+" AND NOT "+right);
		else s=s+f.toString();
		return s+")";
	}
	
	
	public Formula asFormula() {
		return f.f;
	}
	
	
	public boolean isFormula() {
		return (op == LogicOperator.NONE);
	}
	
	
	public Constraint replaceVariable(Variable v1, Variable v2) {
		if (left==null && right==null) {
			Atom a=new Atom(f,v1,v2);
			return new Constraint(a.f, a.args);
		}
		if (left==null || right==null) { 
			if (left!=null) return left.replaceVariable(v1, v2);
			else return right.replaceVariable(v1, v2);
		}
		return new Constraint(left.replaceVariable(v1, v2),op,right.replaceVariable(v1, v2));
	}
	
}
