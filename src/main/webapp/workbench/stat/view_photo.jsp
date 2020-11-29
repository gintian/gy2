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
function winhref(a0100,returnvalue,target)
{
   if(a0100=="")
      return false;
   var o_obj=document.getElementById('a0100');   
   if(o_obj)
     o_obj.value=a0100;
    statForm.action="/workbench/browse/showselfinfo.do?b_search=link&userbase=${statForm.userbase}&flag=notself&returnvalue="+returnvalue+"";
    statForm.target=target;
    statForm.submit();
}
function document.oncontextmenu() 
   { 
      return　false; 
   } 
</script>
<hrms:themes></hrms:themes>
<html:form action="/workbench/stat/view_photo">
<input type="hidden" name="a0100" id="a0100">
<table width="80%" border="0" cellspacing="1"  align="center" cellpadding="1">
          <hrms:paginationdb id="element" name="statForm" sql_str="statForm.strsql" table="" where_str="statForm.cond_str" columns="A0100,B0110,E0122,E01A1,A0101,UserName,"  order_by="statForm.order_by" pagerows="21" page_id="pagination">
          <%
          if(i%7==0)
          {
          %>
          <tr>
          <%
          }
          %>   
          <%
             	LazyDynaBean abean=(LazyDynaBean)pageContext.getAttribute("element");
             	String a0100=(String)abean.get("a0100"); 
                request.setAttribute("name",a0100);       	                           
          %>          
          <td align="center" NOWRAP>
          	
          	  <hrms:ole name="element" dbpre="statForm.userbase" a0100="a0100" scope="page" height="120" width="85" href="###" onclick="winhref('${name}','22','il_body');"/>

          	<br><a href="###" onclick="winhref('${name}','22','il_body');"><bean:write name="element" property="a0101" filter="true"/></a><br>
          	<hrms:codetoname codeid="UM" name="element" codevalue="e0122" codeitem="codeitem" scope="page"/>  	      
          	<a href="###" onclick="winhref('${name}','22','il_body');"><bean:write name="codeitem" property="codename" /></a>&nbsp;<br>  
          	<hrms:codetoname codeid="@K" name="element" codevalue="e01a1" codeitem="codeitem" scope="page"/>  	      
          	<a href="###" onclick="winhref('${name}','22','il_body');"><bean:write name="codeitem" property="codename" /></a>&nbsp;  
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
		          <p align="right"><hrms:paginationdblink name="statForm" property="pagination" nameId="statForm" scope="page">
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
