package LJava;

import java.util.ArrayList;
import java.util.HashMap;

public class VariableValuesMap implements QueryParameter{
	
	public HashMap<Variable, ArrayList<Object>> map;
	
	public VariableValuesMap() {
		map=new HashMap<Variable, ArrayList<Object>>();
	}
	
	public boolean isEmpty() {
		return map.isEmpty();
	}
	
	@Override
	public VariableValuesMap map(){
		return this;
	}

}
