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
function exeReturn(returnStr,target)
{
  target_url=returnStr;
  window.open(target_url,target); 
}
</script>
<hrms:themes></hrms:themes>
<html:form action="/general/deci/statics/employmakeupanalysebrowse" >

<%
	int i=0;
%>
<table width="80%" border="0" cellspacing="1"  align="center" cellpadding="1">
    <tr>
           <td align="left"  nowrap>
              <br>
                (<bean:message key="label.title.org"/>: <bean:write  name="makeupAnalyseForm" property="b0110" filter="true"/>&nbsp;
                <bean:message key="label.title.dept"/>: <bean:write  name="makeupAnalyseForm" property="e0122" filter="true"/>&nbsp;
                <bean:message key="label.title.name"/>: <bean:write  name="makeupAnalyseForm" property="a0101" filter="true"/>&nbsp;
                 )
              </td>
          </tr>
</table>
<table width="100%" border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable">
   	  
   	  <thead>
           <tr>
             <logic:iterate id="element"    name="makeupAnalyseForm"  property="infodetailfieldlist"> 
              <td align="center" class="TableRow" nowrap>
                 <bean:write  name="element" property="itemdesc"/>&nbsp; 
              </td>
             </logic:iterate>         	        
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
           
           <logic:iterate id="info"    name="makeupAnalyseForm"  property="infodetailfieldlist">   
             <logic:notEqual  name="info" property="itemtype" value="N">               
               <td align="left" class="RecordRow" nowrap>        
             </logic:notEqual>
              <logic:equal  name="info" property="itemtype" value="N">               
                <td align="right" class="RecordRow" nowrap>        
              </logic:equal>           
                   <bean:write  name="element" property="string(${info.itemid})" filter="true"/>&nbsp;
              </td>
             </logic:iterate>             	                           	    		        	        	        
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
   <td align="left">
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
