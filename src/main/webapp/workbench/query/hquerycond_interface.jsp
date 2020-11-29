<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.frame.utility.DateStyle"%>

<%
	int i = 0;
	UserView userView = (UserView) session
			.getAttribute(WebConstant.userView);
	//String manager=userView.getManagePrivCodeValue(); 
	String manager = userView.getUnitIdByBusi("4");
%>
<script language="javascript" src="/ajax/common.js"></script>
<!-- 引入ext 和代码控件      wangb 20171117 -->
<script language="JavaScript" src="/module/utils/js/template.js"></script>
<script type="text/javascript" src="/components/codeSelector/codeSelector.js"></script>
<script type="text/javascript" src="/components/extWidget/proxy/TransactionProxy.js"></script>
<script type="text/javascript" src="../../js/constant.js"></script>
<script language="javascript">
   var date_desc;
  
   function setSelectValue()
   {
     if(date_desc)
     {
       date_desc.value=$F('date_box');
       Element.hide('date_panel'); 

     }

   }
   
   function showDateSelectBox(srcobj)
   {
       //if(event.button==2)
       //{
          date_desc=srcobj;
          Element.show('date_panel');   
          var pos=getAbsPosition(srcobj);
	  with($('date_panel'))
	  {
	        style.position="absolute";
	        if(navigator.appName.indexOf("Microsoft")!= -1){
	    		style.posLeft=pos[0]-1;
				style.posTop=pos[1]-1+srcobj.offsetHeight;
			}else{
				style.left=pos[0]+"px";
				style.top=pos[1]+srcobj.offsetHeight+"px";
			}
			style.width=(srcobj.offsetWidth<150)?150:srcobj.offsetWidth+1;
       }                 
       //}
   }
   
   function symbol(editor,strexpr){
	//xuj update 2011-5-26
		var expr_editor=document.getElementById(editor);
	    expr_editor.focus();
		var element = document.selection;
		if (element&&element!=null) 
		{
		  var rge = element.createRange();
		  if (rge!=null)	
		  	  rge.text=strexpr;
		}else{
			var word = expr_editor.value;
			var _length=strexpr.length;
			var startP = expr_editor.selectionStart;
			var endP = expr_editor.selectionEnd;
			var ddd=word.substring(0,startP)+strexpr+word.substring(endP);
	    	expr_editor.value=ddd;
	      		expr_editor.setSelectionRange(startP+_length,startP+_length); 
		}
}   
function selectCheckBox(obj)
{
   if(obj.checked==true)
   {
      var Info=eval("info_cue1");	
	  Info.style.display="block";
   }else
   {
       var Info=eval("info_cue1");	
	   Info.style.display="none";
      
   }

}

function savecond(){	
	if(document.returnValue){
		
		 var theurl="/workbench/query/hquerycond_interface.do?b_list_cond=link";
           var iframe_url="/general/query/common/iframe_query.jsp?src="+theurl;
           var dw=550,dh=400,dl=(screen.width-dw)/2,dt=(screen.height-dh)/2;
            var return_vo= window.showModalDialog(iframe_url,0, 
              "dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:550px; dialogHeight:400px;resizable:no;center:yes;scroll:no;status:no");
	}
}

function search(){
	if(!validate('RS','dbpre','人员库'))
		return false;
	
	if(!checkExpression ())
		return false;
	
	var searchButton = document.getElementById("searchButton");
	if(searchButton)
		searchButton.disabled = true;
	
	var returnButton = document.getElementById("returnButton");
	if(returnButton)
		returnButton.disabled = true;
	
	var clearButton = document.getElementById("clearButton");
	if(clearButton)
		clearButton.disabled = true;
	
	var saveButton = document.getElementById("saveButton");
	if(saveButton)
		saveButton.disabled = true;
	
	var clearupButton = document.getElementById("clearupButton");
	if(clearupButton)
		clearupButton.disabled = true;
	
	highQueryForm.action="/workbench/query/hquerycond_interface.do?b_query=link";
	highQueryForm.submit();
}

