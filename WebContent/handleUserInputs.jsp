<%@ page import = "ls13.productfinder.*" %>
<% 	
	// Iterate over the model variables and set the values.
	HashMap variables = (HashMap )userSession.getEngine().getVariables();
	Iterator it = variables.keySet().iterator();
	String varname;
	String vartype;
	String singleValue;
	String[] multipleValues;
	while (it.hasNext()) {
		varname = (String) it.next();
		vartype = (String) variables.get(varname);
		// Get the values from the request (depends on type - array or not) and
		// set values for session.
		if (vartype.endsWith("[]")) {
			multipleValues = request.getParameterValues(varname);
			if (multipleValues != null) {
				userSession.setInput(varname,multipleValues);
			}
		}
		else {
			singleValue = request.getParameter(varname);
			if (singleValue != null) {
				userSession.setInput(varname,singleValue);
			}
		}
	}
	// Remember the user session object
	pageContext.setAttribute("USER_SESSION",userSession,PageContext.SESSION_SCOPE);
	
	// Handle the -sorting.
	String SORT_BY_DEFAULT = "p_price";
	String SORT_ORDER_DEFAULT = "ASC";
	String SORT_BY = request.getParameter("SORT_BY");
	if (SORT_BY != null) {
		if (!"p_price".equals(SORT_BY)) { 
			SORT_ORDER_DEFAULT = "DESC"; 
			SORT_BY_DEFAULT = SORT_BY;
		}
	}
	// nothing given - try the last sortorder
	else {
		SORT_BY = (String)pageContext.getAttribute("SORT_BY", PageContext.SESSION_SCOPE);
		if (SORT_BY != null) {
			if (!"p_price".equals(SORT_BY)) { 
				SORT_ORDER_DEFAULT = "DESC"; 
				SORT_BY_DEFAULT = SORT_BY;
			}
		}
	}
	pageContext.setAttribute("SORT_BY",SORT_BY_DEFAULT, PageContext.SESSION_SCOPE);
	pageContext.setAttribute("SORT_ORDER",SORT_ORDER_DEFAULT, PageContext.SESSION_SCOPE);

	
%>