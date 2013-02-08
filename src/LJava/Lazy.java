package LJava;



public interface Lazy<P> {
	public P lz();
	public P current();
	public String toString();
	public Variable[] getVars();
	public boolean noVars();
	public void resetLazy();
	public Object base();
	public Lazy<P> branch();
}	



