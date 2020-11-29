<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<script type="text/javascript" src="../../../js/constant.js"></script>
<script type="text/javascript" src="../../../module/utils/js/template.js"></script>
<script type="text/javascript">
var whereStr = "";
//关闭弹窗方法  wangb 20180207
function winClose(){
    if(getBrowseVersion()){//ie浏览器关闭
        parent.window.close();
    }else{//非ie浏览器关闭
    	top.close();
    }
}

function setFieldParam() {
	var fieldSetId = document.getElementById("fieldSetId").value;
	if(!fieldSetId || "#" == fieldSetId){
		alert(SELECT_SUBSET_NOTNULL);
		return;
	}
	
		var thecodeurl ="/train/traincourse/generalsearch.do?b_query=link&empPriv=1&fieldsetid=" + fieldSetId; 
	if(getBrowseVersion()) {
	    var return_vo= window.showModalDialog(thecodeurl, "", 
	        "dialogWidth:700px; dialogHeight:410px;resizable:no;center:yes;scroll:no;status:no;");
	    if(return_vo!=null)
	        whereStr = return_vo;
	} else {
		var dw=400,dh=300,dl=(screen.width-dw)/2;
		var dt=(screen.height-dh)/2;
	    thecodeurl = thecodeurl + "&muduleFlag=batchDelete";
	    window.open(thecodeurl,'_blank','width=700,height=450,toolbar=no,location=yes,resizable=no,top='+dt+',left='+dl);
	}
    
}

function returnValue(where){
	whereStr = where;
}

function change() {
	var subsetDataType;
	var subsetData = document.getElementsByName("subsetData");
    if(subsetData) {
        for(var i = 0; i < subsetData.length; i++){
            if(subsetData[i].checked)
                subsetDataType = subsetData[i].value;
        }
    }
    
    if("2" == subsetDataType)
    	document.getElementById("buttonId").style.display="";
    else
    	document.getElementById("buttonId").style.display="none";
    
}

function saveSelect() {
	var fieldSetId = document.getElementById("fieldSetId").value;
    if(!fieldSetId || "#" == fieldSetId){
        alert(SELECT_SUBSET_NOTNULL);
        return;
    }
    
	var dataRangeType,subsetDataType;
	var dataRange = document.getElementsByName("dataRange");
	if(dataRange) {
		for(var i = 0; i < dataRange.length; i++){
			if(dataRange[i].checked)
				dataRangeType = dataRange[i].value;
		}
	}
	
	var subsetData = document.getElementsByName("subsetData");
	if(subsetData) {
		for(var i = 0; i < subsetData.length; i++){
			if(subsetData[i].checked)
				subsetDataType = subsetData[i].value;
		}
	}
		
	if("2" == subsetDataType) {
		if(!whereStr) {
			alert(SET_SUBSETDATA_WHERE);
			return;
		}
	}
	
	var hashVo=new ParameterSet();
    hashVo.setValue("dbname",'${selfInfoForm.userbase}');
    hashVo.setValue("selectId",document.getElementById("strid").value);
    hashVo.setValue("setname",fieldSetId);
    hashVo.setValue("whereStr",whereStr);
    hashVo.setValue("subsetDataType",subsetDataType);
    hashVo.setValue("dataRangeType",dataRangeType);
    hashVo.setValue("type","count");
    var request=new Request({method:'post',asynchronous:true,
    	onSuccess:function (outparameters){
    		var success = outparameters.getValue("success");
    		if("true" == success) {
	    		var count = outparameters.getValue("count");
	            if(!confirm(BATH_DELETE_SUBSETDATA1 + count + BATH_DELETE_SUBSETDATA2)){
	                    return;
	            }
	            
	            var hashVo=new ParameterSet();
	            hashVo.setValue("dbname",'${selfInfoForm.userbase}');
	            hashVo.setValue("selectId",document.getElementById("strid").value);
	            hashVo.setValue("setname",fieldSetId);
	            hashVo.setValue("whereStr",whereStr);
	            hashVo.setValue("subsetDataType",subsetDataType);
	            hashVo.setValue("dataRangeType",dataRangeType);
			    hashVo.setValue("type","delete");
	            var request=new Request({method:'post',asynchronous:true,
	                onSuccess:function (outparameters){
	                	var success = outparameters.getValue("success");
	                    if("true" == success) {
	                    	if(getBrowseVersion()) {
	                    		window.returnValue="true";
	                    	    window.close();
	                    	} else {
	                    		parent.opener.refreshPage("true");
	                    		top.close();
	                    	}
	                    } else {
	                        alert(success);
	                    }
	                },functionId:'0201001019'},hashVo);
    		} else {
    			alert(success);
    		}
    	},functionId:'0201001019'},hashVo);    
}

function selectSubset() {
	whereStr = "";
}
</script>
<html:hidden name="selfInfoForm" property="strId" styleId="strid"/> 
<body style="margin-right: 10px;">
	<table width='435px' border="0" cellspacing="0"	style="margin-right: 5px;" align="center" cellpadding="0" class="ListTable">
		<thead>
			<tr>
				<td align="left" class="TableRow" colspan="2" nowrap>
				批量删除子集记录设置
				</td>
			</tr>
		</thead>
		<tr>
			<td class="RecordRow notop nobottom noright" style="padding: 5px;" align="right" width="100px" nowrap>
			 选择子集
			</td>
			<td class="RecordRow notop nobottom noleft" style="padding: 5px;" align="left" width="100%" nowrap>
			     <hrms:optioncollection name="selfInfoForm" property="fieldSetDataList" collection="list" />
			     <html:select style="min-width:50px;" onchange="selectSubset();" styleId="fieldSetId" name="selfInfoForm" property="fieldsetid" size="1">
                      <html:options collection="list" property="dataValue" labelProperty="dataName"/>
                 </html:select>
			</td>
		</tr>
		<tr>
			<td class="RecordRow notop nobottom noright" style="padding: 5px;" align="right" width="100px">
			 数据范围
			</td>
			<td class="RecordRow notop nobottom noleft" style="padding: 5px;" align="left">
				<input type="radio" name="dataRange" value="1" checked="checked"/>查询结果
				<input type="radio" name="dataRange" value="2"/>所选记录
				<input type="radio" name="dataRange" value="3"/>全部记录（权限范围下）
			</td>
		</tr>
		<tr>
			<td class="RecordRow notop nobottom noright" style="padding-top: 5px;" valign="top" align="right" width="100px">
			子集记录
			</td>
			<td class="RecordRow notop nobottom noleft" style="padding: 5px;" align="left">
				<input type="radio" name="subsetData" value="1" checked="checked" onclick="change();"/>删除当前子集记录<br>
				<input type="radio" style="margin-top: 5px;" name="subsetData" onclick="change();" value="2"/>删除部分子集记录&nbsp;
				<input type="button" id="buttonId" style="display: none;" class="mybutton"  value="..." onclick="setFieldParam();"><br>
				<input type="radio" style="margin-top: 5px;" name="subsetData" onclick="change();" value="3"/>删除所有子集记录
			</td>
		</tr>
		<tr style="height: 35px;">
			<td valign="middle" align="center" colspan="2" style="padding-top: 5px; padding-bottom: 3px; border: none;" nowrap>
				<input type="button" class="mybutton" onclick="saveSelect()" 
				     value="<bean:message key="reporttypelist.confirm" />" />
				<input type="button" class="mybutton" onclick="winClose()" 
				     value="<bean:message key="button.close" />" />
			
            </td>
		</tr>
	</table>
</body>
</html>