<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hjsj.hrms.actionform.report.actuarial_report.edit_report.EditReport_actuaialForm,
				org.apache.commons.beanutils.LazyDynaBean,
				java.util.*" %>
				
<%
	EditReport_actuaialForm editReport_actuaialForm=(EditReport_actuaialForm)session.getAttribute("editReport_actuaialForm");
	String selfUnitcode=editReport_actuaialForm.getSelfUnitcode();
	String unitcode=editReport_actuaialForm.getUnitcode();
	String flag=editReport_actuaialForm.getFlag();
	String flagSub=editReport_actuaialForm.getFlagSub(); 
	String opt=editReport_actuaialForm.getOpt();
	String opt2=editReport_actuaialForm.getOpt2();
	String idstatus=editReport_actuaialForm.getIdstatus();
	String from_model=editReport_actuaialForm.getFrom_model();
	String subquerysql=editReport_actuaialForm.getSubquerysql();
	String rootUnit=editReport_actuaialForm.getRootUnit();
	String id =editReport_actuaialForm.getId();
	String kmethod = editReport_actuaialForm.getKmethod();
	String isCollectUnit = editReport_actuaialForm.getIsCollectUnit();
	//System.out.println("selfUnitcode:"+selfUnitcode+"unitcode:"+unitcode+"id:"+id+"flag:"+flag+"opt:"+opt+"opt2:"+opt2+"from_model:"+from_model+"subquerysql:"+subquerysql+"rootUnit:"+rootUnit+"idstatus:"+idstatus);
	
%>			
				
				
				
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/common.js"></script>
<script language="javascript" src="/js/dict.js"></script> 
<script language="JavaScript">




