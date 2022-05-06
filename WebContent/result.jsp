
<%@ page import = "ls13.productfinder.*" %>
<%@ page import = "java.util.*" %>
    
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>JPFinder Result</title>
<LINK REL=StyleSheet HREF="jpfinder.css" TYPE="text/css">
</head>

<% 
	//-----------------------------------------------------------------------------------
	// Initialization
	//-----------------------------------------------------------------------------------
	// After that include, the variable "userSession" of type RecommenderSession is initialized
    // and a global, shared engine has been initialized if not already done
%>
<%@ include file='initRecommenderSession.jsp'%>
<%  
	//-----------------------------------------------------------------------------------
	// Input handling
	//-----------------------------------------------------------------------------------
	// Get the user inputs from the request and store them to the model
	// Could be done with bean or whatever other technology. This is only a simple approach
%>
<%@ include file='handleUserInputs.jsp'%>

<%
	//-----------------------------------------------------------------------------------
	// Result computation
	//-----------------------------------------------------------------------------------
	// Only re-compute results if required
	String DO_COMPUTATION = (String)request.getParameter("DO_COMPUTATION");
	if (!"false".equalsIgnoreCase(DO_COMPUTATION)) {
		System.out.println("RECOMPUTATION");
		userSession.computeRecommendationResult(1);
	}
	
	
	userSession.sortResultBy(SORT_BY_DEFAULT, SORT_ORDER_DEFAULT);
	RecommendationResult result = userSession.getLastResult();
	
	// Get the best product
	HashMap bestProduct = new HashMap();
	if (result.getMatchingProducts().size() > 0) {
		bestProduct = (HashMap) result.getMatchingProducts().get(0);
	}
	
%>

