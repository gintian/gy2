<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="java.util.*,
				 com.hjsj.hrms.actionform.performance.options.ConfigParameterForm,
				 com.hrms.struts.constant.WebConstant" %>
<%@ page import="com.hrms.hjsj.sys.EncryptLockClient" %>

<%
	EncryptLockClient lockclient=(EncryptLockClient)session.getServletContext().getAttribute("lock");
	boolean methodFlag = false;
	if(lockclient!=null)
	{
		if(lockclient.isHaveBM(29)){
			methodFlag=true;
		}
	}
	/*UserView userView = (UserView) session.getAttribute(WebConstant.userView);
	int versionFlag = 1;
	if (userView != null)
		versionFlag = userView.getVersion_flag(); // 1:专业版 0:标准版*/

	ConfigParameterForm myForm=(ConfigParameterForm)session.getAttribute("configParameterForm");	
	String calItemStr = myForm.getCalItemStr();
	String busitype = myForm.getBusitype();

%>
<script language="javascript" src="/js/common.js"></script>
<script language="JavaScript" src="/performance/kh_plan/defineTargetItems.js"></script>
<script LANGUAGE=javascript src="/js/function.js"></script>
<style>

.fixedDiv_self
{ 
	overflow:auto; 
	height:458 ; 
	BORDER-BOTTOM: #94B6E6 0pt solid; 
    BORDER-LEFT: #94B6E6 0pt solid; 
    BORDER-RIGHT: #94B6E6 0pt solid; 
    BORDER-TOP: #94B6E6 0pt solid ; 
}

</style>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

	<hrms:themes />
	<script language="JavaScript" src="/js/validate.js"></script>
	<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
	<script language="JavaScript">
	
	var calItemStr='${configParameterForm.calItemStr}'
	var busitype='${configParameterForm.busitype}'	
	
	function IsDigit(obj) 
	{
		return ((event.keyCode >= 46) && (event.keyCode <= 57)); 

	}
