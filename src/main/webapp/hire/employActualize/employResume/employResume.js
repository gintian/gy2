
function showOrClose()
{
		var obj=eval("aa");
		var obj3=eval("b");
		var obj2=eval("document.employResumeForm.isShowCondition");
	
		
    		if(obj.style.display=='none')
	    	{
    			obj.style.display='block'
        		obj3.style.display='block';
	    		obj2.value="block";
	    	
	    		
    		}
    		else
	    	{
	    		obj.style.display='none';
	    		obj3.style.display='none';
	    		obj2.value="none";	
	    	
    		}
    	
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
	

function validateData(obj,itemdesc)
{
	var dd=true;
	if(trim(obj.value).length!=0)
	{						
							 var myReg =/^(-?\d+)(\.\d+)?$/
							 if(IsOverStrLength(obj.value,10))
							 {
								 alert(itemdesc+RIGHT_FORMAT_IS+"！");
								 return false;
							 }
							 else
							 {
							 	if(trim(obj.value).length!=10)
							 	{
							 		 alert(itemdesc+RIGHT_FORMAT_IS+"！");
									 return false;
							 	}
								var year=obj.value.substring(0,4);
								var month=obj.value.substring(5,7);
								var day=obj.value.substring(8,10);
								if(!myReg.test(year)||!myReg.test(month)||!myReg.test(day)) 
							 	{
									 alert(itemdesc+RIGHT_FORMAT_IS+"！");
									 return false;
							 	}
							 	if(year<1900||year>2100)
							 	{
							 		 alert(itemdesc+YEAR_SCORPE+"！");
									 return false;
							 	}
							 	
							 	if(!isValidDate(day, month, year))
							 	{
									 alert(itemdesc+RIGHT_FORMAT_IS+"！");
									 return false;
							 	}
							 }
	}
	return dd
}




	function showDateSelectBox(srcobj)
   {
      
      date_desc=srcobj;      
      Element.show('date_panel');   
      for(var i=0;i<document.employResumeForm.date_box.options.length;i++)
  	  {
  	  	document.employResumeForm.date_box.options[i].selected=false;
  	  }
      var pos=getAbsPosition(srcobj);
	  with($('date_panel'))
	  {
	        style.position="absolute";
    		style.posLeft=pos[0]-1;
			style.posTop=pos[1]-1+srcobj.offsetHeight;
			style.width=110;
      }                 
      
   }
   
   
   
   function operateCheckBox(obj)
   {
   		var value=obj.checked;
   		for(var i=0;i<document.employResumeForm.elements.length;i++)
   		{
	   		
   			if(document.employResumeForm.elements[i].type=='checkbox'&&document.employResumeForm.elements[i].name.length>18&&document.employResumeForm.elements[i].name.substring(0,18)=='pagination.select[')
   				document.employResumeForm.elements[i].checked=value;
   		
   		}
   		if(obj.checked)
	   		document.employResumeForm.isSelectedAll.value="1";
   		else
   			document.employResumeForm.isSelectedAll.value="0";
   
   }
   
   
   function delRecord()
   {
   	 
       var isSelected=false;
   	   for(var i=0;i<document.employResumeForm.elements.length;i++)
   	   {
	   		
   			if(document.employResumeForm.elements[i].type=='checkbox'&&document.employResumeForm.elements[i].name.length>18&&document.employResumeForm.elements[i].name.substring(0,18)=='pagination.select[')
   			{
   				if(document.employResumeForm.elements[i].checked==true)
   				{
   					isSelected=true;
   					
   				}  				
   			}
   		}
   		
  		if(!isSelected)
  		{
  			alert(PLASE_SELECT_RECORD+"！");
  			return ;
  		}
  		if(confirm(GZ_REPORT_CONFIRMDELETE))
  		{
  			 document.employResumeForm.action="/hire/employActualize/employResume.do?b_setState=set&personType=0&operate=del"; 	 		 
		     document.employResumeForm.submit();
  		
  		}
   
   }
   function delRecord2()
   {
   	 
       var isSelected=false;
   	   for(var i=0;i<document.employResumeForm.elements.length;i++)
   	   {
	   		
   			if(document.employResumeForm.elements[i].type=='checkbox'&&document.employResumeForm.elements[i].name.length>18&&document.employResumeForm.elements[i].name.substring(0,18)=='pagination.select[')
   			{
   				if(document.employResumeForm.elements[i].checked==true)
   				{
   					isSelected=true;
   					
   				}  				
   			}
   		}
   		
  		if(!isSelected)
  		{
  			alert(PLASE_SELECT_RECORD+"！");
  			return ;
  		}
  		if(confirm(GZ_REPORT_CONFIRMDELETE))
  		{
  			 document.employResumeForm.action="/hire/employActualize/employResume.do?b_setState=set&personType=4&operate=del"; 	 		 
		     document.employResumeForm.submit();
  		
  		}
   
   }
   
   function switchPersonType(personType)
   {
   	   var isSelected=false;
   	   for(var i=0;i<document.employResumeForm.elements.length;i++)
   	   {
	   		
   			if(document.employResumeForm.elements[i].type=='checkbox'&&document.employResumeForm.elements[i].name.length>18&&document.employResumeForm.elements[i].name.substring(0,18)=='pagination.select[')
   			{
   				if(document.employResumeForm.elements[i].checked==true)
   				{
   					isSelected=true;
   					
   				}  				
   			}
   		}
   		
  		if(!isSelected)
  		{
  			alert(PLASE_SELECT_RECORD+"！");
  			return ;
  		}
  		var desc=TALENTED_PERSON_STOREHOUSE;
  		if(personType==1)
  			desc=EMPLOY_STOREHOUSE;
  		
  		if(confirm(CONFIRM_EXECUTE_SWITCH_TO+desc+HIRE_OPERATION))
  		{
  			 document.employResumeForm.action="/hire/employActualize/employResume.do?b_setState=set&operate=switch&personType="+personType; 	 		 
		     document.employResumeForm.submit();
  		
  		}
   }
   
   
   
   function setSelectValue(rstate)
   {
       Element.hide('date_panel');   
       var isSelected=false;
       var num=0;
       var index=0;
       var ids=document.getElementsByName("ids");  
        if(rstate=="-3"||rstate=="-2")
           return;
       var recordIDs=new Array();
      
  	   for(var i=0;i<document.employResumeForm.elements.length;i++)
   	   {  	   		
   			if(document.employResumeForm.elements[i].type=='checkbox'&&document.employResumeForm.elements[i].name.length>18&&document.employResumeForm.elements[i].name.substring(0,18)=='pagination.select[')
   			{   				
   				if(document.employResumeForm.elements[i].checked==true)
   				{
   					isSelected=true;  				
   					recordIDs[index++]=ids[num].value;
   				}
   				num++;
   			}
   		}
   		
  		if(!isSelected)
  		{
  			alert(PLASE_SELECT_RECORD+"！");
  			return ;
  		}
  		
  		for(var i=0;i<document.employResumeForm.date_box.options.length;i++)
  		{
  			if(document.employResumeForm.date_box.options[i].selected)
  			{
  				if(confirm(CONFIRM_TO_INSTALL+document.employResumeForm.date_box.options[i].text+HIRE_STATUS+"？"))
  				{
  						
  					
  						validateState(recordIDs,document.employResumeForm.date_box.options[i].value);
  				
  				}
  				else
  					break;
  			}
  		}
   }
   
   


	function editUpValue(setName,obj)
	{
		
		var upValueObj=eval("document.employResumeForm.upValue");
		var upValue_str=upValueObj.value.toLowerCase();
		
		if(upValue_str.indexOf(","+setName)!=-1)
		{
			if(obj.checked==false)
				upValue_str=strReplaceAll(upValue_str,","+setName,"")
		}
		else
		{
			if(obj.checked==true)
				upValue_str=upValue_str+","+setName
		}	
		upValueObj.value=upValue_str;
	}




function showRows(objName)
{
	var objs=eval(objName);
	for(var i=0;i<3;i++)
	{
		if(objs[i].style.display=='none')
		{
			objs[i].style.display='block';
			break;
		}	
	}

}

function delRows(objName)
{
	
	var objs=eval(objName);	
	for(var i=2;i>=0;i--)
	{
		if(objs[i].style.display=='block')
		{
			objs[i].style.display='none';
			var a_td=objs[i].cells[1];
			 var   oinput =a_td.getElementsByTagName("input")   ;
			 for(var j=0;j<oinput.length;j++)
			 {
			 	oinput[j].value="";
			 }
			 var   oselect =a_td.getElementsByTagName("select")   ;
			 for(var j=0;j<oselect.length;j++)
			 {
			 	oselect[j].value="";
			 }
			 
			break;
		}	
	}

}	


   function showPhoto()
   {
        document.employResumeForm.action="/hire/employActualize/employResumePhoto.do?br_browse=browse&operate=init";
		document.employResumeForm.submit();
   }

