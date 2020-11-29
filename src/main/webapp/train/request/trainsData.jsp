<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String bosflag="";
	if(userView != null){	  
	  	bosflag=userView.getBosflag();
	  	bosflag=bosflag!=null?bosflag:"";                
	}
%> 
<head>
<meta http-equiv="X-UA-Compatible" content="IE=9; IE=8;">
</head>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/validateDate.js"></script>
<script language="JavaScript" src="../../../../module/utils/js/template.js"></script>
<script language="JavaScript" src="/js/common.js"></script>
<script language='JavaScript' src='../../../components/tableFactory/tableFactory.js'></script>
<script language="JavaScript" src="../../../module/system/questionnaire/questionnaire_resource_zh_CN.js"></script><!-- 问卷分析用资源文件 -->
<script type="text/javascript" src='../../../ext/ext6/charts.js'></script><!-- 问卷分析用Ext图表文件 -->
<script type="text/javascript" src="../../js/hjsjUrlEncode.js"></script>
<script language="JavaScript">
	Ext.Loader.setConfig({
		enabled: true,
		paths: {
				'QuestionnaireTemplate': '../../../module/system/questionnaire/template',//问卷分析
				'SYSF':'../../../components/fileupload',//问卷分析
				'QuestionnaireAnalysis': '../../../module/system/questionnaire/analysis',//问卷分析（图表分析）
		}
	});
