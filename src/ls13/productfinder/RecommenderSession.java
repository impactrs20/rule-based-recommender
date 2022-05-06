package ls13.productfinder;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import ls13.productfinder.UtilityCalculator;
import ls13.productfinder.utils.Utils;

import org.mozilla.javascript.Script;


/**
 * <p>Title:  Recommender session object</p>
 * <p>Description:  Objects of this class can be associated with user sessions.</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * @author DJ
 * @version 1.0
 */

public class RecommenderSession {
	// A handle to the recommender engine
	RecommenderEngine engine = null;
	// A private scripting engine for this session
	ExpressionEvaluator ee = new ExpressionEvaluator();
	// The list of current user inputs
	HashMap userInputs = new HashMap();
	// A handle to the last result
	RecommendationResult lastResult = new RecommendationResult();

	/**
	 * @return Returns the Scripting Engine.
	 */
	public ExpressionEvaluator getExpressionEvaluator() {
		return ee;
	}

	/**
	 * @param lastResult The lastResult to set.
	 */
	public void setLastResult(RecommendationResult lastResult) {
		this.lastResult = lastResult;
	}

	/**
	 * @return Returns the lastResult.
	 */
	public RecommendationResult getLastResult() {
		return lastResult;
	}

	/**
	 * @return Returns the userInputs.
	 */
	public HashMap getUserInputs() {
		return userInputs;
	}

	/**
	 * @param userInputs The userInputs to set.
	 */
	public void setUserInputs(HashMap userInputs) {
		this.userInputs = userInputs;
	}

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
	 * Creates a session object for a given engine
	 * @param engine the initialized engine
	 */
	public RecommenderSession(RecommenderEngine engine) {
		super();
		this.engine = engine;
		ee = new ExpressionEvaluator();
	}
	
	/**
	 * A method that computes the set of matching products for the current user
	 * requirements and stores it internally
	 * @return a handle to the last recommendation result
	 */
	public RecommendationResult computeRecommendationResult() throws ProductFinderException {
		return computeRecommendationResult(-1);
	}
	/**
	 * A method that computes the set of matching products for the current user
	 * requirements and stores it internally
	 * @param number of products for relaxation
	 * @return a handle to the last recommendation result
	 */
	public RecommendationResult computeRecommendationResult(int relax) throws ProductFinderException {
		this.lastResult = computeResult(relax);
		
		// Do some sorting if this is possible
		UtilityCalculator uc = this.engine.getUtilityCalculator(); 
		if (uc.getUtiltityFunctionCode() != null) {
			// Only do this if dynamic calculation required
			if (uc.dynamicCalculaction()) {
				List personalList = uc.computeDynamicUtilities(
										this.lastResult.getMatchingProducts(), 
										this);
				this.lastResult.setMatchingProducts(personalList);
			}
		}
		return this.lastResult;
	}
	
	/**
	 * Internal method for computing the recommendation results
	 * @return the recommendation result
	 * @throws ProductFinderException
	 */
	public RecommendationResult computeResult(int relax) throws ProductFinderException {
		this.getExpressionEvaluator().setVariables(this.getEngine(),this.userInputs,true);
		List applicableFilters = computeApplicableFilters();
		//System.out.println("applicabile filters:" + applicableFilters);
		// If there are no applicable filters, return a copy of handles to products
		if (applicableFilters.size() == 0) {
			return new RecommendationResult(new ArrayList(), 
											new ArrayList(), 
											new ArrayList(this.engine.getProducts()));
		}
		//System.out.println("User Inputs: " + userInputs);
		//System.out.println("Applicable filters: \n" + applicableFilters);
		HashMap bitsetsForFilters = computeBitsetsForFilters(applicableFilters);
		RecommendationResult r;
		BitSet matching = combineBitsets(bitsetsForFilters.values());
		if (relax <= 0) {
			List products = getProducts(matching);
			//System.out.println("Matching: " + matching);
			List relaxedFilters = new ArrayList();
			r = new RecommendationResult(applicableFilters, relaxedFilters,products);
			return r;
		}
		else {
			// relaxation has to be done
			return  computeRelaxedResult(applicableFilters, bitsetsForFilters, relax);
		}
	}
	
