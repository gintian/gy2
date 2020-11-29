<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.gz.gz_accounting.cash.CashListForm,java.util.*,org.apache.commons.beanutils.LazyDynaBean"%>
<script language="Javascript" src="/gz/salary.js"></script>
<script type="text/javascript">
<!--
this.status=GZ_CASH_INFO1;
var prv_filterCondId="${cashListForm.condid}";
function filterPersonMethod(salaryid,tableName,setobj,code)
{
var tabb=document.getElementById("tabb");
var o=$("condid");
var condid="";
for(var i=0;i<setobj.length;i++)
{
  if(setobj[i].selected)
  {
  condid=setobj[i].value;
  
      cond_id_str=setobj[i].value;
      break;
  }
}
  bankdisk_filtertype(salaryid,setobj,tableName,'0');
 if(trim(sql_str).length==0&&condid!="all")
 {
      bankdisk_changeCondList(salaryid);
 }
 else
 { 
cashListForm.filterSql.value=sql_str;
cashListForm.action="/gz/gz_accounting/cash/initCashList.do?b_filter=filter&cond_id="+cond_id_str;
cashListForm.submit();
}
}
function bankdisk_changeCondList(salaryid)
{
 var hashVo=new ParameterSet();
 hashVo.setValue("isclose","2");
 hashVo.setValue("salaryid",salaryid);
 var In_parameters="opt=1";
 var request=new Request({method:'post',asynchronous:false,parameters:In_parameters,onSuccess:cash_condlist_ok,functionId:'3020100017'},hashVo);			
    
}
function cash_condlist_ok(outparameters)
{
  var filterList = outparameters.getValue("filterCondList");
  //var ob=document.getElementsByName("filterCondId");
  AjaxBind.bind(cashListForm.condid,filterList); 
  var obj=$("condid"); 
  var tabb=document.getElementById("tabb");
  if(obj.options.length==2)
  {
    cashListForm.action="/gz/gz_accounting/cash/initCashList.do?b_query=init";
    cashListForm.submit();
  }
  else
  {
     for(var i=0;i<obj.options.length;i++)
     {
        if(obj.options[i].value==prv_filterCondId)
        {
            obj.options[i].selected=true;
            tabb.focus();
            return;
        }
     }
  }  
}

//-->
</script>
<html:form action ="/gz/gz_accounting/cash/initCashList">
<table width="90%" border="0" id="tabb" cellspacing="0"  align="center" cellpadding="0">
<tr>
<td>
<table width="100%" border="0" cellspacing="0"  align="left" cellpadding="0">
<tr>
<td>
<input type="hidden" name="moneyitemids" value=""/>
<bean:message key="label.select"/>
<hrms:optioncollection name="cashListForm" property="itemList" collection="list" />
			<html:select name="cashListForm" property="itemid" size="1" onchange="cashlist_changeItem();">
				             	<html:options collection="list" property="dataValue" labelProperty="dataName"/>
			</html:select>&nbsp;
			<bean:message key="gz.bankdisk.personfilter"/>
			<html:select name="cashListForm" property="condid" size="1" onchange="filterPersonMethod('${cashListForm.salaryid}','${cashListForm.tableName}',this,'${cashListForm.code}');">
			<html:optionsCollection property="filterList" value="dataValue" label="dataName"/>
		    </html:select>
</td>
</tr>
</table>
</td>
</tr>
<tr>
<td>
<table width="100%" border="0" cellspacing="0"  align="left" cellpadding="0" class="ListTable">
  <thead>
       <tr>
        <td align="center" class="TableRow" nowrap>
        <bean:message key="gz.cash.unitperson"/>
        </td>
         <td align="center" class="TableRow" nowrap>
        <bean:message key="gz.csah.moneyamount"/>
        </td>
        <% int i=0;%>
       <logic:iterate id="element" name="cashListForm" property="columnslist" offset="0">
         <td align="center" class="TableRow" nowrap >
		   <bean:write name="element" property="<%=String.valueOf(i)%>"/>
	     </td> 
	     <%i++;%>
	     </logic:iterate>       
	     </tr>
	     </thead>
	     
	     <!--data -->
     <% int j=0;%>
 <hrms:extenditerate id="data" name="cashListForm" property="moneyListForm.list" indexes="indexes"  pagination="moneyListForm.pagination" pageCount="25" scope="session">
	     <%if(j%2==0){ %>
	     <tr class="trShallow">
	     <%} else { %>
	     <tr class="trDeep">
	     <%}%>
	     <td align="left" class="RecordRow" nowrap>
	    <bean:write name="data" property="name"/>
	     </td>
	      <td align="right" class="RecordRow" nowrap>
	    <bean:write name="data" property="value"/>
	     </td>
	      <% 
	     for(int t=0;t<i;t++)
	     {
	     %>  
	    <td align="right" class="RecordRow" nowrap>
	     <bean:write name="data" property="<%=String.valueOf(t)%>"/>
	    </td>
	     <%
	     }
	     %>
	     </tr>
	     <%j++;%>
	     </hrms:extenditerate>	 
<tr>
<td class="RecordRow" colspan="<%=i+2%>">
<table  width="100%" align="center">
		<tr>
		   <td valign="bottom" class="tdFontcolor" nowrap><bean:message key="label.page.serial"/>
		   ${cashListForm.moneyListForm.pagination.current}
		   <bean:message key="label.page.sum"/>
		   ${cashListForm.moneyListForm.pagination.count}
		  <bean:message key="label.page.row"/>
		   ${cashListForm.moneyListForm.pagination.pages}
		   <bean:message key="label.page.page"/>
		   </td>
		   <td align="right" class="tdFontcolor" nowrap>
		   <p align="right">
            <hrms:paginationlink name="cashListForm" property="moneyListForm.pagination" nameId="moneyListForm" propertyId="moneyListProperty">
		   </hrms:paginationlink>
		   </p>
		   </td>
</table>
</td>
</tr> 
<tr>
<td colspan="<%=i+2%>" align="center" style="padding-top:3px;">
<input type="button" class="mybutton" onclick="cashlist_exportExcel('${cashListForm.code}','${cashListForm.salaryid}','${cashListForm.tableName}','${cashListForm.nmoneyid}','${cashListForm.itemid}');" value="<bean:message key="sys.export.derived"/>"/>

<input type="button" class="mybutton" onclick="cashlist_configMoney('${cashListForm.nmoneyid}');" value="<bean:message key="gz.cash.ticket"/>"/>

<input type="button" class="mybutton" onclick="top.close();" value="<bean:message key="button.close"/>"/>
</td>
</tr>   
</table>
<html:hidden name="cashListForm" property="tableName"/>
<html:hidden name="cashListForm" property="nmoneyid"/>
<html:hidden name="cashListForm" property="salaryid"/>
<html:hidden name="cashListForm" property="filterSql"/>
</td>
</tr>
</table>
<table width="90%" border="0" cellspacing="0"  align="center" cellpadding="0">
<TR>
<td align="center">
<html:hidden name="cashListForm" property="code"/>
<html:hidden name="cashListForm" property="beforeSql" styleId="before"/>
<input type="hidden" name="model" value="0" id="gm"/>
</td></TR>
</table>
</html:form>