</script>
<script language="JavaScript" src="/train/request/TrainData.js"></script>
<script language="javascript" src="/js/dict.js"></script> 
<style type="text/css"> 
.selectPre{
	position:absolute;
    left:620px;
    <%if("hcm".equalsIgnoreCase(userView.getBosflag())){%>
    top:40px;
    <%}else {%>
    top:30px;
    <%}%>
}
.selectPre1{
	position:absolute;
    left:620px;
    <%if("hcm".equalsIgnoreCase(userView.getBosflag())){%>
    top:10px;
    <%}else {%>
    top:5px;
    <%}%>
}
</style>
<script language="JavaScript">
function executeOutFile(flag){
	var tablename,table,size,fieldWidths,whl_sql;       
    tablename="table${courseTrainForm.tablename}";
    table=$(tablename);
    size=${courseTrainForm.fieldSize};
    for(var i=1;i<size+2;i++){
        var width=table.getColWidth(table,i);
        if(width!=null)
	       fieldWidths+="/"+width;
    }
    var hashvo=new ParameterSet();
	hashvo.setValue("fieldWidths",fieldWidths.substring(10));
	hashvo.setValue("tablename",'${courseTrainForm.tablename}');
	var In_paramters="flag="+flag;  
	var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:showfile,functionId:'0521010011'},hashvo);
}
//输出 EXCEL OR PDF
function showfile(outparamters){
	var outName=outparamters.getValue("outName");
	outName = getDecodeStr(outName);
	window.location.target="_blank";
	window.location.href="/servlet/vfsservlet?fromjavafolder=true&fileid="+outName;
}
function returnFirst(){
   	self.parent.location= "/general/tipwizard/tipwizard.do?br_train=link";
}
function trainBoard(){
	var tablename,table,dataset,record;
	tablename="table${courseTrainForm.tablename}";
    table=$(tablename);
    dataset=table.getDataset();	
    record=dataset.getCurrent();
	if(!record)
		  return;
	var r3101 = record.getValue("r3101");
	if(record.getValue("r3127")!="04"){
		alert("只能对已经发布的培训班发布通知!");
		return false;
	}
	var thecodeurl ="/selfservice/infomanager/board/searchboard.do?b_add=link`trainid="+r3101+"`flag=1`chflag=1`opt=1`titlename="+getEncodeStr(record.getValue("r3130")+"通知");
	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
    var return_vo= window.showModalDialog(iframe_url,"train", 
              	"dialogWidth:800px; dialogHeight:600px;resizable:yes;center:yes;scroll:yes;status:no");
}
function check_formula(){
	var table,dataset,record;	
    table=$("tabler31");
    dataset=table.getDataset();	
    record=dataset.getCurrent();

    if(!record)
		return;
	var classid=record.getValue("R3101");
	var target_url="/train/request/trainsData/check.do?b_query=link";
	 var iframe_url="/general/query/common/iframe_query.jsp?src="+target_url;
	var return_vo= window.showModalDialog(iframe_url,"px", 
			"dialogWidth=800px;dialogHeight=490px;resizable=yes;scroll=no;status=no;");
}
function auditCollect(){
	   var request=new Request({asynchronous:false,onSuccess:check_ok,functionId:'202003003306'});	
}
function check_ok(outparameters)
{
  var msg=outparameters.getValue("msg");
  if(msg=='0')
  {
    alert(TRAIN_CLASS_NOTCHECK);
    return;
  }
  if(msg=='no')
  {
     alert(TRAIN_CLASS_CHECK_FINISH);
     return;
  }
  else{
	  var filename=outparameters.getValue("fileName");
      if(filename==null)
      	return;
      window.location.target="mil_body";
		window.location.href = "/servlet/vfsservlet?fromjavafolder=true&fileid="+filename;
  }
}
//培训班发布
function trainBoards(){
	var table,dataset,ids="";	
    table=$("tabler31");
    dataset=table.getDataset();	
	var record=dataset.getFirstRecord();
	while (record) {
		if (record.getValue("select")) {
			var classid=record.getValue("R3101");
			ids=ids+classid+",";
		}
		record=record.getNextRecord();
	}
	if(ids==null||ids.length<1){
		alert(SELECT_TRAIN_CLASS);
		return;
	}
	if(!confirm(TRAIN_CALSS_ISSUE))
		return;
	var hashvo=new ParameterSet();
	hashvo.setValue("ids",ids);
	var request=new Request({method:'post',asynchronous:false,onSuccess:check_oks,functionId:'202003003306'},hashvo);
}
function check_oks(outparameters)
{
	   
  var msg=outparameters.getValue("msg");
  
  if(msg=='no'||msg=='0')
  {
	  var ids=outparameters.getValue("ids");
	  var hashvo=new ParameterSet();
	  hashvo.setValue("ids",ids);
	  var request=new Request({method:'post',asynchronous:false,onSuccess:Success,functionId:'2020020220'},hashvo);
	  function Success(outparameters)
		{
		  var flag=outparameters.getValue("msg");
		  if(flag=='true')
		  {
		    courseTrainForm.action="/train/request/trainsData.do?b_query=link&a_code="+'${courseTrainForm.a_code}'+"&model="+'${courseTrainForm.model}';
			courseTrainForm.submit();
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

<html:form action="/train/request/trainsData">
<html:hidden name="courseTrainForm" property="searchstr"/>
<html:hidden name="courseTrainForm" property="isAutoHour"/>
<table>
	<tr>
		<td>
			<table>
				<tr>
					<td>
						<hrms:menubar menu="menu1" id="menubar1">
							<hrms:menuitem name="file" label="conlumn.mediainfo.filename" function_id="32330f,32330g">						
								<hrms:menuitem name="mitem1" label="button.createpdf" icon="/images/print.gif" url="executeOutFile(1)" function_id="32330f"/>
								<hrms:menuitem name="mitem2" label="button.createescel" icon="/images/print.gif" url="executeOutFile(2)" function_id="32330g"/>
								<!-- 16596   ,32330h
								hrms:menuitem name="mitem3" label="kq.report.pagesetup" icon="/images/prop_ps.gif" url="setPageFormat('${courseTrainForm.username}');"  function_id="32330h"/>
								-->
								<hrms:menuitem name="mitem4" label="train.job.export" icon="/images/add_del.gif" url="batchInOut();"  function_id="32330n"/>
								<hrms:menuitem name="mitem5" label="train.job.import" icon="/images/deal.gif" url="fileup();"  function_id="32330o"/>					
							</hrms:menuitem>
							<hrms:menuitem name="edit" label="lable.tz_template.edit" function_id="323300,323302,32330i,32330a,32330p">	
								<hrms:menuitem name="mitem6" label="button.new.add" icon="/images/add.gif" command="" url="add('${courseTrainForm.model}','${courseTrainForm.a_code}','${courseTrainForm.isAutoHour}');" function_id="323300"/>	<!-- append('${courseTrainForm.model}','${courseTrainForm.a_code}'); -->
								<hrms:menuitem name="mitem7" label="infor.menu.batupdate_m" icon="/images/write.gif" command="" url="batchedit('${courseTrainForm.model}','${courseTrainForm.a_code}');" function_id="32330p"/>
								<hrms:menuitem name="mitem4" hint="general.inform.search.confirmed.del" label="button.delete" icon="/images/delete.gif" command="delselected" function_id="323302"/>
								<hrms:menuitem name="mitem19" label="button.query" icon="/images/quick_query.gif" url="searchInform('${courseTrainForm.model}','${courseTrainForm.a_code}');" function_id="32330i"/>
								<hrms:menuitem name="mitem20" label="label.zp_exam.sort" icon="/images/sort.gif" url="sortRecord('${courseTrainForm.model}','${courseTrainForm.a_code}');" function_id="32330a"/>	
							</hrms:menuitem>
							<hrms:menuitem name="trains" label="sys.res.trainjob" function_id="323303,323305">						
								<hrms:menuitem name="mitem1" label="label.commend.execute" icon="" url="trainBoards();" function_id="323303"/>
								<hrms:menuitem name="mitem2" label="kq.pigeonhole.submit" hint="train.b_plan.request.filing" icon="" url="trainArchive('${courseTrainForm.model}','${courseTrainForm.a_code}')" function_id="323305"/>
								<hrms:menuitem name="mitem3" label="button.audit" hint="" icon="" url="auditCollect()" function_id="32330m"/>
							</hrms:menuitem>
							<hrms:menuitem name="cond" label="infor.menu.compute" function_id="32330b,32330c">						
								<hrms:menuitem name="mitem1" label="train.b_plan.request.cost.sharing" icon="" url="costShare()" function_id="32330b"/>
								<hrms:menuitem name="mitem2" label="train.b_plan.request.hours.cal" icon="" function_id="32330c">
								<logic:equal name="courseTrainForm" property="isAutoHour" value='1'>
									<hrms:menuitem name="mitem21" label="hmuster.label.auto_count"  icon="" checked="true" groupindex="1" url="" function_id=""/>
									<hrms:menuitem name="mitem22" label="train.b_plan.request.filled.manually" icon="" groupindex="1" url="autoCalcuStuHour('${courseTrainForm.model}','${courseTrainForm.a_code}','0')"/>
								</logic:equal>
								<logic:notEqual name="courseTrainForm" property="isAutoHour" value='1'>
									<hrms:menuitem name="mitem21" label="hmuster.label.auto_count"  icon="" groupindex="1" url="autoCalcuStuHour('${courseTrainForm.model}','${courseTrainForm.a_code}','1')"/>
									<hrms:menuitem name="mitem22" label="train.b_plan.request.filled.manually" icon="" checked="true" groupindex="1"  url=""/>
								</logic:notEqual>
								</hrms:menuitem>
							</hrms:menuitem>
							<hrms:menuitem name="sets" label="button.orgmapset" function_id="32330d,32330e">						
								<hrms:menuitem name="mitem1" label="train.b_plan.request.standard.units" icon="" url="setStudeyHour('${courseTrainForm.model}','${courseTrainForm.a_code}')" function_id="32330d"/>
								<hrms:menuitem name="mitem2" label="train.b_plan.request.train.table" icon="" url="trainEval()" function_id="32330e"/>
								<hrms:menuitem name="mitem3" label="train.job.shformula" icon="" url="check_formula()" function_id="32330l"/>
							</hrms:menuitem>
						</hrms:menubar>
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>
<bean:define id="rflag" name="courseTrainForm" property="returnvalue" />
<hrms:dataset name="courseTrainForm" property="itemlist" scope="session" setname="${courseTrainForm.tablename}" 
 setalias="data_table"  rowlock="true" rowlockfield="r3127"  rowlockvalues=",01,02,03,07,09," select="true"
 sql="${courseTrainForm.strsql}"  pagerows="${courseTrainForm.pagerows}" buttons="bottom">
	<hrms:commandbutton name="table" hint="" functionId="" refresh="true" type="selected" function_id="323300" setname="${courseTrainForm.tablename}" onclick="add('${courseTrainForm.model}','${courseTrainForm.a_code}','${courseTrainForm.isAutoHour}');">
     <bean:message key="button.insert"/>
  </hrms:commandbutton>
	<hrms:commandbutton name="savedata"  functionId="2020020214" refresh="true" type="all-change" setname="${courseTrainForm.tablename}" function_id="323301" >
		<bean:message key="button.save"/>
	</hrms:commandbutton>  
	<hrms:commandbutton name="delselected" hint="general.inform.search.confirmed.del" functionId="2020020213" refresh="true" function_id="323302" type="selected" setname="${courseTrainForm.tablename}">
     <bean:message key="button.delete"/>
  </hrms:commandbutton>
  <hrms:commandbutton name="issue" hint="" functionId=""   refresh="true" type="selected" onclick="trainBoards();" setname="${courseTrainForm.tablename}"  function_id="323303" >
	    <bean:message key="button.issue"/>
	</hrms:commandbutton>
	<hrms:commandbutton name="stop" hint="gz.acount.determined.stop" functionId="2020020223"   refresh="true" type="selected" setname="${courseTrainForm.tablename}"  function_id="323304" >
	   <bean:message key="label.commend.stop"/>
	</hrms:commandbutton>
	<hrms:commandbutton name="guidang" hint="train.b_plan.request.filing" functionId=""  onclick="trainArchive('${courseTrainForm.model}','${courseTrainForm.a_code}');" function_id="323305">
	    <bean:message key="kq.pigeonhole.submit"/>
	</hrms:commandbutton>
	<hrms:commandbutton name="plannotice" functionId=""  function_id="323310"  refresh="true" type="selected" onclick="trainBoard();" setname="${courseTrainForm.tablename}">
     	<bean:message key="conlumn.infopick.educate.notification"/>
  </hrms:commandbutton>
  <hrms:commandbutton name="export" functionId=""  function_id="32330n"  refresh="true" type="selected" onclick="batchInOut();" setname="${courseTrainForm.tablename}">
     	<bean:message key="train.job.export"/>
  </hrms:commandbutton>
  <hrms:commandbutton name="import" functionId=""  function_id="32330o"  refresh="true" type="selected" onclick="fileup();" setname="${courseTrainForm.tablename}">
     	<bean:message key="train.job.import"/>
  </hrms:commandbutton>
	<%if("dxt".equals(rflag)&&(bosflag.equals("hl")||bosflag.equals("hcm"))){%>
	<hrms:commandbutton name="returnButton" hint="" functionId=""   refresh="true" type="selected" setname="${courseTrainForm.tablename}" onclick="returnFirst();">
	    <bean:message key="reportcheck.return"/>
	</hrms:commandbutton>
	<%} %>
</hrms:dataset>
<%int privbutton=0; %>
<hrms:priv func_id="323300,323301,323302,323303,323304,323305,323310,32330n,32330o">
<% privbutton=1;%>
</hrms:priv>
<%if("dxt".equals(rflag)&&(bosflag.equals("hl")||bosflag.equals("hcm"))){
	privbutton=1;
} 

if(privbutton!=1){%>
<table class="selectPre1">
<%}else{ %>
<table class="selectPre">
<%} %>
	<tr>
		<td style="padding-top: 3px;">
			<bean:message key="sys.export.status"/>
			<hrms:optioncollection name="courseTrainForm" property="flaglist" collection="list" />
			<html:select name="courseTrainForm" property="spflag" onchange="changesReload('${courseTrainForm.model}','${courseTrainForm.a_code}');" style="width:80px;text-align:left">
				<html:options collection="list" property="dataValue" labelProperty="dataName"/>
			</html:select>
	  </td>
		<td style="padding-top: 3px;">
			<bean:message key="sys.export.job_time"/>
			<hrms:optioncollection name="courseTrainForm" property="timelist" collection="tlist" />
			<html:select name="courseTrainForm" property="timeflag" style="width:80px;text-align:left" onchange="timeFlagChange(this,'${courseTrainForm.model}','${courseTrainForm.a_code}');">
				<html:options collection="tlist" property="dataValue" labelProperty="dataName"/>
			</html:select>
		</td>
		<td id="viewtime" style="display:none">
			<bean:message key="hmuster.label.from"/><input type="text" name="startime"  extra="editor" value="${courseTrainForm.startime}" id="editor1" style="width:100px;text-align:left" dropDown="dropDownDate"> <!-- zhangcq 2016-4-25 -->
			<bean:message key="hmuster.label.to"/><input type="text" name="endtime"  extra="editor" value="${courseTrainForm.endtime}" id="editor2" style="width:100px;font-size:10pt;text-align:left" dropDown="dropDownDate">
		</td>
		<td id="viewtime1" style="display:none">
			<input type="button" value="<bean:message key='infor.menu.query'/>" class="mybutton" onclick="changesReload('${courseTrainForm.model}','${courseTrainForm.a_code}');">
		</td>
	</tr>
</table>		
</html:form>

<logic:equal name="courseTrainForm" property="timeflag" value="04">
	<script language="JavaScript">
	  toggles("viewtime");
	  toggles("viewtime1");
	</script>
</logic:equal>

<script language="javascript">
	var sub_page=1;
	function table${courseTrainForm.tablename}_onRowClick(table){
		var dataset=table.getDataset();	
	   	var record=dataset.getCurrent();
	   	var h=window.top.document.body.clientHeight;
	   	var mh = '0px';
	   	
	   	if(record==null){
	   		parent.frames['ril_body2'].location="/train/request/trainsData.do?b_menu=link";
			//return;  //如果是空就不会刷新下半部分 要刷新只能也查询一次 参数默认为空
		}else{
		    var r3101=record.getValue("r3101");
		    var r3127=record.getValue("r3127");    
		    var r3115=record.getValue("r3115");  //培训班开始时间
		    var r3116=record.getValue("r3116");  //培训班结束时间 
		    
		    parent.frames['ril_body2'].location="/train/request/trainsData.do?b_menu=link&r3101="+r3101+"&r3127="+r3127+"&r3115="+numParseDate(r3115)+"&r3116="+numParseDate(r3116)+"&sub_page="+sub_page;
	  	}

	   	window.parent.Ext.getCmp("iframe_body2").show();
	}
	function tabler31_trainplan_onRefresh(cell,value,record){
		if(record!=null){
			var r3125 = record.getString("r3125");
			var r3101 = record.getString("r3101");
			var r3127 = record.getValue("r3127");
			var trainplan = record.getString("trainplan");
			if(trainplan!=null&&trainplan.length>0){
				var html="";
				if(r3127!=null&&(r3127=='04'||r3127=='06'))
					html+=trainplan;
				else{
					<hrms:priv func_id="32330j">
			   			html="<div style=\"cursor:hand;font-size:9pt;color:#1B4A98\" onclick=\"getTrainPlanValue('"+r3101+"')\">";
					</hrms:priv>
						html+=trainplan;
					<hrms:priv func_id="32330j">
			   			html+="</div>";
					</hrms:priv>
				}
				cell.innerHTML=html;
			}else{
				var html="";
				if(r3127!=null&&(r3127=='04'||r3127=='06'))
					html+="未对应";
				else{
					<hrms:priv func_id="32330j">
					   html="<div style=\"cursor:hand;font-size:9pt;color:#1B4A98\" onclick=\"getTrainPlanValue('"+r3101+"');\">";
					</hrms:priv>
					html+="未对应";
					<hrms:priv func_id="32330j">
					   html+="</div>";
					</hrms:priv>
				}
				cell.innerHTML=html;
			}
		}else 
			cell.innerHTML="";
	}
	function getTrainPlanValue(values){
		var tablename,table,dataset;
		
		tablename="tabler31";
    	table=$(tablename);
    	dataset=table.getDataset();
    
	    var record=dataset.getCurrent();
	    
	    var r3127 = record.getValue("r3127");
	    
	    if(r3127!="04"&&r3127!="05"&&r3127!="06"){    
			var thecodeurl="/train/traincourse/trainplan.jsp";
		    var return_vo= window.showModalDialog(thecodeurl, values, 
		              "dialogWidth:300px; dialogHeight:400px;resizable:no;center:yes;scroll:no;status:no");
		    if(return_vo!=null){
		    	var arr = return_vo.split("`");
		    	if(arr.length!=2)
		    		return false;
		    	record.setValue("r3125",arr[0]);
			    record.setValue("trainplan",arr[1]);     
		    }
	    }
	}
	function setSecondPage(page)
	{
		sub_page=page;
	}
	
	function table${courseTrainForm.tablename}_questionnaire_onRefresh(cell,value,record)
	{	
		if(record != null){		
			getEvalSet(isShowQusestionImg,record.getValue("r3101"),cell);
		}else
			cell.innerHTML="";
		
	}
		
	function table${courseTrainForm.tablename}_khresult_onRefresh(cell,value,record)
	{	
		if(record != null){		   
			getEvalSet(isShowKhResultImg,record.getValue("r3101"),cell);
		}else
			cell.innerHTML="";
	}
	function table${courseTrainForm.tablename}_r3117_onRefresh(cell,value,record){
		if(record!=null){
			var id = record.getValue("r3101");
			var r3127 = record.getValue("r3127");
			if(r3127=='04'||r3127=='06')
				cell.innerHTML="&nbsp;&nbsp;&nbsp;&nbsp;<img src=\"/images/view.gif\" onclick=\"eventDesc('"+id+"','0')\" border=\"0\"  style=\"cursor:hand;\">";
			else
				cell.innerHTML="&nbsp;&nbsp;&nbsp;&nbsp;<img src=\"/images/edit.gif\" onclick=\"eventDesc('"+id+"','1')\" border=\"0\"  style=\"cursor:hand;\">";
		} else 
			cell.innerHTML="";
		
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
		var thecodeurl ="/train/request/trainsData.do?b_event=link`id=r3117`tablename=r31`read="+readonly+"`flag=add`classid="+id;
		var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
		openw(iframe_url);     
	}
		
</script>
