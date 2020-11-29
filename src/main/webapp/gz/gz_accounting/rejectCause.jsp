<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.constant.SystemConfig"%>
<%@ page import="com.hjsj.hrms.actionform.gz.gz_accounting.AcountingForm" %>
<html>
  <head>
  <%
  AcountingForm acountingForm=(AcountingForm)session.getAttribute("accountingForm");
  String user_="";
  String user_h="";
  if(request.getParameter("b_confirm")!=null&&request.getParameter("b_confirm").equals("confirm"))
  {
  	user_=acountingForm.getUser_();
  	user_h=acountingForm.getUser_h();
  }
  String fromflag = "";
  if(request.getParameter("fromflag") != null){
	  fromflag = request.getParameter("fromflag");
  }
   %>
   
  </head>
  <SCRIPT LANGUAGE=javascript src="/js/constant.js"></SCRIPT>
  <script language='javascript'>
  var info =[];
  if('<%=fromflag%>' == '1'){
      info.push('');
      info.push('签批意见');
  }else{
      info = window.dialogArguments || parent.opener.dialogArguments;
  }
  	function enter()
  	{
  	<%if(request.getParameter("isMustFill")!=null&&request.getParameter("isMustFill").equals("1")){%>
  	  if(document.form1.cause.value.length==0)
  		{
  			if(!confirm(KH_REJECT_INFO1))
  			   return;
  		}
  	<%}else{%>
  		if(document.form1.cause.value.length==0)
  		{
  			alert(GZ_ACCOUNTING_PLEASEFILL+info[1]+"!");
  			return;
  		}
  		<%}%>
  		var result=new Array();
  		result[0]=document.form1.cause.value;
  		if(info.length==4&&(info[2]=='confirmAll'||info[2]=='confirm')&&info[3]=='1')
	  		result[1]=document.form1.user_h.value;
	  	
	  	if(info.length==4&&(info[2]=='confirmAll'||info[2]=='confirm')&&info[3]=='1')
	  	{	
	  	<% 
	  	  //如果system.properties 添加了 objectMustFill=1，那么需控制薪资批准时，必须填通知对象
	  	   if(SystemConfig.getPropertyValue("objectMustFill")!=null&&SystemConfig.getPropertyValue("objectMustFill").equalsIgnoreCase("1")){
	  	%>
	  		if(trim(document.form1.user_h.value).length==0)
	  		{
	  			alert("批准前请选择通知对象!");
	  			return;
	  		}
	  	<%
	  	   }
	  	 %>	
	  	}	
	  	
	  	
	  	if(parent && parent.parent && parent.parent.Ext && parent.parent.sign_point_ok){
  			parent.parent.sign_point_ok(result);
  			parent.parent.sign_pointWinClose();
  		} else {
	  	    if (window.showModalDialog){
	  	        parent.window.returnValue=result;
            }else if (parent.opener.view_reject_ok){
                parent.opener.view_reject_ok(result)
            }
  		    parent.window.close();
  		}
  		
  	
  	}
  	
  	function clearObjs()
	{
		document.form1.user_.value='';
	   	document.form1.user_h.value='';
	
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
	 				document.form1.user_.value+=","+a_temps[i];
	 				document.form1.user_h.value+=","+temps[i];
	 			}
	 		} 
	 		if(document.form1.user_.value.length>0)
	 		{
	 			if(document.form1.user_.value.charAt(0)==',')
		 			document.form1.user_.value=document.form1.user_.value.substring(1);
	 		}   
	   }
  	
  	}
  	function closeWin(){
  		if(parent && parent.parent && parent.parent.Ext && parent.parent.sign_pointWinClose){
  			parent.parent.sign_pointWinClose();
  		} else {
  			parent.window.close();
  		}
  	}
  </script>
   <script language="javascript" src="/js/validate.js"></script> 
  <link href="/css/css1.css" rel="stylesheet" type="text/css">
  <hrms:themes />
  <body>
  <form name='form1'>
    <table>
    <tr><td colspan='3'>
    <script language='javascript'>
    	document.write(info[1]+":");
    </script>
    </td></tr>
    <tr>
    <td> 
    	<TEXTAREA name='cause' rows="10" style="width:450px;"> </TEXTAREA>
    </td>

    </tr>
    <tr>
        <td  align='center' >
    	<input type='button' class="mybutton"   onclick='enter()' value='<bean:message key="kq.formula.true"/>'  />
    	<input type='button'  class="mybutton"  onclick='closeWin()' value='<bean:message key="lable.content_channel.cancel"/>'  />
    </td>
    </tr>
    <script language='javascript'>
    	var user_str='<%=user_%>';
    	var user_hstr='<%=user_h%>';
    	if(info.length==4&&(info[2]=='confirmAll'||info[2]=='confirm')&&info[3]=='1')
    	{
   			document.write(" <tr><td colspan='3'>通知对象:");
   			document.write("<INPUT type='text' id='user_' value='"+user_str+"' readOnly  class='TEXT2' size='40' maxlength='200'>");
            document.write("<INPUT type='hidden' id='user_h' value='"+user_hstr+"'  size=30>&nbsp;&nbsp;");
    		document.write("<img  src='/images/code.gif' onclick='chooseMen()' />");
    		document.write("&nbsp;<img src='/images/del.gif' title='清空对象' onclick='clearObjs()' />"); 
    		document.write(" </td></tr> ");
    	}
    </script>
    
    </table>
  </form>
  
  
  <script language='javascript' >
  		document.form1.cause.value=info[0];
  </script>
  
  </body>
</html>
