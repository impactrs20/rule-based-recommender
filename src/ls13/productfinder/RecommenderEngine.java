package ls13.productfinder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import ls13.productfinder.rules.RuleEngine;
import ls13.productfinder.utils.DataLoader;
import ls13.productfinder.utils.Globals;


/**
 * <p>Title:  Recommender engine</p>
 * <p>Description:  Class containting engine information - product data and recommendation rules as well as handles to the 
 * filtering engine and infotext processor</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * @author DJ
 * @version 1.0
  */

public class RecommenderEngine {

	// The list of products
	List products;
	// The filter-based matcher
	FilterBasedMatcher filterBasedMatcher; 
	
	// Hashmap mapping variables to types
	HashMap<String,String> variables;

	
	// Hashmap mapping variables to domains
	HashMap<String,List> variableDomains;
	
	// The infotext manager
	InfoTextManager infoTetxtManager;
	
	// The file name where the products can be found
	private String productFile;
	// The file name where the knowledge-base can be found
	private String knowledgeBaseFile;
	
	/**
	 * @return Returns the knowledgeBaseFile.
	 */
	public String getKnowledgeBaseFile() {
		return this.knowledgeBaseFile;
	}
	/**
	 * @param knowledgeBaseFile The knowledgeBaseFile to set.
	 */
	public void setKnowledgeBaseFile(String knowledgeBaseFile) {
		this.knowledgeBaseFile = knowledgeBaseFile;
	}
	/**
	 * @return Returns the productFile.
	 */
	public String getProductFile() {
		return this.productFile;
	}
	/**
	 * @param productFile The productFile to set.
	 */
	public void setProductFile(String productFile) {
		this.productFile = productFile;
	}

	/**
	 * @return Returns the products.
	 */
	public List getProducts() {
		return products;
	}
	/**
	 * @param products The products to set.
	 */
	public void setProducts(List products) {
		this.products = products;
	}
	/**
	 * @return Returns the variables.
	 */
	public HashMap getVariables() {
		return variables;
	}
	
	/** @return the list of variable names */
	public List getVariableNames() {
		return new ArrayList(variables.keySet());
	}
	
	/**
	 * @param variables The variables to set.
	 */
	public void setVariables(HashMap<String, String> variables) {
		this.variables = variables;
		// Add the utility variable
		this.variables.put(UtilityCalculator.UTILITY_DENOMINATOR, "Double");
	}

	/**
	 * Creates an initial recommender-engine instance
	 */ 
	public RecommenderEngine() throws ProductFinderException {
		init();
	}
	/**
	 * Creates an empty engine, remembers file locations, and initializes the filter-based matcher
	 * and the infotext engine
	 * @param kbFile filename of file where rules are defined
	 * @param productFile name of file where products are defined
	 * @throws ProductFinderException in case of parsing errors 
	 */
	public RecommenderEngine(String kbFile, String productFile) throws ProductFinderException {
		init();
		this.knowledgeBaseFile = kbFile;
		this.productFile = productFile;
	}

	/**
	 * Internal initialization method
	 * @throws ProductFinderException in case of parsing errors
	 */
	private void init() throws ProductFinderException {
		this.filterBasedMatcher = new FilterBasedMatcher(this);
		this.variables = new HashMap();
		this.variableDomains = new HashMap();
		this.products = new ArrayList();
		this.infoTetxtManager = new InfoTextManager();
		this.utilityCalculator = new UtilityCalculator(this);
		this.similarityCalculator = new SimilarityCalculator(this);
		this.ruleEngine = new RuleEngine(this);
	}
	
	/**
	 * Loads both the product data as well as all recommendation rules 
	 * (does reload in any case and pre-compiles JavaScript conditions)
	 * @throws ProductFinderException
	 */
	public void loadModel() throws ProductFinderException {
		reloadModel();
	}
	
	/**
	 * Reloads product data and recommendation and advisory rules. Precompiles conditions contained in 
	 * the knowledge base
	 * @throws ProductFinderException in case of parsing errors
	 */
	public void reloadModel() throws ProductFinderException {
		try {
			Globals.evalCount++;
			loadKnowledgeBase();
			loadProducts();
			this.filterBasedMatcher.preCompileFilters();
			this.filterBasedMatcher.initFilterResults();
			this.infoTetxtManager.preCompileConditions();
			// Compute static utilities on startup
			if (this.utilityCalculator.getUtiltityFunctionCode() != null && 
				!this.utilityCalculator.dynamicCalculaction()) {
				this.utilityCalculator.computeStaticUtilities(this.products);
			}
			this.ruleEngine.compileBusinessRules();
		} catch (IOException e) {
			e.printStackTrace();
			throw new ProductFinderException("[FATAL]: I/O-Exception when reading data files. " + e.getMessage());
		}
	}
	
