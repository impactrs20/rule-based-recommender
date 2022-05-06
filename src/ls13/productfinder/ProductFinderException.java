package ls13.productfinder;
/**
 * <p>Title:  Application-specific exception class</p>
 * <p>Description:  Exception root class</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * @author DJ
 * @version 1.0
 */

public class ProductFinderException extends Exception {
	public static final long serialVersionUID = 0L;;
	
	public ProductFinderException(String msg, Throwable t) {
		super(msg,t);
	}
	public ProductFinderException(String msg) {
		super(msg);
	}
}
