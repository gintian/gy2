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
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null){
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	css_url="/css/css1.css"; 
          //out.println("<link href='"+css_url+"' rel='stylesheet' type='text/css' >");  	 
	}
	String db=request.getParameter("db");
%>
<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT> 
<hrms:themes></hrms:themes>
<html:form action="/general/static/singlestatic/showsingleinfo"> 

<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
   <tr>
       <td align="center">
	 <hrms:infobrowse nid="${staticForm.a0100}" infokind="${staticForm.infor_Flag}" pre="<%=db %>" isinfoself="1" setflag="1"/>
       </td>
   </tr>          
</table>
<table  align="center">
   <tr align="left">  
    <td valign="top"  nowrap>
      <input type="button" name="b_save" value="<bean:message key="button.close"/>" class="mybutton" onclick="javascript:window.close();">   
    </td>
  </tr>
 </table>  
</html:form>
