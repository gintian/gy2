/**总分公式开始*/
function addrelate(name,obj)
{
	var no = new Option();
	for(i=0;i<obj.options.length;i++)
	{
		if(obj.options[i].selected)
		{
	    	no.value=obj.options[i].value;
	    	no.text=obj.options[i].text;
		}
	}
	var formula_sys = undefined;
	if(document.getElementById("scoreFormula01")!=null&&document.getElementById("scoreFormula01").style.display!="none"){//总分公式
		formula_sys = document.getElementById("total_formula");
		formula_sys.focus();
	}else if(document.getElementById("scoreFormula02")!=null&&document.getElementById("scoreFormula02").style.display!="none")
	{//总分纠偏公式
		var formula_sys = document.getElementById("total_deviation_formula");
		formula_sys.focus();
	}else{
		var formula_sys = document.getElementById(name);
		formula_sys.focus();
	}
	var element = document.selection;
	if (element!=null) {
		var rge = element.createRange();
		if (rge!=null)	
			rge.text=no.value;
	}else{
        var start =formula_sys.selectionStart;
        formula_sys.value = formula_sys.value.substring(0, start) + no.value + formula_sys.value.substring(start, formula_sys.value.length);
        formula_sys.setSelectionRange(start + no.value.length, start + no.value.length);
    }
}
//排名指标
function show_pmzb(plan_id)
{
	var target_url="/performance/evaluation/performanceEvaluation.do?b_rankTarget=link`plan_id="+plan_id;
 	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(target_url);
	var config = {
	    width:450,
	    height:180,
	    type:'1',
	    id:'show_pmzb_win'
	}

	modalDialog.showModalDialogs(iframe_url,"show_pmzb_win",config,"");
}
function saveExpr()
{
	checkFormula_total('save');
}
function checkFormula_total()
{
/*
	var hashvo=new ParameterSet();
	hashvo.setValue("temp",temp);
	hashvo.setValue("planid",document.evaluationForm.planid.value);
	hashvo.setValue("formula",getEncodeStr(document.getElementById('formula_total').value));
	hashvo.setValue("khObjWhere2",getEncodeStr(document.evaluationForm.khObjWhere2.value));
	var request=new Request({method:'post',onSuccess:showresult,functionId:'9024003102'},hashvo);*/
	
	var hashvo=new ParameterSet();
	hashvo.setValue("type",'total_formula');
	hashvo.setValue("planid",document.evaluationForm.planid.value);
	if(document.getElementById('total_formula')!=null)
		hashvo.setValue("formula",getEncodeStr(document.getElementById('total_formula').value));
	var request=new Request({method:'post',onSuccess:resultCheckFormula,functionId:'9024000026'},hashvo);
}
function showresult(outparamters)
{
	var mess = outparamters.getValue("mess");
	var temp = outparamters.getValue("temp");
	var fsql = outparamters.getValue("fsql");
	if(mess=='ok'){
		if(temp=="check"){
			alert(FORMULA_OK);
		}
		else{
			var isReCalcu='no';
			if(confirm(IS_RECALCU_FORMULA))
				isReCalcu='ok';		

			evaluationForm.action="/performance/evaluation/expressions.do?b_save=link&fsql="+getEncodeStr(getDecodeStr(fsql))+"&isReCalcu="+isReCalcu;
			evaluationForm.submit();
			window.returnValue=isReCalcu;
			window.close();
			window.dialogArguments.window.location = window.dialogArguments.window.location
		}
	}
	else{
		alert(getDecodeStr(mess));
		return;
	}
}
function importexpre1()
{
	var strurl="/performance/evaluation/set_import.do?b_search=link`busitype="+evaluationForm.busitype.value+"`planid="+evaluationForm.planid.value+"`flag=expr";
	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(strurl);
	//var ss=window.showModalDialog(iframe_url,window,"dialogWidth=850px;dialogHeight=700px;resizable=yes;scroll=yes;status=no;");  
	var config = {
	    width:850,
	    height:700,
	    type:'2'
	}

	modalDialog.showModalDialogs(iframe_url,"importexpre1_win",config,importexpre1_ok);
}
function importexpre1_ok(ss) {
	if(ss=='ok')
	{
		var hashvo=new ParameterSet();
		hashvo.setValue("planid",evaluationForm.planid.value);
		hashvo.setValue("opt","17");
		var request=new Request({method:'post',asynchronous:false,onSuccess:chanPlanList,functionId:'9023000003'},hashvo);
	}
}
function chanPlanList(outparamters)
{
	var list=outparamters.getValue("list");
	AjaxBind.bind(evaluationForm.expression,list);
}

