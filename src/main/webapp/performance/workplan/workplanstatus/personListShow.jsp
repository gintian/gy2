<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.performance.workPlan.WorkplanstatusForm" %>
<%
WorkplanstatusForm workplanstatusForm = (WorkplanstatusForm)session.getAttribute("workplanstatusForm");
String status =  workplanstatusForm.getStatus();
String str_whl = workplanstatusForm.getStr_whl();
%>
<style>
<!--
.AutoTable{
   BORDER-BOTTOM: medium none; BORDER-COLLAPSE: collapse; 
   BORDER-LEFT: medium none; BORDER-RIGHT: medium none; BORDER-TOP: medium none; 
   TABLE-LAYOUT:fixed;   
   word-break:break-all;
}
.fixedheight{
height:500px;
vertical-align:top;
}
.RecordRowPer {
	border: inset 1px #C4D8EE;
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
	BORDER-LEFT: #C4D8EE 1pt solid; 
	BORDER-RIGHT: #C4D8EE 1pt solid; 
	BORDER-TOP: #C4D8EE 1pt solid;
	font-size: 12px;
	margin-top:-4px;
	height:22;
}
-->
</style>

<script type="text/javascript">

function operateCheckBox(obj)
   {//点击，则把所有的复选框全部选中
   		var value=obj.checked;
   		for(var i=0;i<document.workplanstatusForm.elements.length;i++)
   		{
	   		
   			if(document.workplanstatusForm.elements[i].type=='checkbox'&&document.workplanstatusForm.elements[i].name.length>18&&document.workplanstatusForm.elements[i].name.substring(0,18)=='pagination.select[')
   				document.workplanstatusForm.elements[i].checked=value;
   		
   		}
   		if(obj.checked)
	   		document.workplanstatusForm.isSelectedAll.value="1";
   		else
   			document.workplanstatusForm.isSelectedAll.value="0";
   
   }
function sub()///当点击刷新时
   {
   		document.workplanstatusForm.action="/performance/workplan/workplanstatus_show.do?b_query=link&isReset=0";
   		document.workplanstatusForm.submit();
   }	
function sendMail()///发送邮件
{
	
	var status = "<%=status%>";
	if(document.workplanstatusForm.isSelectedAll.value=="1")
	{
			var str_whl = "<%=str_whl%>";
			var infos=new Array();
			infos[0]="1";
			infos[1]=str_whl;
			
		 	var url="/performance/objectiveManage/objectiveCardMonitor/objective_state_list.do?b_initMail=link`opt=5`plan_id="+status;
		 	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(url);
	    	window.showModalDialog(iframe_url,infos,"dialogWidth=700px;dialogHeight=470px;resizable=yes;scroll=yes;status=no;");  
	}
	else
	{
		var strIds="";
		var basePrefix="";
		var assistance="";
		var index=0;
		var obj;
		var assist;
		if(status=="01")
		{
			obj = document.getElementsByName("ids");
		}
		else if(status=="02")
		{
			obj = document.getElementsByName("curruserno");
		}
		assist = document.getElementsByName("ids");	
		var  prefix = document.getElementsByName("baseprefix");
		for(var i=0;i<document.workplanstatusForm.elements.length;i++)
			{			
		   		if(document.workplanstatusForm.elements[i].type=='checkbox' &&document.workplanstatusForm.elements[i].name!="selbox")       
		   			{	
			  		if(document.workplanstatusForm.elements[i].checked)
			  			{
							var temp;
							if(obj[index].value==null || obj[index].value=="")
								temp="nodata";
							else
								temp=obj[index].value;
							strIds=strIds+temp+",";
							basePrefix=basePrefix+prefix[index].value+",";
							assistance=assistance+assist[index].value+",";
						}
						index++;
					}
			}
			if(strIds=="")
			{
				alert("请选择人员！");
				return;
			}
			var to_a0100=strIds+";"+basePrefix+";"+assistance;
			var infos=new Array();
			infos[0]=to_a0100;
			infos[1]="1";
		 	var url="/performance/objectiveManage/objectiveCardMonitor/objective_state_list.do?b_initMail=link`opt=5`plan_id="+status;
		 	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(url);
	    	window.showModalDialog(iframe_url,infos,"dialogWidth=700px;dialogHeight=470px;resizable=yes;scroll=yes;status=no;");
	}
}

