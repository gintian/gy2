function query()
{
	bonusForm.action='/gz/bonus/inform.do?b_query=link&a_code='+bonusForm.a_code.value;
	bonusForm.submit();
}  
function del(bonusSet,doStatusFld)
{
	var str='';
	var table = $("tableUsr" + bonusSet);
	var dataset = table.getDataset();
	var record = dataset.getFirstRecord();
	while (record) {
		if (record.getValue("select")) {
		     if(record.getValue(doStatusFld)=='2')
		     {
		        alert(GZ_BONUS_INFO6);
		        return;
		     }
			str += "," + record.getValue("a0100")+":"+record.getValue("i9999")+":"+record.getValue("dbase");
		}
		record = record.getNextRecord();
	}
	if (str == '') {
		alert(CHOISE_DELETE_NOT);
		return;
	}	
	bonusForm.paramStr.value=str.substring(1);
	bonusForm.action='/gz/bonus/inform.do?b_del=link&a_code='+bonusForm.a_code.value;
	bonusForm.submit();
}
function add()
{
	bonusForm.action='/gz/bonus/inform.do?b_add=link&a_code='+bonusForm.a_code.value;
	bonusForm.submit();
}
//输入数值型
	function IsDigit(obj) 
	{
		if((event.keyCode >= 46) && (event.keyCode <= 57) && event.keyCode!=47)
		{
			var values=obj.value;
			if((event.keyCode == 46) && (values.indexOf(".")!=-1))//有两个.
				return false;
			if((event.keyCode == 46) && (values.length==0))//首位是.
				return false;	
			return true;
		}
			return false;	
	}
	//输入整数
	function IsDigit2(obj) 
	{
		if((event.keyCode >47) && (event.keyCode <= 57))
			return true;
		else
			return false;	
	}
