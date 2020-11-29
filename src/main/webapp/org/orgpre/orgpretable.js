function insert(setname,flag,a_code,infor,unit_type,sp_flag){
	//alert(setname+"  "+flag+"  "+a_code+"  "+infor+"  "+unit_type+"  "+sp_flag);
	//return false;
	var tablename,table,dataset,preno,record,i9999;
	tablename="table"+setname;
	gsetname=setname
	i9999="";
    table=$(tablename);
    dataset=table.getDataset();	
    record=dataset.getCurrent();
    var hashvo=new ParameterSet();
    hashvo.setValue("infor",infor);
    hashvo.setValue("unit_type",unit_type);
    hashvo.setValue("a_code",a_code);
    if(a_code==null||a_code.length<1){
    	alert(SELECT_ORGRECORD_ADD);
    	return false;
    }
	if(!record||setname=="B01"||setname=="K01"||setname=="b01"||setname=="k01"){
		a_code = a_code.replace("UN","");
		a_code = a_code.replace("UM","");
		a_code = a_code.replace("@K","");
		var itemid=a_code;
		hashvo.setValue("setname",setname);
		hashvo.setValue("type",flag);		
		hashvo.setValue("itemid",itemid);
		hashvo.setValue("checkadd","1");
	}else{
		var itemid=record.getValue("B0110");
		var sp_flagvalue = record.getValue(sp_flag);
		if(sp_flagvalue==''){
		
		}else if(sp_flagvalue=='01'){
			alert(ORG_ORGPRE_ORGPRETABLE_01)
			return false;
		}else if(sp_flagvalue=='07'){
			alert(ORG_ORGPRE_ORGPRETABLE_07)
			return false;
		}else if(sp_flagvalue=='02'){
			alert(ORG_ORGPRE_ORGPRETABLE_02)
			return false;
		}
		hashvo.setValue("setname",setname);
		hashvo.setValue("type",flag);		
		hashvo.setValue("itemid",itemid);
	
		if(flag=="insert"){
			if(setname!="B01"||setname!="K01"||setname!="b01"||setname!="k01"){
				i9999=record.getValue("I9999");
				hashvo.setValue("I9999",i9999);
			}
		}
	}
	var request=new Request({method:'post',asynchronous:false,onSuccess:setInsertRecord,functionId:'0401000028'},hashvo);
}
function setInsertRecord(outparamters){
	var tablename,table,dataset,preno,bmainset;
	tablename="table"+gsetname;
    table=$(tablename);
    dataset=table.getDataset();
    record=dataset.getCurrent();
    var cloumstr = document.getElementById("cloumstr").value;	
    var sp_flag = document.getElementById("sp_flag").value;
    var cloumarr = 	cloumstr.split(",");
    //alert(gsetname);
	if(gsetname.indexOf("B01")!=-1||gsetname.indexOf("K01")!=-1
		||gsetname.indexOf("b01")!=-1||gsetname.indexOf("k01")!=-1){
		record=dataset.getCurrent();
		if(!record){
            var infor=outparamters.getValue("infor");
            var unit_type=outparamters.getValue("unit_type");
            var a_code=outparamters.getValue("a_code");
            addChange(a_code,infor,unit_type);
        }     
	}else{
		var I9999=outparamters.getValue("I9999");
		var B0110=outparamters.getValue("B0110");
		var itemid=B0110;
		if(I9999==""||I9999=="0"){
			record.setValue("I9999",outparamters.getValue("I9999"));
		}else{
			var temp;
            record=dataset.getCurrent();
            //alert(record.getValue('B0110'));
            if(!record){
            	if(parseInt(I9999)==1){
            		var infor=outparamters.getValue("infor");
            		var unit_type=outparamters.getValue("unit_type");
            		var a_code=outparamters.getValue("a_code");
            		addChange(a_code,infor,unit_type);
            	}else{
					alert(RECORD_NOT_ADD);            	
            	}
            }else{
    	   		record.setValue("I9999",I9999);
    	   		//alert(B0110); 
        		//record.setValue("B0110",B0110); 
	        	//alert(record.getValue('B0110'));  
        		for(var i=0;i<cloumarr.length;i++){
	        		if(cloumarr[i]!=null&&cloumarr[i].length>1){
	        			//alert(cloumarr[i]);
	        			record.setValue(cloumarr[i],"");
	        		}
	        	}
	        	record.setValue(sp_flag,"01"); 
	        }	    
		}
	}
	var fielditem = outparamters.getValue("fielditem");
	if(fielditem!=null&&fielditem.length>0){
		var itemsarr = fielditem.split(",");
		for(var i=0;i<itemsarr.length;i++){
			if(itemsarr[i]!=null&&itemsarr[i].length>0){
				var temValue = outparamters.getValue(itemsarr[i]);
				if(sp_flag.toLowerCase == itemsarr[i].toLowerCase 
						&& (!temValue || typeof(temValue) == "undefined")) {
					record.setValue(sp_flag,"01");
				} else {
					record.setValue(itemsarr[i],temValue);
				}
			}
		}
	}
	
}
function batchCond(setname,a_code,infor,unit_type){
	var nextlevel=document.getElementById("nextlevel").value;
	var thecodeurl="/general/inform/emp/batch/calculation.do?b_query=link&setname="+setname
		+"&a_code="+a_code+"&infor="+infor+"&unit_type="+unit_type + "&flag=ie";
    var return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:420px; dialogHeight:430px;resizable:no;center:yes;scroll:no;status:no");
    if(return_vo!=null){
    	if(return_vo!='ok'){
    		alert(return_vo);
    	}
  	 	orgPreForm.action="/org/orgpre/orgpretable.do?b_query=link&a_code="+a_code+"&infor="+infor+"&unit_type="+unit_type+"&nextlevel="+nextlevel;
   		orgPreForm.submit();
  	}else{
  		return ;
  	}
}
function to_sort_field(setname,a_code,infor,unit_type){
	var nextlevel=document.getElementById("nextlevel").value;
 	var theURL = "/general/inform/emp/view/sortfield.do?b_query=link&setname="+setname+"&flag=1";
	var return_vo = window.showModalDialog(theURL,"",
		"dialogWidth=320px;dialogHeight=360px;resizable=no;scroll:no;center:yes;status=no;");  
	if(return_vo!=null){
		orgPreForm.action="/org/orgpre/orgpretable.do?b_query=link&a_code="+a_code+"&infor="+infor+"&unit_type="+unit_type+"&nextlevel="+nextlevel;
   		orgPreForm.submit();	
	}
}
function change(a_code,infor,unit_type){
	var nextlevel = document.getElementById("nextlevel").value;
	var urlstr ="/org/orgpre/orgpretable.do?b_query=link&a_code="+a_code+"&infor=";
		urlstr+=infor+"&unit_type="+unit_type+"&nextlevel="+nextlevel;
	orgPreForm.action=urlstr;
   	orgPreForm.submit();
}
function addChange(a_code,infor,unit_type){
	var nextlevel = document.getElementById("nextlevel").value;
	var urlstr = "/org/orgpre/orgpretable.do?b_query=link&a_code="+a_code+"&infor=";
		urlstr+=infor+"&unit_type="+unit_type+"&checkadd=1&nextlevel="+nextlevel;
	
	orgPreForm.action=urlstr;
   	orgPreForm.submit();
}
function setCondPerson(a_code,infor,unit_type){
	var nextlevel = document.getElementById("nextlevel").value;
	var thecodeurl="/pos/posparameter/ps_parameter.do?b_search_unit=link`org_flag=1";
	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
    var return_vo= window.showModalDialog(iframe_url, "", 
              "dialogWidth:1200px; dialogHeight:520px;resizable:no;center:yes;scroll:no;status:no");
    if(return_vo!=null){
    	orgPreForm.target="il_body";
    	orgPreForm.action="/org/orgpre/get_org_tree.do?b_query=link&infor=2&unit_type=3";
    	orgPreForm.submit();
    }
   // if(return_vo!=null){
  //	 	orgPreForm.action="/org/orgpre/orgpretable.do?b_query=link&a_code="+a_code+"&infor="+infor+"&unit_type="+unit_type+"&nextlevel="+nextlevel;
  // 		orgPreForm.submit();
  //	}else{
  //		return ;
  //	}
}
