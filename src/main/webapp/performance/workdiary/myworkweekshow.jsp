<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hjsj.hrms.actionform.performance.workdiary.WorkdiaryForm,
				 com.hjsj.hrms.utils.PubFunc"%>
<%	
	WorkdiaryForm workdiaryForm = (WorkdiaryForm)session.getAttribute("workdiaryForm");
    String state = workdiaryForm.getState();
    if(state==null)
    	state="";
	state = PubFunc.encryption(state);
	
	String a0 = PubFunc.encryption("0");
	String a1 = PubFunc.encryption("1");
	String a2 = PubFunc.encryption("2");
%>

<DIV id="overDiv" style="POSITION: absolute; Z-INDEX: 1;background-color:#FFFFCC;overflow:visible;background-image:../images/mainbg.jpg"></DIV><!--  class="RecordRow" -->
<style type="text/css">
#scroll_box {
    border: 1px solid #eee;
    height: 300px;    
    width: 390px;            
    overflow: auto;            
    margin: 1em 1;
}
</style>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="workdiary.js"></script>
<script language="JavaScript" src="/js/wz_tooltip.js"></script>
<link href='/ext/ext6/resources/ext-theme.css' rel='stylesheet' type='text/css'><link>
<script language="JavaScript" src="/ext/ext6/ext-all.js"></script>
<script language="JavaScript" src="/components/personPicker/PersonPicker.js"></script>
<script type="text/javascript" language="javascript">
//<!--
function serchWeek(){
	var yearnum=document.getElementById("yearnum").value;
	var monthnum=document.getElementById("monthnum").value;
	if(yearnum.length<1&&monthnum.length<1){
		return;
	}
	workdiaryForm.action="/performance/workdiary/myworkweekshow.do?b_query=link&state=<%=a1 %>";
    workdiaryForm.submit();
}
function serchMonth(){
	var yearnum=document.getElementById("yearnum").value;
	if(yearnum.length<1){
		return;
	}
	workdiaryForm.action="/performance/workdiary/myworkweekshow.do?b_query=link&state=<%=a2 %>";
    workdiaryForm.submit();
}
function searchweek(){
	workdiaryForm.action="/performance/workdiary/myworkweekshow.do?b_query=link&state=<%=a1 %>";
    workdiaryForm.submit();
}
function searchmonth(){
	workdiaryForm.action="/performance/workdiary/myworkweekshow.do?b_query=link&state=<%=a2 %>";
    workdiaryForm.submit();
}
function showsjfw(){
	var sjfw=$('sjfw');
	sjfw.style.display="block";
}
function checkboxFalse(checkValue){
	checkValue.checked=false;  
}
function getCheck(){
	var checks = 0;
	var tablevos=document.getElementsByTagName("input");
	for(var i=0;i<tablevos.length;i++){
	    if(tablevos[i].type=="checkbox"){
	    	alert(tablevos[i].name);
	    	if(tablevos[i].checked){
	    		checks=1
	    	}
	    	break;
		}
    }
    return checks;
}
function outContent(content){
	config.FontSize='10pt';//hint提示信息中的字体大小
	Tip(getDecodeStr(content),STICKY,true);
}
function chaosong(){
	window.selecteds="";
	var tablevos=document.getElementsByTagName("input");
	for(var i=1;i<tablevos.length;i++){
	    if(tablevos[i].type=="checkbox"){
	    	if(tablevos[i].checked){
	    		selecteds+=tablevos[i].name+",";
	    	}
		}
    }
	if(selecteds==""||selecteds=="0"){
		alert("请选择要抄送项！");
	}else{
		var target_url="/selfservice/lawbase/add_law_text_role.do?b_relating1=link`pri=0`chkflag=11`p0100="+selecteds;
		var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(target_url);
		// 多浏览器兼容showModalDialog改为Ext.window形式 chent 20171226 add
	    if(/msie/i.test(navigator.userAgent)){
	    	var return_vo= window.showModalDialog(iframe_url, 'trainClass_win2', "dialogWidth:600px; dialogHeight:430px;resizable:no;center:yes;scroll:yes;status:no");	
		    chaosong_ok(return_vo);
			return ;
		} else {
			var picker = new PersonPicker({
				multiple : true,
				text : "确定",
				titleText : "选择抄送人",
				isPrivExpression:false,
				callback :function(c){
					var idArray = [];
					for (var i = 0; i < c.length; i++) {
						var staffId = c[i].id;
						idArray.push(staffId);
					}
					chaosong_ok(idArray, '1');
				} 
			}, this);
			picker.open();	
		}
    	
	}
}
   
