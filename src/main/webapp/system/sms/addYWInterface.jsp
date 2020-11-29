<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link rel="stylesheet" href="/css/tabpane.css" type="text/css">
<hrms:themes></hrms:themes>
	

<html:form action="/system/sms/interface_param_yw" styleId="form1">

 <table width="538" border="0" cellpadding="0" cellspacing="0" align="center" class="ftable">
   <tr height="20">
    <td align="left" class="TableRow" colspan="2"><bean:message key="system.sms.ywinterface"/>&nbsp;</td>
           	      
   </tr> 
       <tr class="list3">
           <td align="right" nowrap ><bean:message key="system.sms.ywcode"/></td>
           <td align="left" nowrap >
           		
           		<logic:equal name="interParamForm" property="isUpdate" value="1">
              		<html:text name="interParamForm" property="ywCode" readonly="true" size="40" maxlength="400" styleClass="text" styleId="ywCode"></html:text>
              	</logic:equal>
              	<logic:notEqual name="interParamForm" property="isUpdate" value="1">
              		<html:text name="interParamForm" property="ywCode" size="40" maxlength="400" styleClass="text" styleId="ywCode"></html:text>
              	</logic:notEqual>
              </td>
             </tr>
             <tr class="list3">
                	      <td align="right" nowrap valign="top"><bean:message key="system.sms.ywdesc2"/></td>
                	      <td align="left"  nowrap>
                	      	<html:textarea  name="interParamForm" property="ywDesc" cols="55" rows="10" style="height:180px;"></html:textarea>
                          </td>
              </tr>                 
              <tr class="list3">
                	      <td align="right"  nowrap>
                              <bean:message key="system.sms.ywclasses"/>
                        </td>
                	       <td align="left"  nowrap>                          
			 	             <html:text name="interParamForm" property="ywClasses" size="40" maxlength="400" styleClass="text" styleId="ywClasses"/>       
                      </td>				                   
               </tr> 
                   <tr class="list3">
                	     <td align="right" nowrap><bean:message key="system.sms.ywable2"/></td>
                	       <td align="left"  nowrap>  
                	       	<!-- 添加id，非IE不支持id和name混用  guodd 2019-03-23 -->
                	       	<html:hidden styleId="ywStatus" name="interParamForm" property="ywStatus" />
                	       	<logic:equal name="interParamForm" property="ywStatus" value="1">
                	       	<input type="checkbox" name="ablecheck" id="ablecheck" value="1" checked="checked" />  
                	       	</logic:equal>
                	       	
                	       	<logic:notEqual name="interParamForm" property="ywStatus" value="1">
                	       	<input type="checkbox" name="ablecheck" id="ablecheck" value="1"/>  
                	       	</logic:notEqual>
                	           
                      </td>				                   
               </tr>  
                                    

                                                  
          <tr class="list3">
            <td align="center" colspan="2" style="height:35px">
            	<input type="button" name="saveadd" class="mybutton" value="<bean:message key="button.save"/>" onclick="addSave()"/> 
            	&nbsp;&nbsp;
            	<input type="button" name="saveadd" class="mybutton" value="<bean:message key="button.leave"/>" onclick="returnSelect()"/>
            	
            </td>
          </tr>          
      </table>
  
</html:form>

<script type="text/javascript">
<!--
	var succ = true;
	function addSave() {
		var ywClasses = document.getElementById("ywClasses");
		var ywCode = document.getElementById("ywCode");
		var ablecheck = document.getElementById("ablecheck");
		if (ablecheck.checked == true) {
			document.getElementById("ywStatus").value = "1";
		} else {
			document.getElementById("ywStatus").value = "0";
		}
		if (trimStr(ywClasses.value) == "" || trimStr(ywCode.value) == "") {
			alert("业务代码和业务类不能为空！");
			return false;
		}
		
		if ("1" !="<bean:write name="interParamForm" property="isUpdate"/>") {
			var hashvo=new ParameterSet();
	    hashvo.setValue("opt","check");	
	    hashvo.setValue("code",ywCode.value);
	    var request=new Request({method:'post',asynchronous:false,onSuccess:showd,functionId:'1010020220'},hashvo);
		}
		
		if (!succ) {
			alert("业务代码已存在!");
			return false;
		} else {
		
			var formm = document.getElementById("form1")
			formm.action="/system/sms/interface_param_yw.do?b_update=link&opt=addSave&isUpdate="+"<bean:write name="interParamForm" property="isUpdate"/>";
			formm.submit();
		}
	}
	function showd(outparamters) {
		var exist=outparamters.getValue("exist");
		if (exist == "1") {
			succ = false;
		} else {
			succ = true;
		}
	}
	function returnSelect() {
		var formm = document.getElementById("form1")
		formm.action="/system/sms/interface_param_yw.do?b_query=link&opt=select";
		formm.submit();
	}
	
	function selectCheck() {
	
	}
//-->
</script>