function isNumber(obj)
{
  		var checkOK = "-0123456789.";
 		var checkStr = obj.value;
  		var allValid = true;
  		var decPoints = 0;
  		var allNum = "";
  		if (checkStr=="")
  			return;
  		var count = 0;
  		var theIndex = 0;
  		for (i = 0;  i < checkStr.length;  i++)
	    {
    		ch = checkStr.charAt(i);
    		if(ch=='-')
    		{
    			count=count+1;
    			theIndex=i+1;
    		}
    		for (j = 0;  j < checkOK.length;  j++)
     	    if (ch == checkOK.charAt(j))
       			 break;
    		if (j == checkOK.length)
   		    {
  			   allValid = false;
   			   break;
  		    }
    		if (ch == ".")
    		{
     			 allNum += ".";
     			 decPoints++;
  			 }
    	  else if (ch != ",")
      		allNum += ch;
  		}
  	if(count>1 || (count==1 && theIndex>1))
  			allValid=false;
  	if (decPoints > 1 || !allValid) 
  	{
  		alert('请输入数值类型的值！');
  		obj.value='';  
  		obj.focus();
  	}  	   
} 
function showGH(outparamters)
{
		var objlist=outparamters.getValue("objlist");

		if(objlist!=null)
		  AjaxBind.bind($('a0101_box'),objlist);		
}
//查询工号
function queryGH(type)
{	
   	 var gh=$F('gh');
   	 if(type==1)
   	 	gh=$F('a0101');
   	 	
   	 var hashvo=new ParameterSet();	
     hashvo.setValue("jobnumVal",getEncodeStr(gh));		
     hashvo.setValue("type",type);	
     var request=new Request({asynchronous:false,onSuccess:showGH,functionId:'3020130039'},hashvo); 
}
function showSelectBox(srcobj)
{
      Element.show('a0101_pnl');   
      var pos=getAbsPosition(srcobj);
	  with($('a0101_pnl'))
	  {
	        style.position="absolute";
    		style.posLeft=pos[0]-1;
 		    style.posTop=pos[1]-1+srcobj.offsetHeight;
 		    style.width=srcobj.offsetWidth+10;
      }                 
}
function setSelectValue()
{
       var objid,objtext,i;
       var obj=$('a0101_box');
   	   for(i=0;i<obj.options.length;i++)
       {
          if(obj.options[i].selected)
          {
          	 objid=obj.options[i].value
          }           
       }  
       if(objid)
       {
 			var objids=objid.split(":");
	        document.getElementById("a0100").value=objids[0];  	 
	        document.getElementById("b0110").value=objids[1]; 
	        document.getElementById("e0122").value=objids[2]; 
	        document.getElementById("a0101").value=objids[3]; 
	        document.getElementById("gh").value=objids[4]; 
       }
       Element.hide('a0101_pnl');         
}
function saveAdd(oper)
{
	var a0100 =  document.getElementById("a0100").value;
	if(a0100=='')
	{
		alert('请输入姓名！');
		return;
	}
	if(oper=='saveClose')
		bonusForm.action="/gz/bonus/inform.do?b_save=link&a0100="+a0100+"&a_code="+bonusForm.a_code.value;
	else
		bonusForm.action="/gz/bonus/inform.do?b_saveContinue=link&a0100="+a0100+"&a_code="+bonusForm.a_code.value;
	bonusForm.submit();	
}
//刷新主页面
function freshMain()
{
	bonusForm.action='/gz/bonus/inform.do?b_query=link&a_code='+bonusForm.a_code.value;
	bonusForm.submit();
}
//更新处理状态
function updateStatus(astatus,doStatusFld,bonusSet)
{
	var str='';
	var table = $("tableUsr" + bonusSet);
	var dataset = table.getDataset();	
	var record = dataset.getFirstRecord();

	while (record) {
		if (record.getValue("select")) {
		     if(astatus=='1' && record.getValue(doStatusFld)!='0')
		     {
		        alert(GZ_BONUS_INFO7);
		        return;
		     }
		     if(astatus=='0' && record.getValue(doStatusFld)!='1')
		     {
		        alert(GZ_BONUS_INFO10);
		        return;
		     }
			str += "," + record.getValue("a0100")+":"+record.getValue("i9999")+":"+record.getValue("dbase")+":"+astatus;
		}
		record = record.getNextRecord();
	}
	var noselinfo = '';
	if(astatus=='1')
		noselinfo=GZ_BONUS_INFO8;
	else if(astatus=='0')
		noselinfo=GZ_BONUS_INFO9;
	if (str == '') 
	{		
		alert(noselinfo);
		return;
	}	
	bonusForm.paramStr.value=str.substring(1);
	bonusForm.action='/gz/bonus/inform.do?b_updateStatus=link&a_code='+bonusForm.a_code.value;
	bonusForm.submit();
}
function querycondition(bonusSet)
{
	 var thecodeurl="/general/inform/search/gmsearch.do?b_query=link&type=1&oper=bonus&ps_flag=2&a_code=all&tablename=usr&fieldsetid="+bonusSet;
     return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:700px; dialogHeight:410px;resizable:no;center:yes;scroll:no;status:no");
     if(return_vo)
     {
     	bonusForm.expr.value=return_vo.expr;
     	bonusForm.factor.value=return_vo.factor;
		bonusForm.action='/gz/bonus/inform.do?b_query=link&a_code=';
		bonusForm.submit();
	 }
}
function bathUpdate()
{
	var theurl="/gz/bonus/inform.do?b_batchUpdate=link";
    var iframe_url="/general/query/common/iframe_query.jsp?src="+theurl;
    var return_vo= window.showModalDialog(iframe_url, 'win', 
      				"dialogWidth:400px; dialogHeight:250px;resizable:no;center:yes;scroll:yes;status:no");
	if(return_vo==null)
      	return;
    if(return_vo.flag=='true')
    {
    	bonusForm.action='/gz/bonus/inform.do?b_query=link&a_code=';
		bonusForm.submit();
    }
}
function checkType(){
	var itemid = document.getElementById("itemid").value;
	var arr = itemid.split(":");
	if(arr.length!=3){
		return;	
	}
	switch(arr[1]){
		 case 'N' : 
             viewToggle('addvaluename');
			 viewToggle('reducevaluename');
			 viewToggle('reference');
			 viewHide('addvaluetext');
			 viewToggle('repvaluetext');
			 viewHide('reducevlauetext');
			 document.getElementById("refvalue").disabled =true;
             break ; 
         case 'D' : 
         	viewHide('addvaluename');
			viewHide('reducevaluename');
			viewToggle('reference');
			viewHide('addvaluetext');
			viewToggle('repvaluetext');
			viewHide('reducevlauetext');
			document.getElementById("refvalue").disabled =true;
			break ;
		case 'A' : 
         	if(arr[2]!=0){         	
				viewHide('addvaluename');
				viewHide('reducevaluename');
				viewToggle('reference');
				document.getElementById("refvalue").disabled =true;
			}else{
				viewHide('addvaluename');
				viewHide('reducevaluename');
				viewToggle('reference');
				document.getElementById("refvalue").disabled =true;
			}
			viewHide('addvaluetext');			
			viewToggle('repvaluetext');			
			viewHide('reducevlauetext');
			break ;
		default:
			viewHide('addvaluename');
			viewHide('reducevaluename');
			viewHide('reference');
			document.getElementById("refvalue").disabled =true;
	}
}
function checkView(rediovalue){
	switch(rediovalue){
		 case 1 : 
             viewToggle('addvaluetext');
			 viewHide('repvaluetext');
			 viewHide('reducevlauetext');
			 viewHide('repvalueNtext');
			 document.getElementById("refvalue").disabled =true;
             break ; 
         case 2 : 
         	var codesetid = document.getElementById("codesetid").value;
			var itemtype = document.getElementById("itemtype").value;
		
         	viewHide('addvaluetext');
         	if(itemtype=='D'){
				viewToggle('repvaluetime');
			}else if(itemtype=='N'){
				viewToggle('repvalueNtext');
				viewHide('reducevlauetext');
				viewHide('repvaluetext');
			}else{
				if(codesetid!=null&&codesetid.length>0){
					viewToggle('repvaluecode');
					//if("UM,UN,@K".indexOf(codesetid)==-1)
						//document.getElementById("b").style.display='none';
				//	else
					//	document.getElementById("a").style.display='none';
					viewHide('reducevlauetext');
					viewHide('repvaluetext');
				}else{
					viewToggle('repvaluetext');
					viewHide('reducevlauetext');
				}
			}
			
			document.getElementById("refvalue").disabled =true;
			break ;
		case 3 : 
         	viewHide('addvaluetext');
			viewHide('repvaluetext');
			viewToggle('reducevlauetext');
			viewHide('repvalueNtext');
			document.getElementById("refvalue").disabled =true;
			break ;
		case 4 : 
			document.getElementById("refvalue").disabled =false;
			break ;
		default:
			viewHide('repvalueNtext');
			viewHide('addvaluetext');
			viewToggle('repvaluetext');
			viewHide('reducevlauetext');
			document.getElementById("refvalue").disabled =true;
	}
}
function viewToggle(targetId){
	if (document.getElementById(targetId)){
		target = document.getElementById(targetId);
		target.style.display = "block";
	}
} 
function viewHide(targetId){
	if (document.getElementById(targetId)){
		target = document.getElementById(targetId);
		target.style.display = "none";
	}
}
function checkTime(times){
 	var result=times.match(/^(\d{1,4})(-|\/)(\d{1,2})\2(\d{1,2})$/);
 	if(result==null) return false;
 	var d= new Date(result[1], result[3]-1, result[4]);
 	return (d.getFullYear()==result[1]&&(d.getMonth()+1)==result[3]&&d.getDate());
}
function timeCheck(obj){
	if(!checkTime(obj.value)){
		obj.value='';
	}
}
function isNum(i_value){
    re=new RegExp("[^0-9]");
    var s;
    if(s=i_value.match(re)){
        return false;
     }
    return true;
}

