package LJava;

public class Utils {
	
	static public final LogicOperator OR=LogicOperator.OR;
	static public final LogicOperator AND=LogicOperator.AND;
	static public final LogicOperator DIFFER=LogicOperator.DIFFER;
	static public enum  LogicOperator{
		OR, AND, DIFFER 	}
	
	public static final QueryResult SUCCESS=QueryResult.SUCCESS;
	public static final QueryResult FAILED=QueryResult.FAILED;	
	public static final QueryResult FAILED_INSTANTIATE=QueryResult.FAILED_INSTANTIATE;
	static public enum  QueryResult{
		SUCCESS, FAILED_INSTANTIATE, FAILED		}
		
	
	public static boolean variable(Object x) {
		return (x instanceof Variable);
	}	
	
	
//Predefined Functors
	public static final Functor<Number,Double> max=new Functor<Number,Double>("Max", Number.class) {
		@Override
		protected Double f(Number... p) {
			Double result=Double.MIN_VALUE;
			for (Number n : p) 
				if (n.doubleValue()>result) result=n.doubleValue();
			return result;
		}};
	
	public static final Functor<Number,Double> min=new Functor<Number,Double>("Min", Number.class){
		@Override
		protected Double f(Number... p) {
			if (p.length==0) return Double.MIN_VALUE;
			Double result=p[0].doubleValue();
			for (Number n : p)
				if (n.doubleValue()<result) result=n.doubleValue();
			return result;
		}};

	public static final Functor<Object,Integer> cmp=new Functor<Object,Integer>("Compare", Object.class) {
		@Override
		protected Integer f(Object... p) {
			if (p.length!=2) return Integer.MIN_VALUE;
			double a; 		double b;
			if ((p[0] instanceof Number) && (p[1] instanceof Number)) {
				a=((Number)p[0]).doubleValue();
				b=((Number)p[1]).doubleValue();
			}
			else {
				a=p[0].hashCode();
				b=p[1].hashCode();
			}
			if (a>b) return 1;
			if (a<b) return -1;
			return 0;
		}};
//End of predefined functors
}