//检验数字类型
	function checkValue(obj)
	{
	  	if(obj.value.length>0)
	  	{
	  		if(!checkIsNum2(obj.value))
	  		{
	  			alert('请输入数值！');
	  			obj.value='';
	  			obj.focus();
	  		}
	  	} 
	}
	function setSub_page(sub_page)
	{				
		configParameterForm.sub_page.value=sub_page;
	}			
	function getTargetTraceItems(elementName)
	{
		var items = document.getElementsByName(elementName);
		var itemStr='';
		for(var i=0;i<items.length;i++)
		{
			if(items[i].checked==true)
				itemStr+=items[i].value+',';
		}
		return itemStr;
	}
	function setEnable()
	{			
		var targetTraceItems1 = document.getElementsByName("targetTraceItems");
		var targetCollectItems1 = document.getElementsByName("targetCollectItems");
	
		for(var i=0;i<targetTraceItems1.length;i++)
		{
			if(targetTraceItems1[i].checked==true)
			{
				for(var j=0;j<targetCollectItems1.length;j++)
				{
					if(targetCollectItems1[j].value==targetTraceItems1[i].value)
					{
						if(targetCollectItems1[j].checked==false)
						{
							if(document.getElementById('allowLeaderTrace')!=null)
								document.getElementById('allowLeaderTrace').disabled=false;	
							return;
						}
					}
				}
			}
		}
		if(document.getElementById('allowLeaderTrace')!=null)		
			document.getElementById('allowLeaderTrace').checked=false;
		if(document.getElementById('allowLeaderTrace')!=null)
			document.getElementById('allowLeaderTrace').disabled=true;			
			
//		var isTargetCardTemp = document.getElementById('isTargetCardTemp').checked;
//		var isTargetAppraisesTemp = document.getElementById('isTargetAppraisesTemp').checked;

		if(document.getElementById('isTargetCardTemp')!=null)
		{
			if(document.getElementById('isTargetCardTemp').checked==true)
				document.getElementById('targetCardTemp').disabled=false;
			else
				document.getElementById('targetCardTemp').disabled=true;
		}	
		if(document.getElementById('isTargetAppraisesTemp')!=null)
		{
			if(document.getElementById('isTargetAppraisesTemp').checked==true)
				document.getElementById('targetAppraisesTemp').disabled=false;
			else
				document.getElementById('targetAppraisesTemp').disabled=true;
		}				
	}
	    function setDestFldId(theObj)
	    {
	    	var objs = document.getElementsByName('destFldId');
	    	var i=0;
	  		while(i<objs.length)
	  		{
	  			if(objs[i]==theObj)	
	  			{
	  				i++;break;
	  			}					
	  			i++;
	  		}
	  		document.getElementById('destFldId'+i).value=theObj.value;
	    }
	    function setFieldset(theObj)
	    {
	    	var objs = document.getElementsByName('departfieldset');//objs[i]为子集
	    	var i=0;
	  		while(i<objs.length)
	  		{
	  			if(objs[i]==theObj)	
	  			{
	  				i++;break;
	  			}					
	  			i++;
	  		}
	  		document.getElementById('fieldsetid'+i).value=theObj.value;
	    }
		function getTargetAccordStr()
		{
			var n =0;
			var str ='';
			<logic:iterate id="element" name="configParameterForm" property="targetAccordList">
				n++;
				var itemid = '<bean:write name="element" property="itemid" filter="true"/>';
				 var destFldId = document.getElementById('destFldId'+n).value;
				if(destFldId != "")
					str+=itemid+'='+destFldId+',';
			</logic:iterate>
			if(str!='')
				str=str.substr(0,str.length-1);
			configParameterForm.targetAccordStr.value=str;
		}
		function getDepartTextValue()
		{
			var n =0;
			var str ='';
			<logic:iterate id="element" name="configParameterForm" property="allDataList">
				n++;
				var itemid = '<bean:write name="element" property="departfieldid" filter="true"/>';//字段的itemid
				 var destFldId = document.getElementById('fieldsetid'+n).value;//子集的itemid
				if(destFldId != "")
					str+=itemid+'='+destFldId+',';
			</logic:iterate>
			if(str!='')
				str=str.substr(0,str.length-1);
			configParameterForm.departTextValue.value=str;
		}
		function getTargetTraceItems(elementName)
		{
			var items = document.getElementsByName(elementName);
			var itemStr='';
			for(var i=0;i<items.length;i++)
			{
				if(items[i].checked==true)
					itemStr+=items[i].value+',';
			}
			return itemStr;
		}
		function getTargetItems()
		{									
			configParameterForm.targetTraceItem.value=getTargetTraceItems("targetTraceItems");		
			configParameterForm.targetCollectItem.value=getTargetTraceItems("targetCollectItems");	
			configParameterForm.targetDefineItem.value=getTargetTraceItems("targetDefineItems");	
		}
		function changeSubSet()
		{
			getTargetItems();
 			configParameterForm.action='/performance/options/configParameter.do?b_changeSet=link';
 			configParameterForm.submit();
		}
		
		function save()
		{
			var blind_360=document.getElementById("blind_360").value;
			if(blind_360>100)
			{
				alert("360度考评的评价盲点百分比不能大于100%！");
				return;
			}
			<% if(methodFlag){%>
			if(busitype!=null && busitype=="0")
			{
				var blind_goal=document.getElementById("blind_goal").value;
				if(blind_goal>100)
				{
					alert("目标考评的评价盲点百分比不能大于100%！");
					return;
				}
			}
			
			if(busitype!=null && busitype=="0")
			{
				var inputs=document.getElementsByName("targetCalcItems");
				var s="";
				for(i=0;i<inputs.length;i++){
					if(inputs[i].checked)
						s+=inputs[i].value+",";
					}
				for(i=0;i<inputs.length;i++){
					if(!inputs[i].checked){
						s+=inputs[i].value+",";
					}
				}
				configParameterForm.tarItem.value=s;					
				configParameterForm.targetCalcItem.value=getTargetTraceItems("targetCalcItems");
					
				configParameterForm.targetTraceItem.value=getTargetTraceItems("targetTraceItems");		
				configParameterForm.targetCollectItem.value=getTargetTraceItems("targetCollectItems");	
		
				getTargetAccordStr();
				getDepartTextValue();
				var targetDefineItem_value = getTargetTraceItems("targetDefineItems");			
				if(targetDefineItem_value=='')
					targetDefineItem_value=',';
				configParameterForm.targetDefineItem.value=targetDefineItem_value;
			}
			<% } %>
			var eva=document.getElementsByName("eva");
			var obj = document.getElementsByName("obj");
			var e_str="";
			var o_str="";
			if(eva)
			{
			   for(var i=0;i<eva.length;i++)
			   {
			      if(eva[i].checked)
			      {
			        e_str+=","+eva[i].value;
			      }
			   }
			}
			if(obj)
			{
			   for(var i=0;i<obj.length;i++)
			   {
			      if(obj[i].checked)
			      {
			        o_str+=","+obj[i].value;
			      }
			   }
			}
			if(trim(e_str)!="")
			{
			    configParameterForm.e_str.value=e_str.substring(1);
			}else{
			   configParameterForm.e_str.value="NO";
			}
			if(trim(o_str)!="")
			{
			    configParameterForm.o_str.value=o_str.substring(1);
			}else{
			    configParameterForm.o_str.value="NO";
			}
			var theObj = document.getElementById("isTargetCardTemp");
			//此处因为绩效和能力素质共用相同代码，你能力素质没有下面的选项。所以需要判断对象是否为null haosl update
			if(theObj !== null && theObj.checked){
				var value = document.getElementById('targetCardTemp').value;
				if(theObj.checked && (!value || value=='' || value=="-1")){
					alert("请选择目标卡制订邮件模板！");
					return;
				}
			}
            theObj = document.getElementById("istargetTasktracking");
			if(theObj !== null && theObj.checked){
				value = document.getElementById('targetTasktracking').value;
				if(theObj.checked && (!value || value=='' || value=="-1")){
					alert("请选择目标卡任务跟踪邮件模板！");
					return;
				}
			}
            theObj = document.getElementById("isTargetAppraisesTemp");
			if(theObj !== null && theObj.checked){
				value = document.getElementById('targetAppraisesTemp').value;
				if(theObj.checked && (!value || value=='' || value=="-1")){
					alert("请选择考核评分邮件模板！");
					return;
				}
			}
            theObj = document.getElementById("istargetTaskofadjusting");
			if(theObj !== null && theObj.checked){
				value = document.getElementById('targetTaskofadjusting').value;
				if(theObj.checked && (!value || value=='' || value=="-1")){
					alert("请选择目标卡任务调整邮件模板！");
					return;
				}
			}
			configParameterForm.action='/performance/options/configParameter.do?b_save=link';
			configParameterForm.submit();
		}
		function testCheck(theObj)
		{	
			if(theObj.id=='isTargetCardTemp')
			{	
				if(theObj.checked)
					document.getElementById('targetCardTemp').disabled=false;
				else
					document.getElementById('targetCardTemp').disabled=true;
			}else if(theObj.id=='isTargetAppraisesTemp')
			{	
				if(theObj.checked)
					document.getElementById('targetAppraisesTemp').disabled=false;
				else
					document.getElementById('targetAppraisesTemp').disabled=true;
			}else if(theObj.id=='istargetTasktracking')
			{	
				if(theObj.checked)
					document.getElementById('targetTasktracking').disabled=false;
				else
					document.getElementById('targetTasktracking').disabled=true;
			}else if(theObj.id=='istargetTaskofadjusting')
			{	
				if(theObj.checked)
					document.getElementById('targetTaskofadjusting').disabled=false;
				else
					document.getElementById('targetTaskofadjusting').disabled=true;
			}
		}
		
	var _toObj;
	function setObj(o_obj)
	{
		_toObj=o_obj;
	}
	
	function changeTargetDefineItems(theObj)
	{
		var targetDefineTable=document.getElementById("targetDefineTable");
		var obj_tr = theObj.parentNode.parentNode;
		var theTable,theTables,newRow;
		if(theObj.checked)
		{
			theTable = document.getElementById("targetTraceTable");
		    newRow=theTable.insertRow(theTable.rows.length);
			insertrow(newRow,obj_tr,"targetTraceItems",theObj);
			
			theTable = document.getElementById("targetCollectTable");
			newRow=theTable.insertRow(theTable.rows.length);
			insertrow(newRow,obj_tr,"targetCollectItems",theObj);
			
			
			var isOrnotBr = "yes";			
			if(calItemStr.indexOf(","+theObj.value)!=-1)
			{	
				var objs = document.getElementsByTagName('input');
				for(var i=0;i<objs.length;i++)
				{
					if(objs[i].type=="checkbox" && objs[i].name=='targetCalcItems' && objs[i].value.toUpperCase()==theObj.value.toUpperCase())
					{
						isOrnotBr = "no";	
						break;
					}																					
				}	
				if(isOrnotBr=="yes")
				{	 			 																
					theTable = document.getElementById("targetCalcTable");																									 						 			 
					newRow=theTable.insertRow(theTable.rows.length);
					insertrow(newRow,obj_tr,"targetCalcItems",theObj);	
				 	newRow.attachEvent('onclick',clickMouse2);	
				 	setObj(newRow.cells[0]);
					clickMouse();				
				}			
			}
			/*
			if(calItemStr.indexOf(","+theObj.value)!=-1)
			{				 			 																
				theTable = document.getElementById("targetCalcTable");																									 						 			 
				newRow=theTable.insertRow(theTable.rows.length);
				insertrow(newRow,obj_tr,"targetCalcItems",theObj);	
			 	newRow.attachEvent('onclick',clickMouse2);	
			 	setObj(newRow.cells[0]);
				clickMouse();		
			}
			*/
		}						
		else
		{
			theTable = document.getElementById("targetTraceTable");
			theTables = document.getElementById("targetCalcTable");
			var objs = document.getElementsByTagName('input');
			for(var i=0;i<objs.length;i++)
			{
				if(objs[i].type=="checkbox" && objs[i].name=='targetTraceItems' && objs[i].value.toUpperCase()==theObj.value.toUpperCase())
				{
				 	var temp_tr = objs[i].parentNode.parentNode;
				 	for(var x=0;x<theTable.rows.length;x++)
					{	
						if(theTable.rows[x]==temp_tr) 
						{
							theTable = document.getElementById("targetTraceTable");
		    				theTable.deleteRow(x);
			
							theTable = document.getElementById("targetCollectTable");
							theTable.deleteRow(x);
							break;
						}
					}
				}
				if(objs[i].type=="checkbox" && objs[i].name=='targetCalcItems' && objs[i].value.toUpperCase()==theObj.value.toUpperCase() && theObj.value.toUpperCase()!='TASK_SCORE')
				{
				 	var temp_tr = objs[i].parentNode.parentNode;
				 	for(var x=0;x<theTables.rows.length;x++)
					{	
						     
						if(theTables.rows[x]==temp_tr) 
						{								
							theTables = document.getElementById("targetCalcTable");
							theTables.deleteRow(x);
							break;
						}
					}
				 	break;
				}
			}
		}
		setEnable();
	}

	function insertrow(newRow,CopyRow,thename,checkObj)
	{
		var tabstr = "";
		myNewCell=newRow.insertCell(0);
		myNewCell.className = "RecordRow";
		myNewCell.align="center";		
		tabstr="<input type=\"checkbox\" name=\""+thename+"\"  onclick=\"setEnable()\" value=\""+checkObj.value+"\"/>";		
		myNewCell.innerHTML = tabstr;
		
		myNewCell=newRow.insertCell(1);
		myNewCell.className = "RecordRow";
		myNewCell.innerHTML = CopyRow.cells[1].innerHTML;
		myNewCell.align="left";
	}
	function changeBlind()
	{
		var eva=document.getElementsByName("eva");
		var obj = document.getElementsByName("obj");
		document.getElementById("blind360").style.display="none";
		if(busitype!=null && busitype=="0")
			document.getElementById("blindgoal").style.display="none";
		if(eva)
		{
			for(var i=0;i<eva.length;i++)
			{
			   if(eva[i].checked && eva[i].value==7)
			   {
			      document.getElementById("blind360").style.display="";
			      break;
			   }
			}
		}
		if(obj&&busitype!=null && busitype=="0")
		{
			for(var i=0;i<obj.length;i++)
			{
			   if(obj[i].checked && obj[i].value==7)
			   {
			      document.getElementById("blindgoal").style.display="";
			      break;
			   }
			}
		}
		
	}
