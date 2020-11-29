<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.gz.gz_accounting.bankdisk.BankDiskForm,java.util.*"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<script language="Javascript" src="/gz/salary.js"></script>
<script language="Javascript" src="/js/function.js"></script>
<script language="Javascript" src="/js/constant.js"></script>
<script language="javascript" src="/js/dict.js"></script> 
<% 
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String username=userView.getUserName();
	boolean issuper=userView.isSuper_admin();
 %>
<script type="text/javascript">
<!--
var prv_bank_id="${bankDiskForm.bank_id}";
var prv_filterCondId="${bankDiskForm.filterCondId}";
var code="${bankDiskForm.code}";
var tableName="${bankDiskForm.tableName}";
var salaryid="${bankDiskForm.salaryid}";
var bank_id="${bankDiskForm.bank_id}";
var usrName='<%=username%>';
var issuper='<%=issuper%>';
this.status=GZ_BANKDISK_INFO3;
function bankdisk_changeCondList(salaryid)
{
 var hashVo=new ParameterSet();
 hashVo.setValue("isclose","2");
 hashVo.setValue("salaryid",salaryid);
 var In_parameters="opt=1";
 var request=new Request({method:'post',asynchronous:false,parameters:In_parameters,onSuccess:change_condlist_ok,functionId:'3020100017'},hashVo);			
    
}
function change_condlist_ok(outparameters)
{
  var filterList = outparameters.getValue("filterCondList");
  //var ob=document.getElementsByName("filterCondId");
  AjaxBind.bind(bankDiskForm.filterCondId,filterList); 
  var obj=$("filterCondId"); 
  var tabb=document.getElementById("tabb");
  if(obj.options.length==2)
  {
    bankDiskForm.action="/gz/gz_accountingt/bankdisk/initBankDisk.do?b_init=init&opt=add&code="+code+"&tableName="+tableName+"&salaryid="+salaryid+"&bank_id="+bank_id+"&model=${bankDiskForm.model}&count=${bankDiskForm.boscount}&bosdate=${bankDiskForm.bosdate}";
    bankDiskForm.submit();
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

// 调整页面上边距 add by xiaoyun 2014-9-11
window.onload = function(){
	 window.document.body.style.marginTop=0;
}
//-->
</script>
<html:form action="/gz/gz_accountingt/bankdisk/initBankDisk">
<table width="80%" border="0" id="tabb" cellspacing="1"  align="left" cellpadding="1">
<tr>
<td>
<table width="100%" border="0" cellspacing="1"  align="left" cellpadding="1">
<tr>
<td align="left">
<hrms:menubar menu="menu2" id="menubar2">
<hrms:menuitem name="mitem0" label="kh.field.file" >
	<hrms:menuitem name="mitem1" icon="/images/add.gif" label="gz.bankdisk.newbank" url='bankdisk_addBank("<bean:message key="gz.bankdisk.iuputbankname"/>","${bankDiskForm.salaryid}","${bankDiskForm.code}","${bankDiskForm.tableName}");'>
	</hrms:menuitem>
	<hrms:menuitem name="mitem2" icon="/images/export.gif" label="gz.bankdisk.disk" url='bankdisk_disk("${bankDiskForm.salaryid}","${bankDiskForm.code}","${bankDiskForm.tableName}","${bankDiskForm.bank_id}","${bankDiskForm.filterSql}","${bankDiskForm.columnListSize}","<bean:message key="gz.bankdisk.nodatacontent"/>");'>
	</hrms:menuitem>
</hrms:menuitem>
<hrms:menuitem name="mitem1" label="gz.bankdisk.visible" >
	<hrms:menuitem name="mitem1" icon="/images/view.gif" label="gz.bankdisk.personfilter" url='getPersonFilterSql("${bankDiskForm.bank_id}","${bankDiskForm.salaryid}","${bankDiskForm.tableName}","${bankDiskForm.code}","${bankDiskForm.columnListSize}","<bean:message key="gz.bankdisk.nodatacontent"/>");'>
	</hrms:menuitem>
</hrms:menuitem>
</hrms:menubar>
</td>
</tr>
</table>
</td></tr>
<tr>
<td>
<table width="100%" border="0" cellspacing="1"  align="left" cellpadding="1">
<tr>
<td width="180px" align="left" nowrap>
<button extra="button" id="bankDisk" onclick='bankdisk_disk("${bankDiskForm.salaryid}","${bankDiskForm.code}","${bankDiskForm.tableName}","${bankDiskForm.bank_id}","${bankDiskForm.columnListSize}","<bean:message key="gz.bankdisk.nodatacontent"/>");' allowPushDown="false" down="false"><bean:message key="gz.bankdisk.disk"/></button>
<button extra="button" id="edt" onclick="bankdisk_edit1('${bankDiskForm.salaryid}','${bankDiskForm.bank_id}','${bankDiskForm.code}','${bankDiskForm.tableName}','${bankDiskForm.username}');" allowPushDown="false" down="false"><bean:message key="label.edit.user"/></button>
<button extra="button" id="delete" onclick='bankdisk_del("<bean:message key="gz.bankdisk.deletebank"/>","${bankDiskForm.bank_id}","${bankDiskForm.username}");' allowPushDown="false" down="false"><bean:message key="button.delete"/></button>
<button extra="button" id="filter" onclick='getPersonFilterSql("${bankDiskForm.bank_id}","${bankDiskForm.salaryid}","${bankDiskForm.tableName}","${bankDiskForm.code}","${bankDiskForm.columnListSize}","<bean:message key="gz.bankdisk.nodatacontent"/>");' allowPushDown="false" down="false"><bean:message key="gz.bankdisk.filter"/></button>
<button extra="button" id="clo" onclick="window.close();" allowPushDown="false" down="false"><bean:message key="button.close"/></button>
</td>
<td align="left" nowrap>
<bean:message key="gz.bankdisk.bankname"/>
 <hrms:optioncollection name="bankDiskForm" property="bankList" collection="list" />
			<html:select name="bankDiskForm" property="bank_id" size="1" onchange="bankdisk_change('','${bankDiskForm.salaryid}','${bankDiskForm.code}','${bankDiskForm.tableName}');">
				             	<html:options collection="list" property="dataValue" labelProperty="dataName"/>
			</html:select>&nbsp;
			<bean:message key="gz.bankdisk.personfilter"/>
			<html:select name="bankDiskForm" property="filterCondId" size="1" onchange="bankdisk_filterPersonMethod('${bankDiskForm.bank_id}','${bankDiskForm.salaryid}','${bankDiskForm.tableName}',this,'${bankDiskForm.code}','${bankDiskForm.columnListSize}','','${bankDiskForm.model}');">
			<html:optionsCollection property="filterCondList" value="dataValue" label="dataName"/>
		    </html:select>
			</td>
</tr>
</table>
</td>
</tr>
<tr>
<td>
<table width="100%" border="0" cellspacing="1"  align="left" cellpadding="1">
  <tr><TD>
  <logic:equal name="bankDiskForm" property="count" value="1">
  <br><br><br><p align="center"><strong><bean:message key="gz.bankdisk.nobank"/></strong></p>
  </logic:equal>
  <logic:equal name="bankDiskForm" property="count" value="2">
  <logic:equal name="bankDiskForm" property="columnListSize" value="0"><br><br><br><p align="center"><strong><bean:message key="gz.bankdisk.nodatacontent"/></strong></p>
  </logic:equal>
   <logic:notEqual name="bankDiskForm" property="columnListSize" value="0">
 <hrms:dataset name="bankDiskForm" property="dataList" scope="session" setname="${bankDiskForm.tabname}"  pagerows="${bankDiskForm.pagerows}" setalias="data_set" readonly="false" editable="true" select="false" sql="${bankDiskForm.sql}" buttons="bottom" >     
	</hrms:dataset> 
 
	
  </logic:notEqual> 
  </logic:equal>
  </TD></tr>
       
</table>
<input type="hidden" name="tableName" value="${bankDiskForm.tableName}">
<input type="hidden" name="salaryid" value="${bankDiskForm.salaryid}">
<input type="hidden" name="code" value="${bankDiskForm.code}">
<input type="hidden" name="scope" value="${bankDiskForm.scope}">
<input type="hidden" name="username" value="${bankDiskForm.username}">
<input type="hidden" name="bank_name" value="">
<input type="hidden" name="rightFields" value="">
<html:hidden name="bankDiskForm" property="filterSql"/>
<html:hidden name="bankDiskForm" property="beforeSql" styleId="before"/>
<html:hidden name="bankDiskForm" property="model" styleId="gm"/>
<html:hidden name="bankDiskForm" property="boscount"/>
<html:hidden name="bankDiskForm" property="bosdate"/>
</td>
</tr>
</table>
</html:form>
