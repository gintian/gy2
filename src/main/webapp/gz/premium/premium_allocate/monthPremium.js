function search_data(orgcode)
{
	monthPremiumForm.action="/gz/premium/premium_allocate/monthPremiumList.do?b_query=link&orgcode="+orgcode+"&theYear="+monthPremiumForm.year.value+"&theMonth="+monthPremiumForm.month.value;
	monthPremiumForm.submit();
}
function search_data_y(theObj)
{
	monthPremiumForm.action="/gz/premium/premium_allocate/monthPremiumList.do?b_query=link&orgcode="+monthPremiumForm.operOrg.value+"&theYear="+theObj.value+"&theMonth="+monthPremiumForm.month.value;
	monthPremiumForm.submit();
}
function search_data_m(theObj)
{
	monthPremiumForm.action="/gz/premium/premium_allocate/monthPremiumList.do?b_query=link&orgcode="+monthPremiumForm.operOrg.value+"&theYear="+monthPremiumForm.year.value+"&theMonth="+theObj.value;
	monthPremiumForm.submit();
}
function del(tablename)
{
	var str = "";
	var table = $("table" + tablename);
	var dataset = table.getDataset();
	var record = dataset.getFirstRecord();
	while (record) {
		if (record.getValue("select") && record.getValue("b0110")!='sum') {
			str += "," + record.getValue("b0110")+':'+record.getValue("i9999");
		}
		record = record.getNextRecord();
	}
	if (str == "") {
		alert(SEL_RECORDS_DEL);
		return;
	}
	
	monthPremiumForm.paramStr.value=str.substring(1);
	updateFLag('del');
    
}
function updateFLag(oper)
{
	var paramStr = "&orgcode="+monthPremiumForm.operOrg.value+"&theYear="+monthPremiumForm.year.value+"&theMonth="+monthPremiumForm.month.value;
	paramStr+="&oper="+oper;
	var info='';
	if(oper=='keepSave')	
		info=IS_KEEP_SAVE;
	else if(oper=='distribute')
	    info=IS_DISTRI_OPER;
	else if(oper=='appeal')
		 info=IS_REPORT_OPER;
	else if(oper=='del')
		 info=KHSS_JHSS_YSEDETA; 
	if(confirm(info))
	{	
		monthPremiumForm.action="/gz/premium/premium_allocate/monthPremiumList.do?b_update=link"+paramStr;
		monthPremiumForm.submit();
	}
}
function add(isGzManager,salaryid)
{
	if(isGzManager=='0' && salaryid!='nodefine')
	{
		alert(MONTH_PREMIUM_INFO3);
		return;
	}
	
	var thecodeurl="/gz/premium/premium_allocate/monthPremiumList.do?b_add=link"; 
	var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl;
	var retvo= window.showModalDialog(iframe_url, null, 
		        "dialogWidth:460px; dialogHeight:230px;resizable:no;center:yes;scroll:yes;status:no");			
  	 if(retvo==null)
  	 	return ;		      
	if(retvo.success=="1")
	{
		var theYear = retvo.year;
		var theMonth = retvo.month;
		var paramStr = "&orgcode="+monthPremiumForm.operOrg.value+"&theYear="+theYear+"&theMonth="+theMonth;
		monthPremiumForm.action="/gz/premium/premium_allocate/monthPremiumList.do?b_query=link"+paramStr;
		monthPremiumForm.submit();	
	}
}
function add2()
{
	var thecodeurl="/gz/premium/premium_allocate/monthPremiumList.do?b_add2=link"; 
	var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl;
	var retvo= window.showModalDialog(iframe_url, null, 
		        "dialogWidth:420px; dialogHeight:360px;resizable:no;center:yes;scroll:yes;status:no");			
  	 if(retvo==null)
  	 	return ;		      
	if(retvo.success=="1")
	{
		var paramStr = "&orgcode="+monthPremiumForm.operOrg.value+"&theYear="+monthPremiumForm.year.value+"&theMonth="+monthPremiumForm.month.value;
		monthPremiumForm.action="/gz/premium/premium_allocate/monthPremiumList.do?b_query=link"+paramStr;
		monthPremiumForm.submit();	
	}
}
function isDigit(s)   
{   
		var patrn=/^[0-9]{1,20}$/;   
		if (!patrn.exec(s)) return false  
			return true  
}  

