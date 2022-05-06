package ls13.productfinder.tests;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import ls13.productfinder.FilterRule;
import ls13.productfinder.RecommenderEngine;
import ls13.productfinder.mus.AbsMusFinder;
import ls13.productfinder.mus.MinUnsat1MusFinder;

import org.junit.Test;

public class AllSubsetsMusFinderTest {

	@Test
	public void run() {
		System.out.println("Testing all_subsets method");

		try {
			RecommenderEngine engine = new RecommenderEngine();
			engine.setProductFile("WebContent/data/digicams.xml");
			engine.setKnowledgeBaseFile("WebContent/data/rules.xml");
			engine.loadModel();

			AbsMusFinder musfinder = new MinUnsat1MusFinder(engine);
			List result = musfinder.findMUS();
			System.out.println("Found MUS of size: " + result.size());
			Iterator it = result.iterator();
			Set set = null;
			FilterRule fr = null;
			while (it.hasNext()) {
				set = (Set) it.next();
				System.out.println("List: " + set);
				System.out.println("---------------------");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