function add(unitcode,id,Report_id) 
{ 
  var theurl="/report/actuarial_report/edit_report/editreportU02List.do?b_query=link&unitcode="+unitcode+"&id="+id+"&report_id="+Report_id;
  var iframe_url="/general/query/common/iframe_query.jsp?src="+theurl;
  var return_vo= window.showModalDialog(iframe_url, 'trainClass_win1', 
  					/* 精算报表-编辑报表-新增-滚动条设置为自动 xiaoyun 2014-6-30 start */
  					"dialogWidth:750px; dialogHeight:460px;resizable:no;center:yes;scroll:auto;status:no");
      				//"dialogWidth:750px; dialogHeight:460px;resizable:no;center:yes;scroll:yes;status:yes");
      				/* 精算报表-编辑报表-新增-滚动条设置为自动 xiaoyun 2014-6-30 end */		
  if(return_vo)
     if(return_vo.flag=="true")
     {
        document.editReport_actuaialForm.action="/report/actuarial_report/edit_report/searcheportU02List.do?b_query=init";
  	    document.editReport_actuaialForm.submit();
     }
} 
function query(unitcode,id,Report_id) 
{
  var theurl="/report/actuarial_report/edit_report/editreportU02List.do?b_search=link&unitcode="+unitcode+"&id="+id+"&report_id="+Report_id;
  var iframe_url="/general/query/common/iframe_query.jsp?src="+theurl;
  var return_vo= window.showModalDialog(iframe_url, 'trainClass_win1', 
  					/* 精算报表-编辑报表-操作-查询 去掉滚动条 xiaoyun 2014-6-30 start */
  					"dialogWidth:750px; dialogHeight:480px;resizable:no;center:yes;scroll:no;status:no");
      				//"dialogWidth:740px; dialogHeight:460px;resizable:no;center:yes;scroll:yes;status:yes");
      				/* 精算报表-编辑报表-操作-查询 去掉滚动条 xiaoyun 2014-6-30 end */		
  if(return_vo){
        document.editReport_actuaialForm.action="/report/actuarial_report/edit_report/searcheportU02List.do?b_query=init&subquerysql="+return_vo;
  	    document.editReport_actuaialForm.submit();
  	    }
     
}
function query2(unitcode,id,Report_id) 
{ 
  var theurl="/report/actuarial_report/edit_report/editreportU02List.do?b_search=link&unitcode="+unitcode+"&id="+id+"&report_id="+Report_id;

        document.editReport_actuaialForm.action="/report/actuarial_report/edit_report/searcheportU02List.do?b_query=init&subquerysql=";
  	    document.editReport_actuaialForm.submit();
  	    
     
}
//导出摸板
function downLoadTemp(unitcode,id,report_id)
{	
	var hashvo=new ParameterSet();	
	hashvo.setValue("unitcode",unitcode);
	hashvo.setValue("id",id);
	hashvo.setValue("report_id",report_id);
	hashvo.setValue("sqlStr",getEncodeStr("${editReport_actuaialForm.sql}"));;
	var request=new Request({method:'post',asynchronous:false,onSuccess:showfile1,functionId:'03060000210'},hashvo);
}
function showfile1(outparamters)
{
	var outName=outparamters.getValue("outName");
	outName=getDecodeStr(outName);
	//var name=outName.substring(0,outName.length-1)+".xls";
	//name=getEncodeStr(name);
	window.location.target="_blank";
	window.location.href = "/servlet/vfsservlet?fromjavafolder=true&fileid="+outName;	
}
function importTalbe()
{
   document.editReport_actuaialForm.action="/report/actuarial_report/edit_report/editreportU02List.do?br_import=init";
  	document.editReport_actuaialForm.submit();
}
function importTalbe2()
{
   document.editReport_actuaialForm.action="/report/actuarial_report/edit_report/editreportU02List.do?br_import=init&updatehistory=updatehistory";
  	document.editReport_actuaialForm.submit();
}
 function returnq(opt,from_model)
  {
    if(from_model=="collect")
    {
     var opt = '${editReport_actuaialForm.opt2}';
     var selfUnitcode = '${editReport_actuaialForm.selfUnitcode}';
    var unitcode = '${editReport_actuaialForm.unitcode}';
    	if("1"=="${editReport_actuaialForm.rootUnit}"&&selfUnitcode==unitcode){
       editReport_actuaialForm.action="/report/actuarial_report/report_collect.do?b_query=link&a_code=${editReport_actuaialForm.unitcode}&opt="+opt;
       }else{
       editReport_actuaialForm.action="/report/actuarial_report/report_collect.do?b_query=link&a_code=${editReport_actuaialForm.unitcode}&opt="+opt;
       }
       editReport_actuaialForm.submit();
    }else
    {
      editReport_actuaialForm.action="/report/actuarial_report/edit_report/editreportlist.do?b_query=link&opt=1";
      editReport_actuaialForm.submit();
    }
     
  }
  
  function sub()
  {
  	if(confirm("提交报表后将不能再填写数据，请确认执行提交操作!"))
  	{
  		document.editReport_actuaialForm.action="/report/actuarial_report/edit_report/editreportU02List.do?b_sub=sub";
  		document.editReport_actuaialForm.submit();
  	}
  }
  function exportExcel()
  {
     // var target_url;      
     // target_url="/report/actuarial_report/edit_report/searcheportU02List.do?br_grad=link";
     //  var return_vo= window.showModalDialog(target_url,1, 
     //   "dialogWidth:340px; dialogHeight:200px;resizable:no;center:yes;scroll:yes;status:yes;scrollbars:yes");
     //  if(return_vo)
	 //  {
	      var hashvo=new ParameterSet();	
	      hashvo.setValue("unitcode",'${editReport_actuaialForm.unitcode}');
	      hashvo.setValue("id",'${editReport_actuaialForm.id}');
	      hashvo.setValue("report_id",'${editReport_actuaialForm.report_id}');
	      hashvo.setValue("sqlStr",getEncodeStr("${editReport_actuaialForm.sql}"));
	    //  hashvo.setValue("grad",return_vo.value);
	      var request=new Request({method:'post',asynchronous:false,onSuccess:showfile1,functionId:'03060000217'},hashvo);
	   //}
  }
  function defectData()
  {
      editReport_actuaialForm.action="/report/actuarial_report/edit_report/searcheportU02List.do?b_query=link&opt=x&unitcode=${editReport_actuaialForm.unitcode}&id=${editReport_actuaialForm.id}&report_id=${editReport_actuaialForm.report_id}";
      editReport_actuaialForm.submit();
  }
  function returnData(from_model)
  {
     if(from_model=="collect")
    {
    var selfUnitcode = '${editReport_actuaialForm.selfUnitcode}';
    var unitcode = '${editReport_actuaialForm.unitcode}';
    var opt = '${editReport_actuaialForm.opt2}';
	   if(selfUnitcode!=unitcode&&'${editReport_actuaialForm.flag}'=="1"){
	   editReport_actuaialForm.action="/report/actuarial_report/edit_report/searcheportU02List.do?b_query=link&opt="+opt+"&from_model=collect&unitcode=${editReport_actuaialForm.unitcode}&id=${editReport_actuaialForm.id}&report_id=${editReport_actuaialForm.report_id}";
	   }else{
	   if("1"=="${editReport_actuaialForm.rootUnit}"&&selfUnitcode==unitcode){
	   editReport_actuaialForm.action="/report/actuarial_report/edit_report/searcheportU02List.do?b_query=link&opt="+opt+"&from_model=collect&unitcode=${editReport_actuaialForm.unitcode}&id=${editReport_actuaialForm.id}&report_id=${editReport_actuaialForm.report_id}";
	   }else{
	   editReport_actuaialForm.action="/report/actuarial_report/edit_report/searcheportU02List.do?b_query=link&opt="+opt+"&from_model=collect&unitcode=${editReport_actuaialForm.unitcode}&id=${editReport_actuaialForm.id}&report_id=${editReport_actuaialForm.report_id}";
	   }
	   }
	   					
         
      editReport_actuaialForm.submit();
    }else
    {
   		  editReport_actuaialForm.action="/report/actuarial_report/edit_report/searcheportU02List.do?b_query=link&opt=1&from_model=edit&unitcode=${editReport_actuaialForm.unitcode}&id=${editReport_actuaialForm.id}&report_id=${editReport_actuaialForm.report_id}";
      editReport_actuaialForm.submit();
    }
     
  }
  function rejectData(Report_id)
{
    //document.getElementsByName("report_id")[0].value=Report_id;
  document.editReport_actuaialForm.action="/report/actuarial_report/edit_report/editreportU02List.do?br_import2=init";
  	document.editReport_actuaialForm.submit();
  
}
function remove(){

var n=0;
var tablename="tableU02";
var unitcodes=",";
var u0200s=",";
        table=$(tablename);
        dataset=table.getDataset();
	    var record=dataset.getFirstRecord();
		var selectID="";
		while (record){
		if(record.getValue("select")){
		n++;
		
		if(unitcodes.indexOf(","+record.getValue("unitcode")+",")==-1)
		unitcodes+=record.getValue("unitcode")+",";
		if(u0200s.indexOf(","+record.getValue("u0200")+",")==-1)
		u0200s+=record.getValue("u0200")+",";
		}
		
		record=record.getNextRecord();
		} 
		if(n=='0'){
		alert("没有选择人员！");
		return;
		}
		var strurl="/report/actuarial_report/edit_report/searcheportU02List.do?br_cancelUnit=link";
		var iframe_url="/general/query/common/iframe_query.jsp?src="+strurl; 
		var goalunit=window.showModalDialog(iframe_url,arguments,"dialogWidth=460px;dialogHeight=200px;resizable=yes;scroll=no;status=no;");  
		if(goalunit)
		{
			 var hashvo=new ParameterSet();
			 hashvo.setValue("unitcodes",unitcodes);
			 hashvo.setValue("id",'${editReport_actuaialForm.id}');
			 hashvo.setValue("theyear",'${editReport_actuaialForm.theyear}');
			  hashvo.setValue("kmethod",'${editReport_actuaialForm.kmethod}');
			 hashvo.setValue("personstate",goalunit[0]);
			 hashvo.setValue("unitcode",goalunit[1]);
			  hashvo.setValue("u0200s",u0200s);
			  hashvo.setValue("report_id",'${editReport_actuaialForm.report_id}');
		
			 var request=new Request({asynchronous:false,onSuccess:showlist,functionId:'03060000239'},hashvo);
		}		
   		
}  
function remove2(){

var n=0;
var tablename="tableU02";
var unitcodes=",";
var u0200s=",";
        table=$(tablename);
        dataset=table.getDataset();
	    var record=dataset.getFirstRecord();
		var selectID="";
		while (record){
		if(record.getValue("select")){
		n++;
		if(unitcodes.indexOf(","+record.getValue("unitcode")+",")==-1)
		unitcodes+=record.getValue("unitcode")+",";
		if(u0200s.indexOf(","+record.getValue("u0200")+",")==-1)
		u0200s+=record.getValue("u0200")+",";
		}
		record=record.getNextRecord();
		} 
		if(n=='0'){
		alert("没有选择人员！");
		return;
		}
		var strurl="/report/actuarial_report/edit_report/searcheportU02List.do?br_cancelBase=link";
		var iframe_url="/general/query/common/iframe_query.jsp?src="+strurl; 
		var goalunit=window.showModalDialog(iframe_url,arguments,"dialogWidth=460px;dialogHeight=200px;resizable=yes;scroll=no;status=no;");  
		if(goalunit)
		{
			 var hashvo=new ParameterSet();
			 hashvo.setValue("unitcodes",unitcodes);
			 hashvo.setValue("id",'${editReport_actuaialForm.id}');
			 hashvo.setValue("theyear",'${editReport_actuaialForm.theyear}');
			 hashvo.setValue("kmethod",'${editReport_actuaialForm.kmethod}');
			 hashvo.setValue("personstate",goalunit[0]);
			 hashvo.setValue("u02base",goalunit[1]);
			 hashvo.setValue("u0200s",u0200s);
			 hashvo.setValue("report_id",'${editReport_actuaialForm.report_id}');
			 var request=new Request({asynchronous:false,onSuccess:showlist,functionId:'03060000239'},hashvo);
		}		
   		
}  
 function showlist(outparamters)
   {
   		
   		 document.editReport_actuaialForm.action="/report/actuarial_report/edit_report/searcheportU02List.do?b_query=init";
  	    document.editReport_actuaialForm.submit();
   }
