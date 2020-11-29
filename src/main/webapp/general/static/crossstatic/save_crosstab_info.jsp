<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="com.hjsj.hrms.actionform.general.statics.SaveCrosstabForm"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="com.hrms.frame.dao.RecordVo"%>
<%@ page import="com.hrms.struts.taglib.CommonData"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>

<link href="/css/css1.css" rel="stylesheet" type="text/css">
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/module/utils/js/template.js"></script>
<%
SaveCrosstabForm saveCrosstabForm = (SaveCrosstabForm) session.getAttribute("saveCrosstabForm");
	ArrayList tempCondList = saveCrosstabForm.getTempCondList();
	String css_url = "/css/css1.css";
	UserView userView = (UserView) session.getAttribute(WebConstant.userView);
	if (userView != null) {
		css_url = userView.getCssurl();
		if (css_url == null || css_url.equals(""))
			css_url = "/css/css1.css";
	}
	String bosflag = "";
	if (userView != null) {
		bosflag = userView.getBosflag();
	}
%>
<script type="text/javascript">
function sformula(){
	var conddiv = document.getElementById("conddiv");
	var theurl="/general/static/commonstatic/iframe_query.jsp?src=/general/deci/statics/savecrosstab.do?b_showdialog=link";
	var dw=400,dh=300,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
	//19/3/18 xus 浏览器兼容： 火狐谷歌不弹窗问题
	var config = {id:'sformula_showModalDialogs',width:dw,height:dh};
	modalDialog.showModalDialogs(theurl,'',config,sformula_callbackfunc);
  	//window.showModalDialog(theurl,conddiv,'dialogLeft:'+dl+'px;dialogTop:'+dt+'px;dialogHeight:300px;dialogWidth:400px;center:yes;scroll:no;help:no;resizable:no;status:no;');
}
//19/3/18 xus 回调函数
function sformula_callbackfunc(returnvalues){
	if(returnvalues&&returnvalues!=null){
		document.getElementById("conddiv").innerHTML = returnvalues;
	}
}
function saveInfo(){
	//得到横纵维度
	var crosswise,lengthways;
	crosswise = '${saveCrosstabForm.crosswise}';
	lengthways = '${saveCrosstabForm.lengthways}';
	var hashvo=new ParameterSet();
	//得到名称
	var crossname = document.getElementById("name").value;
	if(crossname==null||crossname=="")
	{
		alert("统计条件名称不能为空！");
		return false;
	}
	if(IsOverStrLength(crossname, 20))
	{
		alert("统计条件名称长度不能超过10个汉字！");
		return false;
	}
	if(crossname.indexOf("\‘")>-1||crossname.indexOf("\”")>-1||crossname.indexOf("\'")>-1||crossname.indexOf("\"")>-1)
	{	
		alert("统计条件名称不能包含\’或\"或\’或\”");
		return false;
	}
	//得到分类
	var type = document.getElementById("hideType").value;
	if(IsOverStrLength(type, 200))
	{
		alert("分类名称长度不能超过100个汉字！");
		return false;
	}
	if(type.indexOf("\‘")>-1||type.indexOf("\”")>-1||type.indexOf("\'")>-1||type.indexOf("\"")>-1)
	{	
		alert("分类名称不能包含\’或\"或\’或\”");
		return false;
	}
	//得到人员库
	var dbnames = document.getElementsByName("dbname");
    var dbname = "";
    for (i = 0; i < dbnames.length; i++) {
    	var el = dbnames[i];
    	if (el.checked==true) {
    		dbname +=el.value+",";
    	}
    }
    dbname=dbname.substr(0,dbname.length-1);
    //得到分类统计条件
    var o = document.getElementById("condition"); 
	var intvalue=""; 
	for(i=0;i<o.length;i++){   
		intvalue+=o.options[i].value+","; 			
	} 
    intvalue=intvalue.substr(0,intvalue.length-1);
    //得到其他选项
    var hiderow,hidecol,crosswiseTotal,lengthwaysTotal,showChart;
    if(document.getElementById("row").checked==true){
    	hiderow="1";
    }else{
    	hiderow="0";
    }
    if(document.getElementById("column").checked==true){
    	hidecol="1";
    }else{
    	hidecol="0";
    }
    if(document.getElementById("sumh").checked==true){
    	crosswiseTotal="1";
    }else{
    	crosswiseTotal="0";
    }
    if(document.getElementById("sumv").checked==true){
    	lengthwaysTotal="1";
    }else{
    	lengthwaysTotal="0";
    }
    if(document.getElementById("chart").checked==true){
    	showChart="1";
    }else{
    	showChart="0";
    }

    hashvo.setValue("crosswise",crosswise);
    hashvo.setValue("lengthways",lengthways);
    hashvo.setValue("crossname",crossname);
    hashvo.setValue("type",type);
    hashvo.setValue("dbname",dbname);
    hashvo.setValue("condition",intvalue);
    hashvo.setValue("hiderow",hiderow);
    hashvo.setValue("hidecol",hidecol);
    hashvo.setValue("crosswiseTotal",crosswiseTotal);
    hashvo.setValue("lengthwaysTotal",lengthwaysTotal);
    hashvo.setValue("showChart",showChart);
    var request=new Request({method:'post',onSuccess:checkAfter,functionId:'05301010026'},hashvo);
}

