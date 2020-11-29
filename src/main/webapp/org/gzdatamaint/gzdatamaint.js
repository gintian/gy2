function viewRecord(viewdata,infor,gzflag,a_code){
   document.getElementById("viewdata").value=viewdata;
   gzDataMaintForm.action="/org/gzdatamaint/gzdatamaint.do?b_query=link&infor="+infor+"&gzflag="+gzflag+"&a_code="+a_code;
   gzDataMaintForm.submit();  	
}
function setAppendRecord(gsetname,loca){
	var tablename,table,dataset,preno,bmainset,record;
	tablename="table"+gsetname;
    table=$(tablename);
    dataset=table.getDataset(); 
    record=dataset.getCurrent();  
    
    dataset.insertRecord(loca);
}
function insert(setname,a_code,flag){
	var tablename,table,dataset,preno,record;
	var infor = document.getElementById("infor").value;
	tablename="table"+setname;
	gsetname=setname
	var itemid="";
	var i9999="";
    table=$(tablename);
    dataset=table.getDataset();	
    record=dataset.getCurrent();
   
	if(!record){
		if(flag=="insert"){
			alert(SELECT_ONE_RECORD);
			return false;
		}
		a_code = a_code.replace("UN","");
		a_code = a_code.replace("UM","");
		a_code = a_code.replace("@K","");
		itemid=a_code;
	}else{
		if(infor=="3"){
    		itemid=record.getValue("E01A1");
    	}else{
    		itemid=record.getValue("B0110");
		}
		i9999=record.getValue("I9999");
	}
	var hashvo=new ParameterSet();
	hashvo.setValue("setname",setname);
	hashvo.setValue("type",flag);		
	hashvo.setValue("itemid",itemid);
	if(setname!="B01"||setname!="K01"||setname!="b01"||setname!="k01"){
		hashvo.setValue("I9999",i9999);
	}
	if(flag=="insert"){
		var request=new Request({method:'post',asynchronous:false,onSuccess:setInsertRecord,functionId:'1602010236'},hashvo);
	}else
		var request=new Request({method:'post',asynchronous:false,onSuccess:setAppendRecord,functionId:'1602010236'},hashvo);
}

