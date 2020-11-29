
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant,
				com.hrms.struts.constant.SystemConfig"%>
<%
	String operate=null;
	if(request.getParameter("operate")!=null)
		   operate=request.getParameter("operate");
	UserView userView=(UserView)session.getAttribute(WebConstant.userView); 
	String aaa="&bs_flag=1";
	String backLink="link";
	if(request.getParameter("businessModel")!=null)
    {
        String businessModel=request.getParameter("businessModel");
        if(businessModel.equals("61")||businessModel.equals("62"))
        {
            aaa="&bs_flag=3";
        }
        else if(businessModel.equals("71")||businessModel.equals("72"))
        {
            aaa="&bs_flag=2";
        }
    }
    request.setAttribute("aaa",aaa);
    if(operate!=null){
        backLink="backlick";
     }
     request.setAttribute("backlick",backLink);
	String type="";
	if(request.getParameter("type")!=null) //链接参数带上type=23时，待办任务  、已办任务、我的申请三个选项卡，同时只列出与考勤业务关联的数据
	{
		   type=request.getParameter("type");
		   if(type.equals("23"))
		   		type="&type=23";
		   	if(type.equals("24"))
		   		type="&type=24";
	}
	request.setAttribute("type",type);
	if(!(userView.hasTheFunction("0107")||userView.hasTheFunction("320")||userView.hasTheFunction("3210")||userView.hasTheFunction("32401")))
	{
		out.println("<br><br><p align='center' >无业务申请功能权限!</p>");
	
	}
	else
	{
 %>
<script type="text/javascript">

        function test(name)
	{
		var obj=$('taskd_desk');
		obj.setSelectedTab(name);
	}
</script>
<hrms:themes />
<body <%=(operate!=null?"onload=\"test('"+operate+"')\"":""  )%>>
<html:form action="/general/template/myapply/busidesktop" style="height:100%;margin-left:-1px;">
<hrms:tabset name="taskd_desk" width="100%" height="100%" type="true"> 
	  <hrms:tab name="dtask1" label="tab.label.dbtask" visible="true" url="/general/template/task_list.do?b_query=link${aaa}&sp_flag=1&fromflag=6${type}">
      </hrms:tab>	
	  <hrms:tab name="dtask2" label="tab.label.ybtask" visible="true" url="/general/template/ins_obj_list2.do?b_query2=${backLink}${aaa}&sp_flag=3&fromflag=6${type}">
      </hrms:tab>
	  <hrms:tab name="dtask4" label="tab.label.myapply" visible="true"  function_id="010706" url="/general/template/ins_obj_list.do?b_query=link&sp_flag=1&fromflag=6${type}">
      </hrms:tab>   
      <% if(!type.equals("&type=23")){ %>   
      <hrms:tab name="dtask3" label="myapply.bussinessname" visible="true"  url="/general/template/myapply/businessApplyList.do?b_query=link&operationcode=${type}">
      </hrms:tab>         
      <% } %>
</hrms:tabset>

</html:form>
</body>
<% 
	}
 %>
