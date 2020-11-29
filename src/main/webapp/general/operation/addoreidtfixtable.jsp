
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>

<script type="text/javascript">
<!--
	function checkform(){
		var message="";
		var tempname=$F('t_wf_defineVo.string(name)');
		var tempinputurl=operationForm.inputurl.value;
		var tempappurl=operationForm.appurl.value;
		if(tempname==null||tempname.length<1){
		message="请输入名称\n";
		}
		if(tempinputurl==null||tempinputurl.length<1){
		message=message+"输入网页不能为空\n";
		}
		if(tempappurl==null||tempappurl.length<1){
		message=message+"审批网页不能为空\n";
		}
		if(message.length>1){
		alert(message);
		return false;
		}else{
		return true;
		}
	}
	function addfixtable(){
		if(checkform()){
			operationForm.action="/general/operation/addoreidtfixtable.do?b_add=link&state=add";
			operationForm.submit();
		}else{
		return;
		}
	
	}
	function updatefixtable(){
		if(checkform()){
			operationForm.action="/general/operation/addoreidtfixtable.do?b_update=link";
			operationForm.submit();
		}else{
		return;
		}
	}
	function view(vie,hidd)
	{
	    var waitInfo=eval(vie);	   
	    waitInfo.style.display="block";
	    waitInfo=eval(hidd);	
	    waitInfo.style.display="none";
	}
	
	function addRow(id)
	{
	   var table=document.getElementById(id);
         if(table==null)
  	    return false;
       var td_num=table.rows.length;
       var rowCount=table.rows.length;	  
	   var tRow = table.insertRow(rowCount);	
	   var cell_0="";
	   var cell_1="";
	   if(id=="inputtable")
	   {
	       cell_0="<TD height='20'><input type='text' name='inputname' size='12' value=''></TD>";
	       cell_1="<TD><input type='text' name='inputparam' size='12' value=''></TD>";
	   }else if(id=="apptable")
	   { 
	      cell_0="<TD height='20'><input type='text' name='appname' size='12' value=''></TD>";
	      cell_1="<TD><input type='text' name='appparam' size='12' value=''></TD>";
	   }
	   var cell_2="<TD align=\"center\"><img src=\"/images/del.gif\" border=0 title=\"删除\" style=\"cursor:hand;\" onclick=\"delete_row('"+id+"','"+td_num+"',this);\"/></TD>";
	   tRow.id=td_num;
	   for (i=0;i<3;i++)
       { 
             var newCell=tRow.insertCell(i);
             switch (i) 
             { 
               case 0 : newCell.innerHTML=cell_0; break; 
               case 1 : newCell.innerHTML=cell_1; break; 
               case 2 : newCell.innerHTML=cell_2; break; 
             } 
       }
	}
	function delete_row(id,row_num,obj)
	{
	  var rowObj=obj.parentNode.parentNode;
	  if(rowObj!=null)
	     row_num=rowObj.rowIndex;
	  var table=document.getElementById(id);
         if(table==null)
  	    return false;
  	  if(confirm("确认要删除该参数？"))
  	  {
  	     table.deleteRow(row_num);
  	     return;
  	  }  	  
	}
