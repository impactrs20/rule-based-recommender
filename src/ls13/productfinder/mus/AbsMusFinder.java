package ls13.productfinder.mus;

import java.util.List;

import ls13.productfinder.ProductFinderException;
import ls13.productfinder.RecommenderEngine;

/**
 * An abstract classes that search for Minimal unsatisfiable subsets (conflicts)
 * @author jannach
 *
 */
public abstract class AbsMusFinder {
	
	RecommenderEngine recommender;
	
	/*
	 * Creates a MUS finder class.
	 */
	public AbsMusFinder(RecommenderEngine recommender) {
		super();
		this.recommender = recommender;
	}


	/**
	 * Method that returns the set of all minimal conflicts for the knowledge base defined
	 * in the jpfinder - recommender system
	 * @return a list of minimal conflicts
	 */
	public abstract List findMUS() throws ProductFinderException; 

}
