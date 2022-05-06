package ls13.productfinder.tests;

import ls13.productfinder.RecommenderEngine;
import ls13.productfinder.RecommenderSession;

import org.junit.Test;

public class RulesTest {
	@Test public void run() {
		try {
	
			
			// Tell the system where things are.
			RecommenderEngine engine = new RecommenderEngine();
			engine.setProductFile("WebContent/data/digicams.xml");
			engine.setKnowledgeBaseFile("WebContent/data/rules.xml");
			engine.loadModel();
			RecommenderSession session = new RecommenderSession(engine);
			session.setInput("c_pref_price", "medium");
			//session.setInput("c_pref_price_derived", "null");
			session.applyRules();
			
			System.out.println("Session values: " + session.getUserInputs());
			
		} catch (Exception e)  {
			e.printStackTrace();
		}
		finally {
			System.out.println("Rules test finished");
		}
		
	}
	

}
