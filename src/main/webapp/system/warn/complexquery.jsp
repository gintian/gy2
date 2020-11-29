<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<hrms:themes></hrms:themes>
<script language="javascript" src="/module/utils/js/template.js"></script>
<script language="javascript">

	
	var temp =new Array();
	var infos = "${complexQueryForm.fieldItems}";
	
	if(infos == ""){}else{
		temp = infos.split(",");
	}
  	var b = false;
  	
	function insertTxt(strtxt)
	{
	    if(strtxt==null)
	   	 	return ;
	   
	    //if((strtxt.toString()).indexOf("(")!=-1)
	     //	strtxt="["+strtxt+"]";
	    
	    if(strtxt=="且"||strtxt=="如果"||strtxt=="或"||strtxt=="否则"||strtxt=="结束"||strtxt=="那么"||strtxt=="非")
	     {
	        var ddd=" "+strtxt+" ";
	        var expr_editor=$('formula');
		    expr_editor.focus();
		    /*
		    var element = document.selection;
		    if (element!=null) 
		    {
		  	  var rge = element.createRange();
		      if (rge!=null)	
		  	     rge.text=ddd;
		    }*/
		    if ( document.selection ) { //ie浏览器下回填计算公式数据   wangb 20190319
			  document.selection.createRange().text=ddd; 
		    }else{ //非ie浏览器下回填计算公式   wangb 20190319
			  expr_editor.value = expr_editor.value.substring(0,expr_editor.selectionStart)+ddd+expr_editor.value.substring(expr_editor.selectionEnd,expr_editor.value.length);
		    } 
	     }else{
		  	var expr_editor=$('formula');
		    expr_editor.focus();
		    /*
			var element = document.selection;
			if (element!=null) 
			{
			  var rge = element.createRange();
			  if (rge!=null)	
			  	  rge.text=strtxt;
			  }
			 */
			 if ( document.selection ) { //ie浏览器下回填计算公式数据   wangb 20190319
			   document.selection.createRange().text=strtxt; 
		     }else{ //非ie浏览器下回填计算公式   wangb 20190319
			   expr_editor.value = expr_editor.value.substring(0,expr_editor.selectionStart)+strtxt+expr_editor.value.substring(expr_editor.selectionEnd,expr_editor.value.length);
		     } 
		}
	}
	
  function  fnOpen(warntype,fieldSetId)  
  {  
       var  wName; 
       var infor="";
       var mode="";
       if(warntype=="1")
          infor="2";
       else if(warntype=="2")
          infor="3";
       else if(warntype=="3" && fieldSetId=="Q03")
       {
           mode="kqrule";
       }
       var thecodeurl ="/org/autostatic/mainp/function_Wizard.do?b_query=link&salaryid=&tableid=&salarytemp=&infor="+infor+"&mode="+mode; 
      /*
      wName= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:400px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
      insertTxts(wName);
      */
      //改用ext 弹窗显示  wangb 20190318
      var win = Ext.create('Ext.window.Window',{
			id:'formulaId',
			title:'计算公式',
			width:400,
			height:430,
			resizable:'no',
			modal:true,
			autoScoll:false,
			autoShow:true,
			autoDestroy:true,
			html:'<iframe style="background-color:#fff" frameborder="0" SCROLLING=NO height="100%" width="100%" src="'+thecodeurl+'"></iframe>',
			renderTo:Ext.getBody()
	  });
  }  
  // 子页面回调父页面方法  wangb 20190318
  function openReturn(return_vo){

  		insertTxts(return_vo);
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
     var expr_editor=$('formula');
	        expr_editor.focus();
	      var element;
	     
	      if ( document.selection ) { //ie浏览器下回填计算公式数据   wangb 20190319
			 document.selection.createRange().text=ggg; 
		  }else{ //非ie浏览器下回填计算公式   wangb 20190319
			 expr_editor.value = expr_editor.value.substring(0,expr_editor.selectionStart)+ggg+expr_editor.value.substring(expr_editor.selectionEnd,expr_editor.value.length);
		  } 
  }

  function checkExpr(){
	var m = document.complexQueryForm.formula.value;
	if(m == ""){
  		alert("预警规则不能为空!");
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
	    hashvo.setValue("setid",'${complexQueryForm.setid}');
	     hashvo.setValue("warntype",'${complexQueryForm.warntype}');
	   	var In_paramters="flag=1"; 	
		var request=new Request({method:'post',asynchronous:false,
			parameters:In_paramters,onSuccess:resultCheckExpr,functionId:'1010020309'},hashvo);				
  	
  	}
  	
  }
  
  function resultCheckExpr(outparamters){
  	var info = outparamters.getValue("info");
	if(info=="ok"){
		b = true;
		alert("预警规则定义正确!");
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
	}
  }
  

  function changeFieldSet(){
	var v = complexQueryForm.fieldSetId.value;
  	var hashvo=new ParameterSet();
    hashvo.setValue("setid",v);
   	var In_paramters="flag=1"; 	
	var request=new Request({method:'post',asynchronous:false,
		parameters:In_paramters,onSuccess:resultChangeFieldSet,functionId:'1010020308'},hashvo);					
  }
  
  function resultChangeFieldSet(outparamters){
  	Element.hide('cid');
  	var fielditemlist=outparamters.getValue("itemList");
	AjaxBind.bind(complexQueryForm.fieldItemId,fielditemlist);
	
  }
  
  function changeFieldItem(){
  	 var m = document.complexQueryForm.fieldItemId.value;
  	 insertTxt(m);
  	 
  	 var hashvo=new ParameterSet();
   	 hashvo.setValue("itemDesc",m);
   	 hashvo.setValue("warntype","${complexQueryForm.warntype}");
   	 var In_paramters="flag=1"; 	
	 var request=new Request({method:'post',asynchronous:false,
		parameters:In_paramters,onSuccess:resultChangeFieldItem,functionId:'1010020310'},hashvo);		
  	 
  	 
  }
  
  function resultChangeFieldItem(outparamters){
  	var info=outparamters.getValue("info");
  	if(info == "error"){
  		 Element.hide('cid');
  	}else{
  		Element.show('cid');
  		AjaxBind.bind(complexQueryForm.codeItemId,info);
  	}
  }
  
  function changeCodeItem(){
  	var m = document.complexQueryForm.codeItemId.value;
  	m="\""+m+"\"";
  	insertTxt(m);
  }
  
  
  function save(){
  	saveCheckExpr();
  }
  
  function saveCheckExpr(){
  	var m = document.complexQueryForm.formula.value;
	if(m == ""){
  		alert("预警规则不能为空!");
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
	    hashvo.setValue("setid",'${complexQueryForm.setid}');
	    hashvo.setValue("warntype",'${complexQueryForm.warntype}');
	   	var In_paramters="flag=1"; 	
		var request=new Request({method:'post',asynchronous:false,
			parameters:In_paramters,onSuccess:resultSaveCheckExpr,functionId:'1010020309'},hashvo);				
  	
  	}
  }
  
  function resultSaveCheckExpr(outparamters){
  	var info = outparamters.getValue("info");
  	var m = document.complexQueryForm.formula.value;
  	if(info=="ok"){
  		if(parent.Ext){//ext 弹窗回回调方法  wangb 20190318
  			if(parent.Ext.getCmp('exebolish')){
  				parent.Ext.getCmp('exebolish').obj=m;
				parent.Ext.getCmp('exebolish').close();
			}
  		}else{
			returnValue=m;
			window.close();
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
	}
  }
  
  
  function cancel(){
  	if(parent.Ext){//ext 弹窗关闭方法  wangb 20190318
  		if(parent.Ext.getCmp('exebolish')){
			parent.Ext.getCmp('exebolish').close();
		}
  		return;
  	}
  	window.close();
  }

</script>
<body>
	<form name="complexQueryForm" method="post" action="">
		<table border="0" align="center" width="490" cellpadding="0" cellspacing="0">
			<tr>
				<td align="center">
					<fieldset style="width:490px;">
						<legend>
							预警规则定义
						</legend>
						<table border="0" align="center" width="100%" cellpadding="0" cellspacing="0">
							<tr>
								<td valign="top" width="96%" style="padding-left:5px;">
									<textarea name="formula"  style="width:100%;height:150px">
									</textarea>
								</td>
								<td align="right" valign="bottom" width="4%" style="padding-left:5px;">
									<input type="button" name="Submit" value="向导" class="mybutton" onClick="fnOpen('${complexQueryForm.warntype}','${complexQueryForm.setid}');">
								</td>
							</tr>
							<tr>
								<td valign="top" width="96%" style="padding-left:5px;">
									<fieldset style="margin:10px 0px 0px 0px;">
										<legend>
											项目参考
										</legend>
										<table border="0" align="center" width="100%" cellpadding="0" cellspacing="0">
											<tr>
												<td height="30px;">
												  <logic:notEqual name="complexQueryForm" property="warntype" value="3">
													&nbsp;&nbsp;指标集
													<hrms:optioncollection name="complexQueryForm" property="fieldSetList" collection="list" />
													<html:select name="complexQueryForm" property="fieldSetId" size="1" onchange="changeFieldSet();" style="width:200px">
														<html:options collection="list" property="dataValue" labelProperty="dataName" />
													</html:select>
												  </logic:notEqual>
												</td>
											</tr>
											<tr>
												<td height="30px;">
													&nbsp;&nbsp;指标项
													<hrms:optioncollection name="complexQueryForm" property="fieldItemList" collection="list" />
													<html:select name="complexQueryForm" property="fieldItemId" size="1" onchange="changeFieldItem();"  style="width:200px">
														<html:options collection="list" property="dataValue" labelProperty="dataName" />
													</html:select>
													
												</td>
												
											</tr>
											
											<tr>
												<td height="30px;">
													<div id="cid">
													&nbsp;&nbsp;代码项
													<html:select name="complexQueryForm" property="codeItemId" size="1" onchange="changeCodeItem();" style="width:200px">
														<option></option>
													</html:select>
													</div>
												</td>
											</tr>
											
										</table>
									</fieldset>
								</td>
								<td align="right" valign="bottom" width="4%" >
									<input type="button" name="bto" value="检查" class="mybutton" onClick="checkExpr()">
								</td>
							</tr>
							<tr>
								<td rowspan="2" valign="top" width="100%" style="padding-left:5px;">
									<fieldset style="margin:10px 0px 10px 0px;">
										<legend>运算符</legend>
										<table  border="0" width="100%" align="center" style="line-height: 12px">
										<tr>
										<td>
										<input type="button" name="Submit4" value="0" class="smallbutton" onclick="insertTxt(this.value);" style="height:19px;width:7.8%;font-size:9pt;line-height: 18px">
										<input type="button" name="Submit42" value="1" class="smallbutton" onclick="insertTxt(this.value)" style="height:19px;width:7.8%;font-size:9pt;line-height: 18px">
										<input type="button" name="Submit43" value="2" class="smallbutton" onclick="insertTxt(this.value)" style="height:19px;width:7.8%;font-size:9pt;line-height: 18px">
										<input type="button" name="Submit44" value="3" class="smallbutton" onclick="insertTxt(this.value)" style="height:19px;width:7.8%;font-size:9pt;line-height: 18px">
										<input type="button" name="Submit45" value="4" class="smallbutton" onclick="insertTxt(this.value)" style="height:19px;width:7.8%;font-size:9pt;line-height: 18px">
										<input type="button" name="Submit46" value="(" class="smallbutton" onclick="insertTxt(this.value)" style="height:19px;width:7.8%;font-size:9pt;line-height: 18px">
										<input type="button" name="Submit477" value="=" class="smallbutton" onclick="insertTxt(this.value)" style="height:19px;width:7.8%;font-size:9pt;line-height: 18px">
										<input type="button" name="Submit4763" value=">=" class="smallbutton" onclick="insertTxt(this.value)" style="height:19px;width:7.8%;font-size:9pt;line-height: 18px">
										<input type="button" name="Submit4753" value="<bean:message key="kq.formula.not"/>" class="smallbutton" onclick="insertTxt(this.value)" style="height:19px;width:8%;font-size:9pt;line-height: 18px">
										<input type="button" name="Submit4764" value="~" class="smallbutton" onclick="insertTxt(this.value)" style="height:19px;width:8%;font-size:9pt;line-height: 18px">
										<input type="button" name="Submit46" value="<bean:message key="kq.wizard.thing"/>" class="smallbutton" onclick="insertTxt(this.value)" style="height:19px;width:11%;font-size:9pt;line-height: 18px">
										</td>
										</tr>
										<tr>
										<td>
										<input type="button" name="Submit47" value="5" class="smallbutton" onclick="insertTxt(this.value)" style="height:19px;width:7.8%;font-size:9pt;line-height: 18px">
										<input type="button" name="Submit472" value="6" class="smallbutton" onclick="insertTxt(this.value)" style="height:19px;width:7.8%;font-size:9pt;line-height: 18px">
										<input type="button" name="Submit473" value="7" class="smallbutton" onclick="insertTxt(this.value)" style="height:19px;width:7.8%;font-size:9pt;line-height: 18px">
										<input type="button" name="Submit474" value="8" class="smallbutton" onclick="insertTxt(this.value)" style="height:19px;width:7.8%;font-size:9pt;line-height: 18px">
										<input type="button" name="Submit475" value="9" class="smallbutton" onclick="insertTxt(this.value)" style="height:19px;width:7.8%;font-size:9pt;line-height: 18px">
										<input type="button" name="Submit476" value=")" class="smallbutton" onclick="insertTxt(this.value)" style="height:19px;width:7.8%;font-size:9pt;line-height: 18px">
										<input type="button" name="Submit4722" value=">" class="smallbutton" onclick="insertTxt(this.value)" style="height:19px;width:7.8%;font-size:9pt;line-height: 18px">
										<input type="button" name="Submit4754" value="<=" class="smallbutton" onclick="insertTxt(this.value)" style="height:19px;width:7.8%;font-size:9pt;line-height: 18px">
										<input type="button" name="Submit4752" value="<bean:message key="kq.formula.even"/>" class="smallbutton" onclick="insertTxt(this.value)" style="height:19px;width:8%;font-size:9pt;line-height: 18px">
										<input type="button" name="Submit46" value="<bean:message key="kq.formula.if"/>" class="smallbutton" onclick="insertTxt(this.value)" style="height:19px;width:8%;font-size:9pt;line-height: 18px">
										<input type="button" name="Submit4764" value="<bean:message key="kq.formula.fou"/>" class="smallbutton" onclick="insertTxt(this.value)" style="height:19px;width:11%;font-size:9pt;line-height: 18px">
										</td>
										</tr>
										<tr>
										<td>
										<input type="button" name="Submit47" value="+" class="smallbutton" onclick="insertTxt(this.value)" style="height:19px;width:7.8%;font-size:9pt;line-height: 18px">
										<input type="button" name="Submit472" value="-" class="smallbutton" onclick="insertTxt(this.value)" style="height:19px;width:7.8%;font-size:9pt;line-height: 18px">
										<input type="button" name="Submit473" value="*" class="smallbutton" onclick="insertTxt(this.value)" style="height:19px;width:7.8%;font-size:9pt;line-height: 18px">
										<input type="button" name="Submit474" value="/" class="smallbutton" onclick="insertTxt(this.value)" style="height:19px;width:7.8%;font-size:9pt;line-height: 18px">
										<input type="button" name="Submit475" value="\" class="smallbutton" onclick="insertTxt(this.value)" style="height:19px;width:7.8%;font-size:9pt;line-height: 18px">
										<input type="button" name="Submit476" value="%" class="smallbutton" onclick="insertTxt(this.value)" style="height:19px;width:7.8%;font-size:9pt;line-height: 18px">
										<input type="button" name="Submit4732" value="<" class=" smallbutton" onclick="insertTxt(this.value)" style="height:19px;width:7.8%;font-size:9pt;line-height: 18px">
										<input type="button" name="Submit4742" value="<>" class="smallbutton" onclick="insertTxt(this.value)" style="height:19px;width:7.8%;font-size:9pt;line-height: 18px">
										<input type="button" name="Submit4762" value="<bean:message key="kq.formula.or"/>" class="smallbutton" onclick="insertTxt(this.value)" style="height:19px;width:8%;font-size:9pt;line-height: 18px">
										<input type="button" name="Submit46" value="<bean:message key="kq.formula.then"/>" class="smallbutton" onclick="insertTxt(this.value)" style="height:19px;width:8%;font-size:9pt;line-height: 18px">
										<input type="button" name="Submit4764" value="<bean:message key="kq.formula.end"/>" class="smallbutton" onclick="insertTxt(this.value)" style="height:19px;width:11%;font-size:9pt;line-height: 18px">
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
			<tr>
				<td align="center" valign="middle" height="35px;">
					<input type="button" name="Submit" value="确定" class="mybutton" onClick="save()">
					<input type="button" name="Submit" value="返回" class="mybutton" onClick="cancel()">
				</td>
			</tr>
		</table>
	</form>
	
</body>

<script language="javaScript">
	var info;
	if(parent.Ext){
		if(parent.Ext.getCmp('exebolish')){
			info = parent.Ext.getCmp('exebolish').parameter;
		}
	}else{
		info = dialogArguments;
	}
  	document.complexQueryForm.formula.value=info;
	Element.hide('cid');
	
</script>
