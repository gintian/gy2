<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="java.util.*" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.hire.employActualize.BatchSendMailForm,com.hrms.hjsj.sys.ResourceFactory"%>
<%@ page import="com.hrms.struts.constant.SystemConfig" %>
<html>
 <head>
 <base target="_self" />
 </head>
  <head>
    <title>发送电子邮件</title>
  </head>
  	<SCRIPT LANGUAGE=javascript src="/js/constant.js"></SCRIPT> 
  <script language='javascript' >
  	function selectTemplate()
  	{
  		document.batchSendMailForm.action="/hire/interviewEvaluating/interviewArrange/initEmailTemplate.do?b_change=change";
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
		
		function sendMail()
		{
			if(document.batchSendMailForm.mailTempID.value.length==0)
			{
				alert(SELECT_EMAIL_TEMPLATE+"!");
				return;
			}
			if(document.batchSendMailForm.content.value.length==0)
			{
				alert("内容不能为空!");
				return;
			}
		  document.getElementById("sendMail01").disabled=true;
		  var rl = document.getElementById("hostname").href;
		  batchSendMailForm.action="/hire/interviewEvaluating/interviewArrange/initEmailTemplate.do?b_send=send&sendtype=0&address="+rl;
		  batchSendMailForm.submit();
		}
		function sendMessage()
		{
		  if(document.batchSendMailForm.mailTempID.value.length==0)
			{
				alert(SELECT_EMAIL_TEMPLATE+"!");
				return;
			}
			if(document.batchSendMailForm.content.value.length==0)
			{
				alert("内容不能为空!");
				return;
			}
		  document.getElementById("sendMessage01").disabled=true;
		  var rl = document.getElementById("hostname").href;
		  batchSendMailForm.action="/hire/interviewEvaluating/interviewArrange/initEmailTemplate.do?b_message=send&sendtype=1&address="+rl;
		  batchSendMailForm.submit();
		}
  <%  
     BatchSendMailForm batchSendMailForm =(BatchSendMailForm)session.getAttribute("batchSendMailForm");
     String flag=batchSendMailForm.getFalg();
	if(flag!=null){
	if(flag.equals("0")){
	out.println("alert('"+ResourceFactory.getProperty("hire.sendemail.success")+"！');");
	out.println("window.returnValue=true;");
	session.removeAttribute("batchSendMailForm");
	out.println("window.close();");
	}else if(flag.equals("1")){
	out.println("alert('"+ResourceFactory.getProperty("hire.system.noconfigserver")+"！');");
	session.removeAttribute("batchSendMailForm");
	out.println("window.close();");
	}else if(flag.equals("2")){
	out.println("alert('"+ResourceFactory.getProperty("hire.emailaddress.erorr")+"！');");
	session.removeAttribute("batchSendMailForm");
	out.println("window.close();");
	}else if(flag.equals("6")){
	out.println("alert('系统未设置移动电话指标参数！');");
	session.removeAttribute("batchSendMailForm");
	}
	else if(flag.equals("7")){
	out.println("alert('系统未设置短信接口参数！');");
	session.removeAttribute("batchSendMailForm");
	out.println("window.close();");
	}
	else if(flag.equals("9")){
	out.println("alert('短信已发送！');");
	out.println("window.returnValue=true;");
	session.removeAttribute("batchSendMailForm");
	out.println("window.close();");
	}
	else if(flag.equals("10")){
	out.println("alert('人员移动电话信息填写错误！');");
	session.removeAttribute("batchSendMailForm");
	out.println("window.close();");
	}
	else if(flag.equals("3")){
	out.println("alert('请选择考官！');");
	session.removeAttribute("batchSendMailForm");
	out.println("window.close();");
	}else if(flag.equals("11")){
		out.println("alert('请选择面试时间！');");
		session.removeAttribute("batchSendMailForm");
		out.println("window.close();");
	}else if(flag.equals("12")){
		out.println("alert('请选择面试地点！');");
		session.removeAttribute("batchSendMailForm");
		out.println("window.close();");
	}else if(flag.equals("tt")){
		out.println("window.close();");
		session.removeAttribute("batchSendMailForm");
	}else if(flag.equals("nexit")){
	
		out.println("window.showModalDialog(\"showAlert.jsp?infos=0\",\"\",\"dialogWidth:600px; dialogHeight:350px;resizable:no;center:yes;scroll:no;status:no\")");
		session.removeAttribute("batchSendMailForm");
		out.println("window.close();");
		
		flag="";
	}else if(flag.equals("allInSended")){
		out.println("alert('没有面试人员处于待通知状态,因此未发送任何邮件！');");
		out.println("window.close();");
	} 
	if(flag.equals("file")){
		String rovkeName=batchSendMailForm.getRovkeName();
		 if(rovkeName!=null&&rovkeName.trim().length()!=0){
	   		//rovkeName=rovkeName.replace("#",".txt");
	   		out.println("var win=open(\"/servlet/vfsservlet?fromjavafolder=true&fileid=decode("+rovkeName+")\");");
	   		//out.println("window.location.href=\"/servlet/DisplayOleContent?filename="+rovkeName);
	   		//setInterval("window.close()",100)
	   		//out.println("session.removeAttribute(\"batchSendMailForm\")");
	   		out.println("window.returnValue=true;");
	   		out.println("setInterval(\"window.close()\",6000)");
	   		//out.println("window.close();");
	   }	
	}
	}
      String url_p=SystemConfig.getServerURL(request);
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
   <input type="hidden" name="isMailField" value="${batchSendMailForm.isMailField}"/>
  		<input type="hidden" name="id" value="${batchSendMailForm.id}"/>
  		<input type="hidden" name="dbname" value="${batchSendMailForm.dbname}"/>
        <input type="hidden" name="a0100" value="${batchSendMailForm.a0100}"/>
        <input type="hidden" name="zp_pos_id" value="${batchSendMailForm.zp_pos_id}"/>
        <input type="hidden" name="zploop" value="${batchSendMailForm.zploop}"/>
        <input type="hidden" name="zpbatch" value="${batchSendMailForm.zpbatch}"/>
        <input type="hidden" name="extendWhereSql" value="${batchSendMailForm.extendWhereSql}"/>
  	<table align='center' style="margin-top:0px; margin-botom:0px;">
  		<tr><td><bean:message key="lable.tz_template.name"/>：</td><td>
  			<html:select name="batchSendMailForm" property="mailTempID"  onchange='selectTemplate()'  size="1">
                              <html:optionsCollection property="mailTempList" value="dataValue" label="dataName"/>
        	</html:select> <a href="<%=url_p%>" style="display:none" id="hostname">for vpn</a>
  		</td></tr>
  		<tr><td><bean:message key="lable.tz_template.title"/>：</td>
  		   <td>
  			<input type='text' value="${batchSendMailForm.title}" name='title'  />
  			<html:hidden name="batchSendMailForm" property="zploop"/>
  		   </td>
  		</tr>
  		<tr><td><bean:message key="hire.inset.field"/>：</td>
  		<td>   
  		
  			<html:select name="batchSendMailForm" property="zbj_id"   size="1">
                              <html:optionsCollection property="zbj_list" value="dataValue" label="dataName"/>
        	</html:select> 
  			
  			<html:select name="batchSendMailForm" property="zb_id"   size="1" onchange="changeFieldItem();"  >
                              <html:optionsCollection property="zb_list" value="dataValue" label="dataName"/>
        	</html:select> 	
  		</td></tr>
  		<tr><td><bean:message key="hire.email.content"/>：</td>
  		<td>   
  		<html:textarea name="batchSendMailForm" property="content"  cols="80" rows="20" >
  		
  		</html:textarea>
  		</td> 		</tr>
  		
  		
  		<tr><td colspan='2' align='center' >
  		    <Input type='button' class='mybutton' id="sendMessage01" value='发送短信' onclick='sendMessage();'  />&nbsp;
  			<Input type='button' class='mybutton' id="sendMail01"  value='<bean:message key="label.zp_employ.sendmail"/>' onclick='sendMail();'  />&nbsp;
  			<Input type='button' class='mybutton'  value=' <bean:message key="button.cancel"/> ' onclick='window.close();' />
  		</td></tr>
  		
  	</table>
  	
  	</html:form>
  </body>
</html>
