<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.gz.gz_data.*,java.util.*,com.hrms.struts.constant.WebConstant,com.hrms.struts.valueobject.UserView"%>
<%@ page import="org.apache.commons.beanutils.LazyDynaBean"%>
<script language="Javascript" src="salarydata.js"/></script>
<script language="javascript" src="/js/dict.js"></script> 
<script type="text/javascript" src="/js/wz_tooltip.js"></script>  
<link href="/css/css1_template.css" rel="stylesheet" type="text/css">
<script type="text/javascript">
	var prv_project_id="${salaryDataForm.itemid}";
	var prv_filter_id="${salaryDataForm.condid}";
</script>
<style type="text/css"> 
.selectPre{
	position:absolute;
    left:400px;
    top:13px;
    z-index: 10;
}
</style>
<%
	SalaryDataForm salaryDataForm=(SalaryDataForm)session.getAttribute("salaryDataForm"); 
	String returnFlag = salaryDataForm.getReturnFlag();
	String isLeafOrg = salaryDataForm.getIsLeafOrg();
	String isAllDistri = salaryDataForm.getIsAllDistri();
	String isOnlyLeafOrgs = salaryDataForm.getIsOnlyLeafOrgs();
	ArrayList musterList = salaryDataForm.getMusterList();
	String salaryid = salaryDataForm.getSalaryid();
	String isLeafOrgReport = salaryDataForm.getIsLeafOrgReport();
	String isOrgCheckNo = salaryDataForm.getIsOrgCheckNo();
	String isLeafOrgDistri = salaryDataForm.getIsLeafOrgDistri();
	int i=0;
	boolean isHaveButtons = false;
	if(returnFlag.equals("0"))
		isHaveButtons = true;
	else if(returnFlag.equals("1"))
	{ 	
		if(isAllDistri.equals("1")) 
		{
			if((isLeafOrg.equals("0") || (isLeafOrg.equals("1") && isLeafOrgDistri.equals("1"))) && isLeafOrgReport.equals("0")) 
				isHaveButtons = true;
			if(((isLeafOrg.equals("1") && isLeafOrgDistri.equals("1")) || isOrgCheckNo.equals("1")) && isLeafOrgReport.equals("0"))
				isHaveButtons = true;
			else if(isLeafOrg.equals("0") && isOrgCheckNo.equals("0") && isLeafOrgReport.equals("0"))
				isHaveButtons = true;
			if((isLeafOrg.equals("0") || (isLeafOrg.equals("1") && isLeafOrgDistri.equals("1"))) && isLeafOrgReport.equals("0"))
				isHaveButtons = true;
			if(musterList.size()>0)
				isHaveButtons = true;				
		}
		if(isOnlyLeafOrgs.equals("0"))
			isHaveButtons = true;	
	}
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String hcmflag="";
	if(userView != null){
	  hcmflag=userView.getBosflag();
	}
	
	/* 自助平台-员工信息-数据上报-下载模版 报错 xiaoyun 2014-10-23 start */
	String _sql=PubFunc.decrypt(salaryDataForm.getSql());
	/* 自助平台-员工信息-数据上报-下载模版 报错 xiaoyun 2014-10-23 end */
	
%>
<script>
<%
	if(returnFlag.equals("1") && isAllDistri!=null && isAllDistri.equals("0")){%>
		window.status='您的操作单位还没有全部下发不可以进行人员奖金分配！';
<%}%>
		
document.body.onbeforeunload=function(){ 
	window.status='';
}
var returnFlag='<%=returnFlag%>'
var theyear = '${salaryDataForm.theyear}';
var themonth = '${salaryDataForm.themonth}';
var operOrg = '${salaryDataForm.operOrg}';
var verify_ctrl='${salaryDataForm.verify_ctrl}';
var isAllDistri='<%=isAllDistri%>';
var isLeafOrg='<%=isLeafOrg%>';
var returnFlag='<%=returnFlag%>';
var salaryid = '${salaryDataForm.salaryid}';
var isOnlyLeafOrgs = '<%=isOnlyLeafOrgs%>';
var isOrgCheckNo = '<%=isOrgCheckNo%>';
var condid = '${salaryDataForm.condid}';
var a_code = '${salaryDataForm.a_code}';
var filterWhl = '${salaryDataForm.filterWhl}';
var url='&returnFlag='+returnFlag+"&salaryid="+salaryid+"&theyear="+theyear+"&themonth="+themonth+"&orgcode="+operOrg+"&isleafOrg="+isLeafOrg+"&isAllDistri="+isAllDistri+"&isOnlyLeafOrgs="+isOnlyLeafOrgs+"&isOrgCheckNo="+isOrgCheckNo;

