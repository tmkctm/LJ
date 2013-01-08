import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import LJava.*;
import static LJava.LJ.*;
import static LJava.Utils.*;
import static LJava.MathFormulas.*;
import static LJava.Reflection.*;

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
		a=new Association("a", 0, c, false, new HashSet<Object>(), LJvar, x, y);
		x.set(t);
		t.set(z);
		z.set(abs);
		assertTrue(_.equals(a));
		assertTrue(_.equals(c));
		a=a.replaceVariables(LJvar, u);
		assertEquals(a.args()[4],u);
		assertFalse(a.args()[5].equals(u));
		assertFalse(a.args()[6].equals(u));
	}

/*	
	@SuppressWarnings("rawtypes")
	@Test
	public void testConstraint() {
		//Constructors and satisfy
		Constraint dump=new Constraint(pow);
		assertTrue(dump.satisfy());
		assertEquals(dump.toString(),"Power()");
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
		String s=q1.toString();
		q1.replaceVariable(x, z);
		assertTrue(s.equals(q1.toString()));
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
		assertTrue(q.lz(m));
		assertEquals(m.getVars().size(),1);
		assertTrue(m.getVars().contains(x));
		assertTrue(m.getVals(x) instanceof ArrayList);
		assertEquals(((ArrayList) m.getVals(x)).size(),1);
		assertTrue(((ArrayList) m.getVals(x)).contains(1));
		assertTrue(q.lz(m));
		assertTrue(q.lz(m));
		assertFalse(q.lz(m));
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
		assertTrue(q.lz(m));
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
		assertTrue(q.lz(m));
		assertFalse(q.lz(m));
		q.startLazy();
		assertTrue(q.lz(m));
		assertTrue(q.lz(m));
		assertFalse(q.lz(m));
		q=q.replaceVariable(y, t);
		q=q.replaceVariable(z, u);		
		m=new VariableMap();
		assertTrue(q.lz(m));
		assertTrue(q.lz(m));
		assertFalse(q.lz(m));
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
*/	
	
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
	
	
	@SuppressWarnings("rawtypes")
	@Test
	public void testLazyGroup() {
		Lazy<Group, VariableMap> l=lz(new Group("testLazyGroup",1,2,3,4,5,6), new Object[] { x,2,z,4,y,t } );
		assertTrue(l.current().isEmpty());
		VariableMap m=new VariableMap();
		VariableMap m2=new VariableMap();
		int counter=0;
		while (!(m=l.lz()).isEmpty()) {
			counter++;
			m2.add(m);
			assertTrue(m.getVars().size()==4);
			assertTrue( ((ArrayList) m.getVals(x)).size()==1);
			assertTrue( ((ArrayList) m.getVals(y)).size()==1);
			assertTrue( ((ArrayList) m.getVals(z)).size()==1);
			assertTrue( ((ArrayList) m.getVals(t)).size()==1);
		}
		assertTrue(counter==24);
		instantiate(m2);
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
		//set values
		assertTrue(v.set(v));
		assertFalse(var(v));
		assertTrue(v.equals(undefined));
		resetVars();
		assertTrue(y.set('a',x,"TM"));
		assertTrue(z.set(y,relation(1,2,y)));
		assertTrue(t.set(undefined,2,1));
		assertTrue(x.set(1,2,y));
		assertFalse(z.set(1));
		assertTrue(u.set(v));
		assertTrue(var(v));
		assertFalse(var(u));		
		assertTrue(u.contains(v));
		v.set("Maimon","Tzali");
		assertTrue(u.contains("Tzali"));
		assertTrue(x.contains(1));
		assertFalse(x.contains(y));
		assertTrue(x.contains(undefined));
		assertTrue(y.contains(x));
		assertTrue(y.contains(undefined));
		assertTrue(y.contains(1));
		assertTrue(z.contains(2));
		assertFalse(x.equalValues(t));
		assertTrue(x.equalValuesSet(t));
		resetVars();
		x.set(1,2,3,4,5);
		y.set(1,9,8,7,6);
		t.set(2,1,3,4,5);
		assertTrue(x.equals(y));
		assertFalse(x.equals(t));
		assertTrue(x.equals(1));
		assertFalse(x.equals(2));
		//set with constraints
		resetVars();
		x.instantiate(new Object[]{6,6,6,1,3,4,5}, c(cmp,1,x,4), c(cmp,-1,x,-100));
		y.instantiate(new Object[]{"a","b",false,10,20}, c(cmp,1,y,4), c(cmp,1,y,((Integer) x.get(3))*100));
		v.instantiate(new ArrayList(), null, c(abs,1,v,10));
		assertFalse(v.contains(10));
		assertFalse(v.noValue());
		assertTrue(v.contains(11));
		assertTrue(v.contains(9));
		Constraint c=x.getConstraint();
		assertTrue(x.contains(6));
		assertFalse(x.contains(1));
		assertFalse(x.contains(3));
		assertFalse(x.contains(4));
		assertTrue(x.contains(-600));
		assertTrue(same(x.get(0),x.get(1)));
		assertTrue(same(x.get(1),x.get(2)));
		assertTrue(same(6,x.get(2)));
		assertTrue(same(5,x.get(3)));
		assertTrue(y.contains(10));
		assertFalse(y.contains("a"));
		assertFalse(y.contains(false));
		assertFalse(y.contains(30));
		assertTrue(y.contains(600));
		assertFalse(y.contains(500));
		assertTrue(y.contains(501));		
		assertTrue(y.getValues().length==2);
		assertFalse(t.equals(v));
		assertFalse(y.equals(x));
		x=var();
		Variable temp=(Variable) c.getVars().toArray()[0];
		assertTrue(c.satisfy(temp,-1000));
		assertFalse(c.satisfy(temp,0));
		assertFalse(x.singleValue());
		assertFalse(y.singleValue());
		assertFalse(v.singleValue());
		x.set(1);
		assertTrue(x.singleValue());
	}
	
	
	@Test
	public void testStrings() {
		//Association
		Relation r=relation("testStrings",x);
		associate(r("testStrings",1,2,r));
		assertTrue(exists(r("testStrings",1,2,x)));
		assertEquals(x.toString(),"[testStrings([$x$])]");
		assertEquals(r.toString(),"testStrings([$x$])");
		assertEquals(sum.toString(), "Sum()");
		r=r(sum,1,2,3);
		assertEquals(r.toString(),"Sum(1,2,3)");
		Constraint q1=new Constraint(max, x, 10, 20, 30, y);
		Constraint q2=new Constraint(min, 100, x, 300);
		Constraint q=new Constraint(q1,AND,q2);
		y.set(q);
		assertEquals(q.toString(), "(Max([$x$],10,20,30,[$y$])) AND (Min(100,[$x$],300))");
		assertEquals(y.toString(), "[(Max([$x$],10,20,30,[$y$])) AND (Min(100,[$x$],300))]");
	}
	
	
	@Test
	public void testMathFormulas() {
		//Max
		x.set(max.invoke(1,2,3,4,"s"), max.satisfy(2,1,2,3,4), max.satisfy(4,1,3,4,2), max.satisfy((Double) max.invoke(1,2), 1,2));
		assertTrue(same(x.get(0),undefined));
		assertTrue(same(x.get(1),false));
		assertTrue(same(x.get(2),true));
		assertTrue(same(x.get(3),true));
		//Min
		resetVars();
		x.set(min.invoke(1,2,3,4,"s"), min.satisfy(2,1,2,3,4), min.satisfy(1,1,3,4,2), min.satisfy((Double) min.invoke(1,2), 1,2));
		assertTrue(same(x.get(0),undefined));
		assertTrue(same(x.get(1),false));
		assertTrue(same(x.get(2),true));
		assertTrue(same(x.get(3),true));
		//Abs
		resetVars();
		x.set(abs.invoke(1,2,3,4), abs.satisfy(2,1,5), abs.satisfy(3,1,4), abs.satisfy((Double) abs.invoke(1,2), 1,2));
		assertTrue(same(x.get(0),-1));
		assertTrue(same(x.get(1),false));
		assertTrue(same(x.get(2),true));
		assertTrue(same(x.get(3),true));	
		//Pow
		resetVars();
		x.set(pow.invoke(2,1,3,2), pow.satisfy(2,1,5), pow.satisfy(1,1,4), pow.satisfy((Double) pow.invoke(1,2), 1,2));
		assertTrue(same(x.get(0),64));
		assertTrue(same(x.get(1),false));
		assertTrue(same(x.get(2),true));
		assertTrue(same(x.get(3),true));
		//Sqrt
		resetVars();
		x.set(sqrt.invoke(2,1,3,2), sqrt.satisfy(2,5), sqrt.satisfy(3,9), sqrt.satisfy((Double) sqrt.invoke(1,2), 1,2));
		assertTrue(same(x.get(0),-1));
		assertTrue(same(x.get(1),false));
		assertTrue(same(x.get(2),true));
		assertTrue(same(x.get(3),true));
		//Sum
		resetVars();
		x.set(sum.invoke(2,1,3,2,new Container()), sum.satisfy(13.4,5,5,1,2.4), sum.satisfy(9,9), sum.satisfy((Double) sum.invoke(1,2), 1,2));
		assertTrue(same(x.get(0),undefined));
		assertTrue(same(x.get(1),true));
		assertTrue(same(x.get(2),true));
		assertTrue(same(x.get(3),true));
		//Product
		resetVars();
		x.set(product.invoke(2,1,3,2,0.1,10), product.satisfy(1,1,3,0.3), product.satisfy(1,20,0.05,2,0.5), product.satisfy((Double) product.invoke(1,2), 1,2));
		assertTrue(same(x.get(0),12));
		assertTrue(same(x.get(1),false));
		assertTrue(same(x.get(2),true));
		assertTrue(same(x.get(3),true));
	}
	
	
	@Test
	public void testReflection() {
		assertEquals(val.invoke(1,2,3),undefined);
		assertTrue(val.satisfy(1,1));
		x.set(y);
		assertTrue(val.satisfy(y,x));
		assertTrue(val.satisfy(x,y));
		y.set("Tzali");
		assertTrue(val.satisfy("Tzali",x));
		
		assertFalse(same(var.invoke(),undefined));
		assertEquals(var.invoke().toString(),"$LJ_Variable$");
		assertFalse((Boolean)var.invoke(x));
		assertTrue((Boolean)var.invoke(t));
		assertFalse((Boolean)var.invoke(new Container()));
		
		assertTrue(same(deepInvoke.invoke(1,sum),undefined));
		Object[][] arr = new Object[2][3];
		for (int i=0; i<2; i++)
			for (int j=0; j<3; j++)
				arr[i][j]=i*j;
		assertFalse(same(deepInvoke.invoke(arr,sum),undefined));
		Object[] sums=(Object[]) deepInvoke.invoke(arr,sum);
		assertEquals(sums[0],0.0);
		assertEquals(sums[1],3.0);
	}		
	

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testExists() {
		Relation r=r("testExists","Tzali","Maimon",30,61262291);
		Relation r2=r("testExists","Tal","Zamir",33,"Don't know!");
		associate(r);
		associate(r("testExists"));
		associate(r("testExists",10,20,30,40,50));
		associate(new Group("testExists",1,2,3,4,5));
		associate(r("testExists","relationType2",r2,false));
		associate(r("testExists","VariableType1",t,0));
		associate(r("testExists","VariableType2",u,1));
		associate(r("testExists","relationType1",r,true));
		associate(r("testExists","VariableType3",v,2));
		associate(r("testExists","usingFormula1",false, false));
		associate(r("testExists","usingFormula2",true, false));
		assertTrue(e(r("testExists")));
		assertTrue(e(r("testExists",10,20,30,40,50)));
		assertTrue(a(r("testExists",3,2,4,1,5)));
		assertTrue(a(r("testExists",3,5,4,1,2)));
		assertFalse(a(r("testExists",3,2,1,1,5)));
		assertFalse(a(r("testExists",3,2,4,1,3)));
		assertTrue(e(r("testExists",x,3,y,2,1)));
		assertFalse(var(x));
		assertFalse(var(y));
		assertTrue((x.equals(4) && y.equals(5)) || (x.equals(5) && y.equals(4)));
		resetVars();
		assertFalse(e(r("testExists",3,2,x,3,y)));
		assertTrue(var(x));
		assertTrue(var(y));
		assertTrue(e(r("testExists",x,y,z), WHERE, c(e,true,y)));
		assertTrue(x.equals("relationType1"));
		assertTrue(same(z,true));
		assertFalse(e(r("testExists",_,_,y), AND, r("testExists",_,true,y)));
		resetVars();
		assertFalse(e(r("testExists",_,_,y), AND, r("testExists",y,true)));
		assertTrue(var(y));
		assertTrue(e(r("testExists",_,_,y), AND, r("testExists",_,true,y)));
		assertEquals(y,false);
		assertTrue(e(r("testExists",x,_,2), OR, r("testExists",1,2,5,4,x)));
		assertTrue(x.contains("VariableType3"));
		assertFalse(x.contains(3));
		assertFalse(x.contains("VariableType2"));
		assertFalse(x.contains(false));
		assertFalse(x.contains(1));
		assertFalse(x.contains(10));
		assertTrue(e(r("testExists",x,t,2)));
		assertFalse(var(t));
		assertFalse(t.contains(v));  //because we resetVars() and v has changed.
		assertTrue(e(r("testExists",x,v,2)));
		assertFalse(var(v));
	}
	
	
	@Test
	public void testAll() {
		Relation r=r("testExists","Tzali","Maimon",30,61262291);
		Relation r2=r("testExists","Tal","Zamir",33,"Don't know!");
		Relation r3=r("testExists","VariableType3",v,2);
		Variable temp=(Variable) r3.args()[1];
		assertTrue(var(temp));		
		associate(r);
		associate(r3);
		associate(r("testExists"));
		associate(r("testExists",10,20,30,40,50));
		associate(new Group("testExists",1,2,3,4,5));
		associate(r("testExists","relationType2",r2,false));
		associate(r("testExists","VariableType1",t,0));
		associate(r("testExists","VariableType2",u,1));
		associate(r("testExists","relationType1",r,true));
		associate(r("testExists","relationTypeR3",r3,true));
		associate(r("testExists","usingFormula1",false, false));
		associate(r("testExists","usingFormula2",true, false));
		assertTrue(a(r("testExists")));
		assertTrue(a(r("testExists",10,20,30,40,50)));
		assertTrue(a(r("testExists",3,2,4,1,5)));
		assertTrue(a(r("testExists",3,5,4,1,2)));
		assertFalse(a(r("testExists",3,2,1,1,5)));
		assertFalse(a(r("testExists",3,2,4,1,3)));
		assertTrue(a(r("testExists",x,3,y,2,1)));
		assertFalse(var(x));
		assertFalse(var(y));
		assertTrue(var(temp));	
		assertTrue((x.equals(4) && y.equals(5)) || (x.equals(5) && y.equals(4)));
		resetVars();
		assertFalse(a(r("testExists",3,2,x,3,y)));
		assertTrue(var(x));
		assertTrue(var(y));
		assertTrue(var(temp));	
		assertTrue(a(r("testExists",x,y,z), WHERE, c(e,true,y)));
		assertFalse(var(temp));	
		assertTrue(x.contains("relationType1"));
		assertTrue(x.contains("relationTypeR3"));
		assertTrue(y.contains(r));
		assertTrue(y.contains(r3));
		assertTrue(same(z,true));
		assertFalse(a(r("testExists",_,_,y), AND, r("testExists",_,true,y)));
		resetVars();
		assertFalse(a(r("testExists",_,_,y), AND, r("testExists",y,true)));
		assertTrue(var(y));
		assertTrue(a(r("testExists",_,_,y), AND, r("testExists",_,true,y)));
		assertEquals(y,false);
		assertTrue(a(r("testExists",x,_,2), OR, r("testExists",1,2,5,4,x)));
		assertTrue(x.contains("VariableType3"));
		assertTrue(x.contains(3));
		assertFalse(x.contains("VariableType2"));
		assertFalse(x.contains(false));
		assertFalse(x.contains(1));
		assertFalse(x.contains(10));
		assertTrue(a(r("testExists",x,t,2)));
		assertFalse(var(t));
		assertTrue(t.contains(temp));
		assertTrue(var(v));
		assertTrue(a(r("testExists",x,v,2)));
		assertFalse(var(v));
	}
	
