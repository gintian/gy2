<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<script type="text/javascript">
	if("${param.oper}"=='close') {
		if(window.showModalDialog){
			parent.window.close();
		}else {
	   		var win = parent.parent.Ext.getCmp('codeAccord_win');
	   		if(win) {
	    		win.close();
	   		}
		}
	}
	function cancAccord()
	{
		 var havaAccord= $('haveaccord');
		 var flag=true;
 		 for(i=0;i<havaAccord.options.length;i++)
 		 {
 		 	 if(havaAccord.options[i].selected)
  			 {
  			    var theVal = havaAccord.options[i].value;
  			    var theArray = theVal.split('=>');
  			    var souVal= $('source');
  			    
  			    var no = new Option();
   				no.value=theArray[0];
  				no.text=theArray[0];
   				souVal.options[souVal.options.length]=no;
  			    
				havaAccord.options.remove(i);
				flag=false;
   			 }
 		 }
 		 if(flag)
 		 	alert('请先选中已对应代码中的条目！');
	}
	function selAccord()
	{
		var sourceCode= $('source');
		var targetCode= $('target');
		var source = '';
		var target = '';
		var havaAccord= $('haveaccord');
		var sourceIndex=0;
	    for(i=0;i<sourceCode.options.length;i++)
 		{
 		   if(sourceCode.options[i].selected)
  		   {  		   
  		   		source=sourceCode.options[i].text;  		   		
  		   		sourceIndex=i;
  		   		break;
  		   }
  		}
  		for(i=0;i<targetCode.options.length;i++)
 		{
 		   if(targetCode.options[i].selected)
  		   {
  		   		target=targetCode.options[i].text;
  		   		break;
  		   }
  		}
  		if(source == '' || target == '')
  		{
  			alert('请选中一个源代码和一个目标代码进行对应！');
  			return;
  		}
		 var no = new Option();
   		 no.value=source+'=>'+target;
  		 no.text=source+'=>'+target;
   		 havaAccord.options[havaAccord.options.length]=no;
   		 sourceCode.options.remove(sourceIndex);
	}
	function mohuAccord()
	{
		var sourceCode= $('source');
		var targetCode= $('target');
		var havaAccord= $('haveaccord');
		var delStr='';
	    for(i=0;i<sourceCode.options.length;i++)
 		{ 		
 			for(j=0;j<targetCode.options.length;j++)
 			{
 				var value1=sourceCode.options[i].value;
 				var value2=targetCode.options[j].value;
 		  	 	 if(sourceCode.options[i].value==targetCode.options[j].value)
  		  		 {
 		  	 		 var no = new Option();
   					 no.value=sourceCode.options[i].text+'=>'+targetCode.options[j].text;
  					 no.text=no.value;
   					 havaAccord.options[havaAccord.options.length]=no;
   					 delStr+='*'+sourceCode.options[i].value;   		 			
 		  	 	 }		  	
  		    }
  		 }
  		 if(delStr!='')
  		 {
  		 	var ids=delStr.substring(1).split('*');
  		 	for(var n=0;n<ids.length;n++)
  		 	{
  		 		 for(var m=0;m<sourceCode.options.length;m++)
 				 { 
  		 		 	if(sourceCode.options[m].value==ids[n])
  		  		 		 sourceCode.options.remove(m); 
  		 		 }  		 		 		 		 
  		 	}
  		 }
	}
	function saveAccord()
	{
		var havaAccord= $('haveaccord');
		var str='';
 		for(i=0;i<havaAccord.options.length;i++)
 		{
 		 	str+='<@>'+havaAccord.options[i].value;
 		}
	if("${param.planid}"!='')	//考核归档
 		resultFiledForm.action='/performance/evaluation/dealWithBusiness/codeAccord.do?b_save=link&destCode=${param.destCode}&oper=close';
 	else if("${param.id}"!='')//培训归档
 		resultFiledForm.action='/performance/evaluation/dealWithBusiness/codeAccord.do?b_save2=link&destCode=${param.destCode}&sourceField=${param.sourceField}&oper=close';        
        resultFiledForm.strParm.value=str; 		
 		resultFiledForm.submit();
	}
</script>
<html:form action="/performance/evaluation/dealWithBusiness/codeAccord">
	<html:hidden name="resultFiledForm" property="strParm" styleId="strParm"/>
	<table border="0" cellspacing="1" cellpadding="1" 
		width="90%" align="center">
		<tr>
			<td>
				<table>
					<tr>
						<td>
							<bean:message key="jx.eval.sourceCode" />
							<bean:message key="jx.eval.noaccord" />
						</td>
					</tr>
					<tr>
						<td>
							<html:select name="resultFiledForm" property="source"
								multiple="true" size="18" styleId="source"
								style="height:180px;width:190px;font-size:10pt">
								<html:optionsCollection property="sourceCodes" value="dataValue"
									label="dataName" />
							</html:select>
						</td>
					</tr>
				</table>
			</td>
			<td>
				<table>
					<tr>
						<td>
							<bean:message key="jx.eval.targetCode" />
						</td>
					</tr>
					<tr>
						<td>
							<html:select name="resultFiledForm" property="target"
								multiple="true" size="18" styleId="target"
								style="height:180px;width:190px;font-size:10pt">
								<html:optionsCollection property="targetCodes" value="dataValue"
									label="dataName" />
							</html:select>
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td colspan="2">
				<table>
					<tr>
						<td>
							<bean:message key="jx.eval.haveaccordcord" />
						</td>
					</tr>
					<tr>
						<td>
							<html:select name="resultFiledForm" property="haveaccord"
								multiple="true" size="18" styleId="haveaccord" ondblclick="cancAccord()"
								style="height:180px;width:428px;font-size:10pt">
								<html:optionsCollection property="accordCodes" value="dataValue"
									label="dataName" />
							</html:select>
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
	<table width='90%' align='center'>
		<tr>
			<td align='center'>
				<input type='button'
					value='<bean:message key='jx.eval.mohuaccord' />'
					class="mybutton" onclick="mohuAccord()">			
				<input type='button'
					value='<bean:message key='jx.eval.selectaccord' />'
					class="mybutton" onclick="selAccord()">
				<input type='button'
					value='<bean:message key='jx.eval.cancelaccord' />'
					class="mybutton" onclick="cancAccord()">
				<input type='button' 
					value='<bean:message key='jx.eval.saveaccord' />'
					onclick='saveAccord();' class="mybutton">
			</td>
		</tr>
	</table>
</html:form>
