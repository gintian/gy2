<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="com.hjsj.hrms.businessobject.train.trainexam.question.questiones.QuestionesBo"%>
<%@page import="com.hrms.frame.codec.SafeCode"%>
<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.taglib.CommonData,java.util.Map,com.hrms.frame.utility.AdminDb,java.sql.Connection" %>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/common.js"></script>
<script language="JavaScript" src="./planTrain.js"></script>
<script language="javascript" src="/js/dict.js"></script> 

<script language="javascript" src="/ajax/constant.js"></script>
<script language="javascript" src="/ajax/basic.js"></script>
<script language="javascript" src="/ajax/common.js"></script>
<script language="javascript" src="/ajax/control.js"></script>
<script language="javascript" src="/ajax/dataset.js"></script>
<script language="javascript" src="/ajax/editor.js"></script>
<script language="javascript" src="/ajax/dropdown.js"></script>
<script language="javascript" src="/ajax/table.js"></script>
<script language="javascript" src="/ajax/menu.js"></script>
<script language="javascript" src="/ajax/tree.js"></script>
<script language="javascript" src="/ajax/pagepilot.js"></script>
<script language="javascript" src="/ajax/command.js"></script>
<script language="javascript" src="/js/validate.js"></script>
<script language="javascript" src="/ajax/format.js"></script>

<style>
.noleft{
	border-left-width:0px;
}
.noright{
	border-right-width:0px;
}
.notop{
	border-top-width: 0px;
}
</style>
<script type="text/javascript">
	var ViewProperties=new ParameterSet();
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
	//导入excel
	function inputTemplete(){
	if("" != "<bean:write name="questionesForm" property="code" />" ){
	var theurl='/train/trainexam/question/import.do?br_selectfile=link';
    var iframe_url="/general/query/common/iframe_query.jsp?src="+theurl;
    var return_vo= window.showModalDialog(iframe_url, 'mytree_win', 
      		"dialogWidth:500px; dialogHeight:500px;resizable:no;center:yes;scroll:yes;status:no");		    				
  	// alert(return_vo)
  	 if(return_vo){
  	 	var waitInfo=eval("wait");	//显示进度条
	    waitInfo.style.display="block";
   		form1.action="/train/trainexam/question/import.do?b_exedata=link&questionClass=<bean:write name="questionesForm" property="code" />";
      	form1.submit(); 
		}
	}else{
		alert('请先定位到左侧具体的试题分类上!');
	}
	}
	
	//下载模板
	function outTemplete(){
		var hashvo=new ParameterSet();	
		hashvo.setValue("model","1");
		var request=new Request({method:'post',asynchronous:false,onSuccess:showfile,functionId:'2020050020'},hashvo);
	}
	function showfile(outparamters)
	{
		var outName=outparamters.getValue("outName");
		window.location.target="_blank";
		window.location.href = "/servlet/vfsservlet?fromjavafolder=true&fileid="+outName;
	}

