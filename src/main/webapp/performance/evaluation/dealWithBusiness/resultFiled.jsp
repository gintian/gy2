<%@ page import="org.apache.commons.beanutils.LazyDynaBean" %>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<style>
.fixedDiv_self
{ 
	overflow:auto; 	
	position:absolute;
	width:690px; 

	height:430px;
	BORDER-TOP: #94B6E6 1pt solid ; 
	/* BORDER-BOTTOM: #94B6E6 1pt solid; 
    BORDER-LEFT: #94B6E6 1pt solid; 
    BORDER-RIGHT: #94B6E6 1pt solid; 
    */
}
</style>
<script>
	function del()
	{
		var imgStr='';
		var selStr='';
		var i=0;
	  	var selectObjs=document.getElementsByTagName("input");	  
	  	while(i<selectObjs.length)
	  	{	  
	  		if(selectObjs[i].type=='checkbox' && selectObjs[i].checked==true && selectObjs[i].name!='selbox')	
			{
				imgStr+='@'+selectObjs[i].value;
				selStr+='@'+selectObjs[i].id;
			}
	  		i++;
	  	}
	  	var imgs=imgStr.split('@'); 
	  	var sels=selStr.split('@');
	  	var selectObjs=document.getElementsByTagName("select");
	  	for(var i=0;i<imgs.length;i++)
	  	{
	  		if(imgs[i]!='')
	  		{
	  			var img = $('img'+imgs[i])
	  			img.style.display='none';
	  			var n =  parseInt(sels[i]);
	  			selectObjs[n].value='';
	  			
	  			var destCode =  $('destFld'+n); 			
				destCode.value='';				
	  		} 	  				
	  	}
	}
	function changeSubSet(value)
	{
 		resultFiledForm.action='/performance/evaluation/dealWithBusiness/resultFiled.do?b_query=link&busitype=${resultFiledForm.busitype}&planid=${param.planid}&filedType=${resultFiledForm.filedType}&dispBt=${resultFiledForm.dispBtFlag}&subSetName='+value;
 		resultFiledForm.submit();
	}
	function isNull(str)
	{
		if(str=='')
			return 'noValue';
		else
			return str;
	}
	function getInfo(oper)
	{
		var info='';
		if(oper=='1')
			info=JX_RESULTFILE_INFO1;
		else if(oper=='2')
			info=JX_RESULTFILE_INFO2;
		else if(oper=='3')
			info=JX_RESULTFILE_INFO3;
		return info;
	}
	function fileSave(oper)
	{
		document.getElementById("tryfile").disabled=true;
		document.getElementById("end").disabled=true;
		document.getElementById("delete").disabled=true;
		document.getElementById("pigeonhole").disabled=true;
		document.getElementById("button_goback").disabled=true;
		var setname = document.getElementById('setName').value;
		if(setname==null || setname=='')
		{
			alert(PLEASE_SEL_SUBSET);
			changeDisplay(1);
			return;
		}
		if(confirm(getInfo(oper)))
		{
		var n =0;
		var sourceCodes=new Array();
		var sourceNames=new Array();
		var destCodes=new Array();
		var destTypes=new Array();
		<logic:iterate id="element" name="resultFiledForm" property="sourcePoints" >
			n++;
			var sourceCode = '<bean:write name="element" property="id" filter="true"/>';
			var name = '<bean:write name="element" property="name" filter="true"/>';
			var destCode =  $F('destFld'+n); 
			var destType =	$F('destCode'+n);

			if(sourceCode=='planname' && destCode=='')
			{
				alert(PLANNAME_NEED_ACCORD);
				changeDisplay(1);
				return;
			}

            if(sourceCode=='plan_id' && destCode=='')
            {
                alert("考核计划ID必须对应！");
                changeDisplay(1);
                return;
            }
			
			sourceCodes[n-1]=sourceCode;
			sourceNames[n-1]=name;
			destCodes[n-1]=isNull(destCode);
			destTypes[n-1]=isNull(destType);
		</logic:iterate>
		 var hashvo=new ParameterSet();
		 hashvo.setValue("oper",oper);
		 hashvo.setValue("sourceCodes",sourceCodes);
		 hashvo.setValue("sourceNames",sourceNames);
		 hashvo.setValue("destCodes",destCodes);
		 hashvo.setValue("destTypes",destTypes);
		 hashvo.setValue("setName",$F('setName'));
		 hashvo.setValue("filedType",'${resultFiledForm.filedType}');
		 In_paramters="planID=${param.planid}";
		 var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onFailure:changeDisplay,onSuccess:returnInfo,functionId:'9024001003'},hashvo);	
		 }else{
			 changeDisplay(1);
		}	
	}
	function returnInfo(outparamters)
	{
		var flag=outparamters.getValue("flag");		
		var filedType=outparamters.getValue("filedType");
		var isHaveTeamLeader=outparamters.getValue("isHaveTeamLeader");
		var planId=outparamters.getValue("planId");
		var oper=outparamters.getValue("oper");//1 试归档 2 归档 3 结束
		if(flag=='success')
		{	
			if(oper=='1')
				alert('试归档成功！');
			else if(oper=='2')
				alert('归档成功！');
			else if(oper=='3')	
			{				
				passParameter(oper);
			}
			
			if(filedType=='1' || filedType=='4')//考核计划的考核对象为人员或者是对团队负责人归档
			{				
				passParameter(oper);
			}else if(filedType=='2')//单位 部门 团队的归档
			{
				if(isHaveTeamLeader=='1')//有团队负责人
				{
					if("1"==oper)
						oper = "all";
					resultFiledForm.action="/performance/evaluation/dealWithBusiness/resultFiled.do?b_query=link&busitype=${resultFiledForm.busitype}&filedType=4&planid="+planId+"&dispBt="+oper;
					resultFiledForm.submit();
				}else
				{
					passParameter(oper);
				}
			}
		}
		else
			alert('归档失败！');	
	}
	function dispImg(fieldName,obj)
	{		
		var setName = $F('setName'); 
		var hashvo=new ParameterSet();
 		hashvo.setValue("destFildSet",setName);
 		hashvo.setValue("fieldName",fieldName);
 		
 		var i=0;
	  	var selectObjs=document.getElementsByTagName("select");
	  	//var imgObjs=document.getElementsByTagName("img");
	  	while(i<selectObjs.length)
	  	{
	  		
	  		if(selectObjs[i]==obj)	
				break;
	  		i++;
	  	}
 		hashvo.setValue("imgcount",i+"");
		var request=new Request({asynchronous:false,
			onSuccess:dispImg2,functionId:'9024001005'},hashvo);
		
	}
	
	function passParameter(oper){
		var thevo=new Object();
		thevo.flag="true";
		thevo.oper=oper;
		if(window.showModalDialog){
            parent.window.returnValue=thevo;
		}else {
     		parent.parent.resultFiled3_ok(thevo);
		}
		closeWin();
	}
	
	function closeWin() {
		if(window.showModalDialog){
            parent.window.close();
		}else {
	   		var win = parent.parent.Ext.getCmp('resultFiled3_win');
	   		if(win) {
	    		win.close();
	   		}
		}
	}
	function dispImg2(outparamters)
	{
		var disp=outparamters.getValue("disp");
		var i = parseInt(outparamters.getValue("imgcount"));
		var destFildid = outparamters.getValue("destFildid");
		var codesetid = outparamters.getValue("codesetid");
		var imgObjs=document.getElementsByTagName("img");	
		var img = $(imgObjs[i+1].id);
		img.style.display=disp;	
		var destFild = $('destFld'+i); 
		destFild.value=	destFildid;
		var destCode = $('destCode'+i); 
		destCode.value=	codesetid;
	}
	function codeAccord(sourceField,i)
	{
		//alert(sourceField+"--"+$F('destFld'+i)+"--" +$F('destCode'+i)+"${param.planid}");
		var theurl="/performance/evaluation/dealWithBusiness/codeAccord.do?b_query=link`planid=${param.planid}`sourceField="+sourceField+"`destCode="+$F('destCode'+i);	
   		var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
   		//var return_vo= window.showModalDialog(iframe_url, 'resultFiled_win', 
      	//			"dialogWidth:550px; dialogHeight:520px;resizable:no;center:yes;scroll:yes;status:no");
   		var config = {
 			width:550,
 			height:520,
 			type:'1',
 			id:"codeAccord_win"
 		}

 		modalDialog.showModalDialogs(iframe_url,"codeAccord_win",config,"");
	}
	function selAll(theObj)
	{
		var chklist,objname,i,typeanme;
      	chklist=document.getElementsByTagName('input');
      
        if(!chklist) 	
        	return;
	  	for(i=0;i<chklist.length;i++)
		{	
	    	 typeanme=chklist[i].type.toLowerCase();
	    	 if(typeanme!="checkbox")
	        	continue;	  
	    	 if(chklist[i].disabled)
	     		continue;	            
  	         chklist[i].checked=theObj.checked;  	     
	  	}   
	}
	function changeDisplay(outparamters){
		document.getElementById("tryfile").disabled=false;
		document.getElementById("end").disabled=false;
		document.getElementById("delete").disabled=false;
		document.getElementById("pigeonhole").disabled=false;
		document.getElementById("button_goback").disabled=false;
	}
