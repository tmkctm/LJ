import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class ReflectionDumper {

	public static String fileName="";
	public static String outName="";
	public static String className="";
	public static boolean newFile=false;
	public static StringBuilder text=new StringBuilder();
	public static HashMap<String, ArrayList<Signature>> sigs=new HashMap<String, ArrayList<Signature>>(); 
	
	public static void main(String[] args) {
		for (int i=0; i<args.length; i++) {
			if (args[i].equals("-file")) fileName=args[i+1];
			else if (args[i].equals("-out")) outName=args[i+1];
			else if (args[i].equals("-class")) className=args[i+1];
			else newFile=(newFile || args[i].equals("-new"));
		}
		
		try{
			if (!newFile) {
				BufferedReader reader=new BufferedReader(new InputStreamReader(new FileInputStream(outName)));
				String line;
				while ((line=reader.readLine())!=null) text.append(line);
				text.deleteCharAt(text.length()-1);
				text.append("\n\n");
				reader.close();
			}
			BufferedReader reader=new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
			BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outName)));
			if (newFile) {
				text.append("package LJava;\n"+
							"import static LJava."+className+".*;\n\n"+
							"public class Reflection {\n\n");				
			}
			writer.write(text.toString());
			writer.flush();
			text=new StringBuilder();
			
			String line="";
			while ((line=reader.readLine())!=null) {
				if (!line.contains("void") && !line.contains("class") && line.contains("public") && line.contains("static") && (line.contains("(") && line.contains(")") && line.contains("{"))) {
					String[] tokens=line.split(" ");
					if (!tokens[1].contains("(")) {
						Signature sig=new ReflectionDumper().new Signature(tokens);
						if (sig.name.length()>0) {
							ArrayList<Signature> l=sigs.get(sig.name);
							if (l==null) {
								l=new ArrayList<Signature>();
								l.add(sig);
								sigs.put(sig.name,l);
							}
							else l.add(sig);
						}
					}
				}
			}
			
			for (Map.Entry<String, ArrayList<Signature>> e: sigs.entrySet()) {
				text.append("public static Formula<Object, Object> "+e.getKey()+"=new Formula<Object, Object>(\""+e.getKey()+"\", Object.class) {\n"+
						"\t@Override\n"+
						"\tprotected Object f(Object... p) {\n");
				for (Signature s: e.getValue()) {
					if (!s.threeDots) {
						text.append("\t\tif (p.length=="+s.types.size()+") {\n"+
									"\t\t\ttry {\treturn "+s.name+"("+createArgs(s.types)+");\t}\n"+
									"\t\t\tcatch (Exception e) {}\n"+
									"\t\t}\n");
					}
					else {
						text.append("\t\ttry {\treturn "+s.name+"(p);\t}\n"+
									"\t\tcatch (Exception e) {}\n");
					}
				}
				text.append("\t\treturn undefined;\n"+
						"\t}};\n\n\n");				
			}
			
			text.append("}");
			writer.write(text.toString());
			writer.flush();
			
			writer.close();
			reader.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	private static String createArgs(ArrayList<String> types) {
		if (types.isEmpty()) return "";
		StringBuilder r=new StringBuilder();
		for (int i=0; i<types.size()-1; i++)
			r.append("("+types.get(i)+") p["+i+"], ");
		r.append("("+types.get(types.size()-1)+") p["+(types.size()-1)+"]");
		return r.toString().replaceAll("\\(int\\)","(Integer)");
	}
	
	
	private class Signature {
		public String name;
		public ArrayList<String> types=new ArrayList<String>();
		public boolean threeDots=false; 
		
		public Signature(String[] tokens) {
			boolean added=false;
			int i;
			for (i=0; i<tokens.length; i++)
				if (tokens[i].contains("(")) break;
			int index=tokens[i].indexOf("(");
			name=tokens[i].substring(0,index);
			if (index==tokens[i].length()-1) i++;
			else tokens[i]=tokens[i].substring(index+1);
			for (int j=i; j<tokens.length; j++) {
				if (!tokens[j].contains(")") && !tokens[j].contains("{")) {
					String t=tokens[j];
					if (tokens[j].contains(",")) 
						if (tokens[j].indexOf(',')!=tokens[j].length()-1) t=tokens[j].substring(tokens[j].indexOf(',')+1);
						else continue;
					if (t.contains("Object...")) 
						if (!added) threeDots=true;
						else {
							name="";
							break;
						}
					types.add(t);
					added=true;
				}
			}
		}
	}
	
}
