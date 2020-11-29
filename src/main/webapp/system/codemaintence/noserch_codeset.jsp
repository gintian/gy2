<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%
int i=0;
%>
<style id=iframeCss>
.fixedDiv2 
{ 
	overflow:auto; 
	height:expression(document.body.clientHeight-150);
	width:expression(document.body.clientWidth-10); 
	BORDER-BOTTOM: #94B6E6 1pt solid; 
    BORDER-LEFT: #94B6E6 1pt solid; 
    BORDER-RIGHT: #94B6E6 1pt solid; 
    BORDER-TOP: #94B6E6 1pt solid; 
}

.TableRow1 {
	background-position : center left;
	font-size: 12px;  
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
	BORDER-LEFT: 0pt solid; 
	BORDER-RIGHT: #C4D8EE 1pt solid; 
	BORDER-TOP: 0pt solid;
	height:22px;
	font-weight: bold;
	background-color:#f4f7f7;	
	/*
	color:#336699;
	*/
	valign:middle;
}
.TableRow2 {
	background-position : center left;
	font-size: 12px;  
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
	BORDER-LEFT: #C4D8EE 1pt solid;  
	BORDER-RIGHT: 0pt solid;  
	BORDER-TOP: 0pt solid;
	height:22px;
	font-weight: bold;
	background-color:#f4f7f7;	
	/*
	color:#336699;
	*/
	valign:middle;
}
.RecordRow1 {
	border: inset 1px #C4D8EE;
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
	BORDER-LEFT: 0pt; 
	BORDER-RIGHT: #C4D8EE 1pt solid; 
	BORDER-TOP: #C4D8EE 1pt solid;
	font-size: 12px;
	border-collapse:collapse; 
	height:22px;
}
.RecordRow2 {
	border: inset 1px #C4D8EE;
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
	BORDER-LEFT: #C4D8EE 1pt solid; 
	BORDER-RIGHT: 0pt; 
	BORDER-TOP: #C4D8EE 1pt solid;
	font-size: 12px;
	border-collapse:collapse; 
	height:22px;
}
</style>
<script language="javascript">
	function winClose() {
		if(parent.parent.Ext.getCmp('nosercodeset')){
            parent.parent.Ext.getCmp('nosercodeset').close();
		}
    }
</script>
<hrms:themes></hrms:themes>
<html:form action="/system/codemaintence/serch_codeset">
<table width="100%" border="0" cellspacing="0" align="center" cellpadding="0" style="margin-top:5px;">
<tr>
<td colspan="2" align="center" nowrap> 
	<table width="590" border="0" cellspacing="0" align="center" cellpadding="0" class="ListTable">
		<THEAD>
			<tr class="fixedHeaderTr">

				<td align="center" class="TableRow" nowrap width="120px;">
					代码类
				</td>
				<td align="center" class="TableRow" nowrap>
					代码类名称
				</td>
			</TR>
		</THEAD>
		</table>
		</td></tr>
		<tr><td  valign="top" align="left" style="height: 370px;">
		<div id="mainDiv" style="height:370px;width:590px;overflow-y:auto;position: absolute;border:1px solid #C4D8EE;border-top:none; margin-left:3px" class="common_border_color">  <!-- changxy 20160801  -->
		<table width="100%" border="0" cellspacing="0" align="center"
			cellpadding="0" style="border-collapse:collapse;">
		<hrms:paginationdb id="element" name="codeSetForm" sql_str="codeSetForm.sql" table="" where_str="codeSetForm.where" columns="codeSetForm.column" order_by="order by codesetid" pagerows="${codeSetForm.pagerows}" page_id="pagination" indexes="indexes">
			 <%if(i%2==0){ %>
	     <tr class="trShallow">
          <%} else { %>
	     <tr class="trDeep">
	      <% }
	      %>
			<td align="left" class="RecordRow1" nowrap style="border-left:none;border-top:none;" width="120px;">
				&nbsp;<bean:write name="element" property="codesetid" />
			</td>
			<td align="left" class="RecordRow2" nowrap style="border-top:none;">
				&nbsp;<bean:write name="element" property="codesetdesc" />
			</td>
			</tr>
			<%i++;%>
		</hrms:paginationdb>
	</table>
</div>
	</td>
	</tr>
	<tr>
<td colspan="2" align="center" nowrap>
	<table width="590px"  border="0" cellspacing="0" align="center" cellpadding="0" class="RecordRowP"> <!-- changxy 20160801  -->
		<tr>
			<td  class="tdFontcolor" style="height: 35px;">
				<hrms:paginationtag name="codeSetForm"
					pagerows="${codeSetForm.pagerows}" property="pagination"
					scope="page" refresh="true"></hrms:paginationtag>
			</td>
			<td align="right" nowrap class="tdFontcolor" style="height: 35px">
					<hrms:paginationdblink name="codeSetForm"
							property="pagination" nameId="codeSetForm" scope="page">
					</hrms:paginationdblink>
			</td>
		</tr>
	</table>
</td>
</tr>
<tr style="height: 35px;">

<td align="center" colspan="2" nowrap>
	<%--<input type="button" name="close" value="<bean:message key="button.close"/>" class="mybutton" onclick="window.close()"/>--%>
	<%--<input type="button" name="close" value="<bean:message key="button.close"/>" class="mybutton" onclick="winClose()"/>  bug 48798 wangb 20190614--%>

</td>
</tr>
</table>
</html:form>
<script>
	var mainDiv = document.getElementById('mainDiv');
	if(getBrowseVersion() && getBrowseVersion() !=10){
		mainDiv.style.marginLeft = '5px';
	}else{
		mainDiv.style.width = parseInt(mainDiv.style.width)-2+'px';
	}

</script>
