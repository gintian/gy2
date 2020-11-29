/*月报 begin*/
var _IsMousedown=0;
var _ClickLeft = 0;
var _ClickTop = 0;
var isContinue=0;
var opt='';
var compareDate='';
function getNode(id){
      nodeId=document.getElementById(id);
      tr_changeColor(nodeId,'#EAEAEA');
      //alert(nodeId.innerHTML);
    }
function stimeBlurFunc(){
     if(compareDate!=document.getElementById("start_time").value){
        document.getElementById("end_time").value = document.getElementById("start_time").value;
     	compareDate = document.getElementById("start_time").value;
     }
}
function showQueryDiv(flag,wholeflag,aatime,aahour,aaminute,bbtime,bbhour,bbminute){
	  var myDate = new Date();
	  if(compareDate=='')
	  compareDate = myDate.getFullYear()+"-"+(parseInt((myDate.getMonth()+1),10)<10?"0"+(myDate.getMonth()+1):myDate.getMonth()+1)+"-"+(parseInt(myDate.getDate(),10)<10?"0"+(myDate.getDate()):myDate.getDate());
      if(flag=='add'){
      	 opt='add';
         //var divNode=document.getElementById("dataDiv");
      	 if(wholeflag!=null&&wholeflag=='addwhole'){
      	 	 document.getElementById("addOrEdit").innerHTML="新增事件";
	         document.getElementById("okOrCancel").innerHTML="<input type='button' value='保存' class=\"epm-baocun\" onclick=\"check('"+flag+"','','');\">&nbsp;&nbsp;<input type='button' value='保存&继续' class=\"epm-baocun-bc\" onclick=\"check('continue','','');\">&nbsp;&nbsp;<input type='button' value='取消' class=\"epm-baocun\" onclick=\"closeDiv('dataDiv')\">";
	         document.getElementById("title").value="";
	         document.getElementById("type").value="1";
	         document.getElementById("type").checked=true;
	         document.getElementById("start_time").value = aatime;
	        document.getElementById("end_time").value = bbtime;
	        document.getElementById("contentid").value="";
	        for(var i=0;i<document.getElementById("startHour").options.length;i++)
			{
				if(document.getElementById("startHour").options[i].value==aahour)
				   document.getElementById("startHour").options[i].selected=true;
			}
			for(var i=0;i<document.getElementById("startMinute").options.length;i++)
			{
				if(document.getElementById("startMinute").options[i].value==aaminute)
				   document.getElementById("startMinute").options[i].selected=true;
			}
	        for(var i=0;i<document.getElementById("endHour").options.length;i++)
			{
				if(document.getElementById("endHour").options[i].value==bbhour)
				   document.getElementById("endHour").options[i].selected=true;
			}
			for(var i=0;i<document.getElementById("endMinute").options.length;i++)
			{
				if(document.getElementById("endMinute").options[i].value==bbminute)
				   document.getElementById("endMinute").options[i].selected=true;
			}
			var divHeight = 420+"px";
	         var divWidth = 403+"px";
	         document.getElementById("mid").style.height = 345;
	         divHeight = divHeight.substring(0,divHeight.length-2);
	         divWidth = divWidth.substring(0,divWidth.length-2);
	         document.getElementById("dataDiv").style.top = (document.body.clientHeight-divHeight)/2+parseInt(document.body.scrollTop,10);
	         document.getElementById("dataDiv").style.left = (document.body.clientWidth-divWidth)/2;
	         Element.hide('dataDetailLeftDiv');
		      Element.hide('dataDetailRightDiv');
		      document.getElementById("resultIframe").className = "epm-j-jieguo";
		      document.getElementById("resultIframe").innerHTML="";
		      Element.hide('queryDiv');
			Element.hide('addwholespan');
		      Element.show('dataDiv');
      	 }else if(wholeflag!=null&&wholeflag=='addperiod'){
      	 Element.show('addwholespan');
      	 	 document.getElementById("addOrEdit").innerHTML="新增事件";
	         document.getElementById("okOrCancel").innerHTML="<input type='button' value='保存' class=\"epm-baocun\" onclick=\"check('"+flag+"','','');\">&nbsp;&nbsp;<input type='button' value='保存&继续' class=\"epm-baocun-bc\" onclick=\"check('continue','','');\">&nbsp;&nbsp;<input type='button' value='取消' class=\"epm-baocun\" onclick=\"closeDiv('dataDiv')\">";
	         document.getElementById("title").value="";
	         document.getElementById("type").value="1";
	         document.getElementById("type").checked=false;
	         document.getElementById("start_time").value = aatime;
	        document.getElementById("end_time").value = bbtime;
	        document.getElementById("contentid").value="";
	         for(var i=0;i<document.getElementById("startHour").options.length;i++)
			{
				if(document.getElementById("startHour").options[i].value==aahour)
				   document.getElementById("startHour").options[i].selected=true;
			}
			for(var i=0;i<document.getElementById("startMinute").options.length;i++)
			{
				if(document.getElementById("startMinute").options[i].value==aaminute)
				   document.getElementById("startMinute").options[i].selected=true;
			}
	        for(var i=0;i<document.getElementById("endHour").options.length;i++)
			{
				if(document.getElementById("endHour").options[i].value==bbhour)
				   document.getElementById("endHour").options[i].selected=true;
			}
			for(var i=0;i<document.getElementById("endMinute").options.length;i++)
			{
				if(document.getElementById("endMinute").options[i].value==bbminute)
				   document.getElementById("endMinute").options[i].selected=true;
			}
			var divHeight = 420+"px";
	         var divWidth = 403+"px";
	         document.getElementById("mid").style.height = 395;
	         divHeight = divHeight.substring(0,divHeight.length-2);
	         divWidth = divWidth.substring(0,divWidth.length-2);
	         document.getElementById("dataDiv").style.top = (document.body.clientHeight-divHeight)/2+parseInt(document.body.scrollTop,10);
	         document.getElementById("dataDiv").style.left = (document.body.clientWidth-divWidth)/2;
	         Element.hide('dataDetailLeftDiv');
		      Element.hide('dataDetailRightDiv');
		      document.getElementById("resultIframe").className = "epm-j-jieguo";
		      document.getElementById("resultIframe").innerHTML="";
		      Element.hide('queryDiv');
		      Element.hide('addperiodspan');
		      Element.show('dataDiv');
      	 }else{
      	 	 Element.show('addwholespan');
      	 	 Element.show('addperiodspan');
	         document.getElementById("addOrEdit").innerHTML="新增事件";
	         document.getElementById("okOrCancel").innerHTML="<input type='button' value='保存' class=\"epm-baocun\" onclick=\"check('"+flag+"','','');\">&nbsp;&nbsp;<input type='button' value='保存&继续' class=\"epm-baocun-bc\" onclick=\"check('continue','','');\">&nbsp;&nbsp;<input type='button' value='取消' class=\"epm-baocun\" onclick=\"closeDiv('dataDiv')\">";
	         document.getElementById("title").value="";
	         document.getElementById("type").value="1";
	         document.getElementById("type").checked=false;
	        document.getElementById("start_time").value = myDate.getFullYear()+"-"+(parseInt((myDate.getMonth()+1),10)<10?"0"+(myDate.getMonth()+1):myDate.getMonth()+1)+"-"+(parseInt(myDate.getDate(),10)<10?"0"+(myDate.getDate()):myDate.getDate());
	        document.getElementById("end_time").value = myDate.getFullYear()+"-"+(parseInt((myDate.getMonth()+1),10)<10?"0"+(myDate.getMonth()+1):myDate.getMonth()+1)+"-"+(parseInt(myDate.getDate(),10)<10?"0"+(myDate.getDate()):myDate.getDate());
	         document.getElementById("contentid").value="";
	         //document.getElementById("startHour").options[0].selected=true;
	         for(var i=0;i<document.getElementById("startHour").options.length;i++)
			 {
				if(document.getElementById("startHour").options[i].value==myDate.getHours())
				   document.getElementById("startHour").options[i].selected=true;
			 }
	         for(var i=0;i<document.getElementById("startMinute").options.length;i++)
			 {
				if(document.getElementById("startMinute").options[i].value==myDate.getMinutes())
				   document.getElementById("startMinute").options[i].selected=true;
			 }
	         document.getElementById("endHour").options[document.getElementById("endHour").options.length-1].selected=true;
	         //document.getElementById("startMinute").options[0].selected=true;
	         document.getElementById("endMinute").options[document.getElementById("endMinute").options.length-1].selected=true;
	         Element.show('startTimeDiv');
	         Element.show('endTimeDiv');
	         //var divHeight = document.getElementById("dataDiv").style.height;
	         //var divWidth = document.getElementById("dataDiv").style.width;
	         var divHeight = 420+"px";
	         var divWidth = 403+"px";
	         divHeight = divHeight.substring(0,divHeight.length-2);
	         divWidth = divWidth.substring(0,divWidth.length-2);
	         document.getElementById("dataDiv").style.top = (document.body.clientHeight-divHeight)/2+parseInt(document.body.scrollTop,10);
	         document.getElementById("dataDiv").style.left = (document.body.clientWidth-divWidth)/2;
	         Element.hide('dataDetailLeftDiv');
		      Element.hide('dataDetailRightDiv');
		      document.getElementById("resultIframe").className = "epm-j-jieguo";
		      document.getElementById("resultIframe").innerHTML="";
		      Element.hide('queryDiv');
		      Element.show('dataDiv');
      	 }
      }else if(flag=='update'){
      Element.show('addwholespan');
        Element.show('addperiodspan');
      	opt='update';
        var hashvo = new ParameterSet();
	    hashvo.setValue("p0100",document.getElementById("p0100").value);
	    hashvo.setValue("record_num",document.getElementById("record_num").value);
	    var request=new Request({method:'post',asynchronous:false,onSuccess:returnUpdate,functionId:'302001020605'},hashvo);
        //var divHeight = document.getElementById("dataDiv").style.height;
         //var divWidth = document.getElementById("dataDiv").style.width;
	     var divHeight = 420+"px";
         var divWidth = 403+"px";
         divHeight = divHeight.substring(0,divHeight.length-2);
         divWidth = divWidth.substring(0,divWidth.length-2);
         document.getElementById("dataDiv").style.top = (document.body.clientHeight-divHeight)/2+parseInt(document.body.scrollTop,10);
         document.getElementById("dataDiv").style.left = (document.body.clientWidth-divWidth)/2;
         Element.hide('dataDetailLeftDiv');
	      Element.hide('dataDetailRightDiv');
	      document.getElementById("resultIframe").className = "epm-j-jieguo";
	      document.getElementById("resultIframe").innerHTML="";
	      Element.hide('queryDiv');
	      Element.show('dataDiv');
      }
    }
