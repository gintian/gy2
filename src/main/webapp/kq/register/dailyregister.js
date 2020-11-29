function go_creat()
{
	var target_url="/kq/register/daily_registerdata.do?b_link=link";
	if (!isIE6()) {
		var winFeatures = "dialogWidth:440px; dialogHeight:350px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes"; 	
	}
	else
		var winFeatures = "dialogWidth:440px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes";
	var return_vo= window.showModalDialog(target_url,1, winFeatures);
	  
	if(!return_vo)
	  return false;	  
     
	if(return_vo.pick_type!='')
	{
      var waitInfo=eval("wait");	   
      waitInfo.style.display="block";	
    
      checkAnalalyseProgress();
      var tul="/kq/register/daily_registerdata.do?b_creat=link&creat_type="+return_vo.creat_type+"&start_date="+return_vo.start+"&end_date="+return_vo.end+"&creat_pick="+return_vo.creat_pick;
    
      dailyRegisterForm.action="/kq/register/daily_registerdata.do?b_creat=link&creat_type="+return_vo.creat_type+"&start_date="+return_vo.start+"&end_date="+return_vo.end+"&creat_pick="+return_vo.creat_pick+"&creat_state="+return_vo.creat_state;
      dailyRegisterForm.submit();
      
      
	}
}

var checkTimes = 0;
function checkAnalalyseProgress() {
    checkTimes++;

    //最初一段时间是在准备数据，记录数不准确，暂时延后1分钟再取数
    if (checkTimes > 1) {
		 var hashvo=new ParameterSet();
	     hashvo.setValue("tran","3");
	     var request=new Request({method:'post',asynchronous:false,onSuccess:analyseProgress,functionId:'152110013124'},hashvo);
    }
    
	setTimeout("checkAnalalyseProgress()",10000);
}

function analyseProgress(outparamters) {	 
	 var flag = outparamters.getValue("flag");
	 if ("begin" == flag) {
		 return;
	 }

	 // 已生成日明细，避免页面超时，强制刷新一下
	 if ("finished" == flag) {	      
		 change();
		 return;
	 }

	 if ("error" == flag) {
		 alert("处理发生错误：" + outparamters.getValue("error_info"));
		 change();
		 return;
	 }
}
 
