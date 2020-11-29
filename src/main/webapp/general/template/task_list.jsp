<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant,com.hrms.struts.constant.SystemConfig,com.hjsj.hrms.actionform.general.template.TaskDeskForm"%>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<%
    UserView userView = (UserView) session.getAttribute(WebConstant.userView);
    int i = 0;
    String backctrl = "";
    TaskDeskForm taskDeskForm = (TaskDeskForm) session.getAttribute("taskDeskForm");
    String type=taskDeskForm.getType(); 
    String otherParam="";
 
   	if(type!=null && type.length()>0 && type.charAt(0)=='t') //例：type=t48  ，只能显示某模板的任务（个性化）
   		otherParam="&type="+type;
 /*   if (templateForm != null && templateForm.getNavigation().equalsIgnoreCase("htbl")) {
        backctrl = "htbl";
    }*/
    String bosflag="";
	if(userView != null)
	{
	    bosflag = userView.getBosflag();
	}
    String begin_task_id=PubFunc.encrypt("0");
%>
<script type="text/javascript">
<!--
	function query()
	{
		//bug 21699 hej upd 20160920 start
		var typevalue ='';  
        var query_type = document.getElementsByName("query_type");
        for(var i = 0;i<query_type.length;i++){  
           if(query_type[i].checked)  
              typevalue = query_type[i].value;  
        }  
        if(typevalue=='2'){
			var start_date = document.getElementById("editor1").value;
			var end_date = document.getElementById("editor2").value;
			if(start_date>end_date){
				alert("开始日期不能大于结束日期！");
				return;
			}
		}
		// end
		var bs_flag=document.getElementsByName("bs_flag")[0].value;
	    taskDeskForm.action="/general/template/task_list.do?b_query=link_query&bs_flag="+bs_flag+"&sp_flag=${taskDeskForm.sp_flag}";
	    taskDeskForm.submit();     		
	}
	
	
	function showlist(sp_flag,ins_id,task_id,tabid,view)
	{
		 var employee="";
		 <%
		 	if(userView.getStatus()==4){
		 %>
		 	employee="&isEmployee=1";
		 <%
		 	}
		 	else
		 	{
		 %>	
		 	if(parent.parent.menupnl)
			 	parent.parent.menupnl.toggleCollapse(false);
		 <%
		 	}
		 %>
		var fromflag = "${taskDeskForm.fromflag}";
		var bs_flag=document.getElementsByName("bs_flag")[0].value;
		if(bs_flag=='2') //加签
		{
		 	 parent.location="/general/template/edit_form.do?b_query=link"+employee+"&type=${taskDeskForm.type}&businessModel=71&sp_flag="+sp_flag+"&ins_id="+ins_id+"&returnflag=${taskDeskForm.fromflag}&taskid="+task_id+"&tabid="+tabid;
		}
		else if(bs_flag=='3') //报备
		{
			 parent.location="/general/template/edit_form.do?b_query=link"+employee+"&type=${taskDeskForm.type}&businessModel=61&sp_flag="+sp_flag+"&ins_id="+ins_id+"&returnflag=${taskDeskForm.fromflag}&taskid="+task_id+"&tabid="+tabid;
		}
		else{
		   //根据view的值判断以列表打开，还是卡片打开 liuzy 20150923
		   if(view=='list'){
			  parent.location="/general/template/templatelist.do?b_init=init"+employee+"&isInitData=1&sp_flag="+sp_flag+"&ins_id="+ins_id+"&returnflag=${taskDeskForm.fromflag}&task_id="+task_id+"&tabid="+tabid+"&index_template=1&wait=1";
	       }else{
	          parent.location="/general/template/edit_form.do?b_query=link"+employee+"&type=${taskDeskForm.type}&businessModel=0&sp_flag="+sp_flag+"&ins_id="+ins_id+"&returnflag=${taskDeskForm.fromflag}&taskid="+task_id+"&tabid="+tabid;
	       }
	    }
	}
	
	
	function _refrash()
	{
		taskDeskForm.action="/general/template/task_list.do?b_query=link&sp_flag=${taskDeskForm.sp_flag}";
	    taskDeskForm.submit();
	}
	
	function yesDelete()
	{
		var str="";
		for(var i=0;i<document.taskDeskForm.elements.length;i++)
		{
			if(document.taskDeskForm.elements[i].type=="checkbox")
			{					
				var ff = taskDeskForm.elements[i].name.substring(0,19);						
				if(document.taskDeskForm.elements[i].checked==true && ff=='taskListForm.select')
				{
					str+=document.taskDeskForm.elements[i+1].value+"/";				
				}
			}
		}
		if(str.length==0)
		{									
			alert("请选择要删除的记录！");				
			return false;
		}
		else
		{
			return ( confirm('确认删除选择的项目？') );
		}
	}
	function returnDh(){
	   parent.parent.location="/general/tipwizard/tipwizard.do?br_ct=link";
	}
	
