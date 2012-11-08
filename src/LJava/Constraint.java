package LJava;

import static LJava.Utils.*;

@SuppressWarnings("rawtypes")
public class Constraint {

	private class Atom {
		private Formula f;
		private Object[] args;
		public Atom(Formula formula, Object... params) {
			f=formula;
			args=params;
		}
		
		public String toString() {
			String s = f.toString()+" on argmunets (";
			if (args!=null && args.length>0) {
				for (int i=0; i<args.length-1; i++)
					s=s+args[i].toString()+",";
				s=s+args[args.length-1].toString();
			}
			return s+")";
		}
		
		public boolean satisfy(Variable v, Object o) {
			Object[] arr = new Object[args.length];
			for (int i=0; i<arr.length; i++) {
				if (args[i]==v) arr[i]=o;
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
		f = new Atom (LJTrue);
		right = r;
		left = l;
		op = lp;
	}
	
	
	public boolean satisfy(Variable v, Object o) {
		if (op==AND) return (left.satisfy(v,o) && right.satisfy(v,o));
		if (op==OR) return (left.satisfy(v,o) || right.satisfy(v,o));
		if (op==DIFFER) return (left.satisfy(v,o) && !right.satisfy(v,o));
		return f.satisfy(v,o);
	}
	
	
	public String toString() {
		String s = "(";
		if (op==AND) s=s+(left+" AND "+right);
		else if (op==OR) s=s+(left+" OR "+right);
		else if (op==DIFFER) s=s+(left+" AND NOT "+right);
		else s=s+f.toString();
		return s+")";
	}
	
}
