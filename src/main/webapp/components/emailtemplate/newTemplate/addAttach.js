var hight=0;//插入附件窗口大小
var template_id="";
/**
 * 调用文件上传插件后毁掉函数
 * @param list   插件返回的结果   list:数组，数组中是object对象，格式为：[{id:'',filename:'',path:'', localname:''},{id:'',filename:'',path:'',localname:''}]
 * @return
 */
function uploadSuccess(list) {
	var file_list = new Array();
	Ext.each(list, function(obj, index) {
		file_list.push({
			"fileid" : !!obj.fileid ? obj.fileid : obj.path,
			"filename" : obj.filename,
			"localname" : obj.localname
		});
	});
	var map = new HashMap();
	map.put("template_id", template_id);
	map.put("file_list", file_list);
	Rpc({
		functionId : 'ZP0000002348',
		async : false,
		success : function judSuccess(response) {
			var result = Ext.decode(response.responseText);
		}
	}, map);
}

 
//上传附件
		function upload(){
			var templateId = Ext.getCmp('tempalteId').getValue();
			template_id = templateId;
			var map = new HashMap();
   			map.put("templateId",templateId);
			Rpc( {
				functionId : 'ZP0000002349',
				success : Global.searchZphj
			}, map);
		}
		Global.searchZphj = function(response){
			var value = response.responseText;
			var map = Ext.decode(value);
			var list = map.attachList;
			var rootPath = map.rootPath;
			if(!map.succeed)//异常
				return;
			if(Ext.isEmpty(rootPath)){//未设置文件上传路径
				Ext.Msg.alert("提示信息","未设置文件存放根目录,请到<br/>系统管理参数设置--系统参数中进行设置.");
				return;
			}
			Ext.create("SYSF.FileUpLoad",{
				renderTo:Ext.getBody(),
//					renderTo:'addattach',
				emptyText:"请输入文件路径或选择文件",
				upLoadType:2,
				isDownload:true,
				fileList:list,
				fileExt:"*.doc;*.docx;*.xlsx;*.xls;*.rar;*.zip;*.ppt;*.jpg;*.jpeg;*.png;*.bmp;*.txt;*.wps;*.pptx;*.pdf;",
				buttonText:'上传',
				success:uploadSuccess,
				fileSizeLimit:'20MB',
				isDelete:true,
				isTempFile:false,
	            VfsModules:VfsModulesEnum.ZP,
	            VfsFiletype:VfsFiletypeEnum.other,
	            VfsCategory:VfsCategoryEnum.other
			});    	
		}
		
