<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@page import="com.hrms.frame.codec.SafeCode"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@page import="com.hrms.struts.constant.WebConstant"%> 
<script language="JavaScript" src="/js/common.js"></script>
<script language="javascript" src="/js/dict.js"></script> 
<script language="javascript" src="./trainPlanList.js"></script> 
<script language="javascript" src="/js/constant.js"></script> 
<%
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	response.setHeader("Pragma", "No-cache");
 	response.setHeader("Cache-Control", "no-cache");
 	response.setDateHeader("Expires", 0);
%>
<html:form action="/train/plan/searchCreatPlanList"> 
<input type='hidden' name="selectIDs"  value="${trainMovementForm.selectIDs}" />
<input type='hidden' name="extendSql"  value="${trainMovementForm.extendSql}" />
<input type='hidden' name="orderSql"  value="${trainMovementForm.orderSql}" />
<table><tr><td>
<table><tr><td>
<hrms:menubar menu="menu1" id="menubar1">
	<hrms:menuitem name="file" label="menu.file" >						
		<hrms:menuitem name="mitem1" label="button.createpdf" icon="/images/print.gif" url="executeOutFile(1,'${trainMovementForm.tablename}','${trainMovementForm.fieldSize}');" function_id="090509" />
		<hrms:menuitem name="mitem2" label="button.createescel" icon="/images/print.gif" url="executeOutFile(2,'${trainMovementForm.tablename}','${trainMovementForm.fieldSize}');" function_id="090511" />
		<hrms:menuitem name="mitem3" label="kq.report.pagesetup" icon="/images/prop_ps.gif" url="setPageFormat('${trainMovementForm.username}');" function_id="090512"/>					
	</hrms:menuitem>
	<hrms:menuitem name="edit" label="infor.menu.edit" >						
		<hrms:menuitem name="mitem5" label="options.save" function_id="090507" icon="/images/save_edit.gif" command="save"/>					
		<hrms:menuitem name="mitem4" label="kq.shift.cycle.del" function_id="090502" icon="/images/delete.gif" command="delselected"/>
		<hrms:menuitem name="mitem19" label="infor.menu.query" icon="/images/quick_query.gif" url="searchInform();"  function_id="090513"/>
		<hrms:menuitem name="mitem20" label="label.zp_exam.sort" icon="/images/quick_query.gif" url="taxis()" function_id="090510" />	
	</hrms:menuitem>
	<hrms:menuitem name="result" label="train.plan.review.ass.results" >
		<hrms:menuitem name="mitem21" label="train.plan.review.view.ass.results"  url="lookResult('1','${trainMovementForm.tablename}')"  />
		<hrms:menuitem name="mitem22" label="train.plan.review.view.survey"  url="doExamint('3','${trainMovementForm.tablename}')"  />
		<hrms:menuitem name="mitem23" label="train.plan.review.teacher.ass.results"  url="lookResult('2','${trainMovementForm.tablename}')"  />
		<hrms:menuitem name="mitem24" label="train.plan.review.teacher.survey"  url="doExamint('4','${trainMovementForm.tablename}')"  />
	</hrms:menuitem>
</hrms:menubar>
</td></tr></table>
</td>
<td>
	<hrms:optioncollection name="trainMovementForm" property="trainPlanList" collection="list" />
	<html:select name="trainMovementForm" property="trainPlanID" size="1" style="text-align:left" onchange="sub('${trainMovementForm.codeSet}','${trainMovementForm.codeID}')" >
		<html:options collection="list" property="dataValue" labelProperty="dataName"/>
	</html:select>	
</td>
<td>
<bean:define id="planid" name="trainMovementForm" property="trainPlanID"></bean:define>
<% String trainPlanID = PubFunc.decrypt(SafeCode.decode(planid.toString()));
	if(!"0".equalsIgnoreCase(trainPlanID) && !"-1".equalsIgnoreCase(trainPlanID)){
%>
	<hrms:priv func_id="090505">
		<img  src="/images/readwrite_obj.gif"  border='1' title="<bean:message key='train.plan.edit.plan'/>"  onclick='editPlan()'/>
		</hrms:priv>
<%} %>
</td>
</table>
<hrms:dataset name="trainMovementForm" property="fieldlist" scope="session" setname="${trainMovementForm.tablename}" 
   setalias="plan_set" rowlock="true"  rowlockfield="r3127"  rowlockvalues=",02,03,"
   select="true" sql="${trainMovementForm.sql}" pagerows="${trainMovementForm.pagerows}" buttons="movefirst,prevpage,nextpage,movelast">
   		<hrms:commandbutton name="tableadd" hint="" functionId="" refresh="true" type="selected" setname="${trainMovementForm.tablename}"  function_id="090501"  onclick="append('${trainMovementForm.codeSet}','${trainMovementForm.codeID}');">
     		<bean:message key="button.insert"/>
    	</hrms:commandbutton>
	     <hrms:commandbutton name="save"  functionId="2020020102" refresh="true" function_id="090507" type="all-change" setname="${trainMovementForm.tablename}">
	        <bean:message key="button.save"/>
	     </hrms:commandbutton>  
	     <hrms:commandbutton name="confirm" functionId="" refresh="true" function_id="090503" type="selected" setname="${trainMovementForm.tablename}" onclick="trainBoards('1')">
	          <bean:message key="button.apply"/>
	     </hrms:commandbutton>  
	     <hrms:commandbutton name="overrule" functionId="2020020106" refresh="true" function_id="090504" type="selected" setname="${trainMovementForm.tablename}">
	         <bean:message key="button.reject"/>
	     </hrms:commandbutton>
	      <hrms:commandbutton name="issue2" functionId="" refresh="true" function_id="090506" type="selected" setname="${trainMovementForm.tablename}" onclick="trainBoards('2')">
	        <bean:message key='hire.jp.pos.promulgat'/>
	     </hrms:commandbutton>
	     <hrms:commandbutton name="delselected" functionId="2020020104"  function_id="090502"  refresh="true" type="selected" setname="${trainMovementForm.tablename}">
	     	<bean:message key="button.delete"/>
	     </hrms:commandbutton>
	     <hrms:commandbutton name="plannotice" functionId=""  function_id="090508"  refresh="true" type="selected" onclick="trainBoard();" setname="${trainMovementForm.tablename}">
	     	<bean:message key="conlumn.infopick.educate.notification"/>
	     </hrms:commandbutton>
	     <hrms:commandbutton name="faaa" functionId="" function_id="090505" refresh="true"  type="selected" onclick="addPlan();" setname="${trainMovementForm.tablename}">
		      <bean:message key="train.plan.create.new.plan"/>
		 </hrms:commandbutton> 
	     <hrms:commandbutton name="faaa2" functionId="" function_id="090505" onclick="adjustPlan('${trainMovementForm.tablename}');" setname="${trainMovementForm.tablename}">
		      <bean:message key="train.plan.input.plan"/>
		 </hrms:commandbutton>  
