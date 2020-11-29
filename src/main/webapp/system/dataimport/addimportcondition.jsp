<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@page import="com.hrms.struts.taglib.CommonData" %>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<hrms:themes></hrms:themes>
<script language="javascript" src="/module/utils/js/template.js"></script>
<script language="javascript">
	function validateValue(textbox) { 
		var   IllegalString ="\`~!#$%^&*()+{}|\\:\"<>?-=/,;\；，、？”“‘’。：；《》·@%'"; 
        var   textboxvalue=   textbox.value; 
		var   index= textboxvalue.length-1;
		var j = 0;
		for (i = 0; i < textboxvalue.length; i++)  {
	        var   s   =   textbox.value.charAt(i); 
	        if(IllegalString.indexOf(s)>=0) { 
	        	s   =   textboxvalue.substring(0,i); 
	            textbox.value   =   s;
	        	j = 1;
	        }
        }
        if (j == 1) {
        	return false;
        } else {
        	return true;
        } 
    }
 
 
 	// 点击checkbox时，将值给hidden;isSingle是否为一个checkbox,1为是，0为否
 	function checkToHidden(check, hiddenId, checkedValue,checkValue, isSingle) {
 		var hidden = document.getElementById(hiddenId);
 		if (hidden) {
 			if (isSingle == 1) {
	 			if (document.getElementById(check).checked == true) {
	 				hidden.value = checkedValue;
	 			} else {
	 				hidden.value = checkValue;
	 			}
 			} else {
 				if (document.getElementById(check).checked == true) {
	 					hidden.value = hidden.value + checkedValue + ",";
	 			} else {
	 				hidden.value = hidden.value.replace(checkedValue + ",", "");
	 			}
 			}
 		
 		}
 	}
 	
 	// 返回
 	function breturn() {
 		dataImportForm.action="/sys/import/dataimport.do?b_query=return";
 		dataImportForm.submit();
 	}
 	
 	// 保存
 	function save() {
 		if (document.getElementById("passwordc").value != document.getElementById("password").value){
 			alert("密码不一致！");			
 			return ;
 		}
 		
 		dataImportForm.action="/sys/import/dataimport.do?b_save=link";
 		dataImportForm.submit();
 	}
 	
