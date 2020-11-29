<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page language="java" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.frame.codec.SafeCode" %>
<%
 String logintype=request.getParameter("logintype");
 String username=request.getParameter("username");
 username=username==null?"":SafeCode.decode(username);
 %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>移动助手</title>
<link rel="stylesheet" href="/phone-app/jquery/css/jquery.mobile-1.0a2.min.css" type="text/css">
	 <script type="text/javascript" src="/phone-app/jquery/jquery-3.5.1.min.js"></script>
	 <script type="text/javascript" src="/phone-app/jquery/rpc_command.js"></script>
<script type="text/javascript">
<!--
function reNew(str)
{
    var re;
	re=/%26amp;/g;
	str=str.replace(re,"&");
	re=/%26apos;/g;  
	str=str.replace(re,"'");
	re=/%26lt;/g;  
	str=str.replace(re,"<");
	re=/%26gt;/g;  
	str=str.replace(re,">");
	re=/%26quot;/g;  
	str=str.replace(re,"\"");
	re=/%25/g;
	str=str.replace(re,"%");
	re=/````/g;
	str=str.replace(re,",");
	return(str);		
}
/****************************
 *取得合法的字符串
 ****************************/
function getValidStr(str) 
{
	str += "";
	if (str=="undefined" || str=="null" || str=="NaN")
		return "";
	else
		return reNew(str);
		
}
/******************************************
 *字符串解码,汉字传输过程中出现乱码问题
 *解码规则:1) ~43~48~45~4e~48~41~4f
 *         2) ^7a0b^7389
 ******************************************/
function decode(strIn)
{
	var intLen = strIn.length;
	var strOut = "";
	var strTemp;

	for(var i=0; i<intLen; i++)
	{
		strTemp = strIn.charAt(i);
		switch (strTemp)
		{
			case "~":{
				strTemp = strIn.substring(i+1, i+3);
				strTemp = parseInt(strTemp, 16);
				strTemp = String.fromCharCode(strTemp);
				strOut = strOut+strTemp;
				i += 2;
				break;
			}
			case "^":{
				strTemp = strIn.substring(i+1, i+5);
				strTemp = parseInt(strTemp,16);
				strTemp = String.fromCharCode(strTemp);
				strOut = strOut+strTemp;
				i += 4;
				break;
			}
			default:{
				strOut = strOut+strTemp;
				break;
			}
		}

	}
	return (strOut);
}
function encode(strIn)
{
	var intLen=strIn.length;
	var strOut="";
	var strTemp;

	for(var i=0; i<intLen; i++)
	{
		strTemp=strIn.charCodeAt(i);
		if (strTemp>255)
		{
			tmp = strTemp.toString(16);
			for(var j=tmp.length; j<4; j++) tmp = "0"+tmp;
			strOut = strOut+"^"+tmp;
		}
		else
		{
			if (strTemp < 48 || (strTemp > 57 && strTemp < 65) || (strTemp > 90 && strTemp < 97) || strTemp > 122)
			{
				tmp = strTemp.toString(16);
				for(var j=tmp.length; j<2; j++) tmp = "0"+tmp;
				strOut = strOut+"~"+tmp;
			}
			else
			{
				strOut=strOut+strIn.charAt(i);
			}
		}
	}
	return (strOut);
}
/*******************************
 *字符串进行编码
 *******************************/

function getDecodeStr(str) {
	return ((str)?decode(getValidStr(str)):"");
}
function getEncodeStr(str) {
	return encode(getValidStr(str));
}
function trim(s)
{ 
	return s.replace(/^\s+|\s+$/, ''); 
}
function sendmail(obj,logintype)
{
	var desc="用户名";
   /*if(type=='1')
       desc="手机号码";
   else
      desc="电子信箱";*/
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
   {
      var mm=/^[0-9]{11}$/;
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
   var mm=/^[\d]+.*$/;
   if(mm.test(zze)){
   		alert("请输入正确的用户名，以方便帮你找回密码");
   		return;
   }
   var map = new HashMap();
				map.put("logintype","logintype");
				map.put("type",type);
				map.put("ZE",getEncodeStr(zze));
			   　Rpc({functionId:'30200710241',success:returnInfo},map);
}
function returnInfo(html)
{
  var value=html;
  var map=JSON.parse(value);
  if(map.succeed){
	  var msg = map.msg;
	  msg = getDecodeStr(msg);
	  if(msg=='0')
	  {
	     var type=map.type;
	     alert("已将您的用户名和密码发送到您的"+(type=='1'?"手机":"电子信箱")+"，请您注意查收！");
	  	window.location.href="/phone-app/index.jsp";
	  }
	  else
	  {
	    alert(msg);
	    window.location.href="/phone-app/index.jsp";
	  }
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
     tdElement.innerHTML="手机号码:";
   }
   else
   {
      tdElement.innerHTML="电子信箱:";
   }
}
//-->
</script>
<script type="text/javascript" src="/phone-app/jquery/jquery.mobile-1.0a2.min.js"></script>	
</head>
<body>
<div data-role="page" id="mainbar"> 
	<div data-role="header"  data-position="fixed" data-position="inline"> 
		<a href="/phone-app/index.jsp" data-role="button" data-icon="forward" data-rel="dialog" data-transition="pop" rel="external">返回</a>
		<h1>获取密码</h1>
	</div>
	<div data-role="content" id="qmain">
				<div data-role="fieldcontain">
					<label for="sel" class="select">方式：</label>
					<select id="sel" onchange="">
						<option value="2">电子信箱</option>
						<option value="1">手机短信</option>
					</select>
				</div>
				<div data-role="fieldcontain">
				    <label for="zzee" id=desc>用户名：</label>
				    <input type="text" id="zzee" value="<%=username %>"  />
				</div>
				<button type="button" data-theme="a" onclick='sendmail(this,"<%=logintype%>");'>确定</button> 
	</div>
</div>
</body>
</html>