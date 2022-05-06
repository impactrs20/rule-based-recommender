package ls13.productfinder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import ls13.productfinder.utils.ScriptCompiler;
import ls13.productfinder.utils.Utils;


import org.mozilla.javascript.Script;



/**
 * <p>Title:  InfoText-Manager</p>
 * <p>Description:  Class to manage variable infotexts</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * 
 * @author DJ
 * @version 1.0
 */

public class InfoTextManager {

	/**
	 * Returns the applicable variant of an infotext 
	 * @param name name of the text to be retrieved
	 * @param session the current user session
	 * @return the text variant or an empty string, if no condition matches
	 * @throws an error if the name is not registerd
	 */
	String getText(String name, RecommenderSession session) throws ProductFinderException {
		// Set the values
		session.ee.setVariables(session.getEngine(),session.userInputs,true);
		List variants = (List)this.infoTexts.get(name);
		if (variants == null|| variants.size() == 0) {
			String defaulttext = (String)this.defaultTexts.get(name);
			if (defaulttext == null) {
				throw new ProductFinderException("[ERROR] Infotext with name " + name + " not defined");
			}
			else {return defaulttext;}
		}
		// Iterate over the variants and check whether the condition holds
		// Set the variablse
		ExpressionEvaluator ee =session.getExpressionEvaluator(); 
		ee.setVariables(
											session.getEngine(),
											session.getUserInputs(),
											false);
		Iterator vit = variants.iterator();
		InfoTextRecord itr;
		while (vit.hasNext()) {
			itr = (InfoTextRecord) vit.next();
			Script s = itr.getCompiledCondition();
			boolean r = Utils.evaluteCompiledExpression(s,ee.getScope(),name);
			if (r == true) {
				return itr.getTextVariant();
			}
		}
		return (String)defaultTexts.get(name);
	}
	
	// the hashmap of infotext objects.
	// contains mapping of textname to infotext list for it
	HashMap infoTexts = new HashMap();
	
	// The default texts
	HashMap defaultTexts = new HashMap();
	public void setDefaultTexts(HashMap t) {this.defaultTexts = t;}

	/**
	 * @return Returns the infoTexts.
	 */
	public HashMap getInfoTexts() {
		return infoTexts;
	}

	/**
	 * @param infoTexts The infoTexts to set.
	 */
	public void setInfoTexts(HashMap infoTexts) {
		this.infoTexts = infoTexts;
	}
	
	/**
	 * Precompiles the condition and the filter expression into java classes and stores
	 * script in object
	 * @throws ProductFinderException in case of errors in filter expressions
	 */
	public void preCompileConditions() throws ProductFinderException {
		ExpressionEvaluator ee = new ExpressionEvaluator();
		Iterator it = this.infoTexts.keySet().iterator();
		List infotextlist;
		Iterator vit;
		InfoTextRecord itr;
		
		String key;
		// Iterate over the infotext entries
		while (it.hasNext()) {
			key = (String)it.next();
			infotextlist = (List) this.infoTexts.get(key);
			// iterate over all the variants
			vit = infotextlist.iterator();
			while (vit.hasNext()) {
				itr = (InfoTextRecord) vit.next();
				String condition = itr.getCondition();
				//System.out.println("compiling: " + condition);
				Script s = ScriptCompiler.compileString(ee.getContext(),condition,key);
				itr.setCompiledCondition(s);
			}
		}
	}
	
	/**
	 * Adds a new infotext with default value; registers an empty list of variants
	 *
	 * @param name the name of the infotext
	 * @param defaultText the default text to be displayed
	 */
	public void addInfoText(String name, String defaultText) {
		this.defaultTexts.put(name,defaultText);
		this.infoTexts.put(name, new ArrayList());
	}
	
	/**
	 * Add a text variant to an info text. If infotext does not already exist, a new
	 * empty default text is registered
	 * @param name the name of the infotext
	 * @param condition the display condition
	 * @param textVariant the text variant to be displayed when the condition is fulfilled
	 */
	public void addInfoTextVariant(String name, String condition, String textVariant) {
		if (this.defaultTexts.get(name) == null) {this.defaultTexts.put(name,"");}
		List<InfoTextRecord> variants = (List)this.infoTexts.get(name); 
		if (variants == null) {
			variants = new ArrayList<InfoTextRecord>();
		}
		InfoTextRecord itr = new InfoTextRecord(condition, textVariant);
		variants.add(itr);
		this.infoTexts.put(name,variants);
		
	}
	
}


