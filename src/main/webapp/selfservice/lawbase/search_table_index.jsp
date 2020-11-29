<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<style>
<!--
.div
{
 overflow:auto; 
 width: 550px;height: 256px;
 line-height:15px; 
 border-color:#C4D8EE;

}
-->
</style>
<script type="text/javascript">
	function getusableIndex(){
		var usable_fields = document.getElementById("usableIndex").value;
		var usable_value = document.getElementById("usableIndex1").innerHTML;
		var table_fields = document.getElementById("tableIndex").value;
		var table_value = document.getElementById("tableIndex1").innerHTML;
		var basetype = document.getElementById("basetype").value;
		var url = "/selfservice/lawbase/setIndex.do?br_set=link&"+encodeURIComponent("flag=left&basetype="+basetype+"&usable_fields=" + usable_fields + "&usable_value=" + getEncodeStr(usable_value) + 
																		"&table_fields=" + table_fields + "&table_value=" + getEncodeStr(table_value));
		var return_vo = window.showModalDialog(url,1,"dialogWidth:600px; dialogHeight:380px;resizable:no;center:yes;scroll:no;status:no");
		if(return_vo){
			document.getElementById("usableIndex").value = return_vo.file_index_fields;
			document.getElementById("usableIndex1").innerHTML = return_vo.file_index_value;
			document.getElementById("tableIndex").value = return_vo.right_fields;
			document.getElementById("tableIndex1").innerHTML = return_vo.right_value;
		}
	}
	
	function settableIndex(){
		var usable_fields = document.getElementById("usableIndex").value;
		var usable_value = document.getElementById("usableIndex1").innerHTML;
		var table_fields = document.getElementById("tableIndex").value;
		var table_value = document.getElementById("tableIndex1").innerHTML;
		var basetype = document.getElementById("basetype").value;
		var url = "/selfservice/lawbase/setIndex.do?br_set=link&"+encodeURIComponent("flag=right&basetype="+basetype+"&usable_fields=" + usable_fields + "&usable_value=" + getEncodeStr(usable_value) +
																 "&table_fields=" + table_fields + "&table_value=" + getEncodeStr(table_value));
		var return_vo = window.showModalDialog(url,1,"dialogWidth:600px; dialogHeight:380px;resizable:no;center:yes;scroll:no;status:no");
		if(return_vo){
			document.getElementById("tableIndex").value = return_vo.file_index_fields;
			document.getElementById("tableIndex1").innerHTML = return_vo.file_index_value;
		}
	}
	
	function save(){
		var right_fields1 = document.getElementById("tableIndex1").innerHTML;
  		var fields = document.getElementById("usableIndex").value;
  		var fieldsname = document.getElementById("usableIndex1").innerHTML;
  		var field = document.getElementById("tableIndex").value;
  		var fieldname = document.getElementById("tableIndex1").innerHTML;
	    var hashvo = new ParameterSet();
	    var baseId = document.getElementById("base_id").value;
	    hashvo.setValue("fields",fields);
	    hashvo.setValue("fieldsname",fieldsname);
	    hashvo.setValue("field",field);
	    hashvo.setValue("fieldname",fieldname);
	    hashvo.setValue("base_id",baseId);
        var request=new Request({asynchronous:false,onSuccess:save_ok,functionId:'10400201056'},hashvo);
        
	}
	
	function save_ok(outparamters){
		var isok = outparamters.getValue("isok");
		var base_id = outparamters.getValue("base_id");
		if(isok == "1"){
			var thevo = new Object();
			thevo.flag = isok;
			thevo.base_id = base_id;
			window.returnValue = thevo;
			window.close();
		}
	}
</script>
<hrms:themes cssName="content.css"></hrms:themes>
<div class="fixedDiv3">
<html:form action="/selfservice/lawbase/setIndex">
	<html:hidden name="lawbaseForm" property="base_id" styleId="base_id"/>
	<html:hidden name="lawbaseForm" property="basetype" styleId="basetype"/>
	<table width="100%" border="0" cellspacing="0" align="center" cellpadding="0" class="ListTable">
		<thead>
			<tr>
				<td class="TableRow" nowrap colspan="1">
					&nbsp;<bean:message key="wd.lawbase.item"/>&nbsp;
				</td>
			</tr>
		</thead>
		<tr>
			<td align="right" class="RecordRow" nowrap>
				<table style="margin: 10 0 10 0;" cellpadding="0" cellspacing="0">
					<tr>
						<td valign="top"><bean:message key="wd.lawbase.available.index"/>&nbsp;&nbsp;</td>
						<td>
							<input type="hidden" id="usableIndex" name="usableIndex" value='<bean:write name="lawbaseForm" property="usable_fields"/>'/>
							<div id="usableIndex1" class="RecordRow" style="width: 420px; height: 75px;text-align: left;padding-left: 3px;overflow:auto;">
								<bean:write name="lawbaseForm" property="usable_value"/>
							</div>
						</td>
						<td valign="top" align="right"><input type="button" value='...' class="mybutton" onclick="getusableIndex();"></td>
					</tr>
					<tr style="padding-top: 10px;">
						<td valign="top"><bean:message key="wd.lawbase.tableIndex"/>&nbsp;&nbsp;</td>
						<td>
							<input type="hidden" id="tableIndex" name="tableIndex" value='<bean:write name="lawbaseForm" property="table_fields"/>'/>
							<div id="tableIndex1" class="RecordRow" style="width: 420px; height: 75px;text-align: left;padding-left: 3px;overflow:auto;">
								<bean:write name="lawbaseForm" property="table_value"/>
							</div>
						</td>
						<td valign="top" align="right"><input type="button" value='...' class="mybutton" onclick="settableIndex();"></td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
	<div align="center" style="margin-top: 5px;">
		<html:button styleClass="mybutton" property="b_save" onclick="save();">
			<bean:message key="button.save" />
		</html:button>
		<html:button styleClass="mybutton" property="cancel" onclick="window.close();">
			<bean:message key="button.cancel" />
		</html:button>
	</div>
</html:form>
</div>