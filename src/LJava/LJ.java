package LJava;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * @author Tzali Maimon
 * The core library of LogicJava.<p>
 * This is meant to be a singleton. you should use import static when using this library to fully experience how LJ bypass Java's OOP.
 */
public final class LJ {
	
	protected static final HashMap<Integer, LinkedHashSet<Association>> LJavaRelationTable=new HashMap<Integer, LinkedHashSet<Association>>();
	/**
	 * Association which is the same as any single other object. read on for same()
	 */
	public final static Association _=new Association("_");
	/**
	 * Association which means an undefined result was caused during a process internally. This isn't an error of program but rather an error in logic. 
	 */
	public final static Association undefined=new Association("$undefined$");
	/**
	 * Association which equals no other object, not even itself.
	 */
	public final static Association none=new Association("$no_variable_value$");
	protected static final LJIterator emptyIterator=iterate(-2);
	
	/**
	 * On the meaning of cut you can read at logicjava.wordpress.com.
	 */
	static public final boolean CUT=true;
	static public final LogicOperator OR=LogicOperator.OR;
	static public final LogicOperator AND=LogicOperator.AND;
	static public final LogicOperator DIFFER=LogicOperator.DIFFER;
	static public final LogicOperator WHERE=LogicOperator.WHERE;
	/**
	 * @author Tzali Maimon
	 * LJ's logic operators. Differ means "not WHERE" 
	 */
	static public enum  LogicOperator{	OR, AND, DIFFER , NONE, WHERE  }	
	
	//System Properties
	/**
	 * @author Tzali Maimon
	 * LJ's inner properties. <p>
	 * you can set properties by using setLJProperty().<p>
	 * DoubleTolerance is set by default to 0.00000000000001. This is used where two doubles are compared within LJ's same() method.<p>
	 * ThreadCount is set by default to 2. Threads are activated internally by LJ for its own purposes. This property limits the number of threads that LJ is allowed to use besides the main program's thread. NOTE: setting this to anything less than 1 will causes LJ to stop working altogether!.
	 */
	static public enum  Property { DoubleTolerance, ThreadCount  }
	protected static double doubleTolerance=0.00000000000001;
	protected static int threadCount=2;

	
	/**
	 * returns a copy of the "world" of rules/facts. <p>
	 * The copy returned has no effect on LJ's "world" besides the fact that they share the objects that appear in their rules' arguments.<p>
	 * So you can change the world you are given but changing the objects in it's rules will effect LJ.<p>
	 * This method tries to return the "latest" version of world possible, so if a rule was added after the method was called it might just slip into the copy but it might as well not.<p>
	 * Besides that there's no worry about Thread-Safety. 
	 * @return a copy of the "world" of rules/facts.
	 */
	public static HashMap<Integer, LinkedHashSet<Association>> world() {
		HashMap<Integer, LinkedHashSet<Association>> w=new HashMap<Integer, LinkedHashSet<Association>>();
		HashSet<Integer> keys=new HashSet<Integer>();
		synchronized (LJavaRelationTable) { keys.addAll(LJavaRelationTable.keySet()); }
		for (Integer len: keys) {
			LinkedHashSet<Association> set=LJavaRelationTable.get(len);
			LinkedHashSet<Association> copy=new LinkedHashSet<Association>();
			synchronized (set) { copy.addAll(set); }
			w.put(len, copy);
		}
		return w;
	}
	
	
	/**
	 * @param r an Association to place in the "world"
	 * @return true if successful.
	 */
	public static boolean associate(Association r) {
		if (r==undefined || r==none) return false;
		addTo(LJavaRelationTable, r.argsLength(), r, LinkedHashSet.class);
		return true;
	}
	

	/**
	 * @param args - arguments to relate with each other. This doesn't require a naming to the Relation.
	 * @return true if successful
	 */
	public static boolean relate(Object... args) {		
		Relation r=new Relation("#LJavaRelationTableEntry#", args);
		return associate(r);
	}	

	
	/**
	 * @param args - arguments to group (without meaning to order of appearance). Read Group class java doc for more info. 
	 * @return true if successful
	 */
	public static boolean group(Object... args) {		
		Group r=new Group("#LJavaGroupTableEntry#", args);
		return associate(r);
	}
	
	
	/**
	 * @param g - a group to activate lazy on
	 * @param args - arguments to use in the lazy where some should be variables in order to get anything back from the lazy.
	 * @return - a Lazy Object that returns LJMap from its lz()
	 */
	public static Lazy<LJMap> lz(Group g, Object... args) {
		return g.goLazy(args);
	}
	
