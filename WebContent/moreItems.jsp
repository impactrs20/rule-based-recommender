 <%@ page import = "ls13.productfinder.*" %>
<%@ page import = "java.util.*" %>
    
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>JPFinder Result</title>
<LINK REL=StyleSheet HREF="jpfinder.css" TYPE="text/css">

<title>Insert title here</title>
</head>
<body>
<%
	RecommenderSession userSession = (RecommenderSession) pageContext.getAttribute("USER_SESSION",PageContext.SESSION_SCOPE);
%>

	<table width="100%" cellpadding="0" cellspacing="0">
		<tr>	<!--  THE HEADER ROW -->
			<td style="border-top:1px dashed black;"> &nbsp;
			</td>
		</tr>
		<tr>	<!--  THE HEADER ROW -->
			<td style="font-size:16px;">
				<span style="font-size:18px;color:#cc0000;">FUNONY </span>Camera Advisor - Here's what I have found.
			</td>
		</tr>
		<tr>	<!--  THE HEADER ROW -->
			<td style="border-bottom:1px dashed black;"> &nbsp;
			</td>
		</tr>
		<tr>	
			<td > &nbsp;
			</td>
		</tr>
		<tr>	<!--  GO BACK-->
			<td ALIGN=LEFT> 
				<a href="javascript:history.back();"> &lt;&lt;&nbsp;Go back </a>
			</td>
		</tr>
		
		<%
		RecommendationResult result = userSession.getLastResult();
		List currentProducts = result.getMatchingProducts();
		
		// Handle variations
		String CURRENT_PRICE = request.getParameter("PRICE");
		String DIRECTION = request.getParameter("DIRECTION");
		if (CURRENT_PRICE != null) {
			String query = "";
			System.out.println(DIRECTION);
			if ("CHEAPER".equals(DIRECTION)) {
				query = "p_price < " + CURRENT_PRICE + " && p_price > " + CURRENT_PRICE + " - 50";
			}
			else {
				query = "p_price > " + CURRENT_PRICE + " && p_price < " + CURRENT_PRICE + " + 50";
			}
			System.out.println("QUERY IS NOW: " + query);
			currentProducts = userSession.getProductsForExpression(query);
		}
		
		
		
		// Iterate over the products
		HashMap currentProduct = new HashMap();
		for (int i=0;i<currentProducts.size();i++) {
			currentProduct = (HashMap) currentProducts.get(i);
		%>
		
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
										<%=currentProduct.get("p_name")%>
									</td>
								</tr>
								<tr>
									<td class=FEATURE-LABEL>
										Description:
									</td>
									<td>
										<%=currentProduct.get("p_summary")%>
									</td>
								</tr>
								<tr>
									<td class=FEATURE-LABEL>
										Technical details:
									</td>
									<td>
										<%=currentProduct.get("p_resolution")%> Megapixels, <%=currentProduct.get("p_lcd_size")%> LCD Display, 
										<%=currentProduct.get("p_optical_zoom")%>-times optical zoom.
									</td>
								</tr>
								<tr>
									<td class=FEATURE-LABEL>
										Price:
									</td>
									<td>
										<span style="font-weight:bold;">EUR <%=currentProduct.get("p_price")%></span>
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
							<% Double p_rating = (Double)currentProduct.get("p_rating");
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
		<% 
		}
		%>
	</table>
</body>
</html>