package LJava;



/**
 * @author Tzali Maimon
 * This interface allows you to use several features of LJ through lazy evaluation. <p>
 * for more details you should read more at logicjava.wordpress.com
 * @param <P> - The type of returned type from the lazy evaluation (prevents you from later casting).
 */
public interface Lazy<P> {
	/**
	 * @return the next evaluation
	 */
	public P lz();
	/**
	 * @return the last evaluation returned
	 */
	public P current();
	public String toString();
	/**
	 * @return the active variables in the lazy object.
	 */
	public Variable[] getVars();
	/**
	 * @return true if there are no active variables in the lazy object (meaning usually that there won't be any evaluation)
	 */
	public boolean noVars();
	/**
	 * resets the lazy object to start evaluating from start. 
	 */
	public void resetLazy();
	/**
	 * @return the object on which the lazy interface is running, so for a lazy that was created on permutations, for example, it will return the Group object that is used through the evaluation. <p>
	 * for more info read at logicjava.wordpress.com
	 */
	public Object base();
}	