function symbol2(cal)
{	
	var formula_sys = undefined;
	if(document.getElementById("scoreFormula01")){
		if(document.getElementById("scoreFormula01").style.display!="none"){//总分公式
			formula_sys = document.getElementById("total_formula");
			formula_sys.focus();
		}else{//总分纠偏公式
			formula_sys = document.getElementById("total_deviation_formula");
			formula_sys.focus();
		}
	}else{
		formula_sys = document.getElementById("total_formula");
		formula_sys.focus();
	}

	var element = document.selection;
	if (element!=null) 
	{
		var rge = element.createRange();
		if (rge!=null)	
			rge.text=cal;
	}else{
        var start =formula_sys.selectionStart;
        formula_sys.value = formula_sys.value.substring(0, start) + cal + formula_sys.value.substring(start, formula_sys.value.length);
        formula_sys.setSelectionRange(start + cal.length, start + cal.length);
    }
}
/**总分公式结束*/
/**考核系数公式开始*/
function subFormula_xishu(plan_id)
{
	var isReCalcu='no';
	if(confirm(IS_RECALCU_FORMULA))
		isReCalcu='ok'		
			
  	var hashvo=new ParameterSet();
	var m = document.evaluationForm.expr.value;	  		
	hashvo.setValue("c_expr",getEncodeStr(m));
	hashvo.setValue("khObjWhere2",getEncodeStr(document.evaluationForm.khObjWhere2.value));
	hashvo.setValue("planid",plan_id);
	hashvo.setValue("isReCalcu",isReCalcu);
	var request=new Request({method:'post',asynchronous:false,onSuccess:returnSubExpr,functionId:'9024000018'},hashvo);		
	

}  
function returnSubExpr(outparamters)
{
	var info = outparamters.getValue("info");
 	var isReCalcu = outparamters.getValue("isReCalcu");
 	if(info=="ok")
 	{			
		returnValue=isReCalcu;
  	  	window.close();			
  	}
	else
	{
		alert(getDecodeStr(info));
	}
}  
function checkFormula_xishu()
{
	/*
	var hashvo=new ParameterSet();
	var m = document.evaluationForm.expr.value;
	hashvo.setValue("c_expr",getEncodeStr(m));
	hashvo.setValue("khObjWhere2",getEncodeStr(document.evaluationForm.khObjWhere2.value));
	hashvo.setValue("planid",plan_id);
	var request=new Request({method:'post',asynchronous:false,onSuccess:resultCheckExpr,functionId:'9024000017'},hashvo);	*/
	
	var hashvo=new ParameterSet();
	hashvo.setValue("type",'xishu_formula');
	hashvo.setValue("planid",document.evaluationForm.planid.value);
	if(document.getElementById('xishu_formula')!=null)
		hashvo.setValue("formula",getEncodeStr(document.getElementById('xishu_formula').value));
	var request=new Request({method:'post',onSuccess:resultCheckFormula,functionId:'9024000026'},hashvo);	
}  
function resultCheckFormula(outparamters)
{
  	var info = outparamters.getValue("errorInfo");
  	info = getDecodeStr(info);
	if(info=="ok"||info.length==0)
		alert("公式通过检查!");
	else
		alert(info);
}

//以防止点击多次报错 在点击确定按钮的时候先设按钮不可用 然后错误提示出来再将按钮设为可用
function setDisabled(flag)
{
	var obj=document.getElementsByName("save3");
	if(obj.length>0)
		obj[0].disabled=flag;
	obj=document.getElementsByName("save2");
	if(obj.length>0)
		obj[0].disabled=flag;
	obj=document.getElementsByName("save3");
	if(obj.length>0)
		obj[0].disabled=flag;
}

