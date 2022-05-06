package ls13.productfinder.utils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import ls13.productfinder.ProductFinderException;
import ls13.productfinder.RecommenderEngine;

import org.xml.sax.InputSource;


/**
 * <p>Title:  Data loader</p>
 * <p>Description:  Helper class to read and load files</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * @author DJ
 * @version 1.0
 */

public class DataLoader {

	/**
	 * Reads the list of products from the specified file and stores it in the engine
	 * @throws ProductFinderException
	 */
	public static void readProductData(RecommenderEngine engine) throws ProductFinderException, IOException {
		if (engine.getProductFile() == null ) {
			return;
		}
		File f = new File(engine.getProductFile());
        FileReader r = new FileReader(f);
        InputSource is = new InputSource(r);
        ProductDataParser parser = new ProductDataParser (is);
        parser.setModel(engine);
        parser.parse();
        // Put everything into the engine
        engine.setProducts(parser.getProducts());
	}

	/**
	 * Reads the knowledge base from the specified file and stores it in the engine
	 * @throws ProductFinderException
	 */
	public static void loadKnowledgeBase(RecommenderEngine engine)throws ProductFinderException, IOException  {
		if (engine.getKnowledgeBaseFile() == null ) {
			return;
		}
		File f = new File(engine.getKnowledgeBaseFile());
        FileReader r = new FileReader(f);
        InputSource is = new InputSource(r);
        KnowledgeBaseParser parser = new KnowledgeBaseParser(is);  
        parser.parse();
        // Put everything into the engine
        engine.setVariables(parser.getVariables());
        engine.setVariableDomains(parser.getVariableDomains());
        engine.getFilterBasedMatcher().setFilters(parser.getFilters());
        engine.getInfoTetxtManager().setInfoTexts(parser.getInfoTexts());
        engine.getInfoTetxtManager().setDefaultTexts(parser.getDefaultTexts());
        engine.getUtilityCalculator().setUtilityFunction(parser.getUtilityFunction());
        engine.getUtilityCalculator().setDynamicUtilities(parser.dynamicUtilities);
        engine.getSimilarityCalculator().setSimilarityFunctionCode(parser.getSimilarityFunction());
        engine.getRuleEngine().setBusinessRules(parser.getBusinessRules());
	}


}