//上传附件保存
  Global.uploadAttach=function(type){
  	//var file = Ext.getDom('uploadFile').value;
	var filepath = Ext.getDom('uploadFile');
	/*if(!checkFileSize(filepath)){//判断文件大小
		return;
	}*/
	//var file = document.getElementById('uploadFile').value;
	var file = Ext.getDom('uploadFile').value;
	/**
	var array=['doc','docx','xls','xlsx','ppt','pdf','txt','jpg','png','jpeg','gif','bmp'];
	var arr=file.split(".");
	if(array.indexOf(arr[1])<0){
		Ext.MessageBox.alert("提示信息","选择的文件不合法");
		return;
	}
	*/
    if(file==null||trim(file).length==0)
	  {
	   Ext.MessageBox.alert("提示信息","请选择文件后再上传！");
	     return;
	  }
	   Ext.Ajax.request({ 
          url : "/recruitment/emailtemplate/emailTemplateList.do?b_insert=link",
          isUpload : true, 
          form:'templateAttachForm',
          success : function(data) {
          var info = data.responseText;
          	if(type=='2'){
          		Global.toLoadAttach();
          	}
          	if(type=='1'){
          		Global.closeAttach();
          	}
          }
         });
    	 //附件上传完成后,发表按钮需要显示
  	// templateReForm.action="/recruitment/emailtemplate/emailTemplateList.do?b_insert=link";
  	 //templateReForm.submit();
  	//window.location.href="/recruitment/emailtemplate/emailTemplateList.do?b_add=link&template_id="+Ext.getCmp('tempalteId').getValue()+"";
  }
  Global.toLoadAttach=function(){
		      var uploadBox = '<form id="templateAttachForm" action="" >'
				+'<table   width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" style="border-collapse:collapse;">'
				+'<tr height="20px">'
				+'</tr>'
				+'<tr height="20px">'
				+'<td align="left" nowrap>'
			    +'<a href="#" style="position:relative; overflow:hidden;float:left;" ><img src="/module/recruitment/image/biez.jpg" style="height:12px;width:8px">上传文件'
				+'<input type="file" name="file" size="35"  id="uploadFile" style="position:absolute; right:0; top:0; opacity:0; filter:alpha(opacity=0);cursor:pointer;" onchange="getFile();" />'
				+'</a>'
				+'<input type="text" id="copyFile" style="border:0px;margin-left:5px;margin-top:2px;width:400px;height:30px; " >'
				+'<input type="hidden" name="path" value=""/>'
				+'</td>'
				+'</tr>'
				+'</table></form>';
				Ext.getCmp('uploadForm').update(uploadBox);
          		var templateId = Ext.getCmp('tempalteId').getValue();
          		var map = new HashMap();
			    map.put("templateId",templateId);
				Rpc({
					functionId : 'ZP0000002349',
					success : Global.deleteAttachOK
				}, map);
  }
  function deleteAttach(attachId,templateId){
 	 Ext.Msg.confirm("提示信息","确认要删除附件吗？",function(btn){ 
		if(btn=="yes"){ 
		// 确认触发，继续执行后续逻辑。 
		var map = new HashMap();
	   map.put("id",attachId);
	   map.put("templateId",templateId);
		Rpc({
			functionId : 'ZP0000002350',
			success : Global.deleteAttachOK
		}, map);
		}
	});
  }
   Global.deleteAttachOK=function(response){
  			var value = response.responseText;
			var map = Ext.decode(value);
			var list = map.attachList;
			var i=0;
			var html = '<form action="" id="attachTable"  >'
			+'<table width="100%" border="1px" cellspacing="0"  align="center" cellpadding="0" style="border-collapse:collapse;margin-top:-1;">'
			+'<tr height="25px" >'
			+'<th style="display:none" id="attachId">id</th>'
			+'<th style="display:none" id="templateId">templateId</th>'
			+'<th align="center" style="width:180px;border:1px solid  #c5c5c5;font-wight:bold">附&nbsp;&nbsp;&nbsp;件</th>'
			+'<th align="center" style="width:80px;border:1px solid  #c5c5c5;font-wight:bold">大&nbsp;&nbsp;&nbsp;小</th>'
			+'<th align="center" style="width:20px;border:1px solid  #c5c5c5;font-wight:bold">删除</th>'
			+'</tr>';
			for(i=0;i<list.length;i++){
				html=html+'<tr  height="25px" >'
				+'<td style="display:none">'
				+list[i]["id"]
				+'</td>'
				+'<td style="display:none">'
				+list[i]["templateId"]
				+'</td>'
				+'<td align="center" style="width:180px;border:1px solid  #c5c5c5;">'
				+list[i]["fileName"]
				+'</td>'
				+'<td align="center" style="width:80px;border:1px solid  #c5c5c5;">'
				+list[i]["fileLength"]
				+'</td>';
				if(list[i]["istotal"]=="no"){
					html=html+'<td align="center" style="width:20px;border:1px solid  #c5c5c5;">'
				+'<img src="/module/recruitment/image/cha.jpg" style="width:10px;height:10px" onclick="deleteAttach(\''+list[i]["id"]+'\',\''+list[i]["templateId"]+'\');"/></td>'
				+'</tr>';
				}else{
					html=html+'<td  align="center" style="width:20px;border:1px solid  #c5c5c5;"></td></tr>';
				}
				
			}
			html=html+'</table></form>';
			//根据附件的条数自动调整window大小
			if(i<=8){
				height=400;
			}else{
				height=400+(i-8)*25;
			}
			Ext.getCmp('uploadAttchWin').setHeight(height);
			Ext.getCmp('uploadId').update(html);
  }
  Global.closeAttach=function(){
    Ext.getCmp('uploadForm').destroy();
    Ext.getCmp('uploadId').destroy();
  	Ext.getCmp('uploadAttchWin').destroy();
  }
