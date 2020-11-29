<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.general.template.TemplateForm"%>
<%int i=0;
  String backctrl="";
  TemplateForm templateForm=(TemplateForm)session.getAttribute("templateForm"); 
  if(templateForm!=null&&templateForm.getNavigation().equalsIgnoreCase("htbl")){
   backctrl="htbl";
  }
%>
<script type="text/javascript">
function returnDh(){
   parent.parent.location="/general/tipwizard/tipwizard.do?br_ct=link";
}
</script>
<link href="../../css/css1.css" rel="stylesheet" type="text/css">
<hrms:themes></hrms:themes>
<html:form action="/system/warn/myinfo_all"> 
<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
		<thead>
			<tr>
				<td align="center" class="TableRow" nowrap>
				预警名称&nbsp;
				</td>           
				<td align="center" class="TableRow" nowrap>
				提示内容&nbsp;
				</td>
			</tr>
		</thead>
	<hrms:extenditerate id="element" name="myConfigForm" property="pageListForm.list" indexes="indexes" pagination="pageListForm.pagination" pageCount="15" scope="session">
          <%
          if(i%2==0)
          {
          %>
          <tr class="trShallow">
          <%}
          else
          {%>
          <tr class="trDeep">
          <%
          }
          i++;          
          %> 
			<td align="left" class="RecordRow" nowrap>		
					&nbsp;<bean:write name="element" property="string(wname)" filter="false" />
				&nbsp;
			</td>
			<td align="left" class="RecordRow" nowrap>		
				<a href="/system/warn/myresult_manager.do?b_query=link&warn_wid=<bean:write name="element" property="string(wid)" filter="false" />">
					&nbsp;<bean:write name="element" property="string(cmsg)" filter="false" />
				</a>
				&nbsp;
			</td>
			
		</tr>
	</hrms:extenditerate>
</table>

<table width="100%" align="center" class="RecordRowP">
	<tr>
		<td valign="bottom" class="tdFontcolor">
		    <bean:message key="label.page.serial"/>
			<bean:write name="myConfigForm" property="pageListForm.pagination.current" filter="true" />
					<bean:message key="label.page.sum"/>
			<bean:write name="myConfigForm" property="pageListForm.pagination.count" filter="true" />
					<bean:message key="label.page.row"/>
			<bean:write name="myConfigForm" property="pageListForm.pagination.pages" filter="true" />
					<bean:message key="label.page.page"/>
		</td>
		<td align="right" nowrap class="tdFontcolor">
			<p align="right">
				<hrms:paginationlink name="myConfigForm" property="pageListForm.pagination" nameId="pageListForm">
				</hrms:paginationlink>
		</td>
	</tr>
</table>
<%
 if(backctrl.equalsIgnoreCase("htbl")){//合同办理导航图进来的，返回按钮的处理 
%>
<table align="center">
        <tr>
             <td >
                 <input type="button" class="mybutton" value="返回" onclick="returnDh()"/>
             </td>
        </tr>
</table>    
<%
}
%>
</html:form>	



