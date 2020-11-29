<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@page import="com.hrms.struts.taglib.CommonData" %>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<hrms:themes></hrms:themes>
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
	 			if (check.checked == "true") {
	 				hidden.value = checkedValue;
	 			} else {
	 				hidden.value = checkValue;
	 			}
 			} else {
 				if (check.checked == "true") {
	 					hidden.value = hidden.value + checkedValue + ",";
	 			} else {
	 				hidden.value = hidden.value.replace(checkedValue + ",","");
	 			}
 			}
 		
 		}
 	}
 	
 	// 保存
 	function save() {
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
 		obj.srcTabCond = document.getElementById("srcTabCond").innerHTML;
 		// eHR数据保护条件
 		obj.tagTabCond = document.getElementById("tagTabCond").innerHTML;
 		// 映射关系
 		obj.mapping = "";
 		var hr_fields = document.getElementsByName("hr_fields");
 		var item = "";
 		for (i = 0; i < hr_fields.length; i++) {
 			field = hr_fields[i].id.replace("hr_", "");
 			extValue = document.getElementById("ext_" + field).value;
 			defValue = document.getElementById("def_" + field).value; 
 			pkObj = document.getElementById("pk_" + field);
 			pkValue = "";
 			if (pkObj.checked == true) {
 				pkValue = "1";
 			} else {
 				pkValue = "0";
 			}
 			
 			item = hr_fields[i].value;
 			if(-1 < item.indexOf('('))
 		        item = item.substr(0,item.indexOf('('));
 			obj.mapping += item + ":" + extValue + ":" + defValue +":" + pkValue ;
 			
 			if (i != hr_fields.length - 1) {
 				obj.mapping += ",";
 			}
 		}	
 		if(parent.parent.Ext && parent.parent.Ext.getCmp('fieldmapping')){
 			var win = parent.parent.Ext.getCmp('fieldmapping');
 			win.return_vo = obj;
 		}else{
	 		window.returnValue=obj;
 		}
     	//window.close();
     	winclose();
 	}
 		
 	function winclose(){
 		if(parent.parent.Ext && parent.parent.Ext.getCmp('fieldmapping')){
 			parent.parent.Ext.getCmp('fieldmapping').close();
 			return;
 		}
 		window.close();
 	}
//-->
</script>
<html:form action="/sys/import/dataimport" target="_self">
	<table width="640" cellspacing="0" align="center" cellpadding="0" class="RecordRow">
		<tr><td>
		<table cellspacing="0" align="center" cellpadding="0" style="margin-top:5px;">
			<tr>
				<td width="20%" align="right"><bean:message key="system.import.dataimport.ehrtable" /></td>
				<td width="30%" align="left"><input type="text" class="text4" name="ehrTable" id="ehrTable" size="30" onchange="searchField(this)" style="margin-left:5px;width:230px;"/></td>
				<td width="20%" align="right"><bean:message key="system.import.dataimport.othertable" /></td>
				<td width="30%" align="left"><input type="text" class="text4" name="extTable" id="extTable" size="30" style="margin-left:5px;width:230px;"/></td>
			</tr>
			<tr>
				<td width="20%" align="right"><bean:message key="system.import.dataimport.ehrmappingfield" /></td>
				<td width="30%" align="left">
					<html:select name="dataImportForm" property="fieldName" size="1" style="width:230px;margin-left:5px;" styleId="hrRelation">
					   <html:optionsCollection property="fieldList" value="dataValue" label="dataName" />
					</html:select>
				</td>
				<td width="20%" align="right"><bean:message key="system.import.dataimport.extmappingfield" /></td>
				<td width="30%" align="left"><input type="text" class="text4" name="extRelation" id="extRelation" size="30" style="margin-left:5px;width:230px;"/></td>
			</tr>
			<tr>
				<td width="20%" align="right"><bean:message key="system.import.dataimport.ehrcondition" /></td>
				<td width="30%" align="left"><textarea name="tagTabCond" id="tagTabCond" rows="4" cols="30" style="margin-left:5px;width:230px;"></textarea></td>
				<td width="20%" align="right"><bean:message key="system.import.dataimport.othercondition" /></td>
				<td width="30%" align="left"><textarea name="srcTabCond" id="srcTabCond" rows="4" cols="30" style="margin-left:5px;width:230px;"></textarea></td>
			</tr>
		</table>
		</td></tr>
		<tr><td>
		  <table width="630" border="0" cellspacing="0" align="center" cellpadding="0" class="ListTable" style="margin-top:5px;margin-bottom:5px;">		

			<tr>
				<td colspan="4" class="RecordRow">
					<div style="width:100%;height:200px;border:0px;vertical-align: top;" id="tableField">
					</div>		
				</td>
			</tr>
		
	</table>
	</td></tr></table>
	
	<table width="100%" align="center">
		<tr>
			<td align="center" height="35">
				<input type="button" name="b_save" value="<bean:message key="button.ok"/>" class="mybutton" onclick="save()"/>	
				<input type="button" name="b_return" value="<bean:message key="button.cancel" />" class="mybutton" onclick="winclose();"/>
				
			</td>
		</tr>
	</table>
