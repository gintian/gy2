<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hjsj.hrms.actionform.train.traincourse.TrainCourseForm"%>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/validateDate.js"></script>
<script language="JavaScript" src="/js/common.js"></script>
<script language="JavaScript" src="./traindata.js"></script>
<script language="javascript" src="/js/dict.js"></script> 
<script type="text/javascript" src="../../js/hjsjUrlEncode.js"></script>
<%
TrainCourseForm trainCourseForm = (TrainCourseForm)session.getAttribute("trainCourseForm");
UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String bosflag="";
	if(userView != null){	  
	  	bosflag=userView.getBosflag();
	  	bosflag=bosflag!=null?bosflag:"";                
	}
if(trainCourseForm.getModel().equals("1")){%> 
<style type="text/css"> 
.selectPre{
	position:absolute;
    left:360px;
    <%if("hcm".equalsIgnoreCase(userView.getBosflag())){%>
     top:40px;
    <%}else{%>
     top:30px;
    <%}%>
    width:620px;
}
.selectPre1{
	position:absolute;
    left:360px;
    top:10px;
    width:620px;
}
</style>
<%}else{%>
<style type="text/css"> 
.selectPre{
	position:absolute;
    left:390px;
    <%if("hcm".equalsIgnoreCase(userView.getBosflag())){%>
     top:40px;
    <%}else{%>
     top:30px;
    <%}%>
}
.selectPre1{
	position:absolute;
    left:360px;
    top:10px;
}

</style>
<%}%>
<script language="JavaScript">
var encryptParam = '${trainCourseForm.encryptParam}';
function executeOutFile(flag){
	var tablename,table,size,fieldWidths,whl_sql;       
    tablename="table${trainCourseForm.tablename}";
    table=$(tablename);
    size=${trainCourseForm.fieldSize};
    for(var i=1;i<size+2;i++){
        var width=table.getColWidth(table,i);
        if(width!=null)
	       fieldWidths+="/"+width;
    }
    var hashvo=new ParameterSet();
	hashvo.setValue("fieldWidths",fieldWidths.substring(10));
	hashvo.setValue("tablename",'${trainCourseForm.tablename}');
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
</script>

<html:form action="/train/traincourse/traindata">
<html:hidden name="trainCourseForm" property="searchstr"/>
<table><tr><td>
<table><tr><td>
	<hrms:menubar menu="menu1" id="menubar1">
	<%if(trainCourseForm.getModel().equals("1")){%>
		<hrms:menuitem name="file" label="conlumn.mediainfo.filename" function_id="323105,323106">						
			<hrms:menuitem name="mitem1" label="button.createpdf" icon="/images/print.gif" url="executeOutFile(1)" function_id="323105"/>
			<hrms:menuitem name="mitem2" label="button.createescel" icon="/images/print.gif" url="executeOutFile(2)" function_id="323106"/>
			<!-- 16596   ,323107
			hrms:menuitem name="mitem3" label="kq.report.pagesetup" icon="/images/prop_ps.gif" url="setPageFormat('${trainCourseForm.username}');" function_id="323107"/>
			 --> 					
		</hrms:menuitem>
		<hrms:menuitem name="edit" label="edit_report.status.bj" function_id="323100,323102,323108,323104">	
			<hrms:menuitem name="mitem6" label="button.new.add" icon="/images/add.gif" command="" function_id="323100" url="add('${trainCourseForm.a_code}','${trainCourseForm.model}');"/>		<!-- append('${trainCourseForm.model}','${trainCourseForm.a_code}'); -->
			<hrms:menuitem name="mitem4" label="button.delete" icon="/images/delete.gif" function_id="323102" command="delselected" />
			<hrms:menuitem name="mitem19" label="button.query" icon="/images/quick_query.gif"  url="searchInform('${trainCourseForm.model}','${trainCourseForm.a_code}');" function_id="323108"/>
			<hrms:menuitem name="mitem20" label="label.zp_exam.sort" icon="/images/quick_query.gif" function_id="323104" url="sortRecord('${trainCourseForm.model}','${trainCourseForm.a_code}');"/>	
		</hrms:menuitem>
	<%}else{%>
		<hrms:menuitem name="file" label="conlumn.mediainfo.filename" function_id="323116,323117">						
			<hrms:menuitem name="mitem1" label="button.createpdf" icon="/images/print.gif" url="executeOutFile(1)" function_id="323116"/>
			<hrms:menuitem name="mitem2" label="button.createescel" icon="/images/print.gif" url="executeOutFile(2)" function_id="323117"/>
			<!-- ,323118
			hrms:menuitem name="mitem3" label="kq.report.pagesetup" icon="/images/prop_ps.gif" url="setPageFormat('${trainCourseForm.username}');" function_id="323118"/>
			 -->					
		</hrms:menuitem>
		<hrms:menuitem name="edit" label="edit_report.status.bj" function_id="323110,323112,323119,323115">	
			<hrms:menuitem name="mite" label="button.new.add" icon="/images/add.gif" command="" function_id="323110" url="add('${trainCourseForm.a_code}','${trainCourseForm.model}');"/>		<!-- append('${trainCourseForm.model}','${trainCourseForm.a_code}'); -->
			<hrms:menuitem name="mitem4" label="button.delete" icon="/images/delete.gif" function_id="323112" command="delselected" />
			<hrms:menuitem name="mitem19" label="button.query" icon="/images/quick_query.gif"  url="searchInform('${trainCourseForm.model}','${trainCourseForm.a_code}');" function_id="323119"/>
			<hrms:menuitem name="mitem20" label="label.zp_exam.sort" icon="/images/quick_query.gif" function_id="323115" url="sortRecord('${trainCourseForm.model}','${trainCourseForm.a_code}');"/>	
		</hrms:menuitem>
	<%} %>	
	</hrms:menubar>
</td></tr></table>
</td></tr>
<tr><td class="RecordRowTable">
<bean:define id="rflag" name="trainCourseForm" property="returnvalue" />
<%if(trainCourseForm.getModel().equals("1")){%>
<hrms:dataset name="trainCourseForm" property="itemlist" scope="session" setname="${trainCourseForm.tablename}"  
setalias="data_table" rowlock="true" rowlockfield="r3127"  rowlockvalues=",01,07," select="true" 
sql="${trainCourseForm.strsql}" pagerows="${trainCourseForm.pagerows}" buttons="movefirst,prevpage,nextpage,movelast">

	<hrms:commandbutton name="table" hint="" functionId="" refresh="true" function_id="323100"  type="selected" setname="${trainCourseForm.tablename}" onclick="add('${trainCourseForm.a_code}','${trainCourseForm.model}');">
     <bean:message key="button.insert"/>
    </hrms:commandbutton>
	<hrms:commandbutton name="savedata"  functionId="2020020214" refresh="true" type="all-change" setname="${trainCourseForm.tablename}" function_id="323101">
		<bean:message key="button.save"/>
	</hrms:commandbutton>  
	<hrms:commandbutton name="delselected" hint="general.inform.search.confirmed.del" functionId="2020020213" refresh="true" function_id="323102" type="selected" setname="${trainCourseForm.tablename}">
     <bean:message key="button.delete"/>
   </hrms:commandbutton>
   <hrms:commandbutton name="appeal" hint="" functionId=""   refresh="true" function_id="323103" type="selected" setname="${trainCourseForm.tablename}" onclick="trainBoards('1')">
	    <bean:message key="button.appeal"/>
	</hrms:commandbutton>
	<%if("dxt".equals(rflag)&&(bosflag.equals("hl") || bosflag.equals("hcm"))){%>
	 <hrms:commandbutton name="returnButton" hint="" functionId=""   refresh="true" type="selected" setname="${trainCourseForm.tablename}" onclick="returnFirst();" function_id="" >
	    <bean:message key="reportcheck.return"/>
	</hrms:commandbutton>
	<%} %>
</hrms:dataset>
<%}else{%>
<hrms:dataset name="trainCourseForm" property="itemlist" scope="session" setname="${trainCourseForm.tablename}"  
setalias="data_table" rowlock="true" rowlockfield="r3127"  rowlockvalues=",02,03," select="true" 
sql="${trainCourseForm.strsql}" pagerows="${trainCourseForm.pagerows}" buttons="movefirst,prevpage,nextpage,movelast">
	<hrms:commandbutton name="table" hint="" functionId="" refresh="true" function_id="323110"  type="selected" setname="${trainCourseForm.tablename}" onclick="add('${trainCourseForm.a_code}','${trainCourseForm.model}');">
     <bean:message key="button.insert"/>
    </hrms:commandbutton>
	<hrms:commandbutton name="savedata"  functionId="2020020214" refresh="true" type="all-change" setname="${trainCourseForm.tablename}" function_id="323111">
		<bean:message key="button.save"/>
	</hrms:commandbutton>  
	<hrms:commandbutton name="delselected" hint="general.inform.search.confirmed.del" functionId="2020020213" refresh="true" function_id="323112" type="selected" setname="${trainCourseForm.tablename}">
     <bean:message key="button.delete"/>
   </hrms:commandbutton>
	<hrms:commandbutton name="bhflag" hint="org.orgpre.orgpretable.bohuiok" functionId="2020020218"   refresh="true" type="selected" setname="${trainCourseForm.tablename}"  function_id="323113" >
	   <bean:message key="info.appleal.state2"/>
	</hrms:commandbutton>
	 <hrms:commandbutton name="pzflag" hint="" functionId=""   refresh="true" type="selected" setname="${trainCourseForm.tablename}"  function_id="323114" onclick="trainBoards('2')">
	    <bean:message key="info.appleal.state3"/>
	</hrms:commandbutton>
	<%if("dxt".equals(rflag)&&(bosflag.equals("hl") || bosflag.equals("hcm"))){%>
	 <hrms:commandbutton name="returnButton" hint="" functionId=""   refresh="true" type="selected" setname="${trainCourseForm.tablename}" onclick="returnFirst();">
	    <bean:message key="reportcheck.return"/>
	</hrms:commandbutton>
	<%} %>
</hrms:dataset>
<%} %>
</td></tr></table>
<%if(trainCourseForm.getModel().equals("1")){
int privbutton=0; 
%>
<hrms:priv func_id="323105,323106,323100,323102,323108,323104">
<% privbutton=1;%>
<style type="text/css"> 
.RecordRowTable {
	border: inset 0px #94B6E6;
	font-size: 12px;
	border-collapse:collapse; 
}
</style>
<table class="selectPre">
</hrms:priv>
<%if(privbutton!=1){%>
<style type="text/css"> 
.RecordRowTable {
	border: inset 0px #94B6E6;
	BORDER-TOP: #94B6E6 1pt solid;
	font-size: 12px;
	border-collapse:collapse; 
}
</style>
<table class="selectPre1">
<%}%>
<%}else{
int privbutton=0; 
%>
<hrms:priv func_id="323116,323117,323110,323112,323119,323115">
<% privbutton=1;%>
<style type="text/css"> 
.RecordRowTable {
	border: inset 0px #94B6E6;
	font-size: 12px;
	border-collapse:collapse; 
}
</style>
<table class="selectPre">
</hrms:priv>
<%if(privbutton!=1){%>
<style type="text/css"> 
.RecordRowTable {
	border: inset 0px #94B6E6;
	BORDER-TOP: #94B6E6 1pt solid;
	font-size: 12px;
	border-collapse:collapse; 
}
</style>
<table class="selectPre1">
<%}%>
<%} %>
<tr><td width="110" style="padding-top: 4px;">
<bean:message key="sys.export.status"/>
<hrms:optioncollection name="trainCourseForm" property="flaglist" collection="list" />
<html:select name="trainCourseForm" property="spflag" onchange="changesReload('${trainCourseForm.model}','${trainCourseForm.a_code}');" style="width:80px;text-align:left">
	<html:options collection="list" property="dataValue" labelProperty="dataName"/>
</html:select>
</td><td align="left" style="padding-top: 4px;">
<bean:message key="sys.export.job_time"/>
<hrms:optioncollection name="trainCourseForm" property="timelist" collection="tlist" />
<html:select name="trainCourseForm" property="timeflag" style="width:80px;text-align:left" onchange="timeFlagChange(this,'${trainCourseForm.model}','${trainCourseForm.a_code}');">
	<html:options collection="tlist" property="dataValue" labelProperty="dataName"/>
</html:select>
</td><td id="viewtime" style="display:none;padding-top: 4px;">
<bean:message key='hmuster.label.from'/><input type="text" name="startime"  extra="editor" value="${trainCourseForm.startime}" id="editor1" style="width:100px;text-align:left" dropDown="dropDownDate"> <!-- zhangcq 2016-4-25 -->
<bean:message key='hmuster.label.to'/><input type="text" name="endtime"  extra="editor" value="${trainCourseForm.endtime}" id="editor2" style="width:100px;text-align:left" dropDown="dropDownDate">
</td><td id="viewtime1" style="display:none;padding-top: 4px;">
	<input type="button" value="<bean:message key='button.query'/>" class="mybutton" onclick="changesReload('${trainCourseForm.model}','${trainCourseForm.a_code}');">
</td>
</tr></table>		
</html:form>
<logic:equal name="trainCourseForm" property="timeflag" value="04">
<script language="JavaScript">
toggles("viewtime");
toggles("viewtime1");
</script>
</logic:equal>
<script type="text/javascript">
function tabler31_trainplan_onRefresh(cell,value,record){
	if(record!=null){
		var r3125 = record.getString("r3125");
		var r3101 = record.getString("r3101");
		var trainplan = record.getString("trainplan");
		if(trainplan!=null&&trainplan.length>0)
			cell.innerHTML="<div style=\"cursor:hand;font-size:9pt;color:#1B4A98\" onclick=\"getTrainPlanValue('"+r3101+"')\">"+trainplan+"</div>";
		else
			cell.innerHTML="<div style=\"cursor:hand;font-size:9pt;color:#1B4A98\" onclick=\"getTrainPlanValue('"+r3101+"');\">未对应</div>";
	}
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
	    var return_vo= window.showModalDialog(thecodeurl, "", 
	              "dialogWidth:300px; dialogHeight:405px;resizable:yes;center:yes;scroll:no;status:no");
	    if(return_vo!=null){
	    	var arr = return_vo.split("`");
	    	if(arr.length!=2)
	    		return false;
	    	record.setValue("r3125",arr[0]);
		    record.setValue("trainplan",arr[1]);     
	    }
    }
}
function tabler31_r3117_onRefresh(cell,value,record){
	if(record!=null){
		var id = record.getValue("r3101");
		var r3127 = record.getValue("r3127");
		var model = ${trainCourseForm.model};
		if(model == '1'){
			if(r3127=='02'||r3127=='03'||r3127=='04'||r3127=='06')
				cell.innerHTML="&nbsp;&nbsp;&nbsp;&nbsp;<img src=\"/images/view.gif\" onclick=\"eventDesc('"+id+"','0')\" border=\"0\"  style=\"cursor:hand;\">";
			else
				cell.innerHTML="&nbsp;&nbsp;&nbsp;&nbsp;<img src=\"/images/edit.gif\" onclick=\"eventDesc('"+id+"','1')\" border=\"0\"  style=\"cursor:hand;\">";
		}else{
		if(r3127=='04'||r3127=='06')
			cell.innerHTML="&nbsp;&nbsp;&nbsp;&nbsp;<img src=\"/images/view.gif\" onclick=\"eventDesc('"+id+"','0')\" border=\"0\"  style=\"cursor:hand;\">";
		else
			cell.innerHTML="&nbsp;&nbsp;&nbsp;&nbsp;<img src=\"/images/edit.gif\" onclick=\"eventDesc('"+id+"','1')\" border=\"0\"  style=\"cursor:hand;\">";
		}
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
	var thecodeurl ="/train/traincourse/traindata.do?b_event=link`id=r3117`tablename=r31`read="+readonly+"`flag=add`classid="+id;
	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
	openw(iframe_url);     
}

</script>