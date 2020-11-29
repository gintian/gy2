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
   <style type="text/css">
	body {  

	font-size: 12px;
	}
   </style>
</HEAD>
<body   topmargin="0" leftmargin="0" marginheight="0" marginwidth="0">
   <div id="treemenu" style="height: 350px;width:100%;overflow: auto;border-style:inset ;border-width:2px">
   		<input type="hidden" name="selectedId" id="selectedId" />
   		<iframe id="cascadetree" name="cascadetree" frameborder="0" scrolling="no" src="/system/options/customreport/cascadetree/tree.jsp" height="345" width="100%"></iframe>
   		<script type="text/javascript">
   			var targetobj,targethidden;
   			function startMod () {
   				var cascadetree = document.getElementById("cascadetree");
   				var paraArray=dialogArguments;
   				var codesetid = paraArray[0];
   				if (!codesetid) {
   					codesetid = 'UM';
   				}
             	var codevalue = paraArray[1];
             	document.getElementById("selectedId").value=codevalue;
             	// 显示的字
             	 targetobj=paraArray[2];
             	//代码值对象
             	 targethidden=paraArray[3];
             	// 是否加载权限
             	var priv = paraArray[4];
             	// 是否是多选
             	var checkmodel = paraArray[5];
             	//层级
             	var level = paraArray[6];
   				cascadetree.src = "/system/options/customreport/cascadetree/tree.jsp?codesetid="+codesetid+"&priv="+priv+"&checkmodel="+checkmodel+"&level="+level;
   				//document.frames.cascadetree.document.getElementById("valueid").value=targethidden.value;
   			}
   			startMod ();
   		</script>
   </div>
   
   <br> 
    <input type="button" name="btnok" value='<bean:message key="button.ok"/>' class="mybutton" onclick="savecode();window.close();">
    <input type="button" name="btncancel" value='<bean:message key="button.cancel"/>' class="mybutton" onclick="window.close();">    
<BODY>
<script type="text/javascript">
	function savecode() {
		document.frames.cascadetree.saveChecked();
		var valueid = document.frames.cascadetree.document.getElementById("valueid");
		var valuename = document.frames.cascadetree.document.getElementById("valuename");
		targetobj.value = valuename.value;
		targethidden.value = valueid.value;		
    }
</script>
</HTML>


