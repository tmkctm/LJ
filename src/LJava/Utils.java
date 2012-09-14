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
}
