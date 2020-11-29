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
body{padding-top: 15px;text-align: center;padding-left: 5px;}
.fixedHeaderTr{
 	border-bottom:1px solid #C4D8EE;
 	border-right:1px solid #C4D8EE;
 	border-left:1px solid #C4D8EE;
 	border-top:1px solid #C4D8EE;
}
.mytop
{
	border-top: none;
}
</style>
<html:form action="/train/exchange/exchangeintegral">
	 <br/>
	<%
		int i = 0;
	%>
	<table border="0" align="center" width="680" cellpadding="0" cellspacing="0" class="ListTable">
	<tr>
	<td class="tableRow">
		　<b>兑换车结算</b>
	</td>
	</tr>
	<tr>
	<td class="RecordRow" style="height: 350px;text-align: center">
	<div style="height: 300px;width: 640px;overflow: auto;margin:0 auto;border:1px solid #c4d8ee; padding: 0px;"  class="RecordRow" >
	<table width="100%" border="0" cellspacing="0" align="center" cellpadding="0" style="border-collapse: collapse;">
		<thead>
			<tr class="fixedHeaderTr">
				<td align="center" width="33%" class="TableRow mytop" style="border-left: 0px;" nowrap>
					&nbsp;奖品名称&nbsp;
				</td>
				<td align="center" width="18%" class="TableRow mytop" nowrap>
					&nbsp;兑换积分&nbsp;
				</td>
				<td align="center" width="18%" class="TableRow mytop" nowrap>
					&nbsp;数量&nbsp;
				</td>
				<td align="center" width="18%" class="TableRow mytop" nowrap>
					&nbsp;所需积分&nbsp;
				</td>
				<td align="center" width="13%" class="TableRow mytop" style="border-right: 0px;" nowrap>
					&nbsp;操作&nbsp;
				</td>
			</tr>
		</thead>
		<hrms:paginationdb id="element" name="exchangeForm"
			sql_str="exchangeForm.strsql" table="" where_str="exchangeForm.strwhere"
			columns="exchangeForm.columns" page_id="pagination"
			pagerows="${exchangeForm.pagerows}" order_by="exchangeForm.order_by">
			<%
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
				<bean:define id="id" name="element" property="id" />
				<bean:define id="r5701" name="element" property="r5701" />
				<%String nid = SafeCode.encode(PubFunc.encrypt(id.toString()));
				String nr5701 = SafeCode.encode(PubFunc.encrypt(r5701.toString()));%>
				<td class="RecordRow" style="border-left: 0px;" onmouseout="UnTip();" onmouseover="outContent('<%=nr5701 %>','r5711');" nowrap>
					&nbsp;<bean:write name="element" property="r5703"/>&nbsp;
				</td>
				<td align="right" class="RecordRow" nowrap>
					&nbsp;<bean:write name="element" property="r5705"/>&nbsp;
				</td>
				<td align="center" class="RecordRow" nowrap>
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td width="40" nowrap style="background-color:#FFFFFF";> 
								<div class="m_frameborder">
								<input type="text" value="<bean:write name="element" property="ncount"/>" onkeypress="event.returnValue=IsDigit2(this);" class="text4"
								 onchange="isNumber(this);integraledit('change','<%=nid %>','<%=nr5701 %>',this.value);" style="width: 40px;height: 20px;text-align: right;"/>
									
								</div>
						    </td>
				 			<td>
								<table border="0" cellspacing="2" cellpadding="0">
									<tr><td><button id="0_up" class="m_arrow" onmouseup="integraledit('up','<%=nid %>','<%=nr5701 %>','');">5</button></td></tr>
									<tr><td><button id="0_down" class="m_arrow" onmouseup="integraledit('down','<%=nid %>','<%=nr5701 %>','');">6</button></td></tr>
							    </table>
					        </td>
						</tr>
					 </table> 
				</td>
				<td align="right" class="RecordRow" nowrap>
					&nbsp;<bean:write name="element" property="npoint"/>&nbsp;
				</td>
				<td align="center" class="RecordRow" style="border-right: 0px;" nowrap>
					<a href="javascript:integraledit('del','<%=nid %>','<%=nr5701 %>','');">删除</a>
				</td>
			</tr>
		</hrms:paginationdb>
	</table>
	</div>
	<div style="width: 640px;border-top:0px; border-bottom: 0px; margin:0 auto; padding: 0px;" class="RecordRow">
		<table width="100%" class="ListTable"  align="center" >
			<tr>
				<td class="RecordRow" width="30%" align="right" style="border-style:solid; border-top-width:0px; border-left-width: 0px;">
					&nbsp;您当前的可用积分&nbsp;
				</td>
				<td nowrap class="RecordRow" width="20%" style="color:blue; border-style:solid;border-top-width:0px;">
					&nbsp;<bean:write name="exchangeForm" property="usable_npoint"/>&nbsp;
				</td>
				<td class="RecordRow" width="30%" align="right" style="border-style:solid;border-top-width:0px;">
					&nbsp;本次兑换所需积分&nbsp;
				</td>
				<td nowrap class="RecordRow" width="20%" style="border-style:solid;border-top:0px; border-right:0px;">
					&nbsp;<bean:write name="exchangeForm" property="npoint"/>&nbsp;
				</td>
			</tr>
		</table>
    </div>
	</td>
	</tr>
		<tr align="center">
		<td style="padding-top: 8px;">
		  <input type="button" name="b_return" value='继续选择奖品' onclick="integral();" class="mybutton"/>&nbsp;
		  	<logic:notEqual value="" name="exchangeForm" property="r5703">						
		  		<input type="button" name="b_submit" value='提交' onclick="integralsubmit('submit','','','');" class="mybutton"/>&nbsp;
			</logic:notEqual>
		</td>
		</tr>
	</table>
</html:form>