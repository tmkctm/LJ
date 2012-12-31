
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import LJava.*;
import static LJava.LJ.*;
import static LJava.Utils.*;
import static LJava.MathFormulas.*;

import org.junit.*;
import static org.junit.Assert.*;

public class Tester {
	
	Variable x;		Variable y;		Variable z;		Variable t;		Variable u;		Variable v;		Variable LJvar;
	
	private class Container {
		public int i;			public double d;
		public String s;		public List<Object> l;
		public boolean b;		public Variable v;
		public Constraint c;	public Relation r;
		public Group g;			public LazyGroup lg;
		public Object[] arr;
		
		public String toString() {
			return "int: "+i+" ; double: "+d+" ; String: "+s+" ; List: "+l+"\n"
					+"boolean: "+b+" ; Variable: "+v+" ; Constraint: "+c+"\n"+
					"Relation: "+r+" ; Group: "+g+" ; LazyGroup: "+lg+" ; Array: "+arr;
		}
	}
	
	
	@Before
	public void resetVars(){
		x=new Variable("x");	y=new Variable("y");
		z=new Variable("z");	t=new Variable("t");
		u=new Variable("u");	v=new Variable("v");
		LJvar=var();
	}
	
	
//Test classes	
	@Test
	public void testAssociation() {
		Association a=new Association("");
		assertEquals(a.name(),"#LJRelation");
		Container container=new Container();
		container.b=false;
		container.l=new ArrayList<Object>();		container.l.add(x);		container.l.add("tzali");		container.l.add(10);
		container.v=var();		container.v.set(1,2,3);
		y.set(11,12,13);
		a=new Association("a", 0, container, false, new HashSet(), LJvar, x, y);
		System.out.println(a);
	}

}
