<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.constant.SystemConfig"%>
<hrms:themes />
<html>
<%
String p0100=request.getParameter("p0100");

%>

  <SCRIPT LANGUAGE=javascript src="/js/constant.js"></SCRIPT>
  <script language='javascript'>

  	function enter()
  	{	
   		var reason=document.workPlanSpForm.reason.value;
  	    if(trim(reason).length==0)
  		{
  			alert("驳回原因不能为空!");
  				return;
  		}
  		if(confirm("确认驳回吗?")){
	     var vo=new Object();
	     vo.reason=workPlanSpForm.reason.value;
	     vo.p0100="<%=p0100%>";
     	 var hashvo = new ParameterSet();
	     hashvo.setValue("vo",vo);
	     var request=new Request({asynchronous:false,onSuccess:enter_ok,functionId:'302001020651'},hashvo); 
  		}
  	
  	
  	}
  	
  	function enter_ok(outparamters){
  		 var p0100=outparamters.getValue("p0100");
  		 var state=outparamters.getValue("state");
  		 var belong_type=outparamters.getValue("belong_type");
      	 var vo=new Object();
	     vo.p0100=p0100;
	     vo.state=state;
	     vo.belong_type=belong_type;
      	 window.returnValue=vo;
	   	 window.close();   
  	
  	}

  
  </script>
   <script language="javascript" src="/js/validate.js"></script> 
  <link href="/css/css1.css" rel="stylesheet" type="text/css">
  <body>
  <html:form action="/performance/nworkplan/nworkplansp/SearchWorkPlanSpTrans">
    <table width="90%">
    <br>
    <tr>
    	<td class='TableRow_lrt'>驳回原因:</td>
      
    </tr>
    <tr>
	    <td> 
	    	<TEXTAREA name='reason' rows="16" cols="50"> </TEXTAREA>
	    </td>
    </tr>
    <tr>
	    <td  valign='top' align='center' >&nbsp;&nbsp;&nbsp;
	    	<input type='button' class="mybutton"   onclick='enter()' value='确定'  />
	    	<input type='button'  class="mybutton"  onclick='javascript:window.close()' value='取消'  />
	    </td>
    </tr>
    </table>
</html:form>
  
  
  </body>
</html>
