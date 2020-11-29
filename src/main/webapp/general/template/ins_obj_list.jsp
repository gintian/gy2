<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hjsj.hrms.actionform.general.template.TemplateForm,com.hjsj.hrms.actionform.general.template.TaskDeskForm"%>
 <link href="/general/template/tasklist/task_list.css" rel="stylesheet" />
<!--我的申请页面 -->
<%
    int i = 0;
    String backctrl = "";
    TemplateForm templateForm = (TemplateForm) session.getAttribute("templateForm");
    TaskDeskForm taskDeskForm=(TaskDeskForm) session.getAttribute("ownerApplyForm");
    if (templateForm != null && templateForm.getNavigation().equalsIgnoreCase("htbl")) {
        backctrl = "htbl";
    }
    String type=taskDeskForm.getType();  //个性配置,指定只显示某些模板的待办
    String otherParam="";
    if(type!=null && type.length()>0 && type.charAt(0)=='t')
    	otherParam="&type="+type;
    
    
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
function query(){
	    ownerApplyForm.action="/general/template/ins_obj_list.do?b_query=link_query&fromflag=<%=(request.getParameter("fromflag") != null ? request.getParameter("fromflag") : "")%>&sp_flag=${ownerApplyForm.sp_flag}";
	    ownerApplyForm.submit();     		
}
function deletekill(){
    if(ifjsho()){ 
        ownerApplyForm.action="/general/template/ins_obj_list.do?b_kill=link&fromflag=<%=(request.getParameter("fromflag") != null ? request.getParameter("fromflag") : "")%>";
	     ownerApplyForm.submit();
	}
} 
function IsDigit(){ 
    return ((event.keyCode != 96)); 
} 
function search(){
	ownerApplyForm.action="/general/template/ins_obj_list.do?b_query=link&sp_flag=${ownerApplyForm.sp_flag}&name=sc&fromflag=<%=(request.getParameter("fromflag") != null ? request.getParameter("fromflag") : "")%>";
	ownerApplyForm.submit();
}
function returnDh(){
      parent.parent.location="/general/tipwizard/tipwizard.do?br_ct=link";
}
function _refrash()
{
    ownerApplyForm.action="/general/template/ins_obj_list.do?b_query=link_query&fromflag=<%=(request.getParameter("fromflag") != null ? request.getParameter("fromflag") : "")%>&sp_flag=${ownerApplyForm.sp_flag}";
    ownerApplyForm.submit(); 
}
//-->
</script>
<hrms:themes/>
<%	   
if ("hcm".equals(bosflag)){	   
%>
<link href="/general/template/template.css" rel="stylesheet" type="text/css"/>
<%} %>
<html:form action="/general/template/ins_obj_list">
   <html:hidden name="ownerApplyForm" property="actorid"/>
   <html:hidden name="ownerApplyForm" property="actorname"/>
   <html:hidden name="ownerApplyForm" property="actor_type"/>
   <html:hidden name="ownerApplyForm" property="specialRoleUserStr"/>

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
							<logic:notEqual name="ownerApplyForm" property="sp_flag" value="3"><!-- 不是已办任务 -->
								<tr>
									<logic:equal name="ownerApplyForm" property="sp_flag" value="2"><!-- 2: 任务监控-->
										<td colspan="9" valign ="top" align="left" height="15">
									</logic:equal>
									<logic:equal name="ownerApplyForm" property="sp_flag" value="1"><!-- 1:我的申请 -->
										<td colspan="9" valign ="top"  align="left" height="15">
									</logic:equal>
									<bean:message key="task.state" />
									<html:radio property="query_method" value="1" onclick="query();" />
									<bean:message key="task.state.run" />
									<html:radio property="query_method" value="3" onclick="query();" />
									已终止
									<html:radio property="query_method" value="2" onclick="query();" />
									<bean:message key="task.state.finished" />
						                 </td>
					           </tr>
					        </logic:notEqual>
				</table>
			</td>
		</tr>
		<tr>
			<td>
				<table width="100%" border="0" cellspacing="0" align="center"
					cellpadding="0" class="ListTable">

					<thead>

						<tr>

							<td align="center" class="TableRow" nowrap>
								<input type="checkbox" name="selbox"onclick="batch_select(this,'pagination.select');" title='<bean:message key="label.query.selectall"/>'>
								&nbsp;
							</td>

							<td align="center" class="TableRow" nowrap>
								<logic:notEqual name="ownerApplyForm" property="sp_flag" value="3">
									<bean:message key="rsbd.wf.name" />&nbsp;   
                                </logic:notEqual>
								<logic:equal name="ownerApplyForm" property="sp_flag" value="3">
									<bean:message key="conlumn.board.topic" />&nbsp;                   
          	                    </logic:equal>
							</td>
							<td align="center" class="TableRow" nowrap>
								<bean:message key="rsbd.wf.applyemp" />
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
							<logic:notEqual name="ownerApplyForm" property="sp_flag" value="3">
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
					<hrms:paginationdb id="element" name="ownerApplyForm"
						sql_str="ownerApplyForm.strsql" table="" where_str=""
						columns="ownerApplyForm.columns" order_by="ownerApplyForm.order_sql"
						page_id="pagination" pagerows="${ownerApplyForm.pagerows}" distinct="" keys=""
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

								<hrms:checkmultibox name="ownerApplyForm"
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
								<bean:write name="element" property="ins_start_date"
									filter="false" />
							</td>
							<td align="left" class="RecordRow" nowrap>
								<bean:write name="element" property="ins_end_date"
									filter="false" />
							</td>
							<logic:notEqual name="ownerApplyForm" property="sp_flag" value="3">
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
									<logic:equal name="ownerApplyForm" property="query_method" value="2">   
                                        <bean:write name="element" property="a0101" filter="false" />
									</logic:equal>
									 -->
								</td>
							</logic:notEqual>
							<td align="center" class="RecordRow" nowrap>
							<!--安全平台改造，加密相应的参数 -->
								<a href="/general/template/view_process.do?b_query=link&from=rwjk&tabid=<bean:write name="element" property="tabid" filter="false"/>&taskid=<%=_task_id%>&ins_id=<bean:write name="element" property="ins_id" filter="false"/>">
								    <img src="/images/view.gif" width="16" height="16" border="0">
								</a>
							</td>
							<td align="center" class="RecordRow">
								<logic:equal name="ownerApplyForm" property="sp_flag" value="1"><!-- 这个代表来自于我的申请,那么下面那个就是来至于任务监控,可以去掉了 -->
									<a href="/general/template/edit_form.do?b_query=link&model=myApply&tabid=<bean:write name="element" property="tabid" filter="false"/>&ins_id=<bean:write name="element" property="ins_id" filter="true"/>&taskid=<%=_task_id%>&sp_flag=2&returnflag=${ownerApplyForm.fromflag}<%=otherParam%>"
										target="_parent">
										<img src="/images/view.gif" width="16" height="16" border="0">
									</a>
								</logic:equal>
								<!-- 对人事异动重新进行了分页的处理,任务监控和我的申请不再用同一界面,所以这里可以去掉了
								<logic:notEqual name="ownerApplyForm" property="sp_flag" value="1">
									<a
										href="/general/template/edit_form.do?b_query=link&tabid=<bean:write name="element" property="tabid" filter="false"/>&ins_id=<bean:write name="element" property="ins_id" filter="true"/>&taskid=<%=_task_id%>&sp_flag=2&returnflag=${ownerApplyForm.fromflag}"
										target="_parent">
										<img src="/images/view.gif" width="16" height="16" border="0">
									</a>
								</logic:notEqual>
								 -->
							</td>
							<td align="left" class="RecordRow" nowrap>
								<logic:equal name="element" property="finished" value="6">  
          		                                                等待
          		                </logic:equal>
								<hrms:codetoname codeid="38" name="element" codevalue="finished" codeitem="codeitem" scope="page" />
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
					 每页显示<html:text styleClass="text4" property="pagerows" name="ownerApplyForm" size="3"></html:text>条&nbsp;&nbsp;<a href="javascript:_refrash();">刷新</a>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationdblink name="ownerApplyForm" property="pagination" nameId="ownerApplyForm" scope="page">
				</hrms:paginationdblink>
			</td>
		</tr>
</table>

      <table  width="100%" align="center">
          <tr height="35px">
            <td align="center">
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
	                            <td align="left"  nowrap >
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
 
	for(var i=0;i<document.ownerApplyForm.elements.length;i++)
	{
		if(document.ownerApplyForm.elements[i].type=='button')
		{
			document.ownerApplyForm.elements[i].style.color='#000000'
		}
	}
</script>