<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.performance.commend.insupportcommend.InSupportCommendForm"%>
<script language="javascript" src="/js/dict.js"></script> 
<script type="text/javascript">
<!--
function outExcel(){
var tablename,table,whl_sql;           
		var hashvo=new ParameterSet();
        hashvo.setValue("tabname","${inSupportCommendForm.tabname}");
		hashvo.setValue("whl_sql","${inSupportCommendForm.sql}");
		hashvo.setValue("p0209","${inSupportCommendForm.p0209}");
		hashvo.setValue("p0201","${inSupportCommendForm.p0201}");
	    var In_paramters="flag=1"; 
	   	var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:showfile,functionId:'9010030022'},hashvo);
		
}
function showfile(outparamters){
var outName=outparamters.getValue("outName");
//xus 20/4/30 vfs改造
var win=open("/servlet/vfsservlet?fromjavafolder=true&fileid="+outName,"excel");
}
function selectCandidate(){
 var p0201=${inSupportCommendForm.p0201};
 var state=${inSupportCommendForm.state};
 if(state=="1"){
 alert("执行和结束状态的推荐计划不支持该操作!");
 return;
 }
 var returnObj=select_org_emp_dialog(1,1,0,0);
 var obj=new Object();
 if(returnObj !=null){
 obj.content=returnObj.content;
 obj.title=returnObj.title;
  var hashVo=new ParameterSet();
  hashVo.setValue("selectId",p0201);
  hashVo.setValue("content",obj.content);
   var In_parameters="flag=1";
   var request=new Request({method:'post',asynchronous:false,parameters:In_parameters,onSuccess:select_ok,functionId:'9010030014'},hashVo);			
}else{
return;
}
}
function select_ok(outparamters){
   inSupportCommendForm.submit();

 }
 function voteTj(){
 var p0201=${inSupportCommendForm.p0201};
 var have="${inSupportCommendForm.have}";
 if(have=='0'){
 alert("只能对执行中的推荐计划进行票数统计!");
 return;
 }
       var hashVo=new ParameterSet();
       hashVo.setValue("p0201",p0201);
        var In_parameters="flag=1";
        var request=new Request({method:'post',asynchronous:false,parameters:In_parameters,onSuccess:select_ok,functionId:'9010030015'},hashVo);			
}
function get_common_query(){
var p0201=${inSupportCommendForm.p0201};
var state=${inSupportCommendForm.state};
var pre="${inSupportCommendForm.privPre}";
 if(state=="1"){
 alert("执行和结束状态的推荐计划不支持该操作!");
 return;
 }
var obj=pre.split(",");
var dbpre_arr=new Array();
if(obj){
for(var j=0;j<obj.length;j++){
  dbpre_arr[j]=obj[j];
}
}else{
alert("没有在你权限范围内的应用人员库,不能查询");
return;
}
var objlist=common_query("1",dbpre_arr,"1");
		
		if(objlist&&objlist.length>0)
		{
		  for(var i=0;i<objlist.length;i++)
		  {
		    objlist[i]=objlist[i];
		  }
	 }
	 else{
	 return;
	 }
	 var hashVo=new ParameterSet();
	 hashVo.setValue("perList",objlist);
	 hashVo.setValue("p0201",p0201);
   var In_parameters="flag=1";
   var request=new Request({method:'post',asynchronous:false,parameters:In_parameters,onSuccess:select_ok,functionId:'9010030025'},hashVo);			
	 
}		

function del(){
if(confirm("确认操作，仅可以删除起草和暂停的推荐计划的候选人！")){
 var hashVo=new ParameterSet();
var tablename="table${inSupportCommendForm.tabname}";
var state="${inSupportCommendForm.state}";
if(state=='1'){
alert("执行和结束的推荐计划不能删除候选人");
return;
}
        table=$(tablename);
        dataset=table.getDataset();
	    var record=dataset.getFirstRecord();
	    var selectID="";	
	    var isUsed=0;	
	    var noNum=0;
	    
		while (record) 
		{
			
			if (record.getValue("select"))
			{							
						
						     selectID+=","+record.getValue("p0300");	
				        	    
			}
			record=record.getNextRecord();
		}  
	    if(selectID.length<0 ||selectID.length==0){
	    alert("请选择要删除的候选人");
	    return;
	    }
       if(selectID.length>0)
       {
       
        hashVo.setValue("selectID",selectID);
        var In_parameters="flag=1";
        var request=new Request({method:'post',asynchronous:false,parameters:In_parameters,onSuccess:refresh,functionId:'9010030021'},hashVo);			
       		 
       }
   }else{
 return;

}
}
function tablep03_b_onRefresh(cell, value, record)
 {
    //table+数据集＋“要加连接的字段”
    if(record!=null){
	   cell.innerHTML = "<a href=\"javascript:showScan('"+record.getValue("a0100")+"','"+record.getValue("nbase")+"');\" >" + "&nbsp;<img src='/images/view.gif' border=0/>&nbsp;"+ "</a >";
	   cell.align="center";
    }    
 }