</script>
</head>
<body onload='setPage()'>
<html:form action="/performance/options/configParameter">
	<html:hidden name="configParameterForm" property="busitype" styleId="busitype" />
	<html:hidden name="configParameterForm" property="targetAccordStr"/>
	<html:hidden name="configParameterForm" property="departTextValue"/>
	<html:hidden name="configParameterForm" property="sub_page"/>
	<table width="100%" align="center">
		<tr>
			<td width="100%">
				<hrms:tabset name="paramset" width="900" height="583" type="false">
					<hrms:tab name="param2" label="打分参数" visible="true" >
						<table border="0" cellspacing="1" cellpadding="2" align="center" onmouseover="setSub_page('param2');">
						  <logic:equal name="configParameterForm" property="busitype" value="0">   <!-- 能力素质  不应该有多人考评设置  2013.11.26 pjf -->
							<tr>
								<td colspan="2" style="padding-top: 16px;">
									<fieldset style="width:100%">
										<legend align="center" style="text-align:center;">
											<bean:message key='jx.param.mulmarkbs' />
										</legend>
										<table width="500" border="0" align="center" style="padding: 15px 0 15px 0;">
											<tr>
												<td>
													<bean:message key='performance.parameterconfig.panway' />:
													</td>
												<td>	
													<html:radio name="configParameterForm" property="redio"
														value="1" />
													<bean:message key='jx.param.planselect' />
												</td>
												<td>
													<html:radio name="configParameterForm" property="redio"
														value="2" />
													<bean:message key='jx.param.pinpuplan' />
												</td>
											</tr>
											<tr>
												<td colspan="3">
													<html:checkbox styleId="togetherCommit"
														name="configParameterForm" property="togetherCommit" value="1"/><bean:message key='performance.unify.submit' />												
												</td>
											</tr>
											<tr>
												<td >
													考核对象基本信息表（人员）										
												</td>
												<td colspan="2">
														<html:select name="configParameterForm" property="nameLinkCard" size="1"
															style="width:200px" styleId="nameLinkCard">
															<html:optionsCollection property="rnameList"
																value="dataValue" label="dataName" />
														</html:select>									
												</td>
											</tr>
										</table>
									</fieldset>
								</td>
							</tr>
							</logic:equal>
							<tr>
								<td colspan="2">
									
								</td>
							</tr>
							<tr>
								<td colspan="2" style="padding-top: 23px;">
									<fieldset style="width:100%">
										<legend align="center" style="text-align:center;">
											<bean:message key='performance.email.notice' />
										</legend>
										<table width="500" border="0" align="center" style="padding: 15px 0 15px 0;">

										<logic:equal name="configParameterForm" property="busitype" value="0">
											<tr <% if(!methodFlag) { %> style="display:none"<% } %> >
												<td>
													<html:checkbox styleId="isTargetCardTemp" onclick="testCheck(this)" 
														name="configParameterForm" property="isTargetCardTemp" value="1" /><bean:message key="performance.email.notice1" />	</td>	<td>
													<html:select name="configParameterForm" property="targetCardTemp" size="1"
															style="width:200px" styleId="targetCardTemp">
															<html:optionsCollection property="emailTempList"
																value="dataValue" label="dataName" />
														</html:select>
												</td>
													</tr>
													
												<tr <% if(!methodFlag) { %> style="display:none"<% } %>>
												<td>
													<html:checkbox styleId="istargetTasktracking" onclick="testCheck(this)" 
														name="configParameterForm" property="istargetTasktracking" value="1" />目标卡任务跟踪需邮件通知	</td>	<td>
													<html:select name="configParameterForm" property="targetTasktracking" size="1"
															style="width:200px" styleId="targetTasktracking">
															<html:optionsCollection property="emailTempList"
																value="dataValue" label="dataName" />
														</html:select>
												</td>
												</tr>
										</logic:equal>	
											
													<tr>
												<td>
													<html:checkbox styleId="isTargetAppraisesTemp"
														name="configParameterForm" property="isTargetAppraisesTemp"
														value="1" onclick="testCheck(this)"/><bean:message key="performance.email.notice2" /></td>	<td>
													<html:select name="configParameterForm" property="targetAppraisesTemp" size="1"
															style="width:200px" styleId="targetAppraisesTemp">
															<html:optionsCollection property="emailTempList"
																value="dataValue" label="dataName" />
													</html:select>
												</td>
											</tr>
										
										<logic:equal name="configParameterForm" property="busitype" value="0">	
												<tr <% if(!methodFlag) { %> style="display:none"<% } %>>
												<td>
													<html:checkbox styleId="istargetTaskofadjusting" onclick="testCheck(this)" 
														name="configParameterForm" property="istargetTaskofadjusting" value="1" />目标卡任务调整需邮件通知	</td>	<td>
													<html:select name="configParameterForm" property="targetTaskofadjusting" size="1"
															style="width:200px" styleId="targetTaskofadjusting">
															<html:optionsCollection property="emailTempList"
																value="dataValue" label="dataName" />
														</html:select>
												</td>
												</tr>
										</logic:equal>	
										
											<tr>
												<td>
													<bean:message key='jx.eval.FeedBack.template' /></td>	<td>
													<html:select name="configParameterForm" property="feedBackTemplate" size="1"
															style="width:200px" styleId="feedBackTemplate">
															<html:optionsCollection property="emailTempList"
																value="dataValue" label="dataName" />
													</html:select>
												</td>
											</tr>
										</table>
									</fieldset>
								</td>
							</tr>
						</table>
					</hrms:tab>
					
	<% if(methodFlag){%>
	<logic:equal name="configParameterForm" property="busitype" value="0">				
	<hrms:tab name="param3" label="jx.configparam.targetitems" visible="true">
	<html:hidden name="configParameterForm" property="tarItem" styleId="tarItem"/>
	<html:hidden name="configParameterForm" property="targetCalcItem" styleId="targetCalcItem"/>
	<html:hidden name="configParameterForm" property="targetTraceItem" styleId="targetTraceItem"/>	
	<html:hidden name="configParameterForm" property="targetCollectItem" styleId="targetCollectItem"/>	
	<html:hidden name="configParameterForm" property="targetDefineItem" styleId="targetDefineItem"/>	
	<table border="0" style="height:300px;" cellspacing="1" cellpadding="2" align="center" onmouseover="setSub_page('param3');"
		width="90%">
		<tr>
			<td height="6" colspan='3'>
				
			</td>
		</tr>
		<tr>
			<td colspan='3' width="100%">
				<fieldset>
					<legend align="center" style="text-align:center;">
							<bean:message key="jx.configparam.targetitems" />
					</legend>			
					<div style='height:230;width:100%; overflow: auto;'>
						<table width="90%" border="0" cellspacing="0" align="center"
							cellpadding="0" class="ListTable" id='targetDefineTable'>
							<logic:iterate id="element" name="configParameterForm"
								property="targetDefineItemList">
								<tr>
									<td align="center" class="RecordRow" width="15%">
										<input name="targetDefineItems" type="checkbox" onclick="changeTargetDefineItems(this)" 
											value="<bean:write name="element" property="itemid" filter="true" />"
											<logic:notEqual name="element" property="selected"
											value="0">checked</logic:notEqual> />
									</td>
									<td align="left" class="RecordRow" nowrap>
										&nbsp;&nbsp;
										<bean:write name="element" property="itemdesc" filter="true" />
									</td>
								</tr>
							</logic:iterate>							
						</table>
					</div>			
				</fieldset>					
			</td>
		</tr>
		<tr>
			<td height="6">
			</td>
		</tr>
		<tr>
			<td style="width:33%">
			<fieldset style="padding-left:0px;">
					<legend align="center" style="text-align:center;">
							<bean:message key="jx.param.targetDisp" />
					</legend>	
					<div style='height:180;width:100%; overflow: auto;'>
						<table width="90%" border="0" cellspacing="0" align="center"
							cellpadding="0" class="ListTable" id='targetTraceTable'>
							<logic:iterate id="element" name="configParameterForm"
								property="targetTraceItemList">
								<tr>
									<td align="center" class="RecordRow" width="15%">
										<input name="targetTraceItems" type="checkbox" onclick="setEnable()" 
											value="<bean:write name="element" property="itemid" filter="true" />"
											<logic:notEqual name="element" property="selected"
											value="0">checked</logic:notEqual> />
									</td>
									<td align="left" class="RecordRow" nowrap>
										&nbsp;&nbsp;
										<bean:write name="element" property="itemdesc" filter="true" />
									</td>
								</tr>
							</logic:iterate>							
						</table>
					</div>
						<table width="90%" border="0" cellspacing="0" align="center"
							cellpadding="0">
							<tr>
									<td align="center" width="15%">
						<html:checkbox styleId="allowLeaderTrace" name="configParameterForm"
										property="allowLeaderTrace" value="1" />
											</td>
									<td align="left"  nowrap>
										&nbsp;&nbsp;
									<bean:message key="plan.param.allowLeaderTrace" />
										</td>
								</tr>
								</table>
					</fieldset>
					</td>
					<td style="width:33%">
					<fieldset>
						<legend align="center" style="text-align:center;">
							<bean:message key="plan.param.targetCollectItems" />
						</legend>		
						<div style='height:200;width:100%; overflow: auto;'>
						<table width="90%" border="0" cellspacing="0" align="center"
							cellpadding="0" class="ListTable" id='targetCollectTable'>
							<logic:iterate id="element" name="configParameterForm"
								property="targetCollectItemList">
								<tr>
									<td align="center" class="RecordRow" nowrap width="15%">
										<input name="targetCollectItems" type="checkbox" onclick="setEnable()" 
											value="<bean:write name="element" property="itemid" filter="true" />"
											<logic:notEqual name="element" property="selected"
											value="0">checked</logic:notEqual> />
									</td>
									<td align="left" class="RecordRow" nowrap>
										&nbsp;&nbsp;
										<bean:write name="element" property="itemdesc" filter="true" />
									</td>
								</tr>
							</logic:iterate>
						</table>
						</div>										
					</fieldset>
					</td>
					
					<% 							
						if(calItemStr!=null && calItemStr.length()>0){  
					%>
					<td style="width:33%" >
					<fieldset>
						<legend align="center" style="text-align:center;">
							<bean:message key="plan.param.targetComputeItems" />
						</legend>		
						<div style='float:left;height:200;width:80%; overflow: auto;'>
						<table width="90%" border="0" cellspacing="0" align="center"
							cellpadding="0" class="ListTable" id="targetCalcTable">
							<logic:iterate id="element" name="configParameterForm"
								property="targetCalcItemList">
								<tr   >
									<td onclick="setObj(this);clickMouse();"  align="center" class="RecordRow" nowrap width="15%">
										<input id="targetCalcItems" name="targetCalcItems" type="checkbox"  
											value="<bean:write name="element" property="itemid" filter="true" />"
											<logic:notEqual name="element" property="selected"
											value="0">checked</logic:notEqual> />
									</td>
									<td onclick="setObj(this);clickMouse();"  align="left" class="RecordRow" nowrap>
										&nbsp;&nbsp;
										<bean:write name="element" property="itemdesc" filter="true" />
									</td>
								</tr>
							</logic:iterate>
						</table>
						</div>	
						<div style='float:left;padding-top:45px;width:20%; height:200;overflow: auto;' visible="true" >
						<table width="100%">
							<tr>
								<td>
									<a href="javaScript:SetRow('up')"><img src="../../images/up01.gif" width="12" height="17" border=0></a>																													
								</td>
							</tr>
							<tr>
							</tr>
							<tr>
							</tr>
							<tr>
								<td>										
									<a href="javaScript:SetRow('down')"><img src="../../images/down01.gif" width="12" height="17" border=0></a>
								</td>
							</tr>
						</table>
					</div>										
					</fieldset>
					</td>
			<%} %>
		</tr>
	</table>																	
	</hrms:tab>
	
	
	<hrms:tab name="param4" label="目标卡岗位职责参数" visible="true">
			<table width="80%" align="center" border='0' class="ListTableF" style="margin-top:-1px;" cellspacing="0" cellpadding="0" onmouseover="setSub_page('param4');" >
				<tr>
					<td align="left" class="RecordRow" colspan="2" >
						<bean:message key="jx.configparam.postset" />:
							&nbsp;<html:select name="configParameterForm" property="targetPostSet" styleId="targetPostSet"  size="1"
									onchange="changeSubSet();"
									style="width:220px">
									<html:optionsCollection property="targetPostSetList" value="dataValue" label="dataName" />
								  </html:select>
			 		</td>
				</tr>
				<tr>
					<td align="left" colspan="2" class="RecordRow">
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<bean:message key="jx.configparam.projectitem" />:
						&nbsp;<html:select name="configParameterForm" property="targetItem" styleId="targetItem"  size="1" 
								style="width:220px">
								<html:optionsCollection property="targetItemList" value="dataValue" label="dataName" />
							  </html:select>
					</td>
				</tr>
		<tr>
			<td  colspan="2" class="RecordRow" >
						<bean:message key="jx.configparam.targetaccordpostset" />
			</td>
		</tr>	
		<tr>
			<td  colspan="2" class="RecordRow" >
				<div class="fixedDiv_self">
					<table width="100%" style="border:0px;" class="ListTable" cellspacing="0" cellpadding="0" >
						<thead>
							<tr	class="fixedHeaderTr">
								<td width='5%' class="TableRow"  align="center"  class="RecordRow" nowrap="nowrap" style="border-top:0px;">
									<bean:message key="label.serialnumber" />
								</td>
								<td width='40%' class="TableRow"  align="center" class="RecordRow" nowrap="nowrap" style="border-top:0px;">
									<bean:message key="jx.configparam.targetitems" />
								</td>
								<td width='15%' class="TableRow"  align="center" class="RecordRow" nowrap="nowrap" style="border-top:0px;">
									<bean:message key="kh.field.type" />
								</td>
								<td width='40%' class="TableRow"  align="center" class="RecordRow" nowrap="nowrap" style="border-top:0px;">
									<bean:message key="jx.configparam.subsetitems" />
								</td>					
							</tr>
						</thead>
						<%
				int i = 0;
				%>
			<logic:iterate id="element" name="configParameterForm" property="targetAccordList" >
			<%
					if (i % 2 == 0)
					{
			%>
			<tr class="trShallow">
				<%
						} else
						{
				%>
			
			<tr class="trDeep">
				<%
						}
						i++;
				%>
				<td align="center" style="border-top:0px;" class="RecordRow" nowrap>
			  		<%=i%>
	        	</td> 
	        	<td align="left" style="border-top:0px;" class="RecordRow" nowrap>
				 &nbsp;<bean:write name="element" property="name" filter="true"/>
				</td>
				<td align="left" style="border-top:0px;" class="RecordRow" nowrap>
					&nbsp; <bean:write name="element" property="dataType" filter="true"/>
				</td>
				<td align="left" style="border-top:0px;" class="RecordRow" nowrap>
					&nbsp; <html:select name="element" property="destFldId" size="1"   style="width:90%" onchange="setDestFldId(this)">
			  	  		<html:optionsCollection  name="element" property="destFldIds" value="dataValue" label="dataName"/>
					 </html:select>
					 <input type="hidden" id="destFldId<%=i%>" value='<bean:write name="element" property="destFldId" filter="true"/>'>
				</td>
			</tr>
			</logic:iterate>
					</table>
				</div>
			</td>
		</tr>	
	</table>
	</hrms:tab>
	</logic:equal>
	
	
	<logic:equal name="configParameterForm" property="busitype" value="0">	
	<hrms:tab name="param6" label="目标卡部门职责参数" visible="true">
			<table width="80%"  align="center" style="margin-top:-1px;" border='0' class="ListTable"  cellspacing="0" cellpadding="0" onmouseover="setSub_page('param6');" >
				<tr>
					<td align="left" class="RecordRow" colspan="2" >
						部门职责子集:
							&nbsp;<html:select name="configParameterForm" property="departDutySet" styleId="departDutySet"  size="1"
									onchange="changeSubSet();"
									style="width:220px">
									<html:optionsCollection property="departDutySetList" value="dataValue" label="dataName" />
								  </html:select>
			 		</td>
				</tr>
				<tr>
					<td align="left" class="RecordRow">
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<bean:message key="jx.configparam.projectitem" />:
						&nbsp;<html:select name="configParameterForm" property="projectField" styleId="projectField"  size="1" 
								style="width:220px">
								<html:optionsCollection property="projectFieldList" value="dataValue" label="dataName" />
							  </html:select>
					</td>
					<td align="left" class="RecordRow">
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;有效时间指标:
						&nbsp;<html:select name="configParameterForm" property="validDateField" styleId="validDateField"  size="1" 
								style="width:220px">
								<html:optionsCollection property="validDateFieldList" value="dataValue" label="dataName" />
							  </html:select>
					</td>
				</tr>
		<tr>
			<td  colspan="2" class="RecordRow" >
						目标卡指标与部门职责子集指标对应关系
			</td>
		</tr>	
		<tr>
			<td  colspan="2" class="RecordRow" >
				<div class="fixedDiv_self">
					<table width="100%" class="ListTable" cellspacing="0" cellpadding="0" >
						<thead>
							<tr	class="fixedHeaderTr">
								<td width='5%' class="TableRow RecordRow"  style="border-top:0px;" align="center" nowrap="nowrap">
									<bean:message key="label.serialnumber" />
								</td>
								<td width='40%' class="TableRow RecordRow"  style="border-top:0px;" align="center" nowrap="nowrap">
									<bean:message key="jx.configparam.targetitems" />
								</td>
								<td width='15%' class="TableRow RecordRow"  style="border-top:0px;" align="center" nowrap="nowrap">
									<bean:message key="kh.field.type" />
								</td>
								<td width='40%' class="TableRow RecordRow"  style="border-top:0px;" align="center" nowrap="nowrap">
									<bean:message key="jx.configparam.subsetitems" />
								</td>					
							</tr>
						</thead>
						<%
				int i = 0;
				%>
			<logic:iterate id="element" name="configParameterForm" property="allDataList" >
			<%
					if (i % 2 == 0)
					{
			%>
			<tr class="trShallow">
				<%
						} else
						{
				%>
			
			<tr class="trDeep">
				<%
						}
						i++;
				%>
				<td align="center" style="border-top:0px;" class="RecordRow" nowrap>
			  		<%=i%>
	        	</td> 
	        	<td align="left" style="border-top:0px;" class="RecordRow" nowrap>
				 &nbsp;<bean:write name="element" property="departfieldname" filter="true"/>
				</td>
				<td align="left" style="border-top:0px;" class="RecordRow" nowrap>
					&nbsp; <bean:write name="element" property="departfieldtype" filter="true"/>
				</td>
				<td align="left" style="border-top:0px;" class="RecordRow" nowrap>
					&nbsp; <html:select name="element" property="departfieldset" size="1" style="width:90%" onchange="setFieldset(this)">
			  	  		<html:optionsCollection  name="element" property="departfieldsetlist" value="dataValue" label="dataName"/>
					 </html:select>
					 <input type="hidden" id="fieldsetid<%=i%>" value='<bean:write name="element" property="departfieldset" filter="true"/>'>
				</td>
			</tr>
			</logic:iterate>
					</table>
				</div>
			</td>
		</tr>	
	</table>
	</hrms:tab>
</logic:equal>
<% } %>

		<hrms:tab name="param5" label="结果反馈方式参数" visible="true">
		<html:hidden property="e_str" name="configParameterForm"/>
		<html:hidden property="o_str" name="configParameterForm"/>
		    <table border="0" cellspacing="1" cellpadding="2" align="center" onmouseover="setSub_page('param5');">
		    <tr>
		    <td>&nbsp;</td>
		    </tr>
		     <tr>
		       <td>
		       <fieldset style="width:80%">
		          <legend align="center" style="text-align:center;">360度考评</legend>
		          <table  border="0" align="center" width="500">
		             <logic:iterate id="evaluate" name="configParameterForm" property="evaluateList" offset="0" indexId="index">
		                <tr><td width="10%" align="center">
                             <logic:equal value="1" name="evaluate" property="select">
                               <input type="checkbox" name="eva" value="<bean:write name="evaluate" property="id"/>" onclick="changeBlind();" checked/>
                             </logic:equal>
                             <logic:equal value="0" name="evaluate" property="select">
                               <input type="checkbox" name="eva" value="<bean:write name="evaluate" property="id"/>" onclick="changeBlind();"/>
                             </logic:equal>
                             </td>
                             <td>
                                	<logic:notEqual value="7" name="evaluate" property="id">
                                		<bean:write name="evaluate" property="name"/>
                                	</logic:notEqual>
	                                <logic:equal value="7" name="evaluate" property="id">
	                                	<bean:write name="evaluate" property="name"/>&nbsp;&nbsp;<span id="blind360"><input type="text" name="blind_360" id="blind_360" value="${configParameterForm.blind_360}" size="3" onkeypress="event.returnValue=IsDigit(this);" onblur="checkValue(this)" class="inputtext"/>%</span>
	                                </logic:equal>
                             </td>
                                </tr>
		             </logic:iterate>
		          </table>
		       </fieldset>
		     </tr>
		      <tr>
		    <td>&nbsp;</td>
		    </tr>

		    <logic:equal name="configParameterForm" property="busitype" value="0">
		      <tr <% if(!methodFlag) { %> style="display:none"<% } %>>
		       <td>
		       <fieldset>
		          <legend align="center" style="text-align:center;">目标考评</legend>
		          <table  border="0" align="center" width="500">
		             <logic:iterate id="objective" name="configParameterForm" property="objectiveList" offset="0" indexId="index">
		                <tr><td width="10%" align="center">
                             <logic:equal value="1" name="objective" property="select">
                               <input type="checkbox" name="obj" value="<bean:write name="objective" property="id"/>" onclick="changeBlind();" checked/>
                             </logic:equal>
                             <logic:equal value="0" name="objective" property="select">
                               <input type="checkbox" name="obj" value="<bean:write name="objective" property="id"/>" onclick="changeBlind();"/>
                             </logic:equal>
                             <td align="left">
                                	<logic:notEqual value="7" name="objective" property="id">
                                		<bean:write name="objective" property="name"/>
                                	</logic:notEqual>
                                 	<logic:equal value="7" name="objective" property="id">
	                                	<bean:write name="objective" property="name"/>&nbsp;&nbsp;<span id="blindgoal"><input type="text" name="blind_goal" id="blind_goal" value="${configParameterForm.blind_goal}" size="3" onkeypress="event.returnValue=IsDigit(this);" onblur="checkValue(this)" class="inputtext"/>%</span>
	                                </logic:equal>
                             </td>
                                </tr>
		             </logic:iterate>
		          </table>
		       </fieldset>
		     </tr>
		     </logic:equal>
		     
		    </table>
		</hrms:tab>
	<hrms:tab name="param1" label="其它参数" visible="true">
		<table border="0" cellspacing="1" cellpadding="2" align="center" onmouseover="setSub_page('param1');">
			<tr>
				<td colspan="2" height="10">

				</td>
			</tr>
			<tr>
				<td>
									<fieldset style="width:80%">
										<legend align="center" style="text-align:center;">
											<bean:message key='system.operation.template' />
										</legend>
										<table  border="0" align="center" width="500" style="padding: 15px 0 15px 0;">
											<tr>
												<td align='right'>
													<logic:notEqual name="configParameterForm" property="busitype" value="1">
														<bean:message key="performance.template.shensu" />
													</logic:notEqual>																	
													<logic:equal name="configParameterForm" property="busitype" value="1">	
														<bean:message key="performance.template.nlshensu" />
													</logic:equal>													
												</td>
												<td>
													<html:select name="configParameterForm" property="appealTemplate" size="1"
															style="width:250px">
															<html:optionsCollection property="busiTempList"
																value="dataValue" label="dataName" />
													</html:select>
												</td>
												</tr>
													<tr>	
												<td align='right'>
													<logic:notEqual name="configParameterForm" property="busitype" value="1">
														<bean:message key="performance.template.interview" />
													</logic:notEqual>																	
													<logic:equal name="configParameterForm" property="busitype" value="1">	
														<bean:message key="performance.template.nlinterview" />
													</logic:equal>													
												</td>
												<td>
													<html:select name="configParameterForm" property="interviewTemplate" size="1"
															style="width:250px">
															<html:optionsCollection property="busiTempList"
																value="dataValue" label="dataName" />
													</html:select>
												</td>
											</tr>
										</table>
									</fieldset>
								</td>
							</tr>
							
							<% if(methodFlag){%>
							<logic:equal name="configParameterForm" property="busitype" value="0">
							<tr>
								<td style="padding-top: 23px;">
									<fieldset style="width:80%">
										<legend align="center" style="text-align:center;">
											<bean:message key='org.performance.card' />
										</legend>
										<table  border="0" align="center" width="500" style="padding: 15px 0 15px 0;">  
											<tr>
												<td align='right'>
													&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<bean:message key="plan.param.DescriptionItem" />
														</td>
														<td>
													<html:select name="configParameterForm" property="descriptionItem" size="1"
															style="width:250px">
															<html:optionsCollection property="targetItemList2"
																value="dataValue" label="dataName" />
													</html:select>
												</td>
												</tr>
													<tr>	
												<td align='right'>
													&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<bean:message key="plan.param.PrincipleItem" />
														</td>
															<td>
													<html:select name="configParameterForm" property="principleItem" size="1"
															style="width:250px">
															<html:optionsCollection property="targetItemList2"
																value="dataValue" label="dataName" />
													</html:select>
												</td>
											</tr>
										</table>
									</fieldset>
								</td>
							</tr>
							</logic:equal>
							<% } %>
							
							<tr>
								<td style="padding-top: 23px;">							
									<fieldset style="width:80%">
										<legend align="center" style="text-align:center;">
											<bean:message key='lable.performance.perPlan' />
										</legend>
										<table  border="0" align="center" width="500">  
											<tr>
												<td align='right' height="50">
													&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
												</td>
												<td align='left' height="50">
													<html:checkbox styleId="controlByKHMoudle"
														name="configParameterForm" property="controlByKHMoudle" value="1"/>														
														<logic:notEqual name="configParameterForm" property="busitype" value="1">
															<bean:message key='jx.parameter.ControlByKHMoudle' />
														</logic:notEqual>																	
														<logic:equal name="configParameterForm" property="busitype" value="1">	
															<bean:message key='jx.parameter.ControlByPGMoudle' />
														</logic:equal>																							
												</td>
											</tr>
										</table>
									</fieldset>											
								</td>
							</tr>
							
							<%-- 
							<tr>
								<td>							
										<html:checkbox styleId="rightCtrlByPerObjType"
														name="configParameterForm" property="rightCtrlByPerObjType" value="1"/><bean:message key='jx.parameter.RightCtrlByPerObjType' />												
								</td>
							</tr>
							--%>															
						</table>
					</hrms:tab>


				</hrms:tabset>

			</td>
		</tr>
			<tr>
				<td  valign="bottom"  align="center" colspan="4">				
					<input type="button" class="mybutton" value="<bean:message key='button.save' />" onClick="save();" />
					
	
		 <%
		 	String temp=request.getParameter("modelflag");

		 if("capability".equals(temp)){ %>
         <hrms:tipwizardbutton flag="capability" target="il_body" formname="configParameterForm"/>  
         <%}else if("performance".equals(temp)){ %>
         <hrms:tipwizardbutton flag="performance" target="il_body" formname="configParameterForm"/>  
         <%} %> 


				</td>
			</tr>
	</table>
