package bayeos.frame;

import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * Methods provided by Javascript Engine
 * 
 * @author oliver
 * 
 *
 */
public class JSEngine {
	private final static ScriptEngine engine = new ScriptEngineManager().getEngineByName("js");
		
	/**
	 * Builds a Map out of a String in JavaScript notation
	 * Can be used with different value types: 
	 * Map<String,Numeric> m = getMap("{'name':10}");
	 * Map<String,String> m = getMap("{'name':'oliver'}");
	 * 
	 * @param values Map in JavaScript notation like: {'name':10} or {'name':'Oliver'}
	 * @return Map
	 * @throws ScriptException
	 */
	@SuppressWarnings("unchecked")
	public static <T> Map<String,T> getMap(String values) throws ScriptException{
		StringBuffer n = new StringBuffer("var map = ");
		n.append(values).append(";");
		engine.eval(n.toString());		
		Object o = engine.get("map");			
		return (Map<String, T>) o; 
	}
			

}
