<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<hrms:themes></hrms:themes>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<style type="text/css"> 
#dis_hide_emp_table {
           border: 1px solid #eee;
           height: 260px;    
           width: 320px;           
           overflow: auto;            
           margin: 1em 1;
}
.fixedHeaderTr{
 	border:1px solid;
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
<script language="JavaScript" >

function save()
{
	
	var tablevos;
	var hidefieldstr="";
	var fieldsetid = "${mInformForm.setname}";
	tablevos=document.getElementsByTagName("SELECT");   
	for(var i=0;i<tablevos.length;i++){
      	var setname=tablevos[i].name;
      	hidefieldstr = hidefieldstr+setname+","+tablevos[i].value+"/"
    }    	
    var hashvo=new ParameterSet();
	hashvo.setValue("hidefieldstr",hidefieldstr);  
	hashvo.setValue("setname",fieldsetid);  
   	var request=new Request({asynchronous:false,functionId:'1010094211'},hashvo); 
//   	alert(hidefieldstr);
	window.returnValue = "ok";
    window.close();
	
}

</script>


<html:form action="/general/inform/emp/view/hidefield"> 
<div class="fixedDiv3">
<table align="center" width="100%">
<tr>
<td >
	<fieldset style="width:100%;height:250">
	<legend><bean:message key='infor.menu.hide'/></legend>
	<table border="0" cellspacing="0" align="left" cellpadding="0" style="padding-left:5px;padding-right:5px;">
	<tr>
	<td>
		<div id="dis_hide_emp_table" class="common_border_color">
		<table width="100%" border="0" cellspacing="0" cellpadding="0" align="center" style="border-collapse: collapse;">
		<tr class="fixedHeaderTr" height="20">
		<td class="TableRow myleft mytop" nowrap ><bean:message key='field.label'/>&nbsp;
		</td>
		<td class="TableRow myleft mytop" nowrap style="border-right: none;"><bean:message key='label.zp_resource.status'/>&nbsp;
		</td>
		</tr>
		<bean:define id="hidefieldlist" name="mInformForm" property="hidefieldlist"/>
	    <logic:iterate id="element" name="mInformForm" property="hidefieldlist">
	    <tr class='trShallow'>	    
	    <td align='center' class='RecordRow myleft'  width="65%" nowrap>
		<bean:write name="element" property="label"/>
	    </td>
	    <td align='center' class='RecordRow' width="35%" nowrap style="border-right: none;">

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
	</td>
	</tr>
	</table>
	</fieldset>
</td>
</tr>
</table>
<div align="center">
	<button name="savechange" class="mybutton" onclick="save();" style="width:35" style="font-size:10pt">
		 <bean:message key="button.save"/></button>
    <button name="return" class="mybutton" onclick="window.close();" style="width:35" style="font-size:10pt">
    	 <bean:message key="button.cancel"/></button>
</div>
</div>
</html:form>
