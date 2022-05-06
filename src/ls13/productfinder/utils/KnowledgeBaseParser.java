package ls13.productfinder.utils;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ls13.productfinder.FilterRule;
import ls13.productfinder.InfoTextRecord;
import ls13.productfinder.ProductFinderException;
import ls13.productfinder.rules.BusinessRule;

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

public class KnowledgeBaseParser extends DefaultHandler {
	/*
	 * Creates a new parser instance from an input source 
	 */
	public KnowledgeBaseParser(InputSource is) {
        this.is = is;
        
	}
    private CharArrayWriter contents = new CharArrayWriter();
    public void characters (char ch[], int start, int length) {
        contents.write(ch,start,length);
    }
    
    // A list to remember and log parse errors.
    // if the list is not empty at the end of the parse process, throw an exception
    List parseErrors = new ArrayList();
        

	// the temp list of variables (mappings to the type
	HashMap variables = new HashMap();
	// gives access to variable list
	public HashMap getVariables() {return variables;}
	// temp list of filters
	
	// the temp list to store the variable enumerated domains
	// maps variables to lists of allowed values
	HashMap variableDomains = new HashMap();

	// gives access to variable domain list
	public HashMap getVariableDomains() {return variableDomains;}
	// temp list of filters
	
	// A list to store the current domain values for a variable;
	List currentVariableDomain = new ArrayList();
	
	
	
	List filters = new ArrayList();
	// give access to the filters
	public List getFilters() {return filters;}
	
	// temp map of infotexts
	HashMap infotexts = new HashMap();
	// access to the infotexts
	public HashMap getInfoTexts() {return infotexts;}
	// hashmap to store default texts, access for it
	public HashMap getDefaultTexts() {return this.defaultTexts;}
	
	// Infotext buffering
	private String infoTextName = null;
	private List textvariants = null;
	HashMap defaultTexts = new HashMap();
	
	// The utility function
	String utilityFunction = null;
	// Flag to remember whether dynamic or static utilities should be used
	boolean dynamicUtilities = false; 
	
	// Similarities
	String similarityFunction;
	public String getSimilarityFunction() {return similarityFunction;}
	
	// Business rules: List of Business rule objects
	List businessRules = new ArrayList();
	public List getBusinessRules() {
		return businessRules;
	}
	// A handle to the current rule to be parsed
	BusinessRule currentRule;
	
	// parser specific 
	XMLReader xr; 
	InputSource is;
    private Locator locator = null;

