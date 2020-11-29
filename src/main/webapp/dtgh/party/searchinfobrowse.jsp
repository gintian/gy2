<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.hjsj.sys.FieldItem"%>
<%@ page import="com.hrms.frame.dao.RecordVo"%>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String bosflag="";	
	if(userView != null){
	  bosflag=userView.getBosflag(); 
	}
	String nid = request.getParameter("codeitemid");
%>
<script language="JavaScript" src="/js/wz_tooltip.js"></script>
<hrms:themes></hrms:themes>
<html:form action="/general/inform/pos/searchorgbrowse"> 
<table width="100%">
  <tr>
    <td>
    <hrms:infobrowse nid="<%=nid %>" infokind="4" isinfoself="1" setflag="1"/> 
    </td>
  </tr>
  <tr><!-- update by xiegh ondate20180413 bug36280 window.history.back();return false; -->
    <td><button class="mybutton" onclick="JavaScript:history.back();return false;">返回</button></td>
  </tr>
</table>
</html:form>