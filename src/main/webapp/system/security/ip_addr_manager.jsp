<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@page import="com.hrms.frame.dao.RecordVo"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%
	int i=0;
%>

<style id=iframeCss>
div{
	cursor:hand;font-size:12px;
   }
a{
text-decoration:none;color:black;font-size:12px;
}

a.a1:active {
	color: #003100;
	text-decoration: none;
}
a.a1:hover {
	color: #FFCC00;
	text-decoration: none;
}
a.a1:visited {	
	text-decoration: none;
}
a.a1:link {
	color: #003100;
	text-decoration: none;
}
</style>

<html:form action="/system/security/ip_addr_manager">
<table width="60%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable" style="margin-top:6px;">
   	  <thead>
           <tr>
            <td align="center" class="TableRow" nowrap width="10%">
			<input type="checkbox" name="selbox" onclick="batch_select(this,'iplistform.select');" title='<bean:message key="label.query.selectall"/>'>&nbsp;
	    </td>         
            <td align="center" class="TableRow" nowrap width="20%">
		<bean:message key="column.sys.ip_addr"/>&nbsp;
	    </td>
            <td align="center" class="TableRow" nowrap width="60%">
		<bean:message key="column.sys.description"/>&nbsp;
	    </td>
            <td align="center" class="TableRow" nowrap >
		<bean:message key="column.sys.status"/>&nbsp;
            </td>
            <td align="center" class="TableRow" nowrap >
		<bean:message key="label.edit"/>&nbsp;
            </td>                  		        	        	        
           </tr>
   	  </thead>
          <hrms:extenditerate id="element" name="ipaddrForm" property="iplistform.list" indexes="indexes"  pagination="iplistform.pagination" pageCount="10" scope="session">
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
            <td align="center" class="RecordRow" nowrap>
   		<hrms:checkmultibox name="ipaddrForm" property="iplistform.select" value="true" indexes="indexes"/>&nbsp;
	    </td>            
            <td align="left" class="RecordRow" nowrap>
                   &nbsp;<bean:write name="element" property="string(ip_addr)" filter="true"/>&nbsp;
	    </td>
         
            <td align="left" class="RecordRow" wrap>
                    &nbsp;<bean:write  name="element" property="string(description)" filter="true"/>&nbsp;
            </td>
           <!-- <td align="center" class="RecordRow" nowrap>
                 <logic:equal name="element" property="string(valid)" value="1">
     		   <bean:message key="column.sys.invalid"/>&nbsp;
     		 </logic:equal>  
                 <logic:equal name="element" property="string(valid)" value="0">
     		   <bean:message key="column.sys.valid"/>&nbsp;
            	 </logic:equal>             	    		   
	    </td>   --> 
         
           <!-- 修改key isvalid 允许访问  novalid 不允许访问 changxy 20160523 -->  
            <td align="center" class="RecordRow" nowrap>
                 <logic:equal name="element" property="string(valid)" value="1">
     		   
     		   <bean:message key="column.sys.novalid"/>&nbsp;
     		 </logic:equal>  
                 <logic:equal name="element" property="string(valid)" value="0">
     		   <bean:message key="column.sys.isvalid"/>&nbsp;
            	 </logic:equal>             	    		   
	    </td>  
	    <%
	    	RecordVo vo = (RecordVo)pageContext.getAttribute("element");
	    	String id = vo.getString("id");
	     %> 
            <td align="center" class="RecordRow" nowrap>
            	<a href="/system/security/ip_addr_maintenance.do?b_query=link&encryptParam=<%=PubFunc.encrypt("a_ip_id="+id)%>"><img src="/images/edit.gif" border=0></a>
	    </td>
	                
          </tr>
        </hrms:extenditerate>
        
</table>
<table  width="60%" align="center" class="RecordRowP">
		<tr>
		    <td valign="bottom" class="tdFontcolor">
		            <bean:message key="label.page.serial"/>
					<bean:write name="ipaddrForm" property="iplistform.pagination.current" filter="true" />
					<bean:message key="label.page.sum"/>
					<bean:write name="ipaddrForm" property="iplistform.pagination.count" filter="true" />
					<bean:message key="label.page.row"/>
					<bean:write name="ipaddrForm" property="iplistform.pagination.pages" filter="true" />
					<bean:message key="label.page.page"/>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationlink name="ipaddrForm" property="iplistform.pagination"
				nameId="iplistform" propertyId="roleListProperty">
				</hrms:paginationlink>
			</td>
		</tr>
</table>
<table  width="50%" align="center">
          <tr>
            <td align="center" height="35px;">
         	<hrms:submit styleClass="mybutton" property="b_add">
            		<bean:message key="button.insert"/>
	 	</hrms:submit>
         	<hrms:submit styleClass="mybutton" property="b_delete" onclick="return ifdel();">
            		<bean:message key="button.delete"/>
	 	</hrms:submit>	 	
            </td>
          </tr>          
</table>

</html:form>
