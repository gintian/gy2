<%@ page contentType="text/html; charset=UTF-8"%>

<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.frame.utility.DateStyle"%>
<%@page import="com.hrms.hjsj.sys.EncryptLockClient,com.hjsj.hrms.businessobject.sys.SysParamBo"%>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
    String css_url="/css/css1.css";   
    String bosflag = "";
    String themes="themes";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null){
	  userName = userView.getUserFullName();
    if(css_url==null||css_url.equals(""))
 	  css_url="/css/css1.css";
    bosflag = userView.getBosflag();
    /*xuj added at 2014-4-18 for hcm themes*/
    themes=SysParamBo.getSysParamValue("THEMES",userView.getUserName());
	}
	String date = DateStyle.getSystemDate().getDateString();
    EncryptLockClient lock=(EncryptLockClient)pageContext.getServletContext().getAttribute("lock");
    int ver=lock.getVersion();
    String bborder="true";
//	if(ver>60)
//	    bborder="false";   
	String width="200";  
	if("link".equals(request.getParameter("b_init"))&&request.getParameter("b_init")!=null)
		width="220";
	
    if("hcm".equals(bosflag)){
        bborder = "false";
    }
%>
<html>
<head>
<title>人力资源信息管理系统　用户名：<%=userName%>　当前日期：<%=date%></title>
   <link href="<%=css_url%>" rel="stylesheet" type="text/css">
<!-- 引入Ext 框架 -->
	    <hrms:linkExtJs frameDegradeId="framedegrade"/>
   <%if("hcm".equals(bosflag)){ %>
   <link href="/css/hcm/themes/<%=themes %>/content.css" rel="stylesheet" type="text/css" />
  
  <%} %>  
</head>
<%if(ver<=40){%>
	<frameset name='forum' cols="200,9,*" border="0" frameborder="no">
	  <frame src="<hrms:insert parameter="HtmlMenu" />" frameborder="no" name="mil_menu" scrolling="auto" noresize>
	  <FRAME name=toogle marginWidth=0 marginHeight=0 src=/templates/but.jsp noResize scrolling=no>
	  <frame src="<hrms:insert parameter="HtmlBody" />" frameborder="no" name="mil_body_budgeting" scrolling="auto" noresize>
	</frameset>
 <%}%>
 <%if(ver>40){%>
 <script type="text/javascript">
    var menuc;

	Ext.onReady(function() { 
	 <%if(!"true".equals(framedegrade)){%>
		Ext.define("Diy.resizer.Splitter",{override:"Ext.resizer.Splitter",size:1,cls:'mysplitter'});
		<%}%>
	     menuc = new Ext.Panel({
	     header:false,
	     margins:'-1 0 -1 -1',	
		 region:'west' 
		,width:<%=width %> 		
		,html:'<iframe src="<hrms:insert parameter="HtmlMenu" />" name="mil_menu" id="center_iframe" scrolling="auto" width="100%" height="100%" frameborder="0"></iframe> ' 
		,collapsible:false
		,split:true 
		,border:<%=bborder%>
		,collapseMode:'mini' 
         });
		Ext.QuickTips.init(); 
		var viewport = new Ext.Viewport({ 
		header:false,
		id:'simplevp' 
		,layout:'border' 
		,items:[menuc,
		{
		header:false,
		 region:'center' 
	    ,margins:'-1 -1 -1 0'			 
		,html:'<iframe src="<hrms:insert parameter="HtmlBody" />" name="mil_body_budgeting" id="center_iframe" scrolling="auto" width="100%" height="100%" frameborder="0"></iframe> ' 
		,border:<%=bborder%>
		}] 
		}); 
	});  
</script>
 <%}%> 
 <script language="javascript">
	//解决IE文本框自带历史记录问题  jingq add 2014.12.31
	var inputs = document.getElementsByTagName("input");
	for ( var i = 0; i < inputs.length; i++) {
		if(inputs[i].getAttribute("type")=="text"){
			inputs[i].setAttribute("autocomplete","off");
		}
	}
</script>
</html>