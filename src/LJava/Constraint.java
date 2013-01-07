package LJava;
import static LJava.Utils.LJFalse;
import static LJava.Utils.LJTrue;
import static LJava.LJ.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicBoolean;

import LJava.LJ.LJIterator;

public class Constraint implements QueryParameter {

	private VariableMap current=new VariableMap();
	private AtomicBoolean inSet=new AtomicBoolean(false);
	
	private interface Node {
		public boolean satisfy(VariableMap restrictions);
		public Node replaceVariable(Variable v, Object o);
		public HashSet<Variable> getVars();
		public boolean conduct(VariableMap restrictions, VariableMap answer);
		public void startLazy();
	}
	

	private final class Atom implements Node {
		private final Relation relation;
		private final Object[] args;
		private LJIterator iterator;
		
		public Atom(Relation r, Object... params) {
			relation=(r==null) ? LJTrue : r;
			args = (params==null) ? new Object[0] : params;
			iterator=emptyIterator;
		}
		
		public Atom(Atom a, Variable v1, Object v2) {
			relation=(Relation) a.relation.replaceVariables(v1, v2);
			args=new Object[a.args.length];
			for (int i=0; i<a.args.length; i++)
				args[i] = (a.args[i]==v1) ? v2 : a.args[i];
			iterator=emptyIterator;
			//debug("new Constraint Atom with value: "+v2+" instead of "+v1);
		}
		
		public String toString() {	
			StringBuilder s = new StringBuilder(relation.name()+"(");
			if (args.length>0) {
				for (int i=0; i<args.length; i++) s.append(string(args[i])+",");	
				s.deleteCharAt(s.length()-1);
			}
			s.append(")");
			return s.toString();
		}
		
		@Override
		public boolean satisfy(VariableMap restrictions) {
			//debug("Satisfying relation: "+relation+" under restrictions: "+restrictions);
			if (restrictions.isEmpty()) return relation.satisfied(args, new VariableMap(), true);
			return relation.satisfied(restrict(restrictions,args), new VariableMap(), true);
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
		public boolean conduct(VariableMap restrictions, VariableMap answer) {
			Relation r;
			if (!restrictions.isEmpty()) {
				Object[] arr=restrict(restrictions,args);
				r=relation(relation.name, arr);
			}
			else if (relation.isFormula()) r=relation(relation.name, args);
			else r=relation;
			if (iterator==emptyIterator) iterator=iterate(r.args.length);
			//debug("Conduction relation: "+r+" at Atom");
			Association a;
			while ((a=iterator.hasAndGrabNext(r.args))!=undefined) 
				if (evaluate(r, answer, iterator, a)) {
					answer.add(restrictions);
					return true; 
				}
			return false;
		}
		
		@Override
		public void startLazy(){
			//debug("Starting Lazy at Atom");
			iterator=emptyIterator;
		}
	}
	
	
	private final class Junction implements Node {
		private final Node left;
		private final Node right;
		private final LogicOperator op;
		
		public Junction(Node l, LogicOperator lp, Node r) {
			right = (r==null) ? new Atom(LJTrue) : r;
			left = (l==null) ? new Atom(LJTrue) : l;
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
		public Node replaceVariable(Variable v, Object o) {
			//debug("Replacing variable: "+v+" at Junction: "+this+" with value: "+o);
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
		public boolean conduct(VariableMap restrictions, VariableMap answer) {
			VariableMap tempAnswer;
			if (op==WHERE) {
				//debug("Conducting WHERE at Junction: "+this);
				do {
					tempAnswer=new VariableMap();
					if (!left.conduct(restrictions, tempAnswer)) return false;
				} while (!right.satisfy(tempAnswer));
				answer.add(tempAnswer);
				//debug("Returning true answer: "+answer);
			}
			else if (op==AND) {
				//debug("Conducting AND at Junction: "+this);
				do {
					tempAnswer=new VariableMap();
					right.startLazy();
					if (!left.conduct(restrictions, tempAnswer)) return false;
				} while (!right.conduct(tempAnswer, answer));
				//debug("Returning true answer: "+answer);
			}
			else if (op==DIFFER) {
				//debug("Conducting DIFFER at Junction: "+this);
				do {
					tempAnswer=new VariableMap();
					if (!left.conduct(restrictions, tempAnswer)) return false;
				} while (right.satisfy(tempAnswer));
				answer.add(tempAnswer);
				//debug("Returning true answer: "+answer);
			}
			else if (op==OR) {
				//debug("Conducting OR at Junction: "+this);
				if (!left.conduct(restrictions, answer) && !right.conduct(restrictions, answer)) return false;
				//debug("Returning true answer: "+answer);
			}
			return true;
		}
		
		@Override
		public void startLazy() {
			//debug("Starts Lazy at Junction: "+this);
			left.startLazy();
			right.startLazy();
		}
	}
	
	private final Node atom;
	
	public Constraint(Relation r, Object... params) {
		atom=new Atom(r, params);
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
		//debug("Satisfying Constraint: "+this);
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
		if (cut) return lz(m);
		if (!lz(m)) return false;
		while (lz(m)) {}
		return true;
	}
	
	
	public boolean lz(VariableMap m) {
		VariableMap answer=new VariableMap();
		if (atom.conduct(new VariableMap(), answer)) {
			m.add(answer);
			if (inSet.compareAndSet(false, true)) {
				current=new VariableMap();
				current.add(answer);
				inSet.set(false);
			}
			return true;
		}
		return false;
	}
	
	
	public final VariableMap lz() {
		VariableMap m=new VariableMap();
		if (!lz(m)) return new VariableMap();
		return m;
	}
	
	
	public final VariableMap current() {
		VariableMap map=new VariableMap();
		if (inSet.compareAndSet(false,true)) {
			map.add(current);
			inSet.set(false);
		}
		return map;
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
	
	
	public HashSet<Variable> getVars() {
		return atom.getVars();
	}
	
	
	public void startLazy() {
		atom.startLazy();
	}
	
	
	@SuppressWarnings("rawtypes")
	private Object[] restrict(VariableMap restrictions, Object[] args) {
		//debug("Restricting: "+restrictions+" upon: "+string(args));
		Object[] arr = new Object[args.length];
		for (int i=0; i<arr.length; i++) {
			arr[i]=restrictions.map.get(args[i]);
			arr[i]=(arr[i]==null)? args[i] : ((ArrayList) arr[i]).get(0);
		}
		return arr;
	}
	

	
}


/* to fix:
 * Restrictions for conduct not perfect for Formula (that has multi vars in args) in Atom.
 * IF NOT PREVENTING THREAD-SAFE: the implementation of the restrictions on arr needs to be above Atom.conduct and not inside it.
 * Junction's conduct at the AND case isn't concurrent + a AND b isn't equal b AND a!
 */
