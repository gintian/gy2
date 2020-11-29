<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.constant.SystemConfig" %>
<%
int i = 0;
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
String maxsize = "2048k";
		if(SystemConfig.getPropertyValue("appendix_size")!=null&&SystemConfig.getPropertyValue("appendix_size").length()>0){
			maxsize = SystemConfig.getPropertyValue("appendix_size");
		}
		if(maxsize.indexOf("k")==-1 && maxsize.indexOf("K")==-1){
		maxsize+="K";
		}
%>


<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>
<script language="javascript" src="/ajax/constant.js"></script>
<script language="javascript" src="/ajax/basic.js"></script>

<script language="javascript" src="/ajax/common.js"></script>

<script language="javascript" src="/ajax/control.js"></script>
<script language="javascript" src="/ajax/dataset.js"></script>
<script language="javascript" src="/ajax/editor.js"></script>
<script language="javascript" src="/ajax/dropdown.js"></script>
<script language="javascript" src="/ajax/table.js"></script>
<script language="javascript" src="/ajax/menu.js"></script>
<script language="javascript" src="/ajax/tree.js"></script>
<script language="javascript" src="/ajax/pagepilot.js"></script>
<script language="javascript" src="/ajax/command.js"></script>
<script language="javascript" src="/ajax/format.js"></script>
<script language="javascript" src="/js/validate.js"></script>
<script language="javascript" src="/js/constant.js"></script>
<script language="javascript">
    var _checkBrowser=true;
	var _disableSystemContextMenu=false;
	var _processEnterAsTab=true;
	var _showDialogOnLoadingData=true;
	var _enableClientDebug=true;
	var _theme_root="/ajax/images";
	var _application_root="";
	var __viewInstanceId="968";
	var ViewProperties=new ParameterSet();
	var webserver=1;
	
</script>

<script language="javascript" src="/ajax/common.js"></script>
<script language="JavaScript" src="/js/wz_tooltip.js"></script>
<script language="JavaScript" src="/performance/batchGrade/batchGrade.js"></script>
<script language="javascript" src="/js/dict.js"></script>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/common.js"></script>
<hrms:themes />

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    <title>My JSP 'searchquarters.jsp' starting page</title>
  </head>
<style>
strong
{
    font-size:18px;
}

