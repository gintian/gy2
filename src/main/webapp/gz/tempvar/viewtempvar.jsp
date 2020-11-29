<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<script language="JavaScript" src="/js/function.js"></script>
<style type="text/css">
.gray {	
	background:  #00FFFF;
	font-size: 12px;
	color: #000000;
}
#scroll_box {
    border: 1px solid #eee;
    height: 355px;    
    width: 470px;            
    overflow: auto;            
    margin: 0 0 0 0;
}
.btn {
 BORDER-RIGHT: #C0C0C0 1px solid;
 BORDER-TOP: #C0C0C0 1px solid; 
 PADDING-LEFT: 1px;
 PADDING-RIGHT: 1px;
 FONT-SIZE: 12px; 
 BORDER-LEFT: #C0C0C0 1px solid; 
 COLOR: #808080; 
 BORDER-BOTTOM: #C0C0C0 1px solid
}
</style>
<hrms:themes />
<%
	String nid1 = (String) request.getParameter("nid");
	nid1 = nid1==""?null:nid1;
	String ntype1 = (String) request.getParameter("ntype");
	ntype1 = ntype1==""?null:ntype1;
 %>
<script language="javascript">
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
function symbol(strexpr){
	document.getElementById("formula").focus();
	var element = document.selection;
	if (element!=null) {
		var rge = element.createRange();
		if (rge!=null)	
		rge.text=strexpr;
	}
}
function function_Wizard(){
	var type = document.getElementById("type").value;
	var cstate = document.getElementById("cstate").value;
	var nflag = "${tempvarForm.nflag}";
	if(nflag==5){
		cstate="-2";
	}
	var thecodeurl ="/org/autostatic/mainp/function_Wizard.do?b_query=link";
	if(type=='3')
		thecodeurl+="&salaryid=&tableid="+cstate+"&checktemp=temp";
	else
		thecodeurl+="&tableid=&salaryid="+cstate+"&checktemp=temp";
    var return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:400px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
    if(return_vo!=null){
    	symbol(return_vo);
  	}else{
  		return ;
  	}
    
}
function formacode(){
	var id = document.getElementById("codeid").value;
	symbol(id);
}
function addTemp(){
	   var cstate = document.getElementById("cstate").value;
	   var type = document.getElementById("type").value;
	   var showflag = document.getElementById("showflag").value;
	   var nflag = "${tempvarForm.nflag}";
	   var thecodeurl="/gz/tempvar/addtempvar.do?b_query=link`type="+type+"`cstate="+cstate+"`nflag="+nflag+"`showflag="+showflag;   
	   var dialogWidth="400px";
	   var dialogHeight="355px";
	   if (isIE6()){
	    	dialogWidth="430px";
	    	dialogHeight="380px";
	   } 
	   var iframe_url="/gz/gz_analyse/iframeHighgrade.jsp?src="+$URL.encode(thecodeurl);
	   var return_vo= window.showModalDialog(iframe_url, "", 
	              "dialogWidth:"+dialogWidth+"; dialogHeight:"+dialogHeight+";resizable:no;center:yes;scroll:no;status:no");
	    if(return_vo!=null)	 {
	    		document.getElementById("showflag").value=return_vo.showflag;
	    		document.getElementById("nid").value=return_vo.nid;
	    		document.getElementById("ntype").value=return_vo.ntype;
	    		/* 薪资总额，新增临时变量，刚新增的在下面，鼠标却定位在第一个，不对。 xiaoyun 2014-10-28 start */
	    		document.getElementById("isAddTempVar").value= "1";
	    		/* 薪资总额，新增临时变量，刚新增的在下面，鼠标却定位在第一个，不对。 xiaoyun 2014-10-28 end */
	    		reflesh();
	    }
	}
