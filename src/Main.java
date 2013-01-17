import static LJava.LJ.*;
import LJava.Formula;
import LJava.Lazy;

public class Main {

	public class A {
		public boolean is() {return false;}
	}
	
	public class B extends A{
	}

	public class C extends B{
		@Override
		public boolean is() {return true;}
	}

	
	public class D {
		public A mine;
		public D(A a) {
			mine=a;
		}
		
		public void say() {
			if (mine.is()) System.out.println("is");
			else System.out.println("wtf");
		}
	}
	
	
	public static void main(String[] args) {
		Main bs=new Main();
		D d=bs.new D(bs.new C());
		d.say();
	}
	
	
}
