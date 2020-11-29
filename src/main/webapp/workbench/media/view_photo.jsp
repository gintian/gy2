<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="org.apache.commons.beanutils.LazyDynaBean"%>
<%
	int i=0;
%>
<script language="javascript">
function winhref(a0100,target)
{
   if(a0100=="")
      return false;
    multMediaForm.action="/workbench/media/searchmediainfolist.do?b_search=link&userbase=${multMediaForm.userbase}&a0100="+a0100+"&flag=notself&setprv=2&returnvalue=11";
    //href=       "/workbench/media/searchmediainfolist.do?b_search=link&userbase=${multMediaForm.userbase}&a0100="+a0100+"&flag=notself&setprv=2&returnvalue=11";
    multMediaForm.target=target;
    multMediaForm.submit();
   
      
}
function document.oncontextmenu() 
   { 
      return　false; 
   } 
</script>
<hrms:themes></hrms:themes>
<html:form action="/workbench/media/view_photo">
<table width="80%" border="0" cellspacing="0"  align="center" cellpadding="0">
          <hrms:paginationdb id="element" name="multMediaForm" sql_str="multMediaForm.strsql" table="" where_str="multMediaForm.cond_str" columns="A0100,B0110,E0122,E01A1,A0101,UserName," order_by="multMediaForm.order_by" pagerows="21" page_id="pagination" keys="">
          <%
          if(i%7==0)
          {
          %>
          <tr>
          <%
          }
             	LazyDynaBean abean=(LazyDynaBean)pageContext.getAttribute("element");
             	String a0100=(String)abean.get("a0100"); 
                request.setAttribute("name",a0100);       	                           
          %>             
          <td align="center" NOWRAP>
          	<hrms:ole name="element" dbpre="multMediaForm.userbase" href="###" a0100="a0100" scope="page" height="120" width="85" onclick="winhref('${name}','mil_body')"/></a>
          	<br><a href="###" onclick="winhref('${name}','mil_body')"><bean:write name="element" property="a0101" filter="true"/></a><br>
          	<hrms:codetoname codeid="UM" name="element" codevalue="e0122" codeitem="codeitem" scope="page"/>  	      
          	<a href="###" onclick="winhref('${name}','mil_body')"><bean:write name="codeitem" property="codename" /></a>&nbsp;<br>  
          	<hrms:codetoname codeid="@K" name="element" codevalue="e01a1" codeitem="codeitem" scope="page"/>  	      
          	<a href="###" onclick="winhref('${name}','mil_body')"><bean:write name="codeitem" property="codename" /></a>&nbsp;  
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
<table  width="80%" align="center">
		<tr>
		    <td valign="bottom" class="tdFontcolor"><bean:message key="label.page.serial"/>
					<bean:write name="pagination" property="current" filter="true" />
					<bean:message key="label.page.sum"/>
					<bean:write name="pagination" property="count" filter="true" />
					<bean:message key="label.page.row"/>
					<bean:write name="pagination" property="pages" filter="true" />
					<bean:message key="label.page.page"/>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationdblink name="multMediaForm" property="pagination" nameId="multMediaForm" scope="page">
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
