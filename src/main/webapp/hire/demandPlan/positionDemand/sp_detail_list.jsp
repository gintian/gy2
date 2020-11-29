<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.constant.SystemConfig" %>
<script type="text/javascript">
<!--
//-->
</script>
<%
 String aurl = (String)request.getServerName();
    String port=request.getServerPort()+"";
    //String prl=request.getProtocol();
   // int idx=prl.indexOf("/");
   // prl=prl.substring(0,idx);
    //String url_p=prl+"://"+aurl+":"+port;
    String url_p=SystemConfig.getServerURL(request);
 %>
<html:form action="/hire/demandPlan/positionDemand/auto_logon_sp">
 <table width="640px" height="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable" style="margin-left: 0px;margin-top:10px;">    
    <tr>
       <td colspan="2" align="left" class="TableRow" nowrap>审批流程&nbsp; </td>
    </tr>
    <tr> 
        <td class="RecordRow" align="center" colspan="2">
              <textarea name="reasons" cols="80" rows="20" readOnly>${positionDemandForm.reasons}</textarea>
        </td>
    </tr>
    <tr>
        <td align="center">
            <input type="button" name="clo" class="mybutton" style="margin-top: 5px;"value="<bean:message key="button.close"/>" onclick="window.close();"/>
        </td>
   </tr>
</table>
</html:form>