function subFormula()
{
/*
	//先验证公式	
	var signLogo = false;
	if(document.getElementById("gradeFormula0")!=null && document.getElementById("gradeFormula0").checked==true)
	{					
		signLogo = true;
	}
	if(document.getElementById("gradeFormula2")!=null && document.getElementById("gradeFormula2").checked==true)
	{
		signLogo = true;
		var custom_formula="";
		if(document.getElementById("custom_formula")!=null)	
			custom_formula=getEncodeStr(document.getElementById('custom_formula').value);						
		if(custom_formula==null || custom_formula.length<=0)
		{		
			alert("请定义等级公式");
			return;
		}	
	}
	if(document.getElementById("gradeFormula1")!=null && document.getElementById("gradeFormula1").checked==true)
	{	
		signLogo = true;
		var procedureName="";
		if(document.getElementById("procedureName")!=null)	
			procedureName=getEncodeStr(document.getElementById('procedureName').value);
		if(procedureName==null || procedureName.length<=0)
		{		
			alert("请输入等级的存储过程名称");
			return;
		}		
	}
	if(!signLogo)
	{
		alert("请设置等级计算方法！");
		return;
	}	
	
	var hashvo=new ParameterSet();
	setDisabled(true);
	hashvo.setValue("type",'total_xishu_formula');			
	hashvo.setValue("planid",document.evaluationForm.planid.value);
	if(document.getElementById("xishu_formula")!=null)
		hashvo.setValue("xishu_formula",getEncodeStr(document.getElementById('xishu_formula').value));
	if(document.getElementById("total_formula")!=null)
		hashvo.setValue("total_formula",getEncodeStr(document.getElementById('total_formula').value));
	if(document.getElementById("custom_formula")!=null)
		hashvo.setValue("custom_formula",getEncodeStr(document.getElementById('custom_formula').value));
	var request=new Request({method:'post',onSuccess:resultCheckFormula2,functionId:'9024000026'},hashvo);	
*/
	

	//先验证公式
	if(document.getElementById("gradeFormula1")!=null && document.getElementById("gradeFormula1").checked==true)
	{	
		var procedureName="";
		if(document.getElementById("procedureName")!=null)	
			procedureName=getEncodeStr(document.getElementById('procedureName').value);
		if(procedureName!=null && procedureName.length>0)
		{
			var hashvo=new ParameterSet();
			setDisabled(true);
			hashvo.setValue("type",'total_xishu_formula');
			hashvo.setValue("planid",document.evaluationForm.planid.value);
			if(document.getElementById("xishu_formula")!=null)
				hashvo.setValue("xishu_formula",getEncodeStr(document.getElementById('xishu_formula').value));
			if(document.getElementById("total_formula")!=null)
				hashvo.setValue("total_formula",getEncodeStr(document.getElementById('total_formula').value));
			if(document.getElementById("custom_formula")!=null)
				hashvo.setValue("custom_formula",getEncodeStr(document.getElementById('custom_formula').value));
			var request=new Request({method:'post',onSuccess:resultCheckFormula2,functionId:'9024000026'},hashvo);		
		}else
		{
			alert("请输入等级的存储过程名称");
			return;
		}
		
	}else if((document.getElementById("gradeFormula2")!=null && document.getElementById("gradeFormula2").checked==true) || (document.getElementById("gradeFormula3")!=null && document.getElementById("gradeFormula3").checked==true))
	{
		var custom_formula="";
		if(document.getElementById("custom_formula")!=null)	
			custom_formula=getEncodeStr(document.getElementById('custom_formula').value);
		
		var gjsjformula="noCheck";
		if(document.getElementById("gjsjformula_c")!=null && document.getElementById("gjsjformula_c").checked==true)
		{
			gjsjformula="check_ed";
		}		
		if(custom_formula!=null && custom_formula.length>0)
		{
			var hashvo=new ParameterSet();
			setDisabled(true);
			hashvo.setValue("type",'total_xishu_formula');
			hashvo.setValue("gjsjformula",gjsjformula);
			hashvo.setValue("planid",document.evaluationForm.planid.value);
			if(document.getElementById("xishu_formula")!=null)
				hashvo.setValue("xishu_formula",getEncodeStr(document.getElementById('xishu_formula').value));
			if(document.getElementById("total_formula")!=null)
				hashvo.setValue("total_formula",getEncodeStr(document.getElementById('total_formula').value));
			if(document.getElementById("custom_formula")!=null)
				hashvo.setValue("custom_formula",getEncodeStr(document.getElementById('custom_formula').value));
			var request=new Request({method:'post',onSuccess:resultCheckFormula2,functionId:'9024000026'},hashvo);	
		}else
		{
			alert("请定义等级公式");
			return;
		}	
	}else
	{	
		if(document.getElementById("deviationScore")!=null&&!document.getElementById("deviationScore").checked)
		 {
		    document.getElementById("deviationScore").value="0";
		    document.getElementById("deviationScore").checked=true;
		 }
		var hashvo=new ParameterSet();
		setDisabled(true);
		hashvo.setValue("type",'total_xishu_formula');
		hashvo.setValue("planid",document.evaluationForm.planid.value);
		if(document.evaluationForm.deviationScore!=null)
		hashvo.setValue("deviationScore",document.evaluationForm.deviationScore.value);//是否使用总分纠偏公式  0不使用 1使用
		if(document.getElementById("xishu_formula")!=null)
			hashvo.setValue("xishu_formula",getEncodeStr(document.getElementById('xishu_formula').value));
		if(document.getElementById("total_formula")!=null)///总分公式
			hashvo.setValue("total_formula",getEncodeStr(document.getElementById('total_formula').value));
		if(document.getElementById("total_deviation_formula")!=null)//总分纠偏公式
			hashvo.setValue("total_deviation_formula",getEncodeStr(document.getElementById('total_deviation_formula').value));
		//个人感觉 不启用自定义等级公式的话就没有必要校验自定义公式的有效性吧 haosl update 2019.12.19
		if((document.getElementById("gradeFormula2")!=null && document.getElementById("gradeFormula2").checked==true) || (document.getElementById("gradeFormula3")!=null && document.getElementById("gradeFormula3").checked==true)){
			if(document.getElementById("custom_formula")!=null)
				hashvo.setValue("custom_formula",getEncodeStr(document.getElementById('custom_formula').value));
		}
		var request=new Request({method:'post',onSuccess:resultCheckFormula2,functionId:'9024000026'},hashvo);
	}	
}
function resultCheckFormula2(outparamters)
{	

  	var info = outparamters.getValue("errorInfo");
  	var gjsj_mula = outparamters.getValue("gjsjformula");
  	info = getDecodeStr(info);
	if(info=="ok"||info.length==0)
	{
		var isReCalcu='no';
		var degreeCompute = IS_RECALCU_FORMULA;//是否重新计算总分,绩效系数及等级？;
		if(evaluationForm.busitype!=null && evaluationForm.busitype.value==1)
			degreeCompute = IS_RECALCU_ComputFORMULA;//是否重新计算总分及等级
		
		if(confirm(degreeCompute))
		{
			isReCalcu='ok'				
			document.getElementById("b_ok").disabled=true;
	  		jinduo_tiao();
			document.evaluationForm.action="/performance/evaluation/performanceEvaluation.do?b_saveFormula=link&gjsj_mula="+gjsj_mula+"&isReCalcu="+isReCalcu;
		    document.evaluationForm.submit();
		    		    
		    var hashvo=new ParameterSet();			
			hashvo.setValue("planid",document.evaluationForm.planid.value);			
			var request=new Request({method:'post',onSuccess:resultYesOrNo,functionId:'9024000292'},hashvo);		    		    
	    }else
	    {
	    	isReCalcu='no'				
	    	document.evaluationForm.action="/performance/evaluation/performanceEvaluation.do?b_saveFormula=link&gjsj_mula="+gjsj_mula+"&isReCalcu="+isReCalcu;
		    document.evaluationForm.submit();
	    }
	}
	else
	{	
	if(document.getElementById("deviationScore")!=null&&document.getElementById("deviationScore").value=="0")
		 {
		    document.getElementById("deviationScore").checked=false;
		 }
		alert(info);
		setDisabled(false);
	}
}
function resultYesOrNo(outparamters)
{
	var yesOrn = outparamters.getValue("yScoreNGrade"); 	  	  	
	if(yesOrn=="yes")
		yScoreNgrade();
  	
}
//  列出分数相同但考核等级不同的考核对象 
function yScoreNgrade()
{
 	var target_url="/performance/evaluation/performanceEvaluation.do?b_searchYSorNG=link";
 	var iframe_url="/general/query/common/iframe_query.jsp?src="+target_url;
	//var return_vo= window.showModalDialog(iframe_url, "", 
	//		"dialogWidth:450px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
	var config = {
	    width:450,
	    height:400,
	    type:'2'
	}

	modalDialog.showModalDialogs(iframe_url,"yScoreNgrade_win",config,"");
}

