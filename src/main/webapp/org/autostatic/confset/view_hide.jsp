<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>

<hrms:themes></hrms:themes>
<style type="text/css"> 
.scroll_box {
    height: 300px;    
    width: 300px;            
    overflow: auto; 
    border-collapse:collapse;           
}
</style>
<html:form action="/org/autostatic/confset/view_hide">
<div class="fixedDiv3">
<table  border="0" align="center" width="100%">
  <tr>	
    <td >
    	<div class="scroll_box" style="width:100%;">
    	<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTableF">
            <tr>      
               <td align="center" style="border-left:0px;" class="TableRow" nowrap><bean:message key='general.defini.target'/>&nbsp;</td>  
               <td align="center" style="border-right:0px;" class="TableRow" nowrap><bean:message key='column.warn.valid'/>&nbsp;</td>    
            </tr>                        
    		${subsetConfsetForm.view_hide}
    	</table>
    	</div>
    </td>
  </tr>
  <tr> 
       <td align="center">
         <input type="button" name="button_ok" value=" <bean:message key='kq.register.kqduration.ok'/> " onclick="check_ok();" Class="mybutton">
         <input type="button" name="button_no" value=" <bean:message key='kq.register.kqduration.cancel'/> " onclick="window.close();" Class="mybutton">
       </td>
  </tr>
</table>
<script language="JavaScript">
function check_ok(){
	var thecontent="";
	var tablevos=document.getElementsByTagName("select");
	for(var i=0;i<tablevos.length;i++){
	     if(tablevos[i].value!='0'){
	      	thecontent +=tablevos[i].value+",";
		 }
     }
    var setname = "${subsetConfsetForm.subset}";
	var hashvo=new ParameterSet();
	hashvo.setValue("hideitemid",thecontent);  
	hashvo.setValue("setid",setname); 
	hashvo.setValue("flag","hide");  
	var request=new Request({asynchronous:false,functionId:'1602010225'},hashvo); 
    window.returnValue = thecontent;
    window.close();
}
</script>
</div>
</html:form>