function go_count()
{
         var target_url;
         var winFeatures = "dialogHeight:300px; dialogLeft:250px;"; 
         target_url="/kq/register/count_register.do?b_select=link";
         //newwindow=window.open(target_url,'rr','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=no,resizable=no,top=170,left=220,width=596,height=354'); 
          var return_vo= window.showModalDialog(target_url,1, 
        "dialogWidth:500px;dialogHeight:290px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes;");
         if(!return_vo)
	      return false;	   
	    if(return_vo.count_type!='')
	    {
	       var waitInfo=eval("wait");	   
	       waitInfo.style.display="block";	
	       var tul="/kq/register/count_register.do?b_count=link&count_type="+return_vo.count_type+"&count_start="+return_vo.count_start+"&count_end="+return_vo.count_end;
	       dailyRegisterForm.action=tul;
	       dailyRegisterForm.submit();
	     }
}
function selectKq(){
	var winFeatures = "dialogWidth:740px; dialogHeight:375px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes"
	var target_url = "/kq/query/searchfiled.do?b_init=link`table=q03";
	if($URL)
        target_url = $URL.encode(target_url);
	var iframe_url = "/general/query/common/iframe_query.jsp?src="+target_url;
	var return_vo= window.showModalDialog(iframe_url,1,winFeatures);
	if(return_vo){
	   dailyRegisterForm.action="/kq/register/daily_registerdata.do?b_query=link&select_flag=2&selectResult="+$URL.encode(return_vo);
       dailyRegisterForm.submit();
	}
}  
function pickup_data(code,kind)
{
      var target_url;
      var winFeatures = "dialogHeight:300px; dialogLeft:250px;"; 
      target_url="/kq/register/daily_registerdata.do?b_pickupdate=link";
       var return_vo= window.showModalDialog(target_url,1, 
        "dialogWidth:500px; dialogHeight:230px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
        if(!return_vo)
		   return false;	   
	if(return_vo.pick_type!='')
	{
	    var waitInfo=eval("wait");	   
	    waitInfo.style.display="block";	      
	    dailyRegisterForm.action="/kq/register/daily_registerdata.do?b_pickup=link&code="+code+"&kind="+kind+"&pick_type="+return_vo.pick_type+"&start_date="+return_vo.start+"&end_date="+return_vo.end;
	    dailyRegisterForm.submit();
	}
} 
function MusterInitData()
   {
	   var waitInfo=eval("wait");	
	   waitInfo.style.display="none";
   }
   var check_obj;
   function checkValue(obj,item,nbase,a0100,q03z0)
   {
      check_obj=obj;
      var values=obj.value;
      if(values=="")
      {
        values="0";
      }else
      {
      	if (isNaN(values)) {
      			alert("只能是数字");
              obj.value="";
              check_obj.focus(); 
              return false;
      	}
        for(var i=0; i<values.length;i++)
        {          
           var code= values.charCodeAt(i);
           if(isNaN(code))
           {
              alert("只能是数字");
              obj.value="";
              check_obj.focus(); 
              return false;
           }
        }              
      }
      var hashvo=new ParameterSet();
      hashvo.setValue("kq_value",values);
      hashvo.setValue("kq_item",item);
      hashvo.setValue("nbase",nbase);
      hashvo.setValue("a0100",a0100);
      hashvo.setValue("q03z0",q03z0);
      hashvo.setValue("table","q03");
      var request=new Request({method:'post',asynchronous:false,onSuccess:showCheckFlag,functionId:'15301110077'},hashvo);
   }
   function showCheckFlag(outparamters)
   {
      var check_flag=outparamters.getValue("flag");
      var check_mess=outparamters.getValue("mess");
      if(check_flag=="false")
      {
         alert(check_mess);         
         check_obj.focus(); 
         check_obj.value = "";
      }
   } 
  function IsDigit() 
  { 
    return ((event.keyCode >= 46) && (event.keyCode <= 57)); 
  }
   function showCollect()
   {
      dailyRegisterForm.action="/kq/register/select_collect.do?b_search=link&action=select_collectdata.do&target=mil_body&flag=noself&viewPost=kq";
      dailyRegisterForm.target="il_body";
      dailyRegisterForm.submit();
   }
    function show_ambiquity()
   {
       dailyRegisterForm.action="/kq/register/ambiquity/search_ambiquity.do?b_search=link&action=search_ambiquitydata.do&target=mil_body&flag=noself&viewPost=kq";
       dailyRegisterForm.target="il_body";
       dailyRegisterForm.submit();
   }    
   function ambiquity()
   {
       var target_url;
       target_url="/kq/register/ambiquity/select_ambiquity.do?b_select=link";
       var winFeatures = "dialogHeight:300px; dialogLeft:250px;"; 
        var return_vo= window.showModalDialog(target_url,1, 
        "dialogWidth:360px; dialogHeight:260px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
        if(!return_vo)
		   return false;	   
		if(return_vo.flag=true)
		{
		
			var len=document.dailyRegisterForm.elements.length;
       		var i;
        	for (i = 0;i < len; i++) {
         		if (document.dailyRegisterForm.elements[i].type == "checkbox") {             
            		document.dailyRegisterForm.elements[i].checked=false;
          		}
        	}
        
		    var stat_end=return_vo.stat_end;
		    var stat_start=return_vo.stat_start;
		    var waitInfo=eval("wait");	   
	        waitInfo.style.display="block";	
	        dailyRegisterForm.action="/kq/register/ambiquity/select_ambiquity.do?b_stat=link&action=select_ambiquitydata.do&target=mil_body&viewPost=kq&stat_start="+stat_start+"&stat_end="+stat_end;
		    dailyRegisterForm.target="il_body";
		    dailyRegisterForm.submit();
		} 
   }
     
   function go_search()
   {
      dailyRegisterForm.action="/kq/register/search_register.do?b_search=link&action=search_registerdata.do&target=mil_body";
      dailyRegisterForm.target="il_body";
      dailyRegisterForm.submit();
   }
   function sing_count()
   {
       if(confirm("请确认更改数据是否已保存!"))
       {
         dailyRegisterForm.action="/kq/register/sing_oper/singcountdata.do?b_saveselect=link";
         dailyRegisterForm.submit();
       }
   }
   function sing_collect()
   {
       if(confirm("请确认更改数据是否已保存!"))
      {
         dailyRegisterForm.action="/kq/register/sing_oper/singcollectdata.do?b_saveselect=link";
         dailyRegisterForm.submit();
      }
   }
    function sing_pickup()
   {
      dailyRegisterForm.action="/kq/register/sing_oper/singpickdata.do?b_saveselect=link";
      dailyRegisterForm.submit();
   }
   function sing_operation()
   {
           dailyRegisterForm.action="/kq/register/sing_oper/sing_operation.do?b_saveselect=link";
           dailyRegisterForm.submit();
   } 
   function indicator()
   {
       var target_url;
       var winFeatures = "dialogHeight:300px; dialogLeft:250px;"; 
       target_url="/kq/register/indicator/indicator.do?b_query=link&re_flag=1";
       newwindow=window.open(target_url,'rr','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=no,top=170,left=220,width=596,height=354'); 
   }
   // 去掉勾选
	function delChecked() {
   		var input = document.getElementsByTagName("input");
   		for (i = 0; i < input.length; i++) {
   			if (input[i].type == "checkbox" && input[i].checked == true) {
   				input[i].checked = false;
   			}
   		}
   	}
   function return_overrule()
   {
       var len=document.dailyRegisterForm.elements.length;
       var isCorrect=false;
       for (i=0;i<len;i++)
       {
           if (document.dailyRegisterForm.elements[i].type=="checkbox")
            {
              if( document.dailyRegisterForm.elements[i].checked==true && document.dailyRegisterForm.elements[i].name != 'selbox')
                isCorrect=true;
            }
       }
       if(!isCorrect)
       {
          alert("请选择人员");
          return false;
       }
       //dailyRegisterForm.action="/kq/register/select_collectdata.do?b_saveover=link";
       //dailyRegisterForm.submit();
       var target_url;
       var winFeatures = "dialogHeight:300px; dialogLeft:200px;"; 
       target_url="/kq/register/select_collectdata.do?br_saveover=link&flag=day&sb=new";
       //newwindow=window.open(target_url,'glWin','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=no,resizable=no,top=170,left=220,width=596,height=354'); 
       return_vo= window.showModalDialog(target_url,1, 
        "dialogWidth:540px; dialogHeight:350px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
       if(!return_vo)
	    return false;	
       if(return_vo.save=="1")
       {
           var overrule=return_vo.text;
           var o_obj=document.getElementById('overrule');
           o_obj.value=overrule;
           dailyRegisterForm.action="/kq/register/daily_registerdata.do?b_rule=link";
           dailyRegisterForm.target="mil_body";
           dailyRegisterForm.submit();
       }
   }
   function getdate(tt)
   {
       var strvalue=tt.value;
       strvalue=strvalue.replace(/\-/g,".");       
       tt.value=strvalue;
   }
   function kq_batch()
   {
   		var winFeatures = "dialogHeight:400px; dialogLeft:250px;";
   		var target_url = "/kq/register/daily_registerdata.do?b_batch=link";
   		var iframe_url="/general/query/common/iframe_query.jsp?src="+target_url;
   		var return_vo= window.showModalDialog(iframe_url,1, 
        				"dialogWidth:420px; dialogHeight:200px;resizable:no;center:yes;scroll:yes;status:no");
   		if(return_vo)
		{
	    var obj = new Object();
	    obj.type=return_vo.type;
	    if(obj.type=="1")
	    {
	      dailyRegisterForm.action="/kq/register/daily_registerdata.do?b_query=link";
	      dailyRegisterForm.submit();
	    }
		}
   }
   function quitRe()
   {
      window.location="/kq/register/daily_register.do?br_quit=link";
   }
   function fashion_set(flag)
   {
   	 var dh="390px";
   	 var wh="350px";
	 if(navigator.appVersion.indexOf('MSIE 6') != -1){
		dh="430px";
		wh="350px";
	 }
      var target_url="";
      var return_vo;
      if(flag=="0")
      {
         target_url="/kq/options/adjustcode/adjustcode.do?b_order=link&table=q03&flag=1&isSave=no";
         return_vo= window.showModalDialog(target_url,1, 
        "dialogWidth:"+wh+"; dialogHeight:"+dh+";resizable:no;center:yes;scroll:no;status:no;scrollbars:yes");
      }else if(flag=="1")
      {
         
         target_url="/kq/options/adjustcode/adjustcode.do?b_hideview=link`table=q03`flag=1`isSave=no";
         if($URL)
             target_url = $URL.encode(target_url);
         var iframe_url="/general/query/common/iframe_query.jsp?src="+target_url;
         return_vo= window.showModalDialog(iframe_url,1, 
        "dialogWidth:"+wh+"; dialogHeight:"+dh+";resizable:no;center:yes;scroll:no;status:no;scrollbars:no");
      }
      if(return_vo)
        change();
   }
//customTemplate
	function customTemp(){
		var dh="370px";
		var wh="460px";
		var target_url="";
		var return_vo;
		target_url = "/kq/register/daily_register.do?b_customTemp=link" + $URL.encode("`tablename=q03`flag=1`isSave=no");
		return_vo = window.showModalDialog(target_url,1,
		"dialogWidth:"+wh+";dialogHeight:"+dh+";resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
		if(return_vo){
			var hashvo=new ParameterSet();	
			hashvo.setValue("indexId",return_vo.indexId);
			hashvo.setValue("indexName",return_vo.indexName);
			var request=new Request({method:'post',asynchronous:false,onSuccess:showfile,functionId:'15301110104'},hashvo);
			function showfile(outparamters){
				var outName=outparamters.getValue("outName");
				outName=getDecodeStr(outName);
				window.location.target="mil_body";
				window.location.href = "/servlet/vfsservlet?fileid=" + outName +"&fromjavafolder=true";
			}
		}
	}
   
//导入模板数据
function exportTempData()
{
	document.dailyRegisterForm.action="/kq/register/daily_register.do?br_import=init&tablename=Q03";
  	document.dailyRegisterForm.submit();
}
var BoxWidth;
var DataTitles,LockCols;
var BoxHeight;
function boxControl(boxheight,boxwidth,titles,lockCols,longStr)
{
    BoxWidth=boxwidth;
    BoxHeight=boxheight;
    DataTitles=titles;
    LockCols=lockCols+1; 
    var widthS_lock=countWidth(longStr);   
    WriteTable(widthS_lock);
}
function WriteTable(widthS_lock){    // 写入表格 
var iBoxWidth=BoxWidth; 
var NewHTML="<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\"><tr><td id=\"topTd1\"><div id=\"topDiv1\" style=\"width:100%;overflow-x:scroll\">"; 
NewHTML+="<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\"><tr>";
for(var i=0;i<DataTitles.length;i++){ 
  if(i<LockCols){ 
    iBoxWidth-=widthS_lock[i]; 
    var cTitle=DataTitles[i].split("#####") ;
    if(i==0)
       NewHTML+="<td align=\"center\" width=\"30\" class=\"t_header_locked2\" nowrap>"+cTitle[0]+"</td>"    
    else
      NewHTML+="<td align=\"center\" width=\""+widthS_lock[i]+"\" id=\"${element.itemid}\" class=\"t_header_locked\"  nowrap>"+cTitle[0]+"</td>"    
  } 
} 
//iBoxWidth=500;
NewHTML+="</tr><tr><td colspan=\""+LockCols+"\"><div id=\"DataFrame1\" style=\"position:relative;width:100%;overflow:hidden; HEIGHT: "+BoxHeight+"px\">";
NewHTML+="<div id=\"DataGroup1\" style=\"position:relative\"></div></div>"; 
NewHTML+="</td></tr></table></div></td>"; 
NewHTML+="<td valign=\"top\"><div style=\"width:"+iBoxWidth+"px;overflow-x:scroll\">"; 
NewHTML+="<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\"><tr>";
for(i=0;i<DataTitles.length;i++){ 
  if(i>=LockCols){  
    var cTitle=DataTitles[i].split("#####") ;    
    NewHTML+="<td align=\"center\" id=\"${element.itemid}\" class=\"t_header_locked\"  nowrap>"+cTitle[0]+"</td>";
  } 
} 
NewHTML+="</tr><tr><td colspan=\""+(DataTitles.length-LockCols)+"\">";
NewHTML+="<div id=\"DataFrame2\" style=\"position:relative;width:100%;overflow:hidden;HEIGHT: "+BoxHeight+"px\">"; 
NewHTML+="<div id=\"DataGroup2\" style=\"position:relative\"></div>"; 
NewHTML+="</div></td></tr></table>"; 
NewHTML+="</div></td><td valign=\"top\">"; 
NewHTML+="<div id=\"DataFrame3\" style=\"position:relative;background:#000;overflow-y:scroll\" onscroll=\"SYNC_Roll()\">"; 
NewHTML+="<div id=\"DataGroup3\" style=\"position:relative;width:1px;visibility:hidden\"></div>"; 
NewHTML+="</div></td></tr></table>"; 
var DataTable=document.getElementById("DataTable");
DataTable.innerHTML=NewHTML;
//return;
//ApplyData() 

} 
function countWidth(longStr)
{
  
  var widthS=new Array();
  widthS[0]=longStr[0];  
  for(var i=1;i<longStr.length;i++){ 
     widthS[i]=getStrLenWidth(longStr[i]);
  } 
  return widthS;
 }
 
 function getStrLenWidth(str)
 {
     var sum=0;
     for(var i=0;i<str.length;i++)
     {
         if ((str.charCodeAt(i)>=0) && (str.charCodeAt(i)<=255))
             sum=sum+1;
         else
             sum=sum+2;
     } 
     return sum*10+10;
 }