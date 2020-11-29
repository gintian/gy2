<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<link rel="stylesheet" href="/css/css0.css" type="text/css">
<script type="text/javascript" src="/general/sys/hjaxmanage.js"></script>
<script language="JavaScript">
function resizeImage(){
	var imagearr = document.getElementsByTagName("img") ;
	var clientWidth = document.body.clientWidth; 
	var clientHeight = document.body.clientHeight; 
	for(var i=0;i<imagearr.length;i++){
		imagearr[i].width = clientWidth;
		imagearr[i].height = clientHeight;
    }
}
function actualSize(){
	parent.document.all('ole').style.height="${mInformForm.photo_h}";
    parent.document.all('ole').style.width="${mInformForm.photo_w}";
    resizeImage();
    showoff();
}
function maxSize(){
	var photo_w = parent.document.all('ole').offsetWidth;
    var photo_h = parent.document.all('ole').offsetHeight; 
	
	photo_w=parseInt(photo_w)+25*(85/120);
	photo_h=parseInt(photo_h)+25;
	if(photo_h<600){		
		parent.document.all('ole').style.height=parseInt(photo_h);
    	parent.document.all('ole').style.width=parseInt(photo_w);
    	resizeImage();
    }
    showoff();
}
function minSize(){
	var photo_w = parent.document.all('ole').offsetWidth;
    var photo_h = parent.document.all('ole').offsetHeight; 
	
	photo_w=parseInt(photo_w)-25*(85/120);
	photo_h=parseInt(photo_h)-25;
	if(photo_w>85){	
		parent.document.all('ole').style.height=parseInt(photo_h);
    	parent.document.all('ole').style.width=parseInt(photo_w);
    	resizeImage();
    }
    showoff()
}
function upload(obj){
    var fileEx = mInformForm.picturefile.value;
    if(fileEx == ""){
        alert(SELECT_PIC_FIELD+"!");
        return ;
    }
    var fileEx1=fileEx.substring(fileEx.lastIndexOf(".")+1).toLowerCase();
    if(!AxManager.setup(null, 'FileView', 0, 0))
    	return false;
    var obj=document.getElementById('FileView'); 
    if (obj != null){
        var facSize=obj.GetFileSize(fileEx);                
        var photo_maxsize="${mInformForm.photo_maxsize}";
        if(parseInt(photo_maxsize,10)>0&&parseInt(photo_maxsize,10)<parseInt(facSize,10)/1024){  
            alert("上传文件大小超过管理员定义大小，请修正！上传文件上限"+photo_maxsize+"KB");
            return false;
        }     
    }
    if(fileEx1=="jpg" || fileEx1=="bmp" || fileEx1=="jpeg"||fileEx1=="png"){
        document.getElementById("type").value = "upload";
	 	document.getElementById("a0100").value = "${mInformForm.a0100}";
	    document.mInformForm.action="/general/inform/emp/view/displaypicture.do?b_save=link&flag=${param.flag}";
		document.mInformForm.submit(); 
     }else{
        alert(SELECT_PIC_FIELD+"!");
        return;
    }
  
}
function bclear(){		
        document.getElementById("type").value = "clear";
 		document.getElementById("a0100").value = "${mInformForm.a0100}";
        document.mInformForm.action="/general/inform/emp/view/displaypicture.do?b_save=link";
		document.mInformForm.submit(); 
}
function setPhoto1(){
	//document.getElementById("picturefile").click();
	window.parent.document.getElementById("test").click();
}
</script>
<style type="text/css"> 
.viewPhoto{
     position:absolute;
     top:-10px;
     margin-left:-5px;
     overflow:visible;
}
</style>
<form name="mInformForm" method="post" action="/general/inform/emp/view/displaypicture.do" enctype="multipart/form-data" >
<div class="viewPhoto">
<table width="100%" align="center" border="0" cellspacing="0" cellpadding="0"  oncontextmenu=return(onloadMenu())>
		<tr>
			<td align="center" valign="top" id="picture" onclick="showoff();">
				<hrms:ole name="mInformForm" dbpre="mInformForm.dbname"  a0100="a0100" scope="session" height="120" width="85"/>
			</td>
		</tr>
</table>
</div>
	<div style="display:none">
		<html:file name="mInformForm" property="picturefile" onchange="upload(this);" styleClass="text6" size="6"/>
	</div>
	<html:hidden name="mInformForm" property="a0100"/>
	<html:hidden name="mInformForm" property="type"/>
	<html:hidden name="mInformForm" property="photo_w"/>
	<html:hidden name="mInformForm" property="photo_h"/>
	<div id="mlay" style="position:absolute;display:none;">
		<table width="70" cellpadding="0"  cellspacing="0" border="0">
			<hrms:priv func_id="260641501">  
			<tr height="15" onclick="bclear();" onMouseover="mover(this);" onMouseout="mout(this);">
				<td>&nbsp;&nbsp;<bean:message key='workdiary.message.clear'/></td>
			</tr>
			<tr height="15">
				<td><hr></td>
			</tr>
			</hrms:priv>
			<tr height="15" onclick="maxSize();" onMouseover="mover(this);" onMouseout="mout(this);">
				<td>&nbsp;&nbsp;<bean:message key='workdiary.message.enlarge'/></td>
			</tr>
			<tr height="15" onclick="minSize();" onMouseover="mover(this);" onMouseout="mout(this);">
				<td>&nbsp;&nbsp;<bean:message key='workdiary.message.narrow'/></td>
			</tr>
			<tr height="15" onclick="actualSize();" onMouseover="mover(this);" onMouseout="mout(this);">
				<td>&nbsp;&nbsp;<bean:message key='workdiary.message.actual.size'/></td>
			</tr>
		</table>
	</div>
</form>
<script language="JavaScript">
//菜单没有选中的背景色和文字色 
var bgc="#eee",txc="black";
//菜单选中的选项背景色和文字色
var cbgc="#FFF8D2",ctxc="black";

function mover(obj){
	obj.style.background=cbgc;
	obj.style.color=ctxc ;
}
function mout(obj){
	obj.style.background=bgc;
	obj.style.color=txc ;
}
function showoff() { 
	mlay.style.display="none"; 
} 
function onloadMenu(){
	mlay.style.display=""; 
	mlay.style.pixelTop=event.clientY; 
	mlay.style.pixelLeft=event.clientX; 
	mlay.style.background=bgc; 
	mlay.style.color=txc; 
 	return false;
}
function init(){
	var imagearr =  document.getElementsByTagName("img") ;
	var clientWidth = document.body.clientWidth; 
	var clientHeight = document.body.clientHeight;
	document.getElementById("picture").width=clientWidth;
	for(var i=0;i<imagearr.length;i++){
		imagearr[i].width = clientWidth;
		imagearr[i].height = clientHeight;
    }
}
init();
</script>
