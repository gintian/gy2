<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="JavaScript">
function saveOk(){
	var r3122 = document.getElementById("r3122").value;
	r3122=getEncodeStr(r3122);
	var hashvo=new ParameterSet();
	hashvo.setValue("r3122",r3122);
	hashvo.setValue("r3101","${courseTrainForm.r3101}");
	var request=new Request({method:'post',asynchronous:false,onSuccess:showfile,functionId:'2020040004'},hashvo);
}
function showfile(outparamters){
	var infor = outparamters.getValue("infor");
	if(infor=='ok'){
		alert("<bean:message key='label.posbusiness.success'/>！");
		return false;
	}
}
</script>
<Style>
.textarea{
	border: 1 solid #C4D8EE;
}
</Style>
<hrms:themes></hrms:themes>
<html:form action="/train/request/trainsData">
<table width="100%" border="0">
 <logic:equal name="courseTrainForm" property="r3127" value="03">
    <tr> 
        <td>
        	<hrms:priv func_id="3233060">
        	<input type="button" value="<bean:message key='kq.add_feast.save'/>" class="mybutton" onclick="saveOk();">
        	</hrms:priv>
        </td>
    </tr>
    </logic:equal>
    <logic:equal name="courseTrainForm" property="r3127" value="09">
    <tr> 
        <td>
        	<hrms:priv func_id="3233060">
        	<input type="button" value="<bean:message key='kq.add_feast.save'/>" class="mybutton" onclick="saveOk();">
        	</hrms:priv>
        </td>
    </tr>
 </logic:equal>
	<tr> 
        <td> 
        	<logic:equal name="courseTrainForm" property="r3127" value="03">
            	<html:textarea name="courseTrainForm" property="r3122" styleClass="textarea common_border_color" cols="105" rows="6" styleId="shry" style="width:100%;"></html:textarea> 
            </logic:equal>
            <logic:equal name="courseTrainForm" property="r3127" value="09">
            	<html:textarea name="courseTrainForm" property="r3122" styleClass="textarea common_border_color" cols="105" rows="6" styleId="shry" style="width:100%;"></html:textarea> 
            </logic:equal>
            <logic:notEqual name="courseTrainForm" property="r3127" value="03">
            	<logic:notEqual name="courseTrainForm" property="r3127" value="09">
            	<html:textarea name="courseTrainForm" property="r3122" styleClass="textarea common_border_color" cols="105" rows="7" readonly="true" styleId="shry" style="width:100%;"></html:textarea> 
           		 </logic:notEqual>
            </logic:notEqual>
        </td>
    </tr>
</table>
</html:form>
<script language='javascript' >			
	parent.parent.ril_body1.setSecondPage(1)			
</script>