</script>
<hrms:themes />
<html:form action="/train/trainexam/question/questiones/questiones.do?b_query=link" styleId="form1">
	<%
		int i = 0;
	%>
		<div id='wait'
				style='position: absolute; top: 200; left: 250; display: none;'>
				<table border="1" width="400" cellspacing="0" cellpadding="4"
					class="table_style" height="87" align="center">
					<tr>
						<td class="td_style" height=24>
							正在导入试题....
						</td>
					</tr>
					<tr>
						<td style="font-size: 12px; line-height: 200%" align=center>
							<marquee class="marquee_style" direction="right" width="300"
								scrollamount="5" scrolldelay="10">
								<table cellspacing="1" cellpadding="0">
									<tr height=8>
										<td bgcolor=#3399FF width=8></td>
										<td></td>
										<td bgcolor=#3399FF width=8></td>
										<td></td>
										<td bgcolor=#3399FF width=8></td>
										<td></td>
										<td bgcolor=#3399FF width=8></td>
										<td></td>
									</tr>
								</table>
							</marquee>
						</td>
					</tr>
				</table>
			</div>
	<table border="0" cellpadding="0" cellspacing="0" >
	<tr>
		<td>
		&nbsp;&nbsp;&nbsp;<bean:message key="train.trainexam.question.questiones.knowledge"/>&nbsp;
			<html:text name="questionesForm" styleClass="textColorWrite" property="knowledgeviewvalue"/><html:hidden name="questionesForm" property="knowledge" onchange="exchange()"></html:hidden>&nbsp;<img src="/images/code.gif" onclick="openTrainInputCodeDialog('68','knowledge');" style="cursor: pointer;" align="absmiddle"/>&nbsp;&nbsp;&nbsp;&nbsp;
			<bean:message key="train.trainexam.question.questiones.questiontype"/>&nbsp;
			<span style="vertical-align: middle;">
			<html:select name="questionesForm" property="questionType" onchange="exchange()">
				<html:optionsCollection name="questionesForm" property="questionTypeList" value="dataValue" label="dataName" />
			</html:select>&nbsp;&nbsp;&nbsp;&nbsp;</span>
			<bean:message key="train.trainexam.question.questiones.difficulty"/>&nbsp;
			<span style="vertical-align: middle;">
					<html:select name="questionesForm" property="difficulty" onchange="exchange()">
						<html:optionsCollection property="difficultyList" value="dataValue" label="dataName" />
					</html:select>&nbsp;&nbsp;&nbsp;&nbsp;
					</span>
		</td>
	</tr>
	<tr>
	<td style="padding-top: 5px;">
	<div class="fixedDiv2" style="border-top: none;">
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<thead>
			<tr class="fixedHeaderTr">
				<td align="center" width="5%" class="TableRow noleft noright">
					&nbsp;<input type="checkbox" name="checkall" alt='<bean:message key="label.query.selectall"/>' onclick="checkalls(this);"/>&nbsp;
				</td>
				<td align="center" class="TableRow noright" width="25%">
					&nbsp;<bean:message key="train.trainexam.question.questiones.knowledge"/>&nbsp;
				</td>
				<td align="center" width="7%" class="TableRow noright">
					&nbsp;<bean:message key="train.trainexam.question.questiones.questiontype"/>&nbsp;
				</td>
				<td align="center" width="8%" class="TableRow noright">
					&nbsp;<bean:message key="train.trainexam.question.questiones.difficulty"/>&nbsp;
				</td>
				<td align="center" width="40%" class="TableRow noright">
					&nbsp;<bean:message key="train.trainexam.question.questiones.questionname"/>&nbsp;
				</td>
				<td align="center" width="5%" class="TableRow noright">
					&nbsp;<bean:message key="train.trainexam.question.questiones.fraction"/>&nbsp;
				</td>
				<hrms:priv func_id="32381303">
				<td align="center" width="5%" class="TableRow noright">
					&nbsp;<bean:message key="train.trainexam.question.questiones.edit"/>&nbsp;
				</td>
				</hrms:priv>
				<td align="center" width="5%" class="TableRow noright">
					&nbsp;<bean:message key="general.mediainfo.view"/>&nbsp;
				</td>
			</tr>
		</thead>
		<%
			Map show = QuestionesBo.isShow();
		%>
		<hrms:paginationdb id="element" name="questionesForm"
			sql_str="questionesForm.strsql" table="" where_str="questionesForm.strwhere"
			columns="questionesForm.columns" page_id="pagination"
			pagerows="${questionesForm.pagerows}" order_by="questionesForm.order" indexes="indexes" allmemo="1" keys="r5200">
			<bean:define id="r" name="element" property="r5200"></bean:define>
			<%
				
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
				String r5200 = SafeCode.encode(PubFunc.encrypt(r.toString()));
				%>
				<td align="center" class="RecordRow noleft noright" style="border-top: none;" nowrap>					
					&nbsp;
					<logic:equal name="element" property="flag" value="1">
						<%if (!show.containsKey(r.toString())){ %>
						<input type="checkbox" name="chck<%=r5200 %>" value="<%=r5200 %>" id="id<%=r5200 %>"/>
						<%} %>
					</logic:equal>
					&nbsp;
				</td>
				<td class="RecordRow noright" style="word-break: break-all; word-wrap:break-word;padding:3px;border-top: none;">
					
					&nbsp;<%=QuestionesBo.getKnowledgeIdByNames(r.toString()) %>&nbsp;
					
				</td>
				<td align="left" class="RecordRow noright" style="border-top: none;" >
					&nbsp;<bean:write name="element" property="type_name" />&nbsp;
				</td>
				<td align="left" class="RecordRow noright" style="border-top: none;" nowrap>
					<logic:iterate id="it" name="questionesForm" property="difficultyList">
						<bean:define id="r5203" name="element" property="r5203"></bean:define>
						<%if (((CommonData)it).getDataValue().equals(r5203.toString())) {%>
							&nbsp;<bean:write name="it" property="dataName"/>&nbsp;
						<%} %>
					</logic:iterate>
				</td>
				<td align="left" class="RecordRow noright" style="word-break: break-all; word-wrap:break-word;padding:3px;border-top: none;">
					<bean:define id="st" name="element" property="r5204"></bean:define>
					
					&nbsp;<%=QuestionesBo.toHtml(st.toString())%>&nbsp;
				</td>
				<td align="right" class="RecordRow noright" style="border-top: none;" nowrap>
					&nbsp;<bean:write name="element" property="r5213"/>&nbsp;
				</td>
				<hrms:priv func_id="32381303">
				<td align="center" class="RecordRow noright" style="border-top: none;" nowrap>
					&nbsp;
						<logic:equal name="element" property="flag" value="1">
							<%if (!show.containsKey(r.toString())){ %>
						<img src="/images/edit.gif" border="0" style="cursor: pointer;" onclick="edit('<%=r5200 %>')" alt="<bean:message key="train.trainexam.question.questiones.edit"/>"/>
						<%} %>
						</logic:equal>
					&nbsp;
				</td>
				</hrms:priv>
				<td align="center" class="RecordRow noright" style="border-top: none;" nowrap>
					&nbsp;<img src="/images/view.gif" border="0" style="cursor: pointer;" onclick="liulan('<%=r5200 %>')" alt="<bean:message key="general.mediainfo.view"/>"/>&nbsp;
				</td>
			</tr>
		</hrms:paginationdb>
	</table>
	</div>
	</td>
	</tr>
	<tr>
			<td style="padding-right: 5px;">
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
	
	<table border="0" cellpadding="0" cellspacing="0" align="left" width="100%">
		<tr>
			<td height="35">				
				<hrms:priv func_id="32381301">
					<input class="mybutton" type="button" name="add" value="<bean:message key="train.trainexam.question.questiones.add"/>"  onclick="adds();"/>
				</hrms:priv>
				
				<hrms:priv func_id="32381304">
				  <input class="mybutton" type="button" name="del" value="<bean:message key="button.setfield.delfield"/>" onclick="dels();"/>
				</hrms:priv>
				
				<hrms:priv func_id="32381306">
				  <input class="mybutton" type="button" name="exp" value="<bean:message key="button.download.template"/>" onclick="outTemplete();"/>
				</hrms:priv>
				<hrms:priv func_id="32381305"> 
				  <input class="mybutton" type="button" name="imp" value="<bean:message key="import.tempData"/>" onclick="inputTemplete();"/>
				</hrms:priv>
				
			</td>
		</tr>
	</table>
</html:form>