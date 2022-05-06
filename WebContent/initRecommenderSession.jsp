
<%@ page import = "ls13.productfinder.*" %>
<% 
	//-----------------------------------------------------------------------------------
	// Initialization (loading of data) done on first page visit
	//-----------------------------------------------------------------------------------
	// First step: Make sure that the model, which is shared by all clients
	// is already initialized.
	// Our strategy is to simply have one shared instance in the application
	RecommenderEngine engine = (RecommenderEngine) pageContext.getAttribute(
														"R_ENGINE",
														PageContext.APPLICATION_SCOPE);
	if (engine == null) {
		// Load data from defined sources in the web application
		String path = ((HttpServletRequest)pageContext.getRequest()).getRealPath(".");
		path += "/data/";
		engine = new RecommenderEngine(path + "/rules.xml",path + "/digicams.xml");
		engine.loadModel();
		// Store it in the global scope
		pageContext.setAttribute("R_ENGINE", engine, PageContext.APPLICATION_SCOPE);
	}
	// Next, create a user session, if we do not already have one. This time, we store
	// the object in the session scope for this user.
	RecommenderSession userSession = (RecommenderSession) pageContext.getAttribute(
														"R_SESSION",
														PageContext.SESSION_SCOPE);
	// No session yet available: Create one and store it.
	if (userSession == null) {
		// create a new one for this engine and store it in the scope
		userSession = new RecommenderSession(engine);
		pageContext.setAttribute("R_SESSION", userSession, PageContext.SESSION_SCOPE);
	}
%>    