</script>
<hrms:themes />
<style type="text/css"> 

.selectPre{
	position:absolute;
    left:800px;
    top:1px;
    z-index: 10;
}

</style>
<html>
  <body>
  <html:form action="/report/actuarial_report/edit_report/searcheportU02List">
    <html:hidden name="editReport_actuaialForm" property="id"/>  
	<html:hidden name="editReport_actuaialForm" property="unitcode"/>  
	<html:hidden name="editReport_actuaialForm" property="report_id"/>
	<table id="selectprename"  class="selectPre"   ><tr>
<td nowrap >
&nbsp;&nbsp;
	<logic:equal name="editReport_actuaialForm" property="report_id" value="U02_1">
		表2-1离休人员
	</logic:equal>
	<logic:equal name="editReport_actuaialForm" property="report_id" value="U02_2">
		表2-2退休人员
	</logic:equal>
	<logic:equal name="editReport_actuaialForm" property="report_id" value="U02_3">
		表2-3内退人员
	</logic:equal>
	<logic:equal name="editReport_actuaialForm" property="report_id" value="U02_4">
	    表2-4遗属
	</logic:equal>	
</td></tr></table>
<table width="90%" border="0" cellpadding="1" cellspacing="0" align="center">

<tr><td class="RecordRowTable">
<logic:notEqual name="editReport_actuaialForm" property="opt" value="0">     
<logic:notEqual name="editReport_actuaialForm" property="opt" value="x"> 
<logic:equal name="editReport_actuaialForm" property="flag" value="0"> 
   <hrms:dataset name="editReport_actuaialForm" property="fieldlistU02" scope="session" setname="U02" setalias="data_table"  select="true" 
