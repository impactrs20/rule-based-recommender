package ls13.productfinder.tests;

import java.util.List;

import ls13.productfinder.RecommenderEngine;
import ls13.productfinder.RecommenderSession;
import ls13.productfinder.UtilityCalculator;

import org.junit.Test;


public class UtilityTest {
	@Test public void run() {
		try {
			// Tell the system where things are.
			RecommenderEngine engine = new RecommenderEngine();
			engine.setProductFile("WebContent/data/digicams.xml");
			engine.setKnowledgeBaseFile("WebContent/data/rules.xml");
			engine.loadModel();
			UtilityCalculator uc = engine.getUtilityCalculator();
			if (uc.dynamicCalculaction()) {
				RecommenderSession session = new RecommenderSession(engine);
				session.setInput("c_pref_interest", "low");
				session.setInput("c_pref_manufacturer", new String[]{"Sony"});
				List products = uc.computeDynamicUtilities(engine.getProducts(),session);
				System.out.println("products now: " + products);
			}
			else {
				System.out.println("static ..");
				uc.computeStaticUtilities(engine.getProducts());
			}
			
/*
			UtilityCalculator uc = engine.getUtilityCalculator();
            String utilityFunction = "function " + UtilityCalculator.UTILITY_FUNCTION + "() {" +
					"     out.println(\"Hallo\"); out.println(p_price);"  + 
					" 	  return 10;" + 
					" }";
            
            System.out.println("util: " + utilityFunction);
            uc.setUtilityFunction(utilityFunction);
			uc.getEe().registerFunction(utilityFunction);

			uc.computeStaticUtilities(engine.getProducts());
*/			
		} catch (Exception e)  {
			e.printStackTrace();
		}
		finally {
			System.out.println("Program terminated");
		}
		
	}

}