function checkAfter(outparamters){
	var infomsg=outparamters.getValue("infomsg");
	if ((infomsg!=null) &&(infomsg!="")){
		alert(infomsg);
		return;
	}else{
		alert("保存成功！");
		closeDialog();
	}
}

function screen(obj,flag)
{
  var targetvalue=document.getElementById(flag); 
  targetvalue.value=obj.value; 
}

function   addDict(obj,event,flag)
{
   var evt = event ? event : (window.event ? window.event : null);
   var np=   evt.keyCode; 
   if(np==38||np==40){ 
   
   } 
   var textv=obj.value;
   var aTag;
   	aTag = obj.offsetParent; 
   if(textv==null||textv=="")
	   return false;
   textv=textv.toLowerCase();  
   var un_vos=document.getElementsByName(flag+"_value");
   if(!un_vos)
		return false;
   var unStrs=un_vos[0].value;	
   var unArrs=unStrs.split(",");
   var   c=0;
   var   rs   =new   Array();
   for(var i=0;i<unArrs.length;i++)
   {
		 var un_str=unArrs[i];
		 if(un_str)
		 {
		     if(un_str.indexOf(textv)!=-1)
	         {
			     rs[c]="<tr id='tv' name='tv'><td id='al"+c+"'  onclick=\"onV("+c+",'"+flag+"')\"  style='height:15;cursor:pointer' onmouseover='alterBg("+c+",0)' onmouseout='alterBg("+c+",1)' nowrap class=tdFontcolor>"+un_str+"</td></tr>"; 
                 c++;
		     }
		 
		 }
        
	}
    resultuser=rs.join("");
    if(textv.length==0){ 
       resultuser=""; 
    } 
    document.getElementById("dict").innerHTML="<table   width='100%' class='div_table'  cellpadding='2' border='0'  bgcolor='#FFFFFF'   cellspacing='2'>"+resultuser+"</table>";
    document.getElementById('dict').style.display = "";
    document.getElementById('dict').style.position="absolute";	
	document.getElementById('dict').style.left=aTag.offsetLeft+"px";
   	document.getElementById('dict').style.top=aTag.offsetTop+20+"px";
}

function onV(j,flag){
   var o = document.getElementById('al'+j).innerHTML; 
   document.getElementById(flag).value=o; 
   document.getElementById(flag+"select").value=o;
   document.getElementById('dict').style.display = "none";
}