sql="${editReport_actuaialForm.sql}" editable="true"  rowlock="true" rowlockfield="editflag"  rowlockvalues=",1,2," pagerows="${editReport_actuaialForm.pagerows}" buttons="bottom">

	<hrms:commandbutton name="table" hint="" functionId="" refresh="true" function_id=""  type="selected" setname="U02" onclick="add('${editReport_actuaialForm.unitcode}','${editReport_actuaialForm.id}','${editReport_actuaialForm.report_id}');">
     <bean:message key="button.insert"/>
    </hrms:commandbutton>
	<hrms:commandbutton name="savedata"  functionId="03060000208" refresh="true" type="all-change" setname="U02" function_id="">
		<bean:message key="button.save"/>
	</hrms:commandbutton>  
	<hrms:commandbutton name="delselected" hint="" functionId="03060000209" refresh="true" function_id="" type="selected" setname="U02">
     <bean:message key="button.delete"/>
   </hrms:commandbutton> 
   <hrms:commandbutton name="table2" hint="" functionId="" refresh="true" function_id=""  type="selected" setname="U02" onclick="query('${editReport_actuaialForm.unitcode}','${editReport_actuaialForm.id}','${editReport_actuaialForm.report_id}');">
     <bean:message key="button.query"/>
    </hrms:commandbutton>
     <%if(subquerysql!=null&&!subquerysql.equals("")){ %>
    <hrms:commandbutton name="table3" hint="" functionId="" refresh="true" function_id=""  type="selected" setname="U02" onclick="query2('${editReport_actuaialForm.unitcode}','${editReport_actuaialForm.id}','${editReport_actuaialForm.report_id}');">
     <bean:message key="workdiary.message.view.all.infor"/>
    </hrms:commandbutton>
   <%} %>
   <hrms:commandbutton name="download" hint="" functionId="" refresh="true" function_id=""  type="selected" setname="U02" onclick="downLoadTemp('${editReport_actuaialForm.unitcode}','${editReport_actuaialForm.id}','${editReport_actuaialForm.report_id}');">
     <bean:message key="button.download.template"/>
    </hrms:commandbutton>  
    <hrms:commandbutton name="importTalbe" hint="" functionId="" refresh="true" function_id=""  type="all-change" setname="U02" onclick="importTalbe();">
     <bean:message key="import.tempData"/>
    </hrms:commandbutton>  
  
     <hrms:commandbutton name="mitem3" hint="" functionId="" refresh="true" function_id=""  type="selected" setname="U02" onclick="exportExcel();">
     <bean:message key="report.actuarial_report.exportExcel"/>
    </hrms:commandbutton> 
    
 
 <%if(!flag.equals("1")&&kmethod.equals("0")&&idstatus.equals("04")) {%>
   <%if(!"0".equals(isCollectUnit)) {%>
    <hrms:commandbutton name="remove" hint=""  refresh="true" function_id="29060301"  type="selected" setname="U02" onclick="remove();">
       人员划转
    </hrms:commandbutton>
    <%} %>
    <%if("0".equals(isCollectUnit)) {%>
    <hrms:commandbutton name="remove2" hint=""  refresh="true" function_id="29060202,29060302"  type="selected" setname="U02" onclick="remove2();">
       人员移库
    </hrms:commandbutton>
      <%} %>
    <%} %>
      <hrms:commandbutton name="mitem2" hint="" functionId="" refresh="true" function_id=""  type="selected" setname="U02" onclick="defectData();">
     <bean:message key="report.actuarial_report.defectData"/>
    </hrms:commandbutton> 
    <hrms:commandbutton name="return" hint="" functionId="" refresh="true" function_id=""  type="selected" setname="U02" onclick="returnq('${editReport_actuaialForm.opt}','${editReport_actuaialForm.from_model}');">
         <bean:message key="button.return"/>
    </hrms:commandbutton>
    
