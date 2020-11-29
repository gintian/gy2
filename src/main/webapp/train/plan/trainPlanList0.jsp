<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.train.plan.TrainMovementForm"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@page import="com.hrms.struts.constant.WebConstant"%> 
<script language="JavaScript" src="/js/common.js"></script>
<script language="javascript" src="/js/dict.js"></script> 
<%
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	response.setHeader("Pragma", "No-cache");
 	response.setHeader("Cache-Control", "no-cache");
 	response.setDateHeader("Expires", 0);
%>
<style type="text/css"> 
.selectPre{
	position:absolute;
    left:230px;
    top:25px;
}
</style>
<script language="javascript">
/*	function display(state,timeFlag)
	{
		document.trainMovementForm.action="/train/plan/searchCreatPlanList.do?b_query=link&model=1";
	    document.trainMovementForm.submit();
	}
*/
var rId;

function checkTime(times){
 	var result=times.match(/^(\d{1,4})(-|\/)(\d{1,2})\2(\d{1,2})$/);
 	if(result==null) return false;
 	var d= new Date(result[1], result[3]-1, result[4]);
 	return (d.getFullYear()==result[1]&&(d.getMonth()+1)==result[3]&&d.getDate());
}
function timeCheck(obj){
	if(!checkTime(obj.value)){
		obj.value='';
	}
}
	function setPageFormat()
   {
   		
   		var param_vo=oageoptions_selete("3","${trainMovementForm.username}");
   
   }

	function subReFlesh()
	{
		document.trainMovementForm.action="/train/plan/trainPlanList0.do?b_query0=link&model=1";
	    document.trainMovementForm.submit();
	}
	
   //输出 EXCEL OR PDF
    function showfile(outparamters)
	{
		var outName=outparamters.getValue("outName");
		var flag=outparamters.getValue("flag");
		if(flag==1)
			var win=open("/servlet/vfsservlet?fromjavafolder=true&fileid="+outName,"pdf");
		else
		{
			window.location.target="_blank";
			window.location.href="/servlet/vfsservlet?fromjavafolder=true&fileid="+outName;
		}
		
	}
	function timeFlagChange(obj){
		var timeflag = obj.value;
		if(timeflag=='5'){
			toggles("viewtime");
			toggles("viewtime1");
			return false;
		}else{
			document.getElementById("startTime").value="";
			document.getElementById("endTime").value="";
			subReFlesh();
		}
	}
	/*
	*flag 1:pdf  2:excel
	*/
	function executeOutFile(flag)
	{
	
		
		var tablename,table,size,fieldWidths,whl_sql;       
        tablename="table${trainMovementForm.tablename}";
        table=$(tablename);
        size=${trainMovementForm.fieldSize};
       
   		for(var i=1;i<size+2;i++)
        {
        	var width=table.getColWidth(table,i);
        	if(width!=null)
	        	fieldWidths+="/"+width;
        }
       
		var hashvo=new ParameterSet();
		hashvo.setValue("fieldWidths",fieldWidths.substring(10));
		hashvo.setValue("tablename","${trainMovementForm.tablename}");
	    var In_paramters="flag="+flag;  
	   	var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:showfile,functionId:'0521010011'},hashvo);
		
	}

	function display(var1,var2)
	{
		if(var1=='time')
		{
			document.trainMovementForm.timeFlag.value=var2;
			if(var2=='5')
			{
				var var3=selectTimeArea();
				if(var3.length>=1)
				{
					document.trainMovementForm.startTime.value=var3[0];
					document.trainMovementForm.endTime.value=var3[1];
				}
			}
		}
		else if(var1=='state')
		{
			document.trainMovementForm.stateFlag.value=var2;
		}
		document.trainMovementForm.action="/train/plan/trainPlanList0.do?b_query0=link&model=1";
	    document.trainMovementForm.submit();
	}
	
	
	
	 //查询
   function query()
   {
   		var hashvo=new ParameterSet();
		hashvo.setValue("opt","query");
		var In_paramters="tableName=R31"; 	
		var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:ReturnQuery,functionId:'3000000104'},hashvo);			
   }
   
   function ReturnQuery(outparamters)
   {
   		
   		var fields_temp=outparamters.getValue("fields");		
   		var fields=new Array();
   		for(var i=0;i<fields_temp.length;i++)
   		{
   			////数组[ 0:列名 1:列描述  2:列的类型 3:如果为代码型,则为代码值,否则为空 ]
   			var a_field=fields_temp[i].split("<@>");
   			fields[i]=a_field
   		}
    	 var extendSql=generalQuery("R31",fields);
    	 if(extendSql)
    	 {
    	 	 trainMovementForm.action='/train/plan/trainPlanList0.do?b_query0=link&model=1&extendSql='+extendSql;
    	 	 trainMovementForm.submit();
    	 }
  
   }
