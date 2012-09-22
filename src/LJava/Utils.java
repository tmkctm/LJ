package LJava;

public class Utils {
	
	static public enum  QueryResult{
		SUCCESS, FAILED_INSTANTIATE, FAILED 
	}
	
	
	public static final QueryResult SUCCESS=QueryResult.SUCCESS;
	public static final QueryResult FAILED=QueryResult.FAILED;
	
	public static boolean variable(Object x) {
		return (x instanceof Variable);
	}	
	
	
//Predefined Functors
	public static final Functor<Number,Integer> cmp=new Functor<Number,Integer>("Compare") {
		@Override
		protected Integer f(Number... p) {
			if (p.length!=2) return Integer.MIN_VALUE;
			if (p[0].doubleValue()>p[1].doubleValue()) return 1;
			if (p[0].doubleValue()<p[1].doubleValue()) return -1;
			return 0;
		}};
		
	public static final Functor<Number,Double> max=new Functor<Number,Double>("Max"){
		@Override
		protected Double f(Number... p) {
			if (p.length==0) return Double.MAX_VALUE;
			Double result=p[0].doubleValue();
			for (Number n : p) {
				if (n.doubleValue()>result) result=n.doubleValue();
			}
			return result;
		}};
	
	public static final Functor<Number,Double> min=new Functor<Number,Double>("Min"){
		@Override
		protected Double f(Number... p) {
			if (p.length==0) return Double.MIN_VALUE;
			Double result=p[0].doubleValue();
			for (Number n : p) {
				if (n.doubleValue()<result) result=n.doubleValue();
			}
			return result;
		}};

	public static final Functor<Object,Integer> cmpObjects=new Functor<Object,Integer>("Compare") {
		@Override
		protected Integer f(Object... p) {
			if (p.length!=2) return Integer.MIN_VALUE;
			Number temp[]=new Number[2];
			if ((p[0] instanceof Number) && (p[1] instanceof Number)) {				
				temp[0]=(Number) p[0];
				temp[1]=(Number) p[1];				
			}
			else {
				temp[0]=p[0].hashCode();
				temp[1]=p[1].hashCode();								
			}
			return cmp.invoke(temp);
		}};
//End of predefined functors
}
