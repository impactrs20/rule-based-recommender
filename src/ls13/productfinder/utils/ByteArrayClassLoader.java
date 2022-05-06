package ls13.productfinder.utils;
/**
 * <p>Title:  Byte array class loader</p>
 * <p>Description:  Loads a java class from a byte array</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * @author DJ
 * @version 1.0
 */

public class ByteArrayClassLoader extends ClassLoader {
	byte[] byteCode;


	public ByteArrayClassLoader(ClassLoader parent, byte[] b) {
		super(parent);
		byteCode = b;
	}
	public ByteArrayClassLoader(byte[] b) {
		byteCode = b;
	}
	
	public void setByteCode(byte[] b) {
		byteCode = b;
	}
	

	public Class findClass(String name) {
		return defineClass(name, byteCode, 0, byteCode.length);
	}

}
