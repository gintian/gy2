<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<script type="text/javascript" src="/js/wz_tooltip.js"></script>  
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null){
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	css_url="/css/css1.css"; 
          //out.println("<link href='"+css_url+"' rel='stylesheet' type='text/css' >");  	 
	}
%>
<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT> 
<SCRIPT LANGUAGE=javascript>
var selectrelut="";
function upok(setid){
	$('setid'+setid).style.display='none';
	$('ssetid'+setid).style.display='none';
	showsetinfo('setid'+setid,setid+'list');
}
function b_oks(bname){
	var pinfo=$('pinfo');
	var setid=bname.substring(2,bname.length);
	var allcheck=document.getElementsByTagName("INPUT");
	var pars="";
	for(var i=0;i<allcheck.length;i++){
		var ck=allcheck[i];
		var ckvalue=ck.value;
		var ssid=ckvalue.split("/");
		if(ssid.length==3&&ssid[0]==setid&&ck.checked==true){
		removevalues(ckvalue);
		pars=pars+"|"+ckvalue;
		}
	}
	pars='itemstr='+pars+'&action=ok&pinfo='+pinfo.value;
 	var request=new Request({method:'post',asynchronous:false,parameters:pars,onSuccess:upok(setid),functionId:'0580010005'});
	

}
function b_rej(bname){
	var pinfo=$('pinfo');
	var setid=bname.substring(2,bname.length);
	var allcheck=document.getElementsByTagName("INPUT");
	var pars="";
	for(var i=0;i<allcheck.length;i++){
		var ck=allcheck[i];
		var ckvalue=ck.value;
		var ssid=ckvalue.split("/");
		if(ssid.length==3&&ssid[0]==setid&&ck.checked==true){
		removevalues(ckvalue);
		pars=pars+"|"+ckvalue;
		}
	}
	pars='itemstr='+pars+'&action=rj&pinfo='+pinfo.value;
 	var request=new Request({method:'post',asynchronous:false,parameters:pars,onSuccess:upok(setid),functionId:'0580010005'});

}
function bs_allokss(){
	var pinfo=$('pinfo');
	var pars="itemstr="+selectrelut+"&action=aok&pinfo="+pinfo.value;
	var request=new Request({method:'post',asynchronous:false,parameters:pars,onSuccess:ref,functionId:'0580010005'});
	
 	
}
function bs_allrejs(){
	var pinfo=$('pinfo');
	var pars="itemstr="+selectrelut+"&action=arj&pinfo="+pinfo.value;
	var request=new Request({method:'post',asynchronous:false,parameters:pars,onSuccess:ref,functionId:'0580010005'});
	
 	}
function ref(){
	window.returnValue='ok';
	window.close();
	

}
function onchecks(item){
 	if(item.checked==true){
 		addvalues(item.value);
 	}else{
 		removevalues(item.value);
 	}
}
function addvalues(value){
	if(selectrelut.lenght<=1){
		selectrelut=value;
	}else{
		selectrelut=selectrelut+"|"+value;
	}
}
function removevalues(value){
	if(selectrelut.length>0){
		var temp=selectrelut.split("|");
		var ret="";
		for(var i=0;i<temp.length;i++){
			
			if(temp[i]!=value){
				if(i==0){
				ret=temp[0];
				}else{
				ret=ret+"|"+temp[i];
				}
			}
		}
		selectrelut=ret;
	}
}
function appealok(){
	alert('信息报批成功！');

	 window.returnValue='ok';
		window.close();
	
}
function appeal(outparamters){

	var cset=outparamters.getValue("cset");
	var citem=outparamters.getValue("citem");
	if(citem.length>0){
		alert(citem);
		window.close();
	}else{
		if(cset.length>0){
			if(confirm(cset)){
				var pars="a0100=${selfInfoForm.a0100}&pdbflag=${selfInfoForm.pdbflag}";
				var request=new Request({method:'post',asynchronous:false,parameters:pars,onSuccess:appealok,functionId:'0201001098'});
				
			}else{
				window.close();
			}
		}else{
			var pars="a0100=${selfInfoForm.a0100}&pdbflag=${selfInfoForm.pdbflag}";
			var request=new Request({method:'post',asynchronous:false,parameters:pars,onSuccess:appealok,functionId:'0201001098'});
	
		
		}
	
	}
}

function checkdate(){
	var pars="a0100=${selfInfoForm.a0100}&pdbflag=${selfInfoForm.pdbflag}";
	var request=new Request({method:'post',asynchronous:false,parameters:pars,onSuccess:appeal,functionId:'0201001099'});
	
}
</SCRIPT> 
<link rel="stylesheet" href="<%=css_url%>" type="text/css">
<hrms:themes />
<html:form action="/general/approve/personinfo/showpersoninfo"> 

<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
   <tr>
       <td align="center">
      <INPUT type="hidden" name="pinfo" value="${selfInfoForm.a0100}|${selfInfoForm.pdbflag}"/>
     <table>
     <tr>
     <td><h3><bean:message key='workdiary.message.person.infor.app'/></h3>
     </td>
     </tr>
     </table>
	 <hrms:infobrowse nid="${selfInfoForm.a0100}" infokind="1" pre="${selfInfoForm.pdbflag}" isinfoself="0" />
       </td>
   </tr>          
</table>
<table  align="left">
   <tr align="left">  
    <td valign="top"  nowrap>
    <logic:notEqual value='2' name="selfInfoForm" property='setprv'>
    <button name="b_o"  class="mybutton" onclick="bs_allokss();"><bean:message key='info.appleal.state3'/></button> &nbsp;
    <button name="b_r"  class="mybutton" onclick="bs_allrejs();"><bean:message key='info.appleal.state2'/></button> &nbsp;
   </logic:notEqual>
    <logic:equal value='2' name="selfInfoForm" property='setprv'>
     <button name="b_o"  class="mybutton" onclick="checkdate();"><bean:message key='info.appleal.state1'/></button> &nbsp;
   </logic:equal>
      <button name="b_save"  class="mybutton" onclick="javascript:window.close();"> <bean:message key="button.close"/> </button> 
    </td>
  </tr>
 </table>  

</html:form>
