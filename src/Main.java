import LJava.*;

public class Main {

	public static void main(String[] args) {	
		
		Functor<Integer,Integer> max=new Functor<Integer,Integer>("per") {
			@Override
			protected Integer f(Integer... p) {				
				return p[1];
			}};
	
		
		System.out.println(max.satisfy(1,-2,1,4,5,643));
		System.out.println(max.satisfy(4,-1,1,3,4,5));
	}
}
