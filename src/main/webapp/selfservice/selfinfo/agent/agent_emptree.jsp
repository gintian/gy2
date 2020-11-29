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
	String showDb="0";
	if(request.getParameter("showDb")!=null)
		showDb=request.getParameter("showDb");
    String showSelfNode="0";
	if(request.getParameter("showSelfNode")!=null)
		showSelfNode=request.getParameter("showSelfNode");
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
         var code=thevo.content;        
         if(code.indexOf("UN")!=-1)
         {
            alert("只能选择人员");
         }else if(code.indexOf("UM")!=-1)
         {
            alert("只能选择人员");
         }else if(code.indexOf("@K")!=-1)
         {
            alert("只能选择人员");
         }else
         {
           window.returnValue=thevo;
           window.close();
         }
	}
	function record()
	{
	    
         
    }
//-->
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
   <hrms:themes />
<html:form action="/selfservice/selfinfo/agent/agentemptree"> 
   <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" >   
	 <tr align="left">
		<td valign="top">
		</td>
	 </tr>          
         <tr>
        
           <td align="left"> 
           
               <hrms:agentemptree flag="${agentForm.flag}"  showDb="<%=showDb%>" showSelfNode="<%=showSelfNode%>"  loadtype="${agentForm.loadtype}" showroot="false" selecttype="${agentForm.selecttype}" dbtype="${agentForm.dbtype}" priv="${agentForm.priv}" isfilter="${agentForm.isfilter}"/>		           
           </td>
         </tr>   
         <tr>
            <td align="center" colspan="2" height="35px;">
         	<html:button styleClass="mybutton" property="b_save" onclick="getemploy();">
            		<bean:message key="button.ok"/>
	 	    </html:button>
         	<html:button styleClass="mybutton" property="br_return" onclick="window.close();">
            		<bean:message key="button.close"/>
	 	    </html:button>            
            </td>         
         </tr>        
   </table>

</html:form>
