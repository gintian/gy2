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
<script language="javascript">

</script>
<html:form action="/hire/zp_employ/search_hire_employee">

<br>
<table width="60%" border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable">
   	  <thead>
           <tr>  
           <td align="center" class="TableRow" nowrap>
		<bean:message key="column.select"/>&nbsp;
            </td>          
            <td align="center" class="TableRow" nowrap>
		<bean:message key="label.title.name"/>&nbsp;
	    </td>
	     <td align="center" class="TableRow" nowrap>
		<bean:message key="label.zp_filter.pos"/>&nbsp;
	    </td>
	    <td align="center" class="TableRow" nowrap>
		<bean:message key="label.zp_employ.status"/>&nbsp;
	    </td>	    		        	        	        
           </tr>
   	  </thead>
   	   <hrms:extenditerate id="element" name="zpEmployForm" property="zpEmployForm.list" indexes="indexes"  pagination="zpEmployForm.pagination" pageCount="10" scope="session">
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
	   	 <hrms:checkmultibox name="zpEmployForm" property="zpEmployForm.select" value="true" indexes="indexes"/>&nbsp;
	    </td>           
            <td align="left" class="RecordRow" nowrap>
                    <bean:write  name="element" property="a0101" filter="true"/>&nbsp;
	    </td>
            <td align="left" class="RecordRow" nowrap>
               <hrms:codetoname codeid="@K" name="element" codevalue="pos_id" codeitem="codeitem" scope="page"/>  	      
               <bean:write name="codeitem" property="codename" />&nbsp; 
	    </td> 
   	     <td align="left" class="RecordRow" nowrap>
   	        <logic:equal name="element" property="status" value="0">
                    <bean:message key="label.zp_employ.noinform"/>&nbsp;
                </logic:equal>
                <logic:equal name="element" property="status" value="1">
                    <bean:message key="label.zp_employ.inform"/>&nbsp;
                </logic:equal>
	    </td> 
               		        	        	        
          </tr>
        </hrms:extenditerate>    	
        
</table>
<table  width="60%" align="center">
		<tr>
		    <td valign="bottom" class="tdFontcolor"><bean:message key="label.page.serial"/>
					<bean:write name="zpEmployForm" property="zpEmployForm.pagination.current" filter="true" />
				<bean:message key="label.page.sum"/>
					<bean:write name="zpEmployForm" property="zpEmployForm.pagination.count" filter="true" />
		<bean:message key="label.page.row"/>
					<bean:write name="zpEmployForm" property="zpEmployForm.pagination.pages" filter="true" />
					<bean:message key="label.page.page"/>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationlink name="zpEmployForm" property="zpEmployForm.pagination"
				nameId="zpEmployForm" propertyId="zpresourceProperty">
				</hrms:paginationlink>
			</td>
		</tr>
</table>
<table  width="60%" align="center">
          <tr>
   	     <td>
   	         <bean:message key="button.law_base.inputbase"/>
   	          <hrms:importgeneraldata showColumn="DBName" valueColumn="Pre" flag="false"  paraValue="${zpEmployForm.dbpre}"
                      sql="select pre,dbname from dbname where pre != ?" collection="list" scope="page"/> 
            	     <html:select name="zpEmployForm" property="userBase" size="1"> 
            	         <html:options collection="list" property="dataValue" labelProperty="dataName"/> 
            	     </html:select>
            	 <hrms:submit styleClass="mybutton" property="b_ok" onclick="return ifyiku()"><bean:message key="button.ok"/></hrms:submit>
            	 <hrms:submit styleClass="mybutton" property="b_email"><bean:message key="label.zp_employ.emailnotify"/></hrms:submit>
            	<!-- <hrms:submit styleClass="mybutton" property="b_message"><bean:message key="label.zp_employ.messagenotify"/></hrms:submit>-->
            </td>
              
   	  </tr>      
</table>

</html:form>
