package ls13.productfinder.tests;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ls13.productfinder.FilterRule;
import ls13.productfinder.RecommenderEngine;
import ls13.productfinder.mus.ConflictChecker;

import org.junit.Test;


public class ConflictTest {

	
	/**
	 * Main entry points
	 * @param args - none
	 */
   @Test public void run() {
	   try{
		   System.out.println("Hello world");
		   
			RecommenderEngine engine = new RecommenderEngine();
			engine.setProductFile("WebContent/data/digicams.xml");
			engine.setKnowledgeBaseFile("WebContent/data/rules.xml");
			engine.loadModel();
		   
	
			
			// add some filters for the test
			List applicableFilters = new ArrayList();
			List allFilters = engine.getFilterBasedMatcher().getFilters();
			applicableFilters.add(allFilters.get(1));
			applicableFilters.add(allFilters.get(2));

			// Debug
			Iterator it = applicableFilters.iterator();
			while (it.hasNext()) {
				FilterRule f = (FilterRule) it.next();
				System.out.println("f:" + f );				
			}

			// Do the consistency check.
			ConflictChecker cc = new ConflictChecker(engine);
			boolean isconflict = cc.isConflict(applicableFilters);
			if (isconflict) {
				System.out.println("conflict - no item remains");
			}
			else {
				System.out.println("no conflict - items remain");
			}
		}
	   catch (Exception e) {
		   e.printStackTrace();
	   }
   }
}
