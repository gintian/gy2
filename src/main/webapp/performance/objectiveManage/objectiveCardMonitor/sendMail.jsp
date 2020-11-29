<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="java.util.*" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<hrms:themes />

<%
	String opt=request.getParameter("opt"); // 1:目标卡状态  2：打分状态 主体  3: 目标执行（回顾情况） 4：目标执行情况
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<script language="JavaScript" src="/js/function.js"></script>
<script language="javascript" src="/ajax/command.js"></script>
<script language='javascript' >
///获得 绩效管理 填报状态的参数
//	var info = dialogArguments;  
function closeWin(){
	//非ie 无法关闭的问题
	if(/msie/i.test(navigator.userAgent))
		parent.window.close();
	else if(parent.opener)// bug 35044  open弹窗无法关闭   wangb 20180301
		parent.window.close();
	else if(parent.parent.Ext){
        parent.parent.Ext.getCmp("sendMessageWin").close();
    }else{
        parent.window.close();
    }

}
function selectField()
{ 
	var m = document.setUnderlingObjectiveForm.field.value; 
  	insertTxt(m);
}
  	 
function insertTxt(strtxt)
{		 
	if(strtxt==null||strtxt=='')
		return ; 
	var expr_editor=$('content');
	expr_editor.focus();
	var element;
	if(document.selection){//IE浏览器下操作 
		element = document.selection;
		if (element!=null) 
		{
			var rge = element.createRange();
			if (rge!=null)	
				rge.text=strtxt;
		}	
	}else{//非IE浏览器下操作 wangb 20180105
		element = window.getSelection();
		var start =expr_editor.selectionStart;
		expr_editor.value = expr_editor.value.substring(0,start)+strtxt+expr_editor.value.substring(start,expr_editor.value.length);
		expr_editor.setSelectionRange(start+strtxt.length,start+strtxt.length);
	}
			
}

function sendMessage(flag)
{
 	if(flag=='2')
 	{
	 	if(trim(document.setUnderlingObjectiveForm.title.value).length==0)
		{
			alert("标题不能为空!");
			return;
		}
 	}
 			
	if(trim(document.setUnderlingObjectiveForm.content.value).length==0)
	{
		alert("内容不能为空!");
		return;
	}
	
	 if(flag=='2')//bug 35519 发送短信成功后发送短信和发送邮件按钮不变灰
	{ 
		var waitInfo=eval("wait");			
	  	waitInfo.style.display="block";
	   	document.getElementById("sendEmail").disabled=true;
	   	//document.getElementById("clo").disabled=true;//取消按钮不置灰,否则没法关闭窗口了 chent 20170418
	 } 
	if(flag=='1')//bug 35519 发送短信成功后发送短信和发送邮件按钮不变灰
	{ 
		var waitInfo=eval("wait");			
	  	waitInfo.style.display="block";
	   	document.getElementById("sendMess").disabled=true;
	   	//document.getElementById("clo").disabled=true;//取消按钮不置灰,否则没法关闭窗口了 chent 20170418
	 } 
	var hashVo=new ParameterSet();
   	hashVo.setValue("opt","<%=(request.getParameter("opt"))%>");  // 1:目标卡状态  2：打分状态 主体  3: 目标执行（回顾情况） 4：目标执行情况
   	<%if(request.getParameter("logo")!=null){%>
   		hashVo.setValue("logo","<%=(request.getParameter("logo"))%>");
   	<%}%>
   	hashVo.setValue("plan_id","<%=(request.getParameter("plan_id"))%>");
   	hashVo.setValue("object_id","<%=(request.getParameter("object_id"))%>");
   	hashVo.setValue("to_a0100","<%=(request.getParameter("to_a0100"))%>");
   	hashVo.setValue("departid","<%=(request.getParameter("departid"))%>");
   	hashVo.setValue("name","<%=(request.getParameter("name"))%>");
   	hashVo.setValue("isAll","<%=(request.getParameter("isAll"))%>");  
   	hashVo.setValue("flag",flag);
   	hashVo.setValue("title",getEncodeStr(document.setUnderlingObjectiveForm.title.value));
   	hashVo.setValue("content",getEncodeStr(document.setUnderlingObjectiveForm.content.value));
   	hashVo.setValue("objMainbodys","${setUnderlingObjectiveForm.objMainbodys}");
   	//hashVo.setValue("numberlist",getEncodeStr(info[0]));
   	//hashVo.setValue("strFrom",getEncodeStr(info[1]));
   	var request=new Request({method:'post',asynchronous:true,onSuccess:send_ok,functionId:'9028000318'},hashVo);
}

function send_ok(outparameters)
{
	var info=outparameters.getValue("info");
	alert(getDecodeStr(info));
	var waitInfo=eval("wait");			
	waitInfo.style.display="none";
	closeWin();
}