	/**
	 * Parses the knowledge base file
	 * @throws IOException on file open errors
	 * @throws ProductFinderException on content errors
	 */
	public void parse() throws IOException, ProductFinderException {
        this.xr = new org.apache.xerces.parsers.SAXParser();
		this.xr.setContentHandler(this);
		this.xr.setErrorHandler(this);
		this.xr.setEntityResolver(this);
		try {
			this.xr.setFeature("http://xml.org/sax/features/validation",false);
		} catch (Exception e) {}
		
		try {
			this.xr.parse(is);
			//System.out.println("Domains. " + this.variableDomains);
			
			if (this.parseErrors.size() != 0) {
				throw new ProductFinderException("Errors occurred during document parsing: " + this.parseErrors);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			System.out.println("ERROR at line: " + this.locator.getLineNumber());
			e.printStackTrace();
			throw new ProductFinderException("[ERROR]: Error when parsing knowledge base: " + e.getMessage());
		}
	}
	
	/**
	 * For rememebering line numbers in case of errors
	 */
    public void setDocumentLocator(Locator l) {
        this.locator = l;
    }
    
    
    /**
     * Local variable to store varname for domain parsing
     * dj 13-10-09
     */
    String currentVarname = "";

	/**
	 * Simples form of parsing. Whenever element of certain type is encountered, store the
	 * information in the model
	 */
    public void startElement (String uri, String name, String qName, Attributes atts) {
    	/* Read the variables */
    	if ( qName.equalsIgnoreCase("Variable") ) {
            String varname = atts.getValue("name").trim();
            String vartype = atts.getValue("type").trim();
            
            // dj 13-10-09: remember varname for domain parsing
            // and reset the list of possible values
            currentVarname = varname;
            currentVariableDomain = new ArrayList();
            
            // register the variable
            variables.put(varname,vartype);
        }
    	// Filters go to the filters list
    	else if ( qName.equalsIgnoreCase("FilterRule") ) {
    		String thename = atts.getValue("name").trim();
            String condition = atts.getValue("condition").trim();
            String filter = atts.getValue("filter").trim();
            int priority = Integer.parseInt(atts.getValue("priority").trim());
            String explanation = atts.getValue("explanation").trim();
            String excuse = atts.getValue("excuse").trim();
            
            FilterRule fr = new FilterRule(thename,condition, filter, priority, explanation, excuse);
            filters.add(fr);
        }
    	else if ( qName.equalsIgnoreCase("InfoText") ) {
    		
    		// Store the last one
    		if (this.infoTextName != null) {
    			this.infotexts.put(this.infoTextName,this.textvariants);
    		}
    		// Create a new one
    		this.infoTextName = atts.getValue("name");
    		this.defaultTexts.put(this.infoTextName,atts.getValue("defaulttext"));
    		this.textvariants = new ArrayList();
    	}
    		// Variant of an infotext
    	else if ( qName.equalsIgnoreCase("TextVariant") ) {
    		this.textvariants.add(new InfoTextRecord(
    									atts.getValue("condition"),
    									atts.getValue("text")));
    	}
		// Utility function begin
    	else if ( qName.equalsIgnoreCase("UtilityFunction") ) {
    		String isdynamic = atts.getValue("type");
    		if (isdynamic != null && "dynamic".equalsIgnoreCase(isdynamic)) {
    			this.dynamicUtilities = true;
    		}
            this.contents.reset();
    	}
    	else if ( qName.equalsIgnoreCase("SimilarityFunction") ) {
            this.contents.reset();
    	}
    	else if ( qName.equalsIgnoreCase("BusinessRule") ) {
    		String brname = atts.getValue("name");
    		if (brname == null) {
    			brname ="Anonymous rule";
    		}
    		this.currentRule = new BusinessRule(brname);
    	}
    	else if ( qName.equalsIgnoreCase("If") ) {
    		if (this.currentRule != null) {
    			this.contents.reset();
    		}
    	}
    	else if ( qName.equalsIgnoreCase("Then") ) {
    		if (this.currentRule != null) {
    			this.contents.reset();
    		}
    	}
    	// dj 13-10-09
    	else if (qName.equalsIgnoreCase("PossibleValue")) {
			this.contents.reset();
    	}
   	}
    
    /**
     * Function to react on ending elements
     */
    public void endElement(String namespaceURI, String localName, String qName) {
    	if (qName.equalsIgnoreCase("UtilityFunction")) {
    		this.utilityFunction = new String(this.contents.toString());
    	} else if (qName.equalsIgnoreCase("SimilarityFunction")) {
    		//System.err.println("sim. found: " + this.contents.toString());
    		this.similarityFunction = new String(this.contents.toString());
    	}
    	else if (qName.equalsIgnoreCase("If")) {
    		if (this.currentRule != null) {
        		this.currentRule.setCode_if(new String(this.contents.toString().trim()));
    		}
    	}
    	else if (qName.equalsIgnoreCase("Then")) {
    		if (this.currentRule != null) {
        		this.currentRule.setCode_then(new String(this.contents.toString().trim()));
    		}
    	}
    	else if (qName.equalsIgnoreCase("BusinessRule")) {
    		if (this.currentRule != null) {
        		this.businessRules.add(this.currentRule);
        		this.currentRule = null;
    		}
    	}
    	// dj 13-10-09
    	// Collect the possible variable values 
    	else if (qName.equalsIgnoreCase("PossibleValue")) {
    		String contents = this.contents.toString();
			//System.out.println("value found: " + contents);
			String type = (String) variables.get(this.currentVarname);
			//System.out.println("current var and type is " + this.currentVarname + " " +  type);
			if (type.toLowerCase().startsWith("string")) { 
				this.currentVariableDomain.add(new String(contents));
			}
			else if (type.toLowerCase().startsWith("double")) {
				try {
					double dval = Double.parseDouble(contents);
					this.currentVariableDomain.add(new Double(dval));
				}
				catch (Exception e) {
					// TODO: proper error handling with error logging and exceptions.
					String error = "Domain value " + this.contents.toString() + " for variable " + this.currentVarname + 
							" cannot be converted to Double";
					System.err.println(error);
					this.parseErrors.add(error);
				}
			}
    	}
    	
    	// if there have been value definitions. add them to the system
    	else if (qName.equalsIgnoreCase("Variable")) {
    		if (this.currentVariableDomain.size() > 0) {
        		this.variableDomains.put(this.currentVarname,this.currentVariableDomain);
        		this.currentVarname = null;
        		this.currentVariableDomain = null;
    		}
    	}

    	
    } 
    
    /**
     * Finally, add all open elements, which have not already added during parsing.
     */
    public void endDocument() {
    	// add the last infotext, which was not yet added to the list
		if (this.infoTextName != null) {
			this.infotexts.put(this.infoTextName,this.textvariants);
		}
		
		//System.out.println("Business rules: " + this.businessRules);
		
    }
	public String getUtilityFunction() {
		return utilityFunction;
	}
}
