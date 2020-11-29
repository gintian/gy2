
<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.gz.gz_accounting.AcountingForm,java.util.*"%>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/gz/salary.js"></script>
<hrms:themes />
 <%
	int i=0;
	AcountingForm accountingForm=(AcountingForm)session.getAttribute("accountingForm"); 
	String reportSql="";
	if(accountingForm!=null)
		reportSql=accountingForm.getReportSql();
	String isPremium="no";  //月奖金管理模块调用 
	if(request.getParameter("premium")!=null)
		isPremium=request.getParameter("premium");
%>

<script language='javascript' >
var isPremium='<%=isPremium%>';
var info=dialogArguments; 
function select_All(obj)
{
	if(obj.checked)
		 batch_set_valid(1,'-2')
	else
		 batch_set_valid(0,'-2')
}
function datacompute()
{	
	var itemids=new Array();
	var temps=document.getElementsByName("chk");
	for(var i=0;i<temps.length;i++)
	{
		if(temps[i].checked)
		{
			itemids[itemids.length]=temps[i].value;
		}
	}
	if(itemids.length==0)
	{
		alert("请选择计算公式!");
		return;
	}
	
	var waitInfo=eval("wait");			
	waitInfo.style.display="block";
	document.getElementsByName("compute")[0].disabled=true;
	var hashvo=new ParameterSet();
	hashvo.setValue("itemids",itemids);
	hashvo.setValue("fieldsetid",info[1]);
	hashvo.setValue("ym",info[2]);
   	var request=new Request({method:'post',asynchronous:true,onSuccess:computeIsOk,functionId:'3020073070'},hashvo);
	
}
function computeIsOk(outparamters)
{
	var waitInfo=eval("wait");			
	waitInfo.style.display="none";
	var flag=outparamters.getValue("succeed");

	if(flag=="false")
		return;
	var retvo=new Object();	
	retvo.success="1";
    parent.window.returnValue=retvo;
    parent.window.close();
}
</script>

<html:form action="/performance/data_collect/data_collect"> 
<br>
<br>
<br>

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
<table align="center"  width="80%">
<tr>
<td>

  <fieldset align="center" style="width:100%;">
   <legend><bean:message key="label.gz.select.formula"/></legend>
	<table width="100%" border="0" cellspacing="0" align="left" cellpadding="0">
	 
	<tr>
	 <td width="80%" align='center' >
		<table border="0" cellspacing="0" cellpadding="0">
		<tr>
 		   <td>
 		    	<div style="height: 350px;overflow: auto">
				<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
				   	  <thead>
			           <tr>
				            <td align="center" class="TableRow" nowrap >
								 <input type="checkbox" name="selectAll"    onclick='select_All(this)'     > 	
					    	</td>         
				            <td align="center" class="TableRow" nowrap width="250">
								<bean:message key="label.gz.formula"/>&nbsp;
					    	</td>
			           </tr>
				   	  </thead>
				          <hrms:extenditerate id="element" name="data_collectForm" property="formulalistform.list" indexes="indexes"  pagination="formulalistform.pagination" pageCount="200" scope="session">
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
							    <input type="checkbox" name="chk"   <logic:equal name="element" property="useflag" value="1">checked</logic:equal>      value="<bean:write name="element" property="itemid" filter="true"/>" onclick ="setformulavalid(this,'<bean:write name="element" property="itemid" filter="true"/>','-2');"> 	            
					    	</td>            
				            <td align="left" class="RecordRow" nowrap>
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
				<button name="btn_all" Class="mybutton" onclick="batch_set_valid(1,'-2')"><bean:message key="button.all.select"/></button>&nbsp;&nbsp;
				<button name="btn_unall" Class="mybutton" onclick="batch_set_valid(0,'-2')"><bean:message key="button.all.reset"/></button>&nbsp;&nbsp;
				<button name="compute" Class="mybutton" onclick="datacompute();"><bean:message key="button.computer"/></button>&nbsp;&nbsp;
			  	<button name="cancel" Class="mybutton" onclick="window.close();"><bean:message key="button.cancel"/></button>
			  
			  	 <input type='hidden' name='reportSql' value="<%=reportSql%>" />
			  </td>
	    	</tr>
    	</table>
	</td>
	</tr>
</table>
</html:form>


  