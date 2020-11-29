
<%@ page contentType="text/html; charset=UTF-8"%>

<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>

<SCRIPT LANGUAGE=javascript src="/js/validate.js"></SCRIPT>
<SCRIPT LANGUAGE=javascript src="/js/function.js"></SCRIPT>
<!-- <script language="JavaScript" src="/js/meizzDate.js"></script> -->
<SCRIPT LANGUAGE=javascript>
	
	function getParticularItem()
	{
	    var hashvo=new ParameterSet();
		hashvo.setValue("tabid","${integrateTableForm.tabid}"); 
		hashvo.setValue("unitcode","${integrateTableForm.unitcode}"); 
		var provisionTerm=document.integrateTableForm.provisionTerm.value;
		In_paramters='provisionTerm='+provisionTerm;	
		 var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfo,functionId:'03030000016'},hashvo);		
	}
	
	function returnInfo(outparamters)
	{
		var fieldlist=outparamters.getValue("defaultItemList");	
		AjaxBind.bind(integrateTableForm.left_fields,fieldlist);
	}
	
	
	//总计
	function totalize()
	{
		vos= eval('document.integrateTableForm.right_fields');  
  		if(vos==null)
  			return false;
        var no = new Option();
    	no.value=':'+TOTALACCOUNT+'::2:';
    	no.text=TOTALACCOUNT;
    	vos.options[vos.options.length]=no;
	}
	
	
	//flag 1:撤选  2:全撤 
	function removeitem(sourcebox_id,flag)
	{
	  var vos,right_vo,i;
	  vos= document.getElementsByName(sourcebox_id);
	  if(vos==null)
	  	return false;
	  right_vo=vos[0];
	  for(i=right_vo.options.length-1;i>=0;i--)
	  {
	  	if(flag==1)
	  	{
		    if(right_vo.options[i].selected)
		    {
			right_vo.options.remove(i);
		    }
		}
		else if(flag==2)
		{
			right_vo.options.remove(i);
		}
	  }
	  return true;	  	
	}
	
	
	//全选
	function selectAll(sourcebox_id)
	{
		vos= document.getElementsByName(sourcebox_id);
		if(vos==null)
	  		return false;
	    right_vo=vos[0];
		for(i=right_vo.options.length-1;i>=0;i--)
	 	{
	 		right_vo.options[i].selected=true;
	 	}
	}
	
	//合并
	function merger(sourcebox_id,targetbox_id)
	{
	
		 var variable=window.prompt(REPORT_INFO47,"");
		 if(variable==null||variable==''||variable==' ')
		 {
			return;
		 }
		 if(variable.indexOf("#")!=-1||variable.indexOf("\"")!=-1)
		 {
		 	alert(REPORT_INFO48);
		 	return;
		 }
		  var left_vo,right_vo,vos,i;
		  vos= document.getElementsByName(sourcebox_id);
		  if(vos==null)
		  	return;
		  left_vo=vos[0];
		  vos= document.getElementsByName(targetbox_id);  
		  if(vos==null)
		  	return;
		  right_vo=vos[0];  
		  var provisionTerm=eval("document.integrateTableForm.provisionTerm");		  
		  var no = new Option();	 
		  var final_value="";
		  if(provisionTerm.value.substring(0,2)=='##')
		  {
		    		no.value="UN:"+variable+":";
		  }
		  else
		  {
		    		var left=provisionTerm.value.split("##");
		    		no.value=left[0]+":"+variable+":";
		    		final_value=left[1];
		   }
		  
		  var mergerItem="";
		  for(i=0;i<left_vo.options.length;i++)
		  {
		    if(left_vo.options[i].selected)
		    {
		    	mergerItem+="\'"+left_vo.options[i].value+"\',";
		    	
		    }
  		  }
  		  if(mergerItem=="")
  		  {
  		  	alert(REPORT_INFO49+"!");
  		  	return;
  		  }  
  		  no.value+=mergerItem+":1:"+final_value;
  		  no.text=variable;
  		  right_vo.options[right_vo.options.length]=no;
  		
	}
	//平均值
	function avg(sourcebox_id,targetbox_id)
	{
	
	//	 var variable=window.prompt(REPORT_INFO47,"");
	//	 if(variable==null||variable==''||variable==' ')
	//	 {
	//		return;
	//	 }
	//	 if(variable.indexOf("#")!=-1||variable.indexOf("\"")!=-1)
	//	 {
	//	 	alert(REPORT_INFO48);
	//	 	return;
	//	 }
		  var left_vo,right_vo,vos,i;
		  vos= document.getElementsByName(sourcebox_id);
		  if(vos==null)
		  	return;
		  left_vo=vos[0];
		  vos= document.getElementsByName(targetbox_id);  
		  if(vos==null)
		  	return;
		  right_vo=vos[0];  
		  var provisionTerm=eval("document.integrateTableForm.provisionTerm");	
		  var no = new Option();	 
		  var final_value="";
		  if(provisionTerm.value.substring(0,2)=='##')
		  {
		    		no.value="UN:平均值:";
		  }
		  else
		  {
		    		var left=provisionTerm.value.split("##");
		    		no.value=left[0]+":平均值:";
		    		final_value=left[1];
		   }
		  
		  var mergerItem="";
		  for(i=0;i<left_vo.options.length;i++)
		  {
		    if(left_vo.options[i].selected)
		    {
		    	mergerItem+="\'"+left_vo.options[i].value+"\',";
		    	
		    }
  		  }
  		  if(mergerItem=="")
  		  {
  		   	alert(REPORT_INFO70+"!");
  		  	return;
//  		var  vos= eval('document.integrateTableForm.right_fields');  
// 		if(vos==null)
// 			return false;
//        for(i=0;i<vos.options.length;i++)
//		  {
//		    	mergerItem+="\'"+vos.options[i].value+"\',";
//  		  }
  		  }  
  		  no.value+=mergerItem+":5:"+final_value;
  		  no.text="平均值";
  		  right_vo.options[right_vo.options.length]=no;
  		
	}
	//最大值
	function maxFun(sourcebox_id,targetbox_id)
	{
		  var left_vo,right_vo,vos,i;
		  vos= document.getElementsByName(sourcebox_id);
		  if(vos==null)
		  	return;
		  left_vo=vos[0];
		  vos= document.getElementsByName(targetbox_id);  
		  if(vos==null)
		  	return;
		  right_vo=vos[0];  
		  var provisionTerm=eval("document.integrateTableForm.provisionTerm");		  
		  var no = new Option();	 
		  var final_value="";
		  if(provisionTerm.value.substring(0,2)=='##')
		  {
		    		no.value="UN:最大值:";
		  }
		  else
		  {
		    		var left=provisionTerm.value.split("##");
		    		no.value=left[0]+":最大值:";
		    		final_value=left[1];
		   }
		  
		  var mergerItem="";
		  for(i=0;i<left_vo.options.length;i++)
		  {
		    if(left_vo.options[i].selected)
		    {
		    	mergerItem+="\'"+left_vo.options[i].value+"\',";
		    	
		    }
  		  }
  		  if(mergerItem=="")
  		  {
  		   	alert(REPORT_INFO71+"!");
  		  	return;
  		  }  
  		  no.value+=mergerItem+":4:"+final_value;
  		  no.text="最大值";
  		  right_vo.options[right_vo.options.length]=no;
  		
	}
	//最小值
	function minFun(sourcebox_id,targetbox_id)
	{
		  var left_vo,right_vo,vos,i;
		  vos= document.getElementsByName(sourcebox_id);
		  if(vos==null)
		  	return;
		  left_vo=vos[0];
		  vos= document.getElementsByName(targetbox_id);  
		  if(vos==null)
		  	return;
		  right_vo=vos[0];  
		  var provisionTerm=eval("document.integrateTableForm.provisionTerm");		  
		  var no = new Option();	 
		  var final_value="";
		  if(provisionTerm.value.substring(0,2)=='##')
		  {
		    		no.value="UN:最小值:";
		  }
		  else
		  {
		    		var left=provisionTerm.value.split("##");
		    		no.value=left[0]+":最小值:";
		    		final_value=left[1];
		   }
		  
		  var mergerItem="";
		  for(i=0;i<left_vo.options.length;i++)
		  {
		    if(left_vo.options[i].selected)
		    {
		    	mergerItem+="\'"+left_vo.options[i].value+"\',";
		    	
		    }
  		  }
  		  if(mergerItem=="")
  		  {
  		   	alert(REPORT_INFO72+"!");
  		  	return;
  		  }  
  		  no.value+=mergerItem+":3:"+final_value;
  		  no.text="最小值";
  		  right_vo.options[right_vo.options.length]=no;
  		
	}
	//选择  1:按条件选取  0：全选
	function selectItem(flag)
	{
		  var left_vo,right_vo,vos,i;
		  vos= document.getElementsByName("left_fields");
		  if(vos==null)
		  	return;
		  left_vo=vos[0];
		  vos= document.getElementsByName("right_fields");  
		  if(vos==null)
		  	return;
		  right_vo=vos[0];
		  
		  var provisionTerm=eval("document.integrateTableForm.provisionTerm");		  
		  for(i=0;i<left_vo.options.length;i++)
		  {
		  		if(flag==1)
		  		{
					    if(left_vo.options[i].selected)
					    {
					    	  var no = new Option();
							  var left_text=left_vo.options[i].text.split(":");
							  var final_value="";
							  if(provisionTerm.value.substring(0,2)=='##')
							  {
							    		no.value="UN:";
							  }
							  else
							  {
							    		var left=provisionTerm.value.split("##");
							    		no.value=left[0]+":";
							    		final_value=left[1];
							   }
					    	  no.value+=left_text[1]+":\'"+left_text[0]+"\':0:"+final_value;	
					    	  no.text=left_text[1];					    	  
					    	  right_vo.options[right_vo.options.length]=no;
					    	  
					    }
					 
				}
				else
				{
					      var no = new Option();
							  var left_text=left_vo.options[i].text.split(":");
							  var final_value="";
							  if(provisionTerm.value.substring(0,2)=='##')
							  {
							    		no.value="UN:";
							  }
							  else
							  {
							    		var left=provisionTerm.value.split("##");
							    		no.value=left[0]+":";
							    		final_value=left[1];
							 
							   }
					    	  no.value+=left_text[1]+":\'"+left_text[0]+"\':0:"+final_value;	
					    	  no.text=left_text[1];
					    	  right_vo.options[right_vo.options.length]=no;
			    }
  		  }
	}
	
	
	//保存方案
	function saveScheme()
	{
		var vos= document.getElementsByName("right_fields"); 
		if(vos==null)
		  	return;
		var right_vo=vos[0];
		var a_value='';
		for(i=0;i<right_vo.options.length;i++)
		{

				a_value+="##"+right_vo.options[i].value;
		}
		if(a_value=='')
		{
			alert(REPORT_INFO50);
			return;
		}
		var a_scheme=eval("document.integrateTableForm.scheme");
		var secid=0;
		if(a_scheme.value==0)
		{
			 var no = new Option();
			 
			 var maxValue=0;
			 for(var a=0;a<a_scheme.options.length;a++)
			 {
				if(a_scheme.options[a].value>0)
				{
					maxValue=a_scheme.options[a].value;
				} 
			 }
			 
			 
			 no.value=maxValue*1+1;	
			 secid=maxValue*1+1;	
			 no.text=REPORTPLAN+secid;					    	  
			 a_scheme.options[a_scheme.options.length]=no;
			 a_scheme.options[0].selected=true;	 
		}
		else
		{
			for(var a=0;a<a_scheme.options.length;a++)
			{
				if(a_scheme.options[a].selected==true)
					secid=a_scheme.options[a].value;
			}
		}

		var hashvo=new ParameterSet();
		hashvo.setValue("tabid","${integrateTableForm.tabid}"); 
		hashvo.setValue("unitcode","${integrateTableForm.unitcode}"); 
		hashvo.setValue("secid",secid); 
		hashvo.setValue("content",a_value.substring(2)); 
		hashvo.setValue("type","0"); 
		In_paramters='flag=1';	
		var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfo2,functionId:'03030000017'},hashvo);		
	}
	
	
	//删除方案
	function delScheme()
	{
		var a_scheme=eval("document.integrateTableForm.scheme");
		var a_value;
		for(var a=0;a<a_scheme.options.length;a++)
		{
				if(a_scheme.options[a].selected==true)
					a_value=a_scheme.options[a].value;
		}
		if(a_value!=0)
		{
			var hashvo=new ParameterSet();
			hashvo.setValue("tabid","${integrateTableForm.tabid}"); 
			hashvo.setValue("unitcode","${integrateTableForm.unitcode}"); 
			hashvo.setValue("secid",a_value); 
			hashvo.setValue("type","0");
			In_paramters='flag=1';	
		    var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfo4,functionId:'03030000021'},hashvo);		
		}
	
	}
	
	
	function returnInfo4(outparamters)
	{	
		var secid=outparamters.getValue("secid");
		var a_scheme=eval("document.integrateTableForm.scheme");
		a_scheme.options[0].selected=true
	    removeitem('right_fields',2)
	    
	    for(var a=0;a<a_scheme.options.length;a++)
		{
				if(a_scheme.options[a].value==secid)
					a_scheme.options.remove(a);	
		}
	}
	
	
	function returnInfo2(outparamters)
	{
		alert(SAVESUCCESS);
	}
	
	
	//选择方案
	function selectScheme()
	{
		var a_scheme=eval("document.integrateTableForm.scheme");
		var a_value;
		for(var a=0;a<a_scheme.options.length;a++)
		{
				if(a_scheme.options[a].selected==true)
					a_value=a_scheme.options[a].value;
		}
		removeitem('right_fields',2)
		if(a_value!=0)
		{
				var hashvo=new ParameterSet();
				hashvo.setValue("tabid","${integrateTableForm.tabid}"); 
				hashvo.setValue("unitcode","${integrateTableForm.unitcode}"); 
				hashvo.setValue("secid",a_value); 
				hashvo.setValue("type","0");
				In_paramters='flag=1';	
				var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfo3,functionId:'03030000018'},hashvo);		
		
		}
	}
	
	//返回方案结果
	function returnInfo3(outparamters)
	{
		var content=outparamters.getValue("content");
		var vos= document.getElementsByName("right_fields"); 
		var right_vo=vos[0];
		if(content.indexOf("##")==-1)
		{
				var no = new Option();
			 	no.value=content;	
			 	var a_value=content.split(":");
				no.text=a_value[1];	
				right_vo.options[right_vo.options.length]=no;
		}	
		else
		{
			var a_content=content.split('##');
		
			for(var a=0;a<a_content.length;a++)
			{
				var no = new Option();
			 	no.value=a_content[a];	
			 	var a_value=a_content[a].split(":");
				no.text=a_value[1];	
				right_vo.options[right_vo.options.length]=no;
			}
		}
	}
	
	
	//生成综合表
	function executeTable()
	{
		var vos= document.getElementsByName('right_fields');
		
		if(vos==null){
			alert(SELECTLEFTITEM);
			return false ;
		}
	  	
	    var right_vo=vos[0];
	     if(right_vo.options.length<1){
	     alert(SELECTLEFTITEM);
			return false ;
	     }
//	     alert(right_vo.options.length);//不能超过127条，数据库查表不能超过256次
//	    if(right_vo.options.length>140)
//	    {
//	    	alert(REPORT_INFO51+"！");
//	    	return;
//	    }
		for(i=right_vo.options.length-1;i>=0;i--)
	 	{
	 		right_vo.options[i].selected=true;
	 	}
	 	
		integrateTableForm.action="/report/report_collect/IntegrateTable.do?b_executeTable=exce";
		integrateTableForm.submit();
	}
	
	
	
	function additem(sourcebox_id,targetbox_id)
	{
		var left_vo,right_vo,vos,i;
 	    vos= document.getElementsByName(sourcebox_id);

  		if(vos==null)
  			return false;
  		left_vo=vos[0];
  		vos= document.getElementsByName(targetbox_id);  
  		if(vos==null)
  			return false;
 		right_vo=vos[0];
 		var provisionTerm=eval("document.integrateTableForm.provisionTerm");	
  		for(i=0;i<left_vo.options.length;i++)
  		{
    		if(left_vo.options[i].selected)
    		{
        	
        	
        		  var no = new Option();
				  var left_text=left_vo.options[i].text.split(":");
				  var final_value="";
				  if(provisionTerm.value.substring(0,2)=='##')
				  {
	    			no.value="UN:";
				  }
				  else
				  {
		    		var left=provisionTerm.value.split("##");
		    		no.value=left[0]+":";
		    		final_value=left[1];
				  }
				  no.value+=left_text[1]+":\'"+left_text[0]+"\':0:"+final_value;	
				  no.text=left_text[1];					    	  
				  right_vo.options[right_vo.options.length]=no;
    		}
  		}
	
	
	
	}
	
	
	
	</SCRIPT>
