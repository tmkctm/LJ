package LJava;
import static LJava.Utils.LJFalse;
import static LJava.Utils.LJTrue;
import static LJava.LJ.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import LJava.LJ.LJIterator;

public class Constraint implements QueryParameter {
	
	private interface ItemInConstraint {
		public boolean satisfy(VariableMap restrictions);
		public Constraint replaceVariable(Variable v, Object o);
		public HashSet<Variable> getVars();
		public boolean conduct(VariableMap restrictions, VariableMap answer);
		public void startLazy();
	}
	
	@SuppressWarnings("rawtypes")
	private final class Atom implements ItemInConstraint {
		private final Relation relation;
		private final Object[] args;
		private LJIterator iterator;
		
		public Atom(Relation r, Object... params) {
			relation=(r==null) ? LJTrue : r;
			args = (params==null) ? new Object[0] : params;
			iterator=emptyIterator;
		}
		
		public Atom(Atom a, Variable v1, Object v2) {
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
		public boolean satisfy(VariableMap restrictions) {
			if (restrictions.isEmpty()) return true;
			Object[] arr = new Object[args.length];
			for (int i=0; i<arr.length; i++) {
				arr[i]=restrictions.map.get(args[i]);
				arr[i]=(arr[i]==null)? args[i] : ((ArrayList) arr[i]).get(0);
			}			
			if (relation instanceof Formula) return relation.satisfy(arr, new VariableMap());
			return relation(relation.name(), arr).map(new VariableMap(), true);
		}
		
		@Override
		public Constraint replaceVariable(Variable v, Object o) {
			Atom a = new Atom(this, v, o);
			return new Constraint(a.relation, a.args);
		}
		
		@Override
		public HashSet<Variable> getVars() {
			HashSet<Variable> set=new HashSet<Variable>();
			for (Object o : args) if (variable(o)) set.add((Variable) o);
			return set;
		}
		
		@Override
		public boolean conduct(VariableMap restrictions, VariableMap answer) {
			Relation r;
			if (!restrictions.isEmpty()) {
				Object[] arr=new Object[args.length];
				for (int i=0; i<arr.length; i++) {
					arr[i]=restrictions.map.get(args[i]);
					arr[i]= (arr[i]==null) ? args[i] : ((ArrayList) arr[i]).get(0);
				}
				r=relation(relation.name, arr);
			}
			else if (relation instanceof Formula) r=relation(relation.name, args);
			else r=relation;
			if (iterator==emptyIterator) iterator=getLJIterator(this.args.length);
			while (iterator.hasNext()) 
				if (evaluate(r, answer, iterator)) {
					answer.add(restrictions);
					return true; 
				}
			return false;
		}
		
		@Override
		public void startLazy(){
			iterator=emptyIterator;
		}
	}
	
	private final class Junction implements ItemInConstraint {
		private final Constraint left;
		private final Constraint right;
		private final LogicOperator op;
		
		public Junction(Constraint l, LogicOperator lp, Constraint r) {
			right = (r==null) ? new Constraint(LJTrue) : r;
			left = (l==null) ? new Constraint(LJTrue) : l;
			op = lp;
		}		
		
		@Override
		public boolean satisfy(VariableMap map) { 
			if (op==AND || op==WHERE) return (left.satisfy(map) && right.satisfy(map));
			if (op==OR) return (left.satisfy(map) || right.satisfy(map));
			if (op==DIFFER) return (left.satisfy(map) && !right.satisfy(map));
			return true;
		}
		
		public String toString() {
			return ("("+left+") "+op+" ("+right+")");
		}
		
		@Override
		public Constraint replaceVariable(Variable v, Object o) {
			return new Constraint(left.replaceVariable(v, o),op,right.replaceVariable(v, o));
		}
		
		@Override 
		public HashSet<Variable> getVars() {
			HashSet<Variable> set=new HashSet<Variable>();
			set.addAll(left.getVars());
			set.addAll(right.getVars());
			return set;
		}
		