//-->
</script>
<hrms:themes />
<%	    if ("hcm".equals(bosflag)){	   
%>
<link href="/general/template/template.css" rel="stylesheet" type="text/css"/>
<%} %>
<html:form action="/general/template/task_list">
	<table width="100%" border="0" cellspacing="0" align="center"
		cellpadding="0" class="dbmargin">
	 <%	   
	if ("hcm".equals(bosflag)){	   
	%>
	 <tr >
	<%} else {%>
	
	 <tr height="35px">
	<%
	}%>
			<td >
				<table width="100%" border="0" style="padding-bottom: 3px;" cellspacing="0" 
					cellpadding="0">
					<tr>
							<logic:equal name="taskDeskForm" property="sp_flag" value="1">
								<!--待办  -->
								<td colspan="8" align="left" valign="center" height="24">
							</logic:equal>
							<logic:equal name="taskDeskForm" property="sp_flag" value="2">
								<td colspan="7" align="left" valign="center" height="24">
							</logic:equal><span style="vertical-align: middle;">
							<bean:message key="tab.label.tasktype" />
							</span><span style="vertical-align: middle;">
							<html:select name="taskDeskForm" onchange="query();"
								property="bs_flag" size="1">
								<html:optionsCollection property="bs_flag_list"
									value="dataValue" label="dataName" />
							</html:select></span>
							&nbsp;

							<span style="vertical-align: middle;">
							<html:radio property="query_type" value="1"
								onclick="Element.hide('datepnl');" /></span><span style="vertical-align: middle;">
							<bean:message key="label.by.date" /></span>
							<html:text name="taskDeskForm" property="days" size="2" styleClass="text4"
								onkeyup="this.value=this.value.replace(/\D/g,'')"></html:text><span style="vertical-align: middle;">
							<bean:message key="label.day" /></span><span style="vertical-align: middle;">
							<html:radio property="query_type" value="2"
								onclick="Element.show('datepnl');" /></span><span style="vertical-align: middle;">
							<bean:message key="label.by.time.domain" /></span>
							<span id="datepnl"><bean:message key="label.from" /> <input
									type="text" name="start_date"
									value="${taskDeskForm.start_date}" extra="editor"
									style="width: 100px; font-size: 10pt; text-align: left"
									id="editor1" dropDown="dropDownDate"> <bean:message
									key="label.to" /> <input type="text" name="end_date"
									value="${taskDeskForm.end_date}" extra="editor"
									style="width: 100px; font-size: 10pt; text-align: left"
									id="editor2" dropDown="dropDownDate"> </span><span style="vertical-align: middle;"> &nbsp; 模板名称&nbsp;</span>
									<span style="vertical-align: middle;">
							<html:select name="taskDeskForm" property="templateId" size="1" styleClass="SELECT">
								<html:optionsCollection property="templateList"
									value="dataValue" label="dataName" />
							</html:select></span>
							&nbsp;<span style="vertical-align: middle;">
							<button class="mybutton" onclick="query();" >
								<bean:message key="button.query" />
							</button></span>
							</td>
						</tr>

				</table>
			</td>
		</tr>
		<tr>
			<td>
				<table width="100%" border="0" cellspacing="0" align="center"
					cellpadding="0" class="ListTable">
					<thead>
						
						<tr>
							<td align="center" class="TableRow" nowrap width="3%">
								<input type="checkbox" name="selbox"
									onclick="batch_select(this,'taskListForm.select');"
									title='<bean:message key="label.query.selectall"/>'>
							</td>
							<logic:equal name="taskDeskForm" property="sp_flag" value="1">
								<td align="center" class="TableRow" nowrap width="5%">
									<bean:message key="column.sys.status" />
									&nbsp;
								</td>
							</logic:equal>
							<td align="center" class="TableRow" nowrap width="4%">
								<img src="/images/imail.gif" width="5" height="13">
							</td>
							<td align="center" class="TableRow" nowrap width="4%">
								<img src="/images/quick_query.gif" width="16" height="16">
							</td>
							<!--<td align="center" class="TableRow" nowrap width="4%">
								<img src="/images/amail_1.gif" width="8" height="13">
							</td>
							--><td align="center" class="TableRow" nowrap width="30%">
								<bean:message key="column.sender" />
								&nbsp;
							</td>
							<td align="center" class="TableRow" nowrap width="30%">
								<bean:message key="conlumn.board.topic" />
								&nbsp;
							</td>
							<td align="center" class="TableRow" nowrap width="20%">
								<bean:message key="column.accept.date" />
								&nbsp;
							</td>
							<td align="center" class="TableRow" nowrap width="25%">
								<bean:message key="rsbd.wf.applyunit" />   <!-- 发起单位 -->
								&nbsp;
							</td>
						</tr>
					</thead>


					<hrms:extenditerate id="element" name="taskDeskForm"
						property="taskListForm.list" indexes="indexes"
						pagination="taskListForm.pagination"
						pageCount="${taskDeskForm.pagerows}" scope="session">

						<%
						    if (i % 2 == 0) {
						%>
						<tr class="trShallow" onclick='tr_onclick(this,"#F3F5FC");'>
							<%
							    } else {
							%>
						
						<tr class="trDeep" onclick='tr_onclick(this,"#E4F2FC");'>
							<%
							    }
							%>
							<td align="center" class="RecordRow" nowrap>

								<logic:equal name="element" property="isMessage" value="0">
									<hrms:checkmultibox name="taskDeskForm"
										property="taskListForm.select" value="true" indexes="indexes" />
								</logic:equal>
							</td>


							<logic:equal name="taskDeskForm" property="sp_flag" value="1">
								<td align="center" class="RecordRow" nowrap>
									<bean:write name="element" property="states" filter="false" />
								</td>
							</logic:equal>
							<td align="center" class="RecordRow" nowrap>
								<logic:equal name="element" property="task_pri" value="1">
									<img src="/images/imail.gif" width="5" height="13">
								</logic:equal>
								<logic:equal name="element" property="task_pri" value="0">
									<img src="/images/imailr.gif" width="5" height="13">
								</logic:equal>
							</td>
							<td align="center" class="RecordRow" nowrap>
								<logic:equal name="element" property="bread" value="1">
									<img src="/images/mail1.gif" width="18" height="16" title="已阅读">
								</logic:equal>
								<logic:equal name="element" property="bread" value="0">
									<img src="/images/mail0.gif" width="18" height="16" title="未阅读">
								</logic:equal>
							</td>
							<!--
							<td align="center" class="RecordRow" nowrap>
								<logic:equal name="element" property="bfile" value="1">
									<img src="/images/cc1.gif" width="16" height="16" title="有附件">
								</logic:equal>
							</td>
							-->
							<td align="left" class="RecordRow" nowrap>
								<bean:write name="element" property="a0101_1" filter="false" />
							</td>

							<td align="left" class="RecordRow" nowrap>
								<logic:equal name="element" property="isMessage" value="0">
									<logic:equal name="taskDeskForm" property="sp_flag" value="1"> <!-- 审批任务  -->
										<%
										    if (userView.getVersion() >= 50) {
										%>
										<a
											href="javascript:showlist('${taskDeskForm.sp_flag}','<bean:write name="element" property="ins_id" filter="true"/>','<bean:write name="element" property="task_id" filter="true"/>','<bean:write name="element" property="tabid" filter="false"/>','<bean:write name="element" property="view" filter="false"/>')"><bean:write
												name="element" property="task_topic" filter="false" />
										</a>
										<%
										    } else {
										%>
										<a
											href="/general/template/edit_form.do?b_query=link&businessModel=0&tabid=<bean:write name="element" property="tabid" filter="false"/>&ins_id=<bean:write name="element" property="ins_id" filter="true"/>&taskid=<bean:write name="element" property="task_id" filter="true"/>&sp_flag=${taskDeskForm.sp_flag}&returnflag=${taskDeskForm.fromflag}"
											target="_parent"><bean:write name="element"
												property="task_topic" filter="false" />
										</a>
										<%
										    }
										%>

									</logic:equal>
									<logic:equal name="taskDeskForm" property="sp_flag" value="2"> <!-- 加签任务 -->
										<%
										    if (userView.getVersion() >= 50) {
										%>
										<a
											href="javascript:showlist('${taskDeskForm.sp_flag}','<bean:write name="element" property="ins_id" filter="true"/>','<bean:write name="element" property="task_id" filter="true"/>','<bean:write name="element" property="tabid" filter="false"/>','<bean:write name="element" property="view" filter="false"/>')"><bean:write
												name="element" property="task_topic" filter="false" />
										</a>
										<%
										    } else {
										%>
										<a
											href="/general/template/edit_form.do?b_query=link&businessModel=0&tabid=<bean:write name="element" property="tabid" filter="false"/>&ins_id=<bean:write name="element" property="ins_id" filter="true"/>&taskid=<bean:write name="element" property="task_id" filter="true"/>&sp_flag=${taskDeskForm.sp_flag}&returnflag=4"
											target="_parent"><bean:write name="element"
												property="task_topic" filter="false" />
										</a>
										<%
										    }
										%>

									</logic:equal>
								</logic:equal>
								<logic:equal name="element" property="isMessage" value="1">
									<logic:equal name="taskDeskForm" property="sp_flag" value="1"> <!-- 审批任务  -->
										<%
										    if (userView.getVersion() >= 50) {
										%>
										<a
											href="javascript:showlist('${taskDeskForm.sp_flag}','0','<%=begin_task_id %>','<bean:write name="element" property="tabid" filter="false"/>','<bean:write name="element" property="view" filter="false"/>')"><bean:write
												name="element" property="task_topic" filter="false" />
										</a>
										<%
										    } else {
										%>
										<a
											href="/general/template/edit_form.do?b_query=link&sp_flag=1&businessModel=0&ins_id=0&returnflag=${taskDeskForm.fromflag}&tabid=<bean:write name="element" property="tabid" filter="false"/>"
											target="_parent"><bean:write name="element"
												property="task_topic" filter="false" />
										</a>
										<%
										    }
										%>
									</logic:equal>
									<logic:equal name="taskDeskForm" property="sp_flag" value="2"> <!-- 加签任务 -->
										<%
										    if (userView.getVersion() >= 50) {
										%>
										<a
											href="javascript:showlist('${taskDeskForm.sp_flag}','0','<%=begin_task_id %>','<bean:write name="element" property="tabid" filter="false"/>','<bean:write name="element" property="view" filter="false"/>')"><bean:write
												name="element" property="task_topic" filter="false" />
										</a>
										<%
										    } else {
										%>
										<a
											href="/general/template/edit_form.do?b_query=link&sp_flag=1&businessModel=0&ins_id=0&returnflag=4&tabid=<bean:write name="element" property="tabid" filter="false"/>"
											target="_parent"><bean:write name="element"
												property="task_topic" filter="false" />
										</a>
										<%
										    }
										%>

									</logic:equal>
								</logic:equal>
							</td>
							<td align="left" class="RecordRow" nowrap>
								<bean:write name="element" property="start_date" filter="false" />
							</td>
                            <td align="left" class="RecordRow" nowrap>
								<bean:write name="element" property="unitname" filter="false" />   <!-- 发起单位对应的列 -->
							</td>
						</tr>
						<%
						    i++;
						%>
					</hrms:extenditerate>
				</table>
			</td>
		</tr>
	</table>
	<table  width="100%" align="center" class="RecordRowP">
		<tr>
			 <td valign="middle" class="tdFontcolor">
		            <bean:message key="label.page.serial"/>
					<bean:write name="taskDeskForm" property="taskListForm.pagination.current" filter="true" />
					<bean:message key="label.page.sum"/>
					<bean:write name="taskDeskForm" property="taskListForm.pagination.count" filter="true" />
					<bean:message key="label.page.row"/>
					<bean:write name="taskDeskForm" property="taskListForm.pagination.pages" filter="true" />
					<bean:message key="label.page.page"/>
					
					 每页显示<html:text styleClass="text4" property="pagerows" name="taskDeskForm" size="3"></html:text>条&nbsp;&nbsp;<a href="javascript:_refrash();">刷新</a>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right">
		          <hrms:paginationlink name="taskDeskForm" property="taskListForm.pagination" nameId="taskListForm" >
				</hrms:paginationlink>
			</td>
		</tr>
