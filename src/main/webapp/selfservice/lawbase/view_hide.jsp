<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>

<style type="text/css"> 
.scroll_box {
    height: 400px;    
    width:100%;            
    overflow: auto;
    padding-left: 0;
    padding-right: 0;
}
.myleft
{
	border-left: none;
}
.mytop
{
	border-top: none;
}
.myright
{
 	border-right:none; 
}
</style>
<div class="fixedDiv3">
<html:form action="/selfservice/lawbase/law_maintenance">
	<div class="scroll_box complex_border_color" >
		<table width="100%" border="0" cellspacing="0" cellpadding="0" style="border-collapse: collapse;">
	        <tr>      
	           <td align="center" class="TableRow mytop myleft" nowrap><bean:message key='general.defini.target'/>&nbsp;</td>  
	           <td align="center" class="TableRow mytop myright" nowrap><bean:message key='column.warn.valid'/>&nbsp;</td>    
	        </tr>                        
			${lawbaseForm.viewhide }
		</table>
	</div>
	<div style="margin-top: 5px" align="center">
	  <input type="button" name="button_ok" value=" <bean:message key='kq.register.kqduration.ok'/> " onclick="check_ok();" Class="mybutton">
	  <input type="button" name="button_no" value=" <bean:message key='kq.register.kqduration.cancel'/> " onclick="top.close();" Class="mybutton">
	</div>
</html:form>
</div>
<script language="JavaScript">
function check_ok(){
	var thecontent="";
	var tablevos=document.getElementsByTagName("select");
	for(var i=0;i<tablevos.length;i++){
	     thecontent +=","+tablevos[i].value;
     }
    var basetype = "${lawbaseForm.basetype}";
	var hashvo=new ParameterSet();
	hashvo.setValue("items",thecontent+",");  
	hashvo.setValue("basetype",basetype);  
	var request=new Request({asynchronous:false,functionId:'10400201053'},hashvo);
	if(navigator.appName.indexOf("Microsoft")!= -1){ 
	    window.returnValue = thecontent+",";
	    window.close();
    }else{
    	top.returnValue = thecontent+",";
	    top.close();
    }
}
</script>



