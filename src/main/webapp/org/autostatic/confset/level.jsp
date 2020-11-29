<%@ page contentType="text/html; charset=UTF-8"%>
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
var input_field_id;
var selfForm;
function savecode(){
  var currnode;
   currnode=Global.selectedItem;	

    targetobj.value=currnode.text;
    targethidden.value=currnode.uid;
    	  
    targetobj.fireEvent("onchange");  
}
</script> 
</HEAD>
<body   topmargin="0" leftmargin="0" marginheight="0" marginwidth="0">
   <div id="treemenu"></div>
   <SCRIPT LANGUAGE=javascript>
   		var paraArray=dialogArguments; 
        var targetobj,targethidden;
            
        targetobj = paraArray[0];
        targethidden = paraArray[1];
        var m_sXMLFile="/org/autostatic/confset/level_tree.jsp";	 
                          
        var root=new xtreeItem("0","等级","","","等级","/images/unit.gif",m_sXMLFile);
        Global.closeAction="savecode();window.close();";
        root.setup(document.getElementById("treemenu"));    
   </SCRIPT> 
   <br> 
    <input type="button" name="btnok" value="确定" class="mybutton" onclick="savecode();window.close();">
    <input type="button" name="btncancel" value="取消" class="mybutton" onclick="window.close();">    
<BODY>
</HTML>
