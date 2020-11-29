<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<style type="text/css"> 
#dis_hide_table {
           border: 1px solid #eee;
           height: 240px;    
           width: 330px;            
           overflow: auto;            
           margin: 1em 1;
}
</style>
<script language="JavaScript">
function check_ok(){
	var thecontent="";
	var tablevos=document.getElementsByTagName("select");
	for(var i=0;i<tablevos.length;i++){    
	      thecontent +=tablevos[i].name+","+tablevos[i].value+"/";
     }
   	var hashVo=new ParameterSet();
	hashVo.setValue("hidefield",thecontent);
    var request=new Request({method:'post',asynchronous:false,functionId:'3020091014'},hashVo);	
    window.returnValue="refresh";		
	window.close();		
}
function bretrun(){
    window.close();		    
}
//如果是ie6
function ie6Style(){
	if(isIE6()){
		document.getElementById('tableId').style.cssText="margin-top:-3px;margin-left=-5px;";
	}
}
</script>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<hrms:themes />
<html:form action="/gz/gz_accounting/tax/hide_tax_table">
<table id="tableId" border="0" align="center"  style="width:345px;margin-left:-5px;margin-top:-5px;">
  <tr>	
  <td align="center">
    <fieldset style="height:250">
	<legend><bean:message key='infor.menu.hide'/></legend>
    <div id="dis_hide_table" class="complex_border_color">
    	<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0">
            <tr class="fixedHeaderTr">      
               <td align="center" class="TableRow" style="border-top:none;border-left:none;" nowrap><bean:message key='field.label'/>&nbsp;</td>  
               <td align="center" class="TableRow" style="border-top:none;border-left:none;border-right:none;" nowrap><bean:message key='label.zp_resource.status'/>&nbsp;</td>    
            </tr>               
    		<logic:iterate id="element" name="taxTableForm" property="hidefieldlist">
    		<tr class='trShallow'  >
    		<td align='center' width="70%" class='RecordRow' style="border-top:none;border-left:none;" nowrap>
    		<bean:write name="element" property="label"/>
    		</td>
    		<td align='center' width="30%"  class='RecordRow' style="border-top:none;border-left:none;border-right:none;" nowrap >
    		<select name="${element.name}">
		    <logic:equal value="true"  name="element" property="visible">
		    <option value="0" selected="selected" ><bean:message key='lable.channel.visible'/></option>
		    <option value="1" ><bean:message key='lable.channel.hide'/></option>
		    </logic:equal>
			    <logic:notEqual value="true"  name="element" property="visible">
		    	<logic:equal value="false"  name="element" property="visible">
		    	<option value="0" ><bean:message key='lable.channel.visible'/></option>
				<option value="1" selected="selected" ><bean:message key='lable.channel.hide'/></option>
		    	</logic:equal>	    	
			    </logic:notEqual>
		    </select>
    		</td>
    		</tr>
    		</logic:iterate>
    	</table>
    	</div>
    	</fieldset>
  </td>
    

  </tr>
</table>

        <table border="0" cellspacing="0" align="center" cellpadding="0">
		<tr height="35" align="center">
    	<td align="center">
    	<input type="button" name="button_ok" value="<bean:message key='reporttypelist.confirm'/>" onclick="check_ok();" Class="mybutton" style="width:40" >
    	<input type="button" name="button_no" value="<bean:message key='button.cancel'/>" onclick="bretrun();" Class="mybutton" style="width:40" >
    	</td>
    	</tr>
		</table>

</html:form>
<script language="JavaScript">
ie6Style();
</script>

