<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.frame.utility.DateStyle"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%
    String css_url="/css/css1.css";
	
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String manager="";
	if(userView!=null)
		manager=userView.getManagePrivCodeValue();  
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
   	function savecode2()
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
    	   targethidden.value=codeitemid.substring(input_code_id.length);    	   
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
   <div id="treemenu" style="height: 300px;overflow: auto;border-style:inset ;border-width:2px"></div>
   <SCRIPT LANGUAGE=javascript>
             var codesetid,codevalue,name;
             var paraArray=dialogArguments; 
             var targetobj,targethidden;	
             var isAccord=1;  //是否 codeseitid 必须与返回的节点值的 codesetid需一致，才能返回值  1：需要  0：不需要
             codesetid = paraArray[0];
             codevalue = paraArray[1];
             //显示代码描述的对象
             targetobj=paraArray[2];
             //代码值对象
             targethidden=paraArray[3];   
             input_code_id=codesetid;
             var m_sXMLFile="/system/get_code_tree_train.jsp?codesetid="+codesetid+"&codeitemid=ALL"+"&flag="+paraArray[5];	 //
             try {
             eval(paraArray[4]);
             } catch(e) {
             	alert("属性设置错误！");
             }
             
             if (Global.defaultInput == 1) {
	             var codevalues = targethidden.value.split(",");
	             var codes = "";
	             for (i = 0; i < codevalues.length; i++) {
	             	codes += ("," + codesetid + codevalues[i]);
	             }
	             Global.checkvalue= "," + codes + ",";
             } else {
             	Global.checkvalue= codesetid + targethidden.value ;
             }
                          
             var root=new xtreeItem("root","代码项目","","","代码项目","/images/add_all.gif",m_sXMLFile);
             Global.closeAction="savecode();window.close();";
             //Global.defaultInput=paraArray[4];checkbox =2 radio
             
             
             root.setup(document.getElementById("treemenu"));
             
	     
   </SCRIPT>
   <br> 
    <input type="button" name="btnok" value='<bean:message key="button.ok"/>' class="mybutton" onclick="savecode();window.close();">
    <input type="button" name="btncancel" value='<bean:message key="button.cancel"/>' class="mybutton" onclick="window.close();">    
<BODY>
<script type="text/javascript">
function savecode() {
		var val= root.getSelected();
		var titles = root.getSelectedTitle();
		if (Global.defaultInput == 1) {
			var reg = new RegExp(","+codesetid,"g");
			val = "," + val;
			val = val.replace(reg,",");
			targethidden.value=val.substring(1,val.length -1);
			var titleArr = titles.split(",");
			var title = "";
			for (i = 0; i < titleArr.length; i++) {
				var tit = titleArr[i].split(":");
				if (tit[1]) {
					title += "," + tit[1];
				}
			}
			if (title.length > 0) {
				targetobj.value=title.substring(1,title.length);
			} else {
				targetobj.value="";
			}
		} else {
			savecode2();
		}
    }
</script>
</HTML>