</hrms:dataset>    
</logic:equal> 
<logic:equal name="editReport_actuaialForm" property="flag" value="-1"> 
   <hrms:dataset name="editReport_actuaialForm" property="fieldlistU02" scope="session" setname="U02" setalias="data_table"  select="true" 
sql="${editReport_actuaialForm.sql}" editable="true"  rowlock="true" rowlockfield="editflag"  rowlockvalues=",1,2," pagerows="${editReport_actuaialForm.pagerows}" buttons="bottom">


	<hrms:commandbutton name="table" hint="" functionId="" refresh="true" function_id=""  type="selected" setname="U02" onclick="add('${editReport_actuaialForm.unitcode}','${editReport_actuaialForm.id}','${editReport_actuaialForm.report_id}');">
     <bean:message key="button.insert"/>
    </hrms:commandbutton>
	<hrms:commandbutton name="savedata"  functionId="03060000208" refresh="true" type="all-change" setname="U02" function_id="">
		<bean:message key="button.save"/>
	</hrms:commandbutton>  
	<hrms:commandbutton name="delselected" hint="" functionId="03060000209" refresh="true" function_id="" type="selected" setname="U02">
     <bean:message key="button.delete"/>
   </hrms:commandbutton> 
  <hrms:commandbutton name="table2" hint="" functionId="" refresh="true" function_id=""  type="selected" setname="U02" onclick="query('${editReport_actuaialForm.unitcode}','${editReport_actuaialForm.id}','${editReport_actuaialForm.report_id}');">
     <bean:message key="button.query"/>
    </hrms:commandbutton>
     <%if(subquerysql!=null&&!subquerysql.equals("")){ %>
    <hrms:commandbutton name="table3" hint="" functionId="" refresh="true" function_id=""  type="selected" setname="U02" onclick="query2('${editReport_actuaialForm.unitcode}','${editReport_actuaialForm.id}','${editReport_actuaialForm.report_id}');">
     <bean:message key="workdiary.message.view.all.infor"/>
    </hrms:commandbutton>
   <%} %>
   <hrms:commandbutton name="download" hint="" functionId="" refresh="true" function_id=""  type="selected" setname="U02" onclick="downLoadTemp('${editReport_actuaialForm.unitcode}','${editReport_actuaialForm.id}','${editReport_actuaialForm.report_id}');">
     <bean:message key="button.download.template"/>
    </hrms:commandbutton>  
    <hrms:commandbutton name="importTalbe" hint="" functionId="" refresh="true" function_id=""  type="all-change" setname="U02" onclick="importTalbe();">
     <bean:message key="import.tempData"/>
    </hrms:commandbutton>   
        
  <hrms:commandbutton name="mitem3" hint="" functionId="" refresh="true" function_id=""  type="selected" setname="U02" onclick="exportExcel();">
     <bean:message key="report.actuarial_report.exportExcel"/>
    </hrms:commandbutton> 
    
  <%if(!flag.equals("1")&&kmethod.equals("0")&&idstatus.equals("04")) {%>
    <%if(!"0".equals(isCollectUnit)) {%>
    <hrms:commandbutton name="remove" hint=""  refresh="true" function_id="29060301"  type="selected" setname="U02" onclick="remove();">
       人员划转
    </hrms:commandbutton>
    <%} %>
     <%if("0".equals(isCollectUnit)) {%>
    <hrms:commandbutton name="remove2" hint=""  refresh="true" function_id="29060202,29060302"  type="selected" setname="U02" onclick="remove2();">
       人员移库
    </hrms:commandbutton>
      <%} %>
    <%} %>
    <hrms:commandbutton name="mitem2" hint="" functionId="" refresh="true" function_id=""  type="selected" setname="U02" onclick="defectData();">
     <bean:message key="report.actuarial_report.defectData"/>
    </hrms:commandbutton>
    <hrms:commandbutton name="return" hint="" functionId="" refresh="true" function_id=""  type="selected" setname="U02" onclick="returnq('${editReport_actuaialForm.opt}','${editReport_actuaialForm.from_model}');">
         <bean:message key="button.return"/>
    </hrms:commandbutton>
   
