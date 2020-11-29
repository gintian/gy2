function checkContent(depart,times,year)
{//在任务维度中点击工作名称或工作内容查看详细任务
	var widthofscreen=document.body.clientWidth;
	document.solarTermsForm.action="/performance/solarterms/specialtask.do?b_search=link&showType=0&depart="+depart+"&times="+times+"&year="+year+"&month=&widthofscreen="+widthofscreen;
	document.solarTermsForm.submit();
}
function checkMonthContent(depart,year,month)
{//在时间维度中点击工作名称或工作内容查看详细任务
	var widthofscreen=document.body.clientWidth;
	document.solarTermsForm.action="/performance/solarterms/specialtask.do?b_search=link&showType=1&depart="+depart+"&times=&year="+year+"&month="+month+"&widthofscreen="+widthofscreen;
	document.solarTermsForm.submit();
}
function changeOption(flag)
{//改变下拉列表
	document.solarTermsForm.action="/performance/solarterms/solarterms.do?b_search=link&frompage=1&showType="+flag;
	document.solarTermsForm.submit();
}