function chaosong_ok(return_vo, bencrypt){
	if(return_vo!=null){
		var recordstr = "";
		for(var i=0;i<return_vo.length;i++){
			if(return_vo[i]!=null&&return_vo[i].length>0){
				recordstr+=return_vo[i]+"`";
			}
		}
		var hashvo=new ParameterSet();
		hashvo.setValue("personstr",recordstr);
		hashvo.setValue("selecteds",selecteds);
		if(bencrypt){
			hashvo.setValue("bencrypt", bencrypt);
		}
		var request=new Request({method:'post',onSuccess:showResult,functionId:'9020010016'},hashvo);
	}
}

function showResult(outparamters){ 
   	 if(outparamters.getValue("result")=="success"){
   	 	alert("抄送已完成！");
   	 } else{
   	 	alert("抄送失败！！！您选中的重要报告已有抄送给"+outparamters.getValue("result").substring(0,outparamters.getValue("result").length-1)+"这些人了，很抱歉不能重复抄送！");
   	 }
}
function printExcel(){
	var hashvo=new ParameterSet();
	hashvo.setValue("state","${workdiaryForm.state}");
	hashvo.setValue("yearnum","${workdiaryForm.yearnum}");
	<logic:equal value="1" name="workdiaryForm" property="state">
		hashvo.setValue("monthnum","${workdiaryForm.monthnum}");
	</logic:equal>
	var request=new Request({method:'post',asynchronous:false,onSuccess:showFieldList,
		functionId:'2020050051'},hashvo);
}
function showFieldList(outparamters){
	var outName=outparamters.getValue("filename");
	if(outName!=null&&outName.length>1)
		window.open("/servlet/vfsservlet?fromjavafolder=true&fileid="+outName);
}
function selAll(selall)
{
	var tablevos=document.getElementsByTagName("input");
	for(var i=1;i<tablevos.length;i++){
		if(tablevos[i].type=="checkbox"){
			tablevos[i].checked=selall.checked==true?"checked":"";
		}
	} 
} 
function view(p0100){
	workdiaryForm.action = "/general/impev/importantevcomment.do?b_query=link&flag=1&p0600="+p0100;
	workdiaryForm.submit();
}
//-->
</script>
<hrms:themes />
<html:form action="/performance/workdiary/myworkdiaryshow">
<table width="100%" border="0" cellspacing="0"  cellpadding="0" style="border-left-width:0px;border-right-width:0px;border-bottom-width:0px;">
<tr>
	<td width="90" nowrap style="padding-left:10px;" height="20px;">
		<logic:equal value="1" name="workdiaryForm" property="state">
			<html:select name="workdiaryForm" property="yearnum" styleId="yearnum" style="width:60"  onchange="serchWeek();">
			<html:optionsCollection property="yearlist" value="dataValue" label="dataName" />
			</html:select>
			<bean:message key="datestyle.year"/> 
		</logic:equal>
		<logic:equal value="2" name="workdiaryForm" property="state" >
			<html:select name="workdiaryForm" property="yearnum" styleId="yearnum" style="width:60" onchange="serchMonth();">
			 	<html:optionsCollection property="yearlist" value="dataValue" label="dataName" />
			</html:select><bean:message key="datestyle.year"/>  
		</logic:equal>
	</td>
	<td>
		<logic:equal value="1" name="workdiaryForm" property="state">
			<html:select name="workdiaryForm" property="monthnum" styleId="monthnum" style="width:60"  onchange="serchWeek();">
			 	<html:optionsCollection property="monthlist" value="dataValue" label="dataName" />
			</html:select><bean:message key="datestyle.month"/> 
		</logic:equal>&nbsp;
	</td>
