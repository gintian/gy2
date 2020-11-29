<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<script language="javascript">
  function save()
   {
      setselectitem('role_list');
      sysForm.action = "/system/security/rolesearch.do?b_ordersave=link&rflag=1";
      sysForm.taget="_self";
      sysForm.submit();

   }
   
  function closeWin(){
      parent.parent.closeWin();
	   
  }
  
   <%String rflag = (String)request.getParameter("rflag");
		if(rflag!=null&&rflag.equals("1")){
	  %>
			parent.parent.commonWinSuccess(true);
	  <%}%>	
</script>
<html:form action="/system/security/rolesearch">
	<table width="390" border="0" cellspacing="0" align="center" cellpadding="0" class="ListTable">
		<tr>
			<td width="100%" align="center" class="RecordRow_lrt" nowrap  colspan="3">
				<table>
					<tr>
						<td width="96%" align="center">
							<table width="100%">
								<tr>
									<td height="250" valign="top"><!-- 【7105】角色管理中，按照角色特称排序后，调整顺序界面显示不对。 jingq upd 2015.01.29 -->
										<html:select name="sysForm" property="role_list" value="" multiple="multiple" style="height:250px;width:100%;font-size:9pt">
											<html:optionsCollection property="orderList" value="dataValue" label="dataName"/>
										</html:select>
									</td>
								</tr>
							</table>
						</td>
						<td width="4%" align="center">
							<html:button styleClass="smallbutton" property="b_up" onclick="upItem($('role_list'));">
								<bean:message key="button.previous" />
							</html:button>
							<html:button styleClass="smallbutton" property="b_down" onclick="downItem($('role_list'));" style="margin-top:30px;">
								<bean:message key="button.next" />
							</html:button>
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td align="center" class="RecordRow" nowrap colspan="3" style="height: 35px">
				<html:button styleClass="mybutton" property="b_save" onclick="save()">
            		<bean:message key="button.ok"/>
	 	    </html:button>
				<html:button styleClass="mybutton" property="cancel" onclick="closeWin();">
					<bean:message key="button.cancel" />
				</html:button>
			</td>
		</tr>
	</table>
</html:form>