	/**
	 * Evaluates a formula f and returns the value of it. The arguments that are used each time are saved internally starting from params and are advanced by the inc Formula after each call to evaluation. 
	 * @param f - a formula to activate the lazy on
	 * @param inc - a formula that increments the inner arguments of f on each lazy evaluation
	 * @param params - the start value to start evaluate from.
	 * @return a lazy object returning the type R that is returned from the Formula f
	 */
	public static <P,R> Lazy<R> lz(Formula<P,R> f, Formula<P,P[]> inc, P... params) {
		return f.goLazy(inc, params);
	}
	
	
	/**
	 * Activates an LJ query with Lazy interface.
	 * @param a - the left query parameters
	 * @param op - an LJ logic operator
	 * @param b - the right query parameters
	 * @return a lazy object that returns LJMap
	 */
	public static Lazy<LJMap> lz(QueryParameter a, LogicOperator op, QueryParameter b) {
		return lz(new Constraint(a,op,b));
	}
	
	
	/**
	 * @param c - a constraint to treat as lazy
	 * @return a lazy interface that returns LJMap 
	 */
	public static Lazy<LJMap> lz(Constraint c) {
		return c;
	}
	
			
	/**
	 * returns e(a)
	 * @param a - a query parameter
	 * @return true if the given parameter exists in the "world" legally.
	 */
	public static boolean exists(QueryParameter a) {
		return e(a);
	}
	
	
	/**
	 * This fills the variables inside the query parameter with the first legal rule in "world"
	 * @param a - a query parameter
	 * @return true if the given parameter exists in the "world" legally.
	 */
	public static boolean e(QueryParameter a) {
		return query(a,true); 
	}
	
	
	/**
	 * returns e(a,op,b)
	 * @param a - left query parameter
	 * @param op - LJ logic operator
	 * @param b - right query parameter
	 * @return true if found at least one answer to the query.
	 */
	public static boolean exists(QueryParameter a, LogicOperator op, QueryParameter b) {
		return e(a,op,b);
	}
	
	/**
	 * This fills the variables inside the query parameters with the first legal rule in "world"
	 * @param a - left query parameter
	 * @param op - LJ logic operator
	 * @param b - right query parameter
	 * @return true if found at least one answer to the query.
	 */
	public static boolean e(QueryParameter a, LogicOperator op, QueryParameter b) {
		return e(new Constraint(a,op,b));
	}
	
	
	/**
	 * @param args - arguments
	 * @return e(args)
	 */
	public static boolean exists(Object... args) {
		return e(args);
	}
	
	
	/**
	 * Fills the variables in args with the first possible answer from the "world".
	 * @param args - arguments to search as relation in the "world". the relation searched has no meaningful name.
	 * @return true if found at least one answer.
	 */
	public static boolean e(Object... args) {
		Relation r=new Relation("#query",args);
		return e(r);
	}
		

	/**
	 * @param a - query parameter
	 * @return a(a)
	 */
	public static boolean all(QueryParameter a) {
		return a(a);
	}
	
	
	/**
	 * This searches the "world" and fill in variables within the query parameter with legal values.
	 * activates a(a)
	 * @param a - a query parameter
	 * @return true if found any answer 
	 */
	public static boolean a(QueryParameter a) {
		return query(a,false);
	}
	
	
	/**
	 * Activates a(a,op,b)
	 * @param a left query parameter
	 * @param op LJ logic operator
	 * @param b right query parameter
	 * @return true if found any legal values for the variables.
	 */
	public static boolean all(QueryParameter a, LogicOperator op, QueryParameter b) {
		return a(a,op,b);
	}
	