	/**
	 * Computes a relaxed relaxation result based on relaxation costs
	 * @param the set of applicable filters
	 * @param bitsetsfor filters
	 * @param relax the number of products to be retrieved (currently
	 * only one prouct is garantueed
	 * @return a recommendation result instance containing the relevant 
	 * information
	 */
	RecommendationResult computeRelaxedResult(List applicableFilters, 
											  HashMap bitsetsForFilters,
											  int relax) throws ProductFinderException {
		RecommendationResult result = new RecommendationResult();
		// Prepare a list of forced filters
		List forcedFilters = new ArrayList();
		Iterator afit = applicableFilters.iterator();
		FilterRule fr;
		while (afit.hasNext()) {
			fr = (FilterRule) afit.next();
			if (fr.getPriority() <= 0) {
				forcedFilters.add(fr);
			}
		}
		// Check, if there remain products, if the forced filters are
		// applied.
		HashMap bitsetsForForcedFilters = computeBitsetsForFilters(forcedFilters);
		BitSet matchingForced = combineBitsets(bitsetsForForcedFilters.values());
		if (matchingForced.cardinality() == 0) {
			// Nothing remains after application of forced filters
			// no relaxation possible -> return empty recommendation result
			return new RecommendationResult(
						applicableFilters, 
						new ArrayList(),
						new ArrayList(this.engine.getProducts())
					);
		}
		else {
			// Find one optimal relaxation in the rest
			// Iterate over all products and determine the costs of relaxing it
			// Remember best one and subsequently get the set of products
			int minPrioritySum = 0;
			List minPriorityFilters = null;
			
			// For storing the filters for one product
			List currentFilters;
			int sumPrios;

			int nbProducts = this.engine.getProducts().size();
			// For all Products
			for (int i=0;i<nbProducts;i++) {
				currentFilters = new ArrayList();
				sumPrios = 0;
				// check if the corresponding bit is set
				for (int j=0;j<applicableFilters.size();j++) {
					// get the filter
					fr = (FilterRule) applicableFilters.get(j);
					// Do not do this for forced filters.
					if (fr.getPriority() > 0) {
						//if the bit is zero, add it to the 
						BitSet bs = (BitSet) bitsetsForFilters.get(fr.getName());
						if (bs.get(i) == false) {
							// add it
							currentFilters.add(fr);
							sumPrios += fr.getPriority();
						}
					}
				}
				// Store the information
				if (minPriorityFilters == null) {
					minPriorityFilters = new ArrayList(currentFilters);
					minPrioritySum = sumPrios;
				}
				else { // found a new best..
					if (sumPrios < minPrioritySum) {
						minPriorityFilters = new ArrayList(currentFilters);
						minPrioritySum = sumPrios;
					}
				}
			}
			// Ok, now we should have the optimal relaxation - get the 
			// corresponding products, by computing the set of products
			// mathcing the remaining filters
			List remainingFilters = new ArrayList(applicableFilters);
			remainingFilters.removeAll(minPriorityFilters);
			
			// Nothing more to do
			if (remainingFilters.size() == 0) {
				return new RecommendationResult(
							new ArrayList(),
							minPriorityFilters,
							engine.getProducts()
						);
			}
			// Else compute the matching products
			HashMap bss = computeBitsetsForFilters(remainingFilters);
			BitSet matching = combineBitsets(bss.values());
			
			// the products
			result.setAppliedFilters(remainingFilters);
			result.setRelaxedFilters(minPriorityFilters);
			List products = getProducts(matching);
			result.setMatchingProducts(products);
		}
		return result;
	}
	
	
	/**
	 * Method mapping bitset flags to products
	 * @param bitset the result bit set
	 * @return the list of products corresponding to the bitset
	 */
	public List getProducts(BitSet bitset){
		List result = new ArrayList();
		for (int i=0;i<bitset.size();i++) {
			if (bitset.get(i) == true) {
				result.add(engine.getProducts().get(i));
			}
		}
		return result;
		
	}
	
