import static LJava.LJ.*;
import static LJava.Utils.*;
import static LJava.MathFormulas.*;
import LJava.Constraint;
import LJava.Group;
import LJava.LazyGroup;
import LJava.Variable;
import LJava.VariableMap;

public class Main {

	public static void main(String[] args) {
		group(1,2,3,4,5,6,7,8);
		Object[] vars = new Variable[8];
		for (int i=0; i<8; i++) vars[i]=new Variable("x"+(i+1));
		
		Constraint[] c=new Constraint[9];
		c[0]=new Constraint(abs,1,vars[0],vars[2]);
		c[1]=new Constraint (c[0],OR,new Constraint(abs,1,vars[1],vars[2]));
		c[2]=new Constraint (c[1],OR,new Constraint(abs,1,vars[1],vars[4]));
		c[3]=new Constraint (c[2],OR,new Constraint(abs,1,vars[2],vars[3]));
		c[4]=new Constraint (c[3],OR,new Constraint(abs,1,vars[2],vars[5]));
		c[5]=new Constraint (c[4],OR,new Constraint(abs,1,vars[3],vars[6]));
		c[6]=new Constraint (c[5],OR,new Constraint(abs,1,vars[4],vars[5]));
		c[7]=new Constraint (c[6],OR,new Constraint(abs,1,vars[5],vars[6]));
		c[8]=new Constraint (c[7],OR,new Constraint(abs,1,vars[5],vars[7]));
		
		System.out.println(exists(relation(vars),DIFFER,c[8])+"\n");
		printResult(vars);
	}
	
	
	public static void printResult(Object[] vars) {
		System.out.println("\t      -------");
		System.out.println("\t      | "+vars[0]+" |");
		System.out.println("\t-------------------");
		System.out.println("\t| "+vars[1]+" | "+vars[2]+" | "+vars[3]+" |");
		System.out.println("\t-------------------");
		System.out.println("\t| "+vars[4]+" | "+vars[5]+" | "+vars[6]+" |");
		System.out.println("\t-------------------");
		System.out.println("\t      | "+vars[7]+" |");
		System.out.println("\t      -------");
	}
	
}
