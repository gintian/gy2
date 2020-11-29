<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<% int i=0; %>

<script language="javascript">
function validates(flag,res){
	var a=0;
	var nFlag="${hmusterForm.modelFlag}"
	for(var i=0;i<document.hmusterForm.tabID.options.length;i++){
		if(document.hmusterForm.tabID.options[i].selected )
			a++;
	}
	if (a>1){
		alert(ONLY_SELECT_ONE_ITEM);
		return;
	}
	if(a==0){
		alert(SELECT_ROSTER);	
	}else{
		if(flag==1){
			if(nFlag==2||nFlag==3||nFlag==21||nFlag==41)
				document.hmusterForm.action="/general/muster/hmuster/select_muster_name.do?b_next2=b_next2&res="+res+"&operateMethod=direct";
		
			document.hmusterForm.submit();
		}else if(flag==2){
			if(nFlag==3||nFlag==21||nFlag==41)
				hmusterForm.action="/general/muster/hmuster/select_muster_name.do?b_next=next";
			else
				hmusterForm.action="/general/muster/hmuster/select_muster_name.do?br_next_singleTable=next";
			
			hmusterForm.submit();
		}
	}
}
</script>
<html:form action="/general/muster/hmuster/select_muster_name">
	<table width="80%" border="0" cellpadding="0" cellspacing="0"
		align="center">
		<tr height="20">
			<!--  <td width=10 valign="top" class="tableft">
				&nbsp;
			</td>
			<td width=130 align=center class="tabcenter">
				&nbsp;
				<bean:message key="hmuster.label.info" />
				&nbsp;
			</td>
			<td width=10 valign="top" class="tabright">
				&nbsp;
			</td>
			<td valign="top" class="tabremain" width="500">
				&nbsp;
			</td>-->
			<td  align=center class="TableRow">
				&nbsp;
				<bean:message key="hmuster.label.info" />
				&nbsp;
			</td>
		</tr>
		<tr>
			<td  class="framestyle9">
				<br>
				<table width="100%" border="0" cellpmoding="0" cellspacing="0"
					class="DetailTable" cellpadding="0">
					<logic:equal name="hmusterForm" property="infor_Flag" value="1">
						<logic:equal name="hmusterForm" property="modelFlag" value="3">
							<tr>
								<td width="100%" align="top">
									&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
									人员库:<hrms:optioncollection name="hmusterForm" property="dblist"
										collection="list" />
									<html:select name="hmusterForm" property="dbpre" size="1">
										<html:options collection="list" property="dataValue"
											labelProperty="dataName" />
									</html:select>
								</td>
							</tr>
							<tr><td>&nbsp;</td></tr>
						</logic:equal>
					</logic:equal>
					
					<tr>
						<td width="100%" align="center">
							<hrms:optioncollection name="hmusterForm" property="hmusterlist"
								collection="list2" />
							<html:select name="hmusterForm" property="tabID" multiple="false"
								style="height:300px;width:88%;font-size:10pt">
								<html:options collection="list2" property="dataValue"
									labelProperty="dataName" />
							</html:select>
						</td>
					</tr>
					<tr class="list3">
						<td align="center" style="height:35px;">
							&nbsp;&nbsp;&nbsp;&nbsp;
							<input type="button" name="b_next"
								value="重新取数" class="mybutton"
								onClick="validates(1,1)">
							<input type="button" name="b_next"
								value="上次数据" class="mybutton"
								onClick="validates(1,0)">
							<input type="button" name="b_next"
								value="&nbsp;&nbsp;<bean:message key="button.close"/>&nbsp;&nbsp;" class="mybutton"
								onClick="window.close();">
						</td>
					</tr>
				</table>
				</html:form>