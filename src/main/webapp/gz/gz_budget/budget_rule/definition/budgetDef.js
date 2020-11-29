
function Openformula() {
	var thecodeurl = "/gz/gz_budget/budget_rule/formula.do?b_query=link&flag=fromdef";
	window.location.href = thecodeurl;
}

function addItem()
{//新增      
    var thecodeurl ="definition/budgetDef_tabname.jsp?tabname=&mode=add"; 
    if(isIE6() ){
        var retvo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:430px; dialogHeight:230px;resizable:no;center:yes;scroll:yes;status:no");  
    }else{
        var retvo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:400px; dialogHeight:200px;resizable:no;center:yes;scroll:yes;status:no");  
    }
    if (retvo==null) return;                           	
	if(retvo.success!="1")	{
		return;
	}
	var newName =retvo.formulaname;      
    //  var newName=prompt(GZ_BUDGET_INFO1,"");
      if(newName==null)
          return;
      if(trim(newName)=='')
      {
         alert(GZ_BUDGET_INFO2);
         return;
      }
      var hashvo=new ParameterSet();
      hashvo.setValue("isAdd","1");
      hashvo.setValue("name",getEncodeStr(newName));
      var request=new Request({asynchronous:false,onSuccess:additem_ok,functionId:'302001020402'},hashvo); 
}

function additem_ok(outparameters)
{//新增成功后
   var isExistName=outparameters.getValue("isExistName");
   if(isExistName=="1")//如果预算表存在了
   {
   		alert(GZ_BUDGET_INFO3);
   }
   else
   {
   		budgetDefForm.action="/gz/gz_budget/budget_rule/definition.do?b_query=other&isadd=1";
   		budgetDefForm.submit();
   }
}

function saveasItem()
{//另存为
	  //先判断是否选中了多条记录。应该只能选择一条记录
	  var dd=false;//判断是否有选中的项
	  var dd2=true;//判断是否选中了多条
	  var budgetTab_id="";
	  var k=1;
	  var index=0;
	  var oldname;
	  var  obj = document.getElementsByName("ids");
	  var oldnameObj=document.getElementsByName("tablename");
	for(var i=0;i<document.budgetDefForm.elements.length;i++)
		{			
	   		if(document.budgetDefForm.elements[i].type=='checkbox'&&document.budgetDefForm.elements[i].name!="selbox")       
	   			{	
			  		if(document.budgetDefForm.elements[i].checked)
			  			{
			  				dd=true;
							budgetTab_id=obj[index].value;
							oldname=oldnameObj[index].value;
							if(k>1)
							{
								dd2=false;
								break;
							}
							k++;
						}
						index++;
				}
		}
	 if(!dd)
	 {
	 	alert('请选择要另存的预算表！');
	 	return;
	 }
	 if(!dd2)
	 {
	 	alert(GZ_BUDGET_INFO5);
	 	return;
	 } 
    var thecodeurl ="definition/budgetDef_tabname.jsp?tabname=&mode=saveas"; 
    if(isIE6() ){
        var retvo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:430px; dialogHeight:230px;resizable:no;center:yes;scroll:yes;status:no"); 
    }else{
        var retvo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:400px; dialogHeight:200px;resizable:no;center:yes;scroll:yes;status:no"); 
    }
 
    if (retvo==null) return;                           	
	if(retvo.success!="1")	{
		return;
	}
	var newName =retvo.formulaname;   
    //  var newName=prompt(GZ_BUDGET_INFO1,"");
      if(newName==null)
          return;
      if(trim(newName)=='')
      {
         alert(GZ_BUDGET_INFO2);
         return;
      }
      var hashvo=new ParameterSet();
      hashvo.setValue("isAdd","2");
      hashvo.setValue("name",getEncodeStr(newName));
      hashvo.setValue("budgetTab_id",getEncodeStr(budgetTab_id));
      var request=new Request({asynchronous:false,onSuccess:additem_ok,functionId:'302001020402'},hashvo); 
}



