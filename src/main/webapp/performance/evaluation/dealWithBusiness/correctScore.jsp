<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="java.util.*" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<html>
  <head>
 	<title>修正分值</title>

  </head>
  <script language="javascript" src="../evaluation.js"></script>
  <script language="javascript" src="/js/function.js"></script>
  <script language='javascript' >
  <% 
  	if(request.getParameter("opt")!=null&&request.getParameter("opt").equals("sub")){
  		out.print("if(window.showModalDialog){");
  		out.print(" parent.window.returnValue='ok'"); 
  		out.print("}else{");
  		out.print(" parent.parent.correctScore_ok('ok')");
  		out.print("}");
  		out.print(" close_score();");
  	}
   %>
 	function close_score() {
 		if(!window.showModalDialog){
		  	var win = parent.parent.Ext.getCmp('correctScore_win');
	 	  	if(win) {
	  			win.close();
	 	  	}
	  	}
  	  	parent.window.close();
 	}
  //提交修正分值
	function subCorrectScore()
	{
			if(!checkIsNum2(document.evaluationForm.correctScore.value))
			{
				alert("分值格式不正确!");
				return;
			}
			
			if(document.evaluationForm.correctScore.value*1>2147483647)
			{
				alert("最大数值范围不能超过2147483647");
				return;
			}
			
			
			if(document.evaluationForm.correctCause.value==0)
			{
				alert("请填写修正分值原因!");
				return;
			}
			
			document.evaluationForm.action="/performance/evaluation/performanceEvaluation.do?b_subCorrectScore=sub&opt=sub";
			document.evaluationForm.submit();
	}
	function IsDigit(obj) 
	{		
		if((event.keyCode >= 45) && (event.keyCode <= 57) && event.keyCode!=47)
		{
			var values=obj.value;

			if((event.keyCode == 46) && (values.indexOf(".")!=-1))//有两个.
				return false;
			if((event.keyCode == 46) && (values.length==0))//首位是.
				return false;	
			if((event.keyCode == 45) && (values.indexOf("-")!=-1))//有两个-
				return false;				
			return true;
		}
			return false;	
	}
	//判断是否为浮点数 
function BASEisNotFloat(theFloat) 
{ 
	var len=theFloat.length; 
	var dotNum=0; 
	var fuhao=0;
	if (len==0) 
		return true; 
	for(var i=0;i<len;i++)
	{ 
		oneNum=theFloat.substring(i,i+1); 

		if (oneNum==".") 
			dotNum++; 
		if (oneNum=="-") 
			fuhao++; 
		if(fuhao>1 || dotNum>1)
			return true; 
		if(oneNum=="-" && fuhao==1 && i>0)
			return true;

		if ( (oneNum<"0" || oneNum>"9") && oneNum!="." && oneNum!='-') 
			return true;
	} 

	if (len>1 && theFloat.substring(0,1)=="0")
	{ 
		if (theFloat.substring(1,2)!=".") 
			return true; 
	} 
	return false; 
}
function isNumber(theObj)
{
	if(BASEisNotFloat(theObj.value))
	{
		alert('不是有效的分值！');
		theObj.value='0';
	}	
}
  </script>
  <body>
    <html:form action="/performance/evaluation/performanceEvaluation" >  
    	<table width='100%' height='100%' >
    		<tr>
    			<td>

    			<fieldset align="center">
    			<table><tr><td valign='middle' >
    					修正分值:</td>
    				   <td><input type='text' class="inputtext" name='correctScore' size='12' value="${evaluationForm.correctScore}" onblur="isNumber(this);" onkeypress="event.returnValue=IsDigit(this);"/>
    				   </td></tr>
    				   <tr><td valign='middle'>修正原因:</td>
    				   <td>
    							<html:textarea name='evaluationForm' cols='40' rows='8'  property="correctCause">
    							</html:textarea>
    					</td></tr>
    			</table>
    			</fieldset>
    			</td>
    			<td valign='top' >
    				&nbsp; &nbsp;<input type='button' value='确  定' onclick='subCorrectScore()'  class="mybutton"  >
				   <br> <br>
				    &nbsp;&nbsp; <input type='button' value='取  消' onclick='close_score()'  class="mybutton"  >
    
    			</td>
    		</tr>
    
    
    </html:form>
  </body>
</html>
