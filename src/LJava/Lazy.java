package LJava;



public interface Lazy<B,P> {
	public P lz();
	public P current();
	public String toString();
	public Variable[] getVars();
	public boolean noVars();
	public void resetLazy();
	public B base();
}	