</table>



      <table    align="center" cellpading="0" >
          <tr height="35px">
          
             <td align="center">
	          <logic:equal name="taskDeskForm" property="bs_flag" value="1">
	          
	          <logic:notEqual name="taskDeskForm" property="sp_flag" value="1">
	         	 <hrms:submit styleClass="button" property="b_delete" function_id="32103">
	            		<bean:message key="button.delete"/>
		 		 </hrms:submit>
		 		 <hrms:tipwizardbutton flag="compensation" target="il_body" formname="taskDeskForm"/>
	           </logic:notEqual>
	           <logic:equal name="taskDeskForm" property="sp_flag" value="1">
	         	 <hrms:submit styleClass="mybutton" property="b_delete" onclick="return yesDelete();" function_id="32103,37003,37103,37203,37303,33001029,33101029,2701529,0C34829,32029,324010129,325010129,010729,3800729">
	            		<bean:message key="button.delete"/>
		 		 </hrms:submit>
	         	 <hrms:submit styleClass="mybutton" property="b_batch" function_id="32102,37002,37102,37202,37302,33001002,33101002,2701502,0C34802,32002,324010101,325010101,010707,3800702">
	            		<bean:message key="button.batapply"/>
		 		 </hrms:submit>
	          </logic:equal>
	         </logic:equal>
            <%
                if (backctrl.equalsIgnoreCase("htbl")) {//合同办理导航图进来的，返回按钮的处理
            %>
                    <input type="button" class="mybutton" value="返回" onclick="returnDh()"/>
            <%
                }
            %>
                </td>
          </tr>          
      </table>

