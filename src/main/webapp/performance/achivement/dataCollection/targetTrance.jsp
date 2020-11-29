<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hjsj.hrms.actionform.performance.achivement.dataCollection.*"%>
<script language="javascript" src="/js/dict.js"></script> 
<script type="text/javascript">

function downloadTemplate()
{
	var hashvo=new ParameterSet();
	hashvo.setValue("planID",'${dataCollectForm.planId}');
	hashvo.setValue("sql",getEncodeStr(dataCollectForm.sqlWhere.value));
	var request=new Request({method:'post',asynchronous:false,onSuccess:showfile,functionId:'9020020311'},hashvo);
}
function showfile(outparamters)
{
	var outName=outparamters.getValue("outName");
	//var name=outName.substring(0,outName.length-1)+".xls";
	//xus 20/4/30 vfs改造
	var win=open("/servlet/vfsservlet?fromjavafolder=true&fileid="+outName,"excel");
}
function importExcel()
{
	var target_url="/performance/achivement/dataCollection/importExcel.do?br_import=link";
 	var iframe_url="/general/query/common/iframe_query.jsp?src="+target_url;
	var return_vo= window.showModalDialog(iframe_url, "importExcel", 
	              "dialogWidth:450px; dialogHeight:210px;resizable:no;center:yes;scroll:no;status:no");	
	if(!return_vo)
		return;	   
	if(return_vo.flag=="true")
	{	
		dataCollectForm.action="/performance/achivement/dataCollection/dataCollect.do?b_query2=link";
		dataCollectForm.submit();
	}		
}
function save_lzw()
{
	var save_bt = document.getElementById('buttonsave');
	if(save_bt!=null)
	{
	   
		save_bt.fireEvent("onclick");
		var thevo=new Object();
		thevo.flag="true";
        parent.window.returnValue=thevo;
        parent.window.close();
	}
}
</script>
<%
	DataCollectForm form=(DataCollectForm)session.getAttribute("dataCollectForm");
	String isFromTarget = (String)form.getIsFromTarget();
	String isHaveRecords= (String)form.getIsHaveRecords();
%>
<html:form action="/performance/achivement/dataCollection/dataCollect"> 
	<html:hidden name="dataCollectForm" property="sql"/>
	<html:hidden name="dataCollectForm" property="sqlWhere"/>
	<hrms:dataset name="dataCollectForm" property="fieldlist"
			scope="session" setname="p04" setalias="data_table" readonly="false"
			editable="true" select="true" sql="${dataCollectForm.sqlWhere}"
			pagerows="${dataCollectForm.pagerows}"
			buttons="movefirst,prevpage,nextpage,movelast">
		<%if(isFromTarget.equals("0")) {%>
		 <%if(isHaveRecords.equals("1")) {%>
			<hrms:commandbutton name="save" functionId="9020020313"
				hint="hire.confirm.save2" function_id="" refresh="true"
				type="all-change" setname="p04">
				<bean:message key="button.save" />
			</hrms:commandbutton>		
				<%} %>
			<hrms:commandbutton name="downTemplate" function_id="" hint=""
				functionId="" visible="true" refresh="true" type="selected"
				setname="p04" onclick="downloadTemplate()">
				<bean:message key="button.download.template" />
			</hrms:commandbutton>
			<hrms:commandbutton name="import" function_id="" hint=""
				functionId="" visible="true" refresh="true" type="selected"
				setname="p04" onclick="importExcel()">
				<bean:message key="import.tempData" />
			</hrms:commandbutton>
			<%}else{ %>
			<%if(isHaveRecords.equals("1")) {%>
				<hrms:commandbutton name="save" functionId="9020020313"
				 function_id=""  refresh="true"
				type="all-change" setname="p04" >
				<bean:message key="button.save" />
			</hrms:commandbutton>	
			<hrms:commandbutton name="ookk" function_id="" hint=""
				functionId="" visible="true" refresh="true" type="selected"
				setname="p04" onclick="save_lzw()">
				<bean:message key="button.ok" />
			</hrms:commandbutton>
			<%} %>
			<hrms:commandbutton name="ccll" function_id="" hint=""
				functionId="" visible="true" refresh="true" type="selected"
				setname="p04" onclick="window.close();">
				<bean:message key="button.close" />
			</hrms:commandbutton>
			<%} %>
		</hrms:dataset>
</html:form>