function saveformula(){
	var formula = document.getElementById("formula").value;
	var ntype = document.getElementById("ntype").value;
	var nid = document.getElementById("nids").value;
	var cstate = document.getElementById("cstate").value;
	if(nid==""){
		alert("请先新增临时变量！");
		return;
	}
  	var hashvo=new ParameterSet();
	hashvo.setValue("c_expr",getEncodeStr(formula));
	hashvo.setValue("type",ntype);
	hashvo.setValue("tabid",cstate);
	hashvo.setValue("nid",nid);
	hashvo.setValue("type_from",document.getElementById("type").value);
	var In_paramters="flag=1"; 	
	var request=new Request({method:'post',asynchronous:false,
	parameters:In_paramters,onSuccess:resultCheckExpr,functionId:'3020050005'},hashvo);		
}
function resultCheckExpr(outparamters){
  	var info = outparamters.getValue("info");
  	info = getDecodeStr(info);
	if(info=="ok"||info.length==0){
		var nid = document.getElementById("nids").value;
		var formula=document.getElementById("formula").value;
		var cstate = document.getElementById("cstate").value;
		
		var hashvo=new ParameterSet();
		hashvo.setValue("c_expr",getEncodeStr(formula));
		hashvo.setValue("nid",nid);
		hashvo.setValue("tabid",cstate);
	
		var request=new Request({asynchronous:false,functionId:'3020050004'},hashvo);
		alert("公式保存成功");
		hides("savebuttonview");
		toggles("savebuttonhide");
	}else{
		alert(info);
	}
} 
function getcheckbox(){
	var tablevos=document.getElementsByTagName("input");
	var nid;
	var n=0;
	for(var i=0;i<tablevos.length;i++){
	     if(tablevos[i].type=="checkbox"){
	     	if(tablevos[i].checked){
	     		nid = tablevos[i].value;
	     		n++;
	     	}
		 }
     }
    if(n<1){
		alert("<bean:message key='gz.tempvar.select.temp'/>");
		return ;
	}else if(n>1){
		alert("<bean:message key='gz.tempvar.set.temp'/>");
		return ;
	}else{
		return nid;
	}
}
//相关引用
function relateReference()
{
	var nids="";
	var tablevos=document.getElementsByTagName("input");
	for(var i=0;i<tablevos.length;i++){
	    if(tablevos[i].type=="checkbox"){
	     	if(tablevos[i].checked){
	     		var cvalue = tablevos[i].value;
	     		var arr = cvalue.split("-");
	     		if(arr.length==2)
	     			nids+=arr[0]+",";
	     	}
		}
    }
    if(nids==null || nids==""){
    	alert("<bean:message key='gz.tempvar.select.temp'/>");
    	return;
    }
    var nid = nids.split(",");
    if(nid.length>2) {
    	alert("只能查看单一临时变量的相关引用，请重新选择！");
    	return;
    }
    nid = nid[0];
    var type = document.getElementById("type").value;
	var cstate = document.getElementById("cstate").value;
	var hashvo=new ParameterSet();
	hashvo.setValue("nid",nid);
	hashvo.setValue("type",type);
	hashvo.setValue("cstate",cstate);
	var request=new Request({asynchronous:false,onSuccess:relateReference_ok,functionId:'3020050018'},hashvo);
}
function relateReference_ok(outparamters)
{
	var isok=outparamters.getValue("isok");
	if(isok==0)
	{
		var filename=outparamters.getValue("filename");
		var fieldName = getDecodeStr(filename);
		var win=open("/servlet/vfsservlet?fileid="+fieldName+"&fromjavafolder=true","txt");
	}else if(isok==1)
	{
		alert("该临时变量暂没被引用！");
	}
}
//撤销共享前的检查。
function revokeShare(flag,nid,ntype,cstate)
{
	var hashvo=new ParameterSet(); 
	hashvo.setValue("flag",flag); 
	hashvo.setValue("nid",nid);
	hashvo.setValue("ntype",ntype);
	hashvo.setValue("cstate",cstate);
   	var request=new Request({asynchronous:false,onSuccess:afterCheckRevokeShare,functionId:'3020050016'},hashvo);
}
function afterCheckRevokeShare(outparamters)
{
	var base=outparamters.getValue("base");
	var nid =outparamters.getValue("nid");
	if(base=="ok"){
		var nid=outparamters.getValue("nid");
		
		var ntype=outparamters.getValue("ntype");
		var flag=outparamters.getValue("flag");
		var cstate=outparamters.getValue("cstate");
		setCvalue(nid,ntype);
		setCodeValue(nid,cstate); 
		if(ntype=="3")
			document.getElementsByName(nid+"_1")[0].onclick=function onclick(){ addShare(flag,nid,ntype,"1") };
		else
			document.getElementsByName(nid+"_1")[0].onclick=function onclick(){ addShare(flag,nid,ntype,"null") };
	}else
	{
		alert(base);
		addover(nid);//如果移除共享失败，通过这种方式将共享的勾再次勾上
		//reflesh();
	}
}
function addover(nid){
	var nidbox=document.getElementsByName(nid+"_1");
	if(nidbox){
		var currentBox =nidbox[0];
		currentBox.checked=true;
	}
}
//设置共享之前的检查。相同名字的临时变量，不能设为共享
function addShare(flag,nid,ntype,cstate)
{
	var hashvo=new ParameterSet(); 
	hashvo.setValue("flag",flag); 
	hashvo.setValue("nid",nid);
	hashvo.setValue("ntype",ntype);
	hashvo.setValue("cstate",cstate);
   	var request=new Request({asynchronous:false,onSuccess:afterCheckAddShare,functionId:'3020050015'},hashvo);
	
}
function afterCheckAddShare(outparamters)
{
	var isok=outparamters.getValue("isok");
	var nid =outparamters.getValue("nid");
	if(isok==0)
	{
		var error=outparamters.getValue("error");
		alert(error);//失败以后采取js的方法把rdio中的勾给去掉 
		removeover(nid);
		//reflesh();
	}else if(isok==1)
	{
		var flag=outparamters.getValue("flag");
		var nid=outparamters.getValue("nid");
		var ntype=outparamters.getValue("ntype");
		var cstate=outparamters.getValue("cstate");
		setCvalue(nid,ntype);
		setCodeValue(nid,cstate);
		if(ntype=="3")
			document.getElementsByName(nid+"_1")[0].onclick=function onclick(){ revokeShare(flag,nid,ntype,"null") };
		else
			document.getElementsByName(nid+"_1")[0].onclick=function onclick(){ revokeShare(flag,nid,ntype,"${tempvarForm.cstate}") };
	}
}
function removeover(nid){
	var nidbox=document.getElementsByName(nid+"_1");
	if(nidbox){
		var currentBox =nidbox[0];
		currentBox.checked=false;
	}
}
function setCvalue(nid,ntype){
	document.getElementById("nids").value=nid;
	document.getElementById("ntype").value=ntype;
	var pars="nid="+nid;  
    var request=new Request({method:'post',asynchronous:false,parameters:pars,onSuccess:getCvalue,functionId:'3020050006'});
    tr_bgcolor(nid);
    viewSaveButton();//切换公式后公式保存按钮显示为正常 lis 20160803
}
function getCvalue(outparamters){
	var cvalue = outparamters.getValue("formula");
	document.getElementById("formula").value=getDecodeStr(cvalue);
}
function delVariables(){
	var nid="";
	var tablevos=document.getElementsByTagName("input");
	for(var i=0;i<tablevos.length;i++){
	    if(tablevos[i].type=="checkbox"){
	     	if(tablevos[i].checked){
	     		var cvalue = tablevos[i].value;
	     		var arr = cvalue.split("-");
	     		if(arr.length==2)
	     			nid+=arr[0]+",";
	     	}
		}
    }
    if(nid==null || nid==""){
    	alert("<bean:message key='gz.tempvar.select.temp'/>");
    	return;
    }
    if(!ifdel()){
    	return ;
    }
    beforeDel(nid);
    reflesh();
}
function beforeDel(nid) {
	var type = document.getElementById("type").value;
	var cstate = document.getElementById("cstate").value;
	var hashvo=new ParameterSet();
	hashvo.setValue("nid",nid);
	hashvo.setValue("type",type);
	hashvo.setValue("cstate",cstate);
	var request=new Request({asynchronous:false,onSuccess:beforeDel_ok,functionId:'3020050017'},hashvo);
}
function beforeDel_ok(outparamters){
	var base=outparamters.getValue("base");
	if(base=="ok")
	{
		var hashvo=new ParameterSet();
		var nid=outparamters.getValue("nid");
		hashvo.setValue("nid",nid);
		var request=new Request({asynchronous:false,functionId:'3020050007'},hashvo);
	} else {
		alert(base);
	}
}
function reflesh(){
	var cstate = document.getElementById("cstate").value;
	var type = document.getElementById("type").value;
	var showflag =document.getElementById("showflag").value;
	
	var nid = document.getElementById("nid").value;
	var ntype =document.getElementById("ntype").value;
	var nflag = "${tempvarForm.nflag}";
	document.tempvarForm.action="/gz/tempvar/viewtempvar.do?b_query=link&state="+cstate+"&type="+type+"&nflag="+nflag+"&nid="+nid+"&ntype="+ntype+"&showflag="+showflag;
    document.tempvarForm.submit();
} 
function editTemp(id){
    var cstate = document.getElementById("cstate").value;
	var type = document.getElementById("type").value;
	var showflag = document.getElementById("showflag").value;
	var nflag = "${tempvarForm.nflag}";
    var thecodeurl="/gz/tempvar/addtempvar.do?b_edit=link&type="+type+"&cstate="+cstate+"&id="+id+"&nflag="+nflag+"&showflag="+showflag;
    var return_vo= window.showModalDialog(thecodeurl,"", 
              "dialogWidth:400px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
    if(return_vo!=null){
    		document.getElementById("showflag").value=return_vo;
    		reflesh();
    }	 
    
}
function returnup(type){
	var cstate = document.getElementById("cstate").value;
	if(type==1){
		if(cstate!=-1){
			document.location.href="/gz/templateset/gz_templatelist.do?b_query=link2";
		}else{
			window.close();
		}
   	}else if(type==2){
   		var nid = document.getElementById("nids").value;
   		window.returnValue=nid;
   		window.close();
   	}else if(type==3){
   		window.close();
   	}else if(type==5){
   		var showflag = document.getElementById("showflag").value;
   		if(showflag==0){
   			document.location.href="/performance/data_collect/data_collect.do?b_query=link";
   		}else{
   			var nid = document.getElementById("nids").value;
   			window.returnValue=nid;
   			window.close();
   		}
   	}
}
function tr_bgcolor(nid){
	var tablevos=document.getElementsByTagName("input");
	for(var i=0;i<tablevos.length;i++){
	    if(tablevos[i].type=="checkbox"){
	    	var cvalue = tablevos[i];
	    	var td = cvalue.parentNode.parentNode;
	    	td.style.backgroundColor = '';
		}
    }
	var c = document.getElementById(nid);
	var tr = c.parentNode.parentNode;
	if(tr.style.backgroundColor!=''){
		tr.style.backgroundColor = '' ;
	}else{
		tr.style.backgroundColor = '#FFF8D2' ;
	}
}
function defSelect(){
	var checkname = "";
	var tablevos=document.getElementsByTagName("input");
	/* 薪资总额，新增临时变量，刚新增的在下面，鼠标却定位在第一个，不对。 xiaoyun 2014-10-28 start */
	var isAddTempVar = '${tempvarForm.isAddTempVar}';
	if(isAddTempVar == "0" || !isAddTempVar) {
		for(var i=0;i<tablevos.length;i++){
		 if(tablevos[i].type=="checkbox"){
	    	checkname=tablevos[i].value;
	    	var arr_item = checkname.split("-");
	    	if(arr_item.length==2)
	    		break;
		 }
    	}
	}
	if(isAddTempVar == "1"&&<%=nid1%>!=null&&<%=ntype1%>!=null){
		setCvalue(<%=nid1%>,<%=ntype1%>);
	}
	document.getElementById("isAddTempVar").value = "0";
	/* 薪资总额，新增临时变量，刚新增的在下面，鼠标却定位在第一个，不对。 xiaoyun 2014-10-28 end */
	if(checkname!=null&&checkname.length>0){
		var arr = checkname.split("-");
		if(arr.length==2){
			setCvalue(arr[0],arr[1]);
		}
	}
}
function toSorting(){
	var cstate = document.getElementById("cstate").value;
	var types = document.getElementById("type").value;
	var nflag = "${tempvarForm.nflag}";
	var thecodeurl="/gz/tempvar/sorting.do?b_query=link&state="+cstate+"&type="+types+"&nflag="+nflag;
   var dialogWidth="400px";
   var dialogHeight="430px";
   if (isIE6()){
    	dialogWidth="430px";
    	dialogHeight="450px";
   } 
	var return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:"+dialogWidth+"; dialogHeight:"+dialogHeight+";resizable:no;center:yes;scroll:yes;status:no");
   if(return_vo!=null){
   		reflesh();
   }
} 	 
function change(){
	hides("viewcode");
    var fieldsetid=document.getElementById("fieldsetid").value;
    var cstate = "${tempvarForm.cstate}";
    var type = "${tempvarForm.type}";
	var hashvo=new ParameterSet();
	hashvo.setValue("fieldsetid",fieldsetid);	
	hashvo.setValue("type",type);	
	hashvo.setValue("cstate",cstate);
	var request=new Request({method:'post',asynchronous:false,onSuccess:showFieldList,functionId:'3020050011'},hashvo);	
}
function showFieldList(outparamters){
	var itemlist=outparamters.getValue("itemlist");
	AjaxBind.bind(tempvarForm.itemid_arr,itemlist);
}

function changecode(){
    var itemid=getItemid().split(":");
    var fieldsetid=document.getElementById("fieldsetid").value;
    if(itemid.length<1){
    	return;
    }
    var hashvo=new ParameterSet();
    hashvo.setValue("fieldsetid",fieldsetid);	
	hashvo.setValue("itemid",itemid[0]);
	var request=new Request({method:'post',asynchronous:false,onSuccess:showCodeFieldList,functionId:'3020050012'},hashvo);	
}
function changeCodeValue(){
   var itemid=getItemid().split(":");
    var fieldsetid=document.getElementById("fieldsetid").value;
    if(itemid==null||itemid==undefined||itemid.length<2){
    	return;
    }
    symbol(itemid[1]);
    var hashvo=new ParameterSet();
	hashvo.setValue("fieldsetid",fieldsetid);	
	hashvo.setValue("itemid",itemid[0]);
	var request=new Request({method:'post',asynchronous:false,onSuccess:showCodeFieldList,functionId:'3020050012'},hashvo);	
	viewSaveButton();
}
function showCodeFieldList(outparamters){	
	var codelist=outparamters.getValue("codelist");
	if(codelist!=null&&codelist.length>1){
		toggles("viewcode");
		AjaxBind.bind(tempvarForm.codesetid_arr,codelist);
	}else{
		hides("viewcode");
	}	
}  
function getItemid(){
	var itemid="";
	var itemid_arr= document.getElementsByName("itemid_arr");
	var itemid_arr_vo = itemid_arr[0];
	if(itemid_arr==null){
		return "";
	}else{
		for(var i=0;i<itemid_arr_vo.options.length;i++){
			if(itemid_arr_vo.options[i].selected){
				itemid =itemid_arr_vo.options[i].text;
				continue;
			}
		}
		return itemid;
	}
}
function getCodesid(){
	var codeid="";
	var codesetid_arr= document.getElementsByName("codesetid_arr");
	var codesetid_arr_vo = codesetid_arr[0];
	if(codesetid_arr==null){
		return;
	}else{
		for(var i=0;i<codesetid_arr_vo.options.length;i++){
			if(codesetid_arr_vo.options[i].selected){
				codeid =codesetid_arr_vo.options[i].value;
				continue;
			}
		}
		if(codeid==null||codeid==undefined||codeid.length<1){
			return;
		}
		symbol("\""+codeid+"\"");
		viewSaveButton();
	}
}
function viewSaveButton(){
	hides("savebuttonhide");
	toggles("savebuttonview");
}
function setCodeValue(nid,cstate){
    var hashvo=new ParameterSet();
	hashvo.setValue("nid",nid);	
	hashvo.setValue("cstate",cstate);
	var request=new Request({method:'post',asynchronous:false,onSuccess:ok,functionId:'3020050014'},hashvo);//成功后不必每次都要刷新界面，没有必要	
}
function ok(){
	
}
			 		
</script>
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
     else {
%>
<br>
<%
}
%>

<style>
.td_no_t_r
{ 
    BORDER-TOP: 0pt;  
    BORDER-RIGHT: 0pt; 
}
.td_no_t
{ 
    BORDER-TOP: 0pt;  
}
</style>
<base target="_self">
<html:form action="/gz/tempvar/viewtempvar">
<html:hidden name="tempvarForm" property="cstate"/> 
<html:hidden name="tempvarForm" property="type"/> 
<html:hidden name="tempvarForm" property="showflag"/>
<html:hidden name="tempvarForm" property="nid"/>
<html:hidden name="tempvarForm" property="ntype"/>
<!-- 薪资总额，新增临时变量，刚新增的在下面，鼠标却定位在第一个，不对。 xiaoyun 2014-10-28 start -->
<html:hidden name="tempvarForm" property="isAddTempVar"/>
<!-- 薪资总额，新增临时变量，刚新增的在下面，鼠标却定位在第一个，不对。 xiaoyun 2014-10-28 end -->


<input type="hidden" name="nids"/>
<input type="hidden" name="ntype"/>
<table width="824" height="480" border="0" align="center" cellpadding="0" class="tempvarmargin">
<tr >
<td>
	<fieldset align="center" style="width:100%;height:450px;padding-left:3px;padding-right:3px;padding-bottom:3px">
	<legend><bean:message key="kq.wizard.variable"/></legend>
		<table width="100%"  border="0" align="center">
		  <tr> 
		    <td width="490" align="center"> 
		    <fieldset align="center" style="width:490;height:440px;padding-left:3px">
		    <legend><bean:message key="gz.tempvar.temp.list"/></legend>
		    <table width="100%" border="0" style="margin-top: 1px;"> <!-- modify by xiaoyun 2014-9-13 -->
		        <tr> 
		          <td height="360" valign="top">
		          <div id="scroll_box">
					<table width="100%" border="0" cellspacing="0" align="center" cellpadding="0"  style="border:0px; margin-top:0px;">
						<tr class="fixedHeaderTr1">
							<td width="10%" class="TableRow_right td_no_t_r common_background_color common_border_color" align="center"><bean:message key="column.select"/></td>
							<td width="30%" class="TableRow td_no_t_r" align="center"><bean:message key="column.name"/></td>
							<td width="10%" class="TableRow td_no_t_r" align="center"><bean:message key="label.org.type_org"/></td>
							<td width="10%" class="TableRow td_no_t_r" align="center"><bean:message key="report.parse.len"/></td>
							<td width="10%" class="TableRow td_no_t_r" align="center"><bean:message key="gz.tempvar.median"/></td>
							<td width="10%" class="TableRow td_no_t_r" align="center"><bean:message key="kq.register.codesetid"/></td>
							<td width="10%" class="TableRow_left common_background_color common_border_color" align="center"><bean:message key="label.edit.user"/></td>
							
								<logic:notEqual name="tempvarForm" property="nflag" value="4">
									<td width="10%" class="TableRow_left common_background_color common_border_color" align="center"><bean:message key="gz.vartemp.share"/></td>
								</logic:notEqual>
							
						</tr>
						<hrms:paginationdb id="element" name="tempvarForm" sql_str="tempvarForm.sql" table="" where_str="tempvarForm.where" columns="tempvarForm.column" order_by="tempvarForm.orderby" pagerows="200" page_id="pagination" indexes="indexes">	
						<bean:define id="nid" name='element' property='nid'/>
						<bean:define id="ntype" name="element" property="ntype"/>
						<bean:define id="chz" name="element" property="chz"/>
						<bean:define id="codesetdesc" name="element" property="codesetdesc"/>
						<bean:define id="codesetid" name="element" property="codesetid"/>
						<bean:define id="cstate" name="element" property="cstate"/>
						<tr> 
							<td class="RecordRow_right td_no_t_r"  align="center" nowrap>
								<input type="checkbox" name="${nid}" value="${nid}-${ntype}" onclick='setCvalue("${nid}","${ntype}");'> 
							</td>
							<td class="RecordRow td_no_t_r" style="word-break: break-all; word-wrap:break-word;" onclick='setCvalue("${nid}","${ntype}");'>
								${chz}
							</td>
							<td class="RecordRow td_no_t_r" onclick='setCvalue("${nid}","${ntype}");' nowrap>
								<logic:equal name="element" property="ntype" value="1"><bean:message key="kq.formula.countt"/></logic:equal>
								<logic:equal name="element" property="ntype" value="2"><bean:message key="kq.formula.charat"/></logic:equal>
								<logic:equal name="element" property="ntype" value="3"><bean:message key="gz.tempvar.date"/></logic:equal>
								<logic:equal name="element" property="ntype" value="4"><bean:message key="gz.tempvar.code"/></logic:equal>
							</td>
							<td align="right" onclick='setCvalue("${nid}","${ntype}");' class="RecordRow td_no_t_r" nowrap>
								<bean:write name="element" property="fldlen"/>
							</td>
							<td align="right" onclick='setCvalue("${nid}","${ntype}");' class="RecordRow td_no_t_r" nowrap>
								<bean:write name="element" property="flddec"/>
							</td>
							<td class="RecordRow td_no_t_r" style="word-break: break-all; word-wrap:break-word;" onclick='setCvalue("${nid}","${ntype}")' nowrap>
								<logic:notEqual name="element" property="codesetid" value="">
									<logic:notEqual name="element" property="codesetid" value="0">
									${codesetid}
									</logic:notEqual>									
								</logic:notEqual>
								&nbsp;
							</td>
							<td align="center" onclick='setCvalue("${nid}","${ntype}");' class="RecordRow_left td_no_t_r" nowrap>
								<a href="###" onclick='editTemp("${nid}");'><img src="/images/edit.gif" border=0></a>
							</td>
							
								<logic:notEqual name="tempvarForm" property="nflag" value="4">
									<td align="center" class="RecordRow_left td_no_t_r" nowrap>
										<logic:equal name="tempvarForm" property="type" value="3">
											<logic:equal name="element" property="cstate" value="1">
												<input type="checkbox" name="${nid}_1" value="${nid}${cstate}" onclick='revokeShare("3","${nid}","${ntype}","null");' checked> 
											</logic:equal>
											<logic:notEqual name="element" property="cstate" value="1">
												<input type="checkbox" name="${nid}_1" value="${nid}${cstate}" onclick='addShare("3","${nid}","${ntype}","1");'> 
											</logic:notEqual>
										</logic:equal>
										<logic:notEqual name="tempvarForm" property="type" value="3">
											<!--判断是不是由数据采集进入的-->
											<logic:equal name="tempvarForm" property="type" value="5">
												<logic:notEqual name="tempvarForm" property="cstate" value="${cstate}">
												<input type="checkbox" name="${nid}_1" value="${nid}${cstate}" onclick='revokeShare("5","${nid}","${ntype}","${tempvarForm.cstate}");' checked> 
												</logic:notEqual>
												<logic:equal name="tempvarForm" property="cstate" value="${cstate}">
												<input type="checkbox" name="${nid}_1" value="${nid}${cstate}" onclick='addShare("5","${nid}","${ntype}","null");'> 
												</logic:equal>
											</logic:equal>
											<!--是不是由数据采集进入的判断完毕-->
											<logic:notEqual name="tempvarForm" property="type" value="5">
												<logic:notEqual name="tempvarForm" property="cstate" value="${cstate}">
													<input type="checkbox" name="${nid}_1" value="${nid}${cstate}" onclick='revokeShare("1","${nid}","${ntype}","${tempvarForm.cstate}");' checked> 
												</logic:notEqual>
												<logic:equal name="tempvarForm" property="cstate" value="${cstate}">
													<input type="checkbox" name="${nid}_1" value="${nid}${cstate}" onclick='addShare("1","${nid}","${ntype}","null");'> 
												</logic:equal>
											</logic:notEqual>
										</logic:notEqual>
									</td>
								</logic:notEqual>
							
						</tr>
						</hrms:paginationdb>
					</table>
					</div>
				  </td>
		        </tr>
		        <tr> 
		          <td height="35" align="center" valign="top">
					<input type="button"  value="<bean:message key='button.insert'/>" onclick="addTemp();" Class="mybutton">
					<input type="button"  value="<bean:message key='button.delete'/>" onclick="delVariables();" Class="mybutton">
					<input type="button"  value="<bean:message key='kq.item.change'/>" onclick="toSorting();" Class="mybutton">
					<input type="button"  value="相关引用" onclick="relateReference();" Class="mybutton">
				  </td>
		        </tr>
		      </table>
		      </fieldset>
		    </td>
		    <td width="330" align="center"> 
		      <fieldset align="center" style="width:330;height:440px;padding-left:5px;margin-left:5px;padding-top: -2px;">
		      <legend><bean:message key="kq.wizard.expre"/></legend>
		      <table width="100%"  border="0">
		        <tr> 
		          <td height="200" valign="bottom" colspan="2">
				 		<html:textarea name="tempvarForm" property="formula" onclick="this.pos=document.selection.createRange();viewSaveButton();" style="width:320;height:195px;" styleId="shry" ></html:textarea> 
				 </td>
		        </tr>
		        <tr>
		          <td height="30" width="77%" align="right">
					<input name="wizard" type="button" id="wizard" value='<bean:message key="kq.formula.function"/>' onclick="function_Wizard('formula');" Class="mybutton">
				  </td>
				  <td align="left" width="23%">
					<div id="savebuttonview">
						<input type="button"  value="<bean:message key='org.maip.formula.preservation'/>"  onclick="saveformula();" Class="mybutton">
				  	</div>
				  	<div id="savebuttonhide" style="display:none">
						<input type="button"  value="<bean:message key='org.maip.formula.preservation'/>" Class="mybutton" disabled="true">
				  	</div>
				  </td>
		        </tr>
		        <tr> 
		          <td height="90" align="center" colspan="2"> 
		            <fieldset align="center" style="width:100%;">
		            <table width="100%" border="0">
		              <tr height="25"> 
		                <td>
		                	<table width="100%"border="0">
		              			<tr>
		              				<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<bean:message key="menu.table"/>&nbsp;&nbsp;
		                			 	<html:select name="tempvarForm" property="fieldsetid" onchange="change();" style="width:200;font-size:9pt" >
					 						<html:optionsCollection property="fieldsetlist" value="dataValue" label="dataName" />
										</html:select>
		         					</td>
		         				</tr>
		         			</table>
		         		 </td>
		         		</tr>
		         		<tr height="25"> 
		                 <td>
		         			<table width="100%"border="0">
		              			<tr>
		              				<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<bean:message key="menu.field"/>&nbsp;&nbsp;
										<select name="itemid_arr" onchange="changeCodeValue();" style="width:200;font-size:9pt">
		             					</select>
		         					</td>
		         				</tr>
		         			</table>
		         		  </td>
		         		</tr>
		         		<tr height="30"> 
		                <td>
		                	<span id="viewcode">
		         			<table width="100%" border="0">
		              			<tr>
		              				<td><bean:message key="conlumn.codeitemid.caption"/>&nbsp;&nbsp;
		              					<select name="codesetid_arr" onchange="getCodesid();" style="width:200;font-size:9pt">
		             					</select>
		         					</td>
		         				</tr>
		         			</table>
		         			</span>
		                </td>
		              </tr>
		            </table>
		            </fieldset>
		          </td>
		        </tr>
		      </table> 
		     </fieldset>
		    </td>
		  </tr>
		</table>
	</fieldset>
	</td>
	</tr>
	<tr height="35px">
     <td height="35" align="center">
    	<logic:equal name="tempvarForm" property="type" value="1"> 
    		<input type="button"  value="<bean:message key='button.return'/>" onclick="returnup(1);" Class="mybutton">
    	</logic:equal>
    	<logic:equal name="tempvarForm" property="type" value="2">
    		<input type="button"  value="<bean:message key='button.ok'/>" onclick="returnup(2);" Class="mybutton">
    	</logic:equal>
    	<logic:equal name="tempvarForm" property="type" value="3"> 
    		<input type="button"  value="<bean:message key='button.close'/>" onclick="returnup(3);" Class="mybutton">
    	</logic:equal>
    	<logic:equal name="tempvarForm" property="type" value="5"> 
    		<logic:equal name="tempvarForm" property="showflag" value="0">
    			<input type="button"  value="<bean:message key='button.ok'/>" onclick="returnup(5);" Class="mybutton">
    		</logic:equal>
    		<logic:notEqual name="tempvarForm" property="showflag" value="0">
    			<input type="button"  value="<bean:message key='button.return'/>" onclick="returnup(5);" Class="mybutton">
    		</logic:notEqual>	
    	</logic:equal>
    </td>
 </tr>
</table>

<script language="javascript">
hides("viewcode");	
change();
changecode();
defSelect();
</script>
</html:form>