	/**
	 * A method that computes the conjunction of bitsets, starting with an bitsets
	 * containing all ones
	 * @param bitsets the bitsets to be joined
	 * @return the joined list
	 */
	public BitSet combineBitsets(Collection bitsets) {
		BitSet result = new BitSet(engine.getProducts().size());
		// Initialize result with all zeros.
		result.flip(0,result.size());
		Iterator bit = bitsets.iterator();
		BitSet bs = null;
		while (bit.hasNext()){
			bs = (BitSet) bit.next();
			result.and(bs);
		}
		return result;
	}

	/**
	 * Computes a dynamic list of bitsets for the currently active filters, 
	 * and also determines bitsets for variable filters;
	 * made public to be accessed for external consistency checker.
	 * @param applicableFilters the list of currently active filters
	 * @return a hashmap mapping filternames to bitsets
	 * @throws ProductFinderException
	 */
	public HashMap computeBitsetsForFilters(List applicableFilters) throws ProductFinderException {
		//System.out.println("applicable filters: " + applicableFilters);
		//this.ee.setVariables(this.engine,userInputs,false);
		// get the bitsets for these filters
		HashMap bitsets = new HashMap();
		Iterator fit = applicableFilters.iterator();
		FilterRule fr = null;
		BitSet bitset;
		while (fit.hasNext()) {
			fr = (FilterRule) fit.next();
			// Get an existing bitset
			bitset = (BitSet) engine.getFilterBasedMatcher().getProductsForFilters().get(fr.getName());
			if (bitset != null) {
				bitsets.put(fr.getName(),bitset);
			}
			else {
				// We have to compute a special bitset for the current requirements
				Iterator pit = engine.getProducts().iterator();
				// Create an empty bit-set; will be all set to zero at the beginning
				BitSet bs = new BitSet(engine.getProducts().size());
				// Do not set any customer properties now
				int i = 0;
				HashMap product;
				while (pit.hasNext()) {
					product = (HashMap) pit.next();
					//System.out.println(".. checking product: " + product);
					// evaluating filters with user variables will fail at the moment
					// do not worry.
					if (engine.getFilterBasedMatcher().productMatches(engine,ee,product,userInputs,fr)) {
						//System.out.println("found matching product " + product);
						bs.set(i);
					}
					i++;
				}
				bitsets.put(fr.getName(),bs);
			}
		}
		return bitsets;
	}
	
	/**
	 * Returns a user-readable represantion of the current user inputs
	 */
	public String toString() {
		String result = "USER INPUTS: \n ----------------------\n";
		Iterator it = this.userInputs.keySet().iterator();
		while (it.hasNext()) {
			Object o = it.next();
			Object v = this.userInputs.get(o);
			
			if (v instanceof String[]) {
				result += o + ":";
				String[] vals = (String[])v;
				for (int i=0;i<vals.length;i++) {
					result += vals[i] + " ";
				}
				result += "\n"; 
			}
			if (v instanceof String) {
				result += o + ":" + v + "\n"; 
			}
		}
		return result;
	}
	
	/**
	 * Returns the list of products matching a certain expression
	 * @param expression the expression to be evaluated
	 * @return the list of matching products
	 * @throws ProductFinderException
	 */
	public List getProductsForExpression(String expression) throws ProductFinderException {
		return this.engine.getFilterBasedMatcher().getProductsForExpression(this.engine,this.ee,expression);
	}
	
	/**
	 * Computes a list of applicable filters for the current user inputs
	 * @return a list of handles to filter rules
	 */
	private List computeApplicableFilters() throws ProductFinderException{
		List result = new ArrayList();
		FilterRule fr = null;
		Iterator fit = engine.getFilterBasedMatcher().getFilters().iterator();
		while (fit.hasNext()) {
			fr = (FilterRule) fit.next();
			// use compiled script
			Script compiledScript = fr.getCompiledCondition();
			
			if (Utils.evaluteCompiledExpression(compiledScript,this.ee.getScope(),fr.getCondition())) {
				result.add(fr);
			}
		}
		return result;
	}
	
	/**
	 * Computes a personalized infotext variant
	 * @param name the name of the text
	 * @return the personalized variant or "" if no variant applies
	 * @throws ProductFinderException if text is not defined in model
	 */
	public String getInfoText(String name) throws ProductFinderException {
		return this.engine.getInfoTetxtManager().getText(name,this);
	}
	
