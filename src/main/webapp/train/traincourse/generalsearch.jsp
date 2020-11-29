<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="com.hrms.struts.valueobject.UserView"%>
<%@page import="com.hrms.struts.constant.WebConstant"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<style type="text/css"> 
#strTable{
           border: 1px solid #C4D8EE;
           height: 240px;    
           width: 400px;             
           overflow: auto;            
           margin: 1em 1;
           position:absolute;
}
</style>

<%
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String manager="";
	if(!userView.isSuper_admin())
		manager = userView.getUnitIdByBusi("6");  
	String moduleFlag=request.getParameter("muduleFlag");//高级花名册标记
	if(moduleFlag==null){
		moduleFlag="";
	}
%>
<script type="text/javascript">
var moduleFlag="<%=moduleFlag%>";

<!--
isPriv="1";//"${trainCourseForm.isPriv}";
var manageCode = '<%=manager%>';
//-->
</script>

<script type='text/javascript' src='../../../ext/ext6/ext-all.js'></script>
<script type='text/javascript' src='../../../ext/ext6/locale-zh_CN.js' ></script>
<script type='text/javascript' src='../../../ext/rpc_command.js'></script>
<link rel='stylesheet' href='../../../ext/ext6/resources/ext-theme.css' type='text/css' />
<script language="JavaScript" src="../../../components/codeSelector/codeSelector.js"></script>
<script language="JavaScript" src="./generalsearch.js"></script>

<html:form action="/train/traincourse/generalsearch">
<input type="hidden" name="itemid" id="itemid">
<table width="690px" border="0" align="center" style="margin-left: -5px;">
  
  <tr> 
    <td width="30%" align="center"> 
      <fieldset style="width:92%;">
      <legend><bean:message key='selfservice.query.queryfield'/></legend>
      <table width="100%" border="0" align="center" cellpadding="0" cellspacing="0">
        <tr> 
          <td height="260" style="padding: 3px;"> 
           <html:select name="trainCourseForm" property="item_field" multiple="multiple" ondblclick="additemtr('item_field');" style="height:250px;width:100%;font-size:9pt"> 
            <html:optionsCollection property="fieldlist" value="dataValue" label="dataName"/> 
            </html:select> </td>
        </tr>
      </table>
     </fieldset>
    </td>
    <td width="9%" align="center"> 
            		<input type="button" name="Submit111" value="<bean:message key='button.setfield.addfield'/>" onclick="additemtr('item_field');" Class="smallbutton"> 
          			<input type="button" name="Submit2" onclick="delTableStr();" value="<bean:message key='button.delete'/>" Class="smallbutton" style="margin-top:30px;"> 
    </td>
   <td align="center" height="270" valign="top" cellpadding="0" cellspacing="0">
   		<fieldset style="width:auto;padding-top: 0px;padding-left: 2px;">
      	<legend><bean:message key='general.inform.search.condset'/></legend>
    	<table width="100%" border="0" cellpadding="0" cellspacing="0">
        	<tr> 
         		<td height="260" valign="top" style="padding-top: 0px;">
         			<div id="strTable" class="common_border_color" style="margin-top: 4px;">
         				<table width="100%" id="tablestr" border="0" cellpadding="0" cellspacing="0">
         					<tr>
         						<td align="center" class="TableRow noleft" width="15%" style="border-left: 0px;border-top:none;"><bean:message key='label.query.logic'/></td>
         						<td align="center" class="TableRow" width="30%" nowrap  style="border-left: 0px;border-top:none;"><bean:message key='general.inform.search.item.object'/></td>
         						<td align="center" class="TableRow" width="15%"  style="border-left: 0px;border-top:none;"><bean:message key='label.query.relation'/></td>
         						<td align="center" class="TableRow noright" nowrap  style="border-left: 0px;border-right:none;border-top:none;"><bean:message key='label.query.value'/></td>
         					</tr>
         				</table>
         			</div>
         		</td>
        	</tr>
      	</table>
      	</fieldset>
   </td>
  </tr>
</table>
<table width="100%" border="0" align="center">
	<tr> 
      <td height="25" width="200">
    	<table width="100%" border="0">
    		<tr>
    			<td><input type="checkbox" name="like" id="like"><bean:message key='label.query.like'/></td>
    		</tr>
    	</table>
      </td>
      <td height="25" width="200">&nbsp;
      </td>
      <td height="25">
    	<table width="100%" border="0">
    		<tr>
    			<td width="25%" align="right"><input type="button" name="searchok" value="<bean:message key='button.query'/>" onclick='searchSetCond("${searchInformForm.a_code}","${searchInformForm.tablename}","${searchInformForm.type}");' Class="mybutton"></td>
    			<td width="65%" align="left"><input type="button" name="close" value="<bean:message key='button.cancel'/>" onclick="top.close();" Class="mybutton"></td>
    		</tr>
    	</table>
      </td>
  </tr>
</table>
<div id="date_panel" style="display:none">
	<select name="date_box" multiple="multiple" size="10"  style="width:120" onchange="setSelectValue();" onclick="setSelectValue();">    
		<option value="$YRS[10]"><bean:message key='general.inform.search.years'/></option>
		<option value="<bean:message key='general.inform.search.this.years'/>"><bean:message key='general.inform.search.this.years'/></option>
		<option value="<bean:message key='general.inform.search.this.month'/>"><bean:message key='general.inform.search.this.month'/></option>
		<option value="<bean:message key='general.inform.search.this.day'/>"><bean:message key='general.inform.search.this.day'/></option>				    
		<option value="<bean:message key='general.inform.search.day'/>"><bean:message key='general.inform.search.day'/></option>
		<option value="<bean:message key='kq.wizard.edate'/>"><bean:message key='kq.wizard.edate'/></option>
		<option value="1992.4.12">1992.04.12</option>	
		<option value="1992.4">1992.04</option>	
		<option value="1992">1992</option>			    
		<option value="1992-04-12">1992-04-12</option>
		<option value="1992-04">1992-04</option>			    			    		    
	</select>
</div>
</html:form>