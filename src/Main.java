import static LJava.LJ.*;
import static LJava.Utils.*;
import static LJava.MathFormulas.*;

import javax.swing.JOptionPane;

import LJava.Constraint;
import LJava.Group;
import LJava.LazyGroup;
import LJava.Variable;
import LJava.VariableMap;

public class Main {

	public static void main(String[] args) {
		//describe the space of the problem
		group(1,2,3,4,5,6,7,8);
		
		//Describe the variables of the problem
		Object[] vars = new Variable[8];
		for (int i=0; i<8; i++) vars[i]=new Variable("x"+(i+1));
		
		//Describe the conditions of the problem
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
		
		//Ask for a result and that's it!
		System.out.println(all(relation(vars),DIFFER,c[8])+"\n");
		
		//Do whatever you want with the result.
		for (int i = 0; i < ((Variable) vars[0]).getValues().length; i++) {
			System.out.println("\n"); 
			printResult(vars,i);
			System.out.println("\n");
			JOptionPane.showConfirmDialog(null, "NEXT?");
		}
	}
	
	
	public static void printResult(Object[] vars, int i) {
		System.out.println("\t      -------");
		System.out.println("\t      | "+((Variable) vars[0]).getValues()[i]+" |");
		System.out.println("\t-------------------");
		System.out.println("\t| "+((Variable) vars[1]).getValues()[i]+" | "+((Variable) vars[2]).getValues()[i]+" | "+((Variable) vars[3]).getValues()[i]+" |");
		System.out.println("\t-------------------");
		System.out.println("\t| "+((Variable) vars[4]).getValues()[i]+" | "+((Variable) vars[5]).getValues()[i]+" | "+((Variable) vars[6]).getValues()[i]+" |");
		System.out.println("\t-------------------");
		System.out.println("\t      | "+((Variable) vars[7]).getValues()[i]+" |");
		System.out.println("\t      -------");
	}
	
}
