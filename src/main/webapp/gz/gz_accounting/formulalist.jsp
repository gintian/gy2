<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.gz.gz_accounting.AcountingForm,java.util.*,com.hrms.struts.constant.WebConstant,com.hrms.struts.valueobject.UserView"%>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/gz/salary.js"></script>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<hrms:themes />
 <%
	int i=0;
	AcountingForm accountingForm=(AcountingForm)session.getAttribute("accountingForm"); 
	String reportSql="";
	String gz_module="";
	if(accountingForm!=null){
		reportSql=accountingForm.getReportSql();
		gz_module =accountingForm.getGz_module();
	}	
	String isPremium="no";  //月奖金管理模块调用 
	if(request.getParameter("premium")!=null)
		isPremium=request.getParameter("premium");
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String hcmflag="";
	if(userView != null){
	  hcmflag=userView.getBosflag();
	}
%>

<script language='javascript' >
var isPremium='<%=isPremium%>';

function select_All(obj)
{
	if(obj.checked)
		 batch_set_valid(1,'${batchForm.salaryid}')
	else
		 batch_set_valid(0,'${batchForm.salaryid}')
}

</script>

<html:form action="/gz/gz_accounting/formulalist"> 

	<div id='wait' style='position:absolute;top:120;left:60;display:none;'>
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
<%if("hl".equals(hcmflag)){ %>
<table align="center"  width="400px;">
<%}else{ %>
<table align="center"  width="400px;" style="margin-top:-5px;">
<%} %>

<tr>
<td>

  <fieldset align="center" style="width:100%;">
   <legend><bean:message key="label.gz.select.formula"/></legend>
	<table width="100%" border="0" cellspacing="0" align="left" cellpadding="0">
	 
	<tr>
	 <td width="90%" align='center' >
		<table border="0" cellspacing="0" cellpadding="0">
		<tr>
 		   <td style="height: 360px;overflow: auto">
 		    	<div style="height: 350px;overflow: auto" class="complex_border_color">
				<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0">
				   	  <thead>
			           <tr class="fixedHeaderTr1">
				            <td align="center" class="TableRow" style="border-top:none;border-left:none;" nowrap >
								 <input type="checkbox" name="selectAll"    onclick='select_All(this)'     > 	
					    	</td>         
				            <td align="center" class="TableRow" style="border-top:none;border-left:none;border-right: none;" nowrap width="250">
								<bean:message key="label.gz.formula"/>&nbsp;
					    	</td>
			           </tr>
				   	  </thead>
				          <hrms:extenditerate id="element" name="batchForm" property="formulalistform.list" indexes="indexes"  pagination="formulalistform.pagination" pageCount="200" scope="session">
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
				            <td align="center" class="RecordRow" style="border-top:none;border-left:none;" nowrap>
							    <input type="checkbox" name="chk"   <logic:equal name="element" property="useflag" value="1">checked</logic:equal>      value="<bean:write name="element" property="itemid" filter="true"/>" onclick ="setformulavalid(this,'<bean:write name="element" property="itemid" filter="true"/>','${batchForm.salaryid}');"> 	            
					    	</td>            
				            <td align="left" class="RecordRow" style="border-top:none;border-left:none;border-right: none;" nowrap>
				                <bean:write name="element" property="hzname" filter="true"/>&nbsp;
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
				<button name="btn_all" Class="mybutton" onclick="batch_set_valid(1,'${batchForm.salaryid}')"><bean:message key="button.all.select"/></button>
				<button name="btn_unall" Class="mybutton" onclick="batch_set_valid(0,'${batchForm.salaryid}')"><bean:message key="button.all.reset"/></button>
				<% if(request.getParameter("flag")==null){ %>
						<% if(request.getParameter("module")!=null&&request.getParameter("module").equals("sp")){ %>
				<button name="compute" Class="mybutton" onclick="gzspcompute('${batchForm.salaryid}','<%=(request.getParameter("strYm"))%>','<%=(request.getParameter("strC"))%>','<%=(request.getParameter("condid"))%>');"><bean:message key="button.computer"/></button>
						
						<% } else { %>
				<button name="compute" Class="mybutton" onclick="gzcompute('${batchForm.salaryid}');"><bean:message key="button.computer"/></button>
			  	<%
			  				}
			  	%>
			  
			  	<%		
			  	 }else{ %>
			  	<button name="compute" Class="mybutton" onclick="gzOk();"><bean:message key="button.computer"/></button>			  		
			  	<% } %>
			  	<button name="cancel" Class="mybutton" onclick="window.close();"><bean:message key="button.cancel"/></button>
			  
			  	 <input type='hidden' name='reportSql' value="<%=reportSql%>" />
			  	 <input type='hidden' name='gz_module' value="<%=gz_module%>" />
			  </td>
	    	</tr>
    	</table>
	</td>
	</tr>
</table>
</html:form>


  