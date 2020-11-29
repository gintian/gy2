<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*"%>


<html:form action="/selfservice/infomanager/askinv/additem">
      <table width="500" border="0" cellpadding="0" cellspacing="0" align="center">
          <tr height="20">
       		<!--  <td width=10 valign="top" class="tableft"></td>
       		<td width=130 align=center class="tabcenter">&nbsp;<bean:message key="lable.investigate_item.repair"/>&nbsp;</td>
       		<td width=10 valign="top" class="tabright"></td>
       		<td valign="top" class="tabremain" width="500"></td>--> 
       		<td align=center class="TableRow">&nbsp;<bean:message key="lable.investigate_item.repair"/>&nbsp;</td>             	      
          </tr> 
          <tr>
            <td  class="framestyle9">
               <table border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0"> 
                     <tr class="list3"><td align="right" nowrap valign="top"><bean:message key="conlumn.investigate.content"/>:</td>
                     <td>
                      <bean:write name="itemForm" property="content" filter="true"/>&nbsp;
                     
                     </td> </tr>   
                      <tr class="list3">
                	      <td align="right" nowrap valign="top"> <bean:message key="conlumn.investigate_item.name"/>:</td>
                	      <td align="left"  nowrap>
                	      	<html:text name="itemForm" property="itemvo.string(name)"/>
                          </td>
                      </tr> 
                      
                 </table>     
              </td>
          </tr>                                                   
          <tr class="list3">
            <td align="center" style="height:35px;">
               <table><tr><td>
         	<hrms:submit styleClass="mybutton" property="b_save" onclick="document.itemForm.target='_self';validate( 'R','itemvo.string(name)','项目名称');return (document.returnValue && ifqrbc());">
            		<bean:message key="button.save"/>
	 	</hrms:submit>
	 	</td>
	 	<td>
		<html:reset styleClass="mybutton" property="reset"><bean:message key="button.clear"/></html:reset>
		</td>
		<td>	 	
         	&nbsp; 
	 	</td>
	 	</tr>
	 	</table>          
            </td>
          </tr>          
      </table>
</html:form>
<%
	int i=0;
%>
<html:form action="/selfservice/infomanager/askinv/searchitem">
<table width="70%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">

   	  <thead>
   	  <tr><td align="center" valign="center" nowrap colspan="10"><h3><bean:message key="conlumn.investigate_item.maintopic"/></h3></td></tr>
           <tr>
            <td align="center" class="TableRow" nowrap>
		<bean:message key="column.select"/>&nbsp;
            </td>           
            <td align="center" class="TableRow" nowrap>
		<bean:message key="conlumn.investigate.content"/>&nbsp;
	    </td>
            <td align="center" class="TableRow" nowrap>
		<bean:message key="conlumn.investigate_item.name"/>&nbsp;
	    </td>
            
            
	   	    	    	    
           
            <td align="center" class="TableRow" nowrap>
		<bean:message key="label.edit"/>            	
	    </td>
	      <td align="center" class="TableRow" nowrap>
		<bean:message key="conlumn.investigate_item.additem"/>&nbsp;
	    </td>
           	    	    		        	        	        
           </tr>
   	  </thead>
   	  
          <hrms:extenditerate id="element" name="itemForm" property="itemForm.list" indexes="indexes"  pagination="itemForm.pagination" pageCount="10" scope="session">
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
     		   <hrms:checkmultibox name="itemForm" property="itemForm.select" value="true" indexes="indexes"/>&nbsp;
	    </td>            
            <td align="left" class="RecordRow" nowrap>
                  <bean:write name="itemForm"  property="content" />&nbsp; 
	    </td>
         
            <td align="left" class="RecordRow" nowrap>
                  <bean:write name="element" property="string(name)" filter="true"/>&nbsp;
	    </td>
           
	  
           
            <td align="center" class="RecordRow" nowrap>
            	<a href="/selfservice/infomanager/askinv/additem.do?b_query=link&itemid=<bean:write name="element" property="string(itemid)" filter="true"/>"><img src="/images/edit.gif" border=0></a>
	    </td>
	      <td align="center" class="RecordRow" nowrap>
            	<a href="/selfservice/infomanager/askinv/addoutline.do?b_addquery=link&itemid=<bean:write name="element" property="string(itemid)" filter="true"/>&itemName=<bean:write name="element" property="string(name)" filter="true"/>" ><img src="/images/edit.gif" border=0></a>
	    </td>
           	    	    		        	        	        
          </tr>
        </hrms:extenditerate>
        
</table>

<table  width="70%" align="center" class="RecordRowP">
		<tr>
		    <td valign="bottom" class="tdFontcolor">
				<bean:message key="label.page.serial" />
					<bean:write name="itemForm" property="itemForm.pagination.current" filter="true" />
				<bean:message key="label.page.sum" />
					<bean:write name="itemForm" property="itemForm.pagination.count" filter="true" />
				<bean:message key="label.page.row" />
					<bean:write name="itemForm" property="itemForm.pagination.pages" filter="true" />
				<bean:message key="label.page.page" />
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationlink name="itemForm" property="itemForm.pagination"
				nameId="itemForm" propertyId="roleListProperty">
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
         	<hrms:submit styleClass="mybutton" property="b_delete" onclick="document.itemForm.target='_self';validate( 'R','itemvo.string(name)','项目名称');return (document.returnValue && ifqrbc());">
            		<bean:message key="button.delete"/>
	 	</hrms:submit>
	 	<hrms:submit styleClass="mybutton" property="br_return">
            		<bean:message key="button.return"/>
	 	</hrms:submit>  
        
            </td>
           
          </tr>          
</table>

</html:form>

