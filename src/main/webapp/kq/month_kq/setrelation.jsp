<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->
<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>My JSP 'setrelation.jsp' starting page</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->

  </head>
  <script LANGUAGE=javascript src="/js/xtree.js"></script> 
  <body onLoad = "shows();"><br>
    <html:form action="/kq/month_kq/searchkqinfo">
    <div class="fixedDiv2" style="height: 100%;border: none">
<table  border="0" cellspacing="0" height="300" width="100%" align="center" cellpadding="0" class="ListTable" >
	<tr>
		<td style="height: 30px;" class="TableRow" align="left">参数设置<br></td>
	</tr>
	<tr>
		<td class="RecordRow"  >
			<div style="width: 100%;height: 240px;overflow: auto;">
				<table width="95%" border="0" cellpadding="0" cellspacing="0" style="padding: 5px;margin-top: 3px;margin-left: 10px;">
					<tr>
					<td align="right">审批关系:</td>
						<td align="left">
							<select name="starflag" id="starflag">
							<logic:iterate id="element" name="monthKqForm" property="setRelationList">
								<bean:define id="ids" name="element" property="itemid"></bean:define>
								<option value="${ids}"><bean:write name="element" property="itemdesc"/></option>
							</logic:iterate>
							</select>
						</td>
					</tr>
				<!--  	<tr>
						<td>
							考勤项目默认值:
						</td>
						<td>
							<select name="starflag1" id="starflag1">
							<logic:iterate id="element1" name="monthKqForm" property="codeitemList">
							<bean:define id="coid" name="element1" property="itemid"></bean:define>
								<option value="${coid}"><bean:write name="element1" property="itemdesc"/></option>
							</logic:iterate>
							</select>
						</td>
					</tr>-->
					<tr>
						<td align="right">
							考勤项目默认值:
						</td>
						<td align="left">
							<input type="text" name="kqx" id="kqx"/>
							<img src="/images/code.gif" onclick="javascript:openInputCodeDialogText1('27','kqx','kqcode');" />
							<!--  <img src="/images/code.gif" onclick="opentree();" />-->
							<input type="hidden" name="kqcode" id="kqcode" />
						</td>
					</tr>
				</table>
			</div>
		</td>
	</tr>
	<tr align="center">
		<td>
			<input type="button" value="确定" class="mybutton" onclick="saveRelation();"/>&nbsp;
			<input type="button" value="关闭" class="mybutton" onclick="window.close();"/>
		<!--  	<img src="/images/code.gif" onclick="javascript:openInputCodeDialogText('27','kqx','kqcode');" />
							<input type="hidden" name="kqcode" id="kqcode" />-->
		</td>
	</tr>
	</table>
	</div>
	</html:form>
  </body>
</html>
<script type="text/javascript">
 	 
 	    function openInputCodeDialogText1(codeid,mytarget,hidden_name) 
		{
		    var codevalue,thecodeurl,target_name,hiddenobj;
		    if(mytarget==null)
		      return;
		    var oldInputs=document.getElementsByName(mytarget);
		    
		    oldobj=oldInputs[0];
	
		    target_name=oldobj.name;
		    var hiddenInputs=document.getElementsByName(hidden_name);
		    if(hiddenInputs!=null)
		    {
		    	hiddenobj=hiddenInputs[0];
		    	codevalue="";
		    }
		    var theArr=new Array(codeid,codevalue,oldobj,hiddenobj); 
		    thecodeurl="/kq/month_kq/codeselect.jsp?codesetid="+codeid+"&codeitemid="; 
		    var popwin= window.showModelessDialog(thecodeurl, theArr, 
		        "dialogWidth:300px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
		}
 	 
 	 function showstree(obj){
 	    var pos = getAbsPosition(obj);  //获取点击时候鼠标的坐标
 	 	document.getElementById("treemenu").style.left = pos[0] - 20;
 	 	document.getElementById("treemenu").style.top = pos[1] + 20;
 	 	document.getElementById("treemenu").style.display = "block";
 	 }
 	 
 	 function demo(){
 	 	var currnode=Global.selectedItem;
 	 	var id=currnode.uid;
 	 	alert(id);
 	 }
 	 
 	 function opentree(){
 	 	var syncurl = "/kq/month_kq/kqorgtree.jsp";
		var iframe_url="/general/query/common/iframe_query.jsp?src="+syncurl;
		var return_vo= window.showModalDialog(iframe_url,"", 
	        "dialogWidth:200px; dialogHeight:280px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
 	 }
 	 
	function shows(){
		var defValue = "${monthKqForm.defValue}";
		//if(defValue < 10){
		//	defValue = "0" + defValue;
		//}
		document.getElementById("starflag").value = "${monthKqForm.relation}";
		//document.getElementById("starflag1").value = defValue;
		document.getElementById("kqx").value = "${monthKqForm.kqdefValue}";
		//document.getElementById("${monthKqForm.relation}").selected = true;
	}
	
	//保存参数设置
	function saveRelation(){
		var hashvo=new ParameterSet();
		var relationFlag = document.getElementById("starflag").value;
		//var defaultFlag = document.getElementById("starflag1").value; 之前的下拉框做法
		var defaultFlag = document.getElementById("kqcode").value; 
		if(relationFlag != null && defaultFlag != null){			
			hashvo.setValue("relationFlag",relationFlag);
			hashvo.setValue("defaultFlag",defaultFlag);
			var request=new Request({method:'post',asynchronous:false,onSuccess:showfile,functionId:'15301110304'},hashvo);
		}else{
			alert("请选择记录进行设置!");
		}
	}
	function showfile(outparamters){
		var saveStatus = outparamters.getValue("saveStatus");
		if(1 == saveStatus){
			alert(SAVESUCCESS+"!");
			window.close();
		}else{
			alert(SAVEFAILED+"!");
		}
	}
</script>
