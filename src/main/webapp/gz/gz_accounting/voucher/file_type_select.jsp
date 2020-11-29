<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView,com.hrms.struts.constant.WebConstant"%>
<%
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String hcmflag="";
	if(userView != null){
	  hcmflag=userView.getBosflag();
	}
%>
<script type="text/javascript">
<!--
function selectok(){
  var fileType="";
  var arr=document.getElementsByName("fileType");
  var Ato=document.getElementsByName("Ato");
  for(var i=0;i<arr.length;i++){
    if(arr[i].checked)
       fileType=arr[i].value;
  }
  if(fileType=='')
  {
      alert("请选择凭证文件类型！");
      return;
  }
 var obj=new Object();
 if(Ato[0].checked){
  obj.fileType=fileType+","+Ato[0].value;
 }else{
  obj.fileType=fileType+","+"0";
 }
 returnValue=obj;
 window.close();
}
//-->
</script>
<html:form action="/gz/gz_accounting/voucher/financial_voucher">
<%if("hl".equals(hcmflag)){ %>
<br>
<%} %>

<table width='440px' border="0" cellspacing="0"  align="center" cellpadding="0" class="listTable">
<tr>
<td>
<fieldset>
<legend>请选择导出凭证文件类型</legend>
<table width='100%' border="0" cellspacing="0"  align="center" cellpadding="0" class="listTable">
<tr><td align="left">
<input type="radio" name="fileType" value="1"><font size="2"><bean:message key="gz.bankdisk.tablefile"/></font>
</td></tr>
<tr><td align="left">
<input type="radio" name="fileType" value="3"><font size="2"><bean:message key="gz.bankdisk.blankfile"/></font>
</td></tr>
<tr><td align="left">
<input type="radio" name="fileType" value="4" checked><font size="2"><bean:message key="gz.bankdisk.noseparatorfile"/></font>
</td></tr>
<tr><td align="left"> 
<input type="radio" name="fileType" value="5"><font size="2"><bean:message key="gz.bankdisk.shufile"/></font>
</td></tr>
<tr><td align="left"> 
<input type="radio" name="fileType" value="6"><font size="2"><bean:message key="gz.bankdisk.doufile"/></font>
</td></tr>
<tr><td align="left"> 
<input type="radio" name="fileType" value="2"><font size="2"><bean:message key="gz.bankdisk.excelfile"/></font>
</td></tr>

</table>
</fieldset>
</td>
</tr>
<tr>
<td><input type="checkbox" name="Ato" value="1"><font size="2"><bean:message key="gz.voucher.AtoWENZI"/></font></td>
</tr>
</table>

<table width='440px' border="0" cellspacing="1"  align="center" cellpadding="1">
<tr>
<td align="center">
<input type="button" name="ok" value="<bean:message key="button.ok"/>" class="mybutton" onclick="selectok();">
<input type="button" name="cancel" value="<bean:message key="button.cancel"/>" class="mybutton" onclick="window.close();">

</td>
</tr>
</table>
</html:form>
