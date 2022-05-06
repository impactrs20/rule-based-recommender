package ls13.productfinder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import ls13.productfinder.utils.ScriptCompiler;
import ls13.productfinder.utils.Utils;

import org.mozilla.javascript.Script;


/**
 * <p>Title:  Computation of Similarities</p>
 * <p>Description:  Contains a method to compute the similarity between two given products</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * @author DJ
 * @version 1.0
 */

public class SimilarityCalculator {

	
	
	// A handle to the recommender engine
	RecommenderEngine engine;
	
	
	// A local expression evaluator
	ExpressionEvaluator ee;

	public ExpressionEvaluator getEe() {
		return ee;
	}

	String similarityFunctionCode;
	String similarityFunctionName;
	Script compiledSimilarityFunction;

	/**
	 * @return Returns the compiledSimilarityFunction.
	 */
	public Script getCompiledSimilarityFunction() {
		return compiledSimilarityFunction;
	}

	/**
	 * @param compiledSimilarityFunction The compiledSimilarityFunction to set.
	 */
	public void setCompiledSimilarityFunction(Script compiledSimilarityFunction) {
		
		this.compiledSimilarityFunction = compiledSimilarityFunction;
	}


	/**
	 * @param similarityFunctionCode The similarityFunctionCode to set.
	 */
	public void setSimilarityFunctionCode(String similarityFunctionCode) throws ProductFinderException{
		// compile the function
		if (similarityFunctionCode != null) {
			// Get the function name
			similarityFunctionName = null;
			this.similarityFunctionCode = similarityFunctionCode.trim();
//			this.similarityFunctionCode = similarityFunctionCode;
//			System.err.println(this.similarityFunctionCode);
			try {
				String temp = this.similarityFunctionCode.substring("function".length(),this.similarityFunctionCode.length());
				this.similarityFunctionName = temp.substring(0,temp.indexOf('(')).trim(); 
				// register the function
//				System.out.println("temp: " + this.similarityFunctionName);
				//System.exit(1);
				getEe().registerFunction(this.similarityFunctionCode);
				// Precompile the call
				Script conditionSx = ScriptCompiler.compileString(
						getEe().getContext(), 
						this.similarityFunctionName + "()", 
						this.similarityFunctionName
						);
				setCompiledSimilarityFunction(conditionSx);
			}
			catch (Exception e) {
				e.printStackTrace();
				throw new ProductFinderException("Error in similarity function:\n" + similarityFunctionCode);
			}
		}
		
		
	}

	/** Method that evaluates the scripted similarity function for two given 
	 * products
	 */
	public double computeSimilarity (HashMap product1, HashMap product2) throws ProductFinderException{
        //Object jsHashMap = Context.javaToJS(product1, ee.scope);
        //ScriptableObject.putProperty(ee.scope, "p1", jsHashMap);
		ee.scope.put("p1",ee.scope,product1);
		ee.scope.put("p2",ee.scope,product2);
		
		Object sim = Utils.evaluateFunction(this.compiledSimilarityFunction,ee.getScope());
		if (sim instanceof Number) {
			Double d = new Double(sim.toString());
			return d.doubleValue();
		}
		throw new ProductFinderException("Similarity function did not return number value: " + sim);
	}

	
	/**
	 * Evaluates the similarity function for all products in the database
	 * @param product the product to compare with
	 * @return a sorted list of all products
	 * @throws ProductFinderException
	 */
	public List getSimilarProducts(HashMap product) throws ProductFinderException {
		return getSimilarProducts(product,-1);
	}
	/**
	 * Evaluates the similarity function for all products in the database
	 * @param product the product to compare with
	 * @param the top-n to be returned
	 * @return a sorted list of all products
	 * @throws ProductFinderException
	 */
	public List getSimilarProducts(HashMap product, int howmany) throws ProductFinderException {
		
		class SimilarityTuple {
			HashMap product;
			double simvalue;
			
			SimilarityTuple (HashMap product, double simvalue) {
				this.product = product;
				this.simvalue = simvalue;
			}
		}
		class SimilarityComparator implements Comparator {
			public int compare(Object o1, Object o2) {
				if (o1 instanceof SimilarityTuple && o2 instanceof SimilarityTuple) {
					SimilarityTuple t1 = (SimilarityTuple) o1;
					SimilarityTuple t2 = (SimilarityTuple) o2;
					return new Double(t2.simvalue).compareTo(new Double(t1.simvalue));
				}
				else {
					return 0;
				}
			}
		}
		
		
		List result = new ArrayList();
		List products = this.engine.getProducts();
		HashMap product2 = null; 
		Iterator it = products.iterator();
		while (it.hasNext()) {
			product2 = (HashMap) it.next();
			if (product != product2) {
				double simvalue = this.computeSimilarity(product, product2);
				result.add(new SimilarityTuple(product2,simvalue));
			}
			
		}
		/* we need to sort the list */
		Collections.sort(result,new SimilarityComparator());
		List resultingProducts = new ArrayList();
		if (howmany == -1) {
			howmany = result.size();
		}
		for (int i=0;i<howmany;i++) {
			// Insert, they are in reverse order
			resultingProducts.add(((SimilarityTuple)result.get(i)).product);
		}
		return resultingProducts;
	}
	
	

	/** Creates a new similarity calculator with a handle to the global engine */
	public SimilarityCalculator(RecommenderEngine engine) {
		ee = new ExpressionEvaluator();
		this.engine = engine;
	}


	

}
