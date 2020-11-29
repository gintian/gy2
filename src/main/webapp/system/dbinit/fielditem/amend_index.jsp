<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>

<hrms:themes></hrms:themes>
<script language="JavaScript" src="../../../js/showModalDialog.js"></script>
<script type="text/javascript" language="javascript">
// 初始化页面
function initShow() {
    dbinitForm.itemid.disabled = "true";
    var useflag = dbinitForm.useflag.value;
    var itemtype = dbinitForm.itemtype.value;
    var itp = itemtype.split(".");
    if (itp[0] == "") {
        alert(KJG_ZBTX_INF20);
        return;
    }
    var itemid = $F("itemid");
    var enditemid = itemid.substring(itemid.length - 2, itemid.length);
    if (enditemid == "Z1" || enditemid == "Z0") {
        dbinitForm.itemtype.disabled = "true";
        dbinitForm.itemlength.disabled = "true";
        dbinitForm.decimala.disabled = "true";
        dbinitForm.bitianxiang.disabled = "true";
        dbinitForm.date.disabled = "true";
    }   
    var len = $("len");
    var codeset = $("codeset");
    var dates = $("dates");
    var decimal = $("decimal");
    var value51 = $("value51"); 
    // 默认隐藏
    len.style.display = "none";
    codeset.style.display = "none";
    dates.style.display = "none";
    decimal.style.display = "none";
    value51.style.display = "none";
    if (itp[0] == 'A') {
        if (itp[1] == 'S') {
            len.style.display = "block";
            dbinitForm.itemlength.disabled = "";
            if (useflag == "1") {
                dbinitForm.itemtype.disabled = "true";
            }
        }else if (itp[1] == 'C') {
            len.style.display = "block";
            codeset.style.display = "block";
            if (useflag == "1") {
                dbinitForm.itemtype.disabled = "true";
                dbinitForm.code.disabled = "true";
            }
            dbinitForm.itemlength.disabled = "true";
        }
    } else if (itp[0] == 'D') {
        dates.style.display = "block";
        if (useflag == "1") {
            dbinitForm.itemtype.disabled = "true";
        }
    } else if (itp[0] == 'N') {
        len.style.display = "block";
        decimal.style.display = "block";
        if (useflag == "1") {
            dbinitForm.itemtype.disabled = "true";
        } else {
            dbinitForm.itemlength.disabled = "";
        }
    }else if (itp[0] == 'M') {
        value51.style.display = "block";
        if (useflag == "1") {
            dbinitForm.itemtype.disabled = "true";
        }
        //修改备注型指标，初始化时去掉限制最大长度属性  wangb 20171122
		var itemlength = $('itemlength');
		itemlength.removeAttribute('maxLength');
		
    }
    getitemtype1();
}

