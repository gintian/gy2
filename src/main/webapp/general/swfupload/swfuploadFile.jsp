<%@ page contentType="text/html;charset=UTF-8"%>

<%
			
	String ___uploadPrefix = "mail";//objectType,默认使用"".
	request.getSession().setAttribute("___uploadPrefix",___uploadPrefix);
	//String attachScript =UploadService.getInstance().getAttchmentScripts(___uploadPrefix,"112233");
%>
<script type="text/javascript">
<!--
	var uploadObj = window.dialogArguments;
//-->
</script>

<%
	double perMaxSize = 200;//单个文件允许的max大小
	String sizeUnit = "MB";//perMaxSize数据对应的单位
	//String ext = "*.jpg;*.jpeg;*.gif";//允许上传的文件类型
	String ext = "*.*";
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<title>批量文件上传</title>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<link href="/css/css1.css" rel="stylesheet" type="text/css" />
		<script type="text/javascript" src="/general/swfupload/js/swfupload.js"></script>
		<script type="text/javascript" src="/general/swfupload/js/swfupload.swfobject.js"></script>
		<script type="text/javascript" src="/general/swfupload/js/swfupload.queue.js"></script>
		<script type="text/javascript" src="/general/swfupload/js/fileprogress.js"></script>
		<script type="text/javascript" src="/general/swfupload/js/handlers.js"></script>

		<script type="text/javascript">
var swfu;

SWFUpload.onload = function () {
	var settings = {
		flash_url : "/general/swfupload/swfupload.swf",
		upload_url: uploadObj.uploadUrl,
		post_params: uploadObj.post_params,
		file_size_limit : uploadObj.fileMaxSize + uploadObj.fileSizeUnit,
		file_types : uploadObj.fileExt,
		file_types_description : uploadObj.file_types_desc,
		file_upload_limit : uploadObj.file_upload_limit,
		file_queue_limit : 0,
		custom_settings : {
			progressTarget : "fsUploadProgress",
			cancelButtonId : "btnCancel",
			uploadButtonId : "btnUpload",
			myFileListTarget : "idFileList"
		},
		debug: false,
		auto_upload:false,

		// Button Settings
		//button_image_url : "/images/XPButtonUploadText_61x22.png",	// Relative to the SWF file
		button_placeholder_id : "spanButtonPlaceholder",
		button_text: '浏览...',
		button_text_style : "color: #36507E;cursor: pointer;font-size: 12px;",
		button_text_left_padding : 6,
		button_text_top_padding : 0,
		
	   
		button_width: 61,
		button_height: 21,

		// The event handler functions are defined in handlers.js
		swfupload_loaded_handler : swfUploadLoaded,
		file_queued_handler : fileQueued,
		file_queue_error_handler : fileQueueError,
		file_dialog_complete_handler : fileDialogComplete,
		upload_start_handler : uploadStart,
		upload_progress_handler : uploadProgress,
		upload_error_handler : uploadError,
		upload_success_handler : uploadSuccess,
		upload_complete_handler : uploadComplete,
		queue_complete_handler : queueComplete,	// Queue plugin event
		
		// SWFObject settings
		minimum_flash_version : "9.0.28",
		swfupload_pre_load_handler : swfUploadPreLoad,
		swfupload_load_failed_handler : swfUploadLoadFailed
	};

	swfu = new SWFUpload(settings);
}

function checkAll(obj) {
	var checkbox = document.getElementsByTagName("input");
	if (obj.checked) {
		for (i = 0; i < checkbox.length; i++) {
			if (checkbox[i].type=='checkbox' && checkbox[i].disabled==false && checkbox[i].name !='check_all') {
				checkbox[i].checked = true;
			}
		}
	} else {
		for (i = 0; i < checkbox.length; i++) {
			if (checkbox[i].type=='checkbox' && checkbox[i].disabled==false && checkbox[i].name !='check_all') {
				checkbox[i].checked = false;
			}
		}
	}
}

