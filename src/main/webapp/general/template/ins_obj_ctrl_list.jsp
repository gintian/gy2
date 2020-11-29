<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hjsj.hrms.actionform.general.template.TemplateForm"%>

 <link href="/general/template/tasklist/task_list.css" rel="stylesheet" />
<%
    int i = 0;
    String backctrl = "";
    TemplateForm templateForm = (TemplateForm) session.getAttribute("templateForm");
    if (templateForm != null && templateForm.getNavigation().equalsIgnoreCase("htbl")) {
        backctrl = "htbl";
    }
    UserView userView = (UserView) session.getAttribute(WebConstant.userView);
    String bosflag="";
	if(userView != null)
	{
	    bosflag = userView.getBosflag();
	}
%>
<script type="text/javascript" src="/general/template/tasklist/task_list.js"></script>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>

<script type="text/javascript">
<!--
	function query()
	{
	    monitorTaskForm.action="/general/template/ins_obj_list_ctrl.do?b_query=link&fromflag=<%=(request.getParameter("fromflag") != null ? request.getParameter("fromflag") : "")%>&sp_flag=${monitorTaskForm.sp_flag}";
	    monitorTaskForm.submit();     		
	}
        function deletekill()
        {
           if(ifjsho())
           { 
            monitorTaskForm.action="/general/template/ins_obj_list_ctrl.do?b_kill=link&fromflag=<%=(request.getParameter("fromflag") != null ? request.getParameter("fromflag") : "")%>";
	        monitorTaskForm.submit();
	       }
        }
	function selectobject(objecttype)
	{
	  $('actor_type').value=objecttype;  
	  if(objecttype=="3")
	  {
     	     var return_vo=select_org_dialog(0,2,1,0); 
		 if(return_vo)
		 {
		    $('actorid').value=return_vo.content;
		    $('actorname').value=return_vo.title;
		    monitorTaskForm.action="/general/template/ins_obj_list_ctrl.do?b_reassign=link&fromflag=<%=(request.getParameter("fromflag") != null ? request.getParameter("fromflag") : "")%>";
		    monitorTaskForm.submit(); 
		    //return true;
	 	}
	   }else if(objecttype=="4")
	   {
	        var return_vo=select_user_dialog('1','2');
	 	if(return_vo)
	 	{
	 		$('actorname').value=return_vo.title;
	 		$('actorid').value=return_vo.content;	
	 		monitorTaskForm.action="/general/template/ins_obj_list_ctrl.do?b_reassign=link&fromflag=<%=(request.getParameter("fromflag") != null ? request.getParameter("fromflag") : "")%>";
		        monitorTaskForm.submit();  		
	 	}
	      
	   }else if(objecttype=="1")
	   {
	        var return_vo=select_org_emp_dialog(1,2,1,0,0,1);   //select_org_emp_dialog(1,2,1,0);   
		 if(return_vo)
		 {
	 		$('actorname').value=return_vo.title;
	 		$('actorid').value=return_vo.content;
	 		 monitorTaskForm.action="/general/template/ins_obj_list_ctrl.do?b_reassign=link&fromflag=<%=(request.getParameter("fromflag") != null ? request.getParameter("fromflag") : "")%>";
		         monitorTaskForm.submit(); 
	 	}	
	   }else if(objecttype=="2")
	   {
	   var num =0;
	   var tabid = "";
			 for(var i=0;i<document.forms[0].elements.length;i++){			
				if(document.forms[0].elements[i].type=='checkbox'){	
					if(document.forms[0].elements[i].checked == true && document.forms[0].elements[i].name !="selbox"){
						num++;
						tabid = document.forms[0].elements[i+1].value;
					}
				}
			 }
	   		if(num>1){
	   		alert("选择角色时只能选择一条记录!");
	   		return;
	   		}
	       var return_vo=select_role_dialog(1);
               if(return_vo&&return_vo.length>0)
               {  
       		    var rolevo=return_vo[0];
	   	    $('actorname').value=rolevo.role_name;
	   	    $('actorid').value=rolevo.role_id;  
	   	    
	   	    var hashvo=new ParameterSet();			
			hashvo.setValue("value",rolevo.role_id);
			hashvo.setValue("tabid",tabid);
			var request=new Request({method:'post',asynchronous:false,onSuccess:successSub,functionId:'0570010144'},hashvo);
	   	   // monitorTaskForm.action="/general/template/ins_obj_list.do?b_reassign=link";
		   // monitorTaskForm.submit();       	   
	       }
	   }	
	}
	
	function successSub(outparamters)
	{
		var flag=outparamters.getValue("flag");	
		var role_id=outparamters.getValue("role_id");
		var tabid=outparamters.getValue("tabid");
		if(flag=='0')
		{
			monitorTaskForm.action="/general/template/ins_obj_list_ctrl.do?b_reassign=link&fromflag=<%=(request.getParameter("fromflag") != null ? request.getParameter("fromflag") : "")%>";
		    monitorTaskForm.submit();    
		}
		else
		{	
			 
			var obj_vo =window.showModalDialog("/general/template/myapply/busiPage.do?b_select=link&roleid="+role_id+"&role_property="+flag+"&sp_mode=1&tabid="+tabid,null,"dialogWidth=650px;dialogHeight=450px;status:no");  
		        		if(obj_vo&&obj_vo.length>0)
		        		{ 
		        			 $('specialRoleUserStr').value=obj_vo; 
		        				monitorTaskForm.action="/general/template/ins_obj_list_ctrl.do?b_reassign=link";
		        				    monitorTaskForm.submit();   
		        		}
		//	var specialOperate="0";
		//	if(confirm("需按业务模板中人员报送给各自领导进行审批处理吗?"))	
		//	{
		//		specialOperate="1";
		//	}
		//	monitorTaskForm.action="/general/template/ins_obj_list.do?b_reassign=link&specialOperate_self="+specialOperate;
		//    monitorTaskForm.submit();   
			
		}	
	}
	function checkselect()
	{
	   	var isselect=false;
	   	for(var i=0;i<document.monitorTaskForm.elements.length;i++)
		{			
		   if(document.monitorTaskForm.elements[i].type=='checkbox'&&document.monitorTaskForm.elements[i].name!="selbox")
		   {	
			  if(document.monitorTaskForm.elements[i].checked)
			  {
			  	isselect=true;
			  	break;
			  }
		   }
    	}
    	return isselect;
	}
	
	function bdelete()
	{
	  var isselect=checkselect();
	  if(!isselect)
	  {
	  	 alert("没有选中记录！");
	  	 return;
	  }
	  if(ifdel())
	  {
	     monitorTaskForm.action="/general/template/ins_obj_list_ctrl.do?b_delete=link&fromflag=<%=(request.getParameter("fromflag") != null ? request.getParameter("fromflag") : "")%>";
		 monitorTaskForm.submit();  
	  }	 
	}
	
	
	function excecuteExcel()
   {
    	var _records="";
    	for(var i=0;i<document.monitorTaskForm.elements.length;i++)
		{
			if(document.monitorTaskForm.elements[i].type=="checkbox")
			{	
				if(monitorTaskForm.elements[i].name=="selbox"&&document.monitorTaskForm.elements[i].checked==true)
				{
					_records="";
					break;
				}
				
				var ff = monitorTaskForm.elements[i].name.substring(0,17);						
				if(document.monitorTaskForm.elements[i].checked==true && ff=='pagination.select')
				{
					_records+=","+document.monitorTaskForm.elements[i].parentElement.id;			
				}
			}
		} 
		var hashvo=new ParameterSet();			
		hashvo.setValue("sp_flag","${monitorTaskForm.sp_flag}");
		hashvo.setValue("query_type",$F("query_type")+"");
		hashvo.setValue("fromflag","<%=(request.getParameter("fromflag") != null ? request.getParameter("fromflag") : "")%>");
		hashvo.setValue("query_method",$F("query_method")+"");
		hashvo.setValue("days",$F("days")+"");
		hashvo.setValue("start_date",$F("start_date")+"");
		hashvo.setValue("end_date",$F("end_date")+"");
		hashvo.setValue("_records",_records);
		sp_flag="${monitorTaskForm.sp_flag}";
		if(sp_flag=='2')
		{
			hashvo.setValue("templateId",document.monitorTaskForm.templateId.value);
			hashvo.setValue("titlename",getEncodeStr(document.monitorTaskForm.titlename.value));
		}
		hashvo.setValue("type","${monitorTaskForm.type}");
		
		var request=new Request({method:'post',asynchronous:false,onSuccess:showExcel,functionId:'0570010138'},hashvo);
   }	
   
   
   function showExcel(outparamters)
   {
	var url=outparamters.getValue("excelfile");	
//	var win=open("/servlet/DisplayOleContent?filename="+url,"excel");
	//20/3/18 xus vfs改造
	var win=open("/servlet/vfsservlet?fileid="+url+"&fromjavafolder=true","excel");
   }	
   
   
   function IsDigit() 
    { 
    return ((event.keyCode != 96)); 
    } 
    
    
