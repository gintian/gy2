<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant,
				com.hrms.struts.constant.SystemConfig,
				com.hjsj.hrms.actionform.general.template.TaskDeskForm,
				com.hjsj.hrms.actionform.general.template.TemplateForm"%>
<!-- 已办任务页面 -->
				
<%

    TaskDeskForm approvedTaskForm=(TaskDeskForm)session.getAttribute("approvedTaskForm");
	String businessModel="0";
	String bs_flag=approvedTaskForm.getBs_flag();
	if(bs_flag.equals("2")) //加签
		businessModel="72";
	else if(bs_flag.equals("3")) //报备
		businessModel="62";
    String backctrl="";
    TemplateForm templateForm=(TemplateForm)session.getAttribute("templateForm"); 
    if(templateForm!=null&&templateForm.getNavigation().equalsIgnoreCase("htbl")){
        backctrl="htbl";
    }
    UserView userView = (UserView) session.getAttribute(WebConstant.userView);
    String bosflag="";
	if(userView != null)
	{
	    bosflag = userView.getBosflag();
	}
 %>				
				
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<script type="text/javascript">
<!--
function search()
{ 
	var bs_flag=document.getElementsByName("bs_flag")[0].value;
 	approvedTaskForm.action="/general/template/ins_obj_list2.do?b_query2=link_query&bs_flag="+bs_flag+"&sp_flag=${approvedTaskForm.sp_flag}&name=sc&fromflag=${approvedTaskForm.fromflag}";
	approvedTaskForm.submit();
}
function returnDh(){
   parent.parent.location="/general/tipwizard/tipwizard.do?br_ct=link";
}
function _refrash()
{
	var bs_flag=document.getElementsByName("bs_flag")[0].value;
	approvedTaskForm.action="/general/template/ins_obj_list2.do?b_query2=link_query&bs_flag="+bs_flag+"&sp_flag=${approvedTaskForm.sp_flag}&name=sc&fromflag=${approvedTaskForm.fromflag}";
	approvedTaskForm.submit();
}
//-->
</script>
<html>
  <head>
   
  </head>
  <hrms:themes />
