<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>

<link rel="stylesheet" href="/css/css1.css" type="text/css">
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="./formula.js"></script>
<style type="text/css">
#scroll_box {
    border: 1px solid #eee;
    height: 250px;    
    width: 230px;            
    overflow: auto;            
    margin: 1em 0;
}
</style>
<hrms:themes />
<html:form action="/gz/gz_amount/countformula">
<html:hidden name="croPayMentForm" property="sortStr" />
<html:hidden name="croPayMentForm" property="year" />
<input type="hidden" name="id"/>
<table width="90%" border="0" align="center">
  <tr>
  	<td width="10%">
  		<table width="100%" border="0">
        <tr> 
          <td height="32" align="center"> 
            <input type="button" name="Submit" value="<bean:message key='kq.shift.cycle.up'/>" 
            onclick="upSort();" Class="mybutton"> 
          </td>
        </tr>
        <tr> 
          <td height="60" >&nbsp; </td>
        </tr>
        <tr> 
          <td height="27" align="center"> 
            <input type="button" name="Submit2" value="<bean:message key='kq.shift.cycle.down'/>" 
            onclick="downSort();" Class="mybutton"> 
          </td>
        </tr>
      </table>
  	</td>
    <td height="260" align="center">
      <fieldset style="width:100%;">
      <legend><bean:message key='kq.item.count'/></legend>
     	<table width="100%" border="0">
        	<tr> 
          		<td height="260" valign="top">
          		<div id="scroll_box">
          			${croPayMentForm.tableStr}
          		</div>
          		</td>
        	</tr>
      	</table>
      	</fieldset>
     	</td>
    	<td width="20%" valign="bottom">
    	<table width="100%" border="0">
        <tr>
          <td height="32" align="center">
			<input type="button" name="Submit32" value="<bean:message key='infor.menu.definition.formula'/>" onclick='setFormula("${croPayMentForm.unit_type}","2","${croPayMentForm.fieldsetid}");' Class="mybutton"></td>
        </tr>
        
         <tr> 
          <td height="32" align="center"> 
            <input type="button" name="Submit" value="<bean:message key='infor.menu.ok1'/>    <bean:message key='infor.menu.ok2'/>" Class="mybutton" onclick='colFormulaOk("${croPayMentForm.unit_type}","${croPayMentForm.fieldsetid}","${croPayMentForm.year}");'> 
          </td>
        </tr>
        <tr> 
          <td height="27" align="center"> 
            <input type="button" name="Submit2" value="<bean:message key='infor.menu.no1'/>    <bean:message key='infor.menu.no2'/>" 
            onclick="window.close();" Class="mybutton"> 
          </td>
        </tr>
      </table>
    	</td>
  </tr>
</table>
<html:hidden name="croPayMentForm" property="unit_type" />
</html:form>
<script language="JavaScript">
defaultSelect("${croPayMentForm.unit_type}");
</script>