</script>
<html:form action="/performance/workplan/workplanstatus_show">
<br>
<table align="center" width="90%">
<tr align="center">
<td align="center">
<fieldset>
	<legend>
	<logic:equal name="workplanstatusForm" property="status" value="01">
	<bean:message key="workplanstatus.wsb"/>
	</logic:equal>
	<logic:equal name="workplanstatusForm" property="status" value="02">
	<bean:message key="workplanstatus.yb"/>
	</logic:equal>
	<logic:equal name="workplanstatusForm" property="status" value="03">
	<bean:message key="workplanstatus.yp"/>
	</logic:equal>
	</legend>
	<div class="fixedheight">
	<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="AutoTable">
	<thead>
		<tr>
				<logic:notEqual name="workplanstatusForm" property="status" value="03">
				<td class="TableRow" align="center" width="5%">
					<input type="checkbox" name="selbox" onclick="operateCheckBox(this);" title='<bean:message key="label.query.selectall"/>'>&nbsp;
				</td>
				</logic:notEqual>
				<td class="TableRow" align="center" width="15%">
					&nbsp;&nbsp;<bean:message key="police.un.name"/>&nbsp;&nbsp;
				</td>
				<td class="TableRow" align="center" width="20%">
					&nbsp;&nbsp;<bean:message key="police.um.name"/>&nbsp;&nbsp;
				</td>
				<td class="TableRow" align="center" width="30%">
					&nbsp;&nbsp;<bean:message key="column.sys.pos"/>&nbsp;&nbsp;
				</td>
				<td class="TableRow" align="center" width="15%">
					&nbsp;&nbsp;<bean:message key="label.title.name"/>&nbsp;&nbsp;
				</td>
				<logic:equal name="workplanstatusForm" property="status" value="02">
					<td class="TableRow" align="center" width="15%">
			     		 <bean:message key="conlumn.board.approveuser"/>&nbsp;
			        </td>
			    </logic:equal>
			    <input type='hidden' value="${workplanstatusForm.isSelectedAll}"  name='isSelectedAll'>
			</tr>	
		</thead>	
		<br>
					<hrms:paginationdb id="element" name="workplanstatusForm" sql_str="${workplanstatusForm.str_sql}" table="" where_str="${workplanstatusForm.str_whl}"  order_by="${workplanstatusForm.order_str}" columns="${workplanstatusForm.colums}"   page_id="pagination" pagerows="${workplanstatusForm.pagerows}"  indexes="indexes">
			         <tr> 
			         	<logic:notEqual name="workplanstatusForm" property="status" value="03">
				        <td align="center" class="RecordRow" nowrap>
				   		<hrms:checkmultibox name="workplanstatusForm" property="pagination.select" value="true" indexes="indexes"/>&nbsp;
					    </td>	
					    </logic:notEqual>				    
						  <input type="hidden" name="ids" value="<bean:write name="element" property="a0100" filter="false"/>" />
						  <input type="hidden" name="curruserno" value="<bean:write name="element" property="curr_user" filter="false"/>" />
						  <input type="hidden" name="baseprefix" value="<bean:write name="element" property="nbase" filter="false"/>" />      
				        <td align="left" class="RecordRow" nowrap>
				        &nbsp;
				        <hrms:codetoname codeid="UN" name="element" codevalue="b0110" codeitem="codeitem" scope="page"/>     
				        <bean:write name="codeitem" property="codename" />
				        &nbsp;
					    </td>
			            <td align="left" class="RecordRow" wrap>
			             &nbsp;
				        <hrms:codetoname codeid="UM" name="element" codevalue="e0122" codeitem="codeitem" scope="page"/>     
				        <bean:write name="codeitem" property="codename" />
				        &nbsp;
			            </td>
			            <td align="left" class="RecordRow" wrap>
			             &nbsp;
				        <hrms:codetoname codeid="@K" name="element" codevalue="e01a1" codeitem="codeitem" scope="page"/>     
				        <bean:write name="codeitem" property="codename" />
				        &nbsp;
			            </td>
			            <td align="left" class="RecordRow" wrap>
			                    &nbsp;<bean:write  name="element" property="a0101" />&nbsp;
			            </td>
			            <logic:equal name="workplanstatusForm" property="status" value="02">
			            <td align="left" class="RecordRow" nowrap>
			                &nbsp;<bean:write  name="element" property="approver" />&nbsp; 		   
				    	</td>
				    	
				    	</logic:equal> 
				    	  
			          </tr>  
			        </hrms:paginationdb>
		</table>
		</div>
	</fieldset>
</td>
</tr>
</table>
				<table  width="89%" cellspacing="0"  align="center" cellpadding="0" class='RecordRowPer' >
					<tr>
					    <td align="left" class="tdFontcolor"><bean:message key="hmuster.label.d"/>
								<bean:write name="pagination" property="current" filter="true" />
								<bean:message key="hmuster.label.paper"/>
								<bean:message key="hmuster.label.total"/>
								<bean:write name="pagination" property="count" filter="true" />
								<bean:message key="label.item"/>
								<bean:message key="hmuster.label.total"/>
								<bean:write name="pagination" property="pages" filter="true" />
								<bean:message key="hmuster.label.paper"/>
								
						</td>	
				        <td  align="right" nowrap class="tdFontcolor">
					        <hrms:paginationdblink name="workplanstatusForm" property="pagination" nameId="workplanstatusForm" scope="page">
							</hrms:paginationdblink>
						</td>
					</tr>
				</table>
				<table  width="50%" align="center">
					<tr>
						<td  colspan='2' align='center' style="height:35px" >
						<logic:notEqual name="workplanstatusForm" property="status" value="03">
			         	<input type="button" class='mybutton' name='<bean:message key="workplanstatus.send"/>' value='<bean:message key="workplanstatus.send"/>' onclick="sendMail();" />&nbsp;
			         	</logic:notEqual>
			            <input type="button" class='mybutton' name='<bean:message key="button.cancel"/>' value='<bean:message key="button.cancel"/>' onclick='window.close();'/>	
			           	</td>
					</tr> 
				</table>		
</html:form>
<script type="text/javascript">
///打开页面自动执行

 if(document.workplanstatusForm.isSelectedAll.value=="1")
{		
		document.workplanstatusForm.selbox.checked=true;
    	for(var i=0;i<document.workplanstatusForm.elements.length;i++)
   		{
   			if(document.workplanstatusForm.elements[i].type=='checkbox'&&document.workplanstatusForm.elements[i].name.length>18&&document.workplanstatusForm.elements[i].name.substring(0,18)=='pagination.select[')
   				document.workplanstatusForm.elements[i].checked=true;
   		}
}
</script>