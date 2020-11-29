<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="com.hjsj.hrms.businessobject.train.trainexam.question.questiones.QuestionesBo"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.taglib.CommonData" %>
<style>
body{padding-top: 5px;text-align: center;padding-left: 5px;}
.myfixedDiv
{  
	overflow:auto; 
	height:expression(document.body.clientHeight-115);
	width:expression(document.body.clientWidth-10); 
	BORDER-BOTTOM: #94B6E6 1pt solid; 
    BORDER-LEFT: #94B6E6 1pt solid; 
    BORDER-RIGHT: #94B6E6 1pt solid; 
    BORDER-TOP: #94B6E6 1pt solid ; 
}
.noleft{
	border-left-width:0px;
}
.noright{
	border-right-width:0px;
}
</style>
<script type="text/javascript">
<!--
	// 查询提交
	function exchange() {
		var form1 = document.getElementById("form1");
		form1.submit();
	}
	
	// 新增
	function adds() {
		var form1 = document.getElementById("form1");
		form1.action="/train/trainexam/question/questiones/questiones.do?b_add=link&opt=add&questionClass=<bean:write name="questionesForm" property="code" />";
		form1.submit();
	}
	
	// 编辑
	function edit(id) {
		var form1 = document.getElementById("form1");
		form1.action="/train/trainexam/question/questiones/questiones.do?b_add=link&id="+id+"&opt=edit";
		form1.submit();
	}
	
	// 浏览
	function liulan(id) {
		var form1 = document.getElementById("form1");
		form1.action="/train/trainexam/question/questiones/questiones.do?b_add=link&id="+id+"&opt=liulan";
		form1.submit();
	}
	
	function dels() {
		var check = false;
		var ids = "";
		var input = document.getElementsByTagName("input");
		for(i = 0; i < input.length; i++) {
			if (input[i].type == "checkbox" && input[i].checked==true && input[i].name != "checkall") {
				check =true;
				ids += "," + input[i].value;
			} 	
		}
	
		if (!check) {
			alert("没有选择记录！");
			return ;
		}
		
		ids = ids.substr(1);
		if (confirm(CONFIRMATION_DEL)) {
			var form1 = document.getElementById("form1");
			form1.action="/train/trainexam/question/questiones/questiones.do?b_del=link&ids="+ids;
			form1.submit();
		}
	}
	
	function checkalls(obj) {
		var ch = false;
		if (obj.checked == true) {
			ch = true;
		} else {
			ch = false;
		}
		
		var input = document.getElementsByTagName("input");
		for(i = 0; i < input.length; i++) {
			if (input[i].type == "checkbox" && input[i].name != "checkall") {
				input[i].checked = ch;
			} 	
		}
	}
