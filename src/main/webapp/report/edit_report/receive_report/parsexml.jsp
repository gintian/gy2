<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%

    UserView userView = (UserView) request.getSession().getAttribute(
	WebConstant.userView);
	String username = userView.getUserName();
%>
<script>
  clew = "<bean:write property="clew" name="receiveReportForm"/>";
  if (clew != "") {
     alert(clew);
  }
  function check() {
     flg = true;
     if (receiveReportForm.unitname.value == "") {
          receiveReportForm.unitname.focus();
          alert("<bean:message key="receive_report.unitname_notnull"/>");
          flg = false;   
     }
     if (receiveReportForm.unitcode.value == "") {
          receiveReportForm.unitcode.focus();
          alert("<bean:message key="receive_report.unitcode_notnull"/>");
          flg = false;
     }
     if(flg){
    	 document.all.ly.style.display="block";   
 		document.all.ly.style.height=document.body.scrollHeight>document.body.offsetHeight?document.body.scrollHeight:document.body.offsetHeight;
 		document.getElementById('wait').style.display='block';
     }   	 
     return flg;
  }
</script>
<html>
	<base id="mybase" target="_self">
	<body>
		<form name="receiveReportForm" method="post" action="/report/edit_report/receive_report.do" onsubmit="return check()">
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
			<table width=40% border="0" cellpadding="0" cellspacing="0" align="center">
				<tr height="20">
					<!--  <td width=10 valign="top" class="tableft"></td>
					<td width=150 align=center class="tabcenter">
						&nbsp;
						<bean:message key="receive_report.affirm" />
						&nbsp;
					</td>
					<td width=20 valign="top" class="tabright"></td>
					<td valign="top" class="tabremain" width="280"></td>-->
					<td  align=center class="TableRow">
						&nbsp;
						<bean:message key="receive_report.affirm" />
						&nbsp;
					</td>
				</tr>
				
				
				<tr>
					<td class="framestyle9">
						<table border="0" cellspacing="0" class="DetailTable" cellpadding="0">
							<tr class="list3">
								<td>
									<bean:message key="eidt_report.receiveScope" />
									<input type="radio" name="scope" value="1" checked ><bean:message key="eidt_report.receiveData" />
									&nbsp;&nbsp;
									<input type="radio" name="scope" value="2"><bean:message key="edit_report.onlyReceiveData" />
								</td>
							</tr>
						</table>
					</td>
				</tr>
				
				
				
				<tr>
					<td  class="framestyle9">
						<table border="0" cellpmoding="0" cellspacing="0" class="DetailTable" cellpadding="0">
							<tr class="list3">
								<td align="right" nowrap valign="top">
									<bean:message key="receive_report.reportunitcode" />
								</td>
								<td align="center" nowrap>
									<input type="text" name="unitcode" size="40" value="<bean:write property="unitcode" name="receiveReportForm"/>">
								</td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td  class="framestyle9">
						<table border="0" cellpmoding="0" cellspacing="0" class="DetailTable" cellpadding="0">
							<tr class="list3">
								<td align="right" nowrap valign="top">
									<bean:message key="receive_report.reportunitname" />
								</td>
								<td align="left" nowrap>
									<input name="unitname" cols="50" size="40" rows="10" value="<bean:write property="unitname" name="receiveReportForm"/>">
									
								</td>
							</tr>

						</table>
					</td>
				</tr>
				<tr class="list3">
					<td align="left" height="35">
						<input type="submit" name="b_save" value="<bean:message key="reporttypelist.confirm" />" onclick="" class="mybutton">
						<input type="button" name="cancel" value="<bean:message key="kq.register.kqduration.cancel" />" onclick="window.location='/report/edit_report/receive_report/receive_report.jsp?returnvalue=${receiveReportForm.returnflag}&editflag=${receiveReportForm.editflag} ';" class="mybutton">
					</td>
				</tr>
			</table>
		</form>
	</body>
</html>
