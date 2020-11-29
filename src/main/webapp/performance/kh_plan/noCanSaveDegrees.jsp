<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<script type="text/javascript">
	function getItems(elementName)
	{
		var items = document.getElementsByName(elementName);
		var itemStr='';
		for(var i=0;i<items.length;i++)
		{
			if(items[i].checked==true)
				itemStr+=items[i].value+',';
		}
		if(itemStr!='')
			itemStr=itemStr.substring(0,itemStr.length-1);
		return itemStr;
	}
	function save()
	{
		var itemStr = getItems("degrees");	
		if(itemStr=='')
		{
			alert(PLEASE_SEL_DEGREE);
			return;
		}
		var thevo=new Object();
		thevo.degrees=itemStr;
		thevo.flag="true";
        parent.window.returnValue=thevo;
        if(window.showModalDialog) {
            parent.window.close();
        }else{
            parent.parent.window.opener.window.sameResultDefine_ok(thevo);
            window.open("about:blank","_top").close();
        }
	}

    function closewindow()
    {
        if(window.showModalDialog) {
            parent.window.close();
        }else{
            window.open("about:blank","_top").close();
        }
    }
</script>
<html:form action="/performance/kh_plan/kh_params">
	<table border="0" cellspacing="0" cellpadding="0" align="center"
		width="100%">
		<tr>
			<td>
				请设定"指标结果全部相同时，不能保存"的标度:
			</td>
		</tr>
		<tr>
			<td>
				<table width="100%" border="0" cellspacing="0" align="center"
					cellpadding="0" class="ListTableF" id="select">
					<tr>
						<td align="center" class="TableRow" nowrap>
							<input type="checkbox" name="selbox"
								onclick="batch_select(this, 'degrees');">
						</td>
						<td align="center" class="TableRow" nowrap>
							<bean:message key="kq.item.code" />
						</td>
						<td align="center" class="TableRow" nowrap>
							<bean:message key="kh.field.content" />
						</td>
					</tr>
					<logic:iterate id="element" name="examPlanForm"
						property="noCanSaveDegreesList">
						<tr>
							<td width="30" align="center" class="RecordRow">
								<input name="degrees" type="checkbox"
									value="<bean:write name="element" property="grade_template_id" filter="true" />"
									<logic:notEqual name="element" property="selected"
											value="0">checked</logic:notEqual> />
							</td>
							<td align="left" class="RecordRow" nowrap>
								&nbsp;&nbsp;
								<bean:write name="element" property="grade_template_id"
									filter="true" />
							</td>
							<td align="left" class="RecordRow" nowrap>
								&nbsp;&nbsp;
								<bean:write name="element" property="gradedesc" filter="true" />
							</td>
						</tr>
					</logic:iterate>
				</table>

			</td>
		</tr>
		<tr>
			<td align='center'>
					<%if(!request.getParameter("planStatus").equals("0") && !request.getParameter("planStatus").equals("5")){ %>
					<input type="button" id="bodyDefine" value="<bean:message key='button.ok'/>" style="margin-top:5px;" disabled onclick="save();" Class="mybutton">
					<%}else{ %>
					<input type="button" id="bodyDefine" value="<bean:message key='button.ok'/>" style="margin-top:5px;" onclick="save();" Class="mybutton">
					<%} %>
				<input type="button" id="bodyDefine" value="<bean:message key='button.cancel'/>" style="margin-top:5px;" onclick="closewindow();" Class="mybutton">
			</td>
		</tr>


	</table>
</html:form>
