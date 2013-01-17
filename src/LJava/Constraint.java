package LJava;

import static LJava.Utils.LJFalse;
import static LJava.Utils.LJTrue;
import static LJava.LJ.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class Constraint implements QueryParameter, Lazy<Constraint, VariableMap> {

	private interface Node {
		public boolean satisfy(VariableMap restrictions, VariableMap answer);
		public Node replaceVariable(Variable v, Object o);
		public HashSet<Variable> getVars();
		public void startLazy();
	}
	

	private final class Atom implements Node {
		private final Relation relation;
		private final Object[] args;
		private final HashMap<VariableMap, Relation> lazyMap=new HashMap<VariableMap, Relation>();
		
		public Atom(Relation r, Object... params) {
			relation=(r==null)? LJTrue : r;
			args=(params==null)? new Object[0] : params;
		}
		
		public Atom(Atom a, Variable v1, Object v2) {
			relation=(a.relation.isFormula())? a.relation : a.relation.replaceVariables(v1, v2);
			args=new Object[a.args.length];
			for (int i=0; i<a.args.length; i++)
				args[i] = (a.args[i]==v1)? v2 : a.args[i];
		}
		
		public String toString() {	
			StringBuilder s = new StringBuilder(relation.name()+"(");
			if (args.length>0) {
				for (int i=0; i<args.length; i++) s.append(string(args[i])+",");
				s.deleteCharAt(s.length()-1);		}
			s.append(')');
			return s.toString();
		}
		
		@Override
		public boolean satisfy(VariableMap restrictions, VariableMap answer) {
			Relation r;
			synchronized (this) {
				if ((r=lazyMap.get(restrictions))==null) {
					r=r(relation.name, restrict(args, restrictions));
					lazyMap.put(restrictions, r);
				}}
			if ((!relation.isFormula() && r.lz(answer)) || (relation.isFormula() && relation.satisfy(r.args, answer))) {
				answer.add(restrictions);
				return true;
			}
			lazyMap.remove(restrictions);
			return false;
		}
		
		@Override
		public Node replaceVariable(Variable v, Object o) {
			return new Atom(this, v, o);
		}
		
		@Override
		public HashSet<Variable> getVars() {
			HashSet<Variable> set=new HashSet<Variable>();
			for (Object o : args) if (variable(o)) set.add((Variable) o);
			return set;
		}
		
		@Override
		public synchronized void startLazy() {
			lazyMap.clear();
		}
		
	}
	
	
	private final class Junction implements Node {
		private final Node left;
		private final Node right;
		private final LogicOperator op;
		private final HashMap<VariableMap, Object> lazyMap=new HashMap<VariableMap, Object>();
		
		public Junction(Node l, LogicOperator lp, Node r) {
			right = (r==null) ? new Atom(LJTrue) : r;
			left = (l==null) ? new Atom(LJTrue) : l;
			op = lp;
		}		
		
		public String toString() {
			return ("("+left+") "+op+" ("+right+")");
		}
		
		@Override
		public Node replaceVariable(Variable v, Object o) {
			return new Junction(left.replaceVariable(v, o), op, right.replaceVariable(v, o));
		}
		
		@Override 
		public HashSet<Variable> getVars() {
			HashSet<Variable> set=new HashSet<Variable>();
			set.addAll(left.getVars());
			set.addAll(right.getVars());
			return set;
		}
		
		@Override
		public boolean satisfy(VariableMap restrictions, VariableMap answer) {
			if (op==AND) return operateAnd(restrictions, answer);
			else if (op==WHERE) return operateWhere(restrictions, answer, true);
			else if (op==DIFFER) return operateWhere(restrictions, answer, false);
			else if (op==OR) return operateOr(restrictions, answer);
			return true;
		}
		
		private boolean operateAnd(VariableMap restrictions, VariableMap answer) {
			VariableMap knownRestriction;
			do { synchronized (this) {
				knownRestriction=(VariableMap) lazyMap.get(restrictions);
				if (knownRestriction==null) {
					knownRestriction=new VariableMap();
					if (!left.satisfy(restrictions, knownRestriction)) return false;
					lazyMap.put(restrictions, knownRestriction);
				}}
				if (right.satisfy(knownRestriction, answer)) return true; 
				lazyMap.remove(restrictions);
			} while (true);
		}
		
		private boolean operateWhere(VariableMap restrictions, VariableMap answer, boolean where) {
			if (lazyMap.get(restrictions)!=null) return false;
			VariableMap tempAnswer;
			do {
				tempAnswer=new VariableMap();
				if (!left.satisfy(restrictions, tempAnswer)) {
					lazyMap.put(restrictions, true);
					return false;
				}
			} while (where ^ right.satisfy(tempAnswer, new VariableMap()));
			answer.add(tempAnswer);
			return true;
		}
		
		private boolean operateOr(VariableMap restrictions, VariableMap answer) {
			Object prev=lazyMap.get(restrictions);
			if (prev!=null || !left.satisfy(restrictions, answer)) {
				if (prev!=null && !(Boolean) prev) return false;
				Boolean rightSatisfied=right.satisfy(restrictions, answer);
				lazyMap.put(restrictions, rightSatisfied);
				return rightSatisfied;
			}
			return true;
		}
		
		@Override
		public void startLazy() {
			lazyMap.clear();
			right.startLazy();
			left.startLazy();
		}
	}
	
	
//The Constraint Class	
	private final Node atom;
	private VariableMap current;
	private static final VariableMap noRestrictions=new VariableMap();
	
	@SuppressWarnings("rawtypes")
	public Constraint(Formula f, Object... params) {
		atom=new Atom(f, params);
	}
	
	
	public Constraint(Relation r) {
		atom=new Atom(r, r.args);
	}
	
	
	private Constraint(Node n) {
		atom=n;
	}
	
	
	public Constraint(QueryParameter l, LogicOperator lp, QueryParameter r) {
		if (lp!=null) {
			Node left=(l instanceof Constraint) ? ((Constraint) l).atom : new Atom((Relation)l, ((Relation)l).args);
			Node right=(r instanceof Constraint) ? ((Constraint) r).atom : new Atom((Relation)r, ((Relation)r).args);
			atom=new Junction(left,lp,right);
		}
		else atom=new Atom(LJTrue);
	}
	
	
	public boolean satisfy(VariableMap map) {
		return atom.satisfy(map, new VariableMap());
	}
	
	
	public boolean satisfy(Variable[] vs, Object[] os) {
		if (vs.length>os.length) return false;
		VariableMap map=new VariableMap();
		for (int i=0; i<vs.length; i++) map.updateValsMap(vs[i],os[i]);
		return atom.satisfy(map, new VariableMap());
	}
	
	
	public boolean satisfy(HashMap<Variable,Object> map) {
		return atom.satisfy(new VariableMap(map), new VariableMap());
	}

	
	public boolean satisfy(Object... pairs) {
		if (pairs.length%2==1) return false;
		VariableMap map=new VariableMap();
		int i=-2;
		while ((i=i+2)<pairs.length) {
			if (!variable(pairs[i])) return false;
			map.updateValsMap((Variable) pairs[i], pairs[i+1]);
		}
		return atom.satisfy(map, new VariableMap());
	}
	
	
	@Override
	public String toString() {
		return atom.toString();
	}
	
	
	@Override
	public boolean map(VariableMap m, boolean cut) {
		if (cut) return lz(m);
		if (!lz(m)) return false;
		while (lz(m)) {}
		return true;		
	}
	
	
	protected boolean lz(VariableMap varsMap) {
		VariableMap answer=new VariableMap();
		boolean result;
		if (result=atom.satisfy(noRestrictions, answer))
			varsMap.add(answer);
		current=answer;
		return result;
	}
	
	
	@Override
	public VariableMap lz() {
		VariableMap m=new VariableMap();
		lz(m);
		return m;
	}
	
	
	@Override
	public VariableMap current() {
		return new VariableMap().add(current);
	}
	
	
	public Relation asRelation() {
		if (isRelation()) return ((Atom) atom).relation;
		return LJFalse;
	}
	
	
	public boolean isRelation() {
		return (atom instanceof Atom);
	}
	
	
	public Constraint replaceVariable(Variable v, Object o) {
		return new Constraint(atom.replaceVariable(v, o));
	}
	
	
	@Override
	public Variable[] getVars() {
		return atom.getVars().toArray(new Variable[0]);
	}
	
	
	@Override
	public boolean noVars() {
		return atom.getVars().isEmpty();
	}
	
	
	@Override
	public void resetLazy() {
		atom.startLazy();
	}
	
	
	@Override
	public Constraint base() {
		return this;
	}
	
	
	@SuppressWarnings("unchecked")
	private Object[] restrict(Object[] args, VariableMap restrictions) {
		if (restrictions.isEmpty()) return args;
		Object[] arr=new Object[args.length];
		for (int i=0; i<args.length; i++) {
			arr[i]=restrictions.map.get(args[i]);
			arr[i]=(arr[i]==null)? arr[i]=args[i]: ((List<Object>) arr[i]).get(0);
		}
		return arr;
	}
	
}