function search(){
	monitorTaskForm.action="/general/template/ins_obj_list_ctrl.do?b_query=link_query&sp_flag=${monitorTaskForm.sp_flag}&name=sc&fromflag=<%=(request.getParameter("fromflag") != null ? request.getParameter("fromflag") : "2")%>";
	monitorTaskForm.submit();
}
function returnDh(){
      parent.parent.location="/general/tipwizard/tipwizard.do?br_ct=link";
}
function _refrash()
{
    monitorTaskForm.action="/general/template/ins_obj_list_ctrl.do?b_query=link&fromflag=<%=(request.getParameter("fromflag") != null ? request.getParameter("fromflag") : "")%>&sp_flag=${monitorTaskForm.sp_flag}";
    monitorTaskForm.submit(); 
}

//流程终止
function processEnd(){
	 var isselect=checkselect();
	  if(!isselect)
	  {
	  	 alert("没有选中记录！");
	  	 return;
	  }
	  if(confirm('确认要终止选中流程吗？'))
	  {
	     monitorTaskForm.action="/general/template/ins_obj_list_ctrl.do?b_processend=link&fromflag=<%=(request.getParameter("fromflag") != null ? request.getParameter("fromflag") : "")%>";
		 monitorTaskForm.submit();  
	  }	 
}



