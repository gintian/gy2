   this.fObj = null;
   var time_r=0; 
   function setFocusObj(obj,time_vv) 
   {		
	this.fObj = obj;
	time_r=time_vv;		
   }
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
    	var isUnUmRela = $F('isUnUmRela');
    	if(isUnUmRela==null || isUnUmRela=='false')
    	{
    		fieldcode2(sourceobj);
    		return;
    	} 

      var dobjun=$('b0110_value') ;     
      var dobjum=$('e0122_value') ;      
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

      if(pretype=="UN")   
      {        
        var request=new Request({method:'post',onSuccess:getchangeposun,functionId:'2020030064'},hashvo);//02010001012
      }
     if(pretype=="UM")
      {
        var request=new Request({method:'post',onSuccess:getchangeposum,functionId:'2020030064'},hashvo);//02010001012
      }
    }
    function getchangeposun(outparamters)
    {
      var pretype=outparamters.getValue("pretype");
      var orgparentcode=outparamters.getValue("orgparentcode");
      var deptparentcode=outparamters.getValue("deptparentcode");

      AjaxBind.bind(trainAddForm.orgparentcode,orgparentcode);
      AjaxBind.bind(trainAddForm.deptparentcode,deptparentcode);
           
      var dobjun=$('e0122') ; 
      if(dobjun!=null)
        dobjun.value="";
  
      dobjun=$('e0122_value') ;
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
            
      var deptparentcode=outparamters.getValue("deptparentcode");
      AjaxBind.bind(trainAddForm.orgparentcode,orgparentcode);  
  }
   function fieldcode2(sourceobj)
   {	
	　var　targetobj,target_name,hidden_name,hiddenobj;
   	  target_name=sourceobj.name;    
      hidden_name=target_name.replace(".viewvalue",".value");       	
      var hiddenInputs=document.getElementsByName(hidden_name);
      if(hiddenInputs!=null)    
    	hiddenobj=hiddenInputs[0];
     hiddenobj.value=sourceobj.value;	
	}
	function openOrgInfo(codeid,mytarget,check,flag){
	var managerstr ="";
	if(check==2){
		managerstr=document.getElementById("companyid").value;
	}else if(check==3){
		managerstr=document.getElementById("depid").value;
	}
    var codevalue,thecodeurl,target_name,hidden_name,hiddenobj;
    if(mytarget==null)
      return;
    var oldInputs=document.getElementsByName(mytarget);
    oldobj=oldInputs[0];
    target_name=oldobj.name;
    hidden_name=target_name.replace(".viewvalue",".value"); 
    hidden_name=hidden_name.replace(".hzvalue",".value");
       
    var hiddenInputs=document.getElementsByName(hidden_name);
    
    if(hiddenInputs!=null)
    {
    	hiddenobj=hiddenInputs[0];
    	codevalue=managerstr;
    }
    
    var theArr=new Array(codeid,codevalue,oldobj,hiddenobj,flag); 
    thecodeurl="/system/untrain.jsp?codesetid="+$URL.encode(codeid)+"&codeitemid=&isfirstnode=" +$URL.encode(flag); 
    var popwin= window.showModelessDialog(thecodeurl, theArr, 
        "dialogWidth:300px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:yes");
}
function isNumber(obj)//检测输入的是否为数字
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
function isNum(obj)//检测输入的是否为非负数
{
  		var checkOK = "0123456789.";
 		var checkStr = obj.value;
  		var allValid = true;
  		var decPoints = 0;
  		var allNum = "";
  		if (checkStr=="")
  			return;
  		for (i = 0;  i < checkStr.length;  i++)
	    {
    		ch = checkStr.charAt(i);
    		
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
  	if (decPoints > 1 || !allValid) 
  	{
  		alert(INPUT_NANUMBER_VALUE+'!');
  		obj.value=''; 
  	    obj.focus();
  	    return false;
  	}  	
 	
}