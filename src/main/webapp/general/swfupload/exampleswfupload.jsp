<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>

    
    <title>My JSP 'index.jsp' starting page</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<script language="javascript" src="/js/constant.js"></script>
	<script language="javascript" src="/js/common.js"></script>
	<hrms:themes></hrms:themes>
  </head>
  
  <body>
    <div id="div"></div>
  </body>
  <script>
	var obj = new Object();
  	// 显示文件名的输入框的样式默认为class='textColorRead'定义的样式
  	obj.inputStyle = "width:300px;";
  	// “选择文件”按钮的样式，默认样式为class='mybutton'定义的样式
  	obj.buttonStyle = "";
  	// 单个文件的最大值，默认为10,
  	obj.fileMaxSize = "200";
  	//单个上传文件的单位，默认为MB,还可以使用GB、KB
  	obj.fileSizeUnit = "MB";
  	// 文件的扩展名，限定上传文件的类型,默认是任意类型（*.*）,多个文件类型用分号隔开，例如*.jpg;*.jpeg;*.gif
  	obj.fileExt = "*.jpg;*.jpeg;*.rmvb;*.flv;*.f4v";
  	// 需要传入的其他参数
  	obj.post_params = {
		"user_id" : "stephen830",
		"pass_id" : "123456"
	};
	// 文件类型的描述，默认为“文件类型”
	obj.file_types_desc = "文件类型";
	// 最多上传的文件个数，默认为100
	obj.file_upload_limit=100;
	
swfupload("div","file","/train/media/upload","0",obj);
//swfuploadsingle("div","file","/train/media/upload","0",obj);
</script>
</html>
