 <%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="JavaScript" src="/js/popcalendar.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/constant.js"></script>
<SCRIPT Language="JavaScript">dateFormat='yyyy.mm.dd'</SCRIPT>
<script language="JavaScript" src="/hire/employNetPortal/employNetPortal.js"></script>
<%@ page import="com.hjsj.hrms.actionform.hire.employNetPortal.EmployPortalForm,
			     org.apache.commons.beanutils.LazyDynaBean,com.hrms.hjsj.utils.Sql_switcher,com.hrms.hjsj.sys.Constant,
			     java.util.*,com.hrms.hjsj.sys.ResourceFactory"%>
<%@ page import="com.hrms.struts.constant.SystemConfig,com.hrms.frame.codec.SafeCode,com.hjsj.hrms.utils.PubFunc" %>
<%@ page import="com.hrms.hjsj.sys.EncryptLockClient"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>

<html>
<head>
<LINK href="/css/employNetStyle.css" type=text/css rel=stylesheet>
<LINK href="/css/main.css" type=text/css rel=stylesheet>
<LINK href="/css/nav.css" type=text/css rel=stylesheet>
<%
    session.setAttribute("islogon",new Boolean("true"));
    boolean isforward=false;
    int version_flag=0;
    EncryptLockClient lockclient=(EncryptLockClient)session.getServletContext().getAttribute("lock");
    if(lockclient!=null){
       if(lockclient.getVersion()>=50)
           isforward=true;
       
   }
   EmployPortalForm employPortalForm=(EmployPortalForm)session.getAttribute("employPortalForm");
   String hirechannel=employPortalForm.getHireChannel();
   String isAll="";
   if(request.getParameter("isAll")!=null)
	   isAll=PubFunc.getReplaceStr2(request.getParameter("isAll"));
   String reUnitCode="";
   if(request.getParameter("unitCode")!=null)
	   reUnitCode=PubFunc.getReplaceStr2(request.getParameter("unitCode"));
 %>
 <%
	String dbtype="1";
  if(Sql_switcher.searchDbServer()== Constant.ORACEL)
  {
    dbtype="2";
  }
  else if(Sql_switcher.searchDbServer()== Constant.DB2)
  {
    dbtype="3";
  }
  String aurl = (String)request.getServerName();
	    String port=request.getServerPort()+"";
	    String prl=request.getScheme();
	    String url_p=prl+"://"+aurl+":"+port;
	    UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	    String userViewName="";
	    if(userView!=null)
	        userViewName=userView.getUserName();

  %>
<script language='javascript'>
var totalwidth=0;
 <%if(isforward){
  if(hirechannel==null||hirechannel.equalsIgnoreCase("out")){%>
       document.location="/hire/hireNetPortal/zp_homepage.do?b_hquery=link";
  <%}else{%>
     document.location="/hire/hireNetPortal/search_zp_position.do?b_query=link&operate=init&hireChannel=${employPortalForm.hireChannel}";
<%}}%>
	
</script> 
</html>