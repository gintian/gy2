
<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/gz/salary.js"></script>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
 <hrms:themes />
<%UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String hcmflag="";
	if(userView != null){
	  hcmflag=userView.getBosflag();
	}
 %>
<html:form action="/gz/gz_accounting/input_gz_date"> 


	<div id='wait' style='position:absolute;top:40;left:50;display:none;'>
		<table border="1" width="67%" cellspacing="0" cellpadding="4"  class="table_style"  height="87" align="center">
			<tr>
				<td class="td_style"  height=24>
					<logic:equal  name="accountingForm" property="gz_module"  value="0">	
					<bean:message key="label.gz.create"/>
					</logic:equal>
					<logic:equal  name="accountingForm" property="gz_module"  value="1">	
					<bean:message key="label.ins.create"/>
					</logic:equal>
				</td>
			</tr>
			<tr>
				<td style="font-size:12px;line-height:200%" align=center>
					<marquee  class="marquee_style"  direction="right" width="300" scrollamount="5" scrolldelay="10"  >
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
<%if("hl".equals(hcmflag)){ %>
<table align="center"  width="392px;" >
<%}else{ %>
<table align="center"  width="392px;" style="margin-top:-5px;margin-left:-3px;">
<%} %>

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
		<td valign="middle">
			<table width="90%" border="0" cellspacing="0" cellpadding="0" >
	         <tr> 
	          <td align="right" style="width:130"></td>                                 
	          <td valign="middle">&nbsp;&nbsp;
	          	<html:text name="accountingForm"  property="theyear" style="width:40;text-align:center;vertical-align:middle;padding-top:0px;" styleClass="inputtext"/>                                      
	          </td>
	          <td valign="middle" align="left">
	             <table border="0" cellspacing="2" cellpadding="0" >
		      		<tr><td><button id="y_up" class="m_arrow" onclick='inc_year($("theyear"));'>5</button></td></tr>
		      		<tr><td><button id="y_down" class="m_arrow" onclick='dec_year($("theyear"));'>6</button></td></tr>
	             </table>
	          </td>
			  <td><bean:message key="hmuster.label.year"/></td>	          
	   				  <td valign="middle"> 
	       	  			<html:text name="accountingForm" property="themonth" style="width:40;text-align:center;vertical-align:middle;padding-top:0px;" styleClass="inputtext"/>                     
	         		 </td>
	          		 <td valign="middle" align="left">
	             		<table border="0" cellspacing="2" cellpadding="0">
		      				<tr><td><button id="m_up" class="m_arrow" onclick='inc_month($("themonth"));'>5</button></td></tr>
		      				<tr><td><button id="m_down" class="m_arrow" onclick='dec_month($("themonth"));'>6</button></td></tr>
	             		</table>
	                 </td>	
	                 <td><bean:message key="hmuster.label.month"/></td>	          
	          <td align="left"></td>
	          </tr>
	          </table>
    	  </td>
		</tr>
		<tr><td>&nbsp;</td></tr>
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
				<button name="new" Class="mybutton" onclick='submit_gz_table($("theyear"),$("themonth"),"${accountingForm.salaryid}","${accountingForm.gz_module}");'><bean:message key="button.ok"/></button>
				<button name="cancel" Class="mybutton" onclick="window.close();"><bean:message key="button.cancel"/></button>
			  </td>
	    	</tr>
    	</table>
	</td>
	</tr>
</table>

<input type="hidden" name="finalDate" value='${accountingForm.finalDate}' />

</html:form>


  