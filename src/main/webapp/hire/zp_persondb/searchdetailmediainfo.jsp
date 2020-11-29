<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
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
<%
	  int i=0;
%>

<script language="javascript">

  function goback()
  {
  		zppersondbForm.action="/templates/menu/hire_m_menu2.do?b_query=link&module=7";
  		zppersondbForm.target="i_body";
  		zppersondbForm.submit();
  	
  }
</script>


<link rel="stylesheet" href="<%=css_url%>" type="text/css">
<hrms:themes></hrms:themes>
<html:form action="/hire/zp_persondb/searchdetailenrollinfo">
<br>
<br>
<table width="80%" border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable">
         
   	  <thead>
           <tr>
            <td align="center" class="TableRow" nowrap>
              <bean:message key="column.select"/>&nbsp;
             </td>
           <!-- <td align="center" class="TableRow" nowrap>
               <bean:message key="conlumn.mediainfo.info_id"/>&nbsp;
             </td>-->            	        
             <td align="center" class="TableRow" nowrap>
                <bean:message key="conlumn.mediainfo.info_title"/>&nbsp;
             </td>
             <td align="center" class="TableRow" nowrap>
                <bean:message key="conlumn.mediainfo.info_sort"/>&nbsp;
             </td>           		        	        	        
           </tr>
   	  </thead>
          <hrms:extenditerate id="element" name="zppersondbForm" property="zppersondbForm.list" indexes="indexes"  pagination="zppersondbForm.pagination" pageCount="10" scope="session">
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
               <hrms:checkmultibox name="zppersondbForm" property="zppersondbForm.select" value="true" indexes="indexes"/>&nbsp;
            </td>           
            <td align="left" class="RecordRow" nowrap>                
              <a href="/workbench/media/showmediainfo?usertable=${zppersondbForm.userbase}A00&usernumber=<bean:write  name="element" property="a0100" filter="true"/>&i9999=<bean:write  name="element" property="i9999" filter="true"/>" target="_blank"><bean:write  name="element" property="title" filter="true"/></a>&nbsp;
            </td>
             <td align="left" class="RecordRow" nowrap>                
               <bean:write  name="element" property="flag" filter="true"/>&nbsp;
            </td>                 	                           	    		        	        	        
          </tr>
        </hrms:extenditerate>
         
</table>
<table  width="70%" align="center">
		<tr>
		    <td valign="bottom" class="tdFontcolor"><bean:message key="label.page.serial"/>
					<bean:write name="zppersondbForm" property="zppersondbForm.pagination.current" filter="true" />
					<bean:message key="label.page.sum"/>
					<bean:write name="zppersondbForm" property="zppersondbForm.pagination.count" filter="true" />
				<bean:message key="label.page.row"/>
					<bean:write name="zppersondbForm" property="zppersondbForm.pagination.pages" filter="true" />
					<bean:message key="label.page.page"/>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationlink name="zppersondbForm" property="zppersondbForm.pagination"
				nameId="zppersondbForm" propertyId="roleListProperty">
				</hrms:paginationlink>
			</td>
		</tr>
</table>
<table  width="70%" align="left">
          <tr>
            <td align="center">
              	   <hrms:submit styleClass="mybutton" property="br_add">
            		<bean:message key="button.insert"/>
	 	   </hrms:submit>  
         	   <hrms:submit styleClass="mybutton" property="b_deletemedia">
            		 <bean:message key="button.delete"/>
	 	   </hrms:submit> 
	 	    <logic:equal name="zppersondbForm" property="isHandWork" value="1">
	          		<input type='button' value="返回" onclick="goback()"  class="mybutton" />
	   		 </logic:equal>
	 	      
	 	   
	     </td>
          </tr>          
 </table>
</html:form>
