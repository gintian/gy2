<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<hrms:themes></hrms:themes>
<style type="text/css">
.btn3 {/*update by xiegh 35922  如何一个历史节点都没选 则需将按钮置灰
    background:url(/images/hcm/themes/default/content/mybutton_bg.gif) 0 0;
    height:23px;
    line-height:20px;
    border: 1px solid #DDD;
    background-color: #F5F5F5;
    color:#ACA899;
    width:42px;
    height:23px;
    line-height:20px;
    width:55px;
    border:1px solid #c5c5c5;
      */
    
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

<table width="100%" height="314" border="0" align="center">

	<tr>
		
    <td height="270" width="65%" valign="top">
    <fieldset align="center" style="width:100%;">
     <legend>人员历史时点</legend>
     	<table width="100%" height="270" border="0" align="center">
     		<tr>
     			<td valign="top">
     			<div id="scroll_box">
    			${historyStatForm.scan_table}
    			</div>
    			</td>
    		</tr>
    	</table>
    </fieldset>
    </td>	
    <td valign="top">
    <table width="95%" height="246" border="0" style="margin-top: 6px;margin-left: 20px;">
        <tr> 
          <td valign="top">
           <input type="button" name="button_all" value=" <bean:message key='label.query.selectall'/> " onclick="checkall();" Class="mybutton"><br><br>
	        <input type="button" name="button_no" value=" <bean:message key='label.query.clearall'/> " onclick="checkclear();checkSelect();" Class="mybutton"><br><br>
          	<span id="viewbutton">
          		<input type="button" name="button_ok" value=" <bean:message key='reporttypelist.confirm'/> " onclick="check_ok();" Class="mybutton"><br><br>
          	</span>
          	<span id="darkbutton">
          		<input type="button" name="button_ok" value=" <bean:message key='reporttypelist.confirm'/> "  Class="mybutton" style="background:#f7f1f1;color:#054977;opacity:0.5;"><br><br>
          	</span>
	        <input type="button" name="button_no" value=" <bean:message key='kq.register.kqduration.cancel'/> " onclick="windowClose();" Class="mybutton"><br><br>
	      </td>
        </tr>
      </table>
      </td>
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
	      	thecontent +=tablevos[i].value+"&";
		 }
     }
     if(thecontent==null||thecontent.length<1){
     	alert("请选择历史时点!");
     	return false;
     }
   //19/3/22 浏览器兼容 回填值
    if(window.showModalDialog){
    	 window.returnValue=thecontent;
  	}else{
  		  parent.getbackdates_callbackfunc(thecontent);
  	}
    windowClose();
    /* 
     window.returnValue = thecontent;
     window.close();
      */
}
//19/3/22 浏览器兼容 关闭窗口方法
function windowClose(){
	if(window.showModalDialog){
		window.close();
	}else{
		parent.closeExtWin();
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
if(getBrowseVersion()==10){ //非IE浏览器兼容性   wangb 20180127
    if(getIE11Version()){
        //点击历史时点文本框后面的按钮，触发页面的样式修改  wangbs
        var fieldset = document.getElementsByTagName('fieldset')[0];
        fieldset.style.paddingLeft = "2px";
        fieldset.style.width = "90%";
        var tableSet = document.getElementsByTagName('table')[6];
        tableSet.style.marginLeft = "-10px";
        var okBtn = document.getElementsByName("button_ok")[1];
        okBtn.style.paddingLeft = "0px";
        okBtn.style.width = "40px";
    }
}
document.getElementById('darkbutton').getElementsByTagName('input')[0].disabled="disabled";
</script>
</html:form>


