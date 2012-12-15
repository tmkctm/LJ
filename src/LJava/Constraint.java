package LJava;
import static LJava.Utils.*;
import static LJava.LJ.*;
import java.util.HashMap;
import java.util.HashSet;
import LJava.LJ.LJIterator;

public class Constraint implements QueryParameter {
	
	private interface ItemInConstraint {
		public boolean satisfy(HashMap<Variable, Object> map);
		public Constraint replaceVariable(Variable v1, Variable v2);
		public HashSet<Variable> getVars();
		public boolean conduct(VariableMap map); 
	}
	
	private final class Atom implements ItemInConstraint {
		private final Relation relation;
		private final Object[] args;
		private LJIterator iterator;
		
		public Atom(Relation r, Object... params) {
			relation=(r==null) ? LJTrue : r;
			args = (params==null) ? new Object[0] : params;
			iterator=emptyIterator;
		}
		
		public Atom(Atom a, Variable v1, Variable v2) {
			relation=a.relation;
			args = new Object[a.args.length];
			for (int i=0; i<args.length; i++)
				args[i] = (a.args[i]==v1) ? v2 : a.args[i];
		}
		
		public String toString() {	
			StringBuilder s = new StringBuilder(relation.name()+"(");
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
			Object[] arr = new Object[args.length];
			for (int i=0; i<arr.length; i++) {
				arr[i]=map.get(args[i]);
				if (arr[i]==null) arr[i]=args[i];
			}
			if (relation instanceof Formula) return relation.satisfy(arr, new VariableMap());
			return relation(relation.name(),args).map(new VariableMap(), true);
		}
		
		@Override
		public Constraint replaceVariable(Variable v1, Variable v2) {
			Atom a = new Atom(this, v1, v2);
			return new Constraint(a.relation, a.args);
		}
		
		@Override
		public HashSet<Variable> getVars() {
			HashSet<Variable> set = new HashSet<Variable>();
			for (Object o : args) if (variable(o)) set.add((Variable) o);
			return set;
		}
		
		@Override
		public boolean conduct(VariableMap map) {
			if (iterator==emptyIterator) iterator=getLJIterator(this.args.length);
			while (iterator.hasNext() && !evaluate(relation, map, iterator)) {} 
			if (!iterator.hasNext()) {
				iterator=emptyIterator;
				return false;
			}
			return true;
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
		
		@Override 
		public HashSet<Variable> getVars() {
			HashSet<Variable> set=new HashSet<Variable>();
			set.addAll(left.getVars());
			set.addAll(right.getVars());
			return set;
		}
		
		@Override
		public boolean conduct(VariableMap map) {
			//TBD
			return false;
		}
	}
	
	private final ItemInConstraint atom;

	
	public Constraint(Relation r, Object... params) {
		atom = new Atom(r, params);
	}
	
	
	public Constraint(QueryParameter l, LogicOperator lp, QueryParameter r) {
		Constraint left = (l instanceof Constraint) ? (Constraint) l : new Constraint((Relation)l, ((Relation)l).args);
		Constraint right = (r instanceof Constraint) ? (Constraint) r : new Constraint((Relation)r, ((Relation)r).args);
		atom = new Junction(left,lp,right);
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

	
	public boolean satisfy(Object... pairs) {
		if (pairs.length%2==1) return false;
		HashMap<Variable, Object> map = new HashMap<Variable, Object>();
		int i=0;
		while (i<pairs.length) {
			if (!variable(pairs[i])) return false;
			map.put((Variable) pairs[i], pairs[i+1]);
			i=i+2;
		}
		return atom.satisfy(map);
	}
	

	public String toString() {
		return atom.toString();
	}
	
	
	@Override
	public boolean map(VariableMap m, boolean cut) {
		if (cut) return conduct(m);
		if (!conduct(m)) return false;
		while (conduct(m)) {}
		return true;
	}
	
	
	private boolean conduct(VariableMap map) {
		VariableMap answer= new VariableMap();
		if (atom.conduct(answer)) {
			map.add(answer);
			return true;
		}
		return false;
	}
	
	
	public Relation asRelation() {
		if (isRelation()) return ((Atom) atom).relation;
		return LJFalse;
	}
	
	
	public boolean isRelation() {
		return (atom instanceof Atom);
	}
	
	
	public Constraint replaceVariable(Variable v1, Variable v2) {
		return atom.replaceVariable(v1, v2);
	}
	
	
	public HashSet<Variable> getVars() {
		return atom.getVars();
	}	
}


/* to fix:
 * implement the TBD.
 * after map() is over in the constraint all the iterators should be initialized to empty in the atoms.
 * toString of atom isn't working good for variables currently. testCase: z has constraint c which has atom that contains z... 
 */
