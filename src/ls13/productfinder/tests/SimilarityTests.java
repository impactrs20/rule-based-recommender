package ls13.productfinder.tests;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import ls13.productfinder.RecommenderEngine;
import ls13.productfinder.SimilarityCalculator;
import ls13.productfinder.utils.ProductPrinter;

import org.junit.Test;


public class SimilarityTests {

	@Test public void run() {
		try {
			// Tell the system where things are.
			RecommenderEngine engine = new RecommenderEngine();
			engine.setProductFile("WebContent/data/digicams.xml");
			engine.setKnowledgeBaseFile("WebContent/data/rules.xml");
			engine.loadModel();
			SimilarityCalculator sc = engine.getSimilarityCalculator();
			List products = engine.getProducts();
			//double sim = sc.computeSimilarity((HashMap) products.get(0), (HashMap) products.get(1));
			List similar = sc.getSimilarProducts((HashMap) products.get(0),3);
			System.out.println("orig: \n" + ProductPrinter.print((HashMap)products.get(0)));
			Iterator it = similar.iterator();
			System.out.println("similar: ");
			while (it.hasNext()) {
				HashMap p = (HashMap) it.next();
				System.out.println(ProductPrinter.print(p));
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
