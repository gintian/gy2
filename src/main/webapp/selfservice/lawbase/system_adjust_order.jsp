<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<script language="javascript">
   function showLawbasefile(outparamters)
    {
       var lawbaselist=outparamters.getValue("lawbasefilelist");
       AjaxBind.bind(lawbaseForm.lawbase,lawbaselist);
    }
   function SearchLawbasefile(base_id,basetype)
    {
       var hashvo=new ParameterSet();
       hashvo.setValue("basetype",basetype);
       var in_paramters="base_id="+base_id;
       var request=new Request({method:'post',asynchronous:false,parameters:in_paramters,onSuccess:showLawbasefile,functionId:'10400201050'},hashvo);
   }
  function save()
   {
      setselectitem('lawbase');
      lawbaseForm.action = "/selfservice/lawbase/adjust_order.do?b_filesave=link&rflag=1";
      lawbaseForm.taget="_self";
      lawbaseForm.submit();
      
   }
   
   <%String rflag = (String)request.getParameter("rflag");
		if(rflag!=null&&rflag.equals("1")){
	  %>
	  var ab = new Object();
	  parent.window.returnValue = ab;
	  window.close();
	  <%}%>	
</script>

<style>
<!--
.RecordRow1 {
	border: inset 1px;
	BORDER-BOTTOM:1pt solid;
	BORDER-LEFT:1pt solid; 
	BORDER-RIGHT: 1pt solid; 
	BORDER-TOP: medium none;
	font-size: 12px;
	border-collapse:collapse; 
	height:22;
}
-->
</style>
<html:form action="/selfservice/lawbase/adjust_order">
	<table width="510" border="0" cellspacing="0" align="center" cellpadding="0">
		<thead>
			<tr>
				<td align="center" class="TableRow" nowrap colspan="3">
					<bean:message key="lable.lawfile.adjustorder" />
					&nbsp;&nbsp;
				</td>
			</tr>
		</thead>
		<tr>
			<td width="100%" align="center" class="RecordRow1 common_border_color" nowrap>
				<table>
					<tr>
						<td width="50%" align="center">
							<table width="100%">
								<tr>
									<td height="250" valign="top">
										<select name="lawbase" multiple="multiple" style="height:250px;width:100%;font-size:9pt">
										</select>
									</td>
								</tr>
							</table>
						</td>
						<td width="5%" align="center">
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
					<tr>
						<td colspan="2" align="center">注：只支持对直属分类下记录进行排序</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td align="center" nowrap colspan="3" style="height: 35">
				<html:button styleClass="mybutton" property="b_save" onclick="save()">
            		<bean:message key="button.ok"/>
	 	    </html:button>
				<html:button styleClass="mybutton" property="cancel" onclick="window.close();">
					<bean:message key="button.cancel" />
				</html:button>
			</td>
		</tr>
	</table>
</html:form>
<script language="javascript">
   SearchLawbasefile('<bean:write name="lawbaseForm"  property="base_id"/>','<bean:write name="lawbaseForm"  property="basetype"/>');
</script>
