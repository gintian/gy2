<%@ page contentType="text/html; charset=UTF-8" %>
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
	String select_id=(String)request.getParameter("role_id");
	
%>
<link href="/css/xtree.css" rel="stylesheet" type="text/css" >
<hrms:themes></hrms:themes>
<script type="text/javascript">  
  function subid()
  {
     var currnode=Global.selectedItem; 
	 var thevo=new Object();		
	 thevo.content=root.getSelected();
	 thevo.title=root.getSelectedTitle();	
	 thevo.flag="true";
     //window.returnValue=thevo;
     //window.close();
     if(parent.Ext && parent.Ext.getCmp('select_role_emp')){//Ext弹窗返回数据  wangb 20190318
         parent.Ext.getCmp('select_role_emp').return_vo=thevo;
     }else{
         parent.window.returnValue=thevo;
     }
     winclose();
  }
  function winclose(){
      if(parent.Ext && parent.Ext.getCmp('select_role_emp')){
  	  	  parent.Ext.getCmp('select_role_emp').close();
      }else{
          window.close();
      }
  }
</script>
<HTML>
<HEAD>
   	<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT> 
</HEAD>
<body   topmargin="0" leftmargin="5" marginheight="0" marginwidth="0">

<html:form action="/system/warn/config_maintenance"> 
   <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" >
		 	            
         <tr>
           <td align="left"> 
            <div id="treemenu" style="height: 330px;width:290px;overflow: auto;border-style:solid ;border-width:1px"> 
            <SCRIPT LANGUAGE=javascript> 
                <hrms:isroletree flag="1" select_id="<%=select_id%>"/>
             </SCRIPT>
             </div>             
           </td>
           </tr>   
           <tr>
            <td align="center" colspan="2" height="35px;">
         	<html:button styleClass="mybutton" property="b_save" onclick="subid();">
            		<bean:message key="button.ok"/>
	 	    </html:button>
         	<html:button styleClass="mybutton" property="br_return" onclick="winclose();">
            		<bean:message key="button.close"/>
	 	    </html:button>            
            </td>         
         </tr>          
    </table>  
    
<script type="text/javascript">
	//root.expandAll();
</script>
</html:form>
</BODY>
</HTML>

