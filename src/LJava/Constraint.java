package LJava;

import static LJava.Utils.LJFalse;
import static LJava.Utils.LJTrue;
import static LJava.LJ.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Tzali Maimon
 * Constraint is the class that is responsible for queries in LJ.<p>
 * When you a query using LJ's logic operators a tree is built within a constraint object and is managed from here.<p>
 * Like any other class in LJ Constraint is also thread-safe.<p>
 * Constraint can be activated through the Lazy interface.  
 */
public class Constraint implements QueryParameter, Lazy<LJMap> {

	private interface Node {
		public boolean satisfy(LJMap restrictions, LJMap answer);
		public Node replaceVariable(Variable v, Object o);
		public HashSet<Variable> getVars();
		public void startLazy();
	}
	

	private final class Atom implements Node {
		private final Relation relation;
		private final Object[] args;
		private final HashMap<LJMap, Relation> lazyMap=new HashMap<LJMap, Relation>();
		
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
		public boolean satisfy(LJMap restrictions, LJMap answer) {
			Relation r;
			synchronized (lazyMap) {
				if ((r=lazyMap.get(restrictions))==null) {
					r=r(relation.name, restrict(args, restrictions));
					lazyMap.put(restrictions, r);
				}}
			if ((!relation.isFormula() && r.lz(answer)) || (relation.isFormula() && relation.satisfy(r.args, answer)))
				return (answer.add(restrictions)!=null);
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
		private final ConcurrentHashMap<LJMap, Object> lazyMap=new ConcurrentHashMap<LJMap, Object>();
		
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
		public boolean satisfy(LJMap restrictions, LJMap answer) {			
			if (op==AND) return operateAnd(restrictions, answer);
			else if (op==WHERE) return operateWhere(restrictions, answer, true);
			else if (op==DIFFER) return operateWhere(restrictions, answer, false);
			else if (op==OR) return operateOr(restrictions, answer);
			return true;
		}
		
		private boolean operateAnd(LJMap restrictions, LJMap answer) {
			LJMap knownRestriction;
			do { synchronized (lazyMap) {
				knownRestriction=(LJMap) lazyMap.get(restrictions);
				if (knownRestriction==null) {
					knownRestriction=new LJMap();
					if (!left.satisfy(restrictions, knownRestriction)) return false;
					lazyMap.put(restrictions, knownRestriction);
				}}
				if (right.satisfy(knownRestriction, answer)) return true; 
				lazyMap.remove(restrictions);
			} while (true);
		}
		
		private boolean operateWhere(LJMap restrictions, LJMap answer, boolean where) {
			if (lazyMap.get(restrictions)!=null) return false;
			LJMap tempAnswer;
			do {
				tempAnswer=new LJMap();
				if (!left.satisfy(restrictions, tempAnswer)) {
					lazyMap.put(restrictions, true);
					return false;
				}
			} while (where ^ right.satisfy(tempAnswer, new LJMap()));
			answer.add(tempAnswer);
			return true;
		}
		
		private boolean operateOr(LJMap restrictions, LJMap answer) {
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
	private LJMap current;
	private final LJMap noRestrictions;
	
	/**
	 * @param f - a formula to serve as the constraint
	 * @param params - arguments to serve as the formula's parameters in the constraint.
	 */
	@SuppressWarnings("rawtypes")
	public Constraint(Formula f, Object... params) {
		atom=new Atom(f, params);
		noRestrictions=new LJMap();
	}
	
	
	/**
	 * @param r - a relation that will serve as the constraint.
	 */
	public Constraint(Relation r) {
		atom=new Atom(r, r.args);
		noRestrictions=new LJMap();
	}
	
	
	private Constraint(Node n) {
		atom=n;
		noRestrictions=new LJMap();
	}
	
	
	/**
	 * @param l - a relation, formula or another constraint
	 * @param lp - a logic operator from LJ.LogicOperator
	 * @param r - a relation, formula or another constraint
	 */
	public Constraint(QueryParameter l, LogicOperator lp, QueryParameter r) {
		if (lp!=null) {
			Node left=(l instanceof Constraint) ? ((Constraint) l).atom : new Atom((Relation)l, ((Relation)l).args);
			Node right=(r instanceof Constraint) ? ((Constraint) r).atom : new Atom((Relation)r, ((Relation)r).args);
			atom=new Junction(left,lp,right);
		}
		else atom=new Atom(LJTrue);
		noRestrictions=new LJMap();
	}
	
	
	/**
	 * Checks if the first value of each variable in the LJMap are legal according to this constraint. 
	 * @param map - LJMap containing the required restrictions to check.
	 * @return true if all values are legal.
	 */
	public boolean satisfy(LJMap map) {
		return atom.satisfy(map, new LJMap());
	}
	
	
	/**
	 * Checks to see if the Variables in vs can be swapped by the corresponding values in os within the constraint legally.  
	 * @param vs - a variables array
	 * @param os - an objects array  
	 * @return true if after swapping the constraint is still satisfied.
	 */
	public boolean satisfy(Variable[] vs, Object[] os) {
		if (vs.length>os.length) return false;
		LJMap map=new LJMap();
		for (int i=0; i<vs.length; i++) map.updateValsMap(vs[i],os[i]);
		return atom.satisfy(map, new LJMap());
	}
	
	
	/**
	 * Creates an LJMap out of the given HashMap and activates satisfy(LJMap)
	 * @param map - a hash map
	 * @return - satisfy(LJMap)
	 */
	public boolean satisfy(HashMap<Variable,Object> map) {
		return atom.satisfy(new LJMap(map), new LJMap());
	}

	
	/**
	 * Divides the given arguments into pairs of Variable, Object and returns true if the constraint is satisfied after swapping each variable within it with the corresponding object.<p>
	 * EXAMPLE: satisfy(x,1,y,"A",z, new Relation("try it");<p>
	 * NOTE: a wrong format in the argument will cause the method to return false as it's obvious that the constraint isn't satisfied in such a case. 
	 * @param pairs - arguments in pairs
	 * @return true if after swapping the constraint is still satisfied.
	 */
	public boolean satisfy(Object... pairs) {
		if (pairs.length%2==1) return false;
		LJMap map=new LJMap();
		int i=-2;
		while ((i=i+2)<pairs.length) {
			if (!variable(pairs[i])) return false;
			map.updateValsMap((Variable) pairs[i], pairs[i+1]);
		}
		return atom.satisfy(map, new LJMap());
	}
	
	
	@Override
	public String toString() {
		return atom.toString();
	}
	

	@Override
	public boolean map(LJMap m, boolean cut) {
		if (cut) return lz(m);
		AtomicBoolean result=new AtomicBoolean(false);
		AtomicBoolean go=new AtomicBoolean(true);
		AtomicInteger workCount=new AtomicInteger(0);
		while (go.get()) {
			if (ThreadsManager.free()) {
				workCount.incrementAndGet();
				ThreadsManager.assign(new RunLazy(m, result, go, workCount));
			}
		}
		while (workCount.get()>0) {}
		return result.get();		
	}  


	protected boolean lz(LJMap varsMap) {
		LJMap answer=new LJMap();
		boolean result;
		if (result=atom.satisfy(noRestrictions, answer))
			varsMap.add(answer);
		current=answer;
		return result;
	}
	
	
	@Override
	public LJMap lz() {
		LJMap m=new LJMap();
		lz(m);
		return m;
	}
	
	
	@Override
	public LJMap current() {
		return new LJMap().add(current);
	}
	
	
	/**
	 * returns this constraint as a relation if it can be represented as such. It means that the constraint only holds a single restriction and it is that the contained relation needs to be satisfied.
	 * @return a relation or LJFalse if not possible
	 */
	public Relation asRelation() {
		if (isRelation()) return ((Atom) atom).relation;
		return LJFalse;
	}
	
	
	/**
	 * @return true if this constraint can be represented as a relation.
	 */
	public boolean isRelation() {
		return (atom instanceof Atom);
	}
	
	
	/**
	 * replace the variable v with the object o within the constraint and returning a new constraint (this constraint won't change).
	 * @param v - a variable to replace
	 * @param o - a target object 
	 * @return - a new constraint.
	 */
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
	public Object base() {
		return this;
	}
	
	
	@SuppressWarnings("unchecked")
	private Object[] restrict(Object[] args, LJMap restrictions) {
		if (restrictions.isEmpty()) return args;
		Object[] arr=new Object[args.length];
		for (int i=0; i<args.length; i++) {
			arr[i]=restrictions.map.get(args[i]);
			arr[i]=(arr[i]==null)? arr[i]=args[i]: ((List<Object>) arr[i]).get(0);
		}
		return arr;
	}
	
	
	private class RunLazy implements Runnable {
		private LJMap map;
		private AtomicBoolean b;
		private AtomicBoolean work;
		private AtomicInteger count;
		public RunLazy(LJMap m, AtomicBoolean result, AtomicBoolean go, AtomicInteger c) {
			map=m;
			b=result;
			work=go;
			count=c;
		}
		@Override
		public void run() {
			boolean result=lz(map);
			b.compareAndSet(false, result);
			work.compareAndSet(true, result);
			count.decrementAndGet();
			ThreadsManager.done();
		}
	}
}