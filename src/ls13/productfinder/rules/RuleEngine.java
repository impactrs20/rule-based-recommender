package ls13.productfinder.rules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import ls13.productfinder.ExpressionEvaluator;
import ls13.productfinder.ProductFinderException;
import ls13.productfinder.RecommenderEngine;
import ls13.productfinder.utils.ScriptCompiler;
import ls13.productfinder.utils.Utils;

import org.mozilla.javascript.Script;


/**
 * A very simple rule engine
 * @author dietmar
 *
 */
public class RuleEngine {
	
	int maxIterations = 5;
	
	
	
	/** The list of business rules */
	List businessRules;

	/**
	 * @return the businessRules
	 */
	public List getBusinessRules() {
		return businessRules;
	}
	/**
	 * @param businessRules the businessRules to set
	 */
	public void setBusinessRules(List businessRules) {
		this.businessRules = businessRules;
	}

	/** A handle to the global engine */
	RecommenderEngine engine;

	/** Constructs a new rule engine with a handle to the global engine */
	public RuleEngine(RecommenderEngine engine) {
		this.engine = engine;
		this.ee = new ExpressionEvaluator();
	}
	
	/** A local Expression evaluator */
	ExpressionEvaluator ee; 

	/**
	 * Pre-compiles the business rules
	 * @throws ProductFinderException
	 */
	public void compileBusinessRules() throws ProductFinderException {
		if (this.businessRules == null ) {
			return;
		}
		Iterator it = this.businessRules.iterator();
		BusinessRule br = null;
		while (it.hasNext()) {
			br = (BusinessRule) it.next();
			Script conditionSx = ScriptCompiler.compileString(
						this.ee.getContext(), 
						br.getCode_if(),
						br.getName());
			br.setCompiledIf(conditionSx);
			conditionSx = ScriptCompiler.compileString(
					this.ee.getContext(), 
					br.getCode_then(),
					br.getName());
			br.setCompiledThen(conditionSx);			
		}
	}
	
	
	/**
	 * Applies the defined business rules
	 * @param session the current set of variables
	 */
	public void applyRules(HashMap values, ExpressionEvaluator ee) throws ProductFinderException{
		// Return, if there is nothing to do
		if (this.businessRules == null || this.businessRules.size() == 0) {
			return;
		}
		
		// A local list of all previously found states
		List previousStates = new ArrayList();
		
		BusinessRule br = null;
		HashMap origValues = new HashMap(values);
		HashMap lastValues = new HashMap(values);
		HashMap newValues = new HashMap(values);
		// Set the variables once
		if (values != null) {
			ee.setVariables(engine,origValues, true);
		}
		// remember the original state in the list
		previousStates.add(origValues);
		
		boolean valuesChanged = true;
		int counter = 0;
		while (valuesChanged) {
			counter++;
			// Break operation if too many iterations
			if (counter > this.maxIterations) {
				throw new ProductFinderException("Rule engine problem: Maximum number of iterations " 
													+ this.maxIterations + " reached.");
			}
			// Iterate over the business rules and check if there is something to do
			Iterator it = this.businessRules.iterator();
			while (it.hasNext()) {
				br = (BusinessRule) it.next();
				//System.out.println("Checking: " + br.getCode_if());
				boolean result = Utils.evaluteCompiledExpression(br.getCompiledIf(),ee.getScope(),br.getName());
				if (result) {
					//System.out.println("Expression applies");
					Utils.evaluateFunction(br.getCompiledThen(),ee.getScope());
					//System.out.println("got value: " + ee.getScope().get("c_pref_price_derived",ee.getScope()));
					// Read back the variables
					Iterator n_it = this.engine.getVariableNames().iterator();
					String varname = null;
					while (n_it.hasNext()) {
						varname = (String) n_it.next();
						Object val = ee.getScope().get(varname, ee.getScope());
						//System.out.println("getting variable: " + varname + " value: " + val);
						if (!(val instanceof org.mozilla.javascript.UniqueTag)) {
							if (val != null) {
								newValues.put(varname, val);
							}
						}
					}
				}
			}
			//System.out.println("lastvalues: " + lastValues);
			//System.out.println("newvalues: " + newValues);
			// Store the new values
			previousStates.add(new HashMap(newValues));
			// Compare the new values with the last values
			if (Utils.identical(newValues,lastValues)) {
				valuesChanged = false;
			}
			else {
				// Find out whether there are loops in the state
				// Do not compare the first two ones
				if (previousStates.size() > 2) {
					// Compare with all but the last state
					HashMap oldMap = null;
					for (int i=0;i<previousStates.size()-1;i++) {
						oldMap = (HashMap) previousStates.get(i);
						// We already had that situation before
						if (Utils.identical(oldMap,newValues)) {
							throw new ProductFinderException("Loop in value state in rule engine - check your business rules (Iteration: " + counter);
						}
					}
				}
				ee.setVariables(engine,newValues, false);
				lastValues = new HashMap(newValues);
				newValues = new HashMap();
			}
		}
		// Now we are done. Return the last new values
		values.putAll(newValues);
	}
	
	
	/**
	 * @return the maxIterations
	 */
	public int getMaxIterations() {
		return maxIterations;
	}
	/**
	 * @param maxIterations the maxIterations to set
	 */
	public void setMaxIterations(int maxIterations) {
		this.maxIterations = maxIterations;
	}
	
}
