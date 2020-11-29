<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<script language="javascript">
   function tache_change()
   {
      zpInterviewForm.action="/hire/zp_interview/interview_result.do?b_query=link";
      zpInterviewForm.submit();
   }
</script>

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
<html:form action="/hire/zp_interview/interview_result">

<br>
<table width="60%" border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable">
   	  <thead>
   	  <tr class="trShallow">
   	     <td align="left" nowrap colspan="4">
		 <bean:message key="label.zp_options.testprocess"/>
                   <hrms:importgeneraldata showColumn="name" valueColumn="plan_id" flag="true"  paraValue="1"
                      sql="select tache_id,name from zp_tache where 1=? " collection="list" scope="page"/> 
            	     <html:select name="zpInterviewForm" property="zptachevo.string(tache_id)" size="1" onchange="tache_change();"> 
            	         <html:options collection="list" property="dataValue" labelProperty="dataName"/> 
            	     </html:select>
            </td>  
   	  </tr>
           <tr>  
           <td align="center" class="TableRow" nowrap>
		<bean:message key="column.select"/>&nbsp;
            </td>          
            <td align="center" class="TableRow" nowrap>
		<bean:message key="label.title.name"/>&nbsp;
	    </td>
	     <td align="center" class="TableRow" nowrap>
		<bean:message key="label.zp_interview.apply_date"/>&nbsp;
	    </td>
            <td align="center" class="TableRow" nowrap>
		<bean:message key="label.zp_interview.interview_result"/>            	
	    </td> 	    		        	        	        
           </tr>
   	  </thead>
   	   <hrms:extenditerate id="element" name="zpInterviewForm" property="zpPosTacheForm.list" indexes="indexes"  pagination="zpPosTacheForm.pagination" pageCount="10" scope="session">
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
	   	 <hrms:checkmultibox name="zpInterviewForm" property="zpPosTacheForm.select" value="true" indexes="indexes"/>&nbsp;
	    </td>           
            <td align="left" class="RecordRow" nowrap>
                    <a href="/hire/zp_interview/interview_result.do?br_view=link&a0100=<bean:write  name="element" property="a0100" filter="true"/>" target="il_body"><bean:write  name="element" property="a0101" filter="true"/></a>&nbsp;
	    </td>
            <td align="right" class="RecordRow" nowrap>
                    <bean:write  name="element" property="apply_date" filter="true"/>&nbsp;
	    </td> 
	    </td>  
	   <td align="center" class="RecordRow" nowrap>
                <a href="/hire/zp_interview/record_result.do?b_query=link&a_a0100id=<bean:write name="element" property="a0100" filter="true"/>"><img src="/images/edit.gif" border=0></a>            	
	   </td>	   	    
               		        	        	        
          </tr>
        </hrms:extenditerate>    	
        
</table>
<table  width="60%" align="center">
		<tr>
		    <td valign="bottom" class="tdFontcolor"><bean:message key="label.page.serial"/>
					<bean:write name="zpInterviewForm" property="zpPosTacheForm.pagination.current" filter="true" />
				   <bean:message key="label.page.sum"/>
					<bean:write name="zpInterviewForm" property="zpPosTacheForm.pagination.count" filter="true" />
				   <bean:message key="label.page.row"/>
					<bean:write name="zpInterviewForm" property="zpPosTacheForm.pagination.pages" filter="true" />
					<bean:message key="label.page.page"/>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationlink name="zpInterviewForm" property="zpPosTacheForm.pagination"
				nameId="zpPosTacheForm" propertyId="zpresourceProperty">
				</hrms:paginationlink>
			</td>
		</tr>
</table>
<table  width="60%" align="center">
          <tr>
   	     <td>
		 <bean:message key="label.zp_interview.moveto"/>
                   <hrms:importgeneraldata showColumn="name" valueColumn="plan_id" flag="true"  paraValue="1"
                      sql="select tache_id,name from zp_tache where 1=? " collection="list" scope="page"/> 
            	     <html:select name="zpInterviewForm" property="zpPosTachevo.string(tache_id)" size="1"> 
            	         <html:options collection="list" property="dataValue" labelProperty="dataName"/> 
            	     </html:select>
            	     <hrms:submit styleClass="mybutton" property="b_ok" onclick="return ifmshj()"><bean:message key="button.ok"/></hrms:submit>
            	     <hrms:submit styleClass="mybutton" property="b_del" onclick="return ifmsdel()"><bean:message key="button.delete"/></hrms:submit>
            	     <input type="button" name="btnreturn" value="<bean:message key="button.return"/>" onclick="history.back();" class="mybutton">
            </td>
              
   	  </tr>      
</table>

</html:form>
