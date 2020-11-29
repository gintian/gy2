<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hjsj.hrms.actionform.gz.premium.premium_allocate.*,
				 java.util.*,
				 com.hrms.struts.taglib.CommonData;"%>
<script language="javascript" src="/js/dict.js"></script> 
<script language="javascript" src="monthPremium.js"></script>
<script language="javascript" src="premium.js"></script>
<style type="text/css"> 
.selectPre{
	position:absolute;
    left:500px;
    top:3px;
    z-index: 3;
}
</style>
<%
	MonthPremiumForm form=(MonthPremiumForm)session.getAttribute("monthPremiumForm");
	String isDistribute = (String)form.getIsDistribute();	
	String isTopOrg = (String)form.getIsTopOrg();
	String isleafOrg = (String)form.getIsLeafOrg();
	String isKeepSave = (String)form.getIsKeepSave();
	String isCanReport = (String)form.getIsCanReport();
	String isCanKeepSave = (String)form.getIsCanKeepSave();
	String salaryid = (String)form.getSalaryid();
	String year = (String)form.getYear();
	String month = (String)form.getMonth();
	String operOrg = (String)form.getOperOrg();
	ArrayList operOrgList=form.getOperOrgList();
	String isAllDistri = form.getIsAllDistri();
	String isOnlyLeafOrgs = form.getIsOnlyLeafOrgs();
	String isOrgCheckNo = form.getIsOrgCheckNo();
	String url="#";
	if(isTopOrg.equals("1"))
		url="/gz/gz_accounting/gz_org_tree.do?b_query=link&gz_module=0&returnFlag=1&salaryid="+salaryid+"&theyear="+year+"&themonth="+month+"&operOrg="+operOrg;
	else if(isTopOrg.equals("0"))
		url="/gz/gz_data/gz_org_tree.do?b_query=link&returnFlag=1&salaryid="+salaryid+"&theyear="+year+"&themonth="+month+"&orgcode="+operOrg+"&isleafOrg="+isleafOrg+"&isAllDistri="+isAllDistri+"&isOnlyLeafOrgs="+isOnlyLeafOrgs+"&isOrgCheckNo="+isOrgCheckNo;
%>
<script type="text/javascript">
var theUrl = "<%=url%>";
<%	if((isleafOrg.equals("1") || isOrgCheckNo.equals("1"))&& !salaryid.equals("nodefine")){%>
		document.location=theUrl;
<%}%>	
</script>
<html:form action="/gz/premium/premium_allocate/monthPremiumList"> 
<html:hidden name="monthPremiumForm" property="paramStr"/>
<table><tr><td>
	<hrms:dataset name="monthPremiumForm" property="fieldlist"
			scope="session" setname="${monthPremiumForm.orgsubset}" setalias="data_table" readonly="false"
			editable="true" select="true" sql="${monthPremiumForm.sql}" 
			pagerows="1000"  rowlock="true"  rowlockfield="${monthPremiumForm.dist_field}" rowlockvalues=",2,"
			buttons="movefirst,prevpage,nextpage,movelast">			 
		<% if(isTopOrg.equals("1")&&isKeepSave.equals("1")){%>
			  <hrms:commandbutton name="batchAdd" function_id="32402150101" hint="" functionId="" visible="true" refresh="true" type="selected" setname="${bonusForm.bonusSet}" onclick='add("${monthPremiumForm.isGzManager}","${monthPremiumForm.salaryid}");' >
    			 <bean:message key="lable.tz_template.new"/> 
  			 </hrms:commandbutton>
	    <% }%>	  
  <% if(isKeepSave.equals("0")){%>
  		 <% if(isDistribute.equals("1")){%>
  		  <hrms:commandbutton name="add" function_id="32402150106" hint="" functionId="" visible="true" refresh="true" type="selected" setname="${bonusForm.bonusSet}" onclick="add2();" >
    			<bean:message key="button.insert"/> 
  		   </hrms:commandbutton>
  		   <hrms:commandbutton name="downTemplate" function_id="32402150104" hint=""
				functionId="" visible="true" refresh="true" type="selected"
				setname="${monthPremiumForm.orgsubset}" onclick="downloadTemplate()">
				<bean:message key="button.download.template" />
			</hrms:commandbutton>
			<hrms:commandbutton name="import" function_id="32402150105" hint=""
				functionId="" visible="true" refresh="true" type="selected"
				setname="${monthPremiumForm.orgsubset}" onclick="importExcel()">
				<bean:message key="button.import" />
			</hrms:commandbutton>  		 
			<hrms:commandbutton name="save" functionId="3000000226"
				hint="hire.confirm.save2" function_id="" refresh="true"
				type="all-change" setname="${monthPremiumForm.orgsubset}">
				<bean:message key="button.save" />
			</hrms:commandbutton>	
        
			<hrms:commandbutton name="delete" function_id="32402150102" hint=""
				functionId="" visible="true" refresh="true" type="selected"
				setname="${monthPremiumForm.orgsubset}" onclick="del('${monthPremiumForm.orgsubset}')">
				<bean:message key="button.delete" />
			</hrms:commandbutton>
		 
			<hrms:commandbutton name="computer" function_id="32402150103" hint=""
				functionId="" visible="true" refresh="true" type="selected"
				setname="${monthPremiumForm.orgsubset}" onclick="computeFormula()">
				<bean:message key="button.computer" />
			</hrms:commandbutton>
			<% }%>
			
			<% if(isDistribute.equals("1")){%>
			<hrms:commandbutton name="distribute" function_id="" hint=""
				functionId="" visible="true" refresh="true" type="selected"
				setname="${monthPremiumForm.orgsubset}" onclick="updateFLag('distribute')">
				<bean:message key="reportManager.distribute" />
			</hrms:commandbutton>
			<%} %>
			<% if(isCanReport.equals("1")){%>
			<hrms:commandbutton name="appeal" function_id="" hint=""
				functionId="" visible="true" refresh="true" type="selected"
				setname="${monthPremiumForm.orgsubset}" onclick="updateFLag('appeal')">
				<bean:message key="reportManager.appeal" />
			</hrms:commandbutton>
			<%} if(isCanKeepSave.equals("1")){%>
				<hrms:commandbutton name="keepSave" function_id="" hint=""
				functionId="" visible="true" refresh="true" type="selected"
				setname="${monthPremiumForm.orgsubset}" onclick="updateFLag('keepSave')">
				<bean:message key="kq.deration_details.usave" />
			   </hrms:commandbutton>			
			<%} %>
		<%} %>			
		</hrms:dataset>
		</td>
		</tr>
		</table>
		<table id="selectprename"  class="selectPre"   ><tr>
