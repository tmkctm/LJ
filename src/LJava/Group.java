package LJava;

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
		return false;
	}
		
}
