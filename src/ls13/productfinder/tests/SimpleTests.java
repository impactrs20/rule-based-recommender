package ls13.productfinder.tests;

import java.util.HashMap;
import java.util.Iterator;

import ls13.productfinder.FilterRule;
import ls13.productfinder.RecommendationResult;
import ls13.productfinder.RecommenderEngine;
import ls13.productfinder.RecommenderSession;

import org.junit.Test;



public class SimpleTests {

	/**
	 * Tests individual functionalities
	 */

	@Test public void run() {

		try {
			System.out.println("----- Starting single tests  ----------");
			// ------------------------------------------------------------
			// SIMPLER PERFORMANCE TESTS
			// ------------------------------------------------------------
			/*

			Context cx = Context.enter();
			Scriptable scope = cx.initStandardObjects();

			String f = "p_price1 > 100 && isContainedIn('USBx',p_extras1)";
			CompilerEnvirons ce = new CompilerEnvirons();
			ce.initFromContext(cx);
            String containmentFunction = "function isContainedIn(s,coll) {" +
        	" for (var i=0;i<coll.length;i++) {" +
        		" if (s == coll[i] || s.equals(coll[i])) return true;" + 
        	  "}" + 
        	  "return false;" + 
            "}";
            cx.evaluateString(scope, containmentFunction, "", 1, null);
            
            ClassCompiler cc = new ClassCompiler(ce);
			Object[] o = cc.compileToClassFiles(f, "", 0, "f1");
			MyClassLoader mc = new MyClassLoader((byte[]) o[1]);
			Class c = mc.findClass("f1");
			Script script = (Script)c.newInstance();
			scope.put("p_price1", scope, new Double(300));
			scope.put("p_extras1", scope, new String[]{"USB","Firewire"});
			
			
			Object rs = script.exec(cx,scope);
			System.out.println("rs: " + rs);
			*/
/*
			long start = System.currentTimeMillis();
			Object r = null;
			for (int i = 0; i < 1500; i++) {
				String filter = "p_price1 > " + i 
						+ " && p_extras1.equals('USB')";
				scope.put("p_price1", scope, new Double(200));
				scope.put("p_price2", scope, new Double(200));
				scope.put("p_price3", scope, new Double(200));
				scope.put("p_price4", scope, new Double(200));
				scope.put("p_price5", scope, new Double(200));
				scope.put("p_extras2", scope, "USB");
				scope.put("p_extras3", scope, "USB");
				scope.put("p_extras4", scope, "USB");
				scope.put("p_extras5", scope, "USB");
				scope.put("p_extras1", scope, "USB");
				scope.put("p_extras2", scope, "USB");
				scope.put("p_extras3", scope, "USB");
				scope.put("p_extras4", scope, "USB");
				scope.put("p_extras5", scope, "USB");

				//Object r1 = cx.evaluateString(scope, filter, "", 0, null);
				// r = script.exec(cx,scope);
				
			}
			System.out.println("result: " + r);
			System.out.println("result done in " + (stop - start) + "ms.");

			cx.exit();
*/
			// ------------------------------------------------------------
			// Tell the system where things are.
			RecommenderEngine engine = new RecommenderEngine();
			engine.setProductFile("WebContent/data/digicams.xml");
			engine.setKnowledgeBaseFile("WebContent/data/rules.xml");
			engine.loadModel();
			
			//System.out.println("Loading finished.");
			
			// TEST: Print the variable domains
			/*
			HashMap variableDomains = engine.getVariableDomains();
			Iterator it = variableDomains.keySet().iterator();
			String key = null;
			while (it.hasNext()) {
				key = (String) it.next();
				System.out.println("Variable: " + key + ":" + variableDomains.get(key));
			}
			*/
			
			/*
			 * try { engine.loadKnowledgeBase(); engine.loadProducts(); //
			 * Simulate some inputs RecommenderSession userSession = new
			 * RecommenderSession(engine);
			 * userSession.getUserInputs().put("c_pref_rating","high");
			 * userSession.getUserInputs().put("c_pref_price","400");
			 * userSession.getUserInputs().put("c_pref_extras", new
			 * String[]{"USB", "Firewire"});
			 * 
			 * FilterRule filter =
			 * (FilterRule)engine.getFilterBasedMatcher().getFilters().get(1);
			 * System.out.println("testing filter: " + filter); HashMap product =
			 * (HashMap) engine.getProducts().get(0); long start =
			 * System.currentTimeMillis(); for (int i=0;i<1500;i++) { boolean
			 * match = engine.getFilterBasedMatcher() .productMatches( engine,
			 * userSession.getExpressionEvaluator(), product,
			 * userSession.getUserInputs(), filter); }
			 * 
			 * long stop = System.currentTimeMillis(); System.out.println("Time: " +
			 * (stop-start) + " ms."); } catch (Exception e) {
			 * e.printStackTrace(); } if (true){System.exit(1);}
			 */
			// Create a new session
			//RecommenderSession userSession = new RecommenderSession(engine);
			//userSession.

			/*
			// Simulate some inputs
			userSession.getUserInputs().put("c_pref_rating", "high");
			userSession.getUserInputs().put("c_pref_price", "100");
			userSession.getUserInputs().put("c_pref_extras",
					new String[] { "USB", "Firewire" });
			*/
			/*
			String text1 = userSession.getInfoText("price");
			String text2 = userSession.getInfoText("extras");
			
			String text3 = "defaultext3";
			userSession.getEngine().getInfoTetxtManager().addInfoText("t3", text3);
			userSession.getEngine().getInfoTetxtManager().addInfoTextVariant("t3","c_pref_rating == 'high'", text3 + "variant");
			userSession.getEngine().getInfoTetxtManager().preCompileConditions();
			*/

			/*
			System.out.println(text1);
			System.out.println(text2);
			System.out.println(text3);
			
			if (true) System.exit(1);
			*/
			
			//System.out.println(userSession);
			// Compute a recommendation
			/*
			Globals.start = System.currentTimeMillis();
			RecommendationResult result = userSession
					.computeRecommendationResult(1);
			// slong stop = System.currentTimeMillis();
			long stop = System.currentTimeMillis();
			*/
			/*
			System.out.println(result);
			System.out.println("\nnb products: " + engine.getProducts().size());
			System.out.println("\nnb filters: "
					+ engine.getFilterBasedMatcher().getFilters().size());
			System.out.println("Time: " + (stop - Globals.start) + " ms.");
			System.out.println("Eval-Count: " + Globals.evalCount);

			
			System.out.println("products: "); 
			Utils.printList(result.getMatchingProducts());
			*/
			/*
			System.out.println(result);
			System.out.println("products:\n"); 
			Utils.printList(result.getMatchingProducts());
			*/
			/*
			userSession.sortResultBy("p_description","desc");
			System.out.println("sorted products: ");
			Utils.printList(result.getMatchingProducts());
			*/
			/*
			 * boolean result = ee.eval(model,values,"c_pref_rating == 'low'");
			 * System.out.println("result1: " + result); result =
			 * ee.eval(model,values,"c_pref_price > 100");
			 * System.out.println("result2: " + result); result =
			 * ee.eval(model,values,"isContainedIn('Firewire',c_pref_extras)");
			 * System.out.println("resul3t: " + result);
			 */
			/*
			 * HashMap product = (HashMap) model.getProducts().get(0);
			 * System.out.println("testing product: " + product); FilterRule
			 * filter =
			 * (FilterRule)model.getFilterBasedMatcher().getFilters().get(0);
			 * System.out.println("testing filter: " + filter);
			 * ee.setVariables(model,values,true); boolean match =
			 * model.getFilterBasedMatcher().productMatches(model,ee,product,values,filter);
			 * System.out.println("result4: " + match);
			 * 
			 * System.out.println("bitsets: " +
			 * model.getFilterBasedMatcher().getProductsForFilters());
			 * 
			 */

			
			RecommenderSession userSession = new RecommenderSession(engine);
			userSession.getUserInputs().put("c_pref_interest", "expert");
			userSession.getUserInputs().put("c_pref_min_resolution","high");
			userSession.getUserInputs().put("c_pref_manufacturer",new String[]{"Sony","Fujifilm"});
			userSession.getUserInputs().put("c_pref_price","medium");
			userSession.getUserInputs().put("c_pref_extras",new String[]{"dockingstation","largedisplay"});
			
			System.out.println("--------------------");
			userSession.computeRecommendationResult(1);
			userSession.sortResultBy("p_price", "ASC");
			
			RecommendationResult result = userSession.getLastResult();
			System.out.println("Found " + result.getMatchingProducts().size() + " products.");
			System.out.println("My proposal: " + ((HashMap)result.getMatchingProducts().get(0)).get("p_name"));
			System.out.println("Detail: " + result.getMatchingProducts().get(0));
			
			System.out.println("applied: " + result.getAppliedFilters());
			System.out.println("relaxed: " + result.getRelaxedFilters());
			
			String explanation="Why do I recommend this model? ";
			explanation += userSession.getInfoText("interests") + " ";
			for (int i=0;i<result.getAppliedFilters().size();i++) {
				explanation += ((FilterRule)result.getAppliedFilters().get(i)).getExplanation() + " ";
			}
			if (result.getRelaxedFilters().size() > 0) {
				explanation += "\n\n" + "Unfortunately, none of the products fulfils " +
						"all of your requirements, maybe because to all product data is available: ";
				for (int i=0;i<result.getRelaxedFilters().size();i++) {
					explanation += ((FilterRule)result.getRelaxedFilters().get(i)).getExcuse();
				}
			}

			System.out.println("\n" + explanation);
			
			
			// Finalize
			System.out.println("----- Normal program termination ------");

		} catch (Throwable e) {
			System.out.println("Status = ERROR: " + e.getMessage());
			e.printStackTrace();
		} finally {
		}
	}

	
}