<td nowrap ><bean:message key="gz.premium.noworg"/></td>
<td>

<% if(operOrgList.size()==1){
		CommonData cd=(CommonData)operOrgList.get(0);
		if(isTopOrg.equals("0"))
			out.print(":&nbsp;&nbsp;<a href='javascript:queryDetail()'>"+cd.getDataName()+"</a>"+"&nbsp;&nbsp;");
 		else
 			out.print(":&nbsp;&nbsp;"+cd.getDataName()+"&nbsp;&nbsp;");
 		out.print("<input type='hidden' name='operOrg' value='"+cd.getDataValue()+"' />");
 %>


<% }else{ %>
<html:select name="monthPremiumForm" styleId="operOrg" property="operOrg" size="1" onchange="search_data(this.value);" style="width:160">
   <html:optionsCollection property="operOrgList" value="dataValue" label="dataName"/>
</html:select> 
<logic:equal  name="monthPremiumForm" property="isTopOrg" value="0">
&nbsp;<img  src="/images/code.gif" onclick='javascript:queryDetail();' align="absmiddle" />
</logic:equal>
<% } %>
</td>
<% if(!salaryid.equals("nodefine")) {%>
<td>
	&nbsp;<a href="<%=url %>"><bean:message key="premium_allocate.people"/> </a>&nbsp;	
</td>
<% } %>
<td >
			<table width="90%" border="0" cellspacing="0" cellpadding="0">
	         <tr> 
	          <td align="right"></td>                                 
	          <td valign="middle">&nbsp;&nbsp;
	          	<html:text name="monthPremiumForm"  property="year" style="width:40"/>                     
	          <td valign="middle">                      
	          </td>
	          <td valign="middle" align="left">
	             <table border="0" cellspacing="2" cellpadding="0" >
		      		<tr><td><button id="y_up" class="m_arrow" onclick='inc_year($("year"));search_data_y($("year"));'>5</button></td></tr>
		      		<tr><td><button id="y_down" class="m_arrow" onclick='dec_year($("year"));search_data_y($("year"));'>6</button></td></tr>
	             </table>
	          </td>
			  <td><bean:message key="hmuster.label.year"/></td>	          
	   				  <td valign="middle"> 
	       	  			<html:text name="monthPremiumForm" property="month" style="width:40"/>                     
	         		 </td>
	          		 <td valign="middle" align="left">
	             		<table border="0" cellspacing="2" cellpadding="0">
		      				<tr><td><button id="m_up" class="m_arrow" onclick='inc_month($("month"));search_data_m($("month"));'>5</button></td></tr>
		      				<tr><td><button id="m_down" class="m_arrow" onclick='dec_month($("month"));search_data_m($("month"));'>6</button></td></tr>
	             		</table>
	                 </td>	
	                 <td><bean:message key="hmuster.label.month"/></td>	          
	          <td align="left"></td>
	          </tr>
	          </table>
    	  </td></tr></table>
</html:form> 