</hrms:dataset>    
</logic:equal> 
<logic:notEqual name="editReport_actuaialForm" property="flag" value="0"> 
 <logic:notEqual name="editReport_actuaialForm" property="flag" value="-1"> 
   <hrms:dataset name="editReport_actuaialForm" property="fieldlistU02" scope="session" setname="U02" setalias="data_table"  select="true" 
sql="${editReport_actuaialForm.sql}" editable="true"  rowlock="true" rowlockfield="editflag"  rowlockvalues=",1,2," pagerows="${editReport_actuaialForm.pagerows}" buttons="bottom">

	<% 
	if(flag.equals("1")&&
			from_model.equals("collect")&&
			((!unitcode.equals(selfUnitcode)&&!flagSub.equals("1"))||rootUnit.equals("1"))
			&&idstatus.equals("04")){ %>
	 
	  <hrms:commandbutton name="reject" hint="" refresh="true" function_id="29060303"  type="selected" setname="U02" onclick="rejectData('${editReport_actuaialForm.report_id}');">
	         <bean:message key="button.reject"/>
	    </hrms:commandbutton>
	<% } %>
	<hrms:commandbutton name="table2" hint="" functionId="" refresh="true" function_id=""  type="selected" setname="U02" onclick="query('${editReport_actuaialForm.unitcode}','${editReport_actuaialForm.id}','${editReport_actuaialForm.report_id}');">
     <bean:message key="button.query"/>
    </hrms:commandbutton>
      <%if(subquerysql!=null&&!subquerysql.equals("")){ %>
    <hrms:commandbutton name="table3" hint="" functionId="" refresh="true" function_id=""  type="selected" setname="U02" onclick="query2('${editReport_actuaialForm.unitcode}','${editReport_actuaialForm.id}','${editReport_actuaialForm.report_id}');">
     <bean:message key="workdiary.message.view.all.infor"/>
    </hrms:commandbutton>
   <%} %>
	   
	  <hrms:commandbutton name="mitem3" hint="" functionId="" refresh="true" function_id=""  type="selected" setname="U02" onclick="exportExcel();">
     <bean:message key="report.actuarial_report.exportExcel"/>
    </hrms:commandbutton> 
    
    <%if(!flag.equals("1")&&kmethod.equals("0")&&idstatus.equals("04")) {%>
     <%if(!"0".equals(isCollectUnit)) {%>
    <hrms:commandbutton name="remove" hint=""  refresh="true" function_id="29060301"  type="selected" setname="U02" onclick="remove();">
       人员划转
    </hrms:commandbutton>
    <%} %>
    <%if("0".equals(isCollectUnit)) {%>
    <hrms:commandbutton name="remove2" hint=""  refresh="true" function_id="29060202,29060302"  type="selected" setname="U02" onclick="remove2();">
       人员移库
    </hrms:commandbutton>
      <%} %>
    <%} %>
        <%if(!idstatus.equals("04")) {%>
    <hrms:commandbutton name="importTalbe" hint=""  refresh="true" function_id="29060304"  type="all-change" setname="U02" onclick="importTalbe2();">
     <bean:message key="import.history.tempData"/>
    </hrms:commandbutton>  
      <%} %>
     <hrms:commandbutton name="mitem2" hint="" functionId="" refresh="true" function_id=""  type="selected" setname="U02" onclick="defectData();">
     <bean:message key="report.actuarial_report.defectData"/>
    </hrms:commandbutton>
     <hrms:commandbutton name="return" hint="" functionId="" refresh="true" function_id=""  type="selected" setname="U02" onclick="returnq('${editReport_actuaialForm.opt}','${editReport_actuaialForm.from_model}');;">
         <bean:message key="button.return"/> 
    </hrms:commandbutton>
   
