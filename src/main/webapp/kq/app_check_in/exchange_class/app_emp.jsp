<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>

<script LANGUAGE=javascript src="/js/xtree.js"></script> 

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
   <style type="text/css">
	#treemenu {  
	height: 300px;
	overflow: auto;
	border-style:solid ;
	border-width:1px
	}
   </style> 
<html:form action="/kq/app_check_in/exchange_class/app_emp"> 
<div class="fixedDiv3">
   <table width="99%" border="0" cellspacing="1"  align="center" cellpadding="1" >   
	 <tr align="left">
		<td valign="top">
		</td>
	 </tr>          
         <tr>
        
           <td align="left"> 
             <SCRIPT LANGUAGE="javascript">
               Global.defaultradiolevel=3;
               Global.showorg=1;
             </SCRIPT>
             <hrms:kqorgemptree flag="${exchangeAppForm.flag}" showroot="false" selecttype="${exchangeAppForm.selecttype}" dbtype="${exchangeAppForm.dbtype}"  priv="${exchangeAppForm.priv}"/>			           
           </td>
         </tr>   
         <tr>
            <td align="center" colspan="2">
               
         	<html:button styleClass="mybutton" property="b_save" onclick="getemploy();">
            		<bean:message key="button.ok"/>
	 	 </html:button>
	 	
         	<html:button styleClass="mybutton" property="br_return" onclick="window.close();">
            		<bean:message key="button.cancel"/>
	 	    </html:button>            
            </td>         
         </tr>        
   </table>
</div>
</html:form>
