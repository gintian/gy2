<%@page import="java.util.Date"%>
<%@page import="com.hrms.struts.constant.WebConstant"%>
<%@page import="com.hrms.struts.valueobject.UserView"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.taglib.CommonData" %>
<%
UserView userView=(UserView)session.getAttribute(WebConstant.userView);
if(userView != null){
	userView.getHm().put("fckeditorAccessTime", new Date().getTime());
}
%>
<style>
body{padding-top: 5px;text-align: center;padding-left: 5px;}
.noleft{
	border-left-width:0px;
}
.noright{
	border-right-width:0px;
}
.notop{
	border-top-width:0px;
}
.nobottom{
	BORDER-LEFT: #C4D8EE 1pt solid;  
	BORDER-RIGHT: #C4D8EE 1pt solid;
	BORDER-TOP: #C4D8EE 1pt solid;
	font-size: 12px;
	border-collapse:collapse;
	height:22px;
}
.textleng{
	width:60px;
}
.RecordRow{
	/**font-size: 12px;
	border-collapse:collapse; 
	height:30px;**/
	border: inset 1px #C4D8EE;
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
	BORDER-LEFT: #C4D8EE 1pt solid; 
	BORDER-RIGHT: #C4D8EE 1pt solid; 
	BORDER-TOP: #C4D8EE 1pt solid;
	font-size: 12px;
	border-collapse:collapse; 
	height:30px;
}

.divtableA{
	margin:5px 0px 0px 0px;
	border-top: #C4D8EE 1pt solid; 
	width:100%;
}
.divtable{
	margin:0px 0px 0px 0px;
	BORDER-TOP: #C4D8EE 1pt solid; 	 
	width:100%;
}

