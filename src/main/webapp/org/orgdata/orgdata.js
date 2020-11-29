function viewRecord(viewdata,infor,a_code){
   document.getElementById("viewdata").value=viewdata;
   orgDataForm.action="/org/orgdata/orgdata.do?b_query=link&infor="+infor+"&a_code="+a_code;
   orgDataForm.submit();  	
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
		if(setname!="B01"&&setname!="K01"){
			i9999=record.getValue("I9999");
		}else{
			a_code = a_code.replace("UN","");
			a_code = a_code.replace("UM","");
			a_code = a_code.replace("@K","");
			itemid=a_code;
		}
	}
	var hashvo=new ParameterSet();
	hashvo.setValue("setname",setname);
	hashvo.setValue("type",flag);		
	hashvo.setValue("itemid",itemid);
	if(setname!="B01"&&setname!="K01"){
		hashvo.setValue("I9999",i9999);
	}else{
		hides("buttontableadd");
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
	if(gsetname.indexOf("B01")!=-1||gsetname.indexOf("K01")!=-1){
        dataset.insertRecord("end");	
	    record=dataset.getCurrent();
       	record.setValue("B0110",outparamters.getValue("B0110")); 
       	if(infor=="3"){
	    	record.setValue("E0122",outparamters.getValue("E0122"));
	    	record.setValue("E01A1",outparamters.getValue("E01A1"));      
	    }        
	}else{
		if(!record){
			dataset.insertRecord("end");	
	    	record=dataset.getCurrent();
		}	
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
			record.setValue("B0110",B0110); 
        	if(infor=="3"){
	        	record.setValue("E0122",E0122);	
	        	record.setValue("E01A1",E01A1);	
	        }	
			record.setValue("I9999",outparamters.getValue("I9999"));
			if(gsetname=='B00'||gsetname=='K00'||gsetname=='b00'||gsetname=='k00'){
				record.setValue("flag","选择");	
				record.setValue("downole","downole");
				record.setValue("upole","upole");		
			}  
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
			if(gsetname=='B00'||gsetname=='K00'||gsetname=='b00'||gsetname=='k00'){
				record.setValue("flag","选择");	
				record.setValue("downole","downole");
				record.setValue("upole","upole");		
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
		if(I9999==null||I9999==""||I9999=="0"){
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
			record.setValue("B0110",B0110); 
       		if(infor=="3"){
	    		record.setValue("E0122",E0122);
	    		record.setValue("E01A1",E01A1);      
	    	} 
	    	
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
			if(gsetname=='B00'||gsetname=='K00'||gsetname=='b00'||gsetname=='k00'){
				record.setValue("flag","选择");	
				record.setValue("downole","downole");
				record.setValue("upole","upole");		
			}  
		}else{
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
			if(gsetname=='B00'||gsetname=='K00'||gsetname=='b00'||gsetname=='k00'){
				record.setValue("flag","选择");	
				record.setValue("downole","downole");
				record.setValue("upole","upole");		
			}	            
	        while(true){
	        	record=record.getNextRecord();
	        	if(record==undefined)
	        		break;
	        	if(infor=="3"){
	        		temp=record.getValue("E01A1");
	        	}else{
	        		temp=record.getValue("B0110");
	        	}
	        	if(temp==itemid){      
	        		record.setValue("I9999",record.getValue("I9999")+1);	
	        	}else
	        		break;
	        }      	    
		}
		
	}
}
function save(isrefresh,a_code,codesetid,codeitemid,codeitemdesc,issuperuser,manageprive){
   	 if(isrefresh=='save'){
   	 	var currnode=parent.frames['mil_menu'].Global.selectedItem;
   	 	var uid = codesetid+a_code+codeitemid;
   	 	var text = codeitemdesc;
   	 	var title = codeitemdesc;
   	 	var issuperuser = issuperuser;
   	 	var manageprive = manageprive;
   	 	var kind = '0';
   	 	if(codesetid=='UM')
   	 		kind='1';
   	 	else if(codesetid=='UN')
   	 		kind='2';
   	 	var action = "/org/orginfo/searchorglist.do?b_search=link&code="+a_code+codeitemid+"&kind="+kind;
   	 	var xml = "/common/org/loadtree?params=child&treetype=org&parentid="  + a_code+codeitemid + "&kind="+kind+"&issuperuser=" + issuperuser + "&manageprive=" + manageprive + "&action=searchorglist.do&target=mil_body";
   	 	if(currnode.load){
   	 		var imgurl;
   	 		if(codesetid=='UM')
   	 			imgurl="/images/dept.gif";
   	 		else if(codesetid=='UN')
   	 			imgurl="/images/unit.gif";
   	 		if(codesetid!='@K')
   	 			parent.frames['mil_menu'].add(uid,text,action,"mil_body",title,imgurl,xml);
   	 	}else
   	 		currnode.expand();
   	 }
}
function setSort(infor,gzflag){
	var target_url="/org/gzdatamaint/gzdatamaint.do?b_addsubclass=link&tagname=0&infor="+infor+"&gzflag="+gzflag;
	var return_vo= window.showModalDialog(target_url,1, 
	        "dialogWidth:540px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
	if(return_vo!=null){
		changeflag();
   	}
}
function to_hide_field(setname){
 	var theURL="/general/inform/emp/view/hidefield.do?b_query=link&setname="+setname;
	var return_vo =window.showModalDialog(theURL,"",
		"dialogWidth=350px;dialogHeight=380px;resizable=yes;scroll:yes;center:yes;status=no;");  
	if(return_vo!=null){
		changeflag();
	}
}
function to_sort_subset_info(setname){
	var thecodeurl ="/gz/sort/sorting.do?b_query=link&flag=3&setname="+setname; 
	var return_vo= window.showModalDialog(thecodeurl, "", 
          "dialogWidth:510px; dialogHeight:420px;resizable:no;center:yes;scroll:yes;status:no");
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
	orgDataForm.action="/org/gzdatamaint/gzdatamaint.do?b_query=link&checkadd=1&a_code="+a_code+"&gzflag="+gzflag+"&infor="+infor;
   	orgDataForm.submit();			
}

function batchHand(flag,a_code){
	var thecodeurl =""; 
	var return_vo;	
	var setname = document.getElementById("setname").value;
	var infor = document.getElementById("infor").value;
	switch(flag){ 
         case 1	:
         	  var strId = selectValueStr(setname,infor);
         	  var tablename="table"+setname;
    		  var table=$(tablename);
    		  var temp=table.getActiveCell();
  			  var field_name=temp.getField();
              thecodeurl="/general/inform/emp/batch/alertind.do?b_query=link&viewsearch=0&setname="+setname+"&a_code="+a_code+"&infor="+infor+"&strid="+strId+"&field_name="+field_name;
              return_vo= window.showModalDialog(thecodeurl, "", 
              	"dialogWidth:400px; dialogHeight:300px;resizable:no;center:yes;scroll:yes;status:no");
              break ; 
         case 2 : 
         	  var strId = selectValueStr(setname);
              thecodeurl="/general/inform/emp/batch/alertmoreind.do?b_query=link&viewsearch=0&setname="+setname+"&a_code="+a_code+"&infor="+infor+"&strid="+strId+"&path=3";
              //var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl;
              return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:550px; dialogHeight:450px;resizable:no;center:yes;scroll:yes;status:no");
              break ; 
         case 3 : 
              thecodeurl="/general/inform/emp/batch/addind.do?b_query=link&viewsearch=0&setname="+setname+"&a_code="+a_code+"&infor="+infor;
               return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:500px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
              break ; 
         case 4 : 
              thecodeurl="/general/inform/emp/batch/delind.do?b_query=link&viewsearch=0&setname="+setname+"&a_code="+a_code+"&infor="+infor;
               return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:340px; dialogHeight:210px;resizable:no;center:yes;scroll:yes;status:no");
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
function selectValueStr(gsetname,infor){
	var tablename="table"+gsetname;
    var table=$(tablename);
    var dataset=table.getDataset();
    var record=dataset.getFirstRecord();
    var str="";
    var itemid="B0110";
    if(infor=='3')
    	itemid="E01A1";
    if(gsetname.toUpperCase().indexOf("B01")!=-1||gsetname.toUpperCase().indexOf("K01")!=-1){
    
    	while(true){
	     	if(record==undefined)
	        	break;
	        
	     	if (record.getValue("select")){
				var A0100=record.getValue(itemid);
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
				var A0100=record.getValue(itemid);
				if(A0100!=null&&A0100.length>0&&I9999!="undefined"&&I9999>0)
	     			str+=A0100+":"+I9999+"`";
	     	}	
	     	record=record.getNextRecord();				
		}
	}
	return str;
}
function printInform(check,dbname,a_code,inforkind,flag){
	var thecodeurl =""; 
	var return_vo;	
	var sFeatures="resizable:yes;center:yes;scroll:yes;status:no;dialogHeight:"+document.body.clientHeight+";dialogWidth:"+document.body.clientWidth;	
	switch(check){ 
         case 1	:     
         	var hashvo=new ParameterSet();
    		hashvo.setValue("dbname",dbname);
    		hashvo.setValue("a_code",a_code);
    		hashvo.setValue("infor",inforkind);
    		hashvo.setValue("flag",flag);
   			var request=new Request({asynchronous:false,functionId:'1010095000'},hashvo);
            var thecodeurl ="/general/muster/hmuster/searchroster.do?b_search=link`a_inforkind="+inforkind+"`result=0`dbpre="+dbname;
			var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl;

    		var return_vo= window.showModalDialog(iframe_url,"",sFeatures);
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
	        		if(inforkind=='2')
	        			a0100+=record.getValue("B0110")+",";	
	        		else
	        			a0100+=record.getValue("E01A1")+",";	
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
    		if(inforkind=='3')
    			inforkind="4";
   			var request=new Request({asynchronous:false,functionId:'1010095001'},hashvo);    		
            var thecodeurl ="/general/card/searchcard.do?b_query=link`home=2`inforkind="+inforkind+"`result=0`dbname="+dbname;
			var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
    		var return_vo= window.showModalDialog(iframe_url,"",sFeatures );
            break ; 
         case 3 : 
         	var hashvo=new ParameterSet();
    		hashvo.setValue("dbname",dbname);
    		hashvo.setValue("a_code",a_code);
    		hashvo.setValue("inforkind",inforkind);
    		hashvo.setValue("flag",flag);
    		var nflag = "21";
    		if(inforkind=='3')
    			nflag = "41";
   			var request=new Request({asynchronous:false,functionId:'1010095002'},hashvo);    	
            var thecodeurl ="/general/muster/hmuster/searchHroster.do?b_search=link`res=0`nFlag="+nflag+"`a_inforkind="+inforkind+"`result=0`dbpre="+dbname;
           //var thecodeurl ="/general/muster/hmuster/select_muster_name.do?b_search=link`nFlag="+nflag+"`a_inforkind="+inforkind+"`dbpre="+dbname;
			var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl;
    		var return_vo= window.showModalDialog(iframe_url,"", sFeatures);
            break ; 
         default:
         	thecodeurl="";
    } 
}
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
    	document.getElementById("viewsearch").value='1';
  	 	changeflag();
  	}else{
  		return ;
  	}
}
function searchOk(viewsearch){
   document.getElementById("viewsearch").value=viewsearch;
   changeflag();
}
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
		searchOk("1");
  }
}
function singleAudit(){
	var infor = document.getElementById("infor").value;
	var setname = document.getElementById("setname").value;
  	var thecodeurl="/general/inform/informcheck/view_check.do?b_query=link&infor="+infor+"&dbname="+setname;
    var return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:400px; dialogHeight:400px;resizable:no;center:yes;scroll:no;status:no");  	
}
function batchCond(a_code){
	var setname = document.getElementById("setname").value;
	var infor = document.getElementById("infor").value;
	var thecodeurl="/general/inform/emp/batch/calculation.do?b_query=link&setname="+setname+"&a_code="+a_code+"&infor="+infor+"&unit_type=3";
    var return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:410px; dialogHeight:430px;resizable:no;center:yes;scroll:no;status:no");
    if(return_vo!=null){
  	 	changeflag();
  	}else{
  		return ;
  	}
}
function changeflag(){
	var a_code=document.getElementById("a_code").value;
	var infor=document.getElementById("infor").value;
	orgDataForm.action="/org/orgdata/orgdata.do?b_query=link&infor="+infor+"&a_code="+a_code;
    orgDataForm.submit();  
}
function edit(setname,a_code,infor)
{
	var tablename,table,dataset,record;
	tablename="table"+setname;
    table=$(tablename);
    dataset=table.getDataset();	
    record=dataset.getCurrent();
   
    var itemVal = '';
	if(infor=='2')
		itemVal = record.getValue("b0110");
	else if(infor=='3')
		itemVal = record.getValue("e01a1");	
		
    var theurl='/org/orgdata/orgedit.do?b_query=link`itemVal='+itemVal+'`infor='+infor;
    var iframe_url="/general/query/common/iframe_query.jsp?src="+theurl;
   	var return_vo= window.showModalDialog(iframe_url, 'orgedit_win', 
      				"dialogWidth:900px; dialogHeight:800px;resizable:no;center:yes;scroll:yes;status:no");	
    if(!return_vo)
	    return false;	   
    if(return_vo.flag=="true")
	{  
		var hashvo=new ParameterSet();
		if((infor=='2' && setname.indexOf("B01")==-1) || (infor=='3' && setname.indexOf("K01")==-1))
   	    {
			var I9999=record.getValue("I9999");
    		hashvo.setValue("I9999",I9999);
    	}		
		hashvo.setValue("fieldset",setname);	
		hashvo.setValue("infor",infor);
		hashvo.setValue("itemVal",itemVal);	
		var request=new Request({method:'post',asynchronous:false,onSuccess:refreshRecord,functionId:'0401000034'},hashvo);
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
				if(isExistField(dataset,itemsarr[i]))
					record.setValue(itemsarr[i],getDecodeStr(outparamters.getValue(itemsarr[i])));
			}			
		}
	}
	record.setState("modify");   
}

