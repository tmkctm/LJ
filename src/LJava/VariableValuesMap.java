package LJava;

import java.util.ArrayList;
import java.util.HashMap;

public class VariableValuesMap implements QueryParameter{
	
	protected HashMap<Variable, ArrayList<Object>> map=new HashMap<Variable, ArrayList<Object>>();;
	protected HashMap<Variable, Constraint> constraints= new HashMap<Variable, Constraint>();
	protected HashMap<Variable, Constraint> valsByConstrains= new HashMap<Variable, Constraint>();
	
	public boolean isEmpty() {
		return (map.isEmpty() && valsByConstrains.isEmpty());
	}
	
	@Override
	public VariableValuesMap map(){
		return this;
	}

}
