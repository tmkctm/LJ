package LJava;
import static LJava.Utils.*;
import java.util.HashMap;

@SuppressWarnings("rawtypes")
public class Constraint {
	
	private interface ItemInConstraint {
		public boolean satisfy(HashMap<Variable, Object> map);
		public Constraint replaceVariable(Variable v1, Variable v2);
	}
	
	private final class Atom implements ItemInConstraint {
		private final Formula f;
		private final Object[] args;
		
		public Atom(Formula formula, Object... params) {
			f = (formula==null) ? LJTrue : formula;
			args = (params==null) ? new Object[0] : params;
		}
		
		public Atom(Atom a, Variable v1, Variable v2) {
			f=a.f;
			args = new Object[a.args.length];
			for (int i=0; i<args.length; i++)
				args[i] = (a.args[i]==v1) ? v2 : a.args[i];
		}
		
		public String toString() {	
			StringBuilder s = new StringBuilder(f.name()+"(");
			if (args.length>0) {
				for (int i=0; i<args.length-1; i++)	
					if (variable(args[i])) s.append("[],");
					else s.append(args[i]+",");
				s.append(args[args.length-1].toString());
			}
			s.append(")");
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
			return f.satisfy(arr, new VariableMap());
		}
		
		@Override
		public Constraint replaceVariable(Variable v1, Variable v2) {
			Atom a = new Atom(this, v1, v2);
			return new Constraint(a.f, a.args);
		}
	}
	
	private final class Junction implements ItemInConstraint {
		private final Constraint left;
		private final Constraint right;
		private final LogicOperator op;
		
		public Junction(Constraint l, LogicOperator lp, Constraint r) {
			right = (r==null) ? new Constraint(LJTrue) : r;
			left = (l==null) ? new Constraint(LJTrue) : l;
			op = (lp==null) ? LogicOperator.NONE : lp;
		}		
		
		@Override
		public boolean satisfy(HashMap<Variable, Object> map) { 
			if (op==AND || op==WHERE) return (left.satisfy(map) && right.satisfy(map));
			if (op==OR) return (left.satisfy(map) || right.satisfy(map));
			if (op==DIFFER) return (left.satisfy(map) && !right.satisfy(map));
			return false;
		}
		
		public String toString() {
			return ("("+left+") "+op+" ("+right+")");
		}
		
		@Override
		public Constraint replaceVariable(Variable v1, Variable v2) {
			return new Constraint(left.replaceVariable(v1, v2),op,right.replaceVariable(v1, v2));
		}
	}
	
	private final ItemInConstraint atom;

	
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
