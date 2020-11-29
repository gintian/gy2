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
function exeReturn(returnStr,target)
{
  target_url=returnStr;
  window.open(target_url,target); 
}
</script>
<hrms:themes></hrms:themes>
<html:form action="/general/deci/statics/employmakeupanalysebrowse">
<br>
<br>
 <html:hidden name="makeupAnalyseForm" property="setname"/> 
<table width="80%" border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable">
         
   	  <thead>
           <tr>   
             <td align="center" class="TableRow" nowrap>
                <bean:message key="conlumn.mediainfo.info_title"/>&nbsp;
             </td>
             <td align="center" class="TableRow" nowrap>
                <bean:message key="conlumn.mediainfo.info_sort"/>&nbsp;
             </td>           		        	        	        
           </tr>
   	  </thead>
          <hrms:extenditerate id="element" name="makeupAnalyseForm" property="analyseForm.list" indexes="indexes"  pagination="analyseForm.pagination" pageCount="10" scope="session">
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
               <a href="/workbench/media/showmediainfo?usertable=${makeupAnalyseForm.dbpre}A00&usernumber=<bean:write  name="element" property="a0100" filter="true"/>&i9999=<bean:write  name="element" property="i9999" filter="true"/>" target="_blank"><bean:write  name="element" property="title" filter="true"/></a>&nbsp;
            </td>
             <td align="left" class="RecordRow" nowrap>                
               <bean:write  name="element" property="flag" filter="true"/>&nbsp;
            </td>                 	                           	    		        	        	        
          </tr>
        </hrms:extenditerate>
         
</table>
<table  width="70%" align="center">
		<tr>
		    <td valign="bottom" class="tdFontcolor">第
					<bean:write name="makeupAnalyseForm" property="analyseForm.pagination.current" filter="true" />
					页
					共
					<bean:write name="makeupAnalyseForm" property="analyseForm.pagination.count" filter="true" />
					条
					共
					<bean:write name="makeupAnalyseForm" property="analyseForm.pagination.pages" filter="true" />
					页
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationlink name="makeupAnalyseForm" property="analyseForm.pagination"
				nameId="makeupAnalyseForm" propertyId="roleListProperty">
				</hrms:paginationlink>
			</td>
		</tr>
</table>
<table width="80%" border="0">
  <tr>
   <td align="center">     
      <logic:equal name="makeupAnalyseForm" property="returnphoto" value="0">
          <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/general/deci/statics/employmakeupanalysebrowse.do?br_return=link','il_body')">                 
      </logic:equal>
       <logic:equal name="makeupAnalyseForm" property="returnphoto" value="1">
          <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeReturn('/general/deci/statics/employmakeupanalysebrowse.do?br_photo=link','il_body')">                 
      </logic:equal>
   </td>
  </tr>
 </table>  
</html:form>