</hrms:dataset>   
</logic:notEqual> 


</logic:notEqual> 
</logic:notEqual>
<logic:equal name="editReport_actuaialForm" property="opt" value="x"> 
   <hrms:dataset name="editReport_actuaialForm" property="fieldlistU02" scope="session" setname="U02" setalias="data_table"  select="true" 
sql="${editReport_actuaialForm.sql}" editable="true"  rowlock="true" rowlockfield="editflag"  rowlockvalues=",1,2," pagerows="${editReport_actuaialForm.pagerows}" buttons="bottom">
<% if(flag.equals("2")&&unitcode.equals(selfUnitcode)&&!from_model.equals("collect")&&!rootUnit.equals("1")){ %>
	 <hrms:commandbutton name="table" hint="" functionId="" refresh="true" function_id=""  type="selected" setname="U02" onclick="add('${editReport_actuaialForm.unitcode}','${editReport_actuaialForm.id}','${editReport_actuaialForm.report_id}');">
     <bean:message key="button.insert"/>
    </hrms:commandbutton>
     <hrms:commandbutton name="savedata"  functionId="03060000208" refresh="true" type="all-change" setname="U02" function_id="">
		<bean:message key="button.save"/>
	</hrms:commandbutton>  
	<hrms:commandbutton name="delselected" hint="" functionId="03060000209" refresh="true" function_id="" type="selected" setname="U02">
     <bean:message key="button.delete"/>
   </hrms:commandbutton> 
    <hrms:commandbutton name="download" hint="" functionId="" refresh="true" function_id=""  type="selected" setname="U02" onclick="downLoadTemp('${editReport_actuaialForm.unitcode}','${editReport_actuaialForm.id}','${editReport_actuaialForm.report_id}');">
     <bean:message key="button.download.template"/>
    </hrms:commandbutton>  
    <hrms:commandbutton name="importTalbe" hint="" functionId="" refresh="true" function_id=""  type="all-change" setname="U02" onclick="importTalbe();">
     <bean:message key="import.tempData"/>
    </hrms:commandbutton>  
<%} %>
<hrms:commandbutton name="table2" hint="" functionId="" refresh="true" function_id=""  type="selected" setname="U02" onclick="query('${editReport_actuaialForm.unitcode}','${editReport_actuaialForm.id}','${editReport_actuaialForm.report_id}');">
     <bean:message key="button.query"/>
    </hrms:commandbutton>
     <%if(subquerysql!=null&&!subquerysql.equals("")){ %>
    <hrms:commandbutton name="table3" hint="" functionId="" refresh="true" function_id=""  type="selected" setname="U02" onclick="query2('${editReport_actuaialForm.unitcode}','${editReport_actuaialForm.id}','${editReport_actuaialForm.report_id}');">
     <bean:message key="workdiary.message.view.all.infor"/>
    </hrms:commandbutton>
   <%} %>
  <hrms:commandbutton name="mitem3" hint="" functionId="" refresh="true" function_id=""  type="selected" setname="U02" onclick="exportExcel();">
    <bean:message key="report.actuarial_report.exportExcel"/>
    </hrms:commandbutton> 
    
  <%if(!flag.equals("1")&&kmethod.equals("0")&&idstatus.equals("04")) {%>
    <%if(!"0".equals(isCollectUnit)) {%>
    <hrms:commandbutton name="remove" hint=""  refresh="true" function_id="29060301"  type="selected" setname="U02" onclick="remove();">
       人员划转
    </hrms:commandbutton>
    <%} %>
    <%if("0".equals(isCollectUnit)) {%>
    <hrms:commandbutton name="remove2" hint=""  refresh="true" function_id="29060202,29060302"  type="selected" setname="U02" onclick="remove2();">
       人员移库
    </hrms:commandbutton>
      <%} %>
    <%} %>
     <hrms:commandbutton name="return" hint="" functionId="" refresh="true" function_id=""  type="selected" setname="U02" onclick="returnData('${editReport_actuaialForm.from_model}');">
         <bean:message key="button.return"/>
    </hrms:commandbutton>
   
