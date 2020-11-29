<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
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
%>
<SCRIPT LANGUAGE="javascript" src="/js/xtree.js"></SCRIPT>
<script type="text/javascript">
<!--
	function savecode() {
		var thevo=new Object();
		thevo.content=root.getSelected();
		thevo.title=root.getSelectedTitle();
	 	window.returnValue=thevo;
     	window.close();
    }
//-->
</script> 
<link rel="stylesheet" href="<%=css_url%>" type="text/css">
<link href="/css/xtree.css" rel="stylesheet" type="text/css">
<html:form action="/general/muster/hmuster/searchHroster">
   <table width="100%" border="0" cellspacing="1"  align="center" cellpadding="1" >          
         <tr>
           <td align="left">  
            <div id="treemenu"> 
             <SCRIPT LANGUAGE=javascript>  
             	Global.defaultInput=2;  
               	Global.defaultradiolevel=2;          	                 
               <bean:write name="customReportForm" property="treeCode" filter="false"/>
             </SCRIPT>
             </div>             
           </td>
           </tr>           
    </table>
    <br> 
    <input type="button" name="btnok" value="确定" class="mybutton" onclick="savecode();window.close();">
    <input type="button" name="btncancel" value="取消" class="mybutton" onclick="window.close();">      
</html:form>
