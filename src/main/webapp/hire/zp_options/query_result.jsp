<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<hrms:themes></hrms:themes>
<html:form action="/hire/zp_options/query_result">
<br>
<table width="70%" border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable">
   	  <thead>
           <tr>
            <td align="center" class="TableRow" nowrap>
             <hrms:fieldtoname name="posFilterSetForm" fieldname="A0101" fielditem="fielditem"/>
	     <bean:write name="fielditem" property="dataValue" />&nbsp;
            </td>                  
            <logic:iterate id="element"    name="posFilterSetForm"  property="resultlist" indexId="index">
              <logic:notEqual name="element" property="priv_status" value="0">               
              <td align="center" class="TableRow" nowrap>
                 <bean:write  name="element" property="itemdesc" filter="true"/>
	      </td>  
	      </logic:notEqual>	                           
            </logic:iterate>     
    	    	    		        	        	        
           </tr>
   	  </thead>
          <hrms:paginationdb id="element" name="posFilterSetForm" sql_str="posFilterSetForm.strsql" table="" where_str="posFilterSetForm.strwhere" columns="posFilterSetForm.columns" order_by=" order by A0000" page_id="pagination" pagerows="21">
	    <tr>
            <td align="left" class="RecordRow" nowrap>
		 <a href="/workbench/browse/showselfinfo.do?b_search=link&userbase=<bean:write name="posFilterSetForm" property="dbpre" filter="true"/>&a0100=<bean:write name="element" property="a0100" filter="true"/>&flag=notself&returnvalue=4"><bean:write name="element" property="a0101" filter="true"/></a>            	
	    </td>
            <logic:iterate id="fielditem"  name="posFilterSetForm"  property="resultlist" indexId="index">
              <logic:notEqual name="fielditem" property="priv_status" value="0">             
              <td align="left" class="RecordRow" nowrap>
                 <logic:notEqual name="fielditem" property="codesetid" value="0">
          	   <hrms:codetoname codeid="${fielditem.codesetid}" name="element" codevalue="${fielditem.itemid}" codeitem="codeitem" scope="page"/>  	      
          	   <bean:write name="codeitem" property="codename" />&nbsp;                    
                 </logic:notEqual>
                 <logic:equal name="fielditem" property="codesetid" value="0">
                   <bean:write name="element" property="${fielditem.itemid}" filter="false"/>&nbsp;                 
                 </logic:equal>                                
	      </td>   
	      </logic:notEqual>	 	                          
            </logic:iterate> 	    	    	    		        	        	        
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
		          <p align="right"><hrms:paginationdblink name="posFilterSetForm" property="pagination" nameId="posFilterSetForm" scope="page">
				</hrms:paginationdblink>
			</td>
		</tr>
</table>
<table  width="70%" align="center">
          <tr>
            <td align="left">  
                    <hrms:submit styleClass="mybutton" property="b_view_photo">
            		<bean:message key="button.query.viewphoto"/>
	 	    </hrms:submit>        
         	    <hrms:submit styleClass="mybutton" property="br_return">
            		<bean:message key="button.return"/>
	 	    </hrms:submit>
            </td>
          </tr>          
</table>
</html:form>
