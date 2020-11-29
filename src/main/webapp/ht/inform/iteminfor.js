var gsetname;
 
//判断表格里面是否包含某个字段（fzg加）
function isExistField(dataset,fieldName)
{
	var field=dataset.getField(fieldName);
	if(field!=null)
		return true;
	 return false;
}
function setAppendRecord(outparamters){
	var tablename,table,dataset,preno,bmainset,record;
	tablename="table"+gsetname;
	table=$(tablename);
    dataset=table.getDataset(); 
    record=dataset.getCurrent();  
	
	var I9999=record.getValue("I9999");
	if(I9999==""||I9999=="0"){
		record.setValue("I9999",outparamters.getValue("I9999"));
	}else{
		var temp,blast;
		blast=false;
        record=dataset.getCurrent();			    
	   	while(true){
	        record=record.getNextRecord();
	        if(record==undefined){
	        	blast=true;
	        	break;
	        }
	        if(isExistField(dataset,'A0100')){
	        	temp=record.getValue("A0100");
	        	if(temp!=A0100)
					break;
			}
	    }   
	    var A0100="",A0101="",B0110="",E0122="",E01A1="";
	    if(isExistField(dataset,'A0100')){
    	    A0100=record.getValue("A0100");
    	    A0100=A0100!=null&&A0100!='undefined'?A0100:"";
    	}    	    	
        if(isExistField(dataset,'A0101')){
    	    A0101=record.getValue("A0101");
    	    A0101=A0101!=null&&A0101!='undefined'?A0101:"";
    	}  	       			
		if(isExistField(dataset,'B0110')){
    	    B0110=record.getValue("B0110");
    	    B0110=B0110!=null&&B0110!='undefined'?B0110:"";
    	}
    	if(isExistField(dataset,'E0122')){
    	    E0122=record.getValue("E0122");
    	    E0122=E0122!=null&&E0122!='undefined'?E0122:"";
    	} 
		if(isExistField(dataset,'E01A1')){
    	    E01A1=record.getValue("E01A1");
    	    E01A1=E01A1!=null&&E01A1!='undefined'?E01A1:"";
    	} 			
		dataset.insertRecord("end");
        record=dataset.getCurrent();
    	record.setValue("I9999",outparamters.getValue("I9999")); 
    	if(isExistField(dataset,'A0100')){
    	    record.setValue("A0100",A0100); 
    	}    	    	
        if(isExistField(dataset,'A0101')){
    	    record.setValue("A0101",A0101); 
    	}  	       			
        if(isExistField(dataset,'B0110')){
    	    record.setValue("B0110",B0110); 
    	} 
    	if(isExistField(dataset,'E0122')){
    	   record.setValue("E0122",E0122); 
    	} 
		if(isExistField(dataset,'E01A1')){
    	   record.setValue("E01A1",E01A1); 
    	} 					        	
	}
	record.setState("modify");
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

function append(setname,a_code){
	var tablename,table,dataset,record,i9999;
	tablename="table"+setname;
	gsetname=setname;
	i9999="";
    table=$(tablename);
    dataset=table.getDataset();	
    record=dataset.getCurrent();
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
function append2(a0100,itemtable,htFlag,dbname,ctflag)
{
	 var theurl="/ht/inform/data_table.do?b_add=link`a0100="+a0100+'`subset='+itemtable+'`dbname='+dbname+'`htFlag='+htFlag+'`i9999=0';
	 var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
	 var returnVo= window.showModalDialog(iframe_url, 'htset_win', 
	      				"dialogWidth:700px; dialogHeight:500px;resizable:no;center:yes;scroll:yes;status:no");
	if(!returnVo)
	return false;	   
	if(returnVo.flag=="true")
	{    
		parent.parent.ril_body2.location='/ht/inform/data_table.do?b_menu=link&a0100='+a0100+'&dbname='+dbname;
	}   				
}
function edit(a0100,itemtable,htFlag,dbname,ctflag,i9999)
{
	 var theurl="/ht/inform/data_table.do?b_add=link`a0100="+a0100+'`subset='+itemtable+'`dbname='+dbname+'`htFlag='+htFlag+'`i9999='+i9999;
	 var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
	 var returnVo= window.showModalDialog(iframe_url, 'htset_win', 
	      				"dialogWidth:700px; dialogHeight:500px;resizable:no;center:yes;scroll:yes;status:no");
	if(!returnVo)
	return false;	   
	if(returnVo.flag=="true")
	{    
		parent.parent.ril_body2.location='/ht/inform/data_table.do?b_menu=link&a0100='+a0100+'&dbname='+dbname;
	}   				
}
/**************** End *****************************/  