function jinduo_tiao()
{
 	var x=document.body.scrollLeft+100;
    var y=document.body.scrollTop+70; 
	var waitInfo;
	waitInfo=eval("wait");	
	waitInfo.style.top=y;
	waitInfo.style.left=x;	
	waitInfo.style.display="block";
}
function symbol(cal)
{
	var formula_sys = document.getElementById("expr");
	if(formula_sys == null) {
		formula_sys = document.getElementById("xishu_formula");
	}
	formula_sys.focus();
	var element = document.selection;
	if (element!=null) 
	{
		var rge = element.createRange();
		if (rge!=null)	
			rge.text=cal;
	}else{
        var start =formula_sys.selectionStart;
        formula_sys.value = formula_sys.value.substring(0, start) + cal + formula_sys.value.substring(start, formula_sys.value.length);
        formula_sys.setSelectionRange(start + cal.length, start + cal.length);
    }
	//if(document.getElementById("expr").pos!=null)
		//document.getElementById("expr").pos.text=cal;
	//else
		//document.getElementById("expr").value +=cal;	
}
var oper_func_wiz2 = "";
function function_Wizard2(planid,oper){
	oper_func_wiz2 = oper;
    var thecodeurl ="/org/funwd/function_Wizard.do?b_query=link`flag=1`checktemp=jixiaoguanli`planid="+planid+"`callBackFunc=function_Wizard2_ok"; 
    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
    //var return_vo= window.showModalDialog(thecodeurl, "", 
    //         "dialogWidth:400px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
    
    var config = {
	    width:400,
	    height:400,
	    type:'1',
	    id:'function_Wizard2_win'
	}

	modalDialog.showModalDialogs(iframe_url,"function_Wizard2_win",config,function_Wizard2_ok);
    
}
function function_Wizard2_ok(return_vo) {
	if(return_vo!=null)
    {
    	if(oper_func_wiz2=='xishu')
  	 		symbol(return_vo);
  	 	else if(oper_func_wiz2=='sum')
			symbol2(return_vo);
		else if(oper_func_wiz2=='custom')
			symbol3(return_vo);
  	}else{
  		return ;
  	}
}

