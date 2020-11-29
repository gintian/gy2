<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
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
<script language="javascript">
function exeReturn(returnStr)
{
  target_url=returnStr;
  window.open(target_url,'il_body'); 
}
</script>
<link rel="stylesheet" href="<%=css_url%>" type="text/css">
<hrms:themes></hrms:themes>
<html:form action="/hire/zp_filter/browsedetailinfo">
<%
	int i=0;
%>
<table width="100%" border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable">
   	  <thead>
           <tr>
             <logic:iterate id="element"    name="zpFilterForm"  property="zpfieldlist"> 
              <td align="center" class="TableRow" nowrap>
                 <bean:write  name="element" property="itemdesc"/>&nbsp; 
              </td>
             </logic:iterate>         	        
           </tr>
   	  </thead>
          <hrms:extenditerate id="element" name="zpFilterForm" property="zpBrowseForm.list" indexes="indexes"  pagination="zpBrowseForm.pagination" pageCount="10" scope="session">
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
             <logic:iterate id="info"    name="zpFilterForm"  property="zpfieldlist">            
              <td align="left" class="RecordRow" nowrap>                
                   <bean:write  name="element" property="${info.itemid}" filter="true"/>&nbsp;
              </td>
             </logic:iterate>             	                           	    		        	        	        
          </tr>
        </hrms:extenditerate>
         
</table>
<table  width="70%" align="center">
		<tr>
		    <td valign="bottom" class="tdFontcolor"><bean:message key="label.page.serial"/>
					<bean:write name="zpFilterForm" property="zpBrowseForm.pagination.current" filter="true" />
					<bean:message key="label.page.sum"/>
					<bean:write name="zpFilterForm" property="zpBrowseForm.pagination.count" filter="true" />
					<bean:message key="label.page.row"/>
					<bean:write name="zpFilterForm" property="zpBrowseForm.pagination.pages" filter="true" />
					<bean:message key="label.page.page"/>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationlink name="zpFilterForm" property="zpBrowseForm.pagination"
				nameId="zpBrowseForm" propertyId="roleListProperty">
				</hrms:paginationlink>
			</td>
		</tr>
</table>
<table width="80%" border="0">
  <tr>
   <td align="center">
           <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/hire/zp_filter/browsedetailinfo.do?br_return=browse','i_body')">              
    </td>
  </tr>
 </table> 
</html:form>