function checkExpression (){
	var expression = document.getElementById("expression");
	if(!expression)
		return true;
	
	var value = expression.value;
	if(value.charAt(0) == "*" || value.charAt(0) == "+" || value.charAt(value.length - 1) == "*"
			|| value.charAt(value.length - 1) == "+" || value.charAt(value.length - 1) == "!") {
		alert(LEFTEXPR_ERROR);	
		return false;
	} else 
		return true;
}
</script>
<hrms:themes />
<style>
.x-btn-default-toolbar-small .x-frame-tl{
	background-color: #c5c5c5;
	background-image: none;
}

.x-btn-default-toolbar-small .x-frame-tc{
	background-color: #c5c5c5;
	background-image: none;
}

.x-btn-default-toolbar-small .x-frame-tr{
	background-color: #c5c5c5;
	background-image: none;
}

.x-btn-default-toolbar-small .x-frame-bl{
	background-color: #c5c5c5;
	background-image: none;
}

.x-btn-default-toolbar-small .x-frame-bc{
	background-color: #c5c5c5;
	background-image: none;
}

.x-btn-default-toolbar-small .x-frame-br{
	background-color: #c5c5c5;
	background-image: none;
}

</style>
<html:form action="/workbench/query/hquerycond_interface">
	<table width="700" border="0" cellpadding="0" cellspacing="0"
		align="center" style="margin-top: 5px;margin-left:-5px;">
		<tr height="20">
			<logic:equal name="highQueryForm" property="query_type" value="1">
				<td align="left" class="TableRow1">
					<bean:message key="label.query.hquery" />
				</td>
			</logic:equal>
			<logic:equal name="highQueryForm" property="query_type" value="2">
				<td align="left" class="TableRow1">
					<bean:message key="label.query.cquery" />
				</td>
			</logic:equal>
			<logic:equal name="highQueryForm" property="query_type" value="3">
				<td align="left" class="TableRow1">
					<bean:message key="label.sys.cond" />
				</td>
			</logic:equal>
		</tr>
		<tr>
			<td class="framestyle">
				<table border="0" cellspacing="0" width="694" cellpadding="0" align="center">
					<tr><td height="5px"></td></tr>
					<logic:notEqual name="highQueryForm" property="query_type"
						value="3">
						<logic:equal name="highQueryForm" property="type" value="1">
							<tr>
								<td height="30">
									&nbsp;<bean:message key="label.query.dbpre" />
									<hrms:importgeneraldata showColumn="dbname" valueColumn="pre"
										flag="false" paraValue="" sql="highQueryForm.dbcond"
										collection="list" scope="page" />
									<bean:size id="length" name="list" scope="page" />
									<html:select name="highQueryForm" property="dbpre" size="1">
										<html:options collection="list" property="dataValue"
											labelProperty="dataName" />
										<html:option value="All">全部人员库</html:option>
									</html:select>
								</td>
							</tr>
						</logic:equal>
					</logic:notEqual>
					<tr><!-- 【6861】员工管理：简单查询当指标选择的比较多的时候，页面出现空白了 jingq upd 2015.01.22 -->
					<logic:notEqual name="highQueryForm" property="query_type" value="1">
						<td align="left" height="200px;" style="padding: 0px 5px;">
					</logic:notEqual>
					<logic:equal name="highQueryForm" property="query_type" value="1">
						<td align="left" height="300px;" style="padding: 0px 5px;">
					</logic:equal>
							<div style="width:100%;height:100%;overflow-y:auto;overflow-x:hidden;border:1px solid;position:relative;" class="common_border_color">
								<table border="0" cellspacing="0" width="100%"
									cellpadding="0" align="center">
									<tr>
										<logic:equal name="highQueryForm" property="query_type"
											value="1">
											<td align="center" nowrap class="TableRow" style="border-top:none;border-left:none;border-right: none;">
												<bean:message key="label.query.logic" />
											</td>
										</logic:equal>
										<logic:notEqual name="highQueryForm" property="query_type"
											value="1">
											<td align="center" nowrap class="TableRow" style="border-top:none;border-left:none;border-right: none;">
												<bean:message key="label.query.number" />
											</td>
										</logic:notEqual>
										<td align="center" nowrap class="TableRow" style="border-top:none;border-right: none;">
											<bean:message key="label.query.field" />
										</td>
										<td align="center" nowrap class="TableRow" style="border-top:none;border-right: none;">
											<bean:message key="label.query.relation" />
										</td>
										<td align="center" nowrap class="TableRow" style="border-top:none; border-right: none;">
											<bean:message key="label.query.value" />
										</td>
									</tr>
									<logic:iterate id="element" name="highQueryForm"
										property="factorlist" indexId="index">
										<tr>
											<logic:equal name="highQueryForm" property="query_type"
												value="1">
												<td align="center" class="RecordRow" nowrap style="border-top:none;border-left:none;border-right: none;">
													<%
														if (i != 0) {
													%>
													<hrms:optioncollection name="highQueryForm"
														property="logiclist" collection="list" />
													<html:select name="highQueryForm"
														property='<%="factorlist[" + index
										+ "].log"%>'
														size="1">
														<html:options collection="list" property="dataValue"
															labelProperty="dataName" />
													</html:select>
													<%
														}
													%>
												</td>
											</logic:equal>
											<logic:notEqual name="highQueryForm" property="query_type"
												value="1">
												<td align="center" class="RecordRow" nowrap style="border-top:none;border-left:none;border-right: none;">
													<%=i + 1%>
												</td>
											</logic:notEqual>
											<td align="center" class="RecordRow" style="border-top:none;border-right: none;" nowrap>
												<bean:write name="element" property="hz" />
												&nbsp;
											</td>
											<td align="center" class="RecordRow" style="border-top:none;border-right: none;" nowrap>
												<hrms:optioncollection name="highQueryForm"
													property="operlist" collection="list" />
												<html:select name="highQueryForm"
													property='<%="factorlist[" + index
										+ "].oper"%>'
													size="1">
													<html:options collection="list" property="dataValue"
														labelProperty="dataName" />
												</html:select>
											</td>
											<!--日期型 -->
											<logic:equal name="element" property="fieldtype" value="D">
												<td align="left" class="RecordRow" nowrap style="border-top:none;border-right: none;">
													<html:text name="highQueryForm"
														property='<%="factorlist[" + index
								+ "].value"%>'
														size="30" maxlength="30" styleClass="text4"
														ondblclick="showDateSelectBox(this);" />
												</td>
											</logic:equal>
											<!--备注型 -->
											<logic:equal name="element" property="fieldtype" value="M">
												<td align="left" class="RecordRow" nowrap style="border-top:none;border-right: none;">
													<html:text name="highQueryForm"
														property='<%="factorlist[" + index
								+ "].value"%>'
														size="30"
														maxlength='<%="factorlist[" + index
								+ "].itemlen"%>'
														styleClass="text4" />
												</td>
											</logic:equal>
											<!--字符型 -->
											<logic:equal name="element" property="fieldtype" value="A">
												<td align="left" class="RecordRow" nowrap style="border-top:none;border-right: none;">
													<logic:notEqual name="element" property="codeid"
														value="0">
														<%String valueName =  "factorlist[" + index + "].value";
														String delFuntion =  "deleteData(this,'"+valueName+"');";
														%>
														<html:hidden name="highQueryForm"
															property='<%="factorlist[" + index
									+ "].value"%>'
															styleClass="text4" />
														<html:text name="highQueryForm"
															property='<%="factorlist[" + index
									+ "].hzvalue"%>'
															onkeydown='<%=delFuntion %>'
															size="30" maxlength="50" styleClass="text4"
															onchange="fieldcode(this,1)" />
														<logic:notEqual name="element" property="codeid"
															value="UN">
															<logic:equal name="element" property="fieldname"
																value="e0122">
																<logic:equal name="highQueryForm" property="query_type" value="3">
																<%-- 使用代码组件控件兼容非IE浏览器 wangb 20180201  --%>
                        <img src="/images/code.gif" align="absmiddle" id="factorlist<%=index %>" editable="true" nmodule="4" ctrltype="3" onlySelectCodeset="true" plugin="codeselector" codesetid="UM" inputname='<%="factorlist["+index+"].hzvalue"%>' multiple='false' valuename="<%="factorlist["+index+"].value"%>"/>
				
																</logic:equal>
																
																<logic:notEqual name="highQueryForm" property="query_type" value="3">
																<%-- 使用代码组件控件兼容非IE浏览器 wangb 20180201  --%>
                        <img src="/images/code.gif" align="absmiddle" id="factorlist<%=index %>" editable="true" nmodule="4" ctrltype="3" onlySelectCodeset="true" plugin="codeselector" codesetid="UM" inputname='<%="factorlist["+index+"].hzvalue"%>' multiple='true' valuename="<%="factorlist["+index+"].value"%>"/>
				
																</logic:notEqual>
																
															</logic:equal>
															<logic:equal name="element" property="codeid"
																value="@K">
																<logic:equal name="highQueryForm" property="query_type" value="3">
																	<%-- 使用代码组件控件兼容非IE浏览器 wangb 20180201  --%>
                        <img src="/images/code.gif" align="absmiddle" id="factorlist<%=index %>" editable="true" nmodule="4" ctrltype="3"  onlySelectCodeset="true" plugin="codeselector" codesetid="@K" inputname='<%="factorlist["+index+"].hzvalue"%>' multiple='false' valuename="<%="factorlist["+index+"].value"%>"/>
																</logic:equal>
																
																<logic:notEqual name="highQueryForm" property="query_type" value="3">
																	<%-- 使用代码组件控件兼容非IE浏览器 wangb 20180201  --%>
                        <img src="/images/code.gif" align="absmiddle" id="factorlist<%=index %>" editable="true" nmodule="4" ctrltype="3"  onlySelectCodeset="true" plugin="codeselector" codesetid="@K" inputname='<%="factorlist["+index+"].hzvalue"%>' multiple='true' valuename="<%="factorlist["+index+"].value"%>"/>
																</logic:notEqual>	
															</logic:equal>
															<logic:notEqual name="element" property="fieldname"
																value="e0122">
																<logic:notEqual name="element" property="codeid"
																	value="@K">
																	<logic:equal name="highQueryForm" property="query_type" value="3">
																		<%-- 使用代码组件控件兼容非IE浏览器 wangb 20180201  --%>
																		<logic:equal name="element" property="fieldname" value="${highQueryForm.part_unit }">
                        													<img src="/images/code.gif" align="absmiddle" id="factorlist<%=index %>" 
                        														editable="true" onlySelectCodeset="false" plugin="codeselector" 
                        														codesetid="${element.codeid}" inputname='<%="factorlist["+index+"].hzvalue"%>' 
                        														multiple='true' valuename="<%="factorlist["+index+"].value"%>"/>
																		</logic:equal>
																		<logic:notEqual name="element" property="fieldname" value="${highQueryForm.part_unit }">
                        													<img src="/images/code.gif" align="absmiddle" id="factorlist<%=index %>" 
                        														editable="true" onlySelectCodeset="false" plugin="codeselector" 
                        														codesetid="${element.codeid}" inputname='<%="factorlist["+index+"].hzvalue"%>' 
                        														multiple='false' ctrltype="0" valuename="<%="factorlist["+index+"].value"%>"/>
																		</logic:notEqual>
																</logic:equal>
																
																<logic:notEqual name="highQueryForm" property="query_type" value="3">
																		<%-- 使用代码组件控件兼容非IE浏览器 wangb 20180201  --%>
                        												<logic:equal name="element" property="fieldname" value="${highQueryForm.part_unit }">
                        													<img src="/images/code.gif" align="absmiddle" id="factorlist<%=index %>" 
                        														editable="true" onlySelectCodeset="false" plugin="codeselector" 
                        														codesetid="${element.codeid}" inputname='<%="factorlist["+index+"].hzvalue"%>' 
                        														multiple='true' valuename="<%="factorlist["+index+"].value"%>"/>
																		</logic:equal>
																		<logic:notEqual name="element" property="fieldname" value="${highQueryForm.part_unit }">
                        													<img src="/images/code.gif" align="absmiddle" id="factorlist<%=index %>" 
                        														editable="true" onlySelectCodeset="false" plugin="codeselector" 
                        														codesetid="${element.codeid}" inputname='<%="factorlist["+index+"].hzvalue"%>' 
                        														multiple='true' valuename="<%="factorlist["+index+"].value"%>"/>
																		</logic:notEqual>
																</logic:notEqual>
																</logic:notEqual>
															</logic:notEqual>
														</logic:notEqual>
														<logic:equal name="element" property="codeid" value="UN">
															<logic:equal name="highQueryForm" property="type"
																value="2">
																<logic:equal name="element" property="fieldname"
																	value="b0110">
																<logic:equal name="highQueryForm" property="query_type" value="3">
																		<%-- 使用代码组件控件兼容非IE浏览器 wangb 20180201  --%>
                        <img src="/images/code.gif" align="absmiddle" id="factorlist<%=index %>" editable="true" nmodule="4" ctrltype="3"  onlySelectCodeset="true" plugin="codeselector" codesetid="UM" inputname='<%="factorlist["+index+"].hzvalue"%>' multiple='false' valuename="<%="factorlist["+index+"].value"%>"/>
																</logic:equal>
																<logic:notEqual name="highQueryForm" property="query_type" value="3">
																		<%-- 使用代码组件控件兼容非IE浏览器 wangb 20180201  --%>
                        <img src="/images/code.gif" align="absmiddle" id="factorlist<%=index %>" editable="true" nmodule="4" ctrltype="3"  onlySelectCodeset="true" plugin="codeselector" codesetid="UM" inputname='<%="factorlist["+index+"].hzvalue"%>' multiple='true' valuename="<%="factorlist["+index+"].value"%>"/>
																</logic:notEqual>
																</logic:equal>
																<logic:notEqual name="element" property="fieldname"
																	value="b0110">
																<logic:equal name="highQueryForm" property="query_type" value="3">
																		<%-- 使用代码组件控件兼容非IE浏览器 wangb 20180201  --%>
                        <img src="/images/code.gif" align="absmiddle" id="factorlist<%=index %>" editable="true" onlySelectCodeset="true" plugin="codeselector" codesetid="UM" inputname='<%="factorlist["+index+"].hzvalue"%>' multiple='false' valuename="<%="factorlist["+index+"].value"%>"/>
																</logic:equal>
																<logic:notEqual name="highQueryForm" property="query_type" value="3">
																		<%-- 使用代码组件控件兼容非IE浏览器 wangb 20180201  --%>
                        <img src="/images/code.gif" align="absmiddle" id="factorlist<%=index %>" editable="true" nmodule="4" ctrltype="3"  onlySelectCodeset="true" plugin="codeselector" codesetid="UM" inputname='<%="factorlist["+index+"].hzvalue"%>' multiple='true' valuename="<%="factorlist["+index+"].value"%>"/>
																</logic:notEqual>
																</logic:notEqual>
															</logic:equal>
															<logic:notEqual name="highQueryForm" property="type"
																value="2">
																<logic:equal name="element" property="fieldname"
																	value="b0110">
																	
																	<logic:equal name="highQueryForm" property="query_type" value="3">
																		<%-- 使用代码组件控件兼容非IE浏览器 wangb 20180201  --%>
                        <img src="/images/code.gif" align="absmiddle" id="factorlist<%=index %>" editable="true" nmodule="4" ctrltype="3"  onlySelectCodeset="true" plugin="codeselector" codesetid="UN" inputname='<%="factorlist["+index+"].hzvalue"%>' multiple='false' valuename="<%="factorlist["+index+"].value"%>"/>
																</logic:equal>
																<logic:notEqual name="highQueryForm" property="query_type" value="3">
																		<%-- 使用代码组件控件兼容非IE浏览器 wangb 20180201  --%>
                        <img src="/images/code.gif" align="absmiddle" id="factorlist<%=index %>" editable="true" nmodule="4" ctrltype="3"  onlySelectCodeset="true" plugin="codeselector" codesetid="UN" inputname='<%="factorlist["+index+"].hzvalue"%>' multiple='true' valuename="<%="factorlist["+index+"].value"%>"/>
																</logic:notEqual>
																</logic:equal>
																<logic:notEqual name="element" property="fieldname"
																	value="b0110">
																	<logic:equal name="highQueryForm" property="query_type" value="3">
																		<%-- 使用代码组件控件兼容非IE浏览器 wangb 20180201  --%>
                        <img src="/images/code.gif" align="absmiddle" id="factorlist<%=index %>" editable="true" onlySelectCodeset="true" plugin="codeselector" codesetid="${element.codeid}" inputname='<%="factorlist["+index+"].hzvalue"%>' multiple='false' valuename="<%="factorlist["+index+"].value"%>"/>
																</logic:equal>
																<logic:notEqual name="highQueryForm" property="query_type" value="3">
																		<%-- 使用代码组件控件兼容非IE浏览器 wangb 20180201  --%>
                        <img src="/images/code.gif" align="absmiddle" id="factorlist<%=index %>" editable="true" onlySelectCodeset="true" plugin="codeselector" codesetid="${element.codeid}" inputname='<%="factorlist["+index+"].hzvalue"%>' multiple='true' valuename="<%="factorlist["+index+"].value"%>"/>
																</logic:notEqual>
																</logic:notEqual>
															</logic:notEqual>
														</logic:equal>
													</logic:notEqual>
													<logic:equal name="element" property="codeid" value="0">
														<html:text name="highQueryForm"
															property='<%="factorlist[" + index
									+ "].value"%>'
															size="30" maxlength="${element.itemlen}"
															styleClass="text4" />
													</logic:equal>
												</td>
											</logic:equal>
											<!--数据值-->
											<logic:equal name="element" property="fieldtype" value="N">
												<td align="left" class="RecordRow" nowrap style="border-top:none;border-right: none;">
													<html:text name="highQueryForm"
														property='<%="factorlist[" + index
								+ "].value"%>'
														size="30" maxlength="${element.itemlen}"
														styleClass="text4" />
												</td>
											</logic:equal>
										</tr>
										<%
											++i;
										%>
									</logic:iterate>
									</table>
                            </div>
                            </td>
                        </tr>
									<logic:notEqual name="highQueryForm" property="query_type"
										value="1">
										<tr>
											<td align="left" nowrap class="RecordRow" style="border: none;padding-top: 5px;">
												<span>
												    <bean:message key="label.query.expression" />
												</span>
												<br>
												<html:textarea property="expression" styleId="expression"
													rows="5" cols="109"
													onclick="this.pos=document.selection.createRange();" 
													style="height:90px;width:600px;margin-bottom:5px;overflow:auto;"/>
											</td>
										</tr>
										<tr>
											<td align="left" nowrap class="RecordRow"
												style="height: 35px;border:none;">
												&nbsp;
												<input type="button" value="&nbsp;(&nbsp;&nbsp;"
													onclick="symbol('expression','(');" class="mybutton">
												<input type="button" value="&nbsp;且&nbsp;"
													onclick="symbol('expression','*');" class="mybutton">
												<input type="button" value="&nbsp;非&nbsp;"
													onclick="symbol('expression','!');" class="mybutton">
												<input type="button" value="&nbsp;&nbsp;)&nbsp;"
													onclick="symbol('expression',')');" class="mybutton">
												<input type="button" value="&nbsp;或&nbsp;"
													onclick="symbol('expression','+');" class="mybutton">

											</td>
										</tr>
									</logic:notEqual>
									<!-- 查询定义才出现此选项 -->
									<logic:notEqual name="highQueryForm" property="query_type" value="3">
										<tr>
											<td align="center" nowrap class="RecordRow" style="border: none;">
												<table width="100%" border="0" cellspacing="0"
													cellpadding="0">
													<tr>
														<td align="center" nowrap>
															<html:checkbox name="highQueryForm" property="like"
																value="1" onclick="selectCheckBox(this);">&nbsp;<bean:message
																	key="label.query.like" />
															</html:checkbox>
															&nbsp;&nbsp;&nbsp;&nbsp;
															<html:checkbox name="highQueryForm" property="history"
																value="1">&nbsp;<bean:message
																	key="label.query.history" />
															</html:checkbox>
															<logic:equal name="userView" property="status"
																value="0">
																<html:checkbox name="highQueryForm" property="result"
																	value="1">
																	<bean:message key="label.query.second" />
																</html:checkbox>
															</logic:equal>
														</td>
													</tr>
													<tr>
														<td align="center" nowrap>
															<div id="info_cue1" style='display: none;'
																class="query_cue1">
																<bean:message key="infor.menu.query.cue2" />
															</div>
														</td>
													</tr>
												</table>

											</td>
										</tr>
									</logic:notEqual>

					<tr class="list3">
						<td height="3"></td>
					</tr>
				</table>
			</td>
		</tr>
		<tr class="list3">
			<td colspan="4" align="center" nowrap
				style="height: 35px">
				<logic:notEqual name="highQueryForm" property="query_type" value="3">
					<logic:equal name="highQueryForm" property="type" value="2">
						<html:radio name="highQueryForm" property="qobj" value="1">
							<bean:message key="label.query.dept" />
						</html:radio>
						<html:radio name="highQueryForm" property="qobj" value="2">
							<bean:message key="label.query.org" />
						</html:radio>
						<html:radio name="highQueryForm" property="qobj" value="0">
							<bean:message key="label.query.all" />
						</html:radio>
					</logic:equal>
					<html:button styleClass="mybutton" styleId="searchButton" property="b_query"
						onclick="search();">
						<bean:message key="button.query" />
					</html:button>
					<logic:notEqual name="highQueryForm" property="query_type"
						value="1">
						<hrms:submit styleClass="mybutton" styleId="clearupButton" property="b_list_cond"
							onclick="document.highQueryForm.target='_self';if(checkExpression()){validate('R','expression','因子表达式');return document.returnValue;} else {return false;}">
							<bean:message key="button.save" />
						</hrms:submit>
					</logic:notEqual>
				</logic:notEqual>
				<logic:equal name="highQueryForm" property="query_type" value="3">
					<hrms:submit styleClass="mybutton" styleId="saveButton" property="b_save"
						onclick="document.highQueryForm.target='_self';if(checkExpression()){validate('R','expression','因子表达式');return document.returnValue;} else {return false;}">
						<bean:message key="button.save" />
					</hrms:submit>
					<hrms:submit styleClass="mybutton" styleId="clearupButton" property="b_clear">
						<bean:message key="button.clearup" />
					</hrms:submit>
				</logic:equal>
				<hrms:submit styleClass="mybutton" styleId="returnButton" property="br_return">
					<bean:message key="button.query.pre" />
				</hrms:submit>
				<html:reset styleClass="mybutton" styleId="clearButton">
					<bean:message key="button.clear" />
				</html:reset>
			</td>
		</tr>
	</table>
	<div id="date_panel" style="display: none">
		<select name="date_box" multiple="multiple" size="10"
			style="width: 200" onchange="setSelectValue();"
			onclick="setSelectValue();">
			<option value="$AGE_Y[10]">
				年份差
			</option>
			<option value="$WORKAGE[10]">
				工龄
			</option>
			<option value="$YRS[10]">
				年限
			</option>
			<option value="当年">
				当年
			</option>
			<option value="当月">
				当月
			</option>
			<option value="当天">
				当天
			</option>
			<option value="今天">
				今天
			</option>
			<option value="截止日期">
				截止日期
			</option>
			<option value="1992.4.12">
				1992.04.12
			</option>
			<option value="1992.4">
				1992.04
			</option>
			<option value="1992">
				1992
			</option>
			<option value="1992-04-12">
				1992-04-12
			</option>
			<option value="1992-04">
				1992-04
			</option>
			<!-- 【6206】员工管理：简单查询、通用查询的日期型指标的下拉框中缺少????.04等格式的可选项  jingq add 2014.12.30 -->
			<option value="????.??.12">
				????.??.12
			</option>
			<option value="????.4.12">
				????.4.12
			</option>
			<option value="????.4">
				????.4
			</option>
		</select>
	</div>
</html:form>
<script language="javascript">
   Element.hide('date_panel');
   if(!getBrowseVersion()){//兼容非IE浏览器  wangb 20180126
			var form =document.getElementsByName('highQueryForm')[0];
			var firstTable = form.getElementsByTagName('table')[0]; 
			firstTable.style.marginLeft='';
			var divTable = firstTable.getElementsByClassName('common_border_color')[0]; 
			//divTable.setAttribute('style','overflow-x:hidden;');
			divTable.style.overflowX='hidden';
	}
</script>