function closeFunc(){
	if(Ext.getCmp("function_Wizard2_win"))
		Ext.getCmp("function_Wizard2_win").close();
}
function setCode()
{
  	if(document.evaluationForm.codeitem.value!='')
  	{
  	   for(var i=0;i<document.evaluationForm.codeitem.options.length;i++)
  	   {
  	        if(document.evaluationForm.codeitem.options[i].selected==true)
  	        	symbol(document.evaluationForm.codeitem.options[i].value);
  	   } 	
  	}
}
/**考核系数公式结束*/
/**等级公式开始*/
function setDis(theObj)
{
	if(theObj.checked)
	{
		if(theObj.value=='0')
			evaluationForm.procedureName.disabled=true;
		else if(theObj.value=='1')
			evaluationForm.procedureName.disabled=false;
	}
}
function symbol3(cal)
{
	var formula_sys = document.getElementById("custom_formula");
	formula_sys.focus();
	var element = document.selection;
	if (element!=null) 
	{
		var rge = element.createRange();
		if (rge!=null)	
			rge.text=cal;
	}else{
        var start =formula_sys.selectionStart;
        formula_sys.value = formula_sys.value.substring(0, start) + cal + formula_sys.value.substring(start, formula_sys.value.length);
        formula_sys.setSelectionRange(start + cal.length, start + cal.length);
    }
}
function changeParams()
{	
	if(document.getElementById("gradeFormula0")!=null && document.getElementById("gradeFormula0").checked==true)
	{
		document.getElementById("gjsjformula_c").checked=false;
		document.getElementById('gjsjformula').style.display = 'none';
		document.getElementById('custom_formula').style.display = 'none';
		document.getElementById('zdygs').style.display = 'none';
		document.getElementById('ccgc').style.display = '';
		document.getElementById('custom_formula_check').style.display = 'none';
		document.getElementById('custom_xd_look').style.display = 'none';
		
	}else if(document.getElementById("gradeFormula1")!=null && document.getElementById("gradeFormula1").checked==true)
	{
		document.getElementById("gjsjformula_c").checked=false;
		document.getElementById('gjsjformula').style.display = 'none';
		document.getElementById('custom_formula').style.display = 'none';
		document.getElementById('zdygs').style.display = 'none';
		document.getElementById('ccgc').style.display = '';
		document.getElementById('custom_formula_check').style.display = 'none';
		document.getElementById('custom_xd_look').style.display = 'none';
		
	}else if((document.getElementById("gradeFormula2")!=null && document.getElementById("gradeFormula2").checked==true) || (document.getElementById("gradeFormula3")!=null && document.getElementById("gradeFormula3").checked==true))
	{
		document.getElementById('gjsjformula').style.display = '';
		document.getElementById('custom_formula').style.display = '';
		document.getElementById('zdygs').style.display = '';
		document.getElementById('ccgc').style.display = 'none';
		document.getElementById('custom_formula_check').style.display = '';
		document.getElementById('custom_xd_look').style.display = '';
	}
}
function setCodes()
{
  	if(document.evaluationForm.codeitems.value!='')
  	{
  	   for(var i=0;i<document.evaluationForm.codeitems.options.length;i++)
  	   {
  	        if(document.evaluationForm.codeitems.options[i].selected==true)
  	        	symbol3(document.evaluationForm.codeitems.options[i].value);
  	   } 	
  	}
}
function checkFormula_custom()
{
	/*
	var hashvo=new ParameterSet();
	var m = document.evaluationForm.expr.value;
	hashvo.setValue("c_expr",getEncodeStr(m));
	hashvo.setValue("khObjWhere2",getEncodeStr(document.evaluationForm.khObjWhere2.value));
	hashvo.setValue("planid",plan_id);
	var request=new Request({method:'post',asynchronous:false,onSuccess:resultCheckExpr,functionId:'9024000017'},hashvo);	*/
	
/*	
	if(document.getElementById("gradeFormula2")!=null && document.getElementById("gradeFormula2").checked==true)
	{
		var custom_formula="";
		if(document.getElementById('custom_formula')!=null)
			custom_formula=getEncodeStr(document.getElementById('custom_formula').value);			
		if(custom_formula==null || custom_formula.length<=0)
		{
			alert("请定义等级公式");
			return;
		}	
	}
*/
	
	if((document.getElementById("gradeFormula2")!=null && document.getElementById("gradeFormula2").checked==true) || (document.getElementById("gradeFormula3")!=null && document.getElementById("gradeFormula3").checked==true))
	{
		var custom_formula="";
		if(document.getElementById('custom_formula')!=null)
			custom_formula=getEncodeStr(document.getElementById('custom_formula').value);			
		if(custom_formula!=null && custom_formula.length>0)
		{
			if(document.getElementById('gjsjformula_c')!=null && document.getElementById('gjsjformula_c').checked==false && document.getElementById('custom_formula').value.indexOf("[等级]")!=-1)
			{
				alert("请选中[先根据等级分类规则生成等级]!");
				return;
			}
		}else
		{
			alert("请定义等级公式");
			return;
		}	
	}
	var hashvo=new ParameterSet();
	hashvo.setValue("type",'custom_formula');
	hashvo.setValue("planid",document.evaluationForm.planid.value);
	if(document.getElementById('custom_formula')!=null)
		hashvo.setValue("formula",getEncodeStr(document.getElementById('custom_formula').value));
	var request=new Request({method:'post',onSuccess:resultCheckFormula_custom,functionId:'9024000026'},hashvo);	
}  
function resultCheckFormula_custom(outparamters)
{
  	var info = outparamters.getValue("errorInfo");
  	info = getDecodeStr(info);
	if(info=="ok"||info.length==0)
		alert("公式通过检查!");
	else
		alert(info);
}

/**等级公式结束*/