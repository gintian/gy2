<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
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
	
%>
<HTML>
<HEAD>
<TITLE>
</TITLE>
   <link href="<%=css_url%>" rel="stylesheet" type="text/css">
   <hrms:themes></hrms:themes>
   <link href="/css/xtree.css" rel="stylesheet" type="text/css" >
   <SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>   
   <script language="JavaScript">       
   	function savecode()
   	{
    	   var currnode,codeitemid;
    	   currnode=Global.selectedItem;	
    	   
    	   if(currnode==null)
    	    	return;   
    	    	
    	   if(currnode.uid=='root')
    	   {
    	   		alert('请选择填报单位!');
    	   		return;
    	   }
    	   codeitemid=currnode.uid;      
    	   targetobj.value=currnode.text;
    	   targethidden.value=codeitemid; 
   	}
   </SCRIPT>
   <style type="text/css">
	body {  
	/*background-color:#DEEAF5;*/
	font-size: 12px;
	}
   </style>
</HEAD>
<body   topmargin="0" leftmargin="0" marginheight="0" marginwidth="0">
   <div id="treemenu" style="height: 340px;width: 280px;overflow: auto;" class="complex_border_color"></div>
   <SCRIPT LANGUAGE=javascript>
             var paraArray=dialogArguments||opener.dialogArguments; 
             var targetobj,targethidden;	
             //显示代码描述的对象
             targetobj=paraArray[0];
             //代码值对象
             targethidden=paraArray[1];
             var m_sXMLFile="/system/report_orgtree.jsp?unitcode=<%=(request.getParameter("unitcode"))%>&report_type=<%=(request.getParameter("report_type"))%>";	 //          
             var root=new xtreeItem("root","填报单位","","","填报单位","/images/add_all.gif",m_sXMLFile);
             Global.closeAction="savecode();window.close();";
             root.setup(document.getElementById("treemenu"));
	     
   </SCRIPT> 
   <br> 
   <div align="center">
    <input type="button" name="btnok" value='<bean:message key="button.ok"/>' class="mybutton" onclick="savecode();window.close();">
    <input type="button" name="btncancel" value='<bean:message key="button.cancel"/>' class="mybutton" onclick="window.close();">    
   </div>
<BODY>
</HTML>


