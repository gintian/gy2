<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<hrms:themes></hrms:themes>
<html:form action="/hire/zp_options/poslist">
<table width="70%" border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable">
   	  <thead>
           <tr>
            <td align="center" class="TableRow" nowrap>
		<bean:message key="label.sys.code"/>            	
	    </td>	    
            <td align="center" class="TableRow" nowrap>
		<bean:message key="label.sys.codename"/>            	
	    </td>	    
            <td align="center" class="TableRow" nowrap>
		<bean:message key="label.zp_options.test_question"/>            	
	    </td>	    
           </tr>
   	  </thead>
           <hrms:paginationdb id="element" name="infoMlrframeForm" sql_str="${infoMlrframeForm.sql_str}" table="" where_str="infoMlrframeForm.cond_str" columns="codeitemid,codeitemdesc" page_id="pagination" indexes="sss">
	    <tr>
            <td align="left" class="RecordRow" nowrap>
		<bean:write name="element" property="codeitemid" filter="true"/>&nbsp;
	    </td>            
            <td align="left" class="RecordRow" nowrap>
		<bean:write name="element" property="codeitemdesc" filter="true"/>&nbsp;            
	    </td>

            <td align="center" class="RecordRow" nowrap>
		<a href="/hire/zp_options/informationlist.do?b_query=link&a_posid=<bean:write name="element" property="string(codeitemid)" filter="true"/>"><img src="/images/role_assign.gif" border=0></a>            	
	    </td>	    
	    	    	    		        	        	        
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
		          <p align="right"><hrms:paginationdblink name="infoMlrframeForm" property="pagination" nameId="infoMlrframeForm" scope="page">
				</hrms:paginationdblink>
			</td>
		</tr>
</table>

</html:form>
