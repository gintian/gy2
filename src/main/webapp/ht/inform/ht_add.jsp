<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.ht.inform.ContractForm,java.util.*"%>
<script language="JavaScript" src="./htadd.js"></script>
<script language="JavaScript" src="/js/popcalendarFormat.js"></script>
<script language="JavaScript" src="/js/popcalendar.js"></script>
<%
	ContractForm contractForm = (ContractForm)session.getAttribute("contractForm");
	HashMap mainFlds = (HashMap)contractForm.getMainFlds();
	String b0110 = (String)mainFlds.get("b0110");
	String e0122 = (String)mainFlds.get("e0122");
	String e01a1 = (String)mainFlds.get("e01a1");
	String a0101 = (String)mainFlds.get("a0101");
%>
<style>
.fixedDiv2{
	height:expression(document.body.clientHeight-70);
}

</style>
<html:form action="/ht/inform/data_table">
<div class="fixedDiv2">
<table width="100%" border="0" cellpadding="0" cellspacing="0"	align="center">
	<tr>
		<td class="RecordRow noleft" style="border-top: none;"  nowrap align="right">
			<bean:message key="tree.unroot.undesc"/>
		</td>
		<td class="RecordRow noleft noright" style="border-top: none;" nowrap>
			<input type="text" value="<%=b0110 %>" class="textColorRead" size="17" readonly="readonly">
		</td>
		<td class="RecordRow" style="border-top: none;" nowrap align="right">
			<bean:message key="tree.umroot.umdesc"/>
		</td>
		<td class=" RecordRow noleft noright" style="border-top: none;" nowrap>
			<input type="text" value="<%=e0122 %>" class="textColorRead" size="17" readonly="readonly">
		</td>
	</tr>
	<tr>				
		<td class="RecordRow noleft" nowrap style="border-top: none;" align="right">
			<bean:message key="tree.kkroot.kkdesc"/>
		</td>
		<td class="RecordRow noleft noright" style="border-top: none;" nowrap>
			<input type="text" value="<%=e01a1 %>" Class="textColorRead" size="17" readonly="readonly">
		</td>
		<td class="RecordRow" nowrap style="border-top: none;" align="right">
			<bean:message key="columns.archive.name"/>
		</td>
		<td class="RecordRow noleft noright" style="border-top: none;" nowrap>
			<input type="text" value="<%=a0101 %>" Class="textColorRead" size="17" readonly="readonly">
		</td>
	</tr>
	<% int i = 0, j = 0; %>	
	<tr>
	<logic:iterate id="element" name="contractForm" property="subFlds"
				indexId="index">
				<logic:equal name="element" property="priv_status" value="2">
				<%if (j % 2 != 0)  {%>							
				<td align="right" class="RecordRow" style="border-top: none;" nowrap>
				<%} else { %>
				<td align="right" class="RecordRow noleft " style="border-top: none;" nowrap>
				<% }%>
					<bean:write name="element" property="itemdesc" filter="true" />
				</td>
				<td align="left" class="RecordRow noleft noright" style="border-top: none;" nowrap>
									
						<logic:equal name="element" property="itemtype" value="A">
							<logic:equal name="element" property="codesetid" value="0">
								    <html:text maxlength="${element.itemlength}" size="17"
									styleClass="textColorRead" name="contractForm"
									styleId="${element.itemid}"
									property='<%="subFlds[" + index + "].value"%>' />
							</logic:equal>
							<logic:notEqual name="element" property="codesetid" value="0">						
										 <html:hidden name="contractForm"
											property='<%="subFlds[" + index + "].value"%>'
											styleId="${element.itemid}_value" />
										 <html:text maxlength="${element.itemlength}" size="17"
											styleClass="textColorWrite" name="contractForm"
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
									onblur='isNumber(this);' name="contractForm"
									styleId="${element.itemid}"
									property='<%="subFlds[" + index + "].value"%>' />
							</logic:equal>
							<logic:notEqual name="element" property="decimalwidth" value="0">
								 <html:text maxlength="${element.itemlength}" size="17"
									styleClass="textColorWrite"
									onkeypress="event.returnValue=IsDigit(this);"
									onblur='isNumber(this);' name="contractForm"
									styleId="${element.itemid}"
									property='<%="subFlds[" + index + "].value"%>' />
							</logic:notEqual>
						</logic:equal>
						<logic:equal name="element" property="itemtype" value="D">
						<html:text name="contractForm" property='<%="subFlds[" + index + "].value"%>' onclick="popUpCalendar(this,this, '','','',true,false,'yyyy-mm-dd');" styleClass="textColorWrite" maxlength="${element.itemlength}" size="17"  styleId="${element.itemid}" onchange="vali(this, '${element.itemdesc}',event)"/>
							  
						</logic:equal>
						<logic:equal name="element" property="itemtype" value="M">
							<html:textarea name="contractForm" styleId="${element.itemid}"
											property='<%="subFlds[" + index + "].value"%>'
											cols="20" rows="3" styleClass="textboxMul"></html:textarea>
						</logic:equal>
					
				</td>
				
					<%
							    						
						   if (++j % 2==0)
						    {
						    	if (i % 2 == 0)
								{
				%>			
		</tr>  
		<tr>
				<%
								} else{
				%>
			</tr>  
		<tr>
			<%
								}
								i++;			
					    }
			%>
			</logic:equal>
			
			<logic:equal name="element" property="priv_status" value="1">
			<%if (j % 2 != 0)  {%>							
				<td align="right" class="RecordRow" style="border-top: none;" nowrap>
				<%} else { %>
				<td align="right" class="RecordRow noleft" style="border-top: none;" nowrap>
				<% }%>							
					<bean:write name="element" property="itemdesc" filter="true" />
				</td>
				<td align="left" class="RecordRow noleft noright" style="border-top: none;" nowrap>
									
						<logic:equal name="element" property="itemtype" value="A">
							<logic:equal name="element" property="codesetid" value="0">
								<html:text maxlength="${element.itemlength}" size="17"
									styleClass="textColorRead" name="contractForm"
									styleId="${element.itemid}"
									property='<%="subFlds[" + index + "].value"%>' readonly="true"/>
							</logic:equal>
							<logic:notEqual name="element" property="codesetid" value="0">						
										<html:hidden name="contractForm"
											property='<%="subFlds[" + index + "].value"%>'
											styleId="${element.itemid}_value" />
										<html:text maxlength="${element.itemlength}" size="17"
											styleClass="textColorRead" name="contractForm"
											property='<%="subFlds[" + index + "].viewvalue"%>'
											onchange="fieldcode(this,2)" styleId="${element.itemid}" readonly="true"/>								
							</logic:notEqual>
						</logic:equal>
						<logic:equal name="element" property="itemtype" value="N">
							<logic:equal name="element" property="decimalwidth" value="0">
								<html:text maxlength="${element.itemlength}" size="17"
									styleClass="textColorRead"
									onkeypress="event.returnValue=IsDigit2(this);"
									onblur='isNumber(this);' name="contractForm"
									styleId="${element.itemid}"
									property='<%="subFlds[" + index + "].value"%>' readonly="true"/>
							</logic:equal>
							<logic:notEqual name="element" property="decimalwidth" value="0">
								<html:text maxlength="${element.itemlength}" size="17"
									styleClass="textColorRead"
									onkeypress="event.returnValue=IsDigit(this);"
									onblur='isNumber(this);' name="contractForm"
									styleId="${element.itemid}"
									property='<%="subFlds[" + index + "].value"%>' readonly="true"/>
							</logic:notEqual>
						</logic:equal>
						<logic:equal name="element" property="itemtype" value="D">
							<input type="text" name='<%="subFlds[" + index + "].value"%>' maxlength="${element.itemlength}" size="17"  id="${element.itemid}"   class="textColorRead"  style="font-size:10pt;text-align:left"
								 value="${element.value}" readonly="readonly">
						</logic:equal>
						<logic:equal name="element" property="itemtype" value="M">
							<html:textarea name="contractForm" styleId="${element.itemid}"
											property='<%="subFlds[" + index + "].value"%>'
											cols="20" rows="3" styleClass="textColorRead" readonly="true"></html:textarea>
						</logic:equal>
					
				</td>
				
					<%
							    						
						   if (++j % 2==0)
						    {
						    	if (i % 2 == 0)
								{
				%>			
		</tr>  
		<tr>
				<%
								} else{
				%>
			</tr>  
		<tr>
			<%
								}
								i++;			
					    }
			%>
			</logic:equal>
			
			
			</logic:iterate>
				<%   if (j % 2!=0){	%>				
		<td class="RecordRow" style="border-top: none;" nowrap align="right">
		&nbsp;
		</td>
		<td class="RecordRow noleft noright" style="border-top: none;" nowrap>
			&nbsp;
		</td>			
		<%} %>
		</tr>
	</table>
	</div>
	<table align="center">
		<tr>
			<td align="center" style="height:35px;">
				<input type='button' value='&nbsp;<bean:message key='button.save' />&nbsp;'	class="mybutton" onclick='save();'>
				<input type='button' value='&nbsp;<bean:message key='button.close' />&nbsp;'	class="mybutton" onclick='closeWin();'>
			</td>
		</tr>
	</table>
</html:form>