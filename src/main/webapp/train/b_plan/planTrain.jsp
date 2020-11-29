<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.taglib.CommonData"%>
<%@ page import="com.hjsj.hrms.actionform.train.b_plan.PlanTrainForm,com.hjsj.hrms.utils.PubFunc"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/validateDate.js"></script>
<script language="JavaScript" src="/js/common.js"></script>
<script language="JavaScript" src="./planTrain.js"></script>
<script language="javascript" src="/js/dict.js"></script> 
<script type="text/javascript" src="../../js/hjsjUrlEncode.js"></script>
<%
PlanTrainForm planTrainForm = (PlanTrainForm)session.getAttribute("planTrainForm"); 
UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String bosflag="";
	if(userView != null){	  
	  	bosflag=userView.getBosflag();
	  	bosflag=bosflag!=null?bosflag:"";                
	}
	boolean flag = true;
if(planTrainForm.getModel().equals("1")){
	
	if(!userView.isSuper_admin()){
		if(userView.getStatus()==0){
				String codeall = userView.getUnit_id();
				if(codeall!=null&&codeall.length()>2){
					codeall=PubFunc.getTopOrgDept(codeall);
					String tmp[]=codeall.split("`");
					StringBuffer codevalue=new StringBuffer();
					for(int i=tmp.length-1;i>=0;i--){
						String t = tmp[i];
						if(t.indexOf("UN")!=-1){
							codevalue.append("`"+t.substring(2));
						}
					}
					if(codevalue.length()==0)
						flag=false;
				}else{
					if(!"UN".equalsIgnoreCase(userView.getManagePrivCode())){
						flag = false;
					}
				}
		}else{
			if(!"UN".equalsIgnoreCase(userView.getManagePrivCode())){
				flag = false;
			}
		}
}
%>
<style type="text/css"> 
.selectPre{
	position:absolute;
    left:350px;
    <%if("hcm".equalsIgnoreCase(userView.getBosflag())){%>
    top:40px;
    <%} else {%>
    top:30px;
    <%}%>
}
.selectPre1{
	position:absolute;
    left:350px;
    top:10px;
}
</style>
<%}else{%>
<style type="text/css"> 
.selectPre{
	position:absolute;
    left:440px;
     <%if("hcm".equalsIgnoreCase(userView.getBosflag())){%>
    top:40px;
    <%} else {%>
    top:30px;
    <%}%>
}
.selectPre1{
	position:absolute;
    left:440px;
    top:10px;
}
</style>
<%}%>
<script language="JavaScript">
function outTemplete(){
	var hashvo=new ParameterSet();	
	hashvo.setValue("model","${planTrainForm.model }");
	var request=new Request({method:'post',asynchronous:false,onSuccess:showTfile,functionId:'2020050016'},hashvo);
}
function showTfile(outparamters)
{
	var outName=outparamters.getValue("outName");
	window.location.target="_blank";
	window.location.href = "/servlet/vfsservlet?fromjavafolder=true&fileid="+outName;
}
function inputTemplete(){
	var theurl='/train/b_plan/planTrain/import.do?br_selectfile=link';
    var iframe_url="/general/query/common/iframe_query.jsp?src="+theurl;
    var return_vo= window.showModalDialog(iframe_url, 'mytree_win', 
      		"dialogWidth:500px; dialogHeight:300px;resizable:no;center:yes;scroll:yes;status:no");		    				
   if(return_vo){
   		planTrainForm.action='/train/b_plan/planTrain/import.do?b_exedata=link';
      planTrainForm.submit();
   }    
}

function executeOutFile(flag){
	var tablename,table,size,fieldWidths,whl_sql;       
    tablename="table${planTrainForm.tablename}";
    table=$(tablename);
    size=${planTrainForm.fieldSize};
    for(var i=1;i<size+2;i++){
        var width=table.getColWidth(table,i);
        if(width!=null)
	       fieldWidths+="/"+width;
    }
    var hashvo=new ParameterSet();
	hashvo.setValue("fieldWidths",fieldWidths.substring(10));
	hashvo.setValue("tablename","${planTrainForm.tablename}");
	hashvo.setValue("model","${planTrainForm.model}");
	var In_paramters="flag="+flag;  
	var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:showfile,functionId:'0521010011'},hashvo);
}
//输出 EXCEL OR PDF
function showfile(outparamters){
	var outName=outparamters.getValue("outName");
	outName = getDecodeStr(outName);
	var flag=outparamters.getValue("flag");
	if(flag==1)
		var win=open("/servlet/vfsservlet?fromjavafolder=true&fileid="+outName,"pdf");
	else{
		window.location.target="_blank";
		window.location.href="/servlet/vfsservlet?fromjavafolder=true&fileid="+outName;
	}
}


