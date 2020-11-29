<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.frame.utility.DateStyle"%>

<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ page import="com.hrms.struts.constant.SystemConfig"%>
<%
    	String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null)
	{
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	   css_url="/css/css1.css";
	}
	
	int flag=1;
	String webserver=SystemConfig.getPropertyValue("webserver");
	if(webserver.equalsIgnoreCase("websphere"))
		flag=2;
		
   String isMobile=request.getParameter("isMobile");
   isMobile = isMobile==null?"":isMobile;
   pageContext.setAttribute("isMobile",isMobile);
%>
<HTML>
<HEAD>
<TITLE>
</TITLE>
<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>
<script language="javascript" src="/ajax/constant.js"></script>
<script language="javascript" src="/ajax/basic.js"></script>
<script language="javascript" src="/ajax/common.js"></script>
<script language="javascript" src="/ajax/control.js"></script>
<script language="javascript" src="/ajax/dataset.js"></script>
<script language="javascript" src="/ajax/editor.js"></script>
<script language="javascript" src="/ajax/dropdown.js"></script>
<script language="javascript" src="/ajax/table.js"></script>
<script language="javascript" src="/ajax/menu.js"></script>
<script language="javascript" src="/ajax/tree.js"></script>
<script language="javascript" src="/ajax/pagepilot.js"></script>
<script language="javascript" src="/ajax/command.js"></script>
<script language="javascript" src="/ajax/format.js"></script>
<script language="javascript" src="/js/validate.js"></script>
<script language="javascript" src="/js/constant.js"></script>
<script language="javascript">
    var _checkBrowser=true;
	var _disableSystemContextMenu=false;
	var _processEnterAsTab=true;
	var _showDialogOnLoadingData=true;
	var _enableClientDebug=true;
	var _theme_root="/ajax/images";
	var _application_root="";
	var __viewInstanceId="968";
	var ViewProperties=new ParameterSet();
	var webserver=<%=flag%>;
	
</script>



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
	    	if(codeitemid=="" ||codeitemid=="root"){
    	   		targetobj.value="";
    	        targethidden.value="";
    	   }else{
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
    	   }
    	   try{
	    	   if (navigator.appName.indexOf("Microsoft")!= -1) { 
	    	   		targethidden.fireEvent('onchange'); 
			         //ie  
			    }else{ 
			        targethidden.onchange(); 
			    }  
			}catch(e){
			}
    	   
    	  //targethidden.fireEvent("onchange");  //chenmengqing changed at 200605
   		  parent.hidePopWin(false);
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
   <div id="treemenu" style="height: 300px;overflow: auto"></div>
   <SCRIPT LANGUAGE=javascript>
             var codesetid,codevalue,name;
             var targetobj,targethidden;
             var flag;	//输入代码类和选中代码类是否需要相同
             var isAccord="<%=request.getParameter("isAccord") %>";  //是否 codeseitid 必须与返回的节点值的 codesetid需一致，才能返回值  1：需要  0：不需要
                //代码值对象    
              codesetid = "<%=request.getParameter("codesetid") %>";
             codevalue = "<%=request.getParameter("codeitemid") %>";
             var mytarget="<%=request.getParameter("mytarget") %>";
	         var oldInputs=parent.document.getElementsByName(mytarget); 
	         //显示代码描述的对象
	         targetobj=oldInputs[0];
	       //根据代码显示的对象名称查找代码值名称	
	         var target_name=targetobj.name;
	         var hidden_name=target_name.replace(".hzvalue",".value");//调用该方法时用到.hzvalue
	         hidden_name=hidden_name.replace(".viewvalue",".value");
	         var hiddenInputs=parent.document.getElementsByName(hidden_name);
	         if(hiddenInputs!=null)
	         {
	        	//代码值对象
	             targethidden=hiddenInputs[0];
	         }else{
	    		targethidden=parent.document.getElementById(hidden_name);
	    	 }
             flag="<%=request.getParameter("isfirstnode") %>";
             input_code_id=codesetid; 
             var m_sXMLFile="";
	         var root;
	        
	         var hashvo = new ParameterSet();
	         hashvo.setValue("codesetid",codesetid); 
	         var request=new Request({method:'post',asynchronous:false,onSuccess:return_ok,functionId:'1020010150'},hashvo); 
	         
	         function return_ok(parameters){
	            itemdesc = parameters.getValue("itemdesc");
	            m_sXMLFile="/system/get_code_treeinputinfo.jsp?codesetid="+codesetid+"&codeitemid="+codevalue + "&isfirstnode=" + flag;	 //
	            if(itemdesc!=null && itemdesc!="" && itemdesc!='null')
                  root=new xtreeItem("root",itemdesc,"","",itemdesc,"/images/unit.gif",m_sXMLFile);
                else
                  root=new xtreeItem("root","代码项目","","","代码项目","/images/unit.gif",m_sXMLFile);
                Global.closeAction="savecode();parent.hidePopWin(false);";
	             //Global.defaultInput=2;//checkbox =2 radio
	             root.setup(document.getElementById("treemenu"));  
	         }	
             
   </SCRIPT> 
   <br> 
    <input type="button" name="btnok" value="确定" class="mybutton" onclick="savecode();">
    <input type="button" name="btncancel" value="取消" class="mybutton" onclick="parent.hidePopWin(false);">    
<BODY>
</HTML>


