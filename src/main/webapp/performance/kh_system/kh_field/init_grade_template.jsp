<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<style>
<!--
.AutoTable{
   BORDER-BOTTOM: medium none; BORDER-COLLAPSE: collapse; 
   BORDER-LEFT: medium none; BORDER-RIGHT: medium none; BORDER-TOP: medium none; 
   TABLE-LAYOUT:fixed;   
   word-break:break-all;
}
-->
</style>
<script type="text/javascript">
function addOrEdit(type,gradeid)
{
    var theurl = "/performance/kh_system/kh_field/init_grade_template.do?b_edit=edit`type="+type+"`gradeid="+gradeid;
    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
    /* if(window.showModalDialog) {
    	var return_vo= window.showModalDialog(iframe_url, arguments, 
        "dialogWidth:500px; dialogHeight:360px;resizable:no;center:yes;scroll:yes;status:no");
        addOrEdit_OK(return_vo);
    }else{
        window.open(iframe_url, "addOrEdit_OK", "width=500; height=360;resizable=no;center=yes;scroll=no;status=no");
	} */
	
    var config = {
  	    width:500,
  	    height:360,
  	    dialogArguments:arguments,
  	    type:'2'
  	}
  	if(!window.showModalDialog)
  		window.dialogArguments = arguments;
  	modalDialog.showModalDialogs(iframe_url,"addOrEdit_OK",config,addOrEdit_OK);
}
function addOrEdit_OK(return_vo) {
    if(return_vo)
    {
        var obj = new Object();
        obj.refresh = return_vo.refresh;
        if(obj.refresh == "2")
        {
            khFieldForm.action="/performance/kh_system/kh_field/init_grade_template.do?b_init=init";
            khFieldForm.submit();
        }
    }
}
function closewindow()
{
    if(window.showModalDialog) {
        parent.window.close();;
    }else{
        window.open("about:blank","_top").close();
    }
}
function deleteRecord()
{
// 	if(ifdel())
   	{
      	var obj=document.getElementsByName("template");
      	var ids ="";
      	var len = obj.length;
      	var num=0;
      	for(var i=0;i<len;i++)
      	{
        	if(obj[i].checked)
        	{
             	ids+="`"+obj[i].value;
             	num++;
        	}
      	}
      	if(num==0)
      	{
         	alert("请选择要删除的标度！");
         	return;
      	}
      	ids=ids.substring(1);
      	
      	if(confirm('确认删除选择的标度吗？'))
      	{
	     	var hashvo=new ParameterSet();
	     	hashvo.setValue("ids",ids);
	     	hashvo.setValue("subsys_id","${khFieldForm.subsys_id}");
	     	var request=new Request({asynchronous:false,onSuccess:delete_ok,functionId:'9021001012'},hashvo);
      	}
   	}
}
function delete_ok(outparameters)
{
  khFieldForm.action="/performance/kh_system/kh_field/init_grade_template.do?b_init=init";
   khFieldForm.submit();
}
</script>
<html:form action="/performance/kh_system/kh_field/init_grade_template">
	<table width="485px" border="0" cellspacing="0" style="margin-left: 3px; margin-top: 1px;" align="left" cellpadding="0">
		<tr>
			<td width="100%">
				<fieldset align="center">
					<legend>
						<bean:message key="kh.field.bdconfig" />
					</legend>
					<div style="height: 210px; overflow:auto;padding:5px 10px">
						<table width="100%" border="0" cellspacing="0" align="center" cellpadding="0" class="AutoTable">
							<thead>
								<tr>
									<td class="TableRow" align="center" width="10%" nowrap>
										<bean:message key="kh.field.select" />
									</td>
									<td class="TableRow" align="center" width="10%" nowrap>
										<bean:message key="kh.field.seq" />
									</td>
									<td class="TableRow" align="center" width="10%" nowrap>
										<bean:message key="kh.field.code" />
									</td>
									<td class="TableRow" align="center" width="20%" nowrap>
										<bean:message key="kh.field.content" />
									</td>
									<td class="TableRow" align="center" width="10%" nowrap>
										<bean:message key="kh.field.scale" />
									</td>
									<td class="TableRow" align="center" width="15%" nowrap>
										<bean:message key="kh.field.topv" />
									</td>
									<td class="TableRow" align="center" width="15%" nowrap>
										<bean:message key="kh.field.bottomv" />
									</td>
									<td class="TableRow" align="center" width="10%" nowrap>
										<bean:message key="kh.field.edit" />
									</td>
								</tr>
							</thead>
							<%
								int i = 0;
							%>
							<logic:iterate id="element" name="khFieldForm" property="gradeList" offset="0" indexId="index">
								<%
									if (i % 2 == 0) {
								%>
								<tr class="trShallow">
									<%
										} else {
									%>
								
								<tr class="trDeep">
									<%
										}
									%>
									<td class="RecordRow" align="center" width="10%">
										<input type="checkbox" name="template" value="<bean:write name="element" property="gradeid"/>" />
									</td>
									<td class="RecordRow" align="right" width="10%">
										<bean:write name="element" property="seq" />
									</td>
									<td class="RecordRow" align="left" width="10%">
										<bean:write name="element" property="gradeid" />
									</td>
									<td class="RecordRow" align="left" width="20%" nowrap>
										<bean:write name="element" property="gradedesc" />
									</td>
									<td class="RecordRow" align="right" width="15%" nowrap>
										<bean:write name="element" property="gradevalue" />
									</td>
									<td class="RecordRow" align="right" width="15%" nowrap>
										<bean:write name="element" property="top_value" />
									</td>
									<td class="RecordRow" align="right" width="15%" nowrap>
										<bean:write name="element" property="bottom_value" />
									</td>
									<td class="RecordRow" align="center" width="10%">
										<img src="/images/edit.gif" border="0" style="cursor: hand;" onclick="addOrEdit('2','<bean:write name="element" property="gradeid"/>');" />
									</td>
								</tr>
								<%
									i++;
								%>
							</logic:iterate>
						</table>
					</div>
				</fieldset>
			</td>
		</tr>
		<tr>
			<td>
				<table width="100%" border="0" cellspacing="0" align="center"
					cellpadding="0" style="margin-top: 7px;">
					<tr>
						<td align="center">
							<input type="button" name="new" class="mybutton" value="<bean:message key="kh.field.new"/>" onclick="addOrEdit('1',' ');" /> 
							<input type="button" name="del" class="mybutton" value="<bean:message key="kh.field.delete"/>" onclick="deleteRecord();" /> 
							<input type="button" name="clo" class="mybutton" value="<bean:message key="button.cancel"/>" onclick="closewindow();" />
						</td>
					</tr>
				</table>
			<td>
		</tr>
	</table>
</html:form>