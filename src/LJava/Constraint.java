package LJava;

import static LJava.Utils.LJFalse;
import static LJava.Utils.LJTrue;
import static LJava.LJ.*;
import java.util.HashMap;
import java.util.HashSet;

public class Constraint implements QueryParameter {

	private interface Node {
		public boolean satisfy(VariableMap restrictions, VariableMap answer, boolean cut);
		public Node replaceVariable(Variable v, Object o);
		public HashSet<Variable> getVars();
	}
	

	private final class Atom implements Node {
		private final Relation relation;
		private final Object[] args;
		
		public Atom(Relation r, Object... params) {
			relation=(r==null) ? LJTrue : r;
			args = (params==null) ? new Object[0] : params;
		}
		
		public Atom(Atom a, Variable v1, Object v2) {
			relation=a.relation.replaceVariables(v1, v2);
			args=new Object[a.args.length];
			for (int i=0; i<a.args.length; i++)
				args[i] = (a.args[i]==v1) ? v2 : a.args[i];
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
		public boolean satisfy(VariableMap restrictions, VariableMap answer, boolean cut) {
			//TBD
			return (relation==LJTrue);
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
		public boolean satisfy(VariableMap restrictions, VariableMap answer, boolean cut) {
			//TBD
			return false;
		}
	}
	
	
//The Constraint Class	
	private final Node atom;
	
	public Constraint(Relation r) {
		atom=new Atom(r, r.args);
	}
	
	
	@SuppressWarnings("rawtypes")
	public Constraint(Formula f, Object... params) {
		atom=new Atom(f, params);
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
		return atom.satisfy(map, new VariableMap(), true);
	}
	
	
	public boolean satisfy(Variable[] vs, Object[] os) {
		if (vs.length>os.length) return false;
		VariableMap map=new VariableMap();
		for (int i=0; i<vs.length; i++) map.updateValsMap(vs[i],os[i]);
		return atom.satisfy(map, new VariableMap(), true);
	}
	
	
	public boolean satisfy(HashMap<Variable,Object> map) {
		return atom.satisfy(new VariableMap(map), new VariableMap(), true);
	}

	
	public boolean satisfy(Object... pairs) {
		if (pairs.length%2==1) return false;
		VariableMap map=new VariableMap();
		int i=-2;
		while ((i=i+2)<pairs.length) {
			if (!variable(pairs[i])) return false;
			map.updateValsMap((Variable) pairs[i], pairs[i+1]);
		}
		return atom.satisfy(map, new VariableMap(), true);
	}
	

	public String toString() {
		return atom.toString();
	}
	
	
	@Override
	public boolean map(VariableMap m, boolean cut) {
		//TBD
		return false;
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
	
	
}