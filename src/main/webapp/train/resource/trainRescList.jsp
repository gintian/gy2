<%@page import="org.apache.commons.lang.StringUtils"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="com.hrms.frame.codec.SafeCode"%>
<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@page import="com.hjsj.hrms.businessobject.train.resource.TrainResourceBo"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
	    UserView userView = (UserView) session.getAttribute(WebConstant.userView);
	    String bosflag = "";
	    if (userView != null)
	    {
		bosflag = userView.getBosflag();
		bosflag = bosflag != null ? bosflag : "";
	    }
	    
	    String type = request.getParameter("type");
%>
<script language="javascript" src="/train/resource/trainResc.js"></script>
<script type="text/javascript" src="../../js/hjsjUrlEncode.js"></script>
<script >
 var url = window.location.href;
 if(url.indexOf("b_save")!=-1)
	 window.location.href="/train/resource/trainRescList.do?b_query=save&type=${trainResourceForm.type}&returnvalue=";
</script>
<style>
body{text-align: center;}
.myfixedDiv
{  
	overflow:auto; 
	height:expression(document.body.clientHeight-150);
	width:expression(document.body.clientWidth-10); 
	BORDER-BOTTOM: #99BBE8 1pt solid; 
	BORDER-LEFT: #99BBE8 1pt solid; 
	BORDER-RIGHT: #99BBE8 1pt solid; 
	BORDER-TOP: #99BBE8 1pt solid;
}
</style>
<html:form action="/train/resource/trainRescList">
	<bean:define id="priFld" name="trainResourceForm"
		property="primaryField" />
	<html:hidden name="trainResourceForm" property="nameFld"
		styleId="nameFld" />
	<html:hidden name="trainResourceForm" property="resType"
		styleId="type" />
	<html:hidden name="trainResourceForm" property="recTable"
		styleId="recTable" />
	<html:hidden name="trainResourceForm" property="strParam"
		styleId="strParam" />
	<%
	int i = 0;
	%>
	<table width="100%" border="0" cellspacing="0" align="center"
		cellpadding="0" class="ListTable">
		<tr>
			<td>
				<div class="myfixedDiv" style="padding: 0;">
					<table width="100%" border="0" cellspacing="0" align="center"
						cellpadding="0" style="border-collapse: collapse;">
						<thead>
							<tr class="fixedHeaderTr">
								<td align="center" style="border-left: none;border-top: none;" class="TableRow">
									<!--  <input type="checkbox" name="selbox" onclick="batch_select(this,'pagination.select');" title='<bean:message key="label.query.selectall"/>' />&nbsp;-->
									<input type="checkbox" name="selbox" onclick="batch_select_all(this);" title='<bean:message key="label.query.selectall"/>'>&nbsp;
								</td>

								<logic:iterate id="element" name="trainResourceForm"
									property="fields">
									<td align="center" class="TableRow" style="border-top: none;" nowrap>
										&nbsp;<bean:write name="element" property="itemName" filter="true" />&nbsp;
									</td>
								</logic:iterate>
							    <logic:equal name="trainResourceForm" property="type" value="1">
								  <hrms:priv func_id="3230102" module_id="">
								  <td align="center" class="TableRow" style="border-top: none;" nowrap>
									&nbsp;<bean:message key="system.infor.oper" />&nbsp;
								  </td>
								  </hrms:priv>
								</logic:equal>
								<logic:equal name="trainResourceForm" property="type" value="2">
								  <hrms:priv func_id="3230506" module_id="">
								  <td align="center" class="TableRow" style="border-top: none;border-top: none;" nowrap>
									&nbsp;<bean:message key="conlumn.infopick.educate.teachlessons"/>&nbsp;
								  </td>
								  </hrms:priv>
								  <hrms:priv func_id="3230202" module_id="">
								  <td align="center" class="TableRow" style="border-top: none;" nowrap>
									&nbsp;<bean:message key="system.infor.oper" />&nbsp;
								  </td>
								  </hrms:priv>
								</logic:equal>
								<logic:equal name="trainResourceForm" property="type" value="3">
								  <hrms:priv func_id="3230302" module_id="">
								  <td align="center" class="TableRow" style="border-top: none;" nowrap>
									&nbsp;<bean:message key="system.infor.oper" />&nbsp;
								  </td>
								  </hrms:priv>
								</logic:equal>
								<logic:equal name="trainResourceForm" property="type" value="4">
								  <hrms:priv func_id="3230402" module_id="">
								  <td align="center" class="TableRow" style="border-top: none;" nowrap>
									&nbsp;<bean:message key="system.infor.oper" />&nbsp;
								  </td>
								  </hrms:priv>
								  <hrms:priv func_id="3230404,3230405" module_id="">
								  <td align="center" class="TableRow" style="border-top: none;" nowrap>
									&nbsp;办理&nbsp;
								  </td>
								  </hrms:priv>
								</logic:equal>
								<logic:equal name="trainResourceForm" property="type" value="5">
								  <hrms:priv func_id="3230506" module_id="">
								  <td align="center" class="TableRow" style="border-top: none;" nowrap>
									&nbsp;<bean:message key="system.infor.oper" />&nbsp;
								  </td>
								  </hrms:priv>
								</logic:equal>
