<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.frame.utility.DateStyle"%>
<%
    String css_url="/css/css1.css";
	
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String manager="";
	if(userView!=null){
		manager=userView.getManagePrivCode()+userView.getManagePrivCodeValue(); 
		if(manager.length()>2){
			manager=manager.substring(2);
		}else if(manager.length()==2){
			manager="ALL";
		}
	} 
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
    	    /*输入相关代码类和选择的相关代码一致*/      	    	   
    	   if(isAccord==1)
    	   {
    	      if(!((input_code_id=="UM")||(input_code_id=="@K")))
		      {
    	   	    if((input_code_id!=codeitemid.substring(0,2))&&codeitemid.substring(0,2)!="1_")
    	    	 return ;    	   
    	      }else if(codeitemid=="" ||codeitemid=="root" || codeitemid.substring(0,input_code_id.length)!=input_code_id)
    	      {
    	        return ; //如果点击了根节点或者非想要选择的组织机构（部门和职位就只能选择部门和职位）   	   
    	      }
	       }else if(isAccord==3)//兼职
	       {
	          if(codeitemid==""||(codeitemid.substring(0,2)!="UN"&&codeitemid.substring(0,2)!="UM"))
    	      {
    	        return ; //如果点击了根节点或者非想要选择的组织机构（部门和职位就只能选择部门和职位）   	   
    	      }
	       }
    	   targetobj.value=currnode.text;
    	   
    	   targethidden.value=codeitemid;    	   
    	   //targetobj.fireEvent("onchange");  chenmengqing added 20070819,谁把它放开啦。。。	
   	}
   </SCRIPT>
   <style type="text/css">
	body {  

	font-size: 12px;
	}
   </style>
</HEAD>
<body   topmargin="0" leftmargin="0" marginheight="0" marginwidth="0">
   <div id="treemenu" style="height: 300px;overflow: auto;" class="complex_border_color"></div>
   <SCRIPT LANGUAGE=javascript>
             var codesetid,codevalue,name;
             var paraArray=dialogArguments; 
             var targetobj,targethidden;	
             var isAccord=0;  //是否 codeseitid 必须与返回的节点值的 codesetid需一致，才能返回值  1：需要  0：不需要
             codesetid = paraArray[0];
             codevalue = paraArray[1];
             //显示代码描述的对象
             targetobj=paraArray[2];
             //代码值对象
             targethidden=paraArray[3];   
             input_code_id=codesetid;
             var m_sXMLFile="/system/get_code_tree.jsp?codesetid="+codesetid+"&codeitemid=ALL";	 //
             if(targethidden.name=="querycond"&&"false"=="<%=userView.isSuper_admin() %>")
             	m_sXMLFile="/system/get_code_tree.jsp?codesetid="+codesetid+"&codeitemid=<%=manager %>&privflag=1";	 //
             if(paraArray.length==5)
              	isAccord=paraArray[4];        
             	
             var root;
			 if("UN" == codesetid || "UM" == codesetid || "@K" == codesetid)/*update by xiegh on 20180518 bug37283 领导桌面：选择机构对话框中字应该改为组织机构  */
             	root = new xtreeItem("root","组织机构","","","组织机构","/images/add_all.gif",m_sXMLFile);
			 else
             	root = new xtreeItem("root","代码项目","","","代码项目","/images/add_all.gif",m_sXMLFile);
			 
             //var root=new xtreeItem("root","代码项目","","","代码项目","/images/add_all.gif",m_sXMLFile);
             Global.closeAction="savecode();window.close();";
             //Global.defaultInput=2;//checkbox =2 radio
             root.setup(document.getElementById("treemenu"));
	     
   </SCRIPT> 
   <br> 
   <table width="100%" border="0" cellspacing="0" align="center" cellpadding="0">
   	<tr>
   		<td align="center">
		    <input type="button" name="btnok" value='<bean:message key="button.ok"/>' class="mybutton" onclick="savecode();window.close();">
		    <input type="button" name="btncancel" value='<bean:message key="button.cancel"/>' class="mybutton" onclick="window.close();">    
   		</td>
   	</tr>
   </table>
<BODY>
</HTML>


