<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.constant.SystemConfig"%>
<html>
  <head>
  
   
  </head>
  <SCRIPT LANGUAGE=javascript src="/js/constant.js"></SCRIPT>
  <%
  String info = request.getParameter("info");
  %>
  <script language='javascript'>
  	var info='<%=info%>';
  
  	function enter()
  	{
  	    if(trim(document.objectCardForm.cause.value).length==0)
  		{
  			alert(GZ_ACCOUNTING_PLEASEFILL+info+"!");
  				return;
  		}
  		var result=new Array();
  		result[0]=document.objectCardForm.cause.value;
	  	result[1]=document.objectCardForm.rejectObj.value;
  		
	  	if(parent && parent.parent && parent.parent.reject_ok){
	  		parent.parent.reject_ok(result);
	  	} else {
		  	parent.returnValue=result;
		    parent.window.close();
	  	}
  	
  	}
  	
  	function clearObjs()
	{
		document.objectCardForm.user_.value='';
	   	document.objectCardForm.user_h.value='';
	
	}
	
  	function chooseMen()
  	{
  	   var return_vo=select_user_dialog('1','1');
       if(return_vo)
       { 
       		var a_temps=return_vo.title.split(",");
	 		var temps=return_vo.content.split(",");
	 		for(var i=0;i<temps.length;i++)
	 		{
	 			if(temps[i].length>0)
	 			{
	 				document.objectCardForm.user_.value+=","+a_temps[i];
	 				document.objectCardForm.user_h.value+=","+temps[i];
	 			}
	 		} 
	 		if(document.objectCardForm.user_.value.length>0)
	 		{
	 			document.objectCardForm.user_.value=document.objectCardForm.user_.value.substring(1);
	 		}   
	   }
  	
  	}
    function closeWin(){
    	if(parent && parent.parent && parent.parent.reject_win_close){
	  		parent.parent.reject_win_close();
	  	} else {
		    parent.window.close();
	  	}
    	
    }
  </script>
   <script language="javascript" src="/js/validate.js"></script> 
<hrms:themes />
  <body>
  <html:form action="/performance/objectiveManage/objectiveCard">
    <table>
    
     <Tr><Td align='right' valign='middle'  ><br>
	    <script language='javascript'>
	    	document.write(KH_PLAN_BACK+":");
	    </script>
	    
	    </td>
	    <td><br>
	   <html:select name="objectCardForm"  property="rejectObj" size="1" >
		   <html:optionsCollection property="rejectObjList" value="dataValue" label="dataName"/>
	   </html:select>
    </Td>
    </Tr>
    <tr>
    <td valign='top'  align='right' >
    <script language='javascript'>
    	document.write("<font style='COLOR: #ff0000'>*</font>&nbsp;"+info+":");
    </script>
    </td> 
    <td> 
    	<TEXTAREA name='cause' style="width:350px;height:200px;"> </TEXTAREA>
    </td>
    </tr>
    <tr>
    <td colspan='2' valign='top' align='center' >&nbsp;&nbsp;&nbsp;
    	<input type='button' class="mybutton"   onclick='enter()' value='<bean:message key="kq.formula.true"/>'  />
    	<input type='button'  class="mybutton"  onclick='closeWin()' value='<bean:message key="lable.content_channel.cancel"/>'  />
    </td>
    </tr>
    </table>
</html:form>
  
  
  </body>
</html>