/*	
	@Test
	public void testLazyAll() {
		Object[] values=new Object[] {1,2,3,4,5,6,7,8};
		group(values);
		Variable[] vars = varArray(8);
		Constraint[] cons=new Constraint[9];
		cons[0]=c(abs,1,vars[0],vars[2]);
		cons[1]=c(cons[0],OR,c(abs,1,vars[1],vars[2]));
		cons[2]=c(cons[1],OR,c(abs,1,vars[1],vars[4]));
		cons[3]=c(cons[2],OR,c(abs,1,vars[2],vars[3]));
		cons[4]=c(cons[3],OR,c(abs,1,vars[2],vars[5]));
		cons[5]=c(cons[4],OR,c(abs,1,vars[3],vars[6]));
		cons[6]=c(cons[5],OR,c(abs,1,vars[4],vars[5]));
		cons[7]=c(cons[6],OR,c(abs,1,vars[5],vars[6]));
		cons[8]=c(cons[7],OR,c(abs,1,vars[5],vars[7]));
		Lazy lazy=lazy(relation(vars),DIFFER,cons[8]);
		VariableMap map=lazy.lz();
		assertFalse(map.isEmpty());
		lazy.startLazy();
		int counter=0;
		for (VariableMap m : lazy) {
			Variable[] vs=varArray(8);
			Object[] os=new Object[8];
			int index=0;
			for (Variable v : m.getVars()) {
				assertTrue(m.getVals(v)!=undefined && ((ArrayList) m.getVals(v)).size()==1);
				vs[index].set(((ArrayList) m.getVals(v)).get(0));
				os[index]=vs[index].get(0);
				index++;
			}
			boolean correct=true;
			for (Object o: values) {
				boolean found=false;
				for (Variable v: vs) 
					if (same(v,o)) {
						found=true;
						break;
					}
				if (!found) {
					correct=false;
					break;
				}
			}
			assertTrue(correct);
			assertFalse(cons[8].satisfy(os));
			counter++;
		}
		assertTrue(same(counter, 1656));
		counter=0;
		for (VariableMap m : lazy) counter++;
		assertTrue(same(counter, 0));
		lazy.startLazy();
		for (VariableMap m : lazy) counter++;
		assertTrue(same(counter, 1656));
	}
*/
}