	/**
	 * This searches the "world" and fill in variables within the query parameters with legal values.
	 * @param a left query parameter
	 * @param op LJ logic operator
	 * @param b right query parameter
	 * @return true if found any legal values for the variables.
	 */	
	public static boolean a(QueryParameter a, LogicOperator op, QueryParameter b) {
		return a(new Constraint(a,op,b));
	}
	
	
	/**
	 * @param args - arguments
	 * @return a(args)
	 */
	public static boolean all(Object... args) {
		return a(args);
	}
	
	
	/**
	 * Searches the arguments without a meaningful name, just like e(args) but fills the variables in args with all possible answers.
	 * @param args
	 * @return true if found any answer
	 */
	public static boolean a(Object... args) {
		Relation r=new Relation("#query",args);
		return a(r);
	}
	
	
	/**
	 * @param f a formula
	 * @param args arguments
	 * @return new Constraint(f,args)
	 */
	@SuppressWarnings("rawtypes")
	public static Constraint c(Formula f, Object... args) {
		return new Constraint(f,args);
	}
	
	
	/**
	 * @param l a left query parameter
	 * @param lp LJ logic operator
	 * @param r a right query parameter
	 * @return new Constraint(l,lp,r)
	 */
	public static Constraint c(QueryParameter l, LogicOperator lp, QueryParameter r) {
		return new Constraint(l,lp,r);
	}
	
		
	/**
	 * @param f Formula
	 * @param args arguments
	 * @return c(f,args)
	 */
	@SuppressWarnings("rawtypes")
	public static Constraint condition(Formula f, Object... args) {
		return c(f,args);
	}

	
	/**
	 * @param l a left query parameter
	 * @param lp LJ logic operator
	 * @param r a right query parameter
	 * @return c(l,lp,r)
	 */
	public static Constraint condition(QueryParameter l, LogicOperator lp, QueryParameter r) {
		return c(l,lp,r);
	}

	
	private static boolean query(QueryParameter a, boolean cut) {
		LJMap varValues=new LJMap();
		if (!a.map(varValues,cut)) return false;
		return instantiate(varValues);
	}
	
	
	@SuppressWarnings("rawtypes")
	protected static boolean evaluate(Relation r, LJMap varValues, LJIterator i) {
		Association element;
		while ((element=i.hasAndGrabNext(r.args))!=undefined)
			if (element.associationNameCompare(r) && element.satisfy(r.args, varValues)) {				
				if (element.isLazy() && ((Lazy) element).noVars()) i.noLazyGroup();
				return true;
			} else i.noLazyGroup();
		return false;
	}
	
	
	/**
	 * This takes two queries and unite them with an AND between them. And is true only when the left query returned an answer that the right side was satisfied by.<p>
	 * It also takes all the possible answers from the right parameter as long as they are legal under the constraints of the left side.  in the case of using "all".<p>
	 * And instantiate all the variables in both query parameters. 
	 * @param a a query parameter (relation or constraint)
	 * @param b a query parameter (relation or constraint)
	 * @return A constraint representing the new query
	 */
	public static Constraint and(QueryParameter a, QueryParameter b) {
		return new Constraint(a,AND,b);
	}
	

	/**
	 * This takes two queries and unite them with an OR between them. Or is true if either side presents an answer from the "world" which is true. 
	 * @param a a query parameter (relation or constraint)
	 * @param b a query parameter (relation or constraint)
	 * @return A constraint representing the new query
	 */
	public static Constraint or(QueryParameter a, QueryParameter b) {
		return new Constraint(a,OR,b);
	}
	

	/**
	 * this is equal to not WHERE.
	 * @param a a query parameter (relation or constraint)
	 * @param b a query parameter (relation or constraint)
	 * @return A constraint representing the new query
	 */
	public static Constraint differ(QueryParameter a, QueryParameter b) {
		return new Constraint(a,DIFFER,b);
	}
	

	/**
	 * Where operates just like AND with one important difference: it only instantiate the variables in the left parameter thus saving time on searching all the answers of the right side.<p>
	 * It is most useful to use WHERE when the right side is a Formula.
	 * @param a a query parameter (relation or constraint)
	 * @param b a query parameter (relation or constraint)
	 * @return A constraint representing the new query
	 */
	public static Constraint where(QueryParameter a, QueryParameter b) {
		return new Constraint(a,WHERE,b);
	}
	
	
	/**
	 * Checks if the LJ variables contains the values in os as their first answer.<p>
	 * For example: if x and y are LJ variables with [1,2] and [2,3] values so inSet([x,y],[1,2]) is true but inSet([x,y],[2,3]) is false.
	 * @param vs LJ Variables
	 * @param os Objects
	 * @return true if the variables contain the values given in os as their first answer.
	 */
	public static boolean isSet(Variable[] vs, Object[] os) {
		if (vs==null || os==null) return false;
		if (vs.length!=os.length) return false;
		if (vs.length==0) return true;
		for (int i=0; i<vs[0].getValues().length; i++) {
			boolean is=true;
			for (int j=0; j<os.length; j++) 
				if (!same(vs[j].get(i), os[j])) {
					is=false;
					break;
				}
			if (is) return true;
		}
		return false;
	}
	
	
	/**
	 * pairs up the arguments into vs and os in pairs Variable, Object and activates inSet(vs, os)
	 * @param args - arguments
	 * @return inSet(vs,os)
	 */
	public static boolean isSet(Object... args) {
		if (args.length%2==1) return false;
		Variable[] vs=new Variable[args.length/2];
		Object[] os=new Object[args.length/2];
		int i=-2;
		while ((i=i+2)<args.length) {
			if (!variable(args[i])) return false;
			vs[i/2]=(Variable) args[i];
			os[i/2]=args[i+1];
		}
		return isSet(vs,os);		
	}
	
	
	/**
	 * @param x
	 * @return true if x is Variable
	 */
	public static boolean variable(Object x) {
		return (x instanceof Variable);
	}
	
	
	/**
	 * @param x
	 * @return true if x is a Variable which wasn't instantiated yet
	 */
	public static boolean var(Object x) {
		if (variable(x)) return var(((Variable) x));
		return false;
	}
	

