<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ page import="com.hrms.frame.codec.SafeCode"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<script language="javascript" src="/js/dict.js"></script>
<script language="JavaScript" src="/js/wz_tooltip.js"></script>
<script type="text/javascript" src="exchange.js"></script>
<script type="text/javascript">

</script>
<body>
<html:form action="/train/exchange/exchangemanage">
	 <html:hidden name="exchangeForm" property="a_code" />
	<%
		int i = 0;
	%>
	<table border="0" cellpadding="0" cellspacing="0" style="margin-top:5px;">
	<tr>
	<td style="padding-bottom:2px;">
		状态
	<span style="vertical-align: middle; margin-right:5px;">
		<html:select name="exchangeForm" property="r5713" onchange="exchange(); " styleId="xz">
			<html:option value="00">全部</html:option>
			<html:option value="01">起草</html:option>
			<html:option value="04">发布</html:option>
			<html:option value="09">暂停</html:option>
		</html:select>
	</span>
		奖品名称
		<span style="vertical-align: middle;">
		<html:text name="exchangeForm" styleClass="text4" property="searchstr"></html:text>
		</span>
		<span style="vertical-align: middle;">
		<input type="button" value="查询" class="mybutton" onclick="exchange();"/>
		</span>
	</td>
	</tr>
	<tr>
	<td style="padding-top:5px;">
	<div class="fixedDiv2">
	<table width="100%" border="0" cellspacing="0" align="center"
		cellpadding="0">
		<thead>
			<tr class="fixedHeaderTr">
				<hrms:priv func_id="323913">
				<td align="center" width="35" height="20" class="TableRow" style="border-left: none;border-top: none;" nowrap>
					<input type="checkbox" name="allsel" id="allsel" onclick="batch_select_all(this);"/>
				</td>
				</hrms:priv>
				<td align="center" width="23%" class="TableRow" style="border-left: none;border-top: none;" nowrap>
					&nbsp;奖品名称&nbsp;
				</td>
				<td align="center" width="11%" class="TableRow" style="border-left: none;border-top: none;" nowrap>
					&nbsp;兑换积分&nbsp;
				</td>
				<td align="center" width="11%" class="TableRow" style="border-left: none;border-top: none;" nowrap>
					&nbsp;剩余数量&nbsp;
				</td>
				<td align="center" width="11%" class="TableRow" style="border-left: none;border-top: none;" nowrap>
					&nbsp;已兑数量&nbsp;
				</td>
				<td align="center" width="23%" class="TableRow" style="border-left: none;border-top: none;" nowrap>
					&nbsp;所属单位&nbsp;
				</td>
				<td align="center" width="23%" class="TableRow" style="border-left: none;border-top: none;" nowrap>
					&nbsp;奖品状态&nbsp;
				</td>
				<td align="center" width="21%" class="TableRow" style="border-left: none;border-top: none;border-right: none;" nowrap>
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
					<bean:define id="r57011" name="element" property="r5701"/>
					<bean:define id="r57031" name="element" property="r5703"/>
					<%
						String r5701 = PubFunc.encrypt(r57011.toString());
						String r5703 = PubFunc.encrypt(r57031.toString());
					%>
				<hrms:priv func_id="323913">
				<td align="center" class="RecordRow" style="border-left: none;border-top: none;" nowrap>
				<input type="hidden" name="names" value = '<bean:write name="element" property="r5713" filter="false" />'/>
					&nbsp;<input type="checkbox" name="r5701" value='<bean:write name="element" property="r5701"/>'/>&nbsp;
				<!-- 
					<logic:lessEqual value="0" name="element" property="r5709">
				<logic:notEqual value="04" name="element" property="r5713">
					&nbsp;<input type="checkbox" name="r5701" value='<bean:write name="element" property="r5701"/>'/>&nbsp;
				</logic:notEqual>
				</logic:lessEqual>
				-->
				</td>
				</hrms:priv>
				<td class="RecordRow"  style="border-left: none;border-top: none;" onmouseout="UnTip();" onmouseover="outContent('<%=r5701%>','r5711');" nowrap>
					&nbsp;<bean:write name="element" property="r5703"/>&nbsp;
				</td>
				<td align="right" style="border-left: none;border-top: none;" class="RecordRow" nowrap>
					&nbsp;<bean:write name="element" property="r5705"/>&nbsp;
				</td>
				<td align="right" class="RecordRow" style="border-left: none;border-top: none;" nowrap>
					&nbsp;<bean:write name="element" property="r5707"/>&nbsp;
				</td>
				<td align="right" class="RecordRow" style="border-left: none;border-top: none;" nowrap>
					&nbsp;<bean:write name="element" property="r5709"/>&nbsp;
				</td>
				<td class="RecordRow" style="border-left: none;border-top: none;" nowrap>
				  &nbsp;
				   <logic:notEqual name="element" property="b0110" value="HJSJ">
						<hrms:codetoname codeid="UN" name="element" codevalue="b0110" codeitem="codeitem" scope="page" />
					    <bean:write name="codeitem" property="codename" />
				   </logic:notEqual>
					<logic:equal name="element" property="b0110" value="HJSJ">
						<bean:message key="jx.khplan.hjsj" />
					</logic:equal>
					&nbsp;
				</td>
				<td class="RecordRow" style="border-left: none;border-top: none;" nowrap>
					&nbsp;<hrms:codetoname codeid="23" name="element" codevalue="r5713" codeitem="codeitem" scope="page" />
					<bean:write name="codeitem" property="codename" />&nbsp;
				</td>
				<td align="left" class="RecordRow" style="border-left: none;border-top: none;border-right: none;" nowrap><font color="#cccccc"></font>
					<!--<logic:equal value="01" name="element" property="r5713">
						&nbsp;<font color="#cccccc">详情</font>&nbsp;
					</logic:equal>-->
					<logic:notEqual value="01" name="element" property="r5713">
						&nbsp;<a href="javascript:exchangeinfo('<%=r5701 %>','<%=r5703 %>');" on title="兑换详情"><img src="/images/view.gif" BORDER="0" style="cursor:hand;" alt="详情"/></a>&nbsp;
					</logic:notEqual>
					<hrms:priv func_id="323912">
					<!--<logic:equal value="04" name="element" property="r5713">
						&nbsp;<font color="#cccccc">修改</font>&nbsp;	
					<!--  	&nbsp;<a href="javascript:exchangestatus('<bean:write name="element" property="r5701"/>','<bean:write name="element" property="r5713"/>');">暂停</a>&nbsp;
					</logic:equal>-->
					<logic:notEqual value="04" name="element" property="r5713">
						&nbsp;<a href="javascript:exchangeadd('<%=r5701 %>');"><img src="/images/edit.gif" BORDER="0" style="cursor:hand;" alt="修改"/></a>&nbsp;
					<!--  	&nbsp;<a href="javascript:exchangestatus('<bean:write name="element" property="r5701"/>','<bean:write name="element" property="r5713"/>');">发布</a>&nbsp;-->
					</logic:notEqual>
					</hrms:priv>
				</td>
			</tr>
		</hrms:paginationdb>
	</table>
	</div>
	</td>
	</tr>
	<tr>
			<td>
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
		<td>
		<hrms:priv func_id="323911">
		  <input type="button" name="b_add" value='新增' onclick="exchangeadd('');" class="mybutton"/>
		</hrms:priv>
		<hrms:priv func_id="323913">
		  <input type="button" name="b_del" value='删除' onclick="exchangedelete();" class="mybutton"/>
		</hrms:priv>
		<hrms:priv func_id="323917">
		  <input type="button" name="b_del" value='发布' onclick="exchangestatus('04');" class="mybutton"/>
		</hrms:priv>
		<hrms:priv func_id="323916">
		  <input type="button" name="b_del" value='暂停' onclick="exchangestatus('09');" class="mybutton"/>
		</hrms:priv>
		<hrms:priv func_id="323915">
		  <input type="button" name="b_moban" value='下载模板' onclick="excelTemplate();" class="mybutton"/>
		</hrms:priv>
		<hrms:priv func_id="323914">
		  <input type="button" name="b_daoru" value='导入数据' onclick="excelimport();" class="mybutton"/>
		</hrms:priv>
		</td>
		</tr>
	</table>
</html:form>
</body>