.myfixedDiv
{  
	overflow:auto; 
	height:100%;
	width:100%;
	BORDER-BOTTOM: #94B6E6 1pt solid; 
    BORDER-LEFT: #94B6E6 1pt solid; 
    BORDER-RIGHT: #94B6E6 1pt solid; 
    BORDER-TOP: #94B6E6 1pt solid ;
}
</style>
<link rel="stylesheet" href="/css/css1.css" type="text/css"/>
<script type="text/javascript" src="/train/trainexam/question/questiones/question.js"></script>
<hrms:themes></hrms:themes>
<script type="text/javascript">
<!--
	var selection;
	var qianvalue = "<bean:write name="questionesForm" property="addQuestionType"/>";
	if (qianvalue == "") {
		qianvalue = "1";
	}
	var question = new Question();
	// 查询提交
	function questionTypeChange(obj) {
		if (obj.value == "1" || obj.value == "2") {
			question.showSelectionTr();		
		} else {
			question.displaySelectionTr();
			
		}
		
		if (obj.value == '4') {
			document.getElementById("tiankong").style.display = "";
			document.getElementById("daan").style.display = "";
			
		} else {
			document.getElementById("tiankong").style.display = "none";
			document.getElementById("daan").style.display = "none";
		}
		
		
		
		if (obj.value == "1" || obj.value == "2" || obj.value == "3") {
			// 隐藏主观题答案fck
			question.getObj("subactive").style.display = "none";
			// 显示客观题答案
			question.getObj("objective").style.display = "";
			question.getObj("tiankongdaan").style.display = "none";
		} else if(obj.value == "4"){
			question.getObj("subactive").style.display = "none";
			// 显示客观题答案
			question.getObj("objective").style.display = "none";
			question.getObj("tiankongdaan").style.display = "";
		} else {
			// 隐藏客观题答案
			question.getObj("objective").style.display = "none";
			// 显示主观题答案fck
			question.getObj("subactive").style.display = "";
			question.getObj("tiankongdaan").style.display = "none";
		}
		
		
		question.type = obj.value;
		
		// 主观题答案清空
		//questionAnswer.Value = "";
		// 客观题答案清空
		var objactiv = question.getObj("objective");
		if (objactiv) {
			for (var i = 0; i < objactiv.children.length; i++) {
				var inpu = objactiv.children(i).children(0);
				var lab = objactiv.children(i).children(1);
				lab.innerHTML = inpu.value;//alert(inpu.name +"---"+ inpu.value + "--" +objactiv.children.length);
				if (obj.value == 1) {
				//<input type='radio' name='questionAnswerl' value='"+va+"' id='"+va+"questionAnswer'/>
					var ra = document.createElement("input");
        			ra.id = inpu.id;       			
					ra.type = "radio";
					ra.name = inpu.name;
					ra.value = inpu.value;
					inpu.replaceNode(ra);
					
					
				} else if (obj.value == 2) {
					var ra = document.createElement("input");
        			ra.id = inpu.id;     			
					ra.type = "checkbox";
					ra.name = inpu.name;
					ra.value = inpu.value;
					inpu.replaceNode(ra);
					
				}
				
				// 标志重置
				if (qianvalue == 3 && (obj.value == 1 ||  obj.value == 2)) {
					question.minValue = "";
					question.currValue = "";
					question.maxValue = "";
					
					objactiv.innerHTML = "";
					document.getElementById("questionDiv").innerHTML = "";
				}
				
			}
		}
		// 删除选项
		//question.getObj("questionDiv").innerHTML = "";
		
		
		// 添加答案选项
		if (obj.value == 3) {
			question.getObj("objective").innerHTML = "<div id='Aanswer' style='width:50px;float:left;'><input type='radio' name='questionAnswerl' value='A' id='AquestionAnswer'/>&nbsp;<label for='AquestionAnswer'>对</label></div>";
			question.getObj("objective").innerHTML += "<div id='Banswer' style='width:50px;float:left;'><input type='radio' name='questionAnswerl' value='B' id='BquestionAnswer'/>&nbsp;<label for='BquestionAnswer'>错</label></div>";
		}
		
		qianvalue = obj.value;
		
	}
	
	// 公开
	function pub(obj) {
		if (obj.checked == true) {
			document.getElementById("isPublic").value="1";
		} else {
			document.getElementById("isPublic").value="2";
		}
	}
	
	function adds() {	
		var oEditor = FCKeditorAPI.GetInstance('selectionView');	
		question.addSelection(oEditor.GetXHTML(true));
		oEditor.SetHTML("",true);
	}
	
	function inserts(){
		var oEditor = FCKeditorAPI.GetInstance('selectionView');	
		var flag = question.insertSelection(oEditor.GetXHTML(true)); 
		if(flag == "add"){
			adds();
		}
		oEditor.SetHTML("",true);
	}
	
	function saves() {
		var oEditor = FCKeditorAPI.GetInstance('selectionView');	
		question.saveSelection(oEditor.GetXHTML(true));
	}
	
	function save(opt) {
		var form1 = document.getElementById("form1");
		// 答案
		var v = document.getElementById("addQuestionTypeId").value;
		if (v == "1" || v == "2" || v == "3") {
			var obj = document.getElementById("objective");	
			var value = "";
			for (var i = 0; i < obj.children.length; i++) {
				var o = obj.children(i).children(0);
				if (o.checked == true) {
					value += "," + o.value;
				}				
			}
			
			if (value.length > 0) {
				value = value.substr(1);
			}	
			document.getElementById("questionAnswerId").value = value;
		} else if (v == "4") {
			var obj = document.getElementById("AnswerId");
			document.getElementById("questionAnswerId").value = obj.value;	
		} else {
			var oEditor = FCKeditorAPI.GetInstance('questionAnswerView');
			document.getElementById("questionAnswerId").value = oEditor.GetXHTML();
		}
		
		// 选项
		if (v == "1" || v == "2") {
			var divObj = document.getElementById("questionDiv");
			var value = "";
			for (var i = 0; i < divObj.children.length; i++) {
				var chil = divObj.children[i];
				var id = chil.id.substr(0,1);
				var selectionvalue=document.getElementById(id + "span").innerHTML;
				if(selectionvalue.length==0)
					selectionvalue=" ";
				value += "`~&~`" + id + "`:`" + selectionvalue;
			}
			
			if (value.length > 0) {
				value = value.substr(5);
			}
			document.getElementById("selectionValue").value = value;
		} else {
			document.getElementById("selectionValue").value = "";
		}
		
		// 验证必填项
		// 知识点非空验证
		if (document.getElementById("addKnowledgeId").value.length <=  0 ) {
			alert("知识点为必填项！");
			return ;
		}
		// 分类非空验证
		if (document.getElementById("questionClassId").value.length <=  0) {
			alert("试题分类为必填项！");
			return ;
		}
		
		// 分数非空验证
		if (document.getElementById("fractionId").value.length <=  0) {
			alert("分数为必填项！");
			return;
		}
		// 分数必须为数字
		if (isNaN(document.getElementById("fractionId").value)) {
			alert("分数必须为数字！");
			return;
		}
		
		// 时间非空验证
		if (document.getElementById("answerTimeId").value.length > 0) {
			if (isNaN(document.getElementById("answerTimeId").value)) {
				alert("答题时间必须为数字！");
				return;
			}
			if (document.getElementById("answerTimeId").value.indexOf(".") != -1){
				alert("答题时间必须为整数！");
				return;
			}
		}
		if (opt == "1") {
			form1.action = "/train/trainexam/question/questiones/questiones.do?b_savecontinue=link";
		}
		
		form1.submit();
	}
	
	// 返回
	function returns() {
		var form1 = document.getElementById("form1");
		form1.action = "/train/trainexam/question/questiones/questiones.do?b_query=return";
		form1.submit();
	}
	
	function changedivcolor(va) {
		tr_onclick(document.getElementById(va + "div"),"#FFF8D2");
		for (var i = "A".charCodeAt(0); i <= question.maxValue.charCodeAt(0); i++) {
			if (va != String.fromCharCode(i)) {
				var div = document.getElementById(String.fromCharCode(i) + "div");
				if(div != null)
				 	div.style.backgroundColor='';
			}
		}
		selectRadio(va);
	
}
	
