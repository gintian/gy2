<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>

<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script type="text/javascript">
<!--
function sub()
{
  var obj=document.getElementById("sf");
  var sortids="";
  if(obj)
  {
     for(var i=0;i<obj.options.length;i++)
     {
        sortids+=","+obj.options[i].value;
     }
  }
  var hashVo=new ParameterSet();
  hashVo.setValue("sortids",sortids);
  hashVo.setValue("bank_id","${bankDiskForm.bank_id}");
  var In_parameters="opt=1";
  var request=new Request({method:'post',asynchronous:false,parameters:In_parameters,onSuccess:sort_ok,functionId:'3020100024'},hashVo);			
		 
}
function sort_ok(outparameters)
{
  var ids=outparameters.getValue("ids");
  var obj=new Object();
  obj.rightField=ids.substring(1);
  returnValue=obj;
  window.close();
}
//-->
</script>
<html:form action="/gz/gz_accounting/bankdisk/sort_template_field">

<table width='100%' border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
<tr>
<td>
<fieldset align="center">
<legend><bean:message key="menu.gz.sortitem"/></legend>
<table><tr><td>
 <hrms:optioncollection name="bankDiskForm" property="sortFieldList" collection="list"/>
		              <html:select styleId="sf" name="bankDiskForm" size="10" property="right_fields" multiple="multiple" style="height:230px;width:90%;font-size:9pt">
		              <html:options collection="list" property="dataValue" labelProperty="dataName"/>
		        </html:select>	
		        </td>
 <td width="10%" align="center" valign="center">
                   <html:button  styleClass="mybutton" property="b_up" onclick="upItem($('right_fields'));">
            		     <bean:message key="button.previous"/> 
	           </html:button >
	           <br>
	           <br>
	           <html:button  styleClass="mybutton" property="b_down" onclick="downItem($('right_fields'));">
            		     <bean:message key="button.next"/>    
	           </html:button >	 

	     </td>
</tr>
</table>
</fieldset>
</td>
</tr>
<tr>
<td height="35px;" align="center">
<input type="button" name="ok" class="mybutton" value="<bean:message key="button.ok"/>" onclick="sub();"/>
<input type="button" name="can" class="mybutton" value="<bean:message key="button.cancel"/>" onclick="window.close();"/> 
</td>
</tr>
</table>
		      
</html:form>