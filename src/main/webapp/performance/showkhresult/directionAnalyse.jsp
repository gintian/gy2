<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<hrms:themes />
<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>
<html>
<html>
  <head>
  </head>
  <script language='javascript' >
  	function sub( infor )
  	{
  		if(!document.directionAnalyseForm.isTotalScore.checked)
  		{
  			document.directionAnalyseForm.isTotalScore.value="0";
  			document.directionAnalyseForm.isTotalScore.checked=true;
  		}
  		document.directionAnalyseForm.action="/performance/showkhresult/showDirectionAnalyse.do?b_show=show&operate="+infor+"&type=<%=(request.getParameter("type"))%>";
  		document.directionAnalyseForm.submit();
  	}
  
    function go_return()
	{
	         
              document.directionAnalyseForm.action="/selfservice/performance/leaderexamine/showinfo.do?b_search2=link&action=showinfodata.do&target=mil_body&isFire=noOpen";
              document.directionAnalyseForm.target="il_body";
              document.directionAnalyseForm.submit();
	}
  
  </script>
  <body>
  <html:form action="/performance/showkhresult/showDirectionAnalyse">
  <table align='center'>
  <tr><td align='left' >

  	考评表：
  	<html:select name="directionAnalyseForm" property="template_id" onchange='sub(0)'  size="1">
                              <html:optionsCollection property="templateList" value="dataValue" label="dataName"/>
     </html:select>  
     &nbsp;&nbsp;
     <html:select name="directionAnalyseForm" property="itemLevelID" onchange='sub(1)'  size="1">
                              <html:optionsCollection property="itemLevelList" value="dataValue" label="dataName"/>
     </html:select> 
      &nbsp;&nbsp;
     <html:checkbox  name="directionAnalyseForm" property="isTotalScore" value="1"  onclick='sub(1)' />总分
     
     <%  if(request.getParameter("type")!=null&&request.getParameter("type").equals("1")){ %>     
        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
        <input type="button" value="<bean:message key="button.return"/>" onclick="go_return();" class="mybutton">
   		<%  } %>
     
  </td></tr>
  <tr><td align='center' >	
  	<br>
  	<hrms:chart name="directionAnalyseForm" title="" isneedsum="false" scope="session" legends="dataMap" data=""  width="750" height="400"    chartParameter="chartParameter"     chart_type="11" >
	</hrms:chart>
  </td></tr>
  </table>
  </html:form>
  </body>
</html>