<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>

<style type="text/css"> 
.vButtonmargin {
	margin-bottom: 30px;

}

.scroll_box {
	border: 1px solid;
	height: 360px;    
	width: 320px;            
	overflow: auto;
	margin:0px 5px 5px ;
}
</style>
<%

    UserView userView = (UserView) session.getAttribute(WebConstant.userView);
    String bosflag = "";
    if (userView != null) {
        bosflag = userView.getBosflag();
    }
%>
 <%
     if ("hcm".equals(bosflag)) {
 %>
<link href="/general/template/template.css" rel="stylesheet" type="text/css"/>
<%
    }
%>
<hrms:themes></hrms:themes>
<html:form action="/general/template/target_viewHide">
<table width="353" border="0"  cellpadding="0" cellspaceing="0" class="hidefieldmargin">
  <tr>	
    <td align="center">
    	<fieldset style="width:100%;height:360">
    	<legend><bean:message key='infor.menu.hide'/></legend>
    	<table width="100%" border="0" align="center">
    		<tr>	
    			<td><div id="scroll_box" class="scroll_box">${templateListForm.hire_table}</div></td>
  			</tr>
    	</table>
    	</fieldset>
    </td>
  </tr>
  <tr height="35px" >	      
 
  			<td  align="center" valgin="top" >
   			<input type="button" value="确定" onclick="saveHide();" Class="mybutton">  		
   			<input type="button" value="取消" onclick="window.close();" Class="mybutton">
  			</td>
 
  </tr>
</table>
<script language="JavaScript">
function check_ok(outparamters){
	var info = outparamters.getValue("info");
//	if(info=="ok"){
//		var viewhide = "";
//		var tablevos=document.getElementsByTagName("select");
//		viewhide =tablevos[0].value+","+tablevos[1].value;
		if(info=="")
		info="ok";
		window.returnValue = info;
     	window.close();
//	}
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
       hashvo.setValue("hiddenItem",'${templateListForm.hiddenItem}');
     hashvo.setValue("tabid",'${templateListForm.tabid}');
	 var request=new Request({method:'post',asynchronous:false,onSuccess:check_ok,functionId:'0570040022'},hashvo);		
}
</script>
</html:form>