</style>
  <script type="text/javascript">
  	//返回到团队
  	function returnoriginal()
  	{
  		var returnurl="${newworkplanForm.returnUrl}";
  		window.location=returnurl;
  	}
  		//加载当前年份和当前季度
  		function init()
  		{
  			var istoobig="${newworkplanForm.isTooBig}";
  			if(istoobig==1)
  				alert("上传的文件过大！");
  			else if(istoobig==2)
  				alert("上传的文件内容不能为空!");
  			document.getElementById("years").value = "${newworkplanForm.year}";
  			if("${newworkplanForm.type}" == 1){  				
	  			document.getElementById("season").value = "${newworkplanForm.season}";
  				document.getElementById("startMonth").innerText = "${newworkplanForm.startMonth}";
  				document.getElementById("endMonth").innerText = "${newworkplanForm.endMonth}";
  			}
		   // if("${newworkplanForm.shows}" != ""){       		
		   //    alert("${newworkplanForm.shows}");
		   // }
		    document.getElementById("desc").innerText = "${newworkplanForm.states}";
		    document.getElementById("code").value = "${newworkplanForm.code}";
		    /**
		    if("${newworkplanForm.code}" == "02" ||
		       			"${newworkplanForm.code}" == "03" ){
		        document.getElementById("message").disabled = "false";
		    	document.getElementById("tq").style.display = "none";
			    document.getElementById("baocun").style.display = "none";
			    document.getElementById("baopi").style.display = "none";
			    document.getElementById("hz").style.display = "none";
		    }
		    
		    if("${newworkplanForm.opt}" == 2)
		    { //领导进入 没有提取功能
		    	document.getElementById("tq").style.display = "none";
		    	document.getElementById("baopi").style.display = "inline";
			    if("${newworkplanForm.isread}" == "1")
			    {
			    	document.getElementById("message").disabled = "false";
			    	document.getElementById("baocun").style.display = "none";
			    	document.getElementById("baopi").style.display = "none";
			    	document.getElementById("hz").style.display = "none";
			    }
			    else
			    {
			    	document.getElementById("message").disabled = false;
			    	document.getElementById("baocun").style.display = "inline";
			    	document.getElementById("baopi").style.display = "inline";
			    	if("${newworkplanForm.isdept}" == 2 )
			    	{
			    		document.getElementById("hz").style.display = "inline";
			    	}
			    }
		    }
		    //if("${newworkplanForm.isdept}" == 1){
		    //	document.getElementById("hz").style.display = "none";
		    //}
		    **/
  		}
  		//保存季报
  		function save(){
  			//var message = document.getElementById("message").value;
  			var year = document.getElementById("years").value;
  			if(confirm("确认要保存吗?")){
  			// opt 判断
  			if("${newworkplanForm.type}" == 1){	
	  			var season = document.getElementById("season").value;
  				newworkplanForm.action = "/performance/nworkplan/searchquarters.do?b_save=link&type="+"${newworkplanForm.type}"+"&year="+year+"&season="+season+"&startMonth="+"${newworkplanForm.startMonth}" + "&endMonth="+"${newworkplanForm.endMonth}" + "&opt=" + "${newworkplanForm.opt}";
  			}else if("${newworkplanForm.type}" == 2){
  				newworkplanForm.action = "/performance/nworkplan/searchquarters.do?b_save=link&type="+"${newworkplanForm.type}"+"&year="+year+"&startMonth="+"${newworkplanForm.startMonth}" + "&endMonth="+"${newworkplanForm.endMonth}"+ "&opt=" + "${newworkplanForm.opt}";
  			}
  				newworkplanForm.submit();
  			}
  		}
  		
  		function isok(outparamters){
  			var message = outparamters.getValue("message");
  			var disable = outparamters.getValue("disable");
  			var desc = outparamters.getValue("states");
  			var code = outparamters.getValue("code");
  			var startMonth = outparamters.getValue("startMonth");
  			var endMonth = outparamters.getValue("endMonth");
  			document.getElementById('message').innerText = getDecodeStr(message);
  			document.getElementById("desc").innerText = desc;
		    document.getElementById("code").value = code;
		    if("02" == code || "03" == code){	    	
			    document.getElementById("tq").style.display = "none";
			    document.getElementById("baocun").style.display = "none";
			    document.getElementById("baopi").style.display = "none";
			    document.getElementById("hz").style.display = "none";
			    document.getElementById("message").disabled = "false";
		    }else{
		        document.getElementById("message").disabled = false;
		        if("${newworkplanForm.isdept}" == 2){
		        	document.getElementById("hz").style.display = "inline";
		        }
		    	document.getElementById("tq").style.display = "inline";
			    document.getElementById("baocun").style.display = "inline";
			    document.getElementById("baopi").style.display = "inline";
		    }
		    if("${newworkplanForm.type}" == 1){	
			    document.getElementById("startMonth").innerText = startMonth;
	  			document.getElementById("endMonth").innerText = endMonth;
  			}
  			 if("${newworkplanForm.opt}" == 2){ //领导进入 没有提取功能
		    	document.getElementById("tq").style.display = "none";
		    	document.getElementById("baopi").style.display = "inline";
			    if("${newworkplanForm.isread}" == "1"){
			    	document.getElementById("message").disabled = "false";
			    	document.getElementById("baocun").style.display = "none";
			    	document.getElementById("baopi").style.display = "none";
			    }else{
			    	document.getElementById("message").disabled = false;
			    	document.getElementById("baocun").style.display = "inline";
			    	document.getElementById("baopi").style.display = "inline";
			    	if("${newworkplanForm.type}" == 2 ){
			    		document.getElementById("hz").style.display = "inline";
			    	}
			    }
		    }
		    if("1" == disable){
		    	document.getElementById("message").disabled = true;
		    }else{
			    if("02" != code && "03" != code){
			    	document.getElementById("message").disabled = false;
			    }
		    }
		    
  		}
  		//提取上一季度的季报内容到本季度的输入文本框中
  		function tiqu(){
  			var year = document.getElementById("years").value;
  			var hashvo=new ParameterSet();
  			hashvo.setValue("year",year);
  			hashvo.setValue("in_type","1");
  			hashvo.setValue("type","${newworkplanForm.type}");
  			if("${newworkplanForm.type}" == 1){	
	  			var season = document.getElementById("season").value;
	  			hashvo.setValue("season",season);
  			}
  			var request=new Request({method:'post',asynchronous:false,onSuccess:isok,functionId:'90100170159'},hashvo);
  		}
  		
  		//增加年份按钮绑定事件
  		function add_year(){
  			var value = getInt(document.getElementById("years").value);
		    value = value+1;
			document.getElementById("years").value = value;
			if("${newworkplanForm.type}" == 1){				
				check(document.getElementById("years").value,document.getElementById("season").value);
			}else if("${newworkplanForm.type}" == 2){
				check(document.getElementById("years").value,"");
			}
  		}
  		
  		//减少年份按钮绑定事件
  		function del_year(){
  			var value = getInt(document.getElementById("years").value);
  			value = value - 1;
  			document.getElementById("years").value = value;
  			if("${newworkplanForm.type}" == 1){				
				check(document.getElementById("years").value,document.getElementById("season").value);
			}else if("${newworkplanForm.type}" == 2){
				check(document.getElementById("years").value);
			}
  		}
  		
  		//增加季度按钮绑定事件
  		function add_season(){
  			var value = getInt(document.getElementById("season").value);
  			var yearValue = getInt(document.getElementById("years").value);
		    value = value+1;
		    if(value > 4){
		      value = 1;
		      yearValue = yearValue + 1;
		      document.getElementById("years").value = yearValue;
		      document.getElementById("season").value = value;
		      check(document.getElementById("years").value,document.getElementById("season").value);
		    }else{
			  document.getElementById("season").value = value;
			  check(document.getElementById("years").value,document.getElementById("season").value);
			}
  		}
  		
  		//减少季度按钮绑定事件
  		function del_season(){
  			var value = getInt(document.getElementById("season").value);
  			var yearValue = getInt(document.getElementById("years").value);
  			value = value - 1;
  			if(value < 1){
  				value = 4;
  				document.getElementById("season").value = value;
  				yearValue = yearValue - 1;
  				document.getElementById("years").value = yearValue; 
  				check(document.getElementById("years").value,document.getElementById("season").value); 				
  			}else{
  			    document.getElementById("season").value = value;
  			    check(document.getElementById("years").value,document.getElementById("season").value);
  			}
  		}
  		
  		//点击按钮切换年份 季度时候触发事件
  		function checks(year,season){
  			var hashvo=new ParameterSet();
  			hashvo.setValue("year",year);
  			hashvo.setValue("in_type","2");
  			hashvo.setValue("type","${newworkplanForm.type}");
  			hashvo.setValue("season",season);
  			hashvo.setValue("opt","${newworkplanForm.opt}");
  			hashvo.setValue("isdept","${newworkplanForm.isdept}");
  			if("${newworkplanForm.opt}" == 2){
  				hashvo.setValue("isread","${newworkplanForm.isread}");
  				hashvo.setValue("p0100","${newworkplanForm.p0100}");
  			}
  			var request=new Request({method:'post',asynchronous:false,onSuccess:isok,functionId:'90100170159'},hashvo);
  		}
  		
  		
  		//导出页面内容  暂时不要了
  		function exportseason(){
  			var year = document.getElementById("years").value;
  			var message = document.getElementById("message").value;
  			var hashvo = new ParameterSet();
  			hashvo.setValue("year",year);
  			if("${newworkplanForm.type}" == 1){	
	  			var season = document.getElementById("season").value;
	  			hashvo.setValue("season",season);
  			}
  			hashvo.setValue("type","${newworkplanForm.type}");
  			hashvo.setValue("message",getEncodeStr(message));
  			var request = new Request({method:'post',asynchronous:false,onSuccess:showfile,functionId:'90100170160'},hashvo);
  		}
  		
  		//导出月度总结
  		function exportyuezongjie(){
  			var year = document.getElementById("years").value;
  			var season = document.getElementById("season").value;
  			var startMonth = getInt(document.getElementById("startMonth").innerText);
  			var endMonth = getInt(document.getElementById("endMonth").innerText);
  			var hashvo = new ParameterSet();
  			hashvo.setValue("year",year);
  			hashvo.setValue("season",season);
  			hashvo.setValue("startMonth",startMonth);
  			hashvo.setValue("endMonth",endMonth);
  			hashvo.setValue("isdept","${newworkplanForm.isdept}");
  			hashvo.setValue("type","${newworkplanForm.type}");
  			var request = new Request({method:'post',asynchronous:false,onSuccess:showfile,functionId:'90100170161'},hashvo);
  		}
  		
  		function exportyearzj(){
  			var year = document.getElementById("years").value;
  			var hashvo = new ParameterSet();
  			hashvo.setValue("year",year);
  			hashvo.setValue("opt","${newworkplanForm.opt}");
  			if("${newworkplanForm.opt}" == 2){
  				hashvo.setValue("a0100","${newworkplanForm.a0100}");
  				hashvo.setValue("nbase","${newworkplanForm.nbase}");
  			}
  			hashvo.setValue("isdept","${newworkplanForm.isdept}");
  			hashvo.setValue("type","${newworkplanForm.type}");
  			var request = new Request({method:'post',asynchronous:false,onSuccess:showfile,functionId:'90100170161'},hashvo);
  		}
  		
  		function showfile(outparamters)
		{
			var outName=outparamters.getValue("outName");
			outName=getDecodeStr(outName);
			var name=outName.substring(0,outName.length-1)+".xls";
			name=getEncodeStr(name);
			window.location.target="_blank";
			window.location.href = "/servlet/vfsservlet?fromjavafolder=true&fileid="+name;
		}
		
		//查询
		function searchinfo(){
		if("${newworkplanForm.opt}" == 1){
			newworkplanForm.action = "/performance/nworkplan/searchquarters.do?b_search=link&type=" + "${newworkplanForm.type}" + "&opt=" +"${newworkplanForm.opt}"+"&content=";
		}else if("${newworkplanForm.opt}" == 2){
			newworkplanForm.action = "/performance/nworkplan/searchquarters.do?b_search=link&type=" + "${newworkplanForm.type}" + "&opt=" +"${newworkplanForm.opt}" +"&p0100=" +"${newworkplanForm.p0100}"+"&content=";		
		}
  			newworkplanForm.submit();
		}
		
		//报批
		function bp(){
			var year = document.getElementById("years").value;
			var hashvo=new ParameterSet();
			hashvo.setValue("year",year);
			hashvo.setValue("type","${newworkplanForm.type}");
			hashvo.setValue("opt","${newworkplanForm.opt}");
			if("${newworkplanForm.type}" == 1){	
				var season = document.getElementById("season").value;
  				hashvo.setValue("season",season);
  			}
  			if("${newworkplanForm.opt}" == 2){
  				hashvo.setValue("p0100","${newworkplanForm.p0100}");
  			}
  			hashvo.setValue("isdept","${newworkplanForm.isdept}");
  			var request=new Request({method:'post',asynchronous:false,onSuccess:rs,functionId:'90100170163'},hashvo);
		}
		
		function rs (outparamters){
			var isok = outparamters.getValue("isok");
			var isone = outparamters.getValue("isone");
			var codes = outparamters.getValue("codes");
			if(isone == "1"){				
				window.location.href=window.location.href;
				alert(isok);
			}else if(isone == "2"){
				 var syncurl="/kq/month_kq/searchkqinfo.do?b_showmodel=link&codes="+codes;
		  		 var return_vo= window.showModalDialog(syncurl,"", 
	        	"dialogWidth:400px; dialogHeight:320px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
				if("" != return_vo ){
					var year = document.getElementById("years").value;
				    var hashvo=new ParameterSet();
					hashvo.setValue("year",year);
					hashvo.setValue("type","${newworkplanForm.type}");
					if("${newworkplanForm.type}" == 1){	
				    	var season = document.getElementById("season").value;
		  				hashvo.setValue("season",season);
		  			}
		  			hashvo.setValue("manypeople",return_vo);
		  			var request=new Request({method:'post',asynchronous:false,onSuccess:rs,functionId:'90100170163'},hashvo);
				}
			}
		}
		
		//汇总(部门)
		function huiz(){
			var year = document.getElementById("years").value;
			if("${newworkplanForm.type}" == 1){
				var startMonth = getInt(document.getElementById("startMonth").innerText);
  				var endMonth = getInt(document.getElementById("endMonth").innerText);
			}
			 var hashvo=new ParameterSet();
			 hashvo.setValue("year",year);
			 hashvo.setValue("opt","${newworkplanForm.opt}");
			 hashvo.setValue("type","${newworkplanForm.type}");
			 hashvo.setValue("isdept","${newworkplanForm.isdept}");
			if("${newworkplanForm.type}" == 1){
				 hashvo.setValue("startMonth",startMonth);
	  			 hashvo.setValue("endMonth",endMonth);
				var season = document.getElementById("season").value;
				hashvo.setValue("season",season);
			}
			var request=new Request({method:'post',asynchronous:false,onSuccess:hz,functionId:'90100170165'},hashvo);
		}
		function hz(outparamters){
			var message = outparamters.getValue("message");
			var desc = outparamters.getValue("states");
  			var code = outparamters.getValue("code");
			document.getElementById('message').innerText = getDecodeStr(message);
			document.getElementById("desc").innerText = desc;
		    document.getElementById("code").value = code;
		    if("02" == code || "03" == code){	    	
			    document.getElementById("tq").style.display = "none";
			    document.getElementById("baocun").style.display = "none";
			    document.getElementById("baopi").style.display = "none";
			    document.getElementById("hz").style.display = "none";
			    document.getElementById("message").disabled = "false";
		    }
		}
		
		//上传 2013-03-14 新加(需求不按照开发文档上面的来了 新改的)
		function upload(){
			 var f_obj = document.getElementsByName("file");
			 var year = document.getElementById("years").value;
			 var hashvo=new ParameterSet();
			 hashvo.setValue("year",year);
			 hashvo.setValue("file",f_obj);
			 var isdept=${newworkplanForm.isdept};
			 var p0100="";
			 var object=document.getElementById("p0100");
			 if(object!=null)
			 	p0100=object.value;
			 if("${newworkplanForm.type}" == 1){
				var startMonth = getInt(document.getElementById("startMonth").innerText);
	  			var endMonth = getInt(document.getElementById("endMonth").innerText);
				var season = document.getElementById("season").value;
				newworkplanForm.action = "/performance/nworkplan/searchquarters.do?b_upload=link&year=" + year + "&season=" +season+"&startMonth="+startMonth + "&endMonth=" + endMonth + "&type=1&isdept="+isdept+"&p0100="+p0100;
			}else if("${newworkplanForm.type}" == 2){
				newworkplanForm.action = "/performance/nworkplan/searchquarters.do?b_upload=link&year=" + year + "&type=2&isdept="+isdept+"&p0100="+p0100;
			}
			newworkplanForm.submit();
		}
		
		function check(year , season){
			newworkplanForm.action = "/performance/nworkplan/searchquarters.do?b_query=link&opt=${newworkplanForm.opt}&type=${newworkplanForm.type}&isdept=${newworkplanForm.isdept}&year="+year+"&season="+season+"&islike=true";
			newworkplanForm.submit();
		}
		
		//提交
		function tijiao(){
			var obj=document.getElementById("p0100");
			if(obj==null)
			{
				alert("请先上传附件");
				return false;
			}
			var p0100 = document.getElementById("p0100").value;
			var hashvo=new ParameterSet();
			hashvo.setValue("p0100",p0100);
			var request=new Request({method:'post',asynchronous:false,onSuccess:tj,functionId:'90100170168'},hashvo);
		}
		//提交成功后
		function tj(outparamters){
			var isok = outparamters.getValue("isok");
			alert(isok);
			var opt=${newworkplanForm.opt};
			var type=${newworkplanForm.type};
			var isdept=${newworkplanForm.isdept};
			window.location.href="/performance/nworkplan/searchquarters.do?b_query=link&opt="+opt+"&type="+type+"&isdept="+isdept;
		}
		//删除
		function del(p0100,file_id){
			var hashvo = new ParameterSet();
			hashvo.setValue("p0100",p0100);
			hashvo.setValue("file_id",file_id);
			var request=new Request({method:'post',asynchronous:false,onSuccess:afterdelete,functionId:'90100170166'},hashvo);
		}
		//删除成功后
		function afterdelete(outparamters)
		{
			var isok = outparamters.getValue("isok");
			if(isok==0)
			{
				alert("删除失败！");
			}
			else if(isok==1)
			{
				alert("删除成功！");
				var opt=${newworkplanForm.opt};
				var type=${newworkplanForm.type};
				var isdept=${newworkplanForm.isdept};
				window.location.href="/performance/nworkplan/searchquarters.do?b_query=link&opt="+opt+"&type="+type+"&isdept="+isdept;
			}
			
		}
  </script>
  <body onLoad="init();">
  <html:form action="/performance/nworkplan/searchquarters" method="post"  enctype="multipart/form-data">
    	<table cellpadding="0"  cellspacing="0" width="100%" id="MyTable" style="padding-left:0px;padding-right:0px;">
    		<tr>
    			<!--<td class="TableRow" style="width:0px;" align="right">
    				<input type="text" value="2013" name="years" style="width:40"/>
    			</td>
    			<td valign="middle" align="left" class="TableRow" style="">
    			 <table border="0" cellspacing="2" cellpadding="0" >
		      		<tr><td><button id="y_up" class="m_arrow" onclick='inc_year($("theyear"));'>5</button></td></tr>
		      		<tr><td><button id="y_down" class="m_arrow" onclick='dec_year($("theyear"));'>6</button></td></tr>
	             </table>
    			</td>
    			<td class="TableRow"  align="left" style="width:3px;">
    				年&nbsp;第
    			</td>
    			<td class="TableRow" style="border:0px;">
    				<input type="text" value="1" name="season" style="width:40" />
    			</td>
    			 <td valign="middle" align="left" class="TableRow" style="border:0px;">
	             		<table border="0" cellspacing="2" cellpadding="0">
		      				<tr><td><button id="m_up" class="m_arrow" onclick='inc_month($("themonth"));'>5</button></td></tr>
		      				<tr><td><button id="m_down" class="m_arrow" onclick='dec_month($("themonth"));'>6</button></td></tr>
	             		</table>
	                 </td>	
    			<td class="TableRow" >
    				季度总结及计划&nbsp;(<bean:write name="newworkplanForm" property="startMonth"/>-<bean:write name="newworkplanForm" property="endMonth"/>月)&nbsp;&nbsp;起草
    			</td>-->
    			<td>
    				<table width="100%" border="0" cellspacing="0" align="left" cellpadding="0">
	
	<tr>
	<td>
	<logic:equal name="newworkplanForm" property="type" value="1">
		<table border="0" cellspacing="0" cellpadding="0" width="100%">
		<tr >
		<td >
			<table border="0" cellspacing="0" cellpadding="0">
	         <tr>
	          <td valign="middle" align="left">
	          	<input type="text"  name="years" style="width:40"/>                     
	          </td>                      
	          <td valign="middle" align="left">
	             <table width="100%" border="0" cellspacing="1" cellpadding="0" >
		      		<tr><td><button id="y_up" class="m_arrow" onclick='add_year();'>5</button></td></tr>
		      		<tr><td><button id="y_down" class="m_arrow" onclick='del_year();'>6</button></td></tr>
	             </table>
	          </td>
			  <td valign="middle" align="left"><strong><bean:message key="hmuster.label.year"/>&nbsp;第</strong></td>	          
	   				  <td valign="middle" align="left"> 
	       	  			<input type="text" value="1" name="season" style="width:40" />                    
	         		 </td>
	          		 <td valign="middle" align="left">
	             		<table border="0" cellspacing="1" cellpadding="0">
		      				<tr><td><button id="m_up" class="m_arrow" onclick='add_season();'>5</button></td></tr>
		      				<tr><td><button id="m_down" class="m_arrow" onclick='del_season();'>6</button></td></tr>
	             		</table>
	                 </td>	
	                 <td valign="middle" align="left">
	                 	<strong>季度总结及计划&nbsp;(<span id="startMonth"></span>-<span id="endMonth"></span>月)&nbsp;&nbsp;</strong><B id="desc"></B>
	                 	<input type="hidden" name="code"/>
	                 </td>	          
	          <td align="right">
	          	<logic:equal name="newworkplanForm" property="opt" value="2">
	          		<input type="button" class="mybutton" value="返回" onclick="returnoriginal();"/>
	          	</logic:equal>
	          </td>
	          </tr>
	          </table>
	      </logic:equal>
	      <logic:equal name="newworkplanForm" property="type" value="2">
	      		<table border="0" cellspacing="0" cellpadding="0" width="35%">
		<tr >
		<td >
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
	         <tr> 
	          <td align="right"></td>                                 
	          <td valign="middle" style="width:6px;">
	          	<input type="text"  name="years" style="width:40"/>                     
	      	  </td>                    

	          <td valign="middle" align="left" style="width:6px;" >
	             <table border="0" cellspacing="2" cellpadding="0" >
		      		<tr><td><button id="y_up" class="m_arrow" onclick='add_year();'>5</button></td></tr>
		      		<tr><td><button id="y_down" class="m_arrow" onclick='del_year();'>6</button></td></tr>
	             </table>
	          </td>
			  <td valign="middle" style="width:6px;"><strong><bean:message key="hmuster.label.year"/></strong>&nbsp;</td>
			  <logic:equal name="newworkplanForm" property="opt" value="1">	          
		          <td valign="middle" align="left" >
		             	<strong>总结及计划</strong>&nbsp;&nbsp;<B id="desc"></B>
		               <input type="hidden" name="code"/>
		          </td>	
	          </logic:equal>
	          <logic:equal name="newworkplanForm" property="opt" value="2">	          
		          <td valign="middle" align="left" style="width:100px;">
		             	<strong>总结及计划</strong><B id="desc"></B>
		               <input type="hidden" name="code"/>
		          </td>	
	          </logic:equal>
	          <td align="left">    
		          <logic:equal name="newworkplanForm" property="opt" value="2">
		          		<input type="button" class="mybutton" value="返回" onclick="returnoriginal();"/>
		          </logic:equal>  
	          </td>    
	          </tr>
	          </table>
	      </logic:equal>
    	  </td>
		</tr>
		</table>    
    			</td>
    		</tr>
    		<tr>
    			<td width="100%" class="RecordRow" style="border-top:0;border-bottom:0;">
    				<!-- <textarea id="message" name="message" cols="148" rows="30" class="text5" ><bean:write name="newworkplanForm" property="message"/></textarea>  -->  
    				<table class="ListTable" width="100%" cellpadding="0" cellspacing="0" border="0">
    					<tr>
    						<td width="10%" align="center" class="TableRow" nowrap>
    							&nbsp;序号
    						</td>
    						<td width="70%" align="center" class="TableRow" nowrap>
    							&nbsp;文件名称
    						</td>
    						<td width="20%" align="center" class="TableRow" nowrap>
    							&nbsp;操作
    						</td>
    					</tr>
    					<hrms:paginationdb id="element1" name="newworkplanForm" 
    					allmemo="1" sql_str="newworkplanForm.sql" table="" 
            			where_str="newworkplanForm.where"
            			columns="newworkplanForm.cols" 
           		    	pagerows="100" page_id="pagination" indexes="indexes" >
           		    	 <% if(i%2==0){ %>
		            	 <tr onMouseOver="javascript:tr_onclick(this,'E4F2FC')">
			             <%  }else{ %>
			            	 <tr onMouseOver="javascript:tr_onclick(this,'E4F2FC')">
			             <%}
			             	i++;
			             %>
			             <td class="RecordRow" nowrap>
			             	&nbsp;${indexes + 1}
			             </td>
			             <td class="RecordRow" nowrap>
			             <bean:define id="p0100" name="element1" property="p0100"></bean:define>
			             <input type="hidden" name="p0100" id="p0100" value="${p0100}" />
			             <bean:define id="file_id" name="element1" property="file_id"></bean:define>
			             	&nbsp;<bean:write name="element1" property="name" />.<bean:write name="element1" property="ext" />
			             </td>
           		    	<td align="center" class="RecordRow" nowrap>
           		    		<a href="/servlet/performance/fileDownLoad?opt=workView&p0100=${p0100}&file_id=${file_id}"><img src="/images/detail.gif" border="0" alt="下载"/></a>
           		    		<logic:equal name="newworkplanForm" property="opt" value="1">
	           		    		<logic:equal name="newworkplanForm" property="isCommitOk" value="0">
	           		    			&nbsp;<a href="#" onclick="del(${p0100},${file_id});"><img src="/images/del.gif" border="0" alt="删除"/></a>
	           		    		</logic:equal>
           		    		</logic:equal>
           		    	</td>
           		    </hrms:paginationdb>
    				</table>
    			</td>
    		</tr>
    		<logic:equal name="newworkplanForm" property="opt" value="1">
	    		<logic:equal name="newworkplanForm" property="isCommitOk" value="0">
	    		<tr>
	    			<td class="RecordRow">
	    				上传的附件最大为<%=maxsize %>!
	    			</td>
	    		</tr>
	    		</logic:equal>
    		</logic:equal>
    		<tr>
    			<td align="left" class="RecordRow" style="border-top:none;padding-top:3px;padding-bottom:3px;">
    				<table width="100%" cellpadding="0" cellspacing="0" border="0">
    					<tr>
    						<td width="70%" align="left">
    						<logic:equal name="newworkplanForm" property="opt" value="1">
	    						<logic:equal name="newworkplanForm" property="isCommitOk" value="0">
	    							<input name="file" onchange='upload();' onkeydown= "if(event.keyCode==13) this.fireEvent('onchange');"  type="file" size="40">  
	    						</logic:equal>
    						</logic:equal>
    						</td>
    						<td width="30%" align="right">
    							<input type="button" class="mybutton" value="查询" onclick="searchinfo();"/>
    							<logic:equal name="newworkplanForm" property="opt" value="1">
    								<logic:equal name="newworkplanForm" property="isCommitOk" value="0">
	    								<input type="button" class="mybutton" value="提交" onclick="tijiao();" name="tj" />
	    							</logic:equal>
				    				<logic:equal name="newworkplanForm" property="type" value="1">
				    					<input type="button" class="mybutton" value="导出月度总结" onclick="exportyuezongjie();"/>
				    				</logic:equal>
				    				<logic:equal name="newworkplanForm" property="type" value="2">
				    					<input type="button" class="mybutton" value="导出年度总结" onclick="exportyearzj()"/>
				    				</logic:equal>
				    			</logic:equal>
			    				&nbsp;
    						</td>
    					</tr>
    				</table>
    			</td>
    		</tr>
    	</table>
   </html:form>
  </body>
</html>
