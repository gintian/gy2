<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.constant.WebConstant,com.hrms.struts.valueobject.UserView"%>
<%
	UserView userview = (UserView) request.getSession().getAttribute(WebConstant.userView);
	String returnvalue = request.getParameter("returnvalue")==null?"":request.getParameter("returnvalue");
%>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<link href="/css/css1.css" rel="stylesheet" type="text/css">
		<script language="JavaScript" src="/js/validate.js"></script>
		<script language="JavaScript">
			
			//表式收发切换
			function showView() {
		       var a = eval("output");
		       var a2 = eval("output2");
		       var b = eval("inputa");
		       var b2 = eval("inputa2");
		       var c = eval("inputb");
		       if (sendReceiveForm.cbInput[0].checked == true) {
		           a.style.display = 'block';
		           a2.style.display = 'block';
		           b.style.display = 'none';
		           b2.style.display = 'none';
		           c.style.display = 'none';
		       } else {
		           a.style.display = 'none';
		           a2.style.display = 'none';
		           b.style.display = 'block';
		           b2.style.display = 'block';
		           c.style.display = 'block';
		       }
			}
			
			//选中的所有报表
			function getTabids() {
			    var tabids = "";
			    var a = 0;
			    for(var i = 0; i < document.sendReceiveForm.tabid.options.length; i++) {
					if (document.sendReceiveForm.tabid.options[i].selected) {
				        tabids += "," + document.sendReceiveForm.tabid.options[i].value;
					    a++;
					} 
				}	
				if (a == 0) {
				    alert(REPORT_INFO12+"！");
				} else {
					var url = "/servlet/SendReceive?tabids=" + tabids;
					
					//alert(parent.frames[1].location.href);
					
				  //  parent.frames[1].location.href = url;
				  
				 //   window.open(url);
				  var win=open(url,"txt");  
				}
			}
			
			//全选
			function selectAll(){
				var obj = document.sendReceiveForm.tabid;
				for(var i=0;i<obj.options.length;i++){
					obj.options[i].selected=true;
				}
			}
			
			//接受表式
			function mysub() {
				var filePath = document.sendReceiveForm.file.value;
				if(filePath == ""){
					alert(REPORT_INFO59+"!");
					return;
				}
				var flag=validateUploadFilePath(filePath);
				if(!flag){
					return;
				}
			    sendReceiveForm.action="/report/edit_report/ReceiveSelect.do?b_query=b_query";
			    sendReceiveForm.submit();
			}
        </script>
        <style>
		.inputT{
		    font-size: 12px;
		    font-family:微软雅黑;
		    height:24px;
		    line-height:22px;
		    border: 1px solid #C4D8EE;
		    margin:0px; 
		    padding:0;
		    *padding:0 0 3px 1px;
		}
		</style>
	</head>
	<hrms:themes />
	<body>
		<FORM name="sendReceiveForm" action="/report/edit_report/sendReceiveView" method="post" enctype="multipart/form-data">
			<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td valign="top">
						<br>
						<br>
						<table width="500" border="0" cellpadding="0" cellspacing="0" align="center">
							<tr height="20">
								<!--  td width=10 valign="top" class="tableft"></td>
								<td width=130 align=center class="tabcenter">
									&nbsp; <bean:message key="report_collect.receiveSendStyle"/> &nbsp;
								</td>
								<td width=10 valign="top" class="tabright"></td>
								<td valign="top" class="tabremain" width="500"></td-->
							<td align="left" colspan="4" class="TableRow"><bean:message key="report_collect.receiveSendStyle"/>&nbsp;</td> 
							</tr>
							<tr>
								<td colspan="4" align="center" class="framestyle3">
									<br>
									<table width="100%" border="0" cellpmoding="0" cellspacing="0" class="DetailTable" cellpadding="0">

										<tr>
											<td width="30%" align="left" style="padding-left: 37px;">
												<input name="cbInput" checked="checked" type="radio" onclick="showView()"><bean:message key="menu.gz.import"/>
												&nbsp;&nbsp;<input name="cbInput" type="radio" onclick="showView()">
												<bean:message key="sys.export.derived"/>
												
												<br>
											</td>

										</tr>
										<table align="center">
											
											<tr><td>
											<div id="inputb" style="display:none;">
											<br><bean:message key="report_collect.selectOutReport"/>:</td>
											</div>
											</tr>
											
											<tr>			
												<td width="100%">
													<div id="inputa" style="display:none;">
														
														<hrms:optioncollection name="sendReceiveForm" property="voList" collection="list2" />
														<html:select name="sendReceiveForm" property="tabid" multiple="" style="height:209px;width:430;font-size:10pt">
															<html:options collection="list2" property="dataValue" labelProperty="dataName" />
														</html:select>
														<br>
														
													</div>

												</td>

											</tr>
										</table>

										<table>
											<tr>
												<td width="100%">
													<div id="output" style="display:none;">
														<table align="center">
															<tr>
																<td align="center" id="temp" width="100%">
																	<INPUT type="file" name="file" size="50" class="inputT"/>
																	<br>
																</td>
															</tr>
															<tr>
																<td>
																	
																</td>
															</tr>
														</table>
													</div>
												</td>
											<tr>
										</table>

										
									</table>
									<div id="inputa2" style="display:none;">
									<table width="500" align="center">
									<tr>
									<td align="center">
											<INPUT type="button" onclick="selectAll()" value="<bean:message key="label.query.selectall"/>" class="mybutton">
											<INPUT type="button" onclick="getTabids()" value="<bean:message key="lable.tz_template.enter"/>" class="mybutton">
														 <% 
								    if(userview!=null && userview.getBosflag()!=null && returnvalue.equals("dxt"))
									{
									%>
										<hrms:tipwizardbutton flag="report" target="il_body" formname="sendReceiveForm"/>
									<%} %>
									</td>
									</tr>
									</table>
														
									</div>
									<div id="output2" style="display:none;">
									<table width="500" align="center">
									<tr>
									<td align="center">
												<INPUT type="button" value="<bean:message key="lable.tz_template.enter"/>" class="mybutton" align="middle" onclick="mysub()">
												 <% 
								    if(userview!=null && userview.getBosflag()!=null && returnvalue.equals("dxt"))
									{
									%>
										<hrms:tipwizardbutton flag="report" target="1" formname="sendReceiveForm"/>
									<%} %>
								    </td>
									</tr>
									</table>
									</div>
								</td>
							</tr>
						</table>
				</tr>
			</table>
		</form>
	</body>
</html>
<script>
    showView();
</script>
