package LJava;

import java.util.Collection;
import java.util.Map;

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
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected static <K,V> void addTo(Map map, K key, V val, Class<?> type) {
		Collection collection=(Collection) map.get(key);
		if (collection==null) {
			try{
				collection=(Collection) type.newInstance();
				map.put(key, collection);
			}catch (Exception e){}
		}
		collection.add(val);
	}
	
	
//Predefined Functors
	public static final Formula<Number,Double> max=new Formula<Number,Double>("Max", Number.class) {
		@Override
		protected Double f(Number... p) {
			Double result=Double.MIN_VALUE;
			for (Number n : p) 
				if (n.doubleValue()>result) result=n.doubleValue();
			return result;
		}};
	
	public static final Formula<Number,Double> min=new Formula<Number,Double>("Min", Number.class){
		@Override
		protected Double f(Number... p) {
			if (p.length==0) return Double.MIN_VALUE;
			Double result=p[0].doubleValue();
			for (Number n : p)
				if (n.doubleValue()<result) result=n.doubleValue();
			return result;
		}};

	public static final Formula<Object,Integer> cmp=new Formula<Object,Integer>("Compare", Object.class) {
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
