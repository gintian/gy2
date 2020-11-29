<%@page import="java.util.Date"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="com.hjsj.hrms.actionform.general.impev.ImportantEvForm"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.hjsj.sys.FieldItem,
com.hrms.struts.constant.WebConstant,
com.hrms.struts.valueobject.UserView" %>
<script type="text/javascript" src="/general/impev/important.js"></script>
<script type="text/javascript" src="/fckeditor/fckeditor.js"></script>
<script language="JavaScript" src="/js/validateDate.js"></script>
<script language="JavaScript" src="/org/orgdata/orgedit.js"></script>
<%
	ImportantEvForm form = (ImportantEvForm) session
			.getAttribute("importantEvForm");
	int len = form.getFieldlist().size();
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String hcmflag="";
	if(userView != null){
	  hcmflag=userView.getBosflag();
	  userView.getHm().put("fckeditorAccessTime", new Date().getTime());
	}
	
%>
<body>
<html:form action="/general/impev/importantev"  enctype="multipart/form-data">
<%if("hl".equals(hcmflag)){ %>
<br>
	<table id="table" width="620" border="0" cellpadding="0"
		class="RecordRow" cellspacing="0" align="center">
<%}else{ %>
	<table id="table" width="700" border="0" cellpadding="0"
		class="RecordRow" cellspacing="0" align="center" style="margin-top:60px">
<%} %>

		<html:hidden name="importantEvForm" property="p0600"/>
		<tr height="15">
			<td align="left" class="TableRow" colspan="4">
				&nbsp;
				<bean:message key="general.impev.importantev" />
				&nbsp;
			</td>
		</tr>
		<tr class="trShallow">
			<td align="right" class="RecordRow" nowrap>
				&nbsp;&nbsp;
				<bean:message key="general.impev.fromdate" />
				</td><td class="RecordRow" nowrap>
				<input type="text" name="fromdate"
					value="${importantEvForm.fromdate}" extra="editor"
					style="width: 222px; font-size: 10pt; text-align: left"
					dropDown="dropDownDate"
					onchange=" if(!validate(this,'日期')) {this.focus(); this.value=''; }">
			</td>
			<td align="right" class="RecordRow" nowrap>
				&nbsp;&nbsp;
				<bean:message key="general.impev.todate" />
				</td><td class="RecordRow" nowrap>
				<input type="text" name="todate" value="${importantEvForm.todate}"
					extra="editor"
					style="width: 222px; font-size: 10pt; text-align: left"
					dropDown="dropDownDate"
					onchange=" if(!validate(this,'日期')) {this.focus(); this.value=''; }">
			</td>
		</tr>
		
		

						<%
							int i = 0, j = 0;
						%>
						<logic:iterate id="element" name="importantEvForm"
							property="fieldlist" indexId="index">
							<%
								FieldItem abean = (FieldItem) pageContext
														.getAttribute("element");
												boolean isFillable1 = abean.isFillable();
												if (i == 2) {
							%>
							<%
								if (j % 2 == 0) {
							%>
						

					<tr class="trShallow">
						<%
							} else {
						%>

					<tr class="trDeep">
						<%
							}
												i = 0;
												j++;
											}
						%>
						<logic:notEqual name="element" property="itemtype" value="M">
							<td align="right" class="RecordRow" nowrap>
								<bean:write name="element" property="itemdesc" filter="true" />
							</td>
							<td align="left" class="RecordRow" nowrap>
								<logic:equal name="element" property="codesetid" value="0">
									<logic:notEqual name="element" property="itemtype" value="D">
										<logic:equal name="element" property="itemtype" value="N">
											<logic:equal name="element" property="decimalwidth" value="0">
												<html:text maxlength="50" size="30" styleClass="textbox"
													onkeypress="event.returnValue=IsDigit2(this);"
													onblur='isNumber(this);' name="importantEvForm"
													styleId="${element.itemid}"
													property='<%="fieldlist["
														+ index + "].value"%>' />
											</logic:equal>
											<logic:notEqual name="element" property="decimalwidth"
												value="0">
												<html:text maxlength="50" size="30" styleClass="textbox"
													onkeypress="event.returnValue=IsDigit(this);"
													onblur='isNumber(this);' name="importantEvForm"
													styleId="${element.itemid}"
													property='<%="fieldlist["
														+ index + "].value"%>' />
											</logic:notEqual>
											<%
												if (isFillable1) {
											%> &nbsp;<font color='red'>*</font>&nbsp;<%
 	}
 %>
										</logic:equal>
										<logic:notEqual name="element" property="itemtype" value="N">
											<html:text maxlength="50" size="30" styleClass="textbox"
												name="importantEvForm" styleId="${element.itemid}"
												property='<%="fieldlist[" + index
													+ "].value"%>' />
											<%
												if (isFillable1) {
											%> &nbsp;<font color='red'>*</font>&nbsp;<%
 	}
 %>
										</logic:notEqual>
									</logic:notEqual>
									<logic:equal name="element" property="itemtype" value="D">
										<input type="text" name='<%="fieldlist[" + index
												+ "].value"%>'
											maxlength="50" size="29" id="${element.itemid}"
											extra="editor" class="textbox"
											style="font-size: 10pt; text-align: left"
											dropDown="dropDownDate" value="${element.value}"
											onchange=" if(!validate(this,'${element.itemdesc}')) {this.focus(); this.value=''; }">
										<%
											if (isFillable1) {
										%> &nbsp;<font color='red'>*</font>&nbsp;<%
 	}
 %>
									</logic:equal>
								</logic:equal>

								<logic:notEqual name="element" property="codesetid" value="0">
									<logic:equal name="element" property="itemid" value="b0110">
										<html:hidden name="importantEvForm"
											property='<%="fieldlist[" + index
												+ "].value"%>'
											onchange="fieldcode2(this)" />
										<html:text maxlength="50" size="30" styleClass="textbox"
											name="importantEvForm"
											property='<%="fieldlist[" + index
												+ "].viewvalue"%>'
											onchange="fieldcode(this,2)" />
										<img src="/images/code.gif"
											onclick='javascript:openInputCodeDialogOrgInputPos("${element.codesetid}","<%="fieldlist[" + index
												+ "].viewvalue"%>","","1");' />&nbsp;
  <%
  	if (isFillable1) {
  %> &nbsp;<font color='red'>*</font>&nbsp;<%
 	}
 %>
									</logic:equal>
										<logic:notEqual name="element" property="itemid" value="b0110">
										<html:hidden name="importantEvForm"
											property='<%="fieldlist[" + index
												+ "].value"%>' />
										<html:text maxlength="50" size="30" styleClass="textbox"
											name="importantEvForm"
											property='<%="fieldlist[" + index
												+ "].viewvalue"%>'
											onchange="fieldcode(this,2)" />
										<img src="/images/code.gif"
											onclick='javascript:openInputCodeDialog("${element.codesetid}","<%="fieldlist[" + index
												+ "].viewvalue"%>","","1");' />&nbsp;
			   <%
			   	if (isFillable1) {
			   %> &nbsp;<font color='red'>*</font>&nbsp;<%
 	}
 %>
									</logic:notEqual>
									
								</logic:notEqual>
								<%
									i++;
								%>

							</td>
							<%
								if (index.intValue() < len - 1) {
							%>
							<logic:equal name="importantEvForm"
								property='<%="fieldlist["
												+ Integer.toString(index
														.intValue() + 1)
												+ "].itemtype"%>'
								value="M">
								<%
									if (i < 2) {
								%>
								<td align="left" class="RecordRow" nowrap></td>
								<td align="left" class="RecordRow" nowrap></td>
								<%
									i++;
																}
								%>

							</logic:equal>
							<%
								} else if (index.intValue() == len - 1) {
							%>
							<%
								if (i < 2) {
							%>
							<td align="left" class="RecordRow" nowrap></td>
							<td align="left" class="RecordRow" nowrap></td>
							<%
								i++;
														}
							%>
							<%
								}
							%>
						</logic:notEqual>
						<logic:equal name="element" property="itemtype" value="M">
							<td align="right" class="RecordRow" nowrap valign="top">
								<bean:write name="element" property="itemdesc" filter="true" />
							</td>
							<td align="left" class="RecordRow" nowrap colspan="4">
								<html:textarea name="importantEvForm"
									property='<%="fieldlist[" + index
											+ "].value"%>' cols="90"
									rows="6" styleClass="textboxMul"></html:textarea>
								<%
									if (isFillable1) {
								%>
								&nbsp;
								<font color='red'>*</font>&nbsp;<%
									}
								%>
							</td>
							<%
								i = 2;
							%>
						</logic:equal>
						</logic:iterate>

					
					
		<tr class="trDeep">
			<td colspan="4" class="RecordRow">
				&nbsp;&nbsp;
				<bean:message key="general.impev.content" />
				:
			</td>
		</tr>
		<tr class="trShallow">
			<td colspan="4">
			<html:textarea name="importantEvForm" property="content" cols="80" rows="20" style="display:none;" />
				<div id="tableEdit">
					<script type="text/javascript">
              //<!--
              var oFCKeditor = new FCKeditor( 'FCKeditor1' ) ;
              oFCKeditor.BasePath	= '/fckeditor/';
              oFCKeditor.Height	= 350 ;
              oFCKeditor.Width=650;
			  oFCKeditor.ToolbarSet='Apply';
			  oFCKeditor.Value = $F("content");
			  oFCKeditor.Create();
              
              //-->
            </script>
				</div>
			</td>
		</tr>
	</table>
	<table align="center">
		<tr>
			<td>
				<INPUT type="button" value="<bean:message key="options.save" />"
					Class="mybutton" onclick="saveImpev();">

				<INPUT type="button"
					value="<bean:message key="lable.enterfor.submit" />"
					Class="mybutton" onclick="saveandsubmit();">

				<INPUT type="button" value="<bean:message key="button.leave" />"
					Class="mybutton" onclick="back();">
			</td>
		</tr>
	</table>
</html:form>

</body>