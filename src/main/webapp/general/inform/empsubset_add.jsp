<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hjsj.hrms.valueobject.common.FieldItemView"%>
<script language="JavaScript" src="./empsubset.js"></script>
<script language="JavaScript">
function save0()
{
	<% int m=0; %>
	<logic:iterate  id="element1"    name="mInformForm"  property="subFlds" indexId="index"> 
	<%
		FieldItemView abean1=(FieldItemView)pageContext.getAttribute("element1");
	    boolean isFillable=abean1.isFillable();	
	%>		
		var aa<%=m%>=document.getElementsByName("subFlds[<%=m%>].value")
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
<style>
.explainFont{
  size:10px;
  color:#979797;
}
</style>
<hrms:themes></hrms:themes>
<style>
.textColorWrite{
	width: 138px;
}
.text4{
	width: 138px;
}
</style>
<html:form action="/general/inform/empsubset_add">
<div class="fixedDiv3">
	<table width="100%" border="0" cellpadding="0" cellspacing="0"align="center" >
		<tr>
			<td>
			    ${mInformForm.empInfo }
				<div class="common_border_color" style="width:99%; border: 1px solid;height:expression(document.body.clientHeight-80);">
					<table width="100%" border="0" cellpadding="0" cellspacing="0">
						<%
						int i = 0, j = 0;
						String className="";
						%>
						<tr class="trShallow1">
							<logic:iterate id="element" name="mInformForm" property="subFlds" indexId="index">
								<%
								    FieldItemView abean = (FieldItemView) pageContext.getAttribute("element");
								    boolean isFillable1 = abean.isFillable();
								    boolean readonly=abean.isReadonly();
								    String itemtype = abean.getItemtype();									
								    if(itemtype.equalsIgnoreCase("M")){										  
								    	  if (j % 2 == 0){
								%>
								<logic:notEqual value="-1" name="element" property="explain">
									<td align="right" class="RecordRow noleft" style="border-top: none;" nowrap>
										<bean:write name="element" property="itemdesc" filter="true" />
										<table>
											<tr>
											   <td align="left">
											       <font class="explainFont"><bean:write name="element" property="explain"/></font>
											   </td>
											</tr>
										</table>
									</td>
								</logic:notEqual>
								<logic:equal value="-1" name="element" property="explain">
									<td align="right" class="RecordRow noleft" style="border-top: none;" nowrap>
										<bean:write name="element" property="itemdesc" filter="true" />
									</td>
								</logic:equal>
								<td align="left" class="RecordRow noleft noright<% if (i % 2 == 1){%> noright<%} %>" style="border-top: none; width: 170px;" nowrap colspan='3' >
									<html:textarea name="mInformForm" styleId="${element.itemid}"
											property='<%="subFlds[" + index + "].value"%>' cols="70"
											rows="5" styleClass="textboxMul"></html:textarea>
								</td>
								<%
								    j++;
								    }else{
								%>
									<td class="RecordRow noleft" style="border-top: none;border-right: none" nowrap align="right" >&nbsp;
									</td>
									<td class="RecordRow noleft noright" style="border-top: none;" nowrap >&nbsp;
									</td>
									</tr>
									<tr class="">
							     <%
								    i++; 
								 %>
								<logic:notEqual value="-1" name="element" property="explain">
								<td align="right" class="RecordRow noleft" style="border-top: none;" nowrap>
									<bean:write name="element" property="itemdesc" filter="true" />
									<table>
										<tr>
											<td align="left">
											   <font class="explainFont"><bean:write name="element" property="explain"/></font>
											</td>
										</tr>
									</table>
								</td>
								</logic:notEqual>
								<logic:equal value="-1" name="element" property="explain">
									<td align="right" class="RecordRow noleft" style="border-top: none;" nowrap>
										<bean:write name="element" property="itemdesc" filter="true" />
									</td>
								</logic:equal>
								<td align="left" class="RecordRow noleft noright<% if (i % 2 == 1){%> noright<%} %>" style="border-top: none;width: 170px;" nowrap colspan='3'>
									<html:textarea name="mInformForm" styleId="${element.itemid}"
											property='<%="subFlds[" + index + "].value"%>' cols="70"
											rows="5" styleClass="textboxMul"></html:textarea>
								</td>	
									
								<%
								    }
							    }//以上是备注型; 
								    else {//非备注型;
								%>	
								<logic:notEqual value="-1" name="element" property="explain">							
									<td align="right" class="RecordRow noleft" style="border-top: none;" nowrap>
										<bean:write name="element" property="itemdesc" filter="true" />
										<table>
											<tr>
												<td align="left">
												  <font class="explainFont"><bean:write name="element" property="explain"/></font>
												</td>
											</tr>
										</table>
									</td>
								</logic:notEqual>
								<logic:equal value="-1" name="element" property="explain">	
									<td align="right" class="RecordRow noleft" style="border-top: none;" nowrap>
										<bean:write name="element" property="itemdesc" filter="true" />
									</td>
								</logic:equal>
								<td align="left" class="RecordRow noleft<% if (i % 2 == 1){%> noright<%} %>" style="border-top: none;" width='180' nowrap>
									<logic:equal name="element" property="itemtype" value="A">
										<logic:equal name="element" property="codesetid" value="0">
											<html:text maxlength="${element.itemlength}" size="17"
												styleClass="textColorWrite" name="mInformForm"
												styleId="${element.itemid}"
												property='<%="subFlds[" + index + "].value"%>' 
												readonly="<%= readonly%>" />
										</logic:equal>
										<logic:notEqual name="element" property="codesetid" value="0">
											<logic:notEqual name="element" property="itemid"
												value="orgname">
												<html:hidden name="mInformForm"
													property='<%="subFlds[" + index + "].value"%>'
													styleId="${element.itemid}_value" />
												<html:text maxlength="${element.itemlength}" size="17"
													styleClass="textColorWrite" name="mInformForm"
													property='<%="subFlds[" + index + "].viewvalue"%>'
													onchange="fieldcode(this,2)" styleId="${element.itemid}" />
												<img id='img${element.itemid}' src="/images/code.gif"
													onclick='javascript:openInputCodeDialog("${element.codesetid}","<%="subFlds[" + index + "].viewvalue"%>");'
													align="absmiddle" />&nbsp;								
											</logic:notEqual>
											<logic:equal name="element" property="itemid" value="orgname">
												<html:hidden name="mInformForm"
													property='<%="subFlds[" + index + "].value"%>'
													styleId="${element.itemid}_value" />
												<html:text maxlength="${element.itemlength}" size="17"
													styleClass="textColorWrite" name="mInformForm"
													property='<%="subFlds[" + index + "].viewvalue"%>'
													onchange="fieldcode(this,2)" styleId="${element.itemid}" />
												<img id='img${element.itemid}' src="/images/code.gif"
													onclick='javascript:openVorgCodeDialog("<%="subFlds[" + index + "].viewvalue"%>");'
													align="absmiddle" />&nbsp;								
											</logic:equal>
										</logic:notEqual>
									</logic:equal>
									<logic:equal name="element" property="itemtype" value="N">
										<logic:equal name="element" property="decimalwidth" value="0">
											<html:text maxlength="${element.itemlength}" size="17"
												styleClass="textColorWrite"
												onkeypress="event.returnValue=IsDigit2(this);"
												onblur='isNumber(this);' name="mInformForm"
												styleId="${element.itemid}"
												property='<%="subFlds[" + index + "].value"%>' />
										</logic:equal>
										<logic:notEqual name="element" property="decimalwidth"
											value="0">
											<html:text maxlength="${element.itemlength}" size="17"
												styleClass="textColorWrite"
												onkeypress="event.returnValue=IsDigit(this);"
												onblur='isNumber(this);' name="mInformForm"
												styleId="${element.itemid}"
												property='<%="subFlds[" + index + "].value"%>' />
										</logic:notEqual>
									</logic:equal>
									<logic:equal name="element" property="itemtype" value="D">
										<input type="text" name='<%="subFlds[" + index + "].value"%>'
											maxlength="${element.itemlength}" size="17"
											id="${element.itemid}" extra="editor" class="textColorWrite"
											style="font-size:10pt;text-align:left"
											dropDown="dropDownDate" value="${element.value}"
											onchange=" if(!validate(this,'${element.itemdesc}')) { this.value='';}">
									</logic:equal>
								
									<%
											    if (isFillable1)
											    {
									%>
									<font color='red'>*</font>
									<%
									}
									%>
								</td>
								<%i++;
								}
									if (++j % 2 == 0){
								%>
							
						</tr>
						<tr >
							<%
									    }
							%>
							</logic:iterate>
							<%
								    if (j % 2 != 0)
								    {
							%>
							<td class="RecordRow noleft" style="border-top: none;border-right: none;" nowrap align="right">
								&nbsp;
							</td>
							<td class="RecordRow noleft noright" style="border-top: none;" nowrap>
								&nbsp;
							</td>
							<%
							}
							%>
						</tr>
					</table>
				</div>
			</td>
		</tr>
		<tr align="center">
			<td style="padding-top:5px;">
		<input type='button'
		value='&nbsp;<bean:message key='button.save' />&nbsp;'
		class="mybutton" onclick='save0();'>
	<input type='button'
		value='&nbsp;<bean:message key='button.close' />&nbsp;'
		class="mybutton" onclick='window.close();'>
	</td>
			</tr>
	</table>
	</div>
</html:form>
<script>
//设置只读的文本框
	<logic:iterate  id="element1" name="mInformForm" property="readOnlyFlds" indexId="index">
		var itemid = '<bean:write name="element1" property="itemid" filter="true" />';
		var codesetid = '<bean:write name="element1" property="codesetid" filter="true" />';
		obj = $(itemid);
		obj.readOnly="true";
		obj.className="textColorRead";
		var imgid = 'img'+itemid;
		obj = $(imgid);
		if(codesetid!='0')
			obj.style.display="none";
	</logic:iterate>
</script>