<body>
	<form name="f" action="result.jsp">
	<!--  some hidden inputs to process interactions -->
	<input type="hidden" name="DO_COMPUTATION">
	
	<table width="100%" cellpadding="0" cellspacing="0">
		<tr>	<!--  THE HEADER ROW -->
			<td style="border-top:1px dashed black;"> &nbsp;
			</td>
		</tr>
		<tr>	<!--  THE HEADER ROW -->
			<td style="font-size:16px;">
				<span style="font-size:18px;color:#cc0000;">FUNONY </span>Camera Advisor - Here is my proposal for you!
			</td>
		</tr>
		<tr>	<!--  THE HEADER ROW -->
			<td style="border-bottom:1px dashed black;"> &nbsp;
			</td>
		</tr>
		<tr>
			<td>
				<!--  Inner table for showing the results -->
				<table width=100%>
					<tr>
						<td style="padding-top:30px;width:120px;vertical-align:top;" >
							<img src="img/cam1.jpg" width="120">
						</td>
						<td colspan=3 ALIGN="LEFT">	<!--  product details -->
							<table WIDTH=100%>
								<tr>
									<td></td>
									<td class=PLABEL >
										<%=bestProduct.get("p_name")%>
									</td>
								</tr>
								<tr>
									<td class=FEATURE-LABEL>
										Description:
									</td>
									<td>
										<%=bestProduct.get("p_summary")%>
									</td>
								</tr>
								<tr>
									<td class=FEATURE-LABEL>
										Technical details:
									</td>
									<td>
										<%=bestProduct.get("p_resolution")%> Megapixels, <%=bestProduct.get("p_lcd_size")%> LCD Display, 
										<%=bestProduct.get("p_optical_zoom")%>-times optical zoom.
									</td>
								</tr>
								<tr>
									<td class=FEATURE-LABEL>
										Price:
									</td>
									<td>
										<span style="font-weight:bold;">EUR <%=bestProduct.get("p_price")%></span>
									</td>
								</tr>
								
							</table>
						</td>
					</tr>
					<tr>
						<td></td>
					</tr>
					<tr>
						<td class=FEATURE-LABEL align=right>
							Price/value rating: 
							<% Double p_rating = (Double)bestProduct.get("p_rating");
							   if (p_rating == null) { p_rating = new Double(3);} // some default
							%>
						</td>
						<td align=left>
							<% for (int r=0;r<p_rating.intValue();r++) { %>
							<img src="img/icon_star_hi.gif">
							<% }%>
							<% for (int r=0;r<5-p_rating.intValue();r++) { %>
								<img src="img/icon_star_lo.gif">
							<% }%>
						</td>
						<td class=FEATURE-LABEL align=right>
							Customer rating: 
						</td>
						<td align=left>
							<img src="img/icon_star_hi.gif">
							<img src="img/icon_star_hi.gif">
							<img src="img/icon_star_hi.gif">
							<img src="img/icon_star_lo.gif">
							<img src="img/icon_star_lo.gif">
						</td>
					</tr>
					<tr>
						<td colspan=4 style="border-bottom:1px dashed black;"> &nbsp;
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr><td>&nbsp;</td></tr>
		<tr>
			<td>
				<table width=100%>
				<tr>
					<td width=70% style="font-size:10px;text-align:justify;">
						<font style="font-weight:bold;">Why do I recommend this model: </font> <br>
						<% 
						// Assemble the information obtained from the recommender
						System.out.println("Applied:" + result.getAppliedFilters());
						System.out.println("relaxed:" + result.getRelaxedFilters());
						
						
						String explanation = userSession.getInfoText("interests") + " ";
						for (int i=0;i<result.getAppliedFilters().size();i++) {
							explanation += ((FilterRule)result.getAppliedFilters().get(i)).getExplanation() + " ";
						}
						if (result.getRelaxedFilters().size() > 0) {
							explanation += "<br><br>" + "Unfortunately, none of the products fulfils " +
									"all of your requirements, maybe because to all product data is available: ";
							for (int i=0;i<result.getRelaxedFilters().size();i++) {
								explanation += ((FilterRule)result.getRelaxedFilters().get(i)).getExcuse();
							}
						}
						out.print(explanation);
						%>
						
					</td>
					<td style="vertical-align:top;padding-left:10px;">
						<table width=100% style="border-left:1px dashed #4f4f4f;height:100%;" >
							<tr>	
								<td style="padding-left:20px;font-size:10px;">
									Get the proposal with the best: 
								</td>
							</tr>
							<tr>	
								<td style="padding-left:20px;">
									<select name="SORT_BY" style="font-size:10px;width:130px;" 
										onchange="document.f.DO_COMPUTATION.value='false';document.f.submit();">
										<% System.out.println(pageContext.getAttribute("SORT_BY", PageContext.SESSION_SCOPE)); %>
										<option value="p_price" <%="p_price".equals(pageContext.getAttribute("SORT_BY", PageContext.SESSION_SCOPE)	)?"selected":" "%> 
										>
											Price
										</option>
										<option value="p_resolution" <%="p_resolution".equals(pageContext.getAttribute("SORT_BY", PageContext.SESSION_SCOPE))?"selected":" "%> 
										>
											Resolution
										</option>
										<option value="p_rating" <%="p_rating".equals(pageContext.getAttribute("SORT_BY", PageContext.SESSION_SCOPE))?"selected":" "%> 
										>
											Utility
										</option>
										<option value="<%=ls13.productfinder.UtilityCalculator.UTILITY_DENOMINATOR%>" 
											<%=ls13.productfinder.UtilityCalculator.UTILITY_DENOMINATOR.
													equals(pageContext.getAttribute("SORT_BY", PageContext.SESSION_SCOPE))?"selected":" "%> 
										>
											Price/value rating
										</option>
									</select>
								</td>
							</tr>
							<tr>	
								<td style="padding-left:20px;font-size:7px;">
									&nbsp;
								</td>
							</tr>
							<%
								if (result.getMatchingProducts().size() > 1) { 
							%>
							<tr>	
								<td style="padding-left:20px;">
									<a style="font-size:10px;" href="moreItems.jsp">Show all matching</a> 
								</td>
							</tr>
							<%
								}
							%>
							<tr>	
								<td style="padding-left:20px;">
									<a style="font-size:10px;" href="moreItems.jsp?PRICE=<%=bestProduct.get("p_price")%>&DIRECTION=CHEAPER">
												A bit cheaper models</a> 
								</td>
							</tr>
							<tr>	
								<td style="padding-left:20px;">
									<a style="font-size:10px;" href="moreItems.jsp?PRICE=<%=bestProduct.get("p_price")%>&DIRECTION=COSTLIER">
												A bit costlier models</a> 
								</td>
							</tr>
							<tr>	
								<td style="padding-left:20px;">
									<a style="font-size:10px;" href="index.jsp"><< Modify requirements</a> 
								</td>
							</tr>
						</table>
					</td>
				</tr>
				</table>
				
			</td>
		</tr>
	</table>
	</form>
</body>
</html>