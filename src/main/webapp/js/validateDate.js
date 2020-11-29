function IsOverStrLength(str,len)
{
   
   return str.replace(/[^\x00-\xff]/g,"**").length>len
   
}
function isValidDate(day, month, year) {
    if (month < 1 || month > 12) {
            return false;
        }
        if (day < 1 || day > 31) {
            return false;
        }
        if ((month == 4 || month == 6 || month == 9 || month == 11) &&
            (day == 31)) {
            return false;
        }
        if (month == 2) {
            var leap = (year % 4 == 0 &&
                       (year % 100 != 0 || year % 400 == 0));
            if (day>29 || (day == 29 && !leap)) {
                return false;
            }
        }
        return true;
    }
	

function validate(obj,aitemdesc,val)
{
	var dd=true;
	var itemdesc="";
	var formd = "";
	if (!val) {
		formd = "yyyy-mm-dd";
	} else {
		formd = val;
	}
	if(aitemdesc==null||aitemdesc==undefined)
		itemdesc="日期";
	else 
		itemdesc=aitemdesc;
	if(trim(obj.value).length!=0)
	{						
							 var myReg =/^(-?\d+)(\.\d+)?$/
							 if(IsOverStrLength(obj.value,10))
							 {
								 alert(itemdesc+"格式不正确，正确格式为"+formd+"！");
								 return false;
							 }
							 else
							 {
							 	if(trim(obj.value).length!=10)
							 	{
							 		 alert(itemdesc+"格式不正确，正确格式为"+formd+"！");
									 return false;
							 	}
								var year=obj.value.substring(0,4);
								var month=obj.value.substring(5,7);
								var day=obj.value.substring(8,10);
								if(!myReg.test(year)||!myReg.test(month)||!myReg.test(day)) 
							 	{
									 alert(itemdesc+"格式不正确，正确格式为"+formd+"！");
									 return false;
							 	}
							 	/*if(year<1900||year>2100)
							 	{
							 		 alert(itemdesc+" 年范围为1900~2100！");
									 return false;
							 	}*/
							 	
							 	if(!isValidDate(day, month, year))
							 	{
									 alert(itemdesc+"错误，无效时间！");
									 return false;
							 	}
							 }
	}
	return dd
}

function validate2(obj,aitemdesc,val)
{
	var dd=true;
	var itemdesc="";
	var formd = "";
	if (!val) {
		formd = "yyyy-mm-dd";
	} else {
		formd = val;
	}
	if(aitemdesc==null||aitemdesc==undefined)
		itemdesc="日期";
	else 
		itemdesc=aitemdesc;
	if(trim(obj.value).length!=0)
	{						
							 var myReg =/^(-?\d+)(\.\d+)?$/		 
							 if(IsOverStrLength(obj.value,10))
							 {
								 alert(itemdesc+"格式不正确，正确格式为"+formd+"！");
								 return false;
							 }
							 else
							 {
							 	if(trim(obj.value).length!=10&&trim(obj.value).length!=7&&trim(obj.value).length!=4)
							 	{
							 		 alert(itemdesc+"格式不正确，正确格式为"+formd+"！");
									 return false;
							 	}
							 	
							 	var year="0";
							 	var month="01";
							 	var day="01";
							 	
								year=obj.value.substring(0,4);
								if(IsOverStrLength(obj.value,5))
									month=obj.value.substring(5,7);
								if(IsOverStrLength(obj.value,9))
									day=obj.value.substring(8,10);
								
								if(!myReg.test(year)||!myReg.test(month)||!myReg.test(day)) 
							 	{
									 alert(itemdesc+"格式不正确，正确格式为"+formd+"！");
									 return false;
							 	}
							 	/*if(year<1900||year>2100)
							 	{
							 		 alert(itemdesc+" 年范围为1900~2100！");
									 return false;
							 	}*/
							 	
							 	if(!isValidDate(day, month, year))
							 	{
									 alert(itemdesc+"错误，无效时间！");
									 return false;
							 	}
							 }
	}
	return dd
}
 


 