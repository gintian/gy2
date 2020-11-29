var _checkBrowser=true;
var _disableSystemContextMenu=false;
var _processEnterAsTab=true;
var _showDialogOnLoadingData=true;
var _enableClientDebug=true;
var _theme_root="/ajax/images";
var _application_root="";
var __viewInstanceId="968";
var ViewProperties=new ParameterSet();

  

  function deletes()
  {
     var len=document.proposeForm.elements.length;
     var isCorrect=false;
     for (i=0;i<len;i++)
     {
           if (document.proposeForm.elements[i].type=="checkbox")
            { 
              if( document.proposeForm.elements[i].checked==true && "selbox" != document.proposeForm.elements[i].name )
                isCorrect=true;
            }
     }
    if(!isCorrect)
    {
          alert("请选择记录！");
          return false;
     }
     if(confirm("确认要删除该记录？"))
     {
          proposeForm.action = "/selfservice/propose/searchpropose.do?b_delete=link";
          proposeForm.submit();
     }
  }

  function outExcel(){
	  var hashvo=new ParameterSet();
		hashvo.setValue("start_date",$F('start_date'));
		hashvo.setValue("end_date",$F('end_date'));
		hashvo.setValue("date_flag", $F('date_flag'))
		var request=new Request({method:'post',asynchronous:false,onSuccess:showExcel,functionId:'1105040006'},hashvo);
  }

  function showExcel(parameters){
	  	var filename=parameters.getValue("filename");
	  	//20/3/5 xus vfs改造
	  	var win=open("/servlet/vfsservlet?fromjavafolder=true&fileid="+filename);
  }

  function checkDate(sdate1,edate2)
  {
  if(sdate1!=""&&edate2!="")
  { //输入不为空时；
  // 对字符串进行处理
  // 以 – / 或 空格 为分隔符, 将日期字符串分割为数组
  var date1 = sdate1.split("-");
  var date2 = edate2.split("-");
  // 创建 Date 对象
  var myDate1 = new Date(date1[0],date1[1],date1[2]);
  var myDate2 = new Date(date2[0],date2[1],date2[2]);

  // 对日起进行比较
  if (myDate1 <= myDate2)
  {
  return true;
  }else
  {
  alert ("提示:  开始时间不能大于结束时间！");
  return false;
  }
  }
  else
  {
  return true;
  }
  }
  /**
  判断日期格式 2000-01-01
  strDate：检测的日期格式
  return： true/false
  **/
  function   isDate(strDate){
  var   strSeparator = "-";   //日期分隔符
  var   strDateArray;
  var   intYear;
  var   intMonth;
  var   intDay;
  var   boolLeapYear;
  //var strDate=form1.a.value   //表单中的日期值
  strDateArray = strDate.split(strSeparator);

  if(strDateArray.length!=3)    {   alert('提示: 日期格式错误!'); return   false;   }

  intYear  =  parseInt(strDateArray[0],10);
  intMonth  =  parseInt(strDateArray[1],10);
  intDay   =   parseInt(strDateArray[2],10);

  if(isNaN(intYear)||isNaN(intMonth)||isNaN(intDay))   { alert('提示: 日期格式错误!'); return   false; }

  if(intMonth>12||intMonth<1) {   alert('提示: 日期格式错误!'); return   false;   }

  if((intMonth==1||intMonth==3||intMonth==5||intMonth==7||intMonth==8||intMonth==10||intMonth==12)&&(intDay>31||intDay<1))   {   alert('提示: 日期格式错误!'); return   false;   }

  if((intMonth==4||intMonth==6||intMonth==9||intMonth==11)&&(intDay>30||intDay<1))   {   alert('提示: 日期格式错误!'); return   false;   }

  if(intMonth==2){
  if(intDay<1)   {   alert('提示: 日期格式错误!'); return   false;   }

  boolLeapYear   =   false;
  if((intYear%4==0 && intYear %100!=0)||(intYear %400==0))
  {
  boolLeapYear=true;
  }

  if(boolLeapYear){
  if(intDay>29) {   alert('提示: 日期格式错误!'); return   false;   }
  }
  else{
  if(intDay>28)  {   alert('提示: 日期格式错误!'); return   false;   }
  }
  }

  return   true;
  }