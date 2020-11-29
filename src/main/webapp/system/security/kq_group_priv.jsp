<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
    	String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null)
	{
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	css_url="/css/css1.css";
	} 
%>

<HTML>
<script language="jscript">
	  
	   function save()
	   {
	   	  var str_id=root.getSelected();
	   	  var hashvo=new ParameterSet();
	   	  hashvo.setValue("priv_selected",str_id);
	   	  hashvo.setValue("flag","<bean:write name="resourceForm" property="flag" />");
          hashvo.setValue("roleid","<bean:write name="resourceForm" property="roleid" />");	        
          hashvo.setValue("res_flag","<bean:write name="resourceForm" property="res_flag" />");
   　       var request=new Request({asynchronous:false,onSuccess:save_ok,functionId:'1010010058'},hashvo);        
	   }
	   function save_ok()
	   {
	   	alert("保存成功！");
	   }
</script>
<HEAD>
 <script LANGUAGE=javascript src="/js/xtree.js"></script> 
<hrms:themes></hrms:themes>
<html:form action="/system/security/kq_group_priv"> 
<body   topmargin="0" leftmargin="5" marginheight="0" marginwidth="0">
	
<table width="600" align="left" border="0" cellpadding="0" cellspacing="0" class="mainbackground">
  <tr align="left">  
    <td valign="middle" height="30px;">
       &nbsp;&nbsp;&nbsp;&nbsp;
       <html:button styleClass="mybutton" property="b_save" onclick="save();"><bean:message key="button.save"/></html:button>
	   <html:button styleClass="mybutton" property="b_all" onclick="root.allSelect();"><bean:message key="label.query.selectall"/></html:button>
	   <html:button styleClass="mybutton" property="b_clear" onclick="root.allClear();"><bean:message key="label.query.clearall"/></html:button>
      
    </td>
  </tr>
  <tr>  
    <td valign="top">
	<div id="treemenu"></div>
    </td>
  </tr>
</table>
<BODY>
</html:form>

<SCRIPT LANGUAGE=javascript>
  Global.defaultInput=1;
  Global.showroot=false;
  Global.checkvalue="<bean:write name="resourceForm" property="law_dir" />";	
  var m_sXMLFile= "/system/security/kq_group_tree.jsp?params=1%3D1";	 
  var root=new xtreeItem("root","考勤班组","","","考勤班组","/images/add_all.gif",m_sXMLFile);
  root.setup(document.getElementById("treemenu"));
</SCRIPT>