<script type="text/javascript">
<!--
    function getMapTabHeader()
    {
        var mapHeader = "<tr class='trShallow'>";
        mapHeader += "<td align='center' class='TableRow' width='30%' nowrap height='30'>";
        mapHeader += "&nbsp;<bean:message key="system.import.dataimport.ehrfield"/>&nbsp;";        
        mapHeader += "</td>";
        mapHeader += "<td align='center' class='TableRow' width='30%' nowrap>";
        mapHeader += "&nbsp;<bean:message key="system.import.dataimport.otherfield"/>&nbsp;";
        mapHeader += "</td>";
        mapHeader += "<td align='center' class='TableRow' width='25%' nowrap height='30'>";
        mapHeader += "&nbsp;<bean:message key="system.import.dataimport.default"/>&nbsp;";     
        mapHeader += "</td>";
        mapHeader += "<td align='center' class='TableRow' width='15%' nowrap>";
        mapHeader += "&nbsp;<bean:message key="system.import.dataimport.ehrkey"/>&nbsp;";
        mapHeader += "</td>";
        mapHeader += "</tr>";

        return mapHeader;
    }

    var initMapping = "";
      
	function showMapping(outparamters)
	{
	    var fileStr = outparamters.getValue("fieldStr");
	    var itemdescStr = outparamters.getValue("itemdescStr");
	    var divObj = document.getElementById("tableField");
	    var strs = fileStr.split(",");
	    var descs = itemdescStr.split(",");
	    var item = "";
	
	    if (null == initMapping || undefined==initMapping) 
	        initMapping="";
	    
	    var initmap = initMapping.split(",");
	    var isKey = 0;
	    var extItem = "";
	    var defValue = "";
	    
	    var divObj = document.getElementById("tableField");
	    var innerStr = "<table width='100%' border='0' cellspacing='0' align='center' cellpadding='0' class='ListTable' id=''>";
	    var mapHeader = getMapTabHeader();
	    innerStr = innerStr + mapHeader;
	    for (i = 0; i < strs.length; i++) 
	    {
	        isKey = 0;
	        extItem = "";
	        defValue = "";
	        for(j = 0; j < initmap.length; j++)
	        {
	            initstrs = initmap[j].split(":");
	            
	            if(strs[i].toLowerCase()==initstrs[0].toLowerCase())
	            {
	                extItem = initstrs[1];
	                defValue = initstrs[2];
	                isKey = initstrs[3];
	                break;
	            }
	        }            
	        
	        item = strs[i];
	        if(descs[i] != "" && descs[i]!=item)
	            item = item + "(" + descs[i] + ")";
	        
	        innerStr += "<tr>";
	        innerStr += "<td class='RecordRow' width='30%' style='border-left: 0px;border-top: 0px;'>&nbsp;<input type='text' name='hr_fields' id='hr_"+strs[i] +"' size='25' value='"+item+"' style=' border:0px;border-bottom: 0pt solid #60A2BD;' readOnly='true'/></td>";
	        innerStr += "<td class='RecordRow' width='30%' style='border-top: 0px;border-left: 0px;'>&nbsp;<input type='text' name='ext_fields' id='ext_" + strs[i]+"' size='25' value='"+extItem+"' style=' border:0px;border-bottom: 1pt solid #60A2BD;'/></td>";
	        innerStr += "<td class='RecordRow' width='25%' style='border-top: 0px;border-left: 0px;border-right: 0px;'>&nbsp;<input type='text' name='def_values' id='def_"+strs[i]+"' value='"+defValue+"' size='15' style=' border:0px;border-bottom: 1pt solid #60A2BD;'/></td>";
	        innerStr += "<td class='RecordRow' width='15%' style='border-top: 0px;border-right: 0px;' align='center'>&nbsp;<input type='checkbox' name='pk_check' id='pk_"+strs[i]+"' ";
	        if (isKey == 1) 
	            innerStr += " checked='checked' ";
	        
	        innerStr += " value='1'/></td>";                                
	        innerStr += "</tr>";
	    }
	    innerStr += "</table>";
	    
	    divObj.innerHTML=innerStr;
	
	}
	// 映射关系
 	function init() {
 		var obj;
 		if(parent.parent.Ext && parent.parent.Ext.getCmp('fieldmapping')){
 			var win = parent.parent.Ext.getCmp('fieldmapping');
 			obj = win.dialogArguments;
 		}else{
 			obj = window.dialogArguments;
 		}
 		
 		// eHR表名
 		document.getElementById("ehrTable").value = obj.ehrTable;
 		// 外部表名
 		document.getElementById("extTable").value = obj.extTable;
 		// 主集指标
 		document.getElementById("hrRelation").value = obj.hrRelation;
 		// 外部指标
 		document.getElementById("extRelation").value = obj.extRelation;
 		// 外部数据过滤条件
 		document.getElementById("srcTabCond").innerHTML = obj.srcTabCond;
 		// eHR数据保护条件
 		document.getElementById("tagTabCond").innerHTML = obj.tagTabCond;
 		
 		if (null == obj.ehrTable || undefined == obj.ehrTable || "" == obj.ehrTable)
 			return;
 		
 		initMapping = obj.mapping;
 		//取eHR表字段列表信息
 	     var hashvo=new ParameterSet();
         hashvo.setValue("tableName",obj.ehrTable);
         hashvo.setValue("opt","ajax");
         var request=new Request({method:'post',asynchronous:false,onSuccess:showMapping,functionId:'1010100137'},hashvo);         
 		
 	}
  	
 	// 下载模板文件和sql条件
	function searchField(obj) {
		var hashvo=new ParameterSet();
	    hashvo.setValue("tableName",obj.value);
	    hashvo.setValue("opt","ajax");
	    var request=new Request({method:'post',asynchronous:false,onSuccess:showdownloadfile,functionId:'1010100137'},hashvo);
	}
	
	function showdownloadfile(outparamters) {
		var fileStr = outparamters.getValue("fieldStr");
		var itemdescStr = outparamters.getValue("itemdescStr");
		var divObj = document.getElementById("tableField");
		var strs = fileStr.split(",");
		var descs = itemdescStr.split(",");
		var item = "";
		
		var innerStr = "<table width='100%' border='0' cellspacing='0' align='center' cellpadding='0'>";
		var mapHeader = getMapTabHeader();
        innerStr = innerStr + mapHeader;
		for (i = 0; i < strs.length; i++) {	
			
			item = strs[i];
			if(descs[i] != "" && descs[i]!=item)
				item = item + "(" + descs[i] + ")";
			innerStr += "<tr>";
			innerStr += "<td class='RecordRow' width='30%' style='border-left: 0px;border-top: 0px;'>&nbsp;<input type='text' name='hr_fields' id='hr_"+strs[i] +"' size='25' value='"+item+"' style=' border:0px;border-bottom: 0pt solid #60A2BD;' readOnly='true'/></td>";
			innerStr += "<td class='RecordRow' width='30%' style='border-top: 0px;border-left: 0px;'>&nbsp;<input type='text' name='ext_fields' id='ext_" + strs[i]+"' size='25' style=' border:0px;border-bottom: 1pt solid #60A2BD;'/></td>";
			innerStr += "<td class='RecordRow' width='25%' style='border-top: 0px;border-left: 0px;border-right: 0px;'>&nbsp;<input type='text' name='def_values' id='def_"+strs[i]+"' size='20' style=' border:0px;border-bottom: 1pt solid #60A2BD;'/></td>";
			innerStr += "<td class='RecordRow' width='15%' style='border-top: 0px;border-right: 0px;' align='center'>&nbsp;<input type='checkbox' name='pk_check' id='pk_"+strs[i]+"' value='1'/></td>";								
			innerStr += "</tr>";
		}
		innerStr += "</table>";
	
		divObj.innerHTML=innerStr;
	}
	
	   init();
	   
	if(!getBrowseVersion() || getBrowseVersion() == 10){//非ie浏览器样式修改  wangb 20190320
		var ListTable = document.getElementsByClassName('ListTable')[0];
		ListTable.setAttribute('width','700');
		var RecordRow = document.getElementsByClassName('RecordRow')[0];
		RecordRow.setAttribute('width','710');
		var tableField = document.getElementById('tableField');
		tableField.style.overflowY='auto';
	}
//-->
</script>
</html:form>