	/**
	 * @param x
	 * @return true if x is a Variable which wasn't instantiated yet
	 */
	public static boolean var(Variable x) {
		return x.isVar();
	}
	
	
	/**
	 * @return new Variable()
	 */
	public static Variable var() {
		return new Variable();
	}
	
	
	/**
	 * Returns an array of Variables using name for naming them all by index (so if name="x" then the names of the Variables will be "x1","x2"...)
	 * @param name - naming 
	 * @param size - size of array wanted
	 * @return a new array of Variables.
	 */
	public static Variable[] varArray(String name, int size) {
		Variable[] arr=new Variable[size];
		for (int i=0; i<size; i++) arr[i]=new Variable(name+i);
		return arr;
	}
	
	
	/**
	 * @param size
	 * @return varArray("LJ_Variable", size);
	 */
	public static Variable[] varArray(int size) {
		return varArray("LJ_Variable", size);
	}
	
	
	/**
	 * @param n a name
	 * @param args arguments
	 * @return new Relation(n,args)
	 */
	public static Relation r(String n,Object... args) {
		return new Relation(n,args);
	}
	
	
	/**
	 * @param n - name
	 * @param args - arguments
	 * @return r(n,args)
	 */
	public static Relation relation(String n,Object... args) {
		return r(n,args);
	}
	

	/**
	 * @param f - a formula
	 * @param args - arguments
	 * @return - a relation created from the formula over the arguments
	 */
	@SuppressWarnings("rawtypes")
	public static Relation r(Formula f,Object... args) {
		return new Relation(f.name,args);
	}
	
	
	/**
	 * @param f - a formula
	 * @param args - arguments
	 * @return - a relation created from the formula over the arguments
	 */
	@SuppressWarnings("rawtypes")
	public static Relation relation(Formula f,Object... args) {
		return r(f,args);
	}

	
	/**
	 * @param args
	 * @return a new relation with no meaningful name
	 */
	public static Relation r(Object... args) {
		return new Relation("",args);
	}

	
	/**
	 * @param args
	 * @return a new relation with no meaningful name
	 */
	public static Relation relation(Object... args) {
		return r(args);
	}
	
	
	/**
	 * @param o an object
	 * @return the value of this object. has no effect for objects which aren't Variables and for those returns the first value they hold.
	 */
	public static Object val(Object o) {
		if (variable(o)) return ((Variable) o).get(0);
		return o;
	}
	
	
	/**
	 * This method should always be used instead of Java's Object.equal method. <p>
	 * This method handles Variables that has values in them as if they were these values. The method consider the first value to mean and the rest do not.
	 * So if v is a Variable and O is an object and v hold O as its first value, o.equals(v) will be false but same(o,v) will be true.
	 * @param a object
	 * @param b object
	 * @return true if objects are the same
	 */
	public static boolean same(Object a, Object b) {
		if ((variable(a)) || (a instanceof Association)) return a.equals(b);
		if ((a instanceof Number) && (b instanceof Number))
				return (Math.abs(((Number) a).doubleValue()-((Number) b).doubleValue())<doubleTolerance);
		return b.equals(a);
	}
	
	
	/**
	 * @param arr an array
	 * @param f - Formula
	 * @return an array where the cell i is the result of activating f on arr[i] 
	 */
	@SuppressWarnings("rawtypes")
	public static Object[] deepInvoke(Object[] arr, Formula f) {
		Object[] a=new Object[arr.length];
		for (int i=0; i<arr.length; i++) a[i]=f.invoke(arr[i]);
		return a;
	}
	
	
	/**
	 * @param o
	 * @return val(o)==undefined
	 */
	public boolean undef(Object o) {
		return (val(o)==undefined);
	}
	

