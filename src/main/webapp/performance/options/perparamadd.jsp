<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>

<hrms:themes />
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/common.js"></script>
<script language="javascript">
<% 
	if(request.getParameter("opt")!=null&&request.getParameter("opt").equals("saveClose")){
		out.print("var thevo=new Object(); thevo.flag='true';");
		out.print("if(window.showModalDialog){");
		out.print(" parent.window.returnValue=thevo"); 
		out.print("}else{");
		out.print(" parent.parent.window.opener.add_ok(thevo)");
		out.print("}");
		out.print(" parent.parent.window.close();");
	}
%>

	function cancelFunc()
	{
		if('${param.oper}'=='saveContinue')
		{
			var thevo=new Object();
			thevo.flag="true";
			if(window.showModalDialog){
				window.returnValue=thevo;
			}else {
				parent.window.opener.add_ok(thevo)
			}
		}
		parent.window.close();
	}
 	function myClose()
 	{
		if('${param.oper}'=='saveContinue')
		{
			var thevo=new Object();
			thevo.flag="true";
			window.returnValue=thevo;
		}
 	}
	function save(oper)
	{
		if($F('param_name')== '')
		 {
		 	alert("<bean:message key='jx.paramset.info1'/>");
		 	return;
		 }
		 var param='';
		 if(oper=='saveContinue')
		 	param+='&info=add';
		 perParamForm.action="/performance/options/perParamAdd.do?b_save=link&opt="+oper+"&oper="+oper+param; 
		 perParamForm.target="_self";
		 perParamForm.submit();	
	}
		function changeRun(runvalue){
			var runflag = runvalue.value;
			var projectDesc=new Array('考核计划','考核时间','人员姓名/部门名称','综合得分（包括修正分值）','等级','选定进行排名的总人数/团队总数','在指定范围中的名次','考核对象所在的分组名称','考核对象所在分组参与此次考核的人数','考核对象在本分组中的考核名次','考核对象所在部门名称','考核对象所在部门参与此次考核的人数','考核对象在本部门中的考核名次','考核计划的单项指标数','总体评价票数统计，没有总体评价时为空','了解程度票数统计，没有了解程度时为空','参与考评的各类主体的评价得分','单项指标得分高于该指标平均分的指标','单项指标得分低于该指标平均分的指标');
			var project=new Array('考核计划','考核时间','考核对象','综合得分','等级','总数','名次','分组名称','组内人数','组内排名','部门名称','部门考核人数','部门内名次','指标总数','评价统计','了解程度','分类得分','高分指标','低分指标');
			var oDiv = document.getElementById("oDiv");
            oDiv.innerHTML = projectDesc[runflag];
           // document.getElementById("content").value=document.getElementById("content").value+"["+project[runflag]+"]";
            //在文本框原来焦点处写值
            symbol("content","["+project[runflag]+"]");
		}
		function example(){
			var hashvo=new ParameterSet();
			var request=new Request({method:'post',asynchronous:false,onSuccess:showlist1,functionId:'9026005006'},hashvo);
		}
		function showlist1(outparamters)
		{
			var content=outparamters.getValue("content");
			document.getElementById("content").value=getDecodeStr(content)+document.getElementById("content").value;
		}
		
</script>
<body onbeforeunload="myClose();">
<html:form action="/performance/options/perParamAdd">
<table border="0" width="100%" cellspacing="0" cellpadding="2" >
<table border="0" cellspacing="0" align="center" cellpadding="2" width="100%">

			<tr>
						<td align="center" nowrap>
							<fieldset style="width:615;">
							<legend>
									评语模板维护
							</legend>
						
		<table border="0" cellspacing="2" align="left" cellpadding="5">
			<html:hidden name="perParamForm" styleId="id" property="perparamvo.string(id)"/>
			<tr>
				<td align="right" nowrap valign="left">
					<bean:message key='column.name' />
				</td>
				<td align="left" nowrap valign="left">
					<html:text name="perParamForm" styleId="param_name" property="perparamvo.string(param_name)" styleClass="inputtext"/>
				</td>
			</tr>
			<tr>
				<td align="right" nowrap valign="left">
					<bean:message key='conlumn.board.content' />
				</td>
				<td align="left" nowrap valign="left">
					<html:textarea name="perParamForm" property="perparamvo.string(content)" styleId="content" cols="90" rows="20"></html:textarea>
					<table border="0" cellspacing="0" align="left" cellpadding="2">
						<tr>
						<br/>
							<td align="right" nowrap valign="left">
								<bean:message key='org.maip.reference.projects'/>：
							</td>
							<td align="left" nowrap valign="left">
								<select name="project"  size="1" onchange="changeRun(this);">
									<option value="0">
										<bean:message key='lable.performance.perPlan'/>
									</option>
									<option value="1">
										<bean:message key='jx.param.examtime'/>
									</option>
									<option value="2">
										<bean:message key='lable.appraisemutual.examineobject'/>
									</option>
									<option value="3">
										<bean:message key='jx.param.zhscore'/>
									</option>
									<option value="4">
										<bean:message key='jx.param.dengji'/>
									</option>
									<option value="5">
										<bean:message key='jx.param.total'/>
									</option>
									<option value="6">
										<bean:message key='jx.param.mingci'/>
									</option>
									<option value="7">
										<bean:message key='jx.param.fenzname'/>
									</option>
									<option value="8">
										<bean:message key='jx.param.zunnumber'/>
									</option>
									<option value="9">
										<bean:message key='jx.param.zunpaiming'/>
									</option>																											
									<option value="10">
										<bean:message key='jx.param.departmentName'/>
									</option>
									<option value="11">
										<bean:message key='jx.param.departmentPersonNumber'/>
									</option>
									<option value="12">
										<bean:message key='jx.param.departmentMingci'/>
									</option>
									<option value="13">
										<bean:message key='jx.param.zhibiaototal'/>
									</option>
									<option value="14">
										<bean:message key='jx.param.pingjiastatis'/>
									</option>
									<option value="15">
										<bean:message key='lable.statistic.knowdegree'/>
									</option>
									<option value="16">
										<bean:message key='jx.param.fenleiscore'/>
									</option>
									<option value="17">
										<bean:message key='jx.param.highindica'/>
									</option>
									<option value="18">
										<bean:message key='jx.param.lowindica'/>
									</option>
								</select>
							</td>
							
							<td align="right" nowrap valign="left">
							&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
								<bean:message key='jx.param.prodescrip'/>：
							</td>
							<td align="left" nowrap valign="left">
								<div id="oDiv"><bean:message key='lable.performance.perPlan'/></div>
							</td>
						</tr>
					</table>
				</td>
			</tr>
		</table>
	</fieldset>
					</td>
			</tr>
				</table>
	<table border="0" align="center">
		<tr>
			<td colspan="2">
				<input type="button" class="mybutton" value="<bean:message key='button.save' />" onClick="save('saveClose');" />
				<% if(request.getParameter("info")!=null && request.getParameter("info").equals("add")){%>
				<input type="button" value="<bean:message key='button.save'/>&<bean:message key='edit_report.continue'/>" onclick="save('saveContinue');" Class="mybutton">
				<%} %>
				<input type="button" class="mybutton" value="<bean:message key='jx.param.example' />" onClick="example();" />
				<input type="button" class="mybutton" value="<bean:message key='button.cancel' />" onClick="cancelFunc();">  
			</td>
		</tr>
	</table>
</html:form>
</body>
