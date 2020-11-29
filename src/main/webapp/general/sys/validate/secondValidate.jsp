<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.constant.SystemConfig"%>
<%@ page import="com.hjsj.hrms.actionform.general.sys.validate.SecondValidateForm" %>
<link href="/css/css1.css" rel="stylesheet" type="text/css">
				 				 
<html>
<head>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
</head>
<style>
	.table1{
	width:450;
	height:200;
	font-size:12px;
	BORDER-BOTTOM:#94B6E6 1pt solid; 
	BORDER-LEFT: #94B6E6 1pt solid; 
	BORDER-RIGHT: #94B6E6 1pt solid; 
	BORDER-TOP: #94B6E6 1pt solid; 
}
	.table2{	
	width:450;	
	font-size:12px;
	BORDER-BOTTOM:#94B6E6 0pt solid; 
	BORDER-LEFT: #94B6E6 0pt solid; 
	BORDER-RIGHT: #94B6E6 0pt solid; 
	BORDER-TOP: #94B6E6 0pt solid; 
}
    .td1{
    font-size:12px;
    border-left:#94B6E6 0pt solid;
    border-top:#94B6E6 0pt solid;
    border-bottom:#94B6E6 1pt solid;
    border-right: #94B6E6 0pt solid; 
    
}
 .td2{
    border-left:#94B6E6 1pt solid;
    border-top:#94B6E6 0pt solid;
    border-bottom:#94B6E6 1pt solid;
    border-right: #94B6E6 0pt solid; 
    white-space:nowrap;
    overflow: hidden;
}
#inputcode{
    height:20;
    font-size:15px;
    valign:center;
    border-left:#94B6E6 1pt solid;
    border-top:#94B6E6 1pt solid;
    border-bottom:#94B6E6 1pt solid;
    border-right: #94B6E6 1pt solid;
}

</style>
<%
String validatecode_time = "180";
if(SystemConfig.getPropertyValue("validatecode_time")!=null&&SystemConfig.getPropertyValue("validatecode_time").length()>0)
	validatecode_time=SystemConfig.getPropertyValue("validatecode_time");
%>

  
	<body>
		<html:form action="/general/sys/validate/secondValidate">
		<div style="position:absolute; top:15px; left:10px; ">
			<table class="table1" align="center"  cellspacing="0" cellpadding="2">
				<tr style="background-color:#f4f7f7;">
					<td class="td1" align="left" Colspan="2" height="14%">
					   &nbsp;&nbsp;手机验证码
					</td>
				</tr>
				<tr>
					<input type="hidden" id="content">
					<td class="td1" align="right" width="25%"  height="15%">&nbsp;手机号码：</td>
					<td class="td2" align="left" width="75%"  height="15%">
					    <%
					       SecondValidateForm svf = (SecondValidateForm)session.getAttribute("secondValidateForm");
					       String phoneNumber = svf.getPhoneNumber();
					       if("".equalsIgnoreCase(phoneNumber)){
					    %>
					                  手机号码为空，无法获取验证码
					    <%
					       }else{
					    %>
					       <bean:write name="secondValidateForm" property="passPhone" filter="true" />
					    <%
					       }
					     %>
					</td>
				</tr>
				<tr>
                    <td class="td1" align="right" width="25%" height="15%">&nbsp;验证码：</td>
                    <td class="td2" align="left" width="75%"  height="15%">
	                    <table border="0">
	                        <tr>
	                            <td>
	                            <%
	                               if("".equalsIgnoreCase(phoneNumber)){
	                             %>
	                               <input type="text" id="inputcode" size="10" >&nbsp;&nbsp;<a id="validatecode" href="#" disabled="true">获取验证码</a>
	                             <%
	                               }else{
	                             %>
	                               <input type="text" id="inputcode" size="10" >&nbsp;&nbsp;<a id="validatecode" href="javascript:sendValidateCode();">获取验证码</a>
	                             <%
	                               }
	                              %>
	                                
	                            </td>
	                            <td valign="bottom">
	                               <span id="totalSecond" style="display:none;">
				                         <font color="red"><span id="setSecond"><%=validatecode_time %></span>秒</font> 
				                   </span>
	                            </td>
	                        </tr>
	                    </table>
                   </td>
                </tr>
				<tr>
				   <td Colspan="2" height="56%" valign="top">
				   <br>
					   <table booder="0">
					       <tr>
					           <td align="left"" Colspan="2" >提示：</td>
					       </tr>
					       <tr>
					           <td align="right">&nbsp;&nbsp;&nbsp;&nbsp;</td><td  align="left">1.请确认上述手机号码是否为接收短信验证码的正确号码。如有误，请不要点击“获取验证码”，并尽快联系您的人力资源主管进行信息更正。</td>
					       </tr>
					       <tr>
					           <td align="right">&nbsp;&nbsp;&nbsp;&nbsp;</td><td align="left">2.点击获取验证码后，短信可能由于网路等原因有所延迟，如果您在<%=validatecode_time %>秒内手机没有收到短信验证码，请重新获取。</td>
					       </tr>
					       <tr>
                               <td align="right">&nbsp;&nbsp;&nbsp;&nbsp;</td><td align="left">3.验证通过后才能进入该模块。</td>
                           </tr>
					   </table>
                   </td>
				</tr>
			</table>
		</div>
		<div style="position:absolute; top:220px;">
			<table  align="center" class="table2" >
	          <tr>
	            <td align="center">
	                <input type="button" class="mybutton" name="button" value="<bean:message key="button.ok"/>" onclick="sub();" />
	                &nbsp;&nbsp;&nbsp;&nbsp;
	                <input type="button" class="mybutton" name="button" value="<bean:message key="button.cancel"/>" onclick="window.close();" />
	            </td>
	          </tr>          
	        </table>
        </div>
	   </html:form>
  </body>
