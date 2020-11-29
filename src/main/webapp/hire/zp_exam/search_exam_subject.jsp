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

<html:form action="/hire/zp_exam/search_exam_subject">

<br>
<table width="70%" border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable">
   	  <thead>
           <tr>
            <td align="center" class="TableRow" nowrap>
		<bean:message key="column.select"/>&nbsp;
            </td>           
            <td align="center" class="TableRow" nowrap>
		<bean:message key="label.zp_exam.subject_id"/>
	    </td>
            <td width="10%" align="center" class="TableRow" nowrap>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		<bean:message key="label.zp_employ.subject_name"/> 
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;           	
	    </td>	          
            <td width="10%" align="center" class="TableRow" nowrap>
		<bean:message key="label.edit"/>            	
	    </td>		    		        	        	        
           </tr>
   	  </thead>
   	   <hrms:extenditerate id="element" name="zpExamForm" property="zpExamForm.list" indexes="indexes"  pagination="zpExamForm.pagination" pageCount="10" scope="session">
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
                   <hrms:checkmultibox name="zpExamForm" property="zpExamForm.select" value="true" indexes="indexes"/>&nbsp;  		   
	    </td>            
	    <td align="right" class="RecordRow" nowrap>
                   <bean:write name="element" property="string(subject_id)" filter="true"/>&nbsp;
	    </td>
	    <td align="left" class="RecordRow" nowrap>
                   <bean:write name="element" property="string(subject_name)" filter="true"/>&nbsp;
	    </td>
	    <td align="center" class="RecordRow" nowrap>
                <a href="/hire/zp_exam/add_exam_subject.do?b_query=link&a_id=<bean:write name="element" property="string(subject_id)" filter="true"/>"><img src="/images/edit.gif" border=0></a>            	
	   </td>
       </tr>
       </hrms:extenditerate>   	    	    	
        
</table>
<table  width="70%" align="center">
		<tr>
		    <td valign="bottom" class="tdFontcolor"><bean:message key="label.page.serial"/>
					<bean:write name="zpExamForm" property="zpExamForm.pagination.current" filter="true" />
				<bean:message key="label.page.sum"/>
					<bean:write name="zpExamForm" property="zpExamForm.pagination.count" filter="true" />
				<bean:message key="label.page.row"/>
					<bean:write name="zpExamForm" property="zpExamForm.pagination.pages" filter="true" />
				<bean:message key="label.page.page"/>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationlink name="zpExamForm" property="zpExamForm.pagination"
				nameId="zpExamForm" propertyId="zpExamProperty">
				</hrms:paginationlink>
			</td>
		</tr>
</table>
<table  width="70%" align="center">
          <tr>
            <td align="center">
         	<hrms:submit styleClass="mybutton" property="b_add">
            		<bean:message key="button.insert"/>
	 	</hrms:submit>
         	<hrms:submit styleClass="mybutton" property="b_delete" onclick="return ifdel()">
            		<bean:message key="button.delete"/>
	 	</hrms:submit>
        
            </td>
          </tr>          
</table>

</html:form>
