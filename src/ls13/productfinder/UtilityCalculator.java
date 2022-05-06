package ls13.productfinder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import ls13.productfinder.utils.ScriptCompiler;
import ls13.productfinder.utils.Utils;

import org.mozilla.javascript.Script;


/**
 * A class to calculate utility values for a set of products
 * @author dietmar
 *
 */

public class UtilityCalculator {
	public static final String UTILITY_DENOMINATOR = "__UTILITY__";
	
	
	// A local expression evaluator
	ExpressionEvaluator ee;

	public ExpressionEvaluator getEe() {
		return ee;
	}
	
	boolean dynamicCalculation = false;
	
	/**
	 * A handle to the engine.
	 */
	RecommenderEngine engine;
	
	public UtilityCalculator(RecommenderEngine engine) {
		this.engine = engine;
		ee = new ExpressionEvaluator();
	}
	
	public void setDynamicUtilities(boolean v) {
		this.dynamicCalculation = v;
	}
	public boolean dynamicCalculaction() {return this.dynamicCalculation;}

	/** 
	 * A handle to the compiled utility funciton
	 */
	Script compiledUtilityFunction;
	
	public void setCompiledUtilityFunction(Script compiledUtilityFunction) {
		this.compiledUtilityFunction = compiledUtilityFunction;
	}
	

	String utiltityFunctionCode;
	String utilityFunctionName;
	
	
	/**
	 * Compile the function __utility()
	 * @param utiltityFunction
	 * @throws ProductFinderException
	 */
	public void setUtilityFunction(String utiltityFunction) throws ProductFinderException{
		this.utiltityFunctionCode = utiltityFunction;
		if (this.utiltityFunctionCode != null) {
			// Get the function name
			utilityFunctionName = null;
			this.utiltityFunctionCode = this.utiltityFunctionCode.trim();
			try {
				String temp = this.utiltityFunctionCode.substring("function".length(),this.utiltityFunctionCode.length());
				utilityFunctionName= temp.substring(0,temp.indexOf('(')); 
				// register the funciton
				getEe().registerFunction(this.utiltityFunctionCode);
				// Precompile the call
				Script conditionSx = ScriptCompiler.compileString(
						getEe().getContext(), 
						utilityFunctionName+ "()", 
						utilityFunctionName
						);
				setCompiledUtilityFunction(conditionSx);
			}
			catch (Exception e) {
				throw new ProductFinderException("Error in utility function:\n" + utiltityFunction);
			}
		}
	}



	/**
	 * This method accepts a list of products (hashmaps) and computes
	 * the utility value by using the defined utiltity function. 
	 * The actual value is added as a special property (double) of the
	 * property hashmap 
	 * @param products the list of products to be evaluated
	 * @return the augmented product list
	 */
	public List computeStaticUtilities(List products) throws ProductFinderException {
		Iterator it = products.iterator();
		HashMap product = null;
		Object utility;
		while (it.hasNext()) {
			product = (HashMap) it.next();
			// push the product values to the engine
			// do not overwrite previous settings of customer properties
			ee.setVariables(engine,product, false);
			utility = Utils.evaluateFunction(this.compiledUtilityFunction,ee.getScope());
			if (!(utility instanceof Double) && !(utility instanceof Integer)) {
				throw new ProductFinderException("Utility function returns value of type " + utility.getClass());
			}
			if (utility != null) {
				if (utility instanceof Integer) {
					utility = new Double(((Integer)utility).doubleValue());
				}
				product.put(UTILITY_DENOMINATOR, utility );
			}
		}
		return products;
	}

	/**
	 * Computes dynamic utilites
	 * @param products the set of products
	 * @return a copy of the products augmented with personal utilities 
	 * @throws ProductFinderException
	 */
	public List computeDynamicUtilities(List products, RecommenderSession session) throws ProductFinderException {
		// Initialize the variables
		ee.setVariables(engine,session.getUserInputs(), true);

		List copiedList = new ArrayList();
		HashMap copiedProduct = null;
		Iterator it = products.iterator();
		HashMap product = null;
		Object utility;
		while (it.hasNext()) {
			product = (HashMap) it.next();
			// push the product values to the engine
			// do not overwrite previous settings of customer properties
			ee.setVariables(engine,product, false);
			utility = Utils.evaluateFunction(this.compiledUtilityFunction,ee.getScope());
			if (!(utility instanceof Double) && !(utility instanceof Integer)) {
				throw new ProductFinderException("Utility function returns value of type " + utility.getClass());
			}
			if (utility != null) {
				if (utility instanceof Integer) {
					utility = new Double(((Integer)utility).doubleValue());
				}
				copiedProduct = (HashMap) product.clone();
				copiedProduct.put(UTILITY_DENOMINATOR, utility );
				copiedList.add(copiedProduct);
			}
		}
		return copiedList;
	}

	public Script getCompiledUtilityFunction() {
		return compiledUtilityFunction;
	}

	public String getUtiltityFunctionCode() {
		return utiltityFunctionCode;
	}	
	
	
	

}