function gzReport()
{	
	var arguments=new Array();     
	var strurl="/gz/gz_accounting/report.do?b_query=link`temp=0`model=0`salaryid=${salaryDataForm.salaryid}`a_code=${salaryDataForm.a_code}`gz_module=0`condid=${salaryDataForm.condid}`s="+getEncodeStr(salaryDataForm.filterWhl.value);
	var iframe_url="/general/query/common/iframe_query.jsp?src="+strurl; 
	var flag=window.showModalDialog(iframe_url,arguments,"dialogWidth=500px;dialogHeight=330px;resizable=yes;scroll=no;status=no;");  
	parent.mil_menu.location=parent.mil_menu.location+"?b_opt=1";
}
</script>
<hrms:themes></hrms:themes>
<html:form action="/gz/gz_data/gz_table">
<%if("hl".equals(hcmflag)){ %>
<table><tr><td height="10px"></td></tr></table>
<%}else{ %>
<table><tr><td height="1px"></td></tr></table>
<%} %>
 <%if(isHaveButtons) {%>
 
 <div id='wait' style='position:absolute;top:150;left:350;display:none;'>
		<table border="1" width="100" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
			<tr>
				<td id='wait_desc' class="td_style" height=24>
					<bean:message key="label.gz.submitData"/>......
				</td>
			</tr>
			<tr>
				<td style="font-size:12px;line-height:200%" align=center>
					<marquee class="marquee_style"  direction="right" width="300" scrollamount="5" scrolldelay="10"  >
						<table cellspacing="1" cellpadding="0">
							<tr height=8>
								<td bgcolor=#3399FF  width=8></td>
								<td></td>
								<td bgcolor=#3399FF  width=8></td>
								<td></td>
								<td bgcolor=#3399FF  width=8></td>
								<td></td>
								<td bgcolor=#3399FF  width=8></td>
								<td></td>
							</tr>
						</table>
					</marquee>
				</td>
			</tr>
		</table>
		<iframe src="javascript:false" style="position:absolute; visibility:inherit; top:0px; left:0px;width:315; height:87; 					    	
			   			 				z-index:-1;filter='progid:DXImageTransform.Microsoft.Alpha(style=0,opacity=0)';"></iframe>	
	</div>
	
<table>
<tr>
<td>
<hrms:dataset name="salaryDataForm" property="fieldlist" scope="session" setname="${salaryDataForm.gz_tablename}" 
pagerows="${salaryDataForm.pagerows}" setalias="gz_table" readonly="false" rowlock="true"  rowlockfield="sp_flag2"    rowlockvalues=",01,07,"   
editable="true" select="true" sql="<%=_sql%>" buttons="bottom">     
    <%if(returnFlag.equals("0")) {%>
     	<hrms:commandbutton name="download"  onclick="downLoadTemp('${salaryDataForm.salaryid}')"  refresh="true" type="selected" setname="${salaryDataForm.gz_tablename}" >
    		<bean:message key="button.download.template"/>
  		 </hrms:commandbutton>
   		<hrms:commandbutton name="import"  onclick="importTempData('${salaryDataForm.salaryid}')"   function_id="031405"  refresh="true" type="selected" setname="${salaryDataForm.gz_tablename}" >
  			  导入数据
   		</hrms:commandbutton>
    	<hrms:commandbutton name="savedata"  functionId="3020070003" refresh="false" type="all-change" setname="${salaryDataForm.gz_tablename}" >
    		 <bean:message key="button.save"/>
  	    </hrms:commandbutton>  
       	<hrms:commandbutton name="appeal2" hint="general.inform.search.confirmed.appeal" function_id="031401" refresh="true"  type="selected" setname="${salaryDataForm.gz_tablename}" onclick="report()" >
    		<bean:message key="button.report"/>
  		</hrms:commandbutton>  
  		<hrms:commandbutton name="computer" hint="" function_id="031402" refresh="true"  type="selected" setname="${salaryDataForm.gz_tablename}" onclick="get_formula('${salaryDataForm.salaryid}')" >
    		<bean:message key="button.computer"/>
  	   	</hrms:commandbutton>  
  	   	   <hrms:commandbutton name="sh_formula"  onclick="verifyFormula()"   function_id="031406"  refresh="true" type="selected" setname="${salaryDataForm.gz_tablename}" >
		   <bean:message key="button.audit"/>
		   </hrms:commandbutton>
  		<%-- <hrms:commandbutton name="reportOut" hint="" function_id="031403" refresh="true"  type="selected" setname="${salaryDataForm.gz_tablename}" onclick="gzReport()" >
    		<bean:message key="report.reportlist.reportOut"/>
  	   	</hrms:commandbutton>--%>		
   		<hrms:commandbutton name="goback"  onclick="go_back()" refresh="true" type="selected" setname="${salaryDataForm.gz_tablename}" >
    		 <bean:message key="button.return"/>
   		</hrms:commandbutton>    
     <%}else if(returnFlag.equals("1")){ %>
      	<%if(isAllDistri.equals("1")) {%>
      		<%if((isLeafOrg.equals("0") || (isLeafOrg.equals("1") && isLeafOrgDistri.equals("1"))) && isLeafOrgReport.equals("0")) {%>
      			 <hrms:commandbutton name="download"  onclick="downLoadTemp('${salaryDataForm.salaryid}')" refresh="true" type="selected" setname="${salaryDataForm.gz_tablename}" >
    				 <bean:message key="button.download.template"/>
  				 </hrms:commandbutton>
   				<hrms:commandbutton name="import"  onclick="importTempData('${salaryDataForm.salaryid}')"   function_id="031405"  refresh="true" type="selected" setname="${salaryDataForm.gz_tablename}" >
  				   <bean:message key="menu.gz.import"/>
   				</hrms:commandbutton>
    			<hrms:commandbutton name="savedata"  functionId="3020070003" refresh="false" type="all-change" setname="${salaryDataForm.gz_tablename}" >
    				 <bean:message key="button.save"/>
  				 </hrms:commandbutton>  
      		<%} %> 
     		<%if(((isLeafOrg.equals("1") && isLeafOrgDistri.equals("1")) || isOrgCheckNo.equals("1")) && isLeafOrgReport.equals("0")) {%>
     			<hrms:commandbutton name="appeal" hint=""
					functionId="" visible="true" refresh="true" type="selected"
					setname="${salaryDataForm.gz_tablename}"  onclick="report2()">
					<bean:message key="reportManager.appeal" />
				</hrms:commandbutton>
       	    <%}else if(isLeafOrg.equals("0") && isOrgCheckNo.equals("0")){ %> 
     			<hrms:commandbutton name="appeal2" hint="general.inform.search.confirmed.appeal" function_id="031401" refresh="true"  type="selected" setname="${salaryDataForm.gz_tablename}" onclick="report()" >
    				<bean:message key="button.report"/>
  				</hrms:commandbutton>      
       		 <%} %> 
       		 <%if((isLeafOrg.equals("0") || (isLeafOrg.equals("1") && isLeafOrgDistri.equals("1"))) && isLeafOrgReport.equals("0")){%>
      		 	<hrms:commandbutton name="computer" hint="" function_id="3240215010701" refresh="true"  type="selected" setname="${salaryDataForm.gz_tablename}" onclick="get_formula('${salaryDataForm.salaryid}')" >
    				<bean:message key="button.computer"/>
  	   			</hrms:commandbutton>  
  	   		 <%}%>
  	   	  <%if(musterList.size()>0) {%>  
  	   	    <hrms:commandbutton name="printMuster" menuid="menu1" function_id="3240215010702"  onclick="" refresh="true" type="selected" setname="${salaryDataForm.gz_tablename}" >
     			<bean:message key="infor.menu.print"/>
  	  		</hrms:commandbutton>	   	         
  	       <%} %>   		 
        <%} %>    
     <%if(isOnlyLeafOrgs.equals("0")) {%>             
       <hrms:commandbutton name="goback2"  onclick="go_back2('${salaryDataForm.theyear}','${salaryDataForm.themonth}','${salaryDataForm.operOrg}','${salaryDataForm.returnFlag}','${salaryDataForm.isLeafOrg}','${salaryDataForm.isOrgCheckNo}')" refresh="true" type="selected" setname="${salaryDataForm.gz_tablename}" >
     		<bean:message key="button.return"/>
  	   </hrms:commandbutton> 
  	          <%} %>      	   
     <%} %> 
</hrms:dataset>
</td>
</tr>
</table>
  	  	   
<hrms:menubar menu="menu1" id="menubar1" container="" visible="false">
	<logic:iterate id="element"  name="salaryDataForm"  property="musterList" indexId="index">
            <%
            	LazyDynaBean item=(LazyDynaBean)pageContext.getAttribute("element");
            	String name=(String)item.get("cname");
            	String tabid=(String)item.get("tabid");
            	String jsfunc="outMuster(\""+tabid+"\",\""+salaryid+"\")";
            	++i;            	
            %>        	
             <hrms:menuitem name='<%="mitem"+i%>' label='<%=name%>' icon="" url='<%=jsfunc%>' command="" enabled="true" visible="true">
             </hrms:menuitem>
   		</logic:iterate>
   	</hrms:menubar> 
<table id="selectprename"  class="selectPre"><tr>
<td nowrap ><bean:message key="label.gz.itemfilter"/></td>
<td>
<html:select name="salaryDataForm" styleId="projectFilter" property="itemid" size="1" onchange="search_gz_data_byitem('${salaryDataForm.salaryid}',this,'0');">
   <html:optionsCollection property="itemlist" value="dataValue" label="dataName"/>
</html:select> 
</td>
<td  nowrap ><bean:message key="label.gz.condfilter"/> </td>
<td>
<html:select name="salaryDataForm" property="condid" size="1" onchange="search_gz_data_bycond('${salaryDataForm.salaryid}',this,'${salaryDataForm.gz_tablename}');">
   <html:optionsCollection property="condlist" value="dataValue" label="dataName"/>
</html:select>  
</td>
     <%if(returnFlag.equals("1")){ %>
		<td>
				&nbsp;&nbsp;&nbsp;&nbsp;<bean:write name="salaryDataForm" property="theyear" /> <bean:message key="hmuster.label.year"/> <bean:write name="salaryDataForm" property="themonth" /> <bean:message key="hmuster.label.month"/> 
		</td>
  	 <%} %> 
</tr></table>
<%}else{ %> 
<table>
	<tr>
		<td>
			<table><tr><td>
<bean:message key="label.gz.itemfilter"/>
<html:select name="salaryDataForm" styleId="projectFilter" property="itemid" size="1" onchange="search_gz_data_byitem('${salaryDataForm.salaryid}',this,'0');">
   <html:optionsCollection property="itemlist" value="dataValue" label="dataName"/>
</html:select> 
</td>
<td  nowrap ><bean:message key="label.gz.condfilter"/> </td>
<td>
<html:select name="salaryDataForm" property="condid" size="1" onchange="search_gz_data_bycond('${salaryDataForm.salaryid}',this,'${salaryDataForm.gz_tablename}');">
   <html:optionsCollection property="condlist" value="dataValue" label="dataName"/>
</html:select>  
</td>
     <%if(returnFlag.equals("1")){ %>
     <td>
				&nbsp;&nbsp;&nbsp;&nbsp;<bean:write name="salaryDataForm" property="theyear" /> <bean:message key="hmuster.label.year"/> <bean:write name="salaryDataForm" property="themonth" /> <bean:message key="hmuster.label.month"/> 
	</td>
  	 <%} %> 
</tr></table>
		</td>		
	</tr>
	<tr>
		<td>
			<hrms:dataset name="salaryDataForm" property="fieldlist" scope="session" setname="${salaryDataForm.gz_tablename}" 
				pagerows="${salaryDataForm.pagerows}" setalias="gz_table" readonly="false" rowlock="true"  rowlockfield="sp_flag2"    rowlockvalues=",01,07,"   
				editable="true" select="true" sql="<%=_sql%>" buttons="bottom"> </hrms:dataset>				
		</td>
	</tr>
</table>	

 <%} %> 