<%--								<td align="center" class="TableRow" style="border-top: none;border-right: none;" nowrap>
									&nbsp;评估&nbsp;
								</td>--%>
								<logic:equal name="trainResourceForm" property="recTable" value="r07">
								<td align="center" width="60" class="TableRow" style="border-top: none;border-right: none;" nowrap>
									&nbsp;附件&nbsp;
								</td>
								</logic:equal>
								<logic:equal name="trainResourceForm" property="recTable" value="r04">
								<td align="center" width="60" class="TableRow" style="border-top: none;border-right: none;" nowrap>
									&nbsp;附件&nbsp;
								</td>
								</logic:equal>
							</tr>
						</thead>
						<hrms:paginationdb id="element2" name="trainResourceForm"
							sql_str="trainResourceForm.strsql" table=""
							where_str="trainResourceForm.strwhere"
							columns="trainResourceForm.columns" page_id="pagination"
							pagerows="${trainResourceForm.pagerows}"
							order_by="order by ${trainResourceForm.primaryField}">

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
								<bean:define id="prifield" name="element2" property="${priFld}" />
								<%String id = SafeCode.encode(PubFunc.encrypt(prifield.toString())); %>
								<td align="center" class="RecordRow" style="border-left: none;" nowrap>&nbsp;
									<!--<hrms:checkmultibox name="trainResourceForm"
										property="pagination.select" value="true" indexes="indexes" />-->
									  <input type="checkbox" value="true" name="<%=id %>"/>
									<Input type='hidden' value="<%=id %>" />
									<!--<bean:write name="trainResourceForm" property="pagination.select"/>-->
								&nbsp;</td>


								<logic:iterate id="element1" name="trainResourceForm"
									property="fields">
									<bean:define id="nid" name="element1" property="itemid" />

									<logic:equal name="element1" property="itemType" value="M">
										<bean:define id="beizhu" name="element2" property="${nid}"/>
										<%
											String remark = TrainResourceBo.substr(beizhu.toString());
											if("1".equals(type) && userView.hasTheFunction("3230102")) 
												remark = StringUtils.isEmpty(remark) ? "无" : remark;
											else if("2".equals(type) && userView.hasTheFunction("3230202")) 
												remark = StringUtils.isEmpty(remark) ? "无" : remark;
											else if("3".equals(type) && userView.hasTheFunction("3230302")) 
												remark = StringUtils.isEmpty(remark) ? "无" : remark;
											else if("4".equals(type) && userView.hasTheFunction("3230402")) 
												remark = StringUtils.isEmpty(remark) ? "无" : remark;
											else if("5".equals(type) && userView.hasTheFunction("3230506")) 
												remark = StringUtils.isEmpty(remark) ? "无" : remark;
										%>
										<td align="left" class="RecordRow" nowrap>&nbsp;
											<a href='javascript:editMemoFild("<%=id %>","${nid}");'>
												<%=remark %>
											</a>
										&nbsp;</td>
									</logic:equal>
									<logic:notEqual name="element1" property="itemType" value="M">
										<logic:equal name="element1" property="itemType" value="N">
											<td align="right" class="RecordRow" nowrap>&nbsp;
										</logic:equal>
										<logic:equal name="element1" property="itemType" value="D">
											<td align="right" class="RecordRow" nowrap>&nbsp;
										</logic:equal>
										<logic:equal name="element1" property="itemType" value="A">
											<td align="left" class="RecordRow" nowrap>&nbsp;
										</logic:equal>

										<logic:equal name="element1" property="itemType" value="A">
											<logic:equal name="element1" property="codesetId" value="0">
												<logic:notEqual name="element1" property="itemid"
													value="${trainResourceForm.nameFld}">
													<bean:write name="element2" property="${nid}"
														filter="false" />
												</logic:notEqual>
												<logic:equal name="element1" property="itemid"
													value="${trainResourceForm.nameFld}"> <!-- browse改为edit -->
													<a href='javascript:edit("<%=id %>");'> 
													<bean:write name="element2" property="${nid}" filter="false" />
													</a>
												</logic:equal>
											</logic:equal>
											<logic:notEqual name="element1" property="codesetId"
												value="0">
												<logic:notEqual name="element1" property="codesetId"
													value="UN">
													<bean:define id="codesetId" name="element1"
														property="codesetId" />
													<hrms:codetoname codeid="${codesetId}" name="element2"
														codevalue="${nid}" codeitem="codeitem" scope="page" />
													<bean:write name="codeitem" property="codename" />
												</logic:notEqual>
												<logic:equal name="element1" property="codesetId" value="UN">
													<logic:notEqual name="element2" property="${nid}"
														value="HJSJ">
														<hrms:codetoname codeid="UN" name="element2"
															codevalue="${nid}" codeitem="codeitem" scope="page" />
														<bean:write name="codeitem" property="codename" />
													</logic:notEqual>
													<logic:equal name="element2" property="${nid}" value="HJSJ">
														<bean:message key="jx.khplan.hjsj" />
													</logic:equal>
												</logic:equal>
											</logic:notEqual>
										</logic:equal>
										<logic:notEqual name="element1" property="itemType" value="A">
											<bean:write name="element2" property="${nid}" filter="false" />
										</logic:notEqual>
										&nbsp;<script>document.write("</td>");</script>
									</logic:notEqual>
								</logic:iterate>
								<logic:equal name="trainResourceForm" property="type" value="1">
								  <hrms:priv func_id="3230102" module_id="">
								  <td align="center" class="RecordRow" nowrap>
									<img src="/images/edit.gif" BORDER="0" style="cursor:hand;"
										onclick='edit("<%=id %>")'>
								  </td>
								  </hrms:priv>
								</logic:equal>
								<logic:equal name="trainResourceForm" property="type" value="2">
								<hrms:priv func_id="3230506" module_id="">
								  <td align="center" class="RecordRow" nowrap>
									<img src="/images/view.gif" BORDER="0" style="cursor:hand;"
										onclick='seachteach("<%=id %>")'>
								  </td>
								  </hrms:priv>
								  <hrms:priv func_id="3230202" module_id="">
								  <td align="center" class="RecordRow" nowrap>
									<img src="/images/edit.gif" BORDER="0" style="cursor:hand;"
										onclick='edit("<%=id %>")'>
								  </td>
								  </hrms:priv>
								</logic:equal>
								<logic:equal name="trainResourceForm" property="type" value="3">
								  <hrms:priv func_id="3230302" module_id="">
								  <td align="center" class="RecordRow" nowrap>
									<img src="/images/edit.gif" BORDER="0" style="cursor:hand;"
										onclick='edit("<%=id %>")'>
								  </td>
								  </hrms:priv>
								</logic:equal>
								<logic:equal name="trainResourceForm" property="type" value="4">
								  <hrms:priv func_id="3230402" module_id="">
								  <td align="center" class="RecordRow" nowrap>
									<img src="/images/edit.gif" BORDER="0" style="cursor:hand;" 
										onclick='edit("<%=id %>")'>
								  </td>
								  </hrms:priv>
								  <hrms:priv func_id="3230404,3230405" module_id="">
								  <td align="center" class="RecordRow" nowrap>
								  <bean:define id="r1101" name="element2" property="r1101" />
								  <%String r1101id = SafeCode.encode(PubFunc.encrypt(r1101.toString())); %>
									<img src="/images/img_m.gif" BORDER="0" style="cursor:hand;" onclick="facility('<%=r1101id %>','<bean:write name="element2" property="r1102" />','<bean:write name="element2" property="r1107" />');" alt="办理">
								  </td>
								  </hrms:priv>
								</logic:equal>
								<logic:equal name="trainResourceForm" property="type" value="5">
								  <hrms:priv func_id="3230506" module_id="">
								  <td align="center" class="RecordRow" nowrap>
									<img src="/images/edit.gif" BORDER="0" style="cursor:hand;"
										onclick='edit("<%=id %>")'>
								  </td>
								  </hrms:priv>
								</logic:equal>
