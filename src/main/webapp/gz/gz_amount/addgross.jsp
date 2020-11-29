<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.frame.utility.DateStyle"%>
<%
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String userName=null;
	if(userView != null)
	{
	  userName = userView.getUserFullName();
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	css_url="/css/css1.css";
	}
	String date = DateStyle.getSystemDate().getDateString();
%>
<HTML>
<HEAD>
<TITLE>
<% if(userName!=null){ %>　用户名：<%=userName%> <% } %>　当前日期：<%=date%>
</TITLE>
<link href="<%=css_url%>" rel="stylesheet" type="text/css">
<link href="/css/xtree.css" rel="stylesheet" type="text/css" >
<style type="text/css">
.m_arrow {
	width: 16px;
	height: 8px;
	font-family: "Webdings";
	font-size: 7px;
	line-height: 2px;
	padding-left: 2px;
	cursor: default;
}
</style>
<hrms:themes />
<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>   

</HEAD>
<body>

<table align="center" width="545px" border="0" style="margin-left:-5px;">
	<tr>
		<td align="center">
			<fieldset align="center" style="width:100%;">
			<legend><bean:message key="gz.acount.add.salaries.records"/></legend>
			<table border="0" width="80%" cellspacing="0" align="center" cellpadding="0">
				<tr>
					<!-- xus 19/12/31 【56889】v771封版：薪资管理/薪资总额，新增的时候界面比较的丑，请将其和下方对齐。-->
					<td width="90" align="right" height="50"><bean:message key="gz.acount.salaries.year"/></td>
					<td colspan="5">
						<table width="50%" border="0" cellspacing="0" cellpadding="0">
							<tr>                                 
								<td valign="middle" align="right">
									<input type="text" name="yearnum" style="width:100" maxlength="4" class="inputtext">                     
								</td>
								<td valign="middle" align="left">
									<table border="0" cellspacing="2" cellpadding="0" >
										<tr><td><button id="y_up" class="m_arrow" onclick="yincrease();">5</button></td></tr>
										<tr><td><button id="y_down" class="m_arrow" onclick="ysubtract();">6</button></td></tr>
									</table>
								</td>

							</tr>
						</table>
					</td>
				</tr>
				<logic:equal name="croPayMentForm" property="ctrl_peroid" value="1">
				<tr style="display=none">
					<td align="right" height="50"><bean:message key="gz.acount.month"/><bean:message key="label.from"/></td>
					<td>
						<table width="100%" border="0" cellspacing="0" cellpadding="0" >
							<tr> 	
								<td align="right" style="padding-left:12px;"> 
									<input type="text" name="monthnum1" value="1" style="width:40" maxlength="2" class="inputtext">                     
								</td>
								<td valign="middle" align="left">
									<table border="0" cellspacing="2" cellpadding="0">
										<tr><td><button id="m_up" class="m_arrow" onclick="mincrease1();">5</button></td></tr>
										<tr><td><button id="m_down" class="m_arrow" onclick="msubtract1();">6</button></td></tr>
									</table>		
								</td>
							</tr>
						</table>
					</td>
					<td><bean:message key="label.query.to"/></td>
					<td>    
						<table width="100%" border="0" cellspacing="0" cellpadding="0" >
							<tr> 
								<td align="right"> 
									<input type="text" name="monthnum2" value="12" style="width:40" maxlength="2" class="inputtext">                     
								</td>
								<td valign="middle" align="left">
									<table border="0" cellspacing="2" cellpadding="0">
										<tr><td><button id="m_up" class="m_arrow" onclick="mincrease2();">5</button></td></tr>
										<tr><td><button id="m_down" class="m_arrow" onclick="msubtract2();">6</button></td></tr>
									</table>		
								</td>
							</tr>
						</table>
					</td>
					<td><bean:message key="datestyle.month"/></td>       
				</tr>
				</logic:equal>
				<logic:equal name="croPayMentForm" property="ctrl_peroid" value="2">
				<tr style="display=block">
					<td align="right" height="50"><bean:message key="kq.wizard.quarter"/><bean:message key="label.from"/></td>
					<td>
						<table width="100%" border="0" cellspacing="0" cellpadding="0" >
							<tr> 
								<td align="right" style="padding-left:12px;"> 
									<input type="text" name="monthnum1" value="1" style="width:50" maxlength="2" class="inputtext">                     
								</td>
								<td valign="middle" align="left">
									<table border="0" cellspacing="2" cellpadding="0">
										<tr><td><button id="m_up" class="m_arrow" onclick="mincrease4();">5</button></td></tr>
										<tr><td><button id="m_down" class="m_arrow" onclick="msubtract1();">6</button></td></tr>
									</table>		
								</td>
							</tr>
						</table>
					</td>
					<td align="right"><bean:message key="label.query.to"/></td>
					<td>    
						<table width="100%" border="0" cellspacing="0" cellpadding="0" >
							<tr> 
								<td align="right"> 
									<input type="text" name="monthnum2" value="4" style="width:50" maxlength="2" class="inputtext">                     
								</td>
								<td valign="middle" align="left">
									<table border="0" cellspacing="2" cellpadding="0">
										<tr><td><button id="m_up" class="m_arrow" onclick="mincrease3();">5</button></td></tr>
										<tr><td><button id="m_down" class="m_arrow" onclick="msubtract2();">6</button></td></tr>
									</table>		
								</td>
							</tr>
						</table>
					</td>
					<td><bean:message key="kq.wizard.quarter"/></td>       
				</tr>
				</logic:equal>
				<logic:equal name="croPayMentForm" property="ctrl_peroid" value="0">
				<tr>
					<td align="right" height="50"><bean:message key="gz.acount.month"/><bean:message key="label.from"/></td>
					<td>
						<table width="100%" border="0" cellspacing="0" cellpadding="0" >
							<tr> 
								<td align="center" style="padding-left:12px;"> 
									<input type="text" name="monthnum1" value="1" style="width:40" maxlength="2" class="inputtext" >                     
								</td>
								<td valign="middle" align="left">
									<table border="0" cellspacing="2" cellpadding="0">
										<tr><td><button id="m_up" class="m_arrow" onclick="mincrease1();">5</button></td></tr>
										<tr><td><button id="m_down" class="m_arrow" onclick="msubtract1();">6</button></td></tr>
									</table>		
								</td>
							</tr>
						</table>
					</td>
					<td><bean:message key="label.query.to"/></td>
					<td>    
						<table width="100%" border="0" cellspacing="0" cellpadding="0" >
							<tr> 

								<td align="right"> 
									<input type="text" name="monthnum2" value="12" style="width:40" maxlength="2" class="inputtext">                     
								</td>
								<td valign="middle" align="left">
									<table border="0" cellspacing="2" cellpadding="0">
										<tr><td><button id="m_up" class="m_arrow" onclick="mincrease2();">5</button></td></tr>
										<tr><td><button id="m_down" class="m_arrow" onclick="msubtract2();">6</button></td></tr>
									</table>		
								</td>
							</tr>
						</table>
					</td>
					<td><bean:message key="datestyle.month"/></td>       
				</tr>
				</logic:equal>
				
			</table>
			<table border="0" width="100%" cellspacing="0" align="center" cellpadding="0">
			<tr><td align="center" colspan="100">
			<table><tr><td align="center" width="25%" nowrap="nowrap">
			<input type="checkbox" name="createType" value="1" checked nowrap/>当前选中机构
			</td>
			<td align="center" width="25%">
			<input type="checkbox" name="createNextType" value="1" nowrap/>下一级机构
			</td>
			<td align="center" width="50%">
			<input type="checkbox" name="createAllNextType" value="1" nowrap/>所有下级机构（不包含部门）
			</td>
			</tr>
			</table>
			</td></tr>
			<tr><td align="center" colspan="100">
			&nbsp;
			</td>
			</tr>
			</table>
			</fieldset>
		</td>
	</tr>
	<tr>
		<td style="padding-top:3px;" align="center">
		<button name="confset" Class="mybutton" onclick="ok();"><bean:message key="button.ok"/></button>
		<button name="button_view" Class="mybutton" onclick="window.close();"><bean:message key="button.cancel"/></button>
		</td>
	</tr>
