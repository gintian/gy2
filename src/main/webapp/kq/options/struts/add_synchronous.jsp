<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.kq.options.struts.KqStrutForm" %>
<%
	KqStrutForm form = (KqStrutForm) request.getSession().getAttribute("kqStrutForm");
	String ops = form.getSyncxml_options();
	ops = "," + ops + ",";
 %>

<style>
<!--
.myinput{
	width: 200px;
	border: inset 1px #B9D2F5;
	BORDER-BOTTOM: #B9D2F5 1pt solid; 
	BORDER-LEFT: #B9D2F5 1pt solid; 
	BORDER-RIGHT: #B9D2F5 1pt solid; 
	BORDER-TOP: #B9D2F5 1pt solid;
	font-size: 12px;
}
-->
</style>
<hrms:themes></hrms:themes>
<html:form action="/kq/options/struts/kqsynchronous.do?b_query=link" method="post">
<html:hidden name="kqStrutForm" property="syncxml_id"/>
<table width="80%" align="center">
 <tr>
 	<td class="framestyle" valign="top">
 		<br>
 		<table  width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0" valign="top">
 			<tr>
 				<td>
					<table width="90%" style="margin-top: 5px;padding: 3px;" align="center" class="ListTable" cellpadding="0" cellspacing="0">
						<tr>
							<td class="RecordRow"  align="right" nowrap>
								类　型&nbsp;
							</td>
							<td class="RecordRow" nowrap>
								<html:select name="kqStrutForm" property="syncxml_dbtype" styleClass="myinput text4" styleId="dbtype" onchange="hiddenDiv()">
									<html:optionsCollection property="syncxml_dbtype_list" value="dataValue" label="dataName" />
								</html:select>
							</td>
						</tr>
						<tr>
							<td class="RecordRow" align="right" nowrap>
								数据库名&nbsp;
							</td>
							<td class="RecordRow" nowrap>
								<html:text name="kqStrutForm" property="syncxml_dbname" styleClass="myinput text4" />
							</td>
						</tr>
						<tr>
							<td class="RecordRow" align="right" nowrap>
								描　述&nbsp;
							</td>
							<td class="RecordRow" nowrap>
								<html:textarea name="kqStrutForm" property="syncxml_desc" cols="45" rows="5" style="myinput text4"></html:textarea>
							</td>
						</tr>
						<tr>
							<td class="RecordRow" align="right" nowrap>
								IP地址&nbsp;
							</td>
							<td class="RecordRow" nowrap>
								<html:text name="kqStrutForm" property="syncxml_ip" styleClass="myinput text4" />
							</td>
						</tr>
						<tr>
							<td class="RecordRow" align="right" nowrap>
								端　口&nbsp;
							</td>
							<td class="RecordRow" nowrap>
								<html:text name="kqStrutForm" property="syncxml_port" styleClass="myinput text4" />
							</td>
						</tr>
						<tr id="di">
							<td class="RecordRow" align="right" nowrap>
								同步的表所属的用户&nbsp;
							</td>
							<td class="RecordRow" nowrap>
								<html:text name="kqStrutForm" property="syncxml_space" styleClass="myinput text4" /><font color="red"> 提示：一般为登录的用户名</font>
							</td>
						</tr>
						<tr>
							<td class="RecordRow" align="right" nowrap>
								用户名&nbsp;
							</td>
							<td class="RecordRow" nowrap>
								<html:text name="kqStrutForm" property="syncxml_user" styleClass="myinput text4" />
							</td>
						</tr>
						<tr>
							<td class="RecordRow" align="right" nowrap>
								密　码&nbsp;
							</td>
							<td class="RecordRow" nowrap>
								<html:password name="kqStrutForm" property="syncxml_pwd" styleClass="myinput text4" styleId="pwdw"></html:password>
							</td>
						</tr>
						<tr>
							<td class="RecordRow" align="right" nowrap>
								密码确认&nbsp;
							</td>
							<td class="RecordRow" nowrap>
								<logic:empty name="kqStrutForm" property="syncxml_pwd">
									<input type="password" name="myinput pwd" class="myinput text4" value="" id="pwdd"/>
								</logic:empty>
								<logic:notEmpty name="kqStrutForm" property="syncxml_pwd">
									<input type="password" name="pwd" class="myinput text4" value="<bean:write name="kqStrutForm" property="syncxml_pwd"/>" id="pwdd"/>
								</logic:notEmpty>
							</td>
						</tr>
						<tr>
							<td class="RecordRow" align="right" nowrap>
								关联指标&nbsp;
							</td>
							<td class="RecordRow" nowrap>
								<html:select name="kqStrutForm" property="syncxml_related" styleClass="myinput text4">
									<html:optionsCollection property="syncxml_related_list" value="dataValue" label="dataName" />
								</html:select>
								<font color=red>提示：该指标用于目标表中的请假、加班、公出与系统中人员进行对应，刷卡表不使用</font>
							</td>
						</tr>
						<tr>
							<td class="RecordRow" align="right" nowrap>
								刷卡数据表&nbsp;
							</td>
							<td class="RecordRow" nowrap>
								<html:text name="kqStrutForm" property="syncxml_source" styleClass="myinput text4" />
							</td>
						</tr>
						<tr>
							<td class="RecordRow" align="right" nowrap>
								目标表&nbsp;
							</td>
							<td class="RecordRow" nowrap>
								<html:hidden name="kqStrutForm" property="syncxml_options"/>
								<%if(ops.contains(",Q15,")) {  %>
									<input type="checkbox" name="options1" value="Q15" checked="checked"/>请假申请表&nbsp;&nbsp;
								<%} else { %>
									<input type="checkbox" name="options1" value="Q15" />请假申请表&nbsp;&nbsp;
								<%} %>
								
								<%if(ops.contains(",Q11,")) {  %>
								<input type="checkbox" name="options1" value="Q11" checked="checked"/>加班申请表&nbsp;&nbsp;
								<%} else { %>
								<input type="checkbox" name="options1" value="Q11" />加班申请表&nbsp;&nbsp;
								<%} %>
								
								<%if(ops.contains(",Q13,")) {  %>
								<input type="checkbox" name="options1" value="Q13" checked="checked"/>公出申请表&nbsp;&nbsp;
								<%} else { %>
								<input type="checkbox" name="options1" value="Q13" />公出申请表&nbsp;&nbsp;
								<%} %>
								
								<%if(ops.contains(",kq_originality_data,")) {  %>
								<input type="checkbox" name="options1" value="kq_originality_data" checked="checked"/>考勤刷卡表&nbsp;&nbsp;
								<%} else { %>
								<input type="checkbox" name="options1" value="kq_originality_data" />考勤刷卡表&nbsp;&nbsp;
								<%} %>
							</td>
						</tr>
						<tr>
							<td class="RecordRow" align="right" nowrap>
								启　用&nbsp;
							</td>
							<td class="RecordRow" nowrap>
								<html:hidden name="kqStrutForm" property="syncxml_status"/>
								<logic:equal value="1" name="kqStrutForm" property="syncxml_status">
									<input name="status" type="checkbox" value="1" checked="checked">
								</logic:equal> 
								<logic:notEqual value="1" name="kqStrutForm" property="syncxml_status">
									<input name="status" type="checkbox" value="1">
								</logic:notEqual> 
							</td>
						</tr>
						<tr>
							<td colspan="2" style="text-align: center; padding-top: 7px;padding-left: 15px;">
								<input type="button" class="mybutton" value="确定" onclick="save()"/>
								<input type="button" class="mybutton" value="返回" onclick="returnSelect()"/>
							</td>
						</tr>
					</table>
					</td>
	 			</tr>
	 		</table>
	 		<br>
	 		<br>
	 	</td>
	 </tr>
