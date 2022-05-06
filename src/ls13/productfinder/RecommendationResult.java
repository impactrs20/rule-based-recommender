package ls13.productfinder;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Title:  Recommendation result</p>
 * <p>Description:  Class holding the results of the last matching task</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * @author DJ
 * @version 1.0
 */

public class RecommendationResult {
	// The list of applied filters
	List appliedFilters;
	// The list of relaxed filters
	List relaxedFilters;
	// The list of matching products
	List matchingProducts;
	
	/**
	 * Creates an empty recommendation result
	 *
	 */
	public RecommendationResult() {
		appliedFilters = new ArrayList();
		relaxedFilters = new ArrayList();
		matchingProducts = new ArrayList();
	}
	
	/**
	 * Creates a recommendation result record
	 * @param appliedFilters list of applied filters
	 * @param relaxedFilters list of relaxed filters
	 * @param matchingProducts list of matching products
	 */
	public RecommendationResult(List appliedFilters, List relaxedFilters, List matchingProducts) {
		this.appliedFilters = appliedFilters;
		this.relaxedFilters = relaxedFilters;
		this.matchingProducts = matchingProducts;
	}


	/**
	 * @return Returns the appliedFilters.
	 */
	public List getAppliedFilters() {
		return appliedFilters;
	}
	/**
	 * @param appliedFilters The appliedFilters to set.
	 */
	public void setAppliedFilters(List appliedFilters) {
		this.appliedFilters = appliedFilters;
	}
	/**
	 * @return Returns the matchingProducts.
	 */
	public List getMatchingProducts() {
		return matchingProducts;
	}
	/**
	 * @param matchingProducts The matchingProducts to set.
	 */
	public void setMatchingProducts(List matchingProducts) {
		this.matchingProducts = matchingProducts;
	}
	/**
	 * @return Returns the relaxedFilters.
	 */
	public List getRelaxedFilters() {
		return relaxedFilters;
	}
	/**
	 * @param relaxedFilters The relaxedFilters to set.
	 */
	public void setRelaxedFilters(List relaxedFilters) {
		this.relaxedFilters = relaxedFilters;
	}
	
	/**
	 * Returns a readable representation of a recommendation result
	 */
	public String toString() {
		String s = 
		 	"----------------------------------\n" + 
			"Recommendation result: \n" + 
		 	"----------------------------------\n" + 
		 	"Products : " + this.matchingProducts.size() + "\n" + 
		 	"Applied: " + this.appliedFilters + "\n" + 
		 	"Relaxed: " + this.relaxedFilters + "\n";
		return s;
	}
	
}
