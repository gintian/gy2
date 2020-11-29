<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*,
				com.hjsj.hrms.utils.ResourceFactory" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
<%
	String opt=request.getParameter("opt");
	String desc=ResourceFactory.getProperty("hire.employActualize.personnelFilter.comment");
	if(opt.equals("2"))
		desc=ResourceFactory.getProperty("lable.performance.report");

 %>
<script type="text/javascript" src="/fckeditor/fckeditor.js"></script>
<script language='javascript' >
function changeObject()
{
	document.evaluationForm.action="/performance/evaluation/performanceEvaluation.do?b_showRemark=query&opt=<%=opt%>";
	document.evaluationForm.submit();
}

function sub()
{
	document.evaluationForm.action="/performance/evaluation/performanceEvaluation.do?b_saveRemark=save&opt=<%=opt%>";
	document.evaluationForm.submit();
}

function remark_close(){
	if(window.showModalDialog){
		window.close();
	}else {
		var win = parent.parent.Ext.getCmp('showRemark_win');
   		if(win) {
    		win.close();
   		}
	}
}

</script>
<html:form action="/performance/evaluation/performanceEvaluation">

<table>
<TR style="height:30px">
	<TD align='right' ><bean:message key="lable.performance.perObject"/>:</TD>
	<TD>
	<!--
		<select name="evaluationForm" property="objectid" size="1" onchange='changeObject()' >
	  		 <optionsCollection property="objectList" value="dataValue" label="dataName"/>
		</select>
		  -->
		  <bean:write name="evaluationForm" property="objName"/>
	</TD>
</TR>
<TR>
	<TD align='right' valign='top' >
	   <%=desc%>:</TD>
	<TD>
	<%
	 boolean flag=true;
	 if(opt.equals("2")){ %>
		<logic:equal name="evaluationForm" property="method"  value="1">
		<% flag=false; %>
		<html:textarea name="evaluationForm" property="summarize"   style="width:600px;height:450px;" >   </html:textarea>		
		 
         </logic:equal>
        <% } 
        if(flag){
        %> 
        <html:textarea name="evaluationForm" property="summarize"    style="width:600px;height:450px;"  >   </html:textarea>		
        <% } %>
        
        
		
	</TD>

</TR>
<% if(opt.equals("2")){ %>
<TR>
	<TD align='right' valign='top' >&nbsp;</TD>
	<TD>
	 附件:&nbsp;
		<logic:iterate id="element" name="evaluationForm" property="summaryFileIdsList" >
                 	   		  	<br>&nbsp;&nbsp;
                 	   		  	<a href='/servlet/performance/fileDownLoad?article_id=<bean:write name="element" property="id" />'  target="_blank"  border='0' >  
                 	   		  	<bean:write name="element" property="name" />
                 	   		  	</a>
                 	   		  	
         </logic:iterate>
	</TD>

</TR>
<% } %>

<TR>
	<TD></TD>
	<TD  align='center' >

<% if(opt.equals("1")){ %>
	<logic:notEqual name="evaluationForm" property="planStatus" value="7">
		<input type='button' value='<bean:message key="kq.kq_rest.submit"/>' onclick='sub()'  class="mybutton"  /> 
	</logic:notEqual>
<% } %>
		<input type='button' value='<bean:message key="lable.welcomeboard.close"/>' onclick='remark_close()'  class="mybutton"  />
	</TD>
</TR>
</table>






</html:form>
</body>
</html>