//-->
</script>
<html:form action="/sys/import/dataimport" target="_self">
		<html:hidden name="dataImportForm" property="id"/>
	  <table width="60%" border="0" cellspacing="0" align="center" cellpadding="0" class="ListTable">
		<tr align="center" nowrap class="trShallow" >
			<td align="left" nowrap class="TableRow" colspan="2">
				<bean:message key="system.import.dataimport" />				
				
			</td>
		</tr>		
		<tr class="trShallow">
			<td align="right" class="RecordRow" width="20%" nowrap height="30">
            	&nbsp;<bean:message key="system.import.dataimport.name" />&nbsp;	    
			</td>
			<td align="left" class="RecordRow" width="80%" nowrap>
				&nbsp;<html:text name="dataImportForm" property="name" maxlength="50" size="70" styleClass="text4" style="width:450px;"></html:text>
			</td>
		</tr>
		<tr class="trDeep" height="30">
			<td align="right" class="RecordRow" width="20%" nowrap>
            	&nbsp;<bean:message key="system.import.dataimport.nbase" />&nbsp;    
			</td>
			<td align="left" class="RecordRow" width="80%" nowrap>
				<bean:define id="nbase" name="dataImportForm" property="nbase"></bean:define>
				<logic:iterate id="el" name="dataImportForm" property="nbaseList">	
					<%	CommonData data = (CommonData) el;
						if (nbase.toString().contains(data.getDataValue() + ",")) {%>				
						<div style="width: 100px;float: left;"><input type="checkbox" checked="checked" id="<bean:write name="el" property="dataValue"/>" name="nbasecheck" value="<bean:write name="el" property="dataValue"/>" onclick="checkToHidden('<bean:write name="el" property="dataValue"/>', 'nbase', '<bean:write name="el" property="dataValue"/>','', 0)"/><label for="<bean:write name="el" property="dataValue"/>"><bean:write name="el" property="dataName"/></label></div>
					<%} else {%>
						<div style="width: 100px;float: left;"><input type="checkbox" id="<bean:write name="el" property="dataValue"/>" name="nbasecheck" value="<bean:write name="el" property="dataValue"/>" onclick="checkToHidden('<bean:write name="el" property="dataValue"/>', 'nbase', '<bean:write name="el" property="dataValue"/>','', 0)"/><label for="<bean:write name="el" property="dataValue"/>"><bean:write name="el" property="dataName"/></label></div>
					<%} %>
				</logic:iterate>
				<html:hidden name="dataImportForm" property="nbase" styleId="nbase"/>
			</td>
		</tr>
		<tr class="trDeep">
			<td align="right" class="RecordRow" width="20%" nowrap>
            	&nbsp;<bean:message key="system.import.dataimport.dbtype" />&nbsp;    
			</td>
			<td align="left" class="RecordRow" width="80%" nowrap>
				&nbsp;<html:select name="dataImportForm" property="dbType" size="1" style="width:450px;" onchange="">
					<html:optionsCollection property="dbTypeList" value="dataValue" label="dataName" />
				</html:select>
			</td>
		</tr>
		<tr class="trShallow">
			<td align="right" class="RecordRow" width="20%" nowrap>
            	&nbsp;<bean:message key="system.import.dataimport.dburl" />&nbsp;	    
			</td>
			<td align="left" class="RecordRow" width="80%" nowrap>
				&nbsp;<html:text name="dataImportForm" property="dbUrl" maxlength="100" size="70" styleClass="text4" style="width:450px;"></html:text>
			</td>
		</tr>
		<tr class="trShallow">
			<td align="right" class="RecordRow" width="20%" nowrap>
            	&nbsp;<bean:message key="system.import.dataimport.username" />&nbsp;	    
			</td>
			<td align="left" class="RecordRow" width="80%" nowrap>
				&nbsp;<html:text name="dataImportForm" property="userName" maxlength="50" size="30" styleClass="text4" style="width:450px;"></html:text>
			</td>
		</tr>
		<tr class="trShallow">
			<td align="right" class="RecordRow" width="20%" nowrap>
            	&nbsp;<bean:message key="system.import.dataimport.password" />&nbsp;	    
			</td>
			<td align="left" class="RecordRow" width="80%" nowrap>
				&nbsp;<html:password name="dataImportForm" property="password" maxlength="50" size="33" styleClass="text4" styleId="password" style="width:450px;"></html:password>
				
			</td>
		</tr>
		<tr class="trShallow">
			<td align="right" class="RecordRow" width="20%" nowrap>
            	&nbsp;<bean:message key="system.import.dataimport.passwordc" />&nbsp;	    
			</td>
			<td align="left" class="RecordRow" width="80%" nowrap>
				
				&nbsp;<input type="password" name="passwordc" maxlength="50" size="33" id="passwordc" class="text4" value="<bean:write name="dataImportForm" property="password"/>" style="width:450px;"/>
			</td>
		</tr>
		<tr class="trShallow">
			<td align="right" class="RecordRow" width="20%" nowrap>
            	&nbsp;<bean:message key="system.import.dataimport.mapping" />&nbsp;	    
			</td>
			<td align="left" class="RecordRow" width="80%" nowrap>
				&nbsp;<html:text name="dataImportForm" property="mapping" maxlength="50" size="70" readonly="true" styleClass="text4" styleId="mapping" style="width:450px;"></html:text>
				<img id="img" align="absmiddle" src="/images/code.gif" onclick='fieldmapping()' style="cursor: pointer;"/>
				
			</td>
		</tr>
		<tr class="trShallow">
			<td align="right" class="RecordRow" width="20%" nowrap>
            	&nbsp;<bean:message key="system.import.dataimport.jobclass" />&nbsp;	    
			</td>
			<td align="left" class="RecordRow" width="80%" nowrap>
				&nbsp;<html:text name="dataImportForm" property="jobClass" maxlength="50" size="30" styleClass="text4" style="width:450px;"></html:text>
			</td>
		</tr>
		<tr class="trShallow">
			<td align="right" class="RecordRow" width="20%" nowrap>
            	&nbsp;<bean:message key="system.import.dataimport.enable"/>&nbsp;	    
			</td>
			<td align="left" class="RecordRow" width="80%" nowrap>
				<logic:equal name="dataImportForm" property="enable" value="1">
					<input id="flag" name="flag" type="checkbox" checked="checked" onclick="checkToHidden('flag', 'enable', '1','0', 1)"/>
				</logic:equal>
				<logic:notEqual name="dataImportForm" property="enable" value="1">
					<input id="flag"  name="flag" type="checkbox" onclick="checkToHidden('flag', 'enable', '1','0', 1)"/>
				</logic:notEqual>
				<html:hidden name="dataImportForm" property="enable" styleId="enable"/>
			</td>
		</tr>
	</table>
	<html:hidden name="dataImportForm" property="ehrTable" styleId="ehrTable"/>
	<html:hidden name="dataImportForm" property="extTable" styleId="extTable"/>
	<html:hidden name="dataImportForm" property="hrRelation" styleId="hrRelation"/>
	<html:hidden name="dataImportForm" property="extRelation" styleId="extRelation"/>
	<html:hidden name="dataImportForm" property="srcTabCond" styleId="srcTabCond"/>
	<html:hidden name="dataImportForm" property="tagTabCond" styleId="tagTabCond"/>
	<table width="85%" align="center">
		<tr>
			<td align="center" height="35px;">
				<input type="button" name="b_save" value="<bean:message key="lable.menu.main.save"/>" class="mybutton" onclick="save()"/>	
				<input type="button" name="b_return" value="<bean:message key="button.return" />" class="mybutton" onclick="breturn()"/>
				
			</td>
		</tr>
	</table>
	