//-->
</script>
<hrms:themes/>
<%	   
if ("hcm".equals(bosflag)){	   
%>
<link href="/general/template/template.css" rel="stylesheet" type="text/css"/>
<%} %>
<html:form action="/general/template/ins_obj_list_ctrl">
   <html:hidden name="monitorTaskForm" property="actorid"/>
   <html:hidden name="monitorTaskForm" property="actorname"/>
   <html:hidden name="monitorTaskForm" property="actor_type"/>
   <html:hidden name="monitorTaskForm" property="specialRoleUserStr"/>

	<table width="100%" border="0" cellspacing="0" align="center"
		cellpadding="0" class="ybmargin">
	 <%	   
	if ("hcm".equals(bosflag)){	   
	%>
	 <tr valign="middle">
	<%} else {%>
	
	 <tr height="35px" valign="middle">
	<%}%>
			<td>
				<table width="100%" border="0" cellspacing="0" align="center" cellpadding="0">
		
				<logic:notEqual name="monitorTaskForm" property="sp_flag" value="3"><!-- 不是已办任务 -->
					<tr>
					 <td colspan="9" valign ="top" align="left" height="15">
						<bean:message key="task.state" />
						<html:radio property="query_method" value="1" onclick="query();" />
						<bean:message key="task.state.run" />
						<html:radio property="query_method" value="3" onclick="query();" />
						已终止
						<html:radio property="query_method" value="2" onclick="query();" />
						<bean:message key="task.state.finished" />&nbsp;
			   	  	 	<html:radio property="query_type" value="1" onclick="Element.hide('datepnl');" />
						<bean:message key="label.by.date" />
						<html:text name="monitorTaskForm" property="days" size="2" styleClass="text4" style="margin-bottom:4px;"></html:text>
						<bean:message key="label.day" />
						<html:radio property="query_type" value="2"	onclick="Element.show('datepnl');"/>
						<bean:message key="label.by.time.domain" />
						<span id="datepnl">
						   <bean:message key="label.from" /> 
						   <input type="text" name="start_date" value="${monitorTaskForm.start_date}" extra="editor"
						       style="width: 100px; font-size: 10pt; text-align: left;margin-bottom:4px;" id="editor1" dropDown="dropDownDate"> 
						   <bean:message key="label.to" /> 
						   <input type="text" name="end_date"value="${monitorTaskForm.end_date}" extra="editor"
							  style="width: 100px; font-size: 10pt; text-align: left;margin-bottom:4px;" id="editor2" dropDown="dropDownDate"> 
					  </span>
		  	  	 		  &nbsp;模板名称&nbsp;
		  	  	 		  <html:select name="monitorTaskForm" property="templateId" size="1" style="vertical-align:middle;">
						<html:optionsCollection property="templateList" value="dataValue" label="dataName" />
					  </html:select>&nbsp;
					  <bean:message key="rsbd.wf.name" />&nbsp;<span style="vertical-align: top">
		  	  	 		  <html:text name="monitorTaskForm" property="titlename" size="9" maxlength="30" onkeypress="event.returnValue=IsDigit();" 
		  	  	 		      styleClass="text4" style="vertical-align:bottom;margin-bottom:4px;"/>
					 </span>&nbsp;
					 <button class="mybutton" onclick='search()'><bean:message key="button.query" /></button>
					</td>
					</tr>
					</logic:notEqual>
				</table>
			</td>
		</tr>
		<tr>
			<td>
				<table width="100%" border="0" cellspacing="0" align="center" cellpadding="0" class="ListTable">
					<thead>
						<tr>
							<td align="center" class="TableRow" nowrap>
								<input type="checkbox" name="selbox"onclick="batch_select(this,'pagination.select');" title='<bean:message key="label.query.selectall"/>'>
								&nbsp;
							</td>

							<td align="center" class="TableRow" nowrap>
								<logic:notEqual name="monitorTaskForm" property="sp_flag" value="3">
									<bean:message key="rsbd.wf.name" />&nbsp;   
               </logic:notEqual>
								<logic:equal name="monitorTaskForm" property="sp_flag" value="3">
									<bean:message key="conlumn.board.topic" />&nbsp;                   
          	    </logic:equal>
							</td>
							<td align="center" class="TableRow" nowrap>

								<bean:message key="rsbd.wf.applyemp" />  <!-- 申请人 -->
								&nbsp;


							</td>
							<td align="center" class="TableRow" nowrap>
								<bean:message key="rsbd.wf.applyunit" />   <!-- 发起单位 -->
								&nbsp;
							</td>
							<td align="center" class="TableRow" nowrap width="15%">
								<bean:message key="general.template.applyStartDate" />
								&nbsp;
							</td>
							<td align="center" class="TableRow" nowrap width="15%">
								<bean:message key="general.template.spEndDate" />
								&nbsp;
							</td>
							<logic:notEqual name="monitorTaskForm" property="sp_flag" value="3">
								<td align="center" class="TableRow" nowrap width="10%">
									<bean:message key="rsbd.task.curremp" />
									&nbsp;
								</td>
							</logic:notEqual>
							<td align="center" class="TableRow" nowrap width="6%">
								<bean:message key="rsbd.wf.sploop" />
								&nbsp;
							</td>
							<td align="center" class="TableRow" nowrap width="6%">
								浏览打印
							</td>
							<td align="center" class="TableRow" nowrap width="6%">
								<bean:message key="task.state" />
								&nbsp;
							</td>
						</tr>
					</thead>
					<hrms:paginationdb id="element" name="monitorTaskForm"
						sql_str="monitorTaskForm.strsql" table="" where_str=""
						columns="monitorTaskForm.columns" order_by="monitorTaskForm.order_sql"
						page_id="pagination" pagerows="${monitorTaskForm.pagerows}" distinct="" keys=""
						indexes="indexes">
						<bean:define id="task_id" name="element" property="task_id" />
						<%
							String _task_id=PubFunc.encrypt(task_id.toString());
						%>
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
							                i++;
							%>
							<td align="center" class="RecordRow" id='<%=_task_id%>' nowrap>
							<!-- <td align="center" class="RecordRow" id='<bean:write name="element" property="task_id" filter="false"/>' nowrap> -->

								<hrms:checkmultibox name="monitorTaskForm"
									property="pagination.select" value="true" indexes="indexes" />
								&nbsp;
								<Input type='hidden'
									value='<bean:write name="element" property="tabid" filter="false"/>'
									name='tabid' />

							</td>

							<td align="left" class="RecordRow" nowrap>
								<bean:write name="element" property="name" filter="false" />
							</td>
							<td align="left" class="RecordRow" nowrap>
								<bean:write name="element" property="fullname" filter="false" />
							</td>
							<td align="left" class="RecordRow" nowrap>
								<bean:write name="element" property="unitname" filter="false" />   <!-- 发起单位对应的列 -->
							</td>
							<td align="left" class="RecordRow" nowrap>
								<bean:write name="element" property="ins_start_date"
									filter="false" />
							</td>
							<td align="left" class="RecordRow" nowrap>
								<bean:write name="element" property="ins_end_date"
									filter="false" />
							</td>
							
							<logic:notEqual name="monitorTaskForm" property="sp_flag" value="3">
								<td align="left" class="RecordRow" nowrap>
							     <logic:equal name="element" property="actor_type" value="2">
							     <A  id="a_role" href="javascript:void(0)" onclick="displayRoleInfo(this,'<bean:write name="element" property="tabid" filter="false"/>','<%=_task_id%>')"> 
							         <bean:write name="element" property="actorname" filter="false" />
							     </A>
                                </logic:equal>
                                <logic:notEqual name="element" property="actor_type" value="2">
                                    <bean:write name="element" property="actorname" filter="false" />
                                </logic:notEqual>
									<!-- 
									<logic:equal name="monitorTaskForm" property="query_method" value="2">  
                                       <bean:write name="element" property="a0101" filter="false" />
									</logic:equal>
									 -->
								</td>
							</logic:notEqual>
							<td align="center" class="RecordRow" nowrap>
							<!--安全平台改造，加密相应的参数 -->
								<a
									href="/general/template/view_process.do?b_query=link&from=rwjk&tabid=<bean:write name="element" property="tabid" filter="false"/>&taskid=<%=_task_id%>&ins_id=<bean:write name="element" property="ins_id" filter="false"/>"><img
										src="/images/view.gif" width="16" height="16" border="0">
								</a>
							</td>
							<td align="center" class="RecordRow">
								<logic:equal name="monitorTaskForm" property="sp_flag" value="1">
									<a
										href="/general/template/edit_form.do?b_query=link&model=myApply&tabid=<bean:write name="element" property="tabid" filter="false"/>&ins_id=<bean:write name="element" property="ins_id" filter="true"/>&taskid=<%=_task_id%>&sp_flag=2&returnflag=${monitorTaskForm.fromflag}"
										target="_parent"><img src="/images/view.gif" width="16"
											height="16" border="0">
									</a>
								</logic:equal>
								<logic:notEqual name="monitorTaskForm" property="sp_flag" value="1">
									<a
										href="/general/template/edit_form.do?b_query=link&tabid=<bean:write name="element" property="tabid" filter="false"/>&ins_id=<bean:write name="element" property="ins_id" filter="true"/>&taskid=<%=_task_id%>&sp_flag=2&returnflag=${monitorTaskForm.fromflag}"
										target="_parent"><img src="/images/view.gif" width="16"
											height="16" border="0">
									</a>
								</logic:notEqual>
							</td>
							<td align="left" class="RecordRow" nowrap>
								<logic:equal name="element" property="finished" value="6">  
          		已完成
          		</logic:equal>
								<hrms:codetoname codeid="38" name="element" codevalue="finished"
									codeitem="codeitem" scope="page" />
								<bean:write name="codeitem" property="codename" />



							</td>
						</tr>
					</hrms:paginationdb>
				</table>
			</td>
		</tr>
	</table>
	<table  width="100%" align="center" class="RecordRowP">
		<tr>
		    <td valign="bottom" class="tdFontcolor">
		            <bean:message key="label.page.serial"/>
					<bean:write name="pagination" property="current" filter="true" />
					<bean:message key="label.page.sum"/>
					<bean:write name="pagination" property="count" filter="true" />
					<bean:message key="label.page.row"/>
					<bean:write name="pagination" property="pages" filter="true" />
					<bean:message key="label.page.page"/>
					 每页显示<html:text styleClass="text4" property="pagerows" name="monitorTaskForm" size="3"></html:text>条&nbsp;&nbsp;<a href="javascript:_refrash();">刷新</a>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationdblink name="monitorTaskForm" property="pagination" nameId="monitorTaskForm" scope="page">
				</hrms:paginationdblink>
			</td>
		</tr>
