<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<hrms:themes />
<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>
<html>
  <head>
  </head>
 
  
  
  <script language='javascript' >
  	function changeTemplate()
  	{
  		<%  if(request.getParameter("type")!=null&&request.getParameter("type").equals("1")){ %>  
  		document.directionAnalyseForm.action="/performance/showkhresult/showDirectionAnalyse.do?b_init1=link&operate=1";
  		<% } else { %>
  		document.directionAnalyseForm.action="/performance/showkhresult/showDirectionAnalyse.do?b_init=link&operate=1";
  		<% } %>
  		document.directionAnalyseForm.target="_parent";
  		document.directionAnalyseForm.submit();
  	
  	}
  	
  	
  	function setValue()
  	{
  		document.directionAnalyseForm.action="/performance/showkhresult/showDirectionAnalyse.do?b_setShowType=link&type=<%=(request.getParameter("type"))%>";
  		document.directionAnalyseForm.target="tbi_top";
  		document.directionAnalyseForm.submit();
  	
  	}
  	
  	
  	function go_return()
	{
	         // document.directionAnalyseForm.action="/selfservice/performance/leaderexamine/showinfo.do?b_search=link&action=showinfodata.do&target=mil_body";
              
              document.directionAnalyseForm.action="/selfservice/performance/leaderexamine/showinfo.do?b_search2=link&action=showinfodata.do&target=mil_body&isFire=noOpen";
              document.directionAnalyseForm.target="i_body";
              document.directionAnalyseForm.submit();
	}
  
  </script>
  <body>
   <html:form action="/performance/showkhresult/showDirectionAnalyse">
   &nbsp;&nbsp;评测表：&nbsp;
   		<html:select name="directionAnalyseForm" property="template_id" onchange='changeTemplate()' size="1">
                              <html:optionsCollection property="templateList" value="dataValue" label="dataName"/>
        </html:select>
        &nbsp;&nbsp;&nbsp;&nbsp;
        <html:radio property="showType"   name="directionAnalyseForm"  onclick='setValue()'  value="1" />按年统计&nbsp;
       
        <html:radio property="showType"   name="directionAnalyseForm"  onclick='setValue()'  value="2" />按月统计
        
   		<%  if(request.getParameter("type")!=null&&request.getParameter("type").equals("1")){ %>     
        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
        <input type="button" value="<bean:message key="button.return"/>" onclick="go_return();" class="mybutton">
   		<%  } %>
   </html:form>
  </body>
</html>
