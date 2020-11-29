<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.frame.codec.SafeCode" %>
<%@ page import="com.hjsj.hrms.businessobject.sys.SysParamBo,com.hrms.hjsj.sys.ConstantParamter"%>

<%
	String logintype = request.getParameter("logintype");
	// 用户输入的用户名
	String username = request.getParameter("username");
	username = username == null ? "" : SafeCode.decode(username);
	session.setAttribute("islogon", new Boolean(true));
	
	// 修改密码的方式
	String type = request.getParameter("type");
	// 发起修改密码请求携带的相关信息
	String msg = request.getParameter("msg");
	msg = msg == null ? "" : SafeCode.decode(msg);

	// 样式
	String bosflag = request.getParameter("bosflag");
	String themes = "default";
	if ("hcm".equals(bosflag)) {
		themes = SysParamBo.getSysParamValue("THEMES", username);
		if (themes == null || themes.length() == 0)
			themes = "default";
	}
	
	String corpid = (String) ConstantParamter.getAttribute("wx",
					"corpid");
	
	String dd_corpid = (String) ConstantParamter.getAttribute("dingtalk",
			"corpid");
	
	
	String imgScr = "/servlet/vaildataCode?channel=1&codelen=4&id="+Math.random();
 %>
 <style>
	body{
		margin:0 !important;
	}
</style>

   		<link href="/css/hcm/themes/default/content.css?<%=Math.random() %>" rel="stylesheet"  type="text/css" />

 <script type="text/javascript">

	var msg = "<%=msg%>";
	// 判断用户是否执行修改密码操作
	if (msg.length > 0) {
	    if (msg == '0') {
	    	// 修改密码成功，提示用户，并关闭界面
	        var type = "<%=type%>";
	        alert("已将您的用户名和密码发送到您的" +(type=='3'?"微信": (type == '1' ? "手机": "电子信箱") )+ "，请您注意查收！");
	        //window.close();
			closeWin();
	    } else {
	    	// 修改密码失败，提示用户
	        alert(msg);
	    }
	}
	

function sendmail(obj,logintype)
{
    var desc="用户名";
    var zze=document.getElementById("zzee").value;
   if(trim(zze).length<=0)
   {
      alert(desc+" 输入不能为空！");
      return;
   }
   obj.disabled=true;
   var type="";
   var obj = document.getElementById("sel");
   for(var i=0;i<obj.options.length;i++)
   {
      if(obj.options[i].selected)
       {
          type=obj.options[i].value;
          break;
       }
   }
   
   /*if(type=='1')
       desc="手机号码";
   else
      desc="电子信箱";*/
   
   /*if(type=='1')
   {
      var mm=/^[0-9]{11}$/;///^(((13[0-9]{1})|159|(15[0-9]{1}))+\d{8})$/;
      if(zze.length!=11)
      {
          alert("请输入正确的手机号码，以方便帮你找回密码");
          return;
      }
      if(!mm.test(zze))
      {
          alert("请输入正确的手机号码，以方便帮你找回密码");
          return;
      }
   }
   if(type=='2')
   {
       var mm=/^([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+\.[a-zA-Z]{2,3}$/;
       if(!mm.test(zze))
       {
          alert("请输入正确的邮箱地址，以方便帮你找回密码");
          return;
       }
   }*/
   /*var mm=/^[\d]+.*$/;
   if(mm.test(zze)){
        alert("请输入正确的用户名，以方便帮你找回密码");
        return;
   }*/
   var hashvo=new ParameterSet();
   hashvo.setValue("logintype",logintype);
   hashvo.setValue("type",type); 
   hashvo.setValue("ZE",getEncodeStr(zze)); 
   var request=new Request({method:'post',asynchronous:true,onSuccess:returnInfo,functionId:'30200710241'},hashvo); 
}

	// 通过servlet找回密码，add 2014-12-30 yangj
	function sendmailServlet(obj, logintype) {
		// 用户输入的用户名
	    var zze = document.getElementById("zzee").value;
	    if (trim(zze).length <= 0) {
	        alert("用户名输入不能为空！");
	        return;
	    }
	    // 获取密码的方式
	    var type = "";
	    var sel = document.getElementById("sel");
	    for (var i = 0; i < sel.options.length; i++) {
	        if (sel.options[i].selected) {
	            type = sel.options[i].value;
	            break;
	        }
	    }
	    // 用户输入验证码
	    var validatecode = document.getElementById("validatecode").value;
	    if (trim(validatecode).length <= 0) {
	        alert("验证码不能为空！");
	        return;
	    }
	    obj.disabled = true;
	    // Servlet Form提交表单发起请求
	    var str = "<form action='/servlet/GetPasswordServlet' method=post name=formx1 style='display:none'>";
	    str += "<input name='logintype' value=" + getEncodeStr(logintype) + " />";
	    str += "<input name='type' value=" + getEncodeStr(type) + " />";
	    str += "<input name='ZE' value=" + getEncodeStr(zze) + " />";
	    str += "<input name='validatecode' value=" + getEncodeStr(validatecode) + " />";
	    str += "<input name='validateFlag' value=" + getEncodeStr("1") + " />";
	    str += "</form>";
	    document.getElementById("biaodan").innerHTML = str;
	    document.formx1.submit();
	}

