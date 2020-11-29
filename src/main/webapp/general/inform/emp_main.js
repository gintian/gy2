   function IsDigit3(obj) 
   {
	if((event.keyCode >= 46) && (event.keyCode <= 57)  && event.keyCode!=47){
		var values=obj.value;
		if((event.keyCode == 46) && (values.indexOf(".")!=-1))
			return false;
		if((event.keyCode == 46) && (values.length==0))
			return false;	
		
		if((values.lastIndexOf(".")<values.length)&&(values.indexOf(".")!=1))
			return true;
		else
			return false;
	}else{
		return false;
	}
   }
   function IsInputTimeValue() 
   {	     
       event.cancelBubble = true;
       var fObj=this.fObj;		
       if (!fObj) return;		
       var cmd = event.srcElement.innerText=="5"?true:false;
       if(fObj.value==""||fObj.value.lenght<=0)
	  fObj.value="0";
       var i = parseInt(fObj.value,10);		
       var radix=parseInt(time_r,10)-1;				
       if (i==radix&&cmd) {
           i = 0;
       } else if (i==0&&!cmd) {
	   i = radix;
       } else {
	   cmd?i++:i--;
       }	
       if(i==0)
       {
	  fObj.value = "00"
       }else if(i<10&&i>0)
       {
	  fObj.value="0"+i;
       }else{
	  fObj.value = i;
       }			
       fObj.select();
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
	function changepos(pretype,sourceobj)
    {    	
      var dobjun=$('b0110_value') ;     
      var dobjum=$('e0122_value') ;  
      var dobjpk=$('e01a1_value') ;   
      var hashvo=new ParameterSet();
      hashvo.setValue("pretype",pretype);
      if(dobjun!=null&&dobjun!="undefined")
       hashvo.setValue("orgparentcodestart",dobjun.value);
      else
       hashvo.setValue("orgparentcodestart","");
      if(dobjum!=null&&dobjum!="undefined")
       hashvo.setValue("deptparentcodestart",dobjum.value);
      else
       hashvo.setValue("deptparentcodestart","");
 	  if(dobjpk!=null&&dobjpk!="undefined")
       hashvo.setValue("posparentcodestart",dobjpk.value);
      else
       hashvo.setValue("posparentcodestart","");
      if(pretype=="UN")   
      {        
        var request=new Request({method:'post',onSuccess:getchangeposun,functionId:'2020030064'},hashvo);//02010001012
      }
     if(pretype=="UM")
      {
        var request=new Request({method:'post',onSuccess:getchangeposum,functionId:'2020030064'},hashvo);
      }
     if(pretype=="@k")
      {
        var request=new Request({method:'post',onSuccess:getchangeposkk,functionId:'2020030064'},hashvo);
      }
    }
    function getchangeposun(outparamters)
    {
      var pretype=outparamters.getValue("pretype");
      var orgparentcode=outparamters.getValue("orgparentcode");
      var deptparentcode=outparamters.getValue("deptparentcode");
	  var posparentcode=outparamters.getValue("posparentcode");		
      AjaxBind.bind(mInformForm.orgparentcode,orgparentcode);
      AjaxBind.bind(mInformForm.deptparentcode,deptparentcode);
      AjaxBind.bind(mInformForm.posparentcode,posparentcode);    
            
      var dobjun=$('e0122') ; 
      if(dobjun!=null)
        dobjun.value="";
  
      dobjun=$('e0122_value') ;
      if(dobjun!=null)
        dobjun.value="";    

	  dobjun=$('e01a1') ; 
      if(dobjun!=null)
        dobjun.value="";
  
      dobjun=$('e01a1_value') ;
      if(dobjun!=null)
        dobjun.value="";   
              
  }
   function getchangeposum(outparamters)
   {
      var pretype=outparamters.getValue("pretype");
      var orgparentcode=outparamters.getValue("orgparentcode");
      var orgvalue=outparamters.getValue("orgvalue");
      if(orgvalue!=null && orgvalue.length>0)
      {
         var orgvalueview=outparamters.getValue("orgviewvalue");
         var dobjun=$('b0110') ;        
         dobjun.value=orgvalueview;
          var dobjun=$('b0110_value') ;        
         dobjun.value=orgvalue;   
      }
      var dobjpk=$('e01a1'); 
      if(dobjpk!=null)
        dobjpk.value="";  
  
      dobjpk=$('e01a1_value');
      if(dobjpk!=null)
        dobjpk.value="";       
            
      var deptparentcode=outparamters.getValue("deptparentcode");
      AjaxBind.bind(mInformForm.orgparentcode,orgparentcode); 
      var posparentcode=outparamters.getValue("posparentcode");
      AjaxBind.bind(mInformForm.posparentcode,posparentcode);
  }
   function getchangeposkk(outparamters)
   {
   	  var pretype=outparamters.getValue("pretype");
      var orgparentcode=outparamters.getValue("orgparentcode");
      var deptparentcode=outparamters.getValue("deptparentcode");
      var posparentcode=outparamters.getValue("posparentcode");
      var orgvalue=outparamters.getValue("orgvalue");
   
      if(orgvalue!=null && orgvalue.length>0)
      {
         var orgvalueview=outparamters.getValue("orgviewvalue");
         var dobjun=$('b0110') ;        
         dobjun.value=orgvalueview;
          var dobjun=$('b0110_value') ;        
         dobjun.value=orgvalue;   
      }
      
      var deptvalue=outparamters.getValue("deptvalue");
      if(deptvalue!=null && deptvalue.length>0)
      { 
         var deptviewvalue=outparamters.getValue("deptviewvalue");
         var dobjum=$('e0122') ;        
         dobjum.value=deptviewvalue;
         var dobjum=$('e0122_value') ; 
         dobjum.value=deptvalue;
      }
      AjaxBind.bind(mInformForm.orgparentcode,orgparentcode);
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
  		alert(INPUT_NUMBER_VALUE+'!');
  		obj.value='';  
  		obj.focus();
  	}  	   
}
function save(dbname)
{
	mInformForm.action="/general/inform/emp_add.do?b_save=link&dbname="+dbname;
	mInformForm.target="_self";
	mInformForm.submit();	
	closeWin();
}
function closeWin()
{
	var thevo=new Object();
	thevo.flag="true";
	window.returnValue=thevo;
	window.close();
}
function calcuBirthday(obj)
{
	if(obj.value=="")
		return;
	 if(!(obj.value.length == 15 || obj.value.length == 18))
	 {
	      alert(ERROR_IDCARD_INFO);
	      obj.value="";
  		  obj.focus();
  		  return;
	}
	var info = checkIdcard(obj.value);
	if(info!='身份证验证通过!')
	{
		alert(info);
		obj.value="";
  		obj.focus();
  	    return;
	}
	var hashvo=new ParameterSet();
    hashvo.setValue("idcardvalue",obj.value);
    var request=new Request({method:'post',onSuccess:getBirthdayAge,functionId:'02010001013'},hashvo);
}
  function getBirthdayAge(outparamters)
  {
     var birthdayvalue=outparamters.getValue("birthdayvalue");    
     var agevalue=outparamters.getValue("agevalue");
     var axvalue=outparamters.getValue("axvalue"); 
	 birthdayvalue=replaceAll(birthdayvalue,'.','-');
     if(birthdayvalue!=null)
     {
         var valueInputs=document.getElementsByName("a0111");
         if(valueInputs[0]!=null){
         	var dobj=valueInputs[0];
        	dobj.value=birthdayvalue;
        }
    
     }
     if(agevalue!=null)
     {
         var valueInputs=document.getElementsByName("c0101");
         if(valueInputs[0]!=null){
         var dobj=valueInputs[0];
          if(dobj!=null)
         dobj.value=agevalue;
         }
     }  
     if(axvalue!=null)
     {
         var valueInputs=document.getElementsByName("a0107_value");
         if(valueInputs[0]!=null){
         var dobj=valueInputs[0];
         //alert(axvalue);
          if(dobj!=null)
         dobj.value=axvalue;
         if(axvalue==1)
         {
            var valueInputs=document.getElementsByName("a0107");
            dobj=valueInputs[0];
            //alert(axvalue);
            if(dobj!=null)
            dobj.value="男";
         }else if(axvalue==2)
         {
            var valueInputs=document.getElementsByName("a0107");
            dobj=valueInputs[0];
            //alert(axvalue);
            if(dobj!=null)
            dobj.value="女";
         }
         }
     }        
     
  }
  function testIdCard()
  {
  	var idcard = document.getElementById('a0177').value;
  	var birthday =  document.getElementById('a0111').value;
  	var Sex = document.getElementById('a0107').value;
    if(idcard.length == 15)
	{
		var ShortYear,Month,Day,BYear;  	
  		ShortYear=idcard.substr(6,2);
      	Month=idcard.substr(8,2);
        Day=idcard.substr(10,2);
        ShortYear=ShortYear+Month+Day;
         
         BYear=birthday.substr(2,2);
         Month=birthday.substr(5,2);
         Day=birthday.substr(8,2);
         BYear=BYear+Month+Day;
         if(ShortYear!=BYear)
         {
      		 alert('身份证与出生日期不符！');
       		return false;
   		 }       		       
	}
	else if(idcard.length == 18)
	{
		 var Year,Month,Month,Day,BYear;  
		 Year=idcard.substr(6,4);
         Month=idcard.substr(10,2);
         Day=idcard.substr(12,2);
         Year=Year+Month+Day;
         
         BYear=birthday.substr(0,4);
         Month=birthday.substr(5,2);
         Day=birthday.substr(8,2);
         BYear=BYear+Month+Day;
         if(Year!=BYear)
         {
      		 alert('身份证与出生日期不符！');
       		 return false;
   		 } 		
	}
	var CodeSex;
	if(idcard.length==15)
    {
       CodeSex=idcard.substr(14,1);
       if(CodeSex==1)
            CodeSex='男';
       else
			CodeSex='女';  
       if(Sex!=CodeSex)
  	   {
      		 alert('身份证与性别不符！');
       		 return false;
   	   } 
   	}
	return true;
  }
  function checkIdcard(idcard){
  /*
var Errors=new Array(
"身份证验证通过!",
"身份证号码位数不对!",
"身份证号码出生日期超出范围或含有非法字符!",
"身份证号码校验错误!",
"身份证地区非法!"
);*/
var Errors=new Array(
"身份证验证通过!",
"身份证输入不正确!",
"身份证输入不正确!",
"身份证输入不正确!",
"身份证输入不正确!"
);
var area={11:"北京",12:"天津",13:"河北",14:"山西",15:"内蒙古",21:"辽宁",22:"吉林",23:"黑龙江",31:"上海",32:"江苏",33:"浙江",34:"安徽",35:"福建",36:"江西",37:"山东",41:"河南",42:"湖北",43:"湖南",44:"广东",45:"广西",46:"海南",50:"重庆",51:"四川",52:"贵州",53:"云南",54:"西藏",61:"陕西",62:"甘肃",63:"青海",64:"宁夏",65:"新疆",71:"台湾",81:"香港",82:"澳门",91:"国外"}


var idcard,Y,JYM;
var S,M;
var idcard_array = new Array();
idcard_array = idcard.split("");
//地区检验
if(area[parseInt(idcard.substr(0,2))]==null) return Errors[4];
//身份号码位数及格式检验
switch(idcard.length){
case 15:
if ( (parseInt(idcard.substr(6,2))+1900) % 4 == 0 || ((parseInt(idcard.substr(6,2))+1900) % 100 == 0 && (parseInt(idcard.substr(6,2))+1900) % 4 == 0 )){
ereg=/^[1-9][0-9]{5}[0-9]{2}((01|03|05|07|08|10|12)(0[1-9]|[1-2][0-9]|3[0-1])|(04|06|09|11)(0[1-9]|[1-2][0-9]|30)|02(0[1-9]|[1-2][0-9]))[0-9]{3}$/;//测试出生日期的合法性
} else {
ereg=/^[1-9][0-9]{5}[0-9]{2}((01|03|05|07|08|10|12)(0[1-9]|[1-2][0-9]|3[0-1])|(04|06|09|11)(0[1-9]|[1-2][0-9]|30)|02(0[1-9]|1[0-9]|2[0-8]))[0-9]{3}$/;//测试出生日期的合法性
}
if(ereg.test(idcard)) return Errors[0];
else return Errors[2];
break;
case 18:
//18位身份号码检测
//出生日期的合法性检查
//闰年月日:((01|03|05|07|08|10|12)(0[1-9]|[1-2][0-9]|3[0-1])|(04|06|09|11)(0[1-9]|[1-2][0-9]|30)|02(0[1-9]|[1-2][0-9]))
//平年月日:((01|03|05|07|08|10|12)(0[1-9]|[1-2][0-9]|3[0-1])|(04|06|09|11)(0[1-9]|[1-2][0-9]|30)|02(0[1-9]|1[0-9]|2[0-8]))
if ( parseInt(idcard.substr(6,4)) % 4 == 0 || (parseInt(idcard.substr(6,4)) % 100 == 0 && parseInt(idcard.substr(6,4))%4 == 0 )){
ereg=/^[1-9][0-9]{5}19[0-9]{2}((01|03|05|07|08|10|12)(0[1-9]|[1-2][0-9]|3[0-1])|(04|06|09|11)(0[1-9]|[1-2][0-9]|30)|02(0[1-9]|[1-2][0-9]))[0-9]{3}[0-9Xx]$/;//闰年出生日期的合法性正则表达式
} else {
ereg=/^[1-9][0-9]{5}19[0-9]{2}((01|03|05|07|08|10|12)(0[1-9]|[1-2][0-9]|3[0-1])|(04|06|09|11)(0[1-9]|[1-2][0-9]|30)|02(0[1-9]|1[0-9]|2[0-8]))[0-9]{3}[0-9Xx]$/;//平年出生日期的合法性正则表达式
}
if(ereg.test(idcard)){//测试出生日期的合法性
//计算校验位
S = (parseInt(idcard_array[0]) + parseInt(idcard_array[10])) * 7
+ (parseInt(idcard_array[1]) + parseInt(idcard_array[11])) * 9
+ (parseInt(idcard_array[2]) + parseInt(idcard_array[12])) * 10
+ (parseInt(idcard_array[3]) + parseInt(idcard_array[13])) * 5
+ (parseInt(idcard_array[4]) + parseInt(idcard_array[14])) * 8
+ (parseInt(idcard_array[5]) + parseInt(idcard_array[15])) * 4
+ (parseInt(idcard_array[6]) + parseInt(idcard_array[16])) * 2
+ parseInt(idcard_array[7]) * 1
+ parseInt(idcard_array[8]) * 6
+ parseInt(idcard_array[9]) * 3 ;
Y = S % 11;
M = "F";
JYM = "10X98765432";
M = JYM.substr(Y,1);//判断校验位
if(M == idcard_array[17]) return Errors[0]; //检测ID的校验位
else return Errors[3];
}
else return Errors[2];
break;
default:
return Errors[1];
break;
}

}
  