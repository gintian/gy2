<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.gz.gz_analyse.HistoryDataForm"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hjsj.hrms.utils.PubFunc"%>
<%
    HistoryDataForm historyDataForm=(HistoryDataForm)session.getAttribute("historyDataForm");
    String isOnlySet = historyDataForm.getIsOnlySet(); 
    
    UserView userView = (UserView) request.getSession().getAttribute(WebConstant.userView);
    String hcmflag="";
	if(userView != null){
	  hcmflag=userView.getBosflag();
	} 
	String margin_top="10";
   if("hl".equals(hcmflag)){ 
   		margin_top="0";
   }
   String strSql=PubFunc.decrypt(historyDataForm.getSql());
	boolean isNewVersion=userView.getVersion()>=70?true:false;
 %>

<style type="text/css">
	.selectPre{
		position:absolute;
	<%if(isNewVersion) {%>
		left:270px;
	<% } else {%>
		left:130px;
		<%} %>
		top:10px;
		z-index: 10;
	}
</style>
<script language="javascript" src="/js/dict.js"></script> 
<script language="Javascript" src="/gz/salary.js"/></script>
<script>
	var a_code = '${historyDataForm.a_code}';
var prv_project_id="${historyDataForm.itemid}";
var prv_filter_id="${historyDataForm.condid}";
	function gzReport()
	{
	       var a00z1=historyDataForm.bosdate.value;
           var a00z0=historyDataForm.count.value;
           if(a00z1==''||a00z0=='')
           {
             alert(GZ_SELECT_BOSDATEANDCOUNT+"！");
             return;
           }
            var rsql="";
		    var arguments=new Array();     
		    var strurl="/gz/gz_accounting/report.do?b_query=link`model=3`salaryid=${historyDataForm.salaryid}`a_code=${historyDataForm.a_code}`gz_module=${historyDataForm.gz_module}`condid=${historyDataForm.condid}`count=${historyDataForm.count}`bosdate=${historyDataForm.bosdate}`s="+rsql;
		    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(strurl);
		    
		    var flag=window.showModalDialog(iframe_url,arguments,"dialogWidth=500px;dialogHeight=330px;resizable=yes;scroll=no;status=no;");  
			parent.mil_menu.location=parent.mil_menu.location+"?b_opt=1";
	}
	
	function goback()
	{
		parent.location="/gz/gz_analyse/historydata/salary_set_list.do?b_query=query&gz_module=${historyDataForm.gz_module}";
		
	}
	
	
</script>
<html:form action="/gz/gz_analyse/historydata/browse" style="margin-top:5px;">
<hrms:dataset name="historyDataForm" property="fieldlist" scope="session" setname="salaryarchive"
 setalias="data_table" pagerows="${historyDataForm.pagerows}" readonly="false" editable="true" select="true" 
 sql="<%=strSql%>"   buttons="bottom">
 <%if(isOnlySet.equals("0")) {%>
	<hrms:commandbutton name="report" hint=""   refresh="true" type="selected" setname="salaryarchive" onclick="gzReport();">
		<bean:message key="menu.report" />
	</hrms:commandbutton>
	<%} %>
	<hrms:commandbutton name="exportdata"   refresh="false" type="selected" setname="salaryarchive" onclick="exportGDData()">
		<bean:message key="sys.export.derived" />
	</hrms:commandbutton>


	<%if(isNewVersion) {%>
		<hrms:commandbutton name="itemfilter"   refresh="false" type="selected" setname="salaryarchive" onclick="to_project_filter_history('${historyDataForm.salaryid}')">
			<bean:message key="menu.gz.itemfilter" />
		</hrms:commandbutton>
		<hrms:commandbutton name="manfilter"   refresh="false" type="selected" setname="salaryarchive" onclick="search_gz_data_bycond_history('${historyDataForm.salaryid}','new')">
			<bean:message key="menu.gz.manfilter" />
		</hrms:commandbutton>
	<%} %>
	<hrms:commandbutton name="back"   refresh="false" type="selected" setname="salaryarchive" onclick="goback()">
		<bean:message key="button.return" />
	</hrms:commandbutton>
</hrms:dataset>
<table id="selectprename"  class="selectPre" style="margin-top: <%=margin_top%>px;top:0px;"><tr>
<td nowrap ><bean:message key="label.gz.itemfilter"/></td>
<td>
	<html:select name="historyDataForm" styleId="projectFilter" property="itemid" size="1"  onchange="history_projectFilter('${historyDataForm.salaryid}',this);">
   <html:optionsCollection property="itemlist" value="dataValue" label="dataName"/>
</html:select> 
</td>
<td nowrap ><bean:message key="label.gz.condfilter" /></td>
<td>
<html:select name="historyDataForm" property="condid" size="1" onchange="search_gz_data_bycond_history('${historyDataForm.salaryid}',this);">
					<html:optionsCollection property="condlist" value="dataValue" label="dataName" />
				</html:select>
</td><td  nowrap ><bean:message key="label.gz.appdate" /> </td>
<td>
<html:select name="historyDataForm" property="bosdate" size="1" onchange="searchdata1();">
					<html:optionsCollection property="datelist" value="dataValue" label="dataName" />
				</html:select> 
</td>
<td  nowrap ><bean:message key="label.gz.count" /> </td>
<td>
<html:select name="historyDataForm" property="count" size="1" onchange="searchdata1();">
					<html:optionsCollection property="countlist" value="dataValue" label="dataName" />
				</html:select>
</td>
</tr></table>
<html:hidden name="historyDataForm" property="fieldStr"/>
<html:hidden name="historyDataForm" property="sql"/>
	<html:hidden name="historyDataForm" property="empfiltersql" />
	<html:hidden name="historyDataForm" property="proright_str" />
	<html:hidden name="historyDataForm" property="cond_id_str" />
</html:form>