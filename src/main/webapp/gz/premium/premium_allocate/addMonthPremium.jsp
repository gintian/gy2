<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="javascript" src="monthPremium.js"></script>
<link rel="stylesheet" href="/css/css1.css" type="text/css"> 
<hrms:themes />
<html:form action="/gz/premium/premium_allocate/monthPremiumList"> 
<br>
<br>
<br>

	<div id='wait' style='position:absolute;top:40;left:50;display:none;'>
		<table border="1" width="67%" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
			<tr>
				<td  class="td_style"  height=24>
					<bean:message key="label.premium.create"/>
				</td>
			</tr>
			<tr>
				<td style="font-size:12px;line-height:200%" align=center>
					<marquee  class="marquee_style"  direction="right" width="300" scrollamount="5" scrolldelay="10" >
						<table cellspacing="1" cellpadding="0">
							<tr height=8>
								<td bgcolor=#3399FF width=8></td>
								<td></td>
								<td bgcolor=#3399FF width=8></td>
								<td></td>
								<td bgcolor=#3399FF width=8></td>
								<td></td>
								<td bgcolor=#3399FF width=8></td>
								<td></td>
							</tr>
						</table>
					</marquee>
				</td>
			</tr>
		</table>
	</div>
<table align="center"  width="80%">
<tr>
<td>

  <fieldset align="center" style="width:100%;">
   <legend><bean:message key="label.gz.defineAppdate"/></legend>
	<table border="0" cellspacing="0" align="left" cellpadding="0">
	<tr><td>&nbsp;</td></tr>
	<tr>
	<td >
		<table border="0" cellspacing="0" cellpadding="0">
		<tr >
		<td >
			<table width="90%" border="0" cellspacing="0" cellpadding="0">
	         <tr> 
	          <td align="right"></td>                                 
	          <td valign="middle">&nbsp;&nbsp;
	          	<html:text name="monthPremiumForm"  property="year" style="width:40"/>                     
	          <td valign="middle">                      
	          </td>
	          <td valign="middle" align="left">
	             <table border="0" cellspacing="2" cellpadding="0" >
		      		<tr><td><button id="y_up" class="m_arrow" onclick='inc_year($("year"));'>5</button></td></tr>
		      		<tr><td><button id="y_down" class="m_arrow" onclick='dec_year($("year"));'>6</button></td></tr>
	             </table>
	          </td>
			  <td><bean:message key="hmuster.label.year"/></td>	          
	   				  <td valign="middle"> 
	       	  			<html:text name="monthPremiumForm" property="month" style="width:40"/>                     
	         		 </td>
	          		 <td valign="middle" align="left">
	             		<table border="0" cellspacing="2" cellpadding="0">
		      				<tr><td><button id="m_up" class="m_arrow" onclick='inc_month($("month"));'>5</button></td></tr>
		      				<tr><td><button id="m_down" class="m_arrow" onclick='dec_month($("month"));'>6</button></td></tr>
	             		</table>
	                 </td>	
	                 <td><bean:message key="hmuster.label.month"/></td>	          
	          <td align="left"></td>
	          </tr>
	          </table>
    	  </td>
		</tr>
		</table>    
    </td>     
	</tr>
	</table>
	</fieldset>
</td>
</tr>
	<tr>
	<td>
		<table align="center">
    		<tr >
		  	  <td>
				<button name="new" Class="mybutton" onclick='generateData($("year"),$("month"),"${monthPremiumForm.operOrg}","${monthPremiumForm.orgsubset}","${monthPremiumForm.salaryid}");'><bean:message key="button.ok"/></button>&nbsp;&nbsp;&nbsp;&nbsp;
				<button name="cancel" Class="mybutton" onclick="window.close();"><bean:message key="button.cancel"/></button>
			  </td>
	    	</tr>
    	</table>
	</td>
	</tr>
</table>

</html:form>


  