</script>

<html:form action="/train/b_plan/planTrain">
<html:hidden name="planTrainForm" property="searchstr"/>
<table><tr><td>
<table><tr><td>
	<hrms:menubar menu="menu1" id="menubar1">
	  <logic:equal value="1" name="planTrainForm" property="model">
		<hrms:menuitem name="file" label="menu.file" function_id="323207,323208,323205,323206">						
			<hrms:menuitem name="mitem1" label="button.createpdf" icon="/images/print.gif" url="executeOutFile(1)" function_id="323207"/>
			<hrms:menuitem name="mitem2" label="button.createescel" icon="/images/print.gif" url="executeOutFile(2)" function_id="323208"/>
			<!-- 16596   ,323209
			hrms:menuitem name="mitem3" label="kq.report.pagesetup" icon="/images/prop_ps.gif" url="setPageFormat('${planTrainForm.username}');"  function_id="323209"/>
			-->					
				<%if(flag){ %>
				<hrms:priv func_id="323205">
					<hrms:menuitem name="mitem2" label="button.download.template" icon="/images/add_del.gif" url="outTemplete();"  />
				</hrms:priv>
				<hrms:priv func_id="323206">
					<hrms:menuitem name="mitem3" label="import.tempData" icon="/images/deal.gif" url="inputTemplete();"  />					
				</hrms:priv>
				<%} %>
		</hrms:menuitem>
		<hrms:menuitem name="edit" label="infor.menu.edit" function_id="323200,323201,323202">	
			<hrms:menuitem name="mitem6" label="button.new.add" icon="/images/add.gif" command="" url="addPlan('${planTrainForm.model}','${planTrainForm.a_code}');" function_id="323200"/>		<!-- url="append('${planTrainForm.model}','${planTrainForm.a_code}');" -->					
			<hrms:menuitem name="mitem5" label="button.save" icon="/images/save_edit.gif" command="savedata" function_id="323201"/>					
			<hrms:menuitem name="mitem4" label="button.delete" icon="/images/delete.gif" command="delselected" function_id="323202"/>
		</hrms:menuitem>
		<hrms:menuitem name="search" label="button.query" function_id="323204,32320a,32320b" >	
			<hrms:menuitem name="mitem20" label="label.zp_exam.sort" icon="/images/quick_query.gif" url="sortRecord('${planTrainForm.model}','${planTrainForm.a_code}');" function_id="323204"/>	
			<hrms:menuitem name="mitem19" label="button.c.query" icon="/images/quick_query.gif" url="searchInform('${planTrainForm.model}','${planTrainForm.a_code}',4);" function_id="32320a"/>
			<hrms:menuitem name="mitem3" label="infor.menu.gquery" icon="/images/quick_query.gif" url="" function_id="32320b">
      		<%int n=0;%>
      		<logic:iterate id="element"  name="planTrainForm"  property="searchlist" indexId="index">  
      		 <%
            	CommonData searhcitem=(CommonData)pageContext.getAttribute("element");
            	String searchname=searhcitem.getDataValue();
            	String id=searhcitem.getDataName();
            	String a_code = (String)request.getParameter("a_code");
            	String searchgeneral = "searchGeneral('"+id+"','"+a_code+"','"+planTrainForm.getModel()+"');";
            %>
      		<hrms:menuitem name='<%="mitem"+n%>' label='<%=searchname%>' icon="" url="<%=searchgeneral%>" command="" enabled="true" visible="true"/>
      		<%n++;%>
      		</logic:iterate>
      			<%if(n>0){%>
      		<hrms:menuitem name='<%="mitem"+(n+1)%>' label='general.inform.search.themore' icon="" url="searchComm(4,'${planTrainForm.model}','${planTrainForm.a_code}');" command="" enabled="true" visible="true"/>
      		<%} %>
      		</hrms:menuitem>
		</hrms:menuitem>
		</logic:equal>
		<logic:equal value="2" name="planTrainForm" property="model">
		   <hrms:menuitem name="file" label="menu.file" function_id="323217,323218,31321c">						
			<hrms:menuitem name="mitem1" label="button.createpdf" icon="/images/print.gif" url="executeOutFile(1)" function_id="323217"/>
			<hrms:menuitem name="mitem2" label="button.createescel" icon="/images/print.gif" url="executeOutFile(2)" function_id="323218"/>
			<!-- 16596   ,32321b
			hrms:menuitem name="mitem3" label="kq.report.pagesetup" icon="/images/prop_ps.gif" url="setPageFormat('${planTrainForm.username}');" function_id="32321b" />
			-->		
			<hrms:menuitem name="mitem4" label="train.examplan.forcedelete" icon="/images/delete.gif" command="delHistoryPlan" function_id="32321c"/>			
		</hrms:menuitem>
		<hrms:menuitem name="edit" label="infor.menu.edit" function_id="323210,323211,323212">	
			<hrms:menuitem name="mitem6" label="button.new.add" icon="/images/add.gif" command="" url="addPlan('${planTrainForm.model}','${planTrainForm.a_code}');" function_id="323210"/>		<!-- url="append('${planTrainForm.model}','${planTrainForm.a_code}');" -->					
			<hrms:menuitem name="mitem5" label="button.save" icon="/images/save_edit.gif" command="savedata" function_id="323211"/>					
			<hrms:menuitem name="mitem4" label="button.delete" icon="/images/delete.gif" command="delselected" function_id="323212"/>
		</hrms:menuitem>
		<hrms:menuitem name="search" label="button.query" function_id="323216,323219,32321a">	
			<hrms:menuitem name="mitem20" label="label.zp_exam.sort" icon="/images/quick_query.gif" url="sortRecord('${planTrainForm.model}','${planTrainForm.a_code}');" function_id="323216"/>	
			<hrms:menuitem name="mitem19" label="button.c.query" icon="/images/quick_query.gif" url="searchInform('${planTrainForm.model}','${planTrainForm.a_code}',4);" function_id="323219"/>
			<hrms:menuitem name="mitem3" label="infor.menu.gquery" icon="/images/quick_query.gif" url="" function_id="32321a">
      		<%int n=0;%>
      		<logic:iterate id="element"  name="planTrainForm"  property="searchlist" indexId="index">  
      		 <%
            	CommonData searhcitem=(CommonData)pageContext.getAttribute("element");
            	String searchname=searhcitem.getDataValue();
            	String id=searhcitem.getDataName();
            	String a_code = (String)request.getParameter("a_code");
            	String searchgeneral = "searchGeneral('"+id+"','"+a_code+"','"+planTrainForm.getModel()+"');";
            %>
      		<hrms:menuitem name='<%="mitem"+n%>' label='<%=searchname%>' icon="" url="<%=searchgeneral%>" command="" enabled="true" visible="true"/>
      		<%n++;%>
      		</logic:iterate>
      			<%if(n>0){%>
      		<hrms:menuitem name='<%="mitem"+(n+1)%>' label='general.inform.search.themore' icon="" url="searchComm(4,'${planTrainForm.model}','${planTrainForm.a_code}');" command="" enabled="true" visible="true"/>
      		<%} %>
      		</hrms:menuitem>
		</hrms:menuitem>
		</logic:equal>
	</hrms:menubar>
