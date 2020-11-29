
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*,com.hrms.struts.constant.WebConstant,
 				 org.apache.commons.beanutils.LazyDynaBean,com.hrms.struts.valueobject.UserView,
				 com.hjsj.hrms.actionform.general.template.TemplateForm,com.hjsj.hrms.actionform.general.template.TaskDeskForm"%>
<script type="text/javascript">

        function test(name)
	{
		var obj=$('task_desk');
		obj.setSelectedTab(name);
	}
</script>
<%
	String operate=null;
	String aaa="&bs_flag=1";
	String backLink="link";
	String queryType="1";
	String templateid="";  //个性化开发，如链接带此参数，只显示该模板下的记录
	if(request.getParameter("operate")!=null)
		   operate=request.getParameter("operate");
	if(request.getParameter("templateid")!=null)
		   templateid=request.getParameter("templateid");
	
	
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
 	if(operate!=null&&operate.equals("task2")){//如果不是返回到待办,就不重置待办的默认天数，后台From中有数据的处理 
 	   backLink="backlick";
 	}
 	request.setAttribute("backlick",backLink);
    TemplateForm templateForm=(TemplateForm)session.getAttribute("templateForm");
    if(templateForm!=null&&templateForm.getType()!=null){
	    String type=templateForm.getType();
	    request.setAttribute("type",type);
    }else
    	if(request.getParameter("type")!=null)
    		request.setAttribute("type",request.getParameter("type"));
    if(templateid.length()>0)
	{
		request.setAttribute("type",templateid);
	}
	UserView userView = (UserView) pageContext.getSession().getAttribute(
	WebConstant.userView);
   int versionFlag = 1;
   //zxj 20160613 人事异动不再区分标准版专业版
   //		if (userView != null)
	//		versionFlag = userView.getVersion_flag(); // 1:专业版 0:标准版	
%>	
<hrms:themes />
<body <%=(operate!=null?"onload=\"test('"+operate+"')\"":""  )%> >
<html:form action="/general/template/task_desktop">
<hrms:tabset name="task_desk" width="100%" height="100%" type="true"> 
	  <hrms:tab name="task1" label="tab.label.dbtask" visible="true" url="/general/template/task_list.do?b_query=link${aaa}&sp_flag=1&fromflag=1&type=${type}">
      </hrms:tab>	
	  <hrms:tab name="task2" label="tab.label.ybtask" visible="true" url="/general/template/ins_obj_list2.do?b_query2=${backLink}${aaa}&sp_flag=3&fromflag=4&type=${type}">
      </hrms:tab>
      <hrms:tab name="task3" label="tab.label.mymsg" visible="true"   function_id="3300104,331014,3204,3215,3240104,3250104,3705,3715,3725,3735" url="/system/warn/myinfo_all.do?b_query=link&type=${type}">
      </hrms:tab> 
      <% if(templateid.length()==0){ %>        
	  <hrms:tab name="task4" label="tab.label.myapply" visible="true" function_id="3300105,331015,3205,3216,3240105,3250105,3706,3716,3726,3736,38008,2306728,23110228" url="/general/template/ins_obj_list.do?b_query=link&sp_flag=1&fromflag=3&type=${type}">
      </hrms:tab>
      <% }else{ %>
      <hrms:tab name="task4" label="tab.label.myapply" visible="true"   url="/general/template/ins_obj_list.do?b_query=link&sp_flag=1&fromflag=3&type=${type}">
      </hrms:tab>
      <% } %>
      
	  <hrms:tab name="task5" label="tab.label.ctrltask" visible="true" function_id="27016,3300101,331011,3201,3211,3240102,3250102,3701,3711,3721,3731,2306729,23110229" url="/general/template/ins_obj_list_ctrl.do?b_query=link&sp_flag=2&fromflag=2&init=1&type=${type}">
      </hrms:tab> 
        <%
			 if(userView.getVersion()>=50&&versionFlag==1){
			  }else{ %>
      <hrms:tab name="task6" label="tab.label.defflow" visible="true"  function_id="3214,3300102,331012,3202,3240103,3250103,3704,3714,3724,3734" url="/general/template/nodedefine/searchtemplatetable.do?b_search=link&type=${type}">
      </hrms:tab> 
      <%} %>
</hrms:tabset>
</html:form>
</body>
