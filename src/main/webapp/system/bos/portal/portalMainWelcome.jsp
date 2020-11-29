<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@page import="java.util.*,org.apache.commons.beanutils.LazyDynaBean"%>
<%
	int i = 0;
%>
<HTML>
	<HEAD>
		<TITLE></TITLE>
		<script LANGUAGE=javascript src="/js/function.js"></script>
		<script LANGUAGE=javascript src="/js/validate.js"></script>
		<script LANGUAGE=javascript src="/system/bos/portal/portalment.js"></script>
		<script type="text/javascript">

		</script>
	</HEAD>
	
	<html:form action="/system/bos/portal/portalMain.do?b_search=query">
	<html:hidden name="portalMainForm" property="parentid"/>
		<table width="100%" border="0" cellspacing="0" align="center"
			cellpadding="0" class="ListTable complex_border_color" style="margin-top:2px;">
			<thead>
				<tr width="100%">
				<logic:equal name="portalMainForm" property="opt" value="0">
				<td align="center" class="TableRow" nowrap>
						<bean:message key="lable.portal.main.id" />
						&nbsp;
					</td>
					<td align="center" class="TableRow" nowrap>
						<bean:message key="lable.portal.main.name" />
						&nbsp;
					</td>
					<td align="center" class="TableRow" nowrap style="border-right:none;">
						<bean:message key="lable.portal.main.colnum" />
						&nbsp;
					</td>
				</logic:equal>
				<logic:equal name="portalMainForm" property="opt" value="1">
				<td align="center" class="TableRow" nowrap>
						<bean:message key="lable.portal.main.id" />
						&nbsp;
					</td>
					<td align="center" class="TableRow" nowrap>
						<bean:message key="lable.portal.main.name" />
						&nbsp;
					</td>
					<td align="center" class="TableRow" nowrap style="border-right:none;">
						<bean:message key="lable.portal.main.colwidth" />
						&nbsp;
					</td>
				</logic:equal>
				<logic:equal name="portalMainForm" property="opt" value="2">
					<td align="center" class="TableRow" nowrap>
						<bean:message key="lable.portal.main.id" />
						&nbsp;
					</td>
					<td align="center" class="TableRow" nowrap>
						<bean:message key="lable.portal.main.name" />
						&nbsp;
					</td>
					<td align="center" class="TableRow" nowrap>
						<bean:message key="lable.portal.main.url" />
						&nbsp;
					</td>
					<!--  td align="center" class="TableRow" nowrap>
						<bean:message key="lable.portal.main.icon" />
						&nbsp;
					</td-->
					<td align="center" class="TableRow" nowrap>
						<bean:message key="lable.portal.panel.height" />
						&nbsp;
					</td>
					
					<td align="center" class="TableRow" nowrap>
						<bean:message key="lable.portal.panel.hide" />
						&nbsp;
					</td>
						<td align="center" class="TableRow" nowrap>
						<bean:message key="lable.portal.panel.priv" />
						&nbsp;
					</td>
						<td align="center" class="TableRow" nowrap style="border-right:none;">
						<bean:message key="lable.portal.panel.operation" />
						&nbsp;
					</td>
				</logic:equal>
					<logic:equal name="portalMainForm" property="opt" value="3">
					<td align="center" class="TableRow" nowrap>
						<bean:message key="lable.portal.main.id" />
						&nbsp;
					</td>
					<td align="center" class="TableRow" nowrap>
						<bean:message key="lable.portal.main.name" />
						&nbsp;
					</td>
					<td align="center" class="TableRow" nowrap>
						<bean:message key="lable.portal.main.url" />
						&nbsp;
					</td>
					<!-- td align="center" class="TableRow" nowrap>
						<bean:message key="lable.portal.main.icon" />
						&nbsp;
					</td -->
					<td align="center" class="TableRow" nowrap>
						<bean:message key="lable.portal.panel.height" />
						&nbsp;
					</td>
					
					<td align="center" class="TableRow" nowrap>
						<bean:message key="lable.portal.panel.hide" />
						&nbsp;
					</td>
						<td align="center" class="TableRow" nowrap>
						<bean:message key="lable.portal.panel.priv" />
						&nbsp;
					</td>
						<td align="center" class="TableRow" nowrap style="border-right:none;">
						<bean:message key="lable.portal.panel.operation" />
						&nbsp;
					</td>
				</logic:equal>
					
				</tr>
			</thead>
			<hrms:extenditerate id="element" name="portalMainForm" property="portalMainForm.list" indexes="indexes" pagination="portalMainForm.pagination" pageCount="${portalMainForm.pagerows}" scope="session">
				
				<%
					if (i % 2 == 0) {
				%>
				<tr class="trShallow" width="100%">
					<%
						} else {
					%>
				
				<tr class="trDeep" width="100%">
					<%
						}
									i++;
					%>
					<!--td align="center" class="RecordRow" nowrap>
						
					 <hrms:checkmultibox name="portalMainForm" property="portalMainForm.select" value="true" indexes="indexes"/>&nbsp;
					<html:hidden name="element" property="codeitemid" />
					</td -->
					<logic:equal name="element" property="codeitemopt" value="1">
	    				<td align="left" class="RecordRow" nowrap width="100"
						style="word-break: break-all">

						&nbsp;<bean:write name="element" property="codeitemid" filter="true" />
						&nbsp;

					</td>

					<td align="left" class="RecordRow" nowrap width="200"
						style="word-break: break-all">
						&nbsp;<bean:write name="element" property="codeitemdesc" filter="true" />
						&nbsp;

					</td>
					<td align="left" class="RecordRow" nowrap width="200"
						style="word-break: break-all;border-right:none;">
						&nbsp;<bean:write name="element" property="codeitemcolumns" filter="true" />
						&nbsp;

					</td>
	    				</logic:equal>
	    				<logic:equal name="element" property="codeitemopt" value="2">
	    					<td align="left" class="RecordRow" nowrap width="100"
						style="word-break: break-all">

						&nbsp;<bean:write name="element" property="codeitemid" filter="true" />
						&nbsp;

					</td>

					<td align="left" class="RecordRow" nowrap width="200"
						style="word-break: break-all">
						&nbsp;<bean:write name="element" property="codeitemdesc" filter="true" />
						&nbsp;

					</td>
					<td align="left" class="RecordRow" nowrap width="200"
						style="word-break: break-all;border-right:none;">
						&nbsp;<bean:write name="element" property="codeitemcolwidth" filter="true" />
						&nbsp;

					</td>
	    				</logic:equal>
	    				<logic:equal name="element" property="codeitemopt" value="3">
	    					<td align="left" class="RecordRow" nowrap width="100"
						style="word-break: break-all">

						&nbsp;<bean:write name="element" property="codeitemid" filter="true" />
						&nbsp;

					</td>

					<td align="left" class="RecordRow" nowrap width="100"
						style="word-break: break-all">
						&nbsp;<bean:write name="element" property="codeitemdesc" filter="true" />
						&nbsp;

					</td>
					
					<td align="left" class="RecordRow" nowrap width="400"
						style="word-break: break-all">
						&nbsp;<bean:write name="element" property="codeitemurl" filter="true" />
						&nbsp;

					</td>
					
					<!--  td align="left" class="RecordRow" nowrap width="300"
						style="word-break: break-all">
						&nbsp;<bean:write name="element" property="codeitemicon" filter="true" />
						&nbsp;
					</td-->
					<td align="left" class="RecordRow" nowrap width="50"
						style="word-break: break-all">
						&nbsp;<bean:write name="element" property="codeitemheight" filter="true" />
						&nbsp;
					</td>
					<td align="left" class="RecordRow" nowrap width="50"
						style="word-break: break-all">
						&nbsp;<bean:write name="element" property="codeitemhide" filter="true" />
						&nbsp;
					</td>
					<td align="left" class="RecordRow" nowrap width="50"
						style="word-break: break-all">
						&nbsp;<bean:write name="element" property="codeitempriv" filter="true" />
						&nbsp;
					</td>
					<td align="left" class="RecordRow" nowrap width="50"
						style="word-break: break-all;border-right:none;">
						&nbsp;<logic:equal name="element" property="codeitemoperation" value="true">
	    					<a href="###" onclick="assign_role('<bean:write name="element" property="codeitemid" filter="true" />')">授权</a>
	    				</logic:equal>
					</td>
	    				</logic:equal>
				

				</tr>
			</hrms:extenditerate>
			
		<tr><td colspan="7">
		<table width="100%"  align="center">
		<tr>
			<td valign="bottom" class="tdFontcolor">
		    	<hrms:paginationtag name="portalMainForm" pagerows="${portalMainForm.pagerows}" property="portalMainForm.pagination" scope="session" refresh="true"></hrms:paginationtag>
			</td>
			<td align="right" nowrap class="tdFontcolor">
				<p align="right">
					<hrms:paginationlink name="portalMainForm" property="portalMainForm.pagination" nameId="portalMainForm" propertyId="roleListProperty">
					</hrms:paginationlink>
			</td>
		</tr>
	</table>
	</td></tr>
	</table>
	</html:form>
	<script>
  function assign_role(portalid)
  {
    
     var isCorrect=false;
    
  
     var target_url;
     var winFeatures = "dialogHeight:400px; dialogLeft:320px;"; 
     target_url="/system/bos/portal/portalPriv.do?b_addpriv=link`portalid="+portalid;
     var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(target_url);
     var return_vo= window.showModalDialog(iframe_url,1, 
        "dialogWidth:600px; dialogHeight:600px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
     if(!return_vo)
		return false;	
//	 if(return_vo.role_id==""||return_vo.role_id=="undefined")
//	    return false;
     portalMainForm.action = "/system/bos/portal/portalPriv.do?b_savepriv=link&a_base_ids="+$URL.encode(return_vo.role_id);
     portalMainForm.submit();
  }


</script>
</HTML>
