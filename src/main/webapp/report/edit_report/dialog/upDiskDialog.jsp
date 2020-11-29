<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
    UserView userView = (UserView) request.getSession().getAttribute(
	WebConstant.userView);
	String username = userView.getUserName();
	String unitName = (String)request.getParameter("unitname");
	//unitName = new String(unitName.getBytes("ISO-8859-1"), "GBK");
%>
<script language="javaScript">
<!--	
	function showPage(code,status)
	{
		editReportForm.action="/report/edit_report/reportSettree.do?b_query=link&code="+code+"&status="+status;
		parent.mil_menu.document.location.reload();
		editReportForm.submit();
	}
	
	function selectAll()
	{
		for(var i=0;i<document.editReportForm.elements.length;i++)
		{
			if(document.editReportForm.elements[i].type=="checkbox")
				document.editReportForm.elements[i].checked=true;
		}
	}
	
	
	function returnInfo(outparamters)
	{
		var info=outparamters.getValue("info");
	//	lawbaseForm.submit();
		var hashvo=new ParameterSet();
		hashvo.setValue("operateObject",${editReportForm.operateObject});
		// hashvo.setValue("unitcode",getEncodeStr("<%=request.getParameter("unitcode")%>"));
		// hashvo.setValue("unitname",getEncodeStr("<%=unitName%>"));
		hashvo.setValue("tabid","<%=request.getParameter("tabids")%>");
		hashvo.setValue("username",getEncodeStr("<%=username%>"));
		hashvo.setValue("db_unitcode",getEncodeStr("<%=request.getParameter("unitcode")%>"));
		
		var unitcodeinput = "";
		var unitnameinput = "";
		if(unitcodeinput==""&&document.getElementsByName("unitcode")[0].value==""){
		alert("单位编码不能为空！");
		var waitInfo=eval("wait");
		waitInfo.style.display="none";
		window.location.target="_blank";
		return;
		}
		if(unitnameinput==""&&document.getElementsByName("unitname")[0].value==""){
		alert("单位名称不能为空！");
		var waitInfo=eval("wait");
		waitInfo.style.display="none";
		window.location.target="_blank";
		return;
		}
		// if(unitcodeinput==""){
		hashvo.setValue("unitcode",getEncodeStr(document.getElementsByName("unitcode")[0].value));
		// hashvo.setValue("db_unitcode",getEncodeStr(document.getElementsByName("unitcode")[0].value));
		// }
		// if(unitnameinput==""){
		hashvo.setValue("unitname",getEncodeStr(document.getElementsByName("unitname")[0].value));
		// }
		var scopes=$("scope");
		var scope="";
		for(var n=0;n<scopes.length;n++)
		{
			if(scopes[n].checked==true)
				scope=scopes[n].value;
		}
		hashvo.setValue("scope",scope);
		var In_paramters="flag=1"; 
		var request=new Request({method:'post',asynchronous:true,parameters:In_paramters,onSuccess:returnInfo2,functionId:'03020000026'},hashvo);			
	}
	
	
	function returnInfo2(outparamters)
	{
		var outName=outparamters.getValue("outName");
		if(outName === undefined){
		    alert("无此表权限，不允许生成报盘！");
		    return;
		}
		var waitInfo=eval("wait");
		waitInfo.style.display="none";
		window.location.target="_blank";
		//xus 20/3/16 vfs改造
		window.location.href="/servlet/vfsservlet?fileid="+outName+"&fromjavafolder=true";
	}
	
	//报表上报
	function appeal_2(outparamters)
	{
		var returninfo=outparamters.getValue("returnInfo");
		var tabid_str=outparamters.getValue("tabid_str");
		if(returninfo!="success")
		{
			// 如果校验不成功，则不让上报
			if(returninfo=='failed1')
			{	
				var errorInfo=getDecodeStr(outparamters.getValue("errorInfo"));
				alert(errorInfo);
			
			}
			else{
				if(returninfo=='failed2'){
					alert(REPORT_INFO73+"！");
				}else{
					alert("\r\n校验错误,不予上报!\r\n");
				}
			}
				var waitInfo=eval("wait");
				waitInfo.style.display="none";
			return; 
		}
		if(tabid_str.length==0)
		{
			alert(REPORT_INFO11+"！");
			return;
		}
		
		var hashvo=new ParameterSet();
		
		hashvo.setValue("tabids",tabid_str);
		hashvo.setValue("changStatus","0");
		hashvo.setValue("operateObject",${editReportForm.operateObject});
		hashvo.setValue("appealUnitcode","<%=request.getParameter("unitcode")%>");
		var In_paramters="flag=1"; 
		var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfo,functionId:'03020000003'},hashvo);			
		
	}
	
	
	//进行报表校验
	function appeal_1()
	{
		var waitInfo=eval("wait");
		waitInfo.style.display="block";
		
		var hashvo=new ParameterSet();
		var tabid_str="<%=request.getParameter("tabids")%>";
		if(tabid_str.length==0)
		{
			alert(REPORT_INFO12+"！");
			return;
		}
		hashvo.setValue("tabids",tabid_str.substring(1));
	    hashvo.setValue("operateObject","${editReportForm.operateObject}");
	    hashvo.setValue("unitcode","${editReportForm.unitcode}");
    	var In_paramters="flag=1"; 
    	<%
    	if(request.getParameter("existunicode").equals("0")){%>
    	var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfo,functionId:'03020000009'},hashvo);    		
    	<%}else{%>
    	    	var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:appeal_2,functionId:'03020000009'},hashvo); 
    	<%}%>
		
	}
	
	
	function change()
	{
		editReportForm.action="/report/edit_report/editReport.do?b_searchAppeal=appeal&tabid=<%=(request.getParameter("tabid"))%>&status=<%=(request.getParameter("status"))%>";
		editReportForm.submit();
	}	   
