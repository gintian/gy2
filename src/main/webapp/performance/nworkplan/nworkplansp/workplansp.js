
//驳回
function reject(p0100){
    var target_url="/performance/nworkplan/nworkplansp/SearchWorkPlanSpTrans.do?br_reject=link`p0100="+p0100;
    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(target_url);
    var return_vo=window.showModalDialog(iframe_url,null,"dialogWidth:400px; dialogHeight:400px;resizable:no;center:yes;scroll:no;status:no");
    if(return_vo==null){
    	return;
    } 
	var p0100=return_vo.p0100;
	var state=return_vo.state;
	var belong_type=return_vo.belong_type;
	var url="";
	if(state==1){
		url="/performance/nworkplan/week/searchWeekWorkplan.do?br_main=link&p0100="+p0100+"&opt=2&isRead=1&belong_type="+belong_type;
	}else if(state==2){
	   url="/performance/nworkplan/searchMonthWorkplan.do?br_main=link&p0100="+p0100+"&opt=2&isRead=1&belong_type="+belong_type;
	}else if(state==3){
	
	}else if(state==4){
	
	}
	document.forms[0].action=url;
	document.forms[0].submit();	
}	

//批准
  	function approval(p0100){
  		 var vo=new Object();
  		 vo.p0100=p0100;
     	 var hashvo = new ParameterSet();
	     hashvo.setValue("vo",vo);
	     var request=new Request({asynchronous:false,onSuccess:approval_ok,functionId:'302001020652'},hashvo); 
  	}
   function approval_ok(outparamters){
   		var p0100=outparamters.getValue("p0100");
   		document.forms[0].action="";
		document.forms[0].submit();	
   }