function isExistField(dataset,fieldName)
{
	var field=dataset.getField(fieldName);
	if(field!=null)
		return true;
	 return false;
}
function editSubSet(setname,itemid,infor)
{
	var tablename,table,dataset,record,theurl,iframe_url,iframe_url,returnVo;
	tablename="table"+setname;
    table=$(tablename);
    dataset=table.getDataset();	
    record=dataset.getCurrent();
    var i9999=record.getValue("i9999");	
    if(i9999=='')
    	return;
    	
    if(setname=='B00' || setname=='K00')
    {
   		  theurl="/general/inform/emp/view/opermultimedia.do?b_edit=link`itemid="+itemid+'`subset='+setname+'`infor='+infor+'`i9999='+i9999;
   		  iframe_url="/general/query/common/iframe_query.jsp?src="+theurl;
   		  returnVo= window.showModalDialog(iframe_url, 'orgsubset_win', 
	      				"dialogWidth:500px; dialogHeight:200px;resizable:no;center:yes;scroll:yes;status:no");
    }else
    {	    	
    	 theurl="/org/orgdata/orgsubset_add.do?b_query=link`itemid="+itemid+'`subset='+setname+'`i9999='+i9999+'`infor='+infor;
		 iframe_url="/general/query/common/iframe_query.jsp?src="+theurl;
		 returnVo= window.showModalDialog(iframe_url, 'orgsubset_win', 
	    				"dialogWidth:700px; dialogHeight:500px;resizable:no;center:yes;scroll:yes;status:no");
	}
	if(!returnVo)
		return false;	   

	if(returnVo.flag=="true")
	{   
		if(parent.parent.ril_body2!=null)
			parent.parent.ril_body2.location="/org/orgdata/orgdata.do?b_menu=link&itemid="+itemid+"&infor="+infor;
		else
			parent.parent.a.location="/org/orgdata/orgdata.do?b_menu=link&itemid="+itemid+"&infor="+infor;
	}   
}
function addSubSet(setname,itemid,infor)
{
	var theurl,iframe_url,iframe_url,returnVo;
    if(setname=='B00' || setname=='K00')
    {
   		  theurl="/general/inform/emp/view/opermultimedia.do?b_add2=link`curri9999=0`itemid="+itemid+'`subset='+setname+'`infor='+infor;
   		  iframe_url="/general/query/common/iframe_query.jsp?src="+theurl;
   		  returnVo= window.showModalDialog(iframe_url, 'orgsubset_win', 
	      				"dialogWidth:500px; dialogHeight:200px;resizable:no;center:yes;scroll:yes;status:no");
    }else
    {
     	 theurl="/org/orgdata/orgsubset_add.do?b_query=link`itemid="+itemid+'`subset='+setname+'`i9999=0`infor='+infor;
   		 iframe_url="/general/query/common/iframe_query.jsp?src="+theurl;
   		 returnVo= window.showModalDialog(iframe_url, 'orgsubset_win', 
	      				"dialogWidth:700px; dialogHeight:500px;resizable:no;center:yes;scroll:yes;status:no");
    }
    
	if(!returnVo)
		return false;	
	if(returnVo.flag=="true")
	{   
		if(parent.parent.ril_body2!=null)
			parent.parent.ril_body2.location="/org/orgdata/orgdata.do?b_menu=link&itemid="+itemid+"&infor="+infor;
		else
			parent.parent.a.location="/org/orgdata/orgdata.do?b_menu=link&itemid="+itemid+"&infor="+infor;
	}
}
function insertSubSet(setname,itemid,infor)
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

		var theurl,iframe_url,iframe_url,returnVo;
    if(setname=='B00' || setname=='K00')
    {
   		  theurl="/general/inform/emp/view/opermultimedia.do?b_add2=link`itemid="+itemid+'`subset='+setname+'`infor='+infor+'`curri9999='+i9999;
   		  iframe_url="/general/query/common/iframe_query.jsp?src="+theurl;
   		  returnVo= window.showModalDialog(iframe_url, 'orgsubset_win', 
	      				"dialogWidth:500px; dialogHeight:200px;resizable:no;center:yes;scroll:yes;status:no");
    }else
    {
 		  theurl="/org/orgdata/orgsubset_add.do?b_query=link`itemid="+itemid+'`subset='+setname+'`i9999=0`infor='+infor+'`curri9999='+i9999;
		  iframe_url="/general/query/common/iframe_query.jsp?src="+theurl;
		  returnVo= window.showModalDialog(iframe_url, 'orgsubset_win', 
	      				"dialogWidth:700px; dialogHeight:500px;resizable:no;center:yes;scroll:yes;status:no");
    }

	if(!returnVo)
		return false;	
	if(returnVo.flag=="true")
	{   
		if(parent.parent.ril_body2!=null)
			parent.parent.ril_body2.location="/org/orgdata/orgdata.do?b_menu=link&itemid="+itemid+"&infor="+infor;
		else
			parent.parent.a.location="/org/orgdata/orgdata.do?b_menu=link&itemid="+itemid+"&infor="+infor;
	}
}
function downLoadOle(setname,b0110,i9999,infor){
	var sqlstr = "select Title,Ole,ext from "+setname+" where ";
	if(infor=="2")
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
			window.location.href="/servlet/DisplayOleContent?filename="+outName;
		}
	}	
}
function selectMedia(dbname,b0110,i9999,infor,isvisable,cell){
	var thecodeurl="/org/orgdata/media_tree.jsp?b0110="+b0110+"`dbname="+dbname+"`infor="+infor+"`isvisable"+isvisable; 
	var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl;
	var returnVo= window.showModalDialog(iframe_url,'orgsubset_win', 
	      				"dialogWidth:300px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
    if(returnVo!=null){
    	var flag = returnVo.uid;
    	var flagname = returnVo.text;
    	var valuestr = "A"+flag+"`A"+b0110+"`A"+i9999;
    	
    	var sqlstr = "update ";
    	if(infor=="2")
    		sqlstr+=" B00 set Flag=? where B0110=? and I9999=?";
    	else if(infor=="3")
    		sqlstr+=" K00 set Flag=? where E01A1=? and I9999=?";
    	
    	var hashvo=new ParameterSet();
    	hashvo.setValue("sqlstr",sqlstr);	
		hashvo.setValue("valuestr",valuestr);		
		var request=new Request({method:'post',asynchronous:false,functionId:'1010090019'},hashvo);
		var cellstr = "<div style=\"cursor:hand;color:#0033FF\" ";
		cellstr+=" onclick=\"selectMedia('','"+b0110+"','"+i9999+"','"+infor+"','"+isvisable+"',cell)\" ";
		cellstr+=">"+flagname+"</div>";
		cell.innerHTML=cellstr;
    }
}
function uploadMedia(dbname,b0110,i9999,infor){
	var thecodeurl="/org/orgdata/orgdata.do?b_load=link`b0110="+b0110+"`dbname="+dbname+"`infor="+infor+"`i9999="+i9999; 
	var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl;
	var returnVo= window.showModalDialog(iframe_url,'orgsubset_win', 
	      				"dialogWidth:500px; dialogHeight:200px;resizable:no;center:yes;scroll:yes;status:no");
}