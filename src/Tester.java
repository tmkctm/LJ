import LJava.*;
import static LJava.LJ.*;
import static LJava.Utils.*;
import org.junit.*;
import static org.junit.Assert.*;

public class Tester {
	
	Variable x=var();
	Variable y=var();
	Variable z=var();
	Variable t=var();	
		
	@SuppressWarnings("unused")
	private class TesterLittleHelper{		
		 private Integer value;
		 public void set(int v) {  value=v;  }		 
		 public int get() {  return value;  }
		 public String toString(){ 
			 if (value==null) return "Helper";
			 return value.toString(); }
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
		assertEquals(x.toString(),"[1,2,3,4,5,$nil$()]");
		assertEquals(y.toString(),"[a,b,c,_,$nil$(),$no_variable_value$(),$LJava_True_Relation$(),$LJava_False_Relation$()]");
		assertEquals(r.toString(),"try(1,2,3)");
	}
	
	
	@Test
	public void testExistsOnVars(){
		TesterLittleHelper aClass=new TesterLittleHelper();
		relate(1,2,3,"a","b","c", aClass, z);
		exists(1,2,3,_,_,_,x,y);
		assertEquals(x,aClass);
		assertEquals(y,z);
	}
	
	
	@Test
	public void testExistsOneVarMultiple(){
		relate(2,10,20,30, y, y);
		exists(2,x,x,_,_,_);
		assertTrue(var(x));		
		exists(2,10,20,_,z,z);
		assertFalse(var(z));
		y.set(100);
		assertEquals(z,100);
	}
	
	
	public void testVariableChain(){
		relate(3,1,2,z);
		relate(3,"A",y,"C");
		exists(3,1,2,y);
		exists(3,"A",x,_);
		exists(3,z,2,_);
		assertEquals(x,1);		
	}
	
		
	@Test
	public void testConduct(){
		relate(4,z,y,0);
		relate(4,1,2,3);
		relate(4,"a","b","c");
		Relation r=relation("test");
		conduct(4,_,x,_);
		y.set("t");
		assertEquals(x,"t");
		Variable c=new Variable();
		c.set(2,"b","t");
		assertTrue(x.equalConstraint(c));
		assertEquals(conduct(r),FAILED);
		associate(r);
		assertEquals(conduct(r),SUCCESS);
		assertEquals(conduct(),FAILED);
		assertEquals(conduct(4,1,2,3),SUCCESS);
		assertEquals(conduct("t","m",2,3),FAILED);
	}
	

