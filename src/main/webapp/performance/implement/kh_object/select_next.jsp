<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant,com.hjsj.hrms.actionform.performance.implement.ImplementForm"%>
<%
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String manager=userView.getManagePrivCodeValue(); 
	String datebase=request.getParameter("datebase"); 
	String showdb = request.getParameter("showdb");
	
	ImplementForm implementForm = (ImplementForm)session.getAttribute("implementForm");
	String selectType=(String)implementForm.getSelectType();
	selectType = selectType == null?"":selectType;
	String callBackFunc = "";
	if(request.getParameter("callbackfunc")!=null){
        callBackFunc = request.getParameter("callbackfunc");
    }else if(request.getParameter("callBackFunc")!=null){
        callBackFunc = request.getParameter("callBackFunc");
    }else if(request.getParameter("callbackFunc")!=null){
        callBackFunc = request.getParameter("callbackFunc");
    }
%>

<style>

.textInterface 
{
	BACKGROUND-COLOR:transparent;
	font-size: 12px;
	height:22;
	border: 1pt solid #94B6E6;
}
body{
    margin:10 0 0 0 !important;
}
.fixedtab{
	overflow:auto; 
	height:100%;
    BORDER-BOTTOM: #94B6E6 0pt solid; 
    BORDER-LEFT: #94B6E6 1pt solid; 
    BORDER-RIGHT: #94B6E6 1pt solid; 
    BORDER-TOP: #94B6E6 1pt solid ; 
}
</style>
<meta http-equiv="X-UA-Compatible" content="IE=9; IE=8; IE=7">
<script language="JavaScript"src="../../../js/showModalDialog.js"></script>
<script language="JavaScript" src="/js/popcalendar.js"></script>
<SCRIPT Language="JavaScript">dateFormat='yyyy-mm-dd'</SCRIPT>
<SCRIPT LANGUAGE=javascript src="/js/validate.js"></SCRIPT>
<SCRIPT LANGUAGE=javascript src="/js/function.js"></SCRIPT>
<!-- 改用新的日期控件 lium
<script language="JavaScript" src="/js/meizzDate.js"></script>
 -->
