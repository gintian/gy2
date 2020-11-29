<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.welcome.WelcomeForm,com.hjsj.hrms.utils.PubFunc" %>
<%@ page import="java.util.*"%>

<%
    int i=0;
    WelcomeForm welcomeForm=(WelcomeForm)session.getAttribute("welcomeForm");
    String mdid=PubFunc.encryption(welcomeForm.getHomePageHotId());
%>
<style>
<!--
.RecordRow {
    BORDER-BOTTOM: #C4D8EE 0pt solid; 
    BORDER-LEFT: #C4D8EE 1pt solid; 
    BORDER-RIGHT: #C4D8EE 1pt solid; 
    BORDER-TOP: #C4D8EE 1pt solid;
    font-size: 12px;
    border-collapse:collapse; 
    height:22px;
}
.RecordRowLast {
    BORDER-BOTTOM: #C4D8EE 1pt solid; 
    BORDER-LEFT: #C4D8EE 1pt solid; 
    BORDER-RIGHT: #C4D8EE 1pt solid; 
    BORDER-TOP: #C4D8EE 1pt solid;
    font-size: 12px;
    border-collapse:collapse; 
    height:100px;
}
.TableRow {
    background-position : center left;
    font-size: 12px;  
    BORDER-BOTTOM: #C4D8EE 0pt solid; 
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
<script type="text/javascript">
<!--
function reutrnBack()
{
    welcomeForm.action="/selfservice/welcome/hot_topic.do?b_query=query&homePageHotId=<%=mdid%>&enteryType=1";
    welcomeForm.submit();
}
//-->
</script>
<html:form action="/selfservice/welcome/welcome" >
         <table width="600" border="0" cellspacing="0"  align="center" cellpadding="0" >
  	<tr>
    		<td class="TableRow" nowrap >
    		<bean:message key="lable.welcome.invtextresult.topicname"/>:
    		<bean:write name="welcomeForm" property="item" filter="true"/>
    		</td>
	</tr>  
	<tr>
     <td class="RecordRowLast" nowrap valign='top'>
      <bean:message key="lable.welcome.invtextresult.context"/>:<br>
      <hr style="border-bottom:1px dotted #000; width:100% ;" align="center">
      <%
          ArrayList itemwhilelst = (ArrayList)welcomeForm.getItemwhilelst();
          for(int x=0;x<itemwhilelst.size();x++){
              WelcomeForm wf = new WelcomeForm();
              wf = (WelcomeForm)itemwhilelst.get(x);
       %>
  	   &nbsp;&nbsp;&nbsp;&nbsp;<%=wf.getContext() %>
  	    <%
                if(x!=itemwhilelst.size()-1){
        %>
        <hr style="border-bottom:1px dotted #000; width:100% ;" align="center">
     <%} %>
  	 <%} %>
  </td>
   </tr>
  <tr>
 <td align="center">
 <logic:notEqual value="-1" name="welcomeForm" property="homePageHotId">
 <br>
 <input type="button" name="dd" value="<bean:message key="button.return"/>" onclick="reutrnBack();" class="mybutton"/>
 
 </logic:notEqual>
 </td>
 </tr>
</table>
 </html:form>
 