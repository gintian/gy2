<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="javascript" src="/ajax/constant.js"></script>
<%int n=0;%>
<hrms:themes></hrms:themes>
<html:form action="/hire/jp_contest/personinfo/showinfodata">
<br><br><br><br>
<table width="50%" border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable">
	<tr  class="TableRow" > 
		<td align="left" class="RecordRow" colspan="2" nowrap>
			<bean:message key="hire.jp.apply.datum"/>
		</td>
	</tr>
	<tr>
		<td width="20%"  align="center" class="TableRow"  nowrap>
			<bean:message key="kh.field.seq"/>
		</td>
		<td width="80%" align="center" class="TableRow" nowrap>
			<bean:message key="hire.jp.apply.datumname"/>
		</td>
	</tr>
	 <hrms:extenditerate id="element" name="showJpPersonForm" property="stuffListForm.list" indexes="indexes"  pagination="stuffListForm.pagination" pageCount="10" scope="session">
		<%	if(n%2==0){
        %>
             	<tr class="trShallow">            
        <%}
       	 	else
        	{%>
            	<tr class="trDeep">  
            <%}
            n++;
            %>
            <td width="20%" align="center" class="RecordRow" nowrap>
            	<%=n%>
            </td>
            <td align="left" class="RecordRow" nowrap>
            	<a href="/jp_contest/personinfo/ShowStuffInfo?id=<bean:write name="element" property="id" filter="true"/>"><bean:write name="element" property="name" filter="true"/></a>
            </td>
	   </tr>
	</hrms:extenditerate>
	<table  width="50%" align="center">
	  <tr>
	      <td valign="bottom" class="tdFontcolor"><bean:message key="label.page.serial"/>
	     <bean:write name="showJpPersonForm" property="stuffListForm.pagination.current" filter="true" />
	    <bean:message key="label.page.sum"/>
	     <bean:write name="showJpPersonForm" property="stuffListForm.pagination.count" filter="true" />
	   <bean:message key="label.page.row"/>
	     <bean:write name="showJpPersonForm" property="stuffListForm.pagination.pages" filter="true" />
	     <bean:message key="label.page.page"/>
	   </td>
	   <td  align="right" nowrap class="tdFontcolor">
	         <p align="right">
	         <hrms:paginationlink name="showJpPersonForm" property="stuffListForm.pagination"
	                  nameId="stuffListForm" propertyId="roleListProperty">
	         </hrms:paginationlink>
	   </td>
	  </tr>
	</table>
</table>
<table  width="50%" align="center"  >
          <tr>
            <td align="left">
            	<input type="button" name="btnreturn" value="<bean:message key="button.return"/>" onclick="history.back();" class="mybutton">
            </td>
          </tr>          
</table>
</html:form>
