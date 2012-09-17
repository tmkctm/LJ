package LJava;

import java.util.Comparator;

public class Utils {
	
	static public enum  QueryResult{
		SUCCESS, FAILED_INSTANTIATE, FAILED 
	}
	
	
	public static final QueryResult SUCCESS=QueryResult.SUCCESS;
	public static final QueryResult FAILED=QueryResult.FAILED;
	

	public static boolean variable(Object x) {
		return (x instanceof Variable);
	}
	
	
	protected static class CompareOperator implements Comparator<Object> {
		public int compare(Object a, Object b) {
			if (variable(a))
				if (!variable(b)) return 1;
			if (variable(b))
				if (!variable(a)) return -1;
			
			if (a.hashCode()>b.hashCode()) return 1;
			if (a.hashCode()<b.hashCode()) return -1;
			return 0;
		}
	}	
}
