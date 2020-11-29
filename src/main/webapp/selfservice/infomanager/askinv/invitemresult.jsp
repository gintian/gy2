<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.welcome.WelcomeForm,com.hjsj.hrms.actionform.askinv.EndViewForm,java.util.*"%>
<jsp:useBean id="endViewForm" class="com.hjsj.hrms.actionform.askinv.EndViewForm" scope="session"/>
<%
EndViewForm endViewForm2 = (EndViewForm)session.getAttribute("endViewForm");
	int i=0;
%>
<style>
<!--
.RecordRowLast {
    BORDER-BOTTOM: #C4D8EE 1pt solid; 
    BORDER-LEFT: #C4D8EE 1pt solid; 
    BORDER-RIGHT: #C4D8EE 1pt solid; 
    BORDER-TOP: #C4D8EE 1pt solid;
    font-size: 12px;
    border-collapse:collapse; 
    height:22px;
}
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
<html:form action="/selfservice/infomanager/askinv/invitemresult" >

     <% int x=0; %>   
  	 <logic:iterate  id="element" name="endViewForm" property="itemwhilelst"  scope="session">
    	
 	   <textarea id="desc<%=x %>" style="BORDER-BOTTOM: 0px solid; BORDER-LEFT: 0px solid; BORDER-TOP: 0px solid;BORDER-RIGHT: 0px solid;overflow-y:hidden;" readonly="true" ></textarea>
 	
   <hr style="border-bottom:1px dotted #000; width:100% ;">
   <% x++; %>
 </logic:iterate>

 </html:form>
 <script>
<%
   ArrayList eassyDesc = (ArrayList)endViewForm2.getItemwhilelst();
   for(int q=0;q<eassyDesc.size();q++){
       WelcomeForm wf = new WelcomeForm();
       wf = (WelcomeForm)eassyDesc.get(q);
%>
  var eassy_point = "desc<%=q %>";
  var eassy_desc = "<%=wf.getContext() %>";
  eassy_desc=eassy_desc.replace(/<br>/g,"\r\n");
  document.getElementById(eassy_point).value="  "+eassy_desc;
  
<%} 
%>
</script>
 
