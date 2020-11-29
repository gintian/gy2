<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>
<hrms:themes /> <!-- 7.0css -->
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>

  </head>
  
  <body>
 <html:form action="/kq/app_check_in/all_app_data">
 <div  class="fixedDiv2" style="height: 100%;border: none">
 <table border="0" cellspacing="0" align="center" cellpadding="0" width="100%" >
    <tr>
     <td nowrap>  
      <logic:notEqual name="appForm" property="select_time_type" value="2">
         时间范围：<bean:write name="appForm" property="start_date" filter="true" />&nbsp;&nbsp;至&nbsp;&nbsp;
      <bean:write name="appForm" property="end_date" filter="true" />
      </logic:notEqual>
       <logic:equal name="appForm" property="select_time_type" value="2">
          
       </logic:equal>
      </td>
     </tr>
    </table> 
    <table width="90%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
      <tr >
          <td height="20" class="TableRow" nowrap colspan="2" align="center">
		   请假汇总统计天数
          </td>            	        	        	        
       </tr>
      <tr>   
       <td align="center" class="TableRow" align="center"  nowrap>请假类型</td>
       <td align="center" class="TableRow" align="center" nowrap>请假天数</td>
     </tr> 
     <%int i=0; %>
        <logic:iterate id="element" name="appForm"  property="leaveTimeList" indexId="index">
        <%
               if(i%2==0){ 
             %>
             <tr class="trShallow">
             <%
               }else{
             %>
             <tr class="trDeep">
             <%}
             %>
             <%i++;%>  
          <td  align="center" class="RecordRow" nowrap>
             <bean:write name="element" property="dataName" filter="true" />
          </td>
          <td  align="center" class="RecordRow" nowrap>
             <bean:write name="element" property="dataValue" filter="true" />
          </td>           
       </tr>
     </logic:iterate>
     </table>
     <br>
   <table border="0" cellspacing="0" width="300" align="center" cellpadding="0" width="90%" >
    <tr>
     <td align="center">   
       <input type="button" name="tdf" value="<bean:message key="button.close"/>"  class="mybutton" onclick="window.close();">
      </td>
     </tr>
    </table> 
    </div>
</html:form>
  </body>
</html>