	/**
	 * Sorts the elements in the current result according to a defined property. As a
	 * default, sort order is ascending 
	 * @param property the sort criteron
	 * @throws ProductFinderException
	 */
	public void sortResultBy(String property) throws ProductFinderException {
		sortResultBy(property, "asc");
	}

	/**
	 * Sorts the elements in the current result according to a defined single-valued property. As a
	 * default, sort order is ascending 
	 * @param property the sort criteron
	 * @param direction "asc|desc" 
	 * @throws ProductFinderException if property not known, or property is multivalued
	 */
	public void sortResultBy(String property, String direction) throws ProductFinderException {
		if (direction == null) { direction = "asc";}
		direction = direction.toLowerCase();
		if (!direction.equals("asc")&& (!direction.equals("desc"))) {
			throw new ProductFinderException("[ERROR] Sorting direction " + direction + " is not known, using \"asc\" as direction.");
		}
		// do nothing, if no result exists
		if (this.lastResult == null || (this.lastResult.getMatchingProducts().size() == 0)) {return;}
		// If this is a multivalued property or is not known, throw an exception
		HashMap vars = this.getEngine().getVariables();
		String v = null;
		v = (String) vars.get(property);
		if (vars.get(property) == null || 
				((String) vars.get(property)).endsWith("[]")
			){ 
				throw new ProductFinderException("[FATAL] Variable " + property + " for sorting is not known or multi-valued.");
			}
		List products = this.lastResult.getMatchingProducts();
		boolean numeric = (v.equalsIgnoreCase("int") || v.equalsIgnoreCase("double"));
		sortInternal(products, property, direction, numeric);
	}
	
	/**
	 * Internal sorter method accepting list of products, sort property and direction
	 *
	 */
	private void sortInternal(List products, String property, String direction, boolean numeric) {
		boolean ascending = direction.equalsIgnoreCase("asc");
		ProductComparator pc = new ProductComparator(property,ascending,numeric);
		Collections.sort(products, pc);
	}
	
	// ---------------------------------------------------------------------
	// Inner sorter classes
	class ProductComparator implements Comparator {
		String property;
		boolean ascending;
		boolean numeric;
		
		public ProductComparator(String property, boolean ascending, boolean numeric) {
			this.property = property;
			this.ascending = ascending;
			this.numeric = numeric;
		}

		public int compare(Object o1, Object o2) {
			HashMap p1 = (HashMap) o1;
			HashMap p2 = (HashMap) o2;
			
			Object v1 = p1.get(property);
			Object v2 = p2.get(property);
			
			if (v1 == null && v2 == null) {return 0;}
			if (v1 == null && v2 != null) {return 1;}
			if (v1 != null && v1 == null) {return -1;}
			
			// return different values, depending on the type
			if (this.numeric) {
				double d1 = ((Double) v1).doubleValue();
				double d2 = ((Double) v2).doubleValue();
				if (ascending) {
					return Double.compare(d1,d2);
				}
				else {
					return Double.compare(d2,d1);
				}
			}
			else {
				if (ascending) {
					return ((String)v1).compareTo((String)v2); 
				}
				else {
					return ((String)v2).compareTo((String)v1);
				}
			}
		}
	} 

	// ---------------------------------------------------------------------
	
	/**
	 * A method that applies the defined business rules
	 */
	public void applyRules() throws ProductFinderException {
		HashMap values = this.userInputs;
		this.engine.ruleEngine.applyRules(values, this.ee);
	}
	
	// ---------------------------------------------------------------------

	/**
	 * Method to conveniently set String inputs
	 * @param varname the variable to be set
	 * @param value the value to store 
	 */
	public void setInput(String varname, String value) throws ProductFinderException {
		this.getUserInputs().put(varname,value);
	}
	/**
	 * Method to conveniently set String inputs
	 * @param varname the variable to be set
	 * @param value the value to store 
	 */
	public void setInput(String varname, String[] value) throws ProductFinderException {
		this.getUserInputs().put(varname,value);
	}


	
	
}
