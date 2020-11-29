<%@ page contentType="text/html; charset=UTF-8"%>

<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.frame.dao.RecordVo"%>
<%
	int i=0;
	String bosflag ="";
	String themes="default";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null)
	{
		bosflag =userView.getBosflag();
	}
%>

<style>
	.fixedtab 
	{ 
		overflow:auto; 
		height:400;
		BORDER-BOTTOM: #94B6E6 1pt solid; 
	    BORDER-LEFT: #94B6E6 1pt solid; 
	    BORDER-RIGHT: #94B6E6 1pt solid; 
	    BORDER-TOP: #94B6E6 0pt solid ; 	
	}
</style>
<script type="text/javascript">
	function excecuteEXCEL()
   {
		var hashvo=new ParameterSet();			
		hashvo.setValue("ins_id","${taskDeskForm.ins_id}");
		hashvo.setValue("ins_ids","${taskDeskForm.ins_ids}");
		hashvo.setValue("taskid","${taskDeskForm.taskid}");
		hashvo.setValue("type","${taskDeskForm.type}");
		hashvo.setValue("sp_flag","${taskDeskForm.sp_flag}");
		var request=new Request({method:'post',asynchronous:false,onSuccess:showExcel,functionId:'0570010147'},hashvo);
   }	
   
   
   function showExcel(outparamters)
   {
	var url=outparamters.getValue("excelfile");	
//	var win=open("/servlet/DisplayOleContent?filename="+url,"excel");
	//20/3/18 xus vfs改造
	var win=open("/servlet/vfsservlet?fileid="+url+"&fromjavafolder=true","excel");
   }
</script>