function showScan(a0100,nbase){
	//显示票 
	var theurl="/performance/commend/insupportcommend/candidateVindicate.do?b_scan=link&p0201=${inSupportCommendForm.p0201}&nbase="+nbase+"&a0100="+a0100;
	var returnValue=window.showModalDialog(theurl,null, 
		        "dialogWidth:650px; dialogHeight:700px;resizable:yes;center:yes;scroll:no;status:no");
	if(returnValue!=null&&returnValue.scanArray!=null&&returnValue.scanArray.length>0){
		//scanFlag=1删除图像
		var In_paramters="scanFlag=1&scanArray="+returnValue.scanArray;
		var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:null,functionId:'9010030029'},null);
	}
}
function refresh(outparamters){
 document.location.reload();
 }
 function ret()
 {
   inSupportCommendForm.action = "/performance/commend/insupportcommend/initInSupportCommend.do?b_init=init";
   inSupportCommendForm.submit();
 }
 function autoserial(){
 	var theurl="/performance/commend/insupportcommend/candidateVindicate.do?b_auto=link";
	var returnValue=window.showModalDialog(theurl,null, 
		        "dialogWidth:440px; dialogHeight:250px;resizable:no;center:yes;scroll:no;status:no");
	if(returnValue!=null){
		document.getElementById("autonum").value=returnValue.split("`")[0];
		var In_parameters="p0201=${inSupportCommendForm.p0201}&autoNumber="+returnValue;
	    var request=new Request({method:'post',asynchronous:false,parameters:In_parameters,onSuccess:null,functionId:'9010030027'},null);
	    inSupportCommendForm.submit();
    }			
 } 
 function outcandidate(){
	var In_parameters="p0201=${inSupportCommendForm.p0201}&autonum="+$F("autonum");
	var request=new Request({method:'post',asynchronous:false,parameters:In_parameters,onSuccess:showoutfile,functionId:'9010030028'},null);
 }
function showoutfile(outparamters){
	if(outparamters!=null&&outparamters.getValue("outName")!=""){
		if(outparamters.getValue("outName")=="1"){
			alert("请先设置人员自动编号");
			return;
		}
		var outName=outparamters.getValue("outName");
		//xus 20/4/30 vfs改造
		var win=open("/servlet/vfsservlet?fromjavafolder=true&fileid="+outName,"excel");
	}
}
function showDateSelectBox(srcobj)
   {   
     Element.show('date_panel');   
      date_desc=srcobj;  
     var expr_editor=$('date_box');
       expr_editor.focus();
    
      for(var i=0;i<document.inSupportCommendForm.date_box.options.length;i++)
  	  {
  	  	document.inSupportCommendForm.date_box.options[i].selected=false;
  	  }
      var pos=getAbsPosition(srcobj);
	  with($('date_panel'))
	  {
	        style.position="absolute";
    		style.posLeft=pos[0]-1;
			style.posTop=pos[1]-1+srcobj.offsetHeight;
			style.width=75;
      }                 
      
   }
   function changeState()
   {

       Element.hide('date_panel');
	    var state;
	    for(var i=0;i<inSupportCommendForm.date_box.options.length;i++){
		    if(inSupportCommendForm.date_box.options[i].selected){
		    	state=inSupportCommendForm.date_box.options[i].value+"";
		    }
	    }
	    if(state=='01'){
	    	outExcel();
	    }
	    if(state=='02'){
	    	outcandidate();
		}  	
   }
//-->
</script>

<html:form action="/performance/commend/insupportcommend/candidateVindicate">
<html:hidden name="inSupportCommendForm" property="autonum"/>
<table width="95%" border="0" cellspacing="1"  align="center" cellpadding="1" id='tab'>
<tr>

<td>

<hrms:dataset name="inSupportCommendForm" property="candidateList" scope="session" setname="${inSupportCommendForm.tabname}"  setalias="p03_set" readonly="false" editable="true" select="true" sql="${inSupportCommendForm.sql}" buttons="movefirst,prevpage,nextpage,movelast">

       <hrms:commandbutton name="deleteall" functionId="" onclick="del();" refresh="true" type="all-change" setname="${inSupportCommendForm.tabname}">
	     	删除
	     </hrms:commandbutton>

	     <hrms:commandbutton name="saveall" functionId="9010030016" refresh="true" type="all-change" setname="${inSupportCommendForm.tabname}">
	     	保存
	     </hrms:commandbutton>
	      <hrms:commandbutton name="candidate" functionId="" onclick="selectCandidate();" refresh="true" type="all-change" setname="${inSupportCommendForm.tabname}">
	     	手工选人
	     </hrms:commandbutton>
	      <hrms:commandbutton name="query" functionId="" onclick="get_common_query();" refresh="true" type="all-change" setname="${inSupportCommendForm.tabname}">
	     	条件选人
	     </hrms:commandbutton>
	      <hrms:commandbutton name="vote" functionId="" onclick="voteTj();" refresh="true" type="selected" setname="${inSupportCommendForm.tabname}" >
	     	票数统计
	     </hrms:commandbutton>
	     <hrms:commandbutton name="outexcel" onclick="showDateSelectBox(this);" functionId="" refresh="true" type="all-change" setname="${inSupportCommendForm.tabname}" >
	     	导出Excel
	     </hrms:commandbutton>
	      <hrms:commandbutton name="autoserial" onclick="autoserial();" functionId="" refresh="true" type="all-change" setname="${inSupportCommendForm.tabname}" >
	     	编号设置
	     </hrms:commandbutton>
	     <hrms:commandbutton name="ret" onclick="ret();" functionId="" refresh="true" type="all-change" setname="${inSupportCommendForm.tabname}" >
	     返回
	     </hrms:commandbutton>
	</hrms:dataset>

 
</td>
</tr>
</table>
<div id="date_panel" >
	<select onblur="Element.hide('date_panel');" id="date_box" name="date_box" multiple="multiple" size="2"  style="width:95px;font-size: 14px;"  onchange="changeState();" onblur="Element.hide('date_panel');" >    
	  <option value="01">推荐情况表</option>
	  <option value="02">候选人名册</option>	
    </select>
</div>
         
<script language="javascript">
   Element.hide('date_panel');
</script>
</html:form>