<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*,com.hjsj.hrms.actionform.performance.singleGrade.SingleGradeForm" %>

<%
	SingleGradeForm singleGradeForm=(SingleGradeForm)session.getAttribute("singleGradeForm");
	String objectStatus=singleGradeForm.getObjectStatus();
	String flag="false";
	if(objectStatus.equals("2"))
		flag="true";
 %>
<html>
  <head>
  
   
  </head>
  <SCRIPT LANGUAGE=javascript src="/js/constant.js"></SCRIPT>
  <script language='javascript'>
  function closeWin(){
	  if(window.showModalDialog){
	  		window.close();
	  	}else{
	  		parent.parent.Ext.getCmp("scoreReasonWin").close();
	  	}
  }
  
  	function enter()
  	{
  	/*  //如果评分说明必填的话 提交打分时会控制 ，在此去掉此判断 JinChunhai 2011.12.08
  		if(document.singleGradeForm.scoreCause.value.length<=0)
  		{
  			alert("请填写评分说明!");
  			return;
  		}
  		*/
  		document.singleGradeForm.action="/selfservice/performance/singleGrade.do?b_saveScoreCause=save&type=<%=(request.getParameter("type"))%>&opt=<%=(request.getParameter("opt"))%>";
  		document.singleGradeForm.submit();
  	
  	}
  
  </script>
   
<hrms:themes />
  <body>
  <html:form action="/selfservice/performance/singleGrade">
    <table  border="0" cellpadding="0" cellspacing="0" align="center">
    <tr><td style='padding-bottom:5px;'>
     评分说明
    </td></tr>
    <tr>
    <td> 
    	<% if(flag.equals("true")){ %>
    	<html:textarea  name="singleGradeForm"  readonly="true"   property="scoreCause" rows="13" cols="70" ></html:textarea>
    	<% }else{ %>
    	<html:textarea  name="singleGradeForm"   property="scoreCause" rows="13" cols="70" ></html:textarea>
    	<% } %>
    </td>
    

    </tr>
    <tr>
        <td style='padding-top:5px;' valign='top' align="center">
    <% if(objectStatus.equals("0")||objectStatus.equals("1")){ %>
    	<input type='button' class="mybutton"   onclick='enter()' value='<bean:message key="kq.formula.true"/>'  />
    <% } %>
    	<input type='button'  class="mybutton"  onclick='closeWin();' value='关闭'  />
    </td>
    </tr></table>
 </html:form>
  </body>
  
  
  <% 
  	if(request.getParameter("b_saveScoreCause")!=null&&request.getParameter("b_saveScoreCause").equals("save"))
  	{
  %>
  <script language='javascript' >
  	if(window.showModalDialog){
        parent.window.returnValue=document.singleGradeForm.scoreCause.value;
  	}else{
  		parent.parent.scoreReason_ok(document.singleGradeForm.scoreCause.value);
  	}
  	closeWin();
  </script>
  <% 	
  	}
   %>
  
</html>
