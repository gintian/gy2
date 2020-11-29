var gsetname;
 
function reloadBybase(dbname)
{
   mInformForm.action="/general/inform/get_data_table.do?b_query=link&dbname="+dbname;
   mInformForm.submit();  	
}

function reloadBySetId(setobj)
{
   var setname;
   for(i=0;i<setobj.options.length;i++)
   {
      if(setobj.options[i].selected)
      {
    	setname=setobj.options[i].value;
    	break;
      }   
   }
   mInformForm.action="/general/inform/get_data_table.do?b_query=link&setname="+setname;
   mInformForm.submit();  	
}

function setInsertRecord(outparamters)
{
		var tablename,table,dataset,preno,bmainset;
	    tablename="table"+gsetname;
        table=$(tablename);
        dataset=table.getDataset();
        record=dataset.getCurrent();	
		if(gsetname.indexOf("A01")!=-1)
		{
            preno=record.getValue("A0000")-1;
            if(preno<1){
            	preno=1;
            }
        	dataset.insertRecord("before");	
	        record=dataset.getCurrent();
	        if(isExistField(dataset,'A0100'))
    	   		record.setValue("A0100",outparamters.getValue("A0100")); 
    	   	if(isExistField(dataset,'B0110'))
        		record.setValue("B0110",outparamters.getValue("B0110")); 
        	if(isExistField(dataset,'E0122'))	
	       		 record.setValue("E0122",outparamters.getValue("E0122")); 
	  		if(isExistField(dataset,'E01A1'))	
	        	record.setValue("E01A1",outparamters.getValue("E01A1"));
	        if(isExistField(dataset,'A0000'))	
    	 	   record.setValue("A0000",preno);  
    	    record.setState("modify");
    	    var fielditem = outparamters.getValue("fielditem");
			if(fielditem!=null&&fielditem.length>0){
				var itemsarr = fielditem.split(",");
				for(var i=0;i<itemsarr.length;i++){
					if(itemsarr[i]!=null&&itemsarr[i].length>0){
						 if(isExistField(dataset,itemsarr[i]))
							record.setValue(itemsarr[i],outparamters.getValue(itemsarr[i]));
					}
				}
			}	
			if (document.getElementById("movDiv")){
				target = document.getElementById("movDiv");
				var photo_state = parent.mil_menu.document.all.photo_state.value;
				photo_state=photo_state!=null&&photo_state.length>0?photo_state:"0";
				if(photo_state=="1")
					target.style.display = "block";
				if(target.style.display != "block"){
					return;
				}
			}
   			document.getElementById("realName").innerHTML="";
    		var photo_w = 127;
    		if(document.all('ole').offsetWidth!=null&&document.all('ole').offsetWidth!=0)
    			photo_w = document.all('ole').offsetWidth;
    		var photo_h = 180;
    		if(document.all('ole').offsetHeight!=null&&document.all('ole').offsetHeight!=0)
    			photo_h = document.all('ole').offsetHeight;
    		document.all('ole').style.height=parseInt(photo_h);
    		document.all('ole').style.width=parseInt(photo_w);
    		document.ole.location.href="/general/inform/emp/view/displaypicture.do?b_query=link&a0100="+outparamters.getValue("A0100")+"&a0101=";
    		toggles("movDiv"); 
		}
		else
		{
			var I9999=record.getValue("I9999");
			
			if(I9999=="")
			{
				record.setValue("I9999",outparamters.getValue("I9999"));
				var fielditem = outparamters.getValue("fielditem");
				if(fielditem!=null&&fielditem.length>0){
					var itemsarr = fielditem.split(",");
					for(var i=0;i<itemsarr.length;i++){
						if(itemsarr[i]!=null&&itemsarr[i].length>0){
						  if(isExistField(dataset,itemsarr[i]))
							record.setValue(itemsarr[i],outparamters.getValue(itemsarr[i]));
						}
					}
				}
				if(gsetname.substring(3)=='A00'||gsetname.substring(3)=='a00'){
					record.setValue("flag","选择");	
					record.setValue("downole","downole");
					record.setValue("upole","upole");		
				}
				record.setState("modify");
			}
			else
			{
			    var temp;
			    var A0100="",A0101="",B0110="",E0122="",E01A1="";
	        	if(isExistField(dataset,'A0100'))
    	    	{
    	    		A0100=record.getValue("A0100");
    	    		A0100=A0100!=null&&A0100!='undefined'?A0100:"";
    	    	}    	    	
        		if(isExistField(dataset,'A0101'))
    	    	{
    	    		A0101=record.getValue("A0101");
    	    		A0101=A0101!=null&&A0101!='undefined'?A0101:"";
    	    	}  	       			
        	    if(isExistField(dataset,'B0110'))
    	    	{
    	    		 B0110=record.getValue("B0110");
    	    		 B0110=B0110!=null&&B0110!='undefined'?B0110:"";
    	    	} 
    			if(isExistField(dataset,'E0122'))
    	    	{
    	    		 E0122=record.getValue("E0122");
    	    		 E0122=E0122!=null&&E0122!='undefined'?E0122:"";
    	    	} 
				if(isExistField(dataset,'E01A1'))
    	    	{
    	    		 E01A1=record.getValue("E01A1");
    	    		 E01A1=E01A1!=null&&E01A1!='undefined'?E01A1:"";
    	    	} 			
           	    dataset.insertRecord("before");
           	    record=dataset.getCurrent();
    	    	record.setValue("I9999",outparamters.getValue("I9999")); 
    	    	if(isExistField(dataset,'A0100'))
    	    	{
    	    		 record.setValue("A0100",A0100); 
    	    	}    	    	
        		if(isExistField(dataset,'A0101'))
    	    	{
    	    		 record.setValue("A0101",A0101); 
    	    	}  	       			
        	    if(isExistField(dataset,'B0110'))
    	    	{
    	    		 record.setValue("B0110",B0110); 
    	    	} 
    			if(isExistField(dataset,'E0122'))
    	    	{
    	    		 record.setValue("E0122",E0122); 
    	    	} 
				if(isExistField(dataset,'E01A1'))
    	    	{
    	    		 record.setValue("E01A1",E01A1); 
    	    	} 
    	    	if(gsetname.substring(3)=='A00'||gsetname.substring(3)=='a00'){
					record.setValue("flag","选择");	
					record.setValue("downole","downole");
					record.setValue("upole","upole");		
				}
	        	record.setState("modify");
	        	var fielditem = outparamters.getValue("fielditem");
				if(fielditem!=null&&fielditem.length>0){
					var itemsarr = fielditem.split(",");
					for(var i=0;i<itemsarr.length;i++){
						if(itemsarr[i]!=null&&itemsarr[i].length>0){
							if(isExistField(dataset,itemsarr[i]))
								record.setValue(itemsarr[i],outparamters.getValue(itemsarr[i]));
						}
					}
				}
	        	while(true)
	        	{
	        		record=record.getNextRecord();
	        		if(record==undefined)
	        			break;
	        		temp=record.getValue("A0100");
	        		if(temp==A0100)
	        		{
	        		    record.setValue("I9999",record.getValue("I9999")+1);	
	        		}
	        		else
	        			break;
	        	}           	    
			}
		}//end.
}
function insert(setname,a_code,reserveitem)
{
		var tablename,table,dataset,preno,record,i9999;
	    tablename="table"+setname;
		gsetname=setname
	    i9999="";
        table=$(tablename);
        dataset=table.getDataset();	
        record=dataset.getCurrent();
		if(!record){
			var arr = reserveitem.split("`");
			var itemvalues="";
			var checkflag="";
			for(var i=0;i<arr.length;i++){
				if(arr[i]!=null&&arr[i].length>0){
					var item_arr = arr[i].split(",.");
					if(item_arr!=null&&item_arr.length==2){
						itemvalues=record.getValue(item_arr[0]); 
						if(itemvalues==null||itemvalues.length<1){
							checkflag = item_arr[1]+"为必填项!";
							break;
						}
					}
				}
			}
			var chkrecord = "0";
			if(setname.indexOf("A01")==-1){
				if(record.getValue("I9999")==null||record.getValue("I9999")==""){
					chkrecord = "1";
				}
			}
			if(chkrecord=="0"&&checkflag!=null&&checkflag.length>3){
				alert(checkflag);
				return false;
			}  
		}
        var a0100=record.getValue("A0100");

		var hashvo=new ParameterSet();
		hashvo.setValue("setname",setname);
		hashvo.setValue("type","insert");		
		hashvo.setValue("A0100",a0100);
		hashvo.setValue("a_code",a_code);	
        if(setname.indexOf("A01")==-1){
		  i9999=record.getValue("I9999");
		  hashvo.setValue("I9999",i9999);
		}else{
	        preno=record.getValue("A0000");		
	    }
		hashvo.setValue("preno",preno);

	   	var request=new Request({method:'post',asynchronous:false,onSuccess:setInsertRecord,functionId:"1010090002"},hashvo);
}
//判断表格里面是否包含某个字段（fzg加）
function isExistField(dataset,fieldName)
{
	var field=dataset.getField(fieldName);
	if(field!=null)
		return true;
	 return false;
}
function setAppendRecord(outparamters)
{
		var tablename,table,dataset,preno,bmainset,record;
	    tablename="table"+gsetname;
        table=$(tablename);
        dataset=table.getDataset(); 
        record=dataset.getCurrent();  
		if(gsetname.indexOf("A01")!=-1){
        	dataset.insertRecord("end");	
	        record=dataset.getCurrent();
	        if(isExistField(dataset,'a0100'))
	        	 record.setValue("a0100",outparamters.getValue("A0100"));
    	   	if(isExistField(dataset,'b0110'))	 
    	   		 record.setValue("b0110",outparamters.getValue("B0110"));       		
            if(isExistField(dataset,'e0122'))	
    	   		 record.setValue("e0122",outparamters.getValue("E0122"));             	      		  
	        if(isExistField(dataset,'a0000'))	
	        	 record.setValue("a0000",outparamters.getValue("A0000"));    	   		
    	    if(isExistField(dataset,'e01a1'))	
    	    	record.setValue("e01a1",outparamters.getValue("E01A1")); 
    	    record.setState("modify");   
    	    if (document.getElementById("movDiv")){
				target = document.getElementById("movDiv");
				var photo_state = parent.mil_menu.document.all.photo_state.value;
				photo_state=photo_state!=null&&photo_state.length>0?photo_state:"0";
				if(photo_state=="1")
					target.style.display = "block";
				if(target.style.display != "block"){
					return;
				}
			}
    		document.getElementById("realName").innerHTML="";
    		var photo_w = 127;
    		if(document.all('ole').offsetWidth!=null&&document.all('ole').offsetWidth!=0)
    			photo_w = document.all('ole').offsetWidth;
    		var photo_h = 180;
    		if(document.all('ole').offsetHeight!=null&&document.all('ole').offsetHeight!=0)
    			photo_h = document.all('ole').offsetHeight;
    		document.all('ole').style.height=parseInt(photo_h);
    		document.all('ole').style.width=parseInt(photo_w);
    		document.ole.location.href="/general/inform/emp/view/displaypicture.do?b_query=link&a0100="+outparamters.getValue("A0100")+"&a0101=";
    		toggles("movDiv");
		}
		else
		{
			var I9999=record.getValue("I9999");
			if(I9999==""||I9999=="0")
			{
				record.setValue("I9999",outparamters.getValue("I9999"));
				record.setValue("A0100",outparamters.getValue("A0100"));
			}else{
			    var temp,blast;
			    blast=false;
			    temp=record.getValue("A0100");		 
	        	while(true)
	        	{
	        		var record1 =record.getNextRecord();
	        		var A0100="";
	        		if(record1==undefined)
	        		{
	        		 	blast=true;
	        		 	break;
	        		 }
	        		
	        		if(isExistField(dataset,'A0100'))
    	    		{
	        			A0100 = record1.getValue("A0100");
	        			if(temp!=A0100)
							break;
						temp=record1.getValue("A0100");
					}
					 record = record1;
	        	} 
	        	
	        	var A0100="",A0101="",B0110="",E0122="",E01A1="";   
	        	if(isExistField(dataset,'A0100'))
    	    	{
    	    		A0100=record.getValue("A0100");
    	    		A0100=A0100!=null&&A0100!='undefined'?A0100:"";  
    	    	}    	    	
        		if(isExistField(dataset,'A0101'))
    	    	{  
    	    		A0101=record.getValue("A0101");
    	    		A0101=A0101!=null&&A0101!='undefined'?A0101:"";
    	    	}  	       			
        	    if(isExistField(dataset,'B0110'))
    	    	{
    	    		 B0110=record.getValue("B0110");
    	    		 B0110=B0110!=null&&B0110!='undefined'?B0110:"";
    	    	} 
    			if(isExistField(dataset,'E0122'))
    	    	{
    	    		 E0122=record.getValue("E0122");
    	    		 E0122=E0122!=null&&E0122!='undefined'?E0122:"";
    	    	} 
				if(isExistField(dataset,'E01A1'))
    	    	{  
    	    		 E01A1=record.getValue("E01A1");
    	    		 E01A1=E01A1!=null&&E01A1!='undefined'?E01A1:"";
    	    	} 	
	        	if(!blast){      	   
	        		dataset.setCurrent(record); 
           	    	dataset.insertRecord("after");
           	    }else{
           	    	dataset.insertRecord("end");   	
           	    }   
           	    record=dataset.getCurrent();
    	    	record.setValue("I9999",outparamters.getValue("I9999")); 
    	    	if(isExistField(dataset,'A0100'))
    	    	{
    	    		 record.setValue("A0100",A0100); 
    	    	}    	    	
        		if(isExistField(dataset,'A0101'))
    	    	{
    	    		 record.setValue("A0101",A0101); 
    	    	}  	       			
        	    if(isExistField(dataset,'B0110'))
    	    	{
    	    		record.setValue("B0110",B0110); 
    	    	} 
    			if(isExistField(dataset,'E0122'))
    	    	{
    	    		 record.setValue("E0122",E0122); 
    	    	} 
				if(isExistField(dataset,'E01A1'))
    	    	{
    	    		 record.setValue("E01A1",E01A1); 
    	    	} 			        	
			}
			if(gsetname.substring(3)=='A00'||gsetname.substring(3)=='a00'){
				record.setValue("flag","选择");	
				record.setValue("downole","downole");
				record.setValue("upole","upole");		
			}	
			record.setState("modify");		
		}
		var fielditem = outparamters.getValue("fielditem");
		if(fielditem!=null&&fielditem.length>0){
			var itemsarr = fielditem.split(",");
			for(var i=0;i<itemsarr.length;i++){
				if(itemsarr[i]!=null&&itemsarr[i].length>0){
					record.setValue(itemsarr[i],outparamters.getValue(itemsarr[i]));
				}
			}
		}
}

