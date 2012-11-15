package LJava;
import static LJava.Utils.*;
import java.util.HashMap;

@SuppressWarnings("rawtypes")
public class Constraint {
	
	private interface AtomInConstraint {
		public boolean satisfy(HashMap<Variable, Object> map);
		public Constraint replaceVariable(Variable v1, Variable v2);
	}
	
	private final class Atom implements AtomInConstraint {
		private final Formula f;
		private final Object[] args;
		
		public Atom(Formula formula, Object... params) {
			if (formula==null) f=LJTrue;
			else f=formula;
			if (params==null) args=new Object[0];
			else args=params;
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
			StringBuilder s = new StringBuilder(f.name());
			int counter=1;
			HashMap<Variable, String> names = new HashMap<Variable, String>();
			if (args.length>0) {
				s.append("(");
				for (int i=0; i<args.length; i++) {
					if (variable(args[i])) {
						String tag = names.get((Variable) args[i]);
						if (tag==null) {
							tag="[var"+counter+"]";
							counter++;
							names.put((Variable) args[i], tag);
						}
						s.append(tag+",");
					}
					else s.append(args[i].toString()+",");
				}
				int len = s.length();
				s.replace(len-1, len, ")");
			}
			return s.toString();
		}
		
		@Override
		public boolean satisfy(HashMap<Variable, Object> map) {
			if (f==LJFalse) return false;
			if (f==LJTrue) return true;
			Object[] arr = new Object[args.length];
			for (int i=0; i<arr.length; i++) {
				arr[i]=map.get(args[i]);
				if (arr[i]==null) arr[i]=args[i];
			}
			return f.satisfy(arr, new VariableValuesMap());
		}
		
		@Override
		public Constraint replaceVariable(Variable v1, Variable v2) {
			Atom a = new Atom(this, v1, v2);
			return new Constraint(a.f, a.args);
		}
	}
	
	private final class Junction implements AtomInConstraint {
		private final Constraint left;
		private final Constraint right;
		private final LogicOperator op;
		
		public Junction(Constraint l, LogicOperator lp, Constraint r) {
			if (r==null) right = new Constraint(LJTrue);
			else right = r;
			if (l==null) left = new Constraint(LJTrue);
			else left = l;
			if (lp==null) op=LogicOperator.NONE;
			else op = lp;
		}		
		
		@Override
		public boolean satisfy(HashMap<Variable, Object> map) { 
			if (op==AND || op==WHERE) return (left.satisfy(map) && right.satisfy(map));
			if (op==OR) return (left.satisfy(map) || right.satisfy(map));
			if (op==DIFFER) return (left.satisfy(map) && !right.satisfy(map));
			return false;
		}
		
		public String toString() {
			StringBuilder s = new StringBuilder("(");
			if (op==AND) s.append(left+") AND ("+right);
			else if (op==WHERE) s.append(left+") WHERE ("+right);
			else if (op==OR) s.append(left+") OR ("+right);
			else if (op==DIFFER) s.append(left+") AND NOT ("+right);
			s.append(")");
			return s.toString();
		}
		
		@Override
		public Constraint replaceVariable(Variable v1, Variable v2) {
			return new Constraint(left.replaceVariable(v1, v2),op,right.replaceVariable(v1, v2));
		}
	}
	
	private final AtomInConstraint atom;

	
	public Constraint(Formula formula, Object... params) {
		atom = new Atom(formula, params);
	}
	
	
	public Constraint(Constraint l, LogicOperator lp, Constraint r) {
		atom = new Junction(l,lp,r);
	}
	
	
	public boolean satisfy(Variable v, Object o) {
		HashMap<Variable, Object> map = new HashMap<Variable, Object>();
		map.put(v,o);		
		return atom.satisfy(map);
	}

	
	public boolean satisfy(Variable[] vs, Object[] os) {
		if (vs.length>os.length) return false;
		HashMap<Variable, Object> map = new HashMap<Variable, Object>();
		for (int i=0; i<vs.length; i++) map.put(vs[i],os[i]);
		return atom.satisfy(map);
	}
	
	
	public boolean satisfy(HashMap<Variable,Object> map) {
		return atom.satisfy(map);
	}
	

	public String toString() {
		return atom.toString();
	}
	
	
	public Formula asFormula() {
		if (isFormula()) return ((Atom) atom).f;
		return LJTrue;
	}
	
	
	public boolean isFormula() {
		return (atom instanceof Atom);
	}
	
	
	public Constraint replaceVariable(Variable v1, Variable v2) {
		return atom.replaceVariable(v1, v2);
	}
	
}