function searchInform(){
	var thecodeurl ="/train/traincourse/generalsearch.do?b_query=link&fieldsetid=r31"; 
	var return_vo= window.showModalDialog(thecodeurl, "", 
              	"dialogWidth:700px; dialogHeight:410px;resizable:no;center:yes;scroll:no;status:no");
    if(return_vo!=null){
    	trainMovementForm.action='/train/plan/trainPlanList0.do?b_query0=link&model=1&extendSql='+return_vo;
    	trainMovementForm.submit();
    }
}
   	function append(){
		var hashvo=new ParameterSet();
		hashvo.setValue("a_code","");	
		hashvo.setValue("model","1");	
		var request=new Request({method:'post',asynchronous:false,onSuccess:setAppendRecord,functionId:'2020020212'},hashvo);
	}
	function setAppendRecord(outparamters){
		rId=outparamters.getValue("r3101"); 
		var tablename,table,dataset,record;
		tablename="tabler31";
		table=$(tablename);
        dataset=table.getDataset(); 
        record=dataset.getCurrent(); 
        
		dataset.insertRecord("begin");	
	    record=dataset.getCurrent();
   	 	record.setValue("r3101",outparamters.getValue("r3101")); 
    	record.setValue("b0110",outparamters.getValue("b0110")); 
		record.setValue("e0122",outparamters.getValue("e0122")); 
    	record.setValue("r3127",outparamters.getValue("r3127"));  
    	record.setValue("r3130",outparamters.getValue("r3130")); 
    	if(record.getString("r3117"))
    		record.setValue("r3117","活动说明"); 
    	record.setState("modify");
    	var r3118 = outparamters.getValue("r3118");
    	if(r3118!=null&&r3118.length>8) 	
    		record.setValue("r3118",outparamters.getValue("r3118"));  
		//新增完数据后刷新表格
    	//dataset.flushData();
		 	
	}
   
   //排序
   function taxis()
   {
   		var hashvo=new ParameterSet();
		hashvo.setValue("opt","taxis");
   		var In_paramters="tableName=R31"; 	
		var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:ReturnTaxis,functionId:'3000000104'},hashvo);			
   }
   
   
   function ReturnTaxis(outparamters)
   {
   		var fields_temp=outparamters.getValue("fields");		
   		var fields=new Array();
   		for(var i=0;i<fields_temp.length;i++)
   		{
   			////数组[ 0:列名 1:列描述  2:列的类型 3:如果为代码型,则为代码值,否则为空 ]
   			var a_field=fields_temp[i].split("<@>");
   			fields[i]=a_field
   		}
    	 var orderSql=taxisDialog("R31",fields);
    	 if(orderSql)
    	 {
    	 	 trainMovementForm.action='/train/plan/trainPlanList0.do?b_query0=link&model=1&orderSql='+orderSql;
    	 	 trainMovementForm.submit();
    	 }
    	
   }
   function trainDeletOrReject(flag){
	   var table,dataset,ids=""; 
       table=$("tabler31");
       dataset=table.getDataset(); 
       var record=dataset.getFirstRecord();
       while (record) {
           if (record.getValue("select")) {
               var classid=record.getValue("R3101");
               //var type=record.recordState;
               var name=record.getValue("R3130");
               var spflag=record.getValue("R3127");
               ids=ids+classid+":"+name+":"+spflag+",";
           }
           record=record.getNextRecord();
       }
       if(ids==null||ids.length<1){
           alert(SELECT_TRAIN_CLASS);
           return;
       }

       if(flag=='1'){
           if(!confirm("确定删除吗？"))
               return;
       }
       if(flag=='2'){
           if(!confirm("确定驳回吗？"))
               return;
       }

       var hashvo=new ParameterSet();
       hashvo.setValue("plan_set_table","R31");
       hashvo.setValue("ids",ids);
       if(flag=='1'){
           var request=new Request({method:'post',asynchronous:false,onSuccess:deleRejectOk,functionId:'2020020103'},hashvo);
       }
       if(flag=='2'){
           var request=new Request({method:'post',asynchronous:false,onSuccess:deleRejectOk,functionId:'2020020115'},hashvo);
       }
       
   }
   function deleRejectOk(outparameters)
   {
	      var msg=outparameters.getValue("msg");
	      if(msg == '0'){
	    	  alert(TRAIN_CLASS_SUCCESS);
		      window.location.reload();
	          return;
		  }
   }
   function trainBoards(flag){
		var table,dataset,ids="";	
	    table=$("tabler31");
	    dataset=table.getDataset();	
		var record=dataset.getFirstRecord();
		while (record) {
			if (record.getValue("select")) {
				var classid=record.getValue("R3101");
				var type=record.recordState;
				ids=ids+classid+":"+type+",";
			}
			record=record.getNextRecord();
		}
		if(ids==null||ids.length<1){
			alert(SELECT_TRAIN_CLASS);
			return;
		}
		if(flag=='1'){
			if(!confirm(TRAIN_CALSS_REPORT))
				return;
		}
		if(flag=='2'){
			if(!confirm(TRAIN_CALSS_APPROVAL))
				return;
		}
		var hashvo=new ParameterSet();
		hashvo.setValue("ids",ids);
		hashvo.setValue("vflag",flag);
		var request=new Request({method:'post',asynchronous:false,onSuccess:check_oks,functionId:'202003003306'},hashvo);
	}
	function check_oks(outparameters)
	{
	  var msg=outparameters.getValue("msg");
	  if(msg=='no'||msg=='0')
	  {
		  var ids=outparameters.getValue("ids");
		  var vflag=outparameters.getValue("vflag");
		  var hashvo=new ParameterSet();
		  hashvo.setValue("ids",ids);
		  if(vflag=='1')
			  var request=new Request({method:'post',asynchronous:false,onSuccess:Success,functionId:'2020020114'},hashvo);
		  if(vflag=='2')
		  	  var request=new Request({method:'post',asynchronous:false,onSuccess:Success,functionId:'2020020110'},hashvo);
		  function Success(outparameters)
			{
			  var flag=outparameters.getValue("msg");
			  if(flag=='true')
			  {
			    alert(TRAIN_CLASS_SUCCESS);
			    window.location.reload();
			    return;
			  }
			}
	  }
	  else{
		  var filename=outparameters.getValue("fileName");
	      if(filename==null)
	      	return;
	      window.location.target="mil_body";
		  window.location.href = "/servlet/vfsservlet?fromjavafolder=true&fileid="+filename;
	  }
	}