</hrms:dataset>
<%if("hcm".equalsIgnoreCase(userView.getBosflag())){ %>
<table style="position:absolute;left:590px;top:40px;";>
<% } else { %>
<table style="position:absolute;left:590px;top:28px;";>
<%} %>
<tr><td align="left" style="padding-top: 3px;">
<bean:message key="sys.export.status"/>
<hrms:optioncollection name="trainMovementForm" property="planStateList" collection="list" />
<html:select name="trainMovementForm" property="stateFlag" onchange="sub('${trainMovementForm.codeSet}','${trainMovementForm.codeID}');" style="width:80px;font-size:10pt;text-align:left">
	<html:options collection="list" property="dataValue" labelProperty="dataName"/>
</html:select>
</td><td style="padding-top: 3px;">
<bean:message key="sys.export.job_time"/>
<hrms:optioncollection name="trainMovementForm" property="timeConditionList" collection="tlist" />
<html:select name="trainMovementForm" property="timeFlag" onchange="timeFlagChange(this,'${trainMovementForm.codeSet}','${trainMovementForm.codeID}');" style="width:80px;font-size:10pt;text-align:left">
	<html:options collection="tlist" property="dataValue" labelProperty="dataName"/>
</html:select>
</td><td id="viewtime" style="display:none">
<bean:message key='hmuster.label.from'/><input type="text" name="startTime"  extra="editor" value="${trainMovementForm.startTime}" onblur="timeCheck(this);" style="width:100px;font-size:10pt;text-align:left" dropDown="dropDownDate">
<bean:message key='hmuster.label.to'/><input type="text" name="endTime"  extra="editor" value="${trainMovementForm.endTime}" onblur="timeCheck(this);" style="width:100px;font-size:10pt;text-align:left" dropDown="dropDownDate">
</td><td id="viewtime1" style="display:none">
	<input type="button" value="<bean:message key='button.query'/>" onclick="sub('${trainMovementForm.codeSet}','${trainMovementForm.codeID}');" class="mybutton">
</td>
</tr></table>	
</html:form>
<logic:equal name="trainMovementForm" property="timeFlag" value="5">
<script language="JavaScript">
toggles("viewtime");
toggles("viewtime1");
</script>
</logic:equal>
<script language="javascript">
function table${trainMovementForm.tablename}_r3117_onRefresh(cell,value,record){
	if(record!=null){
		var id = record.getValue("r3101");
		if(id.length<1){
			id=rId;
			rId="";
		}
		var r3127 = record.getValue("r3127");
		if(r3127=='02'||r3127=='03'||r3127.length<1)
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
	var thecodeurl ="/train/plan/eventdes.do?b_event=link`id=r3117`tablename=r31`read="+readonly+"`flag=add`classid="+id;
	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
	openw(iframe_url);     
}

function trainBoard(){
	var tablename,table,dataset,record;
	tablename="table${trainMovementForm.tablename}";
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
	var thecodeurl ="/selfservice/infomanager/board/searchboard.do?b_add=link`trainid="+r3101+"`flag=1`chflag=1`titlename="+getEncodeStr(record.getValue("r3130")+"通知");
	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
    var return_vo= window.showModalDialog(iframe_url,"", 
              	"dialogWidth:800px; dialogHeight:600px;resizable:yes;center:yes;scroll:yes;status:no");
}

function trainBoards(flag){
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
	if(flag=='1'){
		if(!confirm(TRAIN_CALSS_APPLY))
			return;
	}else if(flag=='2'){
		if(!confirm(TRAIN_CALSS_ISSUE))
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
	  		var request=new Request({method:'post',asynchronous:false,onSuccess:Success,functionId:'2020020105'},hashvo);
	  if(vflag=='2')
		  	var request=new Request({method:'post',asynchronous:false,onSuccess:Success,functionId:'2020020117'},hashvo);
	  function Success(outparameters)
		{
		  var flag=outparameters.getValue("msg");
		  if(flag=='true')
		  {
		    alert(TRAIN_CLASS_SUCCESS);
		    trainMovementForm.action="/train/plan/searchCreatPlanList.do?b_query=query&codeset=${trainMovementForm.codeSet}&code=${trainMovementForm.codeID}&model=2";
		    trainMovementForm.submit();
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