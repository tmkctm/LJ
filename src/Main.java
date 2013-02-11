import static LJava.LJ.*;
import static LJava.LJMath.*;
import javax.swing.JOptionPane;
import LJava.Constraint;
import LJava.Group;
import LJava.Lazy;
import LJava.Variable;
import LJava.LJMap;

public class Main {
	
	public static void main(String[] args) {
		puzzleSolving();
	}
	
	
	public static void puzzleSolving() {
		Object[] values=new Object[] {1,2,3,4,5,6,7,8};
		associate(new Group("testLazyAll",values));
		Variable[] vars = varArray("x",8);

		Integer[] neighbors=new Integer[] {0,2 , 1,2 , 1,4 , 2,3, 2,5 , 3,6 , 4,5 , 5,6 , 5,7};
		Constraint[] cons=new Constraint[9];
		cons[0]=c(abs,1,vars[0],vars[2]);
		for (int i=1; i<9; i++) 
			cons[i]=c(cons[i-1],OR,c(abs,1,vars[neighbors[i*2]],vars[neighbors[(i*2)+1]]));
		Lazy<LJMap> lazy=lz(r("testLazyAll",vars),DIFFER,cons[8]);
		
		LJMap m=new LJMap();
		while (!(m=lazy.lz()).isEmpty()) {
			print(m.get(vars));
			if (JOptionPane.showConfirmDialog(null, "continue?")==JOptionPane.CANCEL_OPTION)
				System.exit(0);
		}
	}		
	
	public static void print(Variable[] vars) {
		System.out.println("\n\n");
		System.out.println("\t"+vars[0]);
		System.out.println(vars[1]+"\t"+vars[2]+"\t"+vars[3]);
		System.out.println(vars[4]+"\t"+vars[5]+"\t"+vars[6]);
		System.out.println("\t"+vars[7]);
	}
	
}
