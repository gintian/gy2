<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.hjsj.sys.FieldItem"%>
<%@ page import="com.hrms.frame.dao.RecordVo"%>
<script type="text/javascript" src="/js/wz_tooltip.js"></script>  
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String bosflag="";	
	if(userView != null){
	  css_url=userView.getCssurl();
	  bosflag=userView.getBosflag(); 
	  if(css_url==null||css_url.equals(""))
	  	css_url="/css/css1.css"; 
	  	        
          //out.println("<link href='"+css_url+"' rel='stylesheet' type='text/css' >");  	 
	}
%>
<script type="text/javascript">
<!--
function exeReturn(returnStr,target)
{
  target_url=returnStr;
  window.open(target_url,target); 
}
//-->
</script>
<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT> 
<hrms:themes></hrms:themes>
<html:form action="/general/inform/pos/searchorgbrowse"> 
<table width="100%" cellpadding="0" cellspacing="0" border="0" style="margin:0px;">
  <tr>
    <td>
    <hrms:infobrowse nid="${infoBrowseForm.nid}" infokind="${infoBrowseForm.infokind}" pre="usr" isinfoself="1" setflag="1"></hrms:infobrowse> 
    </td>
  </tr>
   <tr>
    <td>
          <%if(bosflag!=null&&(bosflag.equals("hl") || bosflag.equals("hcm"))) {//6.0版本%>
<logic:equal name="infoBrowseForm" property="returnvalue" value="scanduty"> <!-- 从信息维护列表的浏览过来 -->
          <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/workbench/dutyinfo/searchdutyinfodata.do?b_query=link&code=${infoBrowseForm.return_codeid}','_self')">                 
</logic:equal>
<logic:equal name="infoBrowseForm" property="returnvalue" value="dxt"><!-- 从导航图过来 -->
	<input type="button" name="b_delete" value='<bean:message key="button.return"/>' class="mybutton" onclick="hrbreturn('org','2','infoBrowseForm');"> 
</logic:equal>
<%}else{ //5.0版本%>
	<logic:equal name="infoBrowseForm" property="returnvalue" value="scanduty"><!-- 从信息维护列表的浏览过来 -->
          <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/workbench/dutyinfo/searchdutyinfodata.do?b_query=link&code=${infoBrowseForm.return_codeid}','_self')">                 
</logic:equal>
<%} %>
    </td>
  </tr>
</table>
</html:form>
<hrms:themes/>
<script>
   var pELE = document.getElementsByTagName("p");
   for(var k=0;k<pELE.length;k++)
     pELE[k].style.lineHeight='normal';
</script>