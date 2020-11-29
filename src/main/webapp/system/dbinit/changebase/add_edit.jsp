<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<script language="javascript">
<!--
function estop()
{
	return event.keyCode!=34&&event.keyCode!=39&&event.keyCode!=44;
}
function IsLetter()
{
	return (((event.keyCode >= 65) && (event.keyCode <= 90))||((event.keyCode >= 97) && (event.keyCode <= 122))); 
}
function IsLetter2()      
{      
	var dbname = document.getElementById('dbname').value;
	if(trim(dbname).length==0){
		alert(NAME_NOT_EMPTY);
		return false;
	}
    var str = document.getElementById('pre').value;     
    if(str.length!=3){
    	alert("人员库前缀必须由3位英文字母构成!");
    	return false;
    }
    else{     
	    reg=/^[a-zA-Z]+$/;      
	    if(!reg.test(str)){     
	        alert("人员库前缀必须由3位英文字母构成!");
	        return false;
	    }     
    }
    return true;     
}      
function save()
{
	if(IsLetter2())
	{
		var dbvo = new Object();
		dbvo.dbname = document.getElementById('dbname').value;
		dbvo.pre = document.getElementById('pre').value;
		//输入的人员库名称长度超出字节数16提示并结束 28039 wangb 20170522
		var byteLength=0;
		for(var i=0 ; i< dbvo.dbname.length ; i++){
			var charCode=dbvo.dbname.charCodeAt(i);
			if(charCode >=0 && charCode <=128)
				byteLength+=1;
			else
				byteLength+=2;
		}
		if(byteLength>16){
			alert("汉字输入不能超过8个或输入的字母数不能超过16个");
			return false;
		}
		parent.parent.return_vo = dbvo;
		winClose();
		// window.returnValue = dbvo;
		// window.close();
	}
}
function winClose() {
	if(parent.parent.Ext.getCmp('newDb')){
        parent.parent.Ext.getCmp('newDb').close();
	}
}
//-->
</script>
<html:form action="/system/dbinit/changebase">
	<table width="290" border="0" cellpadding="0" cellspacing="0" align="center">
		<tr height="20">
			<!-- td width=10 valign="top" class="tableft"></td>
			<td width=130 align=center class="tabcenter">
				
			</td>
			<td width=10 valign="top" class="tabright"></td>
			<td valign="top" class="tabremain" width="250"></td> -->
			
			<td align="left" colspan="4" class="TableRow">
				<logic:equal value="0" name="dbaseForm" property="vflag">
				<bean:message key="kjg.codesystem.add" /></logic:equal>
				<logic:equal value="1" name="dbaseForm" property="vflag">
				<bean:message key="kjg.codesystem.update" /></logic:equal>
				&nbsp;
			</td>
		</tr>
		<tr>
			<td colspan="4" class="framestyle3">
				<table border="0" cellpmoding="0" cellspacing="2" class="DetailTable" cellpadding="0">
					<tr class="list3">
						<td align="right" nowrap style="padding-left:5px;padding-right:5px;">
							<bean:message key="column.name" />
						</td>
						<td align="left" nowrap>
							<html:text styleId="dbname"  name="dbaseForm" property="dbvo.string(dbname)"  size="16" styleClass="text" onkeypress="event.returnValue=estop(this)" style="width:200px;"></html:text>
						</td>
						</tr>
						<tr>
						<td align="right" nowrap style="padding-left:5px;padding-right:5px;">
							<bean:message key="id_factory.prefix" />
						</td>
						<logic:equal value="0" name="dbaseForm" property="vflag">
						<td align="left" nowrap>
							<html:text styleId ="pre" name="dbaseForm" property="dbvo.string(pre)" size="16" styleClass="text" maxlength="3" onkeypress="event.returnValue=IsLetter(this)" style="width:200px;"></html:text>
						</td>
						</logic:equal>
						<logic:equal value="1" name="dbaseForm" property="vflag">
						<td align="left" nowrap>
							<html:text styleId ="pre" name="dbaseForm" property="dbvo.string(pre)" size="16" styleClass="text" readonly="true" style="width:200px;"></html:text>
						</td>
						</logic:equal>
					</tr>
				</table>
			</td>
		</tr>
		<tr class="list3">
			<td align="center" valign="top" colspan="2" style="padding-top:5px;">
				<html:button styleClass="mybutton" property="b_save" onclick="save()">
					<bean:message key="button.save" />
				</html:button>
				<%--<html:button styleClass="mybutton" property="br_return" onclick="window.close();">--%>
					<%--<bean:message key="button.close" />--%>
				<%--</html:button>--%>
				<html:button styleClass="mybutton" property="br_return" onclick="winClose();">
					<bean:message key="button.close"/>
				</html:button>
			</td>
		</tr>
	</table>
</html:form>