function alterBg(j,i){
    var   o   =   document.getElementById('al'+j); 
    if(i==0) 
       o.style.backgroundColor   ="#3366cc"; 
    else   if(i==1) 
       o.style.backgroundColor   ="#FFFFFF"; 
}

function closeDialog(){
	if(getBrowseVersion()){
		window.close();
	}else{
		window.parent.close();
	}
}
</script>
<hrms:themes />
<html:form action="/general/deci/statics/savecrosstab" method="post" style="overflow-y:hidden;">
<table width="380px" align="center" border="0" cellpadding="0" cellspacing="0" class="selectfieldTable" style="margin-left:6px">
	<tr>
		<td valign="top">
			<table width="100%" border="0" cellspacing="0" align="center" cellpadding="0" class="RecordRow" style="">
				<thead>
					<tr>
						<td align="left" class="TableRow" style="" nowrap colspan="3">
							<bean:message key="static.save.crosstab" />
						</td>
					</tr>
				</thead>
				<tr>
					<td height="8px" colspan="3"></td>
				</tr>
				<tr>
					<td align="right" width="120"><bean:message key="static.cross.name" /></td>
					<td width="200">
						<input type="text" name="crossname" id="name" class="complex_border_color" style="width: 200px;">
					</td>
					<td align="left" width="60"></td>
				</tr>
				<tr>
					<td align="right" width="120"><bean:message key="static.cross.type" /></td>
					<td width="200">
								<div style="overflow:hidden;border:none;width:200px;height:24px;" class="complex_border_color">
								<html:select name="saveCrosstabForm" property='type'
									styleId="hideTypeselect"
									style="height:20px;position:absolute;width:200px;margin-top:2px;"
									onchange="screen(this,'hideType');" onfocus=''>
									<option value=""></option>
									<html:optionsCollection property="typeList" value="dataValue" label="dataName" />
								</html:select>
								</div>
								<%if("hcm".equals(bosflag)){ %>
								<div style="position:absolute;z-index:2;top:72px;width:183px;height:22px;overflow:hidden;" class="complex_border_color">
								<input name=type id='hideType' type="text"
									style="position: absolute;border:none; width: 183px; height: 20px;line-height:20px;"
									value='${saveCrosstabForm.type }'
									onkeyup="addDict(document.getElementById('hideType'),event,'hideType');">
								<input type="hidden" name="hideType_value" value='${saveCrosstabForm.hideType }' />
								</div>
								<%} else { %>
								<div style="position:absolute;z-index:2;top:53px;width:183px;height:22px;overflow:hidden;" class="complex_border_color">
								<input name=type id='hideType' type="text"
									style="position: absolute;border:none; width: 183px; height: 25px;line-height:20px;"
									value='${saveCrosstabForm.type }'
									onkeyup="addDict(document.getElementById('hideType'),event,'hideType');">
								<input type="hidden" name="hideType_value" value='${saveCrosstabForm.hideType }' />
								</div>
								<%} %>
					</td>
					<td align="left" width="60"></td>
				</tr>
				<tr>
					<td align="right" width="120" valign="top"><bean:message key="static.cross.dbname" /></td>
					<td align="left" width="200">
						<div style="position: relative; width:220px;">
						<logic:iterate id="element" name="saveCrosstabForm" property="dbnamelist" indexId="index">
							<div style="position: relative; float: left;position: relative; width: 100px;">
								<%
									if (saveCrosstabForm.getDbname().indexOf(((CommonData) element).getDataValue()) != -1) {
								%>
								<input checked="checked" type="checkbox" name="dbname"
									value="<bean:write name="element" property="dataValue"/>"
									id="dbname<bean:write name="index"/>" style="cursor: pointer;" />
								<%
									} else {
								%>
								<input type="checkbox" name="dbname"
									value="<bean:write name="element" property="dataValue"/>"
									id="dbname<bean:write name="index"/>" style="cursor: pointer;" />
								<%
									}
								%>
								<label for="dbname<bean:write name="index"/>"
									style="cursor: pointer;">
									<bean:write name="element" property="dataName" />
								</label>
							</div>
						</logic:iterate>
						<div>
					</td>
					<td align="left" width="60"></td>
				</tr>
				<tr>
					<td align="right" width="120" valign="top"><bean:message key="static.cross.term.type" /></td>
					<td width="200" valign="bottom">
						<span id="conddiv" style="margin: 0px; padding: 0px;"> 
									<select id="condition" name="condition" multiple="multiple"
										style="height: 150px; width: 200px;">
										<%
											if (tempCondList != null) {
														for (int j = 0; j < tempCondList.size(); j++) {
															RecordVo vo = (RecordVo) tempCondList.get(j);
										%>
										<option value="<%=vo.getString("id")%>"
											title="<%=vo.getString("name")%>"><%=vo.getString("name")%></option>
										<%
											}
													}
										%>
									</select>
					 			</span>
					</td>
					<td align="left" width="60" valign="top" style="margin-top: 20px">
						<!--  
						<input type="button" name="btnorgmapset" value='<bean:message key="button.orgmapset" />' onclick="sformula();" class="mybutton">
						-->
						<img src="/images/add.gif" border=0 title='设置分类统计条件' style="cursor:pointer;" onclick="sformula();"/>
					</td>
				</tr>
				<tr>
					<td align="right" width="120"></td>
					<td align="center" width="200">
						<table align="left">
							<tr>
								<td>
									<input type="checkbox" id="row" />
									<bean:message key="static.hide.null.row" />
								</td>
								<td>
									<input type="checkbox" id="column" />
									<bean:message key="static.hide.null.column" />
								</td>
							</tr>
							<tr>
								<td>
									<input type="checkbox" id="sumh" />
									<bean:message key="static.crosswise.total" />
								</td>
								<td>
									<input type="checkbox" id="sumv" />
									<bean:message key="static.lengthways.total" />
								</td>
							</tr>
							<tr>
								<td>
									<input type="checkbox" id="chart" />
									<bean:message key="static.show.chart" />
								</td>
								<td></td>
							</tr>
						</table>
					</td>
					<td align="left" width="60"></td>
				</tr>
			</table>
		</td>
	</tr>
