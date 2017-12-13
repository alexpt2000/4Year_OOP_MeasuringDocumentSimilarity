<%@ include file="includes/header.jsp" %>

<div class="animated bounceInDown" style="font-size:32pt; font-family:arial; color:#990000; font-weight:bold">Document Comparison Service</div>

</p>&nbsp;</p>&nbsp;</p>

<table width="600" cellspacing="0" cellpadding="7" border="0">
	<tr>
		<td valign="top">

			<form bgcolor="white" method="POST" enctype="multipart/form-data" action="doProcess">
				<fieldset>
					<legend><h3>Specify Details</h3></legend>

					<b>Document Title :</b><br>
					<input name="txtTitle" type="text" size="50"/>
					<p/>
					<input type="file" name="txtDocument"/>
					<center><input type="submit" value="Compare Document"></center>
				</fieldset>							
			</form>	

		</td>
	</tr>
</table>
<%@ include file="includes/footer.jsp" %>