</tr>
</table>
${workdiaryForm.tablestr}
<table width="100%" align="center">
<tr>
	<td align="center">
		<BUTTON name="sub" class="mybutton"  onclick="subs();return false;"><bean:message key="button.appeal"/></BUTTON>
		<BUTTON name="sub" class="mybutton"  onclick="dels();return false;" ><bean:message key="button.delete"/>	</BUTTON>
		<BUTTON name="selecrper" class="mybutton"  onclick="chaosong();return false;">抄送</BUTTON>
		<BUTTON name="bdel" class="mybutton" onclick="printExcel();return false;">导出Excel</BUTTON>
		<BUTTON name="return" class="mybutton"  onclick="returnblack();" ><bean:message key="button.return"/></BUTTON>
	</td>
</tr>
</table>
</html:form>
	<script type="text/javascript" language="javascript">
//<!--
///报批 首先检查是否选中了记录，然后检查是否具备报批的条件
function subs(){
	var strname="";//p0100
	var strvalue="";
	var state ="${workdiaryForm.state}";
	var yearnum=document.getElementById("yearnum").value;
	var monthnum=0;
	if(state==1){
		 monthnum=document.getElementById("monthnum").value;
	}
	
	var tablevos=document.getElementsByTagName("input");
	for(var i=1;i<tablevos.length;i++){
	    if(tablevos[i].type=="checkbox"){
	    	if(tablevos[i].checked){
	    		strname+=tablevos[i].name+",";
	    		strvalue+=tablevos[i].value+",";
	    	}
		}
    }
	if(strname.length==0){
		alert("<bean:message key='performance.workdiary.select.daily'/>");
	}else{
		var hashvo=new ParameterSet();
		hashvo.setValue("strname",strname);
		hashvo.setValue("strvalue",strvalue);		
		hashvo.setValue("yearnum",yearnum);
		hashvo.setValue("monthnum",monthnum);
		hashvo.setValue("state",state);
   		var request=new Request({method:'post',asynchronous:false,onSuccess:computeIsOk,functionId:'9020010010'},hashvo);
		
	}
	    
}
///如果符合报批条件，就查找上级（直管领导）
function computeIsOk(outparamters){
	var check=outparamters.getValue("check");
	var strname=outparamters.getValue("strname");
	var fillcheck=outparamters.getValue("fillcheck");
	if(getDecodeStr(check)=='ok'&&getDecodeStr(fillcheck)=='ok'){
		var hashvo=new ParameterSet();
		hashvo.setValue("strname",strname);
		var request=new Request({method:'post',asynchronous:false,onSuccess:getSuperiorUser,functionId:'9020010015'},hashvo);
		
	}else{
		if(getDecodeStr(check)!='ok'){
			alert(getDecodeStr(check));
		}else{
			alert(getDecodeStr(fillcheck));
		}
		
	}
}
///如果有两个或以上直管领导，就会弹出一个框让你选。如果没有，或只有一个，就会直接执行appiary()方法
function getSuperiorUser(outparamters){
	var curr_user="";
	var strname=outparamters.getValue("strname");
	if(outparamters.getValue("outname").length==1){
		curr_user=(outparamters.getValue("outname")[0].split(":"))[0];
		appiary(curr_user,strname);
	}else if(outparamters.getValue("outname").length>1){
		var thecodeurl="/performance/workdiary/cat.jsp?outname="+outparamters.getValue("outname");
    	var return_vo= window.showModalDialog(thecodeurl, "_blank", 
              "dialogHeight:220px;dialogWidth:330px;center:yes;help:no;resizable:yes;status:no;scroll:no;");
       if(return_vo!=null && return_vo.length>=0){
       		appiary(return_vo,strname);
       }
	} else{
		appiary(curr_user,strname);
	}
}
///报批，修改状态字段
function appiary(curr_user,strname){
	if(confirm(APP_OK+"?")){
		workdiaryForm.action="/performance/workdiary/myworkweekshow.do?b_submit=link&curr_user="+curr_user+"&p0100="+strname;
   		workdiaryForm.submit();
	}
}
function dels(){
	var str="";
	var tablevos=document.getElementsByTagName("input");
	for(var i=1;i<tablevos.length;i++){
	    if(tablevos[i].type=="checkbox"){
	    	if(tablevos[i].checked){
	    		str+=tablevos[i].name+",";
	    	}
		}
    }
	if(str.length==0){
		alert("<bean:message key='performance.workdiary.delete.info'/>");
	}else{
		if(confirm("<bean:message key='performance.workdiary.delete.info.ok'/>")){
			workdiaryForm.action="/performance/workdiary/myworkweekshow.do?b_del=link&p0100="+str;
    		workdiaryForm.submit();
		}
	}
}
function returnblack(){
	workdiaryForm.action="/performance/workdiary/index.jsp";
	workdiaryForm.submit();
}

