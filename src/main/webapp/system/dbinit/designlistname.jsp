<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>

<link rel="stylesheet" href="/css/css1.css" type="text/css">
<hrms:themes></hrms:themes>
<script language="javascript">
function amend()
{
	// var tableid = document.getElementById("tableid").value;
	var tableid = document.getElementsByName('tableid')[0].value;
	// var tablename = document.getElementById("tablename").value;
	var tablename = document.getElementsByName("tablename")[0].value;
	if(tablename==null||tablename=="")
	{
		alert("数据库名称不能为空！");
		return;
	}
	var parten = /^\s*$/
	if(parten.test(tablename)){
		alert("数据库名称不能为空格！");
		return;
	}
	var hashvo = new ParameterSet();
	hashvo.setValue("tableid",tableid);
	hashvo.setValue("tablename",tablename);
	var request=new Request({method:'post',asynchronous:false,onSuccess:check_ok,functionId:'1020010147'},hashvo);
}
function check_ok(outparameter)
{
	var msg = outparameter.getValue("msg");
	if(msg=='1')
    {
       amends();
    }
   else
   {
     alert(KJG_ZBTX_INF33);
     return;
   }
}
function amends()
{
	// var tableid = document.getElementById("tableid").value;
	// var tablename = document.getElementById("tablename").value;
    var tableid = document.getElementsByName('tableid')[0].value;
    var tablename = document.getElementsByName("tablename")[0].value;
	var hashvo = new ParameterSet();
	hashvo.setValue("tableid",tableid);
	hashvo.setValue("tablename",tablename);	
	var request=new Request({method:'post',asynchronous:false,onSuccess:saveOk,functionId:'1020010148'},hashvo);
}
function saveOk(outparamters){
		// window.returnValue="aaaaa";
		// window.close();
		parent.parent.return_vo = "aaaa";
		winClose();
	}
	function winClose() {
		if(parent.parent.Ext.getCmp('inforlist')){
            parent.parent.Ext.getCmp('inforlist').close();
		}
    }
</script>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html:form action="/system/dbinit/inforlist">
	<table width="400" border="0" cellpadding="0" cellspacing="0"
		align="center">
		<tr height="20">
			<!-- td width=10 valign="top" class="tableft"></td>
			<td width=140 align=center class="tabcenter">
				
			</td>
			<td width=10 valign="top" class="tabright"></td>
			<td valign="top" class="tabremain" width="500"></td> -->
			
			<td align="left" colspan="4" class="TableRow">输入构库后子集名称</td>
		</tr>
		<tr>
			<td colspan="4" class="framestyle1">

				<table border="0" cellpmoding="0" cellspacing="5"
					class="DetailTable" cellpadding="0">
					<tr>
						<td align="right" nowrap valign="middle">
						<bean:message key="kjg.title.zbjdh"/>
						</td>
						<td align="left" nowrap valign="left">
						<html:text readonly="true" property="tableid" name="dbinitForm" maxlength="3" disabled="true" styleClass="text4" style="width:250px;" size="30"/>
						</td>
					</tr>
					<tr>
						<td align="right" nowrap valign="middle">
						构库后子集名称
						</td>
						<td align="left" nowrap valign="left">
							<html:text property="tablename" name="dbinitForm" styleClass="text4" style="width:250px;" size="30"/>
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
	<table align="center">
		<tr class="list3">
						<td align="center" colspan="4" nowrap style="height:35px;">
						<input type="button" class="mybutton" value="<bean:message key="button.save" />" onClick='amend()' />
							<%--<input type='button' --%>
								<%--value='<bean:message key="button.cancel"/>'--%>
								<%--class="mybutton" onclick='window.close()' >--%>
							<input type='button'
								   value='<bean:message key="button.cancel"/>'
								   class="mybutton" onclick='winClose()' >
						</td>
					</tr>
	</table>
</html:form>
