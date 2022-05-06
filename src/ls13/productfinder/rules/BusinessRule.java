package ls13.productfinder.rules;

import org.mozilla.javascript.Script;

/**
 * A simple data structure for holding business rule data
 * @author dietmar
 *
 */
public class BusinessRule {
	/**
	 * the name of the rule
	 */
	String name;
	/**
	 * The script code of the "if" part
	 */
	String code_if;
	/**
	 * The script code of the "then" part
	 */
	String code_then;
	
	
	public BusinessRule(String name) {
		this.name = name;
	}
	public BusinessRule() {
	}


	public String getCode_if() {
		return code_if;
	}
	public void setCode_if(String code_if) {
		this.code_if = code_if;
	}
	public String getCode_then() {
		return code_then;
	}
	public void setCode_then(String code_then) {
		this.code_then = code_then;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String toString() {
		return "Business rule: " + this.name + "\n" + 
		"IF    " + this.code_if + "\n" + 
		"THEN  " + this.code_then + "\n";
	}

	/** The compiled condition */
	Script compiledIf;
	/** The compiled consequent */
	Script compiledThen;


	/**
	 * @return the compiledIf
	 */
	public Script getCompiledIf() {
		return compiledIf;
	}
	/**
	 * @param compiledIf the compiledIf to set
	 */
	public void setCompiledIf(Script compiledIf) {
		this.compiledIf = compiledIf;
	}
	/**
	 * @return the compiledThen
	 */
	public Script getCompiledThen() {
		return compiledThen;
	}
	/**
	 * @param compiledThen the compiledThen to set
	 */
	public void setCompiledThen(Script compiledThen) {
		this.compiledThen = compiledThen;
	}
	
	
	
	
}
