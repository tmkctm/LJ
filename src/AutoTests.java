
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
		public Group g;			public LazyGroup lg;
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

	
	@Test
	public void testConstraint() {
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
		associate(relation("testConstraint", t, true, "Any", 0));
		associate(relation("testConstraint", 1, 1, "Any", 0));
		assertTrue(var(x));
		assertTrue(var(y));
		q1=new Constraint(relation("testConstraint", x, y, "Any", 0),WHERE,condition(cmp,0,y,true));
		q2=new Constraint(relation("testConstraint", _, _, t, _),OR,relation("testConstraint", _, _, _, t));
		q=new Constraint(q1,AND,q2);
		VariableMap m=new VariableMap();
		assertTrue(q.map(m,false));
		System.out.println(m);
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
		System.out.println(m);
		instantiate(m);
		assertTrue(z.contains("Any"));
		assertFalse(z.contains(0));
		assertTrue(v.contains(0));
		assertTrue(z.contains(v));
		assertTrue(same(u,true));	
	}
}