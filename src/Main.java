import static LJava.LJ.*;
import static LJava.MathFormulas.abs;
import static org.junit.Assert.assertFalse;

import java.util.HashMap;
import java.util.HashSet;

import LJava.Constraint;
import LJava.Formula;
import LJava.Lazy;
import LJava.Variable;
import LJava.VariableMap;

public class Main {
	
	public static void main(String[] args) {
		Object[] values=new Object[] {1,2,3,4,5,6,7,8};
		group(values);
		
		for (int j=0; j<20; j++) {
		Variable[] vars = varArray(8);
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
		
		System.out.println(a(relation(vars),DIFFER,cons[8]));
		int count=vars[0].getValues().length;
		System.out.println("wtf: "+count);
		
		HashMap<String, Integer> set=new HashMap<String, Integer>();
		for (int i=0; i<count; i++) {
			String s=vars[0].getValues()[i].toString()+
					vars[1].getValues()[i].toString()+
					vars[2].getValues()[i].toString()+
					vars[3].getValues()[i].toString()+
					vars[4].getValues()[i].toString()+
					vars[5].getValues()[i].toString()+
					vars[6].getValues()[i].toString()+
					vars[7].getValues()[i].toString();
			if (set.containsKey(s)) {
				System.out.println(i+";  "+set.get(s)+";   "+ s);
			}
			else set.put(s,i);
		}
		}
		System.exit(0);
	}
	
	
}
