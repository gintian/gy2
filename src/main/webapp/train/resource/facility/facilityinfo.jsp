<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="com.hrms.frame.codec.SafeCode"%>
<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<script language="javascript" src="/js/dict.js"></script>
<script language="JavaScript" src="/js/validateDate.js"></script>
<style>
.fixedDiv2{
    height:400px;
	*height:expression(document.body.clientHeight-180);
}
</style>
<html:form action="/train/resource/facility/facilityinfo">
	<%
		int i = 0;
		int back = 0;
	%>
	<table border="0" cellpadding="0" cellspacing="0">
	<tr>
	<td height="30">
		<font size="4" style="font-weight: bold;">【${facilityInfoForm.fieldName }】使用情况&nbsp;</font>　　　　　　　　　　
		按时间段&nbsp;&nbsp;从&nbsp;<input type="text" name="startdate" extra="editor" class="text4" dropDown="dropDownDate" onchange=" if(!validate(this,'日期')) {this.focus(); this.value=''; }" value="${facilityInfoForm.startdate }"/>
		至&nbsp;<input type="text" name="enddate" extra="editor" class="text4"  dropDown="dropDownDate" onchange=" if(!validate(this,'日期')) {this.focus(); this.value=''; }" value="${facilityInfoForm.enddate }"/>
		<span style="vertical-align: middle"><input type="button" value="查询" class="mybutton" onclick="search();"></span>
	</td>
	</tr>
	<tr>
	<td>
	<div class="fixedDiv2">
	<table width="100%" border="0" cellspacing="0" align="center"
		cellpadding="0" style="border-collapse: collapse;">
		<thead>
			<tr class="fixedHeaderTr">
				<logic:iterate id="element1" name="facilityInfoForm"
									property="itemList">
					<td align="center" class="TableRow" style="border-top: none;border-left: none;" nowrap>
						&nbsp;<bean:write name="element1" property="itemdesc" filter="false"/>&nbsp;
					</td>
				</logic:iterate>
				<td align="center" class="TableRow" style="border-top: none;border-right: none;" nowrap>
					归还
				</td>
			</tr>
		</thead>
		<hrms:paginationdb id="element" name="facilityInfoForm"
			sql_str="facilityInfoForm.strsql" table="" where_str="facilityInfoForm.strwhere"
			columns="facilityInfoForm.columns" page_id="pagination"
			pagerows="${facilityInfoForm.pagerows}" order_by="facilityInfoForm.order_by">
			<%
				back = 0;
				if (i % 2 == 0) {
			%>
			<tr class="trShallow" onclick="javascript:tr_onclick(this,'');foucsid('<bean:write name="element" property="r5900"/>');">
				<%
					} else {
				%>
			
			<tr class="trDeep" onclick="javascript:tr_onclick(this,'');foucsid('<bean:write name="element" property="r5900"/>');">
				<%
					}
								i++;
				%>
				<logic:iterate id="element1" name="facilityInfoForm" property="itemList">
					<bean:define id="tmpitemid" name="element1" property="itemid" />
					<logic:equal name="element1" property="itemtype" value="D">
						<td class="RecordRow" style="border-top: none;border-left: none;" align="center" nowrap>
							&nbsp;<bean:write name="element" property="${tmpitemid }"/>&nbsp;
						</td>	
					</logic:equal>
					<logic:equal name="element1" property="itemtype" value="N">
						<td class="RecordRow" style="border-top: none;border-left: none;" align="right" nowrap>
							&nbsp;<bean:write name="element" property="${tmpitemid }"/>&nbsp;
							<html:hidden name="element" property="r5900" styleId="r5900"/>
						</td>	
					</logic:equal>
					<logic:equal name="element1" property="itemtype" value="A">
						<logic:notEqual name="element1" property="codesetid" value="0">
							<td class="RecordRow" style="border-top: none;border-left: none;" nowrap>
								<bean:define id="codesetid" name="element1" property="codesetid" />
								<logic:equal value="UM" name="codesetid">
									<hrms:codetoname codeid="UM" name="element" uplevel="${facilityInfoForm.uplevel }" codevalue="${tmpitemid }" codeitem="codeitem" scope="page" />
									&nbsp;<bean:write name="codeitem" property="codename" />
								</logic:equal>
								<logic:notEqual value="UM" name="codesetid">
									<hrms:codetoname codeid="${codesetid }" name="element" codevalue="${tmpitemid }" codeitem="codeitem" scope="page" />
									&nbsp;<bean:write name="codeitem" property="codename" />
								</logic:notEqual>
							</td>
						</logic:notEqual>
					</logic:equal>
					<logic:notEqual name="element1" property="itemtype" value="D">
					<logic:notEqual name="element1" property="itemtype" value="N">
					<logic:equal name="element1" property="codesetid" value="0">
						<td class="RecordRow" style="border-top: none;border-left: none;">
							&nbsp;<bean:write name="element" property="${tmpitemid }"/>&nbsp;
						</td>	
					</logic:equal>	
					</logic:notEqual>
					</logic:notEqual>
				</logic:iterate>
				<td align="center" class="RecordRow" style="border-top: none;border-right: none;" nowrap>
				    <hrms:priv func_id="3230405" module_id="">
						<logic:equal name="element" property="a0101_r" value="">
							<bean:define id="r59id" name="element" property="r5900"></bean:define>
							<%String r5900 = SafeCode.encode(PubFunc.encrypt(r59id.toString())); %>
							<a href="###" onclick="register('in','<%=r5900 %>');">
								<img src="/images/import.gif" alt="归还" border="0" />
							</a>
						</logic:equal>
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
						<hrms:paginationtag name="facilityInfoForm"
							pagerows="${facilityInfoForm.pagerows}" property="pagination"
							scope="page" refresh="true"></hrms:paginationtag>
					</td>
					<td align="right" class="tdFontcolor">
						<hrms:paginationdblink name="facilityInfoForm"
							property="pagination" nameId="facilityInfoForm" scope="page">
						</hrms:paginationdblink>
					</td>
				</tr>
			</table>
		</td>
	</tr>
	<tr>
		<td style="padding-top: 5px;" align="left">
		  <hrms:priv func_id="3230404" module_id="">
			<input type="button" class="mybutton" value="借出" onclick="register('out');" />
		  </hrms:priv>
		  <!-- 
		  <hrms:priv func_id="3230405" module_id="">
			<input type="button" class="mybutton" value="归还" onclick="register('in');" />&nbsp;
		  </hrms:priv> -->
			<input type="button" class="mybutton" value="<bean:message key='button.return'/>" onclick="returnstr();">
		</td>
	</tr>
</table>
</html:form>
<script>
	var r5900 = "";
	function search(){
		facilityInfoForm.action="/train/resource/facility/facilityinfo.do?b_query=link";
		facilityInfoForm.submit();
	}
	function returnstr(){
		facilityInfoForm.action="/train/resource/trainRescList.do?b_query=link&type=${facilityInfoForm.type}";
		facilityInfoForm.submit();
	}
	function register(state, id){
		if(state=="in"&&id==""){
			alert("请选中借出的记录！");
			return;
		}
		var thecodeurl ="/train/resource/facility/facilityinfo.do?b_add=link&state="+state+"&r5900="+id;
		var return_vo= window.showModalDialog(thecodeurl, "", 
              	"dialogWidth:400px; dialogHeight:310px;resizable:no;center:yes;scroll:no;status:no");
        if(return_vo&&return_vo=="ok")
        	search();
	}
	function foucsid(r){
		r5900 = r;
	}
</script>