function renameItem()
{
	//重命名
	  //先判断是否选中了多条记录。应该只能选择一条记录
	  var dd=false;//判断是否有选中的项
	  var dd2=true;//判断是否选中了多条
	  var budgetTab_id="";
	  var k=1;
	  var index=0;
	  var oldname;
	  var  obj = document.getElementsByName("ids");
	  var oldnameObj=document.getElementsByName("tablename");
	for(var i=0;i<document.budgetDefForm.elements.length;i++)
		{			
	   		if(document.budgetDefForm.elements[i].type=='checkbox'&&document.budgetDefForm.elements[i].name!="selbox")       
	   			{	
			  		if(document.budgetDefForm.elements[i].checked)
			  			{
			  				dd=true;
							budgetTab_id=obj[index].value;
							oldname=oldnameObj[index].value;
							if(k>1)
							{
								dd2=false;
								break;
							}
							k++;
						}
						index++;
				}
		}
	 if(!dd)
	 {
	 	alert(GZ_BUDGET_INFO4);
	 	return;
	 }
	 if(!dd2)
	 {
	 	alert(GZ_BUDGET_INFO5);
	 	return;
	 }   
    var thecodeurl ="definition/budgetDef_tabname.jsp?tabname="+$URL.encode(oldname)+"&mode=update";
    if(isIE6() ){
        var retvo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:430px; dialogHeight:230px;resizable:no;center:yes;scroll:yes;status:no"); 
    }else{
        var retvo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:400px; dialogHeight:200px;resizable:no;center:yes;scroll:yes;status:no"); 
    }
 
    if (retvo==null) return;                           	
	if(retvo.success!="1")	{
		return;
	}
	var newName =retvo.formulaname;   
	
      //var newName=prompt(GZ_BUDGET_INFO6,oldname);
      if(newName==null)
          return;
      if(trim(newName)=='')
      {
         alert(GZ_BUDGET_INFO2);
         return;
      }
      var hashvo=new ParameterSet();
      hashvo.setValue("isAdd","0");
      hashvo.setValue("budgetTab_id",getEncodeStr(budgetTab_id));
      hashvo.setValue("name",getEncodeStr(newName));
      var request=new Request({asynchronous:false,onSuccess:renameitem_ok,functionId:'302001020402'},hashvo); 
}

function renameitem_ok(outparameters)
{//新增成功后
   var isExistName=outparameters.getValue("isExistName");
   if(isExistName=="1")//如果预算表存在了
   {
   		alert(GZ_BUDGET_INFO3);
   }
   else
   {
   		budgetDefForm.action="/gz/gz_budget/budget_rule/definition.do?b_query=other&isadd=0";
   		budgetDefForm.submit();
   }
}

function deleteBudgetTable()
{
	//检查是否被选中了   如果选中，得到选中的项的id号，并执行删除
	var strIds="(";
	var dd=false;
	var index=0;
	var  obj = document.getElementsByName("ids");
	for(var i=0;i<document.budgetDefForm.elements.length;i++)
		{			
	   		if(document.budgetDefForm.elements[i].type=='checkbox'&&document.budgetDefForm.elements[i].name!="selbox")       
	   			{	
			  		if(document.budgetDefForm.elements[i].checked)
			  			{
			  				dd=true;
							strIds=strIds+obj[index].value+",";
						}
						index++;
				}
		}
		if(!dd)
		{
		    alert(GZ_BUDGET_INFO7);
		    return;
    	}
    	else
    	{
    		strIds = strIds.substring(0,strIds.length-1)+")";
			if(confirm(GZ_BUDGET_INFO8))
			{
				var hashvo=new ParameterSet();
	     		hashvo.setValue("ids",getEncodeStr(strIds));
	     		var request=new Request({asynchronous:false,onSuccess:delete_ok,functionId:'302001020403'},hashvo);
			}
    	}
}	
function delete_ok(outparameters)
{
	var errorMessage=outparameters.getValue("errorMessage");
	var isSuccess=outparameters.getValue("isSuccess");
   if(isSuccess=="0")//如果删除失败了
   {
   		alert(errorMessage);
   }
   else
   {
   		budgetDefForm.action="/gz/gz_budget/budget_rule/definition.do?b_query=other";
   		budgetDefForm.submit();
   }
}
	
function setProperty(tab_id)
{
	budgetDefForm.action="/gz/gz_budget/budget_rule/definition.do?b_searchProp=other&tab_id="+tab_id; 
   	budgetDefForm.submit();

}
function closeWindow()
{
	window.close();
}
function saveProperty()
{
	budgetDefForm.action="/gz/gz_budget/budget_rule/definition.do?b_saveProp=other";
   	budgetDefForm.submit();
}

function moveRecord(tab_id,seq,move)
{
	//排序
	var hashvo=new ParameterSet();
	hashvo.setValue("tab_id",getEncodeStr(tab_id));
	hashvo.setValue("seq",getEncodeStr(seq));
	hashvo.setValue("move",getEncodeStr(move));
	var request=new Request({asynchronous:false,onSuccess:seq_ok,functionId:'302001020406'},hashvo);
}
function seq_ok(outparameters)
{
	budgetDefForm.action="/gz/gz_budget/budget_rule/definition.do?b_query=other";
   	budgetDefForm.submit();
}
/**
 * 判断当前浏览器是否为ie6
 * 返回boolean 可直接用于判断 
 * @returns {Boolean}
 */
function isIE6() 
{ 
	if(navigator.appName == "Microsoft Internet Explorer") 
	{ 
		if(navigator.userAgent.indexOf("MSIE 6.0")>0) 
		{ 
			return true;
		}else{
			return false;
		}
	}else{
		return false;
	}
}