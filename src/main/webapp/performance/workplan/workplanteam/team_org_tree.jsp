<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hjsj.hrms.actionform.performance.workPlanTeam.WorkPlanTeamForm,java.util.*"%>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null){
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	 css_url="/css/css1.css";
	}
	
	WorkPlanTeamForm form=(WorkPlanTeamForm)session.getAttribute("workPlanTeamForm"); 
	String workType = (String)form.getWorkType();
	String state = (String)form.getState();
	String operOrg = userView.getUnit_id();
	String showUnitCodeTree="0";//是否按操作单位来显示树
	if (operOrg!=null && operOrg.length() > 3){
		showUnitCodeTree="1";
	}
	String url="/performance/workplan/workplanteam/workplan_team_list.do?b_query=link&workType="+workType+"&state="+state;
	
%>

<script LANGUAGE=javascript src="/js/xtree.js"></script> 
<hrms:themes />
<link href="/css/xtree.css" rel="stylesheet" type="text/css">
<html:form action="/performance/workplan/workplanteam/team_org_tree"> 

  <table width="100%" border="0" cellspacing="1"  align="center" cellpadding="1" >   
	 <tr align="left">
		<td valign="top"></td>
	 </tr>          
     <tr>        
           <td align="left"> 
                 <hrms:orgtree action="<%=url %>" target="mil_body" flag="0" viewunit="<%=showUnitCodeTree%>" loadtype="1" priv="1" showroot="false" dbpre="" rootaction="1" rootPriv="0"/>			           
           </td>
      </tr>            
   </table>
</html:form>
<script>
<% if(request.getParameter("b_opt")==null){ %>
	
	root.openURL();
	
<%  } %>
</script>
