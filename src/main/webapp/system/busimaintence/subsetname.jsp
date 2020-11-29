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
	var tableid = document.getElementsByName("tableid")[0].value;
	var tablename = document.getElementsByName("tablename")[0].value;
	var mainid = document.getElementById("fid").value;
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
	hashvo.setValue("tablename",getEncodeStr(tablename));
	hashvo.setValue("mainid",mainid);
	var request=new Request({method:'post',asynchronous:false,onSuccess:check_ok,functionId:'1010061008'},hashvo);
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
	var tableid = document.getElementsByName("tableid")[0].value;
	var tablename = document.getElementsByName("tablename")[0].value;
	var mainid = document.getElementById("fid").value;
	var hashvo = new ParameterSet();
	hashvo.setValue("tableid",tableid);
	hashvo.setValue("tablename",getEncodeStr(tablename));
	hashvo.setValue("mainid",mainid);
	var request=new Request({method:'post',asynchronous:false,onSuccess:saveOk,functionId:'1010061009'},hashvo);
}
function saveOk(outparamters)
{
		if(parent.parent.Ext && parent.parent.Ext.getCmp('input_dbname')){
			parent.parent.Ext.getCmp('input_dbname').return_vo = "aaaaa";
		}else{
			window.returnValue="aaaaa";
		}
		//window.close();
		winclose();
}
//关闭弹窗方法 wangb 20190323
function winclose(){
	if(parent.parent.Ext && parent.parent.Ext.getCmp('input_dbname')){
		parent.parent.Ext.getCmp('input_dbname').close();
		return;
	}
	window.close();
}
</script>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html:form action="/system/busimaintence/constructcodeset">
	<table width="400" border="0" cellpadding="1" cellspacing="0"
		align="center">
		<tr height="20">
			<!-- <td width=10 valign="top" class="tableft"></td>
			<td width=140 align=center class="tabcenter">
				输入数据库名称
			</td>
			<td width=10 valign="top" class="tabright"></td>
			<td valign="top" class="tabremain" width="500">-->
			<html:hidden styleId="fid" name="busiMaintenceForm" property="mainid"/> 
			<td  align="left" class="TableRow">
				输入数据库名称
			</td>
            	
		</tr>
		<tr>
			<td  class="framestyle9">
				<table border="0" cellpmoding="0" cellspacing="5"
					class="DetailTable" cellpadding="0">
					<tr>
						<td align="right" width="30%" nowrap valign="middle">
						<bean:message key="kjg.title.zbjdh"/>
						</td>
						<td align="left" nowrap valign="left">
						<html:text readonly="true" styleClass="text4" style="width:200px;" property="tableid" name="busiMaintenceForm" maxlength="3" disabled="true"/>
						</td>
					</tr>
					<tr>
						<td align="right" nowrap valign="middle">
						数据库名称
						</td>
						<td align="left" nowrap valign="left">
							<html:text property="tablename" styleClass="text4" style="width:200px;" name="busiMaintenceForm"/>
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
							<input type='button' 
								value='<bean:message key="button.cancel"/>'
								class="mybutton" onclick='winclose();' >
						</td>
					</tr>
	</table>
</html:form>