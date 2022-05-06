package ls13.productfinder;

import java.util.HashMap;
import java.util.Iterator;

import ls13.productfinder.UtilityCalculator;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;


/**
 * <p>Title: Expression evaluator  </p>
 * <p>Description: Class for evaluating JavaScript expressions - hides details of API and 
 * holds reference to properly initialized scripting engine </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * 
 * @author DJ
 * @version 1.0
 */

public class ExpressionEvaluator {
	/**
	 * First un-optimized version
	 * @param vars list of variables to register to the engine
	 * @param expression javascript expression to evaluate
	 * @return the value of the expression
	 */
	public boolean eval(RecommenderEngine engine, HashMap values, String expression) throws ProductFinderException {
		// set values
		if (values != null) {
			setVariables(engine, values, true);
		}
		// Now evaluate the expression.
		Object result = cx.evaluateString(scope,expression,"",0,null);
		if (result instanceof Boolean) {
			return ((Boolean)result).booleanValue();
		}
		else {
			throw new ProductFinderException("[FATAL] Expression " + expression + " does not return boolean value.");
		}
	};
	
	/**
	 * Initializes all variables (i.e. registers them) to the evaluator engine
	 * @param engine the engine for which the variables are defined
	 */
	public void initializeVariables(RecommenderEngine engine)  {
		// set all known variables to null in the engine
		HashMap modelVars = engine.getVariables();
		Iterator it = modelVars.keySet().iterator();
		while (it.hasNext()) {
			String v = (String) it.next();
			this.scope.put(v, scope,null);
		}
		
	}
	
	/**
	 * Sets the variables contained in values in the scope of the expr. evaluator.
	 * Converts string inputs from user session into appropriate data type (string or double)
	 * @param engine the engine for which the variables are defined
	 * @param values the values to be set
	 * @param initAll flag if true re-initializes all previous inputs
	 * @throws ProductFinderException in case of unknown variables or vars with unknown type
	 */
	public void setVariables(RecommenderEngine engine, HashMap values, boolean initAll) throws ProductFinderException {
		// set all known variables to null in the engine
		if (initAll) {
			initializeVariables(engine);
		}
		// The variable name
		String varname;
		// value be strings or string arrays.
		Object value;
		// The type?
		String type;
		Iterator it = values.keySet().iterator();
		while (it.hasNext()) {
			varname = (String) it.next();
			// Skip the internal one
			if (varname.equals(UtilityCalculator.UTILITY_DENOMINATOR)) {
				continue;
			}
			value = values.get(varname);
			type = (String)engine.getVariables().get(varname);
			//System.out.println("setting: " + varname + " to " + value + " type: " + type);
			if (type == null) {throw new ProductFinderException("[FATAL] Variable " + varname + " not registered in model.");}
			// Single values
			
			if (value instanceof String) {
				//System.out.println("last ..value: " + value);
				if (type.equals("String")) { // customer props always come as strings..
					scope.put(varname,scope,value);
				}
				else {
					try {
						if (((String)value).length() !=0) {
							// try to cast the value
							double d = Double.parseDouble((String)value);
							//System.out.println("setting: " + varname + " " + d);
							scope.put(varname,scope,new Double(d));
						}
					}
					catch(NumberFormatException nfe) {
						throw new ProductFinderException("[FATAL] Value of variable " + varname + " is not a number type");
					}
				}
			}
			// If there comes a double (e.g., from a product spec)
			else if (value instanceof Double) {
				// just put it.
				//System.out.println("got a double: " + value);
				scope.put(varname, scope, value);
			}
			else if (value instanceof String[] || value instanceof Double[]) {
				if (type.equals("String[]")) {
					String[] _v = (String[])value;
					scope.put(varname,scope,_v);
				}
				else {
					// if double anyway..
					if (value instanceof Double[]) {
						Double[] _d = (Double[]) value;
						scope.put(varname,scope,_d);
					}
					else {
						// Convert them to doubles
						String[] vs = (String[])value;
						double[] d = new double[vs.length];
						for (int i=0;i<vs.length;i++) {
							try {
								d[i] = Double.parseDouble(vs[i]);
							}
							catch (NumberFormatException nfe) {
								throw new ProductFinderException("[FATAL] Value " + vs[i] + " for variable " + varname + " is not a number type");
							}
						}
						scope.put(varname,scope,d);
					}
				}
			}
			else if (value == null){
					scope.put(varname,scope,value);
			}
			
			else {
				throw new ProductFinderException("[FATAL] User inputs can only be Strings or arrays of Strings");
			}
		}
		
	}
	
	/**
	 * Initialized the Expression evaluator, opens a new context and registers default functions.
	 * Each user session gets its own evaluator instance to allow for concurrency
	 *
	 */
	public ExpressionEvaluator() {
			cx = Context.enter();
            scope = cx.initStandardObjects();
            Object jsOut = Context.javaToJS(System.out, scope);
            ScriptableObject.putProperty(scope, "out", jsOut);

            String containmentFunction = "function isContainedIn(s,coll) {" +
            //" out.println(\"s:\" + s);" +
        	" if (coll == null) {return false;}" + 
        	" for (var i=0;i<coll.length;i++) {" +
        		//" out.println(\"\" + coll[i]);" + 
        		" if (s != null && (s == coll[i] || s.equalsIgnoreCase(coll[i]))) {return true;}" + 
        	  "}" + 
        	  "return false;" + 
            "}";
        registerFunction(containmentFunction);

	}
	
	/**
	 * Leaves the context when the instance is destroyed
	 */
	public void finalize() {
		Context.exit();

	}
	
	/**
	 * Helper function to add arbitrary code to the engine, e.g., for defining application-specific
	 * Java-Script functions
	 * @param code the code to be entered in the expr evaluators scope
	 */
	public void registerFunction(String code) {
        cx.evaluateString(scope, code, "", 1, null);
	}
	
	
	// the context for the scripting engine
	Context cx;
	// the scope of the scripting engine
	Scriptable scope;
	/**
	 * @return Returns the script engine context.
	 */
	public Context getContext() {
		return cx;
	}

	/**
	 * @return Returns the scope.
	 */
	public Scriptable getScope() {
		return scope;
	}
	
	

}
