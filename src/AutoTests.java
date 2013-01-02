
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import LJava.*;
import static LJava.LJ.*;
import static LJava.Utils.*;
import static LJava.MathFormulas.*;

import org.junit.*;
import static org.junit.Assert.*;

public class AutoTests {
	
	Variable x;		Variable y;		Variable z;		Variable t;		Variable u;		Variable v;		Variable LJvar;
	Container c;
	
	private class Container {
		public int i;			public double d;
		public String s;		public List<Object> l;
		public boolean b;		public Variable v;
		public Constraint c;	public Relation r;
		public Group g;			public Lazy lg;
		public Object[] arr;
		
		public String toString() {
			return "int: "+i+" ; double: "+d+" ; String: "+s+" ; List: "+l
					+"boolean: "+b+" ; Variable: "+v+" ; Constraint: "+c+
					"Relation: "+r+" ; Group: "+g+" ; LazyGroup: "+lg+" ; Array: "+arr;
		}
	}
	
	
	@Before
	public void resetVars(){
		x=new Variable("x");	y=new Variable("y");
		z=new Variable("z");	t=new Variable("t");
		u=new Variable("u");	v=new Variable("v");
		LJvar=var();			c=new Container();
	}
	
	
//Test classes	
	@Test
	public void testAssociation() {
		Association a=new Association("");
		assertEquals(a.name(),"#LJRelation");
		c.b=false;
		c.l=new ArrayList<Object>();		c.l.add(x);		c.l.add("tzali");		c.l.add(10);
		c.v=var();		c.v.set(1,2,3);
		y.set(11,12,13);
		a=new Association("a", 0, c, false, new HashSet(), LJvar, x, y);
		assertEquals(a.toString(), "a(0,int: 0 ; double: 0.0 ; String: null ; List: [$x$, tzali, 10]boolean: false ; Variable: [1,2,3] ; Constraint: nullRelation: null ; Group: null ; LazyGroup: null ; Array: null,false,[],$LJ_Variable$,$x$,[11,12,13])");
		x.set(t);
		t.set(z);
		assertEquals(a.toString(), "a(0,int: 0 ; double: 0.0 ; String: null ; List: [[[$z$]], tzali, 10]boolean: false ; Variable: [1,2,3] ; Constraint: nullRelation: null ; Group: null ; LazyGroup: null ; Array: null,false,[],$LJ_Variable$,[[$z$]],[11,12,13])");
		z.set(abs);
		assertEquals(a.toString(), "a(0,int: 0 ; double: 0.0 ; String: null ; List: [[[[Absolute()]]], tzali, 10]boolean: false ; Variable: [1,2,3] ; Constraint: nullRelation: null ; Group: null ; LazyGroup: null ; Array: null,false,[],$LJ_Variable$,[[[Absolute()]]],[11,12,13])");
		assertTrue(_.equals(a));
		assertTrue(_.equals(c));
		a=a.replaceVariables(LJvar, u);
		assertEquals(a.args()[4],u);
		assertFalse(a.args()[5].equals(u));
		assertFalse(a.args()[6].equals(u));
	}

	
	@SuppressWarnings("rawtypes")
	@Test
	public void testConstraint() {
		//Constructors and satisfy
		Constraint q1=new Constraint(sum, 6,1,2,3);
		assertTrue(q1.satisfy());
		assertEquals(q1.toString(),"Sum(6,1,2,3)");
		Constraint q2=new Constraint(abs, 3,7,20);
		assertFalse(q2.satisfy());
		Constraint q=new Constraint(q1,OR,q2);
		assertTrue(q.satisfy());
		q=new Constraint(q1,AND,q2);
		assertFalse(q.satisfy());
		q1=new Constraint(max, x, 10, 20, 30, y);
		q2=new Constraint(min, 100, x, 300);
		q=new Constraint(q1,AND,q2);
		assertEquals(q.toString(), "(Max([$x$],10,20,30,[$y$])) AND (Min(100,[$x$],300))");
		assertTrue(q.satisfy(x,100,y,100));
		assertFalse(q.satisfy(x,100,y,0));
		assertEquals(q.toString(), "(Max([$x$],10,20,30,[$y$])) AND (Min(100,[$x$],300))");
		q=new Constraint(q1,DIFFER,q2);
		assertFalse(q.satisfy(x,100,y,100));
		assertFalse(q.satisfy(x,100,y,0));
		assertTrue(q.satisfy(x,30,y,0));
		q=new Constraint(q1,WHERE,new Constraint(cmp,0,x,y));
		assertTrue(q.satisfy(x,100,y,100));
		assertFalse(q.satisfy(x,30,y,20));
		//map
		associate(relation("testConstraint", t, true, "Any", 0));
		associate(relation("testConstraint", 1, 1, "Any", 0));
		assertTrue(var(x));
		assertTrue(var(y));
		q1=new Constraint(relation("testConstraint", x, y, "Any", 0),WHERE, c(cmp,0,y,true));
		q2=new Constraint(relation("testConstraint", _, _, t, _),OR,relation("testConstraint", _, _, _, t));
		q=new Constraint(q1,AND,q2);
		VariableMap m=new VariableMap();
		assertTrue(q.map(m,false));
		instantiate(m);
		assertTrue(x.contains("Any"));
		assertFalse(x.contains(0));
		assertFalse(t.contains(0));
		assertTrue(x.contains(t));
		assertTrue(same(y,true));
		q1=q1.replaceVariable(x, z);
		q1=q1.replaceVariable(y, u);
		q2=q2.replaceVariable(t, v);
		q=new Constraint(q2,AND,q1);
		m=new VariableMap();
		assertTrue(q.map(m,false));
		instantiate(m);
		assertTrue(z.contains("Any"));
		assertFalse(z.contains(0));
		assertTrue(v.contains(0));
		assertTrue(z.contains(v));
		assertTrue(same(u,true));
		//lazy and current
		resetVars();
		associate(relation("testConstraint",1,2,3,4,5,6,7,8,9,0));
		associate(relation("testConstraint",'a','b','c','d','e','f','g','h','i','j'));
		associate(relation("testConstraint","s1","s2","s3","s4","s5","s6","s7","s8","s9","s0"));
		associate(relation("testConstraint",1, 'c'));
		q=new Constraint(relation("testConstraint",x,_,_,_,_,_,_,_,_,_));
		m=new VariableMap();
		assertTrue(q.lazy(m));
		assertEquals(m.getVars().size(),1);
		assertTrue(m.getVars().contains(x));
		assertTrue(m.getVals(x) instanceof ArrayList);
		assertEquals(((ArrayList) m.getVals(x)).size(),1);
		assertTrue(((ArrayList) m.getVals(x)).contains(1));
		assertTrue(q.lazy(m));
		assertTrue(q.lazy(m));
		assertFalse(q.lazy(m));
		assertEquals(m.getVars().size(),1);
		assertTrue(m.getVars().contains(x));
		assertTrue(m.getVals(x) instanceof ArrayList);
		assertEquals(((ArrayList) m.getVals(x)).size(),3);
		instantiate(m);
		assertTrue(x.contains(1));
		assertTrue(x.contains('a'));
		assertTrue(x.contains("s1"));
		q=new Constraint(relation("testConstraint",y,_,z,_,_,_,_,_,_,_),WHERE, or(relation("testConstraint",_,z), c(cmp,0,1,y)));
		m=new VariableMap();
		assertTrue(q.lazy(m));
		assertEquals(m.getVars().size(),2);
		assertTrue(m.getVars().contains(y));
		assertTrue(m.getVars().contains(z));
		assertTrue(m.getVals(y) instanceof ArrayList);
		assertTrue(m.getVals(z) instanceof ArrayList);
		assertEquals(((ArrayList) m.getVals(y)).size(),1);
		assertEquals(((ArrayList) m.getVals(z)).size(),1);
		assertTrue(((ArrayList) m.getVals(y)).contains(1));
		assertTrue(((ArrayList) m.getVals(z)).contains(3));
		assertFalse(((ArrayList) m.getVals(y)).contains('a'));
		assertFalse(((ArrayList) m.getVals(z)).contains('c'));
		assertTrue(q.lazy(m));
		assertFalse(q.lazy(m));
		q.startLazy();
		assertTrue(q.lazy(m));
		assertTrue(q.lazy(m));
		assertFalse(q.lazy(m));
		q=q.replaceVariable(y, t);
		q=q.replaceVariable(z, u);		
		m=new VariableMap();
		assertTrue(q.lazy(m));
		assertTrue(q.lazy(m));
		assertFalse(q.lazy(m));
		instantiate(m);
		m=q.current();
		assertEquals(m.getVars().size(),2);
		assertTrue(m.getVars().contains(t));
		assertTrue(m.getVars().contains(u));
		assertTrue(m.getVals(t) instanceof ArrayList);
		assertTrue(m.getVals(u) instanceof ArrayList);
		assertEquals(((ArrayList) m.getVals(t)).size(),1);
		assertEquals(((ArrayList) m.getVals(u)).size(),1);
		assertTrue(((ArrayList) m.getVals(t)).contains('a'));
		assertTrue(((ArrayList) m.getVals(u)).contains('c'));
		assertFalse(((ArrayList) m.getVals(t)).contains(1));
		assertFalse(((ArrayList) m.getVals(u)).contains(3));
		assertTrue(t.contains(1));
		assertTrue(u.contains(3));
		assertTrue(t.contains('a'));
		assertTrue(u.contains('c'));
		assertFalse(t.contains("s1"));
		assertFalse(u.contains("s3"));
	}
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testFormula() {
		final Formula f=new Formula("myF", Boolean.class) {
			@Override
			protected Boolean f(Object... p) {
				Boolean b=false;
				for (Object o : p) b=(b || (Boolean) o);
				return b;
			}};
		assertTrue(f.satisfy(true, false, false, true, false));
		assertTrue((Boolean) f.invoke(false,true,false));
		assertFalse((Boolean) f.invoke(false,false));
		Formula<Boolean, Container> f2= new Formula<Boolean, Container>("myF2", Boolean.class) {
			@Override
			protected Container f(Boolean... p) {
				Container c = new Container();
				if (p.length==0) return c;
				c.b=(Boolean) f.invoke(p);
				c.d=(p[0])? 100.0 : -100.0;
				c.i=(f.satisfy(false, p))? 1 : -1;
				c.r=relation("fromF2",p);
				c.v=var();
				c.v.set(c.b, c.d, c.i, c.r);
				return c;
			}};
		assertTrue(f2.value(false).v.contains(1));
		assertTrue(f2.value(false).v.contains(-100));
		assertTrue(f2.value(false,true).v.contains(-100));
		assertTrue(f2.value(false,true).v.contains(-1));
		assertTrue(f2.value(true,false).v.contains(relation("fromF2",true,false)));
	}
	
	
	@Test
	public void testLazyGroup() {
		Container c=new Container();
		c.lg=new Lazy(new Group("testLazyGroup",1,2,3,4,5,6), new Object[] { x,2,z,4,y,t } );
		assertTrue(c.lg.current().isEmpty());
		VariableMap m=new VariableMap();
		int counter=0;
		while (c.lg.lazy(m)) {
			counter++;
			assertTrue(m.getVars().size()==4);
			assertTrue( ((ArrayList) m.getVals(x)).size()==counter);
			assertTrue( ((ArrayList) m.getVals(y)).size()==counter);
			assertTrue( ((ArrayList) m.getVals(z)).size()==counter);
			assertTrue( ((ArrayList) m.getVals(t)).size()==counter);
		}
		assertTrue(counter==24);
		instantiate(m);
		assertTrue(x.equalValuesSet(y));
		assertTrue(y.equalValuesSet(t));
		assertTrue(z.equalValuesSet(t));
		assertTrue(t.equalValuesSet(x));
		assertTrue(x.contains(1));
		assertTrue(x.contains(3));
		assertTrue(x.contains(5));
		assertTrue(x.contains(6));
		assertFalse(x.contains(2));
		assertFalse(x.contains(4));
		assertTrue(isSet(x,6,y,3,z,1,t,5));
		assertTrue(isSet(x,6,y,5,z,3,t,1));
		assertTrue(isSet(x,1,y,3,z,5,t,6));
		assertFalse(isSet(x,6,y,3,z,6,t,5));
		assertFalse(isSet(x,1,y,3,z,6,t,1));
	}
	
	
	@Test
	public void testLazyAll() {
		
	}
	
	
	@Test
	public void testRelation() {
		associate(relation("testRelation",1,2,3,4,"TM"));
		associate(relation("testRelation",1,'a',3,4,"TM"));
		associate(relation("testRelation",1,2,'b',4,"TM"));
		associate(relation("testRelation",1,2,3,'c',"TM"));
		associate(relation("testRelation","TzaliMaimon",2,3,4,"TM"));
		associate(relation("testRelation",10,20,30,t,"TM"));
		Relation r=relation("testRelation",x,y,_,4,_);
		VariableMap m=new VariableMap();
		assertTrue(r.map(m, CUT));
		instantiate(m);
		assertTrue(x.getValues().length==1);
		assertTrue(y.getValues().length==1);
		assertTrue(same(x,1));
		assertTrue(same(y,2));
		assertTrue(var(t));
		m=new VariableMap();
		Relation r2=relation("testRelation",v,_,u,4,_);
		assertTrue(r2.map(m, !CUT));
		instantiate(m);
		assertTrue(v.getValues().length==4);
		assertTrue(u.getValues().length==4);
		assertTrue(v.contains("TzaliMaimon"));
		assertTrue(u.contains('b'));
		assertFalse(isSet(v,10,u,30));
		assertTrue(isSet(v,1,u,3));
		assertTrue(isSet(v,"TzaliMaimon",u,3));
		assertFalse(isSet(v,1,u,30));
	}
	
	
	@Test
	public void testVariable() {
		
	}
	
	
	@Test
	public void testStrings() {
		
	}
	
	
	@Test
	public void testMathFormulas() {
		
	}
	
	
	@Test
	public void testUtils() {
		
	}
	
	
	@Test
	public void testReflection() {
		
	}
	

	//And then test LJ itself
}