//-->
</script>
<script type="text/javascript" src="/fckeditor/fckeditor.js"></script>
<script language="javascript" src="/js/validate.js"></script>
<hrms:themes />
<html:form action="/train/trainexam/question/questiones/questiones.do?b_save=link" styleId="form1">
	<table align="center" width="100%" height="100%" cellpadding="0" cellspacing="0" border="0">
	<tr>
	<td style="height:expression(document.body.clientHeight-60);width:expression(document.body.clientWidth-10);overflow: hidden;padding-right:5px;">
	<html:hidden name="questionesForm" property="questionId"/>
		<div class="myfixedDiv common_border_color" id="mydivid">
			<table width="100%" height="100%" border="0" cellspacing="0" align="center" cellpadding="0" class="ListTable">
				<tr>
					<td align="left" class="TableRow" style="border-left: none;border-right: none;border-top: none;" colspan="5">
					&nbsp;<logic:notEmpty name="questionesForm" property="questionId"><bean:message key="train.trainexam.question.questiones.edit"/></logic:notEmpty><logic:empty name="questionesForm" property="questionId"><bean:message key="train.trainexam.question.questiones.add"/></logic:empty><bean:message key="train.trainexam.question.questiones.question"/>&nbsp;
					</td>
				</tr>
				
				<tr>
					<td align="right" nowrap class="RecordRow noleft" width="10%">					
						&nbsp;<bean:message key="train.trainexam.question.questiones.addknowledge"/>&nbsp;
					</td>
					<td align="left" nowrap class="RecordRow" width="35%">
					
						<logic:empty name="questionesForm" property="addKnowledge">
						&nbsp;<input type="text" class="textColorWrite" name="addKnowledgeviewvalue" size="30"/><html:hidden name="questionesForm" property="addKnowledge" styleId="addKnowledgeId"/>&nbsp;<img src="/images/code.gif" onclick="openTrainInputCodeDialog('68','addKnowledge');" style='cursor:pointer;' align='absmiddle'><font color="red">*</font>&nbsp;
						</logic:empty>
						<logic:notEmpty name="questionesForm" property="addKnowledge">
						&nbsp;<input type="text" class="textColorWrite" name="addKnowledgeviewvalue" size="30" value="<bean:write name="questionesForm" property="addKnowledgeNames"/>"/><html:hidden name="questionesForm" property="addKnowledge" styleId="addKnowledgeId"/>&nbsp;<img src="/images/code.gif" onclick="openTrainInputCodeDialog('68','addKnowledge');" style='cursor:pointer;' align='absmiddle'><font color="red">*</font>&nbsp;
						</logic:notEmpty>
						
					</td>
					<td align="right" nowrap class="RecordRow" width="11%">					
						&nbsp;<bean:message key="train.trainexam.question.questiones.questiontypes"/>&nbsp;
						
					</td>
					<td align="left"  nowrap class="RecordRow noright" width="35%">
						&nbsp;<html:select name="questionesForm" property="addQuestionType" onchange="questionTypeChange(this)" styleId="addQuestionTypeId">
						<html:optionsCollection name="questionesForm" property="questionTypeList" value="dataValue" label="dataName" />
						</html:select>&nbsp;
					</td>
					<td width="9%" class="RecordRow noleft noright">&nbsp;</td>
				</tr>
				
				<tr>
					<td align="right" nowrap class="RecordRow noleft" >
					
						<logic:iterate id="el" name="questionesForm" property="fieldList">
							<logic:equal value="r5201" name="el" property="itemid">
								&nbsp;<bean:write name="el" property="itemdesc"/>&nbsp;
							</logic:equal>
						</logic:iterate>
					
						
					</td>
					<td align="left" nowrap class="RecordRow" >
						
						<logic:notEmpty name="questionesForm" property="questionClass">
							<hrms:codetoname codeid="69" name="questionesForm" codevalue="questionClass" codeitem="codeitem"/>
	                          		&nbsp;<input type="text" class="textColorWrite" name="questionClassviewvalue" size="30" value="<bean:write name="codeitem" property="codename" />"/><html:hidden name="questionesForm" property="questionClass" styleId="questionClassId"></html:hidden>&nbsp;<img src="/images/code.gif" onclick="openTrainInputCodeDialog('69','questionClass');" style='cursor:pointer;' align='absmiddle'><font color="red">*</font>&nbsp;
	                  	</logic:notEmpty>   
	                  	<logic:empty name="questionesForm" property="questionClass">   		 
							&nbsp;<input type="text" class="textColorWrite" name="questionClassviewvalue" size="30"/><html:hidden name="questionesForm" property="questionClass" styleId="questionClassId"></html:hidden>&nbsp;<img src="/images/code.gif" onclick="openTrainInputCodeDialog('69','questionClass');" style='cursor:pointer;' align='absmiddle'><font color="red">*</font>&nbsp;
						</logic:empty>
						  
					</td>
					<td align="right" nowrap class="RecordRow">
						<logic:iterate id="el" name="questionesForm" property="fieldList">
							<logic:equal value="r5211" name="el" property="itemid">
								&nbsp;<bean:write name="el" property="itemdesc"/>&nbsp;
							</logic:equal>
						</logic:iterate>
						<!--  &nbsp;<bean:message key="train.trainexam.question.questiones.answertime"/>&nbsp;-->
					</td>
					<td align="left"  nowrap class="RecordRow noright" colspan="2">
						&nbsp;<html:text name="questionesForm" styleClass="textColorWrite" property="answerTime" size="30" styleId="answerTimeId"></html:text>&nbsp;<bean:message key="train.trainexam.question.questiones.second"/>&nbsp;
					</td>
				</tr>
				
				<tr>
					<td align="right" nowrap class="RecordRow noleft">
						<logic:iterate id="el" name="questionesForm" property="fieldList">
						<logic:equal value="r5203" name="el" property="itemid">
							&nbsp;<bean:write name="el" property="itemdesc"/>&nbsp;
						</logic:equal>
						</logic:iterate>
					<!--  &nbsp;<bean:message key="train.trainexam.question.questiones.questiondifficulty"/>&nbsp;-->	
					</td>
					<td align="left" nowrap class="RecordRow">
						&nbsp;<html:select name="questionesForm" property="addDifficulty">
							<html:optionsCollection property="difficultyList" value="dataValue" label="dataName" />
						</html:select>&nbsp;
					</td>
					<td align="right" nowrap class="RecordRow">
						<logic:iterate id="el" name="questionesForm" property="fieldList">
							<logic:equal value="b0110" name="el" property="itemid">
								&nbsp;<bean:write name="el" property="itemdesc"/>&nbsp;
							</logic:equal>
						</logic:iterate>
						<!--  &nbsp;<bean:message key="train.trainexam.question.questiones.questionorg"/>&nbsp;-->
					</td>
					<td align="left"  nowrap class="RecordRow noright">
						<logic:notEmpty name="questionesForm" property="questionOrg">
							<hrms:codetoname codeid="UM" name="questionesForm" codevalue="questionOrg" codeitem="codeitem"/>
							<hrms:codetoname codeid="UN" name="questionesForm" codevalue="questionOrg" codeitem="codeitems"/>
	                         
	                         &nbsp;<input type="text" class="textColorWrite" name="questionOrgviewvalue" id="questionOrgviewvalue" size="30" value="<bean:write name="codeitem" property="codename" /><bean:write name="codeitems" property="codename" />"/><html:hidden name="questionesForm" property="questionOrg" styleId="questionOrg"></html:hidden>&nbsp;<img src="/images/code.gif" style='cursor:pointer;' align='absmiddle' border="0" onclick="openInputCodeDialogOrgInputPosQuestion('UM','questionOrgviewvalue','questionOrg','<bean:write name="questionesForm" property="orgparentcode"/>','1')">&nbsp;
	                    </logic:notEmpty>
	                    <logic:empty name="questionesForm" property="questionOrg">
						&nbsp;<input type="text" class="textColorWrite" name="questionOrgviewvalue" id="questionOrgviewvalue" size="30" value=""/><html:hidden name="questionesForm" property="questionOrg" styleId="questionOrg"></html:hidden>&nbsp;<img src="/images/code.gif" border="0" onclick="openInputCodeDialogOrgInputPosQuestion('UM','questionOrgviewvalue','questionOrg','<bean:write name="questionesForm" property="orgparentcode"/>','1')" style='cursor:pointer;' align='absmiddle'>&nbsp;
						</logic:empty>
					</td>
					<td class="RecordRow noleft noright">&nbsp;</td>
				</tr>
				
				<tr>
					<td align="right" nowrap class="RecordRow noleft">
						<logic:iterate id="el" name="questionesForm" property="fieldList">
							<logic:equal value="r5213" name="el" property="itemid">
								&nbsp;<bean:write name="el" property="itemdesc"/>&nbsp;
							</logic:equal>
						</logic:iterate>
						<!-- &nbsp;<bean:message key="train.trainexam.question.questiones.addfraction"/>&nbsp;	 -->
					</td>
					<td align="left" nowrap class="RecordRow">
						&nbsp;<html:text name="questionesForm" styleClass="textColorWrite" property="fraction" size="30" styleId="fractionId"></html:text><font color="red">*</font>&nbsp;
					</td>
					<td align="right" nowrap class="RecordRow">
					<logic:iterate id="el" name="questionesForm" property="fieldList">
							<logic:equal value="r5216" name="el" property="itemid">
								&nbsp;<bean:write name="el" property="itemdesc"/>&nbsp;
							</logic:equal>
						</logic:iterate>
						<!-- &nbsp;<bean:message key="train.trainexam.question.questiones.ispublic"/>&nbsp; -->
					</td>
					<td align="left"  nowrap class="RecordRow noright">
						&nbsp;<logic:equal name="questionesForm" property="isPublic" value="1">
									<input type="checkbox" name="isPublicName" onchange="pub(this)" checked="checked" id="isPublicName"/>
								</logic:equal>
								<logic:notEqual name="questionesForm" property="isPublic" value="1">
									<input type="checkbox" name="isPublicName" onchange="pub(this)" id="isPublicName"/> 
								</logic:notEqual>
								<label for="isPublicName"><bean:message key="train.trainexam.question.questiones.public"/></label>
								
								<html:hidden name="questionesForm" property="isPublic" styleId="isPublic"/>
							&nbsp;
					</td>
					<td class="RecordRow noleft noright">&nbsp;</td>
				</tr>
				
				<tr>
					<td align="right" valign="top"  nowrap class="RecordRow noleft">
						 	<logic:iterate id="el" name="questionesForm" property="fieldList">
						<logic:equal value="r5204" name="el" property="itemid">
							&nbsp;<bean:write name="el" property="itemdesc"/>&nbsp;
						</logic:equal>
					</logic:iterate>				
						<!-- &nbsp;<bean:message key="train.trainexam.question.questiones.questionname"/>&nbsp; -->
					</td>
					<td colspan="3" align="left" valign="top" height="110" nowrap class="RecordRow noright">
							<html:textarea name="questionesForm" property="addQuestionName" style="width:100%;height:50"></html:textarea>
					</td>
					<td class="RecordRow noleft noright">&nbsp;</td>
				</tr>
				
				<tr>
					<td align="right" valign="top"  nowrap class="RecordRow noleft">
						<logic:iterate id="el" name="questionesForm" property="fieldList">
						<logic:equal value="r5205" name="el" property="itemid">
							&nbsp;<bean:write name="el" property="itemdesc"/>&nbsp;
						</logic:equal>
					</logic:iterate>					
						<!--  &nbsp;<bean:message key="train.trainexam.question.questiones.questionhead"/>&nbsp;-->
					</td>
					<td colspan="3" align="left" valign="top" height="110" nowrap class="RecordRow noright">
							<textarea rows="2" cols="3" id="questionHead" name="questionHead"><bean:write name="questionesForm" property="questionHead"/></textarea>
							<script type="text/javascript">
								var questionHead = new FCKeditor('questionHead');//传入参数为表单元素（由FCKeditor生成的input或textarea）的name
	            				questionHead.BasePath='/fckeditor/';//指定FCKeditor根路径，也就是fckeditor.js所在的路径
	            				questionHead.Height='100';
	            				questionHead.Width='100%';
	            				questionHead.ToolbarSet='question';//指定工具栏
	            				questionHead.Value="";//默认值
	            				//questionHead.Create();
	            				questionHead.ReplaceTextarea();
            				</script>
            				<div id="tiankong" style="display:none;">&nbsp;<font color="red"><B>注意：</B></font>填空题填写答案的地方使用六个“_”填写。例如:中国古代四大发明:______、______、______、______。</div>
            				
					</td>
					<td class="RecordRow noleft noright">&nbsp;</td>
					
				</tr>
				
				<tr id="trSelection" >
					<td align="right" valign="top"  nowrap class="RecordRow noleft">
						<logic:iterate id="el" name="questionesForm" property="fieldList">
						<logic:equal value="r5207" name="el" property="itemid">
							&nbsp;<bean:write name="el" property="itemdesc"/>&nbsp;
						</logic:equal>
					</logic:iterate>					
						<!--  &nbsp;<bean:message key="train.trainexam.question.questiones.selection"/>&nbsp;-->
					</td>
					<td colspan="4" align="left" valign="top" nowrap style="padding-bottom: 0px;" class="RecordRow noright">
						<table width="100%" border="0" cellspacing="0" align="center" cellpadding="0">
							<tr>
								<td width="90%">
									<textarea rows="2" cols="3" id="selectionView" name="selectionView"></textarea>
									<script type="text/javascript">
										selection = new FCKeditor('selectionView');//传入参数为表单元素（由FCKeditor生成的input或textarea）的name
			            				selection.BasePath='/fckeditor/';//指定FCKeditor根路径，也就是fckeditor.js所在的路径
			            				selection.Height='100';
			            				selection.Width='100%';
			            				selection.ToolbarSet='question';//指定工具栏
			            				selection.Value="";//默认值
			            				selection.ReplaceTextarea();//Create();
	            					</script><html:hidden name="questionesForm" property="selection" styleId="selectionValue"/>
	            				
								</td>
								<td valign="top" width="10%">
								<logic:equal value="0" name="questionesForm" property="liulan">&nbsp;
									<img src="/images/save_edit.gif" border="0" onclick="saves()" style="cursor:pointer;" alt="<bean:message key="button.save"/>"/><br/>&nbsp;
									<img src="/images/add.gif" border="0" onclick="adds()" style='cursor:pointer;' alt="<bean:message key="train.trainexam.question.questiones.add"/>"/><br/>&nbsp;
									<img src="/images/goto_input.gif" border="0" onclick="inserts();" style='cursor:pointer;' alt="<bean:message key="button.new.insert"/>" />
								</logic:equal>
								</td>
							</tr>
							<tr>
								<td colspan="2" style="margin:0px;" valign="bottom">
								<div id="questionDiv"></div>
							</td>
						</tr>
						</table>
					</td>
				</tr>
				
				<tr>
					<td align="right" valign="top"  nowrap class="RecordRow noleft">					
						&nbsp;<bean:message key="train.trainexam.question.questiones.questionanswer"/>&nbsp;
					</td>
					<td colspan="3" align="left" valign="top" style="padding-bottom: 10px;" nowrap class="RecordRow noright">
						<div id="subactive">
						<textarea rows="2" cols="3" id="questionAnswerView" name="questionAnswerView"><bean:write name="questionesForm" property="questionAnswer"/></textarea>
						<script type="text/javascript">
								var questionAnswer = new FCKeditor('questionAnswerView');//传入参数为表单元素（由FCKeditor生成的input或textarea）的name
	            				questionAnswer.BasePath='/fckeditor/';//指定FCKeditor根路径，也就是fckeditor.js所在的路径
	            				questionAnswer.Height='100';
	            				questionAnswer.Width='100%';
	            				questionAnswer.ToolbarSet='question';//指定工具栏
	            				questionAnswer.Value="";//默认值
	            				//questionAnswer.Create();
	            				questionAnswer.ReplaceTextarea();
            			</script>
            			</div>
            			<div id="objective"></div>
            			<html:hidden name="questionesForm" property="questionAnswer" styleId="questionAnswerId"/>
            			<div id="tiankongdaan">
            			<html:textarea name="questionesForm" property="questionAnswer" styleId="AnswerId" style="width:100%;height:50"></html:textarea>
            			</div>
            			<div id="daan" style="display:none;"><!--  &nbsp;<font color="red"><B>注意：</B></font>填空题多个空的答案使用()或[],()表示答案之间没有顺序，[]表示答案之间有顺序。<br>例如:([三国演义][罗贯中])([水浒传][施耐庵])([西游记][吴承恩])([红楼梦][曹雪芹])--></div>
					</td>
					<td class="RecordRow noleft noright">&nbsp;</td>
				</tr>
				
				<tr>
					<td align="right" valign="top"  nowrap  class="nobottom noleft common_border_color">	
					<logic:iterate id="el" name="questionesForm" property="fieldList">
						<logic:equal value="r5210" name="el" property="itemid">
							&nbsp;<bean:write name="el" property="itemdesc"/>&nbsp;
						</logic:equal>
					</logic:iterate>				
						<!--  &nbsp;						
						<bean:message key="train.trainexam.question.questiones.questionanalysis"/>
						&nbsp;-->
					</td>
					<td colspan="3" align="left" valign="top" height="110" nowrap class="nobottom noright common_border_color">
						<textarea name="questionAnalysis" rows="20" cols="40" id="questionAnalysis"><bean:write name="questionesForm" property="questionAnalysis"/></textarea>
						<script type="text/javascript">
								var questionAnalysis = new FCKeditor('questionAnalysis');//传入参数为表单元素（由FCKeditor生成的input或textarea）的name
	            				questionAnalysis.BasePath='/fckeditor/';//指定FCKeditor根路径，也就是fckeditor.js所在的路径
	            				questionAnalysis.Height='150';
	            				questionAnalysis.Width='100%';
	            				questionAnalysis.ToolbarSet='question';//指定工具栏
	            				questionAnalysis.value="";//默认值
	            				//questionAnalysis.Create();
	            				questionAnalysis.ReplaceTextarea();
            				</script>
					</td>
					<td class="nobottom noleft noright common_border_color"></td>
				</tr>
				
				
				
				</table>
				</div>
		
		</td>
		</tr>
		<tr>
		<td>
	<table width="100%" border="0" cellpadding="0" cellspacing="0" align="center">
		<tr>
			<td align="left">
				<logic:equal value="0" name="questionesForm" property="liulan">
				<input class="mybutton" type="button" name="add" value="<bean:message key="button.save"/>" onclick="save('0')">
				</logic:equal>
				<logic:empty name="questionesForm" property="questionId">
				<input class="mybutton" type="button" name="savecontinue" value="<bean:message key="button.savereturn"/>" onclick="save('1')">
				</logic:empty>
				<input class="mybutton" type="button" name="return" value="<bean:message key="button.leave"/>" onclick="returns()">
			</td>
		</tr>
	</table>
	</td>
	</tr>
	</table>