<script type="text/javascript">
<!--
	// 映射关系
 	function fieldmapping() {
 		var obj = new Object();
 		// eHR表名
 		obj.ehrTable = document.getElementById("ehrTable").value;
 		// 外部表名
 		obj.extTable = document.getElementById("extTable").value;
 		// 主集指标
 		obj.hrRelation = document.getElementById("hrRelation").value;
 		// 外部指标
 		obj.extRelation = document.getElementById("extRelation").value;
 		// 外部数据过滤条件
 		obj.srcTabCond = document.getElementById("srcTabCond").value;
 		// eHR数据保护条件
 		obj.tagTabCond = document.getElementById("tagTabCond").value;
 		// 映射关系
 		obj.mapping = document.getElementById("mapping").value;	
 		
 		
 		var action = "/sys/import/dataimport.do?b_showdialog=link`opt=querry";
 		var thecodeurl="/general/query/common/iframe_query.jsp?src="+$URL.encode(action);
 		var dw=650,dh=450,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
 		/*
       	var  return_vo= window.showModalDialog(thecodeurl, obj, 
              "dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:"+dw+"px; dialogHeight:"+dh+"px;resizable:no;center:yes;scroll:yes;status:no");
        // window.open(thecodeurl);
         if (return_vo) {     
	       	// eHR表名
	 		document.getElementById("ehrTable").value = return_vo.ehrTable;
	 		// 外部表名
	 		document.getElementById("extTable").value = return_vo.extTable;
	 		// 主集指标
	 		document.getElementById("hrRelation").value = return_vo.hrRelation;
	 		// 外部指标
	 		document.getElementById("extRelation").value = return_vo.extRelation;
	 		// 外部数据过滤条件
	 		document.getElementById("srcTabCond").value = return_vo.srcTabCond;
	 		// eHR数据保护条件
	 		document.getElementById("tagTabCond").value = return_vo.tagTabCond;
	 		// 映射关系
	 		document.getElementById("mapping").value = return_vo.mapping; 	
 		}
 		*/
 		//改用ext 弹窗显示  wangb 20190318
 		var win = Ext.create('Ext.window.Window',{
			id:'fieldmapping',
			title:'映射关系',
			width:dw+120,
			height:dh+40,
			resizable:'no',
			modal:true,
			autoScoll:false,
			autoShow:true,
			autoDestroy:true,
			html:'<iframe style="background-color:#fff" frameborder="0" SCROLLING=NO height="100%" width="100%" src="'+thecodeurl+'"></iframe>',
			renderTo:Ext.getBody(),
			listeners:{
				'close':function(){
					if (this.return_vo) {     
	       				// eHR表名
	 					document.getElementById("ehrTable").value = this.return_vo.ehrTable;
	 					// 外表名
	 					document.getElementById("extTable").value = this.return_vo.extTable;
	 					// 主集指标
	 					document.getElementById("hrRelation").value = this.return_vo.hrRelation;
	 					// 外部指标
	 					document.getElementById("extRelation").value = this.return_vo.extRelation;
	 					// 外部数据过滤条件
	 					document.getElementById("srcTabCond").value = this.return_vo.srcTabCond;
	 					// eHR数据保护条件
	 					document.getElementById("tagTabCond").value = this.return_vo.tagTabCond;
	 					// 映射关系
	 					document.getElementById("mapping").value = this.return_vo.mapping; 	
 					}
				}
			}
		 }); 
		 win.dialogArguments = obj;
 	}
//-->
</script>
</html:form>

