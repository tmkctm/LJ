import static LJava.LJ.*;
import static LJava.Utils.*;
import LJava.Variable;


public class Main {

	public static void main(String[] args) {
		Variable x=new Variable("x");	Variable y=new Variable("y");
		relate(1,2,3,4);
		relate(1,3,2,4);
		System.out.println(all(relation(1,x,y,4), DIFFER, condition(cmp,0,x,2)));
		System.out.println(x);
		System.out.println(y);
	}
}
