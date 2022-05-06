package ls13.productfinder.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import ls13.productfinder.ProductFinderException;
import ls13.productfinder.RecommenderEngine;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;


/**
 * <p>Title:  Parser for the knowledge base</p>
 * <p>Description:  </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * 
 * @author DJ
 * @version 1.0
 */

public class ProductDataParser extends DefaultHandler {
	public ProductDataParser(InputSource is) {
        this.is = is;
	}
	
	// the temp list of products
	List products = new ArrayList();
	// give access to them
	public List getProducts() {return products;}
	
	// handle to the engine's model - we need it for looking up the 
	// registered variables
	RecommenderEngine model; 
	void setModel(RecommenderEngine m) {model = m;}
	
	// handle of current product parsed
	HashMap currentProduct = null;
	
	XMLReader xr; 
	InputSource is;
    private Locator locator = null;

    /**
     * Start the parsing process
     * @throws IOException in case of file i/o errors
     * @throws ProductFinderException in case of semantic errors
     */
	public void parse() throws IOException, ProductFinderException {
        this.xr = new org.apache.xerces.parsers.SAXParser();
		this.xr.setContentHandler(this);
		this.xr.setErrorHandler(this);
		this.xr.setEntityResolver(this);
		
		try {
			this.xr.parse(is);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			System.out.println("ERROR at line: " + this.locator.getLineNumber());
			e.printStackTrace();
			throw new ProductFinderException("[ERROR]: Error when parsing product data: " + e.getMessage());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * For remembering line numbers in case of errors
	 */
    public void setDocumentLocator(Locator l) {
        this.locator = l;
    }
	
	/**
	 * Simplest form of parsing. Define action for each of the elements we find during parsing.
	 * Should only be product or attribute tags
	 */
    public void startElement (String uri, String name, String qName, Attributes atts) {
        if ( qName.equalsIgnoreCase("Product") ) {
        	// Found a new product definition
        	// Store an old one..
        	if (currentProduct != null) {
        		products.add(currentProduct.clone());
        	}
        	// Create a new one
        	currentProduct = new HashMap();
        }
        else if (( qName.equalsIgnoreCase("Attribute") )) {
        	// Found an attribute: remember values
            String aname = atts.getValue("name").trim();
            String avalue = atts.getValue("value").trim();
            //System.out.println("aname. " + aname + " avalue: " + avalue);
            String type = (String) model.getVariables().get(aname);
            
            // Split up multi-value entries based on the separator (#)
            // Convert elements to double if defined as doubles or ints
            if (avalue.indexOf(Globals.getSeparator())!= -1) {
            	StringTokenizer st = new StringTokenizer(avalue,Globals.getSeparator());
            	List values = new ArrayList();
            	// Separate multi-valued elements
            	while (st.hasMoreTokens()) {
            		Object v = st.nextToken();
            		if (type.equals("double[]") || type.equals("int[]")) {
	            		double d = 0;
	            		try {
	            			// Only use doubles.
	            			d= Double.parseDouble((String)v);
	            			v = new Double(d);
	            		}
	            		// Just add a String if it did not work
	            		catch (Exception e) {}
	            		System.out.println("adding to list: " + v + " " + v.getClass());
	            		values.add(v);
            		}
            		else {
            			values.add(v);
            		}
            		if (type.equals("double[]") || type.equals("int[]")) {
	                	// Convert into an array
	                	Double[] dv = new Double[values.size()];
	                	for (int i=0;i<values.size();i++) {dv[i] = (Double)values.get(i);}
	                	currentProduct.put(aname,dv);
            		}
            		else {
            			String[] sv = new String[values.size()];
	                	for (int i=0;i<values.size();i++) {sv[i] = (String)values.get(i);}
	                	currentProduct.put(aname,sv);
            		}
            	}
            	
            }
            // Single value treatment. Try to convert to double if numeric
            else {
            	if (type.equals("int") || type.equals("double")) {
            		if (avalue.length() != 0)
            		{
            				double d = Double.parseDouble(avalue);
                    		currentProduct.put(aname,new Double(d));
            		}
            	}
            	else {
            		currentProduct.put(aname,avalue);
            	}
            }
        }
    }
    public void endElement (String uri, String name, String qName, Attributes atts) {
    }
    public void endDocument () {
    	// Store the last product
    	products.add(currentProduct.clone());
    }


}
