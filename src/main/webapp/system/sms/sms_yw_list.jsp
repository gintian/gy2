<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<hrms:themes></hrms:themes>
<script type="text/javascript" src="/js/constant.js"></script>

<%
int i=0;

%>

<html:form action="/system/sms/interface_param_yw.do?b_query=link" styleId="form1">

<table width="535" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable"  style="margin-top:0px;margin-left:2px;">
	
    <tr> 
      <td align="center" class="TableRow" nowrap width="10%" >
      <input type="checkbox" name="selbox" onclick="selectAll()" 
      title='<bean:message key="label.query.selectall"/>' id="selbox"/></td>
      <td align="center" class="TableRow" nowrap width="20%"><bean:message key="system.sms.ywcode"/></td>
      <td align="center" class="TableRow" nowrap width="40%"><bean:message key="system.sms.ywdesc"/></td>
      <td align="center" class="TableRow" nowrap width="20%"><bean:message key="system.sms.ywstutas"/></td>	 
      <td align="center" class="TableRow" nowrap width="10%"><bean:message key="system.sms.ywedit"/></td>	  
    </tr>

	<logic:iterate id="element" name="interParamForm" property="ywList" indexId="index">
		<%
          if(i%2==0)
          {
          %>
          <tr class="trShallow">
          <%}
          else
          {%>
          <tr class="trDeep">
          <%
          }
          i++;          
          %> 
		<td align="center" class="RecordRow" style="word-break:break-all;" width="10%">              
			&nbsp;<input type="checkbox" name="check<%=index %>" value="<bean:write name="element" property="code"/>" id="check<%=index %>"/>&nbsp;
		</td>
		<td align="left" class="RecordRow" nowrap width="20%">              
			&nbsp;<bean:write name="element" property="code"/>&nbsp;
		</td>
		<td align="left" class="RecordRow" width="40%" style="word-break:break-all;">              
			&nbsp;<bean:write name="element" property="desc"/>&nbsp;
		</td>
		<td align="left" class="RecordRow" nowrap width="20%">
			&nbsp;
			<logic:equal name="element" property="status" value="1">
				<bean:message key="system.sms.ywable"/>
			</logic:equal>              
			<logic:notEqual name="element" property="status" value="1">
				<bean:message key="system.sms.ywdisable"/>
			</logic:notEqual>
			&nbsp;
		</td>
		<td class="RecordRow" align="center" nowrap width="10%">
			&nbsp;<img src="/images/edit.gif" border=0 onclick="updatelink('<bean:write name="element" property="code"/>')" style="cursor: pointer;">&nbsp;
		</td>
	</logic:iterate>

  </table>

<table  width="90%" align="center">
       <tr height="35px;">
          <td align="center">
          	<input type="button" name="b_add" class="mybutton" onclick="add()" value="<bean:message key="button.new.add"/>"/>&nbsp;&nbsp;
          	<input type="button" name="b_able" class="mybutton" onclick="update('1')" value="<bean:message key="system.sms.ywable"/>"/>&nbsp;&nbsp;
          	<input type="button" name="b_disable" class="mybutton" onclick="update('0')" value="<bean:message key="system.sms.ywstop"/>"/>&nbsp;&nbsp;
          	<input type="button" name="b_delete" class="mybutton" onclick="update('3')" value="<bean:message key="button.delete"/>"/>&nbsp;&nbsp;
          </td>
        </tr>          
</table>

</html:form>

<script type="text/javascript">
<!--
	// 全选
	function selectAll() {
		var selbox = document.getElementById("selbox");
		var ckbs = document.getElementsByTagName("input");
		
		if (selbox.checked == true) {
			for(var i = 0;i < ckbs.length; i++){
				if(ckbs[i].type == "checkbox" && ckbs[i].name != "selbox"){
						ckbs[i].checked = true;
				}
			}
		} else {
			for(var i = 0;i < ckbs.length; i++){
				if(ckbs[i].type == "checkbox" && ckbs[i].name != "selbox"){
						ckbs[i].checked = false;
				}
			}
		}
	}
	
	// 新增
	function add() {
		var formm = document.getElementById("form1");
		formm.action="/system/sms/interface_param_yw.do?b_add=link&encryptParam=<%=PubFunc.encrypt("opt=addLink")%>";
		formm.submit();
	}
	
	// 编辑
	function updatelink(code) {
		var formm = document.getElementById("form1");
		formm.action="/system/sms/interface_param_yw.do?b_add=link&opt=updateLink&code="+$URL.encode(code);
		formm.submit();
	}
	
	// 启用、暂停、删除
	function update(flag) {
		var formm = document.getElementById("form1");
		
		var opts = "";
		var ckbs = document.getElementsByTagName("input");
		for(var i = 0;i < ckbs.length; i++){
			if(ckbs[i].type == "checkbox" && ckbs[i].name != "selbox" &&ckbs[i].checked==true){
					opts = opts + "," + ckbs[i].value;
				}
		}
		if (opts.length > 0) {
			opts = opts.substr(1);
		} else {
			alert("未选择记录！");
			return false;
		}
		
		if (flag =="1") {
			formm.action="/system/sms/interface_param_yw.do?b_update=link&opt=able&status=1&codes="+$URL.encode(opts);
		} else if (flag =="0") {
			formm.action="/system/sms/interface_param_yw.do?b_update=link&opt=able&status=0&codes="+$URL.encode(opts);
		} else  if (flag =="3") {
			if (confirm(DEL_INFO)) {
				formm.action="/system/sms/interface_param_yw.do?b_update=link&opt=delete&status=0&codes="+$URL.encode(opts);
			} else {
				return false;
			}
		}
		formm.submit();
	}
//-->
</script>
