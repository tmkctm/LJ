package LJava;

import java.util.ArrayList;
import java.util.HashMap;

public class VariableValuesMap implements QueryParameter{
	
	protected HashMap<Variable, ArrayList<Object>> map;
	protected HashMap<Variable, Constraint> constraints;
	
	public VariableValuesMap() {
		map=new HashMap<Variable, ArrayList<Object>>();
		constraints=new HashMap<Variable, Constraint>();
	}
	
	public boolean isEmpty() {
		return (map.isEmpty() && constraints.isEmpty());
	}
	
	@Override
	public VariableValuesMap map(){
		return this;
	}

}
