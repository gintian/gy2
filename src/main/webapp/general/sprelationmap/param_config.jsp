<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="javascript" src="/general/sprelationmap/relationMap.js"></script>
<script language="JavaScript" src="/js/calendar.js"></script>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<SCRIPT LANGUAGE=javascript src="/js/page_options_color.js"></SCRIPT> 
<script type="text/javascript">
<!--
<%if(request.getParameter("isClose")!=null&&request.getParameter("isClose").equals("1")){%>
 returnValue="1";
 window.close();
<%}%>
var DisColor,HexColor;
function init(){
  DisColor=document.getElementsByName("DisColor")[0];
  HexColor=document.getElementsByName("HexColor")[0];
}
//-->


</script>
<style>
</style >
<body scroll=no style="padding-right:5px;">
<html:form action="/general/sprelationmap/param_config.do">
<table align="center" border="0" cellpadding="0" cellspacing="0" class="ListTable" width="90%">
<tr>
<td align="left">
<fieldset>
<legend><bean:message key="general.inform.org.graph"/></legend>
<table>
<tr>
<td align="left">&nbsp;
<bean:message key="general.sprelation.direction"/>&nbsp;

<html:radio name="relationMapForm" property="chartParam.direction" value="1"/><bean:message key="kq.register.daily.lrtypeline"/>
<html:radio name="relationMapForm" property="chartParam.direction" value="2"/><bean:message key="kq.register.daily.lrtyperow"/>
</td> 
</tr>
<tr>
<td align="left">&nbsp;
<bean:message key="general.sprelation.nodeshape"/>&nbsp;

<html:radio name="relationMapForm" property="chartParam.shape" value="rectangle"/><bean:message key="general.inform.org.cellrectshape"/>
<html:radio name="relationMapForm" property="chartParam.shape" value="circle"/><bean:message key="general.sprelation.circle"/>
</td> 
</tr>
</table>
</fieldset>
</td>
</tr>

<tr>
<td align="left">
<fieldset>
<legend><bean:message key="general.sprelation.node"/></legend>
<table>
<tr><td>
<bean:message key="general.inform.org.cellhspacewidth"/>&nbsp;<html:text property="chartParam.lr_spacing" size="4" maxlength="3" styleClass="TEXT4"/>

<bean:message key="general.sprelation.nodewidth"/>&nbsp;<html:text property="chartParam.width" size="4" maxlength="3" styleClass="TEXT4"/>

<bean:message key="general.sprelation.borderwidth"/>&nbsp;<html:text property="chartParam.border_width" size="4" maxlength="3" styleClass="TEXT4"/>

</td></tr>
<tr><td>
<bean:message key="general.inform.org.cellvspacewidth"/>&nbsp;<html:text property="chartParam.tb_spacing" size="4" maxlength="3" styleClass="TEXT4"/>

<bean:message key="general.sprelation.nodeheight"/>&nbsp;<html:text property="chartParam.height" size="4" maxlength="3" styleClass="TEXT4"/>
<bean:message key="general.sprelation.radius"/>&nbsp;<html:text property="chartParam.radius" size="4" maxlength="3" styleClass="TEXT4"/>

 <bean:message key="kq.item.color"/>&nbsp;
<html:text  name="relationMapForm" property="chartParam.bgColor" alt="clrDlg" size="6" style="BACKGROUND-COLOR:${relationMapForm.chartParam.bgColor}"  styleClass="textColorWrite" readonly="true"/>

</td>
</tr>
</table>
</fieldset>
</td>
</tr>

<tr>
<td align="left">
<fieldset>
<legend><bean:message key="general.inform.org.fontfamily"/></legend>
<table>
<tr>
 <td>
 <bean:message key="general.inform.org.fontfamily"/>&nbsp;
 <hrms:optioncollection name="relationMapForm" property="chartParam.fontNameList" collection="list" />
						 <html:select name="relationMapForm" property="chartParam.fontName" size="1"  style="width:150px; border:1px red solid;">
				             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
				        </html:select>
 
 
 <bean:message key="general.inform.org.fontsize"/>&nbsp;<html:text property="chartParam.fontSize" size="4" maxlength="3" styleClass="TEXT4"/>
 </td>
</tr>
</table>
</fieldset>
</td>
</tr>

<tr>
<td align="left">
<fieldset>
<legend><bean:message key="general.inform.org.person"/></legend>
<table>
<tr>
<td valign="top">
<bean:message key="general.sprelation.nodedescription"/>&nbsp;<html:textarea property="chartParam.desc_items_desc" styleId="desc_items_desc" readonly="true"  cols="40" rows="4"></html:textarea>
<input type="button" name="sss" value="..."  class="mybutton" onclick="selectItem('desc_items');">
<html:hidden styleId="desc_items" property="chartParam.desc_items"/>
</td>
</tr>
<tr>
<td valign="top">
<bean:message key="general.sprelation.nodehint"/>&nbsp;<html:textarea property="chartParam.hint_items_desc" styleId="hint_items_desc" readonly="true" cols="40" rows="4"></html:textarea>
<input type="button" name="sss" value="..."  class="mybutton" onclick="selectItem('hint_items');">
<html:hidden styleId="hint_items" property="chartParam.hint_items"/>
</td>
</tr>
<tr>
<td><bean:message key="general.sprelation.viewphoto"/>&nbsp;
<html:multibox name="relationMapForm" property="chartParam.show_pic" value="true"/>
</td>
</tr>
</table>
</fieldset>
</td>
</tr>
</table>
<tr height="30px">
<td align="center">
<input type="button" class="mybutton" name="save" value="<bean:message key="button.save"/>" onclick="saveParam();"/>
<input type="button" class="mybutton" name="cancel" value="<bean:message key="button.cancel"/>" onclick="relationClose();"/>
</td>
</tr>
</table>
<div id="colorpanel" style="position:absolute;display:none;width:253px;height:177px;z-index:50"></div> 
</html:form>
</body>
<script language="javascript">
window.setTimeout('init()',1000);   
</script>