<%--								<td align="center" class="RecordRow" style="border-right: none;" nowrap>
									<img src="/images/view.gif" BORDER="0" style="cursor:hand;" 
										onclick="javascript:showAssess('<bean:write name="trainResourceForm" property="recTable"/>','<%=id %>');" alt="评估结果"/>
								</td>--%>
								<logic:equal name="trainResourceForm" property="recTable" value="r07">
								<bean:define id="fileflag" name="element2" property="fileflag" />
								<td align="center" class="RecordRow" style="border-right: none;"  nowrap>
									<span id="<%=id %>_1" style="cursor:hand;color:#0033FF" onclick="uploadFile('<%=id %>','0');">
									<bean:message key="conlumn.infopick.educate.uploadfile"/><%=fileflag.equals("1")?"<img src='/images/amail_1.gif' border=0>":""%>
									</span>
								</td>
								</logic:equal>
								<logic:equal name="trainResourceForm" property="recTable" value="r04">
									<bean:define id="fileflag" name="element2" property="fileflag" />
								<td align="center" class="RecordRow" style="border-right: none;"  nowrap>
									<span id="<%=id %>_1" style="cursor:hand;color:#0033FF" onclick="uploadFile('<%=id %>','1');">
										<bean:message key="conlumn.infopick.educate.uploadfile"/><%=fileflag.equals("1")?"<img src='/images/amail_1.gif' border=0>":""%>
									</span>
								</td>
								</logic:equal>
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
						<td valign="middle" class="tdFontcolor">
							<hrms:paginationtag name="trainResourceForm"
								pagerows="${trainResourceForm.pagerows}" property="pagination"
								scope="page" refresh="true"></hrms:paginationtag>
						</td>
						<td align="right" nowrap class="tdFontcolor">
							<p align="right">
								<hrms:paginationdblink name="trainResourceForm"
									property="pagination" nameId="trainResourceForm" scope="page">
								</hrms:paginationdblink>
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
	<table width="100%" cellpadding="0" cellspacing="0">
		<tr>
			<td align="left" style="padding-top: 5px;">
			<logic:equal name="trainResourceForm" property="type" value="1">
			  <hrms:priv func_id="3230101" module_id="">	
				<input type='button' class="mybutton" property="b_add"
					onclick="add()" value='<bean:message key="button.insert"/>' />
			  </hrms:priv>
			  <hrms:priv func_id="3230103" module_id="">
				<input type='button' class="mybutton" property="b_delete"
					onclick='del()' value='<bean:message key="button.delete"/>' />
			 </hrms:priv>
			 <hrms:priv func_id="3230104" module_id="">
			 	<input type='button' class="mybutton" property="b_query"
					onclick='search("1")' value='<bean:message key="infor.menu.query"/>' />
			</hrms:priv>
			<hrms:priv func_id="3230105" module_id="">
				<input type='button' class="mybutton" property="b_query"
					onclick='exportExcel()'
					value='<bean:message key="goabroad.collect.educe.excel"/>' />
			</hrms:priv>
		   </logic:equal>
		   <logic:equal name="trainResourceForm" property="type" value="2">
			  <hrms:priv func_id="3230201" module_id="">	
				<input type='button' class="mybutton" property="b_add"
					onclick="add()" value='<bean:message key="button.insert"/>' />
			  </hrms:priv>
			  <hrms:priv func_id="3230203" module_id="">
				<input type='button' class="mybutton" property="b_delete"
					onclick='del()' value='<bean:message key="button.delete"/>' />
			 </hrms:priv>
			 <hrms:priv func_id="3230204" module_id="">
			 	<input type='button' class="mybutton" property="b_query"
					onclick='search("1")' value='<bean:message key="infor.menu.query"/>' />
			 </hrms:priv>
			 <hrms:priv func_id="3230204" module_id="">
				<input type='button' class="mybutton" property="b_query"
					onclick='exportExcel()'
					value='<bean:message key="goabroad.collect.educe.excel"/>' />
			 </hrms:priv>
		   </logic:equal>
		   <logic:equal name="trainResourceForm" property="type" value="3">
			  <hrms:priv func_id="3230301" module_id="">	
				<input type='button' class="mybutton" property="b_add"
					onclick="add()" value='<bean:message key="button.insert"/>' />
			  </hrms:priv>
			  <hrms:priv func_id="3230303" module_id="">
				<input type='button' class="mybutton" property="b_delete"
					onclick='del()' value='<bean:message key="button.delete"/>' />
			 </hrms:priv>
			 <hrms:priv func_id="3230305" module_id="">
			 	<input type='button' class="mybutton" property="b_query"
					onclick='search("1")' value='<bean:message key="infor.menu.query"/>' />
			 </hrms:priv>
			 <hrms:priv func_id="3230306" module_id="">
				<input type='button' class="mybutton" property="b_query"
					onclick='exportExcel()'
					value='<bean:message key="goabroad.collect.educe.excel"/>' />
		 	 </hrms:priv>
		   </logic:equal>
		   <logic:equal name="trainResourceForm" property="type" value="4">
			  <hrms:priv func_id="3230401" module_id="">	
				<input type='button' class="mybutton" property="b_add"
					onclick="add()" value='<bean:message key="button.insert"/>' />
			  </hrms:priv>
			  <hrms:priv func_id="3230403" module_id="">
				<input type='button' class="mybutton" property="b_delete"
					onclick='del()' value='<bean:message key="button.delete"/>' />
			 </hrms:priv>
			 <hrms:priv func_id="3230406" module_id="">
			 	<input type='button' class="mybutton" property="b_query"
					onclick='search("1")' value='<bean:message key="infor.menu.query"/>' />
			 </hrms:priv>
			 <hrms:priv func_id="3230407" module_id="">
				<input type='button' class="mybutton" property="b_query"
					onclick='exportExcel()'
					value='<bean:message key="goabroad.collect.educe.excel"/>' />
			 </hrms:priv>
		   </logic:equal>
		   <logic:equal name="trainResourceForm" property="type" value="5">
			  <hrms:priv func_id="3230505" module_id="">	
				<input type='button' class="mybutton" property="b_add"
					onclick="add()" value='<bean:message key="button.insert"/>' />
			  </hrms:priv>
			  <hrms:priv func_id="3230507" module_id="">
				<input type='button' class="mybutton" property="b_delete"
					onclick='del()' value='<bean:message key="button.delete"/>' />
			 </hrms:priv>
			 <hrms:priv func_id="3230510" module_id="">
			 	<input type='button' class="mybutton" property="b_query"
					onclick='search("1")' value='<bean:message key="infor.menu.query"/>' />
			 </hrms:priv>
			 <hrms:priv func_id="3230511" module_id="">
			 	<input type='button' class="mybutton" property="b_query"
					onclick='exportExcel()'
			 		value='<bean:message key="goabroad.collect.educe.excel"/>' />
		   	 </hrms:priv>
		   </logic:equal>
			<logic:equal name="trainResourceForm" property="type" value="3">
				<hrms:priv func_id="3230304" module_id="">	
					<input type="button" class="mybutton" name="register" onclick="trainroom();" value="使用情况"/>
				</hrms:priv>
			</logic:equal>
				<logic:equal value="dxt" name="trainResourceForm" property="returnvalue">
				<%
					    if (bosflag.equals("hl")||bosflag.equals("hcm"))
					    {
				%>
				<logic:notEqual name="trainResourceForm" property="recTable" value="r07">
				<input type='button' class="mybutton" property="returnButton"
					onclick="returnFirstPage();"
					value='<bean:message key='reportcheck.return'/>' />
				</logic:notEqual>
				<logic:equal name="trainResourceForm" property="recTable" value="r07">
				<input type='button' class="mybutton" property="returnButton"
					onclick="returnFirst();"
					value='<bean:message key='reportcheck.return'/>' />
				</logic:equal>
				<%
				}
				%>
				</logic:equal>
			</td>
		</tr>
	</table>
</html:form>