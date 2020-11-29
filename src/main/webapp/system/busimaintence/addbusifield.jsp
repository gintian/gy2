<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<hrms:themes></hrms:themes>
<SCRIPT LANGUAGE=javascript src="/js/validate.js"></SCRIPT>
<script language="JavaScript" src="../../../../module/utils/js/template.js"></script>
<script type="text/javascript" language="javascript">
<!--
function getitemtype(){
	var itemtype=busiMaintenceForm.itemtype.value;
	var itp=itemtype.split(".");
	if(itp[0]==""){
	alert(KJG_ZBTX_INF20);
	return;
	}
	var intlen=$('inlen');
	var limitlen = $('limitlen');
	var len =$('len');
	var codeset=$('codeset');
	var relating=$('relating');
	var dates=$('dates');
	var decimal=$('decimal');
	if(itp[0]=='A'){
		if(itp[1]=='S'){
		len.style.display="block";
		intlen.innerHTML="长度";
		codeset.style.display="none";
		relating.style.display="none";
		dates.style.display="none";
		decimal.style.display="none";
		limitlen.style.display="none";
		busiMaintenceForm.itemlength.value="";
		busiMaintenceForm.itemlength.disabled="";
		return;
		}
		if(itp[1]=='C'){
		len.style.display="block";
		intlen.innerHTML="长度";
		codeset.style.display="block";
		relating.style.display="none";
		dates.style.display="none";
		decimal.style.display="none";
		limitlen.style.display="none";
		busiMaintenceForm.itemlength.value="";
		busiMaintenceForm.itemlength.disabled="true";
		return;
		}
		if(itp[1]=='R'){
		len.style.display="block";
		intlen.innerHTML="长度";
		codeset.style.display="none";
		relating.style.display="block";
		dates.style.display="none";
		decimal.style.display="none";
		limitlen.style.display="none";
		busiMaintenceForm.itemlength.value="";
		busiMaintenceForm.itemlength.disabled="true";
		return;
		}	
	}
	if(itp[0]=='D'){
		len.style.display="none";
		codeset.style.display="none";
		relating.style.display="none";
		dates.style.display="block";
		decimal.style.display="none";
		limitlen.style.display="none";
		getdatelen();
		return;
	}
	if(itp[0]=='N'){
		len.style.display="block";
		intlen.innerHTML="整数位";
		codeset.style.display="none";
		relating.style.display="none";
		dates.style.display="none";
		decimal.style.display="block";
		limitlen.style.display="none";
		busiMaintenceForm.itemlength.value="";
		busiMaintenceForm.itemlength.disabled="";
		return;
	}
	
	if(itp[0]=='M'){
		codeset.style.display="none";
		relating.style.display="none";
		dates.style.display="none";
		decimal.style.display="none";
		limitlen.style.display="";
		/**
		xus 
		备注型默认长度为50 改为 备注型默认长度为10  wangb 31419 2017/09/11
		17/02/08
		*/
//		busiMaintenceForm.itemlength.value="50";
		busiMaintenceForm.itemlength.value="10";
		busiMaintenceForm.itemlength.disabled="";
		
		/*len.style.display="none";
		codeset.style.display="none";
		relating.style.display="none";
		dates.style.display="none";
		decimal.style.display="none";*/
		return;
	}
}
function limitclick(){
    var intlen=$('inlen');
	var limitlen = $('limitlen');
	var len =$('len');
	var limitlength = document.getElementsByName('limitlength')[0].checked;
	var itemlength = $('itemlength');//获取长度   wangb 20170911 31419
		if(limitlength==true){
			len.style.display="block";
			intlen.innerHTML="长度";
			itemlength.value='';//限制时  长度为空 wangb 2017/09/11 31419
		}else{
			len.style.display="none";
			itemlength.value='10';//不限制时  默认为10 wangb 2017/09/11 31419
		}
}
function getcodelen(){
	var codelen=busiMaintenceForm.code.value;
	if(codelen.length<1){
	alert(KJG_ZBTX_INF21);
	return;
	}
	if(codelen=='newcode'){
		var return_vo= window.showModalDialog("/system/codemaintence/add_edit_codeset.do?b_query=link&encryptParam=<%=PubFunc.encrypt("query=query")%>", false, 
        "dialogWidth:500px; dialogHeight:300px;resizable:no;center:yes;scroll:yes;status:no");
        //var return_vo=window.showModalDialog(toCodeitem(codelen));       
     if(return_vo==null)
  	 	return ;
	 
     /* var codesetvo=new Object();
     codesetvo.codesetid=return_vo.codesetid;
     codesetvo.codesetdesc=return_vo.codesetdesc;
     codesetvo.maxlength=return_vo.maxlength;
     codesetvo.status=return_vo.status;
     
    var hashvo=new ParameterSet();
     hashvo.setValue("codesetvo",codesetvo);
     hashvo.setValue("flag","0");
     hashvo.setValue("codestname",codesetvo.codesetdesc);
     hashvo.setValue("codesetid",codesetvo.codesetid);
     var request=new Request({asynchronous:false,onSuccess:add_codeset_ok,functionId:'1010050008'},hashvo); */
     
     toCodeitempen(return_vo.codesetid,'','add');
	}else{
		var code=codelen.split("/");
		var hashvo = new ParameterSet();
		hashvo.setValue("obj",code[0]);
		var request=new Request({asynchronous:false,onSuccess:showFieldList,functionId:'1020010114'},hashvo);
	}
}
function add_codeset_ok(outparamters)
  {
        //alert(KJG_YWZD_INFO1);
        var codesetid  = outparamters.getValue("codesetid");
		toCodeitempen(codesetid);
   }
