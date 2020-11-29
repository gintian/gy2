<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="com.hrms.frame.codec.SafeCode"%>
<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<script language="javascript" src="/js/dict.js"></script>
<script language="JavaScript" src="/js/wz_tooltip.js"></script>
<script type="text/javascript" src="exchange.js"></script>
<style>
body{text-align: center;}
</style>

<script type="text/javascript">
	//页面加载时触发
	function show(a){
		if(a >= 1){
			document.getElementById("wait").style.display="block"; 
			doThis();
		}
	}
	
	//点击关闭图片
	function closed(){
			document.getElementById("wait").style.display="none";
	}
	function bjs(){
		exchangeForm.action = "/train/exchange/exchangeintegral.do?b_account=link";
		exchangeForm.submit();
	}
	
	
</script>
<body onload="show(${exchangeForm.counts});">
	<div id='wait' class="common_border_color" style='position:absolute; display:none;width:350px;height:120px; ' >
				<table border="0" width="350" height="120" cellspacing="0" cellpadding="0"
					class="ListTableF" height="90" align="center" bgcolor="white">
					<tr height="20" class="fixedHeaderTr">
						<td align="left" style= "1pt solid; border-right:0;" class="TableRow">
							&nbsp;<b>兑换车提示&nbsp;</b>
						</td>
						<td align="right" style= "1pt solid; border-left:0;" class="TableRow">
							<img src="/images/del.gif" border="0" onclick="closed();" style="cursor:hand;">&nbsp;
						</td>
					</tr>
					<tr>
						<td colSpan="2" align="center">
						<br/>
							<img src="/images/cc1.gif" border="0" style="cursor:hand;">&nbsp;&nbsp;<b>${exchangeForm.counts}&nbsp;件&nbsp;奖品已成功放入兑换车</b><br/><br/>
							兑换车共<b><font color="blue">&nbsp;<bean:write name="exchangeForm" property="ccount"/>件&nbsp;</font></b>奖品 ,&nbsp;奖品积分合计为:<b><font color="blue">&nbsp;<bean:write name="exchangeForm" property="npoint"/>&nbsp;</font></b><br/><br/>
							 <input type="button" name="b_js" value='去兑换车结算' onclick="bjs();" class="mybutton"/><br/><br/>
						</td>
					</tr>
				</table>
	</div>
