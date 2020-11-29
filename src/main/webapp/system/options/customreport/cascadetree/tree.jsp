<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>My JSP 'tree.jsp' starting page</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!-- zgd 2014-12-22 delete 自定义报表中机构树方法是单独的 不支持新的ext 所以继续走V62的ext start
	<link rel="stylesheet" type="text/css" href="/ext/resources/css/ext-all.css">
	end -->
	<!-- zgd 2014-12-22 add 自定义报表中机构树方法是单独的 不支持新的ext 所以继续走V62的ext start-->
	<link rel="stylesheet" type="text/css" href="/system/options/customreport/cascadetree/js/ext-all-report.css">
	<!-- zgd 2014-12-22 add 自定义报表中机构树方法是单独的 不支持新的ext 所以继续走V62的ext end-->
	<style type="">
	.no-node-icon {display:none;} 
	
	</style>

  </head>
  
  <body>
  	<input id="valuename" type="hidden" name="valuename" />
  	<input id="valueid" type = "hidden" name = "valueid"/>
    <script type="text/javascript" src="/ext/adapter/ext/ext-base.js"></script>
    <!-- zgd 2014-12-22 delete 自定义报表中机构树方法是单独的 不支持新的ext 所以继续走V62的ext start
    <script type="text/javascript" src="/ext/ext-all.js"></script>
     end -->
    <!-- zgd 2014-12-22 add 自定义报表中机构树方法是单独的 不支持新的ext 所以继续走V62的ext start-->
     <script type="text/javascript" src="/system/options/customreport/cascadetree/js/ext-all-report.js"></script>
     <!-- zgd 2014-12-22 add 自定义报表中机构树方法是单独的 不支持新的ext 所以继续走V62的ext end-->
     <script type="text/javascript" src="/system/options/customreport/cascadetree/js/ext-lang-zh_CNl.js"></script>
	<script type="text/javascript">
		 var glbRootPath = "<%=basePath%>";
		 var basePath = "<%=basePath%>";
		 var nid = "0";
		 var rootText = '组织机构';
		 var codeset = '<%=request.getParameter("codesetid") %>';
		 //var width=265;  zgd 2014-12-22 delete
		 //var height=345;  zgd 2014-12-22 delete
		 var width=275;
		 var height=347;
		 var selectedId = window.parent.targethidden.value;
		 // 是否加载权限，0为不加载，1为加载
		 var privs = '<%=request.getParameter("priv") %>';
		 // 单选还是多选
		 var checkmodel = '<%=request.getParameter("checkmodel") %>';
		 // 层级
		 var level = '<%=request.getParameter("level") %>';
		 //Ext.BLANK_IMAGE_URL = "<%=basePath %>tree/js/ext/resources/images/default/s.gif";
	</script>
	<script type="text/javascript" src="/system/options/customreport/cascadetree/js/Ext.ux.tree.TreeCheckNodeUI.js"></script>
    <script type="text/javascript" src="/system/options/customreport/cascadetree/js/console-index.js"></script>
    <script type="text/javascript">
    // 文档加载完毕执行
	Ext.onReady(function(){
		Ext.BLANK_IMAGE_URL = "/ext/resources/images/default/s.gif";
		
		NavTree.init();
		NavTree.show();
		
	});
	
	// 保存已选中的id
	function saveChecked () {
		var tree = Ext.getCmp(nid);
		var ids = tree.getChecked('id');
		var texts = tree.getChecked('text');
		var valueid = document.getElementById("valueid");
		var valuename = document.getElementById("valuename");
		valueid.value=ids;
		valuename.value = texts; 
	}
	
		function  checkNodesByCheckedId(nodeids) {alert("ddd");
			var roottree = Ext.tree;
		}
    </script>
    
     <!--  <input type="button" name="bu" value="确定" onclick="saveChecked ()"/>-->
  </body>
</html>
