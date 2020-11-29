<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%
String flag = request.getParameter("flag");

 %>
<hrms:themes></hrms:themes>
<style type="text/css">
.btn3 {
 BORDER-RIGHT: #C0C0C0 1px solid;
 BORDER-TOP: #C0C0C0 1px solid; 
 PADDING-LEFT: 0px; FONT-SIZE: 12px; 
 BORDER-LEFT: #C0C0C0 1px solid; 
 COLOR: #808080; 
 PADDING-TOP: 0px; 
 PADDING-BOTTOM: 0px;
 BORDER-BOTTOM: #C0C0C0 1px solid;
 font-size:12px;
line-height:18px;
padding-left:1px;
padding-right:2px;
overflow:visible;
cursor: pointer; 	
margin:0px 7px 0px 0;
}
#scroll_box {
    border: 1px solid #eee;
    height: 220px;    
    width: 230px;            
    overflow: auto;            
    margin: 1em 0;
}
</style>
<html:form action="/org/autostatic/confset/setscandata">

<table width="100%" border="0" align="center">
	<tr>
		
    <td>
    <fieldset align="center" style="width:100%;">
     <legend><bean:message key='org.autostatic.mainp.cal.load.data'/></legend>
     	<table width="100%" height="270" border="0" align="center">
     		<tr>
     			<td valign="top">
     			<div id="scroll_box">
    			${subsetConfsetForm.scan_table}
    			</div>
    			</td>
    		</tr>
    	</table>
    </fieldset>
    </td>	
    <td valign="top"><table   border="0" cellpadding="0" cellspacing="0">
        
        <tr>
          <td  align="center" style="padding-top:9px;"><input type="button" name="button_all" value=" <bean:message key='label.query.selectall'/> " onclick="checkall();" Class="mybutton"></td>
        </tr>
        <tr>
          <td  align="center" style="padding-top:10px;"><input type="button" name="button_no" value=" <bean:message key='label.query.clearall'/> " onclick="checkclear();checkSelect();" Class="mybutton"></td>
        </tr>
        <tr> 
          <td  align="center" style="padding-top:10px;">
          	<span id="viewbutton">
          		<input type="button" name="button_ok" value=" <bean:message key='reporttypelist.confirm'/> " onclick="check_ok();" Class="mybutton">
          	</span>
          	<span id="darkbutton">
          		<input type="button" name="button_ok" value=" <bean:message key='reporttypelist.confirm'/> "  Class="btn3">
          	</span>
          </td>
        </tr>
        <tr>
          <td  align="center" style="padding-top:10px;"><input type="button" name="button_no" value=" <bean:message key='kq.register.kqduration.cancel'/> " onclick="window.close();" Class="mybutton"></td>
        </tr>
      </table></td>
	</tr>
</table>
<script language="JavaScript">
function toggles(targetId){
	if (document.getElementById(targetId)){
		target = document.getElementById(targetId);
		target.style.display = "block";
	}
} 
function hides(targetId){
	if (document.getElementById(targetId)){
		target = document.getElementById(targetId);
		target.style.display = "none";
	}
}
function checkall(){
	var tablevos=document.getElementsByTagName("input");
	for(var i=0;i<tablevos.length;i++){
	     if(tablevos[i].type=="checkbox"){
	     	tablevos[i].checked=true;
		 }
     }
     toggles("viewbutton");
     hides("darkbutton");
}
function checkclear(){
	var tablevos=document.getElementsByTagName("input");
	for(var i=0;i<tablevos.length;i++){
	     if(tablevos[i].type=="checkbox"){
	     	tablevos[i].checked=false;
		 }
     }
}
function check_ok(){
	var thecontent="";
	var tablevos=document.getElementsByTagName("input");
	for(var i=0;i<tablevos.length;i++){
	     if(tablevos[i].type=="checkbox"){
	     	if(!tablevos[i].checked){
	      			continue;
	      	}
	      	thecontent +=tablevos[i].value+",";
		 }
     }
     if(thecontent==null||thecontent.length<1){
     	alert("<bean:message key='org.autostatic.mainp.select.loibrary.staff'/>");
     	return false;
     }
     var hashvo=new ParameterSet();
     hashvo.setValue("view_scan",thecontent);
	 var In_paramters="flag=<%=flag %>"; 
	 var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:showmsg,functionId:'1602010238'},hashvo);
     
}

function showmsg(outparamters){
	var msg=outparamters.getValue('msg');
	if(msg=='ok'){
		window.returnValue = outparamters.getValue('view_scan');
     	window.close();
	}else{
		alert("保存失败!");
	}
}
function checkefirst(){
	var tablevos=document.getElementsByTagName("input");
	var checkselect = "1";
	for(var i=0;i<tablevos.length;i++){
	     if(tablevos[i].type=="checkbox"){
	     	if(tablevos[i].checked){
	     		var checkselect = "0";
	     	}
		 }
     }
     if(checkselect=="1"){
     	tablevos[0].checked=true;
     }
}
function checkSelect(){
	var checks = '';
	var tablevos=document.getElementsByTagName("input");
	for(var i=0;i<tablevos.length;i++){
	     if(tablevos[i].type=="checkbox"){
	     	if(tablevos[i].checked){
	     		checks='ok';
	     	}
		 }
     }
     if(checks.length>0){
     	toggles("viewbutton");
     	hides("darkbutton");
     }else{
     	hides("viewbutton");
     	toggles("darkbutton");
     }
}

checkefirst();
checkSelect();
</script>
</html:form>


