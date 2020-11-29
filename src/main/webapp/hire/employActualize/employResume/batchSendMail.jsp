<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="java.util.*,
				com.hjsj.hrms.actionform.hire.employActualize.EmployResumeForm" %>
<%@ page import="com.hjsj.hrms.actionform.hire.employActualize.BatchSendMailForm" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<%
	 EmployResumeForm employResumeForm=(EmployResumeForm)session.getAttribute("employResumeForm");	
	 String str_whl=employResumeForm.getStr_whl();
	 UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	 userView.getHm().put("hire_batch_mail_sql", str_whl);
	 BatchSendMailForm batchSendMailForm =(BatchSendMailForm)session.getAttribute("batchSendMailForm");
%>
<html>
  <head>

    
    <title><bean:message key="hire.batch.sendemail"/></title>
    
  </head>
  <script language="JavaScript" src="/js/constant.js"></script>
  <script language='javascript' >
  	function selectTemplate()
  	{
  		document.batchSendMailForm.action="/hire/employActualize/employResume/batchSendMail.do?b_init=query";
  		document.batchSendMailForm.submit();
  	}
  	
  	 function changeFieldItem(){
  	 	var m = document.batchSendMailForm.zb_id.value;
  		 insertTxt(m);
  	 }
  	 
  	 
  	 	function insertTxt(strtxt)
		{
		    if(strtxt==null)
		   	 	return ; 
		    if((strtxt.toString()).indexOf("(")!=-1)
		     	strtxt="["+strtxt+"]";
			var expr_editor=$('content');
			expr_editor.focus();
		    var element = document.selection;
			if (element!=null) 
			{
				  var rge = element.createRange();
				  if (rge!=null)	
				  	  rge.text=strtxt;
		    }
			
		}
		function sendMessage()
		{
		      if(document.batchSendMailForm.mailTempID.value.length==0)
			{
				alert(SELECT_TEMPLATE);
				return;
			}
			var desc=CONFIRM_SEND_MESSAGE;
			//未加群发
			var type="${batchSendMailForm.type}"
			if(type=='1')
			{
				desc=CONFIRM_GROUP_SEND_MESSAGE;
			}
			
			if(confirm(desc))
			{
				document.batchSendMailForm.action="/hire/employActualize/employResume/batchSendMail.do?b_send=send&operate=1&sendtype=0";
  				document.batchSendMailForm.submit();
			}
		}
		
		function sendMail()
		{
		
			if(document.batchSendMailForm.mailTempID.value.length==0)
			{
				alert(SELECT_EMAIL_TEMPLATE);
				return;
			}
			var desc=CONFIRM_SEND_EMAIL;
			var type="${batchSendMailForm.type}"
			if(type=='1')
			{
				desc=CONFIRM_GROUP_SEND_EMAIL;
			}
			
			if(confirm(desc))
			{   document.getElementById("enter").disabled=true;
				document.batchSendMailForm.action="/hire/employActualize/employResume/batchSendMail.do?b_send=send&operate=1&sendtype=1";			
  				document.batchSendMailForm.submit();
			}
		}

<%  
	if(request.getParameter("operate")!=null&&request.getParameter("operate").equals("1"))
	{
		String rovkeName=batchSendMailForm.getRovkeName();
		
	   if(rovkeName!=null&&rovkeName.trim().length()!=0){
	   //rovkeName=rovkeName.replace("#",".txt");
%>
			var win=open("/servlet/vfsservlet?fromjavafolder=true&fileid="+decode(<%=rovkeName%>));
	   		setInterval("window.close()",3000)
	  <% }else{
		   if(request.getParameter("sendtype")!=null&&request.getParameter("sendtype").equals("0"))
		    	out.println("alert(SEND_MESSAGE_COMPLETE);");
		    else
		        out.println("alert(SEND_EMAIL_COMPLETE);");
			out.println("window.close();");
		}
		
	}
  %>
  function setTPinput(){
    var InputObject=document.getElementsByTagName("input");
    for(var i=0;i<InputObject.length;i++){
        var InputType=InputObject[i].getAttribute("type");
        if(InputType!=null&&(InputType=="text"||InputType=="password")){
            InputObject[i].className=" "+"TEXT4";
        }
    }
 }
  </script>
  <body onload="setTPinput()">
  <hrms:themes></hrms:themes>
   <html:form action="/hire/employActualize/employResume/batchSendMail">
  	<table align='center'  style="margin-top:5px;">
  		<tr><td align='right'><bean:message key="lable.tz_template.name"/>&nbsp;&nbsp;</td><td>
  			<html:select name="batchSendMailForm" property="mailTempID"  onchange='selectTemplate()'  size="1">
                              <html:optionsCollection property="mailTempList" value="dataValue" label="dataName"/>
        	</html:select> 
  		</td></tr>
  		<tr><td align='right'><bean:message key="lable.tz_template.title"/>&nbsp;&nbsp;</td>
  		   <td>
  			<input type='text' value="${batchSendMailForm.title}" name='title'  />
  		   </td>
  		</tr>
  		<tr><td align='right'><bean:message key="hire.inset.field"/>&nbsp;&nbsp;</td>
  		<td>   
  		
  			<html:select name="batchSendMailForm" property="zbj_id"   size="1">
                              <html:optionsCollection property="zbj_list" value="dataValue" label="dataName"/>
        	</html:select> 
  			
  			<html:select name="batchSendMailForm" property="zb_id"   size="1" onchange="changeFieldItem();"  >
                              <html:optionsCollection property="zb_list" value="dataValue" label="dataName"/>
        	</html:select> 	
  		</td></tr>
  		<tr><td align='right' valign='top'><bean:message key="hire.email.content"/>&nbsp;&nbsp;</td>
  		<td>   
  		<html:textarea name="batchSendMailForm" property="content"  cols="80" rows="20" >
  		
  		</html:textarea>
  		
  		</td></tr>
  		
  		<tr><td colspan='2' align='center' >
  		<input type='button' class='mybutton' value="<bean:message key="label.sms.send"/>" onclick='sendMessage();'/>
  			<Input type='button' id="enter" class='mybutton'  value="<bean:message key="label.zp_employ.sendmail"/>" onclick='sendMail()'  />
  			<Input type='button' class='mybutton'  value="<bean:message key="button.cancel"/>" onclick='window.close()' />
  		</td></tr>
  		
  	</table>

  	</html:form>
  </body>
</html>