/**打开职务编码设置，需要传递代码类ID*/
function toCodeitempen(codesetid,codeitem,flag)
{
	var iframe_url = "/pos/posbusiness/searchposbusinesstree.do?b_all=link&codesetid="+codesetid+"&codeitem="+codeitem;
	var return_vo= window.showModalDialog(iframe_url, 'newwindow', 
	"dialogWidth:750px; dialogHeight:680px;resizable:no;center:yes;scroll:yes;status:no");
	if(return_vo!=null&&(/[A-Z]/.test(return_vo)|| /[0-9]/.test(return_vo))){
		//window.location.href=window.location.href;
		if(flag=='add'){
			//alert(return_vo);
			var hashvo=new ParameterSet();
	   		hashvo.setValue("setid",return_vo);
	    	var request=new Request({asynchronous:false,onSuccess:joincodeList,functionId:'1020010112'},hashvo); 
	    	function joincodeList(outparamters){
	    		var joincodeList=outparamters.getValue("joincodeList");
				AjaxBind.bind(busiMaintenceForm.code,joincodeList);
	    		busiMaintenceForm.code.value=return_vo;
	    	}
    	}
    	getcodelen();
	}
}


function getrelatingcodelen(){
	var rcode=busiMaintenceForm.rcode.value;
	var fieldsetid = document.getElementById("fieldset");
	if(rcode.length<1){
		alert(KJG_YWZD_INFO2);
		return;
		}
	if(rcode=="newrelating"){
		//window.location.href="/system/busimaintence/showrelatingcode.do?b_query=link&add_flag=1";
		var theurl="/system/busimaintence/showrelatingcode.do?b_query=link`encryptParam=<%=PubFunc.encrypt("add_flag=1")%>";
		var iframe_url="/general/email_template/iframe_gz_email.jsp?src="+theurl;
		var return_vo= window.showModalDialog(iframe_url, false, 
        "dialogWidth:700px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
       // var cc = document.getElementById("C");
        var hashvo=new ParameterSet();
        hashvo.setValue("flag","1");
        var request=new Request({asynchronous:false,onSuccess:refresh_select,functionId:'1010060019'},hashvo); 
        
	}else{
		var rcodelen=rcode.split("/");
	    busiMaintenceForm.itemlength.value=rcodelen[1];
	}

}
//保存值
function showFieldList(outparamters){
	var fieldlist=outparamters.getValue("itemlength");
	AjaxBind.bind(busiMaintenceForm.itemlength,fieldlist);
	var setid=outparamters.getValue("obj");
	var codesetid=document.getElementById('codesetid');
    codesetid.value=setid;
}
function refresh_select(outparameters)
{
  var sel=outparameters.getValue("relating");
  var cc = document.getElementById("C");
  cc.innerHTML=getDecodeStr(sel);
}
function getdatelen(){
	var datelen=busiMaintenceForm.date.value;
	$('itemlength').value=datelen;
}
var inputCode="";
function press(obj)
{
  var code = window.event.keyCode;
  var ss=String.fromCharCode(code);
  inputCode+=ss;
  for(var i=0;i<obj.options.length;i++)
  {
      var val=obj.options[i].text;
      var one = val.substring(1,2);
      var two = val.substring(1,3);
      if(one.toLowerCase()==inputCode.toLowerCase()||two.toLowerCase()==inputCode.toLowerCase())
      {
         obj.options[i].selected=true;
         obj.fireEvent("onchange");
         break;
      }
  }
   if(trim(inputCode).length>=2)
   {
      inputCode="";
   }
}
function checkItemid()
{
	/*
		业务字典里面的指标代号不限制  wangb 20170603 28037
		if(!testId()){
		return false;
	}*/
     var desc=document.getElementById("itemdescid").value;
     if(trim(desc).length==0)
     {
        alert(KJG_YWZD_INFO3);
        return;
     }
     var reg="~`!@#$%^&*<>?'\"|;；·～＠＃％＆×＋｛［｝］＂＇：？／＞＜，．";
    
	for(var i=0;i<desc.length;i++){
		 var c=desc.substring(i,i+1);
		 if(reg.indexOf(c)!=-1){
		 	alert("名称不能是特殊字符~`!@#$%^&*<>?'\"|;；·～＠＃％＆×＋｛［｝］＂＇：？／＞＜，．");
		 	return false;
		 }
  	}
	if(!desc)
		return;
     var hashvo=new ParameterSet();
     hashvo.setValue("itemid",document.getElementById("itemid").value);
     hashvo.setValue("fieldset",document.getElementById("fieldset").value);
     hashvo.setValue("itemdescid",desc);
     var request=new Request({asynchronous:false,onSuccess:check_ok,functionId:'1010060018'},hashvo); 
   
}
function check_ok(outparameters)
{

  var msg = outparameters.getValue("msg");
  if(msg=="1")
  {
    alert(KJG_YWZD_INFO4);
    return;
  }
  else
  {
	var bitianxiang = document.getElementsByName('bitianxiang')[0].checked;
	if(bitianxiang==true){
		bitianxiang='1';
	}if(bitianxiang==false){
		bitianxiang='0';
	}
	
	var itemtype = document.getElementById("itemtype").value;
    var itemlength = document.getElementById("itemlength").value;
    var itemid = document.getElementById("itemid").value;
    var item = itemid.substring(itemid.length-2,itemid.length);
    if(item=="Z0"||item=="z0"||item=="Z1"||item=="z1"){
    	alert(KJG_YWZD_INFO5);
    	return;
    }
	if(itemtype==null||itemtype==""){
		alert(KJG_ZBTX_INF20);
		return;
	}
	/**
	*xus 备注性指标指标长度不能为空或指标长度不能为0（||itemtype=="M"）
	*16/09/29
	**/
	if(itemtype=="A.S"||itemtype=="N"||itemtype=="M"){
		if(itemlength==""||itemlength==null||itemlength=="0"){
			alert(KJG_ZBTX_INF25);
			return;
		}
	}
	if(itemtype=="A.C"){
		var codelen=busiMaintenceForm.code.value;
   		var codelens = codelen.split("/");
		if(codelens[0]==""||codelens[0]==null){
   			alert(KJG_ZBTX_INF21);
   			return;
   		}
		if(itemlength==""||itemlength==null||itemlength=="0"){
			alert(KJG_ZBTX_INF25);
			return;
		}
	}
	if(itemtype=="A.R"){
		var rcode=busiMaintenceForm.rcode.value;
   		var rcodes = rcode.split("/");
		if(rcodes[0]==""||rcodes==null){
   			alert(KJG_YWZD_INFO6);
   			return;
   		}
   		if(itemlength==""||itemlength==null||itemlength=="0"){
			alert(KJG_ZBTX_INF25);
			return;
		}
	}
	if(itemtype=="D"){
		var date=busiMaintenceForm.date.value;
		if(date==""||date==null){
			alert(KJG_YWZD_INFO7);
			return;
		}
	}
     busiMaintenceForm.action="/system/busimaintence/addbusifield.do?b_add=add&isclose=1&bitianxiang="+bitianxiang;
     busiMaintenceForm.submit();
     //var obj = new Object();
     //obj.type="1";
    // returnValue=obj;
  }
}
function clos()
{
    var obj = new Object();
    obj.type="0";
    returnValue=obj;
    window.close();
}
function clo()
{
   <%if(request.getParameter("isclose")!=null&&request.getParameter("isclose").equals("1")){%>
   var obj = new Object();
   obj.type="1";
   returnValue=obj;
   window.close();
   <%}%>
}
//只能输入数字
function isNum(i_value){
    re=new RegExp("[^0-9]");
    var s;
    if(s=i_value.match(re)){
        return false;
     }
     if("M" != document.getElementById("itemtype").value && i_value >255){
     	return false;
     }
    return true;
}

function checkNuN(obj){
 	if(!isNum(obj.value)){
 		obj.value='';
 		return;
 	}
 	/*数值型指标长度不能超过10位  wangb 32277 20171026*/
 	var itemtype = document.getElementById('itemtype').value;
 	if(itemtype == 'N' && obj.value >10){
 		obj.value = '10';
 		return;
 	}
}
function checknum(obj){
	var itemtype=busiMaintenceForm.itemtype.value;
	if(itemtype=='M'){
		if(obj.value==10){
			document.getElementsByName('limitlength')[0].checked=false;
			obj.value='';
	 		return;
		}
	}
}
//decimal digits
function checkNuS(obj){
	if(!isNumss(obj.value)){
		obj.value='';
		return
	}
}
function isNumss(value){
	re=new RegExp("[^0-9]");
	var s;
	if(s=value.match(re)){
		return false
	}
	return true;
}
//代号输入
function checkNuNS(obj){
	if(!isNums(obj.value)){
 		obj.value='';
 		return;
 	}
}
function isNums(i_value){
	var fy = i_value.substring(0,1);
	rs=new RegExp("[^A-Za-z]");
	var e;
	if(e!=fy.match(rs)){
		alert(KJG_YWZD_INFO8);
		return false;	
	}
    re=new RegExp("[^A-Za-z0-9]");
    var s;
    if(s=i_value.match(re)){
        return false;
     }
    return true;
}
function sign(str){
	return;
	var tmp = '', c=0; 
	for(var i=0;i<str.length;i++){ 
		c = str.charCodeAt(i); 
		tmp += String.fromCharCode((c>32 && c<48)||(c>57 && c<65)||(c>90 &&c<97)||(c>122 && c<126) ? (c+0xfee0) : c) 
	} 
	if(tmp!=str)
		document.all.itemdescid.value=tmp 
}
//关联代码类查看  jingq  add  2014.5.27
function toC(){
	var codeitem = "index";
	var codelen=busiMaintenceForm.code.value;
	var code = codelen.split("/");
	if(code[0]=="newcode"||code[0]==""){
		alert(KJG_ZBTX_INF19);
		return;
	}
	toCodeitempen(code[0],codeitem);
}
//业务字典，根据userType限制新建指标项的指标代号  jingq add 2015.01.21
function testId(){
	/* var userType = document.getElementById("userType").value;
	var itemid = document.getElementById("itemid").value;
	var str = itemid.substring(itemid.length-2,itemid.length-1);
	var reg = /^[a-zA-Z]+$/;
	if(userType==null||userType=="0"||userType==""||userType==undefined){
		if(!reg.test(str)||str.toUpperCase()=="X"||str.toUpperCase()=="Y"||str.toUpperCase()=="Z"){
			alert("指标代号倒数第2位必须是字母且不能为X、Y、Z。");
			return false;
		}
	} else if(userType=="1"){
		if(reg.test(str)){
			alert("指标代号倒数第2位必须为数字。");
			return false;
		}
	}
	return true; */
	var dev_flag = document.getElementById("userType").value;//document.getElementById("dev_flag").value;
	var indexcode = document.getElementById("itemid").value;//document.getElementById("indexcode").value;
	if(dev_flag==null||dev_flag=="0"||dev_flag==""||dev_flag==undefined){
		var reg = /^...[a-wA-W][a-zA-Z]$/;
		if(!reg.test(indexcode)){//||str.toUpperCase()=="X"||str.toUpperCase()=="Y"||str.toUpperCase()=="Z"
			alert("指标代号最后两位必须是字母且倒数第2位不能为X、Y、Z。");
			return false;
		}
	} else if(dev_flag=="1"){
		var reg = /^...[0-9][0-9A-Z]$/;
		var reg2 = /^...[A-W][0-9]$/;
		if(!(reg.test(indexcode) || reg2.test(indexcode))){
			alert("指标代号规则错误,后两位应为[0-9]+[0-9或A-Z]或[A-W]+[0-9]。");
			return false;
		}
	}
	return true;
}
//【7099】业务字典和指标体系，创建的指标字母改为大写。 jingq upd 2015.02.02
function checkcode(){
	var item = document.getElementById("itemid");
	var itemid = item.value;
	var reg = /^[a-zA-Z0-9_]+$/;
	var code = "";
	var index = "";
	if(itemid.length>0){
		for(var i=0;i<itemid.length;i++){
			index = itemid.substring(i,i+1);
			if(reg.test(index)){
				code += index;
			}
		}
		item.value = trim(code).toUpperCase();
	}
}
//-->
</script>

<html:form action="/system/busimaintence/addbusifield">
	<html:hidden styleId="userType" name="busiMaintenceForm" property="userType"></html:hidden>
	<html:hidden name="busiMaintenceForm" styleId="fieldset" property="busiFieldVo.string(fieldsetid)"/>
	<html:hidden styleId="codesetid" name="busiMaintenceForm" property="busiFieldVo.string(codesetid)"/>
	<table width="500" border="0" cellspacing="0" align="center" cellpadding="0">
		<tr height="20">
			<!--  <td width=10 valign="top" class="tableft"></td>
			<td width=110 align=center class="tabcenter">
					<bean:message key="kjg.title.newzhibiaoxiang"/>	
			</td>
			<td width=10 valign="top" class="tabright"></td>
			<td valign="top" class="tabremain" width="680"></td>-->
			<td align="left" class="TableRow">
					<bean:message key="kjg.title.newzhibiaoxiang"/>	
			</td>
		</tr>
		<tr>
			<td  class="framestyle3">
				<table border="0" width="100%" cellspacing="0" align="center" cellpadding="0" class="ListTable">
					<tr height="30px;">
						<td align="right" nowrap  width='80'>
							&nbsp;<bean:message key="kjg.title.zhibiaotaihao"/>
						</td>
						<td align="left" style="padding-left:5px;">							
							<html:text styleId="itemid" name="busiMaintenceForm" property="busiFieldVo.string(itemid)" onkeyup="checkcode();" maxlength="5" styleClass="text4" style="width:150px;"></html:text>
						</td>
						<td align="right" nowrap  width='80'>
							<bean:message key="kjg.title.indexname"/>
						</td>
						<td align="left" style="padding-left:5px;">
							<html:text styleId="itemdescid" name="busiMaintenceForm" property="busiFieldVo.string(itemdesc)" onkeyup="sign(this.value);" styleClass="text4" style="width:150px;"></html:text>
						</td>
					</tr>
					<tr style="height:30px;">
						<td align="right" nowrap width='80'>
							<bean:message key="kjg.title.indextype"/>
						</td>
						<td align="left" style="padding-left:5px;">							
								<html:select  styleId="itemtype" name="busiMaintenceForm" property="busiFieldVo.string(itemtype)" onchange="getitemtype();" style="width:150px;">
									<html:option value="">
										&nbsp;
									</html:option>
									<html:option value="A.S">
										<bean:message key="kjg.title.zifuxing"/>
									</html:option>
									<html:option value="A.C">
										<bean:message key="kjg.title.daimaxing"/>
									</html:option>
									<html:option value="N">
										<bean:message key="system.item.ntype"/>
									</html:option>
									<html:option value="D">
										<bean:message key="kjg.title.date"/>
									</html:option>
									<html:option value="M">
										<bean:message key="kjg.title.remark"/>
									</html:option>
									<html:option value="A.R">
										<bean:message key="kjg.title.table"/>
									</html:option>
								</html:select>
						</td>						
						<td align="center"  colspan='2'>
						 <div id="len" style="display:none;">
						  <table border="0" width="100%" cellspacing="0"  cellpadding="0">
							<tr>
							<td align="right" nowrap width='80' id="inlen">
							&nbsp;<bean:message key="kjg.title.length"/>
							</td>
							<td align="left" style="padding-left:12px;">
							<html:text styleId='itemlength' name="busiMaintenceForm" property="busiFieldVo.string(itemlength)" onkeyup="checkNuN(this)" onblur="checknum(this)" styleClass="text4" style="width:150px;"></html:text>
							</td>
							</tr>
						</table>
						</div>	
						</td>	
					   
					</tr>
					<tr>
					<td  nowrap colspan="4">
					<div id="codeset" style="display:none">
					<table border="0" width="100%" cellspacing="0" align="left" cellpadding="0">
					<tr>
					<td align="right" nowrap  width='80'>
							&nbsp;<bean:message key="kjg.title.xiangguandaima"/>
						</td>
						<td align="left"  nowrap" style="padding-left:5px;">
							<bean:write name="busiMaintenceForm" property="codesetsel" filter="false"/>
							<!-- 相关代码类查看    jingq  add  2014.5.27 -->
							<input type='button' class="mybutton" id="b_codevin"  onclick="toC();"
	 	     										value='<bean:message key="kjg.title.contvindicate"/>' />
						</td>
					</tr>
					</table>
					</div>
					<div id="relating" style="display:none">
					<table border="0" width="100%" cellspacing="0" align="left" cellpadding="0">
					<tr>
					<td align="right" nowrap  width='80'>
							&nbsp;<bean:message key="kjg.title.guanliancode"/>
					</td>
					<td  align="left"  id="C" nowrap style="padding-left:5px;">
						<bean:write name="busiMaintenceForm" property="relating" filter="false"/>
					</td>
					</tr>
					</table>
					</div>
					<div id="dates" style="display:none">
					<table border="0" width="100%" cellspacing="0" align="left" cellpadding="0">
					<tr>
					<td align="right" nowrap width='80'>
					&nbsp;<bean:message key="kjg.title.dategeshi"/>
					</td>
					<TD  align="left" nowrap style="padding-left:5px;">
					<bean:write name="busiMaintenceForm" property="date" filter="false"/>
					</TD>
					</tr>
					</table>
					</div>
					<div id="decimal" style="display:none">
					<table border="0" width="100%" cellspacing="0" align="left" cellpadding="0">
					<tr>
					<td align="right" nowrap  width='80'>
							&nbsp;<bean:message key="kjg.title.xiaoshuwei"/>
						</td>
						<td align="left" colspan='3' nowrap style="padding-left:5px;">
							<html:text name="busiMaintenceForm" property="busiFieldVo.string(decimalwidth)" maxlength="1" onkeyup="checkNuS(this)" styleClass="text4" style="width:150px;"></html:text>
						</td>
					</tr>
					</table>
					</div>
					</td>												
					
					<tr style="height:30px;">
						<td align="right" nowrap width='80' >&nbsp;<bean:message key="kjg.title.bitianxiang"/> </td>
						<td align="left"   nowrap colspan="3" style="padding-left:5px;">
							<input type="checkbox" name="bitianxiang">
						</td>
					</tr>
					<tr id="limitlen" style="height:30px;display: none;">
						<td align="right" nowrap width='80' >&nbsp;<bean:message key="kjg.title.limitlength"/> </td>
						<td align="left"   nowrap colspan="3" style="padding-left:5px;">
							<input type="checkbox" name="limitlength" onclick="limitclick();">
						</td>
					</tr>
					<tr>
						<td align="right"   nowrap colspan="" width="80">
							&nbsp;<bean:message key="kjg.title.content"/>
						</td>
						<td align="left"   nowrap colspan="3" style="padding-bottom:5px;padding-left:5px;">
                        <html:textarea name="busiMaintenceForm" property="busiFieldVo.string(itemmemo)" cols="60" rows="10" style="width:400px;overflow-y:auto;"></html:textarea>
                    </td>
					</tr>
	</table>
	<bean:define id="dev_flag" name="busiMaintenceForm" property="userType"/>
	<html:hidden name="busiMaintenceForm" property="busiFieldVo.string(ownflag)" value="${dev_flag}"/>
	<html:hidden name="busiMaintenceForm" property="busiFieldVo.string(expression)" styleId="expression"/>
		<tr>
			<td align="center" style="height:35px;">
				<logic:notEqual value="35"  name="busiMaintenceForm" property="id">
					<input type="button" name="formula" class="mybutton" value="<bean:message key="hmuster.label.expressions" />" onclick="viewformula('<bean:write name="busiMaintenceForm" property="busiFieldVo.string(fieldsetid)"/>','<bean:write name="busiMaintenceForm" property="busiFieldVo.string(itemid)"/>');"/>
				</logic:notEqual>
					<input type="button" name="add" class="mybutton" value="<bean:message key="button.ok" />" onclick="checkItemid();"/>	
					<input type="button" name="clo" class="mybutton" onclick="returnback();" value="<bean:message key="button.return" />"/>
			</td>
		</tr>
<script type="text/javascript">
clo();

function returnback(){
		busiMaintenceForm.action="/system/busimaintence/showbusifield.do?b_query=links";
	    busiMaintenceForm.submit();

}

function viewformula(setid,itemid){
	var itemtype = document.getElementById("itemtype").value;
	if(itemtype==null||itemtype.length<1){
		alert("请选择指标类型!");
		return;	
	}
	var expression=getEncodeStr($F('expression'));
	var formurl="/system/busimaintence/showbusifield.do?b_viewformula=links&itemsetid="+setid+"&fielditemid="+itemid+"&formula="+$URL.encode(expression)+"&itemtype="+itemtype;
	var dw=450,dh=450,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
	// var return_vo= window.showModalDialog(formurl, false,
    //      "dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:"+dw+"px; dialogHeight:"+dh+"px;resizable:no;center:yes;scroll:yes;status:no");
    // if(return_vo)
    // 	   $('expression').value=return_vo;
	return_vo ='';
	var theUrl = formurl;
	Ext.create('Ext.window.Window', {
		id:'viewformula',
		height:480,
		width: 500,
		resizable:false,
		modal:true,
		autoScroll:false,
		autoShow:true,
		html:'<iframe style="background-color:#ffffff " frameborder="0" scrolling="no" height="100%" width="100%" src="'+theUrl+'"></iframe>',
		renderTo:Ext.getBody()
	});
}
function formulaReturn(formula){
	return_vo = formula;
	$('expression').value=formula;
}

if(getBrowseVersion()){
	var itemtype = document.getElementById('itemtype');
	itemtype.style.width='154px';
	var inlen = document.getElementById('inlen');
	inlen.setAttribute('width','73px');
	
	var dates = document.getElementById('dates');
	var td1 = dates.getElementsByTagName('td')[0];
	td1.setAttribute('width','72px');
	
	var select = dates.getElementsByTagName('select')[0];
	select.style.width='154px';
	
	var decimal = document.getElementById('decimal');
	decimal.getElementsByTagName('td')[0].setAttribute('width','72px');
	
	var codeset = document.getElementById('codeset');
	codeset.getElementsByTagName('td')[0].setAttribute('width','72px');
	codeset.getElementsByTagName('select')[0].style.width='154px';

	var relating = document.getElementById('relating');
	relating.getElementsByTagName('td')[0].setAttribute('width','72px');
	relating.getElementsByTagName('select')[0].style.width='154px';
}
var len = document.getElementById('len');
var td2 = len.getElementsByTagName('td')[1];
td2.style.paddingLeft='5px';

</script>
</html:form>


