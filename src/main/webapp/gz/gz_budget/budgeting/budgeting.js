/**
 * 薪资预算---预算编制
 */
 
 
 function computer(){
	 var hashvo=new ParameterSet();
     hashvo.setValue("flag","checkreportstatus");
     var request=new Request({asynchronous:false,onSuccess:computerOk,functionId:'302001020206'},hashvo);	
}
 
function computerOk(outparameters){
	var info = outparameters.getValue("info");
	if(info=='false'){
		alert("只能计算起草或驳回的数据！");
		return;
	}

	var thecodeurl="/gz/gz_budget/budgeting/budgeting_table.do?b_calc=link";
	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
	var retvo= window.showModalDialog(iframe_url, null, 
		        "dialogWidth:470px; dialogHeight:560px;resizable:no;center:yes;scroll:yes;status:no");			
  	 if(retvo==null)
  	 	return ;		        
	if(retvo.success=="1")	{
		alert("计算成功");
		refreshcur();
		
	}else if(retvo.success=="2")	{
		alert("计算失败："+retvo.strerror);
	}
    
}


function refreshcur(){
	 var currnode=Global.selectedItem; 
	 var tab_id=currnode.uid;
	var zonge=tab_id.substring(5);
	var params=tab_id.substring(6);
	if(tab_id.indexOf("zonge")==0){
	    budgetingForm.action="/gz/gz_budget/budgeting/budgeting_table.do?b_params_ze=link&b0110="+zonge+"&canshu=zonge";
		budgetingForm.target="mil_body";
		budgetingForm.submit()
	}else if(tab_id.indexOf("params")==0){
	    budgetingForm.action="/gz/gz_budget/budgeting/budgeting_table.do?b_params_ze=link&b0110="+params+"&canshu=params";
		budgetingForm.target="mil_body";
		budgetingForm.submit()
	}else if(tab_id==2){
		budgetingForm.action="/gz/gz_budget/budgeting/budgeting_table.do?b_tree=int&tab_id="+tab_id;
		budgetingForm.target="mil_body";
		budgetingForm.submit();
	}else{
	    budgetingForm.action="/gz/gz_budget/budgeting/budgeting_table.do?b_open=open&brefresh=true&tabid="+tab_id;
		budgetingForm.target="mil_body";
		budgetingForm.submit();
	}
}
function reportSp(){
	 var currnode=Global.selectedItem; 
	 var id=currnode.uid;
	 var name=currnode.title;
	 if(id=='root')
		 return;
	 if(id==-1)
		id=3;	 
	 if(confirm("您确认报批吗？")){
		 var hashvo=new ParameterSet();
	     hashvo.setValue("tab_id",id);
	     hashvo.setValue("name",name);
	     var request=new Request({asynchronous:false,onSuccess:reportOK,functionId:'302001020206'},hashvo);	
	 }else{
		 return;
	 }
}
function reportOK(outparameters){
	var info = outparameters.getValue("info");
	var tab_id=outparameters.getValue("tab_id");
	var zonge=tab_id.substring(5);
	var params=tab_id.substring(6);
	if(info=='0')
	{
		alert("报批成功！");
		if(tab_id.indexOf("zonge")==0){
		    budgetingForm.action="/gz/gz_budget/budgeting/budgeting_table.do?b_params_ze=link&b0110="+zonge+"&canshu=zonge";
			budgetingForm.target="mil_body";
			budgetingForm.submit()
		}else if(tab_id.indexOf("params")==0){
		    budgetingForm.action="/gz/gz_budget/budgeting/budgeting_table.do?b_params_ze=link&b0110="+params+"&canshu=params";
			budgetingForm.target="mil_body";
			budgetingForm.submit()
		}else if(tab_id==2){
			budgetingForm.action="/gz/gz_budget/budgeting/budgeting_table.do?b_tree=int&tab_id="+tab_id;
			budgetingForm.target="mil_body";
			budgetingForm.submit();
		}else{
		    budgetingForm.action="/gz/gz_budget/budgeting/budgeting_table.do?b_open=open&tab_id="+tab_id;
			budgetingForm.target="mil_body";
			budgetingForm.submit();
		
		}

	}else if(info=='1'){
		alert("只能报批起草和驳回状态的数据！");
		return;
	}else{
		alert("报批失败！");
		return;
	}
}
function downloadTemplate(tabid,flag){
	if (tabid==2){
		var thecodeurl="/gz/gz_budget/budgeting/budgeting_table.do?b_selectfld=link`setid=SC01";
		var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
		var retvo= window.showModalDialog(iframe_url, null, 
			        "dialogWidth:430px; dialogHeight:530px;resizable:no;center:yes;scroll:yes;status:no");			
	  	 if(retvo==null) 	return ;		        
		if(retvo.success=="1")	{
			var hashvo=new ParameterSet();
	        hashvo.setValue("tab_id",tabid);
	        hashvo.setValue("flag",flag);
			hashvo.setValue("stritemids",retvo.stritemids);
			var request=new Request({asynchronous:false,onSuccess:download,functionId:'302001020204'},hashvo);	
		}
	
	}
	else {
		 var hashvo=new ParameterSet();
	     hashvo.setValue("tab_id",tabid);
	     hashvo.setValue("flag",flag);
	     var request=new Request({asynchronous:false,onSuccess:download,functionId:'302001020204'},hashvo);	
	}

}
function download(outparameters){
	var filename=outparameters.getValue("fileName");
	//20/3/18 xus vfs改造
	var win=open("/servlet/vfsservlet?fileid="+filename+"&fromjavafolder=true","excel");
}
function imports(tab_id){
	budgetingForm.action="/gz/gz_budget/budgeting/budgeting_table.do?br_selectfile=open&tab_id="+tab_id;
	budgetingForm.submit();
}
function addmen(){
    var target_url="/gz/gz_budget/budgeting/budgeting_table.do?b_add=link";
    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(target_url);
    var return_vo=window.showModalDialog(iframe_url,null,"dialogWidth:400px; dialogHeight:450px;resizable:no;center:yes;scroll:no;status:no"); //注意这种弹窗
   if(return_vo!=null){         
    	 var a=Math.random();    	     
	     budgetingForm.action="/gz/gz_budget/budgeting/budgeting_table.do?b_search=int&randomnum="+a+"&tab_id=2";
		 budgetingForm.target='_self';
		 budgetingForm.submit();
   }

}
function initperson(){
	 if(confirm("您确认要初始化名册数据吗？")){
		 var hashvo=new ParameterSet();
	     hashvo.setValue("flag","initperson");
	     var request=new Request({asynchronous:false,onSuccess:initpersonOK,functionId:'302001020206'},hashvo);	
	 }else{
		 return;
	 }

}

function initpersonOK(outparameters){
	var info = outparameters.getValue("info");
	var strerror = outparameters.getValue("error");
	if (info=="true"){
		alert("名册初始化成功");
    	 var a=Math.random();    	     
	     budgetingForm.action="/gz/gz_budget/budgeting/budgeting_table.do?b_search=int&randomnum="+a+"&tab_id=2";
		 budgetingForm.target='_self';
		 budgetingForm.submit();
	}
	else {
		alert("名册初始化失败:"+strerror);
	
	
	}
}

function querypeople()
{
	var thecodeurl =""; 
	var return_vo;	
    thecodeurl="/general/inform/search/generalsearch.do?b_query=link&type=7&a_code=UN&tablename=usr`&history=0&like=0";
    return_vo= window.showModalDialog(thecodeurl, "", 
              	"dialogWidth:700px; dialogHeight:410px;resizable:no;center:yes;scroll:no;status:no");
    if(return_vo!=null&&return_vo!='')
    {  
    	alert(return_vo);
		var hashvo=new ParameterSet();
		hashvo.setValue("strsel",dbname);
		hashvo.setValue("strwhere",getEncodeStr(return_vo));
		
  	}
  	else
  	{
  	  return ;
  	}
}
	