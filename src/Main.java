import static LJava.LJ.*;
import LJava.Formula;
import LJava.Lazy;

public class Main {

	public static void main(String[] args) {
		Formula<Integer, Integer[]> inc=new Formula<Integer, Integer[]>("inc", Integer.class) {
			protected Integer[] f(Integer[] p) {
				for (int i=0; i<p.length; i++) p[i]++;
				return p;
			}
		};
		
		Formula<Integer, Boolean> f=new Formula<Integer, Boolean>("T", Integer.class) {
			protected Boolean f(Integer[] p) {
				return (p[0]%2==0);
			}
		};
		
		
		Lazy l=lz(f, inc, 0);
		for (int i=0; i<10; i++)
			System.out.println(l.lz());
		
		
		
		
	}
	
	
}
