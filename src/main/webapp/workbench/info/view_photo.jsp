<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%
	int i=0;
%>
<hrms:themes></hrms:themes>
<html:form action="/workbench/info/view_photo">
<table width="80%" border="0" cellspacing="1"  align="center" cellpadding="1">
          <hrms:paginationdb id="element" name="selfInfoForm" sql_str="selfInfoForm.strsql" table="" where_str="selfInfoForm.cond_str" columns="A0100,B0110,E0122,E01A1,A0101,UserName," order_by=" order by A0000" pagerows="21" page_id="pagination" keys="a0100">
          <%
           if(i%7==0)
          {
          %>
          <tr>
          <%
          }
          %>             
          <td align="center" NOWRAP>
          	<a href="/workbench/browse/showselfinfo.do?b_search=link&userbase=<bean:write name="selfInfoForm" property="userbase" filter="true"/>&a0100=<bean:write name="element" property="a0100" filter="true"/>" target="mil_body"><hrms:ole name="element" dbpre="selfInfoForm.userbase" a0100="a0100" scope="page" height="120" width="85"/></a><br>
          	<a href="/workbench/browse/showselfinfo.do?b_search=link&userbase=<bean:write name="selfInfoForm" property="userbase" filter="true"/>&a0100=<bean:write name="element" property="a0100" filter="true"/>" target="mil_body"><bean:write name="element" property="a0101" filter="true"/></a><br>
          	<hrms:codetoname codeid="UM" name="element" codevalue="e0122" codeitem="codeitem" scope="page"/>  	      
          	<a href="/workbench/browse/showselfinfo.do?b_search=link&userbase=<bean:write name="selfInfoForm" property="userbase" filter="true"/>&a0100=<bean:write name="element" property="a0100" filter="true"/>" target="mil_body"><bean:write name="codeitem" property="codename" /></a>&nbsp;<br>  
          	<hrms:codetoname codeid="@K" name="element" codevalue="e01a1" codeitem="codeitem" scope="page"/>  	      
          	<a href="/workbench/browse/showselfinfo.do?b_search=link&userbase=<bean:write name="selfInfoForm" property="userbase" filter="true"/>&a0100=<bean:write name="element" property="a0100" filter="true"/>" target="mil_body"><bean:write name="codeitem" property="codename" /></a>&nbsp;  
          </td> 
          <%
          if((i+1)%7==0)
          {%>
          </tr>
          <%
          }
          i++;          
          %>         
        </hrms:paginationdb>
        
</table>
<table  width="70%" align="center">
		<tr>
		    <td valign="bottom" class="tdFontcolor">第
					<bean:write name="pagination" property="current" filter="true" />
					页
					共
					<bean:write name="pagination" property="count" filter="true" />
					条
					共
					<bean:write name="pagination" property="pages" filter="true" />
					页
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationdblink name="selfInfoForm" property="pagination" nameId="selfInfoForm" scope="page">
				</hrms:paginationdblink>
		       </td>
		</tr>
</table>
<table  width="70%" align="center">
          <tr>
            <td align="left">       
         	    <hrms:submit styleClass="mybutton" property="br_return">
            		<bean:message key="button.return"/>
	 	    </hrms:submit>
            </td>
          </tr>          
</table>
</html:form>
