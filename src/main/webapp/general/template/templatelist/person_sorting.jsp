<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/constant.js"></script>
<script language="javascript" src="/general/template/templatelist/templatelist.js"></script> 

<style type="text/css"> 
.dis_sort_table {
           border: 1px solid #eee;
           height: 230px;    
           width: 230px;            
           overflow: auto;            
           margin: 0;
}           

.vButtonmargin {
	margin-bottom: 30px;

}           

</style>
<%

    UserView userView = (UserView) session.getAttribute(WebConstant.userView);
    String bosflag = "";
    if (userView != null) {
        bosflag = userView.getBosflag();
    }
%>
 <%
     if ("hcm".equals(bosflag)) {
 %>
<link href="/general/template/template.css" rel="stylesheet" type="text/css"/>
<%
    }
%>
<html:form action="/general/template/sorting">
<table style="width:525px;" border="0"   align="center" cellpadding="0" cellspacing="0">
		<tr>
			<td>
				<table width="100%" cellspacing="0" class="ListTable">
					<thead>
						<tr>
							<td align="left" class="TableRow" nowrap style="padding-left:5px">
								<bean:message key="label.query.selectfield" />
							</td>
						</tr>
					</thead>

					<tr>
						<td width="100%" align="center" class="RecordRowP" nowrap>
							<table border="0">
								<tr>
									<td align="left" width="44%">
										<table width="100%" height="100%">
											<tr height="16">
												<td align="left" valign="bottom">
													<bean:message key="selfservice.query.queryfield" />
												</td>
											</tr>
											<tr>
												<td align="left" valign="top">
													<html:select name="templateListForm" property="itemid" multiple="multiple" ondblclick="addfield();removeitem('itemid');"  style="height:230px;font-size:9pt;width:100%;">
														<html:optionsCollection property="itemlist" value="dataValue" label="dataName" />
													</html:select>
												</td>
											</tr>
										</table>
									</td>
									<td width="8%" align="center">
										<html:button styleClass="mybutton vButtonmargin" property="b_addfield"
											onclick="addfield();removeitem('itemid');">
											<bean:message key="button.setfield.addfield" />
										</html:button>
										<br>
										<html:button styleClass="mybutton" property="b_delfield"
											onclick="deletefield();">
											<bean:message key="button.setfield.delfield" />
										</html:button>
									</td>
									<td width="44%" align="left">
										<table width="100%" height="100%">
											<tr height="16">
												<td align="left" valign="bottom">
													<bean:message key="label.query.selectedsortfield" />
												</td>
											</tr>
											<tr>
												<td width="100%">
													<div id="dis_sort_table" class='dis_sort_table common_border_color'>
														<table width="100%" border="0">
															<tr>
																<td class="TableRow" width="10%" align="left">
																	&nbsp;
																</td>
																<td class="TableRow" width="65%" align="center">
																	<bean:message key="field.label" />
																</td>
																<td class="TableRow" width="25%" align="center">
																	<bean:message key="label.query.baseDesc" />
																<td>
															</tr>
														</table>
													</div>
												</td>
											</tr>
										</table>
									</td>
									<td width="4%" align="center">
										<html:button styleClass="mybutton vButtonmargin" property="b_up"
											onclick="upSort();">
											<bean:message key="button.previous" />
										</html:button>
										<br>
										<html:button styleClass="mybutton" property="b_down"
											onclick="downSort();">
											<bean:message key="button.next" />
										</html:button>
									</td>
								</tr>
							</table>
						</td>

					</tr>
				</table>


			</td>
		</tr>

		<tr height="35px">
			<td align="center">
				<html:button styleClass="mybutton" property="b_next" onclick="sub()">
					<bean:message key="button.ok" />
				</html:button>
				<html:button styleClass="mybutton" property="b_return"
					onclick="window.close();">
					<bean:message key="button.cancel" />
				</html:button>
			</td>
		</tr>
</table>
<input type="hidden" name="sortitemid"> 
<html:hidden name="templateListForm" property="sortitem"/>
<html:hidden name="templateListForm" property="tabid"/>


<script language="javascript">
var bt=document.getElementById("b_next");
bt.disabled="disabled";	
defField();

</script>

</html:form>


