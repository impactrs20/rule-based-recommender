package ls13.productfinder.mus;


import java.util.BitSet;
import java.util.HashMap;
import java.util.List;

import ls13.productfinder.ProductFinderException;
import ls13.productfinder.RecommendationResult;
import ls13.productfinder.RecommenderEngine;
import ls13.productfinder.RecommenderSession;

public class ConflictChecker {

	
	/* pointer to the knowledge base */
	RecommenderEngine recommender;
	
	
	/** stores a session object for calculations */
	RecommenderSession session;
	
	/**
	 * Get a handle to the session object
	 * @return the internal session object
	 */
	public RecommenderSession getSession() {
		return session;
	}

	/**
	 * Creates a new instance and stores the handle to the recommender engine
	 * and knowldge base; creates a dummy session object.
	 * @param recommender
	 */
	public ConflictChecker (RecommenderEngine recommender) {
		this.recommender = recommender;
		this.session = new RecommenderSession(recommender);
	}
	
	/**
	 * Checks whether a given set of filter rule consequents will lead to an empty
	 * result set, i.e., if they are in conclift.
	 * @param filters the list of filter rules
	 * @return true, if empty result set is result of filter application
	 */
	public boolean isConflict(List filters) throws ProductFinderException {
			HashMap bitsetsForFilters = session.computeBitsetsForFilters(filters);
			BitSet matching = session.combineBitsets(bitsetsForFilters.values());
			if (matching.cardinality() == 0) {
				return true;
			}
			else {
/*				List products = session.getProducts(matching);
				System.out.println("Matching: " + products);
				RecommendationResult r = new RecommendationResult(filters, new ArrayList(),products);
				System.out.println(r);
*/

				return false;
			}
	}
	
}
