package LJava;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import static LJava.LJ.*;


public class Group extends Association {
	
	protected final Map<Object, Integer> argsMap;
	
	public Group(String n, Object... params) {
		super(n, params);
		HashMap<Object, Integer> map=new HashMap<Object, Integer>();
		if (params!=null)
			for (Object element : params) increment(map, element,1);		
		argsMap=Collections.unmodifiableMap(map);
	}
	

	@Override
	public boolean isGroup() {
		return true;
	}
	
	
	@Override
	protected boolean satisfy(Object[] rArgs, VariableMap varValues){	
		return false;
	}
		
}
