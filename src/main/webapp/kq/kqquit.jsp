<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@page import="com.hrms.hjsj.sys.EncryptLockClient"%>
<%
 EncryptLockClient lock=(EncryptLockClient)pageContext.getServletContext().getAttribute("lock");
 int ver=lock.getVersion();    
%>

    
 <script language="javascript">
   function quitRe()
   {
       
      <%if(ver>=40)
      {%>
        quitRe50();
      <%}else
      {%>
        quitRe40();
      <%}%>
   }
   function quitRe50()
   {
      history.back();
   }
   function quitRe40()
   {
      var quitUrl="";
      
      <%        
        UserView userView=(UserView)session.getAttribute(WebConstant.userView);
        if(userView.isBbos()){
      %>
          quitUrl="/templates/menu/kq_m_menu.do?b_query=link&module=6";
      <%
         }else{
      %>
         quitUrl="/templates/menu/kq_dept_menu.do?b_query=link&module=22";
       <%
         }
      %>  
      document.form.action=quitUrl;
      document.form.target="i_body";
      document.form.submit();  
   }
    window.onload = quitRe;  
</script>
<form name="form" method="post" action="">
</form>