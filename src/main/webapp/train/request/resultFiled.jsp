<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<script>
var saveflag=true;//判断是否保存
	function del()
	{
		var imgStr='';
		var selStr='';
		var i=0;
	  	var selectObjs=document.getElementsByTagName("input");	  
	  	while(i<selectObjs.length)
	  	{	  
	  		if(selectObjs[i].type=='checkbox' && selectObjs[i].checked==true)	
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
	  			var img = $('img'+imgs[i]);
	  			if(img != null)
	  			  img.style.display='none';
	  			var n =  parseInt(sels[i]);
	  			selectObjs[n].value='';
	  		} 	  				
	  	}
	}
	function changeSubSet(value)
	{
 		resultFiledForm.action='/train/request/resultFiled.do?b_query=link&type=${param.type}&id=${param.id}&subSetName='+value;
 		resultFiledForm.submit();
	}
	function isNull(str)
	{
		if(str=='')
			return 'noValue';
		else
			return str;
	}
	function Save(schemasave)
	{
		var n =0;
		var havedestcode=false;//是否对应了指标
		
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
			sourceCodes[n-1]=sourceCode;
			sourceNames[n-1]=name;
			destCodes[n-1]=isNull(destCode);
			destTypes[n-1]=isNull(destType);		
			
			if(sourceCode=='R4006' && destCode=='')
			{
				alert("<bean:message key='train.b_plan.trains.startime'/>");
				return;
			}
			
			<logic:equal name="element" property="mustRela" value="1">
			 if("" == destCode)
			 {
				 alert('【<bean:write name="element" property="name" filter="true"/>】<bean:message key="train.archive.schema.mustrela"/>');
				 return;
			 }
			 
			</logic:equal>
			
			if('noValue' != destCodes[n-1])
				havedestcode = true;
		</logic:iterate>
		
		if(!havedestcode)
		{
			alert("<bean:message key='train.archive.schema.nofieldrela'/>");
			return;
		}
		
		 var hashvo=new ParameterSet();
		 hashvo.setValue("sourceCodes",sourceCodes);
		 hashvo.setValue("sourceNames",sourceNames);
		 hashvo.setValue("destCodes",destCodes);
		 hashvo.setValue("destTypes",destTypes);
		 hashvo.setValue("setName",$F('setName'));
		 In_paramters='id=${param.id}';
		 hashvo.setValue("type", ${param.type});
		 hashvo.setValue("schemasave", schemasave);
		 
		 if(0 == schemasave){ //归档
			 if(!saveflag){
				alert(TRAIN_CALSS_RESULTFILED);
				return;
			}
		     var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfo,functionId:'2020030022'},hashvo);
		 }else //保存方案
			var request = new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:schemaSaveOK,functionId:'2020030022'},hashvo);
	}
	
	function returnInfo(outparamters)
	{
		var flag=outparamters.getValue("flag");				
		if(flag=='success')
		{
			alert("<bean:message key='train.b_plan.trains.filingok'/>");
			var thevo=new Object();
			thevo.flag="true";
			window.returnValue=thevo;
			window.close();
		}
		else if(flag=='failure'){
			alert("<bean:message key='train.b_plan.trains.filingno'/>");
		}
		else if(outparamters.getValue("info"))
			alert(getDecodeStr(outparamters.getValue("info")));	
	}
	
    function schemaSaveOK(outparamters)
    {
        var flag=outparamters.getValue("flag");             
        if(flag=='success')
        {
        	saveflag=true;
            alert("<bean:message key='train.archive.schemaok'/>");
        }
        else if(flag=='failure'){
            alert("<bean:message key='train.archive.schemano'/>");
        }
        else if(outparamters.getValue("info"))
            alert(getDecodeStr(outparamters.getValue("info")));         
    }
	var itemid;
	function dispImg(fieldName,obj,id)
	{	
		saveflag=false;
		itemid=id;
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
	function dispImg2(outparamters)
	{
		var disp=outparamters.getValue("disp");
		var i = parseInt(outparamters.getValue("imgcount"));
		var destFildid = outparamters.getValue("destFildid");
		var codesetid = outparamters.getValue("codesetid");
//		var imgObjs=document.getElementsByTagName("img");	
		var img = $("img"+itemid);
		if(img != null)
		  img.style.display=disp;	
		var destFild = $('destFld'+i); 
		destFild.value=	destFildid;
		var destCode = $('destCode'+i); 
		destCode.value=	codesetid;
	}
	function codeAccord(sourceField,i)
	{
		//alert(sourceField+"--"+$F('destFld'+i)+"--" +$F('destCode'+i)+"--"+$F('setName'));				
		var theurl="/performance/evaluation/dealWithBusiness/codeAccord.do?b_query2=link`type=${param.type}`id=${param.id}`sourceField="+sourceField+"`destCode="+$F('destCode'+i);	
   		var iframe_url="/general/query/common/iframe_query.jsp?src="+theurl;
   		var return_vo= window.showModalDialog(iframe_url, 'resultFiled_win', 
      				"dialogWidth:550px; dialogHeight:520px;resizable:no;center:yes;scroll:yes;status:no");
	}
	function selectAll(obj){
		var sels = document.getElementsByTagName("input");
		for(var i=0; i<sels.length; i++){
			if(sels[i].type=="checkbox" && sels[i].name!="selall")
				sels[i].checked = obj.checked;
		}
	}
	//授权保存按钮若有则内容显示正常 若无内容置灰 zhangcq 2016/5/5
	function isDisabled(){
		<logic:equal  name="type" value="3" >
          <hrms:priv func_id="323830801">
            var selects = document.getElementsByTagName("select"); 
            for ( var i=0; i < selects.length; i++){
				    selects[i].disabled = false;
		    }
             </hrms:priv>
        </logic:equal>
                
        <logic:notEqual  name="type" value="3" >
           <hrms:priv func_id="32330501">
            var selects = document.getElementsByTagName("select"); 
            for ( var i=0; i < selects.length; i++){
				    selects[i].disabled = false;
		      }
		 
             </hrms:priv>
        </logic:notEqual>
	}
</script>
<html:form action="/train/request/resultFiled">
<div  class="dixedDiv3">
	<table width="100%" border="0" cellpadding="0" cellspacing="0"  >
		<tr>
			<td align="left" style="padding-left: 8px;">
				<bean:define id="type" value="${param.type}" />
				<logic:equal  name="type" value="3" >
				  <bean:message key="train.examplan.archive.subset" />
				</logic:equal>
				<logic:equal  name="type" value="2" >
				  <bean:message key="train.teacher.archive.subset" />
				</logic:equal>
				<logic:equal  name="type" value="1" >
				  <bean:message key="train.job.archive.subset" />
				</logic:equal>
				<html:select name="resultFiledForm" property="setName" size="1"  
					styleId="setName" onchange="changeSubSet(this.value);"
					style="width:220px;" disabled="true">
					<html:option value=""></html:option>
					<html:optionsCollection property="subSet" value="dataValue"
						label="dataName" />
				</html:select>
			</td>
		</tr>
	</table>

	 <div class="fixedDiv complex_border_color" style="margin-top: 5px;margin-left: 5px;">
		<table border="0" cellspacing="0" cellpadding="0"
			width="100%">
			<thead>
				<tr class="fixedHeaderTr">
					<td width='40%' class="TableRow noleft" style="border-top: none;" align="center"  nowrap="nowrap">
						<bean:message key="kq.pigeonhole.srcfldname" />
					</td>
					<td width='40%' class="TableRow noleft noright" style="border-top: none;" align="center"  nowrap="nowrap">
						<bean:message key="kq.pigeonhole.destfldname" />
					</td>
					<td width='12%' class="TableRow" style="border-top: none;" align="center"  nowrap="nowrap">
						<bean:message key="jx.eval.codeaccord" />
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
	        	<td align="left" class="RecordRow noleft" style="border-top: none;" nowrap>
				  &nbsp;&nbsp;<bean:write name="element" property="name" filter="true"/>
				</td>
				<td align="left" class="RecordRow noleft" style="border-top: none;" nowrap>
					<bean:define id="itemid" name="element" property="id"></bean:define>
					 <html:select  name="element" property="destFldId" size="1"  style="width:100%" disabled="true" onchange="dispImg(this.value,this,'${itemid}')">
			  	  		<html:optionsCollection  name="element" property="destFldIds" value="dataValue" label="dataName"/>
					 </html:select>
				</td>
				<td align="center" class="RecordRow noleft" style="border-top: none;" nowrap>
					<input type="hidden" id="destFld<%=i%>" value="<bean:write name="element" property="destFldId" filter="true"/>">
					<input type="hidden" id="destCode<%=i%>" value="<bean:write name="element" property="destType" filter="true"/>">
					
			    <logic:equal  name="type" value="3" >
                   <hrms:priv func_id="323830801">
                      <img id="img<bean:write name="element" property="id" filter="true"/>" src="/images/edit.gif" border=0 " 
					    <logic:notEqual name="element" property="destType" value="0"> 
					        style="cursor:hand;display:block"
					    </logic:notEqual> 
					    <logic:equal name="element" property="destType" value="0"> 
					        style="cursor:hand;display:none"
					    </logic:equal>
					    onclick="codeAccord('<bean:write name="element" property="id" filter="true"/>','<%=i%>')"> 
                   </hrms:priv>
                </logic:equal>
                
                <logic:notEqual  name="type" value="3" >
                   <hrms:priv func_id="32330501">
					<img id="img<bean:write name="element" property="id" filter="true"/>" src="/images/edit.gif" border=0 " 
					    <logic:notEqual name="element" property="destType" value="0"> 
					        style="cursor:hand;display:block"
					    </logic:notEqual> 
					    <logic:equal name="element" property="destType" value="0"> 
					        style="cursor:hand;display:none"
					    </logic:equal>
					    onclick="codeAccord('<bean:write name="element" property="id" filter="true"/>','<%=i%>')"> 
                   </hrms:priv>
                </logic:notEqual>
				</td>				
			</tr>
		</logic:iterate>
		</table>
	</div>
	<table width='100%' align='center'>
		<tr>
			<td align='center' style="padding-top: 5px;">
	           <logic:equal  name="type" value="3" >
                   <hrms:priv func_id="323830801">
                      <input type='button' value='&nbsp;<bean:message key="button.save"/>&nbsp;' class='mybutton' onclick='Save("1");'>
                   </hrms:priv>
                </logic:equal>
                
                <logic:notEqual  name="type" value="3" >
                   <hrms:priv func_id="32330501">
                      <input type='button' value='&nbsp;<bean:message key="button.save"/>&nbsp;' class='mybutton' onclick='Save("1");'>
                   </hrms:priv>
                </logic:notEqual>
                
                <logic:equal  name="type" value="3" >
                   <hrms:priv func_id="323830802">
                      <input type='button' value='&nbsp;<bean:message key='kq.pigeonhole.submit' />&nbsp;' class="mybutton" onclick='Save("0");'>
                   </hrms:priv>
                </logic:equal>
                
                <logic:notEqual  name="type" value="3" >
                   <hrms:priv func_id="32330502">                
                      <input type='button' value='&nbsp;<bean:message key='kq.pigeonhole.submit' />&nbsp;' class="mybutton" onclick='Save("0");'>
                   </hrms:priv>
                </logic:notEqual>
                
				<input type='button' id="button_goback"	value='&nbsp;<bean:message key='button.cancel' />&nbsp;' class="mybutton" onclick='window.close();'>
			</td>
		</tr>
	</table>
</center>
</div>
<script>
isDisabled();
</script>
</html:form>

