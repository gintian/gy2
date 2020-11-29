<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.train.request.TrainEffectEvalForm"%>
<%@ page import="org.apache.commons.beanutils.LazyDynaBean"%>
<script language="JavaScript" src="/js/validateDate.js"></script>
<% 
TrainEffectEvalForm trainform = (TrainEffectEvalForm)request.getAttribute("trainEffectEvalForm");
LazyDynaBean quesJob = trainform.getQuesJob();
LazyDynaBean quesTeacher = trainform.getQuesTeacher();
LazyDynaBean temJob = trainform.getTemJob();
LazyDynaBean temTeacher = trainform.getTemTeacher();
LazyDynaBean ctrl_apply = trainform.getCtrl_apply();
LazyDynaBean ctrl_count = trainform.getCtrl_count();
String r3127 = trainform.getR3127();
%>
<script>
	if('${param.oper}'=='close'){
		window.returnValue="aaa";
		window.close();
	}
	function save()
	{
		var date1=$F('temJob.end_date');
		if(date1=='')
		{
			alert("<bean:message key='train.b_plan.request.traintime.null'/>");
			return ;
		}
		var date2=$F('temTeacher.end_date');
		if(date2=='')
		{
			alert("<bean:message key='train.b_plan.request.tetime.null'/>");
			return ;
		}
		var date3=$F('quesJob.end_date');
		if(date3=='')
		{
			alert("<bean:message key='train.b_plan.request.seachtime.null'/>");
			return ;
		}
		var date4=$F('quesTeacher.end_date');
		if(date4=='')
		{
			alert("<bean:message key='train.b_plan.request.teseachtime.null'/>");
			return ;
		}

		trainEffectEvalForm.action="/train/request/trainEffectEval.do?b_save=link&classid=${param.classid}&oper=close";
		trainEffectEvalForm.submit();
	}
	function testTheSame(id,obj)
	{
		var temJob = $F('temJob');
		var temTeacher = $F('temTeacher');
		var quesJob = $F('quesJob');
		var quesTeacher = $F('quesTeacher');
		var ctrl_apply = $F('ctrl_apply');
		var ctrl_count = $F('ctrl_count'); 
	
		if(id=='temJob' && temTeacher!='' && temTeacher==temJob)
		{
			alert("<bean:message key='train.b_plan.request.select.temodel'/>");
			obj.value='';
			return;
		}else if(id=='temTeacher' && temJob!='' && temTeacher==temJob)
		{
			alert("<bean:message key='train.b_plan.request.select.trainmodel'/>");
			obj.value='';
			return;
		}else if(id=='quesJob' && quesTeacher!='' && quesTeacher==quesJob)
		{
			alert("<bean:message key='train.b_plan.request.select.questmodel'/>");
			obj.value='';
			return;
		}else if(id=='quesTeacher' && quesJob!='' && quesTeacher==quesJob)
		{
			alert("<bean:message key='train.b_plan.request.select.tquestmodel'/>");
			obj.value='';
			return;
		}
		
		var url = location.href;
		var paraString = url.substring(url.indexOf("?")+1,url.length).split("&");
		var classId = paraString[1].substring(paraString[1].indexOf("=")+1,paraString[1].length);
		 var hashvo = new ParameterSet();
		 hashvo.setValue("id",classId);
		 hashvo.setValue("quesJob",quesJob);
		 hashvo.setValue("quesTeacher",quesTeacher);
		 var request=new Request({method:'post',asynchronous:false,onSuccess:isSearch,functionId:'202003003301'},hashvo);
		 function isSearch(outparamters){
				if(outparamters){
					  var temp=outparamters.getValue("check");
					  var temp1=outparamters.getValue("che");
					  if("yes" == temp){
						  alert(TRAIN_EFFECT_CLASS);
						  document.forms[0].quesJob.value="";
					  }
					  if("yes" == temp1){
						  alert(TRAIN_EFFECT_TEACHER);
						  document.forms[0].quesTeacher.value="";
					  }
				}
			}
	}
	
