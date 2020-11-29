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
<link rel="stylesheet"
	href="/gz/gz_accounting/piecerate/piecerate.css"
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
	for (var i = 0; i < document.pieceRateTjDefineForm.elements.length; i++) {
		if (document.pieceRateTjDefineForm.elements[i].type == "checkbox"&&document.pieceRateTjDefineForm.elements[i].name!="quanxuan") {
			if (document.pieceRateTjDefineForm.elements[i].checked) {
				dd = true;
				strIds = strIds + obj[index].value + ",";
			}
			index++;
		}
	}
	if (!dd) {
		//alert("没有选择任何分组指标");
		strIds="";
		return;
	} else {
		strIds = strIds.substring(0, strIds.length - 1) ;
		window.returnValue=strIds;
	 	window.close();
	}
	
}


//-->
</script>

<html:form action="/gz/gz_accounting/piecerate/piecerate_tj_def">
	<fieldset style="width: 490px; height: 85%" align="center" >
		<legend>
			定义分组指标
		</legend>
		<div id="scroll_box2"  style="width: 100%;height:190">
			<table width="100%" border="0" class="ListTable1">
				<tr >
					<td width="70%" class="TableRow" align="center">
						指标名称
					</td>
					<td width="30%" class="TableRow" align="center">
						是否分组
					</td>
				</tr>

				<hrms:extenditerate id="element" name="pieceRateTjDefineForm"
					property="pageGroupFld.list" indexes="indexes"
					pagination="pageGroupFld.pagination" pageCount="2000"
					scope="session">
					<bean:define id="itemid" name="element" property="itemid" />

					<tr class="trShallow" >
						<td align="left" class="RecordRow" nowrap>
							&nbsp;
							<bean:write name="element" property="itemname" filter="true" />
							&nbsp;
						</td>
						<td align="center" class="RecordRow" nowrap>
						<logic:notEqual name="element" property="isselected" value="1">
							<hrms:checkmultibox name="pieceRateTjDefineForm"
								property="pageGroupFld.select" value="true"
								indexes="indexes" />
						</logic:notEqual>
						
						<logic:equal name="element" property="isselected" value="1">
							<hrms:checkmultibox name="pieceRateTjDefineForm"
								property="pageGroupFld.select" value="false"
								indexes="indexes" />
						</logic:equal>
						
							<input type="hidden" name="ids"
								value="<bean:write  name="element" property="itemid" filter="true"/>">
							<input type="hidden"
								name="<bean:write  name="element" property="itemid" filter="true"/>"
								value="<bean:write  name="element" property="itemid" filter="true"/>">
						</td>


					</tr >	
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