	@Test
	public void testConsistencyForSingleVar(){
		relate(12,100,200,x);
		exists(12,100,200,x);
		assertFalse(var(x));
		assertEquals(x,nil);
	}
	
	
	@Test
	public void testConsistencyForExistsTwoVars(){
		relate(5,100,200,x);
		exists(5,100,200,y);
		relate(5,y,6,6);
		exists(5,x,6,6);
		assertFalse(var(y));
		assertFalse(var(x));
		x.set(30);
		assertEquals(x,y);
		assertEquals(y,nil);
	}
	
	
	@Test
	public void testConsistencyForExistsVarsChain(){
		relate(6,0,0,y);
		relate(6,z,1,1);
		relate(6,x,5,5);
		relate(6,3,t,3);
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
		relate(7,y,y,0,1);
		relate(7,1,1,30,1);
		relate(7,"a","a","b",1);
		relate(7,helper,helper,1,1);
		relate(7,700,0,700,1);
		relate(7,"Some","Stuff",1);
		relate(7,"Some","more","Stuff",1);
		relate(7,7,8,x);		
		conduct(7,x,x,_,_);
		exists(7,7,8,y);	
		Variable c=new Variable();
		c.set(y,1,"a",helper);
		assertTrue(x.equalConstraint(c));
		assertEquals(x,nil);
	}
	
	
	@Test
	public void testORop(){
		relate(8,1,"a");
		relate(8,2,"b");
		relate(8,3,"c");
		query( or( relation(8,x,_) , relation(8,_,x) ));
		assertEquals(x,1);
		Variable c=new Variable();
		c.set(1,2,3,"a","b","c");
		assertTrue(x.equalConstraint(c));
	}
	
	
	@Test
	public void testANDop(){
		relate(9,1,"a");
		relate(9,2,3);
		relate(9,3,"c");
		query( and( relation(9,x,_) , relation(9,_,x) ));
		assertEquals(x,3);
		assertFalse(x.isConstraint());
		assertTrue(x.isNumber());		
	}
	
	
	@Test
	public void testLogicQuery(){
		TesterLittleHelper helper=new TesterLittleHelper();
		helper.set(1000);
		relate(10,1,2,3,4,5,6);
		relate(10,70);
		relate(10,6);
		relate(10,0,9,8,7,6,5,4,3,2,helper);
		relate(10,700,9,8,7,1600,5,4,3,2,1);
		relate(10,helper,10,helper,10,helper);
		relate(10,0,10,0,10,0);
		query( or(
				and(
						relation(10,1,2,3,4,5,x),
						relation(10,x)
				),
				and(
						relation(10,x,9,8,7,_,5,4,3,2,y),
						relation(10,y,10,y,10,y)
				)	
			));
		assertTrue(x.isConstraint());
		assertFalse(y.isConstraint());
		assertEquals(x,6);
		assertEquals(y,helper);
	}
	
	
	@Test
	public void testLogicQueryNoVars(){
		relate(11,1,2,3,4,5,6);
		relate(11,70);
		relate(11,6);
		relate(11,0,11,0,11,0);
		assertEquals(query( or(
					and(
							relation(11,1,2,3,4,5,6),
							relation(11,6)
					),
					and(
							relation(11,_,9,8,7,_,5,4,3,2,0),
							relation(11,1,11,1,11,2)
					)	
				))
				,SUCCESS);
		
	}
	

	@Test
	public void testGroup(){
		group(12,2,3,4,5,6,7,8,9,0);
		group(12,2,3,4,5,x,x,y);
		group(12,12,13,13,x,y,z);
		group(12,12,12,12,12,13);
		group(12,12,12,12,12,13,13,13,13,13);
		group(12,12,14,13,13);
		assertEquals(exists(12,2,3,4,5,t,t,t),FAILED);
		assertEquals(exists(12,2,3,4,5,x,x,y),SUCCESS);
		assertTrue(var(x));
		exists(12,3,y,5,6,2,7,8,9,4);
		exists(12,5,4,z,6,2,7,8,9,y);
		assertEquals(y,0);
		assertEquals(z,3);
		assertEquals(exists(12,12,0,3,13,13,x),SUCCESS);
		x.set(1);
		assertEquals(exists(12,2,3,4,5,x,x,y),SUCCESS);
		assertEquals(exists(0,2,1,4,5,3,1,12),SUCCESS);
		x=var();
		exists(x,x,t,x,x,x);
		assertEquals(x,12);
		assertEquals(t,13);	
		x=var();   t=var();
		assertEquals(exists(x,x,t,x,t),FAILED);
		assertTrue(var(x));
		assertTrue(var(t));
		exists(x,t,x,t,x,t,x,x,t,t);
		assertTrue(((Integer) x.get()==12) || ((Integer) x.get()==13));
		assertTrue(((Integer) t.get()==12) || ((Integer) t.get()==13));
	}
	
	
	@Test
	public void testFunctors() {
		Functor<Integer,Integer> sum=new Functor<Integer,Integer>("Sum", Integer.class){
			@Override
			protected Integer f(Integer... p) {
				return p[0]+p[1];
			}};
		
		assertEquals(exists(sum),FAILED);
		assertEquals(exists(cmp),FAILED);
		associate(sum);
		assertEquals(exists(sum),SUCCESS);
		exists(relation("Sum",t,4,5));
		assertTrue(same(t,9));
		relate(13,1,2,3);
		group(13,11,12,14,200,201,202);
		query(relation(x,12,14,11,202,200,201),AND,relation("Sum",x,4,1));		
		assertFalse(same(x.toString(),none.toString()));
		assertTrue(var(x));
		query(relation(y,12,14,11,202,200,201),AND,relation("Sum",y,12,1));	
		assertTrue(same(y,13));		
	}
}
