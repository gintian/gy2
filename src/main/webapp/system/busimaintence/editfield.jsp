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
	busiMaintenceForm.itemid.disabled="true";
	var useflag=busiMaintenceForm.useflag.value;
	var itemtype=busiMaintenceForm.itemtype.value;
	var itp=itemtype.split(".");
	if(itp[0]==""){
	alert(KJG_ZBTX_INF20);
	return;
	}
	var len =$('len');
	var limitlen = $('limitlen');
	var codeset=$('codeset');
	var relating=$('relating');
	var dates=$('dates');
	var decimal=$('decimal');
	if(itp[0]=='A'){
		if(itp[1]=='S'){
		len.style.display="block";
		codeset.style.display="none";
		relating.style.display="none";
		dates.style.display="none";
		decimal.style.display="none";
		limitlen.style.display="none";
		busiMaintenceForm.itemlength.disabled="";
		if(useflag=="1"){
			busiMaintenceForm.itemtype.disabled="true";
			//busiMaintenceForm.itemlength.disabled="true";
		}
		return;
		}
		if(itp[1]=='C'){
		len.style.display="block";
		codeset.style.display="block";
		relating.style.display="none";
		dates.style.display="none";
		decimal.style.display="none";
		limitlen.style.display="none";
		if(useflag=="1"){
			busiMaintenceForm.itemtype.disabled="true";
			busiMaintenceForm.code.disabled="true";
		}
			busiMaintenceForm.itemlength.disabled="true";
			
		return;
		}
		if(itp[1]=='R'){
		len.style.display="block";
		codeset.style.display="none";
		relating.style.display="block";
		dates.style.display="none";
		decimal.style.display="none";
		limitlen.style.display="none";
		if(useflag=="1"){
			busiMaintenceForm.itemtype.disabled="true";
			
			busiMaintenceForm.rcode.disabled="true";
		}
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
		if(useflag=="1"){
			busiMaintenceForm.itemtype.disabled="true";
			
			//busiMaintenceForm.date.disabled="true";
		}
		return;
	}
	if(itp[0]=='N'){
		len.style.display="block";
		codeset.style.display="none";
		relating.style.display="none";
		dates.style.display="none";
		decimal.style.display="block";
		limitlen.style.display="none";
		if(useflag=="1"){
			busiMaintenceForm.itemtype.disabled="true";
			//busiMaintenceForm.itemlength.disabled="true";
			//busiMaintenceForm.decimala.disabled="true";
		}else{
			busiMaintenceForm.itemlength.disabled="";
		}
		return;
	}
	
	if(itp[0]=='M'){
		//len.style.display="block";
		codeset.style.display="none";
		relating.style.display="none";
		dates.style.display="none";
		decimal.style.display="none";
		limitlen.style.display="";
		busiMaintenceForm.itemlength.disabled="";
		if(useflag=="1"){
			busiMaintenceForm.itemtype.disabled="true";
			//busiMaintenceForm.itemlength.disabled="true";
		}
		var limitlength = document.getElementsByName('limitlength')[0].checked;
		if(limitlength==true){
			len.style.display="block";
			//intlen.innerHTML="长度";
		}else{
			len.style.display="none";
		}
		/*len.style.display="none";
		codeset.style.display="none";
		relating.style.display="none";
		dates.style.display="none";
		decimal.style.display="none";
		if(useflag=="1"){
			busiMaintenceForm.itemtype.disabled="true";
		}*/
		return;
	}
}

function getcodelen(){
	var codelen=busiMaintenceForm.code.value;
	if(codelen.length<1){
	alert(KJG_ZBTX_INF21);
	return;
	}
	//var code=codelen.split("/");
	//busiMaintenceForm.itemlength.value=code[1];
	if(codelen=='newcode'){
		var return_vo= window.showModalDialog("/system/codemaintence/add_edit_codeset.do?b_query=link&encryptParam=<%=PubFunc.encrypt("query=query")%>", false, 
        "dialogWidth:500px; dialogHeight:300px;resizable:no;center:yes;scroll:yes;status:no");
        //var return_vo=window.showModalDialog(toCodeitem(codelen));       
     if(return_vo==null)
  	 	return ;
     toCodeitempen(return_vo.codesetid,'','add');
	}else{
		var code=codelen.split("/");
		var hashvo = new ParameterSet();
		hashvo.setValue("obj",code[0]);
		var request=new Request({asynchronous:false,onSuccess:showFieldList,functionId:'1020010114'},hashvo);
	}
}