</html:form>
</body>
<script language='javascript' >
	
	changeBlind();
	setEnable();
	testCheck(document.getElementById('isTargetAppraisesTemp'));
	<% if(methodFlag){%>
	if(busitype!=null && busitype=="0")
	{
		testCheck(document.getElementById('isTargetCardTemp'));
		testCheck(document.getElementById('istargetTasktracking'));
		testCheck(document.getElementById('istargetTaskofadjusting'));
	}
	<%}%>
	function setPage()
	{		
	<%	
		ConfigParameterForm cpf=(ConfigParameterForm)session.getAttribute("configParameterForm");	
		String sub_page = cpf.getSub_page();
		int spage=0;

		if("param2".equals(sub_page)){
		    spage=0;
		}
		if("param3".equals(sub_page)){
		    spage=1;
		}
		if("param4".equals(sub_page)){
		    spage=2;
		}
		if("param6".equals(sub_page)){
		    spage=3;
		}
		if("param5".equals(sub_page)){
		    spage=4;
		}
		if("param1".equals(sub_page)){
		    spage=5;
		}
		if(sub_page!=null && sub_page.length()>0){
	%>
        var obj=$('paramset');
        if(obj.setSelectedTab){
            obj.setSelectedTab('<%=sub_page%>');
        }else{
            $('#tabset_paramset').tabs('select', <%=spage%>);
        }
	<%}else{%>
			var obj=$('paramset');
        if(obj.setSelectedTab){
            obj.setSelectedTab('param2');
        }else{
            $('#tabset_paramset').tabs('select', 0);
        }
	<%}%>
	}
</script>