function bok() {
	var count = 0;
	var name = "";
	var value = "";
	var checkbox = document.getElementsByTagName("input");
	for (i = 0; i < checkbox.length; i++) {
		if (checkbox[i].type=='checkbox' && checkbox[i].disabled==false && checkbox[i].name !='check_all' && checkbox[i].checked==true) {
			count++;
			name += document.getElementById(checkbox[i].value + "_hidden_name").value + ",";
			value += document.getElementById(checkbox[i].value + "_hidden_value").value +":" + document.getElementById(checkbox[i].value + "_hidden_name").value + ",";
		}
	}
	
	if (count > 0) {
		var obj = new Object();
		obj.name = name.substring(0,name.length - 1);
		obj.value = value.substring(0,value.length - 1);
		window.returnValue = obj;
		window.close();
	} else {
		alert("未选择上传的文件！");
		return;
	}
}
</script>
	</head>
	<body>
		<br>
		<table width="95%" cellspacing="0" align="center" cellpadding="0" class="ListTable">
			<tr>
				<td>
					
					<!-- 内容开始 -->
					
						<form id="form1" action="UploadFileExampleSubmit.jsp"
							method="post" enctype="multipart/form-data">
							<div id="content" style="height: 250px;BORDER-LEFT: #C4D8EE 1pt solid;BORDER-right: #C4D8EE 1pt solid;" >
							<!-- 文件列表 -->
							<table id="idFileList" border="0" cellspacing="0" cellpadding="0" class="ListTable" width="100%" style="margin-top: 0px;" >
								<tr >
									<td width='6%' align="center" class="TableRow" style="border-left-width: 0px;">
										<input type="checkbox" name="check_all" id="check_all" title="全选" onclick='checkAll(this)'/>
									</td>
									<td align="center" class="TableRow" width='44%'>
										文件名
									</td>
									<td align="center" class="TableRow" width='15%' nowrap="nowrap">
										文件大小
									</td >
									<td width=100px align="center" class="TableRow" width='25%' nowrap="nowrap">
										状态
									</td>
									<td width=35px align="center" class="TableRow" width='10%' nowrap="nowrap" style="border-right-width: 0px;">
										取消
									</td>
								</tr>
							</table>
							</div>
							
							<table  border="0" cellspacing="0" cellpadding="0"  class="ListTable" width="100%" >
								<tr >
									<td width='10%' align="left" class="RecordRow" style="border-top-width: 1px;">
										<!-- 文件上传的状态栏 -->
							&nbsp;等待上传<span id="idFileListCount">0</span> 个 ，
							成功上传<span id="idFileListSuccessUploadCount">0</span> 个
							<div id="divSWFUploadUI" style="visibility: hidden;"></div>
									</td>
								</tr>
							</table>
							<br>
							
							<!-- 控制按钮 -->
							<table border="0" cellspacing="0" cellpadding="0" class="ListTable" width="100%">
								<tr>
									<td align="center">
										<span id='spanButtonPlaceholder'></span>
										
										&nbsp;<input id="btnUpload" type="button" value="上传文件" class="mybutton" />
										&nbsp;<input id="btnCancel" type="button" value="取消全部上传"
											disabled="disabled" class="mybutton" />
											&nbsp;<input type="button" value="确定" class="mybutton" onclick="bok()"/>
										&nbsp;<input type="button" value="取消"
											class="mybutton" onclick="window.close();"/>
									</td>
								</tr>
							</table>
							<noscript
								style="display: block; margin: 10px 25px; padding: 10px 15px;">
								很抱歉，相片上传界面无法载入，请将浏览器设置成支持JavaScript。
							</noscript>
							<div id="divLoadingContent" class="content"
								style="background-color: #FFFF66; border-top: solid 4px #FF9966; border-bottom: solid 4px #FF9966; margin: 10px 25px; padding: 10px 15px; display: none;">
								相片上传界面正在载入，请稍后...
							</div>
							<div id="divLongLoading" class="content"
								style="background-color: #FFFF66; border-top: solid 4px #FF9966; border-bottom: solid 4px #FF9966; margin: 10px 25px; padding: 10px 15px; display: none;">
								相片上传界面载入失败，请确保浏览器已经开启对JavaScript的支持，并且已经安装可以工作的Flash插件版本。
							</div>
							<div id="divAlternateContent" class="content"
								style="background-color: #FFFF66; border-top: solid 4px #FF9966; border-bottom: solid 4px #FF9966; margin: 10px 25px; padding: 10px 15px; display: none;">
								很抱歉，相片上传界面无法载入，请安装或者升级您的Flash插件。 请访问：
								<a
									href="http://www.adobe.com/shockwave/download/download.cgi?P1_Prod_Version=ShockwaveFlash"
									target="_blank">Adobe网站</a> 获取最新的Flash插件。
							</div>
							
							
							
							
							<!-- 已经删除的附件的逻辑名称（必须）,用逗号分割 -->
							<input type="hidden" id="deleteFiles" name="deleteFiles" value="">
							<!--新增的附件的逻辑名称（必须）,用逗号分割 -->
							<input type="hidden" id="fileOids" name="fileOids" value="">
		
							<!--objType -->
							<input type="hidden" id="objType" name="objType" value="<%=___uploadPrefix %>">
							<!-- //附件存放位置：DB，数据库，FileDir ,文件目录 , DBAndFileDir , 两者都保存。 此处为冗余．-->
							<input type="hidden" id="UploadMode" name="UploadMode" value="1111111111111">
							
							<input type="hidden" id="uploadPrefix" name="uploadPrefix" value="<%=___uploadPrefix %>">
							
							
							
							
						</form>
					
				</td>
			</tr>
		</table>
	</body>
</html>
