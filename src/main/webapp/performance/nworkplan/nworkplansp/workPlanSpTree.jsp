<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant,
 com.hjsj.hrms.actionform.performance.nworkplan.nworkplansp.WorkPlanSpForm"%>

<%
String userName = null;
String css_url="/css/css1.css";
UserView userView=(UserView)session.getAttribute(WebConstant.userView);
if(userView != null){
  css_url=userView.getCssurl();
  if(css_url==null||css_url.equals(""))
  	 css_url="/css/css1.css";
}
	WorkPlanSpForm form=new WorkPlanSpForm();
	String dbname=form.getDbname();
	String a0100=form.getA0100();
%>
<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
<hrms:themes />
<link href="/css/xtree.css" rel="stylesheet" type="text/css" >
<html>
  <body>

    <hrms:relationtree action="/performance/nworkplan/nworkplansp/SearchWeekWorkPlanSpTrans.do?b_search=link" target="mil_body"  default_line="1" dbnamekey="<%=dbname %>" a0100key="<%=a0100 %>" paramkey="a_code"/>

  </body>
  

</html>

