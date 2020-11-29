<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<script type="text/javascript">
	function edit(){
		var indexName = document.getElementById("index").value;
		if("" == indexName ){
			alert("请输入要显示的名称！");
		}
		if(indexName.indexOf("、")!= -1){
			alert("重命名指标名称不能包含特殊字符'、'！");
		}else{
			window.returnValue = indexName;
			window.close();
		}
	}
	
	function init(){
		var selected_name = window.dialogArguments;
		document.getElementById("index").value = selected_name;
	}
</script>
<html:form action="/selfservice/lawbase/setIndex">
	<table border="0" cellspacing="0" width="260" align="center" cellpadding="0" width="90%" >
		<tr>
			<td>
				<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
					<thead class="TableRow">
						<tr>
							<td align="center" class="TableRow" nowrap colspan="0">
								重命名
							</td>
						</tr>
					</thead>
					<tr>
						<td align="center" class="RecordRow">
    					    <br/>
							显示名称&nbsp;<input type="text" id="index" name="indexName" class="text4"/>
	    					<br/>
	    					<br/>
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
	<div align="center" style="margin-top: 5px;">
		<html:button styleClass="mybutton" property="b_save" onclick="edit();">
			<bean:message key="button.ok" />
		</html:button>
		<html:button styleClass="mybutton" property="cancel" onclick="window.close();">
			<bean:message key="button.cancel" />
		</html:button>
	</div>
</html:form>
<script language="javascript">
    init();
</script>