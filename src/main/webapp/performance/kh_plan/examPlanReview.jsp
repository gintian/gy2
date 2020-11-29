<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<script language="javascript">
		 function save(){	
		 	var agree_idea = $F('agree_idea');
		 	if(agree_idea=='')
		 	{
		 		alert("<bean:message key='jx.khplan.pleaseInput'/>");
		 		return;
		 	}
		 	var thevo=new Object();
       		thevo.flag="true";
       		thevo.approve_result=$F('approve_result');
       		thevo.agree_idea=$F('agree_idea');
       		thevo.plan_id=$F('plan_id');
             parent.window.returnValue=thevo;
             if(window.showModalDialog) {
                 parent.window.close();
             }else{
                 parent.parent.review_window_ok(thevo);
             }
		 }
         function closewindow()
         {
             if(window.showModalDialog) {
                 parent.window.close();
             }else{
                 parent.parent.review_window_ok();
             }
         }
</script>
<body>
	<html:form action="/performance/kh_plan/examPlanReview">
		<br>
		<html:hidden name="examPlanForm" property="examPlanVo.string(plan_id)" styleId="plan_id"/>
		<center>
		<fieldset style="width:80%;">
			<legend>
				<bean:message key="jx.khplan.leaderlook" />
			</legend>
			<table border="0" cellspacing="0" align="center" cellpadding="5">
				<tr>
					<td align="right" nowrap>  
						<bean:message key='jx.khplan.approveresult' />
					</td>
					<td align="left" nowrap>
						<html:select name="examPlanForm"
							property="examPlanVo.string(approve_result)" size="1" styleId="approve_result">
							<html:option value="1">
								<bean:message key='label.agree' />
							</html:option>
							<html:option value="0">
								<bean:message key='label.nagree' />
							</html:option>						
						</html:select>
					</td>
				</tr>
				<tr>
					<td align="right" nowrap>
						<bean:message key='kq.register.overrule' />
					</td>
					<td align="left" nowrap>
						<html:textarea name="examPlanForm" styleId="agree_idea"
							property="examPlanVo.string(agree_idea)" cols="40" rows="6"></html:textarea>
					</td>
				</tr>
			</table>
		</fieldset>
		</center>
		<table border="0" cellspacing="0" align="center" width="40%" cellpadding="5">
				<tr>
					<td align="center">
						<input type="button" class="mybutton"
							value="<bean:message key='button.save' />" onClick="save();" />
					</td>
					<td>
						<input type="button" class="mybutton"
							value="<bean:message key='button.cancel' />"
							onClick="closewindow();">
					</td>
				</tr>
			</table>
	</html:form>
</body>

