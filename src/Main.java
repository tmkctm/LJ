import static LJava.LJ.*;
import static LJava.MathFormulas.*;
import javax.swing.JOptionPane;
import LJava.Constraint;
import LJava.Lazy;
import LJava.Variable;
import LJava.VariableMap;

public class Main {

	public static void main(String[] args) {
		//describe the space of the problem
		group(1,2,3,4,5,6,7,8);
		
		//Describe the variables of the problem
		Variable[] vars = varArray(8);
		
		//Describe the conditions of the problem
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
		
		//Ask for a result and that's it!
/*		System.out.println(all(relation(vars),DIFFER,cons[8])+"\n");
		
		//Do whatever you want with the result.
		for (int i = 0; i < ((Variable) vars[0]).getValues().length; i++) {
			System.out.println("\n"); 
			printResult(vars,i);
			System.out.println("\n");
			int answer = JOptionPane.showConfirmDialog(null, "NEXT?"); 
			if (answer==JOptionPane.CANCEL_OPTION || answer==JOptionPane.NO_OPTION)
				break;
		}
*/
		
		//Another Way of doing it is the lazy way:
		Lazy lazy=lazy(relation(vars),DIFFER,cons[8]);
		VariableMap m;
		while (!(m=lazy.lazy()).isEmpty()) {
			System.out.println("\n"); 
			printResult(m.toArray(vars),0);
			System.out.println("\n");
			int answer = JOptionPane.showConfirmDialog(null, "NEXT?"); 
			if (answer==JOptionPane.CANCEL_OPTION || answer==JOptionPane.NO_OPTION)
				break;			
		}
	}
	
	
	public static void printResult(Variable[] vars, int i) {
		System.out.println("\t    -----");
		System.out.println("\t    | "+(vars[0]).getValues()[i]+" |");
		System.out.println("\t-------------");
		System.out.println("\t| "+(vars[1]).getValues()[i]+" | "+((Variable) vars[2]).getValues()[i]+" | "+((Variable) vars[3]).getValues()[i]+" |");
		System.out.println("\t-------------");
		System.out.println("\t| "+(vars[4]).getValues()[i]+" | "+((Variable) vars[5]).getValues()[i]+" | "+((Variable) vars[6]).getValues()[i]+" |");
		System.out.println("\t-------------");
		System.out.println("\t    | "+(vars[7]).getValues()[i]+" |");
		System.out.println("\t    -----");
	}
	
}
