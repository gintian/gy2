<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
	// 在标题栏显示当前用户和日期 2004-5-10 
	String css_url = "/css/css1.css";
	UserView userView = (UserView) session
			.getAttribute(WebConstant.userView);
	if (userView != null) {
		css_url = userView.getCssurl();
		if (css_url == null || css_url.equals(""))
			css_url = "/css/css1.css";
		//out.println("<link href='"+css_url+"' rel='stylesheet' type='text/css' >");  	 
	}
	String bosflag="";
	if(userView != null)
	{
		bosflag=userView.getBosflag();
	}
	String count =request.getParameter("count");
	count = count==null? "":count;
	
	String type = request.getParameter("type_categories")==null? "":request.getParameter("type_categories");//修改统计分类 type参数值为categories wangb 201907905
%>
<script language="JavaScript" src="/js/function.js"></script>
<script type="text/javascript">
<!--
function submitT()
{
<%if("categories".equalsIgnoreCase(type)){%>
	   var  hidcategories=$F('hidcategories');
	   if(hidcategories.indexOf("\‘")>-1||hidcategories.indexOf("\”")>-1||hidcategories.indexOf("\'")>-1||hidcategories.indexOf("\"")>-1)
	   {	
			alert("分类名称不能包含\’或\"或\’或\”");
			return false;
	   }  
	   var hashvo=new ParameterSet();
	   hashvo.setValue("opflag","${staticFieldForm.opflag}"); 
   	   hashvo.setValue("infor_Flag","${staticFieldForm.infor_Flag}"); 
   	   hashvo.setValue("categories",getEncodeStr($F('hidcategories'))); 
   	   hashvo.setValue("type_categories",'categories'); 
       hashvo.setValue("type",'<bean:write name="staticFieldForm" property='type'/>'); 	
       hashvo.setValue("categories_type","categories");
       var old_categories = '${staticFieldForm.categories }'.replace(/\n/g,'\\n');
       hashvo.setValue("old_categories",getEncodeStr(old_categories));
	   var request=new Request({onSuccess:submitRe,functionId:'11080204091'},hashvo);     
<%}%>
<% if(type == null || !"categories".equalsIgnoreCase(type)){%>
  var hashvo=new ParameterSet();
  var titles=$F('stat_name');
  if(titles==null||titles=="")
  {
     alert("统计条件名称不能为空！");
    return false;
  }  
   if(titles.indexOf("\‘")>-1||titles.indexOf("\”")>-1||titles.indexOf("\'")>-1||titles.indexOf("\"")>-1)
  {	
       		alert("统计条件名称不能包含\’或\"或\’或\”");
       		return false;
  } 
  var sbase=getSbase();
  if(sbase.length==0){
  	alert("请选择人员库！");
    return false;
  }
  var hv="";
<logic:equal name="staticFieldForm" property='type' value="2" >
	var checkobj=document.getElementsByName("h");
	var h="";
	var v="";
	for(var i=0;i<checkobj.length;i++){
			if(checkobj[i].checked){
				h+=","+checkobj[i].value;
			}
	}
	if(h.length<1){
		alert("请选择横向条件!");
		return false;
	}
	checkobj=document.getElementsByName("v");
	for(var i=0;i<checkobj.length;i++){
			if(checkobj[i].checked){
				v+=","+checkobj[i].value;
			}
	}
	if(v.length<1){
		alert("请选择纵向条件!");
		return false;
	}
   var  hidcategories=$F('hidcategories');
   if(hidcategories.indexOf("\‘")>-1||hidcategories.indexOf("\”")>-1||hidcategories.indexOf("\'")>-1||hidcategories.indexOf("\"")>-1)
   {	
       		alert("分类名称不能包含\’或\"或\’或\”");
       		return false;
   }  

   var hashvo=new ParameterSet();
   hashvo.setValue("stat_name",titles);             
   hashvo.setValue("statid","${staticFieldForm.statid}");
   hashvo.setValue("stype",$F('stype'));   
   hashvo.setValue("sformula",$F('sformula')); 
   var hv=h.substring(1)+"|"+v.substring(1);
   hashvo.setValue("hv",hv);   
   hashvo.setValue("sbase",sbase);             
   hashvo.setValue("opflag","${staticFieldForm.opflag}"); 
   hashvo.setValue("infor_Flag","${staticFieldForm.infor_Flag}"); 
   hashvo.setValue("categories",getEncodeStr($F('hidcategories'))); 
   hashvo.setValue("type",'<bean:write name="staticFieldForm" property='type'/>'); 
   var request=new Request({onSuccess:submitRe,functionId:'11080204091'},hashvo);     
</logic:equal>

<logic:equal name="staticFieldForm" property='type' value="1" >
	var stype=$F('stype');
	if(stype!=0){
		var sformula = $F('sformula');
		if(sformula.length<1){
			alert("公式不能为空！");
			return false;
		}
		var hashvo=new ParameterSet();
		hashvo.setValue("sformula",getEncodeStr(sformula));  
   		var request=new Request({onSuccess:afterCheck,functionId:'11080204098'},hashvo);     
		 
		function afterCheck(outparamters){
			var flag=outparamters.getValue("flag"); 
			if("ok"==flag){
				var hashvo=new ParameterSet();
				  	hashvo.setValue("stat_name",titles);             
				   hashvo.setValue("statid","${staticFieldForm.statid}");
				   hashvo.setValue("opflag","${staticFieldForm.opflag}"); 
				   hashvo.setValue("infor_Flag","${staticFieldForm.infor_Flag}");
				   var  hidcategories=$F('hidcategories');
				   if(hidcategories.indexOf("\‘")>-1||hidcategories.indexOf("\”")>-1||hidcategories.indexOf("\'")>-1||hidcategories.indexOf("\"")>-1)
				  {	
				       		alert("分类名称不能包含\’或\"或\’或\”");
				       		return false;
				  }   
				   hashvo.setValue("categories",getEncodeStr($F('hidcategories'))); 
				   hashvo.setValue("stype",$F('stype'));   
				   hashvo.setValue("sformula",$F('sformula')); 
				   hashvo.setValue("type",'<bean:write name="staticFieldForm" property='type'/>'); 
				   hashvo.setValue("hv",hv);   
				   hashvo.setValue("sbase",sbase);             
				   var request=new Request({onSuccess:submitRe,functionId:'11080204091'},hashvo);     
			}else{
				alert(getDecodeStr(flag));
			}
		} 
	
	}else{
		var hashvo=new ParameterSet();
				  	hashvo.setValue("stat_name",titles);             
				   hashvo.setValue("statid","${staticFieldForm.statid}");
				   hashvo.setValue("opflag","${staticFieldForm.opflag}"); 
				   hashvo.setValue("infor_Flag","${staticFieldForm.infor_Flag}"); 
				   var  hidcategories=$F('hidcategories');
				   if(hidcategories.indexOf("\‘")>-1||hidcategories.indexOf("\”")>-1||hidcategories.indexOf("\'")>-1||hidcategories.indexOf("\"")>-1)
				  {	
				       		alert("分类名称不能包含\’或\"或\’或\”");
				       		return false;
				  }  
				   hashvo.setValue("categories",getEncodeStr($F('hidcategories'))); 
				   hashvo.setValue("stype",$F('stype'));   
				   hashvo.setValue("sformula",$F('sformula')); 
				   hashvo.setValue("type",'<bean:write name="staticFieldForm" property='type'/>'); 
				   hashvo.setValue("hv",hv);   
				   hashvo.setValue("sbase",sbase);             
				   var request=new Request({onSuccess:submitRe,functionId:'11080204091'},hashvo); 
	
	}
</logic:equal>
<%}%>
}