		@Override
		public boolean conduct(VariableMap restrictions, VariableMap answer) {
			VariableMap tempAnswer;
			if (op==WHERE) {
				do {
					tempAnswer=new VariableMap();
					if (!left.conduct(restrictions, tempAnswer)) return false;
				} while (!right.satisfy(tempAnswer));
			}
			else if (op==AND) {
				do {
					tempAnswer=new VariableMap();
					right.startLazy();
					if (!left.conduct(restrictions, tempAnswer)) return false;
				} while (!right.conduct(tempAnswer, answer));
			}
			else if (op==DIFFER) {
				do {
					tempAnswer=new VariableMap();
					if (!left.conduct(restrictions, tempAnswer)) return false;
				} while (right.satisfy(tempAnswer));
			}
			else if (op==OR)
				if (!left.conduct(restrictions, answer) && !right.conduct(restrictions, answer)) return false;
			return true;
		}
		
		@Override
		public void startLazy() {
			left.startLazy();
			right.startLazy();
		}
	}
	
	private final ItemInConstraint atom;

	
	public Constraint(Relation r, Object... params) {
		atom = new Atom(r, params);
	}
	
	
	public Constraint(QueryParameter l, LogicOperator lp, QueryParameter r) {
		if (lp!=null) {
			Constraint left = (l instanceof Constraint) ? (Constraint) l : new Constraint((Relation)l, ((Relation)l).args);
			Constraint right = (r instanceof Constraint) ? (Constraint) r : new Constraint((Relation)r, ((Relation)r).args);
			atom=new Junction(left,lp,right);
		}
		else atom=new Atom(LJTrue);
	}
	
	
	public boolean satisfy(VariableMap map) {
		return atom.satisfy(map);
	}
	
	
	public boolean satisfy(Variable[] vs, Object[] os) {
		if (vs.length>os.length) return false;
		VariableMap map=new VariableMap();
		for (int i=0; i<vs.length; i++) map.updateValsMap(vs[i],os[i]);
		return atom.satisfy(map);
	}
	
	
	public boolean satisfy(HashMap<Variable,Object> map) {
		return atom.satisfy(new VariableMap(map));
	}

	
	public boolean satisfy(Object... pairs) {
		if (pairs.length%2==1) return false;
		VariableMap map=new VariableMap();
		int i=-2;
		while ((i=i+2)<pairs.length) {
			if (!variable(pairs[i])) return false;
			map.updateValsMap((Variable) pairs[i], pairs[i+1]);
		}
		return atom.satisfy(map);
	}
	

	public String toString() {
		return atom.toString();
	}
	
	
	@Override
	public boolean map(VariableMap m, boolean cut) {
		if (cut) return lazyMap(m);
		if (!lazyMap(m)) return false;
		while (lazyMap(m)) {}
		return true;
	}
	
	
	public boolean lazyMap(VariableMap m) {
		VariableMap answer = new VariableMap();
		if (conduct(new VariableMap(), answer)) {
			m.add(answer);
			return true;
		}
		return false;
	}
	
	
	private boolean conduct(VariableMap restrictions, VariableMap answer) {
		return atom.conduct(restrictions, answer); 
	}
	
	
	public Relation asRelation() {
		if (isRelation()) return ((Atom) atom).relation;
		return LJFalse;
	}
	
	
	public boolean isRelation() {
		return (atom instanceof Atom);
	}
	
	
	public Constraint replaceVariable(Variable v, Object o) {
		return atom.replaceVariable(v, o);
	}
	
	
	public HashSet<Variable> getVars() {
		return atom.getVars();
	}
	
	
	private void startLazy() {
		atom.startLazy();
	}
}


/* to fix:
 * Restrictions for conduct not perfect for Group and Formula in Atom.
 * toString of atom isn't working good for variables currently. testCase: z has constraint c which has atom that contains z...
 * the implementation of the restrictions on arr needs to be above Atom.conduct and not inside it 
 */