</script>
<hrms:themes />
<html:form action="/performance/evaluation/dealWithBusiness/resultFiled">

	<table width="100%" >
		<tr>
			<td align="left">
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<bean:message key="jx.eval.resultFiledSubSet" />
				<html:select name="resultFiledForm" property="setName" size="1"
					styleId="setName" onchange="changeSubSet(this.value);"
					style="width:220px">
					<html:option value=""></html:option>
					<html:optionsCollection property="subSet" value="dataValue"
						label="dataName" />
				</html:select>
			</td>
		</tr>
	</table>

	<!-- <div style='height:280;width:90%; overflow: auto;' id='aa'> -->
	 <div class="fixedDiv_self common_border_color">
		<table  cellspacing="0" cellpadding="0"
			class="ListTable" width="100%" style='border-collapse: separate;border:0px;'>
			<thead>
				<tr	class="fixedHeaderTr1">
					<td width='8%' class="TableRow"  align="center" nowrap="nowrap" style="border-top:0px;border-right: 0px;">
						&nbsp;<input type="checkbox" name="selbox" onclick="selAll(this);" title='<bean:message key="label.query.selectall"/>'>&nbsp;
					</td>
					<td style="border-top:0px;border-right: 0px;" width='30%' class="TableRow"  align="center" nowrap="nowrap">
						<bean:message key="kq.pigeonhole.srcfldname" /><!-- 源指标 -->
					</td>
					<td style="border-top:0px;border-right: 0px;" width='20%' class="TableRow"  align="center" nowrap="nowrap">
						<bean:message key="kh.field.type" /><!-- 类型 -->
					</td>
					<td style="border-top:0px;border-right: 0px;" width='30%' class="TableRow"  align="center" nowrap="nowrap">
						<bean:message key="kq.pigeonhole.destfldname" /><!-- 目的指标 -->
					</td>
					<td style="border-top:0px;" width='12%' class="TableRow"  align="center" nowrap="nowrap">
						<bean:message key="jx.eval.codeaccord" /><!-- 代码对应 -->
					</td>
				</tr>
			</thead>
				<%
				int i = 0;
				%>
			<logic:iterate id="element" name="resultFiledForm" property="sourcePoints" >
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
				<td align="center" class="RecordRow" nowrap style="border-top:0px;border-right: 0px;">
			  		<input type='checkbox' id='<%=i%>'  value='<bean:write name="element" property="id" filter="true"/>'   />
	        	</td> 
	        	<td align="left" class="RecordRow" nowrap style="border-top:0px;border-right: 0px;">&nbsp;
					<bean:write name="element" property="name" filter="true"/>
					<%
						LazyDynaBean abean=(LazyDynaBean)pageContext.getAttribute("element");
						String id=(String) abean.get("id");
						if("plan_id".equalsIgnoreCase(id)||"planname".equalsIgnoreCase(id)){
					%>
					<span style="color: red">*</span>
					<% }%>


				</td>
				<td align="left" class="RecordRow" nowrap style="border-top:0px;border-right: 0px;">&nbsp;
					 <bean:write name="element" property="dataType" filter="true"/>
				</td>
				<td align="left" class="RecordRow_left" nowrap style="border-top:0px;border-right: 0px;">
					 <html:select name="element" property="destFldId" size="1"  style="width:100%" onchange="dispImg(this.value,this)">
			  	  		<html:optionsCollection  name="element" property="destFldIds" value="dataValue" label="dataName"/>
					 </html:select>
				</td>
				<td align="center" class="RecordRow" nowrap style="border-top:0px;">
					<input type="hidden" id="destFld<%=i%>" value="<bean:write name="element" property="destFldId" filter="true"/>">
					<input type="hidden" id="destCode<%=i%>" value="<bean:write name="element" property="destType" filter="true"/>">
					<img id="img<bean:write name="element" property="id" filter="true"/>" src="/images/edit.gif" border=0 <logic:notEqual name="element" property="destType" value="0"> style="cursor:hand;display:block"</logic:notEqual> <logic:equal name="element" property="destType" value="0"> style="cursor:hand;display:none"</logic:equal>onclick="codeAccord('<bean:write name="element" property="id" filter="true"/>','<%=i%>')"> 
				</td>				
			</tr>
		</logic:iterate>
		</table>
	</div>

	<div style='position:absolute;left:5px;top:470px;'>
	<table width='100%' align='center'>
		<tr><!-- 【5423】绩效管理：绩效评估，在进行试归档的时候删除等按钮不能紧贴着表格，应该有点缝隙    jingq add 2014.12.26 -->
			<td align='center' height="35px;">
			<logic:notEqual name="evaluationForm" property="planStatus" value="7">
				<input type='button' id="delete" value='<bean:message key='kq.shift.cycle.del' />'	class="mybutton" onclick="del()">
			</logic:notEqual>
				<logic:equal name="resultFiledForm" property="dispBtFlag" value="all">
					<input type='button' id="end" value='<bean:message key='org.performance.end' />'	class="mybutton" onclick="fileSave(3);">
					
					<logic:equal name="resultFiledForm" property="busitype" value="0">
						<hrms:priv func_id="326041102">
							<input type='button' id="tryfile" value='<bean:message key='jx.evalution.tryfile' />' class="mybutton" onclick='fileSave(1);'>
						</hrms:priv>
						<hrms:priv func_id="326041101">
							<input type='button' id="pigeonhole" value='<bean:message key='kq.pigeonhole.submit' />' class="mybutton" onclick='fileSave(2);'>
						</hrms:priv>
					</logic:equal>
					<logic:equal name="resultFiledForm" property="busitype" value="1">
						<hrms:priv func_id="3603031002">
							<input type='button' id="tryfile" value='<bean:message key='jx.evalution.tryfile' />' class="mybutton" onclick='fileSave(1);'>
						</hrms:priv>
						<hrms:priv func_id="3603031001">
							<input type='button' id="pigeonhole" value='<bean:message key='kq.pigeonhole.submit' />' class="mybutton" onclick='fileSave(2);'>
						</hrms:priv>
					</logic:equal>
								
				</logic:equal>
			   	<logic:equal name="resultFiledForm" property="dispBtFlag" value="2">
			   	<logic:notEqual name="evaluationForm" property="planStatus" value="7">
					<input type='button' id="end" value='<bean:message key='org.performance.end' />'	class="mybutton" onclick="fileSave(3);">
					
					<logic:equal name="resultFiledForm" property="busitype" value="0">
						<hrms:priv func_id="326041102">
							<input type='button' id="tryfile" value='<bean:message key='jx.evalution.tryfile' />' class="mybutton" onclick='fileSave(1);'>
						</hrms:priv>
						<hrms:priv func_id="326041101">
							<input type='button' id="pigeonhole" value='<bean:message key='kq.pigeonhole.submit' />' class="mybutton" onclick='fileSave(2);'>
						</hrms:priv>
					</logic:equal>
					<logic:equal name="resultFiledForm" property="busitype" value="1">
						<hrms:priv func_id="3603031002">
							<input type='button' id="tryfile" value='<bean:message key='jx.evalution.tryfile' />' class="mybutton" onclick='fileSave(1);'>
						</hrms:priv>
					</logic:equal>
					
				</logic:notEqual>
				</logic:equal>
				<logic:equal name="resultFiledForm" property="dispBtFlag" value="1">
					<input type='button' id="end" value='<bean:message key='org.performance.end' />'	class="mybutton" onclick="fileSave(3);">	
					
					<logic:equal name="resultFiledForm" property="busitype" value="0">				
						<hrms:priv func_id="326041101">
							<input type='button' id="pigeonhole" value='<bean:message key='kq.pigeonhole.submit' />' class="mybutton" onclick='fileSave(2);'>
						</hrms:priv>
					</logic:equal>
					<logic:equal name="resultFiledForm" property="busitype" value="1">				
						<hrms:priv func_id="3603031001">
							<input type='button' id="pigeonhole" value='<bean:message key='kq.pigeonhole.submit' />' class="mybutton" onclick='fileSave(2);'>
						</hrms:priv>
					</logic:equal>
								
				</logic:equal>
		
				<input type='button' id="button_goback"	value='<bean:message key='button.cancel' />' onclick='closeWin();' class="mybutton">
			</td>
		</tr>
	</table>
	</div>
</html:form>