<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<script language="javascript">
   function showLawbasefile(outparamters)
    {
       var lawbaselist=outparamters.getValue("lawbaselist");
       AjaxBind.bind(lawbaseForm.lawbase,lawbaselist);
    }
   function SearchLawbasefile(base_id,basetype)
    {
       var hashvo=new ParameterSet();
       hashvo.setValue("basetype",basetype);
       var in_paramters="base_id="+base_id;
       var request=new Request({method:'post',asynchronous:false,parameters:in_paramters,onSuccess:showLawbasefile,functionId:'10400201010'},hashvo);
   }
  function save()
   {
      setselectitem('lawbase');
      window.returnValue="aaa";
      window.close();
   }	
</script>
<div class="fixedDiv3">
<html:form action="/selfservice/lawbase/adjust_order">
	<table width="100%" border="0" cellspacing="0" align="center" cellpadding="0" class="ListTable">
		<thead>
			<tr>
				<td align="center" class="TableRow" nowrap colspan="1">
					<bean:message key="lable.lawfile.adjustorder" />
					&nbsp;&nbsp;
				</td>
			</tr>
		</thead>
		<tr>
			<td width="100%" align="center" class="RecordRow" nowrap>
				<table>
					<tr>
						<td width="80%" align="center">
							<table width="100%">
								<tr>
									<td height="250" valign="top">
										<select name="lawbase" multiple="multiple" style="height:250px;width:100%;font-size:9pt">
										</select>
									</td>
								</tr>
							</table>
						</td>
						<td width="8%" align="center" valign="middle" style="padding-top: 5px;vertical-align: middle;">
							<html:button styleClass="mybutton" property="b_up" onclick="upItem($('lawbase'));">
								<bean:message key="button.previous" />
							</html:button>
							<br>
							<br>
							<html:button styleClass="mybutton" property="b_down" onclick="downItem($('lawbase'));">
								<bean:message key="button.next" />
							</html:button>
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
	<div style="margin-top: 5px;" align="center">
		<hrms:submit styleClass="mybutton" property="b_save" onclick="save();">
			<bean:message key="button.ok" />
		</hrms:submit>
		<html:button styleClass="mybutton" property="cancel" onclick="window.close();">
			<bean:message key="button.cancel" />
		</html:button>
	</div>
</html:form>
</div>
<script language="javascript">
   SearchLawbasefile('<bean:write name="lawbaseForm"  property="base_id"/>','<bean:write name="lawbaseForm"  property="basetype"/>');
</script>