</script>

<html:form action="/train/plan/trainPlanList0">
<table><tr><td>
<hrms:menubar menu="menu1" id="menubar1">
	<hrms:menuitem name="file" label="menu.file" >						
		<hrms:menuitem name="mitem1" label="button.cardpdf" icon="/images/print.gif" url="executeOutFile(1)" function_id="090408"/>
		<hrms:menuitem name="mitem2" label="button.createescel" icon="/images/print.gif" url="executeOutFile(2)" function_id="090409"/>
		<hrms:menuitem name="mitem3" label="kq.report.pagesetup" icon="/images/prop_ps.gif" url="setPageFormat()" function_id="090410"/>					
	</hrms:menuitem>
	<hrms:menuitem name="edit" label="edit_report.status.bj">						
		<hrms:menuitem name="mitem5" label="options.save" function_id="090406" icon="/images/save_edit.gif"    command="save"/>					
		<hrms:menuitem name="mitem4" label="kq.emp.change.emp.leave" function_id="090402" icon="/images/delete.gif" url="trainDeletOrReject('1');"/>
		<hrms:menuitem name="mitem19" label="lable.law_base_file_search.query" icon="/images/quick_query.gif" url="searchInform();" function_id="090411"/>
		<hrms:menuitem name="mitem20" label="label.zp_exam.sort" icon="/images/quick_query.gif" url="taxis()" function_id="090407"/>	
	</hrms:menuitem>
