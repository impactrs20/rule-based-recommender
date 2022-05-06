package ls13.productfinder.mus;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import ls13.productfinder.FilterRule;
import ls13.productfinder.ProductFinderException;
import ls13.productfinder.RecommenderEngine;

/**
 * Implements the basic MUS finding procedures from de la Banda et al, 2003
 * 
 * @author jannach
 * 
 */
public class MinUnsat1MusFinder extends AbsMusFinder {

	/**
	 * Object creation
	 * 
	 * @param recommender
	 */
	public MinUnsat1MusFinder(RecommenderEngine recommender) {
		super(recommender);
	}

	/**
	 * A handle for the conflict checker
	 * 
	 */
	ConflictChecker cc = null;

	/**
	 * A list for storing the available filters
	 */
	List filters = null;

	List conflictset = new ArrayList();

	/**
	 * Finds all minimal unsat subsets based on the CS-tree and the function
	 * all_subsets form de la banda.
	 */
	public List findMUS() throws ProductFinderException {
		// retrieve the filters
		List filters = recommender.getFilterBasedMatcher().getFilters();
		// create a conflict checker object
		cc = new ConflictChecker(recommender);

		// implements the basic recursive method
		int depth = 0;
		// return min_unsat1(new ArrayList(),filters,new ArrayList(),depth);

		// ACHTUNG: TODO write min_unsat1
		Set result = min_unsat1(new HashSet(), new HashSet(filters),
				new HashSet(), depth);
		return new ArrayList(result);
	}

	/**
	 * The all_subsets method from de la Banda Params and result as specified in
	 * the paper
	 * 
	 * @param D
	 * @param P
	 * @param A
	 * @param depth
	 * @return
	 * @throws ProductFinderException
	 */
	private Set all_subsets(Set D, Set P, Set A, int depth)
			throws ProductFinderException {
		HashSet DUP = new HashSet();
		DUP.addAll(D);
		DUP.addAll(P);
		A.add(DUP);
		FilterRule p = null;

		while (P.size() != 0) {
			// retrieve the first element and remove it from the list
			// do not use iterators.
			p = (FilterRule) (P.toArray()[0]);
			P.remove(p);
			A = all_subsets(new HashSet(D), new HashSet(P), A, depth + 1);
			D.add(p);
		}
		return A;
	}

	/**
	 * recursive implementation of the min_unsat1 method  from de la Banda et al*
	 * 
	 * @param D
	 * @param P
	 * @param A
	 * @return A, a list of all minimal unsatisfiable subsets
	 */
	private Set min_unsat1(Set D, Set P, Set A, int depth)
			throws ProductFinderException {

		HashSet DUP = new HashSet();
		DUP.addAll(D);
		DUP.addAll(P);
		FilterRule p = null;

		if (!cc.isConflict(new ArrayList(DUP))) {
			return A;
		} 

		while (P.size() != 0) {
			p = (FilterRule) (P.toArray()[0]);
			P.remove(p);
			A = min_unsat1(new HashSet(D), new HashSet(P), A, depth + 1);
			D.add(p);
		}

		// Check subset relations.
		// If there is element in A of which D is a superset, we add it.
		boolean foundsubset = false;
		Iterator it = A.iterator();
		while (it.hasNext()) {
			Set a = (Set) it.next();
			if (D.size() > a.size() && D.containsAll(a)) {
				foundsubset = true;
				break;
			}
		}
		if (!foundsubset) {
			A.add(D);
		}

		return A;

	}
}