// 选择指标类型
function getitemtype1() {
	dbinitForm.itemid.disabled = "true";
	var useflag = dbinitForm.useflag.value;
	var itemtype = dbinitForm.itemtype.value;
	var itp = itemtype.split(".");
	if (itp[0] == "") {
	    alert(KJG_ZBTX_INF20);
	    return;
	}
	var len = $("len");
	var codeset = $("codeset");
	var dates = $("dates");
	var decimal = $("decimal");
	var value51 = $("value51");
	var limitlen = $('limitlen');
	value51.style.display = "none";
	if (itp[0] == 'A') {
	    if (itp[1] == 'S') {
	        len.style.display = "block";
	        codeset.style.display = "none";
	        dates.style.display = "none";
	        decimal.style.display = "none";
	        limitlen.style.display="none";
	        dbinitForm.itemlength.disabled = "";
	        if (useflag == "1") {
	            dbinitForm.itemtype.disabled = "true";
	        } else {
	            document.getElementById("itemlength").value = "50";
	        }
	    } else if (itp[1] == 'C') {
	        len.style.display = "block";
	        codeset.style.display = "block";
	        dates.style.display = "none";
	        decimal.style.display = "none";
	        limitlen.style.display="none";
	        if (useflag == "1") {
	            dbinitForm.itemtype.disabled = "true";
	            dbinitForm.code.disabled = "true";
	        } else {
	        	   // 设置为空后无法修改名称保存，因为这个是必填项。注掉此处 guodd 2015-11-20
	           // document.getElementById("itemlength").value = "";
	        }
	        dbinitForm.itemlength.disabled = "true";
	    }
	} else if (itp[0] == 'D') {
	    len.style.display = "none";
	    codeset.style.display = "none";
	    dates.style.display = "block";
	    decimal.style.display = "none";
	    limitlen.style.display="none";
	    if (useflag == "1") {
	        dbinitForm.itemtype.disabled = "true";
	    }
	} else if (itp[0] == 'N') {
	    len.style.display = "block";
	    codeset.style.display = "none";
	    dates.style.display = "none";
	    decimal.style.display = "block";
	    limitlen.style.display="none";
	    if (useflag == "1") {
	        dbinitForm.itemtype.disabled = "true";
	    } else {
	        dbinitForm.itemlength.disabled = "";
	        document.getElementById("itemlength").value = "8";
	    }
	} else if (itp[0] == 'M') {
	    value51.style.display = "block";
	   // len.style.display = "none";
	    codeset.style.display = "none";
	    dates.style.display = "none";
	    decimal.style.display = "none";
	    limitlen.style.display="";
	    if (useflag == "1") {
	        dbinitForm.itemtype.disabled = "true";
	    }
	    var limitlength = document.getElementsByName('limitlength')[0].checked;
		if(limitlength==true){
			len.style.display="block";
		}else{
			len.style.display="none";
		}
	}
}
 function limitclick(){
    var len=$('len');
	var limitlen = $('limitlen');
	var limitlength = document.getElementsByName('limitlength')[0].checked;
		if(limitlength==true){
			len.style.display="block";
			document.getElementById('itemlength').value='';//勾选 限制长度选项 默认长度为空 wangb 20170711
		}else{
			len.style.display="none";
			document.getElementById('itemlength').value=10;//不勾选 限制长度选项  默认长度为10   wangb 20170517
		}
}
function getcodelen(){
	var codelen=dbinitForm.code.value;
	if(codelen.length<1){
	alert(KJG_ZBTX_INF21);
	return;
	}
	var code=codelen.split("/");
	//dbinitForm.itemlength.value=code[1];
	var obj = code[0];
	if(obj=="newcode"){
		//toAddCodeSet(); 以前
		toAddCodeSetk();
		return;
	}
	var hashvo = new ParameterSet();
	hashvo.setValue("obj",obj);
	if(obj=="UM"||obj=="UN"||obj=="@K"){
		document.getElementById("b_codevin").setAttribute("disabled","disabled");
	}else{
		document.getElementById("b_codevin").setAttribute("disabled","");
	}
	var request=new Request({asynchronous:false,onSuccess:check_oks,functionId:'1020010123'},hashvo); 
}
/**新增代码类*/
function toAddCodeSetk()
{
	var bflag=false;
	var currname=1;
	var return_vo= window.showModalDialog("/system/codemaintence/add_edit_codeset.do?b_query=link&query=query", bflag, 
	"dialogWidth:500px; dialogHeight:300px;resizable:no;center:yes;scroll:yes;status:no");       
	if(return_vo==null)
	return ;
	toCodeitempen(return_vo.codesetid,'','add');
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
	var request=new Request({asynchronous:false,onSuccess:to_toAddCodeSet_oklo,functionId:'1010050008'},hashvo);   */
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
       //saveOk();
       countdeta();
   }
   else
   {
     alert(KJG_ZBTX_INF17);
     return;
   }
}

function saveOk(outparamters) {
	window.returnValue="aaaaa";
}
	
function getdatelen() {
	var datelen=dbinitForm.date.value;
	dbinitForm.itemlength.value=datelen;
}