<html:hidden name="salaryDataForm" property="proright_str" /> 
<html:hidden name="salaryDataForm" property="sql" /> 
<html:hidden name="salaryDataForm" property="empfiltersql" />
<input type="hidden" name="model" value="0" id="gm"/>
<input type='hidden' name='cond_id_str' value=''/>
<html:hidden name="salaryDataForm" property="filterWhl" />
</html:form>
<script language="javascript">
   function ${salaryDataForm.gz_tablename}_afterChange(dataset,field)
   {
   	  var field_name=field.getName();
   	  var record;
   	  var a0100;
   	  if(field_name=="A00Z1")
   	  {
   	  	record=dataset.getCurrent(); 
   	  	var newvalue=record.getValue(field_name);
   	  	var oldvalue=record.getOldValue(field_name);
   	  }
   	  if(field_name=="A00Z0")
   	  {
   	  	record=dataset.getCurrent(); 
   	  	var newvalue=record.getValue(field_name);
   	  	var oldvalue=record.getOldValue(field_name);		 
   	  }   
   	  if(field_name=="A00Z1"||field_name=="A00Z0")
   	  {
	   	   var hashVo=new ParameterSet();
		   hashVo.setValue("field_name",field_name);
		    if(field_name=="A00Z0")
		   { 
   		      hashVo.setValue("newvalue",newvalue*1);
		      hashVo.setValue("oldvalue",oldvalue*1);
		  	  hashVo.setValue("A00Z1",record.getValue("A00Z1"));
		   }
		   else
		   {
		   	  hashVo.setValue("newvalue",newvalue);
		      hashVo.setValue("oldvalue",oldvalue);
		   	  hashVo.setValue("A00Z0",record.getValue("A00Z0")*1);
		   }
		   hashVo.setValue("NBASE",record.getValue("NBASE"));	    
		   hashVo.setValue("A0100",record.getValue("A0100"));
		   hashVo.setValue("salaryid","${salaryDataForm.salaryid}");
		   var request=new Request({method:'post',asynchronous:false,onSuccess:changeOk,functionId:'3020100022'},hashVo);			
	  }  
   }
   
   function changeOk(outparameters){
   
   }
</script>