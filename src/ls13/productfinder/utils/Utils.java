package ls13.productfinder.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import ls13.productfinder.ProductFinderException;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;


/**
 * <p>Title: Utility functions </p>
 * <p>Description:  </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * 
 * @author DJ
 * @version 1.0
 */

public class Utils {
	// debug function for printing lists
	static public void printList(List list) {
		Iterator it = list.iterator();
		while (it.hasNext()) {System.out.println(it.next());}
	}
	
	/**
	 * Helper to execute a compiled script in a new context
	 * @param s the compiled script
	 * @param scope the scope containing the user data
	 * @param msgaddon an addon for debugging
	 * @return true, if the expression returns true
	 * @throws ProductFinderException if the script does not return a boolean
	 */
	static public boolean evaluteCompiledExpression(Script s, Scriptable scope, String msgaddon) throws ProductFinderException {
		Context c = Context.enter();
		Object result = s.exec(c,scope);
		Context.exit();
		if (result instanceof Boolean) {
			//System.out.println("result of match: " + result);
			return ((Boolean)result).booleanValue();
		}
		else throw new ProductFinderException("[FATAL] Expression for " + msgaddon + " does not return boolean value");

	} 
	static public Object evaluateFunction(Script s, Scriptable scope) throws ProductFinderException {
		if (s == null) {
			System.err.println("S == null");
		}
		
		
		Context c = Context.enter();
		Object result = s.exec(c,scope);
		Context.exit();
		return result;
	} 
	
	/** 
	 * This is a global counter for creating unique class names when re-compiling
	 * scripts, e.g. when the knowledge base is reloaded
	 */
	public static int reloadCounter = 0;
	
	/**
	 *  method to check identity of hashmaps based on values
	 * @param a first hashmap
	 * @param b second hashmap
	 * @return true, if the hashmaps contain the same key value-pairs
	 * (not the same references)
	 * todo: array checks.
	 */
	public static boolean identical (HashMap a, HashMap b) {
		// not the same size
		if (a.keySet().size() != b.keySet().size()) return false;
		List aKeys = new ArrayList(a.keySet());
		Iterator it = aKeys.iterator();
		Object name;
		Object o1;
		Object o2;
		while (it.hasNext()) {
			name = it.next();
			//System.out.println("getting " + name);
			o1 = a.get(name);
			o2 = b.get(name);
			//System.out.println("o1: " + o1 + " o2: " + o2);
			if (o1 == null && o2 != null) return false;
			if (o1 != null && o2 == null) return false;
			if (o1 == null && o2 == null) continue;
			if (o1.equals(o2)) continue;
			if (o1.getClass() != o2.getClass()) return false;
			
			if (
					(o1 instanceof Integer && o2 instanceof Integer) ||
					(o1 instanceof Double && o2 instanceof Double) ||
					(o1 instanceof String && o2 instanceof String) && 
					
					(!(o1.equals(o2)))) return false;
			
			if (o1 instanceof Object[]) {
				Object[] a1 = (Object[]) o1;
				Object[] a2 = (Object[]) o2;
				if (a1.length != a2.length) return false;
				List l1 = Arrays.asList(a1);
				List l2 = Arrays.asList(a2);
				if (!(l1.containsAll(l2))) {return false;}
				return true;
			}
			
			
			
			
		}
		return true;
	}
	
	
	
}
