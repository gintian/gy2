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
    	     	targethidden.value=codeitemid.substring(codeitemid.indexOf("-"));
    	   }

    	  targethidden.fireEvent("onchange");  //chenmengqing changed at 200605
   	}
   </SCRIPT>
   <style type="text/css">
	#treemenu {  
	height: 300px;overflow: 
	auto;border-style:inset ;
	border-width:2px
	}
   </style>     
</HEAD>
<body   topmargin="0" leftmargin="0" marginheight="0" marginwidth="0">
   <div id="treemenu"></div>
   <SCRIPT LANGUAGE=javascript>
             var codesetid,codevalue,name;
             var paraArray=dialogArguments; 
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
             var m_sXMLFile="/system/get_hire_tree.jsp?codesetid="+codesetid+"&codeitemid="+codevalue + "&isfirstnode=" + flag;	 //
                          
             var root=new xtreeItem("root","代码项目","","","代码项目","/images/unit.gif",m_sXMLFile);
             Global.closeAction="savecode();window.close();";
             //Global.defaultInput=2;//checkbox =2 radio
             root.setup(document.getElementById("treemenu"));
	     
   </SCRIPT> 
   <br> 
    <input type="button" name="btnok" value="确定" class="mybutton" onclick="savecode();window.close();">
    <input type="button" name="btncancel" value="取消" class="mybutton" onclick="window.close();">    
<BODY>
</HTML>