<logic:equal name="taskDeskForm" property="sp_batch" value="1"><!-- 如果是批量审批进来，这个默认被点击一下 -->
	<logic:equal name="taskDeskForm" property="view" value="card">
       <a href="/general/template/edit_form.do?b_query=link&sp_batch=1&businessModel=0&returnflag=${taskDeskForm.fromflag}&sp_flag=${taskDeskForm.sp_flag}<%=otherParam%>&homeflag=0" id="aa" target="_parent" style="display:none">sss</a>
	</logic:equal>
	<logic:notEqual name="taskDeskForm" property="view" value="card">
       <a href="/general/template/templatelist.do?b_init=init&isInitData=1&sp_batch=1&ins_id=1&task_id=${taskDeskForm.taskid}&batch_task=${taskDeskForm.batch_task}&tabid=${taskDeskForm.tabid}&tasklist_str=${taskDeskForm.tasklist_str}&businessModel=0&index_template=1&returnflag=${taskDeskForm.fromflag}&sp_flag=${taskDeskForm.sp_flag}&homeflag=1" id="aa" target="_parent" style="display:none">sss</a>
	</logic:notEqual>
	<script>
		$('aa').click();
	</script>
</logic:equal>
	<script>
		if($F('query_type')=="2")
		  Element.show('datepnl');
		else
		  Element.hide('datepnl');
	</script>
</html:form>
