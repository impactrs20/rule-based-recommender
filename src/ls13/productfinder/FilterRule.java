package ls13.productfinder;

import org.mozilla.javascript.Script;

/**
 * <p>Title: Filter rule handle </p>
 * <p>Description:  A class representing a filter rule</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * 
 * @author DJ
 * @version 1.0
 */

public class FilterRule {

	// Instance members
	String name; // name of the filter
	String condition; // condition, when filter should be applied
	String filter; // filter condition to be checked on products
	int priority; // priority of filter 
	String explanation; // user-readable text when filter was successfully applied
	String excuse; // the excuse for non-applied filters
	
	// handles to compiled scripts
	Script compiledCondition;
	Script compiledFilter;
	
	
	
	/**
	 * @return Returns the precompiledCondition.
	 */
	public Script getCompiledCondition() {
		return compiledCondition;
	}
	/**
	 * @param precompiledCondition The precompiledCondition to set.
	 */
	public void setCompiledCondition(Script precompiledCondition) {
		this.compiledCondition = precompiledCondition;
	}
	/**
	 * @return Returns the precompiledFilter.
	 */
	public Script getCompiledFilter() {
		return compiledFilter;
	}
	/**
	 * @param precompiledFilter The precompiledFilter to set.
	 */
	public void setCompiledFilter(Script precompiledFilter) {
		this.compiledFilter = precompiledFilter;
	}
	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return Returns the condition.
	 */
	public String getCondition() {
		return condition;
	}
	/**
	 * @param condition The condition to set.
	 */
	public void setCondition(String condition) {
		this.condition = condition;
	}
	/**
	 * @return Returns the excuse.
	 */
	public String getExcuse() {
		return excuse;
	}
	/**
	 * @param excuse The excuse to set.
	 */
	public void setExcuse(String excuse) {
		this.excuse = excuse;
	}
	/**
	 * @return Returns the explanation.
	 */
	public String getExplanation() {
		return explanation;
	}
	/**
	 * @param explanation The explanation to set.
	 */
	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}
	/**
	 * @return Returns the filter.
	 */
	public String getFilter() {
		return filter;
	}
	/**
	 * @param filter The filter to set.
	 */
	public void setFilter(String filter) {
		this.filter = filter;
	}
	/**
	 * @return Returns the priority.
	 */
	public int getPriority() {
		return priority;
	}
	/**
	 * @param priority The priority to set.
	 */
	public void setPriority(int priority) {
		this.priority = priority;
	}
	
	/**
	 * Creates a new filter rule object
	 * @param name name of the filter
	 * @param condition condition, when filter should be applied
	 * @param filter expression over product properties to be checked by filters
	 * @param priority priority of filter application in relaxation process
	 * @param explanation explanation for end user 
	 * @param excuse explanation when filter could not be applied in relaxation process
	 */
	public FilterRule(String name, String condition, String filter, int priority, String explanation, String excuse) {
		super();
		this.name = name;
		this.condition = condition;
		this.filter = filter;
		this.priority = priority;
		this.explanation = explanation;
		this.excuse = excuse;
	}

	/**
	 * returns the filter information in readable form
	 */
	public String toString(){
		// return "Filter rule: (" +name+ ")  \n\tIF USER SPECIFIES: " + condition + " \n\tTHEN ONLY PRODUCTS WHERE: " + filter + "\n";
		return name + " ";
	}
}
