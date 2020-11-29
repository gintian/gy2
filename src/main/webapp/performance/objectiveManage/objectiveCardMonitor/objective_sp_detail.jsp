<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.constant.SystemConfig,
com.hrms.struts.constant.WebConstant,
com.hrms.struts.valueobject.UserView" %>
<%
   String tt4CssName="ttNomal4";
   String tt3CssName="ttNomal3";
   String buttonClass="mybutton";
   if(SystemConfig.getPropertyValue("clientName").equalsIgnoreCase("zglt"))
   {
      tt4CssName="tt4";
      tt3CssName="tt3";
      buttonClass="mybuttonBig";
   }
   	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String hcmflag="";
	if(userView != null){
	  hcmflag=userView.getBosflag();
	}
 %>
<script type="text/javascript">
   function closeWin(){
        if (window.showModalDialog) {
            parent.window.close();
        }else{
            var win = parent.parent.Ext.getCmp("show_detail_window");
            if (win) {
                win.close();
            }
        }
   }
</script>
<style>
    html{
        overflow: hidden;
    }
</style>
<link href="/performance/objectiveManage/objectiveCard/objectiveCard.css" rel="stylesheet" type="text/css">
<html:form action="/performance/objectiveManage/objectiveCardMonitor/objective_state_list">
<%if("hl".equals(hcmflag)){ %>
<br>
<%} %>
  <table width="580px" height="370"   align="center"> 
			<tr> <td class="framestyle" valign="top"  align='center'>
				  <div style="overflow:auto;width:500px;height:300px;" >
				<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="DetailTable">

<tr>
<td align="left">
 <font class='<%=tt4CssName%>'>${setUnderlingObjectiveForm.a0101}的审批流程明细</font>
</td>
</tr>
<tr>
<td align="center">
<html:textarea property="objectSpDetailInfo" name="setUnderlingObjectiveForm" cols="68" rows="13" ></html:textarea>
</td>
</tr>
</table>
</div>
</td>
</tr>
<tr>
<td align="center">
<input type="button" name="clo" class="<%=buttonClass%>" value="<bean:message key="button.return"/>" onclick="closeWin();"/>
</td>
</tr>
</table>
</html:form>