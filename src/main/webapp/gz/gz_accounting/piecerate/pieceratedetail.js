 function getCurrentColumn(setname)
 {
    var table="table"+setname;
    table=$(table);
    var dataset=table.getDataset();
    var temp=table.getActiveCell(); 
    if(temp==null)  return;
    var    recidx=dataset.getValue("i9999");

 }   
 
  function deleteproducts(setname)
 {
	  var tablename="table"+setname;
	  table=$(tablename);
	  dataset=table.getDataset();
	  var record=dataset.getFirstRecord();
	  var selectID="";	
	  var num=0;	
	  var noNum=0;
	  while (record) 
	  {
		if (record.getValue("select"))
		{			
			num++;
			selectID=selectID+"/"+record.getValue("I9999");
					   
		}
		record=record.getNextRecord();
	  }  	
		
	  if(num==0)
	  {
		alert(NOTING_SELECT);
		return;
	  }
	  else 
	  {	
	    if (confirm(GZ_REPORT_CONFIRMDELETE)) {
			var hashvo=new ParameterSet();
			hashvo.setValue("s0100",pieceRateDetailForm.s0100.value);
			hashvo.setValue("strsel",selectID);
			hashvo.setValue("model","delproduct");
			var request=new Request({asynchronous:false,onSuccess:refreshForm,functionId:'3020091047'},hashvo);	
		}

	 
	 }

 }
   
 function deleteobjs(setname)
 {
	  var tablename="table"+setname;
	  table=$(tablename);
	  dataset=table.getDataset();
	  var record=dataset.getFirstRecord();
	  var selectID="";	
	  var num=0;	
	  var noNum=0;
	  while (record) 
	  {
		if (record.getValue("select"))
		{			
			num++;
			selectID=selectID+"/"+record.getValue("Nbase")+"`"+record.getValue("A0100")+"`"+record.getValue("I9999");
					   
		}
		record=record.getNextRecord();
	  }  	
		
	  if(num==0)
	  {
		alert(NOTING_SELECT);
		return;
	  }
	  else 
	  {	
	    if (confirm(GZ_REPORT_CONFIRMDELETE)) {
			var hashvo=new ParameterSet();
			hashvo.setValue("s0100",pieceRateDetailForm.s0100.value);
			hashvo.setValue("strsel",selectID);
			hashvo.setValue("model","delpeople");
			var request=new Request({asynchronous:false,onSuccess:refreshForm,functionId:'3020091047'},hashvo);	
		}

	 
	 }

 }
   
 function goback()
 {
  url="/gz/gz_accounting/piecerate/search_piecerate.do?b_query=back"
  if(url=="")
     return false;
  pieceRateDetailForm.action=url;
  pieceRateDetailForm.submit();
 }  

   
 function handSelectProduct()
  { 
	var right_fields="";	
	var s0100=pieceRateDetailForm.s0100.value;
	var infos=new Array();
	infos[0]=s0100;
	   var strurl="/gz/gz_accounting/piecerate/handselproduct.do?b_query=link";
	var iframe_url="/gz/gz_accounting/bankdisk/iframe_bank_disk.jsp?src="+strurl; 
	var objList=window.showModalDialog(iframe_url,infos,"dialogWidth=600px;dialogHeight=480px;resizable=yes;scroll=no;status=no;");  
	   if(objList==null)
		return false;	
	
	if(objList.length>0)
	{
	   	for(var i=0;i<objList.length;i++)
	   	{
	   		right_fields+="/"+objList[i];		   		
	   	}	
	
		var hashvo=new ParameterSet();
		hashvo.setValue("s0100",pieceRateDetailForm.s0100.value);
		hashvo.setValue("strsel",right_fields);
		hashvo.setValue("model","handselproduct");
		var request=new Request({asynchronous:false,onSuccess:refreshForm,functionId:'3020091047'},hashvo);	

	}		 
}

