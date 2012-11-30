package LJava;

import java.util.HashSet;

public interface QueryParameter {
	public boolean map(VariableMap m, boolean cut);
	public HashSet<Variable> getVars();
}
