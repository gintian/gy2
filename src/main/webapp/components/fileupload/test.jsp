<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    
    <title>My JSP 'test.jsp' starting page</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<link rel="/css/css1.css" type="text/css" href="styles.css">
	<script src="/ext/ext-all.js" ></script>
    <script src="/ext/ext-lang-zh_CN.js" ></script>
    <script src="/ext/rpc_command.js" ></script>
    <script src="/components/fileupload/FileUpLoad.js" ></script>
    <link rel="stylesheet" href="/components/tableFactory/tableGrid-theme/tableGrid-theme-all.css"/>
    <style type="text/css">
    	.mybutton{
	border:1px solid #84ADC9;
	background:url(../images/shu_bg_bg.gif) repeat-x 0 -2 right;
	height:23px;
	font-size:12px;
	line-height:20px;
	padding-left:4px;
	padding-right:3px;
	overflow:visible;
	/*margin-left:1px;*/
	color:#36507E;
	background-color: transparent;	
	cursor: pointer; 	
	margin:0px 7px 0px 0;
	font-family:"微软雅黑";
}

	.swfupload{
		font-family:"微软雅黑";
		line-height:16px;
		text-align:'center';
		cursor: pointer; 
		color: #1b4a98 !important;
	}
    </style>
  </head>
  
  <body>
  		<br><br><br>
    	<div id="aa" style="margin-left:50px;"></div>
    	<br><br><br>
    	<div id="bb" style="width:30px;height:30px;border:1px solid red;"></div>
  </body>
  <script>
  	Ext.onReady(function(){ 		
  		 		
  		Ext.create("SYSF.FileUpLoad",{
  			savePath:'xGqN2Cn8E6dyGPAATTP2HJFPAATTPcNHNIz8FhSBnEOCO97uZAVsXIBBXocDrCWXwHNXwPAATTP3HJDPAATTPPAATTP3HJDPAATTP',
  			emptyText:"请输入文件路径或选择文件",renderTo:'bb',upLoadType:2,success:aa,isDownload:true,isDelete:false,isShowOrEdit:'1',
  			fileTypeMapList:[{dataValue:'bbb',dataName:'bbb'},{dataValue:'aaa',dataName:'aaa'}]
  		});
  		
  	});
  	function aa(fileObj){
  		console.log(fileObj);
  	}
  	
  </script>
</html>
