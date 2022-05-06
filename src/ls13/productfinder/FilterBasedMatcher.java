package ls13.productfinder;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import ls13.productfinder.utils.Globals;
import ls13.productfinder.utils.ScriptCompiler;
import ls13.productfinder.utils.Utils;

import org.mozilla.javascript.Script;


/**
 * <p>Title:  Filter-based matcher</p>
 * <p>Description:  Central class for computing matching products based on filters</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * 
 * @author DJ
 * @version 1.0
 */

public class FilterBasedMatcher {
	// The list of known filters
	List filters = new ArrayList();

	/**
	 * @return Returns the filters.
	 */
	public List getFilters() {
		return filters;
	}

	/**
	 * @param filters The filters to set.
	 */
	public void setFilters(List filters) {
		this.filters = filters;
	}
	
	// A handle to the recommender engine containing the model
	RecommenderEngine engine;

	/**
	 * @return Returns the engine.
	 */
	public RecommenderEngine getEngine() {
		return engine;
	}

	/**
	 * @param engine The engine to set.
	 */
	public void setEngine(RecommenderEngine engine) {
		this.engine = engine;
	}

	/**
	 * Creates a new matcher to be registered with the engine
	 * @param engine the recommender engine containing the model
	 * @throws ProductFinderException
	 */
	public FilterBasedMatcher(RecommenderEngine engine) throws ProductFinderException{
		super();
		this.engine = engine;
		// Create a private expression evaluator for initializing
		// filters and pre-compiling them
	
		ee = new ExpressionEvaluator();
	} 
	
	// Create a private evaluator for functions.
	ExpressionEvaluator ee; 
	
	/**
	 * Pre-compiles all the filter conditions and filter expressions; handles to the 
	 * executable classes are stored with the filter.
	 * @throws ProductFinderException
	 */
	public void preCompileFilters() throws ProductFinderException {
		
		FilterRule fr;
		Iterator fit = this.getFilters().iterator();
		while (fit.hasNext()) {
			fr = (FilterRule) fit.next();
			Script conditionSx = ScriptCompiler.compileString(
													this.ee.getContext(), 
													fr.getCondition(),
													fr.getName());
			fr.setCompiledCondition(conditionSx);
			Script filterSx = ScriptCompiler.compileString(
													this.ee.getContext(), 
													fr.getFilter(),
													Globals.COMPILED_CLASS_PREFIX + fr.getName());
			fr.setCompiledFilter(filterSx);
		}
	}

	/**
	 * Inistializes the matrix of matching products per filter for those filters with no variables
	 * @throws ProductFinderException
	 */
	public void initFilterResults() throws ProductFinderException {
		productsForFilters = new HashMap();
		
		List filters = engine.getFilterBasedMatcher().getFilters();
		Iterator fit = filters.iterator();
		Iterator pit = engine.getProducts().iterator();
		FilterRule fr;
		HashMap product;
		while (fit.hasNext()) {
			fr = (FilterRule) fit.next();
			try  {
				// Create an empty bit-set; will be all set to zero at the beginning
				BitSet bs = new BitSet(engine.getProducts().size());
				// Do not set any customer properties now
				// reset iterator
				pit = engine.getProducts().iterator();
				int i = 0;
				while (pit.hasNext()) {
					product = (HashMap) pit.next();
					// evaluating filters with user variables will fail 
					// Will be caught in outer loop, i.e., we will simply continue with the next filter
					if (this.productMatches(engine,ee,product,null,fr)) {
						bs.set(i);
					}
					i++;
				}
				this.productsForFilters.put(fr.getName(),bs);
			}
			catch (Exception e) {
			// Do nothing and get on with next filter
			}
		}
		//System.out.println("evalcount: " + Globals.evalCount);
	}
	
	// The bitset list
	HashMap productsForFilters = new HashMap();
	
	/**
	 * Computes a list of products for a given expression
	 * @param engine the current recommender engine
	 * @param ee the expression evaluator of the client
	 * @param values a set of user inputs to be set 
	 * @return a list of matching products
	 * @throws ProductFinderExpression
	 */
	public List getProductsForExpression(RecommenderEngine engine,
			ExpressionEvaluator ee, 
			String expression) throws ProductFinderException {
		return getProductsForExpression(engine,ee,new HashMap(),expression);
	}

		/**
		 * Computes a list of products for a given expression
		 * @param engine the current recommender engine
		 * @param ee the expression evaluator of the client
		 * @param expression the expression to be evaluated online
		 * @param values a set of user inputs to be set 
		 * @return a list of matching products
		 * @throws ProductFinderExpression
		 */
	public List getProductsForExpression(RecommenderEngine engine,
										ExpressionEvaluator ee, 
										HashMap values,
										String expression) throws ProductFinderException {
		List result = new ArrayList<HashMap>();
		// First compile the expression
		Script conditionSx = ScriptCompiler.compileString(
				this.ee.getContext(), 
				expression,
				"on-the-fly");
		if (values != null && values.size() > 0) {
			ee.setVariables(engine,values,false);
		}
		// Iterate over the products
		Iterator pit = engine.getProducts().iterator();
		HashMap product;
		int i = 0;
		while (pit.hasNext()) {
			product = (HashMap) pit.next();
			// evaluating filters with user variables will fail 
			// Will be caught in outer loop, i.e., we will simply continue with the next filter
			if (this.productMatches(engine,ee,product,null,conditionSx)) {
				result.add(product);
			}
			i++;
		}

		return result;
		
	}
	
	
	/**
	 * @return Returns the productsForFilters.
	 */
	public HashMap getProductsForFilters() {
		return productsForFilters;
	}

	/**
	 * Method that determines whether a certain product matches a filter
	 * @param product the product to be checked
	 * @param values the list of current user inputs
	 * @param filter the filter (it's filter rule) to check
	 * @return true, if the filter expression is true for this product
	 * @throws ProductFinderException
	 */
	public boolean productMatches(RecommenderEngine engine, ExpressionEvaluator ee, HashMap product, HashMap values, FilterRule filter) throws ProductFinderException {
		
		// allow variables to be null, i.e., assume that values are set beforehand
		// but allow to set them manually
		if (values != null) {
			ee.setVariables(engine,values, true);
		}
		// push the product values to the engine
		// do not overwrite previous settings of customer properties
		ee.setVariables(engine,product, false);
		
		//return ee.eval(engine,null,filter.getFilter());
		// new version: use compiled code
		Script filterScript = filter.getCompiledFilter();
		return Utils.evaluteCompiledExpression(filterScript,ee.getScope(),filter.getName());
	}
	/**
	 * Method that determines whether a certain product matches a compiled expression
	 * @param product the product to be checked
	 * @param values the list of current user inputs
	 * @param filter the filter (it's filter rule) to check
	 * @return true, if the filter expression is true for this product
	 * @throws ProductFinderException
	 */
	public boolean productMatches(RecommenderEngine engine, 
								ExpressionEvaluator ee, 
								HashMap product, 
								HashMap values, 
								Script script) throws ProductFinderException {
		
		// allow variables to be null, i.e., assume that values are set beforehand
		// but allow to set them manually
		if (values != null) {
			ee.setVariables(engine,values, true);
		}
		// push the product values to the engine
		// do not overwrite previous settings of customer properties
		ee.setVariables(engine,product, false);
		
		//return ee.eval(engine,null,filter.getFilter());
		// new version: use compiled code
		
		return Utils.evaluteCompiledExpression(script,ee.getScope(),"on-the-fly");
	}
	
	
	
}
