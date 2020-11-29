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
	}
	String codeSetID=request.getParameter("codesetid");
%>
<script LANGUAGE=javascript src="/js/xtree.js"></script> 
<link rel="stylesheet" href="/css/xtree.css" type="text/css"/>
<style>
</style>
<hrms:themes></hrms:themes>
<script type="text/javascript">
<!--
	function getemploy()
	{
	/*
     var currnode=Global.selectedItem; 
     var iconurl=currnode.icon;
     if(iconurl!="/images/man.gif") 
       return;    
	 window.returnValue=currnode.uid;
	 */
	 var thevo=new Object();
	 thevo.content=root.getSelected();
	 
	 thevo.title=root.getSelectedTitle();

	 window.returnValue=thevo;
         window.close();			
	}
//-->
</script>
<form name='f1' >

   <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" >   
         <tr>
           <td align="left" style="padding-top:5px;padding-left:1px;"> 
           	<SCRIPT LANGUAGE=javascript>    
               Global.defaultchecklevel=1; 
             </script>
             <hrms:codeChecktree  showroot="true" selecttype="1"  codeSetID="<%=codeSetID%>"/>
           </td>
         </tr>   
         <tr>
            <td align="center" colspan="2" height="35px;">
         	<html:button styleClass="mybutton" property="b_save" onclick="getemploy();">
            		<bean:message key="button.save"/>
	 	    </html:button>
         	<html:button styleClass="mybutton" property="br_return" onclick="window.close();">
            		<bean:message key="button.return"/>
	 	    </html:button>            
            </td>         
         </tr>        
   </table>

</form>
