<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.hjsj.sys.FieldItem"%>
<%
	int i=0;
	String name=null;
%>
<html:form action="/system/security/operuser_module">
   <table width="70%" border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable">
   	  <thead>
         <tr>
            <td align="center" class="TableRow" nowrap>
                <bean:message key="label.mail.username"/>&nbsp;          	
 	        </td>
            <logic:iterate id="element"   name="aboutForm"  property="fieldlist" indexId="index">
               <td align="center" class="TableRow" nowrap>
                 <bean:write  name="element" property="itemdesc" filter="true"/>
	      	   </td> 
            </logic:iterate> 	    
          </tr>
   	  </thead>
      <hrms:paginationdb id="element" name="aboutForm" sql_str="aboutForm.strsql" table="" where_str=" from operuser where roleid=0" columns="aboutForm.columns" order_by=" order by ingrporder" page_id="pagination" pagerows="21" distinct="" keys="username" indexes="indexes">
	    <tr>
            <td align="left" class="RecordRow" nowrap>
                 <bean:write name="element" property="username" filter="false"/>&nbsp;              
	        </td> 
            <logic:iterate id="fielditem"  name="aboutForm"  property="fieldlist" indexId="index">
            <%
            	FieldItem item=(FieldItem)pageContext.getAttribute("fielditem");
            	name=item.getItemid();
            %>
              <td align="center" class="RecordRow" nowrap>
              	<html:checkbox name="aboutForm" property='<%="pagination.curr_page_list["+i+"]."+name%>' value="1"></html:checkbox>              
	          </td> 
            </logic:iterate>                
	        <%i++;%>        	        	        
          </tr>
      </hrms:paginationdb>

   </table>
<table  width="70%" align="center">
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
		          <p align="right"><hrms:paginationdblink name="aboutForm" property="pagination" nameId="aboutForm" scope="page">
				</hrms:paginationdblink>
			</td>
		</tr>
</table>
<table  width="70%" align="center">
          <tr>
            <td align="left">
         	<hrms:submit styleClass="mybutton" property="b_save">
            		<bean:message key="button.save"/>
	 		</hrms:submit>
            </td>
          </tr>          
</table>
</html:form>
