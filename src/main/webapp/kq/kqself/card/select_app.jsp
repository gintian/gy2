<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.kq.net_signin.BaseNetSignInForm" %>
<%@ page import="java.util.ArrayList" %>
<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->
<STYLE type=text/css>
.div2
{
 overflow:auto; 
 width: 360px;height: 300px;
 line-height:15px; 
 border-width:0px; 
 border-style: groove;
 border-width :thin ;
 border-color:#C4D8EE;
 BORDER-BOTTOM: #C4D8EE 1pt solid; 
 BORDER-LEFT: #C4D8EE 1pt solid; 
 BORDER-RIGHT: #C4D8EE 1pt solid; 
 BORDER-TOP: #C4D8EE 1pt solid;

}
</STYLE>
<script type="text/javascript">

function select_app(){
	var app_account = "";
	var all_array = document.getElementsByName("index");
	for(var i = 0;i<all_array.length;i++){
		if(all_array[i].checked){
			app_account=all_array[i].value;
			break;
		}
	}
	if(app_account.length == 0){
		alert("请选择一个审批对象！");
	}else{
		var obj = new Object();
		obj.app_account = app_account;
		window.returnValue=obj;
		window.close();
	}
}
</script>

<%
	int i=0;
	BaseNetSignInForm baseNetSignInForm = (BaseNetSignInForm)session.getAttribute("baseNetSignInForm");
	ArrayList app_e0122 = baseNetSignInForm.getApp_e0122();
	ArrayList app_a0101 = baseNetSignInForm.getApp_a0101();
	ArrayList app_acconut = baseNetSignInForm.getApp_account();
%>

<html:form action="/kq/kqself/card/carddata" >
<div id="d"  align="center"  class="fixedDiv2" style="height: 100%;border: none">
<table border="0" cellspacing="0" width="300" align="center" cellpadding="0" width="100%" >
	<tr>
		<td>&nbsp;</td>
	</tr>
	<tr>
		<td width="100%">
			<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
				<thead>              
		        	<tr>     
		        		<td align="center" class="TableRow" nowrap> 
		         			选择
		        		</td>
		        		<td align="center" class="TableRow" nowrap>
							部门
		        		</td>  
		        		<td align="center" class="TableRow" nowrap>
							姓名
		       			</td>  
		        	</tr>                           
		   	    </thead>
				<logic:iterate id="element" name="baseNetSignInForm" property="app_account" indexId="index" >
					<tr>
						<td align="center" class="RecordRow" nowrap>
							<input type="radio" name="index" id="<%=app_acconut.get(i)%>" value="<bean:write name="element"/>"/>
						</td>
						<td align="center" class="RecordRow" nowrap>
                          	<%=app_e0122.get(i) %>
						</td>
						<td align="center" class="RecordRow" nowrap>
							<%=app_a0101.get(i) %>
						</td>
						<%
							i++; 
						%>
					</tr>
				</logic:iterate>
			</table>
   		</td>
	</tr>
</table>
</div>
<table border="0" cellspacing="0" width="300" align="center" cellpadding="0" width="90%" style="margin-top: 10px;">
	<tr>
		<td align="center">    
			<input type="button" value="确定" class="mybutton" onclick="select_app();"/>
			<input type="button" value="取消" class="mybutton" onclick="window.close();"/>
		</td>
	</tr>
</table>
</html:form>