function refreshForm(outparamters)
{	var s0100=pieceRateDetailForm.s0100.value;
	pieceRateDetailForm.action="/gz/gz_accounting/piecerate/search_piecerate_detail.do?b_query=link&s0100="+s0100 ;
	pieceRateDetailForm.submit();

}

 function handSelectPeople()
  { 
	var right_fields="";	
	var s0100=pieceRateDetailForm.s0100.value;
	var infos=new Array();
	infos[0]=s0100;
	
	var strurl="/gz/gz_accounting/piecerate/handselpeople.do?b_query=link";
	var iframe_url="/gz/gz_accounting/bankdisk/iframe_bank_disk.jsp?src="+strurl; 
	var objList=window.showModalDialog(iframe_url,infos,"dialogWidth=600px;dialogHeight=480px;resizable=yes;scroll=no;status=no;");  
	   if(objList==null)
		return false;	
	
	if(objList.length>0)
	{
	   	for(var i=0;i<objList.length;i++)
	   	{
	   		right_fields+="/"+objList[i];		   		
	   	}	
		var hashvo=new ParameterSet();
		hashvo.setValue("s0100",pieceRateDetailForm.s0100.value);
		hashvo.setValue("strsel",right_fields);
		hashvo.setValue("model","handselpeople");
		var request=new Request({asynchronous:false,onSuccess:refreshForm,functionId:'3020091047'},hashvo);	
	}
	 
}
    
function conditionselect(dbname)
{
	var thecodeurl =""; 
	var return_vo;	
    thecodeurl="/general/inform/search/generalsearch.do?b_query=link&type=6&a_code=UN&tablename="+dbname;
    return_vo= window.showModalDialog(thecodeurl, "", 
              	"dialogWidth:700px; dialogHeight:410px;resizable:no;center:yes;scroll:no;status:no");
    if(return_vo!=null&&return_vo!='')
    {  
		var hashvo=new ParameterSet();
		hashvo.setValue("s0100",pieceRateDetailForm.s0100.value);
		hashvo.setValue("strsel",dbname);
		hashvo.setValue("strwhere",getEncodeStr(return_vo));
		hashvo.setValue("model","condselpeople");
		var request=new Request({asynchronous:false,onSuccess:refreshForm,functionId:'3020091047'},hashvo);	
  	}
  	else
  	{
  	  return ;
  	}
}


function setformula(){
	 var thecodeurl = "/gz/gz_accounting/piecerate/iframpiecerateformula.jsp?b_query=link&busiid="
	                   +pieceRateDetailForm.busiid.value;
	var newwindow = window.showModalDialog(thecodeurl, 
	                  "", "dialogWidth:800px; dialogHeight:525px;resizable:no;center:yes;scroll:yes;status:no"); // modify by xiaoyun 2014-9-1 计件薪资-计算公式-去掉滚动条

}

function setformula1(){
	 var theURL = "/gz/gz_accounting/piecerate/piecerate_tj_def.do?b_selectfld=link'mode=add";
     var iframe_url="/gz/gz_accounting/bankdisk/iframe_bank_disk.jsp?src="+theURL;   
	var objlist =window.showModalDialog(iframe_url,null,"dialogWidth=600px;dialogHeight=400px;resizable:no;center:yes;scroll:yes;status:no");  
	var obj=new Object();
	if(objlist==null)
	{
	   return;
	}else{
	 alert(objlist.defid);
	 alert(objlist.defname);
	 
	}

}

function calc(setname){
	    var s0100=pieceRateDetailForm.s0100.value;
	var thecodeurl="/gz/gz_accounting/piecerate/search_piecerate_formula.do?b_selformulalist=link`busiid="+pieceRateDetailForm.busiid.value
	+"`s0100="+s0100; 
	var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl;
	var retvo= window.showModalDialog(iframe_url, null, 
		        "dialogWidth:460px; dialogHeight:550px;resizable:no;center:yes;scroll:yes;status:no");			
  	 if(retvo==null)
  	 	return ;		        
	if(retvo.success=="1")
	{
		pieceRateDetailForm.action="/gz/gz_accounting/piecerate/search_piecerate_detail.do?b_query=link&s0100="+s0100 ;
		pieceRateDetailForm.submit();
	}

}
 