	/**
	 * Default implementation: loads data from given XML file
	 * @return a list of hashmaps which maps keynames to objects
	 * @throws ProductFinderException
	 */
	public void loadProducts() throws ProductFinderException, IOException {
		DataLoader.readProductData(this);
		
	};
	/**
	 * Loads the knowledge base into the engine
	 * @throws ProductFinderException
	 */
	public void loadKnowledgeBase() throws ProductFinderException, IOException{
		DataLoader.loadKnowledgeBase(this);
	};
	
	/**
	 * Returns a user-readable reprseentation of the engine's model
	 */
	public String toString() {
		String s = "RECOMMENDATION MODEL: \n"  +
			"----------------------------------\n"; 
		String vars ="VARIABLES: \n"; 
		Iterator it = this.variables.keySet().iterator();
		while (it.hasNext()) {
			String key = (String) it.next();
			vars += key + " (" + this.variables.get(key) + ")";
			List domain = variableDomains.get(key);
			if (domain != null) {
				vars +=  "[ENUM] " + domain + "\n" ;
			}
			else {
				vars += "\n";
			}
		}
		s +=vars;
		s += "----------------------------------\n";
		s+= "PRODUCTS: \n";
		it = this.getProducts().iterator();
		while (it.hasNext()) {
			HashMap p = (HashMap) it.next();
			s+= p + "\n";
		}
		s += "----------------------------------\n";
		s+= "FILTER RULES: \n";
		it = this.filterBasedMatcher.getFilters().iterator();
		while (it.hasNext()) {
			FilterRule f = (FilterRule) it.next();
			s+= f;
		}
		return s;
	}
	/**
	 * @return Returns the fbMatcher.
	 */
	public FilterBasedMatcher getFilterBasedMatcher() {
		return filterBasedMatcher;
	}
	/**
	 * @param fbMatcher The fbMatcher to set.
	 */
	public void setFilterBasedMatcher(FilterBasedMatcher fbMatcher) {
		this.filterBasedMatcher = fbMatcher;
	}
	/**
	 * @return Returns the infotextmgr.
	 */
	public InfoTextManager getInfoTetxtManager() {
		return infoTetxtManager;
	}
	
	/**
	 * The handle to the global static utility 
	 * calculator
	 */
	UtilityCalculator utilityCalculator;

	public UtilityCalculator getUtilityCalculator() {
		return utilityCalculator;
	}
	public void setUtilityCalculator(UtilityCalculator utilityCalculator) {
		this.utilityCalculator = utilityCalculator;
	}
	/**
	 * The handle to the session global similarity calculator
	 * 
	 */
	SimilarityCalculator similarityCalculator;

	/**
	 * @return Returns the similarityCalculator.
	 */
	public SimilarityCalculator getSimilarityCalculator() {
		return similarityCalculator;
	}
	/**
	 * @param similarityCalculator The similarityCalculator to set.
	 */
	public void setSimilarityCalculator(SimilarityCalculator similarityCalculator) {
		this.similarityCalculator = similarityCalculator;
	} 
	
	/**
	 * A handle to the local rule engine
	 */
	RuleEngine ruleEngine;

	/**
	 * @return the ruleEngine
	 */
	public RuleEngine getRuleEngine() {
		return ruleEngine;
	}
	/**
	 * @param ruleEngine the ruleEngine to set
	 */
	public void setRuleEngine(RuleEngine ruleEngine) {
		this.ruleEngine = ruleEngine;
	}
	
	/**
	 * Getter for the variable domains
	 * @return the hashmap with the mappings
	 */
	public HashMap<String, List> getVariableDomains() {
		return variableDomains;
	}
	/**
	 * Setter for the variable domains
	 * @param variableDomains 
	 */
	public void setVariableDomains(HashMap<String, List> variableDomains) {
		this.variableDomains = variableDomains;
	}
	
	
	
}
