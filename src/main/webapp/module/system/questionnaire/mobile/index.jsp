<%@page import="com.hrms.frame.codec.SafeCode" pageEncoding="UTF-8"%>
<% request.getSession().removeAttribute("UserView"); %>
<!DOCTYPE HTML>
<html>
  <head>
    <title></title>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <meta name="viewport" content="initial-scale=1, maximum-scale=1, minimum-scale=1">
    <link rel="stylesheet" href="../../../../module/system/questionnaire/mobile/Font-Awesome-3.2.1/css/font-awesome.css" type="text/css" />
    <link rel="stylesheet" href="../../../../ext/ext6/cupertino-resources/theme-cupertino.css" type="text/css" />
    <script language="javascript" src="http://pv.sohu.com/cityjson"></script>
    <style type="text/css">
    	.component-title2 {
			font-size:1.1em;
			color:#157EFB;
			line-height:50px;
			padding:5px !important;
			text-align:center;
			vertical-align: middle;
		}
    	.component-title .x-innerhtml{
			background-color:#F8F9F9;
			font-size:1.1em;
			color:#157EFB;
			line-height:30px;
			padding:5px !important;
		}
		.option-title .x-innerhtml{
			background-color:#fff;
			font-size:1.1em;
			line-height:35px;
			padding-left:10px !important;
			border-bottom:1px dashed #dbdbe0;
			border-top:1px dashed #dbdbe0;
		}
		.x-input-myCls{
			color:gray;
		}
		.x-form-label span{
			font-weight:unset !important;
			margin-left:5px;
		}
		.x-form-label-nowrap .x-form-label{
			text-align: left !important;
		}
		.x-field-checkbox .x-field-mask::after, .x-field-radio .x-field-mask::after{
			margin-right:10px;
		}
		.img-border-radius{
			width:70px; height:70px;
			border: solid 3px #f0f0f0;
			border-radius:50px; 
		}
		.img-selected{
			box-shadow: 0 0 0 3px #f0f0f0;
			content:'3';
			font-family:'Pictos';
		  	border-radius: 50px;
 		 	filter:alpha(opacity=50); -moz-opacity:0.1; -khtml-opacity: 0.1; opacity: 0.3;
		}
		.sel-img{
			position:Relative;
		}
		.x-form-label-nowrap .x-form-label{
			text-align:left !important;
			padding-right:20px;
		}
		.x-form-label1 {
			margin-top:-40px;
			margin-left:25px;
    		padding: 0.6em 0.6em 0.6em 1em;
		}
    </style>
    <script type="text/javascript" src="../../../../ext/ext6/ext-modern-all.js"></script>
	<script type="text/javascript" src="../../../../ext/ext6/cupertino-resources/theme-cupertino.js"></script>
	<script type="text/javascript" src="../../../../ext/rpc_command.js"></script>
	<script type="text/javascript">
	<%
		String ip = request.getHeader("x-forwarded-for");
	    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
	      ip = request.getHeader("Proxy-Client-IP"); 
	    } 
	    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
	      ip = request.getHeader("WL-Proxy-Client-IP"); 
	    } 
	    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
	      ip = request.getHeader("HTTP_CLIENT_IP"); 
	    } 
	    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
	      ip = request.getHeader("HTTP_X_FORWARDED_FOR"); 
	    } 
	    if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
	      ip = request.getRemoteAddr(); 
	    } 
	    
        ip =ip.equals("0:0:0:0:0:0:0:1")?"127.0.0.1":ip;
	  	
	%>
		var planid = "<%=request.getParameter("planid")%>";
		var planid2 = "<%=request.getSession().getAttribute("planid")%>";
		var qnId = "<%=request.getParameter("qnId")%>";
		var mainObject = "<%=request.getParameter("mainObject")%>";
		var subObject = "<%=request.getParameter("subObject")%>";
		var cip ="<%=ip%>";
		var callback = "<%=request.getParameter("callback")%>";
		
		var isPreview = "<%=request.getParameter("isPreview")%>";
		var module = "<%=request.getParameter("module")%>";
		var w0501 = "<%=request.getParameter("w0501")%>";
		var w0301 = "<%=request.getParameter("w0301")%>";
		var url = "<%=request.getParameter("url")%>";
		if(module=="jobtitle")
			document.title = "鉴定意见";
		var configObj = {};
		configObj.cip = cip;
		configObj.qnId = qnId;
		configObj.mainObject = mainObject;
		configObj.subObject = subObject;
		configObj.isPreview = isPreview;
		configObj.module = module;
		configObj.w0501 = w0501;
		configObj.w0301 = w0301;
	</script>
	<script type="text/javascript" src="app.js"></script>
	
  </head>
  <body>
  </body>
  <script>
  	/*微信浏览器特殊处理顶部下拉出网址*/
  	document.addEventListener('touchstart', function(e) {
		var type = e.target.type;
		var clientWidth =e.target.clientWidth;
		var clientHeight =e.target.clientHeight;
		var scrollWidth =e.target.scrollWidth;
		var scrollHeight =e.target.scrollHeight;
		if(type == 'textarea'){//文本域特殊处理
			if(!(clientWidth == scrollWidth && clientHeight == scrollHeight)){
			    e.stopPropagation();
			}
		}
	}, { passive: false });
    document.addEventListener('touchmove', function(e) {
		var type = e.target.type;
		var clientWidth =e.target.clientWidth;
		var clientHeight =e.target.clientHeight;
		var scrollWidth =e.target.scrollWidth;
		var scrollHeight =e.target.scrollHeight;
		if(type == 'textarea'){//文本域特殊处理
			if((clientWidth == scrollWidth && clientHeight == scrollHeight)){
			    e.preventDefault();
			}
		}else{
			e.preventDefault();
		}
	}, { passive: false });
  </script>
</html>