</table>

      <table  width="100%" align="center">
          <tr height="35px">
            <td align="center">
            

	        <logic:equal name="monitorTaskForm" property="sp_flag" value="2"><!--从任务监控进来的 -->
            <logic:equal name="monitorTaskForm" property="query_method" value="1">
	        <hrms:priv func_id="270160,33001033,324010202,325010202,32131,32010,33101105,230672904,2311022904">
          		<button extra="button" class="mybutton" allowPushDown="false" onclick="processEnd();" function_id="33001033,324010202,325010202,32131,32010,33101105,230672904,2311022904" down="false" style="margin-right:10px">流程终止</button>
	        </hrms:priv>
              <hrms:priv func_id="270162,33001012,33101101,32012,32112,37012,37112,37212,37312,324010201,325010201,230672902,2311022902">
		        <button extra="button" class="mybutton" allowPushDown="false" onclick="bdelete();" down="false" function_id="32103" style="margin-right:10px"><bean:message key="button.delete"/></button>
	        </hrms:priv>   
            <!--	        	        
	            <hrms:priv func_id="32110,32010,324010202,325010202">
	              <button extra="button" allowPushDown="false" onclick="deletekill();" down="false" function_id="32010,32110"><bean:message key="button.kill"/></button>
		        </hrms:priv>   
	-->
	         	  <!--<hrms:submit styleClass="mybutton" property="b_kill" function_id="32010,32110">
	            		<bean:message key="button.kill"/>
		 		 </hrms:submit>
		 		 -->
	            <hrms:priv func_id="270161,33001011,33101103,32011,32111,324010203,325010203,230672904,2311022904">
	    	 	  <button extra="button" class="mybutton" menu="menu2" allowPushDown="false"  down="false" function_id="33001011,33101103,32011,32111,230672904,2311022904" style="margin-right:10px"><bean:message key="button.reassign"/></button>	 
	
		        <hrms:menubar menu="menu2" id="menubar2" container="" visible="false">
	       		    <hrms:menuitem name="mitem1" label="task.selectobject.orgcell" icon="" url="selectobject('3');" command="" enabled="true" visible="true"/>
	       		    <hrms:menuitem name="mitem2" label="task.selectobject.user" icon="" url="selectobject('4');" command="" enabled="true" visible="true"/>
	       		    <hrms:menuitem name="mitem3" label="task.selectobject.personnel" icon="" url="selectobject('1');" command="" enabled="true" visible="true"/>
	       		    <hrms:menuitem name="mitem3" label="task.selectobject.role" icon="" url="selectobject('2');" command="" enabled="true" visible="true"/>
	  	        </hrms:menubar> 
	  	        	        </hrms:priv>     
	         	<!-- 
	         	  <hrms:submit styleClass="mybutton" property="b_reassign" function_id="32011,32111" onclick="return selectobject();">
	            		<bean:message key="button.reassign"/>
		 	 </hrms:submit>
		 	 -->
		 	
           </logic:equal>
           <logic:equal name="monitorTaskForm" property="query_method" value="2">
              <hrms:priv func_id="270167,33001027,33101102,32027,32127,324010227,325010227,230672903,2311022903">
		        <button extra="button" class="mybutton" allowPushDown="false" onclick="bdelete();" down="false" function_id="32103" style="margin-right:10px"><bean:message key="button.delete"/></button>
	        </hrms:priv>
           </logic:equal>	
           <logic:equal name="monitorTaskForm" property="query_method" value="3">
              <hrms:priv func_id="270167,33001027,33101102,32027,32127,324010227,325010227,230672903,2311022903">
		        <button extra="button" class="mybutton" allowPushDown="false" onclick="bdelete();" down="false" function_id="32103" style="margin-right:10px"><bean:message key="button.delete"/></button>
	        </hrms:priv>
           </logic:equal>	 	 
            <hrms:priv func_id="270163,33001013,33101104,32013,32113,324010204,3254010204,230672905,2311022905">
           		<button extra="button" class="mybutton" allowPushDown="false"  onclick="excecuteExcel();" down="false" style="margin-right:10px"><bean:message key="goabroad.collect.educe.excel"/></button>
           </hrms:priv>
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
<script>
		if($F('query_type')=="2")
		  Element.show('datepnl');
		else
		  Element.hide('datepnl');
	</script>	

 
  
