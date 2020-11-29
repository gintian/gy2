function computeFormula()
{
	var thecodeurl="/gz/premium/premium_allocate/monthPremiumList.do?b_formula=link`year="+monthPremiumForm.year.value+"`month="+monthPremiumForm.month.value; 
	var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl;
	var retvo= window.showModalDialog(iframe_url, null, 
		        "dialogWidth:460px; dialogHeight:550px;resizable:no;center:yes;scroll:yes;status:no");	
	
	if(retvo!=null&&retvo=='1')
	{
		var url=document.location.href;
		document.location=url;
	}
}



function select_All(obj)
{
	var objs=document.getElementsByName("chk");
	for(var i=0;i<objs.length;i++)
	{
		objs[i].checked=obj.checked;
	}
}
function batchcompute(year,month,operateUnitCode)
{
	var itemids=new Array();
	var temps=document.getElementsByName("chk");
	for(var i=0;i<temps.length;i++)
	{
		if(temps[i].checked)
		{
			itemids[itemids.length]=temps[i].value;
		}
	}
	if(itemids.length==0)
	{
		alert(REPORT_INFO28);
		return;
	}
	var waitInfo=eval("wait");			
	waitInfo.style.display="block";
	
	document.getElementsByName("compute")[0].disabled=true;
	var hashvo=new ParameterSet();
	
	hashvo.setValue("itemids",itemids);
	hashvo.setValue("year",year);
	hashvo.setValue("month",month);		
	hashvo.setValue("operateUnitCode",operateUnitCode);
   	var request=new Request({method:'post',asynchronous:true,onSuccess:computeIsOk,functionId:'3020132012'},hashvo);
}

function computeIsOk(outparamters)
{
	var waitInfo=eval("wait");			
	waitInfo.style.display="none";
	var retvo="1";
    window.returnValue=retvo;
	window.close();
}