//-->
</script>
<link href="/css/css1.css" rel="stylesheet" type="text/css">
<hrms:themes></hrms:themes>
<html>
	<base id="mybase" target="_self">
	<body>
		<form name="lawbaseForm" method="post" action="/servlet/UpDiskDownLoad">
			<table width=70% height="140" border="0" cellpadding="5" cellspacing="0" align="center">
				<tr height="20">
					<!--  <td width=5 valign="top" class="tableft"></td>
					<td width=130 align=center class="tabcenter">
						&nbsp;
						<bean:message key="reportManager.executeAppealInfo" />
						&nbsp;
					</td>
					<td width=5 valign="top" class="tabright"></td>
					<td valign="top" class="tabremain" width="300"></td>-->
					<td align="left" class="TableRow">						
						<bean:message key="reportManager.executeAppealInfo" />
					</td>
				</tr>
								
						<div id="sp" style="display:none" >
				<tr>
					<td class="framestyle9">
						<table border="0" cellspacing="0" class="DetailTable" cellpadding="0">
							<tr class="list3">
								<td>
									<bean:message key="reportlist.datascope" />
									<input type="radio" name="scope" value="1" checked ><bean:message key="reportlist.containbasicun" />
									&nbsp;&nbsp;
									<input type="radio" name="scope" value="2"><bean:message key="reportlist.onlygatherun" />
								</td>
							</tr>
						</table>
					</td>
				</tr>
						</div>

				<tr>
					<td  class="framestyle9">
						<table border="0" cellspacing="0" class="DetailTable" cellpadding="0">
							<tr class="list3" style="vertical-align: middle;">
								<td align="right" nowrap valign="middle">
									<bean:message key="hmuster.label.unitNo" />&nbsp;
								</td>
								<td align="left" nowrap>
									<input type="text" name="unitcode" class="text4" size="31" value="<%=request.getParameter("unitcode")%>" style="vertical-align: middle;">
								</td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td  class="framestyle9">
						<table border="0" cellspacing="0" class="DetailTable" cellpadding="0">
							<tr class="list3" style="vertical-align: middle;">
								<td align="right" nowrap valign="middle">
									<bean:message key="column.sys.org" />&nbsp;
								</td>
								<td align="left" nowrap>
									<input name="unitname" class="text4" size="31" value="<%=unitName%>">
								</td>
							</tr>

						</table>
					</td>
				</tr>
				<tr class="list3">
					<td align="center" style="height:35px;" >
						<input type="button" name="b_save" value="<bean:message key="reporttypelist.confirm" />" onclick="appeal_1()" class="mybutton">
						<input type="button" name="cancel" value="<bean:message key="kq.register.kqduration.cancel" />" onclick="window.close();" class="mybutton" style="margin-left: -2px;">
						<input type="reset" name="reset" value="<bean:message key="options.reset" />" class="mybutton" style="margin-left: -2px;">
					</td>
				</tr>
			</table>
			<input type="hidden" name="tabid" value="<%=request.getParameter("tabids")%>" />
			<input type="hidden" name="username" value="<%=username%>" />
			<input type="hidden" name="operateObject" value="${editReportForm.operateObject}" /> <!--编辑报表上报与汇总报表上报的标识-->
			<input type="hidden" name="db_unitcode" value="<%=request.getParameter("unitcode")%>" />
		</form>
	</body>
</html>




<div id='wait' style='position:absolute;top:160;left:40;display:none;'>
		<table border="1" width="37%" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
			<tr>
				<td class="td_style"  height=24>
					<bean:message key="auto_fill_report.batchFillData.info5" />......
				</td>
			</tr>
			<tr>
				<td style="font-size:12px;line-height:200%" align=center>
					<marquee class="marquee_style" direction="right" width="300" scrollamount="5" scrolldelay="10" >
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




<script language="javaScript">
//50928 根据弹出窗口大小动态设置滚动条纵向位置
var wait=document.getElementById("wait")
wait.style.top=document.body.clientHeight/3




<!--
    // 1：编辑没上报表 (编辑报表过程中) 2：编辑上报后的表(1无,2有)汇总过程
	var flag = "${editReportForm.operateObject}";
	if(flag == "2"){
		document.getElementById("sp").style.display="";
	}else{
		document.getElementById("sp").style.display="none";
	}
//-->
</script>
