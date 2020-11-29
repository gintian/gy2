<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<hrms:themes></hrms:themes>
<script language="javascript" src="/module/utils/js/template.js"></script>
<script language="javascript">
var saveflag=0;
function clement(){
	var name= document.getElementById("name").value;
	var itemlength = document.getElementsByName('itemlength')[0];
	var itp=name.split("/");
	var value1 =$('value1');
	var value2 =$('value2');
	var value22 =$('value22');
	var value3 =$('value3');
	var value4 =$('value4');
	var value5 =$('value5');
	var value5 =$('value51');
	var limitlen = $('limitlen');
	if(itp[0]=='value1'){
		value1.style.display="block";
		value2.style.display="none";
		value22.style.display="none";
		value3.style.display="none";
		value33.style.display="none";
		value4.style.display="none";
		value5.style.display="none";
		value51.style.display="none";
		limitlen.style.display="none";
		itemlength.setAttribute('maxLength','3');// 字符型指标 长度限制为3位  wangb 20171121 32854
		//itemlength.value='';//长度清空  wangb 20171121 32854
		return;
	}
	if(itp[0]=='value2'){
		value1.style.display="none";
		value2.style.display="block";
		value22.style.display="block";
		value3.style.display="none";
		value33.style.display="none";
		value4.style.display="none";
		value5.style.display="none";
		value51.style.display="none";
		limitlen.style.display="none";
		return;
	}
	if(itp[0]=='value3'){
		value1.style.display="none";
		value2.style.display="none";
		value22.style.display="none";
		value3.style.display="block";
		value33.style.display="block";
		value4.style.display="none";
		value5.style.display="none";
		value51.style.display="none";
		limitlen.style.display="none";
		return;
	}
	if(itp[0]=='value4'){
		value1.style.display="none";
		value2.style.display="none";
		value22.style.display="none";
		value3.style.display="none";
		value33.style.display="none";
		value4.style.display="block";
		value5.style.display="none";
		value51.style.display="none";
		limitlen.style.display="none";
		return;
	}
	if(itp[0]=='value5'){
		value1.style.display="none";
		value2.style.display="none";
		value22.style.display="none";
		value3.style.display="none";
		value33.style.display="none";
		value4.style.display="none";
		value5.style.display="block";
		value51.style.display="block";
		limitlen.style.display="";
		itemlength.removeAttribute('maxLength');//备注型指标不限制最大长度  wangb 20171121 32854
		itemlength.value='10';//值置为10 不限制长度  wangb 20171121 32854  33445
		return;
	}
}
function save(sflag){
	if(!testId()){
		return false;
	} 
	saveflag=sflag;
	var indexcodex = document.getElementsByName('indexcode')[0].value;
    var indexname = document.getElementsByName("indexname")[0].value;
	var indexcode = indexcodex.toUpperCase();
	var hashvo=new ParameterSet();
	var parten = /^\s*$/;   //不能输入空格
    // var value1 = document.getElementById("name").value;
	//hashvo.setValue("app_fashion",value1);
	if(indexcode==null||indexcode==""){
		alert(KJG_ZBTX_INFO5);
		return;
	}
	if(indexname==null||indexname==""){
		alert(KJG_ZBTX_INFO6);
		return;
	}
	if(parten.test(indexname))
	{
		alert("指标名称不能为空!");
		return;
	} 
	if (indexname == "人员编号") {
		alert("“人员编号”是系统指标名称，请修改!");
		return;
	}
	
	var reg="~`!@#$%^&*<>?'\"|;；·～！＠＃％＆×＋｛［｝］＂＇：？／＞＜，．)(";
	for(var i=0;i<indexname.length;i++){
		 var c=indexname.substring(i,i+1);
		 if(reg.indexOf(c)!=-1){
		 	alert("名称不能是特殊字符~`!@#$%^&*<>?'\"|;；·～！＠＃％＆×＋｛［｝］＂＇：？／＞＜，．)(");
		 	return false;
		 }
  	}
	/*if(document.getElementById("indexcode").value.length!=5){
		alert(KJG_ZBTX_INFO7);
		return;
	}*/
	if(indexcodex.length!=5){
		alert(KJG_ZBTX_INFO7);
		return;
	}
	var ixc = indexcode.substring(0,1);
	if(ixc>'0' && ixc<'9'){
		alert(KJG_ZBTX_INFO8);
		return;
	}
	indexname=indexname.replace(/(^\s*)|(\s*$)/g,""); //去掉指标前后空格 wangb 20171120 32717
	indexname=indexname.replace(/[\r\n]/g,"");//去掉指标回车换行符 wangb 20171120 32717
	 hashvo.setValue("indexcode",indexcode);
     hashvo.setValue("indexname",indexname);
     hashvo.setValue("fieldsetid",'${dbinitForm.setid }');
     var request=new Request({asynchronous:false,onSuccess:check_ok,functionId:'1020010115'},hashvo);
}
function check_ok(outparameters){
 var msg = outparameters.getValue("msg");
 /**
 *xus 字符型、数字型、备注性指标 指标长度不能为空或指标长度不能为0
 *16/09/29
 **/
 var itemtype = document.getElementById("name").value;
 var itemlength = document.getElementsByName('itemlength')[0].value;
 // var itemlength = document.getElementById("itemlength").value;
 if(itemtype=="value1"||itemtype=="value3"||itemtype=="value5"){
		if(itemlength==""||itemlength==null||itemlength=="0"){
			alert(KJG_ZBTX_INF25);
			return;
		}
 }
	
  if(msg=="1")
   {
       sav();
   }
   else
   {
     alert(KJG_ZBTX_INFO9);
     return;
   }
}
function sav(){
    var indexcodes = document.getElementsByName('indexcode')[0].value;
    var indexname = document.getElementsByName("indexname")[0].value;
    var	itemtype = document.getElementsByName("itemtype")[0].value;
    var content = document.getElementsByName("content")[0].value;
    var inputtype = document.getElementsByName("inputtype")[0].value;
	// var indexcodes = document.getElementById("indexcode").value;
	var indexcode = indexcodes.toUpperCase();
	// var indexname = document.getElementById("indexname").value;
	// var itemtype = document.getElementById("itemtype").value;
	// var content = document.getElementById("content").value;
	var value1 = document.getElementById("name").value;
	// var inputtype = document.getElementById("inputtype").value;
	var bitianxiang = document.getElementsByName('bitianxiang')[0].checked;
	if(bitianxiang==true){
		bitianxiang='1';
	}if(bitianxiang==false){
		bitianxiang='0';
	}
	var hashvo=new ParameterSet();
	hashvo.setValue("app_fashion",value1);
	if(value1=="value1"){
		var itemlength=$F('itemlength');
		var itemtype=$F('itemtype');
		var th =isNaN(itemlength);
		if(th){
		alert(KJG_ZBTX_INF10);
		return;
		}
		hashvo.setValue("itemlength",itemlength);
		hashvo.setValue("itemtype",itemtype);
	}else if(value1=="value2"){
		var joincodename = $F('joincodename');
		var codelength = $F('codelength');
		var codeitemtype = $F('codeitemtype');
		if(joincodename=="0"){
			alert(KJG_ZBTX_INF11);
			return;
		}
		if(codelength=="#$"||codelength=="0"||codelength==null||codelength==""){
			alert(KJG_ZBTX_INF12);
			return;
		}
		if(joincodename=="xinjian"||joincodename=="#$"){
			alert(KJG_ZBTX_INF13);
			return;
		}
		hashvo.setValue("joincodename",joincodename);
		hashvo.setValue("codelength",codelength);
		hashvo.setValue("codeitemtype",codeitemtype);
	}else if(value1=="value3"){
		var numberlength = $F('numberlength');
		var decimalwidth = $F('decimalwidth');
		var intitemtype = $F('intitemtype');
		var r =isNaN(numberlength);
		var s = isNaN(decimalwidth);
		if(r){
			alert(KJG_ZBTX_INF14)
			return;
		}
		if(s){
			alert(KJG_ZBTX_INF15)
			return;
		}
		if(numberlength==null||numberlength==""){
			alert(KJG_ZBTX_INF16);
			return;
		}
		hashvo.setValue("numberlength",numberlength);
		hashvo.setValue("decimalwidth",decimalwidth);
		hashvo.setValue("intitemtype",intitemtype);
	}else if(value1=="value4"){
		var datelength = $F('datelength');
		var dateitemtype = $F('dateitemtype');
		hashvo.setValue("datelength",datelength);
		hashvo.setValue("dateitemtype",dateitemtype);
	}else if(value1=="value5"){
	    var limitlength = document.getElementsByName('limitlength')[0].checked;
		if(limitlength==true){
			limitlength=$F('itemlength');
		}if(limitlength==false){
			limitlength=10;
		}
		var bzitemtype = $F('bzitemtype');
		hashvo.setValue("bzitemtype",bzitemtype);
		hashvo.setValue("limitlength",limitlength);
	}
	hashvo.setValue("indexcode",indexcode);
	hashvo.setValue("indexname",$F('indexname'));
	hashvo.setValue("content",getEncodeStr(content));
	hashvo.setValue("bitianxiang",bitianxiang);
	hashvo.setValue("setid",$F('setid'));
	hashvo.setValue("inputtype",inputtype);
	var request=new Request({method:'post',asynchronous:false,onSuccess:reFlag,functionId:'1020010113'},hashvo);
}
 function reFlag()
    {
    	if(saveflag==1){
    		document.dbinitForm.action="/system/dbinit/fielditemlist.do?b_add=links";
	    	document.dbinitForm.submit(); 
	    }else{
	    	document.dbinitForm.action="/system/dbinit/fielditemlist.do?b_query=links";
	    	document.dbinitForm.submit(); 
	    }  
    }
    
 function limitclick(){
    var value1=$('value1');
	var limitlen = $('limitlen');
	var limitlength = document.getElementsByName('limitlength')[0].checked;
		if(limitlength==true){
			value1.style.display="block";
		}else{
			value1.style.display="none";
		}
}
//代码类是否为null
function changelocations(obj){
	var obj = obj.value;
	if(obj=="xinjian"){
		toAddCodeSetk();
		return;
	}
	 if(obj=="#$"){
     	alert(KJG_ZBTX_INF18);
     	AjaxBind.bind(dbinitForm.codelength,'');
     	return;
     }
	var hashvo = new ParameterSet();
	hashvo.setValue("obj",obj);
	if(obj=="UM"||obj=="UN"||obj=="@K"||obj=="@@"){
		document.getElementById("b_codevin").setAttribute("disabled","disabled");
	}else{
		document.getElementById("b_codevin").removeAttribute("disabled");
	}
	var request=new Request({asynchronous:false,onSuccess:check_oks,functionId:'1020010123'},hashvo); 
	
}
/**新增代码类*/
function toAddCodeSetk()
{
	var bflag=false;
	var currname=1;
	/*
	var return_vo= window.showModalDialog("/system/codemaintence/add_edit_codeset.do?b_query=link&query=query&categories=", bflag, 
	"dialogWidth:500px; dialogHeight:300px;resizable:no;center:yes;scroll:yes;status:no");       
	if(return_vo==null)
	return ;
	toCodeitempen(return_vo.codesetid,'','add');*/
	/*var codesetvo=new Object();
	codesetvo.codesetid=return_vo.codesetid;
	codesetvo.codesetdesc=return_vo.codesetdesc;
	codesetvo.maxlength=return_vo.maxlength;
	codesetvo.status=return_vo.status;
	var hashvo=new ParameterSet();
	hashvo.setValue("codesetvo",codesetvo);
	hashvo.setValue("flag","0");
	hashvo.setValue("codestname",codesetvo.codesetdesc);
	hashvo.setValue("codesetid",codesetvo.codesetid);
	var request=new Request({asynchronous:false,onSuccess:to_toAddCodeSet_oklo,functionId:'1010050008'},hashvo);    */
	
	//改用ext 弹窗显示  wangb 20190329
	var url = "/system/codemaintence/add_edit_codeset.do?b_query=link&query=query&categories=";
	var win = Ext.create('Ext.window.Window',{
			id:'add_codeset',
			title:'',
			width:520,
			height:320,
			resizable:false,
			modal:true,
			autoScoll:false,
			autoShow:true,
			autoDestroy:true,
			html:'<iframe style="background-color:#fff" frameborder="0" SCROLLING=NO height="100%" width="100%" src="'+url+'"></iframe>',
			renderTo:Ext.getBody(),
			listeners:{
				'close':function(){
					if(this.return_vo==null)
						return ;
					toCodeitempen(this.return_vo.codesetid,'','add');
				}
			}
	});  
	
}
function to_toAddCodeSet_oklo(outparamters)
{
	var codesetid  = outparamters.getValue("codesetid");
	toCodeitempen(codesetid);
}
function check_oks(outparameter)
{
   var msg = outparameter.getValue("msgs");
   if(msg=='1')
   {
       countdeta();
   }
   else
   {
   	 AjaxBind.bind(dbinitForm.codelength,'');
     alert(KJG_ZBTX_INF17);
     //var joincodename = $('joincodename');
     //joincodename.value='#$';
     var jo = $F('joincodename');
     if(jo=="#$"){
     	alert(KJG_ZBTX_INF18);
     	return;
     }
   }
}
//计算数值
function countdeta(){
	var obj = $F('joincodename');
	var hashvo=new ParameterSet();
    hashvo.setValue("obj",obj);
    var request=new Request({asynchronous:false,onSuccess:showFieldList,functionId:'1020010114'},hashvo); 
}
//保存值
function showFieldList(outparamters){
	var fieldlist=outparamters.getValue("itemlength");
	AjaxBind.bind(dbinitForm.codelength,fieldlist);
}
//只能输入数字
function isNum(i_value){
   re=new RegExp("[^0-9]");
    var s;
    if(s=i_value.match(re)){
        return false;
     }
    //字符型 最大长度 为255字符  备注型不限制最大长度    wangb 20171121 32854
    var itemname = document.getElementById("name").value;
     if(i_value >255 && itemname == 'value1'){
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
 	var itemname = document.getElementById("name").value;
 	if(itemname == 'value3' && obj.value > 10){
 		obj.value ='10';
 		return;
 	}
}
function checknum(obj){
	var itemname = document.getElementById("name").value;
	if(itemname=='value5'){
		if(obj.value==10){
			document.getElementsByName('limitlength')[0].checked=false;
			obj.value='';
	 		return;
		}
	}
}
//限制代号输入
function isNums(i_value){
    re=new RegExp("[^A-Za-z0-9]");
    var s;
    if(s=i_value.match(re)){
        return false;
     }
    return true;
}
function checkNuNS(obj){
 	if(!isNums(obj.value)){
 		obj.value='';
 		return;
 	}
}
function toC(name){
	var codeitem = "index";
	if(name=="#$"||name=="xinjian"){
		alert(KJG_ZBTX_INF19);
		return;
	}
	//toCodeitem(name,codeitem); 原来的js
	toCodeitempen(name,codeitem);
}
function sign(str){
	/*var tmp = '', c=0; 
	for(var i=0;i<str.length;i++){ 
		c = str.charCodeAt(i); 
		tmp += String.fromCharCode((c>32 && c<48)||(c>57 && c<65)||(c>90 &&c<97)||(c>122 && c<126) ? (c+0xfee0) : c) 
	} 
	if(tmp!=str)
		document.all.indexname.value=tmp */
}
/**打开职务编码设置，需要传递代码类ID*/
var return_flag;
function toCodeitempen(codesetid,codeitem,flag)//flag 用于标示是不是新建了代码类
{
	var target_url = "/pos/posbusiness/searchposbusinesstree.do?b_all=link`codesetid="+codesetid+"`codeitem="+codeitem+"`param=LEVEL";
	target_url = $URL.encode(target_url);
    var iframe_url="/general/query/common/iframe_query.jsp?src="+target_url;
    
    if(getBrowseVersion()){
		var return_vo= window.showModalDialog(iframe_url, 'newwindow', 
			"dialogWidth:750px; dialogHeight:680px;resizable:no;center:yes;scroll:yes;status:no");
	
		if(return_vo!=null&&(/[A-Z]/.test(return_vo)|| /[0-9]/.test(return_vo))){
			if(flag=='add'){
				var hashvo=new ParameterSet();
	   			hashvo.setValue("setid",return_vo);
	    		var request=new Request({asynchronous:false,onSuccess:joincodeList,functionId:'1020010112'},hashvo); 
	    		function joincodeList(outparamters){
	    			var joincodeList=outparamters.getValue("joincodeList");
					AjaxBind.bind(dbinitForm.joincodename,joincodeList);
	    			dbinitForm.joincodename.value=return_vo;
	    		}
	    	}
    		countdeta();
			//document.dbinitForm.action="/system/dbinit/fielditemlist.do?b_add=link";
			//document.dbinitForm.submit();
		}
    }else{//非ie浏览器 使用open弹窗  wangb 20190329
    	return_flag = flag;
    	var iTop = (window.screen.height-30-680)/2;       //获得窗口的垂直位置;
		var iLeft = (window.screen.width-10-750)/2;        //获得窗口的水平位置;
		window.open(iframe_url,'','height=680, width=750,top='+iTop+',left='+iLeft+',toolbar=no,menubar=no,scrollbars=no,resizable=no,location=no,status=no');
    }
	
}
//设置代码回调方法 wangb 20190329
function toCodeitempenReturn(return_vo){
	if(return_vo!=null&&(/[A-Z]/.test(return_vo)|| /[0-9]/.test(return_vo))){
			if(return_flag=='add'){
				var hashvo=new ParameterSet();
	   			hashvo.setValue("setid",return_vo);
	    		var request=new Request({asynchronous:false,onSuccess:joincodeList,functionId:'1020010112'},hashvo); 
	    		function joincodeList(outparamters){
	    			var joincodeList=outparamters.getValue("joincodeList");
					AjaxBind.bind(dbinitForm.joincodename,joincodeList);
	    			dbinitForm.joincodename.value=return_vo;
	    		}
	    	}
    		countdeta();
			//document.dbinitForm.action="/system/dbinit/fielditemlist.do?b_add=link";
			//document.dbinitForm.submit();
	}
}
function back()
{
	document.dbinitForm.action="/system/dbinit/fielditemlist.do?b_return=link";
	document.dbinitForm.submit();
}
//重置方法  jingq add 2014.6.9
function rset(){
    var icode = document.getElementById("icode").value;
    document.getElementsByName('indexcode')[0].value = icode;
    document.getElementsByName('indexname')[0].value = "";
    document.getElementsByName('content')[0].value = "";
	document.getElementById("name").value="value1";
	$('value1').style.display="block";
	$('value2').style.display="none";
	$('value22').style.display="none";
	$('value3').style.display="none";
	$('value33').style.display="none";
	$('value4').style.display="none";
	$('value5').style.display="none";
	document.getElementsByName('bitianxiang')[0].checked = false;

}
//指标体系，根据dev_flag限制新建指标项的指标代号  jingq add 2015.01.22
function testId(){
	var dev_flag = document.getElementById("dev_flag").value;
    var indexcode = document.getElementsByName('indexcode')[0].value;
	var setid = "${dbinitForm.setid}";
	if(indexcode.indexOf(setid)!=0){
		alert("指标代号前三位必须为子集代号‘"+setid+"’");
		return false;
	}
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
//【7099】业务字典和指标体系，创建的指标字母改为大写。 jingq add 2015.02.02
function checknode(){
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
</script>

<html:form action="/system/dbinit/fielditemlist">
	<html:hidden name="dbinitForm" property="dev_flag" styleId="dev_flag"/>
	<table width="500" border="0" cellpadding="0" cellspacing="0"
		align="center">
		<tr height="20">
			<!-- td width=10 valign="top" class="tableft"></td>
			<td width=130 align=center class="tabcenter">
				
			</td>
			<td width=10 valign="top" class="tabright"></td>
			<td valign="top" class="tabremain" width="500"></td> -->
			
			<td align="left" colspan="4" class="TableRow"><bean:message key="kjg.title.newzhibiaoxiang"/></td>
		</tr>
		<tr>
			<html:hidden name="dbinitForm" property="setid"/> 
			<td colspan="4" class="framestyle3">
				<table border="0" width="100%" cellspacing="0" align="center" cellpadding="0" class="ListTable">
					<tr style="height:30px;">
						<td align="left"  width='80' nowrap>
							&nbsp;<bean:message key="kjg.title.zhibiaotaihao"/>
						</td>
						<td align="left" nowrap>
							<html:text styleId="itemid" property="indexcode" name="dbinitForm" maxlength="5" onkeyup="checknode();" styleClass="text4" style="width:150px;"/>
						</td>
						<td align="left" width='80' nowrap>
							&nbsp;<bean:message key="kjg.title.indexname"/>
						</td>
						<td align="left" nowrap >
							<input type="text" name="indexname" value="" maxlength="20" onkeyup="sign(this.value);" class="text4" style="width:150px;">
						</td>
					</tr>
					<tr style="height:30px;">
						<td align="left" width='80' nowrap>
							&nbsp;<bean:message key="kjg.title.indextype"/>
						</td>
						<td align="left" nowrap>
							<select onchange="clement()" id="name" style="width:150px;">
								<option value="value1">
									<bean:message key="kjg.title.zifuxing"/>
								</option>
								<option value="value2">
							 		<bean:message key="kjg.title.daimaxing"/>
								</option>
								<option value="value3">
									<bean:message key="kjg.title.shuzixing"/>
								</option>
								<option value="value4">
									<bean:message key="kjg.title.date"/>
								</option>
								<option value="value5">
									<bean:message key="kjg.title.remark"/>
								</option>
							</select>
						</td>
						<td align="left" nowrap colspan='2'>
							<div id="value1" style="display:block;">
								<table border="0" width="100%" cellspacing="0" align="center" cellpadding="0">
									<tr>
										<td align="left" width='80' nowrap>
											&nbsp;<bean:message key="kjg.title.length"/>
										</td>
										<td align="left"  nowrap>
											<input type="text" name="itemlength" value="50" maxlength="3" onkeyup="checkNuN(this)" onblur="checknum(this)" class="text4" style="width:150px;">
											<html:hidden name="dbinitForm" property="itemtype" value="A"/> 
										</td>
									</tr>
								</table>
							</div>
							<div id="value22" style="display:none;">
								<table border="0" width="100%" cellspacing="0" align="center" cellpadding="0">
									<tr>
										<td align="left" width='80' nowrap>
											&nbsp;<bean:message key="kjg.title.length"/>
										</td>
										<td align="left"  nowrap>
											<input readonly="true" type="text" name="codelength" value="" maxlength="2" class="text4" style="width:150px;">
										</td>
									</tr>
								</table>
							</div>
							<div id="value33" style="display:none;">
								<table border="0" width="100%" cellspacing="0" align="center" cellpadding="0">
									<tr>
										<td align="left" width='80' nowrap>
											&nbsp;<bean:message key="kjg.title.zhengshuwei"/>
										</td>
										<td align="left"  nowrap>
											<input type="text" name="numberlength" value="8" maxlength="2"  onkeyup="checkNuN(this);" class="text4" style="width:150px;">
										</td>
									</tr>
								</table>
							</div>
					    </td>
					</tr>
					<tr>
					  <td align="left" colspan="4" nowrap valign="left">
							<div id="value2" style="display:none;">
								<table border="0" width="100%" cellspacing="0" align="center" cellpadding="0">
									<tr>
										<td align="left"  width='80' nowrap>
											&nbsp;<bean:message key="kjg.title.xiangguandaima"/>
										</td>
										<td align="left"  nowrap>
											<hrms:optioncollection name="dbinitForm" property="joincodeList"
												collection="list" />
											<html:select name="dbinitForm" property="joincodename" style="width:150px" onchange="changelocations(this);" value="0">
											<html:options collection="list" property="dataValue"
												labelProperty="dataName" />
											</html:select>
											<input type='button' class="mybutton" id="b_codevin"  onclick="toC($F('joincodename'))"
	 	     										value='<bean:message key="kjg.title.contvindicate"/>' />
										</td>
										<td>
											<html:hidden name="dbinitForm" property="codeitemtype" value="A"/> 
										</td>
									</tr>
								</table>
							</div>
						</td>
						<tr>
						<td align="left" colspan="2" nowrap valign="left">
							<div id="value3" style="display:none;">
								<table border="0" width="100%" cellspacing="0" align="center" cellpadding="0">
									<tr>
										<td align="left"  width='80' nowrap>
											&nbsp;<bean:message key="kjg.title.xiaoshuwei"/>
										</td>
										<td align="left" nowrap>
											<input type="text" name="decimalwidth" value="0" maxlength="1" onkeyup="checkNuN(this);" class="text4" style="width:150px;">
										</td>
										<td>
											<html:hidden name="dbinitForm" property="intitemtype" value="N"/> 
										</td>
									</tr>
								</table>
							</div>
						</td>
						</tr>
						<tr>
						<td align="left" colspan="2" nowrap valign="left">
							<div id="value4" style="display:none;">
								<table border="0" width="100%" cellspacing="0" align="center" cellpadding="0">
									<tr>
									<td align="left"  width='80' nowrap>
									&nbsp;<bean:message key="kjg.title.dategeshi"/>
									</td>
									<td align="left" nowrap>
									<hrms:optioncollection name="dbinitForm" property="dateList" collection="list" />
									<html:select name="dbinitForm" property="datelength" size="1" style="width:150px;">
									<html:options collection="list" property="dataValue" labelProperty="dataName" />
									</html:select>
									</td>
									<td>
									<html:hidden name="dbinitForm" property="dateitemtype" value="D"/> 
								    </td>
									</tr>
								</table>
							</div>
						</td>
						</tr>
						<tr>
						<td align="left" colspan="2" nowrap valign="left">
							<div id="value51" style="display:none;">
								<table border="0" width="100%" cellspacing="0" align="center" cellpadding="0">
									<tr>
										<td align="left" width='80' nowrap>
											&nbsp;<bean:message key="kjg.title.inputtype"/>
										</td>
										<td align="left" nowrap>
											<hrms:optioncollection name="dbinitForm" property="inputtypeMList" collection="list" />
											<html:select name="dbinitForm" property="inputtype" size="1" style="width:150px;">
												<html:options collection="list" property="dataValue" labelProperty="dataName" />
											</html:select>
										</td>
									</tr>
								</table>
							</div>
						</td>
						</tr>
						<tr>
						<td align="left" colspan="2" nowrap valign="left">
							<div id="value5" style="display:none;">
								<table border="0" width="100%" cellspacing="0" align="center" cellpadding="0">
									<tr>
										<td>
										<html:hidden name="dbinitForm" property="bzitemtype" value="M"/> 
										</td>
										</tr>
								</table>
							</div>
						</td>
		</tr>
		           <tr style="height:30px;">
						
                   <td align="left" width='80' valign="middle" nowrap>&nbsp;<bean:message key="kjg.title.bitianxiang"/> </td>
						<td align="left"  nowrap>
							<input type="checkbox" name="bitianxiang">
						</td>
					</tr>
					
					<tr id="limitlen" style="height:30px;display: none;">
						<td align="left" nowrap width='80' valign="middle">&nbsp;<bean:message key="kjg.title.limitlength"/> </td>
						<td align="left"   nowrap >
							<input type="checkbox" name="limitlength" onclick="limitclick();">
						</td>
					</tr>
					<tr>
						<td align="left"  colspan="4" valign="middle" nowrap>
							&nbsp;<bean:message key="kjg.title.content"/>
						</td>
					</tr>
					<tr>
						<td align="center"  nowrap colspan="4">
							<html:textarea name="dbinitForm" property="content" value="" cols="60" rows="10" styleClass="text5" style="width:88%"/>
						</td>
					</tr>
					<tr>
						<td height="10"></td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
	<table align="center">
		<tr class="list3">
						<td align="center" colspan="4" nowrap height="35px;">
						<input type="button" class="mybutton" value="<bean:message key="button.save" />" onClick="save(0);" />
						<input type="button" class="mybutton" value="<bean:message key="button.savereturn" />" onClick="save(1);" />
						<!--<html:reset styleClass="mybutton" property="reset">
								<bean:message key="button.clear" />
							</html:reset>-->
							<!-- 重置按钮   jingq  add  2014.6.9 -->
							<html:hidden name="dbinitForm" property="indexcode" styleId="icode"/>
							<input type="button" value="<bean:message key="button.clear" />" class="mybutton" onClick="rset();">
							<input type="button" name="br_approve"
								value='<bean:message key="button.return"/>' class="mybutton"
								onclick="back();">

						</td>
					</tr>
	</table>
</html:form>