function checkname()
{
	var indexname = document.getElementById("indexname").value;
	var itemlength = document.getElementById("itemlength").value;  
	var decimala = document.getElementById("decimala").value; 
	var itemid = document.getElementById("itemid").value;
	var setid = document.getElementById("setid").value;
	var itemtype = document.getElementById("itemtype").value;
	var hashvo=new ParameterSet();
	var parten = /^\s*$/;   //不能输入空格
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
	
	var reg="~`!@#$%^&*<>?'\"|;；·～！＠＃％＆×＋｛［｝］＂＇：？／＞＜，．";
	for(var i=0;i<indexname.length;i++){
		 var c=indexname.substring(i,i+1);
		 if(reg.indexOf(c)!=-1){
		 	alert("名称不能是特殊字符~`!@#$%^&*<>?'\"|;；·～！＠＃％＆×＋｛［｝］＂＇：？／＞＜，．");
		 	return false;
		 }
  	}
  	/**
  	*xus 备注型指标长度不能为空或指标长度不能为0(else)
  	*16/09/27
  	**/
  	if(itemtype!="M"){
	  	if(itemlength==null||itemlength==""){
			alert(KJG_ZBTX_INF22);
			return;
		}
  	}else{
  		if(itemlength==null||itemlength==""||itemlength==0){
			alert('备注型指标勾选了限制长度选项，长度不能设置为0或10!');//勾选限制长度  长度不能输入0或10 wangb 20170711 
			return;
		}
  	}
	if(decimala==null||decimala==""){
		alert(KJG_ZBTX_INF23);
		return;
	}
	hashvo.setValue("indexname",indexname);
	hashvo.setValue("itemid",itemid);
	hashvo.setValue("setid",setid);
	var request=new Request({method:'post',asynchronous:false,onSuccess:check_ok,functionId:'1020010117'},hashvo);
}

// 校验名称回调
function check_ok(outparameter) {
    var msg = outparameter.getValue("msg");
    if (msg == '1') {
        sav();
    } else {
        alert(KJG_ZBTX_INF24);
    }
}

// 执行保存操作
function sav() {
    var bitianxiang = document.getElementsByName('bitianxiang')[0].checked;
    var useflag = dbinitForm.useflag.value;
    var itemtype = document.getElementById("itemtype").value;
    var itemlength = document.getElementById("itemlength").value;
 
    if (itemtype == null || itemtype == "") {
        alert(KJG_ZBTX_INF20);
        return;
    } else if (itemtype == "D") {
        itemlength = document.getElementsByName("date")[0].value;
        document.getElementById("itemlength").value = itemlength;
    } else if (itemtype == "A.S") {
        if (itemlength == "" || itemlength == null || itemlength == "0") {
            alert(KJG_ZBTX_INF25);
            return;
        }
        if (useflag == "1") {
            var zheng = document.getElementById("itemlength").value;
            if (parseInt(zheng) < parseInt(yy)) {
                alert("已构库的指标，长度不能小于原始值");
                return;
            }
        }
    } else if (itemtype == "N") {
        if (itemlength == "" || itemlength == null || itemlength == "0") {
            alert(KJG_ZBTX_INF25);
            return;
        }
        if (useflag == "1") {
            var xiao = document.getElementById("decimala").value;
            var zheng = document.getElementById("itemlength").value;
            if (parseInt(xiao) < parseInt(ss)) {
                alert("已构库的指标，小数位不能小于原始值");
                return;
            }
            if (parseInt(zheng) < parseInt(yy)) {
                alert("已构库的指标，长度不能小于原始值");
                return;
            }
        }
    } else if (itemtype == "A.C") {
        var codelen = dbinitForm.code.value;
        var codelens = codelen.split(".");
        if (codelens[0] == "" || codelens[0] == null) {
            alert(KJG_ZBTX_INF21);
            return;
        }
        if (codelens == "newcode" || codelens == "") {
            alert(KJG_ZBTX_INF18);
            return;

        }
        if (itemlength == "" || itemlength == null || itemlength == "0") {
            alert(KJG_ZBTX_INF25);
            return;
        }
    }
    if (bitianxiang == true) {
        bitianxiang = '1';
    } else {
        bitianxiang = '0';
    }
    dbinitForm.action = "/system/dbinit/fielditemlist.do?b_update=update&bitianxiang=" + bitianxiang;
    dbinitForm.submit();
    //var thevo=new Object();
    //thevo.flag="true";
    //window.returnValue=thevo;
    //window.close();
}

