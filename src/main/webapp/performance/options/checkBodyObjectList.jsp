<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<hrms:themes />
<script language="JavaScript" src="/js/function.js"></script>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<style>
.myfixedDiv 
{ 
	overflow:auto;
	BORDER-BOTTOM: #94B6E6 1pt solid; 
    BORDER-LEFT: #94B6E6 1pt solid; 
    BORDER-RIGHT: #94B6E6 1pt solid; 
    BORDER-TOP: #94B6E6 1pt solid ; 
}
</style>
<hrms:themes />
<script>
   function add(){   
	   var bodyType = document.getElementById("bodyType").value;	
	   var target_url="/performance/options/checkBodyObjectAdd.do?b_add=link`bodyType="+bodyType+"`callbackFunc=check_ok";
	   var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(target_url);
	   var height = 230;
	   if(getBrowseVersion()) {
		   height = 190;
	   }
	   var config = {
		    width:410,
		    height:height,
		    type:'1',
            title:(bodyType=='0')?'主体类别维护':'对象类别维护',
		    id:'checkBody_win'
		}

		modalDialog.showModalDialogs(iframe_url,"checkBody_win",config,check_ok);
	}
   
   function check_ok(return_vo) {
	   if(return_vo==null)
			 return ;	
	   if(return_vo.flag=="true") 
	   		 reflesh();   	
   }
   
   function reflesh(){
   		var bodyType = document.getElementById("bodyType").value;
		document.checkBodyObjectForm.action="/performance/options/checkBodyObjectList.do?b_query=link&bodyType="+bodyType+"&noself=${checkBodyObjectForm.noself}";
	    document.checkBodyObjectForm.submit();
   }
   function edit(bodyId)
   {   
   	   var target_url="/performance/options/checkBodyObjectAdd.do?b_edit=link`bodyId="+bodyId+"`info=edit`callbackFunc=check_ok";
	   var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(target_url);
	   //var return_vo=window.showModalDialog(iframe_url,'glWin','dialogWidth:400px; dialogHeight:300px;resizable:no;center:yes;scroll:no;status:no');
	   var height = 230;
	  if(window.showModalDialog) {
          height = 210;
      }
       var bodyType = document.getElementById("bodyType").value;
	   var config = {
		    width:410,
		    height:height,
		    type:'1',
            title:bodyType==0?'主体类别维护':'对象类别维护',
		    id:'checkBody_win'
		}

		modalDialog.showModalDialogs(iframe_url,"checkBody_win",config,check_ok);
   }

	
	function checkdelete() {
		var bodyType = document.getElementById("bodyType").value;
		var str = "";
		for (var i = 0; i < document.checkBodyObjectForm.elements.length; i++) {
			if (document.checkBodyObjectForm.elements[i].type == "checkbox") {
				if (document.checkBodyObjectForm.elements[i].checked == true && document.checkBodyObjectForm.elements[i].name != "selbox") {
					str += document.checkBodyObjectForm.elements[i + 1].value + "/";
				}
			}
		}
		if (str.length == 0) {
			alert("<bean:message key='jx.paramset.selDel'/>");
			return;
		} else {
			var message = "";
			if (bodyType == 1)
				message = "确认删除对象类别？";
			else
				message = "确认删除主体类别？";

			if (confirm(message)) {
				//checkBodyObjectForm.action="/performance/options/checkBodyObjectList.do?b_delete=link&deletestr="+str+"&bodyType="+bodyType;
				//checkBodyObjectForm.submit();

				var hashvo = new ParameterSet();
				hashvo.setValue("deletestr", str);
				hashvo.setValue("bodyType", bodyType);
				var request = new Request({
					method : 'post',
					asynchronous : false,
					onSuccess : delRefresh,
					functionId : '9026001005'
				}, hashvo);
			}
		}
	}
	function delRefresh(outparamters) {
		reflesh();
	}

	// 清空条件
	function clearoption() {
		var bodyType = document.getElementById("bodyType").value;
		var str = "";
		for (var i = 0; i < document.checkBodyObjectForm.elements.length; i++) {
			if (document.checkBodyObjectForm.elements[i].type == "checkbox") {
				if (document.checkBodyObjectForm.elements[i].checked == true && document.checkBodyObjectForm.elements[i].name != "selbox") {
					str += document.checkBodyObjectForm.elements[i + 1].value + "/";
				}
			}
		}
		if (str.length == 0) {
			alert("请选择要清空条件的考核主体类别！");
			return;
		} else {
			if (confirm("确认要清除考核主体筛选条件吗？")) {
				var hashvo = new ParameterSet();
				hashvo.setValue("deletestr", str);
				hashvo.setValue("bodyType", bodyType);
				var request = new Request({
					method : 'post',
					asynchronous : false,
					onSuccess : delRefresh,
					functionId : '9026001010'
				}, hashvo);
			}
		}
	}

	function moveSeq(num) {
		var str = "";
		var seq = "";
		var j = 0;
		for (var i = 0; i < document.checkBodyObjectForm.elements.length; i++) {
			if (document.checkBodyObjectForm.elements[i].type == "checkbox") {
				if (document.checkBodyObjectForm.elements[i].checked == true) {
					str += document.checkBodyObjectForm.elements[i + 1].value;
					seq += document.checkBodyObjectForm.elements[i + 2].value;
					j++;
				}
			}
		}
		if (j > 1) {
			alert("<bean:message key='jx.paramset.info3'/>?");
			return;
		}

		if (str.length == 0) {
			alert("<bean:message key='label.select'/>!");
			return;
		} else {
			checkBodyObjectForm.action = "/performance/options/checkBodyObjectList.do?b_remove=link&deletestr=" + str + "&num=" + num + "&seq=" + seq;
			checkBodyObjectForm.submit();
		}
	}

	function IfWindowClosed() {
		if (newwindow.closed == true) {
			window.clearInterval(timer)
			checkBodyObjectForm.action = "/performance/options/checkBodyObjectList.do?b_query=link"
			checkBodyObjectForm.submit();
		}
	}
	function tr_bgcolor(nid) {
		var tablevos = document.getElementsByTagName("input");
		for (var i = 0; i < tablevos.length; i++) {
			if (tablevos[i].type == "checkbox") {
				var cvalue = tablevos[i];
				var td = cvalue.parentNode.parentNode;
				td.style.backgroundColor = '';
			}
		}
		var c = document.getElementById(nid);
		var tr = c.parentNode.parentNode;
		if (tr.style.backgroundColor != '') {
			tr.style.backgroundColor = '';
		} else {
			tr.style.backgroundColor = '#add6a6';
		}
	}

	function toSorting() {
		var bodyType = document.getElementById("bodyType").value;
		var target_url = "/performance/options/checkBodySort.do?b_sort=link`noself=${checkBodyObjectForm.noself}`bodyType=" + bodyType;
		var iframe_url = "/general/query/common/iframe_query.jsp?src=" + $URL.encode(target_url);
		/* if(isIE6()){
		    var return_vo= window.showModalDialog(iframe_url, "", 
		       "dialogWidth:505px; dialogHeight:430px;resizable:no;center:yes;scroll:no;status:no");
		}else{
		    var return_vo= window.showModalDialog(iframe_url, "", 
		       "dialogWidth:500px; dialogHeight:430px;resizable:no;center:yes;scroll:no;status:no");
		} */

		var config = {
			width : 550,
			height : 440,
			type : '1',
			id : "toSorting_win"
		}

		modalDialog.showModalDialogs(iframe_url, "toSorting_win", config, toSorting_ok);
	}

	function toSorting_ok(return_vo) {
		if (return_vo != null) {
			reflesh();
		}
	}
	function myClose() {
		var thevo = new Object();
		thevo.flag = "true";
		window.returnValue = thevo;
	}
	function setType(id1, id2, id3) {
		var obj = eval(id1);
		var obj2 = eval(id2);
		var obj3 = eval(id3);
		obj.style.display = 'inline';
		obj2.style.display = 'none';
		if (id1.indexOf("b") != -1) {
			obj3.style.display = 'inline';
		} else {
			obj3.style.display = 'none';
		}
	}
	var body_id_ = "";
	function simpleCondition(exist, body_id, a_cexpr, a_condStr) {
		body_id_ = body_id;
		var info, queryType, dbPre;
		info = "1";
		dbPre = "Usr";
		queryType = "1";
		var express = "";
		if (exist = 1) {
			express = a_cexpr + '|' + a_condStr;
		}
		express = getDecodeStr(express);
		var strExpression = generalExpressionDialog(info, dbPre, queryType, express,'','',true);
		extOpenStrExpression(strExpression);
	}
	function extOpenStrExpression(strExpression) {
		if (strExpression) {

			var temps = strExpression.split("|");
			var hashvo = new ParameterSet();
			hashvo.setValue("plan_id", "");
			hashvo.setValue("body_id", body_id_);
			hashvo.setValue("flag", "0");
			hashvo.setValue("cond", getEncodeStr(temps[1]));
			hashvo.setValue("cexpr", temps[0]);
			var request = new Request({
				method : 'post',
				asynchronous : false,
				onSuccess : setCondResult,
				functionId : '9026001008'
			}, hashvo);
		}
	}
	var body_complex_id = "";
	function complexCondition(exist, body_id, a_cexpr, a_condStr) {
		if (exist != 1) {
			a_condStr = "";
		}
		body_complex_id = body_id;
		a_condStr = getDecodeStr(a_condStr);
		var arguments = new Array(a_condStr, "0", GZ_TEMPLATESET_LOOKCONDITION, "4");
		var strurl = "/general/query/common/complexCondition.do?br_init=link`mode=jixiao_aoto";
		var iframe_url = "/general/query/common/iframe_query.jsp?src=" + $URL.encode(strurl);
		var config = {
			width : 870,
			height : 500,
			type : '2',
			id : 'complexCondition_win',
			dialogArguments : arguments
		}
		if (!window.showModalDialog)
			window.dialogArguments = arguments;
		modalDialog.showModalDialogs(iframe_url, "complexCondition_win", config, complexCondition_ok);
	}

	function complexCondition_ok(strExpression) {
		if (strExpression) {
			var hashvo = new ParameterSet();
			hashvo.setValue("plan_id", "");
			hashvo.setValue("body_id", body_complex_id);
			hashvo.setValue("flag", "0");
			hashvo.setValue("cond", getEncodeStr(strExpression));
			hashvo.setValue("cexpr", "");
			var request = new Request({
				method : 'post',
				asynchronous : true,
				onSuccess : setCondResult,
				functionId : '9026001008'
			}, hashvo);
		}
	}
	function setCondResult(outparamters) {
		var info = outparamters.getValue("info");
		if (info == "ok") {
			alert(PERFORMANCE_COND_SUCCES);
			reflesh();

		} else {
			alert(PERFORMANCE_COND_FAIL);
		}
	}