function showSearchDiv(state,flag){
    if(flag=='query'){
      	 document.getElementById("queryTitle").value="";
         document.getElementById("queryStart_time").value="";
         document.getElementById("queryEnd_time").value="";
         document.getElementById("queryContent").value="";
         document.getElementById("queryStartHour").options[0].selected=true;
         document.getElementById("queryEndHour").options[document.getElementById("queryEndHour").options.length-1].selected=true;
         document.getElementById("queryStartMinute").options[0].selected=true;
         document.getElementById("queryEndMinute").options[document.getElementById("queryEndMinute").options.length-1].selected=true;
         var divHeight = 420+"px";
         var divWidth = 403+"px";
         divHeight = divHeight.substring(0,divHeight.length-2);
         divWidth = divWidth.substring(0,divWidth.length-2);
         document.getElementById("queryDiv").style.top = (document.body.clientHeight-divHeight)/2+parseInt(document.body.scrollTop,10);
         document.getElementById("queryDiv").style.left = (document.body.clientWidth-divWidth)/2;
         frompage = state;
         Element.hide('dataDetailLeftDiv');
	     Element.hide('dataDetailRightDiv');
	     Element.hide('dataDiv');
	     Element.show('queryDiv');
      }
}
function queryData(a){
    var queryTitle = getEncodeStr(document.getElementById("queryTitle").value);
    var fromyear = document.getElementById("fromyear").value;
    var frommonth = document.getElementById("frommonth").value;
    var fromday = document.getElementById("fromday").value;
    var nbase = document.getElementById("nbase").value;
    var a0100 = document.getElementById("a0100").value;
    var queryStart_time = document.getElementById("queryStart_time").value;
    var queryEnd_time = document.getElementById("queryEnd_time").value;
    var queryStartHour = document.getElementById("queryStartHour").value;
    var queryEndHour = document.getElementById("queryEndHour").value;
    var queryStartMinute = document.getElementById("queryStartMinute").value;
    var queryEndMinute = document.getElementById("queryEndMinute").value;
    var queryContent = getEncodeStr(document.getElementById("queryContent").value);
    var hashvo = new ParameterSet();
	hashvo.setValue("queryTitle",queryTitle);
	hashvo.setValue("queryStart_time",queryStart_time);
	hashvo.setValue("queryEnd_time",queryEnd_time);
	hashvo.setValue("queryStartHour",queryStartHour);
	hashvo.setValue("queryEndHour",queryEndHour);
	hashvo.setValue("queryStartMinute",queryStartMinute);
	hashvo.setValue("queryEndMinute",queryEndMinute);
	hashvo.setValue("queryContent",queryContent);
	hashvo.setValue("frompage",frompage);
	hashvo.setValue("fromyear",fromyear);
	hashvo.setValue("frommonth",frommonth);
	hashvo.setValue("fromday",fromday);
	hashvo.setValue("nbase",nbase);
	hashvo.setValue("a0100",a0100);
	hashvo.setValue("a",a);
    var request=new Request({method:'post',asynchronous:false,onSuccess:querysuccess,functionId:'302001020621'},hashvo);
}
function querysuccess(outparamters){
    var tableHtml = outparamters.getValue("tableHtml");
    tableHtml=getDecodeStr(tableHtml);
    if(tableHtml.indexOf("<table")!=-1)
         document.getElementById("resultIframe").className = "epm-j-jieguo1";
    else
         document.getElementById("resultIframe").className = "epm-j-jieguo";
    document.getElementById("resultIframe").innerHTML=tableHtml;
}
function check(flag,p0100,record_num){
      //验证格式
      if(flag=='add'||flag=='update'||flag=='continue'){
           if(trim(document.getElementById("title").value).length<=0)
		   {
		      alert("请您填写标题！");
		      return;
		   }
		   if(trim(document.getElementById("title").value).length>50)
		   {
		      alert("标题长度过长！请限制在50汉字以内");
		      return;
		   }
           var stime = document.getElementById("start_time").value;
           var etime = document.getElementById("end_time").value;
           if(trim(stime).length<=0)
		   {
		      alert("请您选择开始时间！");
		      return;
		   }
		     if(trim(etime).length<=0)
		   {
		      alert("请您选择结束时间！");
		      return;
		   }
		   var reg = /^(\d{4})((-|\.)(\d{1,2}))((-|\.)(\d{1,2}))$/;;
			if(!reg.test(stime))
			{
				alert(STARTTIME_FORMAT+"！");
				return;
			}
			if(!reg.test(etime))
			{
				alert(ENDTIME_FORMAT+"！");
				return;
			}
			var syear = stime.substring(0,4);
			var smonth=stime.substring(5,7);
			var sday=stime.substring(8);
			var shour = document.getElementById("startHour").value;
			var sminute = document.getElementById("startMinute").value;
			//if(!isValidDate(sday,smonth,syear))
			//{
			 //  alert(tableStr);
			 //  alert("起始时间的时间范围不正确,请注意年，月，日的有效性！");
			 //  return;
			//}
			var eyear = etime.substring(0,4);
			var emonth=etime.substring(5,7);
			var eday=etime.substring(8);
			
			var ehour = document.getElementById("endHour").value;
			var eminute = document.getElementById("endMinute").value;
			//if(!isValidDate(eday, emonth, eyear))
			//{
			  // alert("结束时间的时间范围不正确,请注意年，月，日的有效性！");
			   //return;
			//}
//			alert(parseInt(syear)>parseInt(eyear));
//			alert(parseInt(smonth)>parseInt(emonth));
//			alert(parseInt(sday)>parseInt(eday));
//			alert(parseInt(shour)>parseInt(ehour));
//			alert(parseInt(sminute)>parseInt(eminute));
			//alert(eday);
			//alert(eday*1);
			if(parseInt(syear,10)>parseInt(eyear,10)||(parseInt(syear,10)==parseInt(eyear,10)&&parseInt(smonth,10)>parseInt(emonth,10))||(parseInt(syear,10)==parseInt(eyear,10)&&parseInt(smonth,10)==parseInt(emonth,10)&&parseInt(sday,10)>parseInt(eday,10))||(parseInt(syear,10)==parseInt(eyear,10)&&parseInt(smonth,10)==parseInt(emonth,10)&&parseInt(sday,10)==parseInt(eday,10)&&parseInt(shour,10)>parseInt(ehour,10)) ||(parseInt(syear,10)==parseInt(eyear,10)&&parseInt(smonth,10)==parseInt(emonth,10)&&parseInt(sday,10)==parseInt(eday,10)&&parseInt(shour,10)==parseInt(ehour,10)&&parseInt(sminute,10)>parseInt(eminute,10)))
			{
			    alert(ENDTIME_LARGER_STARTTIME+"！");
			    return;
			}
      
      }
      if(flag=='add'){
        var hashvo = new ParameterSet();
	    hashvo.setValue("title",document.getElementById("title").value);
	    if(document.getElementById("type").checked){
            document.getElementById("type").value="0";
         }else{
            document.getElementById("type").value="1";
         }
	    hashvo.setValue("flag",flag);
	    hashvo.setValue("type",document.getElementById("type").value);
	    hashvo.setValue("start_time",document.getElementById("start_time").value);
	    hashvo.setValue("startHour",document.getElementById("startHour").value);
	    hashvo.setValue("startMinute",document.getElementById("startMinute").value);
	    hashvo.setValue("end_time",document.getElementById("end_time").value);
	    hashvo.setValue("endHour",document.getElementById("endHour").value);
	    hashvo.setValue("endMinute",document.getElementById("endMinute").value);
	    hashvo.setValue("content",getEncodeStr(document.getElementById("contentid").value));
	    var request=new Request({method:'post',asynchronous:false,onSuccess:success,functionId:'302001020603'},hashvo);
      }else if(flag=='update'){
        var hashvo = new ParameterSet();
        hashvo.setValue("flag",flag);
        hashvo.setValue("title",document.getElementById("title").value);
        hashvo.setValue("p0100",p0100);
        hashvo.setValue("record_num",record_num);
        if(document.getElementById("type").checked){
            document.getElementById("type").value="0";
         }else{
            document.getElementById("type").value="1";
         }
	    hashvo.setValue("type",document.getElementById("type").value);
	    hashvo.setValue("start_time",document.getElementById("start_time").value);
	    hashvo.setValue("startHour",document.getElementById("startHour").value);
	    hashvo.setValue("startMinute",document.getElementById("startMinute").value);
	    hashvo.setValue("end_time",document.getElementById("end_time").value);
	    hashvo.setValue("endHour",document.getElementById("endHour").value);
	    hashvo.setValue("endMinute",document.getElementById("endMinute").value);
	    hashvo.setValue("content",getEncodeStr(document.getElementById("contentid").value));
        var request=new Request({method:'post',asynchronous:false,onSuccess:success,functionId:'302001020606'},hashvo);
      }else if(flag=='continue'){
        var hashvo = new ParameterSet();
	    hashvo.setValue("title",document.getElementById("title").value);
	    if(document.getElementById("type").checked){
            document.getElementById("type").value="0";
         }else{
            document.getElementById("type").value="1";
         }
	    hashvo.setValue("flag",flag);
	    hashvo.setValue("type",document.getElementById("type").value);
	    hashvo.setValue("start_time",document.getElementById("start_time").value);
	    hashvo.setValue("startHour",document.getElementById("startHour").value);
	    hashvo.setValue("startMinute",document.getElementById("startMinute").value);
	    hashvo.setValue("end_time",document.getElementById("end_time").value);
	    hashvo.setValue("endHour",document.getElementById("endHour").value);
	    hashvo.setValue("endMinute",document.getElementById("endMinute").value);
	    hashvo.setValue("content",getEncodeStr(document.getElementById("contentid").value));
	    var request=new Request({method:'post',asynchronous:false,onSuccess:success,functionId:'302001020603'},hashvo);
      }else if(flag=='delete'){
        if(!confirm('确定要删除吗?')){
            return;
        }
        var hashvo = new ParameterSet();
        hashvo.setValue("flag",flag);
        hashvo.setValue("p0100",p0100);
        hashvo.setValue("record_num",record_num);
        var request=new Request({method:'post',asynchronous:false,onSuccess:success,functionId:'302001020607'},hashvo);
      }
    }
