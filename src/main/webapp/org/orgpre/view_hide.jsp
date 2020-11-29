<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>

<hrms:themes></hrms:themes>
<style type="text/css"> 
#scroll_box {
   <%-- border: 1px solid #eee;
    height: 300px;    
    width: 300px;            
    overflow: auto;            
    margin: 1em 1; --%>
    height: 300px;    
    width: 300px;            
    overflow: auto; 
    border-collapse:collapse; 
}
</style>
<html:form action="/org/orgpre/orgpretable">
<div class="fixedDiv3">
<table width="100%" border="0" align="center">
  <tr>	
    <td valign="top" align="center">
    	<div id="scroll_box" style="margin-bottom:0px;width: 100%;">
    	<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTableF">
            <tr>      
               <td align="center" class="TableRow" style="border-left:0px;" nowrap><bean:message key='general.defini.target'/>&nbsp;</td>  
               <td align="center" class="TableRow" style="border-right:0px;" nowrap><bean:message key='column.warn.valid'/>&nbsp;</td>    
            </tr>                        
    		${orgPreForm.viewhide }
    	</table>
    	</div>
    </td>
  </tr>
</table>
<div style="padding-top:5px;" align="center">
			  <input type="button" name="button_ok" value=" <bean:message key='kq.register.kqduration.ok'/> " onclick="check_ok();" Class="mybutton">
			  <input type="button" name="button_no" value=" <bean:message key='kq.register.kqduration.cancel'/> " onclick="top.close();" Class="mybutton">
			</div>
<script language="JavaScript">
function check_ok(){
	var thecontent="";
	var tablevos=document.getElementsByTagName("select");
	for(var i=0;i<tablevos.length;i++){
	     thecontent +=","+tablevos[i].value;
     }
    var setname = "${orgPreForm.setid}";
	var hashvo=new ParameterSet();
	hashvo.setValue("items",thecontent.substring(1));  
	hashvo.setValue("setid",setname);  
	var request=new Request({asynchronous:false,functionId:'0401000047'},hashvo);
	if(navigator.appName.indexOf("Microsoft")!= -1){ 
	    window.returnValue = thecontent;
	    window.close();
    }else{
    	top.returnValue = thecontent;
	    top.close();
    }
}
</script>
</div>
</html:form>


