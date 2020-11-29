<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.hire.demandPlan.HireOrderForm"%>
<script language="javascript" src="/js/dict.js"></script> 
<script language="JavaScript" src="/js/validateDate.js"></script>
<script language="javascript" src="/hire/demandPlan/hireOrder/hireorder.js"></script> 
<style type="text/css"> 
.selectPre
{
	position:absolute;
    left:250px;
    top:0px;
    z-index: 10;
}
</style>
<%
		HireOrderForm myform=(HireOrderForm)session.getAttribute("hireOrderForm");	
		String delFlag = myform.getDelFlag();
 %>
<html:form action="/hire/demandPlan/hireOrder"> 
	<!-- 
  		 <hrms:commandbutton name="delselected" function_id="310151,0A081" hint="" visible="true" functionId="3000000225" refresh="true" type="selected" setname="z04" >
    		 <bean:message key="button.delete"/>
  		 </hrms:commandbutton>   -->
<hrms:dataset name="hireOrderForm" property="fieldlist" scope="session" setname="z04"  
setalias="data_table" readonly="false" rowlock="true" editable="true" select="true" 
sql="${hireOrderForm.sql}" pagerows="${hireOrderForm.pagerows}"  rowlockfield="z0410" rowlockvalues=",2," buttons="movefirst,prevpage,nextpage,movelast">
   <hrms:commandbutton name="save" functionId="3000000226"  hint="hire.confirm.save2"   function_id="310153,0A083"   refresh="true" type="all-change" setname="z04"  >
	   <bean:message key="button.save"/>
	</hrms:commandbutton> 
	<% if(delFlag.equals("1")){%>	
  		 <hrms:commandbutton name="delselected" function_id="310151,0A081" hint="" visible="true"  refresh="true" type="selected" setname="z04" onclick="delOrder()">
    		 <bean:message key="button.delete"/>
  		 </hrms:commandbutton>   
<%} %>
   <hrms:commandbutton name="paidan" function_id="310152,0A082" hint="" functionId="" visible="true" refresh="true" type="selected" setname="z04" onclick="assignOrder()" >
    <bean:message key="hire.button.paidan"/>
   </hrms:commandbutton>  
</hrms:dataset>
<table id="selectprename"  class="selectPre" style="margin-top:10px;">
 <tr>
	<td nowrap>
   		<bean:message key="system.option.an"/>
   		<html:select name="hireOrderForm" property="queryItem" size="1" style="selectPre" onchange="showQueryValue(1,'')">
			<html:optionsCollection property="queryItemList" value="dataValue" label="dataName" />
		</html:select>
		&nbsp;
		<span id="datapnl" style="display:none"> <bean:message
									key="label.from" /> <input type="text" name="start_date"
									value="${hireOrderForm.startDate}" extra="editor" onchange=" if(!validate(this,'起始日期')) { this.value='';}"
									style="width:100px;font-size:10pt;text-align:left" id="editor1"
									dropDown="dropDownDate"> <bean:message key="label.to"/>
								<input  type="text" name="end_date"
									value="${hireOrderForm.endDate}" extra="editor"
									style="width:100px;font-size:10pt;text-align:left" id="editor2"
									dropDown="dropDownDate" onchange="if(!validate(this,'结束日期')) { this.value='';}"> &nbsp; </span>
		<span id="datapn2" style="display:none"> 
			<html:text name="hireOrderForm" property="queryValue"></html:text>
		</span>
		<span id="datapn3" style="display:none"> 
			<html:select styleId="codevalue" name="hireOrderForm" property="codeValue" size="1">	
			</html:select>
		</span>
		<span id="datapn4" style="display:none"> 
		    <bean:message key="label.from"/>
			<html:text name="hireOrderForm" property="startNum" size="10"/>
			<bean:message key="label.to"/>
			<html:text name="hireOrderForm" property="endNum" size="10"/>
		</span>
		<input style="display:none" type="button" id='querybutton' onclick="searchOrder();" Class="mybutton" value="<bean:message key="button.query"/>"> 
	</td>
 </tr>
</table>
<html:hidden name="hireOrderForm" property="startDate" styleId="startDate" />
<html:hidden name="hireOrderForm" property="endDate" styleId="endDate" />
<html:hidden name="hireOrderForm" property="paramStr" styleId="paramStr" />
</html:form>


<script type="text/javascript">
	function delPosition(){
	var str="请选择删除的记录";
	var tablename = "z04";
	var table = $("table"+tablename);
	var dataset = table.getDataset();
	var record = dataset.getFirstRecord();
	var selectID ="";	
	
	while(record){
	str = "";
	selectID+="/"+record.getValue("z0400");
	record = record.getNextRecord();
	}
	if(str==""){
	if(confirm("确认要删除吗?")){
	
	}else
	{
	return;
	}
	}else{
	alert(str);
	return;
	}
			document.hireOrderForm.action="/hire/demandPlan/hireOrder.do?b_del=del&z0400="+selectID.substring(1);
     		document.hireOrderForm.submit();
	
	}
	function tablez04_oper_onRefresh(cell,value,record)
	{	
		if(record!=null)
		{
			var z0400 = record.getValue("Z0400");	
			if(z0400!='')//有子集的时候才能编辑	
				cell.innerHTML="<img src=\"/images/edit.gif\" border=\"0\" onclick=\"editOrder('"+z0400+"')\" style=\"cursor:hand;\">";
		}			
	}
	showQueryValue(0,'${hireOrderForm.codeValue2}');
</script>