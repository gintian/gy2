
<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/constant.js"></script>
<script language="JavaScript" src="/gz/premium/premium_allocate/premium.js"></script>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
 <%
	int i=0;
%>
<hrms:themes />
<html:form action="/gz/premium/premium_allocate/monthPremiumList"> 
<br>
<br>
<br>

	<div id='wait' style='position:absolute;top:60;left:60;display:none;'>
		<table border="1" width="67%" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
			<tr>
				<td  class="td_style"  height=24>
					<bean:message key="org.autostatic.mainp.calculation.wait"/>
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
<table align="center"  width="80%">
<tr>
<td>

  <fieldset align="center" style="width:100%;">
   <legend><bean:message key="label.gz.select.formula"/></legend>
	<table width="100%" border="0" cellspacing="0" align="left" cellpadding="0">
	<tr><td>&nbsp;</td></tr>
	<tr>
	 <td width="80%">
		<table border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td>&nbsp;</td>
 		   <td>
 		    	<div style="height: 340px;overflow: auto">
				<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
				   	  <thead>
			           <tr>
				            <td align="center" class="TableRow" nowrap >
								<input type="checkbox" name="selectAll"   checked   onclick='select_All(this)'     > 	
					    	</td>         
				            <td align="center" class="TableRow" nowrap width="200">
								<bean:message key="hmuster.label.expressions"/>&nbsp;
					    	</td>
					    	<td align="center" class="TableRow" nowrap width="100">
								<bean:message key="label.org.type_org"/>&nbsp;
					    	</td>
			           </tr>
				   	  </thead>
				       <logic:iterate id="element" name="monthPremiumForm" property="formulaList" >
				          <%
				          if(i%2==0)
				          {
				          %>
				          <tr class="trShallow">
				          <%}
				          else
				          {%>
				          <tr class="trDeep">
				          <%
				          }
				          i++;          
				          %>  
				            <td align="center" class="RecordRow" nowrap>
							    <input type="checkbox" name="chk"   checked     value="<bean:write name="element" property="itemid" filter="true"/>" > 	            
					    	</td>            
				            <td align="left" class="RecordRow" nowrap>
				                <bean:write name="element" property="hzname" filter="true"/>&nbsp;
					    	</td>
					    	<td align="left" class="RecordRow" nowrap>
					    		<logic:equal value="0" name="element" property="fmode"  >
					    		&nbsp;<bean:message key="kq.item.count"/>
					    		</logic:equal>
					    		<logic:equal value="1" name="element" property="fmode"  >
					    		&nbsp;<bean:message key="menu.gz.importformula"/>
					    		</logic:equal>
					    		<logic:equal value="2" name="element" property="fmode"  >
					    		&nbsp;<bean:message key="kq.item.formula"/>
					    		</logic:equal>
					    	</td>
				          </tr>
				       </logic:iterate>
				</table>
 		   </div>
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
				<button name="compute" onclick='batchcompute("<%=(request.getParameter("year"))%>","<%=(request.getParameter("month"))%>","${monthPremiumForm.operOrg}")'  Class="mybutton" ><bean:message key="infor.menu.compute"/></button>&nbsp;&nbsp;
						  		
			  
			  	<button name="cancel" Class="mybutton" onclick="window.close();"><bean:message key="button.cancel"/></button>
			  </td>
	    	</tr>
    	</table>
	</td>
	</tr>
</table>
</html:form>

