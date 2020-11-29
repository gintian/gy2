
<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*,com.hrms.struts.constant.WebConstant,com.hrms.struts.valueobject.UserView"%>

<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/gz/salary.js"></script>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
 <%
	int i=0;
	Calendar d=Calendar.getInstance();
	int year=d.get(Calendar.YEAR);
	int month=d.get(Calendar.MONTH)+1;
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String hcmflag="";
	if(userView != null){
	  hcmflag=userView.getBosflag();
	}
	
%>
<style type="text/css">
	#scroll_box {
        border: 0px solid #ccc;
        height: 300px;    
        overflow: auto;            
        margin: 1em 0;
	}
</style>
<hrms:themes />
<html:form action="/gz/gz_accounting/batchimport"> 


	<div id='wait' style='position:absolute;top:40;left:70;display:none;'>
		<table border="1" width="67%" cellspacing="0" cellpadding="4"   class="table_style" height="87" align="center">
			<tr>
				<td  class="td_style"  height=24>
					<bean:message key="label.gz.importing"/>...
				</td>
			</tr>
			<tr>
				<td style="font-size:12px;line-height:200%" align=center>
					<marquee  class="marquee_style"   direction="right" width="300" scrollamount="5" scrolldelay="10"  >
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
<table align="center" width="515px;">
<%}else{ %>
<table align="center" width="515px;" style="margin-top:-5px;">
<%} %>


<tr>
<td>

  <fieldset align="center"  width="100%">
   <legend><bean:message key="label.gz.batch.import"/></legend>
	<table width="100%" border="0" cellspacing="0" align="center" cellpadding="0">
	<% if(request.getParameter("fromModel")==null||!(request.getParameter("fromModel").equals("ff")||request.getParameter("fromModel").equals("sp"))){ %>
	  <tr>
	   <td align="center" style="padding-top: 5px;">  		
	      <fieldset align="center" style="width:98%;">
   			 <legend><bean:message key="label.gz.import.type"/></legend>	
				<table border="0" cellspacing="0" cellpadding="0" align="center">
					<tr>
			 		   <td>   	
							<html:radio name="batchForm" property="importtype" value="1"><bean:message key="label.gz.mc"/></html:radio>			 		   			 
			 		   </td>
			 		   <td>   	
							<html:radio name="batchForm" property="importtype" value="2"><bean:message key="label.gz.premc"/></html:radio>			 		   			 
			 		   </td>	
			 		   <td nowrap >
			 		   		<html:radio name="batchForm" property="importtype" value="4">
			 		   		<select name='year' >
			 		   			<% for(int a=year+5;a>year-15;a--){
			 		   			 	if(a==year)
			 		   			 		out.println("<option value='"+a+"' selected  >"+a+"</option>"); 
			 		   			 	else
				 		   			 	out.println("<option value='"+a+"' >"+a+"</option>"); 
			 		   			 }
			 		   			  %>
			 		   		</select><bean:message key="datestyle.year"/>
			 		   		<select name='month' >
			 		   			<% for(int a=1;a<13;a++){
			 		   			 	if(a==month)
			 		   			 		out.println("<option value='"+a+"' selected  >"+a+"</option>"); 
			 		   			 	else
				 		   			 	out.println("<option value='"+a+"' >"+a+"</option>"); 
			 		   			 }
			 		   			  %>
			 		   		</select><bean:message key="datestyle.month"/>
			 		   		<select name='count' >
			 		   			<% for(int a=1;a<=20;a++){
				 		   			 	out.println("<option value='"+a+"' >"+a+"</option>"); 
			 		   			 }
			 		   			  %>
			 		   		</select><bean:message key="hmuster.label.count"/>
			 		   		</html:radio>
			 		   </td>		 		
			 		   <td>   	
							<html:radio name="batchForm" property="importtype" value="3"><bean:message key="label.gz.archive"/></html:radio>			 		   			 
			 		   </td>	
			 		      		 		   
			 		</tr>
			 	</table>
   		 </fieldset>	
   	   </td>
   	  </tr>	
   	  <% }else{ %>
   	  
   	  <input type='hidden' name='importtype' value='3' />
   	  
   	  <% } %>
   	  
	<tr>
	 <td width="80%">
		<table border="0" cellspacing="0" cellpadding="0" align="center">
		<tr>
 		   <td style="padding-top: 5px;">
 
			<bean:message key="label.gz.import.item"/> 		   
 		   </td>
 		</tr>		
		<tr>
 		   <td align="center">
 		    	<div id="scroll_box" style="border:1px solid;">
				<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0">
				   	  <thead>
			           <tr class="fixedHeaderTr1">
				            <td align="center" class="TableRow" style="border-top:none;border-left: none;border-right: none;" nowrap >
								<input type="checkbox" name="allSelect" onclick="allSel(this);"/>
					    	</td>         
				            <td align="center" class="TableRow" style="border-top:none;" nowrap width="380">
				            <logic:equal name="batchForm" property="gz_module" value="0">
								<bean:message key="label.gz.gzitem"/>&nbsp;
							</logic:equal>
							<logic:equal name="batchForm" property="gz_module" value="1">
								<bean:message key="label.gz.insitem"/>&nbsp;
							</logic:equal>
					    	</td>
			           </tr>
				   	  </thead>
				          <hrms:extenditerate id="element" name="batchForm" property="formulalistform.list" indexes="indexes"  pagination="formulalistform.pagination" pageCount="300" scope="session">
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
				            <td align="center" class="RecordRow" style="border-left: none;border-right: none;border-top: none;" nowrap>
								<input type="checkbox" name="chk" value='<bean:write name="element" property="itemid" filter="true"/>'> 
					    	</td>            
				            <td align="left" class="RecordRow" style="border-top: none;" nowrap>
				                <bean:write name="element" property="itemdesc" filter="true"/>&nbsp;
					    	</td>
				          </tr>
				        </hrms:extenditerate>
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
				<% if(request.getParameter("opt")!=null&&request.getParameter("opt").equals("sp")){ %>
				<button name="import" Class="mybutton" onclick="run_batch_import_history('${batchForm.salaryid}','<%=(request.getParameter("ym"))%>','<%=(request.getParameter("count"))%>')"><bean:message key="button.ok"/></button>&nbsp;&nbsp;
				<% }else{ %>
				<button name="import" Class="mybutton" onclick="run_batch_import('${batchForm.salaryid}')"><bean:message key="button.ok"/></button>&nbsp;&nbsp;
				<% } %>
				<button name="cancel" Class="mybutton" onclick="window.close();"><bean:message key="button.cancel"/></button>
			  </td>
	    	</tr>
    	</table>
	</td>
	</tr>
</table>
</html:form>


  