</hrms:dataset>  
</logic:equal>
</logic:notEqual>
<logic:equal name="editReport_actuaialForm" property="opt" value="0">    

<hrms:dataset name="editReport_actuaialForm" property="fieldlistU02" scope="session" setname="U02" setalias="data_table"  select="true" 
sql="${editReport_actuaialForm.sql}" editable="true"  rowlock="true" rowlockfield="editflag"  rowlockvalues=""  pagerows="${editReport_actuaialForm.pagerows}" buttons="bottom">
	      
	 <hrms:commandbutton name="table2" hint="" functionId="" refresh="true" function_id=""  type="selected" setname="U02" onclick="query('${editReport_actuaialForm.unitcode}','${editReport_actuaialForm.id}','${editReport_actuaialForm.report_id}');">
     <bean:message key="button.query"/>
    </hrms:commandbutton>
 <%if(subquerysql!=null&&!subquerysql.equals("")){ %>
    <hrms:commandbutton name="table3" hint="" functionId="" refresh="true" function_id=""  type="selected" setname="U02" onclick="query2('${editReport_actuaialForm.unitcode}','${editReport_actuaialForm.id}','${editReport_actuaialForm.report_id}');">
     <bean:message key="workdiary.message.view.all.infor"/>
    </hrms:commandbutton>
   <%} %>     
	      
	  <hrms:commandbutton name="mitem3" hint="" functionId="" refresh="true" function_id=""  type="selected" setname="U02" onclick="exportExcel();">
    <bean:message key="report.actuarial_report.exportExcel"/>
</hrms:commandbutton> 

   <%if(!flag.equals("1")&&kmethod.equals("0")&&idstatus.equals("04")) {%>
     <%if(!"0".equals(isCollectUnit)) {%>
    <hrms:commandbutton name="remove" hint=""  refresh="true" function_id="29060301"  type="selected" setname="U02" onclick="remove();">
       人员划转
    </hrms:commandbutton>
    <%} %>
    <%if("0".equals(isCollectUnit)) {%>
    <hrms:commandbutton name="remove2" hint=""  refresh="true" function_id="29060202,29060302"  type="selected" setname="U02" onclick="remove2();">
       人员移库
    </hrms:commandbutton>
      <%} %>
    <%} %>
        <%if(!idstatus.equals("04")) {%>
    <hrms:commandbutton name="importTalbe" hint=""  refresh="true" function_id="29060304"  type="all-change" setname="U02" onclick="importTalbe2();">
     <bean:message key="import.history.tempData"/>
    </hrms:commandbutton>  
      <%} %>
<hrms:commandbutton name="mitem2" hint="" functionId="" refresh="true" function_id=""  type="selected" setname="U02" onclick="defectData();">
     <bean:message key="report.actuarial_report.defectData"/>
    </hrms:commandbutton>
    <hrms:commandbutton name="return" hint="" functionId="" refresh="true" function_id=""  type="selected" setname="U02" onclick="returnq('${editReport_actuaialForm.opt}','${editReport_actuaialForm.from_model}');">
         <bean:message key="button.return"/>
    </hrms:commandbutton>
    
</hrms:dataset>


</logic:equal>




        </td>
      </tr>
      <tr>
       <td>
         
       </td>
      </tr>
    </table>
  </html:form>
  </body>
</html>
