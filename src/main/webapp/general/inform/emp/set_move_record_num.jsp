<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.frame.utility.AdminCode"%>
<%@ page import="com.hrms.frame.codec.SafeCode"%>
<hrms:themes></hrms:themes>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
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
.tabletoglle{
	border-top:#A9A9A9 1px solid;
	border-bottom:#A9A9A9 1px solid;
	border-left:#A9A9A9 1px solid;
	border-right:#A9A9A9 1px solid;
}
</style>
<script language="javascript">

function sub(){
    var recordnum = document.getElementById("recordnum").value
    if(recordnum=="0")
    {
    	return ;
    }
    window.returnValue = recordnum;
  	window.close();
}
function mincrease(){
	var recordnum=document.getElementById("recordnum").value;
	var recordset = parseInt(recordnum);
	recordset = recordset+1;
	document.all.recordnum.value = recordset;
}
function msubtract(){
	var recordnum=document.getElementById("recordnum").value;
	var recordset = parseInt(recordnum);
	if(recordset<2){
		document.all.recordnum.value = 1;
	}else{
		recordset = recordset-1;
		document.all.recordnum.value = recordset;
	}
}
/*
*只能输入数字
*/
function IsDigit() { 
    return ((event.keyCode >= 47) && (event.keyCode <= 57)); 
}
</script>
<title><bean:message key="general.info.emp.save.recordnum" />
</title>
<%
	String a0101 = request.getParameter("a0101");
	a0101 = a0101 != null ? a0101 : "";
	a0101 = SafeCode.decode(a0101);
	String b0110 = request.getParameter("b0110");
	b0110 = b0110 != null ? b0110 : "";
	String e0122 = request.getParameter("e0122");
	e0122 = e0122 != null ? e0122 : "";
	String e01a1 = request.getParameter("e01a1");
	e01a1 = e01a1 != null ? e01a1 : "";
	String select_record = request.getParameter("select_record");
	select_record = select_record != null ? select_record : "";
	String dbstr = request.getParameter("dbstr");
	dbstr = dbstr != null ? dbstr : "";
	dbstr = SafeCode.decode(dbstr);
%>
<html:form action="/general/inform/get_data_table">
<table border="0" align="center">
		<tr>
			<td>
				<%=AdminCode.getCodeName("UN", b0110)%>
				<%=AdminCode.getCodeName("UM", e0122)%>
				<%=a0101%>
			</td>
		</tr>
		<tr>
			<td align="center" height="100">
				<fieldset align="center" style="width:100%;">
					<legend><%=dbstr%></legend>
					<table border="0" align="center">
						<tr>
							<td>&nbsp;</td>
						</tr>
						<tr>
							<td align="center">
							<table border="0" cellspacing="0" cellpadding="0">
								<tr>
								<td align="left" width>从第&nbsp;<%=select_record%> 条记录移到第&nbsp;</td>
								<td align="left"><html:text name="mInformForm" styleClass="text4" property="recordnum" maxlength="9"
									onkeypress="event.returnValue=IsDigit();"
									style="height:20px;width:40;font-size:9pt;ime-mode:disabled" /></td>
								<td align="left">
								<table border="0" cellspacing="2" cellpadding="0" height="10">
									<tr>
										<td><button id="m_up" class="m_arrow" onclick="mincrease();">5</button></td>
									</tr>
									<tr>
										<td><button id="m_down" class="m_arrow" onclick="msubtract();">6</button></td>
									</tr>
								</table>
								</td>
								<td>条记录</td>
								</tr>
								</table>
							</td>
						</tr>
						<tr>
							<td>&nbsp;</td>
						</tr>
					</table>
				</fieldset>
			</td>
		</tr>
	</table>
	<table border="0" align="center" width="150">
	<tr>
		<td align="center" colspan="3">
			<html:button styleClass="mybutton" property="b_next" onclick="sub();">
				<bean:message key="button.ok" />
			</html:button>
			<html:button styleClass="mybutton" property="b_return" onclick="window.close();">
				<bean:message key="button.cancel" />
			</html:button>
		</td>
	</tr>
	</table>

</html:form>

