var gsetname;

function reloadBySetId(){
   contractForm.action="/ht/inform/data_table.do?b_query=link&";
   contractForm.submit();  	
}
//判断表格里面是否包含某个字段（fzg加）
function isExistField(dataset,fieldName)
{
	var field=dataset.getField(fieldName);
	if(field!=null)
		return true;
	 return false;
}

function batchHand(flag,a_code,dbname,viewsearch){
	var thecodeurl =""; 
	var return_vo;	
	var setname = "A01";
	switch(flag){ 
         case 1	: 
         	  var strId = selectValueStr(dbname+setname);
         	  var tablename="table"+dbname+setname;
    		  var table=$(tablename);
    		  var temp=table.getActiveCell();
  			  var field_name=temp.getField();
              thecodeurl="/general/inform/emp/batch/alertind.do?b_query=link&setname="+setname+"&a_code="+a_code+"&dbname="+dbname+"&viewsearch="+viewsearch+"&infor=1&strid="+strId+"&field_name="+field_name;
              return_vo= window.showModalDialog(thecodeurl, "", 
              	"dialogWidth:400px; dialogHeight:300px;resizable:no;center:yes;scroll:yes;status:no");
              break ; 
         case 2 : 
          	  var strId = selectValueStr(dbname+setname);
              thecodeurl="/general/inform/emp/batch/alertmoreind.do?b_query=link&setname="+setname+"&a_code="+a_code+"&dbname="+dbname+"&viewsearch="+viewsearch+"&infor=1&strid="+strId;
               return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:550px; dialogHeight:500px;resizable:no;center:yes;scroll:yes;status:no");
              break ; 
         case 3 : 
              thecodeurl="/general/inform/emp/batch/addind.do?b_query=link&setname="+setname+"&a_code="+a_code+"&dbname="+dbname+"&viewsearch="+viewsearch+"&infor=1";
               return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:500px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
              break ; 
         case 4 : 
              thecodeurl="/general/inform/emp/batch/delind.do?b_query=link&setname="+setname+"&a_code="+a_code+"&dbname="+dbname+"&viewsearch="+viewsearch+"&infor=1";
               return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:340px; dialogHeight:210px;resizable:no;center:yes;scroll:yes;status:no");
              break ; 
         case 5 : 
              thecodeurl="/general/inform/emp/batch/calculation.do?b_query=link`unit_type=1`setname="+setname+"`a_code="+a_code+"`dbname="+dbname+"`viewsearch="+viewsearch+"`infor=4";
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
  	 	contractForm.action = "/ht/inform/data_table.do?b_query=link";
		contractForm.submit();   
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
function indCheck(outparamters){
	var check = outparamters.getValue("check");
	if(check!='no'){
		window.returnValue=check;
		window.close();
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
  	 	contractForm.action="/ht/inform/data_table.do?b_query=link&viewsearch=1";
   		contractForm.submit();
  	}else{
  		return ;
  	}
}
function printInform(check,dbname,a_code,inforkind,flag){
	var thecodeurl =""; 
	var return_vo;	
	var ctflag =document.getElementById("ctflag").value; 
	//var feather = "fullscreen=1";
	//var feather = "left=0,top=0,width="+screen.availWidth+",height="+screen.availHeight+",scrollbars=yes,toolbar=no,menubar=no,location=no,resizable=yes,status=yes,alwaysRaised=yes,z-look=yes";
	var feather = "dialogleft:0px;dialogtop:0px;dialogwidth:"+screen.availWidth+"px;dialogheight:"+screen.availHeight+"px;resizable:yes;status:yes;center:yes;Minimize=yes;Maximize=yes";
	var newwindow = null;
	switch(check){ 
         case 1	:     
         	var hashvo=new ParameterSet();
    		hashvo.setValue("dbname",dbname);
    		hashvo.setValue("a_code",a_code);
    		hashvo.setValue("infor",inforkind);
    		hashvo.setValue("flag",flag);
   			var request=new Request({asynchronous:false,functionId:'1010095000'},hashvo);
            var thecodeurl ="/general/muster/hmuster/searchroster.do?b_search=link`a_inforkind="+inforkind+"`a_hflag=1`result=0`dbpre="+dbname+"`closeWindow=2";
            var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
            // 需要放到iframe中,否则js使用self.parent.location刷新时,会打开新ie窗口
            window.showModelessDialog(iframe_url,'',feather);	
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
    		hashvo.setValue("type","card_new");//调用新登记表标识。用于后台交易类区分
   			var request=new Request({asynchronous:false,functionId:'1010095001'},hashvo);    		
            /*var thecodeurl ="/general/card/searchcard.do?b_query=link`home=2`inforkind="+inforkind+"`result=0`dbname="+dbname;
			var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl;
    		var return_vo= window.showModalDialog(iframe_url,"", 
              	"dialogWidth:1000pxpx; dialogHeight:800px;resizable:yes;center:yes;scroll:yes;status:yes");*/ 
   			
   		   var thecodeurl ="/general/query/common/iframe_query.jsp?src="+$URL.encode("/module/card/cardCommonSearch.jsp?inforkind="+inforkind);
            //newwindow = window.open(thecodeurl,'newwindow', feather); 
            window.showModelessDialog(thecodeurl,'',feather); 	
            break ; 
         case 3 : 
         	var hashvo=new ParameterSet();
    		hashvo.setValue("dbname",dbname);
    		hashvo.setValue("a_code",a_code);
    		hashvo.setValue("inforkind",inforkind);
    		hashvo.setValue("flag",flag);
    		hashvo.setValue("ctflag",ctflag);
   			var request=new Request({asynchronous:false,functionId:'1010095002'},hashvo);    	
            /*var thecodeurl ="/general/muster/hmuster/searchHroster.do?b_search=link`nFlag=2`res=1`a_inforkind="+inforkind+"`result=0`dbpre="+dbname;
			var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl;
    		var return_vo= window.showModalDialog(iframe_url,"", 
              	"dialogWidth:950px; dialogHeight:600px;resizable:yes;center:yes;scroll:yes;status:yes");*/
              	
            var thecodeurl ="/general/muster/hmuster/searchHroster.do?b_search=link&nFlag=2&res=1&a_inforkind="+inforkind+"&result=0&dbpre="+dbname+"&closeWindow=2";   	
            //newwindow = window.open(thecodeurl,'newwindow', feather);  
            window.showModelessDialog(thecodeurl,'',feather);	
            break ; 
         default:
         	thecodeurl="";
    }
   // if (newwindow != null) {
   // 	newwindow.focus();
    //} 
}

function searchOk(viewsearch){
   contractForm.action="/ht/inform/data_table.do?b_query=link&viewsearch="+viewsearch;
   contractForm.submit();  	
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
		contractForm.action="/ht/inform/data_table.do?b_query=link&viewsearch=1";
   		contractForm.submit();
  	}
}	 