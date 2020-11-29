<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hjsj.hrms.actionform.report.edit_report.*"%>
<%@ page import="java.util.*"%>
<%@ page import="com.hjsj.hrms.transaction.report.edit_report.send_receive_report.*"%>
<script type="text/javascript">

    function getTabids() {
        var checkStr = "";  //接收的报表ID集合
        var operateStr = ""; //接收方式(默认为添加)
        
        check = document.getElementsByName("tabids");
        operate = document.getElementsByName("operateType");
        
	    for(i = 0; i < check.length; i++) {
	        if (check[i].checked) {//选中
	             if (checkStr == "") {//第一次
	                 checkStr = check[i].value;
	                 operateStr = operate[i].value;
	             } else {
	                 checkStr = checkStr + "," + check[i].value;
	                 operateStr = operateStr + "," + operate[i].value;
	             }
	        }
	    }	
	    
		if (checkStr == "") {
		    alert(REPORT_INFO12+"！");
		} else {
			document.all.ly.style.display="block";   
			document.all.ly.style.height=document.body.scrollHeight;
			document.getElementById('wait').style.display='block';
		    sendReceiveForm.action = "/report/edit_report/ReceiveParseXml.do?b_query=link&check=" 
		                             + checkStr+ "&operate=" + operateStr;
		    sendReceiveForm.submit();
		}
	}
	
	//全选
	function selectAll(){
		for(var j=0;j<document.sendReceiveForm.elements.length;j++){
			if(document.sendReceiveForm.elements[j].type=="checkbox"){
				document.sendReceiveForm.elements[j].checked = true;
			}
		}
	}
	
</script>

<html>
	<hrms:themes />
	<body>
		<FORM name="sendReceiveForm" method="post" enctype="multipart/form-data" action="/report/edit_report/ReceiveParseXml.do?b_query=link">
		<div id="ly" style="position:absolute;left:0px;top:0px;FILTER:alpha(opacity=30);opacity:0.3;background-color:#c5c5c5;z-index:2;display:none;width:100%;
	    "></div>
		<div id='wait' style='position:absolute;top:180;left:400;display:none;z-index:1000;'>
			<table border="1" width="300" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
				<tr>
					<td class="td_style" height=24>
						正在导入数据，请稍候...
					</td>
				</tr>
				<tr>
					<td style="font-size:12px;line-height:200%" align=center>
						<marquee class="marquee_style" direction="right" width="300" scrollamount="5" scrolldelay="10">
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
			<INPUT type="hidden" name="flg">
			<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td valign="top">
						<br>
						<br>
						<table width="500" border="0" cellpadding="0" cellspacing="0" align="center">
							<tr height="20">
								<!-- <td width=10 valign="top" class="tableft"></td>
								<td width=130 align=center class="tabcenter">
									&nbsp; <bean:message key="reporttypelist.selectReport"/> &nbsp;
								</td>
								<td width=10 valign="top" class="tabright"></td>
								<td valign="top" class="tabremain" width="500"></td> -->
								<td align=center class="TableRow">
									&nbsp; <bean:message key="reporttypelist.selectReport"/> &nbsp;
								</td>
							</tr>
							<tr>
								<td align='center' class="framestyle9">
									<br>
									
									<table width="100%" border="0" cellpmoding="0" cellspacing="0" class="DetailTable" cellpadding="0">

										<table  class="ListTable" >
										<thead>	<tr>
												<td class="TableRow"  >
													<bean:message key="reporttypelist.select"/>
												</td>
												<td class="TableRow" >
													<bean:message key="reprottypelist.reportno"/>
												</td>
												<td class="TableRow" >
													<bean:message key="report.reportlist.reportname"/>
												</td>
												<td class="TableRow" >
													<bean:message key="label.gz.submit.type"/>
												</td>
											</tr></thead>
											<%

        									 int j=0; 
											SendReceiveForm sendReceiveForm = (SendReceiveForm) session
					.getAttribute("sendReceiveForm");
			ArrayList list = sendReceiveForm.getSelectVoList();
			for (int i = 0; i < list.size(); i++) {
				ReportData rd = (ReportData) list.get(i);

				%>
				
											 <tr class="<%=(j%2==0?"trShallow":"trDeep")%>">
												<td  class="RecordRow"   >
													<INPUT type="checkbox" name="tabids" value="<%=rd.getTabid()%>">
												</td>
												<td class="RecordRow" >
													<%=rd.getTabid()%>
												</td>
												<td class="RecordRow" >
													<%=rd.getTabName()%>
												</td>
												<td class="RecordRow" >
													<%if (rd.isRepeat()) {%>
													<SELECT name="operateType" tabindex="0">
														<OPTION value="modify">
															<bean:message key="gz.templateset.repeat"/>
														</OPTION>
													</SELECT>
													<%} else {%>
													<SELECT name="operateType" tabindex="0">
														<OPTION value="append">
															<bean:message key="gz.acount.filter.add"/>
														</OPTION>
													</SELECT>
													<%}%>
												</td>
											</tr>
											<%
											j++;
											}

		%>
										</table><Br><br>
										</td></tr>
										
										<tr class="list3">
											<td align="center" colspan="2">
												
											</td>
										</tr>
										
									</table><br>
									<div align='center' >
												<INPUT type="button" onclick="selectAll()" value="<bean:message key="label.query.selectall"/>" class="mybutton">
												<INPUT type="button" onclick="getTabids()" value="<bean:message key="lable.tz_template.enter"/>" class="mybutton">
												<input type="button" value="<bean:message key="lable.tz_template.cancel"/>" class="mybutton" onclick="window.location='/report/edit_report/sendReceiveView.do?b_query=b_query';">
									</div>
								</td>
							</tr>
						</table>
				</tr>
			</table>
		</form>

	</body>
</html>
