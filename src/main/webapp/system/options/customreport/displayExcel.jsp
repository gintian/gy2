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
				
	</head>

	<body onload="load();" style="position: relative;margin: 0px; padding: 0px;overflow: hidden;" onunload="clo();">
	<div id="div" style="position: relative;margin:0xp;padding:0px;">
     <script type="text/javascript" src="WebOffice.js"></script>
   
		
	</div></body>

	
	<script type="text/javascript" for="oframe" event="NotifyToolBarClick(index)">
		//alert(index);
		//oframe_NotifyToolBarClick(index);
	</script>
	<script type="text/javascript" for="oframe" event="WindowBeforeDoubleClick">
		//return ;
	</script>
	<script type="text/javascript">
	<!--
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
		     			readyCtrl();
		         		clearInterval(startOb);
		          	} catch(err) {}
		         }
		     }
		
		function readyCtrl() { 
			clearInterval(startOb);
			document.all.oframe.ReadOnly = 1;
			
			document.all.oframe.ShowToolBar=0;
			
			document.all.oframe.LoadOriginalFile("http://<%=request.getServerName() %>:<%=request.getServerPort() %>/servlet/DisplayOleFile?filename=<%=filename %>", "xls");
			//转成aip
			//document.all.oframe.ConvertToAip (0,1);
			
			//document.all.oframe.HideMenuAction(5,0);
			
			
			//document.all.oframe.UnActiveExcel();
			// 隐藏所有菜单
			document.all.oframe.HideMenuArea("hideall","","","");
			
			
			var obj = document.all.oframe.GetDocumentObject();
			// 隐藏菜单栏
			obj.Application.CommandBars(1).Enabled = false;
			parent.getScript();
			
			//document.all.oframe.HideMenuAction(5,0);
			//document.all.oframe.SetCustomToolBtn(0,"打印");
			// 隐藏新建
			//document.all.oframe.HideMenuItem(0x01 + 0x1000);
			//document.all.oframe.HideMenuItem(0x02 + 0x1000);
			//document.all.oframe.HideMenuItem(0x04 + 0x1000);
			//document.all.oframe.HideMenuItem(0x20 + 0x1000);
			//document.all.oframe.HideMenuItem(0x1000 + 0x1000);
			//document.all.oframe.HideMenuItem(0x4000 + 0x1000);
			
			
		}
		function clo() {
			var obj = document.all.oframe.GetDocumentObject();
			obj.Application.CommandBars(1).Enabled = true;
			obj.Application.CommandBars("cell").Enabled = true;
			document.all.oframe.CloseDoc(0);
			
		}
		function printview() {
		
		}


			// 页面设置
		     function pageset() {
		     	document.all.oframe.UnActiveExcel();
		     	var obj = document.all.oframe.GetDocumentObject();
		     	//document.all.oframe.ShowDialog(7);
		     	obj.Application.Dialogs(7).Show();
		     	
		     }
		     
		     // 打印预览
		     function printview() {
				document.all.oframe.UnActiveExcel();
				var obj = document.all.oframe.GetDocumentObject();
				obj.Worksheets("Sheet1").PrintPreview().Activate();
		     }
		     
		     //打印
		     function printout() {
		     	document.all.oframe.UnActiveExcel();
		     	document.all.oframe.PrintDoc(1);		     	
		     }
		     
		     function oframe_NotifyToolBarClick(iIndex){
				if (iIndex == 0) {
				document.all.oframe.PrintDoc(1);
				}
			}
	//-->	
	</script>
</html>
