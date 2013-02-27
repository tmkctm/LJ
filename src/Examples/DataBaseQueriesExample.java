package Examples;
import static LJava.LJ.*;
import static LJava.Utils.*;
import LJava.Formula;
import LJava.Variable;

public class DataBaseQueriesExample {

	public static void main(String[] args) {
		//Read data from file into the world of LJ
		int index=file.indexOf("\n");
		int prev=-1;
		while (index>-1) {
			String line=file.substring(prev+1,index);
			String[] record=line.split(",");
			associate(r(record[0], record[1], Integer.parseInt(record[2]), Integer.parseInt(record[3])));
			prev=index;
			index=file.indexOf("\n", index+1);
		}
		
		//Now use LJ to query on data.
		Variable name=var();
		System.out.println(e(r("doctor", name, _, _)));  // <--- exists?
		System.out.println(name);
		name=var();
		System.out.println(a(r("doctor", name, _, _)));  // <--- all!
		System.out.println(name);
		System.out.println(e(r("vet", _, 123565498 , _)));  // <--- doesn't exist!
		
		Variable doc=var();  Variable sick=var();
		Variable age=var();
		Formula<String, Character> initial=new Formula<String, Character>("Initial", String.class) {
			@Override
			protected Character f(String[] s) {				
				return s[0].charAt(0);
			}};
		System.out.println(a(r("doctor", doc, _, age),WHERE,
					 c(c(cmp,1,age,20),AND,c(cmp,1,30,age))));
		System.out.println(a(r("patient", sick, _, _),WHERE, c(initial,'A',sick)));
		System.out.println(doc+"\t"+sick);		
	}


	
	public static String file=
			"doctor,Guliver,1112223,34\n" +
			"doctor,Kavorkian,22233322,61\n" +
			"vet,liliput,22222222,5\n" +
			"patient,nimnim,654654654,12\n" +
			"patient,Al Bundy,321321321,32\n" +
			"doctor,Lovely,1111111,23\n" +
			"patient,SnoppDogg,99999999,1\n";
	

}