</table>
<table width="100%" align="center">
	<tr>
		<td align="center">
			<input type="button" name="btnsave" value='<bean:message key="button.save" />' onclick="saveInfo()" class="mybutton">
			<input type="button" name="btncancel" value='<bean:message key="button.cancel" />' onclick="closeDialog();" class="mybutton">
		</td>
	</tr>
</table>
</html:form>
<div id="dict" style="display: none; z-index: +999; position: absolute; height: 100px; width: 200px; overflow: auto;"></div>
<script  language="javascript">
	if(!getBrowseVersion()|| getBrowseVersion()==10){//非IE浏览器样式兼容性问题   wangb 20180803 bug  39353
		var  childFrame = window.parent.document.getElementById("childFrame");
		//19/3/22 xus 谷歌隐藏滚动条 
		childFrame.parentNode.parentNode.parentNode.style.overflowY = 'hidden';
		//childFrame.setAttribute('height','90%');
		var hideTypeselect = document.getElementById('hideTypeselect');
		hideTypeselect.style.height='20px';
		hideTypeselect.parentNode.style.width='199px';
		hideTypeselect.parentNode.style.height='20px';
		hideTypeselect.parentNode.style.marginTop='1px';
		hideTypeselect.parentNode.parentNode.children[1].style.top='69px';
		hideTypeselect.parentNode.parentNode.children[1].style.height='20px';
		hideTypeselect.parentNode.parentNode.children[1].style.width='177px';
		//19/3/25 xus 谷歌浏览器 选择框样式错乱
		document.getElementById('hideType').parentNode.style.marginTop='2px';
		document.getElementById('hideType').parentNode.style.height='17px';
	}
</script>
