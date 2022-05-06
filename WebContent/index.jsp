<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<style>
	SELECT {
		width:100%;
	}
</style>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<LINK REL=StyleSheet HREF="jpfinder.css" TYPE="text/css">
<title>JPFinder Inputs</title>
</head>
<body>
	<form name="f" action="result.jsp">
	<table width="100%" cellpadding="0" cellspacing="0">
		<tr>	<!--  THE HEADER ROW -->
			<td align="center" style="padding-bottom:10px;"> 
				<img src="img/cameras.gif" width=400px >
			</td>
		</tr>
		<tr>	<!--  THE HEADER ROW -->
			<td style="border-top:1px dashed black;" > &nbsp;
			</td>
		</tr>
		<tr>	<!--  THE HEADER ROW -->
			<td style="font-size:16px;">
				<span style="font-size:18px;color:#cc0000;">FUNONY </span>Camera Advisor - The way to your perfect camera!
			</td>
		</tr>
		<tr>	<!--  THE HEADER ROW -->
			<td style="border-bottom:1px dashed black;"> &nbsp;
			</td>
		</tr>
		<tr>	<!--  Some introductory text -->
			<td style="padding-top:16px;font-size:12px;font-weight:bold;padding-left:10px;padding-right:10px;"> 
				Let us find the perfect camera for you. Please fill in your basic requirements
				or preferences and I shall help you in getting the right camera in a second!
			</td>
		</tr>
		<tr>	<!--  The questions -->
			<td style="padding-top:16px;font-size:12px;padding-left:10px;padding-right:10px;"> 
				<table width=100% >
					<tr>
						<td style="padding-right:10px;"> What would you like to do with your camera?
						</td>
						<td width=50%>
							<select size="1" name="c_pref_interest">
								<option value="dunno">-- Please select --</option>
								<option value="beginner">Holiday snaps, first interest in cameras</option>
								<option value="medium">Print out high-quality pics, photo editing</option>
								<option value="expert">Take professional pics</option>
							</select>
						</td>
					</tr>
					<tr>
						<td style="padding-top:10px;padding-right:10px;"> 
							Any requirements with respect to minimum resolution?
						</td>
						
						<td>
							<select name="c_pref_min_resolution">
								<option value="dunno">-- Please select --</option>
								<option value="low">3-5 Megapixels (snapshots, Internet pics)</option>
								<option value="medium">4-6 Megapixels (adequate quality for printouts)</option>
								<option value="high">More than 6 Megapixels</option>
							</select>
						</td>
					</tr>
					<tr>
						<td style="padding-top:10px;padding-right:10px;"> 
							Some brands you would prefer?
						</td>
						
						<td>
							<select size=3 name="c_pref_manufacturer" multiple>
								<option value="sony">Sony</option>
								<option value="canon">Canon</option>
								<option value="nikon">Nikon</option>
								<option value="fujifilm">Fujifilm</option>
								<option value="olympus">Olympus</option>
								<option value="kodak">Kodak</option>
							</select>
						</td>
					</tr>
					<tr>
						<td style="padding-top:10px;padding-right:10px;"> 
							Preferred price range?
						</td>
						
						<td>
							<select name="c_pref_price">
								<option value="dunno">-- Please select --</option>
								<option value="low">100-200 Euro</option>
								<option value="medium">200-400 Euro</option>
								<option value="high">More than 500 Euro</option>
							</select>
						</td>
					</tr>
					<tr>
						<td style="padding-top:10px;padding-right:10px;"> 
							Other preferred features?
						</td>
						
						<td>
							<select size=3 multiple name="c_pref_extras">
								<option value="batteries">Should be usable with batteries</option>
								<option value="dockingstation">Docking Station required</option>
								<option value="largedisplay">Large display</option>
								<option value="opticalzoom">Above average optical zoom</option>
							</select>
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>	<!--  THE HEADER ROW -->
			<td style="border-bottom:1px dashed black;"> &nbsp;
			</td>
		</tr>
		<tr>	<!--  THE HEADER ROW -->
			<td> 
				<table width="100%">
					<tr>
						<td align="right" style="padding-top:10px;padding-right:10px;">
							<input type="submit" value="Find cameras!">
						</td>
					</tr>
				</table>
				
			</td>
		</tr>
	</table>
	</form>
</body>
</html>