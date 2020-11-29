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
   <SCRIPT LANGUAGE=javascript src="/js/constant.js"></SCRIPT> 
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
   if(fielditemid.length<4)
    	return;
  
    targetobj.value=currnode.text;
    	   
    if(targetformula.pos!=null){
    	targetformula.pos.text = currnode.text;
    }else{
    	targetformula.value +=currnode.text;
    }

    targethidden.value=fielditemid;
    	  
    targetobj.fireEvent("onchange");  //chenmengqing changed at 200605
}
</SCRIPT>
</HEAD>
<body   topmargin="0" leftmargin="0" marginheight="0" marginwidth="0">
   <div id="treemenu"></div>
   <SCRIPT LANGUAGE=javascript>
             var paraArray=dialogArguments; 
             var targetobj,targethidden,targetformula;
            
             targetobj = paraArray[0];
             targethidden = paraArray[1];
             targetformula = paraArray[2];

             var m_sXMLFile="/org/autostatic/mainp/get_project_tree.jsp";	 //
                          
             var root=new xtreeItem("root",FORMULA_ITEM,"","",FORMULA_ITEM,"/images/unit.gif",m_sXMLFile);
             Global.closeAction="savecode();window.close();";
             //Global.defaultInput=2;//checkbox =2 radio
             root.setup(document.getElementById("treemenu"));
	     
   </SCRIPT> 
   <br> 
    <input type="button" name="btnok" value="<bean:message key='button.ok'/>" class="mybutton" onclick="savecode();window.close();">
    <input type="button" name="btncancel" value="<bean:message key='button.cancel'/>" class="mybutton" onclick="window.close();">    
<BODY>
</HTML>


