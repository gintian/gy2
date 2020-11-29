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
   <hrms:themes />
   <SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>   
<script language="JavaScript">
var input_field_id;
var selfForm;
function savecode(){
   var currnode,fielditemid;
   currnode=Global.selectedItem;	
   if(currnode==null)
    	return;   
   fielditemid=currnode.uid;
   
   var arr = fielditemid.split("-");
   if(arr.length!=2)
    	return;
  
    targetobj.value=currnode.text;
    	   
    targetfidlen.value =arr[1];

    targethidden.value=arr[0];
    	  
    targetobj.fireEvent("onchange");  //chenmengqing changed at 200605
}
</SCRIPT>
</HEAD>
<body   topmargin="0" leftmargin="0" marginheight="0" marginwidth="0">
   <div id="treemenu"></div>
   <SCRIPT LANGUAGE=javascript>
             var paraArray=dialogArguments; 
             var targetobj,targethidden,targetfidlen;
            
             targetobj = paraArray[0];
             targethidden = paraArray[1];
             targetfidlen = paraArray[2];

             var m_sXMLFile="/gz/tempvar/code_tree.jsp";	 //
                          
             var root=new xtreeItem("root",FORMULA_ITEM,"","",FORMULA_ITEM,"/images/unit.gif",m_sXMLFile);
             Global.closeAction="savecode();window.close();";
             //Global.defaultInput=2;//checkbox =2 radio
             root.setup(document.getElementById("treemenu"));
	     
   </SCRIPT> 
   <br> 
    <input type="button" name="btnok" value="<bean:message key='lable.tz_template.enter'/>" class="mybutton" onclick="savecode();window.close();">
    <input type="button" name="btncancel" value="<bean:message key='lable.tz_template.cancel'/>" class="mybutton" onclick="window.close();">    
</BODY>
</HTML>


