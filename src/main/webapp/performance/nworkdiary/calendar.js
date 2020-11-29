//日报-----点击日历的某一天时的事件
function selectNewDate(year,month,day)
{
	if(year==-1)
	{
		year="";
		month="";
		day="";
	}
	var obj = document.getElementById("recordlist");
	var scrollValue=obj.scrollTop;
	document.calendarDayForm.scrollValue.value=scrollValue;
	document.calendarDayForm.p01_key.value="";
	document.calendarDayForm.recordNum.value="";
	document.calendarDayForm.action="/performance/nworkdiary/myworkdiary/daywork.do?b_init=link&year="+year+"&month="+month+"&day="+day;
	document.calendarDayForm.submit();
}

//点击变为蓝色   待加
var curObjTr=null;
var tmp=null;
var index=0;
function tr_click(id)
{
	if(index==0)
		curObjTr=document.getElementById(tmp);
	index++;
	var objTr = document.getElementById(id);
	if(curObjTr!=null)
		curObjTr.style.backgroundColor="";
	curObjTr=objTr;
	curObjTr.style.backgroundColor='C5E3B1';
}

//日报-----点击记录列表中某一条具体的记录时触发的事件
function showRecord(year,month,day,p0100,record_num)
{
	var obj = document.getElementById("recordlist");
	var scrollValue=obj.scrollTop;
	document.calendarDayForm.scrollValue.value=scrollValue;
	document.calendarDayForm.p01_key.value=p0100;
	document.calendarDayForm.recordNum.value=record_num;
	var combineid=p0100+""+record_num;
	document.calendarDayForm.action="/performance/nworkdiary/myworkdiary/daywork.do?b_init=link&year="+year+"&month="+month+"&day="+day+"&combineid="+combineid;
	document.calendarDayForm.submit();
}
//双击周报的某个日期时，返回到日报
function selectNewDate2(year,month,day)
{
	window.location.href="/performance/nworkdiary/myworkdiary/daywork.do?b_init=link&year="+year+"&month="+month+"&day="+day+"&frompage=1";
}
//由日报返回到周报、月报、年报
function returnOriginal(year,month,day,frompage)
{
	if(frompage==1)//周报
	{
		window.location.href="/performance/nworkdiary/myworkdiary/weekwork.do?b_init=link&year="+year+"&month="+month+"&day="+day;
	}
	else if(frompage==2)//月报
	{
		window.location.href="/performance/nworkdiary/myworkdiary/monthwork.do?b_search=link&init=3&currentYear="+year+"&currentMonth="+month+"&currentDay="+day;
	}
	else if(frompage==3)//年报
	{
		window.location.href="/performance/nworkdiary/myworkdiary/yearwork.do?b_search=link&init=2&currentYear="+year;
	}
}
//周报-----点击‘今天’或左右按钮时，切换周报
function changeWeekRecord(year,month,day)
{
	if(year==-1)
	{
		year="";
		month="";
		day="";
	}
	document.calendarWeekForm.action="/performance/nworkdiary/myworkdiary/weekwork.do?b_init=link&year="+year+"&month="+month+"&day="+day;
	document.calendarWeekForm.submit();
}
//返回到员工日志
function returnStaffDiary(staff_url)
{
	window.location.href=staff_url;
}