</script>
<%
int i = 0;
%>
<body onbeforeunload="myClose();">

	<html:form action="/performance/options/checkBodyObjectList">

	<table width="100%" border="0" align="center">
		<tr>
			<td >
			<div id="tbl-container" class="myfixedDiv">
		<table width="100%" border="0" cellspacing="0" align="center"
			cellpadding="0" class="ListTableF1">
			<input type="hidden" name="bodyType" id="bodyType" 
				value="${checkBodyObjectForm.bodyType}" />
			<thead>
				<tr class="fixedHeaderTr">
					<td align="center"  class="TableRow_right common_background_color common_border_color" nowrap>
						<input type="checkbox" name="selbox"
							onclick="batch_select(this, 'setlistform.select');">
					</td>
					<td align="center"  class="TableRow" nowrap>
						<bean:message key="report.number" />
					</td>


					<logic:equal name="checkBodyObjectForm" property="bodyType"
						value="0">
						<td align="center"  class="TableRow" nowrap>
							<bean:message key="jx.paramset.mainbodyname" />
						</td>
						<!-- 如果是民主推荐-投票人类别不显示该列 -->
						<logic:notEqual value="1" name="checkBodyObjectForm" property="noself">
						<td align="center"  class="TableRow" nowrap>
							<bean:message key="jx.param.dengji" />
						</td>
						</logic:notEqual>
					</logic:equal>
					<logic:equal name="checkBodyObjectForm" property="bodyType"
						value="1">
						<td align="center"  class="TableRow" nowrap>
							<bean:message key="jx.paramset.objectbodyname" />
						</td>
						<td align="center"  class="TableRow" nowrap>
							<bean:message key="jx.param.objectype" />
						</td>
					</logic:equal>

					<!-- 如果是民主推荐-投票人类别不显示该列 -->
					<logic:notEqual value="1" name="checkBodyObjectForm" property="noself">
					<td align="center"  class="TableRow" nowrap>
						<bean:message key="kh.field.flag" />
					</td>
					<logic:equal name="checkBodyObjectForm" property="bodyType"
						value="0">
					<!-- td align="center" class="TableRow" nowrap>
						<bean:message key="menu.performance.mainBodyrange" />
					</td -->
					<td align="center"  class="TableRow" width="250" nowrap>
						<bean:message key="label.title.selectcond" />
					</td>
					</logic:equal>
					</logic:notEqual>
					<td align="center"  class="TableRow_left common_background_color common_border_color" nowrap>
						<bean:message key="label.edit.user" />
					</td>

				</tr>
			</thead>
			<hrms:extenditerate id="element" name="checkBodyObjectForm"
				property="setlistform.list" indexes="indexes"
				pagination="setlistform.pagination" pageCount="1000" scope="session">
				<bean:define id="nid" name="element" property="string(body_id)" />
				<%
						if (i % 2 == 0)
						{
				%>
				<tr class="trShallow">
					<%
							} else
							{
					%>
				
				<tr class="trDeep">
					<%
							}
							i++;
					%>
					<td align="center" class="RecordRow_right" style="border-top:0px;" nowrap>
					<logic:notEqual name="element" property="string(body_id)"
							value="-1">
						<logic:notEqual name="element" property="string(body_id)"
							value="5">
							<hrms:checkmultibox name="checkBodyObjectForm"
								property="setlistform.select" value="true" indexes="indexes" />
						</logic:notEqual>
					</logic:notEqual>
					</td>
					<td align="right" style="border-top:0px;" class="RecordRow" nowrap>
						 <bean:write name="element" property="string(body_id)"
							filter="true" />
						<Input type='hidden'
							value='<bean:write name="element" property="string(body_id)" filter="true"/>'
							name='bodyId' />
						<Input type='hidden'
							value='<bean:write name="element" property="string(seq)" filter="true"/>'
							name='seq' />
					</td>
					<td align="left" style="border-top:0px;" class="RecordRow" nowrap>
						 &nbsp;<bean:write name="element" property="string(name)" filter="true" />
					</td>
					
			<!-- 如果是民主推荐-投票人类别不显示该列 -->
			<logic:notEqual value="1" name="checkBodyObjectForm" property="noself">
					
					<logic:equal name="checkBodyObjectForm" property="bodyType" value="1">
						<td align="left" style="border-top:0px;" class="RecordRow" nowrap>
							 &nbsp;<bean:write name="element" property="string(object_type)" filter="true" />
						</td>
					</logic:equal>
					<logic:equal name="checkBodyObjectForm" property="bodyType"
						value="0">
						<td align="left" style="border-top:0px;" class="RecordRow" nowrap> &nbsp;
							<logic:notEqual name="checkBodyObjectForm" property="dbType"
								value="oracle">
								<logic:equal name="element" property="string(level)" value="-2">
									<bean:message key='jx.param.degree8' />
								</logic:equal>
								<logic:equal name="element" property="string(level)" value="-1">
									<bean:message key='jx.param.degree7' />
								</logic:equal>
								<logic:equal name="element" property="string(level)" value="0">
									<bean:message key='jx.param.degree0' />
								</logic:equal>
								<logic:equal name="element" property="string(level)" value="1">
									<bean:message key='jx.param.degree1' />
								</logic:equal>
								<logic:equal name="element" property="string(level)" value="2">
									<bean:message key='jx.param.degree2' />
								</logic:equal>
								<logic:equal name="element" property="string(level)" value="3">
									<bean:message key='jx.param.degree3' />
								</logic:equal>
								<logic:equal name="element" property="string(level)" value="4">
									<bean:message key='jx.param.degree4' />
								</logic:equal>
								<logic:equal name="element" property="string(level)" value="5">
									<bean:message key='jx.param.degree5' />
								</logic:equal>
								<logic:equal name="element" property="string(level)" value="6">
									<bean:message key='jx.param.degree6' />
								</logic:equal>
							</logic:notEqual>
							<logic:equal name="checkBodyObjectForm" property="dbType"
								value="oracle">
								<logic:equal name="element" property="string(level_o)" value="-2">
									<bean:message key='jx.param.degree8' />
								</logic:equal>
								<logic:equal name="element" property="string(level_o)" value="-1">
									<bean:message key='jx.param.degree7' />
								</logic:equal>
								<logic:equal name="element" property="string(level_o)" value="0">
									<bean:message key='jx.param.degree0' />
								</logic:equal>
								<logic:equal name="element" property="string(level_o)" value="1">
									<bean:message key='jx.param.degree1' />
								</logic:equal>
								<logic:equal name="element" property="string(level_o)" value="2">
									<bean:message key='jx.param.degree2' />
								</logic:equal>
								<logic:equal name="element" property="string(level_o)" value="3">
									<bean:message key='jx.param.degree3' />
								</logic:equal>
								<logic:equal name="element" property="string(level_o)" value="4">
									<bean:message key='jx.param.degree4' />
								</logic:equal>
								<logic:equal name="element" property="string(level_o)" value="5">
									<bean:message key='jx.param.degree5' />
								</logic:equal>
								<logic:equal name="element" property="string(level_o)" value="6">
									<bean:message key='jx.param.degree6' />
								</logic:equal>
							</logic:equal>
						</td>
					</logic:equal>
					<td align="left" style="border-top:0px;" class="RecordRow" nowrap> &nbsp;
						<logic:equal name="element" property="string(status)" value="1">
							<bean:message key='column.law_base.status' />
						</logic:equal>
						<logic:equal name="element" property="string(status)" value="0">
							<bean:message key='lable.lawfile.invalidation' />
						</logic:equal>
					</td>
					<logic:equal name="checkBodyObjectForm" property="bodyType"
						value="0">
					<!--  td align="left" class="RecordRow" nowrap> &nbsp;
					<logic:notEqual name="element" property="string(body_id)" value="5">
						 <bean:write name="element" property="string(scope)" filter="true" />
					 </logic:notEqual>
					</td-->
					<td align="left" style="border-top:0px;" class="RecordRow" nowrap> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					<logic:notEqual name="element" property="string(body_id)" value="-1">
						<logic:notEqual name="element" property="string(body_id)" value="5">
							<logic:notEqual name="element" property="string(cexpr)" value="">
								 <input type="radio" name="operater<%=i %>"  onclick="setType('operater<%=i %>a','operater<%=i %>b','operater<%=i %>c')"  checked>
								  <bean:message key="gz.templateset.simpleCondition" />
								 <div  id="operater<%=i %>a" style='display:inline;' nowrap>
								 <a
									onclick="simpleCondition('1','<bean:write name="element" property="string(body_id)" filter="true"/>','<bean:write name="element" property="string(cexpr)" filter="true"/>','<bean:write name="element" property="string(cond)" filter="true"/>');"><img
										src="/images/edit.gif" border=0 style="cursor:hand;">
								</a>
								</div>
								<div  id="operater<%=i %>c" style='display:none;' nowrap>&nbsp;&nbsp;&nbsp;&nbsp;</div>
							 </logic:notEqual>
							 <logic:equal name="element" property="string(cexpr)" value="">
							 <input type="radio" name="operater<%=i %>"  onclick="setType('operater<%=i %>a','operater<%=i %>b','operater<%=i %>c')" >
							  <bean:message key="gz.templateset.simpleCondition" />
							  <div  id="operater<%=i %>a" style='display:none;' nowrap>
							 <a
								onclick="simpleCondition('0','<bean:write name="element" property="string(body_id)" filter="true"/>','<bean:write name="element" property="string(cexpr)" filter="true"/>','<bean:write name="element" property="string(cond)" filter="true"/>');"><img
									src="/images/edit.gif" border=0 style="cursor:hand;">
							</a>
							</div>
							 <div  id="operater<%=i %>c" style='display:inline;' nowrap>&nbsp;&nbsp;&nbsp;&nbsp;</div>
							 </logic:equal>
							
						  
							
							<logic:notEqual name="element" property="string(cexpr)" value="">
							 <input type="radio" name="operater<%=i %>"  onclick="setType('operater<%=i %>b','operater<%=i %>a','operater<%=i %>c')" >
							 <bean:message key="popedom.gjtj" />
							  <div  id="operater<%=i %>b" style='display:none;' nowrap>
							  
							 <a
								onclick="complexCondition('0','<bean:write name="element" property="string(body_id)" filter="true"/>','<bean:write name="element" property="string(cexpr)" filter="true"/>','<bean:write name="element" property="string(cond)" filter="true"/>');"><img
									src="/images/edit.gif" border=0 style="cursor:hand;">
							</a>
							</div>
							 </logic:notEqual>
							 <logic:equal name="element" property="string(cexpr)" value="">
							  <logic:notEqual name="element" property="string(cond)" value="">
							 <input type="radio" name="operater<%=i %>"  onclick="setType('operater<%=i %>b','operater<%=i %>a','operater<%=i %>c')" checked>
							  <bean:message key="popedom.gjtj" />
							 <div  id="operater<%=i %>b" style='display:inline;' nowrap>
							
							 <a
								onclick="complexCondition('1','<bean:write name="element" property="string(body_id)" filter="true"/>','<bean:write name="element" property="string(cexpr)" filter="true"/>','<bean:write name="element" property="string(cond)" filter="true"/>');"><img
									src="/images/edit.gif" border=0 style="cursor:hand;">
							</a>
							</div>
							</logic:notEqual>
							<logic:equal name="element" property="string(cond)" value="">
							 <input type="radio" name="operater<%=i %>"  onclick="setType('operater<%=i %>b','operater<%=i %>a','operater<%=i %>c')" >
							  <bean:message key="popedom.gjtj" />
							 <div  id="operater<%=i %>b" style='display:none;' nowrap>
							
							 <a
								onclick="complexCondition('0','<bean:write name="element" property="string(body_id)" filter="true"/>','<bean:write name="element" property="string(cexpr)" filter="true"/>','<bean:write name="element" property="string(cond)" filter="true"/>');"><img
									src="/images/edit.gif" border=0 style="cursor:hand;">
							</a>
							</div>
							</logic:equal>
							 </logic:equal>
						 </logic:notEqual>
					</logic:notEqual>
					</td>
					</logic:equal>
			</logic:notEqual>
					
					<td align="center" style="border-top:0px;" class="RecordRow_left" nowrap>
					
						<a
							onclick="edit('<bean:write name="element" property="string(body_id)" filter="true"/>');"><img
								src="/images/edit.gif" border=0 style="cursor:hand;">
						</a>

					</td>
				</tr>
			</hrms:extenditerate>
		</table>
		</div>
	</td>
				</tr>
	</table>

		<table width="100%">
			<tr>
				<td align="center">
				
						<input type='button' class="mybutton" property="b_add"
							onclick='add()' value='<bean:message key="button.insert"/>' />
			
						<input type='button' class="mybutton" property="b_delete"
							onclick='checkdelete()'
							value='<bean:message key="button.delete"/>' />
						
						<logic:equal name="checkBodyObjectForm" property="bodyType" value="0">
						<input type='button' class="mybutton" property="b_clear"
							onclick='clearoption()'
							value='<bean:message key="jx.options.clearoption"/>' />
						</logic:equal>
						
					<input type="button" value="<bean:message key='kq.item.change'/>"
						onclick="toSorting();" Class="mybutton">
		 <%
		 	String temp=request.getParameter("modelflag");

		 if("capability".equals(temp)){ %>
         <hrms:tipwizardbutton flag="capability" target="il_body" formname="checkBodyObjectForm"/>  
         <%}else{ %>
         <hrms:tipwizardbutton flag="performance" target="il_body" formname="checkBodyObjectForm"/>  
         <%} %> 


				</td>
			</tr>
		</table>
	</html:form>
<script type="text/javascript">
    var div = document.getElementById("tbl-container");
    if(div){
        div.style.height= (document.body.clientHeight-100)+"px";
        div.style.width= (document.body.clientWidth-100)+"px";
    }
</script>
</body>
