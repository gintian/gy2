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
<hrms:themes></hrms:themes>
<script language="javascript">
	function getuser()
	{
     var selectflag='1';
     if(selectflag=='1'){
     	 var currnode=Global.selectedItem; 
	     var iconurl=currnode.icon;    
		 var selectedValue = root.getSelected();
		 sv = selectedValue.split(",");
		 if ("1"!="${customReportForm.num}")
		 if(selectedValue == ''){
		 	alert('<bean:message key="error.notselect.object"/>');
	     	return;	
		 }
		 for(var i = 0; i< sv.length ; i++){
		 	var temp = sv[i];
		 	if(temp.indexOf('@')!=-1){
		 		alert('<bean:message key="error.exist.group"/>');
	     		return;	
		 	}
		 }
		 var thevo=new Object();
		 thevo.content=root.getSelected();
		 thevo.title=root.getSelectedTitle();
         // window.returnValue=thevo;
	     // window.close();
         parent.return_vo = thevo;
         winClose();
     }else{
     	 var currnode=Global.selectedItem; 
	     var iconurl=currnode.icon;    
	     if(!(iconurl=="/images/not_admin.gif"||iconurl=="/images/admin.gif"))
	     {
	     	alert('<bean:message key="error.notselect.object"/>');
	     	return;	
	     }
		 var thevo=new Object();
		 thevo.content=root.getSelected();
		 thevo.title=root.getSelectedTitle();
         // window.returnValue=thevo;
	     // window.close();
         parent.return_vo = thevo;
         winClose();
     }
    
	}	
    Global.showroot=false;
    function winClose() {
        // parent.return_vo = '';
        if(parent.Ext.getCmp('power')){
            parent.Ext.getCmp('power').close();
        }
    }
</script>
   <style type="text/css">
	#treemenu {  
	height: 330px;
	width:290px;
	overflow: auto;
	border-style:solid ;
	border-width:1px
	}
   </style>

<html:form action="/system/options/customreport"> 
   <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" >   
         <tr>
           <td align="left"> 
            <div id="treemenu"> 
             <SCRIPT LANGUAGE=javascript>
             	Global.defaultInput=1;  
               	Global.defaultchecklevel=1;
				Global.checkvalue=","+"${customReportForm.privusers}"+",";         
               <bean:write name="customReportForm" property="userTree" filter="false"/>

             </SCRIPT>
             </div>             
           </td>
           </tr> 
         <tr>
            <td align="center" colspan="2" height="35px;">
         	<html:button styleClass="mybutton" property="b_save" onclick="getuser();">
            		<bean:message key="button.ok"/>
	 	    </html:button>
         	<%--<html:button styleClass="mybutton" property="br_return" onclick="window.close();">--%>
            		<%--<bean:message key="button.close"/>--%>
	 	    <%--</html:button>         --%>
                <html:button styleClass="mybutton" property="br_return" onclick="winClose();">
            		<bean:message key="button.close"/>
	 	    </html:button>
            </td>         
         </tr>                         
   </table>

</html:form>