</script>
<div class="fixedDiv3">
<html:form action="/train/request/trainEffectEval">
	<table width="100%" border="0" cellspacing="0" cellpadding="0" align="center" >	
		<tr>
			<td>
				&nbsp;&nbsp;&nbsp;<bean:message key="train.job.name" /><bean:write name="trainEffectEvalForm" property="className" filter="false"/>
			</td>
		</tr>
	</table>
	<table width="98%" border="0" cellspacing="0" align="center"
		cellpadding="0" class="ListTableF" style="margin-top: 3px;padding: 2px;">
		<thead>
			<tr>
				<td align="center" class="TableRow" nowrap>
					<bean:message key="train.job.evaltype" />
				</td>
				<td align="center" class="TableRow" nowrap>
					<bean:message key="train.job.evaltable" />
				</td>
				<td align="center" class="TableRow" nowrap>
					<bean:message key="performance.implement.start" />
				</td>
				<td align="center" class="TableRow" nowrap>
					<bean:message key="train.job.endate" />
				</td>
			</tr>			
		</thead>
		<tr>
				<td align="left"  class="RecordRow" nowrap>
					&nbsp;<bean:write name="trainEffectEvalForm" property="temJob.title" filter="true" />&nbsp;
				</td>
				<td align="left"  class="RecordRow" nowrap>
					&nbsp;<html:select name="trainEffectEvalForm" property="temJob.text" size="1" style="width:163px" styleId="temJob" onchange="testTheSame('temJob',this);">
						<html:optionsCollection property="temJob.textSet" value="dataValue" label="dataName" />
					</html:select>&nbsp;
				</td>
				<td align="center"  class="RecordRow" nowrap>
					<html:checkbox  name="trainEffectEvalForm" property="temJob.run" value="1"/>
				</td>  
				<td align="left"  class="RecordRow" nowrap>
					 &nbsp;<input type="text" class="textColorWrite" name="temJob.end_date" size="16"   extra="editor"  class="m_input"  style="font-size:10pt;text-align:left"
							 dropDown="dropDownDate" value="<%=(String)temJob.get("end_date") %>" onchange=" if(!validate(this,'')) { this.value='';}">&nbsp;
				</td>
		</tr>
		<tr>
				<td align="left"  class="RecordRow" nowrap>
					&nbsp;<bean:write name="trainEffectEvalForm" property="temTeacher.title" filter="true" />&nbsp;
				</td>
				<td align="left"  class="RecordRow" nowrap>
					&nbsp;<html:select name="trainEffectEvalForm" property="temTeacher.text" size="1" style="width:163px" styleId="temTeacher" onchange="testTheSame('temTeacher',this);"> 
						<html:optionsCollection property="temTeacher.textSet" value="dataValue" label="dataName" />
					</html:select>&nbsp;
				</td>
				<td align="center"  class="RecordRow" nowrap>
					<html:checkbox  name="trainEffectEvalForm" property="temTeacher.run" value="1"/>
				</td>
				<td align="left"  class="RecordRow" nowrap>
					  &nbsp;<input type="text" class="textColorWrite"  name="temTeacher.end_date" size="16"   extra="editor"  class="m_input"  style="font-size:10pt;text-align:left"
							 dropDown="dropDownDate" value="<%=(String)temTeacher.get("end_date") %>" onchange=" if(!validate(this,'')) { this.value='';}">&nbsp;
			    </td>
		</tr>
		<tr>
				<td align="left"  class="RecordRow" nowrap>
					&nbsp;<bean:write name="trainEffectEvalForm" property="quesJob.title" filter="true" />&nbsp;
				</td>
				<td align="left"  class="RecordRow" nowrap>
					&nbsp;<html:select name="trainEffectEvalForm" property="quesJob.text" size="1" style="width:163px" styleId="quesJob" onchange="testTheSame('quesJob',this);">
						<html:optionsCollection property="quesJob.textSet" value="dataValue" label="dataName" />
					</html:select>&nbsp;
				</td>
				<td align="center"  class="RecordRow" nowrap>
					<html:checkbox  name="trainEffectEvalForm" property="quesJob.run" value="1"/>
				</td>
				<td align="left"  class="RecordRow" nowrap>
					 &nbsp;<input type="text" class="textColorWrite"  name="quesJob.end_date" size="16"   extra="editor"  class="m_input"  style="font-size:10pt;text-align:left"
							 dropDown="dropDownDate" value="<%=(String)quesJob.get("end_date") %>" onchange=" if(!validate(this,'')) { this.value='';}">&nbsp;
				</td>
		</tr>
		<tr>
				<td align="left"  class="RecordRow" nowrap>
					&nbsp;<bean:write name="trainEffectEvalForm" property="quesTeacher.title" filter="true" />&nbsp;
				</td>
				<td align="left"  class="RecordRow" nowrap>
					&nbsp;<html:select name="trainEffectEvalForm" property="quesTeacher.text" size="1" style="width:163px" styleId="quesTeacher" onchange="testTheSame('quesTeacher',this);">
						<html:optionsCollection property="quesJob.textSet" value="dataValue" label="dataName" />
					</html:select>&nbsp;
				</td>
				<td align="center"  class="RecordRow" nowrap>
					<html:checkbox  name="trainEffectEvalForm" property="quesTeacher.run" value="1"/>
				</td>
				<td align="left"  class="RecordRow" nowrap>
					 &nbsp;<input type="text" class="textColorWrite"  name="quesTeacher.end_date"  size="16"   extra="editor"  class="m_input"  style="font-size:10pt;text-align:left"
							 dropDown="dropDownDate" value="<%=(String)quesTeacher.get("end_date")%>" onchange=" if(!validate(this,'')) { this.value='';}">&nbsp;
				</td>
		</tr>
		<tr><td>&nbsp;</td></tr>
		<tr>
			<td align="center" valign="bottom" colspan="4">			  
				<fieldset style="width:100%">
					<legend>
						<bean:message key="train.Application"/>
					</legend>
					
					<table cellspacing="0" width="100%" cellpadding="0" border="0">
						<tr>
							<td height='10' colspan='2'></td>
						</tr>
						<tr>
							<td align="center">
							 <%if("04".equalsIgnoreCase(r3127)|| "06".equalsIgnoreCase(r3127)){%>
								<bean:write name="trainEffectEvalForm" property="ctrl_apply.title" filter="true" />：
								<html:select name="trainEffectEvalForm" property="ctrl_apply.text" size="1" style="width:40px" styleId="ctrl_apply"
								   onchange="testTheSame('ctrl_apply',this);" disabled="true">
								<html:optionsCollection property="ctrl_apply.textSet" value="dataValue" label="dataName" />
								</html:select>
							<%} else{%>
								<bean:write name="trainEffectEvalForm" property="ctrl_apply.title" filter="true" />：
								<html:select name="trainEffectEvalForm" property="ctrl_apply.text" size="1" style="width:40px" styleId="ctrl_apply"
								   onchange="testTheSame('ctrl_apply',this);">
								<html:optionsCollection property="ctrl_apply.textSet" value="dataValue" label="dataName" />
								</html:select>
							<%} %>
							</td>
							<td align="center">
							<%if("04".equalsIgnoreCase(r3127)|| "06".equalsIgnoreCase(r3127)){%>
								<bean:write name="trainEffectEvalForm" property="ctrl_count.title" filter="true" />：
						    	<html:select name="trainEffectEvalForm" property="ctrl_count.text" size="1" style="width:40px" styleId="ctrl_count" onchange="testTheSame('ctrl_count',this);"
						    	disabled="true">
								<html:optionsCollection property="ctrl_count.textSet" value="dataValue" label="dataName" />
								</html:select>
							<%} else{%>
								<bean:write name="trainEffectEvalForm" property="ctrl_count.title" filter="true" />：
						    	<html:select name="trainEffectEvalForm" property="ctrl_count.text" size="1" style="width:40px" styleId="ctrl_count" onchange="testTheSame('ctrl_count',this);">
								<html:optionsCollection property="ctrl_count.textSet" value="dataValue" label="dataName" />
								</html:select>
							<%} %>
							</td>
						</tr>	
							<tr>
							<td height='5' colspan='2'></td>
						</tr>				
					</table>					
					
                </fieldset>
			</td>
		</tr>
	</table>
		<table width="98%" border="0" cellspacing="0" cellpadding="0" align="center">
		<tr>
			<td align="center" style="padding-top: 5px;">
				<input type="button" value="<bean:message key="button.ok"/>" onClick="save()"  class="mybutton" >&nbsp;&nbsp;&nbsp;&nbsp;
				<input type="button" value="<bean:message key="button.cancel"/>" onClick="window.close();"  class="mybutton" >
			</td>
		</tr>
	</table>
</html:form>
</div>