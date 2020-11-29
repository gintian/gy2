<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<script type="text/javascript">
	function getusableIndex(){
		var url = "/selfservice/lawbase/setIndex.do?br_query=link&flag=left";
		var return_vo = window.showModalDialog(url,1,"dialogWidth:550px; dialogHeight:380px;resizable:no;center:yes;scroll:yes;status:no");
		if(return_vo){
			document.getElementById("usableIndex").value = return_vo.file_index_fields;
			document.getElementById("usableIndex1").innerHTML = return_vo.file_index_value;
		}
	}
	
	function settableIndex(){
		var usable_fields = document.getElementById("usableIndex").value;
		var usable_value = document.getElementById("usableIndex1").innerHTML;
		var table_fields = document.getElementById("tableIndex").value;
		var table_value = document.getElementById("tableIndex1").innerHTML;
		var url = "/selfservice/lawbase/setIndex.do?br_query=link&flag=right&table_fields=" + table_fields + "&table_value=" + getEncodeStr(table_value) +
																 "&usable_fields=" + usable_fields + "&usable_value=" + getEncodeStr(usable_value);
		var return_vo = window.showModalDialog(url,1,"dialogWidth:550px; dialogHeight:380px;resizable:no;center:yes;scroll:yes;status:no");
		if(return_vo){
			document.getElementById("tableIndex").value = return_vo.file_index_fields;
			document.getElementById("tableIndex1").innerHTML = return_vo.file_index_value;
		}
	}
	
	function save(){
		var right_fields1 = document.getElementById("tableIndex1").innerHTML;
		
		if(right_fields1.length > 0)
	  	{
	  		var fields = document.getElementById("tableIndex").value;
	  		var fieldsname = document.getElementById("tableIndex1").innerHTML;
		    var hashvo = new ParameterSet();
		    var baseId = document.getElementById("base_id").value;
		    hashvo.setValue("fields",fields);
		    hashvo.setValue("fieldsname",fieldsname);
		    hashvo.setValue("base_id",baseId);
	        var request=new Request({asynchronous:false,onSuccess:save_ok,functionId:'10400201056'},hashvo);
        }
	}
	
	function save_ok(outparamters){
		var isok = outparamters.getValue("isok");
		if(isok == "1"){
			var thevo = new Object();
			thevo.flag = isok;
			window.returnValue = isok;
			window.close();
		}
	}
</script>
<html:form action="/selfservice/lawbase/setIndex">
	<html:hidden name="lawbaseForm" property="base_id" />
	<br>
	<table width="90%" border="0" cellspacing="0" align="center" cellpadding="0" class="ListTable">
		<thead>
			<tr>
				<td class="TableRow" nowrap colspan="1">
					&nbsp;<bean:message key="wd.lawbase.item"/>&nbsp;
				</td>
			</tr>
		</thead>
		<tr>
			<td align="center" class="RecordRow" nowrap>
				<table style="margin: 10 0 10 0;" cellpadding="4">
					<tr>
						<td valign="top"><bean:message key="wd.lawbase.available.index"/></td>
						<td>
							<input type="hidden" id="usableIndex" name="usableIndex" value='<bean:write name="lawbaseForm" property="usable_fields"/>'/>
							<div id="usableIndex1" style="width: 390px; height: 75px;text-align: left;border: 1px solid #C4D8EE;padding-left: 3px;overflow:auto;">
								<bean:write name="lawbaseForm" property="usable_value"/>
							</div>
						</td>
						<td valign="top"><input type="button" value='...' class="mybutton" onclick="getusableIndex();"></td>
					</tr>
					<tr>
						<td valign="top"><bean:message key="wd.lawbase.tableIndex"/></td>
						<td>
							<input type="hidden" id="tableIndex" name="tableIndex" value='<bean:write name="lawbaseForm" property="table_fields"/>'/>
							<div id="tableIndex1" style="width: 390px; height: 75px;text-align: left;border: 1px solid #C4D8EE;padding-left: 3px;overflow:auto;">
								<bean:write name="lawbaseForm" property="table_value"/>
							</div>
						</td>
						<td valign="top"><input type="button" value='...' class="mybutton" onclick="settableIndex();"></td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td align="center" class="RecordRow" nowrap colspan="1"  style="height: 35">
				<html:button styleClass="mybutton" property="b_save" onclick="save();">
					<bean:message key="button.ok" />
				</html:button>
				<html:button styleClass="mybutton" property="cancel" onclick="window.close();">
					<bean:message key="button.cancel" />
				</html:button>
			</td>
		</tr>
	</table>
</html:form>