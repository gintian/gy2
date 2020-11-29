<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%
	int i=0;
	String flag="0";
	if(request.getParameter("txtf")!=null)
	{
	flag=request.getParameter("txtf");
	}
%>
<style>
<!--
.RecordRow {
    border: inset 0px #C4D8EE;
    border-bottom-style: dotted;
    border-bottom-width: 1px;
    
    BORDER-LEFT: #C4D8EE 1pt solid; 
    BORDER-RIGHT: #C4D8EE 1pt solid; 
    BORDER-TOP: #C4D8EE 0pt solid;
    font-size: 12px;
    height:22px;
}
.RecordRowLast {
    BORDER-BOTTOM: #C4D8EE 1pt solid; 
    BORDER-LEFT: #C4D8EE 1pt solid; 
    BORDER-RIGHT: #C4D8EE 1pt solid; 
    BORDER-TOP: #C4D8EE 1pt solid;
    font-size: 12px;
    border-collapse:collapse; 
    height:22px;
}
.TableRow {
    background-position : center left;
    font-size: 12px;  
    BORDER-BOTTOM: #C4D8EE 1pt solid; 
    BORDER-LEFT: #C4D8EE 1pt solid; 
    BORDER-RIGHT: #C4D8EE 1pt solid; 
    BORDER-TOP: #C4D8EE 1pt solid;
    height:22px;
    font-weight: bold;
    background-color:#f4f7f7;   
    /*
    color:#336699;
    */
    valign:middle;
}
-->
</style>
<hrms:themes></hrms:themes>
<html:form action="/selfservice/welcome/infodescribe" >
         <table width="600" border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable">
  	<tr>
    		<td class="TableRow" style="border-bottom: none;" nowrap >
    		<bean:message key="lable.welcome.invtextresult.topicname"/>:
    		<bean:write name="welcomeForm" property="item" filter="true"/>
    		</td>
	</tr>  
        
  	 <logic:iterate  id="element" name="welcomeForm" property="pointList"  scope="session">
  	 <!--  
    	<tr class="trDeep"> 
   		 <td width="130" class="RecordRow" nowrap >
   		 <bean:message key="lable.welcome.invtextresult.username"/>：
   		 <bean:write name="element" property="userName"/>&nbsp;
   		 </td>
   		
       </tr>
  	-->
  <tr>
 	 <td class="RecordRow" nowrap width="500" >
 	   <bean:message key="lable.welcome.invtextresult.context"/>:<br>
  	   &nbsp;&nbsp;<bean:write name="element" property="context"/>&nbsp;
 	 </td>
   </tr>
 </logic:iterate>
 
</table>
<br>
<center>
<table boader="0">
<tr>
 	<td >
 		<%if("train".equalsIgnoreCase(request.getParameter("train"))){ %>
 		<input type="button" class="mybutton" value="<bean:message key="button.return"/>" onclick="javaScript:returnback();">
 		<%} else { %>
	 	<input type="button" class="mybutton" value="<bean:message key="button.return"/>" onclick="javaScript:history.back();">
		<%} %>	 	 
	</td>
</tr> 	
</table>
</center>
 </html:form>
 <SCRIPT type="text/javascript">
 function returnback(){
	 welcomeForm.action="/selfservice/infomanager/askinv/searchendview.do?b_query=link&f=1&id=<%=request.getParameter("classid")%>";
	 welcomeForm.submit();
	}
 </SCRIPT>