</html:form>
<script>
	function T(instr){
		var divObj = document.createElement("div");
    	divObj.innerHTML = getDecodeStr(instr);
		var outstr = divObj.innerText;
		return outstr;
	}
	var obj = document.getElementById("addQuestionTypeId");
	function initquestion() {
	
		questionTypeChange(obj,obj.value);
		<logic:empty name="questionesForm" property="questionId">
			if (obj.value == "1" || obj.value == "2" || obj.value == "3") {
				question.addSelection("");
			}
		</logic:empty>
		<logic:notEmpty name="questionesForm" property="questionId">
			if (obj.value == "1" || obj.value == "2" || obj.value == "3") {
				// 将答案和选项初始化
				var selectionStr = "${questionesForm.selection}";
				selectionStr = getDecodeStr(selectionStr);
				var answer = "<bean:write name="questionesForm" property="questionAnswer"/>";
				var seles = selectionStr.split("`~&~`");
				var ans = answer.split(",");
				var parentDiv = document.getElementById("questionDiv")
 				var objective = document.getElementById("objective");
 				
 				if (obj.value == "1" || obj.value == "2") {
 					for (var i = 0; i < seles.length; i++) {
 						if (seles[i].length > 0)  {
 						var se = seles[i].split("`:`");
 						var va = se[0];
 						var vacon = "";
 						if (se.length > 1) {
 							vacon = se[1];
 						}
 						
 						if (obj.value == 1) {
	 						if ("A" == va) {
	 							parentDiv.innerHTML += "<div id='"+va+"div' class='divtableA common_border_color'  onclick='changedivcolor(\""+va+"\")'><img src='/images/del.gif' onclick='show_confirmDel(\""+va+"\")' alt='<bean:message key="button.setfield.delfield"/>' border='0' onclick='delSelection(\""+va+"\")' id='"+va+"img' style='cursor:pointer;' align='absmiddle'/>&nbsp;<input type='radio' name='selectionradio' id='"+va+"radio' onclick='selectRadio(\""+va+"\")' value='"+va+"'/><label for='"+va+"radio'>"+va+"、</label>&nbsp;<span id='"+va+"span'>"+vacon+"</span></div>";
	 						} else {
	 							parentDiv.innerHTML += "<div id='"+va+"div' class='divtable common_border_color'  onclick='changedivcolor(\""+va+"\")'><img src='/images/del.gif' onclick='show_confirmDel(\""+va+"\")' alt='<bean:message key="button.setfield.delfield"/>' border='0' onclick='delSelection(\""+va+"\")' id='"+va+"img' style='cursor:pointer;' align='absmiddle'/>&nbsp;<input type='radio' name='selectionradio' id='"+va+"radio' onclick='selectRadio(\""+va+"\")' value='"+va+"'/><label for='"+va+"radio'>"+va+"、</label>&nbsp;<span id='"+va+"span'>"+vacon+"</span></div>";
	 						}
							var str = "<div id='"+va+"answer' style='width:50px;float:left;'><input type='radio' name='questionAnswerl' value='"+va+"' id='"+va+"questionAnswer' ";
							var fu = "," + answer + ",";
							if (fu.indexOf("," + va + ",") != -1) {
								str += " checked='checked' ";
							}
							str += " />&nbsp;<label for='"+va+"questionAnswer'>"+va+"</label></div>";	
							objective.innerHTML += str;
						} else {
							if ("A" == va) {
								parentDiv.innerHTML += "<div id='"+va+"div' class='divtableA common_border_color' onclick='changedivcolor(\""+va+"\")'><img src='/images/del.gif' onclick='show_confirmDel(\""+va+"\")' alt='<bean:message key="button.setfield.delfield"/>' border='0' onclick='delSelection(\""+va+"\")' id='"+va+"img' style='cursor:pointer;' align='absmiddle'/>&nbsp;<input type='radio' name='selectionradio' id='"+va+"radio' onclick='selectRadio(\""+va+"\")' value='"+va+"'/><label for='"+va+"radio'>"+va+"、</label>&nbsp;<span id='"+va+"span'>"+vacon+"</span></div>";
							} else {
								parentDiv.innerHTML += "<div id='"+va+"div' class='divtable common_border_color'  onclick='changedivcolor(\""+va+"\")'><img src='/images/del.gif' onclick='show_confirmDel(\""+va+"\")' border='0' alt='<bean:message key="button.setfield.delfield"/>' onclick='delSelection(\""+va+"\")' id='"+va+"img' style='cursor:pointer;' align='absmiddle'/>&nbsp;<input type='radio' name='selectionradio' id='"+va+"radio' onclick='selectRadio(\""+va+"\")' value='"+va+"'/><label for='"+va+"radio'>"+va+"、</label>&nbsp;<span id='"+va+"span'>"+vacon+"</span></div>";
							}
							var str = "<div id='"+va+"answer' style='width:50px;float:left;'><input type='checkbox' name='questionAnswerl' value='"+va+"' id='"+va+"questionAnswer' ";
							var fu = "," + answer + ",";
							if (fu.indexOf("," + va + ",") != -1) {
								str += " checked='checked' ";
							}
							str += " />&nbsp;<label for='"+va+"questionAnswer'>"+va+"</label></div>";	
							objective.innerHTML += str;
						}
						question.minValue = 'A';
						question.currValue = va;
						question.maxValue = va;
						}
 					}
 				}
 				
 				if (obj.value == "3") {
 					var str = "<div id='Aanswer' style='width:50px;float:left;'><input type='radio' name='questionAnswerl' value='A' id='AquestionAnswer'";
 					if (ans[0] == "A") {
 						str += " checked='checked' ";
 					}
 					str += "/>&nbsp;<label for='AquestionAnswer'>对</label></div>";
					str += "<div id='Banswer' style='width:50px;float:left;'><input type='radio' name='questionAnswerl' value='B' id='BquestionAnswer'";
					if (ans[0] == "B") {
 						str += " checked='checked' ";
 					}
					str += "/>&nbsp;<label for='BquestionAnswer'>错</label></div>";
					 objective.innerHTML = str;
 				}
 				
			}
			
			
			// 为fck赋值
			//var oEditor = FCKeditorAPI.GetInstance('selectionView');	
			//question.saveSelection(oEditor.GetXHTML(true))
		</logic:notEmpty>
	}
	initquestion();
	//修改加载最后一项选项 lwc   bug:0031132
	<logic:notEmpty name="questionesForm" property="questionId">
	if (obj.value == "1" || obj.value == "2") {
		window.onload=function(){
			var va = question.maxValue;
			if(!document.getElementById(va + "div"))
				return;
			tr_onclick(document.getElementById(va + "div"),"#FFF8D2");
			for (var i = "A".charCodeAt(0); i <= question.maxValue.charCodeAt(0); i++) {
				if (va != String.fromCharCode(i)) {
					var div = document.getElementById(String.fromCharCode(i) + "div");
					if(div !=null)
						div.style.backgroundColor='';
				}
			}
			// 选中改选项
			question.getObj(va + "radio").checked = "true";
			document.getElementById("selectionView").value=question.getObj(va + "span").innerHTML;
		}
	}
	</logic:notEmpty>
	
	function show_confirmDel(va)
	{
	 var r=confirm("你确定删除该选项");
	  if (r==true)
	  {
		delSelection(va);
	  }
	
	}


</script>