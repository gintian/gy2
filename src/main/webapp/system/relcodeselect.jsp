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
   	function savecode()
   	{
    	   var currnode,codeitemid;
    	   currnode=Global.selectedItem;	
    	   if(currnode==null)
    	    	return;   
    	   codeitemid=currnode.uid;
    	   /*输入相关代码类和选择的相关代码一致*/
    	   //if(input_code_id!=codeitemid.substring(0,2))
    	  //   return ;  
    	  if(currnode.text=='代码项目')
    	  {
    	  targetobj.value='';
    	  targethidden.value='';
    	  return;
    	  }  	   
    	   targetobj.value=currnode.text

    	   //alert(codeitemid.substring(0,2));
    	   targethidden.value=codeitemid;
    	   //alert(root.getSelected());	  	
   	}
   </SCRIPT>
</HEAD>
<body   topmargin="0" leftmargin="0" marginheight="0" marginwidth="0">
   <div id="treemenu"></div>
   <SCRIPT LANGUAGE=javascript>
             var codesetid,codevalue,name;
             var paraArray=dialogArguments; 
             var targetobj,targethidden;
             var fieldtable,fieldid,fielddesc,orgid;	
             codesetid = paraArray[0];
             codevalue = paraArray[1];
             //显示代码描述的对象
             targetobj=paraArray[2];
             //代码值对象
             targethidden=paraArray[3]; 
             fieldtable=paraArray[4];  
	     fieldid=paraArray[5];
	     fielddesc=paraArray[6];
	     orgid=paraArray[7];	
             input_code_id=codesetid;
             var m_sXMLFile="/system/get_relcode_tree.jsp?codesetid="+fieldtable+"&codeitemid="+fieldid+"&privflag="+fielddesc+"&orgid="+orgid;	 //
                          
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


