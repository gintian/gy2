<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="com.hrms.frame.codec.SafeCode"%>
<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="javascript"><!--
	// 过滤非法字符
	function handlKeyDown(event) {
		if (event.shiftKey) {
			if (event.keyCode != 37 && event.keyCode != 38
				&& event.keyCode != 39 && event.keyCode != 40) {
				event.returnValue = false;
			}
		} else if (event.keyCode != 48 && event.keyCode != 49
		&& event.keyCode != 50 && event.keyCode != 51 
		&& event.keyCode != 52 && event.keyCode != 53 
		&& event.keyCode != 54 && event.keyCode != 55
		&& event.keyCode != 56 && event.keyCode != 57
		&& event.keyCode != 8 && event.keyCode != 46
		&& event.keyCode != 37 && event.keyCode != 38
		&& event.keyCode != 39 && event.keyCode != 40
		&& event.keyCode != 96 && event.keyCode != 97
		&& event.keyCode != 98 && event.keyCode != 99
		&& event.keyCode != 100 && event.keyCode != 101
		&& event.keyCode != 101 && event.keyCode != 102
		&& event.keyCode != 103 && event.keyCode != 104
		&& event.keyCode != 105) {
			event.returnValue = false;
		}
	}
	
	// 保存设置
	function save() {
		courseForm.action="/train/resource/course.do?b_setparam=link&opt=save";
		courseForm.submit();
	}
--></script>
<html>
	<head></head>
<style>
.textColorRead{
	width: 150px;
}
</style>
<body onload="execu()">
<html:form action="/train/resource/course">
<center>
<table width="390px" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTableF common_border_color" style="border:1px solid;">
	<tr>
		<td width="100%" align="left" class="TableRow">
			<!-- 参数设置标题 -->
          	&nbsp;&nbsp;<bean:message key='train.resource.course.setparam'/>
       	</td>
	</tr>
	<tr>
		<td width="85%" height="100" align="right">
			<fieldset align="center" style="width:90%;">
				<legend ><bean:message key='train.resource.course.setparam.diycourse'/></legend>
             	<table width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">                   	
		 			<tr>
		  				<td width="100%" height="60" align="left" nowrap style="padding-left:40px;">
			  				<bean:message key='train.resource.course.setparam.diycoursetype'/>
			  				<html:text name="courseForm" property="diyTypeName" styleClass="textColorRead" readonly="true"></html:text>
			  				<img align=absMiddle src="/images/code.gif" onclick="javascript:openInputCodeDialogText('55_1','diyTypeName','diyType');" />
			  				<bean:define id="diyType" name="courseForm" property="diyType"/>
			  				<html:hidden name="courseForm" property="diyType" styleId="diyTypeid"/>
		  				</td>
		 			</tr>
        		</table>
            </fieldset> 	                    	            
         </td>
	</tr>
	<tr>
		<td width="85%" height="100" align="right">
            <fieldset align="center" style="width:90%;">
				<legend ><bean:message key='train.resource.course.setparam.hotcourse'/></legend>
             	<table width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">                   	
		 			<tr>
		  				<td width="100%" height="60" align="left" nowrap style="padding-left:40px;">
		  				<bean:message key='train.resource.course.setparam.courseplay.qian'/>
		  				<html:text styleClass="TEXT4" name="courseForm" property="hotCount" size="5" onkeydown="handlKeyDown(event)" styleId="hotCount"></html:text>
		  				<bean:message key='train.resource.course.setparam.courseplay.hou'/>
		  				</td>
		 			</tr>
        		</table>
            </fieldset> 	                    	            
         </td>
	</tr>
</table> 
<table width="100%" border="0" cellpadding="0" cellspacing="0">	
    <tr>
		<td align="center" style="height:35px;">
			<input type="button" name="btnreturn" value='<bean:message key="button.ok"/>' class="mybutton" onclick="save()" />
    		<input type="button" name="btnreturn" value='<bean:message key="button.cancel"/>' class="mybutton" onclick="window.close();"/>
		</td>                              
	</tr>
</table>   
 
</center>
</html:form>
</body>
</html>
<script language="javascript"><!--
	function execu() {
		<logic:equal name="courseForm" property="saveStatus" value="1">
			alert(SAVESUCCESS);
		</logic:equal>
		<logic:equal name="courseForm" property="saveStatus" value="2">
			alert(SAVEFAILED);
		</logic:equal>
	}
	
--></script>
