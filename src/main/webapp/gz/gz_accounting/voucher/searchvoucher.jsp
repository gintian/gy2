<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>

<%
	String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null)
	{
		css_url=userView.getCssurl();
		if(css_url==null||css_url.equals(""))
		css_url="/css/css1.css";
	}
	
	
%>


<HTML>
<HEAD>
	<TITLE>
	</TITLE>
	<link href="<%=css_url%>" rel="stylesheet" type="text/css">
	<link href="/css/xtree.css" rel="stylesheet" type="text/css" >
	<hrms:themes />
	<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
	<SCRIPT LANGUAGE=javascript src="/js/validate.js"></SCRIPT>
	<script language="javascript" src="/js/constant.js"></script>     
	<SCRIPT LANGUAGE=javascript>
</SCRIPT>     
</HEAD>
<body>
	<table width="444px;" align="center" border="0" cellpadding="0" cellspacing="0" style="margin-left:-3px;">	
	<tr>  
		<td valign="top" align="center">
			<div id="treemenu" style="width:98%;height: 350px;overflow:auto;border-style:solid;border-width:1px;color:rgb(203,228,253);">
			 <table width="100%" border="0" cellspacing="0" align="center" cellpadding="0" class="ListTable">
				
				<tr>
					<td align="center" class="TableRow" width="20%" nowrap style="border-left:0px;border-top:0px;">
            			<input type="checkbox" name="selbox" onclick="selectAll(this)" title='<bean:message key="label.query.selectall"/>'>	    
					</td>
					<td align="center" class="TableRow" width="80%" nowrap style="border-right:0px;border-top:0px;">
						<bean:message key="gz.voucher.name" />
					</td>
				</tr>
				<logic:iterate id="el" name="voucherForm" property="voucherList">
					<tr align="center" nowrap >
						<td align="center" nowrap class="RecordRow_right">
							<input type="checkbox" name="selbox2" value="<bean:write name="el" property="dataValue"/>" >	
						</td>
						<td align="left" nowrap class="RecordRow_left">
							&nbsp;<bean:write name="el" property="dataName"/>
						</td>
					</tr>
				</logic:iterate>
			</table>
	     	
			</div>
			 
		</td>
	</tr>
	<tr>
		<td align="center" height="35px;">

    <input type="button" name="btnok" value="确定" class="mybutton" onclick="savecode();">
    <input type="button" name="btncancel" value="取消" class="mybutton" onclick="window.close();"> 
		</td>
	</tr>
</table>	

<BODY>
</HTML>

<script type="text/javascript">
var paraArray=dialogArguments;//add by xiegh 新疆中泰集团 bug36541 
var voucherid = paraArray[0];
var objs = document.getElementsByName("selbox2");
for(var i =0;i<objs.length;i++){
	if(objs[i].value == voucherid){
		objs[i].checked = true;
	}
}
<!--
	function savecode() {
		if (checkSelect()) {
		
	 	window.returnValue=getSelected();
     	window.close();
     	} else {
     		alert("请选择凭证！");
     	}
     	
     	
    }
    
    function selectAll(obj) {
    	input = document.getElementsByName("selbox2");
    	if (obj.checked == true) {
    		for(i = 0; i < input.length; i++) {
				input[i].checked = true; 	
			}
    	} else {
    		for(i = 0; i < input.length; i++) {
				input[i].checked = false; 	
			}
    	}
    	
    }
    
    function checkSelect() {
		check = false;
		input = document.getElementsByName("selbox2");
		for(i = 0; i < input.length; i++) {
			if (input[i].type == "checkbox" && input[i].checked==true && input[i].name != "selbox") {
				check =true;
				break;
			} 	
		}
		return check;
	}
	
	function getSelected() {
		check = "";
		input = document.getElementsByName("selbox2");
		for(i = 0; i < input.length; i++) {
			if (input[i].checked==true) {
				if (i == 0) {
					check += input[i].value;
				} else {
					check += "," + input[i].value;
				}
			} 	
		}
		return check;
	}
//-->
</script>