function getSbase(){
	var sbases = document.getElementsByName("sbase");
	var sbase="";
	for(var i=0;i<sbases.length;i++){
		if(sbases[i].checked){
			sbase+=","+sbases[i].value;
		}
	}
	if(sbase.length>1)
		sbase=sbase.substring(1);
	return sbase;
}
function submitRe(outparamters)
{
  var opflag=outparamters.getValue("opflag"); 
  if(opflag=="true")
  {
      alert("操作成功！");
      var vo=new Object();
      vo.flag="true";
      var action=outparamters.getValue("action");
      vo.action=action;
      var text=outparamters.getValue("text");
      vo.text=text;
      var uid=outparamters.getValue("uid");
      vo.uid=uid;
      var xml=outparamters.getValue("xml");
      vo.xml=xml;
      var categories=outparamters.getValue("categories");
      vo.categories=getDecodeStr(categories);
      vo.oldcategories='${staticFieldForm.categories }';
      if(getBrowseVersion())
      	window.returnValue=vo;
      else
      	parent.opener.openReturn(vo,'<%=count%>');
      winclose()
  }else
  {
     alert("操作失败！")
  }
}
function screen(obj,flag)
{
  var targetvalue=document.getElementById(flag); 
  targetvalue.value=obj.value; 
}
function   addDict(obj,event,flag)
{ 
   var evt = event ? event : (window.event ? window.event : null);
   var np=   evt.keyCode; 
   if(np==38||np==40){ 
   
   } 
   var textv=obj.value;
   var aTag;
   	aTag = obj.offsetParent; 
   if(textv==null||textv=="")
	   return false;
   textv=textv.toLowerCase();  
   var un_vos=document.getElementsByName(flag+"_value");
   if(!un_vos)
		return false;
   var unStrs=un_vos[0].value;	
   var unArrs=unStrs.split(",");
   var   c=0;
   var   rs   =new   Array();
   for(var i=0;i<unArrs.length;i++)
   {
		 var un_str=unArrs[i];
		 if(un_str)
		 {
		     if(un_str.indexOf(textv)!=-1)
	         {
			     rs[c]="<tr id='tv' name='tv'><td id='al"+c+"'  onclick=\"onV("+c+",'"+flag+"')\"  style='height:15;cursor:pointer' onmouseover='alterBg("+c+",0)' onmouseout='alterBg("+c+",1)' nowrap class=tdFontcolor>"+un_str+"</td></tr>"; 
                 c++;
		     }
		 
		 }
        
	}
    resultuser=rs.join("");
    if(textv.length==0){ 
       resultuser=""; 
    } 
    document.getElementById("dict").innerHTML="<table   width='100%' class='div_table'  cellpadding='2' border='0'  bgcolor='#FFFFFF'   cellspacing='2'>"+resultuser+"</table>";//???????????????? 
    document.getElementById('dict').style.display = "block";
    document.getElementById('dict').style.position="absolute";	
	document.getElementById('dict').style.left=aTag.offsetLeft;
   	document.getElementById('dict').style.top=aTag.offsetTop+20;
} 
function onV(j,flag){
   var  o =   document.getElementById('al'+j).innerHTML; 
   document.getElementById(flag).value=o; 
   document.getElementById(flag+"select").value=o;
   document.getElementById('dict').style.display = "none";
} 
function   alterBg(j,i){
    var   o   =   document.getElementById('al'+j); 
    if(i==0) 
       o.style.backgroundColor   ="#3366cc"; 
    else   if(i==1) 
       o.style.backgroundColor   ="#FFFFFF"; 
}
function hiddendict(){
	if(document.getElementById('dict'))
		document.getElementById('dict').style.display = 'none';
}
var returnFormula;
function function_Wizard(busi,formula){
    var thecodeurl ="/org/autostatic/mainp/function_Wizard.do?b_query=link&busi="+busi; 
    if(getBrowseVersion()){
    	var return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:400px; dialogHeight:430px;resizable:no;center:yes;scroll:yes;status:no");
    	if(return_vo!=null)
			symbol(formula,return_vo);
    }else{
    	returnFormula = formula;
    	//兼容非IE浏览器 弹窗替换用 open弹窗  wangb 20171205
		var iTop = (parent.parent.window.screen.height - 430) / 2;  //获得窗口的垂直位置
		var iLeft = (parent.parent.window.screen.width - 400) / 2; //获得窗口的水平位置 
		window.open(thecodeurl,"","width=400px,height=430px,resizable=no,scroll=no,status=no,left="+iLeft+"px,top="+iTop+"px");
    }
    
}
function openReturn(return_vo){
if(return_vo && return_vo!=null)
	symbol(returnFormula,return_vo);
}
function symbol(editor,strexpr){
	var expr_editor=document.getElementById(editor);
	expr_editor.focus();
	var element = document.selection;
	if (element!=null) {
		var rge = element.createRange();
		if (rge!=null)	
		rge.text=strexpr;
	}else{
		var word = expr_editor.value;
				var _length=strexpr.length;
				var startP = expr_editor.selectionStart;
				var endP = expr_editor.selectionEnd;
				var ddd=word.substring(0,startP)+strexpr+word.substring(endP);
		    	expr_editor.value=ddd;
        		expr_editor.setSelectionRange(startP+_length,startP+_length);
	}
}

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
function changeRun(runvalue,itemid,itemname){
	var runflag = runvalue.value;
	if(runflag==1){
		var standid = selectStandard(itemname);
		standid=standid!=null?standid:"";
		linkIframe(standid);
	}else if(runflag==2){
		var taxid = selectScale();
		if(taxid!=null&&taxid.length>0){
			linkScale(taxid);
		}else{
			runvalue.value=0;
			runflag=0;
		}
	}else{
		changebox(itemid,runflag,itemname);
	}
	runFlagCheck(runflag);
}
function changebox(checkvalue,runf,itemname){
	var useflag = '0';
	var usef = document.getElementById(checkvalue);
	if(usef!=null&&usef.length>0){
		if(usef.checked){
			useflag='1';
		}else{
			useflag='0';
		}
	}else{
		useflag = '';
	}
	if(checkvalue==null&&checkvalue.length<1){
		checkvalue="";	
	}
	document.getElementById("item").value=checkvalue;
	itemname=itemname!=null?itemname:'';
	document.getElementById("itemname").value=itemname;
	var salaryid = document.getElementById("salaryid").value
	var hashvo=new ParameterSet();
	hashvo.setValue("salaryid",salaryid);
	hashvo.setValue("itemid",checkvalue);
	hashvo.setValue("useflag",useflag);
	hashvo.setValue("runflag",runf);
	
	
	var request=new Request({asynchronous:false,onSuccess:getformulavalue,functionId:'3020060002'},hashvo);
	checkvalue=checkvalue+"";
	if(checkvalue!=null&&checkvalue.length>0){
		tr_bgcolor(checkvalue);
	}
}
function getformulavalue(outparamters){
	var formulavalue= outparamters.getValue("formulavalue");
	var runflag = outparamters.getValue("runflag");
	var standid = outparamters.getValue("standid");
	if(runflag==1){
		linkIframe(standid);
	}else if(runflag==2){
		linkScale(standid);
	}
	runFlagCheck(runflag);
	
	document.getElementById("sformula").value=getDecodeStr(formulavalue);
}
function runFlagCheck(runflag){
	if(runflag==0){
		toggles("expression");
		hides("standard");
		hides("ratetable");
	}else if(runflag==1){
		hides("expression");
		toggles("standard");
		hides("ratetable");
	}else if(runflag==2){
		toggles("ratetable");
		hides("standard");
		hides("expression");
	}else{
		toggles("expression");
		hides("standard");
		hides("ratetable");
	}
}
function getItemList(val){
	var in_paramters="stype"+val;
    var request=new Request({method:'post',asynchronous:false,parameters:in_paramters,onSuccess:showCodeFieldList,functionId:'3020050012'});
}

