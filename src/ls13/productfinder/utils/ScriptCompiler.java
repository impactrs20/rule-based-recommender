package ls13.productfinder.utils;

import ls13.productfinder.ProductFinderException;
import ls13.productfinder.utils.Globals;

import org.mozilla.javascript.CompilerEnvirons;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.optimizer.ClassCompiler;


/**
 * <p>Title:  Script-compiler</p>
 * <p>Description:  Compiles a JavaScript method into an executable Script object</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * 
 * @author DJ
 * @version 1.0
 */

public class ScriptCompiler {
	// Handle to global ClassCompiler needed for compiling the source.
	// Stored here for performance reasons.
	static ClassCompiler cc = null;
	/**
	 * Compiles a given JS code into a Script object
	 * @param cx the context of the scripting engine
	 * @param code the code to be compiled
	 * @param name the name of the class to be generated
	 * @return an executable Script object
	 * @throws ProductFinderException
	 */
	public static Script compileString(Context cx, String code, String name) throws ProductFinderException {
		Globals.classCount++;
		if (ScriptCompiler.cc == null) {
			CompilerEnvirons ce = new CompilerEnvirons();
			ce.initFromContext(cx);
	        ScriptCompiler.cc = new ClassCompiler(ce);
		}
		String _name = Globals.COMPILED_CLASS_PREFIX + Globals.evalCount + 
			"_" + Globals.classCount;
		Object[] o = cc.compileToClassFiles(code, "", 0, _name);
		ByteArrayClassLoader mc = new ByteArrayClassLoader(
										ScriptCompiler.class.getClassLoader(),
										(byte[]) o[1]);
		Class c = mc.findClass(_name);
		Script script;
		try {
			script = (Script)c.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ProductFinderException("[FATAL] Problem pre-compiling code: " + code, e);
		}
		return script;
	}

}
