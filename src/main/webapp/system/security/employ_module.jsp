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
<html:form action="/system/security/employ_module">
   <table width="70%" border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable">
   	  <thead>
           <tr>
            <td align="center" class="TableRow" nowrap>
             <hrms:fieldtoname name="aboutForm" fieldname="B0110" fielditem="fielditem"/>
	     <bean:write name="fielditem" property="dataValue" />&nbsp;
            </td>           
            <td align="center" class="TableRow" nowrap>
             <hrms:fieldtoname name="aboutForm" fieldname="E0122" fielditem="fielditem"/>
	     <bean:write name="fielditem" property="dataValue" />&nbsp;
	    </td>
            <td align="center" class="TableRow" nowrap>
             <hrms:fieldtoname name="aboutForm" fieldname="E01A1" fielditem="fielditem"/>
	     <bean:write name="fielditem" property="dataValue" />&nbsp;
	    </td>
            <td align="center" class="TableRow" nowrap>
             <hrms:fieldtoname name="aboutForm" fieldname="A0101" fielditem="fielditem"/>
	     <bean:write name="fielditem" property="dataValue" />&nbsp;          	
	    </td>

            <logic:iterate id="element"   name="aboutForm"  property="fieldlist" indexId="index">
               <td align="center" class="TableRow" nowrap>
                 <bean:write  name="element" property="itemdesc" filter="true"/>
	      	   </td> 
            </logic:iterate> 	    
          </tr>
   	  </thead>
   	  <!--
   	  order by b0110,e0122,e01a1,A0000 ,top union 出错，暂时不加排序
   	  -->
      <hrms:paginationdb id="element" name="aboutForm" sql_str="aboutForm.strsql" table="" where_str="" columns="aboutForm.columns" order_by="" page_id="pagination" pagerows="14" distinct="" keys="a0100" indexes="indexes">
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
                  <bean:write name="element" property="a0101" filter="true"/>        	
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