function generateData(they,themon,operOrg,setid,salaryid)
{	
	var year=they.value;
	var month=themon.value;
	
	if(!isDigit(year)||!isDigit(month))
	{
		alert("日期格式不正确！");
		return;
	}
	if(year*1<=1900||year*1>=2100)
	{
		alert("年要大于1900,小于2100!");
		return;
	}
	if(month*1<1||month*1>12)
	{
		alert("月要大于等于1,小于等于12!");
		return;
	}
	
	var waitInfo=eval("wait");
	waitInfo.style.display="block";

	var hashvo=new ParameterSet();
	hashvo.setValue("year",year);
	hashvo.setValue("month",month);	
	hashvo.setValue("orgsubset",setid);
	hashvo.setValue("operOrg",operOrg);
	hashvo.setValue("salaryid",salaryid);
	hashvo.setValue("from_module",'monthPremium');
	var request;
	if(salaryid=='nodefine')
	{
		if(confirm(MONTH_PREMIUM_INFO1))
			 request=new Request({method:'post',asynchronous:true,onSuccess:generateDataOk,functionId:'3020132010'},hashvo);
		else
			window.close();
	}   		
   	else
   		 request=new Request({method:'post',asynchronous:true,onSuccess:generateDataOk,functionId:'3020132004'},hashvo);
	
}
function generateDataOk(outparamters)
{
	var waitInfo=eval("wait");			
	waitInfo.style.display="none";
	var retvo=new Object();	
	retvo.success="1";
	var month=outparamters.getValue("month");
	var year=outparamters.getValue("year");	
	retvo.year=year;
	retvo.month=month;
    window.returnValue=retvo;
	window.close();
}
function queryDetail()
{
    var par="top=0, left=0,height="+(window.screen.height-50)+",width="+(window.screen.width-10);
	var thecodeurl="/gz/premium/premium_allocate/monthPremiumList.do?b_detail=link"; 
	window.open(thecodeurl,'',par);
//	var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl;
//	var retvo= window.showModalDialog(iframe_url, null, 
//		        "dialogWidth:900px; dialogHeight:650px;resizable:no;center:yes;scroll:yes;status:yes");		
}
function downloadTemplate()
{
	var hashvo=new ParameterSet();
	hashvo.setValue("theYear",monthPremiumForm.year.value);
	hashvo.setValue("theMonth",monthPremiumForm.month.value);
	var request=new Request({method:'post',asynchronous:false,onSuccess:showfile,functionId:'3020132006'},hashvo);
}
function showfile(outparamters)
{
	var outName=outparamters.getValue("outName");
	var win=open("/servlet/vfsservlet?fileid="+outName+"&fromjavafolder=true","excel");	
}
function importExcel()
{
	var target_url="/gz/premium/premium_allocate/monthPremiumList.do?br_import=link";
 	var iframe_url="/general/query/common/iframe_query.jsp?src="+target_url;
	var return_vo= window.showModalDialog(iframe_url, "importExcel", 
	              "dialogWidth:450px; dialogHeight:210px;resizable:no;center:yes;scroll:no;status:no");	
	if(!return_vo)
		return;	   
	if(return_vo.flag=="true")
	{	
		alert(MONTH_PREMIUM_INFO4);
		monthPremiumForm.action="/gz/premium/premium_allocate/monthPremiumList.do?b_query=link&orgcode="+monthPremiumForm.operOrg.value+"&theYear="+monthPremiumForm.year.value+"&theMonth="+monthPremiumForm.month.value;
		monthPremiumForm.submit();
	}else
		alert(MONTH_PREMIUM_INFO5);		
}
function generateData2(they,themon,operOrg,setid)
{
	var objs = document.getElementsByName('b0110');
	var i=0;
	var b0110s = '';
	while(i<objs.length)
	{
		if(objs[i].checked==true)			
				b0110s+=','+objs[i].value;		
		i++;
	}
	if(b0110s=='')
	{
		alert(MONTH_PREMIUM_INFO2);
		return;
	}

	var hashvo=new ParameterSet();
	hashvo.setValue("year",they);
	hashvo.setValue("month",themon);	
	hashvo.setValue("orgsubset",setid);
	hashvo.setValue("b0110s",b0110s.substring(1));
   	var request=new Request({method:'post',asynchronous:true,onSuccess:generateDataOk2,functionId:'3020132009'},hashvo);	
}
function generateDataOk2(outparamters)
{
	var ok=outparamters.getValue("ok");
	var retvo=new Object();	
	retvo.success=ok;
    window.returnValue=retvo;
	window.close();
}