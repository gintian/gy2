function getNewField1()
{
	//当改变子集时，相应的指标也要发生变化。
	var hashvo=new ParameterSet();
	hashvo.setValue("setType","1"); 
	hashvo.setValue("fieldsetid",getEncodeStr(budgetSysForm.ysze_set.value));
   	var request=new Request({asynchronous:false,onSuccess:showSelectList1,functionId:'302001020409'},hashvo);
}
function getNewField2()
{
	//当改变子集时，相应的指标也要发生变化。
	var hashvo=new ParameterSet(); 
	hashvo.setValue("setType","0"); 
	hashvo.setValue("fieldsetid",getEncodeStr(budgetSysForm.ysparam_set.value));
   	var request=new Request({asynchronous:false,onSuccess:showSelectList2,functionId:'302001020409'},hashvo);
}
 function showSelectList1(outparamters)
 {
 	var budgetIndexList=outparamters.getValue("budgetIndexList");
 	var budgetTotalList=outparamters.getValue("budgetTotalList");
 	var spStatusList=outparamters.getValue("spStatusList");
	AjaxBind.bind(budgetSysForm.ysze_idx_menu,budgetIndexList);
	AjaxBind.bind(budgetSysForm.ysze_ze_menu,budgetTotalList);
	AjaxBind.bind(budgetSysForm.ysze_status_menu,spStatusList);

	///var fielditem_vo=eval("document.trainStationForm.post_setid");
	
 }
 function showSelectList2(outparamters)
 {
 	var budgetIndexFieldList=outparamters.getValue("budgetIndexFieldList");
 	var employeeList=outparamters.getValue("employeeList");
	AjaxBind.bind(budgetSysForm.ysparam_idx_menu,budgetIndexFieldList);
	AjaxBind.bind(budgetSysForm.ysparam_newmonth_menu,employeeList);
		
	///var fielditem_vo=eval("document.trainStationForm.post_setid");
	
 }
 function saveDblist()
 {
 	//先获取所有选中的人员库（字母）
 	var dblist1="";
	var index=0;
	var  obj = document.getElementsByName("ids");
	for(var i=0;i<document.budgetSysForm.elements.length;i++)
		{			
	   		if(document.budgetSysForm.elements[i].type=='checkbox'&&document.budgetSysForm.elements[i].name!="selbox")       
	   			{	
			  		if(document.budgetSysForm.elements[i].checked)
			  			{
							dblist1=dblist1+obj[index].value+",";
						}
						index++;
				}
		}
	//再获取所有选中的人员库（汉字）
	var dblist2="";
	var index2=0;
	var  obj2 = document.getElementsByName("dbname");
	for(var i=0;i<document.budgetSysForm.elements.length;i++)
		{			
	   		if(document.budgetSysForm.elements[i].type=='checkbox'&&document.budgetSysForm.elements[i].name!="selbox")       
	   			{	
			  		if(document.budgetSysForm.elements[i].checked)
			  			{
							dblist2=dblist2+obj2[index2].value+",";
						}
						index2++;
				}
		}
	//最后把数据保存起来
	var thevo=new Object();
	thevo.dblist_no=dblist1;
	thevo.dblist_name=dblist2;
	window.returnValue=thevo;
	window.close();	
 }
 
 function setDBList()
 {
 	var url="/gz/gz_budget/budget_rule/options.do?b_searchDblist=link`dblist="+budgetSysForm.dblist.value;
	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(url)
	var return_vo=window.showModalDialog(iframe_url,"","dialogWidth=300px;dialogHeight=300px;resizable=yes;scroll=no;status=no;");
	if(return_vo!=null)
	{
		var dblist1=return_vo.dblist_no;
 		var dblist2=return_vo.dblist_name;
 		budgetSysForm.dblist.value=dblist1;
 		budgetSysForm.dblist_name.value=dblist2;
	}
 	
 }
 function complexCondition()
{
	//调用人员范围  	
	var tmp =document.getElementsByName("range")[0];
	var tmpvalue=tmp.value;
	var expr=tmpvalue;
	var strExpression=generalComplexConditionDialog(expr,"0",GZ_TEMPLATESET_LOOKCONDITION,"4");
	if(strExpression!=undefined)
	{
		document.budgetSysForm.range.value=strExpression;
		expr=strExpression;
	}
}

function getorg()
{
		//获得单位
		//参数：flag=0`selecttype=1（1代表有checkbox,0代表没有checkbox）`dbtype=0`priv=1`isfilter=0`loadtype=1(0时，连岗位都出来了。2时，只有单位。1时，有单位和部门。)
		var ret_vo=select_org_emp_dialog(0,1,0,1,0,2);
		if(ret_vo)
		{
			var str=ret_vo.content;
			document.budgetSysForm.units_name.value=ret_vo.title;
			document.budgetSysForm.units.value=ret_vo.content;
		}
}
function saveSystemParams()
{
	var createTXrecord="0";
	if(budgetSysForm.createTXrecord.checked)
		createTXrecord="1";
	var datatoze="0";
	if(budgetSysForm.datatoze.checked)
		datatoze="1";
	var hashvo=new ParameterSet(); 
	hashvo.setValue("kindstr",getEncodeStr(budgetSysForm.kindstr.value));
	hashvo.setValue("rylb_codeset",getEncodeStr(budgetSysForm.rylb_codeset.value));
	hashvo.setValue("unitmenu",getEncodeStr(budgetSysForm.unitmenu.value));
	hashvo.setValue("dblist",getEncodeStr(budgetSysForm.dblist.value));
	hashvo.setValue("range",getEncodeStr(budgetSysForm.range.value));
	hashvo.setValue("units",getEncodeStr(budgetSysForm.units.value));
	hashvo.setValue("createTXrecord",getEncodeStr(createTXrecord));
	hashvo.setValue("datatoze",getEncodeStr(datatoze));
	hashvo.setValue("ysze_set",getEncodeStr(budgetSysForm.ysze_set.value));
	hashvo.setValue("ysze_idx_menu",getEncodeStr(budgetSysForm.ysze_idx_menu.value));
	hashvo.setValue("ysze_ze_menu",getEncodeStr(budgetSysForm.ysze_ze_menu.value));
	hashvo.setValue("ysze_status_menu",getEncodeStr(budgetSysForm.ysze_status_menu.value));
	hashvo.setValue("ysparam_set",getEncodeStr(budgetSysForm.ysparam_set.value));
	hashvo.setValue("txCode",getEncodeStr(budgetSysForm.txCode.value));
	hashvo.setValue("ysparam_idx_menu",getEncodeStr(budgetSysForm.ysparam_idx_menu.value));
	hashvo.setValue("ysparam_newmonth_menu",getEncodeStr(budgetSysForm.ysparam_newmonth_menu.value));
   	var request=new Request({asynchronous:false,onSuccess:save_ok,functionId:'302001020408'},hashvo);
}
function save_ok(outparamters)
{
	var mess=outparamters.getValue("mess");
	alert(mess);
}