//-->
</script>
<html:form action="/train/trainexam/question/questiones/questiones.do?b_query=link" styleId="form1">
	<%
		int i = 0;
	%>
	
	<table border="0" cellpadding="0" cellspacing="0" >
	<tr>
		<td height="30" valign="top">
		&nbsp;&nbsp;&nbsp;<bean:message key="train.trainexam.question.questiones.knowledge"/>&nbsp;
			<html:text name="questionesForm" property="knowledgeviewvalue"/><html:hidden name="questionesForm" property="knowledge" onchange="exchange()"></html:hidden>&nbsp;<img src="/images/code.gif" onclick="openTrainInputCodeDialog('68','knowledge');" style="cursor: pointer;" align="absmiddle"/>&nbsp;&nbsp;&nbsp;&nbsp;
			<bean:message key="train.trainexam.question.questiones.questiontype"/>&nbsp;
			<html:select name="questionesForm" property="questionType" onchange="exchange()">
				<html:optionsCollection name="questionesForm" property="questionTypeList" value="dataValue" label="dataName" />
			</html:select>&nbsp;&nbsp;&nbsp;&nbsp;
			<bean:message key="train.trainexam.question.questiones.difficulty"/>&nbsp;
					<html:select name="questionesForm" property="difficulty" onchange="exchange()">
						<html:optionsCollection property="difficultyList" value="dataValue" label="dataName" />
					</html:select>&nbsp;&nbsp;&nbsp;&nbsp;
		</td>
	</tr>
	<tr>　　　
	<td>
	<div class="myfixedDiv">
	<table width="100%" border="0" cellspacing="0" align="center"
		cellpadding="0" class="ListTableF">
		<thead>
			<tr class="fixedHeaderTr">
				<td align="center" width="5%" class="TableRow noleft">
					&nbsp;<input type="checkbox" name="checkall" alt='<bean:message key="label.query.selectall"/>' onclick="checkalls(this);"/>&nbsp;
				</td>
				<td align="center" class="TableRow" width="25%">
					&nbsp;<bean:message key="train.trainexam.question.questiones.knowledge"/>&nbsp;
				</td>
				<td align="center" width="7%" class="TableRow">
					&nbsp;<bean:message key="train.trainexam.question.questiones.questiontype"/>&nbsp;
				</td>
				<td align="center" width="8%" class="TableRow">
					&nbsp;<bean:message key="train.trainexam.question.questiones.difficulty"/>&nbsp;
				</td>
				<td align="center" width="40%" class="TableRow">
					&nbsp;<bean:message key="train.trainexam.question.questiones.questionname"/>&nbsp;
				</td>
				<td align="center" width="5%" class="TableRow">
					&nbsp;<bean:message key="train.trainexam.question.questiones.fraction"/>&nbsp;
				</td>
				<td align="center" width="5%" class="TableRow">
					&nbsp;<bean:message key="train.trainexam.question.questiones.edit"/>&nbsp;
				</td>
				<td align="center" width="5%" class="TableRow noright">
					&nbsp;<bean:message key="general.mediainfo.view"/>&nbsp;
				</td>
			</tr>
		</thead>
		<hrms:paginationdb id="element" name="questionesForm"
			sql_str="questionesForm.strsql" table="" where_str="questionesForm.strwhere"
			columns="questionesForm.columns" page_id="pagination"
			pagerows="${questionesForm.pagerows}" order_by="questionesForm.order" indexes="indexes" allmemo="1">
			<bean:define id="r" name="element" property="r5200"></bean:define>
			<%
				String show = QuestionesBo.isShow(r.toString());
				if (i % 2 == 0) {
			%>
			<tr class="trShallow">
				<%
					} else {
				%>
			
			<tr class="trDeep">
				<%
					}
								i++;
				%>
				<td align="center" class="RecordRow noleft" nowrap>					
					&nbsp;
					<logic:equal name="element" property="flag" value="1">
						<%if ("0".equals(show)){ %>
						<input type="checkbox" name="chck<bean:write name="element" property="r5200" />" value="<bean:write name="element" property="r5200" />" id="id<bean:write name="element" property="r5200" />"/>
						<%} %>
					</logic:equal>
					&nbsp;
				</td>
				<td class="RecordRow" style="word-break: break-all; word-wrap:break-word;padding:3px;">
					
					&nbsp;<%=QuestionesBo.getKnowledgeIdByNames(r.toString()) %>&nbsp;
					
				</td>
				<td align="left" class="RecordRow" >
					&nbsp;<bean:write name="element" property="type_name" />&nbsp;
				</td>
				<td align="left" class="RecordRow" nowrap>
					<logic:iterate id="it" name="questionesForm" property="difficultyList">
						<bean:define id="r5203" name="element" property="r5203"></bean:define>
						<%if (((CommonData)it).getDataValue().equals(r5203.toString())) {%>
							&nbsp;<bean:write name="it" property="dataName"/>&nbsp;
						<%} %>
					</logic:iterate>
				</td>
				<td align="left" class="RecordRow" style="word-break: break-all; word-wrap:break-word;padding:3px;">
					<bean:define id="st" name="element" property="r5204"></bean:define>
					&nbsp;<%=st.toString().replaceAll("\r\n", "<br>") %>&nbsp;
				</td>
				<td align="right" class="RecordRow" nowrap>
					&nbsp;<bean:write name="element" property="r5213"/>&nbsp;
				</td>
				<td align="center" class="RecordRow" nowrap>
					&nbsp;
						<logic:equal name="element" property="flag" value="1">
							<%if ("0".equals(show)){ %>
						<img src="/images/edit.gif" border="0" style="cursor: pointer;" onclick="edit('<bean:write name="element" property="r5200"/>')" alt="<bean:message key="train.trainexam.question.questiones.edit"/>"/>
						<%} %>
						</logic:equal>
					&nbsp;
				</td>
				<td align="center" class="RecordRow noright" nowrap>
					&nbsp;<img src="/images/view.gif" border="0" style="cursor: pointer;" onclick="liulan('<bean:write name="element" property="r5200"/>')" alt="<bean:message key="general.mediainfo.view"/>"/>&nbsp;
				</td>
			</tr>
		</hrms:paginationdb>
	</table>
	</div>
	</td>
	</tr>
	<tr>
			<td>
				<table width="100%" class="RecordRowP" align="center">
					<tr>
						<td valign="bottom" class="tdFontcolor">
							<hrms:paginationtag name="questionesForm"
								pagerows="${questionesForm.pagerows}" property="pagination"
								scope="page" refresh="true"></hrms:paginationtag>
						</td>
						<td align="right" nowrap class="tdFontcolor">
							<p align="right">
								<hrms:paginationdblink name="questionesForm"
									property="pagination" nameId="questionesForm" scope="page">
								</hrms:paginationdblink>
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
	
	<table border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td height="35">
				
					&nbsp;&nbsp;
					<input class="mybutton" type="button" name="add" value="<bean:message key="train.trainexam.question.questiones.add"/>"  onclick="adds();"/>
				
				<hrms:priv func_id="32381301">
				&nbsp;&nbsp;
				<input class="mybutton" type="button" name="del" value="<bean:message key="button.setfield.delfield"/>" onclick="dels();"/>
				</hrms:priv>
				<!--  
				&nbsp;&nbsp;
				<input class="mybutton" type="button" name="imp" value="<bean:message key="button.import"/>"/>
				&nbsp;&nbsp;
				<input class="mybutton" type="button" name="exp" value="<bean:message key="button.export"/>"/>-->
			</td>
		</tr>
	</table>
</html:form>