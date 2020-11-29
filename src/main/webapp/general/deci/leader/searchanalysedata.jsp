<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<hrms:themes></hrms:themes>
<script language="javascript">
	function back()
	{
		leaderForm.action ="/general/deci/leader/candi_stat.do";
		leaderForm.submit();
	}
</script>
<html:form action="/general/deci/leader/analysedata">
<br>
<table width="70%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
   	   <thead>
           <tr>
            <td align="center" class="TableRow" nowrap>
             <hrms:fieldtoname name="leaderForm" fieldname="B0110" fielditem="fielditem"/>
	     <bean:write name="fielditem" property="dataValue" />&nbsp;
            </td>           
            <td align="center" class="TableRow" nowrap>
             <hrms:fieldtoname name="leaderForm" fieldname="E0122" fielditem="fielditem"/>
	     <bean:write name="fielditem" property="dataValue" />&nbsp;
	    </td>
            <td align="center" class="TableRow" nowrap>
             <hrms:fieldtoname name="leaderForm" fieldname="E01A1" fielditem="fielditem"/>
	     <bean:write name="fielditem" property="dataValue" />&nbsp;
	    </td>
            <td align="center" class="TableRow" nowrap>
             <hrms:fieldtoname name="leaderForm" fieldname="A0101" fielditem="fielditem"/>
	     <bean:write name="fielditem" property="dataValue" />&nbsp;   
	    </td>          	    	    	    		        	        	        
           </tr>
   	  </thead>
      <hrms:paginationdb id="element" name="leaderForm" sql_str="leaderForm.strsql" table="" where_str="leaderForm.cond_str" columns="A0100,B0110,E0122,E01A1,A0101,UserName," order_by="leaderForm.order_by" pagerows="21" page_id="pagination">
	    <tr>
            <td align="left" class="RecordRow" nowrap>
          	<hrms:codetoname codeid="UN" name="element" codevalue="b0110" codeitem="codeitem" scope="page"/>  	      
          	<bean:write name="codeitem" property="codename" />&nbsp;
	    </td>            
            <td align="left" class="RecordRow" nowrap>
          	<hrms:codetoname codeid="UM" name="element" codevalue="e0122" codeitem="codeitem" scope="page"/>  	      
          	<bean:write name="codeitem" property="codename" />&nbsp;            
	    </td>
            <td align="left" class="RecordRow" nowrap>
                <hrms:codetoname codeid="@K" name="element" codevalue="e01a1" codeitem="codeitem" scope="page"/>  	      
          	<bean:write name="codeitem" property="codename" />&nbsp;     
	    </td>
        <td align="left" class="RecordRow" nowrap>
        	<a href="/workbench/browse/showselfinfo.do?b_search=link&userbase=<bean:write name="leaderForm" property="dbpre" filter="true"/>&a0100=<bean:write name="element" property="a0100" filter="true"/>&flag=notself&returnvalue='1'" target="_blank"><bean:write name="element" property="a0101" filter="true"/></a>&nbsp;
	    </td>      	    	    		        	        	        
        </tr>
     </hrms:paginationdb>
     <table  width="70%" align="center"  class="RecordRowP">
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
	            <p align="right"><hrms:paginationdblink name="leaderForm" property="pagination" nameId="leaderForm" scope="page">
	    </hrms:paginationdblink>
	   </td>
	  </tr>
	</table>
     
<table  width="70%" align="center">
          <tr>
            <td align="center">
    	   <html:button styleClass="mybutton" property="se_all" onclick="back()">
  	       			<bean:message key="button.return"/>
  	       		 </html:button>
            </td>            
          </tr>          
</table>
        
</table>

</html:form>
