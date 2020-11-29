<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="com.hjsj.hrms.interfaces.webservice.SysoutSyncInterf"%>

<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.frame.utility.DateStyle"%>
<%@page import="com.hrms.hjsj.sys.EncryptLockClient,com.hjsj.hrms.businessobject.sys.SysParamBo"%>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
    String bosflag = "";
    String css_url="/css/css1.css";    
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null){
	  userName = userView.getUserFullName();
    if(css_url==null||css_url.equals(""))
 	  css_url="/css/css1.css";
      bosflag = userView.getBosflag();
	}
	String date = DateStyle.getSystemDate().getDateString();
    EncryptLockClient lock=(EncryptLockClient)pageContext.getServletContext().getAttribute("lock");
    int ver=lock.getVersion();
    String bborder="true";
//	if(ver>60)
//	    bborder="false";   
	String width="200";  
	///////////////////////特殊处理文档管理中间菜单宽度
	String url=request.getHeader("Referer");//System.out.println(url);得到点击路径
	if(url!=null){//liuy 2014-8-13 组织机构登记表500
		String[] urls=url.split("/general/tipwizard/tipwizard.do?");//根据特殊字符串拆分
		if(urls.length>1){
			String url_=urls[1];
			url_=url_.substring(1,7);//得到文档的跳转路径System.out.println(url_);
			if("link".equals(request.getParameter("b_init"))&&request.getParameter("b_init")!=null){
				if(url_.equals("br_law"))
				{
					width="255";
				}else{
					width="220";
				}
			}
		}else{
			if("link".equals(request.getParameter("b_init"))&&request.getParameter("b_init")!=null){
					width="220";
				}
		}
	}
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
<meta http-equiv="X-UA-Compatible" content="IE=9; IE=8; IE=7">
<title>人力资源信息管理系统　用户名：<%=userName%>　当前日期：<%=date%></title>

   <link href="<%=css_url%>" rel="stylesheet" type="text/css">
<!-- 引入Ext 框架 -->
	    <hrms:linkExtJs frameDegradeId="framedegrade"/> 
   <%if("hcm".equals(bosflag)){ %>
   <link href="/css/hcm/themes/<%=themes %>/content.css" rel="stylesheet" type="text/css" />
  
  <%} %>
   <style type="text/css">
   
   </style>   
</head>
<%if(ver<=40){%>
	<frameset name='forum' cols="200,9,*" border="0" frameborder="no">
	  <frame src="<hrms:insert parameter="HtmlMenu" />" frameborder="no" name="mil_menu" scrolling="auto" noresize>
	  <FRAME name=toogle marginWidth=0 marginHeight=0 src=/templates/but.jsp noResize scrolling=no>
	  <frame src="<hrms:insert parameter="HtmlBody" />" frameborder="no" name="mil_body" scrolling="auto" noresize>
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
		,border:false//<%=bborder%>
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
	    ,margins:'<%if("hcm".equals(bosflag)){ %>1<%}else{%>-1<%}%> -1 -1 0',
	    
	    html:'<iframe src="<hrms:insert parameter="HtmlBody" />" name="mil_body" id="center_iframe" scrolling="auto" width="100%" height="100%" frameborder="0"></iframe> ' 
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