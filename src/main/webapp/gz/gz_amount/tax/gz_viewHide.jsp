<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<%@ page import="java.util.*,com.hrms.struts.constant.WebConstant,com.hrms.struts.valueobject.UserView"%>
 <%
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String hcmflag="";
	if(userView != null){
	  hcmflag=userView.getBosflag();
	}
%>
<style type="text/css"> 
.gztable {
 	border-right:#7b9ebd 1px solid;
 	border-left:#7b9ebd 1px solid;
 	border-top:#7b9ebd 1px solid;
 	border-bottom:#7b9ebd 1px solid;
 	word-break: break-all; 
 	word-wrap:break-word;
}

#scroll_box {
    border: 1px solid #eee;
    height: 260px;    
    width: 340px;            
    overflow: auto;            
    margin: 0px 5px 5px ;
}
</style>
<hrms:themes />
<html:form action="/gz/gz_amount/tax/gz_viewHide">
<%if("hl".equals(hcmflag)){ %>
<table width="100%" border="0" align="center">
<%}else{ %>
<table width="100%" border="0" align="center" style="margin-top:-5px;margin-left:-5px;">
<%} %>

  <tr>	
    <td valign="top" >
    	<fieldset style="width:100%;height:250">
    	<legend><bean:message key='infor.menu.hide'/></legend>
    			<div id="scroll_box">${payrollForm.gz_table}</div>
    	</fieldset>
    </td>

  </tr>
</table>

    	<table border="0" align="center" width="100%">
    		<tr>	
    			<td height="20" align="center">
    			<input type="button" value="确定" onclick="saveHide();" Class="mybutton">
    			<input type="button" value="取消" onclick="window.close();" Class="mybutton">
    			</td>
  			</tr>
    	</table>

<script language="JavaScript">
function check_ok(outparamters){
	var info = outparamters.getValue("info");
	if(info=="ok"){
		var viewhide = "";
		var tablevos=document.getElementsByTagName("select");
		viewhide =tablevos[0].value+","+tablevos[1].value;
		window.returnValue = viewhide;
     	window.close();
	}
}
     
function saveHide(){
	var hirecontent="";
	var viewcontent="";
	var tablevos=document.getElementsByTagName("select");
	for(var i=0;i<tablevos.length;i++){
	     if(tablevos[i].value=='0'){
	      	hirecontent +=tablevos[i].name+",";
		 }else{
		 	viewcontent +=tablevos[i].name+",";
		 }
     }
     var hashvo=new ParameterSet();
     hashvo.setValue("hirecontent",hirecontent);
     hashvo.setValue("viewcontent",viewcontent);
     hashvo.setValue("salaryid",'${payrollForm.salaryid}');
	 var request=new Request({method:'post',asynchronous:false,onSuccess:check_ok,functionId:'3020090003'},hashvo);		
}
</script>
</html:form>