function success(outparamters){
	var flag = outparamters.getValue("flag");
	var href = window.location.href;
	var myDate = new Date();
	//alert(window.location.href);
	if(flag=='add'){
		//document.URL = href;
		self.location=href;
		alert("保存成功！");
	}else if(flag=='update'){
		//document.URL = href;
		self.location=href;
		alert("保存成功！");
	}else if(flag=='delete'){
		//document.URL = href;
		self.location=href;
		alert("删除成功！");
	}else if(flag=='continue'){
		alert("保存成功！");
		document.getElementById("title").value="";
		document.getElementById("type").value="1";
         document.getElementById("type").checked=false;
         if(compareDate=='')
         compareDate = myDate.getFullYear()+"-"+(parseInt((myDate.getMonth()+1),10)<10?"0"+(myDate.getMonth()+1):myDate.getMonth()+1)+"-"+(parseInt(myDate.getDate(),10)<10?"0"+(myDate.getDate()):myDate.getDate());
         document.getElementById("start_time").value = myDate.getFullYear()+"-"+(parseInt((myDate.getMonth()+1),10)<10?"0"+(myDate.getMonth()+1):myDate.getMonth()+1)+"-"+(parseInt(myDate.getDate(),10)<10?"0"+(myDate.getDate()):myDate.getDate());
        document.getElementById("end_time").value = myDate.getFullYear()+"-"+(parseInt((myDate.getMonth()+1),10)<10?"0"+(myDate.getMonth()+1):myDate.getMonth()+1)+"-"+(parseInt(myDate.getDate(),10)<10?"0"+(myDate.getDate()):myDate.getDate());
         document.getElementById("contentid").value="";
         for(var i=0;i<document.getElementById("startHour").options.length;i++)
		 {
			if(document.getElementById("startHour").options[i].value==myDate.getHours())
			   document.getElementById("startHour").options[i].selected=true;
		 }
         for(var i=0;i<document.getElementById("startMinute").options.length;i++)
		 {
			if(document.getElementById("startMinute").options[i].value==myDate.getMinutes())
			   document.getElementById("startMinute").options[i].selected=true;
		 }
         //document.getElementById("startHour").options[0].selected=true;
         document.getElementById("endHour").options[document.getElementById("endHour").options.length-1].selected=true;
        // document.getElementById("startMinute").options[0].selected=true;
         document.getElementById("endMinute").options[document.getElementById("endMinute").options.length-1].selected=true;
         Element.show('startTimeDiv');
         Element.show('endTimeDiv');
         isContinue = 1;
         return false;
	}
	isContinue=0;
	self.location=href;
    
}
function showDetail(isOwner,p0100,record_num){
	  if(document.getElementById("dataDiv").style.display=='none'&&document.getElementById("queryDiv").style.display=='none')
	  {
      var hashvo = new ParameterSet();
	  hashvo.setValue("isOwner",isOwner);
	  hashvo.setValue("p0100",p0100);
	  hashvo.setValue("record_num",record_num);
	  var request=new Request({method:'post',asynchronous:false,onSuccess:detailsuccess,functionId:'302001020604'},hashvo);
	  }else{ 
	   return false;
	  }
    }
