<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>


<html:form action="/kq/options/sign_point/setsign_point">
<table align="center" >
  <tr>
     <td align="center" valign="bottom" height=120>
          <fieldset>
              <legend>参数设置</legend>
              <br>
              &nbsp;&nbsp;&nbsp;&nbsp;有效范围&nbsp;<html:text styleClass="inputtext" property="pointRadius" name="kqSignPointForm" onkeyup="this.value=this.value.replace(/\D/g,'')" />&nbsp;&nbsp;&nbsp;米&nbsp;&nbsp;&nbsp;&nbsp;
              <br><br>
          </fieldset>
     </td>
  </tr>
  <tr>
    <td align="center"><button class="mybutton" onclick="saveArguments()">确定</button></td>
  </tr>
</table>
</html:form>

<script>
    function saveArguments(){
    	
    	var pointRadius = kqSignPointForm.pointRadius.value;
    	var hashvo=new ParameterSet();
 	    hashvo.setValue("pointRadius",pointRadius);	
 	    var request=new Request({asynchronous:false,onSuccess:saveOk,functionId:'151211001126'},hashvo);  
    }
    function saveOk(out){
    	window.close();
    }
    
</script>