function append(setname,a_code,reserveitem)
{
	var tablename,table,dataset,record,i9999;
	tablename="table"+setname;
	gsetname=setname;
	i9999="";
    table=$(tablename);
    dataset=table.getDataset();	
    record=dataset.getCurrent();
    if(record){
  		var arr = reserveitem.split("`");
		var itemvalues="";
		var checkflag="";
		for(var i=0;i<arr.length;i++){
			if(arr[i]!=null&&arr[i].length>0){
				var item_arr = arr[i].split(",.");
				if(item_arr!=null&&item_arr.length==2){
					itemvalues=record.getValue(item_arr[0]); 
					if(itemvalues==null||itemvalues.length<1){
						checkflag = item_arr[1]+"为必填项!";
						break;
					}
				}
			}
		}
		var chkrecord = "0";
		if(setname.indexOf("A01")==-1){
			if(record.getValue("I9999")==null||record.getValue("I9999")==""){
				chkrecord = "1";
			}
		}
		if(chkrecord=="0"&&checkflag!=null&&checkflag.length>3){
			alert(checkflag);
			return false;
		}   
    }
	var a0100="";
	if(record!=undefined){
		a0100=record.getValue("A0100");
	}else{
		if(setname.indexOf("A01")==-1)
			return;
	}
	var hashvo=new ParameterSet();
	hashvo.setValue("setname",setname);	
	hashvo.setValue("A0100",a0100);	
	hashvo.setValue("a_code",a_code);	
	hashvo.setValue("type","append");	
	var request=new Request({method:'post',asynchronous:false,onSuccess:setAppendRecord,functionId:'1010090002'},hashvo);
}
function batchHand(flag,a_code,dbname,viewsearch,inforflag){
	//inforflag 1:员工管理 2：外部培训
	if(typeof(inforflag) == "undefined" || inforflag == null)
		 inforflag = "1";

	var thecodeurl =""; 
	var return_vo;	
	var setname = document.getElementById("setname").value;
	switch(flag){ 
         case 1	: 
         	  var strId = selectValueStr(dbname+setname);
         	  var tablename="table"+dbname+setname;
    		  var table=$(tablename);
    		  var temp=table.getActiveCell();
  			  var field_name=temp.getField();
              thecodeurl="/general/inform/emp/batch/alertind.do?b_query=link&setname="+setname+"&a_code="+a_code+"&dbname="+dbname+"&viewsearch="+viewsearch+"&infor=1&strid="+strId+"&field_name="+field_name+"&inforflag="+inforflag;
              return_vo= window.showModalDialog(thecodeurl, "", 
              	"dialogWidth:400px; dialogHeight:300px;resizable:no;center:yes;scroll:yes;status:no");
              break ; 
         case 2 : 
          	  var strId = selectValueStr(dbname+setname);
              //thecodeurl="/general/inform/emp/batch/alertmoreind.do?b_query=link&setname="+setname+"&a_code="+a_code+"&dbname="+dbname+"&viewsearch="+viewsearch+"&infor=1&strid="+strId+"&path=0";
               //return_vo= window.showModalDialog(thecodeurl, "", 
              //"dialogWidth:550px; dialogHeight:500px;resizable:no;center:yes;scroll:yes;status:no");
              //window.open(thecodeurl,"_blank",'toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=no,top=170,left=220,width=520,height=500');
              thecodeurl="/general/inform/emp/batch/alertmoreind.do?b_query=link`setname="+setname+"`a_code="+a_code+"`dbname="+dbname+"`viewsearch="+viewsearch+"`infor=1`strid="+strId+"`path=0`inforflag="+inforflag;
			  var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
     		  return_vo= window.showModalDialog(iframe_url,1, 
        	  "dialogWidth:520px; dialogHeight:500px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
			  break ; 
         case 3 : 
              thecodeurl="/general/inform/emp/batch/addind.do?b_query=link&setname="+setname+"&a_code="+a_code+"&dbname="+dbname+"&viewsearch="+viewsearch+"&infor=1&inforflag="+inforflag;
               return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:500px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
              break ; 
         case 4 : 
              thecodeurl="/general/inform/emp/batch/delind.do?b_query=link&setname="+setname+"&a_code="+a_code+"&dbname="+dbname+"&viewsearch="+viewsearch+"&infor=1&inforflag="+inforflag;
               return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:340px; dialogHeight:210px;resizable:no;center:yes;scroll:yes;status:no");
              break ; 
         case 5 : 
              thecodeurl="/general/inform/emp/batch/calculation.do?b_query=link`unit_type=2`setname="+setname+"`a_code="+a_code+"`dbname="+dbname+"`viewsearch="+viewsearch+"`infor=1`inforflag="+inforflag;
              var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
              return_vo= window.showModalDialog(iframe_url, "", 
              "dialogWidth:410px; dialogHeight:430px;resizable:no;center:yes;scroll:no;status:no");
              break ; 
         default:
         	thecodeurl="";
    } 
    if(thecodeurl.length<1){
    	return;
    }
    if(return_vo!=null){
  	 	mInformForm.action = "/general/inform/get_data_table.do?b_query=link";
		mInformForm.submit();   
  	}else{
  		return ;
  	}
}

function viewToggle(targetId){
	if (document.getElementById(targetId)){
		target = document.getElementById(targetId);
		target.style.display = "block";
	}
} 
function viewHide(targetId){
	if (document.getElementById(targetId)){
		target = document.getElementById(targetId);
		target.style.display = "none";
	}
}
function checkType(){
	var itemid = document.getElementById("itemid").value;
	var arr = itemid.split(":");
	if(arr.length!=3){
		return;	
	}
	switch(arr[1]){
		 case 'N' : 
             viewToggle('addvaluename');
			 viewToggle('reducevaluename');
			 viewToggle('reference');
			 viewHide('addvaluetext');
			 viewToggle('repvaluetext');
			 viewHide('reducevlauetext');
			 document.getElementById("refvalue").disabled =true;
             break ; 
         case 'D' : 
         	viewHide('addvaluename');
			viewHide('reducevaluename');
			viewToggle('reference');
			viewHide('addvaluetext');
			viewToggle('repvaluetext');
			viewHide('reducevlauetext');
			document.getElementById("refvalue").disabled =true;
			break ;
		case 'A' : 
         	if(arr[2]!=0){         	
				viewHide('addvaluename');
				viewHide('reducevaluename');
				viewToggle('reference');
				document.getElementById("refvalue").disabled =true;
			}else{
				viewHide('addvaluename');
				viewHide('reducevaluename');
				viewToggle('reference');
				document.getElementById("refvalue").disabled =true;
			}
			viewHide('addvaluetext');			
			viewToggle('repvaluetext');			
			viewHide('reducevlauetext');
			break ;
		default:
			viewHide('addvaluename');
			viewHide('reducevaluename');
			viewHide('reference');
			document.getElementById("refvalue").disabled =true;
	}
}
function checkView(rediovalue){
	switch(rediovalue){
		 case 1 : 
             viewToggle('addvaluetext');
			 viewHide('repvaluetext');
			 viewHide('reducevlauetext');
			 viewHide('repvalueNtext');
			 document.getElementById("refvalue").disabled =true;
             break ; 
         case 2 : 
         	var codesetid = document.getElementById("codesetid").value;
			var itemtype = document.getElementById("itemtype").value;
		
         	viewHide('addvaluetext');
         	if(itemtype=='D'){
				viewToggle('repvaluetime');
			}else if(itemtype=='N'){
				viewToggle('repvalueNtext');
				viewHide('reducevlauetext');
				viewHide('repvaluetext');
			}else{
				if(codesetid!=null&&codesetid.length>0){
					viewToggle('repvaluecode');
					//if("UM,UN,@K".indexOf(codesetid)==-1)
						//document.getElementById("b").style.display='none';
				//	else
					//	document.getElementById("a").style.display='none';
					viewHide('reducevlauetext');
					viewHide('repvaluetext');
				}else{
					viewToggle('repvaluetext');
					viewHide('reducevlauetext');
				}
			}
			
			document.getElementById("refvalue").disabled =true;
			break ;
		case 3 : 
         	viewHide('addvaluetext');
			viewHide('repvaluetext');
			viewToggle('reducevlauetext');
			viewHide('repvalueNtext');
			document.getElementById("refvalue").disabled =true;
			break ;
		case 4 : 
			document.getElementById("refvalue").disabled =false;
			break ;
		default:
			viewHide('repvalueNtext');
			viewHide('addvaluetext');
			viewToggle('repvaluetext');
			viewHide('reducevlauetext');
			document.getElementById("refvalue").disabled =true;
	}
}
function saveAlert(setname,a_code,viewsearch,dbname,infor,his,inforflag){
	var radiobutton = checkAterRadio("valuebutton");
	var updatevalue="";
	if(radiobutton==1){
		updatevalue=document.getElementById("addvalue").value;
	}else if(radiobutton==2){
		var codesetid = document.getElementById("codesetid").value;
		var itemtype = document.getElementById("itemtype").value;
		if(itemtype=='D'){
			updatevalue=document.getElementById("time.value").value;
		}else if(itemtype=='N'){
			updatevalue=document.getElementById("repvaluen").value;
		}else{
			if(codesetid!=null&&codesetid.length>0){
				updatevalue=document.getElementById("codeid.value").value;
			}else{
				updatevalue=document.getElementById("repvalue").value;
			}
		}
	}else if(radiobutton==0){
		updatevalue=document.getElementById("reducevlaue").value;
	}else if(radiobutton==3){
		updatevalue=document.getElementById("refvalue").value;
	}
	var itemid = document.getElementById("itemid").value;
	if(itemid==null||itemid==undefined||itemid.length<1){
		alert("请选择您要修改的指标!");
		return false;
	}
	var selectid = checkAterRadio("selectid");
	
	var history = "0";
	if(selectid=="1"&&his==1){
		if(document.getElementById("history").checked){
			history="1";
		}
	}
	var count = document.getElementById("count").value;
	var countall = document.getElementById("countall").value;
	var strid = document.getElementById("strid").value;
	var secount = document.getElementById("secount").value;
	
	if(selectid=="1"&&history=="1")
		count=countall;
	if(selectid=="0")
		count=secount;
	
	if(!confirm(ALERT_SELECT_ITEM+count+SELECT_ITEM_RECORDE)){
			return false;
	}
	var hashvo=new ParameterSet();
	hashvo.setValue("flag","alert");	
	hashvo.setValue("setname",setname);	
	hashvo.setValue("history",history);	
	hashvo.setValue("a_code",a_code);
	hashvo.setValue("infor",infor);
	hashvo.setValue("viewsearch",viewsearch);	
	hashvo.setValue("updatevalue",updatevalue);	
	hashvo.setValue("flagcheck",radiobutton);	
	hashvo.setValue("itemid",itemid);	
	hashvo.setValue("dbname",dbname);	
	hashvo.setValue("strid",strid);
	hashvo.setValue("selectid",selectid);	
	hashvo.setValue("inforflag",inforflag);	
	var request=new Request({method:'post',asynchronous:false,onSuccess:indCheck,functionId:'1010092006'},hashvo);
}
function indCheck(outparamters){
	var check = outparamters.getValue("check");
	var infor = outparamters.getValue("infor");
	
	//检查编制
	var scanInfo = outparamters.getValue("scanformation");
	if( (scanInfo!=null) && (scanInfo!='')){
      alert(scanInfo);
	}
	if(check!='no'){
		//19/4/4 xus 员工管理-信息浏览-高级 无法关闭
		if(getBrowseVersion()){//ie浏览器
			if(navigator.appName.indexOf("Microsoft")== -1){
				top.returnValue=check;
				top.close();
			}else{
				window.returnValue=check;
				window.close();
			}
		}else{//非IE浏览器  wangb 20180126
			parent.parent.openReturn(check);//回调父页面的方法 统一用openReturn方法  等同windowShowDialog弹窗返回值
		}
		
	}
	
	
	/*if(1==infor){
		
	}else{
			if(path==0){//表格录入 列表形式
				indBatchHandForm.target="mil_body";
				indBatchHandForm.action = "/general/inform/get_data_table.do?b_query=link";
				indBatchHandForm.submit(); 
			}else if(path==1){//表格录入
				indBatchHandForm.target="ril_body1";
				indBatchHandForm.action = "/general/inform/get_data_table.do?b_rmain=link";
				indBatchHandForm.submit(); 
			}else if(path==2){//信息录入
				indBatchHandForm.target="nil_body";
				indBatchHandForm.action = "/workbench/info/showinfodata.do?b_search=link";
				indBatchHandForm.submit(); 
			}else if(path==3){
				indBatchHandForm.target="ril_body1";
				indBatchHandForm.action = "/org/orgdata/orgdata.do?b_rmain=link";
				indBatchHandForm.submit();
			}else if(path==4){
				indBatchHandForm.target="mil_body";
				indBatchHandForm.action = "/org/orgdata/orgdata.do?b_query=link";
				indBatchHandForm.submit();
			}else if(path==5){
				indBatchHandForm.target="mil_body";
				indBatchHandForm.action = "/org/gzdatamaint/gzdatamaint.do?b_query=link";
				indBatchHandForm.submit();
				
			}
			window.close();
	}*/
}
/**********************************************
*codeid:??????????
*mytarget:??????????????????????????
***********************************************/
function openCodeDialog(code,mytarget) {
	var codeid = document.getElementById(code).value;
    var codevalue,thecodeurl,target_name,hidden_name,hiddenobj;
    if(mytarget==null)
      return;
    //????????????????????????????????????
    if(typeof mytarget!="object")
    {
      var oldInputs=document.getElementsByName(mytarget);
      mytarget=oldInputs[0];    	
    }
    target_name=mytarget.name;
    hidden_name=target_name.replace(".hzvalue",".value");
    var hiddenInputs=document.getElementsByName(hidden_name);
    if(hiddenInputs!=null)
    {
    	hiddenobj=hiddenInputs[0];
    	codevalue="";
    }
    var theArr=new Array(codeid,codevalue,mytarget,hiddenobj); 
    thecodeurl="/system/codeselect.jsp?codesetid="+codeid+"&codeitemid=ALL"; 
    var popwin= window.showModelessDialog(thecodeurl, theArr, 
        "dialogWidth:300px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
}
function saveSet(setname,a_code,viewsearch,dbname,infor,his,inforflag){
	var tablevos=document.getElementsByTagName("input");
	var itemid_arr=new Array();
	var itemvalue_arr=new Array();
	var j=0;
	for(var i=0;i<tablevos.length;i++){
		if(tablevos[i].name=='history')
			break;
		if(tablevos[i].type=="checkbox"){
	     	if(tablevos[i].checked){
	     		var itemid = tablevos[i].name;
	     		itemid_arr[j]=itemid;
	     		var itemidValue=document.getElementsByName(itemid+".value")[0].value;
	     		if(itemidValue.indexOf(",")!=-1||itemidValue.indexOf("\\")!=-1){
	     			alert("修改值中不允许出现\"\\\"和\",\"。");
	     			return;
	     		}
	     		
	     		itemvalue_arr[j] = getEncodeStr(itemidValue);
	     		j++;
	     	}
		}
    }
    if(itemid_arr.length<1){
    	alert("请选择将要修改的指标!");
    	return;
    }
    
    var selectid = "";
    if("2" == inforflag || 2 == inforflag)
    	selectid = checkTrainAterRadio("selectid");
    else
	    selectid = checkAterRadio("selectid");
	
	var history = "0";
	//if(selectid=="1"&&his==1){
		if((0<document.getElementsByName("history").length) && document.getElementsByName("history")[0].checked){
			history="1";
		} 
	//}
	//var count = document.getElementById("count").value;
	//var countall = document.getElementById("countall").value;
	var strid = document.getElementById("strid").value;
	//var secount = document.getElementById("secount").value;
	
	//if(selectid=="1"/*&&history=="1"*/)
		//count=countall;
	//if(selectid=="0")
		//count=secount;
	//var newsetname=document.getElementById("setname").value;
	var hashvo1=new ParameterSet();
	hashvo1.setValue("flag","alertmore");	
	hashvo1.setValue("selectid",selectid);	
	hashvo1.setValue("history",history);	
	hashvo1.setValue("setname",setname);
	hashvo1.setValue("dbname",dbname);
	hashvo1.setValue("itemid_arr",itemid_arr);
	hashvo1.setValue("infor",infor);
	hashvo1.setValue("strid",strid);
	var request=new Request({method:'post',asynchronous:false,onSuccess:showcount,functionId:'1010092020'},hashvo1);
	
	function showcount(outparamters){
		var isunique=outparamters.getValue("isUnique");
		if(isunique=='true'){
			alert("设置为唯一性指标不允许批量修改！");
			return;
		}
			
		var count=outparamters.getValue("count");
		if(!confirm(ALERT_SELECT_ITEM+count+SELECT_ITEM_RECORDE)){
				return;
		}
		
	    var hashvo=new ParameterSet();
		hashvo.setValue("flag","alertmore");	
		hashvo.setValue("itemid_arr",itemid_arr);
		hashvo.setValue("history",history);
		hashvo.setValue("itemvalue_arr",itemvalue_arr);	
		
		hashvo.setValue("setname",setname);	
		hashvo.setValue("infor",infor);	
		hashvo.setValue("a_code",a_code);
		hashvo.setValue("dbname",dbname);
		hashvo.setValue("viewsearch",viewsearch);	
		hashvo.setValue("strid",strid);
		hashvo.setValue("selectid",selectid);		
		var request=new Request({method:'post',asynchronous:false,onSuccess:indCheck,functionId:'1010092006'},hashvo);
	}
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
    //????????????????????????????????????	
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
    thecodeurl="/system/codeselectposinputpos.jsp?codesetid="+codeid+"&codeitemid=&isfirstnode=" + flag; 
    var popwin= window.showModelessDialog(thecodeurl, theArr, 
        "dialogWidth:300px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
}
function orgchngeinfo(obj,check){
	if(check==1){
		document.getElementById("companyid").value=obj.value;
	}else if(check==2){
		document.getElementById("depid").value=obj.value;
	}else if(check==3){
		document.getElementById("jobid").value=obj.value;
	}
}
function changepos(pretype){  
      var valueInputsun=document.getElementById("comp");
      if(valueInputsun==null)
      	return;
      var dobjun=document.getElementsByName(valueInputsun.value+".value")[0];      
      var valueInputsum=document.getElementById("dep");
      if(valueInputsum==null)
      	return;
      var dobjum=document.getElementsByName(valueInputsum.value+".value")[0];
      var valueInputskk=document.getElementById("job");
      if(valueInputskk==null)
      	return;
      var dobjkk=document.getElementsByName(valueInputskk.value+".value")[0];
      var hashvo=new ParameterSet();
      hashvo.setValue("pretype",pretype);
      hashvo.setValue("orgparentcodestart",dobjun.value);
      hashvo.setValue("deptparentcodestart",dobjum.value);
      if(dobjkk!=null){
      	hashvo.setValue("posparentcodestart",dobjkk.value);
      }else{
      	hashvo.setValue("posparentcodestart","");
      }
     if(pretype=="UM")
      {
        var request=new Request({method:'post',onSuccess:getchangeposum,functionId:'02010001012'},hashvo);
      }
      if(pretype=="@K")
      {
        var request=new Request({method:'post',onSuccess:getchangeposkk,functionId:'02010001012'},hashvo);
      }
}
/****************  FengXiBin Add Begin*****************************/
function to_hide_field()
{
	var setname = document.getElementById("setname").value;
 	var theURL="/general/inform/emp/view/hidefield.do?b_query=link&setname="+setname;
	var return_vo =window.showModalDialog(theURL,"",
		"dialogWidth=350px;dialogHeight=380px;resizable=yes;scroll:yes;center:yes;status=no;");  
	if(return_vo!=null){
		mInformForm.action = "/general/inform/get_data_table.do?b_query=link";
		mInformForm.submit();   
	}
}
function to_sort_field(setname)
{
	var setname = document.getElementById("setname").value;
 	var theURL = "/general/inform/emp/view/sortfield.do?b_query=link&setname="+setname;
	var return_vo = window.showModalDialog(theURL,"",
		"dialogWidth=320px;dialogHeight=380px;resizable=yes;scroll:yes;center:yes;status=no;");  
	if(return_vo!=null){
		mInformForm.action = "/general/inform/get_data_table.do?b_query=link";
		mInformForm.submit();
	}
}
function selectValueStr(gsetname){
	var tablename="table"+gsetname;
    var table=$(tablename);
    var dataset=table.getDataset();
    var record=dataset.getFirstRecord();
    var str="";
    if(gsetname.indexOf("A01")!=-1){
    	while(true){
	     	if(record==undefined)
	        	break;
	        
	     	if (record.getValue("select")){
				var A0100=record.getValue("A0100");
				if(A0100!=null&&A0100.length>0)
	     			str+=A0100+",";
	     	}
	     	record=record.getNextRecord();					
		}
	}else{
		while(true){
	     	if(record==undefined)
	        	break;
	     	if (record.getValue("select")){
	     		var I9999=record.getValue("I9999");
				var A0100=record.getValue("A0100");
				if(A0100!=null&&A0100.length>0&&I9999!="undefined"&&I9999>0)
	     			str+=A0100+":"+I9999+",";
	     	}	
	     	record=record.getNextRecord();				
		}
	}
	return str;
}
/**************** End *****************************/
function getchangeposum(outparamters){
      var orgvalue=outparamters.getValue("orgvalue");
      var orgvalueview=outparamters.getValue("orgviewvalue");
      var valueInputsun=document.getElementById("comp").value;
      if(orgvalue!=null&&orgvalue.length>0){
      	 var dobjunid = document.getElementsByName(valueInputsun+".value")[0];
      	 dobjunid.value=orgvalue;
      	var deptId = document.getElementById("deptId")
      	if(deptId)
      		deptId.setAttribute("parentid", orgvalue);
      }
      if(orgvalueview!=null&&orgvalueview.length>0){
      	var dobjunDesc=document.getElementsByName(valueInputsun+".hzvalue")[0];
      	dobjunDesc.value=orgvalueview;
      }
     
}
function getchangeposkk(outparamters){
      var deptparentcode=outparamters.getValue("deptparentcode");
      var orgvalueview=outparamters.getValue("orgviewvalue");
      var deptviewvalue=outparamters.getValue("deptviewvalue");
      var deptvalue=outparamters.getValue("deptvalue");
	  var orgvalue=outparamters.getValue("orgvalue");
	  var valueInputsun=document.getElementById("comp").value;
      if(orgvalue!=null&&orgvalue.length>0){
      	 var dobjunid = document.getElementsByName(valueInputsun+".value")[0];
      	 dobjunid.value=orgvalue;
      	var deptId = document.getElementById("deptId")
      	if(deptId)
      		deptId.setAttribute("parentid", orgvalue);
      }
      if(orgvalueview!=null&&orgvalueview.length>0){
      	var dobjunDesc=document.getElementsByName(valueInputsun+".hzvalue")[0];
      	dobjunDesc.value=orgvalueview;
      }
     
      var valueInputsum=document.getElementById("dep").value;
      if(deptvalue!=null&&deptvalue.length>0){
      	 var dobjumid = document.getElementsByName(valueInputsum+".value")[0];
      	 dobjumid.value=deptvalue;
      	var jobId = document.getElementById("jobId")
      	if(jobId)
      		jobId.setAttribute("parentid", deptvalue);
      }
      if(deptviewvalue!=null&&deptviewvalue.length>0){
      	 var dobjum = document.getElementsByName(valueInputsum+".hzvalue")[0];
      	 dobjum.value = deptviewvalue;
      }
}
function comCheckAll(obj,itemid){
	if('b0110,e0122,e01a1'.indexOf(itemid)==-1)
		return;
	var tablevos=document.getElementsByTagName("input");
	var j=0;
	if(obj.checked==true){
		for(var i=0;i<tablevos.length;i++){
			if(tablevos[i].type=="checkbox"){
				/*if(itemid=='b0110' && "b0110,e0122,e01a1".indexOf(tablevos[i].name)>-1)
					tablevos[i].checked=true;
				else if(itemid=='e0122' && "e0122,e01a1".indexOf(tablevos[i].name)>-1)
					tablevos[i].checked=true;
				else if(itemid=='e01a1' && "e01a1".indexOf(tablevos[i].name)>-1)
					tablevos[i].checked=true;*/
				if("b0110,e0122,e01a1".indexOf(tablevos[i].name)>-1)
					tablevos[i].checked=true;
      	 	}
   		}
    }else{
		for(var i=0;i<tablevos.length;i++){
			if(tablevos[i].type=="checkbox"){
				/*if(itemid=='b0110' && "b0110,e0122,e01a1".indexOf(tablevos[i].name)>-1)
					tablevos[i].checked=false;
				else if(itemid=='e0122' && "e0122,e01a1".indexOf(tablevos[i].name)>-1)
					tablevos[i].checked=false;
				else if(itemid=='e01a1' && "e01a1".indexOf(tablevos[i].name)>-1)
					tablevos[i].checked=false;*/
				if("b0110,e0122,e01a1".indexOf(tablevos[i].name)>-1)
					tablevos[i].checked=false;
      	 	}
   		}
    }
}
function checkTime(times){
 	var result=times.match(/^(\d{1,4})(-|\/)(\d{1,2})\2(\d{1,2})$/);
 	if(result==null) return false;
 	var d= new Date(result[1], result[3]-1, result[4]);
 	return (d.getFullYear()==result[1]&&(d.getMonth()+1)==result[3]&&d.getDate());
}
function timeCheck(obj){
	if(!checkTime(obj.value)){
		obj.value='';
	}
}
function isNum(i_value){
    re=new RegExp("[^0-9]");
    var s;
    if(s=i_value.match(re)){
        return false;
     }
    return true;
}

function checkNuN(obj){
 	if(!isNum(obj.value)){
 		obj.value='';
 	}
}
function saveAddind(setname,a_code,viewsearch,dbname,infor,countid,inforflag){
	if(!confirm(ADD_SELECT_ITEM+countid+SELECT_ITEM_RECORDE)){
		return false;
	}

	//inforflag 1:员工管理 2：外部培训
	if(typeof(inforflag) == "undefined" || inforflag == null)
		 inforflag = "1";
	
	var tablevos=document.getElementsByTagName("input");
	var itemid_arr=new Array();
	var itemvalue_arr=new Array();
	var j=0;
	for(var i=0;i<tablevos.length;i++){
		var id = tablevos[i].name;
		var arr = id.split(".");
		if(arr.length==2){
			if(arr[1]=='value'){
	     		itemid_arr[j]=arr[0];
	     		itemvalue_arr[j]=tablevos[i].value;
	     		j++;
			}
		}
    }
    if(itemid_arr.length<1){
    	alert("您没有权限批量新增记录!");
    	return false;
    }
    var hashvo=new ParameterSet();
	hashvo.setValue("flag","add");	
	hashvo.setValue("itemid_arr",itemid_arr);
	hashvo.setValue("itemvalue_arr",itemvalue_arr);	
	hashvo.setValue("setname",setname);	
	hashvo.setValue("a_code",a_code);
	hashvo.setValue("infor",infor);
	hashvo.setValue("dbname",dbname);
	hashvo.setValue("viewsearch",viewsearch);	
	hashvo.setValue("inforflag", inforflag);
	var request=new Request({method:'post',asynchronous:false,onSuccess:indCheck,functionId:'1010092006'},hashvo);
}
function viewTimes(rediovalue){
	switch(rediovalue){
		 case 0 : 
			viewHide('timeSpan');
             break ; 
         case 1 : 
         	viewHide('timeSpan');
			break ;
		case 2 : 
         	viewToggle('timeSpan');
			break ;
		default:
			viewHide('timeSpan');
	}
}
function checkRadio(){
	var buttonvalue="";
	var tablevos=document.getElementsByTagName("input");
	for(var i=0;i<tablevos.length;i++){
		if(tablevos[i].type=="radio"){
			if(tablevos[i].checked){
				buttonvalue = tablevos[i].value;
			}
		}
    }
	return buttonvalue;
}
function checkAterRadio(valuebutton){
	var buttonvalue="";
	var tablevos=document.getElementsByTagName("input");
	for(var i=0;i<tablevos.length;i++){
		if(tablevos[i].type=="radio"){
			if(tablevos[i].checked){
				if(tablevos[i].name==valuebutton)
					buttonvalue = tablevos[i].value;
			}
		}
    }
	return buttonvalue;
}

function checkTrainAterRadio(valuebutton){
	var buttonvalue="";
	var tablevos=document.getElementsByTagName("input");
	for(var i=0;i<tablevos.length;i++){
		if(tablevos[i].type=="radio"){
			if(tablevos[i].checked){
				if(tablevos[i].name==valuebutton)
					buttonvalue = tablevos[i].value;
			}
		}
    }
	return buttonvalue;
}

function saveDelind(setname,a_code,viewsearch,dbname,infor){
	var radiovalue = checkRadio();
	
	var updatevalue = "";
	var year="";
	var month = "";
	var frequency = "";
	var prive = document.getElementById("prive").value;
	if(prive!='2'){
		alert("您没有删除记录的权限!");
		return false;
	}
	
	var count = document.getElementById("count").value;
	var countall = document.getElementById("countall").value;

	if(radiovalue==1){
		if(!confirm(DELETE_SELECT_ITEM+count+SELECT_ITEM_RECORDE)){
			return false;
		}
		updatevalue = "1";
	}else if(radiovalue==2){
		updatevalue = "2";
		year = document.getElementById("year").value;
		month = document.getElementById("month").value;
		frequency = document.getElementById("frequency").value;
		if(!confirm(DEL_INFO_RECORDE)){
			return false;
		}
	}else if(radiovalue==0){
		if(!confirm(DELETE_SELECT_ITEM+countall+SELECT_ITEM_RECORDE)){
			return false;
		}
		updatevalue = "0";
	}
	var hashvo=new ParameterSet();
	hashvo.setValue("flag","del");	
	hashvo.setValue("updatevalue",updatevalue);
	hashvo.setValue("year",year);
	hashvo.setValue("month",month);		
	hashvo.setValue("frequency",frequency);	
	hashvo.setValue("setname",setname);	
	hashvo.setValue("infor",infor);	
	hashvo.setValue("dbname",dbname);	
	hashvo.setValue("a_code",a_code);
	hashvo.setValue("viewsearch",viewsearch);			
	var request=new Request({method:'post',asynchronous:false,onSuccess:indCheck,functionId:'1010092006'},hashvo);
}
function shiftlabrary(dbname,setname,a_code){
  		var tablename,table,dataset,preno,record,i9999;
	    tablename="table"+setname;
		gsetname=setname
	    table=$(tablename);
	    dataset=table.getDataset();	
	    var record=dataset.getFirstRecord();
	    if(!record)
	    	return;
	    var i9999="";
	    var a0100 = "";
	    var temp = setname.substring(3);
	    while(record){
	        if(record==undefined)
	        	break;
	        if(record.getValue("select")){
	        	a0100+=record.getValue("A0100")+",";	
	        }
	       record=record.getNextRecord();
	    }      
	    if(a0100.length<1){
	    	alert(SELECT_SHIFT_PERSON);
	    	return false;
	    }      	 
		var theURL = "/general/inform/emp/shift.do?b_query=link&dbname="+dbname;
		var return_vo = window.showModalDialog(theURL,"",
			"dialogWidth=330px;dialogHeight=180px;resizable=yes;scroll:yes;center:yes;status=no;");   		
    	if(return_vo!=null){
    		var pre = return_vo;
    		var arr = pre.split(",");
    		if(arr.length!=2)
    			return;
	    	var hashvo=new ParameterSet();
    		hashvo.setValue("dbname",dbname);
    		hashvo.setValue("pre",arr[0]);
    		hashvo.setValue("check",arr[1]);
			hashvo.setValue("a0100",a0100);
   			var request=new Request({asynchronous:false,onSuccess:shiftlabraryCheck,functionId:'1010091011'},hashvo); 
	    }
}
function shiftlabraryCheck(outparamters){
      var checkflag=outparamters.getValue("checkflag");
      var a_code=document.getElementById("a_code").value;
      if(checkflag=='ok'){
      		mInformForm.action = "/general/inform/get_data_table.do?b_query=link&flag=1&a_code="+a_code;
			mInformForm.submit();	
      }
}
/**
* type ???????????? [1.????????  2.????????  3.????????]
* query_type  ????????????,[1.????????  2.????????  3.????????]
* a_code ?????????? [UM UN]
* tablename ??????
*/
function searchInform(type,query_type,a_code,tablename){
	var thecodeurl =""; 
	var return_vo;	
	switch(query_type){ 
         case 1	: 
              thecodeurl="/general/inform/search/generalsearch.do?b_query=link&type="+type+"&a_code="+a_code+"&tablename="+tablename;
              return_vo= window.showModalDialog(thecodeurl, "", 
              	"dialogWidth:700px; dialogHeight:410px;resizable:no;center:yes;scroll:no;status:no");
              break ; 
         case 2 : 
         	   thecodeurl="/general/inform/search/gmsearch.do?b_query=link&type="+type+"&a_code="+a_code+"&tablename="+tablename;
               return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:700px; dialogHeight:430px;resizable:no;center:yes;scroll:no;status:no");
              break ; 
         case 3 : 
              thecodeurl="/general/inform/search/searchcommon.do?b_query=link&type="+type+"&flag=search&a_code="+a_code+"&tablename="+tablename;
               return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:400px; dialogHeight:280px;resizable:no;center:yes;scroll:no;status:no");
              break ; 
         default:
         	thecodeurl="";
    } 
    if(thecodeurl.length<1){
    	return;
    }
    if(return_vo!=null){
  	 	mInformForm.action="/general/inform/get_data_table.do?b_query=link&viewsearch=1";
   		mInformForm.submit();
  	}else{
  		return ;
  	}
}
/**
* check ????????????????????[1.?????????? 2.?????? 3.??????????]
* dbname  ??????
* inforkind ???? [1.?????? 2.?????? 3.?????? 
* flag ????????????????{1.??????????[1.???????? 2.????????] 
  2.??????[1.???????? 2.???????? 3.????????] 3.??????????[1.???????? 2.????????]}
*/
function printInform(check,dbname,a_code,inforkind,flag){
	var thecodeurl =""; 
	var return_vo;	
	switch(check){ 
         case 1	:     
         	var hashvo=new ParameterSet();
    		hashvo.setValue("dbname",dbname);
    		hashvo.setValue("a_code",a_code);
    		hashvo.setValue("infor",inforkind);
    		hashvo.setValue("flag",flag);
   			var request=new Request({asynchronous:false,functionId:'1010095000'},hashvo);
            var thecodeurl ="/general/muster/hmuster/searchroster.do?b_search=link`a_inforkind="+inforkind+"`result=0`dbpre="+dbname;
			var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
    		var return_vo= window.showModalDialog(iframe_url,"", 
              	"dialogWidth:1000px; dialogHeight:800px;resizable:yes;center:yes;scroll:yes;status:no");
            break ; 
         case 2 : 
	    	var tablename="table"+dbname;
	    	var table=$(tablename);
	    	var dataset=table.getDataset();	
	    	var record=dataset.getFirstRecord();
	    	if(!record)
	    		return;
	    	var a0100 = "";
	    	while(record){
	        	if(record==undefined)
	        		break;
	        	if(record.getValue("select")){
	        		a0100+=record.getValue("A0100")+",";	
	        	}
	       		record=record.getNextRecord();
	    	}
	    	if(flag=='2'){
	    		if(a0100.length<1){
	    			alert(SELECT_RECORD);
	    			return false;
	    		}
	    	}     
         	var hashvo=new ParameterSet();
    		hashvo.setValue("dbname",dbname);
    		hashvo.setValue("a_code",a_code);
    		hashvo.setValue("A0100",a0100);
    		hashvo.setValue("inforkind",inforkind);
    		hashvo.setValue("flag",flag);
   			var request=new Request({asynchronous:false,functionId:'1010095001'},hashvo);    		
            var thecodeurl ="/general/card/searchcard.do?b_query=link`home=2`inforkind="+inforkind+"`result=0`dbname="+dbname;
			var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
    		var return_vo= window.showModalDialog(iframe_url,"", 
              	"dialogWidth:1000pxpx; dialogHeight:800px;resizable:yes;center:yes;scroll:yes;status:no");
            break ; 
         case 3 : 
         	var hashvo=new ParameterSet();
    		hashvo.setValue("dbname",dbname);
    		hashvo.setValue("a_code",a_code);
    		hashvo.setValue("inforkind",inforkind);
    		hashvo.setValue("flag",flag);
   			var request=new Request({asynchronous:false,functionId:'1010095002'},hashvo);    	
            var thecodeurl ="/general/muster/hmuster/searchHroster.do?b_search=link`nFlag=3`a_inforkind="+inforkind+"`res=0`result=0`dbpre="+dbname;
			//var thecodeurl ="/general/muster/hmuster/select_muster_name.do?b_search=link`nFlag=3`a_inforkind="+inforkind+"`dbpre="+dbname;
			var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
    		var return_vo= window.showModalDialog(iframe_url,"", 
              	"dialogWidth:1000px; dialogHeight:800px;resizable:yes;center:yes;scroll:yes;status:no");
            break ; 
         default:
         	thecodeurl="";
    } 
}
function if_main_sort(){
		return ( confirm(GENERAL_INFO_VIEW_MAIN_SORT) );	
}
function if_subset_sort(){
	return ( confirm(GENERAL_INFO_VIEW_MAIN_SORT) );	
}
function searchOk(viewsearch){
   mInformForm.action="/general/inform/get_data_table.do?b_query=link&viewsearch="+viewsearch;
   mInformForm.submit();  	
}
function singleAudit(infor,dbname){
  	var thecodeurl="/general/inform/informcheck/view_check.do?b_query=link&infor="+infor+"&dbname="+dbname;
    var return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:400px; dialogHeight:400px;resizable:no;center:yes;scroll:no;status:no");  	
}
/**
* type ???????????? [1.????????  2.????????  3.????????]
* id   ????????id
* a_code ?????????? [UM UN]
* tablename ??????
*/
function searchGeneral(type,id,a_code,tablename){
	var hashvo=new ParameterSet();
	hashvo.setValue("id",id);	
	hashvo.setValue("a_code",a_code);
	hashvo.setValue("tablename",tablename);
	hashvo.setValue("type",type);
	hashvo.setValue("flag","search");				
	var request=new Request({method:'post',asynchronous:false,onSuccess:checkSearchGeneral,functionId:'3020110076'},hashvo);
}
function checkSearchGeneral(outparamters){
	var check = outparamters.getValue("check");
	if(check=='ok'){
		mInformForm.action="/general/inform/get_data_table.do?b_query=link&viewsearch=1";
   		mInformForm.submit();
  	}
}
function check_sort_type()
{
		return ( confirm('????????????????????????????????????????????????????????y/n????') );	
}	
/****************  FengXiBin Add Begin*****************************/
function to_sort_main_info(){
		var thecodeurl ="/gz/sort/sorting.do?b_query=link&flag=r1"; 
    	var return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:510px; dialogHeight:420px;resizable:no;center:yes;scroll:yes;status:no");
		if(return_vo!=null){
			var result = if_main_sort();
			if(result==true)
			{
				//alert(return_vo);
				return_vo=return_vo!='not'?return_vo:"";
				document.getElementById("sort_str").value=return_vo;
				mInformForm.action="/general/inform/get_data_table.do?b_query=link";
	  	    	mInformForm.submit();
			}	
		}
}
function to_sort_subset_info(setname, inforflag){
	var thecodeurl = "/gz/sort/sorting.do?b_query=link&flag=";
	if(typeof(inforflag) == "undefined" || inforflag == null || inforflag != "2")
		thecodeurl = thecodeurl + "r1";
	else{
		if(setname.length == 6)
			setname = setname.substring(3,6);
		thecodeurl = thecodeurl + "outer_train&setid=" + setname;
	}
	var return_vo= window.showModalDialog(thecodeurl, "", 
          "dialogWidth:510px; dialogHeight:420px;resizable:no;center:yes;scroll:yes;status:no");
	if(return_vo!=null){
		var result = if_subset_sort();
		if(result!=true){
			var tablename,table,dataset,preno,record,i9999;
			tablename="table"+setname;
			gsetname=setname
			table=$(tablename);
			dataset=table.getDataset();	
			record=dataset.getCurrent();	
			if(!record)
			    return;
			var a0100=record.getValue("A0100");	
			document.getElementById("a0100").value=a0100;
			document.getElementById("sort_record_scope").value="selected";
		}else{
			document.getElementById("sort_record_scope").value="all";
		}
		document.getElementById("sort_str").value=return_vo;
		mInformForm.action="/general/inform/get_data_table.do?b_query=link";
    	mInformForm.submit();		
	}
}

 function to_multimedia_tree(setname,dbname)
 {
 	var tablename,table,dataset,preno,record,i9999;
    tablename="table"+setname;
	gsetname=setname
    table=$(tablename);
    dataset=table.getDataset();	
    record=dataset.getCurrent();	
    if(!record)
    return;
    var a0100=record.getValue("A0100");	
 	var thecodeurl ="/general/inform/emp/view/multimedia_tree.do?b_query=link&kind=6&userbase="+dbname+"&a0100="+a0100+"&multimediaflag="; 
 	var return_vo= window.showModalDialog(thecodeurl, "", 
	    "dialogWidth:620px; dialogHeight:460px;resizable:yes;center:yes;scroll:yes;status:no");
	while(return_vo!=null)
	{
		var thecodeurl ="/general/inform/emp/view/multimedia_tree.do?b_query=link&kind=6&userbase="+dbname+"&a0100="+a0100+"&multimediaflag="; 
 		var return_vo= window.showModalDialog(thecodeurl, "", 
	    "dialogWidth:620px; dialogHeight:460px;resizable:yes;center:yes;scroll:yes;status:no");
	}
 }
function to_move_record(setname){
  		var tablename,table,dataset,preno,record,i9999;
	    tablename="table"+setname;
		gsetname=setname
	    table=$(tablename);
	    dataset=table.getDataset();	
	    record=dataset.getCurrent();
	    var dbstr = document.getElementById("dbstr").value;	
	    if(!record)
	    	return;
	    var temp = setname.substring(3);
	    var select_record = "";
	    var a0100 = "";
	    var a0101 = "";
	    var b0110 = "";
	    var e0122 = "";
	    var e01a1 = "";
	    if(temp=="a01" || temp=="A01"){
	    	 var a0000=record.getValue("A0000");
	    	 a0100=record.getValue("A0100");
	    	 a0101=record.getValue("A0101");
	    	 b0110=record.getValue("B0110");
	    	 e0122=record.getValue("E0122");
	    	 e01a1=record.getValue("E01A1");	
	    	 select_record = a0000;
	    }else{
	    	 var i9999=record.getValue("i9999");	
	    	 a0100=record.getValue("A0100");	
	    	 a0101=record.getValue("A0101");
	    	 b0110=record.getValue("B0110");
	    	 e0122=record.getValue("E0122");
	    	 e01a1=record.getValue("E01A1");
	    	 select_record = i9999;
	    }
	    var select_record_num=table.activeRowIndex;
		var thecodeurl ="/general/inform/emp/set_move_record_num.jsp?a0101="+$URL.encode(getEncodeStr(a0101))+"&b0110=";
		thecodeurl+=b0110+"&e0122="+e0122+"&e01a1="+e01a1+"&select_record="+select_record+"&dbstr="+$URL.encode(getEncodeStr(dbstr)); 
    	var return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:400px; dialogHeight:250px;resizable:no;center:yes;scroll:yes;status:no");
        if(return_vo==null)	 
			return_vo="";    		
    	if(return_vo!="")	   
	    {
	    	var hashvo=new ParameterSet();
	    	hashvo.setValue("table",setname);
    		hashvo.setValue("setname",temp);
			hashvo.setValue("move_to_num",return_vo);  
			hashvo.setValue("select_record",select_record);  
			hashvo.setValue("select_record_num",select_record_num);
			hashvo.setValue("a0100",a0100);
   			var request=new Request({asynchronous:false,functionId:'1010091000'},hashvo); 
   			mInformForm.action = "/general/inform/get_data_table.do?b_query=link";
			mInformForm.submit();
	    }
}
function viewRecord(viewdata){
   	document.getElementById("viewdata").value=viewdata;
   	mInformForm.action = "/general/inform/get_data_table.do?b_query=link";
	mInformForm.submit();	
}
function diplaypicture(setname){
	var tablename,table,dataset,preno,record,i9999;
    tablename="table"+setname;
	gsetname=setname
    table=$(tablename);
    dataset=table.getDataset();	
    record=dataset.getCurrent();	
    if(!record)
    	return;
    var a0100=record.getValue("A0100");	
    var a0101=record.getValue("A0101");	
    document.getElementById("realName").innerHTML=a0101;
    var photo_w = 85*3/2;
    var photo_h = 120*3/2;      
    parent.mil_menu.document.all.photo_state.value="1";
    document.all('ole').style.height=parseInt(photo_h);
    document.all('ole').style.width=parseInt(photo_w);
	if (document.getElementById("movDiv")){
		target = document.getElementById("movDiv");
		if(target.style.display == "block"){
			target.style.display="none";
		}else{
			target.style.display = "block"	
			document.ole.location.href="/general/inform/emp/view/displaypicture.do?b_query=link&a0100="+a0100+"&a0101="+$URL.encode(getEncodeStr(a0101));
		}
	} 
}

function maxSize(){
	var photo_w = document.all('ole').offsetWidth;
    var photo_h = document.all('ole').offsetHeight; 
	
	photo_w=parseInt(photo_w)+25*(85/120);
	photo_h=parseInt(photo_h)+25;
	if(photo_h<600){
		
		document.all('ole').style.height=parseInt(photo_h);
    	document.all('ole').style.width=parseInt(photo_w);
    	resizeImage();
    }
}
function minSize(){
	var photo_w = document.all('ole').offsetWidth;
    var photo_h = document.all('ole').offsetHeight; 
	
	photo_w=parseInt(photo_w)-25*(85/120);
	photo_h=parseInt(photo_h)-25;
	if(photo_w>85){	
		document.all('ole').style.height=parseInt(photo_h);
    	document.all('ole').style.width=parseInt(photo_w);
    	resizeImage();
    }
}
function resizeImage()
{
	var imagearr =  window.frames['ole'].document.getElementsByTagName("img") ;
	if(window.frames['ole'].document.body!=null)//fzg修改针对照片切换点击放大或者缩小按钮有js错误
	{
		var clientWidth = window.frames['ole'].document.body.clientWidth; 
		var clientHeight = window.frames['ole'].document.body.clientHeight; 
		for(var i=0;i<imagearr.length;i++)
		{
			imagearr[i].width = clientWidth;
			imagearr[i].height = clientHeight;
   		}
    }
}
function setPhoto(){
	if(window.frames['ole'].document.getElementById("picturefile")!=null)
		window.frames['ole'].document.getElementById("picturefile").click();
}
function edit(setname,a_code,dbname)
{	
	var tablename,table,dataset,record;
	tablename="table"+setname;
    table=$(tablename);
    dataset=table.getDataset();	
    record=dataset.getCurrent();
    if(record==null)
    	return;
    var a0100=record.getValue("A0100");	
    
    var theurl='/general/inform/emp_add.do?b_query=link`fieldset='+setname+'`a0100='+a0100+'`dbname='+dbname;
    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
   	var return_vo= window.showModalDialog(iframe_url, 'empadd_win', 
      				"dialogWidth:900px; dialogHeight:800px;resizable:no;center:yes;scroll:yes;status:no");		
    if(!return_vo)
	    return false;	   
    if(return_vo.flag=="true")
	{  
		var hashvo=new ParameterSet();
		if(setname.indexOf("A01")==-1)
   	    {
			var I9999=record.getValue("I9999");
    		hashvo.setValue("I9999",I9999);
    	}		
		hashvo.setValue("fieldset",setname);	
		hashvo.setValue("a0100",a0100);		
		hashvo.setValue("dbname",dbname);	
		var request=new Request({method:'post',asynchronous:false,onSuccess:refreshRecord,functionId:'1010090016'},hashvo);
	}   
}
function refreshRecord(outparamters)
{
	var tablename,table,dataset,preno,bmainset,record;
	var setname=outparamters.getValue("setname");	
	tablename="table"+setname;
    table=$(tablename);
    dataset=table.getDataset(); 
    record=dataset.getCurrent(); 
    
    var fielditem = outparamters.getValue("fielditem");
	if(fielditem!=null&&fielditem.length>0)
	{
		var itemsarr = fielditem.split(",");
		
		for(var i=0;i<itemsarr.length;i++)
		{
			if(itemsarr[i]!=null&&itemsarr[i].length>0)	
			{
				record.setValue(itemsarr[i],outparamters.getValue(itemsarr[i]));
			}			
		}
	}
	record.setState("modify");   
}
function editSubSet(setname,a0100,dbname)
{
	var tablename,table,dataset,record;
	tablename="table"+setname;
    table=$(tablename);
    dataset=table.getDataset();	
    record=dataset.getCurrent();
    var i9999=record.getValue("i9999");	
    if(i9999=='')
    	return;
    	
    var theurl,iframe_url,returnVo;
	var subset = setname.substring(setname.length-3);
	if(subset=='A00')
	{	
    	theurl="/general/inform/emp/view/opermultimedia.do?b_edit=link`itemid="+a0100+'`subset='+subset+'`infor=1`i9999='+i9999+'`dbpre='+setname.substring(0,3);
   		iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
   		returnVo= window.showModalDialog(iframe_url, 'orgsubset_win', 
	      				"dialogWidth:500px; dialogHeight:200px;resizable:no;center:yes;scroll:yes;status:no");
    }else
    {	    	
    	
      theurl="/general/inform/empsubset_add.do?b_query=link`a0100="+a0100+'`subset='+setname+'`i9999='+i9999+'`dbname='+dbname+"`flag=train";//chenxg 外部培训特殊处理
	  iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
	  returnVo= window.showModalDialog(iframe_url, 'empsubset_win', 
	      				"dialogWidth:600px; dialogHeight:500px;resizable:no;center:yes;scroll:yes;status:no");
	}
	if(!returnVo)
		return false;	   
	if(returnVo.flag=="true")
	{    
		if(parent.parent.ril_body2!=null)
			parent.parent.ril_body2.location="/general/inform/get_data_table.do?b_menu=link&a0100="+a0100+"&dbname="+dbname;
		else
			parent.parent.a.location="/general/inform/get_data_table.do?b_menu=link&a0100="+a0100+"&dbname="+dbname;		
	}    
}
function addSubSet(setname,a0100,dbname)
{
	var theurl,iframe_url,returnVo;
	var subset = setname.substring(setname.length-3);
	if(subset=='A00')
	{
		theurl="/general/inform/emp/view/opermultimedia.do?b_add2=link`curri9999=0`itemid="+a0100+'`subset='+subset+'`infor=1';
   		iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
   		returnVo= window.showModalDialog(iframe_url, 'orgsubset_win', 
	      				"dialogWidth:500px; dialogHeight:200px;resizable:no;center:yes;scroll:yes;status:no");
	}else
	{
      theurl="/general/inform/empsubset_add.do?b_query=link`a0100="+a0100+'`subset='+setname+'`i9999=0'+"`dbname="+dbname+"`flag=train";//chenxg 外部培训特殊处理
	  iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
	  returnVo= window.showModalDialog(iframe_url, 'empsubset_win', 
	      				"dialogWidth:700px; dialogHeight:500px;resizable:no;center:yes;scroll:yes;status:no");
	}
	if(!returnVo)
	return false;	   
	if(returnVo.flag=="true")
	{   
		if(parent.parent.ril_body2!=null)
			parent.parent.ril_body2.location="/general/inform/get_data_table.do?b_menu=link&a0100="+a0100+"&dbname="+dbname;
		else
			parent.parent.a.location="/general/inform/get_data_table.do?b_menu=link&a0100="+a0100+"&dbname="+dbname;
	}
}

function insertSubSet(setname,a0100,dbname)
{
	var tablename,table,dataset,preno,record,i9999;
	tablename="table"+setname;
	gsetname=setname
	i9999="";
    table=$(tablename);
    dataset=table.getDataset();	
    record=dataset.getCurrent();
    if(!record)
		return;
	i9999=record.getValue("I9999");
	
	var theurl,iframe_url,returnVo;
	var subset = setname.substring(setname.length-3);
	if(subset=='A00')
	{
     	 theurl="/general/inform/emp/view/opermultimedia.do?b_add2=link`itemid="+a0100+'`subset='+setname+'`infor=1`curri9999='+i9999+"`dbname"+dbname;
   		 iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
   		 returnVo= window.showModalDialog(iframe_url, 'orgsubset_win', 
	      				"dialogWidth:500px; dialogHeight:200px;resizable:no;center:yes;scroll:yes;status:no");
    }else
    {
     	theurl="/general/inform/empsubset_add.do?b_query=link`a0100="+a0100+'`subset='+setname+'`i9999=0`curri9999='+i9999+"`dbname="+dbname+"`flag=train";//chenxg 外部培训特殊处理
	  	iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
	 	returnVo= window.showModalDialog(iframe_url, 'empsubset_win', 
	      				"dialogWidth:700px; dialogHeight:500px;resizable:no;center:yes;scroll:yes;status:no");
	}
	if(!returnVo)
	return false;	   
	if(returnVo.flag=="true")
	{   
		if(parent.parent.ril_body2!=null)
			parent.parent.ril_body2.location="/general/inform/get_data_table.do?b_menu=link&a0100="+a0100+"&dbname="+dbname;
		else
			parent.parent.a.location="/general/inform/get_data_table.do?b_menu=link&a0100="+a0100+"&dbname="+dbname;
	}
}
function selectMedia(dbname,b0110,i9999,infor,isvisable,cell){
	var thecodeurl="/org/orgdata/media_tree.jsp?b0110="+b0110+"`dbname="+dbname+"`infor="+infor+"`isvisable"+isvisable; 
	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
	var returnVo= window.showModalDialog(iframe_url,'orgsubset_win', 
	      				"dialogWidth:300px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
    if(returnVo!=null){
    	var flag = returnVo.uid;
    	var flagname = returnVo.text;
    	var valuestr = "A"+flag+"`A"+b0110+"`A"+i9999;
    	
    	var sqlstr = "update ";
    	if(infor=="1")
    		sqlstr+=dbname+"A00  set Flag=? where A0100=? and I9999=?";
    	else if(infor=="2")
    		sqlstr+=" B00 set Flag=? where B0110=? and I9999=?";
    	else if(infor=="3")
    		sqlstr+=" K00 set Flag=? where E01A1=? and I9999=?";
    	
    	var hashvo=new ParameterSet();
    	hashvo.setValue("sqlstr",sqlstr);	
		hashvo.setValue("valuestr",valuestr);		
		var request=new Request({method:'post',asynchronous:false,functionId:'1010090019'},hashvo);
		var cellstr = "<div style=\"cursor:hand;color:#0033FF\" ";
		cellstr+=" onclick=\"selectMedia('"+dbname+"','"+b0110+"','"+i9999+"','"+infor+"','"+isvisable+"',cell)\" ";
		cellstr+=">"+flagname+"</div>";
		cell.innerHTML=cellstr;
    }
}
function downLoadOle(setname,b0110,i9999,infor){
	var sqlstr = "select Title,Ole,ext from "+setname+" where ";
	if(infor=="1")
		sqlstr+="A0100='"+b0110+"'";
	else if(infor=="2")
		sqlstr+="B0110='"+b0110+"'";
	else
		sqlstr+="E01A1='"+b0110+"'";
	sqlstr+=" and I9999='"+i9999+"'";
	var hashvo=new ParameterSet();
	hashvo.setValue("sqlstr",getEncodeStr(sqlstr));	
	hashvo.setValue("titleid","Title");	
	hashvo.setValue("ext","ext");	
	hashvo.setValue("ole","Ole");		
	var request=new Request({method:'post',asynchronous:false,onSuccess:showDownOle,functionId:'0401000037'},hashvo);
}
function showDownOle(outparamters){
	var outName=outparamters.getValue("outName");
	if(outName!=null&&outName.length>0){
		if(outName=="no"){
			alert("还没有已经上传的文件!");
			return false;
		}else{
			window.location.target="_blank";
			window.location.href="/servlet/vfsservlet?fromjavafolder=true&fileid="+outName;
		}
	}	
}
function uploadMedia(dbname,b0110,i9999,infor){
	var thecodeurl="/org/orgdata/orgdata.do?b_load=link`b0110="+b0110+"`dbname="+dbname+"`infor="+infor+"`i9999="+i9999; 
	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
	var returnVo= window.showModalDialog(iframe_url,'orgsubset_win', 
	      				"dialogWidth:500px; dialogHeight:200px;resizable:no;center:yes;scroll:yes;status:no");
}
/**************** End *****************************/  