function checkNuN(obj){
 	if(!isNum(obj.value)){
 		obj.value='';
 	}
}
function checkAterRadio(valuebutton){
	var buttonvalue="";
	var tablevos=document.getElementsByTagName("input");
	for(var i=0;i<tablevos.length;i++){
		if(tablevos[i].type=="radio"){
			if(tablevos[i].checked){
				if(tablevos[i].name==valuebutton)
					buttonvalue = tablevos[i].value;
			}
		}
    }
	return buttonvalue;
}
function updateData(){
	var radiobutton = checkAterRadio("valuebutton");
	var updatevalue="";
	if(radiobutton==1){
		updatevalue=document.getElementById("addvalue").value;
	}else if(radiobutton==2){
		var codesetid = document.getElementById("codesetid").value;
		var itemtype = document.getElementById("itemtype").value;
		if(itemtype=='D'){
			updatevalue=document.getElementById("time.value").value;
		}else if(itemtype=='N'){
			updatevalue=document.getElementById("repvaluen").value;
		}else{
			if(codesetid!=null&&codesetid.length>0){
				updatevalue=document.getElementById("codeid.value").value;
			}else{
				updatevalue=document.getElementById("repvalue").value;
			}
		}
	}else if(radiobutton==0){
		updatevalue=document.getElementById("reducevlaue").value;
	}else if(radiobutton==3){
		updatevalue=document.getElementById("refvalue").value;
	}
	var itemid = document.getElementById("itemid").value;
	if(itemid==null||itemid==undefined||itemid.length<1){
		alert("请选择您要修改的指标!");
		return false;
	}
	var hashvo=new ParameterSet();
	hashvo.setValue("updatevalue",updatevalue);	
	hashvo.setValue("itemid",itemid);	
	hashvo.setValue("sql",getEncodeStr(bonusForm.sql.value));	
	hashvo.setValue("doStatusFld",getEncodeStr(bonusForm.doStatusFld.value));	
	hashvo.setValue("flagcheck",radiobutton);	
	var request=new Request({method:'post',asynchronous:false,onSuccess:indCheck,functionId:'3020130044'},hashvo);
}
function indCheck(outparamters){
	var check = outparamters.getValue("check");
	if(check=='ok'){
		var thevo=new Object();
	thevo.flag="true";
	window.returnValue=thevo;
	window.close();
	}
}
function outhmuster()
{
	var hashvo=new ParameterSet();
	hashvo.setValue("sql",getEncodeStr(bonusForm.sql.value));	
	var request=new Request({asynchronous:false,functionId:'3020130045'},hashvo);  
	var thecodeurl ="/general/muster/hmuster/searchHroster.do?b_search=link`nFlag=3`a_inforkind=1`res=0`result=0`dbpre=Usr";
	var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl;
 		var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl;
    		var return_vo= window.showModalDialog(iframe_url,"", 
              	"dialogWidth:1000px; dialogHeight:800px;resizable:yes;center:yes;scroll:yes;status:no");
}
function exportExcel(bonusSet,jobnumFld)
{
	var hashvo=new ParameterSet();	
	hashvo.setValue("sql",getEncodeStr(bonusForm.sql.value));	
	hashvo.setValue("bonusSet",bonusSet);
	hashvo.setValue("jobnumFld",jobnumFld);
	var request=new Request({method:'post',asynchronous:false,onSuccess:showfile1,functionId:'3020130046'},hashvo);
}
function showfile1(outparamters)
{
	var outName=outparamters.getValue("outName");
	outName=getDecodeStr(outName);
	window.location.target="_blank";
	window.location.href = "/servlet/vfsservlet?fileid="+outName+"&fromjavafolder=true";
}
function downLoadTemp(bonusSet,jobnumFld)
{
	var hashvo=new ParameterSet();	
	hashvo.setValue("bonusSet",bonusSet);
	hashvo.setValue("jobnumFld",jobnumFld);
	var request=new Request({method:'post',asynchronous:false,onSuccess:showfile1,functionId:'3020130047'},hashvo);
}
function importTable()
{
	bonusForm.action='/gz/bonus/inform.do?br_import=link';
	bonusForm.submit();
}
function exportSumData(bonusSet)
{
	if(bonusForm.businessDate.value=='all')
	{
		alert('请选择一个业务日期');
		return;
	}
	var hashvo=new ParameterSet();	
	hashvo.setValue("bonusSet",bonusSet);
	hashvo.setValue("businessDate",bonusForm.businessDate.value);
	hashvo.setValue("sql",getEncodeStr(bonusForm.sql.value));	
	var request=new Request({method:'post',asynchronous:false,onSuccess:showfile1,functionId:'3020130049'},hashvo);
}
