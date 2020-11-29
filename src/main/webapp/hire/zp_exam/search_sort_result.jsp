<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.hire.ZpExamReportForm"%>

<%
	int i=0;
%>

<style id=iframeCss>
div{
	cursor:hand;font-size:12px;
   }
a{
text-decoration:none;color:black;font-size:12px;
}

a.a1:active {
	color: #003100;
	text-decoration: none;
}
a.a1:hover {
	color: #FFCC00;
	text-decoration: none;
}
a.a1:visited {	
	text-decoration: none;
}
a.a1:link {
	color: #003100;
	text-decoration: none;
}
</style>
<script language="javascript">

</script>

<html:form action="/hire/zp_exam/search_sort_result">

<br>
<% ZpExamReportForm zpExamReportForm=(ZpExamReportForm)session.getAttribute("zpExamReportForm");
      if(zpExamReportForm.getFieldList().size()>0){%>
<table width="70%" border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable">
   	  <thead>
           <tr>          
            <td align="center" class="TableRow" nowrap>
		<bean:message key="label.title.name"/>
	    </td>  
	           
            <td align="center" class="TableRow" nowrap>
		<bean:message key="label.zp_exam.read_score"/>
	    </td>
            <td align="center" class="TableRow" nowrap>
		<bean:message key="label.zp_exam.written_score"/>            	
	    </td>	    
            
	    <logic:iterate  id="namelist"    name="zpExamReportForm"  property="nameList" indexId="index">
	    <td align="center" class="TableRow" nowrap>
                   <bean:write name="zpExamReportForm" property="<%="nameList["+index+"]"%>" filter="true"/>
	    </td>
           </logic:iterate> 
           <td align="center" class="TableRow" nowrap>
		<bean:message key="label.zp_exam.sum_score"/>            	
	    </td> 		    		        	        	        
           </tr>
   	  </thead>
   	  <logic:iterate  id="element"    name="zpExamReportForm"  property="sortCondList" indexId="index"> 
   	  <tr class="trDeep1"> 
           <%
              int count = zpExamReportForm.getFieldList().size();
              for(int j=0;j<count;j++){
                 String name = (String)zpExamReportForm.getFieldList().get(j);
                 if(name.toUpperCase().equals("A0100")){
           %>
   	              
	    <td align="left" class="RecordRow" nowrap> 
                   <bean:write name="zpExamReportForm" property="<%="sortCondList["+index+"].a0101"%>" filter="true"/>
	    </td>
	  <%
	     }else if(!name.toUpperCase().equals("SUM_SCORE")){
	  %>
	   <td align="right" class="RecordRow" nowrap> 
                   <bean:write name="zpExamReportForm" property="<%="sortCondList["+index+"]."+name%>" filter="true"/>
	    </td>
	    <%
	     }else{
	  %>
	   <td align="right" class="RecordRow" nowrap> 
                   <bean:write name="zpExamReportForm" property="<%="sortCondList["+index+"].sum_score"%>" filter="true"/>
	    </td>
	    <%}}%>	 
	    </tr>
	    </logic:iterate>
</table>
<table  width="70%" align="center">
          <tr>
            <td align="center">
         	<hrms:submit styleClass="mybutton" property="b_return">
            		<bean:message key="button.return"/>
	 	</hrms:submit>
        
            </td>
          </tr>          
</table>
<%}%>
</html:form>
