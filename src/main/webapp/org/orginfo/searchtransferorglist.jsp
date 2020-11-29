<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
	  int i=0;
	    String userName = null;
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null){
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	 css_url="/css/css1.css";
          //out.println("<link href='"+css_url+"' rel='stylesheet' type='text/css' >");  	 
	}
    
%>
<hrms:themes></hrms:themes>
<html:form action="/org/orginfo/searchtransferorgtree">
<br>
<table  width="100%" border="0">
 <tr>
  <td width="60%">
    <table width="90%" border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable">
   	  <thead>
           <tr>
             <td align="center" class="TableRow" nowrap>
                <bean:message key="column.select"/>&nbsp;
             </td>
             <td align="center" class="TableRow" nowrap>
                <bean:message key="conlumn.codeitemdesc.caption"/>&nbsp;
             </td>                
           </tr>
   	  </thead>
          <hrms:extenditerate id="element" name="orgInformationForm" property="organizationForm.list" indexes="indexes"  pagination="organizationForm.pagination" pageCount="15" scope="session">
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
               <hrms:checkmultibox name="orgInformationForm" property="organizationForm.select" value="true" indexes="indexes"/>&nbsp;
            </td>
            <td align="left" class="RecordRow" nowrap>                
                <bean:write  name="element" property="string(codeitemdesc)" filter="true"/>&nbsp;
            </td> 
                           	    		        	        	        
          </tr>
        </hrms:extenditerate>
         
    </table>
    <table  width="100%" align="center" class="RecordRowP">
		<tr>
		    <td valign="bottom" class="tdFontcolor"><bean:message key="label.page.serial"/>
					<bean:write name="orgInformationForm" property="organizationForm.pagination.current" filter="true" />
					<bean:message key="label.page.sum"/>
					<bean:write name="orgInformationForm" property="organizationForm.pagination.count" filter="true" />
					<bean:message key="label.page.row"/>
					<bean:write name="orgInformationForm" property="organizationForm.pagination.pages" filter="true" />
					<bean:message key="label.page.page"/>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationlink name="orgInformationForm" property="organizationForm.pagination"
				nameId="organizationForm" propertyId="roleListProperty">
				</hrms:paginationlink>
			</td>
		</tr>
   </table>
  
  </td>
  <td width="4%">
     
  </td>
  <td width="36%">
     
  </td>
 </tr>
</table>
 <table  width="90%" align="center">
      <tr>
       <td align="center"  nowrap colspan="4">
           <hrms:submit styleClass="mybutton"  property="b_transfer">
                  <bean:message key="button.ok"/>
	   </hrms:submit> 
	   
	</td>	  
      </tr>
   </table>
</html:form>