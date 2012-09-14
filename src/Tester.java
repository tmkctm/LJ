import LJava.*;
import static LJava.LJ.*;
import org.junit.*;
import static org.junit.Assert.*;

public class Tester {
	
	Variable x=new Variable();
	Variable y=new Variable();
	Variable z=new Variable();
	Variable t=new Variable();	
		
	private class TesterLittleHelper{		
		 private Integer value;
		 public void set(int v) {  value=v;  }
		 public int get() {  return value;  }
		 public String toString(){ return value.toString(); }
	}
	
	
	@Before
	public void init(){
		x=new Variable();
		y=new Variable();
		z=new Variable();
		t=new Variable();
	}
	
	
	@Test 
	public void checkString() {
		x.set(1,2,3,4,5,x);
		y.set("a","b","c",_,nil,none,LJavaTrueRelation, LJavaFalseRelation);
		Relation r=new Relation("try",1,2,3);
		System.out.println(x);
		System.out.println(y);
		System.out.println(r);
		assertEquals(x.toString(),"[1,2,3,4,5,$nil$()]");
		assertEquals(y.toString(),"[a,b,c,_,$nil$(),$no_variable_value$(),$true$(),$false$()]");
		assertEquals(r.toString(),"try(1,2,3)");
	}
	
	
	@Test
	public void testExistsOnVars(){
		TesterLittleHelper aClass=new TesterLittleHelper();
		nil(1,2,3,"a","b","c", aClass, z);
		exists(1,2,3,_,_,_,x,y);
		assertEquals(x,aClass);
		aClass.set(2);
		assertEquals(y,z);
	}
	
	
	@Test
	public void testExistsOneVarMultiple(){
		nil(2,10,20,30, y, y);
		exists(2,x,x,_,_,_);
		assertTrue(var(x));		
		exists(2,10,20,_,z,z);
		assertTrue(!var(z));
		y.set(100);
		assertEquals(z,100);
	}
	
	
	public void testVariableChain(){
		nil(3,1,2,z);
		nil(3,"A",y,"C");
		exists(3,1,2,y);
		exists(3,"A",x,_);
		exists(3,z,2,_);
		assertEquals(x,1);		
	}
	
		
	@Test
	public void testConduct(){
		nil(4,z,y,0);
		nil(4,1,2,3);
		nil(4,"a","b","c");		
		conduct(4,_,x,_);
		y.set("t");
		assertEquals(x,"t");
		Variable c=new Variable();
		c.set(2,"b","t");
		assertTrue(x.equalConstrain(c));		
	}
	

	@Test
	public void testConsistencyForSingleVar(){
		nil(12,100,200,x);
		exists(12,100,200,x);
		assertFalse(var(x));
		assertEquals(x,nil);
	}
	
	
	@Test
	public void testConsistencyForExistsTwoVars(){
		nil(5,100,200,x);
		exists(5,100,200,y);
		nil(5,y,6,6);
		exists(5,x,6,6);
		assertTrue(!var(y));
		assertTrue(!var(x));
		x.set(30);
		assertEquals(x,y);
		assertEquals(y,nil);
	}
	
	
	@Test
	public void testConsistencyForExistsVarsChain(){
		nil(6,0,0,y);
		nil(6,z,1,1);
		nil(6,x,5,5);
		nil(6,3,t,3);
		exists(6,y,5,5);
		exists(6,0,0,z);
		exists(6,t,1,1);
		exists(6,3,x,3);
		assertEquals(x,z);
		assertEquals(y,t);
		assertEquals(x,nil);
	}
	
	
	@Test
	public void testConsistencyForCunduct(){
		TesterLittleHelper helper=new TesterLittleHelper();
		helper.set(29);
		nil(7,y,y,0,1);
		nil(7,1,1,30,1);
		nil(7,"a","a","b",1);
		nil(7,helper,helper,1,1);
		nil(7,700,0,700,1);
		nil(7,"Some","Stuff",1);
		nil(7,"Some","more","Stuff",1);
		nil(7,7,8,x);		
		conduct(7,x,x,_,_);
		exists(7,7,8,y);	
		Variable c=new Variable();
		c.set(y,1,"a",helper);
		assertTrue(x.equalConstrain(c));
		assertEquals(x,nil);
	}
	
	
	@Test
	public void testORop(){
		nil(8,1,"a");
		nil(8,2,"b");
		nil(8,3,"c");
		query( or( relate(8,x,_) , relate(8,_,x) ));
		assertEquals(x,1);
		Variable c=new Variable();
		c.set(1,2,3,"a","b","c");
		assertTrue(x.equalConstrain(c));
	}
	
	
	@Test
	public void testANDop(){
		nil(9,1,"a");
		nil(9,2,3);
		nil(9,3,"c");
		query( and( relate(9,x,_) , relate(9,_,x) ));
		assertEquals(x,3);
		assertTrue(!x.isConstrain());
		assertTrue(x.isNumber());		
	}
	
	
	@Test
	public void testLogicQuery(){
		TesterLittleHelper helper=new TesterLittleHelper();
		helper.set(1);
		nil(10,1,2,3,4,5,6);
		nil(10,70);
		nil(10,6);
		nil(10,0,9,8,7,6,5,4,3,2,helper);
		nil(10,0,9,8,7,1600,5,4,3,2,1);
		nil(10,helper,10,helper,10,helper);
		nil(10,0,10,0,10,0);
		query( or(
				and(
						relate(10,1,2,3,4,5,x),
						relate(10,x)
				),
				and(
						relate(10,x,9,8,7,_,5,4,3,2,y),
						relate(10,y,10,y,10,y)
				)	
			));
		assertTrue(x.isConstrain());
		assertTrue(!y.isConstrain());
		assertEquals(x,6);
		assertEquals(y,helper);
	}
	
	
	@Test
	public void testLogicQueryNoVars(){
		nil(11,1,2,3,4,5,6);
		nil(11,70);
		nil(11,6);
		nil(11,0,11,0,11,0);
		assertTrue(query( or(
					and(
							relate(11,1,2,3,4,5,6),
							relate(11,6)
					),
					and(
							relate(11,_,9,8,7,_,5,4,3,2,0),
							relate(11,1,11,1,11,2)
					)	
				)));
		
	}
	
	
}
