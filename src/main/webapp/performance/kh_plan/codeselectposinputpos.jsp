<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
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
<hrms:themes />
   <link href="/css/xtree.css" rel="stylesheet" type="text/css" >
   <SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>   
   <script language="JavaScript">
        var input_code_id;
        var selfForm;
   	function savecode()
   	{
      	   var currnode,codeitemid;
    	   currnode=Global.selectedItem;	
    	   if(currnode==null)
    	    	return;   
    	   codeitemid=currnode.uid;
    	    /*输入相关代码类和选择的相关代码一致*/
    	   if(isAccord==1)
    	   {
	    	   if(input_code_id!=codeitemid.substring(0,2)&&!flag)
	    	     return ;  
	    	   if(input_code_id!=codeitemid.substring(0,2)&&(flag=="1" || flag=="2"))
	    	     return ; 
	    	} 
	    	//alert(currnode.text);
    	   targetobj.value=currnode.text
    	   //alert(codeitemid.substring(0,2));	
    	   if(!flag)
    	     targethidden.value=codeitemid.substring(2);
    	   else
    	   {
    	     if(flag=="query")
    	        targethidden.value=codeitemid.substring(2);    	     
    	     else if(flag=="1" || flag=="2")
    	        targethidden.value=codeitemid.substring(2);   
    	     else
    	     	targethidden.value=codeitemid;
    	   }
    	   if(targethidden.fireEvent)
    	  		targethidden.fireEvent("onchange");  //chenmengqing changed at 200605
   	}
   </SCRIPT>
   <style type="text/css">
	#treemenu {  
		height: 370px;
		width:290px;
		overflow:auto;
		border:1px solid;
		/*margin:6px 3px 0 0;*/
		
	}
   </style>   
</HEAD>
<body   topmargin="0" leftmargin="0" marginheight="0" marginwidth="0">
   <div id="treemenu"></div>
   <SCRIPT LANGUAGE=javascript>
      function closeWin(){
  		 if(window.showModalDialog){
  	 		parent.window.close();
  		 }else{
  	 		parent.parent.Ext.getCmp("openInputOrgCodeDialogOrgWin").close();
  	 	}
  	 } 
             var codesetid,codevalue,name;
             var paraArray=window.dialogArguments||parent.parent.dialogArguments; 
             var targetobj,targethidden;
             var flag;	//输入代码类和选中代码类是否需要相同
             var isAccord=1;  //是否 codeseitid 必须与返回的节点值的 codesetid需一致，才能返回值  1：需要  0：不需要
                //代码值对象    
              codesetid = paraArray[0];
             codevalue = paraArray[1];
             //显示代码描述的对象
             targetobj=paraArray[2];
             //代码值对象
             targethidden=paraArray[3];
             flag=paraArray[4];
             input_code_id=codesetid; 
             if(paraArray.length==6)
              	isAccord=paraArray[5];
             var m_sXMLFile="/performance/kh_plan/get_code_treeinputinfo.jsp?codesetid="+codesetid+"&codeitemid="+codevalue + "&isfirstnode=" + flag;	 //
                          
             var root=new xtreeItem("root","代码项目","","","代码项目","/images/unit.gif",m_sXMLFile);
             Global.closeAction="savecode();closeWin();";
             //Global.defaultInput=2;//checkbox =2 radio
             root.setup(document.getElementById("treemenu"));
	     
   </SCRIPT> 
<table width="100%"><tr><td align="center" height="35px;">
    <input type="button" name="btnok" value="确定" class="mybutton" onclick="savecode();closeWin();">
    <input type="button" name="btncancel" value="取消" class="mybutton" onclick="closeWin();"> 
</td></tr></table>
   
<BODY>
</HTML>


