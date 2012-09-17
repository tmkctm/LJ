package LJava;

import java.util.Arrays;
import LJava.Utils.CompareOperator;

public class Group extends Association{
	
	public Group(String n, Object... params) {
		super(true, n, params);
	}
	

	@Override
	public boolean isGroup() {
		return true;
	}	
	
	@Override
	protected boolean satisfy(Relation r, VariableValuesMap varValues){
		Object rArgs[]=r.args();
		Arrays.sort(rArgs,new CompareOperator());
		return false;
	}
		
}
