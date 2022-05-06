package ls13.productfinder.utils;
/**
 * <p>Title:  Globals</p>
 * <p>Description: Describes global definitions </p>
 * <p>Copyright: Copyright (c) 2006</p>

 * @author DJ
 * @version 1.0
 */

public class Globals {
	static String separator = "#";

	/**
	 * @return Returns the separator.
	 */
	public static String getSeparator() {
		return Globals.separator;
	}

	/**
	 * @param separator The separator to set.
	 */
	public static void setSeparator(String separator) {
		Globals.separator = separator;
	}
	
	// Reload counter: To ensure unique class ids
	public static int evalCount = 0;
	// Class counter, to ensure unique id's in one
	// compilation run
	public static int classCount = 0;
	
	public static final String COMPILED_CLASS_PREFIX = "JPF__"; 
	

}