</hrms:menubar>
</td></tr></table>
<hrms:dataset name="trainMovementForm" property="fieldlist" scope="session" setname="${trainMovementForm.tablename}" 
   setalias="plan_set" readonly="false"  rowlock="true"  rowlockfield="r3127"  rowlockvalues=",01,07,"
    select="true" sql="${trainMovementForm.sql}" pagerows="${trainMovementForm.pagerows}" buttons="movefirst,prevpage,nextpage,movelast">
    	<hrms:commandbutton name="table" hint="" functionId="" refresh="true" type="selected" setname="${trainMovementForm.tablename}" function_id="090401" onclick="append();">
     		<bean:message key="button.insert"/>
    	</hrms:commandbutton>
    	<hrms:commandbutton name="save" function_id="090406" functionId="2020020109" refresh="true" type="all-change" setname="${trainMovementForm.tablename}">
	        <bean:message key="button.save"/>
	     </hrms:commandbutton>  
	     <hrms:commandbutton name="delselected" functionId="" function_id="090402" refresh="true" type="selected" setname="${positionDemandForm.tablename}" onclick="trainDeletOrReject('1');">
	     	<bean:message key="button.delete"/>
	     </hrms:commandbutton>
	      <hrms:commandbutton name="sub" functionId="" function_id="090404" refresh="true" type="selected" setname="${positionDemandForm.tablename}" onclick="trainBoards('1');">
	     	<bean:message key="button.report"/>
	     </hrms:commandbutton>   
	      <hrms:commandbutton name="appeal" functionId="" function_id="090403" refresh="true" type="selected" setname="${positionDemandForm.tablename}" onclick="trainBoards('2');">
	     	<bean:message key="button.appeal"/>
	     </hrms:commandbutton>
	     <hrms:commandbutton name="back" functionId="" function_id="090405"  refresh="true" type="selected" setname="${positionDemandForm.tablename}" onclick="trainDeletOrReject('2');">
	     	<bean:message key="edit_report.status.dh"/>
	     </hrms:commandbutton>
</hrms:dataset>
<%int privbutton=0; %>
<hrms:priv func_id="090401,090402,090403,090404,090405,090406">
<% privbutton=1;%>
<%if("hcm".equalsIgnoreCase(userView.getBosflag())){ %>
<table style="position:absolute;left:370px;top:34px;">
<% } else { %>
<table style="position:absolute;left:370px;top:22px;">
<%} %>
<tr><td style="padding-top: 3px;">
<bean:message key="sys.export.status"/>
<hrms:optioncollection name="trainMovementForm" property="planStateList" collection="list" />
<html:select name="trainMovementForm" property="stateFlag" onchange="subReFlesh();" style="width:80px;text-align:left">
	<html:options collection="list" property="dataValue" labelProperty="dataName"/>
</html:select>
</td><td style="padding-top: 3px;">
<bean:message key="sys.export.job_time"/>
<hrms:optioncollection name="trainMovementForm" property="timeConditionList" collection="tlist" />
<html:select name="trainMovementForm" property="timeFlag" onchange="timeFlagChange(this);" style="width:80px;text-align:left">
	<html:options collection="tlist" property="dataValue" labelProperty="dataName"/>
