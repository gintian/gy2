<meta http-equiv="Pragma" content="no-cache">

<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.hjsj.sys.FieldItem"%>
<%@ page import="com.hrms.frame.dao.RecordVo"%>
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
if(confirm("确定要批准！")){
	var pinfo=$('pinfo');
	var pars="itemstr="+selectrelut+"&action=aok&pinfo="+pinfo.value;
	var request=new Request({method:'post',asynchronous:false,parameters:pars,onSuccess:ref,functionId:'0580010005'});
	}
 	
}
function bs_allrejs(){
	if(confirm("确定要驳回!")){
	var theurl="/general/approve/personinfo/approvemessage.do";
	var retvalue=	window.showModalDialog(theurl, false, 
        "dialogWidth:600px; dialogHeight:500px;resizable:no;center:yes;scroll:yes;status:no;");
    if(retvalue=="bcc"){
    	return;
    } else{  
	var pinfo=$('pinfo');
	//alert(pinfo);
	
	var pars="itemstr="+selectrelut+"&action=arj&pinfo="+pinfo.value+"&message="+retvalue;
	var request=new Request({method:'post',asynchronous:false,parameters:pars,onSuccess:ref,functionId:'0580010005'});
	
	}
	}
 	}
 	
function bcmessage(){
	var pinfo=$('pinfo');
	var pars="itemstr="+selectrelut+"&action=arj&pinfo="+pinfo.value;
	var request=new Request({method:'post',asynchronous:false,parameters:pars,onSuccess:ref,functionId:'0580010006'});
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
				var pars="a0100=${approvePersonForm.a0100}&pdbflag=${approvePersonForm.pdbflag}";
				var request=new Request({method:'post',asynchronous:false,parameters:pars,onSuccess:appealok,functionId:'0201001098'});
				
			}else{
				window.close();
			}
		}else{
			var pars="a0100=${approvePersonForm.a0100}&pdbflag=${approvePersonForm.pdbflag}";
			var request=new Request({method:'post',asynchronous:false,parameters:pars,onSuccess:appealok,functionId:'0201001098'});
	
		
		}
	
	}
}

function checkdate(){
	var pars="a0100=${approvePersonForm.a0100}&pdbflag=${approvePersonForm.pdbflag}";
	var request=new Request({method:'post',asynchronous:false,parameters:pars,onSuccess:appeal,functionId:'0201001099'});
	
}
function  updateinfo(){
	var nid=$('pinfo').value;
	var temp=nid.split("|");
	var theurl="/workbench/info/addselfinfo.do?b_add=add&a0100="+temp[0]+"&i9999=I9999&actiontype=update&userbase=${approvePersonForm.pdbflag}&setname=A01&flag=notself&writeable=1";
	var retvalue=	window.showModalDialog(theurl, false, 
      "dialogWidth:800px; dialogHeight:600px;resizable:no;center:yes;scroll:yes;status:no");
	// b_reload(temp[0]);	
	// window.open(window.location);
	 window.returnValue='re';
	 window.close();
}

function b_reload(a0100)
  {
    theurl="/general/approve/personinfo/showpersoninfo.do?b_query=link&pdbflag1=${approvePersonForm.pdbflag}&a01001="+a0100;
	var retvalue=window.showModalDialog(theurl, false, 
        "dialogWidth:800px; dialogHeight:1000px;resizable:no;center:yes;scroll:yes;status:no;");  
    
   
  }

</SCRIPT> 
<hrms:themes></hrms:themes>
<html:form action="/general/approve/personinfo/showpersoninfo"> 

<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
   <tr>
       <td align="center">
      <INPUT type="hidden" name="pinfo" value="${approvePersonForm.a0100}|${approvePersonForm.pdbflag}"/>
     <table>
     <tr>
     <td  align="left"><h3>人员信息审核</h3>
     </td>
     </tr>
     </table>
	 <hrms:infobrowse nid="${approvePersonForm.a0100}" infokind="1" pre="${approvePersonForm.pdbflag}" isinfoself="1" setflag="1"/>
     </td>
    </tr>          
</table>
<table  align="left">
   <tr align="left">  
    <td valign="top"  nowrap>
    <logic:notEqual value='2' name="approvePersonForm" property='setprv'>
     <logic:notEqual value='0' name="state">
      <button name="b_o"  class="mybutton" onclick="bs_allokss();"> 批准 </button> &nbsp;
      <button name="b_r"  class="mybutton" onclick="bs_allrejs();"> 驳回 </button> &nbsp;
    </logic:notEqual>
    <button name='sd' class='mybutton' onclick='updateinfo()'>修改</button>&nbsp;
   </logic:notEqual>
    <logic:equal value='2' name="approvePersonForm" property='setprv'>
     <button name="b_o"  class="mybutton" onclick="checkdate();"> 报批 </button> &nbsp;
   </logic:equal>
      <button name="b_save"  class="mybutton" onclick="javascript:window.close();"> <bean:message key="button.close"/> </button> 
    </td>
  </tr>
 </table>  

</html:form>
