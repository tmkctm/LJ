package LJava;
import static LJava.LJ.*;

public class Reflection {

public static Formula<Object, Object> val=new Formula<Object, Object>("val", Object.class) {
	@Override
	protected Object f(Object... p) {
		if (p.length==1) {
			try {	return val((Object) p[0]);	}
			catch (Exception e) {}
		}
		return undefined;
	}};


public static Formula<Object, Object> isSet=new Formula<Object, Object>("isSet", Object.class) {
	@Override
	protected Object f(Object... p) {
		if (p.length==2) {
			try {	return isSet((Variable[]) p[0], (Object[]) p[1]);	}
			catch (Exception e) {}
		}
		try {	return isSet(p);	}
		catch (Exception e) {}
		return undefined;
	}};


public static Formula<Object, Object> instantiate=new Formula<Object, Object>("instantiate", Object.class) {
	@Override
	protected Object f(Object... p) {
		if (p.length==1) {
			try {	return instantiate((VariableMap) p[0]);	}
			catch (Exception e) {}
		}
		return undefined;
	}};


public static Formula<Object, Object> e=new Formula<Object, Object>("e", Object.class) {
	@Override
	protected Object f(Object... p) {
		if (p.length==1) {
			try {	return e((QueryParameter) p[0]);	}
			catch (Exception e) {}
		}
		if (p.length==3) {
			try {	return e((QueryParameter) p[0], (LogicOperator) p[1], (QueryParameter) p[2]);	}
			catch (Exception e) {}
		}
		try {	return e(p);	}
		catch (Exception e) {}
		return undefined;
	}};


public static Formula<Object, Object> c=new Formula<Object, Object>("c", Object.class) {
	@Override
	protected Object f(Object... p) {
		if (p.length==3) {
			try {	return c((QueryParameter) p[0], (LogicOperator) p[1], (QueryParameter) p[2]);	}
			catch (Exception e) {}
		}
		return undefined;
	}};


public static Formula<Object, Object> condition=new Formula<Object, Object>("condition", Object.class) {
	@Override
	protected Object f(Object... p) {
		if (p.length==3) {
			try {	return condition((QueryParameter) p[0], (LogicOperator) p[1], (QueryParameter) p[2]);	}
			catch (Exception e) {}
		}
		return undefined;
	}};


public static Formula<Object, Object> var=new Formula<Object, Object>("var", Object.class) {
	@Override
	protected Object f(Object... p) {
		if (p.length==1) {
			try {	return var((Object) p[0]);	}
			catch (Exception e) {}
		}
		if (p.length==1) {
			try {	return var((Variable) p[0]);	}
			catch (Exception e) {}
		}
		if (p.length==0) {
			try {	return var();	}
			catch (Exception e) {}
		}
		return undefined;
	}};


public static Formula<Object, Object> or=new Formula<Object, Object>("or", Object.class) {
	@Override
	protected Object f(Object... p) {
		if (p.length==2) {
			try {	return or((QueryParameter) p[0], (QueryParameter) p[1]);	}
			catch (Exception e) {}
		}
		return undefined;
	}};


public static Formula<Object, Object> a=new Formula<Object, Object>("a", Object.class) {
	@Override
	protected Object f(Object... p) {
		if (p.length==1) {
			try {	return a((QueryParameter) p[0]);	}
			catch (Exception e) {}
		}
		if (p.length==3) {
			try {	return a((QueryParameter) p[0], (LogicOperator) p[1], (QueryParameter) p[2]);	}
			catch (Exception e) {}
		}
		try {	return a(p);	}
		catch (Exception e) {}
		return undefined;
	}};


public static Formula<Object, Object> relation=new Formula<Object, Object>("relation", Object.class) {
	@Override
	protected Object f(Object... p) {
		try {	return relation(p);	}
		catch (Exception e) {}
		return undefined;
	}};


public static Formula<Object, Object> same=new Formula<Object, Object>("same", Object.class) {
	@Override
	protected Object f(Object... p) {
		if (p.length==2) {
			try {	return same((Object) p[0], (Object) p[1]);	}
			catch (Exception e) {}
		}
		return undefined;
	}};


public static Formula<Object, Object> where=new Formula<Object, Object>("where", Object.class) {
	@Override
	protected Object f(Object... p) {
		if (p.length==2) {
			try {	return where((QueryParameter) p[0], (QueryParameter) p[1]);	}
			catch (Exception e) {}
		}
		return undefined;
	}};


public static Formula<Object, Object> and=new Formula<Object, Object>("and", Object.class) {
	@Override
	protected Object f(Object... p) {
		if (p.length==2) {
			try {	return and((QueryParameter) p[0], (QueryParameter) p[1]);	}
			catch (Exception e) {}
		}
		return undefined;
	}};


public static Formula<Object, Object> differ=new Formula<Object, Object>("differ", Object.class) {
	@Override
	protected Object f(Object... p) {
		if (p.length==2) {
			try {	return differ((QueryParameter) p[0], (QueryParameter) p[1]);	}
			catch (Exception e) {}
		}
		return undefined;
	}};


public static Formula<Object, Object> deepInvoke=new Formula<Object, Object>("deepInvoke", Object.class) {
	@Override
	protected Object f(Object... p) {
		if (p.length==2) {
			try {	return deepInvoke((Object[]) p[0], (Formula) p[1]);	}
			catch (Exception e) {}
		}
		return undefined;
	}};


public static Formula<Object, Object> r=new Formula<Object, Object>("r", Object.class) {
	@Override
	protected Object f(Object... p) {
		try {	return r(p);	}
		catch (Exception e) {}
		return undefined;
	}};


public static Formula<Object, Object> exists=new Formula<Object, Object>("exists", Object.class) {
	@Override
	protected Object f(Object... p) {
		if (p.length==1) {
			try {	return exists((QueryParameter) p[0]);	}
			catch (Exception e) {}
		}
		if (p.length==3) {
			try {	return exists((QueryParameter) p[0], (LogicOperator) p[1], (QueryParameter) p[2]);	}
			catch (Exception e) {}
		}
		try {	return exists(p);	}
		catch (Exception e) {}
		return undefined;
	}};


public static Formula<Object, Object> lazy=new Formula<Object, Object>("lazy", Object.class) {
	@Override
	protected Object f(Object... p) {
		if (p.length==1) {
			try {	return lazy((Constraint) p[0]);	}
			catch (Exception e) {}
		}
		if (p.length==3) {
			try {	return lazy((QueryParameter) p[0], (LogicOperator) p[1], (QueryParameter) p[2]);	}
			catch (Exception e) {}
		}
		return undefined;
	}};


public static Formula<Object, Object> variable=new Formula<Object, Object>("variable", Object.class) {
	@Override
	protected Object f(Object... p) {
		if (p.length==1) {
			try {	return variable((Object) p[0]);	}
			catch (Exception e) {}
		}
		return undefined;
	}};


public static Formula<Object, Object> all=new Formula<Object, Object>("all", Object.class) {
	@Override
	protected Object f(Object... p) {
		if (p.length==1) {
			try {	return all((QueryParameter) p[0]);	}
			catch (Exception e) {}
		}
		if (p.length==3) {
			try {	return all((QueryParameter) p[0], (LogicOperator) p[1], (QueryParameter) p[2]);	}
			catch (Exception e) {}
		}
		try {	return all(p);	}
		catch (Exception e) {}
		return undefined;
	}};


}