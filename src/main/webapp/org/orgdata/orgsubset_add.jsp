<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="JavaScript" src="./orgsubset.js"></script>
<script language="JavaScript" src="/js/validateDate.js"></script>
<%@ page import="com.hjsj.hrms.valueobject.common.FieldItemView"%>
<style>
<!--
.textColorWrite{width: 130px;}
-->
</style>
<script language="JavaScript">
function save0()
{
	<% int m=0; %>
	<logic:iterate  id="element1"    name="orgDataForm"  property="subFlds" indexId="index"> 
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
<html:form action="/org/orgdata/orgsubset_add">
	<input type='button' value='&nbsp;<bean:message key='button.save' />&nbsp;'	class="mybutton" onclick='save0();'>
	<input type='button' value='&nbsp;<bean:message key='button.close' />&nbsp;'	class="mybutton" onclick='closeWin();'>	
<div class="fixedDiv2" style="padding:3px;">
<table width="100%" border="0" cellpadding="0" cellspacing="0"	align="center" class="ListTableF" style="border-bottom:0px;">
	<% int i = 1, j = 0; %>	
	<tr class="trShallow1">
	<logic:iterate id="element" name="orgDataForm" property="subFlds"
				indexId="index">
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
							<html:text maxlength="${element.itemlength}" size="17"
								styleClass="textColorWrite" name="orgDataForm"
								styleId="${element.itemid}"
								property='<%="subFlds[" + index + "].value"%>' />
						</logic:equal>
						<logic:notEqual name="element" property="codesetid" value="0">						
									<html:hidden name="orgDataForm"
										property='<%="subFlds[" + index + "].value"%>'
										styleId="${element.itemid}_value" />
									<html:text maxlength="${element.itemlength}" size="17"
										styleClass="textColorWrite" name="orgDataForm"
										property='<%="subFlds[" + index + "].viewvalue"%>'
										onchange="fieldcode(this,2)" styleId="${element.itemid}" />
									<img id='img${element.itemid}' src="/images/code.gif"
										onclick='javascript:openInputCodeDialog("${element.codesetid}","<%="subFlds[" + index + "].viewvalue"%>");'
										align="absmiddle" />&nbsp;								
						</logic:notEqual>
					</logic:equal>
					<logic:equal name="element" property="itemtype" value="N">
						<logic:equal name="element" property="decimalwidth" value="0">
							<html:text maxlength="${element.itemlength}" size="17"
								styleClass="textColorWrite"
								onkeypress="event.returnValue=IsDigit2(this);"
								onblur='isNumber(this);' name="orgDataForm"
								styleId="${element.itemid}"
								property='<%="subFlds[" + index + "].value"%>' />
						</logic:equal>
						<logic:notEqual name="element" property="decimalwidth" value="0">
							<html:text maxlength="${element.itemlength}" size="17"
								styleClass="textColorWrite"
								onkeypress="event.returnValue=IsDigit(this);"
								onblur='isNumber(this);' name="orgDataForm"
								styleId="${element.itemid}"
								property='<%="subFlds[" + index + "].value"%>' />
						</logic:notEqual>
					</logic:equal>
					<logic:equal name="element" property="itemtype" value="D">
						<input type="text" name='<%="subFlds[" + index + "].value"%>' maxlength="${element.itemlength}" size="17"  id="${element.itemid}" extra="editor"  class="m_input textColorWrite"  style="font-size:10pt;text-align:left"
							dropDown="dropDownDate" value="${element.value}"  onchange=" if(!validate(this,'${element.itemdesc}')) { this.value='';}">
					</logic:equal>
					<logic:equal name="element" property="itemtype" value="M">
						<html:textarea name="orgDataForm" styleId="${element.itemid}"
										property='<%="subFlds[" + index + "].value"%>'
										cols="20" rows="3" styleClass="textboxMul"></html:textarea>
					</logic:equal>
															  <%
  	if (isFillable1) {
  %> &nbsp;<font color='red'>*</font>&nbsp;<%
 	}
 %>	
				</td>
					<%
							    						
						   if (++j % 2==0)
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
				<%   if (j % 2!=0){	%>				
		<td class="RecordRow" nowrap align="right">
		
		</td>
		<td class="RecordRow" nowrap>
			
		</td>			
		<%} %>
		</tr>
	</table>
	</div>
</html:form>
<script>
//设置只读的文本框
	<logic:iterate  id="element1" name="orgDataForm" property="readOnlyFlds2" indexId="index">
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