<hrms:themes></hrms:themes>
<html:form action="/general/template/view_process">
<%if(!"hcm".equals(bosflag)){ %><Br><%;}%>	
	<table width="900" border="0" cellpadding="0" cellspacing="0" align="center" >
		<tr height="20">
		<!--<td width=10 valign="top" class="tableft"></td>
			<td width=130 align=center class="tabcenter">&nbsp;<bean:message key="rsbd.task.idea" />&nbsp;</td>
			<td width=10 valign="top" class="tabright"></td>
			<td valign="top" width="600" class="tabremain"></td>  -->	
			
			<td align="left" colspan="4" class="TableRow">&nbsp;<bean:message key="rsbd.task.idea" />&nbsp;</td>
		</tr>
		
		<tr class="list3" height="10" id="yj">
			<td colspan="4">
			<div class='fixedtab common_border_color' style='width:100%'  >
			
				<table border="0" cellpmoding="0" cellspacing="0"    cellpadding="0" width="100%" class="ListTable3">
					<tr height="10">
						<td align="right"  class="RecordRowTop0 noleft"    width="10%"  nowrap >&nbsp;模板</td>
						<td align="left"  class="RecordRowTop0 noright" width="90%" colspan="7" nowrap ><bean:write  name="taskDeskForm" property="tableName" filter="false"/></td>
					</tr>
					<tr height="10">
						<td align="right"  class="RecordRow noleft"  nowrap >
						<logic:notEqual name="taskDeskForm" property="type" value="10">
						<logic:notEqual name="taskDeskForm" property="type" value="11">     
		   	  	 			<bean:message key="label.title.name"/>
			   	  	 	</logic:notEqual>     
			   	  	 	</logic:notEqual>
		   	  	 	<logic:equal name="taskDeskForm" property="type" value="10">     
		   	  	 		<bean:message key="general.inform.org.organizationName"/>:
		   	  	 	</logic:equal>
		   	  	 	<logic:equal name="taskDeskForm" property="type" value="11">     
		   	  	 		<bean:message key="kq.shift.employee.e01a1"/>:
		   	  	 	</logic:equal></td>
						<td align="left"  class="RecordRow noright" colspan="7" nowrap ><bean:write  name="taskDeskForm" property="a0101s" filter="false"/></td>
					</tr>
          			<hrms:extenditerate id="element" name="taskDeskForm" property="sp_yjListForm.list" indexes="indexes"  pagination="sp_yjListForm.pagination" pageCount="100" scope="session">
			          <%
			          RecordVo vo=(RecordVo)pageContext.getAttribute("element");
			          String bs_flag=vo.getString("bs_flag"); //1：待批 2：加签 3报备
			          
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
			          	<td align="right" class="RecordRow noleft" nowrap>
			          	<% if(i==1){ %>
			          	<bean:message key="rsbd.wf.applyemp"/>
			          	<% } 
			          	 else if(bs_flag.equals("2")){
			          	%>
			          	<bean:message key="rsbd.wf.jqemp"/>
			          	<%	
			          	  } else if(bs_flag.equals("3")){
			          	%>
			          	<bean:message key="rsdb.wf.bbemp"/>
			          	<%	
			          	  } else {
			          	 %>
			          	<bean:message key="rsbd.task.applyemp"/>
			          	<% } %>
			          	</td>
			          	<td align="left" class="RecordRow" nowrap><bean:write  name="element" property="string(a0101)" filter="false"/>&nbsp;&nbsp;
			          	
			          	
			          	</td>
			          	
			          	
			          	<td align="right" class="RecordRow" nowrap>节点名称</td>
			          	<td align="left" class="RecordRow" nowrap>&nbsp;<bean:write  name="element" property="string(appuser)" filter="false"/></td>
			          	
			          	
			          	<td align="right" class="RecordRow"        nowrap>
			          	<% if(i==1){ %>
			          	<bean:message key="rsbd.wf.applytime"/>
			          	<% } 
			          	 else if(bs_flag.equals("2")){
			          	%>
			          	<bean:message key="rsbd.wf.jqtime"/>
			          	<%	
			          	  } else if(bs_flag.equals("3")){
			          	%>
			          	<bean:message key="rsdb.wf.bbtime"/>
			          	<%	
			          	  } else {
			          	 %>
			            <bean:message key="rsbd.task.applytime"/>
			          	<% } %>
			          	 
			          	</td>
			          	<td align="left" class="RecordRow"      <%=(!bs_flag.equals("1")?"colspan='3'":"")%>        nowrap><bean:write  name="element" property="time(end_date)" filter="false"/></td>
			         
			          	<% if(bs_flag.equals("1")){ %>
				          	<td align="right" class="RecordRow" nowrap>
				          	<% if(i==1){ %>
				          	<bean:message key="rsbd.wf.applyDesc"/>
				          	<% }else{ %>
				          	<bean:message key="rsbd.task.idea"/>
				          	<% } %>
				          	</td>
				          	<td align="left" class="RecordRow noright" nowrap>
	          					<hrms:codetoname codeid="30" name="element" codevalue="string(sp_yj)" codeitem="codeitem" scope="page"/>  	      
	          					<bean:write name="codeitem" property="codename" />
	          					<logic:equal name="element" property="string(task_state)"
								value="4">  
								<bean:write  name="element" property="string(appuser)" filter="false"/>   
								</logic:equal>       
				          	</td>
			          	<% } %>
			          	
          		      </tr>	
          		      <tr>
          		      	<td class="RecordRow noleft"  >&nbsp; </td>
          		      	<td colspan="7" align="left" class="RecordRow noright" >
          		      		&nbsp;<bean:write  name="element" property="string(content)" filter="false"/>
          		      	</td>
          		      </tr>		                				
		            </hrms:extenditerate>
				</table>
			
			
			</div>
			
			</td>
		</tr>	
	     <tr class="list3">
			<td align="left" colspan="4">
			
	 	    </td>
		</tr>
		<tr height="35px" >
			<td align="center" valign="middle" colspan="4">
           		<input type="button" value="<bean:message key="goabroad.collect.educe.excel"/>" class="mybutton" onclick="excecuteEXCEL()">
				<html:button styleClass="mybutton" property="br_return" onclick="history.back();"><bean:message key="button.return"/></html:button>
	 	      
	 	    </td>
		</tr>
	</table>
</html:form>

