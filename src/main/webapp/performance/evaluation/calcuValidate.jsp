<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<html:form action="/performance/evaluation/performanceEvaluation">  
	 <table>
    <tr><td colspan='3'>
  			<logic:equal name="evaluationForm" property="validateOper" value="1">
  				<bean:message key="jx.evalution.calcuInfo1"/>
  			</logic:equal>
  			<logic:equal name="evaluationForm" property="validateOper" value="2">
  				<bean:message key="jx.evalution.calcuInfo2"/>
  			</logic:equal>
    </td></tr>
    <tr>
    <td> 
    	<html:textarea name="evaluationForm" readonly="true" property="validateInfo" rows="20" style="width:680px;"></html:textarea>
    </td>
    </tr>
    <tr>
    <td align='center' >
    
     <input type='button' class="mybutton"   onclick='enter()' value='<bean:message key="lable.tz_template.enter"/>'  />
 	 <input type='button' class="mybutton"   onclick='close_cla("false");' value='<bean:message key="lable.tz_template.cancel"/>'  />
    </td>
    </tr>
    
    </table>
</html:form>
<script type="text/javascript">
  	<% 
		String callbackFunc = request.getParameter("callbackFunc");
	%>
	function enter()
  	{	// 1-考核主体对考核对象的考评的校验 2-等级结果的检查校验
  		if('${evaluationForm.validateOper}'=='2')  	
  		{
  			close_cla("true");
  		}  			  	
  		else if	('${evaluationForm.validateOper}'=='1')  
  			window.location="/performance/evaluation/performanceEvaluation.do?b_calcuValidate=link&planid=${evaluationForm.planid}&validateOper=2&callbackFunc=<%=callbackFunc%>"; 
  	}  
  	if(evaluationForm.validateInfo.value=='')
  	{
  		close_cla("true");
  	}
  	
		
	
  	function close_cla(flag) {
  		var thevo=new Object();
  		thevo.flag=flag;
  		if(window.showModalDialog){
  			parent.window.returnValue=thevo;
	  		parent.window.close();
		}else{
			<%
				if(callbackFunc != null && callbackFunc.length() > 0) {
			%>
				parent.parent.<%=callbackFunc%>(thevo);
			<%}%>
			parent.parent.close_cal();
		}
  	}
  	
</script>