function changeCodeValue(){
  	var item=$F("fielditemid");
  	if(item==null||item==undefined||item.length<1){
  		return;
  	}
  	var itemid = item.split(":");
    symbol('sformula',itemid[1]);
	//var in_paramters="itemid="+itemid[0];
    //var request=new Request({method:'post',asynchronous:false,parameters:in_paramters,onSuccess:showCodeFieldList,functionId:'3020050012'});
}
function settemp(){
	var salaryid=document.getElementById("salaryid").value;
	var thecodeurl = "/gz/gz_accounting/iframvartemp.jsp?state="+salaryid;
   	var return_vo= window.showModalDialog(thecodeurl,"window2",
   						"dialogWidth:900px;dialogHeight:550px;resizable:no;center:yes;scroll:yes;status:no");
    if(return_vo!=null){
    	var hashvo=new ParameterSet();
    	hashvo.setValue("salaryid",salaryid);
    	hashvo.setValue("itemid",return_vo);
    	var request=new Request({method:'post',asynchronous:false,
     		onSuccess:setItemList,functionId:'3020060021'},hashvo);
    }
}
function setItemList(outparamters){
	var itemlist=outparamters.getValue("itemlist");
	var itemid = outparamters.getValue("itemid");
	if(itemlist.length>0){
		AjaxBind.bind(formulaForm.itemid,itemlist);
		document.getElementById("itemid").value=itemid;
		var arr = itemid.split(":");
		if(arr.length==2){
			symbol('formula',arr[1]);
		}
	}
}
function showCodeFieldList(outparamters){
	var codelist=outparamters.getValue("codelist");
	if(codelist!=null&&codelist.length>1){
		toggles("codeview");
		AjaxBind.bind(busiMaintenceForm.codesetid_arr,codelist);
	}else{
		hides("codeview");
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
		symbol('sformula',"\""+codeid+"\"");
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
function getCheck(){
	var checks = 0;
	var tablevos=document.getElementsByTagName("input");
	for(var i=0;i<tablevos.length;i++){
	    if(tablevos[i].type=="checkbox"){
	    	alert(tablevos[i].name);
	    	if(tablevos[i].checked){
	    		checks=1
	    	}
	    	break;
		}
    }
    return checks;
}

function linkScale(taxid){
	var salaryid=document.getElementById("salaryid").value;
	var itemid=document.getElementById("item").value;
	document.iframe_rate.location.href="/gz/templateset/tax_table/initTaxDetailTable.do?b_init=init&taxid="+taxid+"&salaryid="+salaryid+"&itemid="+itemid;
}
function linkIframe(standid){
	standid=standid!=null&&standid.length>0?standid:"";
	document.iframe_user.location.href="/gz/formula/standard.do?b_query=init&opt=edit&standardID="+standid;
}

function addFormula(){
	var salaryid=document.getElementById("salaryid").value;
    var thecodeurl ="/gz/gz_accounting/addformula.do?b_query=link&salaryid="+salaryid; 
    var return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:400px; dialogHeight:200px;resizable:no;center:yes;scroll:yes;status:no");
	if(return_vo!=null){
    	 var thecodeurl ="/gz/formula/viewformula.do?b_query=link&salaryid="+salaryid+"&itemid="+return_vo; 
    	 window.location.href=thecodeurl;
    }
}
function delProject(itemid){
	var itemid=document.getElementById("item").value;
	var salaryid = document.getElementById("salaryid").value;
	if(itemid==null&&itemid.length<1&&salaryid==null&&salaryid.length<1){
		return;
	}
	 if(!ifdel()){
    	return ;
    }
	var hashvo=new ParameterSet();
	hashvo.setValue("salaryid",salaryid);
	hashvo.setValue("itemid",itemid);
	
	var request=new Request({method:'post',asynchronous:false,
     	onSuccess:del,functionId:'3020060006'},hashvo);
}
function del(outparamters){
	var base=outparamters.getValue("base");
	if(base=='ok'){
		reflesh();
	}else{
		alert("<bean:message key='gz.formula.del.project.failure'/>");;
	}
}
function reflesh(){
	var salaryid=document.getElementById("salaryid").value;
    var thecodeurl ="/gz/formula/viewformula.do?b_query=link&salaryid="+salaryid; 
    window.location.href=thecodeurl;
} 
function sorting(){
	var salaryid=document.getElementById("salaryid").value;
	document.location.href="/gz/formula/sorting.do?b_query=link&salaryid="+salaryid;
}
function setcond(){
	var salaryid=document.getElementById("salaryid").value;
	var item=document.getElementById("item").value;
  	var hashvo=new ParameterSet();
	hashvo.setValue("salaryid",salaryid);
	hashvo.setValue("item",item);
	var In_paramters="flag=1"; 	
	var request=new Request({method:'post',asynchronous:false,
		parameters:In_paramters,onSuccess:getcond,functionId:'3020060019'},hashvo);				
}
function getcond(outparamters){
	var conditions = outparamters.getValue("conditions");
	var salaryid=document.getElementById("salaryid").value;
	var cond = condiTions(conditions,salaryid);
	if(cond!=null){
		savecond(cond);
	}
}
function savecond(cond){
	var hashvo=new ParameterSet();
	var salaryid = document.getElementById("salaryid").value;
	hashvo.setValue("salaryid",salaryid);
		
	var itemid = document.getElementById("item").value;
	hashvo.setValue("item",itemid);
		
	hashvo.setValue("conditions",cond);
	var request=new Request({method:'post',asynchronous:false,functionId:'3020060010'},hashvo);
}
function savemula(){
	var formula=document.getElementById("sformula").value;
	var itemname = '${busiMaintenceForm.fielditemid}';
  		var hashvo=new ParameterSet();
	    hashvo.setValue("c_expr",getEncodeStr(formula));
	    hashvo.setValue("itemid",itemname);
	    hashvo.setValue("salaryid",'');
	    hashvo.setValue("itemtype",'');
	   	var In_paramters="flag=1"; 	
		var request=new Request({method:'post',asynchronous:false,
			parameters:In_paramters,onSuccess:resultCheckExpr,functionId:'3020060020'},hashvo);	
}

function resultCheckExpr(outparamters){
  	var info = outparamters.getValue("info");
	if(info=="ok"){
		var formula=document.getElementById("sformula").value;
		//formula = getEncodeStr(formula);
		window.returnValue=formula;
		window.close();
	}else{
		if(info.length<4){
			var formula=document.getElementById("sformula").value;
			alert(formula+" "+SYNTAX_ERROR+"!");
		}else{
			alert(getDecodeStr(info));
		}
	}
}
function selectStandard(itemname){
	document.getElementById("itemname").value=itemname;
	var salaryid=document.getElementById("salaryid").value;
	var itemid = document.getElementById("item").value;
    var thecodeurl ="/gz/formula/selectstandard.do?b_query=link&itemname="+itemname+"&salaryid="+salaryid+"&item="+itemid; 
    var return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:400px; dialogHeight:230px;resizable:no;center:yes;scroll:yes;status:no");
    if(return_vo!=null){
  	 	return return_vo;
  	}
}

function changeshade(val){
	if(val==0){
		document.all.ly.style.display="block";   
		document.all.ly.style.width=document.body.clientWidth-20; 
		/* 
		if(navigator.appName.indexOf("Microsoft")!= -1){
			//员工管理，历史时点，修改统计项目页面样式不对   jingq add 2014.10.27。
			var b_version=navigator.appVersion;
			var version=b_version.split(";");
			var trim_Version=version[1].replace(/[ ]/g,"");
			if(trim_Version.indexOf("MSIE7")==-1){
				document.all.ly.style.height=document.body.clientHeight-310;
			} else {
				//IE11version为MSIE7.0,IE7不支持document.documentMode
				if(document.documentMode){
					document.all.ly.style.height=document.body.clientHeight-340;
				} else {
					document.all.ly.style.height=document.body.clientHeight-310;
				}
			}
		} else {
			document.all.ly.style.height=document.body.clientHeight-360;
		}*/
		if(getBrowseVersion() && getBrowseVersion()!=10){
			document.all.ly.style.height='290px';//盖住了底部按钮，无法点击 wangbo updated 2019-09-20  bug 53652
		}else{
			document.all.ly.style.height='240px';
		}
		$('wizard').disabled="disabled";
		$('wizard').setAttribute('style','background:#f7f1f1;color:#054977;opacity:0.5;');
		$('sformula').value="";
		$("fielditemid").value="";
	}else{
		document.all.ly.style.display="none";  
		$('wizard').disabled="";
		$('wizard').setAttribute('style','');
	}
}
//-->
</script>
<style>
<!--
.div_table {
	border-width: 1px;
	BORDER-BOTTOM: #aeac9f 1pt solid;
	BORDER-LEFT: #aeac9f 1pt solid;
	BORDER-RIGHT: #aeac9f 1pt solid;
	BORDER-TOP: #aeac9f 1pt solid;
}

.tdFontcolor {
	text-decoration: none;
	Font-family: ????;
	font-size: 12px;
	height:20px;
	align:"center"
}
.btn1 {
 BORDER-RIGHT: #7b9ebd 1px solid;
 PADDING-RIGHT: 0px;
 BORDER-TOP: #7b9ebd 1px solid; 
 PADDING-LEFT: 0px; FONT-SIZE: 12px; 
 BORDER-LEFT: #7b9ebd 1px solid; 
 CURSOR: hand; 
 COLOR: black; 
 PADDING-TOP: 1px; 
 PADDING-BOTTOM: 0px;
 BORDER-BOTTOM: #7b9ebd 1px solid;
 border:#0042A0 1px solid;
 background-image:url(/images/button.jpg);	
}
.btn2 {
 BORDER-RIGHT: #7b9ebd 1px solid;
 PADDING-RIGHT: 3px; 
 BORDER-TOP: #7b9ebd 1px solid; 
 PADDING-LEFT: 3px; FONT-SIZE: 12px; 
 BORDER-LEFT: #7b9ebd 1px solid; 
 CURSOR: hand; 
 COLOR: black; 
 PADDING-TOP: 1px; 
 PADDING-BOTTOM: 0px;
 BORDER-BOTTOM: #7b9ebd 1px solid;
 border:#0042A0 1px solid;
 background-image:url(/images/button.jpg);	
}
.btn3 {
BORDER-RIGHT: #7b9ebd 1px solid;
 PADDING-RIGHT: 2px;
 BORDER-TOP: #7b9ebd 1px solid; 
 PADDING-LEFT: 2px; FONT-SIZE: 12px; 
 BORDER-LEFT: #7b9ebd 1px solid; 
 CURSOR: hand; 
 COLOR: black; 
 PADDING-TOP: 1px; 
 PADDING-BOTTOM: 0px;
 BORDER-BOTTOM: #7b9ebd 1px solid;
 border:#0042A0 1px solid;
 background-image:url(/images/button.jpg);	
}

#scroll_box {
	border: 1px solid #eee;
	height: 280px;
	width: 270px;
	overflow: auto;
	margin: 1em 1;
}

.ListTablex {
	border-collapse: collapse;
	BORDER-BOTTOM: medium none;
	BORDER-TOP: #94B6E6 2px solid;
	BORDER-LEFT:1px solid #C4D8EE;
	BORDER-RIGHT:1px solid #C4D8EE;
	margin-top: 5px;
}
.btn {
	height: 23px;
	width: 18px;
}
-->
</style>
<hrms:themes />
<%if("hcm".equalsIgnoreCase(bosflag)){ %>
<style>
.StaticTable {
	width:expression(document.body.clientWidth-10);
	margin-left:-5px;
}
.TableRow {
	border-left:medium none;
	border-right:medium none;
}
</style>
<%}else{ %>
<style>
.StaticTable {
	margin-top: 10px;
	width:expression(document.body.clientWidth-10);
	margin-left:-5px;
}
.TableRow {
	border-left:medium none;
	border-right:medium none;
}
</style>
<%} %>
<body onclick="hiddendict();">
	<html:form action="/general/static/commonstatic/editstatic/history">
	<% if(type == null || !"categories".equalsIgnoreCase(type)){%>
		<table border="0" cellspacing="0" align="center" cellpadding="0" style="margin:0 auto"
			class="StaticTable">
			<tr>
				<td colspan="2" class="framestyle1">
					<table width="100%" border="0" cellspacing="0" align="center"
						cellpadding="0">
						<tr align="left">
							<td valign="middle" class="TableRow" colspan="2">统计项目</td>
						</tr>
						<tr>
							<td height="10">
							</td>
						</tr>
						<tr align="left">
							<td colspan="2" class="list3">
								<table>
									<tr>
										<td align="right" width="52px">名&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;称&nbsp;</td>
										<td>
											<html:text name="staticFieldForm"
									style="height:20px;width:353px;" property='stat_name' size="30"
									maxlength='30' styleClass="text4"/>
										</td>
									</tr>
								</table>
							</td>
						</tr>
						<tr>
							<td height="10px">
							</td>
						</tr>
						<tr>
							<td colspan="2">
								<table>
									<tr>
										<td align="right" width="52px">分类名称&nbsp;</td>
										<td>
											<div style="position: absolute; z-index: 1;border:1px solid; width: 354.5px;height: 22px;margin-top:-12px;" class="common_border_color">
											 	<html:select name="staticFieldForm" property='categories'
													styleId="hidcategoriesselect"
													style="position:absolute;width:353px;height:18px;clip:rect(0 353 20 337);border:none;"
													onchange="screen(this,'hidcategories');" onfocus=''>
													<option value=""></option>
													<html:optionsCollection property="catelist" value="dataValue"
														label="dataName" />
												</html:select>
											 <input name=categories id='hidcategories'
													style="position: absolute;border:none;border-right:1px solid;line-height:20px; width: 337px; height: 20px;"
													value='${staticFieldForm.categories }'
													onkeyup="addDict(document.getElementById('hidcategories'),event,'hidcategories');" class="common_border_color">
											
											 <input type="hidden" name="hidcategories_value"
												value='${staticFieldForm.hidcategories }' class="inputtext"/>
											</div>
										</td>
									</tr>
								</table>
							</td>
						</tr>
						<tr height="20">
							<td>
								&nbsp;
							</td>
						</tr>
						<tr>
							<td colspan="2">
								<fieldset align="center" style="width: auto;margin-left:5px;margin-right:5px;">
									<legend>
										人员库
									</legend>
									<div id="querynorm1"
										style="height: 50px; text-align: left; padding-left: 10px; overflow-x: hidden; overflow-y: auto;">
										${staticFieldForm.sbasehtml }
										<html:hidden name="staticFieldForm" property='sbase' />
									</div>
								</fieldset>
							</td>
						</tr>
						<tr height="20">
							<td>
								&nbsp;
							</td>
						</tr>
						<logic:equal value="1" name="staticFieldForm" property='type'>
							<tr>
								<td height="21" align="left">
									&nbsp;统计方式
									<html:select property="stype" name="staticFieldForm"
										onchange="changeshade(this.value);getItemList(this.value);">
										<option value="0"
											<logic:equal value="0" property="stype" name="staticFieldForm">selected="selected"</logic:equal>>
											个&nbsp;&nbsp;数
										</option>
										<option value="1"
											<logic:equal value="1" property="stype" name="staticFieldForm">selected="selected"</logic:equal>>
											求&nbsp;&nbsp;和
										</option>
										<option value="2"
											<logic:equal value="2" property="stype" name="staticFieldForm">selected="selected"</logic:equal>>
											最小值
										</option>
										<option value="3"
											<logic:equal value="3" property="stype" name="staticFieldForm">selected="selected"</logic:equal>>
											最大值
										</option>
										<option value="4"
											<logic:equal value="4" property="stype" name="staticFieldForm">selected="selected"</logic:equal>>
											平均值
										</option>
									</html:select>
								</td>
								<td height="21" align="right">
									<input name="wizard" type="button" id="wizard"
										value='<bean:message key="kq.formula.function"/>'
										onclick="function_Wizard('${busiMaintenceForm.itemsetid }','sformula');"
										Class="mybutton">
								</td>
							</tr>
							<tr>
								<td colspan="2">
									<fieldset align="center" style="width: auto;margin-left:5px;margin-right:5px;margin-bottom:5px;">
										<legend>
											<bean:message key="hmuster.label.expressions" />
										</legend>
										<table width="100%" border="0" height="100">
											<tr>
												<td colspan="2" align="center">
													<html:textarea name="staticFieldForm" property="sformula"
														cols="56" rows="7" styleId="sformula"
														style="font-size:13px;height:60px!important;height:;width:418px;"></html:textarea>
												</td>
											</tr>

											<tr>
												<td width="52%" align="center">
													<fieldset align="center" style="width: auto;margin-left:2px;">
														<legend>
															<bean:message key='org.maip.reference.projects' />
														</legend>
														<table width="100%" border="0" height="112">
															<tr height="30">
																<td>
																	<table width="100%" border="0">
																		<tr>
																			<td height="30">
																				<bean:message key="gz.formula.project" />
																				<hrms:optioncollection name="staticFieldForm"
																					property="itemlist" collection="list" />
																				<html:select name="staticFieldForm"
																					property="fielditemid"
																					onchange="changeCodeValue();" style="width:140">
																					<html:options collection="list"
																						property="dataValue" labelProperty="dataName" />
																				</html:select>
																			</td>
																		</tr>
																	</table>
																</td>
															</tr>
															<tr height="30">
																<td>
																	<span id="codeview" style="display: none">
																		<table width="100%" border="0">
																			<tr>
																				<td height="30">
																					<bean:message key="conlumn.codeitemid.caption" />
																					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
																					<select name="codesetid_arr"
																						onchange="getCodesid();"
																						style="width: 142; font-size: 9pt">
																					</select>
																				</td>
																			</tr>
																		</table> </span>
																</td>
															</tr>
														</table>
													</fieldset>
												</td>
												<td width="48%">
													<fieldset align="center" style="width: auto;margin-right:2px;">
														<legend>
															<bean:message key="gz.formula.operational.symbol" />
														</legend>
														<table width="80%" border="0">
															<tr>
																<td>
																	<table width="100%" border="0">
																		<tr>
																			<td>
																				<input type="button" value="0"
																					onclick="symbol('sformula','0');" class="smallbutton" style="width:100%">
																			</td>
																			<td>
																				<input type="button" value="1"
																					onclick="symbol('sformula','1');" class="smallbutton" style="width:100%">
																			</td>
																			<td>
																				<input type="button" value="2"
																					onclick="symbol('sformula','2');" class="smallbutton" style="width:100%">
																			</td>
																			<td>
																				<input type="button" value="3"
																					onclick="symbol('sformula','3');" class="smallbutton" style="width:100%">
																			</td>
																			<td>
																				<input type="button" value="4"
																					onclick="symbol('sformula','4');" class="smallbutton" style="width:100%">
																			</td>
																			<td>
																				<input type="button" value="("
																					onclick="symbol('sformula','(');" class="smallbutton" style="width:100%">
																			</td>
																			<td colspan="2">
																				<input type="button"
																					value="<bean:message key='gz.formula.if'/>"
																					onclick="symbol('sformula','<bean:message key='gz.formula.if'/>');"
																					class="smallbutton" style="width:100%">
																			</td>
																		</tr>
																		<tr>
																			<td>
																				<input type="button" value="5"
																					onclick="symbol('sformula','5');" class="smallbutton" style="width:100%">
																			</td>
																			<td>
																				<input type="button" value="6"
																					onclick="symbol('sformula','6');" class="smallbutton" style="width:100%">
																			</td>
																			<td>
																				<input type="button" value="7"
																					onclick="symbol('sformula','7');" class="smallbutton" style="width:100%">
																			</td>
																			<td>
																				<input type="button" value="8"
																					onclick="symbol('sformula','8');" class="smallbutton" style="width:100%">
																			</td>
																			<td>
																				<input type="button" value="9"
																					onclick="symbol('sformula','9');" class="smallbutton" style="width:100%">
																			</td>
																			<td>
																				<input type="button" value=")"
																					onclick="symbol('sformula',')');" class="smallbutton" style="width:100%">
																			</td>
																			<td colspan="2">
																				<input type="button"
																					value="<bean:message key='gz.formula.else'/>"
																					onclick="symbol('sformula','<bean:message key='gz.formula.else'/>');"
																					class="smallbutton" style="width:100%">
																			</td>
																		</tr>
																		<tr>
																			<td>
																				<input type="button" value="+"
																					onclick="symbol('sformula','+');" class="smallbutton" style="width:100%">
																			</td>
																			<td>
																				<input type="button" value="-"
																					onclick="symbol('sformula','-');" class="smallbutton" style="width:100%">
																			</td>
																			<td>
																				<input type="button" value="*"
																					onclick="symbol('sformula','*');" class="smallbutton" style="width:100%">
																			</td>
																			<td>
																				<input type="button" value="/"
																					onclick="symbol('sformula','/');" class="smallbutton" style="width:100%">
																			</td>
																			<td>
																				<input type="button" value="\"
																					onclick="symbol('sformula','\\');" class="smallbutton" style="width:100%">
																			</td>
																			<td>
																				<input type="button" value="%"
																					onclick="symbol('sformula','%');" class="smallbutton" style="width:100%">
																			</td>
																			<td>
																				<input type="button"
																					value="<bean:message key='general.mess.and'/>"
																					onclick="symbol('sformula','<bean:message key='general.mess.and'/>');"
																					class="smallbutton" style="width:100%">
																			</td>
																			<td>
																				<input type="button"
																					value="<bean:message key='general.mess.or'/>"
																					onclick="symbol('sformula','<bean:message key='general.mess.or'/>');"
																					class="smallbutton" style="width:100%">
																			</td>
																		</tr>
																		<tr>
																			<td>
																				<input type="button" value="="
																					onclick="symbol('sformula','=');" class="smallbutton" style="width:100%">
																			</td>
																			<td>
																				<input type="button" value="&gt;"
																					onclick="symbol('sformula','&gt;');" class="smallbutton" style="width:100%">
																			</td>
																			<td>
																				<input type="button" value="&lt;"
																					onclick="symbol('sformula','&lt;');"
																					class="smallbutton" style="width:100%">
																			</td>
																			<td>
																				<input type="button" value="&lt;&gt;"
																					onclick="symbol('sformula','&lt;&gt;');"
																					class="smallbutton" style="width:100%">
																			</td>
																			<td>
																				<input type="button" value="&lt;="
																					onclick="symbol('sformula','&lt;=');"
																					class="smallbutton" style="width:100%">
																			</td>
																			<td>
																				<input type="button" value="&gt;="
																					onclick="symbol('sformula','&gt;=');"
																					class="smallbutton" style="width:100%">
																			</td>
																			<td>
																				<input type="button" value="~"
																					onclick="symbol('sformula','~');" class="smallbutton" style="width:100%">
																			</td>
																			<td>
																				<input type="button"
																					value="<bean:message key='kq.wizard.not'/>"
																					onclick="symbol('sformula','<bean:message key='kq.wizard.not'/>');"
																					class="smallbutton" style="width:100%">
																			</td>
																		</tr>
																	</table>
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
						</logic:equal>
						<logic:equal value="2" name="staticFieldForm" property='type'>
							<tr>
								<td width="30%" height="240" align="center">

									<table width="90%" height="240" border="0">
										<tr>
											<td height="90%" align="center" valign="top">
												<fieldset align="center" style="width: auto">
													<legend>
														横向条件
													</legend>
													<table width="90%" height="240" border="0">
														<tr>
															<td height="90%" align="center" valign="top">
																<div id="itemtable"
																	style="height: 230; overflow-x: hidden; overflow-y: auto;">
																	${staticFieldForm.hformulatable}
																</div>
															</td>
														</tr>
													</table>
												</fieldset>
											</td>
										</tr>
									</table>
								</td>
								<td width="30%" height="240" align="center">

									<table width="90%" height="240" border="0">
										<tr>
											<td height="90%" align="center" valign="top">
												<fieldset align="center" style="width: atuo">
													<legend>
														纵向条件
													</legend>
													<table width="90%" height="240" border="0">
														<tr>
															<td height="90%" align="center" valign="top">
																<div id="itemtable"
																	style="height: 230; overflow-x: hidden; overflow-y: auto;">
																	${staticFieldForm.vformulatable}
																</div>
															</td>
														</tr>
													</table>
												</fieldset>
											</td>
										</tr>
									</table>

								</td>
							<tr>
						</logic:equal>
					</table>
				</td>
			</tr>
		</table>
		<div id="dict"
			style="display: none; z-index: +999; position: absolute; height: 100px; width: 353px; overflow: auto;"></div>
		<div id="ly"
			style="position: absolute; top: 255px !important; left:14px;top: 245px; FILTER: alpha(opacity = 3); -moz-opacity: 0.03; opacity: 0.03; background-color: #000; z-index: 2; left: 0px; display: none;"></div>
		<% }else{ %><%--修改统计分类   wangb 20190705 --%>
			<table width="98%" style="margin-left:2px;"border="0" cellspacing="0" align="center" cellpadding="0" class="StaticTable">
			<tr>
			<td colspan="2" class="framestyle1">
				<table width='100%' border="0" cellspacing="0" align="center" cellpadding="0">
					<tr align="left">
					<td colspan="2" valign="middle" class="TableRow"
						style="border-left: 0px; border-right: 0px;">
								修改分类名称
					</td>
					</tr>
					<tr align="center" height="80px">
						<td align="right" width="20%px">分类名称&nbsp;</td>
						<td align="left">
						<input name=categories id='hidcategories'
							style="border:1px #C4D8EE solid;width:220px;"
							value='${staticFieldForm.categories }'
							onkeyup="addDict(document.getElementById('hidcategories'),event,'hidcategories');"/>
											
							<input type="hidden" name="hidcategories_value"
								value='${staticFieldForm.hidcategories }' class="inputtext"/>
						</td>
					</tr>	
				</table>
			</td>
			</tr>
			</table>
		<%}%>
		<table width="100%" border="0" cellspacing="0" align="center"
			cellpadding="0">
			<tr>
				<td height="5"></td>
			</tr>
			<tr>
				<td align="center">

					<html:button styleClass="mybutton" property="" onclick="submitT();">
						<bean:message key="button.ok" />
					</html:button>
					<html:button styleClass="mybutton" property=""
						onclick="winclose();">
						<bean:message key="button.close" />
					</html:button>
				</td>
			</tr>
		</table>
	</html:form>
</body>
<script>
function winclose(){
	top.close();
}
<% if(type == null || !"categories".equalsIgnoreCase(type)){%>	
	<logic:equal value="1" name="staticFieldForm" property='type'>
		changeshade('<bean:write name="staticFieldForm" property="stype" />');
	</logic:equal>
	<logic:equal value="2" name="staticFieldForm" property='type'>
	function boxfilter(hv){
		if('h'==hv){
			var checkobj=document.getElementsByName("h");
			var stype="";
			var sformula="";
			for(var i=0;i<checkobj.length;i++){
					if(checkobj[i].checked){
						stype=checkobj[i].stype;
						sformula=checkobj[i].sformula;
						break;
					}
			}
			if(stype==""){
				checkobj=document.getElementsByName("v");
				for(var i=0;i<checkobj.length;i++){
						if(checkobj[i].checked){
							stype=checkobj[i].stype;
							sformula=checkobj[i].sformula;
							break;
						}
				}
				if(stype==""){
					checkobj=document.getElementsByName("h");
					for(var i=0;i<checkobj.length;i++){
						checkobj[i].disabled="";
					}
					checkobj=document.getElementsByName("v");
					for(var i=0;i<checkobj.length;i++){
						checkobj[i].disabled="";
					}
				}
			}else if("0"==stype){
				for(var i=0;i<checkobj.length;i++){
					if(checkobj[i].stype!="0"){
						checkobj[i].disabled="disabled";
					}
				}
				checkobj=document.getElementsByName("v");
				for(var i=0;i<checkobj.length;i++){
					if(checkobj[i].stype!="0"){
						checkobj[i].disabled="disabled";
					}
				}
			}else{
				for(var i=0;i<checkobj.length;i++){
					if(checkobj[i].stype!=stype||checkobj[i].sformula!=sformula){
						checkobj[i].disabled="disabled";
					}
				}
				checkobj=document.getElementsByName("v");
				for(var i=0;i<checkobj.length;i++){
					if(checkobj[i].stype!=stype||checkobj[i].sformula!=sformula){
						checkobj[i].disabled="disabled";
					}
				}
			
			}
			
		}else{
			var checkobj=document.getElementsByName("v");
			var stype="";
			var sformula="";
			for(var i=0;i<checkobj.length;i++){
					if(checkobj[i].checked){
						stype=checkobj[i].stype;
						sformula=checkobj[i].sformula;
						break;
					}
			}
			if(stype==""){
				checkobj=document.getElementsByName("h");
				for(var i=0;i<checkobj.length;i++){
						if(checkobj[i].checked){
							stype=checkobj[i].stype;
							sformula=checkobj[i].sformula;
							break;
						}
				}
				if(stype==""){
					checkobj=document.getElementsByName("v");
					for(var i=0;i<checkobj.length;i++){
						checkobj[i].disabled="";
					}
					checkobj=document.getElementsByName("h");
					for(var i=0;i<checkobj.length;i++){
						checkobj[i].disabled="";
					}
				}
			}else if("0"==stype){
				for(var i=0;i<checkobj.length;i++){
					if(checkobj[i].stype!="0"){
						checkobj[i].disabled="disabled";
					}
				}
				checkobj=document.getElementsByName("h");
				for(var i=0;i<checkobj.length;i++){
					if(checkobj[i].stype!="0"){
						checkobj[i].disabled="disabled";
					}
				}
			}else{
				for(var i=0;i<checkobj.length;i++){
					if(checkobj[i].stype!=stype||checkobj[i].sformula!=sformula){
						checkobj[i].disabled="disabled";
					}
				}
				checkobj=document.getElementsByName("h");
				for(var i=0;i<checkobj.length;i++){
					if(checkobj[i].stype!=stype||checkobj[i].sformula!=sformula){
						checkobj[i].disabled="disabled";
					}
				}
			
			}
		
		}
	}
	
	boxfilter('h');
	
	function batch_select_filter(obj,name){
	
	if(obj.checked){
		var checkobj=document.getElementsByName(name);
		var isfilter=false;
		for(var i=0;i<checkobj.length;i++){
			if(checkobj[i].disabled){
				isfilter=true;
			}
		}
		if(isfilter){
			for(var i=0;i<checkobj.length;i++){
				if(!(checkobj[i].disabled)){
					checkobj[i].checked=true;
				}
			}
		}else{
			for(var i=0;i<checkobj.length;i++){
				checkobj[i].checked=true;
				break;
			}
			boxfilter(name);
			for(var i=0;i<checkobj.length;i++){
				if(!(checkobj[i].disabled)){
					checkobj[i].checked=true;
				}
			}
		}
	}else{
		var checkobj=document.getElementsByName(name);
		for(var i=0;i<checkobj.length;i++){
			if(!(checkobj[i].disabled)){
				checkobj[i].checked=false;
			}
		}
		boxfilter(name);
	}
	}
	</logic:equal>
	
    if(getBrowseVersion()==10){ //非IE浏览器非兼容性   wangb 20180127
        if(getIE11Version()){
            //修改下拉框样式  wangbs
            var hidcategoriesSelect = document.getElementById("hidcategoriesselect");
            var parentDiv = hidcategoriesSelect.parentNode;
            parentDiv.style.width = "351.5px";
            parentDiv.style.height = "18px";

            var categoriesInput = document.getElementById("hidcategories");
            categoriesInput.style.height = "18px";

            /*修改横向条件下面的table样式*/
            var itemTable = document.getElementById("itemtable");
            itemTable.style.padding = "2px";
        }
    }
    if(!getBrowseVersion()){
    	var hidcategoriesSelect = document.getElementById("hidcategoriesselect");
    	hidcategoriesSelect.style.width = "351px";
        hidcategoriesSelect.style.height = "22px";
        var parentDiv = hidcategoriesSelect.parentNode;
        parentDiv.style.width = "351px";
        parentDiv.style.height = "22px";
        var categoriesInput = document.getElementById("hidcategories");
        categoriesInput.style.height = "22px";
        categoriesInput.style.lineHeight = "22px";
        categoriesInput.style.width = "335px";
    }
<%}%>
</script>