String.prototype.replaceAll = function(s1,s2) { 
    return this.replace(new RegExp(s1,"gm"),s2); 
}
function detailsuccess(outparamters){
      var isOwner = outparamters.getValue("isOwner");
      var p0100 = outparamters.getValue("p0100");
      var record_num = outparamters.getValue("record_num");
      var content = getDecodeStr(outparamters.getValue("content"));
      var title = outparamters.getValue("title");
      var type = outparamters.getValue("type");
      var timeDetail = outparamters.getValue("timeDetail");
      document.getElementById("p0100").value = p0100;
      document.getElementById("record_num").value = record_num;
      
      
      //var divHeight = document.getElementById("dataDetailLeftDiv").style.height;
      var divHeight = 152+"px";
      //var divWidth = document.getElementById("dataDetailLeftDiv").style.width;
      var divWidth = 365+"px";
      divHeight = divHeight.substring(0,divHeight.length-2);
      divWidth = divWidth.substring(0,divWidth.length-2);
      var x = parseInt(window.event.clientX,10)+parseInt(document.body.scrollLeft,10);
      var y = parseInt(window.event.clientY,10)+parseInt(document.body.scrollTop,10);
      var upflag ='';
      var leftflag='';
      if(parseInt(x,10)<=document.body.clientWidth/2){
         leftflag='1';
      }else{
         leftflag='2';
      }
      if(leftflag=='1'){
      	document.getElementById("leftTitleDetail").innerHTML=title;
        document.getElementById("leftTimeDetail").innerHTML=timeDetail;
        document.getElementById("leftContentDetail").value=content;
      	 document.getElementById("dataDetailLeftDiv").style.top=(parseInt(y,10)-divHeight/2)>0?parseInt(y,10)-divHeight/2:0;
      	 document.getElementById("dataDetailLeftDiv").style.left=parseInt(x,10)+30;
         
         var divTop = document.getElementById("dataDetailLeftDiv").style.top;
	      var divLeft = document.getElementById("dataDetailLeftDiv").style.left;
	      divTop = divTop.substring(0,divTop.length-2);
	      divLeft = divLeft.substring(0,divLeft.length-2);
	      Element.hide('dataDiv');
	      document.getElementById("resultIframe").className = "epm-j-jieguo";
	      document.getElementById("resultIframe").innerHTML="";
	      Element.hide('queryDiv');
	      Element.hide('dataDetailRightDiv');
	      if(isOwner=='1')
	         Element.hide('leftEditButton');
	      Element.show('dataDetailLeftDiv');
      }else{
      	 document.getElementById("rightTitleDetail").innerHTML=title;
         document.getElementById("rightTimeDetail").innerHTML=timeDetail;
         document.getElementById("rightContentDetail").value=content;
      	 document.getElementById("dataDetailRightDiv").style.top=(parseInt(y,10)-divHeight/2)>0?parseInt(y,10)-divHeight/2:0;
         document.getElementById("dataDetailRightDiv").style.left=parseInt(x,10)-parseInt(divWidth,10)-60;
         var divTop = document.getElementById("dataDetailRightDiv").style.top;
	      var divLeft = document.getElementById("dataDetailRightDiv").style.left;
	      
	      divTop = divTop.substring(0,divTop.length-2);
	      divLeft = divLeft.substring(0,divLeft.length-2);
	      Element.hide('dataDiv');
	      document.getElementById("resultIframe").className = "epm-j-jieguo";
	      document.getElementById("resultIframe").innerHTML="";
	      Element.hide('queryDiv');
	      Element.hide('dataDetailLeftDiv');
	      if(isOwner=='1')
	         Element.hide('rightEditButton');
	      Element.show('dataDetailRightDiv');
      }
    }
