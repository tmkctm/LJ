package LJava;

public class MathFormulas {

	public static Formula<Number,Double> max=new Formula<Number,Double>("Max", Number.class) {
		@Override
		protected Double f(Number[] p) {
			Double result=Double.MIN_VALUE;
			for (Number n : p) 
				if (n.doubleValue()>result) result=n.doubleValue();
			return result;
		}};
	
		
	public static Formula<Number,Double> min=new Formula<Number,Double>("Min", Number.class){
		@Override
		protected Double f(Number[] p) {
			if (p.length==0) return Double.MIN_VALUE;
			Double result=p[0].doubleValue();
			for (Number n : p)
				if (n.doubleValue()<result) result=n.doubleValue();
			return result;
		}};	
	
	
	public static Formula<Number, Double> abs=new Formula<Number, Double>("Absolute", Number.class) {
		@Override
		protected Double f(Number[] p) {
			if (p.length!=2) return -1.0;
			return Math.abs(p[0].doubleValue()-p[1].doubleValue());
		}};
		
		
	public static Formula<Number, Double> pow=new Formula<Number, Double>("Power", Number.class) {
		@Override
		protected Double f(Number[] p) {
			if (p.length==0) return 0.0;
			double result=p[0].doubleValue();
			for (int i=1; i<p.length; i++) result=Math.pow(result, p[i].doubleValue());
			return result;
		}};
		
	
	public static Formula<Number, Double> sqrt=new Formula<Number, Double>("Sqrt", Number.class) {
		@Override
		protected Double f(Number[] p) {
			if (p.length!=1) return -1.0;
			return Math.sqrt(p[0].doubleValue());
		}};
		
		
	public static Formula<Number, Double> sum=new Formula<Number, Double>("Sum", Number.class) {
		@Override
		protected Double f(Number[] p) {
			if (p.length==0) return 0.0;
			double result=p[0].doubleValue();
			for (int i=1; i<p.length; i++) result=result+p[i].doubleValue();
			return result;
		}};
		
		
	public static Formula<Number, Double> product=new Formula<Number, Double>("Product", Number.class) {
		@Override
		protected Double f(Number[] p) {
			if (p.length==0) return 0.0;
			double result=p[0].doubleValue();
			for (int i=1; i<p.length; i++) result=result*p[i].doubleValue();
			return result;
		}};
	
}
