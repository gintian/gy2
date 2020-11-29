<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<style type="text/css"> 
.gztable {
 	border-right:#7b9ebd 1px solid;
 	border-left:#7b9ebd 1px solid;
 	border-top:#7b9ebd 1px solid;
 	border-bottom:#7b9ebd 1px solid;
 	word-break: break-all; 
 	word-wrap:break-word;
}
.scroll_box {
	border: 1px solid #eee;
 	border-bottom:#7b9ebd 1px solid;
	height: 380px;    
	width: 325px;            
	overflow: auto;            
	margin: 1em 1;
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
<html:form action="/general/template/target_viewLock">
<table width="352" border="0" align="center" Style="padding:0;" cellpadding="0" class="viewLockmargin">
  <tr>	
    <td valign="top">
    	<fieldset style="width:100%;">
    	<legend><bean:message key='infor.menu.lock'/></legend>
    	<table width="100%" height="100%" border="0" align="center">
    		<tr>	
    			<td>
    			<div id="scroll_box" class="scroll_box">${templateListForm.lock_table}</div></td>
  			</tr>
    	</table>
    	</fieldset>
    </td>
  </tr>
  
   <tr  height="35">	
      <td valign="middle">
    	<table border="0" align="center" width="100%">
    		<tr>	
    			<td  align="center">
    			<input type="button" value="确定" onclick="saveHide();" Class="mybutton">  		
    			<input type="button" value="取消" onclick="window.close();" Class="mybutton">
    			</td>
  			</tr>
  	
    	</table>
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
function selectFresh(targetid){
	var tablevos=document.getElementsByTagName("select");
	var value="";
	for(var i=0;i<tablevos.length;i++){
		if(tablevos[i].name==targetid){
		value=tablevos[i].value;
		break;
		}
	    
     }
 	var hashvo=new ParameterSet();
     hashvo.setValue("targetid",targetid);
      hashvo.setValue("value",value);
     hashvo.setValue("tabid",'${templateListForm.tabid}');
  //   hashvo.setValue("templateSetList",'${templateListForm.templateSetList}');
     hashvo.setValue("fieldSetSortStr",'${templateListForm.fieldSetSortStr}');
	 var request=new Request({method:'post',asynchronous:false,onSuccess:select_ok,functionId:'0570040036'},hashvo);		


}   

function select_ok(outparamters){

	var infotable = outparamters.getValue("infotable");
	//alert(document.getElementById("scroll_box").tagName);
	//AjaxBind.bind(document.getElementById("scroll_box"),infotable);
	//document.getElementById("scroll_box").display="none";
	document.getElementById("scroll_box").innerHTML=infotable;
} 
function saveHide(){
	var hirecontent="";
	var viewcontent="";
	var tablevos=document.getElementsByTagName("select");
	for(var i=0;i<tablevos.length;i++){
	     if(tablevos[i].value=='1'){
	      	hirecontent +=tablevos[i].name+",";
		 }else{
		 	viewcontent +=tablevos[i].name+",";
		 }
     }
     var hashvo=new ParameterSet();
     hashvo.setValue("hirecontent",hirecontent);
     hashvo.setValue("viewcontent",viewcontent);
     hashvo.setValue("tabid",'${templateListForm.tabid}');
	 var request=new Request({method:'post',asynchronous:false,onSuccess:check_ok,functionId:'0570040026'},hashvo);		
}
</script>
</html:form>


