<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>

<script language="javascript">
<% 
if(request.getParameter("opt")!=null&&request.getParameter("opt").equals("saveInfo")){
	out.print("closeWin(true);");
}
%>
function save()
{
	var name = document.getElementById("degreename").value;	
	if(ltrim(rtrim(name)) == "")
	{
		alert("<bean:message key='jx.paramset.info1'/>");
		return;
	}
	perDegreeForm.action="/performance/options/perDegreeAdd.do?b_save=link&info=save&opt=saveInfo"; 
	perDegreeForm.target="_self";
	perDegreeForm.submit();
}

function closeWin(flag){
	if(flag) {
		var thevo=new Object();
		thevo.flag="true";
		if(window.showModalDialog){
            parent.window.returnValue=thevo;
		}else {
	 		parent.window.opener.add_ok(thevo);
		}
	}
	parent.window.close();
}


function isPercent(thevalue)
{
	if(thevalue=='1')
		document.getElementById('domainflag').style.display="none";
	else
		document.getElementById('domainflag').style.display="";
}
</script>

<body>
<html:form action="/performance/options/perDegreeAdd">

	<table border="0" cellspacing="0" align="center" cellpadding="0">
	

		<tr>
			<td align="center" nowrap>
				<fieldset align="center" style="width: 420px;">
					<legend>
						等级分类维护	
					</legend>
				<table border="0" cellspacing="2" align="left" cellpadding="5">
				<html:hidden name="perDegreeForm" styleId="degreeId" property="perdegreevo.string(degree_id)"/>
				<tr>
					<td align="right" nowrap valign="left">
						&nbsp;<bean:message key="column.name"/>
					</td>
					<td align="left" nowrap valign="left">
						<html:text name="perDegreeForm" styleId="degreename" property="perdegreevo.string(degreename)" styleClass="inputtext"/>
					</td>
				</tr>
				<tr>
					<td align="right" nowrap valign="left">
						&nbsp;<bean:message key="column.desc"/>
					</td>
					<td align="left" nowrap valign="left">
						 <html:textarea name="perDegreeForm" styleId="degreedesc" property="perdegreevo.string(degreedesc)"   cols="30" rows="6"></html:textarea>
					</td>
				</tr>
				
				
					<tr>
						<td align="right" nowrap valign="left">
							&nbsp;<bean:message key="jx.param.degreeflag"/>
						</td>
						<td align="left" nowrap valign="left">
							<html:select name="perDegreeForm" styleId="theflag" property="perdegreevo.string(flag)" size="1" onchange="isPercent(this.value)">
							<logic:equal name="perDegreeForm" property="busitype" value="0">
								<html:option value="0">
									<bean:message key="jx.param.mark"/>
								</html:option>
								<html:option value="1">
									<bean:message key="jx.param.bili"/>
								</html:option>
								<html:option value="2">
									<bean:message key="jx.param.mix"/>
								</html:option>
								<html:option value="3">
									<bean:message key="jx.param.wx_ratic"/>
								</html:option>
							</logic:equal>
							<logic:equal name="perDegreeForm" property="busitype" value="1">
								<html:option value="4">
									<bean:message key="lable.performance.evaluation.ppd"/>
								</html:option>
								<html:option value="5">
									<bean:message key="label.zp_exam.sum_score"/>
								</html:option>
							</logic:equal>
							</html:select>
						</td>
					</tr>
				
				
				<tr id="domainflag">
					<td align="right" nowrap valign="left">
						&nbsp;<bean:message key="jx.param.fengbiflag"/>
					</td>
					<td align="left" nowrap valign="left">					
						<html:select name="perDegreeForm" property="perdegreevo.string(domainflag)" size="1">
							<html:option value="0">
								<bean:message key="jx.param.upmargin"/>
							</html:option>
							<html:option value="1">
								<bean:message key="jx.param.downmargin"/>
							</html:option>
						</html:select>
					</td>
				</tr>
				<tr>
					<td align="right" nowrap valign="left">
						&nbsp;<bean:message key="kh.field.flag"/>
					</td>
					<td align="left" nowrap valign="left">
						<html:select name="perDegreeForm" property="perdegreevo.string(used)" size="1">
							<html:option value="1">
								<bean:message key="lable.lawfile.availability"/>
							</html:option>
							<html:option value="0">
								<bean:message key="lable.lawfile.invalidation"/>
							</html:option>
						</html:select>
					</td>
				</tr>
		
				</table>
			</fieldset>
		</td>
	</tr>
	</table>
	<table border="0" cellspacing="0" align="center" cellpadding="5" width="100%">
		<tr>
			<td align="center" colspan="2">
				<input type="button" class="mybutton" value="<bean:message key='button.save' />" onClick="save();" />
				<input type="button" class="mybutton" value="<bean:message key='button.cancel' />" onClick="closeWin(false);">  
			</td>
		</tr>
	</table>
</html:form>
</body>
<script type="text/javascript">
<!--
	if($F('theflag')=='1')
		document.getElementById('domainflag').style.display="none";
//-->
</script>