//只能输入数字
function isNum(i_value){
    re=new RegExp("[^0-9]");
    var s;
    if(s=i_value.match(re)){
        return false;
     }
	/*字符型255的限制改为1000 guodd 2020-05-15*/
     if(i_value >1000){
     	return false;
     }
    return true;
}

function checkNuN(obj,d){
	var type = document.getElementById('itemtype').value;
	if('M'==type)//xiegh add 20170629
		return;
 	if(!isNum(obj.value)){
 		obj.value='';
 		return;
 	}
}
function checknum(obj){
	var itemtype = dbinitForm.itemtype.value;
	if(itemtype=='M'){
		if(obj.value==10){
			document.getElementsByName('limitlength')[0].checked=false;
			obj.value='';
	 		return;
		}
	}
}
function codevin(){
	var codeitem = "index";
	var co=dbinitForm.code.value;
	var cod=co.split("/");
	var s = cod[0];
	if(s==""||s=="newcode"){
		alert(KJG_ZBTX_INF19);
		return;
	}
	//toCodeitem(s,codeitem);
	toCodeitempen(s,codeitem);
}
/**打开职务编码设置，需要传递代码类ID*/
function toCodeitempen(codesetid,codeitem,flag)
{
	var iframe_url = "/pos/posbusiness/searchposbusinesstree.do?b_all=link&codesetid="+codesetid+"&codeitem="+codeitem;
	/*
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
				AjaxBind.bind(dbinitForm.code,joincodeList);
	    		dbinitForm.code.value=return_vo;
	    	}
    	}
		countdeta();
	}
	*/
	var config={ 
		width:750,
        height:450,
        resizable:"no",
        location:"no",
        scrollbars:"no",
        status:"no",
        modal:"yes",
        title:"设置",
        type:'2',
        id:""
    };
	modalDialog.showModalDialogs(iframe_url,'',config,function(return_vo){
		openReturn(return_vo,flag);
	});
}
function openReturn(return_vo,flag){
	if(return_vo!=null){
		//window.location.href=window.location.href; 
		if(flag=='add'){
			var hashvo=new ParameterSet();
	   		hashvo.setValue("setid",return_vo);
	    	var request=new Request({asynchronous:false,onSuccess:joincodeList,functionId:'1020010112'},hashvo); 
	    	function joincodeList(outparamters){
	    		var joincodeList=outparamters.getValue("joincodeList");
				AjaxBind.bind(dbinitForm.code,joincodeList);
	    		dbinitForm.code.value=return_vo;
	    	}
    	}
		countdeta();
	}
}