function setAppendRecord(outparamters){
	var tablename,table,dataset,preno,bmainset,record;
	var gsetname = outparamters.getValue("setname");
	var infor = document.getElementById("infor").value;
	if(gsetname==null||gsetname.length<1)
		return;
	tablename="table"+gsetname;
    table=$(tablename);
    dataset=table.getDataset(); 
    record=dataset.getCurrent();
    if(!record){
    	addChange();
    }else{  
		if(gsetname.indexOf("B01")!=-1&&gsetname.indexOf("K01")==-1
			&&gsetname.indexOf("b01")!=-1&&gsetname.indexOf("k01")==-1){
        	dataset.insertRecord("end");	
	    	record=dataset.getCurrent();
       		record.setValue("B0110",outparamters.getValue("B0110")); 
       		if(infor=="3"){
	    		record.setValue("E0122",outparamters.getValue("E0122"));
	    		record.setValue("E01A1",outparamters.getValue("E01A1"));      
	    	}     
		}else{
			var I9999=record.getValue("I9999");
			var B0110=outparamters.getValue("B0110");
			var E0122,E01A1;	
			if(infor=="3"){
				E0122=outparamters.getValue("E0122");
				E01A1=outparamters.getValue("E01A1");
			}
			var itemid="";
			if(infor=="3"){
				itemid=E01A1;
			}else{
				itemid=B0110;
			}
			if(I9999==""||I9999=="0"){
				record.setValue("I9999",outparamters.getValue("I9999"));
			}else{
				var temp,blast;
				blast=false;
           		record=dataset.getCurrent();	
           		if(!record){
           			addChange();
           		}else{
	        		while(true){
	        			record=record.getNextRecord();
	        			if(record==undefined){
	        		 		blast=true;
	        			 	break;
	        			}
	        			if(infor=="3"){
	        				temp=record.getValue("E01A1");
	        			}else{
	        				temp=record.getValue("B0110");
	        			}
	        			if(temp!=itemid)
							break;
	        		} 
	            
	        		if(!blast){   
	        			dataset.setCurrent(record); 
           	   	 		dataset.insertRecord("before");
           			}else{
           	    		dataset.insertRecord("end");
           			}
           			record=dataset.getCurrent();
    	    		record.setValue("I9999",outparamters.getValue("I9999")); 
        			record.setValue("B0110",B0110); 
        			if(infor=="3"){
	        			record.setValue("E0122",E0122);	
	        			record.setValue("E01A1",E01A1);	
	        		}			        	
				}
			}
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
}
function setInsertRecord(outparamters){
	var tablename,table,dataset,preno,bmainset;
	var infor = document.getElementById("infor").value;
	tablename="table"+gsetname;
    table=$(tablename);
    dataset=table.getDataset();
    record=dataset.getCurrent();	
	if(gsetname.indexOf("B01")!=-1||gsetname.indexOf("K01")!=-1
		||gsetname.indexOf("b01")!=-1||gsetname.indexOf("k01")!=-1){
        dataset.insertRecord("end");	
	    record=dataset.getCurrent();
       	record.setValue("B0110",outparamters.getValue("B0110")); 
       	if(infor=="3"){
	    	record.setValue("E0122",outparamters.getValue("E0122"));
	    	record.setValue("E01A1",outparamters.getValue("E01A1"));      
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
	}else{
		var I9999=record.getValue("I9999");
		var B0110=record.getValue("B0110");
		var E0122,E01A1;
		if(infor=="3"){
			E0122=record.getValue("E0122");
			E01A1=record.getValue("E01A1");
		}
		var itemid="";
		if(infor=="3"){
			itemid=E01A1;
		}else{
			itemid=B0110;
		}
		if(I9999==""||I9999=="0"){
			record.setValue("I9999",outparamters.getValue("I9999"));
			var fielditem = outparamters.getValue("fielditem");
			if(fielditem!=null&&fielditem.length>0){
				var itemsarr = fielditem.split(",");
				for(var i=0;i<itemsarr.length;i++){
					if(itemsarr[i]!=null&&itemsarr[i].length>0){
						record.setValue(itemsarr[i],outparamters.getValue(itemsarr[i]));
					}
				} 
			}         
		}else{
			var temp;
           	dataset.insertRecord("before");
           	record=dataset.getCurrent();
    	    record.setValue("I9999",outparamters.getValue("I9999")); 
        	record.setValue("B0110",B0110); 
       		if(infor=="3"){
	    		record.setValue("E0122",E0122);
	    		record.setValue("E01A1",E01A1);      
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
 			//第一个单独处理 zhaoxg add 2015-4-9
	        record=dataset.getFirstRecord(); 
         	if(infor=="3"){
        		temp=record.getValue("E01A1");
        	}else{
        		temp=record.getValue("B0110");
        	}
        	if(temp==itemid&&record.getValue("I9999")>outparamters.getValue("I9999")){//第一个不考虑等于，因为如果选择第一个来插入，刚插入的和之前选中的是相同的，这时候只考虑自增第二列后面会处理 zhaoxg add 2015-4-9   
        		record.setValue("I9999",record.getValue("I9999")+1);	
        	}
        	//end
	        while(true){
	        	record=record.getNextRecord();
	        	if(record==undefined)
	        		break;
	        	if(record==dataset.getCurrent()){
	        		continue;
	        	}
	        	if(infor=="3"){
	        		temp=record.getValue("E01A1");
	        	}else{
	        		temp=record.getValue("B0110");
	        	}
	        	if(temp==itemid&&record.getValue("I9999")>=outparamters.getValue("I9999")){//除了第一个其他的要考虑等于的情况，因为刚插入的和之前选中的是相同的 zhaoxg add 2015-4-9      
	        		record.setValue("I9999",record.getValue("I9999")+1);	
	        	}else
	        		continue;
	        }          	    
		}
	}
}
function setSort(infor,gzflag){
	var target_url="/org/gzdatamaint/gzdatamaint.do?b_addsubclass=link&tagname=0&tempflag=1&infor="+infor+"&gzflag="+gzflag;
	if(isIE6() ){
			var return_vo= window.showModalDialog(target_url,1, 
	        "dialogWidth:590px; dialogHeight:410px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
	}else{
		var return_vo= window.showModalDialog(target_url,1, 
	        "dialogWidth:540px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
	}

	if(return_vo!=null){
		changeflag();
   	}
}
function to_hide_field(setname){
 	var theURL="/general/inform/emp/view/hidefield.do?b_query=link&setname="+setname;
 	if(isIE6()){
 		var return_vo =window.showModalDialog(theURL,"",
		"dialogWidth=360px;dialogHeight=480px;resizable=yes;scroll:yes;center:yes;status=no;");  
 	}else{
 		var return_vo =window.showModalDialog(theURL,"",
		"dialogWidth=350px;dialogHeight=380px;resizable=yes;scroll:yes;center:yes;status=no;");  
 	}
	if(return_vo!=null){
		changeflag();
	}
}
function to_sort_subset_info(setname){
	var thecodeurl ="/gz/sort/sorting.do?b_query=link&flag=3&setname="+setname; 
	var return_vo= window.showModalDialog(thecodeurl, "", 
          "dialogWidth:570px; dialogHeight:420px;resizable:no;center:yes;scroll:yes;status:no");
	if(return_vo!=null){
		return_vo=return_vo!='not'?return_vo:"";
		document.getElementById("sort_str").value=return_vo;
		changeflag();		
	}
}
function to_sort_field(setname){
 	var theURL = "/general/inform/emp/view/sortfield.do?b_query=link&setname="+setname;
	var return_vo = window.showModalDialog(theURL,"",
		"dialogWidth=320px;dialogHeight=380px;resizable=yes;scroll:yes;center:yes;status=no;");  
	if(return_vo!=null){
		changeflag();
	}
}
function addChange(){
	var a_code=document.getElementById("a_code").value;
	var infor=document.getElementById("infor").value;
	var gzflag=document.getElementById("gzflag").value;
   	gzDataMaintForm.action="/org/gzdatamaint/gzdatamaint.do?b_query=link&checkadd=1&infor="+infor+"&gzflag="+gzflag+"&a_code="+a_code;
    gzDataMaintForm.submit(); 			
}
/**
 * 判断当前浏览器是否为ie6
 * 返回boolean 可直接用于判断 
 * @returns {Boolean}
 */
function isIE6() 
{ 
	if(navigator.appName == "Microsoft Internet Explorer") 
	{ 
		if(navigator.userAgent.indexOf("MSIE 6.0")>0) 
		{ 
			return true;
		}else{
			return false;
		}
	}else{
		return false;
	}
}
function batchHand(flag,a_code,infor,gzflag){
	var thecodeurl =""; 
	var return_vo;	
	var setname = document.getElementById("fieldsetid").value;
	if(a_code==""){
		alert("不能选择根节点！");
		return;
	}
	switch(flag){ 
         case 1	: 
         	  var strId = selectValueStr(setname);
         	  var tablename="table"+setname;
    		  var table=$(tablename);
    		  var temp=table.getActiveCell();
  			  var field_name=temp.getField();
  			  //strId由于是以`分隔，不能用模板，会导致取数不对
              thecodeurl="/general/inform/emp/batch/alertind.do?b_query=link&viewsearch=0&setname="+$URL.encode(setname)+"&a_code="+$URL.encode(a_code)+
              				"&infor="+$URL.encode(infor)+"&strid="+$URL.encode(strId)+"&field_name="+$URL.encode(field_name);
              if(isIE6()){
              return_vo= window.showModalDialog(thecodeurl, "", 
              	"dialogWidth:420px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
              }else{
              return_vo= window.showModalDialog(thecodeurl, "", 
              	"dialogWidth:400px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
              }
              
              break ; 
         case 2 : 
         	   var strId = selectValueStr(setname);
         	   
              thecodeurl="/general/inform/emp/batch/alertmoreind.do?b_query=link&viewsearch=0&setname="+$URL.encode(setname)+"&a_code="+$URL.encode(a_code)+
              				"&infor="+$URL.encode(infor)+"&strid="+$URL.encode(strId);
             
               return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:550px; dialogHeight:450px;resizable:no;center:yes;scroll:yes;status:no");
              break ; 
         case 3 : 
              thecodeurl="/general/inform/emp/batch/addind.do?b_query=link`viewsearch=0`setname="+setname+"`a_code="+a_code+"`infor="+infor;
              var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
              if(isIE6()){
                  return_vo= window.showModalDialog(iframe_url, "", 
              	"dialogWidth:550px; dialogHeight:450px;resizable:no;center:yes;scroll:yes;status:no");
              }else{
                  return_vo= window.showModalDialog(iframe_url, "", 
              	"dialogWidth:500px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
              }
              break ; 
         case 4 : 
              thecodeurl="/general/inform/emp/batch/delind.do?b_query=link`viewsearch=0`setname="+setname+"`a_code="+a_code+"`infor="+infor;
              var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
              if(isIE6()){
                   return_vo= window.showModalDialog(iframe_url, "", 
              		"dialogWidth:360px; dialogHeight:210px;resizable:no;center:yes;scroll:yes;status:no");
              }else{
                   return_vo= window.showModalDialog(iframe_url, "", 
              		"dialogWidth:340px; dialogHeight:210px;resizable:no;center:yes;scroll:yes;status:no");
              }
              break ; 
         default:
         	thecodeurl="";
    } 
    if(thecodeurl.length<1){
    	return;
    }
    if(return_vo!=null){
  	 	changeflag();
  	}else{
  		return ;
  	}
}
function selectValueStr(gsetname){
	var tablename="table"+gsetname;
    var table=$(tablename);
    var dataset=table.getDataset();
    var record=dataset.getFirstRecord();
    var str="";
    if(gsetname.indexOf("B01")!=-1){
    	while(true){
	     	if(record==undefined)
	        	break;
	        
	     	if (record.getValue("select")){
				var A0100=record.getValue("B0110");
				if(A0100!=null&&A0100.length>0)
	     			str+=A0100+"`";
	     	}
	     	record=record.getNextRecord();					
		}
	}else{
		while(true){
	     	if(record==undefined)
	        	break;
	     	if (record.getValue("select")){
	     		var I9999=record.getValue("I9999");
				var A0100=record.getValue("B0110");
				if(A0100!=null&&A0100.length>0&&I9999!="undefined"&&I9999>0)
	     			str+=A0100+":"+I9999+"`";
	     	}	
	     	record=record.getNextRecord();				
		}
	}
	return str;
}
function batchCond(a_code,infor,unit_type,gzflag){
	var setname = document.getElementById("fieldsetid").value;
	var thecodeurl="/general/inform/emp/batch/calculation.do?b_query=link&entranceFlag=1&setId=1&setname="+setname+"&a_code="+a_code+"&infor="+infor+
						"&unit_type="+unit_type+"&flag=ie";
    var return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:420px; dialogHeight:430px;resizable:no;center:yes;scroll:no;status:no");
    if(return_vo!=null){
  	 	changeflag();
  	}else{
  		return ;
  	}
}
function changeflag(){
	var a_code=document.getElementById("a_code").value;
	var infor=document.getElementById("infor").value;
	var gzflag=document.getElementById("gzflag").value;
	gzDataMaintForm.action="/org/gzdatamaint/gzdatamaint.do?b_query=link&infor="+infor+"&gzflag="+gzflag+"&a_code="+a_code;
    gzDataMaintForm.submit();  
}
function changeSub(){
	document.getElementById("sort_str").value='';
	document.getElementById("viewdata").value='';
	var a_code=document.getElementById("a_code").value;
	var infor=document.getElementById("infor").value;
	var gzflag=document.getElementById("gzflag").value;
	gzDataMaintForm.action="/org/gzdatamaint/gzdatamaint.do?b_query=link&infor="+infor+"&gzflag="+gzflag+"&a_code="+a_code;
    gzDataMaintForm.submit();  
}
function returnFirst(gzflag){
   	if(gzflag=='2')
   			self.parent.location= "/general/tipwizard/tipwizard.do?br_compensation=link";
   		else
   			self.parent.location= "/general/tipwizard/tipwizard.do?br_Insurance=link";
}