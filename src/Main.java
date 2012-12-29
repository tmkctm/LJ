import static LJava.LJ.*;
import static LJava.Utils.*;
import LJava.Group;
import LJava.LazyGroup;
import LJava.Variable;
import LJava.VariableMap;

public class Main {

	public static void main(String[] args) {
		group(1,2,3,4);
		System.out.println(exists(2,4,3,1));
	}
}