//计算数值
function countdeta(){
	var co=dbinitForm.code.value;
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
//保存值
function showFieldList(outparamters){
	var fieldlist=outparamters.getValue("itemlength");
	AjaxBind.bind(dbinitForm.itemlength,fieldlist);
	var setid=outparamters.getValue("obj");
	var codesetid=document.getElementById('codesetid');
    codesetid.value=setid;
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
var ss;
var yy;
function yevalue(){
	var p=document.getElementById("decimala").value;
	var y=document.getElementById("itemlength").value;
	ss=p;
	yy=y;
}

function returnto(){
	dbinitForm.action="/system/dbinit/fielditemlist.do?b_return=link&setid="+$F('setid');
	dbinitForm.submit();
	
}
</script>

<html:form action="/system/dbinit/fielditemlist">
	<html:hidden styleId="setid" name="dbinitForm" property="busiFieldVo.string(fieldsetid)"/>
	<html:hidden styleId="codesetid" name="dbinitForm" property="busiFieldVo.string(codesetid)"/>
	<table width="500" border="0" cellspacing="0" align="center" cellpadding="0">
		<tr height="20">
			<!-- td width=10 valign="top" class="tableft"></td>
			<td width=110 align=center class="tabcenter">
					
			</td>
			<td width=10 valign="top" class="tabright"></td>
			<td valign="top" class="tabremain" width="680"></td> -->
			
			<td align="left" colspan="4" class="TableRow"><bean:message key="kjg.title.reviseindex"/>	</td>
		</tr>
		<tr>
			<td class="framestyle3" colspan="4">
				<table border="0" width="100%" cellspacing="0" align="center" cellpadding="0" class="ListTable">
					<tr style="height:30px;">
						<td align="left" width='80'>
							&nbsp;<bean:message key="kjg.title.zhibiaotaihao"/>
						</td>
						<td align="left" nowrap>							
								<html:text styleId="itemid" name="dbinitForm" property="busiFieldVo.string(itemid)" styleClass="text4" style="width:150px;"></html:text>
						</td>
						<td align="left" width='80'>
							&nbsp;<bean:message key="kjg.title.indexname"/>
						</td>
						<td align="left" nowrap>
							<html:text styleId="indexname" name="dbinitForm" property="busiFieldVo.string(itemdesc)" maxlength="20" onkeyup="sign(this.value);" styleClass="text4" style="width:150px;"></html:text>
						</td>
					</tr>
					<tr style="height:30px;">
						<td align="left" width='80'>
							&nbsp;<bean:message key="kjg.title.indextype"/>
						</td>
						
						<td align="left"  nowrap>							
								<html:select  styleId="itemtype" name="dbinitForm" property="busiFieldVo.string(itemtype)"  onchange="getitemtype1();" style="width:150px;">
									<html:option value="">
										&nbsp;
									</html:option>
									<!-- 指标体系，修改指标，指标类型为A/S时保存后指标类型为空，此处修改为A.S   jingq upd 2014.09.28 -->
									<html:option value="A.S">
										<bean:message key="kjg.title.zifuxing"/>
									</html:option>
									<html:option value="A.C">
										<bean:message key="kjg.title.daimaxing"/>
									</html:option>
									<html:option value="N">
										<bean:message key="kjg.title.shuzixing"/>
									</html:option>
									<html:option value="D">
										<bean:message key="kjg.title.date"/>
									</html:option>
									<html:option value="M">
										<bean:message key="kjg.title.remark"/>
									</html:option>
								</html:select>
						</td>						
						<td align="left" nowrap colspan='2'>
						 <div id="len" style="display=block;">
						  <table border="0" width="100%" cellspacing="0" align="center" cellpadding="0">
							<tr>
							<td align="left" width='80'>
								<!-- 指标类型为数值型时，显示为整数位，其他类型显示为长度	jingq add 2014.07.18 -->
								<logic:notEqual value="N" name="dbinitForm" property="busiFieldVo.string(itemtype)">
								&nbsp;<bean:message key="kjg.title.length"/>
								</logic:notEqual>
								<logic:equal value="N" name="dbinitForm" property="busiFieldVo.string(itemtype)">
								&nbsp;<bean:message key="kjg.title.zhengshuwei"/>
								</logic:equal>
							</td>
							<td align="left"  nowrap>
							<html:text styleId='itemlength' name="dbinitForm" property="busiFieldVo.string(itemlength)" disabled="false" onkeyup="checkNuN(this)" onblur="checknum(this)" maxlength="3" styleClass="text4" style="width:150px;"></html:text>
							</td>
							</tr>
						</table>
						</div>
						</td>	
					   
					</tr>
					<tr>
					<td align="left" nowrap colspan="4" valign="left">
					<div id="codeset" style="display=block">
					<table border="0" width="100%" cellspacing="0" align="center" cellpadding="0">
					<tr>
					<td align="left"  width='80' nowrap>
							&nbsp;<bean:message key="kjg.title.xiangguandaima"/>
						</td>
						<td align="left" nowrap>
							<bean:write name="dbinitForm" property="joincodename" filter="false"/>
							<input type='button' class="mybutton" id="b_codevin"  onclick='codevin()'
	 	     										value='<bean:message key="kjg.title.contvindicate"/>' />
						</td>
						<td></td>
					</tr>
					</table>
					</div>
					<div id="dates" style="display=block">
					<table border="0" width="100%" cellspacing="0" align="center" cellpadding="0">
					<tr>
					<td align="left"  width='80'>
						&nbsp;<bean:message key="kjg.title.dategeshi"/>
					</td>
					<TD  align="left" nowrap>
					<bean:write name="dbinitForm" property="datelength" filter="false"/>
					</TD>
					</tr>
					</table>
					</div>
					<div id="decimal" style="display=block">
					<table border="0" width="100%" cellspacing="0" align="center" cellpadding="0">
					<tr>
					<td align="left"  width='80'>
							&nbsp;<bean:message key="kjg.title.xiaoshuwei"/>
						</td>
						<td align="left" colspan='3' nowrap>
							<html:text styleId='decimala' name="dbinitForm" property="busiFieldVo.string(decimalwidth)" onkeyup="checkNuN(this)" maxlength="1" styleClass="text4" style="width:150px;"></html:text>
						</td>
					</tr>
					</table>
					</div>
					<div id="value51" style="display:none;">
						<table border="0" width="100%" cellspacing="0" align="center" cellpadding="0">
							<tr>
								<td align="left"  width='80' nowrap>
									&nbsp;<bean:message key="kjg.title.inputtype"/>
								</td>
								<td align="left" nowrap>
									<hrms:optioncollection name="dbinitForm" property="inputtypeMList" collection="list" />
											<html:select name="dbinitForm" property="busiFieldVo.string(inputtype)" size="1" style="width:150px;">
												<html:options collection="list" property="dataValue" labelProperty="dataName" />
									</html:select>
								</td>
								<td></td>
							</tr>
						</table>
					</div>
					</td>												
					<tr>
						<td align="left" width='80' >
							&nbsp;<bean:message key="kjg.title.bitianxiang"/>
						</td>
	
						<td align="left"  nowrap>
							<input type="checkbox" name="bitianxiang"
								<logic:equal name="dbinitForm" property="busiFieldVo.string(reserveitem)" value="1">
									checked="true"
								</logic:equal>
							>
						</td>
					</tr>
					
					<tr id="limitlen" style="display: none;">
						<td align="left" width='80' >&nbsp;<bean:message key="kjg.title.limitlength"/> </td>
						<td align="left"   nowrap >
							<input type="checkbox" name="limitlength" onclick="limitclick();"
							<logic:equal name="dbinitForm" property="busiFieldVo.string(itemtype)" value="M">
							<logic:notEqual name="dbinitForm" property="busiFieldVo.string(itemlength)" value="10">
								checked="true"
							</logic:notEqual>
							</logic:equal>
							>
						</td>
					</tr>
					<tr>
						<td align="left"  colspan="4">
							&nbsp;<bean:message key="kjg.title.content"/>
						</td>
					</tr>
					<tr>
					<td align="center" style="padding-bottom:5px;" nowrap colspan="4">
						<html:textarea name="dbinitForm" property="busiFieldVo.string(itemmemo)" cols="60" rows="10"></html:textarea>
					</td>
					</tr>
	</table>
</td>
	<bean:define id="useflag" name="dbinitForm" property="useflag"/>
	<html:hidden name="dbinitForm" property="useflag" value="${useflag}"/>
	<script type="text/javascript" language="javascript">
		initShow();
	</script>
	<table width="50%" align="center">
		<tr>
			<td align="center" height="35px;">
					<input type="button" name="tt" class="mybutton" onclick="checkname();" value="<bean:message key="lable.func.main.return"/>"/>
					<input type='button' value='<bean:message key="button.return"/>' class="mybutton" onclick='returnto();' >
			</td>
		</tr>
	</table>
</html:form>
<script type="text/javascript" language="javascript">
	yevalue();
</script>