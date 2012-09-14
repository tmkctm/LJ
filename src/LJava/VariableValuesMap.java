package LJava;

import java.util.HashMap;
import java.util.LinkedHashSet;

public class VariableValuesMap implements QueryParameter{
	
	public HashMap<Variable, LinkedHashSet<Object>> map;
	
	public VariableValuesMap() {
		map=new HashMap<Variable, LinkedHashSet<Object>>();
	}
	
	public boolean isEmpty() {
		return map.isEmpty();
	}

}