<div id="roleInfoDiv" class="roleInfoDiv " style="display:none"   >
    <div class="roleInfoDiv-top"></div>
    <div class="roleInfoDiv-midden">   
       <div id="roleInfoDivContent" class='roleInfoDivContent common_border_color'>
             <DIV class="role_title common_background_color">审批人</DIV>
             <DIV id="role_div_people" class="role_people">                      
                <table border="0"  cellspacing="0"    cellpadding="0" width="100%" >
                 <tr height="20">
                     <td align="left" width="50%"  nowrap ><IMG  align="left"  src="/images/edit.gif">1</td>
                     <td align="left" width="50%"  nowrap >&nbsp;&nbsp;1</td>
                 </tr>
                 <tr height="20">
                     <td align="left" width="50%"  nowrap ><IMG  align="left"  src="/images/edit.gif">2</td>
                     <td align="left" width="50%"  nowrap >&nbsp;&nbsp;2</td>
                 </tr>
                </table>                      
             </DIV>  
             <DIV class="role_title common_background_color">审批意见</DIV>
             <DIV id="role_div_content" class="role_people">   
                <table border="0"  cellspacing="0"    cellpadding="0" width="100%" >
                        <tr height="20">        
                            <td align="left"  >
                            张三 
                            </td>
                        </tr>
                 </table>     
            </DIV>
       </div>  
       <div id="roleInfoDivWait" class='roleInfoDivWait'>
           <table border="0" width="100%" cellspacing="0" cellpadding="4"  height="150px" align="center">
            <tr>
                <td class="td_style common_background_color" height=24>
                    正在加载数据，请稍候...
                </td>
            </tr>
            <tr>
                <td style="font-size:12px;line-height:200%" align=center>
                    <marquee class="marquee_style" direction="right" width="100%" scrollamount="5" scrolldelay="10" >
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
             

    </div>
    <div class="roleInfoDiv-bottom"></div>
    <div class="roleInfoDiv-right" id="task_right"><img src="/general/template/tasklist/right.png" /></div>
</div>

</html:form>

<script language='javascript' >
    document.onclick = function (event)  
    {     
        var e = event || window.event;  
        var elem = e.srcElement||e.target;  
               
        while(elem)  
        {   
            if(elem.id == "roleInfoDiv"|| elem.id ==  'a_role')  
            {  
                return;  
            }  
            elem = elem.parentNode;       
        }  
        
        hideDiv('roleInfoDiv');  
    }
</script>

<script language='javascript' >
 
	for(var i=0;i<document.monitorTaskForm.elements.length;i++)
	{
		if(document.monitorTaskForm.elements[i].type=='button')
		{
			document.monitorTaskForm.elements[i].style.color='#000000'
		}
	}
</script>