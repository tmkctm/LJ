import static LJava.LJ.*;
import static LJava.Utils.*;
import LJava.Constraint;
import LJava.Variable;


public class Main {

	public static void main(String[] args) {
		Variable x=var();
		Variable y=var();
		Constraint c = new Constraint(new Constraint(cmp,1,x,2),OR,new Constraint(max,6,x,y));
		y.set(6);
		System.out.println(c.satisfy(x, 5));
		System.out.println(x.isVar());
	}
}
