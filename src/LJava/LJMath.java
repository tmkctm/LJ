package LJava;

import static LJava.LJ.lz;

/**
 * @author Tzali Maimon
 * This is a class that offers a pre-defined Formulas for some math uses.<p>
 * A formula that gets more arguments that needed will ignore them or will return an unreasonable answer.
 */
public class LJMath {

//Formulas for known math functions
	/**
	 * returns the maximum between numbers
	 */
	public static Formula<Number,Double> max=new Formula<Number,Double>("Max", Number.class) {
		@Override
		protected Double f(Number[] p) {
			Double result=Double.MIN_VALUE;
			for (Number n : p) 
				if (n.doubleValue()>result) result=n.doubleValue();
			return result;
		}};
	
		
	/**
	 * returns the minimum between numbers 
	 */
	public static Formula<Number,Double> min=new Formula<Number,Double>("Min", Number.class){
		@Override
		protected Double f(Number[] p) {
			if (p.length==0) return Double.MIN_VALUE;
			Double result=p[0].doubleValue();
			for (Number n : p)
				if (n.doubleValue()<result) result=n.doubleValue();
			return result;
		}};	
	
	
	/**
	 * the absolute between numbers. 
	 */
	public static Formula<Number, Double> abs=new Formula<Number, Double>("Absolute", Number.class) {
		@Override
		protected Double f(Number[] p) {
			if (p.length!=2) return -1.0;
			return Math.abs(p[0].doubleValue()-p[1].doubleValue());
		}};
		
		
	/**
	 * returns p[0] in the power of p[1] in the power of p[2]... 
	 */
	public static Formula<Number, Double> pow=new Formula<Number, Double>("Power", Number.class) {
		@Override
		protected Double f(Number[] p) {
			if (p.length==0) return 0.0;
			double result=p[0].doubleValue();
			for (int i=1; i<p.length; i++) result=Math.pow(result, p[i].doubleValue());
			return result;
		}};
		
	
	/**
	 * returns the square root of p[0]
	 */
	public static Formula<Number, Double> sqrt=new Formula<Number, Double>("Sqrt", Number.class) {
		@Override
		protected Double f(Number[] p) {
			if (p.length!=1) return -1.0;
			return Math.sqrt(p[0].doubleValue());
		}};
		
		
	/**
	 * returns the sum of numbers
	 */
	public static Formula<Number, Double> sum=new Formula<Number, Double>("Sum", Number.class) {
		@Override
		protected Double f(Number[] p) {
			if (p.length==0) return 0.0;
			double result=p[0].doubleValue();
			for (int i=1; i<p.length; i++) result=result+p[i].doubleValue();
			return result;
		}};
		
		
	/**
	 * returns the product of numbers
	 */
	public static Formula<Number, Double> product=new Formula<Number, Double>("Product", Number.class) {
		@Override
		protected Double f(Number[] p) {
			if (p.length==0) return 0.0;
			double result=p[0].doubleValue();
			for (int i=1; i<p.length; i++) result=result*p[i].doubleValue();
			return result;
		}};
		
		
//Lazy for Countable Math Domains
		
	//Q
		private static Formula<Integer, Integer[]> Qf=new Formula<Integer, Integer[]>("f", Integer.class) {
			@Override
			protected Integer[] f(Integer[] args) {
				return args;
			}};
		private static Formula<Integer, Integer[]> Qinc=new Formula<Integer, Integer[]>("inc", Integer.class) {
			@Override
			protected Integer[] f(Integer[] args) {
				if (args[0]>=0) return new Integer[]{ -args[0], args[1], args[2] };
				if (args[2]==2) return new Integer[]{ (-args[0]-1), (args[1]+1), 0};
				if (args[2]==3) return new Integer[]{ (-args[0]+1), (args[1]-1), 1};
				if (args[1]==1) return new Integer[]{ (-args[0]+1), args[1], 2};
				if (args[0]==-1)	return new Integer[]{ -args[0], (args[1]+1), 3};
				if (args[2]==0) return new Integer[]{ (-args[0]-1), (args[1]+1), 0};
				if (args[2]==1) return new Integer[]{ (-args[0]+1), (args[1]-1), 1};
				return args;
			}};
		/**
		 * A Lazy object that represents the Q domain.
		 */
		public static Lazy<Integer[]> LjQ=lz(Qf, Qinc, 1, 1, 0);

		
	//Z
		private static Formula<Integer, Integer> Zf=new Formula<Integer, Integer>("f", Integer.class) {
			@Override
			protected Integer f(Integer[] args) {
				return args[0];
			}};
		private static Formula<Integer, Integer[]> Zinc=new Formula<Integer, Integer[]>("inc", Integer.class) {
			@Override
			protected Integer[] f(Integer[] args) {
				if (args[0]>0) return new Integer[] {-args[0]};
				return new Integer[] {-args[0]+1};
			}};
		/**
		 * A Lazy object that represents the Z domain.
		 */
		public static Lazy<Integer> LjZ=lz(Zf, Zinc, 0);		
		
}
