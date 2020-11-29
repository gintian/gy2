<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="java.net.URLDecoder"%>
<%	String filename = request.getParameter("filename"); 
	filename = URLDecoder.decode(filename);
	//filename = URLEncoder.encode(filename);
	//System.out.println(filename);
%>
<html>
	<head>
		<title></title>
		<script type="text/javascript" src="dso.js"></script>
				
	</head>

	<body onload="load();" onunload="unload();" style="margin: 0px; padding: 0px;">
	<div id="div">
     <object classid="clsid:00460182-9E5E-11d5-B7C8-B8269041DD57" codebase ="dsoframer.ocx" id="oframe" width="0" height="0">
<param name="BorderStyle" value="0"/>
<param name="TitlebarColor" value="0"/>
<param name="TitlebarTextColor" value="0"/>
<!-- 此属性在ie7中没有问题，在ie8中有问题 -->
  <!--   <param name="FrameHookPolicy" value="1">-->
<param name="ActivationPolicy" value="1">
</object>
   
		
	</div></body>
	<script type="text/javascript" for="oframe" event="OnPrintPreviewExit">
		//alert("退出");
		document.getElementById('oframe').Toolbars = 1;
		var pagesetobj = parent.document.getElementsByName('pageset')[0];
		if (pagesetobj) {
			pagesetobj.disabled = false;
		}
		var excelprintobj = parent.document.getElementsByName('excelprint')[0];
		if (excelprintobj) {
			excelprintobj.disabled = false;
		}
	</script>	
	<script type="text/javascript">
		
		/*用法说明：
		  1，创建 word对象
		  2，设置文件上传url
		  3，在页面加载时，打开word文档，根据是否传人docUrl参数，决定是本地新建，还是从服务器端获取
		  4，在页面关闭时，执行上传操作。
		*/
            var word = new word();
            document.getElementById("oframe").OnPrintPreviewExit = function () {alert("vvv");}
		     //word.setUploadUrl("http://127.0.0.1:9080/aaa/upload.jsp");
		    var startOb ;
		     function load(){
		         //方法：openDoc(docName, docUrl)
		         // docName:必填，本地保存的文件名, 也为上传到服务器上时的文件名
		         // docUrl: 填时，为从服务器端获取doc文档的路径, 不填时，表示本地新建doc文档 
		         //word.openDoc('zhwm.doc');
			
		         startOb = setInterval("loadxls()", 100);
		        		         
		         
		     }
		     
		     function loadxls() {
		     	if (document.readyState == "complete") {
    				try{
		     			word.openDoc('1.xls','http://<%=request.getServerName() %>:<%=request.getServerPort() %>/servlet/DisplayOleFile?filename=<%=filename %>');
		     			var obj = document.getElementById("oframe");
		     			obj.width="100%";
		     			obj.height="100%";
		         		clearInterval(startOb);
		          	} catch(err) {}
		         }
		     }
		     function unload(){
		         //word.saveDoc();
		         word.close();
		     }
		     
		     // 页面设置
		     function pageset() {
		     	document.getElementById("oframe").ShowDialog(5);
		     	
		     }
		     
		     // 打印预览
		     function printview() {
		     	document.getElementById("oframe").PrintPreview()
		     }
		     
		     //打印
		     function printout() {
		     	document.getElementById("oframe").printout(true);
		     }
		</script>
</html>
