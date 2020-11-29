<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<script language="JavaScript" src="/js/popcalendar.js"></script>

<html:form action="/train/job/browseTrainClassList">
	<%
	    int flag = 0;
	        int n = 0;
	%>
	<br>

	<table width="96%" border="0" cellpadding="0" cellspacing="0" align="center">
		<tr height="20">
			<!--  
       		<td width='10' valign="top" class="tableft"></td>
       		<td width='230' align=center class="tabcenter">&nbsp;${trainClassForm.titleName}&nbsp;</td>
       		<td width='10' valign="top" class="tabright"></td>
       		<td valign="top" class="tabremain" width="700"></td>   
       		   -->
			<td align="left" class="TableRow" style="border-bottom: none;">
				&nbsp;${trainClassForm.titleName}&nbsp;
			</td>

		</tr>
		<tr>
			<td >
				<table width="100%" border="0" cellspacing="0" align="center"
					cellpadding="3" class="ListTableF">
					<logic:iterate id="element" name="trainClassForm"
						property="trainResourceDesc" indexId="index">


						<logic:equal name="element" property="itemtype" value="A">
							<%
							    if (flag == 0)
							                {
							                    if (n == 0)
							                    {
							                        out.println("<tr class=\"trDeep\">");
							                        n = 1;
							                    }
							                    else
							                    {
							                        out.println("<tr class=\"trShallow\">");
							                        n = 0;
							                    }
							                    flag = 1;
							                }
							                else
							                {
							                    flag = 0;
							                }
							%>
							<logic:equal name="element" property="codesetid" value="0">
								<td align="right" class="RecordRow" nowrap >
									<bean:write name="element" property="itemdesc" />
								</td>
								<td align="left" class="RecordRow" nowrap >
									<input type="text" name="a" size="30" class="text4"
										value="<bean:write  name="element" property="value"/>"
										readonly="true"
										maxlength="<bean:write  name="element" property="itemlength"/>" />
									&nbsp;&nbsp;&nbsp;&nbsp;
								</td>
							</logic:equal>
							<logic:notEqual name="element" property="codesetid" value="0">
								<td align="right" class="RecordRow" nowrap >
									<bean:write name="element" property="itemdesc" />
								</td>
								<td align="left" class="RecordRow" nowrap >
									<input type='text' name='a' size="30" class="text4"
										value="<bean:write  name="element" property="viewvalue"/>"
										readonly="true" />
								</td>
							</logic:notEqual>
							<%
							    if (flag == 0)
							                    out.println("</tr>");
							%>
						</logic:equal>
						<logic:equal name="element" property="itemtype" value="D">
							<%
							    if (flag == 0)
							                {
							                    if (n == 0)
							                    {
							                        out.println("<tr class=\"trDeep\">");
							                        n = 1;
							                    }
							                    else
							                    {
							                        out.println("<tr class=\"trShallow\">");
							                        n = 0;
							                    }
							                    flag = 1;
							                }
							                else
							                {
							                    flag = 0;
							                }
							%>
							<td align="right" class="RecordRow" nowrap >
								<bean:write name="element" property="itemdesc" />
							</td>
							<td align="left" class="RecordRow" nowrap >
								<input type="text" name="a" size="30" class="text4"
									value="<bean:write  name="element" property="value"/>" readOnly
									maxlength="<bean:write  name="element" property="itemlength"/>" />
								&nbsp;&nbsp;&nbsp;&nbsp;
							</td>
							<%
							    if (flag == 0)
							                    out.println("</tr>");
							%>
						</logic:equal>
						<logic:equal name="element" property="itemtype" value="M">
							<%
							    if (flag != 0)
							                {
							                    out.println("<td class=\"RecordRow\">&nbsp;</td><td class=\"RecordRow\">&nbsp;</td></tr>");
							                    flag = 0;
							                }
							                if (n == 0)
							                {
							                    out.println("<tr class=\"trDeep\">");
							                    n = 1;
							                }
							                else
							                {
							                    out.println("<tr class=\"trShallow\">");
							                    n = 0;
							                }
							%>
							<td align="right" class="RecordRow" nowrap valign="top">
								<bean:write name="element" property="itemdesc" />
							</td>
							<td align="left" class="RecordRow" nowrap  colspan="3">
							    <textarea name="a" cols="90" rows="6" class="textboxMul"  styleClass="text4" readonly><bean:write name="element" property="value" />
								</textarea>
							</td>
							<%
							    out.println("</tr>");
							%>
						</logic:equal>
						<logic:equal name="element" property="itemtype" value="N">
							<%
							    if (flag == 0)
							                {
							                    if (n == 0)
							                    {
							                        out.println("<tr class=\"trDeep\">");
							                        n = 1;
							                    }
							                    else
							                    {
							                        out.println("<tr class=\"trShallow\">");
							                        n = 0;
							                    }
							                    flag = 1;
							                }
							                else
							                {
							                    flag = 0;
							                }
							%>
							<td align="right" class="RecordRow" nowrap >
								<bean:write name="element" property="itemdesc" />
							</td>
							<td align="left" class="RecordRow" nowrap >
								<input type="text" name="a" size="30" class="text4"
									value="<bean:write  name="element" property="value"/>" readOnly
									maxlength="<bean:write  name="element" property="itemlength"/>" />
								&nbsp;&nbsp;&nbsp;&nbsp;
							</td>
							<%
							    if (flag == 0)
							                    out.println("</tr>");
							%>
						</logic:equal>
					</logic:iterate>
					<%
					    if (flag != 0)
					        {
					            out.println("<td class=\"RecordRow\">&nbsp;</td><td class=\"RecordRow\">&nbsp;</td>");
					        }
					        out.println("</tr>");
					%>
				</table>
			</td>

	<table width="90%" border="0">
		<tr>
			<td align="center" style='height: 35px'>
				<Input type='button' name='a'
					value="<bean:message key='reportcheck.return'/>" onclick='history.go(-1);'
					class="mybutton">

			</td>
		</tr>
	</table>
</html:form>