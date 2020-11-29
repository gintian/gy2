<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.frame.utility.DateStyle"%>
<%@page import="com.hrms.hjsj.sys.EncryptLockClient"%>
<%@ page import="com.hrms.struts.constant.SystemConfig,com.hjsj.hrms.businessobject.sys.SysParamBo"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
    String css_url="/css/css1.css";  
    String bosflag = "";  
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null){
	  userName = userView.getUserFullName();
    if(css_url==null||css_url.equals(""))
 	  css_url="/css/css1.css";
  bosflag = userView.getBosflag();
	}
	int flag=1;
	String webserver=SystemConfig.getPropertyValue("webserver");
	if(webserver.equalsIgnoreCase("websphere"))
		flag=2;
	String date = DateStyle.getSystemDate().getDateString();
    EncryptLockClient lock=(EncryptLockClient)pageContext.getServletContext().getAttribute("lock");
    int ver=lock.getVersion();	
    String bborder="true";
    if("hcm".equals(bosflag)){
        bborder = "false";
    }
    String themes="default";
    if(userView != null){
        /*xuj added at 2014-4-18 for hcm themes*/
        themes=SysParamBo.getSysParamValue("THEMES",userView.getUserName());
    }
%>
<html>
<head>
<title>人力资源信息管理系统　用户名：<%=userName%>　当前日期：<%=date%></title>
   <link href="<%=css_url%>" rel="stylesheet" type="text/css">
<!-- 引入Ext 框架 -->
	    <hrms:linkExtJs frameDegradeId="framedegrade"/> 
     <script type="text/javascript" src="/train/resource/course/scorm/scormapi.js"></script>
     
      <link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>
<script language="javascript" src="/ajax/constant.js"></script>
<script language="javascript" src="/ajax/basic.js"></script>
<script language="javascript" src="/train/resource/course/scorm/common.js"></script>
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
 <script type="text/javascript">
  	function test() {
	  	var api = new SCOAPI();
	  	api.init("123213");
	  	alert(api.servletURL);
  	}
  	function init() {
  		API = new SCOAPI();
  		API.debug = false;
  		<logic:equal value="0" name="courseLessonForm"  property="isLearn">
  			API.isLearn = "0";
  		</logic:equal>
  		<logic:notEqual value="0" name="courseLessonForm"  property="isLearn">
  			API.isLearn = "1";
  		</logic:notEqual>
  	}
  </script> 
  <%if("hcm".equals(bosflag)){ %>
   <link href="/css/hcm/themes/<%=themes %>/content.css" rel="stylesheet" type="text/css" />
  
  <%} %> 
</head>
<body onload="init()">
<%if(ver<=40){%>
<frameset cols="250,*"  bordercolor='#DCEFFE' framespacing="2" >
  <frame src="<hrms:insert parameter="HtmlMenu" />" frameborder="1"  name="mil_menu" scrolling="auto" >
  <frame src="<hrms:insert parameter="HtmlBody" />" frameborder="1"  name="mil_body" scrolling="auto" >
</frameset>
 <%}%>
  <%if(ver>40){%>
 <script type="text/javascript">
	Ext.onReady(function() { 
		<%if(!"true".equals(framedegrade)){%>
		Ext.define("Diy.resizer.Splitter",{override:"Ext.resizer.Splitter",size:1,cls:'mysplitter'});
		<%}%>
		Ext.QuickTips.init(); 
		var viewport = new Ext.Viewport({
		header:false, 
		id:'simplevp' 
		,layout:'border' 
		,items:[
		{ 
		header:false,
	     margins:'-1 0 -1 -1',	
		 region:'west' 
		,width:180 
		,html:'<iframe src="<hrms:insert parameter="HtmlMenu" />" name="mil_menu" id="center_iframe" scrolling="auto" width="100%" height="100%" frameborder="0"></iframe> ' 
		,collapsible:false
		,split:true 
		,collapseMode:'mini'
		,border:<%=bborder%> 
		},
		{
		header:false, 
		 region:'center' 
	    ,margins:'-1 -1 -1 0'			 
		,html:'<iframe src="<hrms:insert parameter="HtmlBody" />" name="mil_body" id="center_iframe" scrolling="auto" width="100%" height="100%" frameborder="0"></iframe> ' 
		,border:<%=bborder%>
		}] 
		}); 
	});  
</script>
 <%}%> 
 </body>
 <script language="javascript">
	//解决IE文本框自带历史记录问题  jingq add 2014.12.31
	var inputs = document.getElementsByTagName("input");
	for ( var i = 0; i < inputs.length; i++) {
		if(inputs[i].getAttribute("type")=="text"){
			inputs[i].setAttribute("autocomplete","off");
		}
	}
</script>
 <script language="javascript">
   var dropDownDate=createDropDown("dropDownDate");
   var __t=dropDownDate;
   __t.type="date";
   __t.tag="";
   _array_dropdown[_array_dropdown.length]=__t;
   initDropDown(__t);
   
   var dropDownList=createDropDown("dropDownList");
   var __t=dropDownList;
   __t.type="list";
   __t.tag="";
   _array_dropdown[_array_dropdown.length]=__t;
   initDropDown(__t);   
</script>
<script language="javascript">
    var dropdownCode=createDropDown("dropdownCode");
    var __t=dropdownCode;
    __t.type="custom";
    __t.path="/general/muster/select_code_tree.do";
	//__t.path="/system/gcodeselect.jsp";    
    __t.readFields="codeitemid";
    //__t.writeFields="xxxx";
    __t.cachable=true;__t.tag="";
    _array_dropdown[_array_dropdown.length]=__t;
    initDropDown(__t);
</script>
<script language="javascript">
  initDocument();
</script>
</html>