package ls13.productfinder.utils;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * <p>Title:  Simple function to pretty print product information</p>
 * <p>Description:  </p>
 * <p>Copyright: Copyright (c) 2007</p>
 * 
 * @author DJ
 * @version 1.0
 */

public class ProductPrinter {
	/**
	 * Prints a hashmap in one line, resolving multi-valued arrays using alphanumeric sorting
	 * @param product the product to display
	 * @return a String representation
	 */
	public static String print(HashMap product) {
		StringBuffer result = new StringBuffer();
		Set keys = product.keySet();
		List keysAsList = Arrays.asList(keys.toArray());
		Collections.sort(keysAsList);
		// System.out.println(keysAsList);
		Iterator it = keysAsList.iterator();
		Object o = null;
		Object value = null;
		while (it.hasNext()) {
			o = it.next();
			value = product.get(o);
			result.append(o).append(":");
			if (value instanceof Object[]) {
				Object[] arr = (Object[]) value;
				for (int i=0;i<arr.length;i++) {
					result.append(arr[i]);
					if (i<arr.length-1) {
						result.append(",");
					}
				}
				result.append("\n");
			}
			else {
				result.append(value).append("\n");
			}
		}
		
		return result.toString();
	}
}