function blurDiv(){
      document.getElementById("titleDetail").innerHTML="";
      document.getElementById("timeDetail").innerHTML="";
      document.getElementById("contentDetail").value="";
      document.getElementById("p0100").value = "";
      document.getElementById("record_num").value = "";
      Element.hide('dataDetailLeftDiv');
    }
function returnUpdate(outparamters){
      var p0100 = outparamters.getValue("p0100");
      var record_num = outparamters.getValue("record_num");
      var content = getDecodeStr(outparamters.getValue("content"));
      var title = outparamters.getValue("title");
      var type = outparamters.getValue("type");
      var start_time = outparamters.getValue("start_time");
      var end_time = outparamters.getValue("end_time");
      var startHour = outparamters.getValue("startHour");
      var endHour = outparamters.getValue("endHour");
      var startMinute = outparamters.getValue("startMinute");
      var endMinute = outparamters.getValue("endMinute");
      document.getElementById("contentid").value = content;
      document.getElementById("title").value = title;
      document.getElementById("start_time").value = start_time;
      document.getElementById("end_time").value = end_time;
      for(var i=0;i<document.getElementById("startHour").options.length;i++)
		 {
			if(document.getElementById("startHour").options[i].value==startHour)
			   document.getElementById("startHour").options[i].selected=true;
		 }
	  for(var i=0;i<document.getElementById("endHour").options.length;i++)
		 {
			if(document.getElementById("endHour").options[i].value==endHour)
			   document.getElementById("endHour").options[i].selected=true;
		 }
      for(var i=0;i<document.getElementById("startMinute").options.length;i++)
		 {
			if(document.getElementById("startMinute").options[i].value==startMinute)
			   document.getElementById("startMinute").options[i].selected=true;
		 }
	  for(var i=0;i<document.getElementById("endMinute").options.length;i++)
		 {
			if(document.getElementById("endMinute").options[i].value==endMinute)
			   document.getElementById("endMinute").options[i].selected=true;
		 }
      if(type=='0'){
         document.getElementById("type").checked=true;
         Element.hide('startTimeDiv');
         Element.hide('endTimeDiv');
      }else{
         document.getElementById("type").checked=false;
         Element.show('startTimeDiv');
         Element.show('endTimeDiv');
      }
      document.getElementById("addOrEdit").innerHTML="编辑";
      document.getElementById("okOrCancel").innerHTML="<input type='button' class=\"epm-baocun\" value='保存'  onclick=\"check('update','"+p0100+"','"+record_num+"');\">&nbsp;&nbsp;<input class=\"epm-baocun\" type='button' value='删除' onclick=\"check('delete','"+p0100+"','"+record_num+"');\">&nbsp;&nbsp;<input class=\"epm-baocun\" type='button' value='取消' onclick=\"closeDiv('dataDiv')\">";
      Element.hide('dataDetailLeftDiv');
      Element.hide('dataDetailRightDiv');
      document.getElementById("resultIframe").className = "epm-j-jieguo";
      document.getElementById("resultIframe").innerHTML="";
      Element.hide('queryDiv');
	  Element.show('dataDiv');
    }