<hrms:themes />
<style>
.mybutton{
	width:50px;
	padding:0 5px 0 5px;
}
</style>
<html:form action="/report/report_collect/IntegrateTable">
	<table  width="100%">
	<tr>
	<td>
	<fieldset align="center" style="width:90%;">
		<legend>
			<bean:message key="report_collect.executeTable"/>
		</legend>

		<table width="100%" height="290">
			<tr>

				<td width="45%" height="268" align="center">
					<table width="100%">
						<tr>
							<td width="100%" align="left">

								<select name="provisionTerm" style="width:100%" onchange="getParticularItem()">
									<logic:iterate id="element" name="integrateTableForm" property="provisionTermList">
										<option value='<bean:write name="element" property="value" />'>
											<bean:write name="element" property="name" />
										</option>
									</logic:iterate>
								</select>
							</td>
						</tr>
						<tr>
							<td width="100%" align="left">
								<select name="left_fields" multiple="multiple" size="10" ondblclick="additem('left_fields','right_fields');" style="height:230px;width:100%;font-size:9pt">
									<logic:iterate id="element" name="integrateTableForm" property="defaultItemList">
										<option value='<bean:write name="element" property="value" />'>
											<bean:write name="element" property="name" />
										</option>
									</logic:iterate>
								</select>
							</td>
						</tr>
					</table>
				</td>

				<td width="11%" valign="middle" align="center">
					<table width="100%" cellpadding="0" cellspacing="2">
						<tr>
							<td style="height:20px" align="center" >
								<input type="button" name="b_up" value="&nbsp;&nbsp;<bean:message key="workdiary.message.total"/>&nbsp;&nbsp;" onClick="totalize();" class="mybutton">
							</td>
						</tr>
						<tr>
							<td style="height:20px" align="center" >
								<input type="button" name="b_up" value="&nbsp;&nbsp;<bean:message key="button.combine"/>&nbsp;&nbsp;" onClick="merger('left_fields','right_fields')" class="mybutton">
							</td>
						</tr>
						<tr>
							<td style="height:20px" align="center" >
								<input type="button" name="b_up" value="平均值&nbsp;" onClick=" avg('left_fields','right_fields')" class="mybutton">
							</td>
						</tr>
						<tr>
							<td style="height:20px" align="center" >
								<input type="button" name="b_up" value="最大值&nbsp;" onClick="maxFun('left_fields','right_fields')" class="mybutton">
							</td>
						</tr>
						<tr>
							<td style="height:20px" align="center" >
								<input type="button" name="b_up" value="最小值&nbsp;" onClick="minFun('left_fields','right_fields')" class="mybutton">
							</td>
						</tr>
						<tr>
							<td style="height:20px" align="center">
								<input type="button" name="b_up" value="&nbsp;&nbsp;<bean:message key="column.select"/>&nbsp;&nbsp;" onClick="selectItem(1);" class="mybutton">
							</td>
						</tr>
						<tr>
							<td style="height:20px" align="center" >
								<input type="button" name="b_up" value="&nbsp;&nbsp;<bean:message key="label.query.selectall"/>&nbsp;&nbsp;" onClick="selectItem(0);" class="mybutton">
							</td>
						</tr>
						<tr>
							<td style="height:20px" align="center" >
								<input type="button" name="b_up" value="&nbsp;&nbsp;<bean:message key="train.job.remove"/>&nbsp;&nbsp;" onClick="removeitem('right_fields','1');" class="mybutton">
							</td>
						</tr>
						<tr>
							<td style="height:20px" align="center">
								<input type="button" name="b_up" value="&nbsp;&nbsp;<bean:message key="lable.performance.clear"/>&nbsp;&nbsp;" onClick="removeitem('right_fields','2');" class="mybutton">
							</td>
						</tr>					
					</table>					
				</td>
				<td width="45%" align="center">
					<table width="100%">
						<tr>
							<td width="100%" align="left">
								<select name="scheme" style="width:100%" onchange='selectScheme()'>
									<logic:iterate id="element" name="integrateTableForm" property="schemeList">
										<option value='<bean:write name="element" property="value" />'>
											<bean:write name="element" property="name" />
										</option>
									</logic:iterate>
								</select>
							</td>
						</tr>
						<tr>
							<td width="100%" align="left">
								<select name="right_fields" multiple="multiple" size="10" ondblclick="removeitem('right_fields');" style="height:230px;width:100%;font-size:9pt">

								</select>
							</td>
						</tr>
					</table>
				</td>
			</tr>
					</table>

	</fieldset>
</td>
</tr>
<tr style="margin-top: 2px; " valign="top">
				<td colspan="3" style="height:35px;" align="center">
					<input type="button" name="b_down" value="<bean:message key="report_collect.saveplan"/>" onClick="saveScheme();" class="mybutton" style="width:60px">
					<input type="button" name="b_down" value="<bean:message key="report_collect.deleteplan"/>" onClick="delScheme();" class="mybutton" style="width:60px;margin-left: -3px;">
					<input type="button" name="b_down" value="<bean:message key="kq.formula.true"/>" onClick="executeTable();" class="mybutton" style="margin-left: -3px;">
					<input name="reset" type="button" class="mybutton" onClick="removeitem('right_fields','2')" value="<bean:message key="button.clear"/>" style="margin-left: -3px;">

				</td>
			</tr>

</table>
</html:form>
