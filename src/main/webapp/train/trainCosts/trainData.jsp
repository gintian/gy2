<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.train.b_plan.PlanTrainForm"%>
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
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/common.js"></script>
<script language="JavaScript" src="./planTrain.js"></script>
<script language="javascript" src="/js/dict.js"></script> 
<style type="text/css">  
.selectPre{
	position:absolute;
    left:350px;
    <%if("hcm".equalsIgnoreCase(userView.getBosflag())){%>
    top:6px;
    <%}else{%>
    top:1px;
    <%}%>
}
</style>
<script language="JavaScript">
function executeOutFile(flag){
	var tablename,table,size,fieldWidths,whl_sql;       
    tablename="table${planTrainForm.tablename}";
    table=$(tablename);
    size=${planTrainForm.fieldSize};
    for(var i=1;i<size;i++){
        var width=table.getColWidth(table,i);
        if(width!=null)
	       fieldWidths+="/"+width;
    }
    var hashvo=new ParameterSet();
	hashvo.setValue("fieldWidths",fieldWidths.substring(10));
	hashvo.setValue("tablename",'${planTrainForm.tablename}');
	var In_paramters="flag="+flag;  
	var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:showfile,functionId:'2020040023'},hashvo);
}
//输出 EXCEL OR PDF
function showfile(outparamters){
	var outName=outparamters.getValue("outName");
	var flag=outparamters.getValue("flag");
	if(flag==1)
		var win=open("/servlet/vfsservlet?fromjavafolder=true&fileid="+outName,"pdf");
	else{
		var win=open("/servlet/vfsservlet?fromjavafolder=true&fileid="+outName,"excel");
	}
}
function setColcul(){
	var thecodeurl="/general/inform/emp/batch/calculation.do?b_query=link&unit_type=5&setname=R45&a_code=UN&infor=5";
    var return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:450px; dialogHeight:430px;resizable:no;center:yes;scroll:no;status:no");
    if(!return_vo)return;
    var tablename,table,dataset,preno,bmainset;
	tablename="table${planTrainForm.tablename}";
    table=$(tablename);
    dataset=table.getDataset();
    record=dataset.getCurrent();
    if(!record)
	    return;
    var r2501=record.getValue("r2501");
    var b0110=record.getValue("b0110");
    var e0122=record.getValue("e0122");
  	parent.frames['ril_body2'].location="/train/trainCosts/trainCosts.do?b_menu=link&r2501="+r2501+"&b0110="+b0110+"&e0122="+e0122;
}
</script>
<style>
body{padding-top: 5px;padding-left: 5px;}
</style>
<html:form action="/train/trainCosts/trainData">
<bean:define id="rflag" name="trainCostsForm" property="returnvalue" />
<hrms:dataset name="planTrainForm" property="itemlist" scope="session" setname="${planTrainForm.tablename}"  
setalias="data_table" readonly="false" editable="true" select="false" sql="${planTrainForm.strsql}"  
pagerows="${planTrainForm.pagerows}" buttons="movefirst,prevpage,nextpage,movelast">
	<hrms:commandbutton name="printExcel" hint="" functionId="" refresh="true" type="all_change" setname="${planTrainForm.tablename}" onclick="executeOutFile('0');" function_id="323501">
     <bean:message key='goabroad.collect.educe.excel'/>
    </hrms:commandbutton>
	<hrms:commandbutton name="setformula"  functionId="" refresh="true" type="all_change" setname="" onclick="setColcul();" function_id="323502" >
		<bean:message key='formula.set'/>
	</hrms:commandbutton> 
	<%if("dxt".equals(rflag)&&(bosflag.equals("hl")||bosflag.equals("hcm"))){%>
	 <hrms:commandbutton name="returnButton" hint="" functionId=""   refresh="true" type="selected" setname="${planTrainForm.tablename}" onclick="returnFirst();" function_id="" >
	    <bean:message key="reportcheck.return"/>
	</hrms:commandbutton>
	<%} %> 
</hrms:dataset>
<table class="selectPre">
<tr><td style="padding-top: 5px;">
<bean:message key="sys.export.status"/>
<hrms:optioncollection name="planTrainForm" property="flaglist" collection="list" />
<html:select name="planTrainForm" property="spflag" onchange="changesReload('3','${planTrainForm.a_code}');" style="width:80px;text-align:left">
	<html:options collection="list" property="dataValue" labelProperty="dataName"/>
</html:select>
</td><td style="padding-top: 5px;">
<bean:message key="sys.export.job_time"/>
<hrms:optioncollection name="planTrainForm" property="timelist" collection="tlist" />
<html:select name="planTrainForm" property="timeflag" style="width:80px;text-align:left" onchange="timeFlagChange(this,'3','${planTrainForm.a_code}');">
	<html:options collection="tlist" property="dataValue" labelProperty="dataName"/>
</html:select>
</td><td id="viewtime" style="display:none">
<bean:message key='hmuster.label.from'/><input type="text" name="startime"  extra="editor" value="${planTrainForm.startime}" onblur="timeCheck(this);" style="width:100px;text-align:left" dropDown="dropDownDate">
<bean:message key='hmuster.label.to'/><input type="text" name="endtime"  extra="editor" value="${planTrainForm.endtime}" onblur="timeCheck(this);" style="width:100px;text-align:left" dropDown="dropDownDate">
&nbsp;&nbsp;<input type="button" value="<bean:message key='infor.menu.query'/>" class="mybutton" onclick="changesReload('3','${planTrainForm.a_code}');">
</td>
</tr></table>		
</html:form>
<logic:equal name="planTrainForm" property="timeflag" value="04">
<script language="JavaScript">
toggles("viewtime");
</script>
</logic:equal>
<logic:equal name="planTrainForm" property="novalue" value="0">
<script language="JavaScript">
parent.frames['ril_body2'].location="/train/trainCosts/trainCosts.do?b_menu=link&r2501=";
</script>
</logic:equal>
<script language="javascript">
function table${planTrainForm.tablename}_onRowClick(table){
	var dataset=table.getDataset();	
   	var record=dataset.getCurrent();
   	if(!record)
	    return;
    var r2501=record.getValue("r2501");
    var b0110=record.getValue("b0110");
    var e0122=record.getValue("e0122");
  	parent.frames['ril_body2'].location="/train/trainCosts/trainCosts.do?b_menu=link&r2501="+r2501+"&b0110="+b0110+"&e0122="+e0122;
}
function returnFirst(){
   	self.parent.location= "/general/tipwizard/tipwizard.do?br_train=link";
}
</script>

