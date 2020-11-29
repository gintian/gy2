<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="java.util.*"%>
<%@ page import="com.hjsj.hrms.actionform.general.approve.personinfo.ApprovePersonForm"%>
<%@ page import="com.hrms.struts.valueobject.Pagination"%>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script type="text/javascript">
<!--
function selchange(){
	approvePersonForm.action="/general/approve/personinfo/sumre.do?b_query=link";
	approvePersonForm.submit();
}
//-->
</script>
<hrms:themes />
<html:form action="/general/approve/personinfo/sumre">
<table width="70%" border="0" cellspacing="0"  align="center" cellpadding="0">
 <tr><td  nowrap>
		<table width="100%" align="center" border="0" cellspacing="0"  cellpadding="0">
		<tr align="left">
			<td class="RecordRow"><logic:equal value="a" name="approvePersonForm" property="abkflag">
				<bean:message key='menu.base'/>
				
				&nbsp;<bean:write name="approvePersonForm" property="selstr" filter="false" />
				&nbsp;&nbsp;</logic:equal>
				<bean:message key='column.sys.status'/>	<bean:write name="approvePersonForm" property="stateselstr" filter="false" />
			</td>
		</tr>
	</table>
</td></tr>
<tr><td >	
	<div class="fixedDiv2" > 
	<table width="100%" align="center" border="0" cellspacing="0"  cellpadding="0" class="ListTableF">
		<TR class="fixedHeaderTr">
				<td height='50'align='center' class="TableRow" nowrap width="50">
					<bean:message key="label.query.number"/>
				</td>
				<td height='50'align='center' class="TableRow" nowrap>
					<bean:message key="set.label"/>
				</td>
				<td height='50'align='center' class="TableRow" nowrap>
					<bean:message key="approve.result.num"/>
				</td>
		</TR>
		<%int i=1;%>
		<hrms:paginationdb id="element" name="approvePersonForm" sql_str="approvePersonForm.sql" table="" where_str="approvePersonForm.where" columns="approvePersonForm.column" order_by="" pagerows="10" page_id="pagination" indexes="indexes" curpage="curpage">
		<bean:define id="upnum" name="element" property="editnum"></bean:define>
		<bean:define id="setsid" name="element" property="setid"></bean:define>
		<bean:define id="fielsetddesc" name="element" property="setdesc"></bean:define>
		<bean:define id="states" name="approvePersonForm" property="state"></bean:define>		
		<logic:notEqual value="0" name="upnum">
		<tr>		
		<td align="center" class="RecordRow" nowrap>
		<%
		String curpage=(String)pageContext.getAttribute("curpage");	
		int currp=0;
		if(curpage!=null&&curpage.length()>0)
		{
		   int cur=Integer.parseInt(curpage);
		   currp=(cur-1)*10+i;
		}	
		out.println(currp);		
		i++;
		%>
		
		</td>
		<td  class="RecordRow" nowrap>
		<A href='/general/approve/personinfo/orgsum.do?b_query=link&setid=<%=setsid%>&pdbflag=${approvePersonForm.pdbflag}&state=${states}'>
		 <logic:notEqual value='A00' name="element" property='setid'>
		   <%=fielsetddesc%>
		</logic:notEqual>
		<logic:equal value='A00' name="element" property='setid'>
		     多媒体子集
		 </logic:equal>
		</A>
		</td>
		<td  class="RecordRow" nowrap>
		<bean:write name="element" property="editnum"/>
		</td>
		</tr>
		</logic:notEqual>
		</hrms:paginationdb>
	</table>
	</div>
</td></tr>
<tr><td >
	<table width="100%" align="center" class="RecordRowP">
			<tr>
				<td width="40%" valign="bottom" align="center" nowrap>
					<bean:message key="label.page.serial" />
					<bean:write name="pagination" property="current" filter="true" />
					<bean:message key="label.page.sum" />
					<bean:write name="pagination" property="count" filter="true" />
					<bean:message key="label.page.row" />
					<bean:write name="pagination" property="pages" filter="true" />
					<bean:message key="label.page.page" />
				</td>
				<td width="60%" align="left" nowrap class="tdFontcolor">
					<p align="left">
						<hrms:paginationdblink name="approvePersonForm" property="pagination" nameId="browseRegisterForm" scope="page">
						</hrms:paginationdblink>
				</td>
			</tr>
</table>
		<html:hidden name="approvePersonForm" property="abkflag"/>
</html:form>