/**打开职务编码设置，需要传递代码类ID*/
function toCodeitempen(codesetid,codeitem,flag)
{
	var iframe_url = "/pos/posbusiness/searchposbusinesstree.do?b_all=link&codesetid="+codesetid+"&codeitem="+codeitem;
	var return_vo= window.showModalDialog(iframe_url, 'newwindow', 
	"dialogWidth:750px; dialogHeight:680px;resizable:no;center:yes;scroll:yes;status:no");
	if(return_vo!=null){
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

function limitclick(){
	var limitlen = $('limitlen');
	var len =$('len');
	var limitlength = document.getElementsByName('limitlength')[0].checked;
		if(limitlength==true){
			len.style.display="block";
			//intlen.innerHTML="长度";
		}else{
			len.style.display="none";
			document.getElementById("itemlength").value=10;//不勾选 限制长度选项  默认长度为10   wangb 20170517
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
function getrelatingcodelen(){
	var rcode=busiMaintenceForm.rcode.value;
	if(rcode.length<1){
		alert(KJG_YWZD_INFO2);
		return;
		}
		if(rcode=="newrelating"){
		//window.location.href="/system/busimaintence/showrelatingcode.do?b_query=link&add_flag=1";
		var theurl="/system/busimaintence/showrelatingcode.do?b_query=link&encryptParam=<%=PubFunc.encrypt("add_flag=1")%>";
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

function refresh_select(outparameters)
{
  var sel=outparameters.getValue("relating");
  var cc = document.getElementById("C");
  cc.innerHTML=getDecodeStr(sel);
}
function getdatelen(){
	var datelen=busiMaintenceForm.date.value;
	busiMaintenceForm.itemlength.value=datelen;
}
function updateshow(){

	getitemtype();
}
function disabletype(){
	busiMaintenceForm.itemtype.disabled='true';
}
function checkname()
{
    var itemid=document.getElementById("itemid").value;
    var setid=document.getElementById("setid").value;  
    var itemdesc=document.getElementById("itemdescription").value;
     var reg="~`!@#$%^&*<>?'\"|;；·～＠＃％＆×＋｛［｝］＂＇：？／＞＜，．";
    var parten = /^\s*$/;   //不能输入空格
    // var value1 = document.getElementById("name").value;
    //hashvo.setValue("app_fashion",value1);
    if(itemid==null||itemid==""){
        alert(KJG_ZBTX_INFO5);
        return;
    }
    if(itemdesc==null||itemdesc==""){
        alert(KJG_ZBTX_INFO6);
        return;
    }
    if(parten.test(itemdesc))
    {
        alert("指标名称不能为空!");
        return;
    }

    for(var i=0;i<itemdesc.length;i++){
		 var c=itemdesc.substring(i,i+1);
		 if(reg.indexOf(c)!=-1){
		 	alert("名称不能是特殊字符~`!@#$%^&*<>?'\"|;；·～＠＃％＆×＋｛［｝］＂＇：？／＞＜，．");
		 	return false;
		 }
  	}
     var hashVo=new ParameterSet();
     hashVo.setValue("itemid",itemid);
     hashVo.setValue("itemdesc", itemdesc);
     hashVo.setValue("setid",setid);
     var In_parameters="opt=1";
     var request=new Request({method:'post',asynchronous:false,parameters:In_parameters,onSuccess:check_ok,functionId:'1010060024'},hashVo);			
}
function check_ok(outparameter)
{
   var msg = outparameter.getValue("msg");
   var useflag=busiMaintenceForm.useflag.value;
   if(msg=='1')
   {
        alert(KJG_ZBTX_INF24);
        return;
   }
   var bitianxiang = document.getElementsByName('bitianxiang')[0].checked;
	if(bitianxiang==true){
		bitianxiang='1';
	}if(bitianxiang==false){
		bitianxiang='0';
	}
    var itemtype = document.getElementById("itemtype").value;
    var itemlength = document.getElementById("itemlength").value;
    
	if(itemtype==null||itemtype==""){
		alert(KJG_ZBTX_INF20);
		return;
	}
	/**
    *许硕 判断备注型长度不能为空或为0(||itemtype=="M")
    *16/09/27
    **/
	if(itemtype=="A.S"||itemtype=="N"||itemtype=="D"||itemtype=="M"){
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
		if(rcodes[0]==""||rcodes==null||rcodes=="newrelating"){
   			alert(KJG_YWZD_INFO6);
   			return;
   		}
   		if(itemlength==""||itemlength==null||itemlength=="0"){
			alert(KJG_ZBTX_INF25);
			return;
		}
	}
	if(useflag=="1")
	{
		if(itemtype=="N")
		{
			var xiao = document.getElementById("decimala").value;
			var zheng = document.getElementById("itemlength").value;
			var d =parseInt(zheng);
			var x =parseInt(xiao);  
			if(x<ss){
				alert("已构库的指标，小数位不能小于原始值");
				return;
			}
			if(d<yy)
			{
				alert("已构库的指标，长度不能小于原始值");
				return;
			}
		}
		if(itemtype=="A.S")
		{
			var zheng = document.getElementById("itemlength").value;
			var d =parseInt(zheng); 
			if(d<yy)
			{
				alert("已构库的指标，长度不能小于原始值");
				return;
			}
		}
		
	}
    busiMaintenceForm.action="/system/busimaintence/editfield.do?b_update=update&bitianxiang="+bitianxiang;
    busiMaintenceForm.submit();
}
//只能输入数字
function isNum(i_value){
    re=new RegExp("[^0-9]");
    var s;
    if(s=i_value.match(re)){
        return false;
     }
	/*字符型255的限制改为1000 guodd 2020-05-15*/
     if("M" != document.getElementById("itemtype").value&&i_value >1000){
     	return false;
     }
    return true;
}

function checkNuN(obj){
 	if(!isNum(obj.value)){
 		obj.value='';
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
	if(!isNums(obj.value)){
		obj.value='';
		return
	}
}
function isNums(value){
	re=new RegExp("[^0-9]");
	var s;
	if(s=value.match(re)){
		return false
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
		document.all.itemdescription.value=tmp 
}
//-->
var ss;
var yy;
function yevalue()
{
	var p=document.getElementById("decimala").value;
	var y=document.getElementById("itemlength").value;
	ss =parseInt(p);
	yy =parseInt(y);
}
//添加查看相关代码类功能   jingq  add   2014.5.19
function codevin(){
	var codeitem = "index";
	var co=busiMaintenceForm.code.value;
	var cod=co.split("/");
	var s = cod[0];
	if(s==""||s=="newcode"){
		alert(KJG_ZBTX_INF19);
		return;
	}
	//toCodeitem(s,codeitem);
	toCodeitempen1(s,codeitem);
}
//打开职务编码设置，需要传递代码类ID
function toCodeitempen1(codesetid,codeitem,flag)
{
	var iframe_url = "/pos/posbusiness/searchposbusinesstree.do?b_all=link&codesetid="+codesetid+"&codeitem="+codeitem;
	var return_vo= window.showModalDialog(iframe_url, 'newwindow', 
	"dialogWidth:750px; dialogHeight:680px;resizable:no;center:yes;scroll:yes;status:no");
	if(return_vo!=null){
		//window.location.href=window.location.href; 
		if(flag=='add'){
			var hashvo=new ParameterSet();
	   		hashvo.setValue("setid",return_vo);
	    	var request=new Request({asynchronous:false,onSuccess:joincodeList,functionId:'1020010112'},hashvo); 
	    	function joincodeList(outparamters){
	    		var joincodeList=outparamters.getValue("joincodeList");
				AjaxBind.bind(busiMaintenceForm.code,joincodeList);
	    		busiMaintenceForm.code.value=return_vo;
	    	}
    	}
		countdeta();
	}
}
//计算数值
function countdeta(){
	var co=busiMaintenceForm.code.value;
	var cod=co.split("/");
	var s = cod[0];
	var obj = s;
	var hashvo=new ParameterSet();
    hashvo.setValue("obj",obj);
    
    hashvo.setValue('fieldsetid',$F('setid'));
    hashvo.setValue('codesetid',$F('codesetid'));
    hashvo.setValue('itemid',$F('itemid'));
    
    var request=new Request({asynchronous:false,onSuccess:showFieldList,functionId:'1020010114'},hashvo); 
}
</script>

<html:form action="/system/busimaintence/editfield">
	<html:hidden styleId="setid" name="busiMaintenceForm" property="busiFieldVo.string(fieldsetid)"/>
	<html:hidden styleId="codesetid" name="busiMaintenceForm" property="busiFieldVo.string(codesetid)"/>
	<table width="500" border="0" cellspacing="0" align="center" cellpadding="0">
		<tr height="20">
			<!--  <td width=10 valign="top" class="tableft"></td>
			<td width=110 align=center class="tabcenter">
					<bean:message key='kjg.title.reviseindex'/>	
			</td>
			<td width=10 valign="top" class="tabright"></td>
			<td valign="top" class="tabremain" width="680"></td>-->
			<td colspan="4"  align="left" class="TableRow">
					<bean:message key='kjg.title.reviseindex'/>	
			</td>
		</tr>
		<tr>
			<td class="framestyle9" colspan="4">
				<table border="0" width="100%" cellspacing="0" align="center" cellpadding="0" class="ListTable">
					<tr style="height:30px;">
						<td align="right" width='100'>
							&nbsp;<bean:message key='kjg.title.zhibiaotaihao'/>
						</td>
						<td align="left" nowrap style="padding-left:5px;">							
							<html:text styleId="itemid" styleClass="text4" name="busiMaintenceForm" property="busiFieldVo.string(itemid)" style="width:150px;"></html:text>
						</td>
						<td align="right" width='80'>
							<bean:message key='kjg.title.indexname'/>
						</td>
						<td align="left" nowrap style="padding-left:5px;">
							<html:text styleId="itemdescription" styleClass="text4" name="busiMaintenceForm" property="busiFieldVo.string(itemdesc)" onkeyup="sign(this.value);" style="width:150px;"></html:text>
						</td>
					</tr>
					<tr style="height:30px;">
						<td align="right" width='90'>
							&nbsp;<bean:message key='kjg.title.indextype'/>
						</td>
						
						<td align="left"  nowrap style="padding-left:5px;">							
								<html:select  styleId="itemtype" name="busiMaintenceForm" property="busiFieldVo.string(itemtype)"  onchange="getitemtype();" style="width:150px;">
									<html:option value="">
										&nbsp;
									</html:option>
									<!-- 业务字典，修改指标，指标类型为A/S时保存后指标类型为空，此处修改为A.S   jingq upd 2014.10.13 -->
									<html:option value="A.S">
										<bean:message key='kjg.title.zifuxing'/>
									</html:option>
									<html:option value="A.C">
										<bean:message key='kjg.title.daimaxing'/>
									</html:option>
									<html:option value="N">
										<bean:message key='system.item.ntype'/>
									</html:option>
									<html:option value="D">
										<bean:message key='kjg.title.date'/>
									</html:option>
									<html:option value="M">
										<bean:message key='kjg.title.remark'/>
									</html:option>
									<html:option value="A.R">
										<bean:message key='kjg.title.table'/>
									</html:option>
								</html:select>
						</td>						
						<td align="left" nowrap colspan='2'>
						<div id="len" style="display:block;">
						  <table border="0" width="100%" cellspacing="0" align="center" cellpadding="0">
							<tr>
							<td align="right" width='59'>
							<bean:message key='kjg.title.length'/>		
							</td>
							<td align="left"  nowrap style="padding-left:5px;">
							<html:text styleId='itemlength' styleClass="text4" name="busiMaintenceForm" property="busiFieldVo.string(itemlength)" disabled="false" onkeyup="checkNuN(this)" onblur="checknum(this)" style="width:150px;"></html:text>
							</td>
							</tr>
						</table>
						</div>
						</td>	
					   
					</tr>
					<tr>
					<td  colspan="4">
					<div id="codeset" style="display:block">
					<table border="0" width="100%" cellspacing="0" align="center" cellpadding="0">
					<tr>
					<td align="right" nowrap  width='67'>
							<bean:message key='kjg.title.xiangguandaima'/>
						</td>
						<td align="left"  nowrap" style="padding-left:5px;">
							<bean:write name="busiMaintenceForm" property="codesetsel" filter="false"/>
							<!-- 添加查看相关代码类功能   jingq  add  2014.5.19 -->
							<input type='button' class="mybutton" id="b_codevin"  onclick='codevin()'
	 	     										value='<bean:message key="kjg.title.contvindicate"/>' />
						</td>
					</tr>
					</table>
					</div>
					<div id="relating" style="display:block">
					<table border="0" width="100%" cellspacing="0" align="center" cellpadding="0">
					<tr>
					<td align="right" nowrap="nowrap"  width='67'>
							<bean:message key='kjg.title.guanliancode'/>
					</td>
					<td  align="left" id="C" nowrap style="padding-left:5px;">
						<bean:write name="busiMaintenceForm" property="relating" filter="false"/>
					</td>
					</tr>
					</table>
					</div>
					<div id="dates" style="display:block">
					<table border="0" width="100%" cellspacing="0" align="center" cellpadding="0">
					<tr>
					<td align="right"  width='67'>
					&nbsp;<bean:message key='kjg.title.dategeshi'/>
					</td>
					<TD  align="left" nowrap style="padding-left:5px;">
					<bean:write name="busiMaintenceForm" property="date" filter="false"/>
					</TD>
					</tr>
					</table>
					</div>
					<div id="decimal" style="display:block">
					<table border="0" width="100%" cellspacing="0" align="center" cellpadding="0">
					<tr>
					<td align="right"  width='66'>
							<bean:message key='kq.wizard.decimal'/>
						</td>
						<td align="left" colspan='3' nowrap style="padding-left:6px;">
							<html:text styleId='decimala' styleClass="text4" name="busiMaintenceForm" property="busiFieldVo.string(decimalwidth)" maxlength="1" onkeyup="checkNuS(this)" style="width:150px;"></html:text>
						</td>
					</tr>
					</table>
					</div>
					</td>												
					
					<tr style="height:30px;">
						<td align="right" width='90' >&nbsp;<bean:message key="kjg.title.bitianxiang"/> </td>
						<td align="left"   nowrap colspan="3" style="padding-left:5px;">
							<input type="checkbox" name="bitianxiang"
								<logic:equal name="busiMaintenceForm" property="busiFieldVo.string(reserveitem)" value="1">
									checked="true"
								</logic:equal>
							>
						</td>
					</tr>
					<tr id="limitlen" style="height:30px;display: none;">
						<td align="right" width='90' >&nbsp;<bean:message key="kjg.title.limitlength"/> </td>
						<td align="left"   nowrap colspan="3" style="padding-left:5px;">
							<input type="checkbox" name="limitlength" onclick="limitclick();"
							<logic:equal name="busiMaintenceForm" property="busiFieldVo.string(itemtype)" value="M">
							<logic:notEqual name="busiMaintenceForm" property="busiFieldVo.string(itemlength)" value="10">
							checked="true"
							</logic:notEqual>
							</logic:equal>
							>
						</td>
					</tr>
					<tr>
						<td align="right" width='90' colspan="">
							&nbsp;<bean:message key='kjg.title.content'/>
						</td>
						<td align="left"  nowrap colspan="3" style="padding-bottom:5px;padding-left:5px;">
                        <html:textarea name="busiMaintenceForm" property="busiFieldVo.string(itemmemo)" cols="60" rows="10" style="width:395px;"></html:textarea>
                    </td>
					</tr>
	</table>
	<bean:define id="dev_flag" name="busiMaintenceForm" property="userType"/>
	<bean:define id="useflag" name="busiMaintenceForm" property="useflag"/>
	<html:hidden name="busiMaintencForm" property="useflag" value="${useflag}"/>
	<html:hidden name="busiMaintenceForm" property="busiFieldVo.string(expression)" styleId="expression"/>
	<script type="text/javascript" language="javascript">
			updateshow();
	</script>
<tr>
		<tr>
			<td align="center" height="35px;">
				<logic:notEqual value="35"  name="busiMaintenceForm" property="id">
					<input type="button" name="formula" class="mybutton" value="<bean:message key="hmuster.label.expressions" />" onclick="viewformula('<bean:write name="busiMaintenceForm" property="busiFieldVo.string(fieldsetid)"/>','<bean:write name="busiMaintenceForm" property="busiFieldVo.string(itemid)"/>');"/>
				</logic:notEqual>
					<input type="button" name="tt" class="mybutton" onclick="checkname();" value="<bean:message key="button.ok"/>"/>
					<hrms:submit styleClass="mybutton" property="br_return">
						<bean:message key="button.return" />
					</hrms:submit>
			</td>
		</tr>

	
</html:form>
<script type="text/javascript" language="javascript">
	yevalue();
	
	function viewformula(setid,itemid){
		var itemtype = document.getElementById("itemtype").value;
		if(itemtype==null||itemtype.length<1){
			alert("请选择指标类型!");
			return;	
		}
		var expression=$URL.encode(getEncodeStr($F('expression')));
		//【61770】ZCSB：系统管理，业务字典，选择数值型指标/修改，设置公式为“取整(数值型指标)”，点确定，提示左右类型不一致，见附件
		var formurl="/system/busimaintence/showbusifield.do?b_viewformula=links&itemsetid="+setid+"&fielditemid="+itemid+"&formula="+expression + "&itemtype=" + itemtype;
		var dw=450,dh=450,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
		if(getBrowseVersion()){
			var return_vo= window.showModalDialog(formurl, false,
					"dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:"+dw+"px; dialogHeight:"+dh+"px;resizable:no;center:yes;scroll:yes;status:no");
			if(return_vo!=null)
				$('expression').value=return_vo;
		}else{
			Ext.create('Ext.window.Window', {
				id:'viewformula',
				height:480,
				width: 500,
				resizable:false,
				modal:true,
				autoScroll:false,
				autoShow:true,
				html:'<iframe style="background-color:#ffffff " frameborder="0" scrolling="no" height="100%" width="100%" src="'+formurl+'"></iframe>',
				renderTo:Ext.getBody()
			});
		}
	}

	/**
	 * 非IE浏览器公式回调方法
	 * zhangh 2020-1-17
	 * @param formula
	 */
	function formulaReturn(formula){
		if(formula!=null){
			$('expression').value=formula;
		}
	}

	if(!getBrowseVersion()){//非IE浏览器样式修改   wangb  20190522 bug 48182
		var len = document.getElementById('len');
		len.getElementsByTagName('td')[0].setAttribute('width','80');
		
		var dates = document.getElementById('dates');
		dates.getElementsByTagName('td')[0].setAttribute('width','97');
		
		var decimal = document.getElementById('decimal');
		decimal.getElementsByTagName('td')[0].setAttribute('width','96');
		
		var codeset = document.getElementById('codeset');
		codeset.getElementsByTagName('td')[0].setAttribute('width','97');

		var relating = document.getElementById('relating');
		relating.getElementsByTagName('td')[0].setAttribute('width','97');
	}

</script>