</html>
<script type="text/javascript">

	var second = document.getElementById('totalSecond').textContent;
	var interval;
	function executeTimer()
	{
	    document.getElementById('totalSecond').style.display="block";
		second = document.getElementById('setSecond').innerText; 
		interval=setInterval("redirect()", 1000);
	}
	
	function redirect()
	{
		if (second < 0)
		{
			//让超链接可用，并改变文本。然后清空content内容
			document.getElementById("validatecode").setAttribute("href","javascript:sendValidateCode();");
			document.getElementById("validatecode").setAttribute("disabled",false);
			//document.getElementById('getValidateCode').innerText="重新获得校验码";
			document.getElementById('totalSecond').style.display="none";
			document.getElementById("content").value="";
			clearInterval(interval);//关掉定时器
		} 
		else
		{
			document.getElementById('setSecond').innerText = second--; 
		}
	}
	
	function sendValidateCode()
	{
		//点击发送，则时间设为最大值，等执行完ajax，开始倒计时
		second="<%=validatecode_time %>";
		document.getElementById('setSecond').innerText = second;
		
		//将超链接置灰并实效
		document.getElementById("validatecode").setAttribute("disabled",true);
		document.getElementById("validatecode").removeAttribute("href");
		//发送校验
		var phoneNumber="${secondValidateForm.phoneNumber}";
		var hashvo=new ParameterSet(); 
		hashvo.setValue("phoneNumber",getEncodeStr(phoneNumber));
   		var request=new Request({asynchronous:false,onSuccess:send_ok,functionId:'0202011020'},hashvo);
	}
	function send_ok(outparamters)
	{
		var error = outparamters.getValue("error");
		var content=outparamters.getValue("content");
		if(error==1)
		{
			alert(content);
			window.close();
		}
		else
		{
			//开启定时器
			executeTimer();
			document.getElementById("content").value=content;
		}
	}
	function sub()
	{
		var s=document.getElementById("inputcode").value;
		var checkcode = document.getElementById("content").value;
		if(s.length==0)
		{
			alert("请输入验证码!");
			return false; 
		}
		if(s!=checkcode){
		  alert("验证码不正确,请确认您的验证码");
		  return false;
		}
		var contentvo=new Object();
	    contentvo.content = document.getElementById("content").value;
	    contentvo.inputcode = document.getElementById("inputcode").value;
	    window.returnValue=contentvo;
	    window.close();
	}
</script>
