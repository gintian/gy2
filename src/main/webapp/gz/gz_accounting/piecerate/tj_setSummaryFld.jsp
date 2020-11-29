<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<script language="JavaScript" src="/js/popcalendar.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/constant.js"></script>
<SCRIPT Language="JavaScript">dateFormat='yyyy.mm.dd'</SCRIPT>
<link href="/css/css1.css" rel="stylesheet" type="text/css">
<link rel="stylesheet"	href="/gz/gz_accounting/piecerate/piecerate.css"
	type="text/css">
	<hrms:themes />
<script type="text/javascript">
<!--
var value="";

function saveProject() {
	var tab_id ; 
	var strIds = "";
	var dd = false;
	var index = 0;
	var obj = document.getElementsByName("ids");
	var objValue = document.getElementsByName("summarytype");
	for (var i = 0; i < objValue.length; i++) {
			dd = true;
			strIds = strIds + obj[index].value +":"+objValue[index].value+ ",";
			index++;

	}
	if (!dd) {
		strIds="";

	} else {
		strIds = strIds.substring(0, strIds.length - 1) ;
	}
		window.returnValue=strIds;
	 	window.close();
}

//-->
</script>

<html:form action="/gz/gz_accounting/piecerate/piecerate_tj_def">

	<fieldset style="width: 490px; height: 95%;align:center" align="center">
		<legend>
			定义汇总方式
		</legend>
		<div id="scroll_box2" style="width: 100%;height:190" >
			<table width="100%" border="0" class="ListTable1">
				<tr >
					<td width="70%" class="TableRow" align="center">
						指标名称
					</td>
					<td width="30%" class="TableRow" align="center">
						汇总方式
					</td>
				</tr>

				<hrms:extenditerate id="element" name="pieceRateTjDefineForm"
					property="pageSummaryFld.list" indexes="indexes"
					pagination="pageSummaryFld.pagination" pageCount="2000"
					scope="session">
					<bean:define id="itemid" name="element" property="itemid" />

					<tr class="trShallow">
						<td align="left" class="RecordRow" nowrap>
							&nbsp;
							<bean:write name="element" property="itemname" filter="true" />
							&nbsp;
						</td>
						<td align="center" class="RecordRow" nowrap>

							<input type="hidden" name="ids"
								value="<bean:write  name="element" property="itemid" filter="true"/>">
							<html:select name="element" property="summarytype" onchange="" style="width:80">
								<logic:equal name="element" property="fldtype"	value="N">
									<html:option value="sum">
										<bean:message key="kq.formula.sum" />
									</html:option>
									<html:option value="avg">
										<bean:message key="kq.formula.average" />
									</html:option>
								</logic:equal>
								<html:option value="count">
									<bean:message key="kq.formula.count" />
								</html:option>
								<html:option value="max">
									<bean:message key="kq.formula.max" />
								</html:option>
								<html:option value="min">
									<bean:message key="kq.formula.min" />
								</html:option>
							</html:select>
						</td>
					</tr>
				</hrms:extenditerate>
			</table>
		</div>
        <div id="divbottom" style="width: 100%">
		    <table width="100%"  border="0" align="center">
    			<tr>
    				<td align="center"><input type="button" onclick="saveProject();" value="<bean:message key='button.ok'/>" Class="mybutton">
    				<input type="button" value="<bean:message key='button.cancel'/>" onclick="window.close();" Class="mybutton"></td>
    			</tr>
    		</table>
 	     </div>
	</fieldset>

</html:form>