<%	   
if ("hcm".equals(bosflag)){	   
%>
<link href="/general/template/template.css" rel="stylesheet" type="text/css"/>
<%} %>
  <body>
   
   <html:form action="/general/template/ins_obj_list2">
   <% int i=0; %>
 <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ybmargin">
	 <%	   
	if ("hcm".equals(bosflag)){	   
	%>
	 <tr>
	<%} else {%>
	
	 <tr height="35px">
	<%}%>
	    <td>
		    <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0">
	
	   	    <logic:equal name="approvedTaskForm" property="sp_flag" value="3">
	   	     <tr>
   	  	 		<td colspan="8" align="left" >  
   	  	 		<table height="20px;" style="margin-top: -3px;margin-bottom: -2px;">
   	  	 		<tr height="20px;">
   	  	 		<td align="right" valign="middle">
   	  	 		<bean:message key="tab.label.tasktype"/>&nbsp;
   	  	 		</td>
   	  	 		<td align="left" valign="middle">
	          	 <html:select name="approvedTaskForm"  onchange="search();"  property="bs_flag" size="1"  >
						<html:optionsCollection property="bs_flag_list" value="dataValue" label="dataName"/>
				 </html:select>
			    </td>
			    <td valign="middle">
		   	  	 	<html:radio property="query_type" value="1" onclick="Element.hide('datepnl');"/>
		   	  	</td>
		   	  	<td valign="middle">
		   	  	  <bean:message key="label.by.date"/>
		   	  	</td>
		   	  	<td valign="middle"> 	
		   	  	 	<html:text name="approvedTaskForm" property="days" size="2" styleClass="text4"></html:text><bean:message key="label.day"/>
		   	  	</td>
		   	  	<td valign="middle">
		   	  	 	<html:radio property="query_type" value="2" onclick="Element.show('datepnl');"/>
		   	  	</td>
		   	  	<td valign="middle">
		   	  	   <bean:message key="label.by.time.domain"/>
		   	  	</td>
		   	  	<td valign="middle">
		   	  	 <span id="datepnl"><bean:message key="label.from"/>
		   	  	 	<input type="text" name="start_date" value="${approvedTaskForm.start_date}" extra="editor" style="width:100px;font-size:10pt;text-align:left;" id="editor1"  dropDown="dropDownDate" class="text4">
		   	  	 	<bean:message key="label.to"/>
		   	  	 	<input type="text" name="end_date"  value="${approvedTaskForm.end_date}" extra="editor" style="width:100px;font-size:10pt;text-align:left" id="editor2"  dropDown="dropDownDate" class="text4">
	   	  	 	</span> 
		   	  	</td>
		   	  	<td valign="middle">模板名称&nbsp;
		   	  	</td>
	   	   		<td valign="middle">
   	  	 		  
   	  	 		  <html:select name="approvedTaskForm" property="templateId" size="1"  >
					<html:optionsCollection property="templateList" value="dataValue" label="dataName"/>
				  </html:select>
				 </td>
				 <td valign="middle">
			         <button class="mybutton" onclick='search()'><bean:message key="button.query"/></button>
			      </td>
					</tr>
				</table>
	   	  	 	</td>	
	   	  </tr>
	   	  </logic:equal>  
		   
		
		    </table>
	   </td>
   </tr>
   <tr>
   <td>
    
    <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
   	  <thead>

   	  	 <tr >
   	  	   <td align="center" class="TableRow" nowrap ><bean:message key="conlumn.board.topic"/>&nbsp; </td>
   	  	   <td align="center" class="TableRow"   width="10%" nowrap ><bean:message key="rsbd.wf.applyemp"/>&nbsp; </td>
   	  	   <td align="center" class="TableRow"   width="10%" nowrap ><bean:message key="rsbd.wf.applyunit"/>&nbsp; </td>  <!-- 发起单位 -->
   	  	   <td align="center" class="TableRow"   width="15%" nowrap ><bean:message key="general.template.applyStartDate"/>&nbsp;</td>
   	  	   <td align="center" class="TableRow"   width="15%" nowrap ><bean:message key="general.template.spEndDate"/>&nbsp;</td>
   	  	   <td align="center" class="TableRow" nowrap width="10%"><bean:message key="rsbd.task.curremp"/>&nbsp;</td>
   	  	   <td align="center" class="TableRow"   width="10%" nowrap ><bean:message key="rsbd.wf.sploop"/>&nbsp; </td>
   	  	   <td align="center" class="TableRow"   width="10%" nowrap >浏览打印</td>
   	  	   <td align="center" class="TableRow"   width="5%" nowrap ><bean:message key="task.state"/>&nbsp;</td>
   	  	 </tr>
   	  </thead>
   	  
   	  <hrms:extenditerate id="element" name="approvedTaskForm" property="taskListForm.list"   
			indexes="indexes"  pagination="taskListForm.pagination" pageCount="${approvedTaskForm.pagerows}" scope="session">
   	   
   	  	  <%
          if(i%2==0)
          {
          %>
          <tr class="trShallow"  onclick='tr_onclick(this,"#F3F5FC");' >
          <%}
          else
          {%>
          <tr class="trDeep" onclick='tr_onclick(this,"#E4F2FC");' >
          <%
          }
          i++;          
          %> 	
   	  
   	       <td align="left" class="RecordRow" nowrap>
                <bean:write name="element" property="task_topic" filter="false"/>     
	       </td> 
   	       <td align="left" class="RecordRow" nowrap>
                <bean:write name="element" property="fullname" filter="false"/>
           </td> 
	       <td align="left" class="RecordRow" nowrap>
                <bean:write name="element" property="unitname" filter="false"/>      
	       </td> 
   	  	   <td align="left" class="RecordRow" nowrap>
               <bean:write name="element" property="start_date" filter="false"/>    
	       </td>
	       <td align="left" class="RecordRow" nowrap>
                <bean:write name="element" property="end_date" filter="false"/>     
	       </td>
	      <td align="left" class="RecordRow" nowrap>
                <bean:write name="element" property="sp_info" filter="false"/>    
	       </td>
	       <td align="center" class="RecordRow" nowrap>
               <a href="/general/template/view_process.do?b_query=link&from=yprw&tabid=<bean:write name="element" property="tabid" filter="false"/>&taskid=<bean:write name="element" property="task_id" filter="true"/>&ins_id=<bean:write name="element" property="ins_id" filter="false"/>"><img src="/images/view.gif" width="16" height="16" border="0"></a>            	                
	       </td>  
   	  	   <td align="center" class="RecordRow" nowrap>
               <a href="/general/template/edit_form.do?b_query=link&type=${approvedTaskForm.type}&businessModel=<%=businessModel%>&tabid=<bean:write name="element" property="tabid" filter="false"/>&ins_id=<bean:write name="element" property="ins_id" filter="true"/>&model=yp&taskid=<bean:write name="element" property="task_id" filter="true"/>&sp_flag=2&returnflag=${approvedTaskForm.fromflag} " target="_parent"><img src="/images/view.gif" width="16" height="16" border="0"></a>            	            
	       </td> 
   	      <td align="left" class="RecordRow" nowrap>
                <bean:write name="element" property="flag" filter="false"/>   
	       </td> 	
   	  	
   	  	 </tr>
   	  </hrms:extenditerate>
   	  
   	</table>
  </td>
  </tr>
  </table>
   
<table  width="100%" align="center" class="RecordRowP">
		<tr>
			 <td valign="bottom" class="tdFontcolor">
		            <bean:message key="label.page.serial"/>
					<bean:write name="approvedTaskForm" property="taskListForm.pagination.current" filter="true" />
					<bean:message key="label.page.sum"/>
					<bean:write name="approvedTaskForm" property="taskListForm.pagination.count" filter="true" />
					<bean:message key="label.page.row"/>
					<bean:write name="approvedTaskForm" property="taskListForm.pagination.pages" filter="true" />
					<bean:message key="label.page.page"/>
					 每页显示<html:text styleClass="text4" property="pagerows" name="approvedTaskForm" size="3"></html:text>条&nbsp;&nbsp;<a href="javascript:_refrash();">刷新</a>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right">
		          <hrms:paginationlink name="approvedTaskForm" property="taskListForm.pagination" nameId="taskListForm" >
				</hrms:paginationlink>
			</td>
		</tr>
</table>
<%
 if(backctrl.equalsIgnoreCase("htbl")){//合同办理导航图进来的，返回按钮的处理 
%>
<table align="center">
        <tr>
	         <td>
	             <input type="button" class="mybutton" value="返回" onclick="returnDh()"/>
	         </td>
        </tr>
</table>    
<%
}
%>

  <script>
		if($F('query_type')=="2")
		  Element.show('datepnl');
		else
		  Element.hide('datepnl');
	</script>	 
   </html:form>
  </body>
</html>
