<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant" %>
<%
	String userName = null;
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String bosflag="";
	if(userView != null){
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	css_url="/css/css1.css";
	  	bosflag=userView.getBosflag();
	}
%>
<script language="javascript">

var dt=(window.screen.availHeight - 30 - 460) / 2;  //获得窗口的垂直位置
var dl=(window.screen.availWidth - 10 - 790) / 2; //获得窗口的水平位置 
var userAgent = window.navigator.userAgent; //取得浏览器的userAgent字符串  
var isOpera = userAgent.indexOf("Opera") > -1;
var isIE = (userAgent.indexOf("compatible") > -1 && userAgent.indexOf("MSIE") > -1 && !isOpera); 

var temp =new Array();
var isshow;
	var infos = "${complexInterfaceForm.fieldItems}";
	
	if(infos == ""){}else{
		temp = infos.split(",");
	}
  	var b = false;
  	
	function insertTxt(strtxt)//xuj update 兼容ff、gg
	{
	    if(strtxt==null)
	   	 	return ;
	   
	    //if((strtxt.toString()).indexOf("(")!=-1)
	     //	strtxt="["+strtxt+"]";
	    if(strtxt=="且"||strtxt=="如果"||strtxt=="或"||strtxt=="否则"||strtxt=="结束"||strtxt=="那么"||strtxt=="非")
	     {
	        var ddd=" "+strtxt+" ";
	        var expr_editor=$('complex_expr');
		    expr_editor.focus();
		    var element = document.selection;
		    if (element&&element!=null) 
		    {//ie
		  	  var rge = element.createRange();
		      if (rge!=null)	
		  	     rge.text=ddd;
		    }else{//ff、gg //xuj update 2011-5-26
		    	var _length=strtxt.length;
				var word = expr_editor.value;
				var startP = expr_editor.selectionStart;
				var endP = expr_editor.selectionEnd;
				var ddd=word.substring(0,startP)+strtxt+word.substring(endP);
		    	expr_editor.value=ddd;
		    	expr_editor.setSelectionRange(startP+_length,startP+_length);
		    }
		    /*else if (expr_editor.selectionStart || expr_editor.selectionStart == '0') {
				var startP = expr_editor.selectionStart;
				var endP = expr_editor.selectionEnd;
				if (startP != endP) {
					word = expr_editor.value.substring(startP, endP);
				}
			}*/
	     }else{
		  	var expr_editor=$('complex_expr');
		    expr_editor.focus();
			var element = document.selection;
			if (element&&element!=null) 
			{
			  var rge = element.createRange();
			  if (rge!=null)	
			  	  rge.text=strtxt;
			}else{ //xuj update 2011-5-26
				//alert(expr_editor.selectionStart+" : "+expr_editor.selectionEnd);
				var word = expr_editor.value;
				var _length=strtxt.length;
				var startP = expr_editor.selectionStart;
				var endP = expr_editor.selectionEnd;
				var ddd=word.substring(0,startP)+strtxt+word.substring(endP);
				/*if(startP==endP){
					if((startP+1)==_length){
						ddd=word+strtxt;
					}else{
						ddd=word.substring(0,startP)+strtxt+word.substring(startP);
					}
				}else{
					ddd=word.substring(0,startP)+strtxt+word.substring(endP);
				}*/
		    	expr_editor.value=ddd;
        		expr_editor.setSelectionRange(startP+_length,startP+_length); 
		    	
			}
		}
	}
	
  function  fnOpen()  
  {  
       var  wName; 
       var dw=400,dh=400,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
       var thecodeurl ="/org/autostatic/mainp/function_Wizard.do?b_query=link&salaryid=&tableid=&salarytemp="; 
      if(isIE){
    	  wName= window.showModalDialog(thecodeurl, "", 
                  "dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:400px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
          insertTxts(wName);
      }else{
	      window.open(thecodeurl,"_blank",'toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=no,resizable=no,top='+dt+'px,left='+dl+'px,width=400px,height=420px');
      }
     
  }  
  
  function openReturn(wName){
	  insertTxts(wName);
  }
  
  function insertTxts(strtxt)
  {
    if(strtxt==null)
      return ;
   var ggg;
   if(strtxt=="如果 那么 否则 结束")
   {
      var dgf=strtxt.replace(" ","\n");
      var dgg=dgf.replace(" ","\n");
      var ddd=dgg.replace(" ","\n");
      ggg=ddd.replace(" ","\n");
   }else if(strtxt=="分情况")
   {
       ggg="如果 Lexp1 那么 exp1"+"\n"+"如果 Lexp2 那么 exp2"+"\n"+"否则 expn..."+"\n"+"结束";
   }else{
    
      ggg=strtxt;  
   }
     var expr_editor=$('complex_expr');
	        expr_editor.focus();
		  var element = document.selection;
		  if (element&&element!=null) 
		  {
		  	var rge = element.createRange();
		   	if (rge!=null)	
		  	     rge.text=ggg;
		  }else{
		  	var oldv=expr_editor.value;
		  	expr_editor.value=oldv+ggg;
		  }
  }

  function checkExpr(){
	var m = document.complexInterfaceForm.complex_expr.value;
	if(m == ""){
  		alert("条件表达式不能为空!");
  		return ;
  	}else{
		//alert(m);
		//m = m.replace( /\r|\n/g, "`" ); 
		m = m.replace( /\r/g, "!" ); 
		m = m.replace( /\n/g, "`" ); 
		
  		//规范字符串
		for(var i = 0 ; i<m.length; i++){
			if(m.charAt(i) == "\""){
				m = m.replace("\"" , "'");
			}
		}
  		var hashvo=new ParameterSet();
	    hashvo.setValue("c_expr",m);
	    hashvo.setValue("midvariable","1");	   
	    hashvo.setValue("warntype","1");
	   	var In_paramters="flag=1"; 	
		var request=new Request({method:'post',asynchronous:false,
			parameters:In_paramters,onSuccess:resultCheckExpr,functionId:'1010020309'},hashvo);	
  	}
  }  
  function resultCheckExpr(outparamters){
  	var info = outparamters.getValue("info");
	if(info=="ok"){
		b = true;
		alert("条件表达式定义正确!");
		return true;
	}else{
		for(var i = 0 ; i<info.length; i++){
			if(info.charAt(i) == "'"){
				info = info.replace("'" , "\"");
			}
			if(info.charAt(i) == "`"){
				info = info.replace("`" , "\r\n");
			}
		}
		alert(info);
        return false;
	}
  }
  

  function changeFieldSet(){
	var v = complexInterfaceForm.setid.value;
  	var hashvo=new ParameterSet();
    hashvo.setValue("setid",v);
   	var In_paramters="flag=1"; 	
	var request=new Request({method:'post',asynchronous:false,
		parameters:In_paramters,onSuccess:resultChangeFieldSet,functionId:'1010020308'},hashvo);					
  }
  
  function resultChangeFieldSet(outparamters){
  	Element.hide('cid');
  	var fielditemlist=outparamters.getValue("itemList");
	AjaxBind.bind(complexInterfaceForm.fieldItemId,fielditemlist);
	
  }
  
  function changeFieldItem(){
  	 var m = document.complexInterfaceForm.fieldItemId.value;
  	 var v = complexInterfaceForm.setid.value;
  	 if(v=="midvariable"&&m=="newcreate")
  	 {
  	       settemp();
  		   return;
  	 }else
  	 {
  	     insertTxt(m);
  	    if(v!="midvariable")
  	    {
  	       var hashvo=new ParameterSet();
   	       hashvo.setValue("itemDesc",m);
   	       var In_paramters="flag=1"; 	
	       var request=new Request({method:'post',asynchronous:false,
		   parameters:In_paramters,onSuccess:resultChangeFieldItem,functionId:'1010020310'},hashvo);
  	    } 	  	    
  	 }
  	 
  }
  function settemp(){
	
	var thecodeurl = "/general/template/iframvartemp.jsp?state=0&nflag=3";
   	var return_vo= window.showModalDialog(thecodeurl,"window2",
   						"dialogWidth:900px;dialogHeight:550px;resizable:no;center:yes;scroll:yes;status:no");
    var hashvo=new ParameterSet();
    hashvo.setValue("setid","midvariable");
   	var In_paramters="flag=1"; 	
	var request=new Request({method:'post',asynchronous:false,
		parameters:In_paramters,onSuccess:resultChangeFieldSet,functionId:'1010020308'},hashvo);	
  }
  
  function resulteMidvariable(outparamters)
  {
     var nvalue=outparamters.getValue("nvalue");     
     if(nvalue!="")
     {
        nvalue=getDecodeStr(nvalue);
        insertTxt(nvalue); 
     }     
  }
  function resultChangeFieldItem(outparamters){
  	var info=outparamters.getValue("info");
  	if(info == "error"){
  		 Element.hide('cid');
  	}else{
  		Element.show('cid');
  		AjaxBind.bind(complexInterfaceForm.codeItemId,info);
  	}
  }
  
  function changeCodeItem(){
  	var m = document.complexInterfaceForm.codeItemId.value;
  	m="\""+m+"\"";
  	insertTxt(m);
  }
  
  var complex_name;
  function save(name){
  	complex_name=name;
    saveCheckExpr();
  }
  
  function saveCheckExpr(){
  	var m = document.complexInterfaceForm.complex_expr.value;
	if(m == ""){
  		alert("条件表达式不能为空!");
  		return ;
  	}else{
		//alert(m);
		//m = m.replace( /\r|\n/g, "`" ); 
		m = m.replace( /\r/g, "!" ); 
		m = m.replace( /\n/g, "`" ); 
		
  		//规范字符串
		for(var i = 0 ; i<m.length; i++){
			if(m.charAt(i) == "\""){
				m = m.replace("\"" , "'");
			}
		}
  		var hashvo=new ParameterSet();
	    hashvo.setValue("c_expr",m);	 
	    hashvo.setValue("midvariable","1");	   
	    hashvo.setValue("warntype","1");	   
	   	var In_paramters="flag=1"; 		   	
		var request=new Request({method:'post',asynchronous:false,
			parameters:In_paramters,onSuccess:resultSaveCheckExpr,functionId:'1010020309'},hashvo);				
  	
  	}
  }
  
  function resultSaveCheckExpr(outparamters){
  	var info = outparamters.getValue("info");
  	var m = document.complexInterfaceForm.complex_expr.value;
  	if(info=="ok"){
  	    var hashvo=new ParameterSet();
  	    hashvo.setValue("complex_id","${complexInterfaceForm.complex_id}")
	    hashvo.setValue("complex_expr",m);	 	 
	    hashvo.setValue("complex_name",complex_name);   
        var request=new Request({method:'post',asynchronous:false,onSuccess:saveResult,functionId:'0202002004'},hashvo);
	}else{
		for(var i = 0 ; i<info.length; i++){
			if(info.charAt(i) == "'"){
				info = info.replace("'" , "\"");
			}
			if(info.charAt(i) == "`"){
				info = info.replace("`" , "\r\n");
			}
		}		
	}
  }
  function saveResult(outparamters)
  {
     var info = outparamters.getValue("info");
     var complex_id=outparamters.getValue("id");
     if(info=="ok")
     {
       alert("保存成功！");
       complexInterfaceForm.action="/workbench/query/complex_interface.do?b_query=link&complex_id="+complex_id;
       complexInterfaceForm.submit();
     }else
     {
       alert("保存失败！");
     }
  }
  function resultSearcheCheckExpr(outparamters)
  {     var info = outparamters.getValue("info");
        if(info=="ok")
        {
        	//防止重复提交后台报错，置灰按钮 14-10-25 guodd
        	document.getElementById('searchButton').disabled = true;
          complexInterfaceForm.action="/workbench/query/complex_interface.do?b_qsearch=link";
          complexInterfaceForm.submit();
        }else
        {
           for(var i = 0 ; i<info.length; i++){
			if(info.charAt(i) == "'"){
				info = info.replace("'" , "\"");
			}
			if(info.charAt(i) == "`"){
				info = info.replace("`" , "\r\n");
			}
		  }  
		  alert(info);
          return false;
        }
  }
  function search(){
  	var m = document.complexInterfaceForm.complex_expr.value;
	if(m == ""){
  		alert("条件表达式不能为空!");
  		return ;
  	}else{
		//alert(m);
		//m = m.replace( /\r|\n/g, "`" ); 
		m = m.replace( /\r/g, "!" ); 
		m = m.replace( /\n/g, "`" ); 
		
  		//规范字符串
		for(var i = 0 ; i<m.length; i++){
			if(m.charAt(i) == "\""){
				m = m.replace("\"" , "'");
			}
		}
  		var hashvo=new ParameterSet();
	    hashvo.setValue("c_expr",m);
	    hashvo.setValue("midvariable","1");	   
	    hashvo.setValue("warntype","1");	    
	   	var In_paramters="flag=1"; 		   	
		var request=new Request({method:'post',asynchronous:false,
			parameters:In_paramters,onSuccess:resultSearcheCheckExpr,functionId:'1010020309'},hashvo);				
  	
  	}
  }
  function query(){
      if($F('complex_id')!="#")
      {
         complexInterfaceForm.action="/workbench/query/complex_interface.do?b_query=link&complex_id="+$F('complex_id');
         complexInterfaceForm.submit();
      }else
     {
        alert("请选择条件！");
        $('complex_expr').value=''
     }
      MusterInitData();
  }
  function addName(){
	var m = document.complexInterfaceForm.complex_expr.value;
	if(m == ""){
  		alert("条件表达式不能为空!");
  		return ;
  	}else{
		//alert(m);
		//m = m.replace( /\r|\n/g, "`" ); 
		m = m.replace( /\r/g, "!" ); 
		m = m.replace( /\n/g, "`" ); 
		
  		//规范字符串
		for(var i = 0 ; i<m.length; i++){
			if(m.charAt(i) == "\""){
				m = m.replace("\"" , "'");
			}
		}
  		var hashvo=new ParameterSet();
	    hashvo.setValue("c_expr",m);
	    hashvo.setValue("midvariable","1");	   
	    hashvo.setValue("warntype","1");
	   	var In_paramters="flag=1"; 	
		var request=new Request({method:'post',asynchronous:false,
			parameters:In_paramters,onSuccess:resultCheckExpr2,functionId:'1010020309'},hashvo);	
  	}
  }  
  function resultCheckExpr2(outparamters){
  	var info = outparamters.getValue("info");
	if(info=="ok"){
		target_url="/workbench/query/complex_interface.do?br_name=link";
        var winFeatures = "dialogHeight:300px; dialogLeft:250px;";
        var dw=440,dh=140,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2; 
      
        var return_vo;
        if(isIE){
        	return_vo=window.showModalDialog(target_url,1, 
        		        "dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:"+dw+"px; dialogHeight:"+dh+"px;resizable:no;center:yes;scroll:no;status:no;scrollbars:no");
        	if(return_vo!=null)
            {
              if(return_vo.flag="ok")
              {
                 //complexInterfaceForm.action="/workbench/query/complex_interface.do?b_name=link&complex_name="+return_vo.complex_name;
                 //complexInterfaceForm.submit();  
                 save(return_vo.complex_name);
              }
           }
        }else{//兼容多浏览器
        	window.open(target_url,"_blank",'toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=no,resizable=no,top='+dt+'px,left='+dl+'px,width='+dw+'px,height='+140+'px');

        }
        
	}else{
		for(var i = 0 ; i<info.length; i++){
			if(info.charAt(i) == "'"){
				info = info.replace("'" , "\"");
			}
			if(info.charAt(i) == "`"){
				info = info.replace("`" , "\r\n");
			}
		}
		alert(info);
        return false;
	}
  }
  
  function saveOpenReturn(return_vo){
	  if(return_vo!=null)
      {
        if(return_vo.flag="ok")
        {
           //complexInterfaceForm.action="/workbench/query/complex_interface.do?b_name=link&complex_name="+return_vo.complex_name;
           //complexInterfaceForm.submit();  
           save(return_vo.complex_name);
        }
     }
  }
  
  function deleteName()
  {
     if($F('complex_id')!="#")
     {
        if(confirm("确认删除该条件？"))
        {
          complexInterfaceForm.action="/workbench/query/complex_interface.do?b_delete=link&complex_id="+$F('complex_id');
          complexInterfaceForm.submit();
        }
     }else
     {
        alert("请选择条件！")
     }
     
      
  }
  function hideterm()
  {
     var waitInfo=eval("term");       
     if(waitInfo.style.display=="block"&&!isshow)
     { 
        waitInfo.style.display="none";
     }else
     {
        isshow=false;
     }
  }
</script>
<hrms:themes />
<style>
.button{
	height:20px;
	line-height:18px;
	padding:0;
}
</style>
<html:form action="/workbench/query/complex_interface">
<html:hidden property="fromFlag" name="complexInterfaceForm"/>
<!-- 
<table border="0" align="center" width="100%" height="100%">
<tr>
 <td width="100%" height="100%" onclick="hideterm();">
  -->
  <!-- 员工管理，复杂查询页面优化  jingq add 2014.11.28 -->
  <%if("hcm".equals(bosflag)){ %> 
  <table width="620" border="0" cellpadding="0" cellspacing="0" align="center">
  <%}else{ %>
  <table width="620" border="0" cellpadding="0" cellspacing="0" align="center" style="margin-top: 10px">
  <%} %> 
  	<tr height="20">
  		<td align="left" class="TableRow1">复杂查询</td>
  	</tr>
			<tr>
				<td align="center" class="framestyle">
						<table border="0"  cellspacing="0" width="605" cellpadding="2" align="center" class="ListTable">
						<tr>
	                       <td height="30">
	                        <table border="0" >
	                           <tr>
	                           <bean:size id="lengthv" name="complexInterfaceForm" property="compledblist"/>
	                            <td <logic:lessThan value="2" name="lengthv">style="display: none"</logic:lessThan>>
	                               <bean:message key="menu.base"/>
	                               <html:select name="complexInterfaceForm" property="comple_db" size="1">

                                   <html:optionsCollection property="compledblist" value="dataValue" label="dataName"/>
                                   <html:option value="ALL">全部人员库</html:option>
                                   </html:select> 
	                            </td>
	                            <td>
	                                  &nbsp;&nbsp;常用条件
	                                  <html:select name="complexInterfaceForm" property="complex_id" size="1" onchange="query();">
				                        <option value="#"></option>
				                        <html:optionsCollection property="complexList" value="dataValue" label="dataName"/>
		                              </html:select>
	                            </td>
	                            <td>
	                            	&nbsp;<input type="button" name="Submit" value="删除" class="mybutton" onClick="deleteName();">
	                            </td>
	                            </tr>
	                         </table>
	                        </td>
	                        <td>&nbsp;
	                        </td>
                           </tr>
							<tr>
								<td valign="top">
									<html:textarea name="complexInterfaceForm" property="complex_expr"   cols="60" rows="10" style="width:570px;height:300px;overflow:auto;"/>
								</td>
								<td valign="top" align="right">
								<table border="0px" align="right" cellpadding="0" cellspacing="0">
								    <tr height="43px;">
								    <td valign="top"><input type="button" name="Submit" value="保存" class="smallbutton" onClick="addName();">                                 
                                    </td></tr>
								    <tr height="43px;">
								    <td valign="top"><input type="button" name="Submit" value="向导" class="smallbutton" onClick="fnOpen();">
                                    </td></tr>
								    <tr height="43px;">
								    <td valign="top"><input type="button" name="bto" value="检查" class="smallbutton" onClick="checkExpr()">
                                    </td></tr>
								    <tr height="43px;">
								    <td valign="top"><input type="button" id='searchButton' name="Submit" value="查询" class="smallbutton" onClick="search();"></td></tr>
								</table>
								</td>
							</tr>
							
							<tr>
								<td>
									<table width="100%" cellpadding="0" cellspacing="0" border="0">
									<!-- 【7493】员工管理-查询浏览-复杂查询（界面有问题）   jingq upd 2015.02.12 -->
									<tr><td width="55%" height="100px;">
									<fieldset style="width:98%;float:left;padding:0px;height: 100px;">
										<legend>
											参考项目
										</legend>
										<table border="0" >
											<tr>
												<td>
													&nbsp;&nbsp;指标集
													<hrms:optioncollection name="complexInterfaceForm" property="setlist" collection="list" />
													<html:select name="complexInterfaceForm" property="setid" style="width:200px;" size="1" onchange="changeFieldSet();">
														<html:options collection="list" property="dataValue" labelProperty="dataName" />
													</html:select>
												</td>
											</tr>
											<tr>
												<td>
													&nbsp;&nbsp;指标项
													<hrms:optioncollection name="complexInterfaceForm" property="itemlist" collection="list" />
													<html:select name="complexInterfaceForm" property="fieldItemId" style="width:200px;" size="1" onchange="changeFieldItem();">
														<html:options collection="list" property="dataValue" labelProperty="dataName" />
													</html:select>
													
												</td>
												
											</tr>
											
											<tr>
												<td>
													<div id="cid">
													&nbsp;&nbsp;代码项
													<html:select name="complexInterfaceForm" property="codeItemId" size="1" style="width:200px;"  onchange="changeCodeItem();">
														<option></option>
													</html:select>
													</div>
												</td>
											</tr>
											
										</table>
									</fieldset>
									</td><td width="44%" >
									<fieldset align="center" style="height:100px;padding:0;">
										<legend>运算符号</legend>
										<table cellpadding="0" border="0" width="100%" align="center">
											<tr>
												<td nowrap="nowrap" align="center" width="100%">
												<input type="button" name="Submit4" value="0" class="button  " style="width:15px;" onclick="insertTxt(this.value);" >
												<input type="button" name="Submit42" value="1" class="button " style="width:15px;" onclick="insertTxt(this.value)" >
												<input type="button" name="Submit43" value="2" class="button " style="width:15px;" onclick="insertTxt(this.value)" >
												<input type="button" name="Submit44" value="3" class="button " style="width:15px;" onclick="insertTxt(this.value)" >
												<input type="button" name="Submit45" value="4" class="button " style="width:15px;" onclick="insertTxt(this.value)" >
												<input type="button" name="Submit46" value="(" class="button " style="width:15px;" onclick="insertTxt(this.value)" >
												<input type="button" name="Submit477" value="=" class="button " style="width:15px;" onclick="insertTxt(this.value)" >
												<input type="button" name="Submit4763" value=">=" class="button " style="width:23px;" onclick="insertTxt(this.value)" >
												<input type="button" name="Submit4753" value="<bean:message key="kq.formula.not"/>" class="button " style="width:17px;" onclick="insertTxt(this.value)" >
												<input type="button" name="Submit4764" value="~" class="button " style="width:15px;" onclick="insertTxt(this.value)" >
												<input type="button" name="Submit46" value="<bean:message key="kq.wizard.thing"/>" class="button " style="width:41px;" onclick="insertTxt(this.value)" >
												</td>
											</tr>
											<tr><td nowrap="nowrap" align="center" width="100%">
												<input type="button" name="Submit47" value="5" class="button " style="width:15px;" onclick="insertTxt(this.value)" >
												<input type="button" name="Submit472" value="6" class="button " style="width:15px;" onclick="insertTxt(this.value)" >
												<input type="button" name="Submit473" value="7" class="button " style="width:15px;" onclick="insertTxt(this.value)" >
												<input type="button" name="Submit474" value="8" class="button " style="width:15px;" onclick="insertTxt(this.value)" >
												<input type="button" name="Submit475" value="9" class="button " style="width:15px;" onclick="insertTxt(this.value)" >
												<input type="button" name="Submit476" value=")" class="button " style="width:15px;" onclick="insertTxt(this.value)" >
												<input type="button" name="Submit4722" value=">" class="button " style="width:15px;" onclick="insertTxt(this.value)" >
												<input type="button" name="Submit4754" value="<=" class="button " style="width:23px;" onclick="insertTxt(this.value)" >
												<input type="button" name="Submit4752" value="<bean:message key="kq.formula.even"/>" class="button " style="width:17px;" onclick="insertTxt(this.value)" >
												<input type="button" name="Submit46" value="<bean:message key="kq.formula.if"/>" class="button " style="width:28px;" onclick="insertTxt(this.value)" >
												<input type="button" name="Submit4764" value="<bean:message key="kq.formula.fou"/>" class="button " style="width:28px;" onclick="insertTxt(this.value)" >
											</td></tr>
											<tr><td nowrap="nowrap" align="center" width="100%">
												<input type="button" name="Submit47" value="+" class="button " style="width:15px;" onclick="insertTxt(this.value)" >
												<input type="button" name="Submit472" value="-" class="button " style="width:15px;" onclick="insertTxt(this.value)" >
												<input type="button" name="Submit473" value="*" class="button " style="width:15px;" onclick="insertTxt(this.value)" >
												<input type="button" name="Submit474" value="/" class="button " style="width:15px;" onclick="insertTxt(this.value)" >
												<input type="button" name="Submit475" value="\" class="button " style="width:15px;" onclick="insertTxt(this.value)" >
												<input type="button" name="Submit476" value="%" class="button " style="width:15px;" onclick="insertTxt(this.value)" >
												<input type="button" name="Submit4732" value="<" class="button" style="width:15px;" onclick="insertTxt(this.value)" >
												<input type="button" name="Submit4742" value="<>" class="button " style="width:23px;" onclick="insertTxt(this.value)" >
												<input type="button" name="Submit4762" value="<bean:message key="kq.formula.or"/>" class="button " style="width:17px;" onclick="insertTxt(this.value)" >
												<input type="button" name="Submit46" value="<bean:message key="kq.formula.then"/>" class="button " style="width:28px;" onclick="insertTxt(this.value)" >
												<input type="button" name="Submit4764" value="<bean:message key="kq.formula.end"/>" class="button " style="width:28px;" onclick="insertTxt(this.value)" >
											</td></tr>
										</table>
										</fieldset>
										</td></tr></table>
								</td>
								<td height="5px"></td>
							</tr>
							<tr>
								<td height="2px" colspan="2"></td>
							</tr>
						</table>
		</td>
	</tr>
</table>
<!-- 
 </td>
 </tr>
 </table>
 -->
</html:form>
<div id=term style="border-style:nono;position:absolute;">
    <table width="100" border="0" cellspacing="2"  align="center" cellpadding="2" bgcolor="#E1F1FB">
     <tr>
     <td>
          
		 
	</td></tr>
	<tr><td align="center">
			<input type="button" name="Submit" value="确定" class="mybutton" onClick="query();">&nbsp;&nbsp;
			<input type="button" name="Submit" value="删除" class="mybutton" onClick="deleteName();">
     </td>
     </tr>
</div>
<script language="javaScript">	    
	Element.hide('cid');	
	function showterm(obj)
    {
       var pos=getAbsPosition(obj);  
       var waitInfo=eval("term");	
	   waitInfo.style.display="block";
       with($('term'))
       {
	      style.posLeft=pos[0]-150;
 	      style.posTop=pos[1]+obj.offsetHeight;
	      style.position="absolute"; 	 
	      isshow=true;     
	   }
    }	
    function MusterInitData()
    {
	   var waitInfo=eval("term");	
	   waitInfo.style.display="none";
    }
    MusterInitData();
</script>