<html:form action="/train/exchange/exchangeintegral">
	 <html:hidden name="exchangeForm" property="a_code" />
	<%
		int i = 0;
	%>
	<table width="100%" border="0" cellpadding="0" cellspacing="0">
	<tr>
	<td style="padding-bottom: 5px;">
				<span style="vertical-align: middle;">
					　　奖品名称&nbsp;<html:text name="exchangeForm" styleClass="text4" property="searchstr"></html:text>&nbsp;
				</span>
				<span style="vertical-align: middle;">
					<input type="button" value="查询" class="mybutton" onclick="integral();"/>
				</span>
	</td>
			<td align="right" style="padding-right:5px">
				<b>可用积分&nbsp;<bean:write name="exchangeForm" property="usable_npoint"/></b>
			</td>
	</tr>
	<tr>
	<td colSpan="2">
	<div class="fixedDiv2">
	<table width="100%"  cellspacing="0" cellpadding="0">
		<thead>
			<tr class="fixedHeaderTr">
				<td align="center" width="5%" class="TableRow" style="border-left:none;border-top: none;" nowrap>
					&nbsp;<input type="checkbox" name="allsel" id="allsel" onclick="batch_select_all(this);"/>&nbsp;
				</td>
				<td align="center" width="33%" class="TableRow" style="border-left:none;border-top: none;" nowrap>
					&nbsp;奖品名称&nbsp;
				</td>
				<td align="center" width="18%" class="TableRow" style="border-left:none;border-top: none;" nowrap>
					&nbsp;兑换积分&nbsp;
				</td>
				<td align="center" width="18%" class="TableRow" style="border-left:none;border-top: none;" nowrap>
					&nbsp;剩余数量&nbsp;
				</td>
				<td align="center" width="18%" class="TableRow" style="border-left:none;border-top: none;" nowrap>
					&nbsp;已兑数量&nbsp;
				</td>
				<td align="center" width="13%" class="TableRow" style="border-left:none;border-top: none;border-right: none;" nowrap>
					&nbsp;操作&nbsp;
				</td>
			</tr>
		</thead>
		<hrms:paginationdb id="element" name="exchangeForm"
			sql_str="exchangeForm.strsql" table="" where_str="exchangeForm.strwhere"
			columns="exchangeForm.columns" page_id="pagination"
			pagerows="${exchangeForm.pagerows}" order_by="exchangeForm.order_by">
			<bean:define id="r5701" name="element" property="r5701"></bean:define>
			<%
			String er5701 = SafeCode.encode(PubFunc.encrypt(r5701.toString()));
				if (i % 2 == 0) {
			%>
			<tr class="trShallow" onMouseOver="javascript:tr_onclick(this,'')">
				<%
					} else {
				%>
			
			<tr class="trDeep" onMouseOver="javascript:tr_onclick(this,'')">
				<%
					}
								i++;
				%>
				<td align="center" class="RecordRow" style="border-left:none;border-top: none;" nowrap>
				<logic:greaterThan value="0" name="element" property="r5707">
					&nbsp;<input type="checkbox" name="r5701" value='<%=er5701 %>'/>&nbsp;
				</logic:greaterThan>
				</td>
				<td class="RecordRow" onmouseout="UnTip();" style="border-left:none;border-top: none;" onmouseover="outContent('<%=er5701 %>','r5711');" nowrap>
					&nbsp;<bean:write name="element" property="r5703"/>&nbsp;
				</td>
				<td align="right" class="RecordRow" style="border-left:none;border-top: none;" nowrap>
					&nbsp;<bean:write name="element" property="r5705"/>&nbsp;
				</td>
				<td align="right" class="RecordRow" style="border-left:none;border-top: none;" nowrap>
					&nbsp;<bean:write name="element" property="r5707"/>&nbsp;
				</td>
				<td align="right" class="RecordRow" style="border-left:none;border-top: none;" nowrap>
					&nbsp;<bean:write name="element" property="r5709"/>&nbsp;
				</td>
				<td align="center" class="RecordRow" style="border-left:none;border-top: none;border-right: none;" nowrap>
					<logic:lessEqual value="0" name="element" property="r5707">
						&nbsp;<font color="#cccccc">加入</font>&nbsp;
					</logic:lessEqual>
					<logic:greaterThan value="0" name="element" property="r5707">
					<bean:define id="r5707" name="element" property="r5707"></bean:define>
					<%
						
					    String er5707 = SafeCode.encode(PubFunc.encrypt(r5707.toString()));
					%>
						&nbsp;<a href="javascript:integraladd('<%=er5701 %>','<%=er5707 %>');">加入</a>&nbsp;
					</logic:greaterThan>
				</td>
			</tr>
		</hrms:paginationdb>
	</table>
	</div>
	</td>
	</tr>
	<tr>
			<td colSpan="2">
				<table width="100%" class="RecordRowP" align="center">
					<tr>
						<td valign="bottom" class="tdFontcolor">
							<hrms:paginationtag name="exchangeForm"
								pagerows="${exchangeForm.pagerows}" property="pagination"
								scope="page" refresh="true"></hrms:paginationtag>
						</td>
						<td align="right" nowrap class="tdFontcolor">
								<hrms:paginationdblink name="exchangeForm"
									property="pagination" nameId="exchangeForm" scope="page">
								</hrms:paginationdblink>
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr align="left" style="padding-top: 8px;">
		<td colSpan="2">
		  <input type="button" name="b_batch" value='批量加入' onclick="integralbatch();" class="mybutton"/>
		  <input type="button" name="b_account" value='结算' onclick="account();" class="mybutton"/>
		</td>
		</tr>
	</table>
</html:form>
</body>