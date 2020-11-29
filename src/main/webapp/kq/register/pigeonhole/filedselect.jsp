<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.frame.utility.DateStyle"%>
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
   <link href="/css/xtree.css" rel="stylesheet" type="text/css" >
   <SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>   
   <script language="JavaScript">
        var input_code_id;
   	function savecode()
   	{
    	   var currnode,codeitemid;
    	   currnode=Global.selectedItem;	
    	   if(currnode==null)
    	    	return;   
    	   codeitemid=currnode.uid;     	   
    	   if(codeitemid=="root")
    	   {
    	     targetobj.value="";              	  
    	     targethidden.value=""; 
    	     return false;
    	   }else
    	   {
    	     targetobj.value=currnode.text;            	  
    	     targethidden.value=codeitemid; 
    	   }    	      	    
    	   window.close();   	  	
   	}
   </SCRIPT>
   <hrms:themes></hrms:themes>
</HEAD>
<body   topmargin="0" leftmargin="0" marginheight="0" marginwidth="0">
   <div id="treemenu" style="height: 300px;overflow: auto;border-style:solid ;border-width:1px"></div>
   <SCRIPT LANGUAGE=javascript>
             var codesetid,codevalue,name;
             var paraArray=dialogArguments; 
             var targetobj,targethidden;	
             setname = paraArray[0];             
             intype = paraArray[1];                  
                     
             //显示代码描述的对象
             targetobj=paraArray[2];
             //代码值对象
             targethidden=paraArray[3];   
             input_code_id=codesetid;
             var m_sXMLFile="/kq/register/pigeonhole/get_field_tree.jsp?setname="+setname+"&intype="+intype;	 //
             
             var root=new xtreeItem("root","指标项目","","","指标项目","/images/add_all.gif",m_sXMLFile);
             Global.closeAction="savecode();window.close();";
             //Global.defaultInput=2;//checkbox =2 radio
             root.setup(document.getElementById("treemenu"));
	     
   </SCRIPT> <p align="center">
    <input type="button" name="btnok" value="确定" class="mybutton" onclick="savecode();">
    <input type="button" name="btncancel" value="取消" class="mybutton" onclick="window.close();">   
    </p> 
<BODY>
</HTML>