	/**
	 * Tries to instantiate all Variables in the LJMap with the values in that map.<p>
	 * NOTE: LJ Variable turns immutable after first instantiation. So this method returns false if a Variable is already instantiated once it tries to isntantiate it.<p>
	 * That doesn't prevent it from instantiating the other variables. 
	 * @param varValues LJMap
	 * @return true if successful
	 */
	public static boolean instantiate(LJMap varValues) {
        boolean answer=true;
		for (Variable v : varValues.getVars())
			answer=(v.instantiate(varValues.map.get(v), null, varValues.constraints.get(v)) && answer);	
		return answer;
	}
	

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected static <K,V> void addTo(Map map, K key, V val, Class<?> type) {
		Collection collection=(Collection) map.get(key);
		if (collection==null)
			try{
				collection=(Collection) type.newInstance();
				synchronized (map) { map.put(key, collection); }
			}catch (Exception e){}
		synchronized (collection) { collection.add(val); }
	}	
	
	
	protected static <T> void increment(Map<T,Integer> m, T element, int delta) {
		Integer count=m.get(element);
		if (count==null) count=0;
		m.put(element,count+delta);	
	}		
	
	
	protected static String string(Object o) {
		if (o==null) return "null";
		if (variable(o)) return "[$"+((Variable) o).getID()+"$]";
		if (o.getClass().isArray()) {
			StringBuilder s=new StringBuilder("[");
			for (Object obj: (Object[]) o) s.append(string(obj)+",");
			if (s.length()>1) s.deleteCharAt(s.length()-1);
			s.append("]");
			return s.toString();
		}
		return o.toString();
	}
	
	
	protected static LJIterator iterate(int index) {
		return new LJ().new LJIterator(index);
	}
	
	
	/**
	 * Setting the LJProperty to a new value. Watch out. this shouldn't be while threading. Don't change LJ's properties while using its commands in another thread.
	 * @param p LJProperty
	 * @param v value
	 */
	public static void setLJProperty(Property p, Object v) {
		try {
			switch (p) {
			case DoubleTolerance: doubleTolerance=(Double)v; break;
			case ThreadCount: threadCount=(Integer)v; break;
			default: break;
			}
		}catch (Exception e) {}
	}
		
	
//An inner LJ iterator.	
	protected final class LJIterator {
		public Iterator<Association> i;
		public boolean onFormulas;
		public Association lazyGroup;
		
		public LJIterator(int index) {
			onFormulas=false;
			lazyGroup=none;
			LinkedHashSet<Association> table=LJavaRelationTable.get(index);
			if (table==null) {
				table=LJavaRelationTable.get(-1);
				onFormulas=true;
				i=(table==null) ? null : table.iterator();
			}
			else i=table.iterator();
		}
		
		private boolean hasNext() {
			if (i==null) return false;
			return (i.hasNext() || lazyGroup!=none || (!onFormulas && LJavaRelationTable.get(-1)!=null));
		}
		
		private Association next() {
			if (lazyGroup!=none) return lazyGroup;
			if (i.hasNext()) return i.next();
			i=LJavaRelationTable.get(-1).iterator();
			onFormulas=true;
			return i.next();
		}
		
		public synchronized Association hasAndGrabNext(Object[] args) {
			if (!hasNext()) return undefined;
			Association element=next();
			if (element.isGroup()) {
				element=((Group) element).goLazy(args);
				lazyGroup=element;
			}			
			return element;
		}
		
		public synchronized void noLazyGroup() {
			lazyGroup=none;
		}
		
	}	
	
	
	protected static class ThreadsManager {
		public static AtomicInteger workingThreads=new AtomicInteger(0);
		public static ExecutorService pool = Executors.newCachedThreadPool();
		public static ArrayList<Runnable> queue=new ArrayList<Runnable>();
		
		public synchronized static void assign(Runnable r) {
			if (workingThreads.get()<threadCount) {
				workingThreads.incrementAndGet();
				pool.execute(r);
			}
			else queue.add(r);
		}
		
		public synchronized static void done() {
			workingThreads.decrementAndGet();
			if (workingThreads.get()<threadCount && !queue.isEmpty()) {
				workingThreads.incrementAndGet();
				pool.execute(queue.remove(0));
			}
		}
		
		public synchronized static boolean free() {
			return (workingThreads.get()<threadCount);
		}
	}
	
	
//Queries parameters interface
	public interface QueryParameter {
		public boolean map(LJMap m, boolean cut);
	}
	
	
}


/* future plan:
 * - DataBase structure into hamt and ability to load, save and switch worlds inside the memory without disk.
*/

/* Next version:
 * - Reverse Formulas.  
 * - the Future Answer ability.
 * - smart map() in constraint, only use threads when size of constraint is big.
 * - distinct solutions from query (without duplications).
*/