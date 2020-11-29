<%@page import="com.hrms.struts.constant.WebConstant"%>
<%@page import="com.hrms.struts.valueobject.UserView"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page errorPage="error.jsp"%>
<%@ page import="javax.servlet.ServletException"%>

<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%
	UserView userView = (UserView)session.getAttribute(WebConstant.userView);
	String bosflag = userView.getBosflag();
%>
<SCRIPT LANGUAGE=javascript src="/js/validate.js"></SCRIPT>
<script language="javascript">
function IsDigit() 
{ 
    return ((event.keyCode >= 47) && (event.keyCode <= 57)); 
}
</script>
<style type="text/css">
.RecordRowf {
	border: inset 1px #C4D8EE;
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
	BORDER-LEFT: #C4D8EE 1pt solid; 
	BORDER-RIGHT: medium none; 
	BORDER-TOP: medium none;
	font-size: 12px;
	border-collapse:collapse; 
	height:28px;
}
.RecordRowftitle {
	border: inset 1px #C4D8EE;
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
	BORDER-LEFT: #C4D8EE 1pt solid; 
	BORDER-RIGHT: medium none; 
	BORDER-TOP: medium none;
	font-size: 12px;
	border-collapse:collapse; 
	height:28px;
	font-weight: bold;
	background-color:#f4f7f7;
}
.RecordRowd {
	border: inset 1px #C4D8EE;
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
	BORDER-LEFT: #C4D8EE 1pt solid; 
	BORDER-RIGHT: #C4D8EE 1pt solid; 
	BORDER-TOP: #C4D8EE 1pt solid;
	font-size: 12px;
	border-collapse:collapse; 
	height:28px;
}
.RecordRowr {
	border: inset 1px #C4D8EE;
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
	BORDER-LEFT: #C4D8EE 1pt solid; 
	BORDER-RIGHT: #C4D8EE 1pt solid;
	BORDER-TOP: medium none;
	font-size: 12px;
	border-collapse:collapse; 
	height:28px;
}
.RecordRowrtitle {
	border: inset 1px #C4D8EE;
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
	BORDER-LEFT: #C4D8EE 1pt solid; 
	BORDER-RIGHT: #C4D8EE 1pt solid;
	BORDER-TOP: medium none;
	font-size: 12px;
	border-collapse:collapse; 
	height:28px;
	font-weight: bold;
	background-color:#f4f7f7;
}
</style>
<hrms:themes></hrms:themes>
<script language="javascript">
function savepath(){
	var multimedia_maxsize = document.filepathForm.multimedia_maxsize.value;
	var ck_mm_size = checksize(multimedia_maxsize);
	if(ck_mm_size==false){
		alert("多媒体文件大小格式设置有误，请重新设置后再保存!");
		return;
	}
	var doc_maxsize = document.filepathForm.doc_maxsize.value;
	var ck_dc_size = checksize(doc_maxsize);
	if(ck_dc_size==false){
		alert("文档文件大小格式设置有误，请重新设置后再保存!");
		return;
	}
	var videostreams_maxsize = document.filepathForm.videostreams_maxsize.value;
	var ck_vs_size = checksize(videostreams_maxsize);
	if(ck_vs_size==false){
		alert("培训课件文件大小格式设置有误，请重新设置后再保存!");
		return;
	}
	var asyn_maxsize = document.filepathForm.asyn_maxsize.value;
	var asyn_maxsize = checksize(asyn_maxsize);
	if(asyn_maxsize==false){
		alert("分布集成文件大小格式设置有误，请重新设置后再保存!");
		return;
	}
	var newfpath = document.filepathForm.fileRootPath.value;
	//var oldfpath = "${filepathForm.fileRootPath}";
	var hashvo=new ParameterSet();  
	hashvo.setValue("newfpath", newfpath); 
    var request=new Request({method:'post',onSuccess:checkpath,functionId:'10200700016'},hashvo);
}
function checkpath(outparamters){
	var existspath=outparamters.getValue("existspath");
	var changepath=outparamters.getValue("changepath");
	var oldfpath=outparamters.getValue("oldfpath");
	var newfpath=outparamters.getValue("newfpath");
	if(existspath=="false"){
		alert("文件存放根目录设置有误，请重新设置后再保存！");
		return;
	}
	oldfpath = replaceAll(oldfpath, "*", "\\");
	newfpath = replaceAll(newfpath, "*", "\\");
	if(changepath=="true"){
		if(confirm("确认修改文件存放根目录？目录将从"+oldfpath+"改为"+newfpath+"，请及时将附件文件目录调整到新的目录，否则，附件资源将无法访问。"))
      	{
			filepathForm.action = "/system/options/param/filepath.do?b_save=link";
			filepathForm.submit();
       	}
	}else{
		filepathForm.action = "/system/options/param/filepath.do?b_save=link";
		filepathForm.submit();
	}
}
function checksize(size){
	var last_char = size.substring(size.length-1,size.length);
	var last_second_char = size.substring(size.length-2,size.length-1);
	var last_size =  size.substring(0,size.length-1);
	var last_second_size =  size.substring(0,size.length-2);
	var first_char=size.substring(0,1);
	var checksize;
	if(first_char=="-"){
		return false;
	}
	if("B"==last_char.toUpperCase()){
		if("K"==last_second_char.toUpperCase()||"M"==last_second_char.toUpperCase()||"G"==last_second_char.toUpperCase()){
			checksize = parseFloat(last_second_size);
			if(checksize==last_second_size)
				return true;
			else
				return false;
		}else{
			checksize = parseFloat(last_size);
			if(checksize==last_size)
				return true;
			else
				return false;
		}
	}else if("K"==last_char.toUpperCase()||"M"==last_char.toUpperCase()||"G"==last_char.toUpperCase()){
		checksize = parseFloat(last_size);
		if(checksize==last_size)
			return true;
		else
			return false;
	}else {
	   checksize = parseFloat(size);
       if(checksize==size)
	       return true;
        else
		  return false;
	}
}
/**
*xushuo 16/10/10
*bug checkpath方法同名
**/
function checkchar(input){
	//不允许输入“.”
	 if(event.keyCode===190)
		 event.returnValue = false;
}
</script>
<html:form action="/system/options/param/filepath">
	<table width="100%" align="center" border="0" cellpadding="0"
		cellspacing="0" <%if(!"hcm".equals(bosflag)){ %>style="margin-top:10px;"<%} %>>
		<tr><!-- 系统管理，参数设置，hr页面，内容与顶部之间间隔10px   jingq  add 2014.12.23 -->
			<td valign="top" align="left">
				<fieldset align="center" style="width: 60%;">
					<legend>
						<bean:message key="train.setparam.mediaserver.filepath" />
					</legend>
					<table border="0" cellspacing="2" align="center" cellpadding="0">
						<tr>
							<td colspan="2" style="height: 35px" align="left">
								<bean:message key="sys.options.param.explain" />
							</td>
						</tr>
						<tr>
							<td  style="height: 16px" align="left" colspan="2"><bean:message key="sys.options.param.rootfilepath" /></td>
						</tr>
						<tr style="height: 26px" valign="top">
							<td align="left" valign="top" colspan="2"><!-- 【7923】系统管理/参数设置/系统参数：文件存放目录，linux环境时应该是/的斜杠，不应该是\斜杠，建议规则跟操作系统统一。  jingq upd 2015.03.10 -->
								<html:text name="filepathForm" onkeydown="checkchar(this);" property="fileRootPath" size="70" style="width:500px;" styleClass="text4"/>
							</td>
						</tr>
						<tr>
							<td colspan="2">
								<table width="100%" align="center" border="0" cellpadding="0"
									cellspacing="0">
									<tr>
										<td align="left" class="RecordRowd common_border_color" style="padding-left:5px;" nowrap colspan="3">
											<bean:message key="sys.options.param.controlssize" />
										</td>
									</tr>
									<tr>
										<td align="center" class="RecordRowftitle common_border_color common_background_color" nowrap>
											<bean:message key="sys.options.param.resourcetype" />
										</td>
										<td align="center" class="RecordRowftitle common_border_color common_background_color" nowrap>
											<bean:message key="sys.options.param.resourcename" />
										</td>
										<td align="center" class="RecordRowrtitle common_border_color common_background_color" nowrap>
											<bean:message key="sys.options.param.filesize" />
										</td>
									</tr>
									<tr>
										<td align="center" class="RecordRowf common_border_color" nowrap>
											<bean:message key="sys.options.param.media" />
										</td>
										<td align="center" class="RecordRowf common_border_color" nowrap>multimedia</td>
										<td align="center" class="RecordRowr common_border_color" nowrap>
											<html:text name="filepathForm" property="multimedia_maxsize" size="20" styleClass="text4" style="text-align:center;width:200px;"/>
										</td>
									</tr>
									<tr>
										<td align="center" class="RecordRowf common_border_color" nowrap>
											<bean:message key="sys.options.param.document" />
										</td>
										<td align="center" class="RecordRowf common_border_color" nowrap>doc</td>
										<td align="center" class="RecordRowr common_border_color" nowrap>
											<html:text name="filepathForm" property="doc_maxsize" size="20" styleClass="text4" style="text-align:center;width:200px;"/>
										</td>
									</tr>
									<tr>
										<td align="center" class="RecordRowf common_border_color" nowrap>
											<bean:message key="sys.options.param.trainfile" />
										</td>
										<td align="center" class="RecordRowf common_border_color" nowrap>videostreams</td>
										<td align="center" class="RecordRowr common_border_color" nowrap>
											<html:text name="filepathForm" property="videostreams_maxsize" size="20" styleClass="text4" style="text-align:center;width:200px;"/>
										</td>
									</tr>
									<tr>
										<td align="center" class="RecordRowf common_border_color" nowrap>
											<bean:message key="sys.options.param.asynfile" />
										</td>
										<td align="center" class="RecordRowf common_border_color" nowrap>asyn</td>
										<td align="center" class="RecordRowr common_border_color" nowrap>
											<html:text name="filepathForm" property="asyn_maxsize" size="20" styleClass="text4" style="text-align:center;width:200px;"/>
										</td>
									</tr>
								</table>
							</td>
						</tr>
						<tr>
							<td align="center" colspan="2" style="height: 35px">
								<html:button styleClass="mybutton" property="b_save"
									onclick="savepath()">
									<bean:message key="button.save" />
								</html:button>
							</td>
						</tr>
					</table>
				</fieldset>
			</td>
		</tr>
	</table>
</html:form>
<script>
	if(!getBrowseVersion() || getBrowseVersion()==10){// 非ie浏览器样式修改  wangb 20190319
		var fieldset = document.getElementsByTagName('fieldset')[0];
		fieldset.setAttribute('align','left');
		fieldset.style.margin = '0 auto';
	}
	
</script>