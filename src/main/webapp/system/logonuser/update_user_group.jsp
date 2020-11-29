<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.sys.LogonUserForm" %>

<%
	String group_name = (String)request.getParameter("group_name");
 %>
 <link rel="stylesheet" href="/css/css1.css" type="text/css"></link>
 <hrms:themes></hrms:themes>
<html:form action="/system/logonuser/search_user_tree">
<table width="290" cellpadding="0" cellspacing="0" align="right" style="margin-top:5px;">
<tr>
<td align="center">
<table width="100%" cellpadding="0" cellspacing="0" class="ListTable">
	<thead>
	<tr class="list3">
    	<td align="left" class="TableRow" nowrap>
    		<bean:message key="label.update.usergroupname"/>&nbsp;
    	</td> 
	</tr> 
	</thead>
  <tr>
  	<td align="center" nowrap class="RecordRow" style="height: 85px;">
  		<bean:message key="column.name"/>
  		<input type="text" id="newname" value =<%=group_name %> maxlength="20" class="text4" style="width:200px;margin-left:5px;">
  		<input type="hidden" id="oldname" value="<%=group_name %>">
  	</td>
  </tr>
  <tr>
    <td align="center" class="RecordRow" nowrap style="height: 35px;">
    	<button type="button" class="mybutton" onclick="updgroup();"><bean:message key="button.ok"/></button>&nbsp;&nbsp;&nbsp;
    	<button type="button" class="mybutton" onclick="top.close();"><bean:message key="lable.welcomeboard.close"/></button>
    </td>
  </tr>
</table>
</td>
</tr>
</table>
</html:form>

<script>
    function updgroup(){
    	var oldname=document.getElementById("oldname").value;
    	var nname=document.getElementById("newname");
    	if(nname.value==""){
    		alert("用户组名称不能为空！");
    		nname.focus();
    		return;
    	}
    	var ctrlvalue="`%'$#@!~^&*()_+\"'";
    	var newname = nname.value;
    	if(checkIsIntNum(newname.substring(0,1))==true||ctrlvalue.indexOf(newname.substring(0,1))!=-1||newname.indexOf("'")!=-1||newname.indexOf("(")!=-1||newname.indexOf(")")!=-1||newname.indexOf("（")!=-1||newname.indexOf("）")!=-1||newname.indexOf(".")!=-1||newname.indexOf("\\")!=-1)
      	{
      		alert('<bean:message key="error.user.number"/>');
      		return;
      	}
      	var hashvo = new Object();
 	    hashvo.oldname = oldname;
 	    hashvo.newname = newname;	
 	    
 	   if(window.showModalDialog){
			top.returnValue=hashvo;
		   	top.close();
		}else{
			top.close();
			top.opener.update_group_success(hashvo);
		}
 	    //top.returnValue = hashvo;//兼容浏览器选择top
 	    //top.close();
    }
    //正则表达式判断value是否为数字
    function checkIsIntNum(value){
		return /^[0-9]*[1-9][0-9]*$/.test(value);
	}
</script>