var st = "";
var en = "";
function addiary(state,startime,endtime,index){
		// 先去验证该日期下的日志能否报批
		st = startime;
		en = endtime;
		var state ="${workdiaryForm.state}";
		var yearnum=document.getElementById("yearnum").value;
		var monthnum=0;
		if(state==1){
			monthnum=document.getElementById("monthnum").value;
		}
		//if(navigator.appName=="Microsoft Internet Explorer"){
			var hashvo=new ParameterSet();
			hashvo.setValue("strvalue",index);		
			hashvo.setValue("yearnum",yearnum);
			hashvo.setValue("monthnum",monthnum);
			hashvo.setValue("state",state);
			var request=new Request({method:'post',asynchronous:true,onSuccess:checkAddIsOk,functionId:'9020010010'},hashvo);
		//}else{
		//	var map = new HashMap();
		//	map.put("strvalue",index);
		//	map.put("yearnum",yearnum);
		//	map.put("monthnum",monthnum);
		//	map.put("state",state);
		//	Rpc({functionId:'9020010010',success:checkAddIsOk},map);
		//}
	    /*workdiaryForm.action="/performance/workdiary/myworkdiaryshow.do?b_add=link&query=add&state="+state
	    			+"&startime="+startime+"&endtime="+endtime;
    	workdiaryForm.submit();*/
}
function checkAddIsOk(outparamters) {
	var check="";
	//if(navigator.appName=="Microsoft Internet Explorer"){
		check=outparamters.getValue("check");
	//}else{
	//	var map=JSON.parse(outparamters);
	//	if(map.succeed){
	//		check=map.check;
	//	}
	//}
	if(getDecodeStr(check)=='ok'){
		checkApp = 'ok';
	} else {
		checkApp = check;
	}
	workdiaryForm.action="/performance/workdiary/myworkdiaryshow.do?b_add=link&query=add&state=<%=state %>"
	    			+"&startime="+st+"&endtime="+en+"&checkApp="+checkApp;
    workdiaryForm.submit();
}
var ps = "";
function updatediary(p0100,desp0100){
		// 先去验证该日期下的日志能否报批
		var obj = document.getElementsByName(p0100+"")[0];
		var state ="${workdiaryForm.state}";
		var yearnum=document.getElementById("yearnum").value;
		var monthnum=0;
		if(state==1){
			monthnum=document.getElementById("monthnum").value;
		} 
		var strvalue = obj.value;
		ps = desp0100;
		var hashvo=new ParameterSet();			
		hashvo.setValue("strname",p0100);
		hashvo.setValue("strvalue",strvalue);		
		hashvo.setValue("yearnum",yearnum);
		hashvo.setValue("monthnum",monthnum);
		hashvo.setValue("state",state);
		var request=new Request({method:'post',asynchronous:true,onSuccess:checkIsOk,functionId:'9020010010'},hashvo);
}
function checkIsOk(outparamters) {
	var check="";
	check=outparamters.getValue("check");
	if(getDecodeStr(check)=='ok'){
		checkApp = 'ok';
	} else {
		checkApp = check;
	}
	workdiaryForm.action="/performance/workdiary/myworkdiaryshow.do?b_add=link&query=update&state=<%=state %>&p0100="+ps+"&checkApp="+checkApp;
    workdiaryForm.submit();
}
//-->
//bug 34772 下拉框和table边线重叠      wangb 20180209
var form = document.getElementsByName('workdiaryForm')[0];
var table1 = form.getElementsByTagName('table')[0];
table1.style.marginBottom ='4px';

</script>