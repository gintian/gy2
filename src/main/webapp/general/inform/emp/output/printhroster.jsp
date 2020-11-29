<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<style type="text/css"> 
.btn3 {
 BORDER-RIGHT: #C0C0C0 1px solid;
 BORDER-TOP: #C0C0C0 1px solid; 
 PADDING-LEFT: 0px; FONT-SIZE: 12px; 
 BORDER-LEFT: #C0C0C0 1px solid; 
 COLOR: #808080; 
 PADDING-TOP: 0px; 
 PADDING-BOTTOM: 0px;
 BORDER-BOTTOM: #C0C0C0 1px solid;
  border:#0042A0 1px solid;
 background-image:url(/images/button.jpg);	
}
</style>
<script language='javascript'>
function reloadBySetId(setobj){
   var setname="";
   for(i=0;i<setobj.options.length;i++){
      if(setobj.options[i].selected){
    	setname=setobj.options[i].value;
    	break;
      }   
   }
   document.getElementById("hid").innerHTML=setname;
}
function outStyleHRoster(){
	hmusterForm.action="/general/muster/hmuster/select_muster_name.do?b_next=next&checkflag=1"; 
   	hmusterForm.submit();
}
</script>
<html:form action="/general/inform/emp/output/printhroster">
<table width="100%" border="0">
<tr><td>&nbsp;</td></tr>
</table>
<fieldset align="center" style="width:90%;">
<legend><bean:message key="hmuster.label.info"/></legend>
<table width="100%" border="0">
  <tr> 
    <td align="center">
      <html:select name="hmusterForm"  multiple="multiple" onclick="reloadBySetId(this);" property="tabID" style="height:200px;width:100%;font-size:9pt"> 
      	<html:optionsCollection property="hmusterlist" value="dataValue" label="dataName"/> 
      </html:select>
   </td>
  </tr>
</table>
</fieldset>
<table width="100%" height="30" border="0">
<tr> 
	<td height="30" width="10">&nbsp;</td>
    <td height="30" valign="top" width="50"><bean:message key="general.inform.search.table.id"/>：</td>
	<td height="30" valign="top" width="50"><div id="hid" style="font-size:9pt">&nbsp;</div></td>
    <td height="30" align="right"> 
      <table width="35%" border="0">
        <tr> 
          <td align="center"> 
          	<input type="button" name="Submit"  class="mybutton" onclick="outStyleHRoster();" value="<bean:message key='button.open'/>">
          </td>
          <td align="center">
          	<input name="Submit2" onclick="window.close();" class="mybutton" type="button" value="<bean:message key='button.close'/>"></td>
        </tr>
      </table></td>
  </tr>
</table>
</html:form>