function showOrHideHour(obj){
	var myDate = new Date();
      if(obj.checked){
        Element.hide('startTimeDiv');
        Element.hide('endTimeDiv');
        if(opt=='add'){
        if(compareDate=='')
        compareDate = myDate.getFullYear()+"-"+(parseInt((myDate.getMonth()+1),10)<10?"0"+(myDate.getMonth()+1):myDate.getMonth()+1)+"-"+(parseInt(myDate.getDate(),10)<10?"0"+(myDate.getDate()):myDate.getDate());
          document.getElementById("start_time").value = myDate.getFullYear()+"-"+(parseInt((myDate.getMonth()+1),10)<10?"0"+(myDate.getMonth()+1):myDate.getMonth()+1)+"-"+(parseInt(myDate.getDate(),10)<10?"0"+(myDate.getDate()):myDate.getDate());
        document.getElementById("end_time").value = myDate.getFullYear()+"-"+(parseInt((myDate.getMonth()+1),10)<10?"0"+(myDate.getMonth()+1):myDate.getMonth()+1)+"-"+(parseInt(myDate.getDate(),10)<10?"0"+(myDate.getDate()):myDate.getDate());
        }
      }else{
         Element.show('startTimeDiv');
         Element.show('endTimeDiv');
         //document.getElementById("start_time").value = '';
         //document.getElementById("end_time").value = '';
      }
    }
