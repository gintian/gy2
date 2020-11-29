<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>


<!DOCTYPE html>
<html>
  <head>
    <script src="/ext/ext-all.js" ></script>
    <script src="/ext/ext-lang-zh_CN.js" ></script>
    <script src="/components/querybox/QueryBox.js" ></script>
    <link rel="stylesheet" href="/components/tableFactory/tableGrid-theme/tableGrid-theme-all.css"/>
    <style type="text/css">

		.text-sh{text-overflow:ellipsis;white-space:nowrap;overflow:hidden} 
		
    </style>
  </head>
  <body>
  		<br>
  		<br>
  		<br>
  		<br>
  		<br>
	   &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<div id="aa" style="width:200px;"></div>
  </body>
<script>
<!--
	
	Ext.onReady(function(){
				
		var SearchBox = Ext.create("SYSQ.QueryBox",{renderTo : "aa",hideQueryScheme:false,selectedId:74,subModuleId:"aaaaaaaaaa11111",funcId:"ZJ100000052",success:success,width:380,
		
			fieldsArray:[{type:"A",itemid:"A0101",itemdesc:"姓名",codesetid:"0",formate:"Y-m-d H:i:s"},{type:"A",itemid:"A0107",itemdesc:"性别",codesetid:"AX",formate:""},{type:"D",itemid:"A0111",itemdesc:"出生日期",codesetid:"0",formate:"Y-m-d"},{type:"N",itemid:"A0108",itemdesc:"年龄",codesetid:"0",formate:""}]
		});

	});
	
	function success(map){
		console.log(map.status);
	}
	
//-->
</script>
</html>

