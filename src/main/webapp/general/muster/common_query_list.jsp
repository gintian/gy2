<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
	// 在标题栏显示当前用户和日期 2004-5-10 
	UserView userView = (UserView) session
			.getAttribute(WebConstant.userView);
	String bosflag="";
	if(userView != null)
	{
		bosflag=userView.getBosflag();
	}
%>
<script type="text/javascript">
<!--
function sub(type)
{
  if(type=='0')
  {
     window.returnValue=null;
     window.close();
     return;
  }
  
   var obj = document.getElementById("cqi");
   var ids="";
   for(var i=0;i<obj.options.length;i++)
   {
     if(obj.options[i].selected)
      {
          ids=obj.options[i].value;
          break;
      }
   } 
   var hashvo=new ParameterSet();
   hashvo.setValue("tabID","${musterForm.currid}");
   hashvo.setValue("condid",ids);
   var request=new Request({method:'post',asynchronous:false,onSuccess:save_ok,functionId:'0521010025'},hashvo);
}
function save_ok(outparameters)
{
   var tt=outparameters.getValue("tt");
   window.returnValue=tt;
   window.close();
   return;
}
//-->
</script>
<%if("hcm".equalsIgnoreCase(bosflag)){ %>
<style>
.commonquerylistTable {
	margin-top:-3px;
	margin-left:-4px;
	height:expression(document.body.clientHeight-20);
	width:expression(document.body.clientWidth-10);
}
</style>
<%}else{ %>
<style>
.commonquerylistTable {
	margin-top:7px;
	margin-left:-4px;
	height:expression(document.body.clientHeight-20);
	width:expression(document.body.clientWidth-10);
}
</style>
<%} %>
<html:form action="/general/muster/hmuster/searchroster">
	<table id="commonTbl" align="center" height="100%" width="85%" border="0" cellpadding="0" cellspacing="0" class="commonquerylistTable">
		<tr>
			<td valign='top'>
				<fieldset align="center">
					<legend>
						选择自动取数条件
					</legend>
					<table width="100%" border="0" cellpadding="0" cellspacing="0"
						align="center">
						<tr><td height="40"></td></tr>
						<tr>
							<td align="center" valign="top" colspan="4"
								style="border-collapse: collapse">
								<table border="0" cellpadding="0" cellspacing="0"
									class="DetailTable">
									<tr>
										<td align="center">
											常用查询&nbsp;
											<hrms:optioncollection name="musterForm"
												property="commonQueryList" collection="list" />
											<html:select name="musterForm" styleId="cqi"
												property="commonQueryId" size="1" style="width:250px;">
												<html:options collection="list" property="dataValue"
													labelProperty="dataName" />
											</html:select>
										</td>
									</tr>
								</table>
							</td>
						</tr>
						<tr><td height="70px"></td></tr>
					</table>
				</fieldset>
			</td>
		</tr>
		<tr>
			<td align="center" height="35px">
				<html:hidden name="musterForm" property="currid" />
				<html:hidden name="musterForm" property="infor_Flag" />
				<input type="button" name="btn"
					value='<bean:message key="button.ok"/>' onclick="sub('1')"
					class="mybutton">
				<input type="button" name="btnreturn"
					value='<bean:message key="button.close"/>' onclick="sub('0')"
					class="mybutton">
			</td>
		</tr>
	</table>
</html:form>
<%if("hcm".equalsIgnoreCase(bosflag)){ %>
<script language="javascript" src="/js/function.js"></script>
<script type="text/javascript">
	if(isIE6()){
		var tbl = document.getElementById("commonTbl");
		if(tbl)
			tbl.style.marginTop = '0px';
	}
	if(!getBrowseVersion() || getBrowseVersion() == 10){//非ie兼容模式 样式修改  wangb 20190308
		var tbl = document.getElementById("commonTbl");
		tbl.setAttribute('width','99%');
		tbl.style.marginLeft='2px';
	}
</script>
<%} %>