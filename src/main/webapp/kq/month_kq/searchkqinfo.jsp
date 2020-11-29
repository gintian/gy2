<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="com.hjsj.hrms.businessobject.train.trainexam.question.questiones.QuestionesBo,com.hjsj.hrms.transaction.kq.month_kq.MonthKqBean"%>
<%@page import="com.hjsj.hrms.transaction.kq.month_kq.MonthKqBo"%>
<%@page import="com.hrms.frame.utility.AdminDb"%>
<%@page import="java.sql.Connection"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%
	int i = 0;
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>

<%
	try
	{
		Connection conn = null;
		conn = AdminDb.getConnection();
		MonthKqBo bo = new MonthKqBo(conn);
%>

<script language="javascript" src="/ajax/common.js"></script>
<script language="JavaScript" src="/js/wz_tooltip.js"></script>
<script language="JavaScript" src="/performance/batchGrade/batchGrade.js"></script>
<script language="javascript" src="/js/dict.js"></script>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/common.js"></script>
<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<script type="text/javascript">

	
	var IVersion=getBrowseVersion();

	if(IVersion==8)
	{
	  	document.writeln("<link href=\"/performance/kh_plan/kh_planTableLocked_8.css\" rel=\"stylesheet\" type=\"text/css\">");
	}else
	{
 	 	document.writeln("<link href=\"/performance/kh_plan/kh_planTableLocked.css\" rel=\"stylesheet\" type=\"text/css\">");
	}
</script>
<script type="text/javascript">
	var a0100 = "";//人员编码
	var field = "";//日期编码
	var tdids = "";//td的id 唯一的不一样的
	//点击表格中的td时 动态显示div
	
	function shows(a01001,fields,obj,objs){
		a0100 = a01001;
		field = fields;
		tdids = objs;
		var pos = getAbsPosition(obj);  //获取点击时候鼠标的坐标
		//alert(obj.title);
		//obj.html.hidden= "";
		//alert(obj.title.hidden);
		document.getElementById("shows").style.left = pos[0];
		document.getElementById("shows").style.top = pos[1];
		document.getElementById("shows").style.display = "block";
	}
	//保存考勤信息
	function savekq(codeid){
	document.getElementById("shows").style.display = "none";
	var year = document.getElementById("years").value;
	var month = document.getElementById("months").value; 
		var hashvo=new ParameterSet();
		hashvo.setValue("a0100",a0100);
		hashvo.setValue("field",field);
		hashvo.setValue("codeid",codeid);
		hashvo.setValue("year",year);
		hashvo.setValue("month",month);
		var request=new Request({method:'post',asynchronous:false,onSuccess:showfiles,functionId:'15301110302'},hashvo);
	}
	//回调函数
	function showfiles(outparamters){
		var isok = outparamters.getValue("isok");
		if(isok == 0){
			var shows = outparamters.getValue("info");
			var field = outparamters.getValue("field");
			document.getElementById(tdids).innerHTML = shows;
			//document.getElementById(tdids).style.align = "center";
			document.getElementById("shows").style.display = "none";
		}else if(isok == 1){
			alert("只能对起草、驳回的数据进行修改赋值操作!");
		}else if(isok == 3){
			alert("当月数据已经封存，无法编辑!");
		}
	}
	
	//鼠标离开时候 隐藏此DIV
	function divOnMouseLeave(){
		document.getElementById("shows").style.display = "none";
	}
	//设置
	function check(){
		var syncurl="/kq/month_kq/searchkqinfo.do?b_set=link";
		   var return_vo= window.showModalDialog(syncurl,"", 
	        "dialogWidth:400px; dialogHeight:380px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
	}
	//删除
	function del(){
	var tablevos=document.getElementsByTagName("input");
	var nid="";
	var year = document.getElementById("years").value;
	var month = document.getElementById("months").value; 
	for(var i=0;i<tablevos.length;i++){
	     if(tablevos[i].type=="checkbox"&&tablevos[i].name!="selbox"){
	     	if(tablevos[i].checked){
	     		nid += tablevos[i].value+",";
	     	}
		 }
     }
	     if(nid!=null&&nid.length>0){
			if(!confirm("是否要删除所选中的考勤记录?")){
				return false;
			}
	     	var hashvo=new ParameterSet();
			hashvo.setValue("keyid",nid);
			hashvo.setValue("year",year);
			hashvo.setValue("month",month);
			var request=new Request({method:'post',asynchronous:false,onSuccess:checkDelOk,functionId:'15301110305'},hashvo);	
	     }else{
	     	alert("请选择要删除的记录!");
	     	return false;
	     }
	}
	function checkDelOk(outparamters){
		var isok = outparamters.getValue("isok");
		alert(isok);
		window.location.href=window.location.href;
	}
	//报批
	function baopi(){
		var tablevos=document.getElementsByTagName("input");
		var nid = "";
		for(var i=0;i<tablevos.length;i++){
		     if(tablevos[i].type=="checkbox"&&tablevos[i].name!="selbox"){
		     	if(tablevos[i].checked){
		     		nid += tablevos[i].value+",";
		     	}
			 }
	     }
		 var year = document.getElementById("years").value;
		 var month = document.getElementById("months").value;
			 if(confirm("是否报批"+year+"年"+month+"月的数据?")){
		     var hashvo=new ParameterSet();
			 hashvo.setValue("type","1");
			 if("" != nid){
			 	hashvo.setValue("nid",nid);
			 }
			 hashvo.setValue("years",year);
			 hashvo.setValue("months",month);
			 var request=new Request({method:'post',asynchronous:false,onSuccess:isone,functionId:'15301110306'},hashvo);	
		}
	}
	
	function isone(outparamters){
		var isok = outparamters.getValue("isok");
		var isone = outparamters.getValue("isone");
		var usersList = outparamters.getValue("usersList");
		var codes = outparamters.getValue("codes");

		if(isone == "1"){
			window.location.href = window.location.href;
			alert(isok);
		}else{
		   var year = document.getElementById("years").value;
		   var month = document.getElementById("months").value;
		   var syncurl="/kq/month_kq/searchkqinfo.do?b_showmodel=link&codes="+codes;
		   var return_vo= window.showModalDialog(syncurl,usersList, 
	        "dialogWidth:400px; dialogHeight:320px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
		   if("" != return_vo && undefined != return_vo){		   		
			    var hashvo=new ParameterSet();
			    hashvo.setValue("type","1");
			    hashvo.setValue("years",year);
			    hashvo.setValue("months",month);
			    hashvo.setValue("manypeople",return_vo);
			    var request=new Request({method:'post',asynchronous:false,onSuccess:isOk,functionId:'15301110306'},hashvo);	
		   }
		}
	}
	
	//批准
	function pizhun(){
		 var tablevos=document.getElementsByTagName("input");
			var nid = "";
			for(var i=0;i<tablevos.length;i++){
			     if(tablevos[i].type=="checkbox"&&tablevos[i].name!="selbox"){
			     	if(tablevos[i].checked){
			     		nid += tablevos[i].value+",";
			     	}
				 }
		 }
	     var year = document.getElementById("years").value;
		 var month = document.getElementById("months").value;
		 if(confirm("是否批准"+year+"年"+month+"月的数据?")){
			 var hashvo=new ParameterSet();
			 if("" != nid){
			 	hashvo.setValue("nid",nid);
			 }
			 hashvo.setValue("years",year);
			 hashvo.setValue("months",month);
			 hashvo.setValue("type","2");
			 var request=new Request({method:'post',asynchronous:false,onSuccess:isOk,functionId:'15301110306'},hashvo);	
		}
	}
	
	//驳回
	function bohui(){
	     var tablevos=document.getElementsByTagName("input");
			var nid = "";
			for(var i=0;i<tablevos.length;i++){
			     if(tablevos[i].type=="checkbox"&&tablevos[i].name!="selbox"){
			     	if(tablevos[i].checked){
			     		nid += tablevos[i].value+",";
			     	}
				 }
		 }
	   	 var year = document.getElementById("years").value;
		 var month = document.getElementById("months").value;
		 if(confirm("是否将"+year+"年"+month+"月的数据驳回?")){
			 var hashvo=new ParameterSet();
			 if("" != nid){
			 	hashvo.setValue("nid",nid);
			 }
			 hashvo.setValue("years",year);
			 hashvo.setValue("months",month);
			 hashvo.setValue("type","3");
			 var request=new Request({method:'post',asynchronous:false,onSuccess:isOk,functionId:'15301110306'},hashvo);	
		}
	}
	
	function isOk(outparamters){
		var isok = outparamters.getValue("isok");
		alert(isok);
		window.location.href = window.location.href;
	}
	
	//封存
	function fc(){
		var year = document.getElementById("years").value;
		var month = document.getElementById("months").value;
		if(confirm("是否将"+year+"年"+month+"月的数据封存?")){
			var hashvo=new ParameterSet();
			hashvo.setValue("type","1");
			hashvo.setValue("year",year);
			hashvo.setValue("month",month);
			var request=new Request({method:'post',asynchronous:false,onSuccess:isOk,functionId:'15301110307'},hashvo);	
		}
	}
	
	//解封 
	function jf(){
		var year = document.getElementById("years").value;
		var month = document.getElementById("months").value;
		if(confirm("是否将"+year+"年"+month+"月的数据解封?")){
			var hashvo=new ParameterSet();
			hashvo.setValue("type","2");
			hashvo.setValue("year",year);
			hashvo.setValue("month",month);
			var request=new Request({method:'post',asynchronous:false,onSuccess:isOk,functionId:'15301110307'},hashvo);	
		}
	}
	//人员引入 直接拿薪资方面现成的过来套用的
	function peopleimports(){
    var infos=new Array();
	infos[0]="usra01";
	infos[1]="${monthKqForm.nbase}"; //${monthKqForm.nbase}
	infos[2]="N";
	infos[3]=document.getElementById("years").value;
	infos[4]=document.getElementById("months").value;
    var strurl="/kq/month_kq/searchkqinfoimport.do?b_query=link";
	var iframe_url="/gz/gz_accounting/bankdisk/iframe_bank_disk.jsp?src="+strurl; 
	var flag=window.showModalDialog(iframe_url,infos,"dialogWidth=600px;dialogHeight=480px;resizable=yes;scroll=no;status=no;");  
	if(flag == "retrun"){
		window.location.href=window.location.href;
	}	
	}
	//切换下拉框时所触发的事件
	function change(){
		var years = document.getElementById("years").value;
		var month = document.getElementById("months").value;
		monthKqForm.action="/kq/month_kq/searchkqinfo.do?b_query=link&type=change&years="+years+"&months="+month;
		monthKqForm.submit();
		//document.getElementById("years").value = years;
		//var hashvo=new ParameterSet();
		//hashvo.setValue("type","change");
		//hashvo.setValue("years",years);
		//hashvo.setValue("months",month);
		//var request=new Request({method:'post',asynchronous:false,onSuccess:ref,functionId:'15301110301'},hashvo);	
	}
	
	//function ref(outparamters){
	//	var year = outparamters.getValue("years");
	//	var month = outparamters.getValue("months");
	//	document.getElementById("years").value = year;
	//	document.getElementById("months").value = month;
	//	//window.location.href=window.location.href;
	//}
	//刷新右上角下拉框
	function onRef(){
		var year = ${monthKqForm.years};
		var month = ${monthKqForm.months};
		document.getElementById("years").value = year;
		document.getElementById("months").value = month;
	}
	
	function showdetail(a0100){
		var userCode = a0100;
		var strurl="/kq/month_kq/searchkqinfo.do?b_showdetail=link`userCode="+userCode;
		var iframe_url="/gz/gz_accounting/bankdisk/iframe_bank_disk.jsp?src="+strurl; 
		var flag=window.showModalDialog(iframe_url,"dialogWidth=400px;dialogHeight=380px;resizable=yes;scroll=no;status=no;");
	}
	
	//新建 后面添加的功能 点击新建 当前人所在管理范围内的人员全部引入当前年月
	function xinjian(){
		if(confirm("是否将所属范围内的人员引入进来?")){
			var years = document.getElementById("years").value;
			var month = document.getElementById("months").value;
			var hashvo=new ParameterSet();
			hashvo.setValue("year",years);
			hashvo.setValue("month",month);
			var request=new Request({method:'post',asynchronous:false,onSuccess:isOk,functionId:'15301110321'},hashvo);	
		}
	}
	//导出excel
	function exports(){
		var years = document.getElementById("years").value;
		var month = document.getElementById("months").value;
	//	alert("${monthKqForm.where_str}");
		var hashvo=new ParameterSet();
		hashvo.setValue("year",years);
		hashvo.setValue("month",month);
		hashvo.setValue("where","${monthKqForm.where_str}");
		var request=new Request({method:'post',asynchronous:false,onSuccess:showfile,functionId:'15301110322'},hashvo);	
	}
	function showfile(outparamters)
	{
		var outName=outparamters.getValue("outName");
		var name=outName.substring(0,outName.length-1)+".xls";
		window.location.target="_blank";
		window.location.href = "/servlet/vfsservlet?fileid="+name+"&fromjavafolder=true";
	}
</script>
<style>
	div#tbl-container 
	{
		width:100%;
		overflow:auto;
		BORDER-BOTTOM:#94B6E6 1pt solid; 
		BORDER-LEFT: #94B6E6 1pt solid; 
		BORDER-RIGHT: #94B6E6 1pt solid; 
		
	}
</style>
  <head>
    <base href="<%=basePath%>">
    
    <title>My JSP  starting page</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">

  </head>
  
  <body onLoad="onRef();"><br>
  <html:form action="/kq/month_kq/searchkqinfo">
  <div>
		<script language='javascript' >
				document.write("<div id=\"tbl-container\"  style='position:absolute;left:3;height:"+(document.body.clientHeight-70)+";width:99%'  >");
 </script> 
  	<table cellpadding="0" cellspacing="0" style="margin-left:10px;margin-top:15px;" width="90%">
  		<tr>
			<td align="left" width="30%" class = "t_cell_locked" style="border:0;">
			<table cellpadding="0"  cellspacing="0" width="40%" >
  					<tr>
  					<td width="30%">
  
  			<hrms:priv func_id="0AC020108">
       		<input type="button" value="新建" onclick="xinjian();" class="mybutton" />&nbsp;
       		</hrms:priv>
       		<hrms:priv func_id="0AC020113">
       		<input type="button" value="手工引入" onclick="peopleimports();" class="mybutton" />&nbsp;
       			
       		</hrms:priv>
       		<hrms:priv func_id="0AC020102">
       		
       			<input type="button" value="删除" onclick="del();" class="mybutton" />&nbsp;
       		</hrms:priv>
       		<hrms:priv func_id="0AC020103">
       			<logic:equal name="monthKqForm" property="isShowButton" value="false">
       			<input type="button" value="报批" onclick="baopi();" class="mybutton" />&nbsp;
       			</logic:equal>
       		</hrms:priv>
       			<hrms:priv func_id="0AC020105">
       			<logic:equal name="monthKqForm" property="isShowButton" value="true">
       			<input type="button" value="批准" onclick="pizhun();" class="mybutton" />&nbsp;
       			</logic:equal>
       		</hrms:priv>
       		<hrms:priv func_id="0AC020104">
       			<input type="button" value="驳回" onclick="bohui();" class="mybutton" />&nbsp;
       			
       		</hrms:priv>
       	
       		<hrms:priv func_id="0AC020106">
       			<input type="button" value="封存" onclick="fc();" class="mybutton" />&nbsp;
       			
       		</hrms:priv>
       		<hrms:priv func_id="0AC020107">
       			<input type="button" value="解封" onclick="jf();" class="mybutton" />&nbsp;
       			
       		</hrms:priv>
       		
       		
       		<hrms:priv func_id="0AC020114">
       		<input type="button" value="导出Excel" onclick="exports();" class="mybutton" />
       			
       		</hrms:priv>
  	   
  	        </td></tr></table>

  			</td>
  			<td width="70%" align="left" class = "t_cell_locked"  style="border:0;" nowrap>
  				年份:<select id="years" name="years" onchange="change();">
  						<logic:iterate id="element5" name="monthKqForm"  property="yearList" indexId="index">
  							<bean:define id="yearId" name="element5" property="years"></bean:define>
  							<option value="${yearId}">${yearId}</option>
  						</logic:iterate>
  					</select>&nbsp;
  				月份:<select id="months" name="months" onchange="change();">
  						<logic:iterate id="element6" name="monthKqForm"  property="monthList" indexId="index">
  							<bean:define id="monthId" name="element6" property="months"></bean:define>
  							<option value="${monthId}">${monthId}</option>
  						</logic:iterate>
  					</select>
  			</td>
  		
  		</tr>
  		</table>
  		<br/>
  				<table cellpadding="0"  cellspacing="0" width="100%" id="MyTable">
  					<tr>
  						<td rowspan="2" class = "t_cell_locked" style="BORDER-BOTTOM: #C4D8EE 1pt solid; BORDER-LEFT: #C4D8EE 1pt solid;BORDER-RIGHT: 0; BORDER-TOP: #C4D8EE 1pt solid;background-color:#f4f7f7;font-weight: bold;" align="center"/>
  							<input type="checkbox" name="selbox" onclick="batch_select_all(this);" title='<bean:message key="label.query.selectall"/>'>
  						</td>
  						<td rowspan="2" class = "t_cell_locked" align="center" style="BORDER-BOTTOM: #C4D8EE 1pt solid; BORDER-LEFT: #C4D8EE 1pt solid;BORDER-RIGHT: #C4D8EE 1pt solid; BORDER-TOP: #C4D8EE 1pt solid;background-color:#f4f7f7;font-weight: bold;">
  							姓&nbsp;名
  						</td>
  						<td colspan="<bean:write name='monthKqForm' property='clos'/>" class = "t_cell_locked" align="left" class="TableRow" style="BORDER-BOTTOM: #C4D8EE 1pt solid; BORDER-LEFT: 0;BORDER-RIGHT: 0; BORDER-TOP: #C4D8EE 1pt solid;background-color:#f4f7f7;font-weight: bold;">
  							&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
  							&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
  							&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
  							&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
  							&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
  							&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
  							&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
  							&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
  							&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;考勤情况
  						</td>
  					</tr>
  					<tr>
  						<logic:iterate id="element" name="monthKqForm"  property="list" indexId="index">
  							<logic:equal name="element" property="state" value="1">
  								<logic:equal name="monthKqForm" property="riqi" value="31">
  								 <logic:notEqual name="element" property="itemid" value="a0101">
  								 <logic:equal name="element" property="isSuoDing" value="yes">
  									<td class = "t_cell_locked" style="font-weight: bold;BORDER-BOTTOM: #C4D8EE 1pt solid; BORDER-LEFT: 0;BORDER-RIGHT: #C4D8EE 1pt solid; BORDER-TOP: 0;background-color:#f4f7f7;" align="center"  nowrap >
	  									&nbsp;<bean:write  name="element" property="itemdesc" filter="true"/>&nbsp;
	 	 							</td>
  								 </logic:equal>
  								  <logic:notEqual name="element" property="isSuoDing" value="yes">
  								 	<td class="TableRow" style="border-left:0px;border-top:0px;" align="center"  nowrap >
	  									&nbsp;<bean:write  name="element" property="itemdesc" filter="true"/>&nbsp;
	 	 							</td>
	 	 							  </logic:notEqual>
	 	 						</logic:notEqual>
 	 							</logic:equal>
 	 							<logic:equal name="monthKqForm" property="riqi" value="30">
 	 								<logic:notEqual name="element" property="itemid" value="a0101">
	 	 								<logic:notEqual name="element" property="itemid" value="q3531">
		  									<logic:equal name="element" property="isSuoDing" value="yes">
  									<td class = "t_cell_locked" style="font-weight: bold;BORDER-BOTTOM: #C4D8EE 1pt solid; BORDER-LEFT: #C4D8EE 1pt solid;BORDER-RIGHT: #C4D8EE 1pt solid; BORDER-TOP: #C4D8EE 1pt solid;background-color:#f4f7f7;" align="center"  nowrap >
	  									&nbsp;<bean:write  name="element" property="itemdesc" filter="true"/>&nbsp;
	 	 							</td>
  								 </logic:equal>
  								  <logic:notEqual name="element" property="isSuoDing" value="yes">
  								 	<td class="TableRow" style="border-left:0px;border-top:0px;" align="center"  nowrap >
	  									&nbsp;<bean:write  name="element" property="itemdesc" filter="true"/>&nbsp;
	 	 							</td>
	 	 							  </logic:notEqual>
		 	 							</logic:notEqual>
	 	 							</logic:notEqual>
 	 							</logic:equal>
 	 							<logic:equal name="monthKqForm" property="riqi" value="29">
	 	 							<logic:notEqual name="element" property="itemid" value="a0101">
	 	 								<logic:notEqual name="element" property="itemid" value="q3531">
	 	 									<logic:notEqual name="element" property="itemid" value="q3530">
			  									<logic:equal name="element" property="isSuoDing" value="yes">
					  									<td class = "t_cell_locked" style="font-weight: bold;BORDER-BOTTOM: #C4D8EE 1pt solid; BORDER-LEFT: 0;BORDER-RIGHT: #C4D8EE 1pt solid; BORDER-TOP: 0;background-color:#f4f7f7;" align="center"  nowrap >
					  								 	&nbsp;<bean:write  name="element" property="itemdesc" filter="true"/>&nbsp;
				 	 								</td>
					  								 </logic:equal>
					  								  <logic:notEqual name="element" property="isSuoDing" value="yes">
					  								 	<td class="TableRow" style="border-left:0px;border-top:0px;" align="center"  nowrap >
													  		&nbsp;<bean:write  name="element" property="itemdesc" filter="true"/>&nbsp;
				 	 								    </td>
													  </logic:notEqual>
		 	 								</logic:notEqual>
		 	 							</logic:notEqual>
		 	 						</logic:notEqual>
  							</logic:equal>
  							<logic:equal name="monthKqForm" property="riqi" value="28">
	  							<logic:notEqual name="element" property="itemid" value="a0101">
	 	 								<logic:notEqual name="element" property="itemid" value="q3531">
	 	 									<logic:notEqual name="element" property="itemid" value="q3530">
		 	 									<logic:notEqual name="element" property="itemid" value="q3529">
				  									<logic:equal name="element" property="isSuoDing" value="yes">
					  									<td class = "t_cell_locked" style="font-weight: bold;BORDER-BOTTOM: #C4D8EE 1pt solid; BORDER-LEFT: 0;BORDER-RIGHT: #C4D8EE 1pt solid; BORDER-TOP: 0;background-color:#f4f7f7;" align="center"  nowrap >
					  								 	&nbsp;<bean:write  name="element" property="itemdesc" filter="true"/>&nbsp;
				 	 								</td>
					  								 </logic:equal>
					  								  <logic:notEqual name="element" property="isSuoDing" value="yes">
					  								 	<td class="TableRow" style="border-left:0px;border-top:0px;" align="center"  nowrap >
													  		&nbsp;<bean:write  name="element" property="itemdesc" filter="true"/>&nbsp;
				 	 								    </td>
													  </logic:notEqual>
				  									
				 	 							</logic:notEqual>
		 	 								</logic:notEqual>
		 	 							</logic:notEqual>
		 	 					</logic:notEqual>
  							</logic:equal>
  							</logic:equal>
  						</logic:iterate>
  					</tr>
  					<hrms:paginationdb id="element1" name="monthKqForm" 
            		sql_str="monthKqForm.sql_str" table="" 
            		where_str="monthKqForm.where_str"
            		columns="monthKqForm.cols_str" 
           		    pagerows="${monthKqForm.pagerows}" page_id="pagination" indexes="indexes" >
            		 <%
            		 	if (i % 2 == 0)
            		 				{
            		 %>
		            	 <tr class="trShallow" onMouseOver="javascript:tr_onclick(this,'E4F2FC')" height="70px">
		             <%
		             	}
		             				else
		             				{
		             %>
		            	 <tr class="trDeep" onMouseOver="javascript:tr_onclick(this,'E4F2FC')" height="70px">
		             <%
		             	}
		             				i++;
		             %>
		             	<td align="center"style="BORDER-BOTTOM: #C4D8EE 1pt solid; BORDER-LEFT: #C4D8EE 1pt solid;BORDER-RIGHT: 0; border-top:0"  class = "t_cell_locked" nowrap >
		             		<bean:define id="nid" name='element1' property='a0100'/>
    						<input type="checkbox" name="${nid}" value="${nid}">
		             	</td>
		             	<td align="left" style="BORDER-BOTTOM: #C4D8EE 1pt solid; BORDER-LEFT: #C4D8EE 1pt solid;BORDER-RIGHT: #C4D8EE 1pt solid; border-top:0" class = "t_cell_locked" nowrap>
		             		<bean:define id="a0100" name="element1" property="a0100"></bean:define>
		             		&nbsp;<bean:write name="element1" property="a0101"/>
		             	</td>
		             		<logic:iterate id="element2" name="monthKqForm"  property="list" indexId="index">
	  							<logic:equal name="element2" property="state" value="1">
	  							<logic:equal name="element2" property="codesetid" value="27">
	  								<logic:equal name="monthKqForm" property="riqi" value="31">
	  								<logic:notEqual name="element2" property="itemid" value="a0101">
	  								<logic:equal name="element2" property="isshow" value="no">
	  								
	  								<bean:define id="qids" name="element2" property="itemid"></bean:define>
		  								<td  class="RecordRow" style="cursor:pointer;border-left:0px;border-top:0px;" title="点击修改考勤情况" onclick="shows('${a0100}','${qids}',this,'${a0100}'+'${index}');" id="${a0100}${index}" align="center" nowrap>
		  									<logic:equal name="element2" property="itemtype" value="A">		
		  										<logic:equal name="element2" property="codesetid" value="0">
		  											<bean:define id="names" name="element2" property="itemid" />  										
		  											<bean:write  name="element1" property="${names}" filter="true"/>
		  										</logic:equal>
		  										<logic:notEqual name="element2" property="codesetid" value="0"> 
		  											<bean:define id="codes" name="element2" property="itemid"></bean:define>
		  											<bean:define id="codesetsid" name="element2" property="codesetid" />
		  											<hrms:codetoname codeid="${codesetsid}" name="element1"
														codevalue="${codes}" codeitem="codeitem" scope="page" />
													<bean:write name="codeitem" property="codealiasname" />&nbsp;
		  										</logic:notEqual>
		  									</logic:equal>
		  									<logic:notEqual name="element2" property="itemtype" value="A">
		  										<bean:write  name="element2" property="itemdesc" filter="true"/>
		  									</logic:notEqual>
		 	 							</td>
		 	 							
									</logic:equal>
									</logic:notEqual>
		 	 						 <logic:equal name="element2" property="isshow" value="yes">
		 	 						 <bean:define id="qids1" name="element2" property="itemid"></bean:define>
		 	 						    <bean:define id="names" name="element2" property="itemid" /> 
		 	 						    <logic:notEqual name="element1" property="${names}" value=""> 
		 	 						  	 <td style="background-color:#f4f7f7;height:22px;BORDER-BOTTOM: #C4D8EE 1pt solid; BORDER-RIGHT: #C4D8EE 1pt solid; " onclick="shows('${a0100}','${qids1}',this,'${a0100}'+'${index}');" id="${a0100}${index}" align="center" nowrap>
		  											<bean:define id="codes" name="element2" property="itemid"></bean:define>
		  											<bean:define id="codesetsid" name="element2" property="codesetid" />
		  											<hrms:codetoname codeid="${codesetsid}" name="element1"
														codevalue="${codes}" codeitem="codeitem" scope="page" />
													<bean:write name="codeitem" property="codealiasname" />&nbsp;
										</td>
		  								</logic:notEqual>
		  								<logic:equal name="element1" property="${names}" value="">
		 	 						 	<td class="TableRow" style="border-left:0px;border-top:0px;" onclick="shows('${a0100}','${qids1}',this,'${a0100}'+'${index}');" id="${a0100}${index}">
		 	 						 		&nbsp;
		 	 						 	</td>
		 	 						 	</logic:equal>
		 	 						 </logic:equal>
 	 								</logic:equal>
 	 								<logic:equal name="monthKqForm" property="riqi" value="30">
 	 									<logic:notEqual name="element2" property="itemid" value="a0101">
	 	 								<logic:notEqual name="element2" property="itemid" value="q3531">
	 	 								<logic:equal name="element2" property="isshow" value="no">
	 	 								<bean:define id="qids" name="element2" property="itemid"></bean:define>
			  								<td  class="RecordRow" style="cursor:pointer;border-left:0px;border-top:0px;" title="点击修改考勤情况" onclick="shows('${a0100}','${qids}',this,'${a0100}'+'${index}');" id="${a0100}${index}" align="center" nowrap>
			  									<logic:equal name="element2" property="itemtype" value="A">		
			  										<logic:equal name="element2" property="codesetid" value="0">
			  											<bean:define id="names" name="element2" property="itemid" />  										
			  											<bean:write  name="element1" property="${names}" filter="true"/>
			  										</logic:equal>
			  										<logic:notEqual name="element2" property="codesetid" value="0"> 
			  											<bean:define id="codes" name="element2" property="itemid"></bean:define>
			  											<bean:define id="codesetsid" name="element2" property="codesetid" />
			  											<hrms:codetoname codeid="${codesetsid}" name="element1"
															codevalue="${codes}" codeitem="codeitem" scope="page" />
														<bean:write name="codeitem" property="codealiasname" />&nbsp;
			  										</logic:notEqual>
			  									</logic:equal>
			  									<logic:notEqual name="element2" property="itemtype" value="A">
			  										<bean:write  name="element2" property="itemdesc" filter="true"/>
			  									</logic:notEqual>
			 	 							</td>
			 	 						</logic:equal>
			 	 						<logic:equal name="element2" property="isshow" value="yes">
			 	 						<bean:define id="qids1" name="element2" property="itemid"></bean:define>
		 	 						    <bean:define id="names" name="element2" property="itemid" /> 
		 	 						    <logic:notEqual name="element1" property="${names}" value=""> 
		 	 						  	 <td style="background-color:#f4f7f7;height:22px;BORDER-BOTTOM: #C4D8EE 1pt solid; BORDER-RIGHT: #C4D8EE 1pt solid; " onclick="shows('${a0100}','${qids1}',this,'${a0100}'+'${index}');" id="${a0100}${index}" align="center" nowrap>
		  											<bean:define id="codes" name="element2" property="itemid"></bean:define>
		  											<bean:define id="codesetsid" name="element2" property="codesetid" />
		  											<hrms:codetoname codeid="${codesetsid}" name="element1"
														codevalue="${codes}" codeitem="codeitem" scope="page" />
													<bean:write name="codeitem" property="codealiasname" />&nbsp;
										</td>
		  								</logic:notEqual>
		  								<logic:equal name="element1" property="${names}" value="">
		 	 						 	<td class="TableRow" style="border-left:0px;border-top:0px;" onclick="shows('${a0100}','${qids1}',this,'${a0100}'+'${index}');" id="${a0100}${index}">
		 	 						 		&nbsp;
		 	 						 	</td>
		 	 						 	</logic:equal>
		 	 						 </logic:equal>
			 	 						</logic:notEqual>
			 	 						</logic:notEqual>
 	 								</logic:equal>
 	 								<logic:equal name="monthKqForm" property="riqi" value="29">
 	 								<logic:notEqual name="element2" property="itemid" value="a0101">
 	 								<logic:notEqual name="element2" property="itemid" value="q3531">
 	 								<logic:notEqual name="element2" property="itemid" value="q3530">
 	 								<logic:equal name="element2" property="isshow" value="no">
 	 								<bean:define id="qids" name="element2" property="itemid"></bean:define>
			  							<td  class="RecordRow" style="cursor:pointer;border-left:0px;border-top:0px;" title="点击修改考勤情况" onclick="shows('${a0100}','${qids}',this,'${a0100}'+'${index}');" id="${a0100}${index}" align="center" nowrap >
		  									<logic:equal name="element2" property="itemtype" value="A">		
		  										<logic:equal name="element2" property="codesetid" value="0">
		  											<bean:define id="names" name="element2" property="itemid" />  										
		  											<bean:write  name="element1" property="${names}" filter="true"/>
		  										</logic:equal>
		  										<logic:notEqual name="element2" property="codesetid" value="0"> 
		  											<bean:define id="codes" name="element2" property="itemid"></bean:define>
		  											<bean:define id="codesetsid" name="element2" property="codesetid" />
		  											<hrms:codetoname codeid="${codesetsid}" name="element1"
														codevalue="${codes}" codeitem="codeitem" scope="page" />
													<bean:write name="codeitem" property="codealiasname" />&nbsp;
		  										</logic:notEqual>
		  									</logic:equal>
		  									<logic:notEqual name="element2" property="itemtype" value="A">
		  										<bean:write  name="element2" property="itemdesc" filter="true"/>
		  									</logic:notEqual>
		 	 							</td>
		 	 							</logic:equal>
		 	 							<logic:equal name="element2" property="isshow" value="yes">
		 	 							<bean:define id="qids1" name="element2" property="itemid"></bean:define>
		 	 						    <bean:define id="names" name="element2" property="itemid" /> 
		 	 						    <logic:notEqual name="element1" property="${names}" value=""> 
		 	 						  	 <td style="background-color:#f4f7f7;height:22px;BORDER-BOTTOM: #C4D8EE 1pt solid; BORDER-RIGHT: #C4D8EE 1pt solid; " onclick="shows('${a0100}','${qids1}',this,'${a0100}'+'${index}');" id="${a0100}${index}" align="center" nowrap>
		  											<bean:define id="codes" name="element2" property="itemid"></bean:define>
		  											<bean:define id="codesetsid" name="element2" property="codesetid" />
		  											<hrms:codetoname codeid="${codesetsid}" name="element1"
														codevalue="${codes}" codeitem="codeitem" scope="page" />
													<bean:write name="codeitem" property="codealiasname" />&nbsp;
										</td>
		  								</logic:notEqual>
		  								<logic:equal name="element1" property="${names}" value="">
		 	 						 	<td class="TableRow" style="border-left:0px;border-top:0px;" onclick="shows('${a0100}','${qids1}',this,'${a0100}'+'${index}');" id="${a0100}${index}" >
		 	 						 		&nbsp;
		 	 						 	</td>
		 	 						 	</logic:equal>
		 	 						 </logic:equal>
		 	 							</logic:notEqual>
		 	 							</logic:notEqual>
		 	 							</logic:notEqual>
 	 								</logic:equal>
 	 								<logic:equal name="monthKqForm" property="riqi" value="28">
 	 								<logic:notEqual name="element2" property="itemid" value="a0101">
 	 								<logic:notEqual name="element2" property="itemid" value="q3531">
 	 								<logic:notEqual name="element2" property="itemid" value="q3530">
 	 								<logic:notEqual name="element2" property="itemid" value="q3529">
 	 								<logic:equal name="element2" property="isshow" value="no">
 	 								<bean:define id="qids" name="element2" property="itemid"></bean:define>
			  							<td  class="RecordRow" style="cursor:pointer;border-left:0px;border-top:0px;" title="点击修改考勤情况" onclick="shows('${a0100}','${qids}',this,'${a0100}'+'${index}');" id="${a0100}${index}" align="center" nowrap>
		  									<logic:equal name="element2" property="itemtype" value="A">		
		  										<logic:equal name="element2" property="codesetid" value="0">
		  											<bean:define id="names" name="element2" property="itemid" />  										
		  											<bean:write  name="element1" property="${names}" filter="true"/>
		  										</logic:equal>
		  										<logic:notEqual name="element2" property="codesetid" value="0"> 
		  											<bean:define id="codes" name="element2" property="itemid"></bean:define>
		  											<bean:define id="codesetsid" name="element2" property="codesetid" />
		  											<hrms:codetoname codeid="${codesetsid}" name="element1"
														codevalue="${codes}" codeitem="codeitem" scope="page" />
													<bean:write name="codeitem" property="codealiasname" />&nbsp;
		  										</logic:notEqual>
		  									</logic:equal>
		  									<logic:notEqual name="element2" property="itemtype" value="A">
		  										<bean:write  name="element2" property="itemdesc" filter="true"/>
		  									</logic:notEqual>
		 	 							</td>
		 	 							</logic:equal>
		 	 							<logic:equal name="element2" property="isshow" value="yes">
		 	 							<bean:define id="qids1" name="element2" property="itemid"></bean:define>
		 	 						    <bean:define id="names" name="element2" property="itemid" /> 
		 	 						    <logic:notEqual name="element1" property="${names}" value=""> 
		 	 						   <td style="background-color:#f4f7f7;height:22px;BORDER-BOTTOM: #C4D8EE 1pt solid; BORDER-RIGHT: #C4D8EE 1pt solid; " onclick="shows('${a0100}','${qids1}',this,'${a0100}'+'${index}');" id="${a0100}${index}" align="center" nowrap>
		  											<bean:define id="codes" name="element2" property="itemid"></bean:define>
		  											<bean:define id="codesetsid" name="element2" property="codesetid" />
		  											<hrms:codetoname codeid="${codesetsid}" name="element1"
														codevalue="${codes}" codeitem="codeitem" scope="page" />
													<bean:write name="codeitem" property="codealiasname" />&nbsp;
										</td>
		  								</logic:notEqual>
		  								<logic:equal name="element1" property="${names}" value="">
		  								
		  								<td style="background-color:#f4f7f7;height:22px;BORDER-BOTTOM: #C4D8EE 1pt solid; BORDER-RIGHT: #C4D8EE 1pt solid; " onclick="shows('${a0100}','${qids1}',this,'${a0100}'+'${index}');" id="${a0100}${index}" align="center" nowrap>
		  									&nbsp;
		  								</td>
		  								</logic:equal>
		 	 						 	
		 	 						 </logic:equal>
		 	 							</logic:notEqual>
		 	 							</logic:notEqual>
		 	 							</logic:notEqual>
		 	 							</logic:notEqual>
 	 								</logic:equal>
	  							</logic:equal>
	  							</logic:equal>
	  							<logic:notEqual name="element2" property="codesetid" value="27">
	  							<logic:notEqual name="element2" property="itemid" value="a0101">
	  							<logic:equal name="element2" property="codesetid" value="0">
	  							<logic:equal name="element2" property="isSuoDing" value="yes">
	  								<td style="BORDER-BOTTOM: #C4D8EE 1pt solid; BORDER-LEFT: 0;BORDER-RIGHT: #C4D8EE 1pt solid; border-top:0" class = "t_cell_locked" nowrap>
	  									<bean:define id="names" name="element2" property="itemid" /> 
	  									<bean:define id="nn" name="element1" property="id"></bean:define>
	  									<bean:define id="name" name="element1" property="${names}" type="java.lang.String"></bean:define>
	  									<logic:equal name="element2" property="itemid" value="approcess">
	  									<logic:notEqual name="element1" property="${names}" value="">										
		  									<a href="#" onclick="showdetail(${nn})">${name}</a>
		  								</logic:notEqual> 
		  								</logic:equal>
		  								<logic:notEqual name="element2" property="itemid" value="approcess">
		  								<logic:notEqual name="element2" property="itemid" value="userflag">
		  								<logic:notEqual name="element2" property="itemid" value="curr_user">
		  									<!--  <a href="#" onclick="showdetail(${nn})">${name}</a>-->
		  									${name}
		  								</logic:notEqual>
		  								</logic:notEqual>
		  								</logic:notEqual>
		  								<logic:equal name="element2" property="itemid" value="userflag">
		  									&nbsp;<%=bo
														.getUserNameById(name)%>
		  								</logic:equal>
		  								<logic:equal name="element2" property="itemid" value="curr_user">
		  									&nbsp;<%=bo
														.getUserNameById(name)%>
		  								</logic:equal>
		  								<logic:equal name="element1" property="${names}" value="">
											&nbsp;
										</logic:equal>
	  								</td>
	  							</logic:equal>
	  							<logic:notEqual name="element2" property="isSuoDing" value="yes">
	  								<td class="RecordRow" style="border-left:0px;border-top:0px;" nowrap>
	  									<bean:define id="names" name="element2" property="itemid" /> 
	  									<bean:define id="nn" name="element1" property="id"></bean:define>
	  									<bean:define id="name" name="element1" property="${names}" type="java.lang.String"></bean:define>
	  									<logic:equal name="element2" property="itemid" value="approcess">
	  									<logic:notEqual name="element1" property="${names}" value="">										
		  									<a href="#" onclick="showdetail(${nn})">${name}</a>
		  								</logic:notEqual> 
		  								</logic:equal>
		  								<logic:notEqual name="element2" property="itemid" value="approcess">
		  								<logic:notEqual name="element2" property="itemid" value="userflag">
		  								<logic:notEqual name="element2" property="itemid" value="curr_user">
		  									<!--  <a href="#" onclick="showdetail(${nn})">${name}</a>-->
		  									${name}
		  								</logic:notEqual>
		  								</logic:notEqual>
		  								</logic:notEqual>
		  									
		  								<logic:equal name="element2" property="itemid" value="userflag">
		  									&nbsp;<%=bo
														.getUserNameById(name)%>
		  								</logic:equal>
		  								<logic:equal name="element2" property="itemid" value="curr_user">
		  									&nbsp;<%=bo
														.getUserNameById(name)%>
		  								</logic:equal>
		  								<logic:equal name="element1" property="${names}" value="">
											&nbsp;
										</logic:equal>
	  								</td>
	  							</logic:notEqual>
	  							</logic:equal>
	  							<logic:notEqual name="element2" property="codesetid" value="0">
	  								<logic:notEqual name="element2" property="codesetid" value="">
	  								<logic:equal name="element2" property="isSuoDing" value="yes">
	  								<td style="BORDER-BOTTOM: #C4D8EE 1pt solid; BORDER-LEFT: 0; solid;BORDER-RIGHT: #C4D8EE 1pt solid; border-top:0" class = "t_cell_locked" nowrap>
	  									<bean:define id="codesetsid" name="element2" property="codesetid" />
	  									<bean:define id="codes" name="element2" property="itemid"></bean:define>
	  									<hrms:codetoname codeid="${codesetsid}" name="element1"
															codevalue="${codes}" codeitem="codeitem" scope="page" />
										<logic:notEqual name="codeitem" property="codename" value="">
										&nbsp;<bean:write name="codeitem" property="codename" />
										</logic:notEqual>
										<logic:equal name="codeitem" property="codename" value="">
											&nbsp;
										</logic:equal>
										</td>
									</logic:equal>
									<logic:notEqual name="element2" property="isSuoDing" value="yes">
										<td class="RecordRow" style="border-left:0px;border-top:0px;" align="center" nowrap>
	  									<bean:define id="codesetsid" name="element2" property="codesetid" />
	  									<bean:define id="codes" name="element2" property="itemid"></bean:define>
	  									<hrms:codetoname codeid="${codesetsid}" name="element1"
															codevalue="${codes}" codeitem="codeitem" scope="page" />
										<logic:notEqual name="codeitem" property="codename" value="">
										&nbsp;<bean:write name="codeitem" property="codename" />
										</logic:notEqual>
										<logic:equal name="codeitem" property="codename" value="">
											&nbsp;
										</logic:equal>
										</td>
									</logic:notEqual>
									</logic:notEqual>
									
	  							</logic:notEqual>
	  							</logic:notEqual>
	  							</logic:notEqual>
  							</logic:iterate>
		             </tr>
            		</hrms:paginationdb>
            		
  				</table>
  			</td>
  		</tr>
  	
  		</table></div>
  <div style='position:absolute; bottom:25px; left:3px;width:99%'>	
  <table width="100%" align="center" class="RecordRowP">
					<tr>
						<td valign="bottom" class="tdFontcolor">
							<hrms:paginationtag name="monthKqForm"
								pagerows="${monthKqForm.pagerows}" property="pagination"
								scope="page" refresh="true"></hrms:paginationtag>
						</td>
						<td align="right" nowrap class="tdFontcolor">
							<p align="right">
								<hrms:paginationdblink name="monthKqForm"
									property="pagination" nameId="monthKqForm" scope="page">
								</hrms:paginationdblink>
								</p>
						
			</td>
		</tr>
  	</table>
  </div><div>
  </html:form>
   <div id='shows' style='position:absolute;top:100;left:400;display:none; height:400px; overflow-y:auto;padding-left:0px;' onMouseLeave="divOnMouseLeave();">
 	<table cellpadding="0px" cellspacing="0px" width="120px" height="100px" bgColor='#FFFFFF'  align="left" class='ListTable' border="0">
 		<logic:iterate id="element3" name="monthKqForm" property="list2">
 		<bean:define id="codeid" name="element3" property="itemid"></bean:define>
 		<tr onclick="savekq('${codeid}');" align='left' class="RecordRow" nowrap>
 			<td style="cursor:pointer">&nbsp;&nbsp;<bean:write name="element3" property="corcode"/></td>
 		</tr>
 		</logic:iterate>
 	</table>
 </div>
  </body>
</html>
<%
	}
	catch (Exception e)
	{
		e.printStackTrace();
		throw GeneralExceptionHandler.Handle(e);
	}
	finally
	{
		if (conn != null)
		{
			conn.close();
		}
	}
%>