</table>
</html:form>
<script type="text/javascript">
<!--
function save(){

	if (!vali()){
		alert("密码不一致！请重新确认密码！");
		return;
	}
	var statuarr = document.getElementsByName("status");
	if (statuarr[0].checked==true) {
		document.getElementsByName("syncxml_status")[0].value="1";
	} else {
		document.getElementsByName("syncxml_status")[0].value="0";
	}
	var op = document.getElementsByName("options1");
	var opts = "";
	for ( i = 0; i < op.length; i++) {
		if (op[i].checked == true) {
			opts = opts + "," + op[i].value;
		}
	}
	document.getElementsByName("syncxml_options")[0].value=opts.substr(1);
	document.forms[0].action="/kq/options/struts/kqsynchronous.do?b_save=link&type="+"<bean:write name="kqStrutForm" property="type"/>";
	document.forms[0].submit();
}

/**验证密码*/
function vali() {
	var pwd = document.getElementById("pwdd");
	var pwdy = document.getElementById("pwdw");
	if (pwd.value == pwdy.value) {
		return true;
	} else {
		return false;
	}
}

function returnSelect() {
	document.forms[0].action="/kq/options/struts/kqsynchronous.do?b_query=link";
	document.forms[0].submit();
}
//-->
</script>
<script>
//指定父窗体的高度
/*var doc = document,p = window;
while(p = p.parent){
	var frames = p.frames,frame,i = 0;
	while(frame = frames[i++]){
		if(frame.document == doc){
			frame.frameElement.style.height = doc.body.scrollHeight;
			doc = p.document;
			break;
		}
	}
	if(p == top){
	break;
	}
}*/

function hiddenDiv() {
	var dbtype = document.getElementById("dbtype");
	if (dbtype.value == "mssql") {
		document.getElementById("di").style.display="none";
	} else {
		document.getElementById("di").style.display="";
	}

}
hiddenDiv();
//屏蔽鼠标右键
/**if (window.Event) 
	document.captureEvents(Event.MOUSEUP); 

function nocontextmenu() { 
	event.cancelBubble = true 
	event.returnValue = false; 
	return false; 
} 
document.oncontextmenu = nocontextmenu; //对ie5.0以上 
*/
</script>