<SCRIPT LANGUAGE=javascript>
	function check()
	{
		
		<% int n=0;  %>
		<logic:iterate  id="element"    name="implementForm"  property="selectedFieldList" indexId="index"> 
			<% n++; %>
			<logic:equal name="element" property="itemtype" value="N">
					var a<%=n%>=document.getElementsByName("aa<%=n%>.value")
					if(a<%=n%>[0].value!='')
					{
					 var myReg =/^(-?\d+)(\.\d+)?$/
					 if(!myReg.test(a<%=n%>[0].value)) 
					 {
						alert("<bean:write  name="element" property="itemdesc"/>请输入数字！");
						return;
					 }
					 }
			</logic:equal>
			
		</logic:iterate>

		var relation=new Array();
		var fielditemid=new Array();
		var operate=new Array();
		var values=new Array();
		var a=0;		
		if(document.implementForm.relation.length)
		{
			for(var i=0;i<document.implementForm.relation.length;i++)
			{
			
				var a_relation=document.implementForm.relation[i].value;
				var a_fielditemid=document.implementForm.itemid[i].value;
				var a_operate=document.implementForm.operate[i].value;
				var name=$("aa"+(i+1)+".value");	
				var hzname=$("aa"+(i+1)+".hzvalue");	

				//if(name.value!=''&&name.value!=' ')
				{
					relation[a]=a_relation;
					fielditemid[a]=a_fielditemid;
					operate[a]=a_operate;
					if(ltrim(rtrim(name.value))!='')
						values[a]=name.value;
					else
						values[a]=hzname.value;
					a++;
				}
			}
		}
		else
		{
				var a_relation=document.implementForm.relation.value;
				var a_fielditemid=document.implementForm.itemid.value;
				var a_operate=document.implementForm.operate.value;
				var name=$("aa1.value");	
				var hzname=$("aa1.hzvalue");	
			//	if(name.value!=''&&name.value!=' ')
				{
					relation[a]=a_relation;
					fielditemid[a]=a_fielditemid;
					operate[a]=a_operate;
					if(ltrim(rtrim(name.value))!='')
						values[a]=name.value;
					else
						values[a]=hzname.value;
					a++;
				}
		
		}	
		if(relation.length==0)
		{
			alert("请选择条件！");
			return;
		}		

		var hashvo=new ParameterSet();
		 hashvo.setValue("relation",relation);
		 hashvo.setValue("fielditemid",fielditemid);
		 hashvo.setValue("operate",operate);
		 hashvo.setValue("values",values);
		 hashvo.setValue("db_name","${param.db}");
		 hashvo.setValue("dbpre","${implementForm.dbpre}");
		 //hashvo.setValue("tableName",'${implementForm.setname}');
		 In_paramters='flag=1';
		 
		 var queryType = '';
		 var obj=$('like');
		 if(obj.checked==true)
		 	queryType=queryType+'like=1';
		 else
		 	queryType=queryType+'like=0';
		 
		 obj=$('history');
		 if(obj.checked==true)
		 	queryType=queryType+'&history=1';
		 else
		 	queryType=queryType+'&history=0';		 
		 
		 In_paramters=queryType;
		 
		 <logic:equal name="implementForm" property="selectType" value="general">
		    var expobj = document.getElementById("expression");
		    var expression = expobj.value;
		    if(expression==null||expression==""){
		    	alert("请填写因子表达式！");
				return;
		    }
		 	hashvo.setValue("expression",expression);
		 </logic:equal>
		 hashvo.setValue("selectType","${implementForm.selectType}");
		 var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfo,functionId:'9023000103'},hashvo);
	}
	
	
	
	function returnInfo(outparamters)
	{
		var sql=outparamters.getValue("sql");	
		var thevo=new Object();
		thevo.flag="true";
		thevo.sql=sql;
		thevo.dbpre=outparamters.getValue("dbpre");
        thevo.accordByDepartment='false';
		if('${implementForm.accordByDepartmentFlag}'=='1')
			thevo.accordByDepartment=document.getElementById('accordByDepartment').checked;		
		if(window.showModalDialog){
			parent.window.returnValue=thevo;
		}else{
            <% if(callBackFunc.length()>0){ %>
		        eval(parent.window.opener.<%=callBackFunc%>)(thevo);
			<%}else {%>
                if(parent.window.opener.conditionselect_ok) {
                    parent.window.opener.conditionselect_ok(thevo);
                }
			<%}%>
		}
		parent.window.close();
	}
	
	//上一步
	function pre_phase()
	{
		document.implementForm.action="/performance/implement/kh_object/condition_select.do?b_query2=link&db=${param.db}&accordByDepartmentFlag=${implementForm.accordByDepartmentFlag}&datebase=<%=datebase %>&showdb=<%=showdb %>&selecttype=<%=selectType%>&callBackFunc=<%=callBackFunc%>";
		document.implementForm.submit();
	}
	
	function symbol(editor,strexpr){
		var expr_editor=document.getElementById(editor);
		expr_editor.focus();
		var element = document.selection;
		if (element&&element!=null){
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
	
	
	</SCRIPT>
<hrms:themes />
<style>
.notop{
	BORDER-TOP: none ; 
}
.noleft{
	BORDER-LEFT: none ; 
}
.noright{
	BORDER-RIGHT: none ; 
}
</style>
<base id="mybase" target="_self">
<html:form action="/performance/implement/kh_object/select_next">
<html:hidden name="implementForm" property="dbpre"/>

		<table border="0" cellspacing="0"  width="525px" align="center" cellpadding="0" class="ListTable">
		<tr>
				<td class='TableRow'>
					<logic:equal name="implementForm" property="selectType" value="general">
						&nbsp;&nbsp;	<bean:message key="button.c.query" />
					</logic:equal>
					<logic:notEqual name="implementForm" property="selectType" value="general">
						&nbsp;&nbsp;	<bean:message key="button.h.query" />
					</logic:notEqual>
				</td>
		</tr>
		<tr>
				<td class='RecordRow'>
		
		<table border="0" cellspacing="0" width="100%" align="center"
			cellpadding="0" width="525px">

			<tr>
				<td>
					<table border="0" cellspacing="0" width="100%" class="ListTable"
						cellpadding="2" align="center">
						<tr>
							<td colspan="4">
								<br>
								<table border="0" cellspacing="0" width="97%" class="ListTable1 noright"
									cellpadding="0" align="center">
									<tr>
   									<td height="200px" class="noright">
     								<div class="fixedtab"> 
     								<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
									<tr class="fixedHeaderTr">
										<td width="16%" align="center" nowrap class="TableRow notop noleft">
											<logic:equal name="implementForm" property="selectType" value="general">
												<bean:message key="label.query.number" />
											</logic:equal>
											<logic:notEqual name="implementForm" property="selectType" value="general">
												<bean:message key="label.query.logic" />
											</logic:notEqual>	
										</td>
										<td width="29%" align="center" nowrap class="TableRow notop">
											<bean:message key="label.query.field" />
										</td>
										<td width="13%" align="center" nowrap class="TableRow notop">
											<bean:message key="label.query.relation" />
										</td>
										<td width="42%" align="center" nowrap class="TableRow notop noright">
											<bean:message key="label.query.value" />
										</td>
									</tr>
									<%
									int i = 0;
									%>
									<logic:iterate id="element" name="implementForm"
										property="selectedFieldList">

										<tr >
											<td align="center" class="RecordRow noleft" nowrap>
												<logic:equal name="implementForm" property="selectType" value="general">
													<input type='hidden' name='relation' value='*' />
													<%=i + 1%>
													<%i++; %>
												</logic:equal>
												<logic:notEqual name="implementForm" property="selectType" value="general">
													<%
															if (i++ == 0)
															{
													%>
													<input type='hidden' name='relation' value='*' />
													<%
															} else
															{
													%>
													<select name="relation" size="1">
														<option value="*" selected="selected">
															<bean:message key="kq.wizard.even" />
														</option>
														<option value="+">
															<bean:message key="kq.wizard.and" />
														</option>
													</select>
													<%
													}
													%>
												</logic:notEqual>
												&nbsp;
											</td>
											<td align="center" class="RecordRow" nowrap>
												<input type='hidden' name='itemid'
													value='<bean:write name="element" property="itemid" />§§<bean:write name="element" property="itemtype" />§§<bean:write name="element" property="itemsetid" />§§<bean:write name="element" property="table_name" />' />
												<bean:write name="element" property="itemdesc" />
											</td>
											<td align="center" class="RecordRow" nowrap>
												<select name="operate" size="1" style="width:100%">
													<option value="=" selected="selected">
														=
													</option>
													<option value="&gt;">
														&gt;
													</option>
													<option value="&gt;=">
														&gt;=
													</option>
													<option value="&lt;">
														&lt;
													</option>
													<option value="&lt;=">
														&lt;=
													</option>
													<option value="&lt;&gt;">
														&lt;&gt;
													</option>
												</select>
											</td>

											<td align="left" class="RecordRow noright" nowrap>
												<!--日期型 -->
												<logic:equal name="element" property="itemtype" value="D">
													<input type='text' name="aa<%=i%>.value" size="24" class="textInterface"
														maxlength="10" extra="editor" dropDown="dropDownDate" itemlength="10" dataType="simpledate"
														readOnly />
												</logic:equal>
												<!--字符型 -->
												<logic:equal name="element" property="itemtype" value="A">

													<logic:notEqual name="element" property="itemsetid"
														value="0">
														<input type='hidden' name="aa<%=i%>.value" />
														<input type="text" name="aa<%=i%>.hzvalue" size="24"
															value=""  class="textInterface" readonly>
													
														<logic:notEqual name="element" property="itemsetid" value="UN">
															<logic:notEqual name="element" property="itemsetid" value="UM">
																<logic:notEqual name="element" property="itemsetid" value="@K">
																	
																	<img src="/images/code.gif"	onclick='openCondCodeDialog("<bean:write name="element" property="itemsetid" />","aa<%=i%>.hzvalue");' align="absmiddle"/>
																</logic:notEqual>
															</logic:notEqual>	
														</logic:notEqual>	
														<logic:equal name="element" property="itemsetid" value="UN">
															<img src="/images/code.gif" onclick="openInputOrgCodeDialogOrg('UN','aa<%=i%>.hzvalue','',1);" align="absmiddle"/>
														</logic:equal>
														<logic:equal name="element" property="itemsetid" value="UM">
															<img src="/images/code.gif" onclick="openInputOrgCodeDialogOrg('UM','aa<%=i%>.hzvalue','',1);" align="absmiddle"/>
														</logic:equal>
														<logic:equal name="element" property="itemsetid" value="@K">
															<img src="/images/code.gif" onclick="openInputOrgCodeDialogOrg('@K','aa<%=i%>.hzvalue','',1);" align="absmiddle"/>
														</logic:equal>
														
													</logic:notEqual>
													
													<logic:equal name="element" property="itemsetid" value="0">
														<input type='text' name="aa<%=i%>.value" size="24" class="textInterface"/>
													</logic:equal>
												</logic:equal>
												<!--数据值-->
												<logic:equal name="element" property="itemtype" value="N">
													<input type='text' name="aa<%=i%>.value" size="24" class="textInterface"/>
												</logic:equal>
											</td>
										</tr>
									</logic:iterate>
									</table>
									</div>
									</td>
									</tr>
									<logic:equal name="implementForm" property="selectType" value="general">
										<tr>
											<td align="left" nowrap class="RecordRow" colspan="4">
												<span><bean:message key="label.query.expression" />
												</span>
												<br>
												<html:textarea property="expression" styleId="expression"
													rows="2" cols="82"
													onclick="this.pos=document.selection.createRange();" style="margin-bottom:5px;"/>
											</td>
										</tr>
										<tr>
											<td align="left" nowrap class="RecordRow" colspan="4"
												style="height: 35px;">
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
									</logic:equal>
									<tr>
										<td align="center" nowrap class="RecordRow" colspan="4">
											<input type="checkbox" name="like">
											&nbsp;
											<bean:message key="label.query.like" />
											&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;												
											<logic:equal name="implementForm" property="accordByDepartmentFlag" value="1">	
												<input type="checkbox" id="accordByDepartment">
												&nbsp;						
												<bean:message key="jx.plan.accordByDepartment" />
												&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
											</logic:equal>		
											<span id='history_query' style="display:none">
											<input type="checkbox" name="history">&nbsp;
											<bean:message 	key="label.query.history" /> </span>
										</td>

									</tr>
								</table>
							</td>
						</tr>
						<tr>
							<td height="15" colspan="4"></td>
						</tr>
					</table>
				</td>
			</tr>
		</table>
	</td>
			</tr>
		</table>
		<table border="0" cellspacing="0" width="525px" align="center"
			cellpadding="0">
			<tr>
				<td align="center" height="35px">

					<input type="button" value="<bean:message key="button.query.pre"/>"
						class="mybutton" onclick='pre_phase()' />
					<input type="reset" value="<bean:message key="button.clear"/>"
						class="mybutton">
					<input type="button" name="b_update"
						value="<bean:message key="button.ok"/>" onclick='check()'
						class="mybutton">
				</td>
			</tr>
		</table>
	<script LANGUAGE=javascript>
		if("${param.isHistory}"=='1')
			Element.show('history_query');
		var aa=document.getElementsByTagName("input");
		for(var i=0;i<aa.length;i++){
			if(aa[i].type=="text"){
				aa[i].className="inputtext";
			}
		}
	</script>
	
</html:form>
