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
%>
<script LANGUAGE=javascript src="/js/xtree.js"></script> 
<link rel="stylesheet" href="<%=css_url%>" type="text/css">
<link href="/css/xtree.css" rel="stylesheet" type="text/css" >
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
<html:form action="/kq/app_check_in/redeploy_rest/app_emp"> 
<div  class="fixedDiv2" style="height: 100%;border: none">
   <table width="100%" border="0" cellspacing="1"  align="center" cellpadding="1" >   
	 <tr align="left">
		<td valign="top">
		</td>
	 </tr>          
         <tr>
           <SCRIPT LANGUAGE="javascript">
               Global.defaultradiolevel=3;
             </SCRIPT>
           <td align="left"> 
             <hrms:kqorgemptree flag="${redeployAppForm.flag}" showroot="false" selecttype="${redeployAppForm.selecttype}" dbtype="${redeployAppForm.dbtype}" priv="${redeployAppForm.priv}"/>			           
           </td>
         </tr>   
         <tr>
            <td align="center" colspan="2">
             
         	<html:button styleClass="mybutton" property="b_save" onclick="getemploy();">
            		<bean:message key="button.ok"/>
	 	    </html:button>
	     
         	<html:button styleClass="mybutton" property="br_return" onclick="window.close();">
            		<bean:message key="button.return"/>
	 	    </html:button>            
            </td>         
         </tr>        
   </table>
</div>
</html:form>