</table>
<script language="JavaScript">
var now = new Date();
var yearnum = now.getYear(); 
document.getElementById("yearnum").value=yearnum;
var yearset = parseInt(yearnum);

var monthnum1=document.getElementById("monthnum1").value;
var monthset1 = parseInt(monthnum1);

var monthnum2=document.getElementById("monthnum2").value;
var monthset2 = parseInt(monthnum2);
function yincrease(){
	yearset = yearset+1;
	document.all.yearnum.value = yearset;
}
function ysubtract(){
	if(yearset<1991){
		document.all.yearnum.value = 1990;
	}else{
		yearset = yearset-1;
		document.all.yearnum.value = yearset;
	}
}
function mincrease1()
{
	if(monthset1>11){
		document.all.monthnum1.value = monthset1;
	}else{
		monthset1 = monthset1+1;
		document.all.monthnum1.value = monthset1;
	}

}
function mincrease4()
{
	if(monthset1>3){
		document.all.monthnum1.value = monthset1;
	}else{
		monthset1 = monthset1+1;
		document.all.monthnum1.value = monthset1;
	}

}
function msubtract1(){
	if(monthset1<2){
		document.all.monthnum1.value = 1;
	}else{
		monthset1 = monthset1-1;
		document.all.monthnum1.value = monthset1;
	}
}
function mincrease2()
{
	if(monthset2>11){
		document.all.monthnum2.value = monthset2;
	}else{
		monthset2 = monthset2+1;
		document.all.monthnum2.value = monthset2;
	}

}
function mincrease3()
{
	if(monthset2>3){
		document.all.monthnum2.value = monthset2;
	}else{
		monthset2 = monthset2+1;
		document.all.monthnum2.value = monthset2;
	}

}
function msubtract2(){
	if(monthset2<2){
		document.all.monthnum2.value = 1;
	}else{
		monthset2 = monthset2-1;
		document.all.monthnum2.value = monthset2;
	}
}
function ok(){
	var year=document.getElementById("yearnum").value;
	if(isqcode(year)){
		year= zuzhuang(year)
	}	
	var r = /^[0-9]*[1-9][0-9]*$/;	
	if(r.test(year)){
		var getyear = parseInt(year);
		if(getyear<1990){
			alert("<bean:message key='gz.acount.year.suggested'/>");
			return;
		}
	}else{
		alert("请输入正确的年份！");
		return;
	}

	var month1=document.getElementById("monthnum1").value;
	if(isqcode(month1)){
		month1= zuzhuang(month1)
	}	
	if(r.test(month1)){
		var getmonth1 = parseInt(month1);
		if(getmonth1>12||getmonth1<1){
			alert("<bean:message key='gz.acount.start.month.suggested'/>");
			return;
		}
	}else{
		alert("请输入正确的起始季度或月份！");
		return;
	}

	var month2=document.getElementById("monthnum2").value;
	if(isqcode(month2)){
		month2= zuzhuang(month2)
	}	
	if(r.test(month2)){
		var getmonth2 = parseInt(month2);
		if(getmonth2>12||getmonth2<1){
			alert("<bean:message key='gz.acount.stop.month.suggested'/>");
			return;
		}
		
		if(getmonth1>getmonth2){
			alert("<bean:message key='gz.acount.month.suggested'/>");
			return;
		}
	}else{
		alert("请输入正确的结束季度或月份！");
		return;
	}
	var val="0";
	var createType=document.getElementById("createType");
	if(createType.checked)
	    val="1";
	var val2="0";
	var createNextType=document.getElementById("createNextType");
	if(createNextType.checked)
	   val2="1";
	var val3="0";
	var createAllNextType=document.getElementById("createAllNextType");
	if(createAllNextType.checked)
	   val3="1";
	if(val == 0 && val2 == 0 && val3 == 0) {
		alert("请选择要设置薪资总额的机构！");
		return;
	}
	var times = year+"-"+getmonth1+"-"+getmonth2+"`"+val+"-"+val2+"-"+val3;
	window.returnValue = times;
	window.close();
}
function isqcode(Str){
	for(var   i=0;i <Str.length;i++) 
        { 
      			   strCode=Str.charCodeAt(i); 
                   if((strCode> 65248)||(strCode==12288)) 
                   { 
                  	 return true; 
                   } 
        } 
}
function zuzhuang(str){
	var   result= "";
                        for   (var   i   =   0;   i   <   str.length;   i++) 
                        { 
                                if(str.charCodeAt(i)==12288) 
                                { 
                                    result+=   String.fromCharCode(str.charCodeAt(i)-12256); 
                                    continue; 
                                } 
                                if(str.charCodeAt(i)> 65280   &&   str.charCodeAt(i) <65375) 
                                        result+=String.fromCharCode(str.charCodeAt(i)-65248); 
                                else 
                                result+=String.fromCharCode(str.charCodeAt(i)); 
                        }       
                        return result; 
}
</SCRIPT>
<BODY>
</HTML>


