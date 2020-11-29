<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hjsj.hrms.valueobject.common.FieldItemView"%>
<%
	UserView userView = (UserView) session.getAttribute(WebConstant.userView);
	String orgparentcode ="";
	if (userView != null) {
		orgparentcode= userView.getManagePrivCodeValue();
	}
 %>
<script language="JavaScript" src="/js/validateDate.js"></script>
<script language="JavaScript" src="./orgedit.js"></script>
<style>
<!--
.fixedDiv4 
{ 
	overflow:auto; 
	*height:expression(document.body.clientHeight-80);
	*width:expression(document.body.clientWidth-10); 
	/*BORDER-BOTTOM: #94B6E6 1pt solid; 
    BORDER-LEFT: #94B6E6 1pt solid; 
    BORDER-RIGHT: #94B6E6 1pt solid; 
    BORDER-TOP: #94B6E6 1pt solid ; */
}
.textColorWrite{width: 100px;}
-->
</style>
<script language="JavaScript">
function save0(dbname)
{
	<% int m=0; %>
	<logic:iterate  id="element1"    name="orgDataForm"  property="fieldslist" indexId="index"> 
	<%
		FieldItemView abean1=(FieldItemView)pageContext.getAttribute("element1");
	    boolean isFillable=abean1.isFillable();	
	%>		
		var aa<%=m%>=document.getElementsByName("fieldslist[<%=m%>].value")
		if(<%=isFillable%>)
		{
			if(aa<%=m%>[0].value=="")
			{
				alert("<bean:write  name="element1" property="itemdesc"/>"+THIS_IS_MUST_FILL+"！");
				return;						
			}
		}		
		<% m++; %>
	</logic:iterate>
	save();
}
</script>
<html:form action="/org/orgdata/orgedit">
<input type='button' value='&nbsp;<bean:message key='button.save' />&nbsp;'	class="mybutton" onclick='save0();'>
	<input type='button' value='&nbsp;<bean:message key='button.close' />&nbsp;'	class="mybutton" onclick='closeWin();'>
	<%
	int i = 1, j = 0;  
	%>
	<div class="fixedDiv4 RecordRow">
	<table width="100%" align="center"  border="0" cellpadding="0" cellspacing="0" style="border-collapse: collapse;margin:3px 0px;">
		<tr class="trShallow1">
			<logic:iterate id="element" name="orgDataForm" property="fieldslist" indexId="index">	
										<%
								FieldItemView abean = (FieldItemView) pageContext
														.getAttribute("element");
												boolean isFillable1 = abean.isFillable();
											
							%>			
				<td align="right" class="RecordRow" nowrap>
					<bean:write name="element" property="itemdesc" filter="true" />
				</td>
				<td align="left" class="RecordRow" nowrap>
				
					<logic:equal name="element" property="itemtype" value="A">
						<logic:equal name="element" property="codesetid" value="0">
							<logic:equal name="element" property="readonly" value="true">
									<html:text maxlength="${element.itemlength}" size="17"
								styleClass="textColorWrite" name="orgDataForm"
								styleId="${element.itemid}"
								property='<%="fieldslist[" + index + "].value"%>'  readonly="true" />
							</logic:equal>
						<logic:notEqual name="element" property="readonly" value="true">
							<html:text maxlength="${element.itemlength}" size="17"
								styleClass="textColorWrite" name="orgDataForm"
								styleId="${element.itemid}"
								property='<%="fieldslist[" + index + "].value"%>' />
						</logic:notEqual>
						</logic:equal>
						<logic:notEqual name="element" property="codesetid" value="0">
							<logic:equal name="element" property="itemid" value="b0110">
								<html:hidden name="orgDataForm"
									property='<%="fieldslist[" + index + "].value"%>'
									styleId='b0110_value'/>
								<logic:equal name="element" property="readonly" value="true">
									<html:text maxlength="${element.itemlength}" size="17"
										styleClass="textColorWrite" styleId="b0110" name="orgDataForm"
										property='<%="fieldslist[" + index + "].viewvalue"%>'
										onchange="fieldcode(this,2)" readonly="true"/>									
								</logic:equal>									
								<logic:notEqual name="element" property="readonly" value="true">
									<html:text maxlength="${element.itemlength}" size="17"
										styleClass="textColorWrite" styleId="b0110" name="orgDataForm"
										property='<%="fieldslist[" + index + "].viewvalue"%>'
										onchange="fieldcode(this,2)" />
									<img src="/images/code.gif" id='img${element.itemid}'
										onclick='openInputCodeDialogOrgInputPos("${orgDataForm.orgType}","<%="fieldslist[" + index + "].viewvalue"%>","<%=orgparentcode%>","1");'
										align="absmiddle" />&nbsp;	
								</logic:notEqual>
						</logic:equal>
							<logic:equal name="element" property="itemid" value="e0122">								
									<html:hidden name="orgDataForm"
										property='<%="fieldslist[" + index + "].value"%>'
										styleId='e0122_value' onchange="changepos('UM',this)" />
									<logic:equal name="element" property="readonly" value="true">
										<html:text maxlength="${element.itemlength}" size="17"
											styleClass="textColorWrite" styleId="e0122" name="orgDataForm"
											property='<%="fieldslist[" + index + "].viewvalue"%>'
											onchange="fieldcode(this,2)" readonly="true"/>
									</logic:equal>
									<logic:notEqual name="element" property="readonly" value="true">
										<html:text maxlength="${element.itemlength}" size="17"
											styleClass="textColorWrite" styleId="e0122" name="orgDataForm"
											property='<%="fieldslist[" + index + "].viewvalue"%>'
											onchange="fieldcode(this,2)" />			
										<img src="/images/code.gif" id='img${element.itemid}'
											onclick='openInputCodeDialogOrgInputPos("${element.codesetid}","<%="fieldslist[" + index + "].viewvalue"%>","<%=orgparentcode%>","2");'
											align="absmiddle" />&nbsp;	
									</logic:notEqual>
						</logic:equal>
						<logic:equal name="element" property="itemid" value="e01a1">
								<html:hidden name="orgDataForm"
									property='<%="fieldslist[" + index + "].value"%>'
									styleId='e01a1_value'  />
									<logic:equal name="element" property="readonly" value="true">
										<html:text maxlength="${element.itemlength}" size="17"
											styleClass="textColorWrite" styleId="e01a1" name="orgDataForm"
											property='<%="fieldslist[" + index + "].viewvalue"%>'
											onchange="fieldcode(this,2)" readonly="true"/>
									</logic:equal>
										<logic:notEqual name="element" property="readonly" value="true">									
								<html:text maxlength="${element.itemlength}" size="17"
									styleClass="textColorWrite" styleId="e01a1" name="orgDataForm"
									property='<%="fieldslist[" + index + "].viewvalue"%>'
									onchange="fieldcode(this,2)" />
								<img src="/images/code.gif" id='img${element.itemid}'
									onclick='openInputCodeDialogOrgInputPos("${element.codesetid}","<%="fieldslist[" + index + "].viewvalue"%>","<%=orgparentcode%>","2");'
									align="absmiddle" />&nbsp;	
										</logic:notEqual>
						</logic:equal>
							<logic:notEqual name="element" property="itemid" value="b0110">
								<logic:notEqual name="element" property="itemid" value="e0122">
									<logic:notEqual name="element" property="itemid" value="e01a1">
									<html:hidden name="orgDataForm"
										property='<%="fieldslist[" + index + "].value"%>'
										styleId="${element.itemid}_value" />
									<logic:equal name="element" property="readonly" value="true">	
										<html:text maxlength="${element.itemlength}" size="17"
										styleClass="textColorWrite" name="orgDataForm"
										property='<%="fieldslist[" + index + "].viewvalue"%>'
										onchange="fieldcode(this,2)" styleId="${element.itemid}" readonly="true"/>
									</logic:equal>
									<logic:notEqual name="element" property="readonly" value="true">	
									<html:text maxlength="${element.itemlength}" size="17"
										styleClass="textColorWrite" name="orgDataForm"
										property='<%="fieldslist[" + index + "].viewvalue"%>'
										onchange="fieldcode(this,2)" styleId="${element.itemid}" />
									<img id='img${element.itemid}' src="/images/code.gif"
										onclick='javascript:openInputCodeDialog("${element.codesetid}","<%="fieldslist[" + index + "].viewvalue"%>");'
										align="absmiddle" />&nbsp;
									</logic:notEqual>									
								</logic:notEqual>
			 				</logic:notEqual>
							</logic:notEqual>
						</logic:notEqual>
					</logic:equal>
					<logic:equal name="element" property="itemtype" value="N">
						<logic:equal name="element" property="decimalwidth" value="0">
							<logic:equal name="element" property="readonly" value="true">
								<html:text maxlength="${element.itemlength}" size="17"
								styleClass="textColorWrite"
								onkeypress="event.returnValue=IsDigit2(this);"
								onblur='isNumber(this);' name="orgDataForm"
								styleId="${element.itemid}"
								property='<%="fieldslist[" + index + "].value"%>' readonly="true" />
							</logic:equal>
							<logic:notEqual name="element" property="readonly" value="true">						
								<html:text maxlength="${element.itemlength}" size="17"
									styleClass="textColorWrite"
									onkeypress="event.returnValue=IsDigit2(this);"
									onblur='isNumber(this);' name="orgDataForm"
									styleId="${element.itemid}"
									property='<%="fieldslist[" + index + "].value"%>' />
							</logic:notEqual>
						</logic:equal>
						<logic:notEqual name="element" property="decimalwidth" value="0">
							<logic:equal name="element" property="readonly" value="true">
								<html:text maxlength="${element.itemlength}" size="17"
								styleClass="textColorWrite"
								onkeypress="event.returnValue=IsDigit(this);"
								onblur='isNumber(this);' name="orgDataForm"
								styleId="${element.itemid}"
								property='<%="fieldslist[" + index + "].value"%>' readonly="true"/>
							</logic:equal>
							<logic:notEqual name="element" property="readonly" value="true">		
								<html:text maxlength="${element.itemlength}" size="17"
								styleClass="textColorWrite"
								onkeypress="event.returnValue=IsDigit(this);"
								onblur='isNumber(this);' name="orgDataForm"
								styleId="${element.itemid}"
								property='<%="fieldslist[" + index + "].value"%>' />
							</logic:notEqual>
						</logic:notEqual>						
					</logic:equal>
					<logic:equal name="element" property="itemtype" value="D">
						<input type="text" name='<%="fieldslist[" + index + "].value"%>' maxlength="${element.itemlength}" size="17"  id="${element.itemid}" extra="editor"  class="m_input textColorWrite"  style="font-size:10pt;text-align:left"
							dropDown="dropDownDate" value="${element.value}"  onchange=" if(!validate(this,'${element.itemdesc}')) { this.value='';}"
							<logic:equal name="element" property="readonly" value="true">	
								readOnly="true" 
							</logic:equal>
								>
					</logic:equal>
					<logic:equal name="element" property="itemtype" value="M">
						<logic:equal name="element" property="readonly" value="true">					
							<html:textarea name="orgDataForm" styleId="${element.itemid}"
										property='<%="fieldslist[" + index + "].value"%>'
										cols="20" rows="3" readonly="true" styleClass="textboxMul"></html:textarea>
						</logic:equal>
						<logic:notEqual name="element" property="readonly" value="true">
							<html:textarea name="orgDataForm" styleId="${element.itemid}"
										property='<%="fieldslist[" + index + "].value"%>'
										cols="20" rows="3" styleClass="textboxMul"></html:textarea>
						</logic:notEqual>
					</logic:equal>
															  <%
  	if (isFillable1) {
  %> &nbsp;<font color='red'>*</font>&nbsp;<%
 	}
 %>	
				</td>
					<%
							    						
						   if (++j % 3==0)
						    {
						    	if (i % 2 == 0)
								{
				%>			
		</tr>  
		<tr class="trShallow1">
				<%
								} else{
				%>
			</tr>  
		<tr class="trDeep1">
			<%
								}
								i++;			
					    }
			%>
			</logic:iterate>
		</tr>
	</table>
	</div>
</html:form>
<script>
	parent.frames['a'].location="/org/orgdata/orgdata.do?b_menu=link&itemid=${orgDataForm.itemVal}&infor=${orgDataForm.infor}";
	//设置只读的文本框
	<logic:iterate  id="element1" name="orgDataForm" property="readOnlyFlds" indexId="index">
		var itemid = '<bean:write name="element1" property="itemid" filter="true" />';
		var codesetid = '<bean:write name="element1" property="codesetid" filter="true" />';
		obj = $(itemid);
		obj.readOnly="true";
		obj.className="textColorRead";
	</logic:iterate>
</script>