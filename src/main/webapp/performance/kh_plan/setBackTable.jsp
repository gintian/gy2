<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<html:form action="/performance/kh_plan/kh_params">
<STYLE type=text/css>
.div2
{
 overflow:auto; 
 width: 400px;height: 300px;
 line-height:15px; 
 border-width:1px; 
 border-style: groove;
 border-width :thin ;
 
  border: inset 1px #C4D8EE;
 BORDER-BOTTOM: #C4D8EE 1pt solid; 
 BORDER-LEFT: #C4D8EE 1pt solid; 
 BORDER-RIGHT: #C4D8EE 1pt solid; 
 BORDER-TOP: #C4D8EE 1pt solid; 
 
/*scrollbar-base-color:#ff66ff; 红色滚动条的嫌疑
 scrollbar-face-color:none;
 scrollbar-arrow-color:none;
 scrollbar-track-color:#ffffff;
 scrollbar-3dlight-color:#ffffff;
 scrollbar-darkshadow-color:#ffffff;
 scrollbar-highlight-color:none;
 scrollbar-shadow-color:#e5c8e5"
 SCROLLBAR-DARKSHADOW-COLOR: #ffffff;
 BORDER-BOTTOM: #ffccff 1px dotted;*/
}
</STYLE>
<script>
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
		var itemStr = getItems("cardItems");	
		var thevo=new Object();
		thevo.cardIds=itemStr;
		thevo.flag="true";
        parent.window.returnValue=thevo;
        if(window.showModalDialog) {
            parent.window.close();
        }else{
            window.top.opener.showBackTableSet_window_ok(thevo);
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
<table border="0" cellspacing="0" cellpadding="0" align="center"
		width="90%">
		<tr>
			<td>
				<logic:equal name="examPlanForm" property="showBackTablesInfo" value="0">
					<bean:message key='jx.parameter.showBackTableInfo'/>
				</logic:equal>
				<logic:equal name="examPlanForm" property="showBackTablesInfo" value="1">
					<bean:message key='jx.parameter.showBackTableSet'/>
				</logic:equal>
			</td>
		</tr>
		<tr>
			<td>
	<div class="div2 common_border_color">
			<table width="100%" border="0" cellspacing="0" align="left"
								cellpadding="0" class="ListTable" id="select">
							<logic:iterate id="element" name="examPlanForm"
								property="cardList">
								<tr>
									<td  width="30" align="center" class="RecordRow_right" style="border-top:0px;">
										<input name="cardItems" type="checkbox" 
											value="<bean:write name="element" property="tabid" filter="true" />"
											<logic:notEqual name="element" property="selected"
											value="0">checked</logic:notEqual> />
									</td>
									<td align="left" class="RecordRow_left" style="border-top:0px;" nowrap>
										&nbsp;&nbsp;
										<bean:write name="element" property="tabid" filter="true" />.<bean:write name="element" property="name" filter="true" />
									</td>
								</tr>
							</logic:iterate>							
						</table>
		</div>
		</td>
		</tr>
		<tr>
			<td align='center'>			
				<logic:equal name="examPlanForm" property="showBackTablesInfo" value="0">
					<input type="button" id="bodyDefine" value="<bean:message key='button.ok'/>" style="margin-top:5px;"	disabled onclick="save();" Class="mybutton">
				</logic:equal>
				<logic:equal name="examPlanForm" property="showBackTablesInfo" value="1">
					<%if(!request.getParameter("planStatus").equals("0") && !request.getParameter("planStatus").equals("5")){ %>
					<input type="button" id="bodyDefine" value="<bean:message key='button.ok'/>" style="margin-top:5px;"	disabled onclick="save();" Class="mybutton">
					<%}else{ %>
					<input type="button" id="bodyDefine" value="<bean:message key='button.ok'/>" style="margin-top:5px;"	onclick="save();" Class="mybutton">
					<%} %>
				</logic:equal>
				
				<input type="button" id="close" value="<bean:message key='button.cancel'/>" style="margin-top:5px;"	onclick="closewindow();" Class="mybutton">
			</td>
		</tr>
	</table>
</html:form>