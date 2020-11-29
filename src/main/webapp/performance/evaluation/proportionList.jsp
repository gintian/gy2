<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="java.util.*" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*,
                 com.hjsj.hrms.actionform.performance.evaluation.CalcRuleForm,
                 org.apache.commons.beanutils.LazyDynaBean" %>
<%
	CalcRuleForm crf=(CalcRuleForm)session.getAttribute("calcRuleForm");	
	String UnLeadSingleAvg=crf.getUnLeadSingleAvg();
	
 %>
<html>
  <head>
   

  </head>
  <style>
	div#treemenu {
	BORDER-BOTTOM:#94B6E6 1pt solid;
	BORDER-LEFT: #94B6E6 1pt solid;
	BORDER-RIGHT: #94B6E6 1pt solid;
	BORDER-TOP: #94B6E6 1pt solid;
	width: 500px;
	height: 270px;
	overflow: auto;
	}
</style>
  
  <script language='javascript' >
  	function numCheck(){
		if ( !(((window.event.keyCode >= 48) && (window.event.keyCode <= 57)) 
		|| (window.event.keyCode == 13) || (window.event.keyCode == 46) 
		|| (window.event.keyCode == 45)))
		{
			window.event.keyCode = 0 ;
		}
	}
  function checkNum(obj)
  {
  	if(ltrim(rtrim(obj.value))=='')
  	{
  		obj.value="0.0";
  		return;
  	}
  	if(checkIsNum(obj.value))
  	{
  		if(obj.value*1>1)
  		{
  			alert(P_E_SJXY1);
  			obj.value="0.0";
  			return;
  		}
  	}
  	else
  	{
  		alert(KHPLAN_ERRORINFO3);
  		obj.value="0.0";
  		return;
  	}
  }
  
  
  function enter()
  {
 	var value=0;
  	for(var i=0;i<document.calcRuleForm.elements.length;i++)
  	{
  		if(document.calcRuleForm.elements[i].type=='text')
  		{
  			value+=document.calcRuleForm.elements[i].value*1000000000000000;
  		}
  	}
  	if(value!=1000000000000000)
  	{
  		alert(P_E_INFO);
  		return;
  	}
  	var flag=1;//0:至少有一个选上      ：全部都没有选上
  	for(var i=0;i<document.calcRuleForm.elements.length;i++)
  	{
  		if(document.calcRuleForm.elements[i].type=='checkbox'&&document.calcRuleForm.elements[i].checked==true)
  		{
  			flag=0;
  			break;
  		}
  	}
  	for(var i=0;i<document.calcRuleForm.elements.length;i++)
  	{
  		if(document.calcRuleForm.elements[i].type=='checkbox'&&document.calcRuleForm.elements[i].checked==false)
  		{
  			document.calcRuleForm.elements[i].value="0";
  			document.calcRuleForm.elements[i].checked=true;
  		}
  	}
  	document.calcRuleForm.action="/performance/evaluation/calculate.do?b_sub=sub";
  	document.calcRuleForm.submit();
  	window.returnValue =flag;
  }
  
  <%
  	if(request.getParameter("b_sub")!=null&&request.getParameter("b_sub").equals("sub"))
  		out.println("parent.window.close();");
  %>
  
  </script>
  <script language="JavaScript" src="../../js/function.js"></script>
  <body>
   <html:form action="/performance/evaluation/calculate"> 
     <table width='100%'> 
     	<tr><td width='80%'>
     	 <div id='treemenu' >
     		<table width="100%" border="0" cellspacing="0" align="center" cellpadding="0"  style="margin-top:-1;" class="ListTable">
		   	  <thead><tr>
		         <td align="center" class="TableRow" width='30px' style="border-left:none;" nowrap><bean:message key="label.serialnumber"/></td>         
     			 <td align="center" class="TableRow" width='120px'  nowrap><bean:message key="performance.implement.examinebodytype"/></td> 
     			 <td align="center" class="TableRow" width='90px'  nowrap><bean:message key="label.kh.template.qz"/></td> 
     			 <td align="center" class="TableRow" width='80px'  nowrap><bean:message key="jx.evaluation.clyf"/></td> 
     			 <td align="center" class="TableRow" width='80px' style="border-right:none;" nowrap><bean:message key="jx.evaluation.kpbzf"/></td>
     		    </tr></thead>
     		    <%  int i=0; %>
     		    <logic:iterate id="element"  name="calcRuleForm" property="weightList" indexId="index" >
     		    <%
     		    	if(i%2==0){
     		     %>
     		     <tr class="trShallow">
     		     <% } else { %>
     		      <tr class="trDeep">
     		     
     		     <% } i++; %>
     		     
     		    	 <td align="center" class="RecordRow" style="border-left:none;" nowrap><%=i%></td>
  		   		     <td align="center" class="RecordRow" nowrap><bean:write name="element" property="name" filter="true"/></td>
     			     <td align="center" class="RecordRow" nowrap>
     			     	<%-- 确认的主体权重为0，且不允许修改 by 刘蒙 --%>
						<bean:define id="rankValue" value="${element.map.rank }"></bean:define>
						<bean:define id="isDisabled" value=""></bean:define>
						<logic:equal name="element" property="pbOpt" value="1">
							<bean:define id="isDisabled" value="disabled"></bean:define>
							<bean:define id="rankValue" value="0.0"></bean:define>
						</logic:equal>
     			     	<input type="text" name="<%="weightList["+index+"].rank"%>"
							onblur='checkNum(this)' onKeypress="numCheck()"  size='5'
							value="${rankValue }" ${isDisabled } />
     			     </td>
     				 <logic:equal name="calcRuleForm" property="zeroByNull" value="false">
	     				 <td align="center" class="RecordRow" nowrap><input type="checkbox" name="<%="weightList["+index+"].flag"%>" <logic:equal name='element' property='flag' value="1">checked</logic:equal>  value="1"  size='5' /></td>
	     				 <td align="center" class="RecordRow" style="border-right:none;" nowrap><input type="checkbox" name="<%="weightList["+index+"].lead"%>" <logic:equal name='element' property='lead' value="1">checked</logic:equal>  value="1"  size='5' /></td>
	     			 </logic:equal>
	     			 <logic:equal name="calcRuleForm" property="zeroByNull" value="true">
	     				 <td align="center" class="RecordRow" nowrap><input type="checkbox" name="<%="weightList["+index+"].flag"%>" disabled  value="0"  size='5' /></td>
	     				 <td align="center" class="RecordRow" style="border-right:none;" nowrap><input type="checkbox" name="<%="weightList["+index+"].lead"%>" disabled  value="0"  size='5' /></td>
	     			 	<input type="hidden" name="<%="weightList["+index+"].flag"%>" value="0"/>
	     			 	<input type="hidden" name="<%="weightList["+index+"].lead"%>" value="0"/>
	     			 </logic:equal>
	     			 </tr>
     			</logic:iterate>
     		</table>
     	</div>
     	</td>
        <td width='20%' valign='top' >
          <table width='100%' > 
     			<tr><td>
      				 <input type='button' value='<bean:message key="lable.tz_template.enter"/>'   onclick='enter()'  class="mybutton"  >
      		   </td></tr>
      		   <tr><td height='10'>
      				&nbsp;
      		   </td></tr>
        	   <tr><td>		
					<input type='button' value='<bean:message key="lable.tz_template.cancel"/>' onclick='parent.window.close()'  class="mybutton"  >
			   </td></tr>
			</table>  
        </td></tr>
        <tr><td colspan='2'>
        <logic:equal name="calcRuleForm" property="zeroByNull" value="false">
        	<input type='checkbox' name='unLeadSingleAvg'  value='1' <logic:equal name='element' property='unLeadSingleAvg' value="1">checked</logic:equal>  /><bean:message key="jx.evaluation.info0"/>
        </logic:equal>
	    <logic:equal name="calcRuleForm" property="zeroByNull" value="true">
	     	<input type='checkbox' name='unLeadSingleAvg' disabled value='0' /><bean:message key="jx.evaluation.info0"/>
	     	<input type='hidden' name='unLeadSingleAvg' value='0' />
	    </logic:equal>
	    <br><br>&nbsp;<bean:message key="jx.evaluation.info1"/>
		<br>&nbsp;<bean:message key="jx.evaluation.info2"/>
        
        </td></tr>
     </table>
    
    
   </html:form> 
  </body>
</html>
  <script>  
var aa=document.getElementsByTagName("input");
for(var i=0;i<aa.length;i++){
	if(aa[i].type=="text"){
		aa[i].className="inputtext";
	}
}
  </script>  