</html:select>
</td><td id="viewtime" style="display:none">
<bean:message key='hmuster.label.from'/><input type="text" name="startTime"  extra="editor" value="${trainMovementForm.startTime}" onblur="timeCheck(this);" style="width:100px;font-size:10pt;text-align:left" dropDown="dropDownDate">
<bean:message key='hmuster.label.to'/><input type="text" name="endTime"  extra="editor" value="${trainMovementForm.endTime}" onblur="timeCheck(this);" style="width:100px;font-size:10pt;text-align:left" dropDown="dropDownDate">
</td><td id="viewtime1" style="display:none">
	<input type="button" value="<bean:message key='button.query'/>" onclick="subReFlesh();" class="mybutton">
</td>
</tr></table>
</hrms:priv>
<%if(privbutton==0){ %>
<table style="position:absolute;left:400px;top:0px;">
<tr><td style="padding-top: 3px;">
<bean:message key="sys.export.status"/>
<hrms:optioncollection name="trainMovementForm" property="planStateList" collection="list" />
<html:select name="trainMovementForm" property="stateFlag" onchange="subReFlesh();" style="width:80px;text-align:left">
	<html:options collection="list" property="dataValue" labelProperty="dataName"/>
</html:select>
</td><td style="padding-top: 3px;">
<bean:message key="sys.export.job_time"/>
<hrms:optioncollection name="trainMovementForm" property="timeConditionList" collection="tlist" />
<html:select name="trainMovementForm" property="timeFlag" onchange="timeFlagChange(this);" style="width:80px;text-align:left">
	<html:options collection="tlist" property="dataValue" labelProperty="dataName"/>
</html:select>
</td><td id="viewtime" style="display:none">
<bean:message key='hmuster.label.from'/><input type="text" name="startTime"  extra="editor" value="${trainMovementForm.startTime}" onblur="timeCheck(this);" style="width:100px;font-size:10pt;text-align:left" dropDown="dropDownDate">
<bean:message key='hmuster.label.to'/><input type="text" name="endTime"  extra="editor" value="${trainMovementForm.endTime}" onblur="timeCheck(this);" style="width:100px;font-size:10pt;text-align:left" dropDown="dropDownDate">
</td><td id="viewtime1" style="display:none">
	<input type="button" value="<bean:message key='button.query'/>" onclick="subReFlesh();" class="mybutton">
</td>
</tr></table>
<%} %>
</html:form>
<script language="javascript">
function table${trainMovementForm.tablename}_r3117_onRefresh(cell,value,record){
	if(record!=null){
		var id = record.getValue("r3101");
		if(id.length<1){
			id=rId; 
			rId="";
		}
		var r3127 = record.getValue("r3127");
		if(r3127=='01'||r3127=='07'||r3127.length<1)
			cell.innerHTML="&nbsp;&nbsp;&nbsp;&nbsp;<img src=\"/images/edit.gif\" onclick=\"eventDesc('"+id+"','1')\" border=\"0\"  style=\"cursor:hand;\">";
		else
			cell.innerHTML="&nbsp;&nbsp;&nbsp;&nbsp;<img src=\"/images/view.gif\" onclick=\"eventDesc('"+id+"','0')\" border=\"0\"  style=\"cursor:hand;\">";
	}
	var name = document.getElementsByTagName("td");
	for(var i = 0;i < name.length;i++){
		if(name[i].name == "r3117"){
			name[i].title = "";
		}
	}
}
function openw(url){
	var ww = 800;//window.screen.width-5;
	var hh = 600;//window.screen.height - 40;
	window.open(url, "_new","toolbar=no,location=no,directories=no,menubar=no,scrollbars=yes,resizable=no,status=no,top=0,left=0,width="+ww+",height="+hh);
   
}
function eventDesc(id,readonly){
	var thecodeurl ="/train/plan/eventdes.do?b_event=link`id=r3117`flag=add`read="+readonly+"`classid="+id;
	var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl;
	openw(iframe_url);     
}
</script>
<logic:equal name="trainMovementForm" property="timeFlag" value="5">
<script language="JavaScript">
toggles("viewtime");
toggles("viewtime1");
</script>
</logic:equal>