function returnInfo(outparameters)
{
  var msg = outparameters.getValue("msg");
  msg = getDecodeStr(msg);
  if(msg=='0')
  {
     var type=outparameters.getValue("type");
     alert("已将您的用户名和密码发送到您的"+(type=='1'?"手机":"电子信箱")+"，请您注意查收！");
     top.close();
  }
  else
  {
    alert(msg);
    top.close();
  }
   
}
function changeDesc(obj)
{
   var type="";
   for(var i=0;i<obj.options.length;i++)
   {
      if(obj.options[i].selected)
       {
          type=obj.options[i].value;
          break;
       }
   }
   var tdElement=document.getElementById("desc");
   if(type=='1')
   {
     tdElement.innerHTML="请输入手机号码&nbsp;&nbsp;";
   }
   else
   {
      tdElement.innerHTML="请输入电子信箱&nbsp;&nbsp;";
   }
}

function validataCodeReload(){
	var url = document.getElementById('vaildataCode').src;
	document.getElementById('vaildataCode').src = url+"&id=" + Math.random(); 
}

function closeWin(){
		if(top.closePassWin)
			top.closePassWin();
	    else
	    	top.close();
}
//-->
</script>
<html>
	<div id="biaodan" style="display:none;">
		<!-- 用于进行servlet提交 -->
	</div>
	<head>
		<title>找回密码</title>

	</head>
	<!-- 【6810】忘记密码，输入用户名如su，再输入验证码，忘记密码界面就出现2个滚动条了，不对。 jingq upd 2015.02.05 -->
	<html:form action="/gz/gz_analyse/historydata/salary_set_list">
		<table width="360" height="120px;" border="0" cellspacing="0" align="center"
			cellpadding="0" class="ListTable" style="margin:10 0 0 5;">
			<thead>
				<tr>
					<td colspan="2" align="left" class="TableRow">通过手机短信<%if(corpid.length()>0){ %>、微信<%} %><%if(dd_corpid.length()>0){ %>、钉钉<%} %>或电子信箱获取密码</td>
				</tr>
			</thead>
			<tr>
				<td align="right" class="RecordRow">方式&nbsp;&nbsp;</td>
				<td align="left" class="RecordRow">
					&nbsp;
					<select name="type" style="width:155px" id="sel" onchange="">
						<option value="2" >电子信箱</option>
<%if("1".equals(type)) { %>
   						<option value="1" selected=true>手机短信</option>
<%} else { %>
						<option value="1" >手机短信</option>
<%} 
if(corpid.length()>0){//配置微信接口参数后才显示
if("3".equals(type)){
%>
<option value="3" selected=true>微信消息</option>
<%} else{%>
<option value="3" >微信消息</option>
<%}
} %>


<%
if(dd_corpid.length()>0){//配置微信接口参数后才显示
if("4".equals(type)){
%>

<option value="4" selected=true>钉钉消息</option>
<%} else{%>
<option value="4" >钉钉消息</option>
<%}
} %>



					</select>
				</td>
			</tr>
			<tr>
				<td id="desc" align="right" class="RecordRow">请输入用户名&nbsp;&nbsp;</td>
				<td align="left" class="RecordRow">
					&nbsp; <input id="zzee" type="text" class="inputtext" name="ZE" value="<%=username %>" style="width:155px" size="15" />
				</td>
			</tr>
			<tr>
				<td id="desc" align="right" class="RecordRow">验证码&nbsp;&nbsp;</td>
				<td align="left" class="RecordRow" vAlign="middle">
					&nbsp; <input class="s_input inputtext" id="validatecode" size="8" type="text" value="" name="validatecode" /> 
					&nbsp; <img onclick="validataCodeReload()" title="换一张" align="absMiddle" src="<%=imgScr %>" id="vaildataCode">
				</td>
			</tr>
			<tr>
				<td align="left" colspan="2">
					<table width="100%" cellpadding="0" cellspacing="0" border="0">
						<tr>
							<td height="35px" align="center">
								<input type="hidden" value="<%=session.getAttribute("validatecode")%>" id="validates"/>
								<input type="button" class="mybutton" name="oo" value="<bean:message key="button.ok"/>" onclick='sendmailServlet(this,"<%=logintype%>");' />
								&nbsp;
								<input type="button" class="mybutton" name="cc" value="<bean:message key="button.close"/>" onclick="closeWin()" />
							</td>
						</tr>
					</table>
				</td>
			</tr>
		</table>
	</html:form>
</html>
<script>
if(!getBrowseVersion() || getBrowseVersion() == 10){//非ie浏览器样式修改  wangb 20190524 bug 44221
	parent.document.getElementById('childFrame').setAttribute('height','90%');
}
</script>