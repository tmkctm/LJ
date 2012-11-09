import static LJava.LJ.*;
import static LJava.Utils.*;
import LJava.Constraint;
import LJava.Variable;


public class Main {

	public static void main(String[] args) {
		Integer[] arr = {1,2,3,4,5,6,7,8,9,0};
		Variable x = var();
		Variable y = var();
		Object[] arr2 = new Object[arr.length+1];
		arr2[0] = x;
		for (int i=0; i<arr.length; i++) arr2[i+1]=arr[i];
		Constraint c1 = new Constraint(cmp,1,x,y);
		Constraint c2 = new Constraint(cmp,-1,x,1);
		Constraint c3 = new Constraint(cmp,0,x,9);
		Constraint c = new Constraint(new Constraint(c1,AND,c2),OR, c3);
		System.out.println(c);
		System.out.println(c1.satisfy(x,400));
		Variable[] vs = {x,y};
		Object[] os = {400,1400};
		System.out.println(var(x));
		System.out.println(var(y));
		System.out.println(c1.satisfy(vs,os));
		y.set(7);
		System.out.println(c1.satisfy(x,400));
		x.instantiate(arr, c, new Constraint(LJFalse));
		System.out.println(x);
		Variable z=var();
		Constraint zConstraint=c.replaceVariable(x, z);
		System.out.println(zConstraint);
		z.instantiate(null, zConstraint, new Constraint(cmp,0,z,50));
		System.out.println(z);
		System.out.println(z.contains(90));
		System.out.println(z.contains(50));
		System.out.println(z.contains(5));
	}
}
