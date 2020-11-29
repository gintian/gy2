<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>

<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->
<script language="javascript">
function amend()
{
	var settype = document.getElementById("settype").value;
	var value  = document.getElementById("value").value;
	if(settype=="##")
	{
		alert("请选择指标！");
		return;
	}
	if(value=="")
	{
		alert("请输入代替值！");
		return;
	}
	var hashvo=new ParameterSet();
    hashvo.setValue("settype",settype);
    hashvo.setValue("value",value);
    hashvo.setValue("table","Q05");
    var request=new Request({asynchronous:false,onSuccess:check_ok,functionId:'15301110055'},hashvo);
}
function check_ok(outparameters){
 var msg = outparameters.getValue("msg");
  if(msg=="1")
   {
       sav();
   }
   else
   {
     alert(msg);
     return;
   }
}
function sav()
{
	var settype = document.getElementById("settype").value;
	var value  = document.getElementById("value").value;
	dailyRegisterForm.action="/kq/register/select_collectdata.do?b_addsave=save&settype="+settype+"&value="+value;
	dailyRegisterForm.submit();
	var obj = new Object();
    obj.type="1";
    returnValue=obj;
    window.close();
}
function checkNuNS(obj){
 	if(!isNums(obj.value)){
 		obj.value='';
 		return;
 	}
}
function isNums(i_value){
    re=new RegExp("[^A-Za-z0-9\.]");
    var s;
    if(s=i_value.match(re)){
        return false;
     }
    return true;
}
</script>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html:form action="/kq/register/select_collectdata">
<div class="fixedDiv2" style="height: 100%;border: none">
	<table width="100%" border="0" cellpadding="1" cellspacing="0"
		align="center" style="margin-top:5px;">
		<tr height="20">
			<!-- <td width=10 valign="top" class="tableft"></td>
			<td width=140 align=center class="tabcenter">
				批量修改
			</td>
			<td width=10 valign="top" class="tabright"></td>
			<td valign="top" class="tabremain" width="500"></td> -->
			<td colspan="4" align=center class="TableRow">
				批量修改
			</td>
		</tr>
		<tr>
			<td  class="framestyle9">
				<table border="0" cellpmoding="0" cellspacing="5"
					class="DetailTable" cellpadding="0">
					<tr>
						<td align="right" nowrap valign="middle">
						<bean:message key="kjg.title.indexname"/>&nbsp;
						</td>
						<td align="left" nowrap valign="middle">
							<html:select name="dailyRegisterForm" property="settype" size="1">
								<html:optionsCollection property="batchlist" value="dataValue" label="dataName"/>
							</html:select>
						</td>
					</tr>
					<tr>
						<td align="right" nowrap valign="middle">
						替代值&nbsp;
						</td>
						<td align="left" nowrap valign="middle">
							<input type="text" name="value" value="" class="inputtext" onkeyup="checkNuNS(this)">
						</td>
					</tr>
				</table>
			</td>
		</tr>
	
	</table>
	<table align="center">
		<tr class="list3">
						<td align="center"  nowrap style="height:35px;">
						<input type="button" class="mybutton" value="<bean:message key="button.save" />" onClick='amend()' />
							<input type='button' 
								value='<bean:message key="button.cancel"/>'
								class="mybutton" onclick='window.close()' >
						</td>
					</tr>
	</table>
	</div>
</html:form>