package LJava;

/**
 * @author Tzali Maimon
 * This class offers a pre-define Formulas for some basic logical commands. This is used since LJ is still under the "rules" of the Java compiler and needs to represents its conditions as Objects. 
 */
public final class Utils {
	
	
//Predefined Functors
	/**
	 * Formulating the compare of Objects.
	 */
	public static Formula<Object,Integer> cmp=new Formula<Object,Integer>("Compare", Object.class) {
		@SuppressWarnings({ "rawtypes", "unchecked" })
		@Override
		protected Integer f(Object[] p) {
			if (p.length!=2) return Integer.MIN_VALUE;
			if ((p[0] instanceof Number) && (p[1] instanceof Number)) {
				double a=((Number)p[0]).doubleValue();
				double b=((Number)p[1]).doubleValue();
				if (a>b) return 1;
				if (b>a) return -1;
				return 0;
			}
			else {
				if (p[0].getClass().equals(p[1].getClass()) && (p[0] instanceof Comparable)) {
					int result = ((Comparable) p[0]).compareTo(p[1]);
					if (result<0) return -1;
					if (result>0) return 1;
					return 0;
				}
				return Integer.MIN_VALUE;
			}
		}};

	/**
	 * a formula for True
	 */
	public static Formula<Object, Boolean> LJTrue=new Formula<Object,Boolean>("$LJava_True$", Object.class) {
		@Override
		protected Boolean f(Object[] p) {
			return true;
		}};

	/**
	 * a formula for false
	 */
	public static Formula<Object, Boolean> LJFalse=new Formula<Object,Boolean>("$LJava_False$", Object.class) {
		@Override
		protected Boolean f(Object[] p) {
			return false;
		}};
//End of predefined functors
}
