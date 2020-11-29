<!--
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
  		alert(INPUT_NUMBER_VALUE+'!');
  		obj.value='';  
  		obj.focus();  
  	}  	   
}
function save()
{
	contractForm.action="/ht/inform/data_table.do?b_save=link";
	contractForm.target="_self";
	contractForm.submit();
	closeWin();	
}
function closeWin()
{
	var thevo=new Object();
	thevo.flag="true";
	window.returnValue=thevo;
	window.close();
}
function fomateDate(obj) {
	var	reg =/^\d{4}-\d{1,2}-\d{1,2}$/;
	
	if (reg.test(obj.value)) {
		return true;
	} else {
		return false;
	}
}

function vali(obj, desc,event) {
		if (fomateDate(obj)) {
			if(!validate(obj,desc)){  
				obj.value='';
		 	}
		 } else {
		 	alert(desc + "格式不正确，应为yyyy-MM-dd!");
		 	obj.value='';
		 }
}

