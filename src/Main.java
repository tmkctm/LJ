import static LJava.LJ.*;
import LJava.Variable;


public class Main {

	public static void main(String[] args) {

		Variable x=new Variable("x");	Variable y=new Variable("y");
		Variable t=var();
		
		relate(1,t,3,4);
		relate(1,3,2,4);
		relate(1,0,3,4);
		relate(1,10,112,4);
		
		all(relation(1,x,y,4), DIFFER, relation(1,0,t,4));
		
		t.set(1000);
		
		System.out.println(x);
		System.out.println(y);
	}
}
