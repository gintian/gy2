<%@page import="com.hrms.struts.constant.WebConstant"%>
<%@page import="java.util.Date"%>
<%@page import="com.hrms.struts.valueobject.UserView"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="com.hjsj.hrms.businessobject.train.MediaServerParamBo,com.hrms.struts.constant.SystemConfig,com.hrms.hjsj.sys.FieldItem"%>

<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%
UserView userView=(UserView)session.getAttribute(WebConstant.userView);
if(userView != null){
	userView.getHm().put("fckeditorAccessTime", new Date().getTime());
}
%>
<style>
<!--
.textbox{border:solid 1px #98C2E8;}
.textboxMul{border:solid 1px #98C2E8;}
-->
</style>
<%

	String filepath = request.getSession().getServletContext().getRealPath("/");
	if(SystemConfig.getPropertyValue("webserver").equals("weblogic")) {
		filepath = session.getServletContext().getResource("/").getPath();//.substring(0);
	   	if(filepath.indexOf(':') != -1) {
		   filepath = filepath.substring(1);   
		} else {
			filepath=filepath.substring(0);      
		}
	}
	filepath = filepath.replace("\\", "``");
%>
<script language="JavaScript" src="/js/validateDate.js"></script>
<script type="text/javascript" src="/fckeditor/fckeditor.js"></script>
<script language="javascript" src="/ajax/constant.js"></script>
<script language="javascript" src="/ajax/basic.js"></script>
<script language="javascript" src="/ajax/common.js"></script>
<script language="javascript" src="/ajax/command.js"></script>
<script language="javascript" src="/js/constant.js"></script>
<script language="javascript" src="/js/common.js"></script>
<link rel="stylesheet" href="/css/xtree.css" type="text/css">
<script type="text/javascript" src="/general/swfupload/js/swfupload_single.js"></script>
<script type="text/javascript" src="/general/swfupload/js/swfupload.queue_single.js"></script>
<script type="text/javascript" src="/general/swfupload/js/fileprogress_single.js"></script>
<script type="text/javascript" src="/general/swfupload/js/handlers_single.js"></script>
<script type="text/javascript"
	src="/train/resource/course/courseTrain.js"></script>
<script language="JavaScript">
	var countNum = 1;
	function closedView(flag){
		var aa = eval("tableEdit");
		var bb= eval("table");
		var oEditor;
		oEditor = FCKeditorAPI.GetInstance('FCKeditor1');
        if (oEditor.EditorDocument == null) {
        	alert("编辑器正在加载中！请重新点击编辑概要按钮！");
    	    return;
    	}
        aa.style.display = 'none';
        bb.style.display = '';
	    oEditor.GetXHTML(true);
	}
	function showView(flg,id) {
		var aa = eval("tableEdit");
		var bb= eval("table");
		var oEditor;	
		var el=document.getElementById(id);	
		if (flg == true) {
	    	oEditor = FCKeditorAPI.GetInstance('FCKeditor1'); 
    	    if (oEditor.EditorDocument == null) {
	        	alert("编辑器正在加载中！请重新点击编辑概要按钮！");
	    	    return;
	    	}
	    	document.getElementsByName("tempname")[0].value=el.name;
    	    aa.style.display = '';
	        bb.style.display = "none"; 
    	    oEditor.EditorDocument.body.innerHTML = el.value; 
	    } else {
	        oEditor = FCKeditorAPI.GetInstance('FCKeditor1');
	        if (oEditor.EditorDocument == null) {
	        	alert("编辑器正在加载中！请重新点击编辑概要按钮！");
	    	    return;
	    	}
	    	var tempname=document.getElementsByName("tempname")[0].value;
	        aa.style.display = 'none';
	        bb.style.display = '';
 	        document.getElementsByName(tempname)[0].value = oEditor.GetXHTML(true);
 	        document.getElementById("div6").innerHTML = oEditor.GetXHTML(true);
	    }
	    
	}
	
    function IsDigit() 
    { 
    return ((event.keyCode != 96)); 
    }      
    function namelen()
    {
      var obj=document.getElementById("setname");
      var len=obj.value.length;
      alert(len);
      if(len>=50)
      {
        alert("文件名称字数不能超过50！");
        return false;
      }else
      {
        return true;
      }
    }
   
    
    function delete_file(ext,fileid)
    {
    	var  text;
    	if(ext=="ext")
    	{
    		text = "确定要删除文件吗？";
    	}
    	else if (ext=="orgext")
    	{
    		text = "确定要删除原件吗？";
    	}
    	if(confirm(text))
		{
			var hashvo=new ParameterSet();
			hashvo.setValue("ext",ext);
			hashvo.setValue("file_id",fileid);
			var request=new Request({method:'post',asynchronous:false,onSuccess:delete_ok,functionId:'10400201047'},hashvo);
		}
    }
    function delete_ok(outparamters)
	{
		var mess = outparamters.getValue("mess");
		if(mess=="ok"){
			coursewareForm.action = parent.frames["mil_menu"].Global.selectedItem.action;
			coursewareForm.submit();
		}
    }
   
    function change(){
    	var selectr5105 = document.getElementById("courseType").value;
		var _tr5 = document.getElementById("trnameswf");
		var _tr6 = document.getElementById("trnamer5115");
		var _tr7 = document.getElementById("trnamer5103");
		if(selectr5105==2){
			_tr5.style.display = 'none';
			_tr6.style.display = '';
			_tr7.style.display = '';
			//document.getElementById("editr5115").value=document.getElementById("tempedit").value;
		}else{
			//document.getElementById("tempedit").value = document.getElementById("editr5115").value;
			//document.getElementById("editr5115").value="";
			_tr6.style.display = 'none';
			_tr7.style.display = 'none';
			_tr5.style.display = '';
			loadobj(selectr5105,"false");
		}
		var _tr8 = document.getElementById("trnameurl");
		if(_tr8){
			if(selectr5105==6){
				_tr8.style.display = '';
				_tr5.style.display = 'none';
			}else
				_tr8.style.display = 'none';
		}
    }
    function change1(){
    	var selectr5105 = document.getElementById("courseType").value;
		var _tr5 = document.getElementById("trnameswf");
		var _tr6 = document.getElementById("trnamer5115");	
		if(selectr5105==2){
			_tr5.style.display = 'none';
			_tr6.style.display = '';
		}else{
			_tr6.style.display = 'none';
			_tr5.style.display = '';
			loadobj(selectr5105,"false");
		}
		
		var _tr8 = document.getElementById("trnameurl");
		if(_tr8){
			if(selectr5105==6){
				_tr8.style.display = '';
				_tr5.style.display = 'none';
			}else
				_tr8.style.display = 'none';
		}
    }
function savecourseware0(){
	/**if(document.getElementById("path_name").value!=''&&document.getElementById("selectr5105").value!=2&&document.getElementById("divupload_progress_div").innerHTML.indexOf('上传成功')==-1){
		alert("请成功上传课件后再提交!");
		return;
	}**/
	 var name = document.getElementById("r5103name").value;
  if(name==""){
    alert("请输入课程名称！");
    return;
  }

  var courseType = document.getElementById("courseType").value; //课程分类
  if(courseType == "2"){   //如果课程分类为文本课件  
	  var textName = document.getElementById("textName").value;
	  if(textName == null || textName == ""){
		  document.getElementById("textName").value=name;
		  
	  }
	  
		document.getElementById("wait").style.display="block";
		document.getElementById("r5103name").disabled=false;
		document.getElementById("meidasave").disabled=true;
		document.getElementById("returnbutton").disabled=true;
			//swfu.startUpload();
  		savecourseware1();
  	  
  	 }else{
  	 		document.getElementById("wait").style.display="block";
			document.getElementById("r5103name").disabled=false;
			document.getElementById("meidasave").disabled=true;
			document.getElementById("returnbutton").disabled=true;
			//swfu.startUpload();
  			savecourseware1();
  	 }
}
//验证非法字符
function checkComments(){
//33-47 ascii码对应!"#$%&"()*+'-./     58-64 ascii码对应 ：；<=>?@      91-96 ascii码对应[\]^_`      123-126 ascii码对应{|}~
if (( event.keyCode > 32 && event.keyCode < 48) || 
	( event.keyCode > 57 && event.keyCode < 65) || 
	( event.keyCode > 90 && event.keyCode < 97) || 
	( event.keyCode > 122 && event.keyCode < 127)) { 
		event.returnValue = false; 
	}
 	var courseName = document.getElementById("r5103name").value;
 	courseName=replace_code(courseName);
 	document.getElementById("r5103name").value=courseName;
} 
	// 处理html乱码
	function T(instr){
		var divObj = document.createElement("div");
    	divObj.innerHTML = getDecodeStr(instr);
		var outstr = divObj.innerText;
		return outstr;
	}
	
	// 保存文本到页面
	function saveText() {
		var edit = FCKeditorAPI.GetInstance('text'); 
    	if (edit.EditorDocument == null) {
	        alert("编辑器正在加载中！请重新点击编辑概要按钮！");
	    	return;
	    }
	   	
	   	// 获得列表
	   	var ulObj = document.getElementById("ware");
	   	var liObj = document.createElement("li");
	   	var courseType = document.getElementById("courseType");
	   	liObj.id = "li_" + courseType.value +"_" + countNum;
	   	countNum ++;
	   	liObj.innerHTML += "<span style='width:100px;height:20px;overflow:hidden;'>" + edit.EditorDocument.body.innerText + "</span>";
	   	
	   	ulObj.appendChild(liObj);
	   	oEditor.EditorDocument.body.innerText;
	   	
	   	
		edit.SetHTML("",true);
 	        document.getElementsByName(tempname)[0].value = edit.GetXHTML(true);
 	        document.getElementById("div6").innerHTML = edit.GetXHTML(true);
	}

	function ly(){
		document.getElementById("r5103name").disabled=true;
	}
</script>
<body onLoad="change1();">
<html:form action="/train/resource/myupload/myuploadcourse"
	enctype="multipart/form-data">
	<center>
	<html:hidden name="myUploadCourseForm" property="diyType" styleId="classCode"/>
	<html:hidden name="myUploadCourseForm" property="lessonId" />
	<input type="hidden" name="newPath" id="newPathId" />
	<input type="hidden" name="fileName" value="" />
	<input type="hidden" name="filepath" value="<%=filepath%>" />
	<input type="hidden" name="isResult" value = "true"/>
	<!--<input type="text" name="isUploadValue" id="isUploadValue" value="" />-->
	<table id="table" width="750" border="0" cellpadding="0"
		cellspacing="0" align="center" class="ListTableF" style="margin-top: 10px;line-height: 30px;">
		<tr>
			<td align="left" class="TableRow" colspan="3" style="height: 30px;">&nbsp;<bean:message key="train.resource.course.myupload.courseupload"/>&nbsp;</td>     
		</tr>
		<tr>
			<td align="right" class="RecordRow" style="height: 30px;">&nbsp;<bean:message key="train.resource.course.myupload.coursename"/>&nbsp;</td>
			<td align="left" class="RecordRow" style="height: 30px;border-right-width: 0xp;">
            	&nbsp;<html:text name="myUploadCourseForm" property="uploadCourseName" 
            	styleClass="textColorWrite" style="width:99%" 
            	styleId="r5103name" onkeypress="checkComments();"
            	onkeyup="checkComments();buttonDisabled();"></html:text>
			</td> 
			<td align="left" class="RecordRow" valign="top" style="border-left-width: 0xp;" width="5%">
				&nbsp;
			</td>     
		</tr>
		<tr>
			<td align="right" class="RecordRow" style="height: 30px;">&nbsp;<bean:message key="train.resource.course.myupload.coursedesc"/>&nbsp;</td>
			<td align="left" class="RecordRow" style="height: 30px;border-right-width: 0xp;" >
				&nbsp;<textarea rows="6" cols="90" id="courseDesc" name="courseDesc"><bean:write name="myUploadCourseForm" property="uploadCourseDesc"/></textarea>
			</td> 
			<td align="left" class="RecordRow" valign="top" style="border-left-width: 0xp;" width="5%">
				&nbsp;
			</td>     
		</tr>
		<!-- 
		<tr>
			<td align="right" class="RecordRow" width="15%">&nbsp;<bean:message key="train.resource.course.myupload.courseware"/>&nbsp;</td>
			<td align="left" class="RecordRow" style="border-right-width: 0xp;" width="80%">
				<div style="border: solid 0px #C4D8EE; height: 100px;width:99%;margin:2px 0px 2px 5px;overflow: auto;">
						<ul id="ware" style="margin:0px;padding:0px;">
						
						</ul>
				<div>
				
			</td> 
			<td align="left" class="RecordRow" valign="top" style="border-left-width: 0xp;" width="5%">
				&nbsp;
			</td>    
		</tr>
		 -->
		<tr>
			<td align="right" class="RecordRow" style="height: 30px;">&nbsp;<bean:message key="train.resource.course.myupload.coursewaretype"/>&nbsp;</td>
			<td class="RecordRow" colspan="2">
					<hrms:optioncollection name="myUploadCourseForm" property="courseTypeList" collection="list" />
		         	&nbsp;<html:select name="myUploadCourseForm" property="courseType" size="1" onchange="change()" styleId="courseType">
		         		<html:options collection="list" property="dataValue" labelProperty="dataName"/>
		         		
		   		 	</html:select>
				
			</td>
		</tr>
		<tr id="trnameswf">
			<td align="right" class="RecordRow" style="height: 30px;">&nbsp;<bean:message key="train.resource.course.myupload.uploadcourseware"/>&nbsp;</td>
			<td class="RecordRow" colspan="2">
				<table>
					<tr>
						<td>
				&nbsp;<span id="divupload" style="width: 410px;vertical-align: middle;"></span>&nbsp;
				<input type="hidden" name="path_old" id="path_old" >							
						</td>
					</tr>
					<tr><td>&nbsp;</td></tr>
				</table>			
			</td>
			
		</tr>	
		
		<tr id="trnamer5115">
			<td align="right" class="RecordRow">&nbsp;<bean:message key="train.resource.course.myupload.uploadcoursewaretext"/>&nbsp;</td>
			<td align="left" class="RecordRow" valign="top" style="border-right-width: 0xp;">
				&nbsp;<textarea rows="2" cols="3" id="text" name="text"><bean:write name="myUploadCourseForm" property="text"/></textarea>
				<script type="text/javascript">
					var questionHead = new FCKeditor('text');//传入参数为表单元素（由FCKeditor生成的input或textarea）的name
       				questionHead.BasePath='/fckeditor/';//指定FCKeditor根路径，也就是fckeditor.js所在的路径
       				questionHead.Height='150';
       				questionHead.Width='99%';
       				questionHead.ToolbarSet='question';//指定工具栏
       				questionHead.Value="";//默认值
       				//questionHead.Create();
       				questionHead.ReplaceTextarea();
       			</script>
				
			</td>
			  <td align="left" class="RecordRow" valign="middle" style="border-left-width: 0xp;">
				&nbsp;<!--<img alt="<bean:message key="button.save" />" src="/images/save_edit.gif" onclick="javascript:saveText();"/>&nbsp;
			-->
			</td>	
		</tr>
		<tr id="trnamer5103">
			<td align="right" class="RecordRow">&nbsp;课件名称&nbsp;</td>
			<td align="left" colspan="2" class="RecordRow"  style="height: 30px;">&nbsp;<input name="textName" class="textColorWrite" style="width:93.5%"></td>
		</tr>		
	
		<tr id="trnameurl">
			<td class="RecordRow" align="right" valign="bottom">
				&nbsp;<bean:message key="lable.channel_detail.out_url" />&nbsp;
			</td>
			<td class="RecordRow" style="padding-left:3px;">
				&nbsp;<html:text styleClass="text6" name="myUploadCourseForm" property="url" style="width: 365px;"></html:text>				
			</td>
		</tr>
		<tr>
			<td align="center" colspan="2" style="padding-top: 5px;padding-bottom: 5px;">
				<input type="button" class="mybutton"
					value="<bean:message key="button.save" />"
					onclick="savecourseware0();" id="meidasave"/>
				&nbsp;&nbsp;<html:submit property="b_return" styleClass="mybutton" styleId="returnbutton"><bean:message key="button.return"/></html:submit>
				
			</td>
		</tr>
	</table>

	<div id="tableEdit" style="display:none;margin-top: 5px;" align="center">
		<script type="text/javascript">
              //<!--
              var oFCKeditor = new FCKeditor('FCKeditor1') ;
              oFCKeditor.BasePath	= '/fckeditor/';
              oFCKeditor.Height	= 480 ;
              oFCKeditor.Width=750;
			  oFCKeditor.ToolbarSet='My1';
              oFCKeditor.Create() ;
              //-->
            </script>
		<INPUT type="button" value="<bean:message key="button.ok" />"
			Class="mybutton" style="margin-top: 5px;" onclick="showView(false)">
		<INPUT type="button" value="<bean:message key="button.return" />"
			Class="mybutton" style="margin-top: 5px;" onclick="closedView(false)" id="">
	</div>
	</center>
</html:form>
<div id='wait' style='position:absolute;top:180;left:300;display: none;'>
<table border="1" width="430" cellspacing="0" cellpadding="4" class="table_style" height="150" align="center">
           <tr>

             <td class="td_style" height="40">正在保存课程...</td>

           </tr>
           <tr>
             <td style="font-size:12px;line-height:200%;" class="complex_border_color" align=center>
               <marquee class="marquee_style" direction="right" width="430" scrollamount="7" scrolldelay="10">
                 <table cellspacing="1" cellpadding="0">
                   <tr height=8>
                     <td bgcolor=#3399FF width=8></td>
                         <td></td>
                         <td bgcolor=#3399FF width=8></td>
                         <td></td>
                         <td bgcolor=#3399FF width=8></td>
                         <td></td>
                         <td bgcolor=#3399FF width=8></td>
                         <td></td>
                    </tr>
                  </table>
               </marquee>
             </td>
          </tr>
</table>
</div>
<script>

function loadobj(type,isUpload){
	var obj = new Object();
    if(document.getElementById("courseType").value ==2){
    	document.getElementById("trnamer5103").style.display = '';
    }else{
    	document.getElementById("trnamer5103").style.display = 'none';
    }

	
	// 显示文件名的输入框的样式默认为class='textColorRead'定义的样式
  	obj.inputStyle = "width:300px;";
  	// “选择文件”按钮的样式，默认样式为class='mybutton'定义的样式
  	obj.buttonStyle = "";
  	// 单个文件的最大值，默认为500,
  	<%String fileSize = MediaServerParamBo.getFileSize();
  	if (fileSize == null || fileSize.length() <= 0) {%>
  		obj.fileMaxSize = "500";
  	<%} else {%>
  		obj.fileMaxSize = "<%=fileSize%>";
  	<%}%>
  	//单个上传文件的单位，默认为MB,还可以使用GB、KB
  	obj.fileSizeUnit = "MB";
  	// 文件的扩展名，限定上传文件的类型,默认是任意类型（*.*）,多个文件类型用分号隔开，例如*.jpg;*.jpeg;*.gif
  	if(type==3){
	  	<%String mediaServerType = MediaServerParamBo.getMediaServerType();
	  	
	  	if ("red5".equalsIgnoreCase(mediaServerType)) {%>
	  		obj.fileExt = "*.mp3;*.mp4;*.flv;*.f4v";
	  	<%} else if ("microsoft".equalsIgnoreCase(mediaServerType)) {%>
	  		obj.fileExt = "*.asf;*.wma;*.wmv";
	  	<%} else {%>
	  		obj.fileExt = "*.mp3;*.flv;*.f4v;*.mp4";
	  	<%}%>
	  	
	  	// 文件类型的描述，默认为“文件类型”
		obj.file_types_desc = "多媒体文件";
  	}else if (type == 1){
  		obj.fileExt = "*.doc;*.docx;*.xls;*.xlsx;*.pdf;*.ppt;*.pptx;*.zip";
  		
  		// 文件类型的描述，默认为“文件类型”
		obj.file_types_desc = "普通文件";
  	} else if (type == 4) {
  		obj.fileExt = "*.zip";
  		
  		// 文件类型的描述，默认为“文件类型”
		obj.file_types_desc = "SCORM标准课件";
  	}
	obj.post_params = {
		"acode" : "${myUploadCourseForm.diyType}", //此处直接定义死了"01" 原本是${myUploadCourseForm.diyType}
		// 61代表上传培训的多媒体文件
		"keyCode" : "61",
		"fileName" :document.getElementById("r5103name").value,
		"fileType" : type//,
		//"diy":"true"
	};
	
	// 最多上传的文件个数，默认为100
	obj.file_upload_limit=1000;
	obj.file_queue_limit = 1;
	// 上传开始后，哪些按钮需要禁用，将按钮的id列出，多个用逗号隔开
	obj.forbiddenButton = "meidasave,returnbutton";
	// 上传时哪些组件的值不能为空，将组件的id列出，多个用逗号隔开
	obj.isNotNullIds = "r5103name";
	obj.isNotNullDesc = "课程名称不能为空！";

    obj.autoUpload = "true";
  	swfuploadsingle("divupload","path","/train/media/upload","61",obj,"true");
}


</script>
</body>
