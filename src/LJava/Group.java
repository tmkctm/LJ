package LJava;
import static LJava.Utils.*;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

public class Group extends Relation{
	
	private HashMap<Object, Integer> args;
	
	public Group(String n, Object... params) {
		super(n, params.length);		
	}
	
	
	@Override
	public Object[] args() {
		Object arr[]=new Object[this.argsLength()];
		int i=-1;
		for (Map.Entry<Object, Integer> entry : args.entrySet()) {		
			
		}
		return arr;
	}
	
	
	@Override
	protected boolean satisfy(Relation r, VariableValuesMap varValues){
		return false;
	}
	
}