//-->
</script>
<% int rows=1;%>
<html:form action="/general/operation/addoreidtfixtable">
	<br>
	<br>
	<table width="500" border="0" cellpadding="0" cellspacing="0" align="center">
		<tr height="20">
			<!--  <td width=10 valign="top" class="tableft"></td>
			<td width=130 align=center class="tabcenter">
				<logic:equal value="1" name="operationForm" property="uflag">	
				<bean:message key="t_wf_define.update.message"/>
				</logic:equal>
				<logic:notEqual value="1" name="operationForm" property="uflag">
				<bean:message key="t_wf_define.add.message"/>
				</logic:notEqual>
				&nbsp;
			</td>
			<td width=10 valign="top" class="tabright"></td>
			<td valign="top" class="tabremain" width="350"></td>-->
			
			<td align=center class="TableRow">
				<logic:equal value="1" name="operationForm" property="uflag">	
				<bean:message key="t_wf_define.update.message"/>
				</logic:equal>
				<logic:notEqual value="1" name="operationForm" property="uflag">
				<bean:message key="t_wf_define.add.message"/>
				</logic:notEqual>
			</td>
		</tr>
		<tr>
			<td class="framestyle9">
			<br/>
				<table border="0" cellpmoding="0" cellspacing="2" class="DetailTable" cellpadding="0">
					<tr class="list3">
						<td align="right" height="25" nowrap>
							<bean:message key="kq.item.name"/>
							:
						</td>
						<td align="left" nowrap>
						
						<html:text name="operationForm" property="t_wf_defineVo.string(name)" maxlength="20" size="20" styleClass="text"></html:text>
						</td>
						</tr>
						<tr>
						<tr class="list3">
						<td align="right" height="25" nowrap>
							<bean:message key="operation.class"/>
							:
						</td>
						<td align="left" nowrap>
						<bean:write name="operationForm" property="selstr" filter="false"/>
						</td>
						</tr>
						<tr>
						<td align="right" height="25" nowrap>
							<bean:message key="t_wf_define.inputurl"/>
							:
						</td>
						<td align="left" nowrap>
							<html:text name="operationForm" property="inputurl"  size="50" styleClass="text"></html:text>
						</td>
					</tr>
					<tr>
						<td align="right" height="25" nowrap>
							输入参数
							:
						</td>
						<td align="left" nowrap>
						  <div id="inputview" style='display=block;'>
							<a href="###" onclick="view('inputparam','inputview');">显示</a>
						  </div>							
						  <div id="inputparam" style='display:none;'>
							  <table width="90%" id="inputtable">
							   <tr>
							     <td width="35%">
							       参数名
							     </td>
							     <td width="35%">
							      参数值
							     </td>
							     <td align="center">							      
							      <a href="###" onclick="addRow('inputtable');">添加</a>|
							      <a href="###" onclick="view('inputview','inputparam');">隐藏</a>							      
							     </td>
							   </tr>		
							   <%rows=1; %>
							     <logic:iterate id="element" indexId="index" name="operationForm"  property="edit_param">
							       <tr> 							      
							         <TD height='20'><input type='text' name='inputname' size='12' value='<bean:write name="element" property="name"/>'></TD>
	                                 <TD><input type='text' name='inputparam' size='12' value='<bean:write name="element" property="value"/>'></TD>
	                                 <TD align="center"><img src="/images/del.gif" border=0 title="删除" style="cursor:hand;" onclick="delete_row('inputtable','<%=rows%>',this);"/></TD>
							       <%rows++; %>
							       </tr>
							     </logic:iterate>					     
							  </table>
							</div>
						</td>
					</tr>
					<tr>
						<td align="right"  height="25" nowrap>
							<bean:message key="t_wf_define.appurl"/>
							:
						</td>
						<td align="left" nowrap>
							<html:text name="operationForm" property="appurl" size="50" styleClass="text"></html:text>
						</td>
					</tr>					
                    <tr>
						<td align="right" height="25" nowrap>
							审批参数
							:
						</td>
						<td align="left" nowrap>
						  <div id="appview" style='display=block;'>
							<a href="###" onclick="view('appparam','appview');">显示</a>
						  </div>							
						  <div id="appparam" style='display:none;'>
							  <table width="90%" id="apptable">
							   <tr>
							     <td width="35%">
							       参数名
							     </td>
							     <td width="35%">
							      参数值
							     </td>
							     <td align="center">							      
							      <a href="###" onclick="addRow('apptable');">添加</a>|
							      <a href="###" onclick="view('appview','appparam');">隐藏</a>							      
							     </td>
							   </tr>		
							   <%rows=1; %>
							     <logic:iterate id="element" indexId="index" name="operationForm"  property="appeal_param">
							       <tr> 							       
							         <TD height='20'><input type='text' name='appname' size='12' value='<bean:write name="element" property="name"/>'></TD>
	                                 <TD><input type='text' name='appparam' size='12' value='<bean:write name="element" property="value"/>'></TD>
	                                 <TD align="center"><img src="/images/del.gif" border=0 title="删除" style="cursor:hand;" onclick="delete_row('apptable','<%=rows%>',this);"/></TD>
							       <%rows++; %>
							       </tr>
							     </logic:iterate>							  								     
							  </table>
							</div>
						</td>
					</tr>
					<tr>
						<td align="right" nowrap>
							<bean:message key="column.law_base.status"/>					
						</td>
						<td align="left" nowrap>
						<logic:equal value="1"  name="operationForm" property="validateflag">
						<input type="checkbox" name="validateflag" checked="checked"/>
						</logic:equal>
						<logic:notEqual value="1"  name="operationForm" property="validateflag">
						<input type="checkbox" name="validateflag"/>
					    </logic:notEqual>
						</td>
					</tr>
				</table>
				<br/>
			</td>
		</tr>
		<tr class="list3">
			<td align="center" style="height:35px;">
			<logic:equal value="1" name="operationForm" property="uflag">
			<button name="update" class="mybutton" onclick="updatefixtable();"><bean:message key="kq.report.update"/></button>
			</logic:equal>
			<logic:notEqual value="1" name="operationForm" property="uflag">
				<button name="add" class="mybutton" onclick="addfixtable();"><bean:message key="kq.emp.button.add"/></button>
			</logic:notEqual>
					<hrms:submit styleClass="mybutton" property="br_return">
						<bean:message key="kq.emp.button.return"/>
					</hrms:submit>
			</td>
		</tr>
	</table>
</html:form>
<script type="text/javascript">
<!--
	if(window.dialogArguments)
	{
		Element.readonly('codesetvo.string(codesetid)');
	}
//-->
</script>
