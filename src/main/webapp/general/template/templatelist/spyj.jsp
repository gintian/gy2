<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ page import="com.hrms.frame.dao.RecordVo"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%

	String bosflag ="";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null)
	{
		bosflag =userView.getBosflag();
	}
%>
<html>
  <head>
    

  </head>
<style>
	.fixedtab 
	{ 
		overflow:auto; 
		height:310;
		BORDER-BOTTOM: #94B6E6 1pt solid; 
	    BORDER-LEFT: #94B6E6 1pt solid; 
	    BORDER-RIGHT: #94B6E6 1pt solid; 
	    BORDER-TOP: #94B6E6 1pt solid ; 	
	}
</style>
<script type="text/javascript">
	function excecuteEXCEL()
   {
		var hashvo=new ParameterSet();			
		hashvo.setValue("ins_id","${templateListForm.ins_id}");
		hashvo.setValue("ins_ids","");
		hashvo.setValue("taskid","${templateListForm.task_id}");
		hashvo.setValue("infor_type","${templateListForm.infor_type}");
		var request=new Request({method:'post',asynchronous:false,onSuccess:showExcel,functionId:'0570010147'},hashvo);
   }	
   
   
   function showExcel(outparamters)
   {
	var url=outparamters.getValue("excelfile");	
//	var win=open("/servlet/DisplayOleContent?filename="+url,"excel");
	//20/3/17 xus vfs改造
	var win=open("/servlet/vfsservlet?fileid="+url+"&fromjavafolder=true","excel");
   }
</script>
<%	   
if ("hcm".equals(bosflag)){	   
%>
<link href="/general/template/template.css" rel="stylesheet" type="text/css"/>
<%} %>
  <body>
  <html:form action="/general/template/templatelist">
    <%if(!"hcm".equals(bosflag)){ %><Br><%;}%>
			<table width='625' class="spyjformmragin">
				<Tr>	
					<Td>		
						<div class='fixedtab common_border_color' style='width: 100%'>
							<table border="0" cellpmoding="0" cellspacing="0"
								class="ListTable3" cellpadding="0" width=100%">
								<tr height="10">
									<td align="right" class="RecordRowTop0 noleft" nowrap>
										模板
									</td>
									<td align="left" class="RecordRowTop0 noright" colspan="5" nowrap>
										<bean:write name="templateListForm" property="tableName"
											filter="false" />
									</td>
								</tr>
								<tr height="10">
									<td align="right" class="RecordRow noleft" nowrap>
										<logic:equal name="templateListForm" property="infor_type"
											value="1">
											<bean:message key="label.title.name" />
			   	  	 	</logic:equal>
										<logic:equal name="templateListForm" property="infor_type"
											value="2">
											<bean:message key="general.inform.org.organizationName" />
		   	  	 	</logic:equal>
										<logic:equal name="templateListForm" property="infor_type"
											value="3">
											<bean:message key="kq.shift.employee.e01a1" />
		   	  	 	</logic:equal>
									</td>
									<td align="left" class="RecordRow noright" colspan="5" nowrap>
										<bean:write name="templateListForm" property="a0101s"
											filter="false" />
									
									</td>
								</tr>
								<%
								    int i = 0;
								%>
								<hrms:extenditerate id="element" name="templateListForm"
									property="sp_yjListForm.list" indexes="indexes"
									pagination="sp_yjListForm.pagination" pageCount="100"
									scope="session">
									<%
										RecordVo vo=(RecordVo)pageContext.getAttribute("element");
							          	String bs_flag=vo.getString("bs_flag"); //1：待批 2：加签 3报备
									    if (i % 2 == 0) {
									%>
									<tr class="trShallow">
										<%
										    } else {
										%>
									
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
										<td align="left" class="RecordRow" nowrap>
											<bean:write name="element" property="string(a0101)"
												filter="false" />
											
										</td>
										<td align="right" class="RecordRow" nowrap>
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
										<td align="left" class="RecordRow" nowrap>
											<bean:write name="element" property="time(end_date)"
												filter="false" />
										
										</td>
										
								        <td align="right"  class="RecordRow" nowrap>
								          	<% if(i==1){ %>
								          	<bean:message key="rsbd.wf.applyDesc"/>
								          	<% }else{ %>
								          	<bean:message key="rsbd.task.idea"/>
								          	<% } %>
							          	</td>
										<td align="left" class="RecordRow noright" nowrap>
											<hrms:codetoname codeid="30" name="element"
												codevalue="string(sp_yj)" codeitem="codeitem" scope="page" />
											<bean:write name="codeitem" property="codename" />
											
										</td>
									</tr>
									<tr>
									   <td class="RecordRow noleft"  > </td>
										<td colspan="7" align="left" class="RecordRow noright">
											<bean:write name="element" property="string(content)"
												filter="false" />
											
										</td>
									</tr>
								</hrms:extenditerate>
							</table>


						</div>

					</Td>
				</Tr>
				<tr height="35px">
					<td align="center" valgin="middle">
						<input type="button"
							value="<bean:message key="goabroad.collect.educe.excel"/>"
							class="mybutton" onclick="excecuteEXCEL()">
						<input type="button" value="<bean:message key="button.close"/>"
							class="mybutton" onclick="window.close();">

					</td>
				</tr>
			</table>


		</html:form>
  </body>
</html>
