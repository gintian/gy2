<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page
	import="com.hrms.struts.constant.WebConstant,java.text.SimpleDateFormat,java.util.Date"%>
<%
	// 在标题栏显示当前用户和日期 2004-5-10 
	String css_url = "/css/css1.css";
	UserView userView = (UserView) session
			.getAttribute(WebConstant.userView);
	if (userView != null) {
		css_url = userView.getCssurl();
		if (css_url == null || css_url.equals(""))
			css_url = "/css/css1.css";
		//out.println("<link href='"+css_url+"' rel='stylesheet' type='text/css' >");  	 
	}
	String bosflag="";
	if(userView != null)
	{
		bosflag=userView.getBosflag();
	}
%>
<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/module/utils/js/template.js"></script>
<script type="text/javascript">
<!--
function submitT(){
		var create_date=$F('create_date');
		if(create_date.length<8){
			alert("请正确填写生成日期!");
			return;
		}
		var description=$F('description');
		var hashvo=new ParameterSet();
		hashvo.setValue("create_date",create_date);
		hashvo.setValue("description",getEncodeStr(description));
		hashvo.setValue("uniqueitem",'${personHistoryForm.uniqueitem }');
		var In_paramters="flag=1"; 	
		var request=new Request({method:'post',asynchronous:false,
			parameters:In_paramters,onSuccess:resultmsg,functionId:'0201001190'},hashvo);	
		function resultmsg(outparamters){
  			var msg = outparamters.getValue("msg");
			if(msg=='ok'){
				msgAlert("快照完成！");
				//19/3/14 xus 快照按钮 浏览器兼容 
				if(window.showModalDialog){
					window.returnValue="aa";
				}else{
					window.opener.backup_callbackfunc("aa");
				}
				window.close();
			}else if(msg=="equal"){
				msgAlert("同一天内不能快照人员信息多次!");
			}else{
				msgAlert("快照失败！");
			}
		}
	}
//19/3/22 xus 浏览器兼容 alert兼容
function msgAlert(msg){
	if(getBrowseVersion()){
		alert(msg);
	}else{
		Ext.MessageBox.alert("",msg);
	}
}
//-->
</script>
<hrms:themes />
<style>
.TableRow{
	BORDER-LEFT: 0pt; 
	BORDER-RIGHT: 0pt;
}
</style>
<%if("hcm".equalsIgnoreCase(bosflag)){ %>
<style>
.backupTable {
	width:expression(document.body.clientWidth-10);
	margin-left:-5px;
}
</style>
<%}else{ %>
<style>
.backupTable {
	width:expression(document.body.clientWidth-10);
	margin-top:10px;
	margin-left:-5px;
}
</style>
<%} %>
<body>
	<html:form action="/workbench/browse/history/showinfo">
		<table width="95%" border="0" cellspacing="0" id="tableId" align="center"
			cellpadding="0" class="backupTable">
			<tr>
				<td colspan="2" class="framestyle1">
					<table width="100%" border="0" cellspacing="0" align="center"
						cellpadding="0">
						<tr align="center">
							<td align="left" class="TableRow" colspan="2">
								人员信息快照
							</td>
						</tr>
						<tr>
							<td height="10"></td>
						</tr>
						<tr align="left" class="list3">
							<td align="right">
								生成日期&nbsp;
							</td>
							<td><%
									SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
										String date = sdf.format(new Date());
								%>
								<input type="text" name="create_date"
									value="<%=date%>" maxlength="50"
									style="BACKGROUND-COLOR: #F8F8F8; width: 200px"
									extra="editor" dropDown="dropDownDate"
									onchange="if(!validate(this,'时间点')) {this.focus(); this.value='<%=date%>'; }" class="inputtext"/>
							</td>
						</tr>
						<tr>
							<td height="20"></td>
						</tr>
						<tr align="left" class="list3">
							<td align="right">
								历史时点名称&nbsp;
							</td>
							<td>
								<input type="text" name="description" maxlength="50" style="width: 200px;" class="inputtext"/>
							</td>
						</tr>
						<tr>
							<td height="10"></td>
						</tr>
					</table>
				</td>
			</tr>
		</table>
		<table width="100%" border="0" cellspacing="0" align="center"
			cellpadding="0">
			<tr>
				<td height="5"></td>
			</tr>
			<tr>
				<td align="center">

					<html:button styleClass="mybutton" property="" onclick="submitT();">
						<bean:message key="button.ok" />
					</html:button>
					<html:button styleClass="mybutton" property=""
						onclick="window.close();">
						<bean:message key="button.close" />
					</html:button>
				</td>
			</tr>
		</table>
		<script type="text/javascript">
		if(!getBrowseVersion()) {
	    	document.getElementById("tableId").style.marginLeft = "5px";
	    }
		</script>
	</html:form>
</body>