function tr_changeColor(objTr,bgcolor)
{
	/*
	if(curObjTr!=null)
		curObjTr.style.background=oldObjTr_c;
	curObjTr=objTr;
	oldObjTr_c=bgcolor;
	curObjTr.style.background='FFF8D2';
	* */
	if(curObjTr!=null)
		curObjTr.style.backgroundColor="";
	curObjTr=objTr;
	oldObjTr_c="FFF8D2";
	curObjTr.style.backgroundColor=bgcolor;		 
	//curObj.style.color='#ffdead'; 
}    
/*月报 end*/

function exportDiary(state,syear,smonth,sday,eyear,emonth,eday){
      var hashvo = new ParameterSet();
	  hashvo.setValue("state",state);
	  hashvo.setValue("syear",syear);
	  hashvo.setValue("smonth",smonth);
	  hashvo.setValue("sday",sday);
	  hashvo.setValue("eyear",eyear);
	  hashvo.setValue("emonth",emonth);
	  hashvo.setValue("eday",eday);
	  var nbase = document.getElementById("nbase").value;
      var a0100 = document.getElementById("a0100").value;
      hashvo.setValue("nbase",nbase);
      hashvo.setValue("a0100",a0100);
	  var request=new Request({method:'post',asynchronous:false,onSuccess:exportsuccess,functionId:'302001020620'},hashvo);
}
function exportsuccess(outparamters){
	var outName=outparamters.getValue("outName");
	outName=getDecodeStr(outName);
	var name=outName.substring(0,outName.length-1)+".xls";
	name=getEncodeStr(name);
	window.location.target="_blank";
	window.location.href = "/servlet/vfsservlet?fromjavafolder=true&fileid="+name;
}
function hideDiv(divId){
   if(divId=='queryDiv'){
      document.getElementById("resultIframe").className = "epm-j-jieguo";
      document.getElementById("resultIframe").innerHTML="";
   }
   Element.hide(divId);
}
function moveInit(divID,evt){
   _IsMousedown = 1;
    var obj=document.getElementById(divID);
    _ClickLeft = evt.x-parseInt(obj.style.left,10);
    _ClickTop = evt.y-parseInt(obj.style.top,10);
}
function move(divID,evt){
   if(_IsMousedown==0){
      return;
   }
   var obj=document.getElementById(divID);
   obj.style.left = evt.x-_ClickLeft;
   obj.style.top = evt.y-_ClickTop;
}
function stopMove(divID,evt){
   _IsMousedown=0;
}
function closeDiv(divID){
   Element.hide(divID);
   if(isContinue==1){
     isContinue=0;
     //document.URL = window.location.href;
     var href = window.location.href;
     self.location=href;
   }
}
