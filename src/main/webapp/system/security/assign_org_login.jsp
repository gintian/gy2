<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@page import="com.hrms.frame.dao.RecordVo"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%int i=0;%>
<html:form action="/system/security/assign_org_login">
<table width="70%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable" style="margin-top:7px;">
   	  <thead>
           <tr>
            <td align="center" class="TableRow" nowrap>
		<bean:message key="label.sys.code"/>            	
	    </td>	    
            <td align="center" class="TableRow" nowrap>
		<bean:message key="label.sys.codename"/>            	
	    </td>	    
            <td align="center" class="TableRow" nowrap>
		<bean:message key="label.role.assign"/>            	
	    </td>	    
           </tr>
   	  </thead>
          <hrms:paginationdb id="element" name="accountForm" sql_str="${accountForm.sql_str}" table="organization" where_str="accountForm.cond_str" order_by=" order by a0000" columns="${accountForm.columns}" page_id="pagination" indexes="sss">
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
		&nbsp;<bean:write name="element" property="string(codeitemid)" filter="true"/>&nbsp;
	    </td>            
            <td align="left" class="RecordRow" nowrap>
		&nbsp;<bean:write name="element" property="string(codeitemdesc)" filter="true"/>&nbsp;            
	    </td>
		<%
			RecordVo vo = (RecordVo)pageContext.getAttribute("element");
			String codeitemid = vo.getString("codeitemid");
		 %>
            <td align="center" class="RecordRow" nowrap>
		<a href="/system/security/assign_role_org.do?b_query=link&encryptParam=<%=PubFunc.encrypt("ret_ctrl=0&a_userflag=2&a_roleid="+codeitemid) %>"><img src="/images/role_assign.gif" border=0></a>            	
	    </td>	    
	    	    	    		        	        	        
          </tr>
        </hrms:paginationdb>
        
</table>
<table  width="70%" align="center" class="RecordRowP">
		<tr>
		    <td valign="bottom" class="tdFontcolor">
		            <bean:message key="label.page.serial"/>
					<bean:write name="pagination" property="current" filter="true" />
					<bean:message key="label.page.sum"/>
					<bean:write name="pagination" property="count" filter="true" />
					<bean:message key="label.page.row"/>
					<bean:write name="pagination" property="pages" filter="true" />
					<bean:message key="label.page.page"/>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationdblink name="accountForm" property="pagination" nameId="accountForm" scope="page">
				</hrms:paginationdblink>
			</td>
		</tr>
</table>

</html:form>
