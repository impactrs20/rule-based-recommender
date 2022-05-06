package ls13.productfinder;

import org.mozilla.javascript.Script;

/**
 * <p>
 * Title: Infotext record
 * </p>
 * <p>
 * Description: Internal class to store infotext information
 * </p>
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 * <p>
 * 
 * </p>
 * 
 * @author DJ
 * @version 1.0
 */

// class for storing info text records
public class InfoTextRecord {
	String condition;

	Script compiledCondition;

	String textVariant;

	/**
	 * @return Returns the compiledCondition.
	 */
	public Script getCompiledCondition() {
		return compiledCondition;
	}

	/**
	 * @param compiledCondition
	 *            The compiledCondition to set.
	 */
	public void setCompiledCondition(Script compiledCondition) {
		this.compiledCondition = compiledCondition;
	}

	/**
	 * @return Returns the condition.
	 */
	public String getCondition() {
		return condition;
	}

	/**
	 * @param condition
	 *            The condition to set.
	 */
	public void setCondition(String condition) {
		this.condition = condition;
	}

	/**
	 * @return Returns the textVarant.
	 */
	public String getTextVariant() {
		return textVariant;
	}

	/**
	 * @param textVarant
	 *            The textVarant to set.
	 */
	public void setTextVariant(String textVarant) {
		this.textVariant = textVarant;
	}

	public InfoTextRecord(String condition, String textVarant) {
		super();

		this.condition = condition;
		this.textVariant = textVarant;
	}

	public InfoTextRecord() {
		super();
		
	}
	
	public String toString() {
		String result = "Infotext: IF " + this.condition + " THEN " + this.textVariant + "\n";
		return result;
	}

	
	
	
}
