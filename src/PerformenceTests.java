import static LJava.LJ.AND;
import static LJava.LJ.DIFFER;
import static LJava.LJ.OR;
import static LJava.LJ._;
import static LJava.LJ.a;
import static LJava.LJ.c;
import static LJava.LJ.group;
import static LJava.LJ.r;
import static LJava.LJ.relate;
import static LJava.LJ.relation;
import static LJava.LJ.setLJProperty;
import static LJava.LJ.varArray;
import static LJava.MathFormulas.abs;

import java.util.Date;

import org.junit.Test;

import LJava.Constraint;
import LJava.Variable;
import LJava.LJ.Property;

public class PerformenceTests {

	private static int threads=3;
	private static int testAllDBLoad=20000;
	private static int testAllConstraintLoad=10;
	private static int testAllLoopCount=5;
	
	
	@Test
	public void testAll() {
		Object[] values=new Object[] {1,2,3,4,5,6,7,8};
		group(values);
		setLJProperty(Property.ThreadCount, threads);
		for (int j=0; j<testAllDBLoad; j++) 
			relate(5,8,3,4,5,1,j,3);
		double sum=0;
		for (int j=0; j<testAllLoopCount; j++) {
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
			Constraint c=cons[8];
			for (int k=0; k<testAllConstraintLoad; k++) {
				Constraint temp=new Constraint(r(5,8,3,4,5,1,k,3),AND,r(_,_,_,_,_,_,k,_));
				c=new Constraint(temp,AND,c); 
			}
	
			long start=new Date().getTime();
			System.out.println("starting "+j);
			System.out.println(a(c(relation(vars),DIFFER,cons[8]),AND,c));
			sum=sum+(new Date().getTime())-start;
			System.out.println("finished "+j);
		}
		System.out.println("TestAll report: "+(sum/testAllLoopCount));
		System.exit(0);		
	}
	
	
}