function jinduo_tiao()
{
 	var x=document.body.scrollLeft+100;
    var y=document.body.scrollTop+70; 
	var waitInfo;
	waitInfo=eval("wait");	
	waitInfo.style.top=y;
	waitInfo.style.left=x;	
	waitInfo.style.display="block";
}

</script>

<body>
<html:form action="/performance/objectiveManage/objectiveCardMonitor/objective_state_list">

  	<table align='center'  width="680px" border="0" cellspacing="5"  align="center" cellpadding="0">
  		 
  		<tr height="35"><td align="right"><bean:message key="lable.tz_template.title"/></td>
  		   <td>
  			<input type='text' value="${setUnderlingObjectiveForm.subject}" name='title'  size='50' class="inputtext" />
  		   </td>
  		</tr>
  		<tr height="35"><td><bean:message key="hire.inset.field"/></td>
  		<td>   
  		 	<select name='field'  onchange='selectField()' >
  		 		<option value=''></option> 
  		 	<% if(opt!=null && opt.equals("2")){ %>
  		 	
  		 		<option value='＃发件人名称＃' >＃发件人名称＃</option> 
  		 		<option value='＃考核主体名称＃' >＃考核主体名称＃</option> 
  		 		<option value='＃考核计划名称＃' >＃考核计划名称＃</option>  
  		 		<option value='＃系统时间＃' >＃系统时间＃</option>  
  		 		  		 	
  		 	<% }else if(opt!=null && (opt.equals("3") || opt.equals("4"))){ %>
  		 	
  		 		<option value='＃发件人名称＃' >＃发件人名称＃</option> 
  		 		<option value='＃考核对象名称＃' >＃考核对象名称＃</option> 
  		 		<option value='＃考核主体名称＃' >＃考核主体名称＃</option> 
  		 		<option value='＃考核计划名称＃' >＃考核计划名称＃</option>  
  		 		<option value='＃系统时间＃' >＃系统时间＃</option>
  		 		
  		 	<% }else if(opt!=null && (opt.equals("5"))){ %>
  		 		<option value='＃发件人名称＃' >＃发件人名称＃</option> 
  		 		<option value='＃收件人名称＃' >＃收件人名称＃</option> 
  		 		<option value='＃审批人名称＃' >＃审批人名称＃</option> 
  		 		<option value='＃系统时间＃' >＃系统时间＃</option> 
  		 		
  		 	<% }else{ %>
  		 	
  		 		<option value='＃发件人名称＃' >＃发件人名称＃</option> 
  		 		<option value='＃审批人名称＃' >＃审批人名称＃</option> 
  		 		<option value='＃目标对象名称＃' >＃目标对象名称＃</option> 
  		 		<option value='＃目标计划名称＃' >＃目标计划名称＃</option>  
  		 		<option value='＃系统时间＃' >＃系统时间＃</option> 
  		 		  
  		 	<% } %>
  		 	</select>
  		</td></tr>
  		<tr><td align="right"><bean:message key="hire.email.content"/></td>
  		<td>   
  		<textarea name='content' cols="97" rows="16" >${setUnderlingObjectiveForm.content}
  		</textarea>
  		</td> 		
  		</tr> 		
  		
  		<tr><td colspan='2' align='center' >
  		<Input type='button' class='mybutton' id="sendMess" value='发送短信' onclick='sendMessage(1);'  />
  			<Input type='button' class='mybutton' id="sendEmail" value='<bean:message key="menu.gz.sendmail"/>' onclick='sendMessage(2);'  />
  			<Input type='button' class='mybutton' id="clo" value=' <bean:message key="button.cancel"/> ' onclick='closeWin();' />
  		</td></tr>
  		
  	</table>
  	
  	<div id='wait' style='position:absolute;top:160;left:250;display:none;'>
  		<table border="1" width="300" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
           <tr>
             <td id="wait_desc" class="td_style" height=24>正在发送邮件，请稍候....</td>
           </tr>
           <tr>
             <td style="font-size:12px;line-height:200%" align=center>
               <marquee class="marquee_style" direction="right" width="260" scrollamount="5" scrolldelay="10" >
                 <table cellspacing="1" cellpadding="0">
                   <tr height=8>
                     <td bgcolor=#3399FF width=8></td>
                         <td></td>
                         <td bgcolor=#3399FF width=8></td>
                         <td></td>
                         <td bgcolor=#3399FF width=8></td>
                         <td></td>
                         <td bgcolor=#3399FF width=8></td>
                         <td></td>
                    </tr>
                  </table>
               </marquee>
             </td>
          </tr>
        </table>
	</div> 	
  	
</html:form> 
</body>
</html>