</td></tr></table>
</td></tr>
<tr><td class="RecordRowTable">
<bean:define id="rflag" name="planTrainForm" property="returnvalue" />
<%if(planTrainForm.getModel().equals("1")){%>
<hrms:dataset name="planTrainForm" property="itemlist" scope="session" setname="${planTrainForm.tablename}"
  setalias="data_table" rowlock="true" rowlockfield="r2509"  rowlockvalues=",01,07,"  select="true" 
  sql="${planTrainForm.strsql}"  pagerows="${planTrainForm.pagerows}" buttons="movefirst,prevpage,nextpage,movelast">
   <hrms:commandbutton name="table" hint="" functionId="" refresh="true" type="selected" function_id="323200" setname="${planTrainForm.tablename}" onclick="addPlan('${planTrainForm.model}','${planTrainForm.a_code}');">
     <bean:message key="button.insert"/>
    </hrms:commandbutton>
	<hrms:commandbutton name="savedata"  functionId="2020050006" refresh="true" type="all-change" setname="${planTrainForm.tablename}" function_id="323201" >
		<bean:message key="button.save"/>
	</hrms:commandbutton>  
	<hrms:commandbutton name="delselected" hint="general.inform.search.confirmed.del" functionId="2020050005" refresh="true" function_id="323202" type="selected" setname="${planTrainForm.tablename}">
     <bean:message key="button.delete"/>
   </hrms:commandbutton>
   <hrms:commandbutton name="appeal" hint="performance.workdiary.approval.ok" functionId="2020050007"   refresh="true" type="selected" setname="${planTrainForm.tablename}"  function_id="323203" >
	    <bean:message key="button.appeal"/>
	</hrms:commandbutton>
	
	<%if("dxt".equals(rflag)&&(bosflag.equals("hl")||bosflag.equals("hcm"))){%>
	 <hrms:commandbutton name="returnButton" hint="" functionId=""   refresh="true" type="selected" setname="${planTrainForm.tablename}" onclick="returnFirst();" function_id="" >
	    <bean:message key="reportcheck.return"/>
	</hrms:commandbutton>
	<%} %>
</hrms:dataset>
<%}else{%>
<hrms:dataset name="planTrainForm" property="itemlist" scope="session" setname="${planTrainForm.tablename}"
  setalias="data_table" rowlock="true" rowlockfield="r2509"  rowlockvalues=",02,03,09,"  select="true" 
  sql="${planTrainForm.strsql}"  pagerows="${planTrainForm.pagerows}" buttons="movefirst,prevpage,nextpage,movelast">
	<hrms:commandbutton name="table" hint="" functionId="" refresh="true" type="selected" function_id="323210" setname="${planTrainForm.tablename}" onclick="addPlan('${planTrainForm.model}','${planTrainForm.a_code}');">
     <bean:message key="button.insert"/>
    </hrms:commandbutton>
	<hrms:commandbutton name="savedata"  functionId="2020050006" refresh="true" type="all-change" setname="${planTrainForm.tablename}" function_id="323211" >
		<bean:message key="button.save"/>
	</hrms:commandbutton>  
	<hrms:commandbutton name="delselected" hint="general.inform.search.confirmed.del" functionId="2020050005" refresh="true" function_id="323212" type="selected" setname="${planTrainForm.tablename}">
     <bean:message key="button.delete"/>
   </hrms:commandbutton>
	<hrms:commandbutton name="bhflag" hint="org.orgpre.orgpretable.bohuiok" functionId="2020050014"   refresh="true" type="selected" setname="${planTrainForm.tablename}"  function_id="323213" >
	   <bean:message key="info.appleal.state2"/>
	</hrms:commandbutton>
	 <hrms:commandbutton name="pzflag" hint="org.orgpre.orgpretable.approvalok" functionId="2020050013"   refresh="true" type="selected" setname="${planTrainForm.tablename}"  function_id="323214" >
	    <bean:message key="info.appleal.state3"/>
	</hrms:commandbutton>
	<hrms:commandbutton name="fbflag" hint="gz.acount.determined.published" functionId="2020050015"   refresh="true" type="selected" setname="${planTrainForm.tablename}"  function_id="323215" >
	    <bean:message key="info.appleal.state9"/>
	</hrms:commandbutton>
	<hrms:commandbutton name="ztflag" hint="gz.acount.determined.stop" functionId="2020050021"   refresh="true" type="selected" setname="${planTrainForm.tablename}"  function_id="32321e" >
	    <bean:message key="label.commend.stop"/>
	</hrms:commandbutton>
	<%if("dxt".equals(rflag)&&(bosflag.equals("hl")||bosflag.equals("hcm"))){%>
	 <hrms:commandbutton name="returnButton" hint="" functionId=""   refresh="true" type="selected" setname="${planTrainForm.tablename}" onclick="returnFirst();" function_id="" >
	    <bean:message key="reportcheck.return"/>
	</hrms:commandbutton>
	<%} %>
	
</hrms:dataset>
   
    <script>
	    var delHistoryPlan=new UpdateCommand("delHistoryPlan");var __t=delHistoryPlan;__t.setAction("/ajax/ajaxService");__t.setFunctionId("2020050019");__t.setHint("确认要删除吗?");var __f=__t.addDatasetInfo(r25,"selected");__f.setFlushDataOnSuccess(true);
    </script>
<%} %>
</td></tr></table>
<%if(planTrainForm.getModel().equals("1")){
int privbutton=0; 
%>
<hrms:priv func_id="323207,323208,323209,323205,323206,323200,323201,323202,323204,32320a,32320b">
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
<hrms:priv func_id="323217,323218,31321c,323210,323211,323212,323216,323219,32321a">
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
<tr><td style="padding-top: 4px;">
<bean:message key="sys.export.status"/>
<hrms:optioncollection name="planTrainForm" property="flaglist" collection="list" />
<html:select name="planTrainForm" property="spflag" onchange="changesReload('${planTrainForm.model}','${planTrainForm.a_code}');" style="width:80px;text-align:left">
	<html:options collection="list" property="dataValue" labelProperty="dataName"/>
</html:select>
</td><td style="padding-top: 4px;">
<bean:message key="sys.export.job_time"/>
<hrms:optioncollection name="planTrainForm" property="timelist" collection="tlist" />
<html:select name="planTrainForm" property="timeflag" style="width:80px;text-align:left" onchange="timeFlagChange(this,'${planTrainForm.model}','${planTrainForm.a_code}');">
	<html:options collection="tlist" property="dataValue" labelProperty="dataName"/>
</html:select>
</td><td id="viewtime" style="display:none">
<bean:message key='hmuster.label.from'/><input type="text" name="startime"  extra="editor" value="${planTrainForm.startime}" id="editor1" style="width:100px;text-align:left" dropDown="dropDownDate"><!-- zhangcq 2016-4-25 -->
<bean:message key='hmuster.label.to'/><input type="text" name="endtime"  extra="editor" value="${planTrainForm.endtime}" id="editor2" style="width:100px;text-align:left" dropDown="dropDownDate">
</td>
<td id="viewtime1" style="display:none">
<input type="button" value="<bean:message key='button.query'/>" class="mybutton" onclick='changesReload("${planTrainForm.model}","${planTrainForm.a_code}");'>
</td>
</tr></table>		
</html:form>
<logic:equal name="planTrainForm" property="timeflag" value="04">
<script language="JavaScript">
toggles("viewtime");
toggles("viewtime1");
</script>
</logic:equal>
<logic:equal name="planTrainForm" property="novalue" value="0">
<script language="JavaScript">
parent.frames['ril_body2'].location="/train/b_plan/train.do?b_query=link&r2501=&model=1";
</script>
</logic:equal>

<script language="javascript">
function table${planTrainForm.tablename}_onRowClick(table){
	var dataset=table.getDataset();	
   	var record=dataset.getCurrent();
   	
   	if(!record){
   		parent.frames['ril_body2'].location="/train/b_plan/train.do?b_query=link&r2501=&model=";
	    return;
	}
    var r2501=record.getValue("r2501");
    var model=record.getValue("model");
    var b0110=record.getValue("b0110");
    var e0122=record.getValue("e0122");
    var r2509=record.getValue("r2509");
  	parent.frames['ril_body2'].location="/train/b_plan/train.do?b_query=link&r2501="+r2501+"&model="+model+"&b0110="+b0110+"&e0122="+e0122+"&spflag="+r2509;
}


function returnFirst(){
   	self.parent.location= "/general/tipwizard/tipwizard.do?br_train=link";
}
<%
	if(request.getParameter("b_exedata")!=null&&request.getParameter("b_exedata").length()>0){
 %>
 	alert("已有${planTrainForm.num }条培训计划被成功导入！");
 	planTrainForm.action = "/train/b_plan/planTrain.do?b_query=link&model=${planTrainForm.model}&a_code=${planTrainForm.a_code}";
 	planTrainForm.submit();
 <%}%>
 
</script>

